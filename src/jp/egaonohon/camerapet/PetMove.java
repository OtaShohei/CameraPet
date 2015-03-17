package jp.egaonohon.camerapet;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class PetMove extends SurfaceView implements SurfaceHolder.Callback,
		Runnable {
	/** 描画開始位置：X軸 */
	private int currentX;
	/** 描画開始位置：Y軸 */
	private int currentY;
	/** 移動距離：X軸 */
	private int moveX = 3;
	/** 移動距離：Y軸 */
	private int moveY = 5;
	/** 描画用スレッド */
	private Thread thread;
	/** 画像 */
	private Bitmap petPh;
	/** SurfaceHolderをメンバ変数として確保 */
	private SurfaceHolder holder;
	/** 画面サイズ。ペットなどの表示の機種ごとの差異を吸収するため。 */
	private int width;
	private int height;
	/** Logのタグを定数で確保 */
	private static final String TAG = "PetMove";

	// /** 初動加速の判断用 */
	// private boolean speedMove = false;

	/**
	 * コンストラクタ引数1つのここに記述すると例外発生。SurfaceViewを全画面に用いてないから?
	 *
	 * @param context
	 */
	public PetMove(Context context) {
		super(context);
	}

	/**
	 * カスタムビューとしてXMLに定義するときに必要なコンストラクタ
	 *
	 * @param context
	 * @param attrs
	 */
	public PetMove(Context context, AttributeSet attrs) {
		super(context, attrs);
		// 画像の読み込み
		Resources res = getResources();
		petPh = BitmapFactory.decodeResource(res, R.drawable.alpaca02);

		// SurfaceHolder の取得
		holder = getHolder();

		// SurfaceHolder に コールバックを設定
		holder.addCallback(this);
		holder.setFixedSize(getWidth(), getHeight());

		// フォーカスをあてる
		setFocusable(true);

		initialize();
	}

	/**
	 * コンストラクタ。
	 *
	 * @param context
	 * @param attrs
	 * @param defStyle
	 */
	public PetMove(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	private void initialize() {
		// 半透明を設定
		getHolder().setFormat(PixelFormat.TRANSLUCENT);
		// コールバック登録
		getHolder().addCallback(this);
		// フォーカス可
		setFocusable(true);
		// このViewをトップにする
		setZOrderOnTop(true);
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		thread = new Thread(this);
		thread.start();
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
//		boolean retry = true;
//
//		synchronized (holder) { // 同期処理
//			threadRun = false; // メンバ変数　終了フラグを立てる
//		}
//
//		while (retry) {
//			try {
//				thread.join(); // 別スレッドが終了するまで待つ
//				retry = false;
//			} catch (InterruptedException e) {
//			}
//		}
		thread = null; // スレッド終了
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		/**
		 * 例外対策として以下の3行を追加。2行目と3行目はsurfaceCreatedからこちらへ移動してみた。
		 */
		this.holder = holder;
		thread = new Thread(this);
		thread.start();

		/**
		 * 機種間の画面サイズ大小によって見た目が変化するのを防ぐために画面サイズをとる。
		 * このメソッドの引数に.widthとheightがあるのでこれを利用する。
		 *
		 * 変数を縦横で入れるときは、width→heightの順に必ずしておく。 そうすると引数に数字をいれるときに悩まなくて済む。
		 *
		 * これらの数字で移動距離を出す。
		 *
		 * @param context
		 */
		this.width = width;
		this.height = height;
	}

	@Override
	public void run() {

		/**
		 * どうしても安全にスレッドを停止できないのでtry-catchで囲みます…。
		 */
		try {
			/**
			 * ペットの表示サイズをここで調整。
			 * ペットが正方形なのでともにwidth/3で設定
			 */
			petPh = Bitmap.createScaledBitmap(petPh, width/3, width/3, true);

			while (thread != null) {
				// ホルダーからキャンバスの取得
				Canvas canvas = getHolder().lockCanvas();

				// 描画処理
				draw(canvas);

				// 描画内容の確定
				getHolder().unlockCanvasAndPost(canvas);

				// 移動処理
				moveProc();
			}
		} catch (Exception e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
	}

	/**
	 * 描画処理
	 */
	@Override
	public void draw(Canvas canvas) {
		// 現在の状態を保存
		canvas.save();

		Paint paint = new Paint();

		/**
		 * Canvasの背景色を透明で塗る。 第二引数にPorterDuff.Mode.CLEARを付けないと透明にならないので注意。
		 */
		canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);

		// 画像の描画
		canvas.drawBitmap(petPh, currentX, currentY, paint);

		// 現在の状態の変更
		canvas.restore();
	}

	@SuppressLint("ClickableViewAccessibility")
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		float x = event.getX(); // X座標を取得
		float y = event.getY(); // Y座標を取得
		// speedMove = true;
		move(x, y);
		return true;
	}

	/**
	 * 移動量取得
	 */
	public void move(float x, float y) {
		moveX = (int) (x / 100);
		moveY = (int) (y / 100);
	}

	/**
	 * 移動処理
	 */
	private void moveProc() {
		// 画面端のチェック：X軸
		if (currentX < 0 || getWidth() - petPh.getWidth() < currentX) {
			moveX = -moveX;
		}
		// 画面端のチェック：Y軸
		if (currentY < 0 || getHeight() - petPh.getHeight() < currentY) {
			moveY = -moveY;
		}

		// 描画座標の更新
		currentX += moveX;
		currentY += moveY;
	}
}
