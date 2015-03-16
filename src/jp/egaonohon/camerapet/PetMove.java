package jp.egaonohon.camerapet;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.view.SurfaceHolder;
import android.graphics.PorterDuff;

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

	// /** 初動加速の判断用 */
	// private boolean speedMove = false;

	/**
	 * コンストラクタ。
	 * 
	 * @param context
	 * @param attrs
	 * @param defStyle
	 */
	public PetMove(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO 自動生成されたコンストラクター・スタブ
	}

	/**
	 * カスタムビューとしてXMLに定義するときに必要なコンストラクタ
	 * 
	 * @param context
	 */
	public PetMove(Context context, AttributeSet attrs) {
		super(context, attrs);
		// 画像の読み込み
		Resources res = getResources();
		petPh = BitmapFactory.decodeResource(res, R.drawable.alpaca02);

		// SurfaceHolder の取得
		SurfaceHolder holder = getHolder();

		// SurfaceHolder に コールバックを設定
		holder.addCallback(this);
		holder.setFixedSize(getWidth(), getHeight());

		// フォーカスをあてる
		setFocusable(true);

		initialize();
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
		// boolean retry = true;
		//
		// synchronized (holder) { // 同期処理
		// threadRun = false; // メンバ変数　終了フラグを立てる
		// }
		//
		// while (retry) {
		// try {
		// thread.join(); // 別スレッドが終了するまで待つ
		// retry = false;
		// } catch (InterruptedException e) {
		// }
		// }
		thread = null; // スレッド終了
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {

	}

	@Override
	public void run() {
		/**
		 * どうしても安全にスレッドを停止できないのでtry-catchで囲みます…。
		 */
		try {
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
