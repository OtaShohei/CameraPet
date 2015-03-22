package jp.egaonohon.camerapet;

import java.util.ArrayList;

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

/**
 * メイン画面内のペットが動き回るエリアを司るクラス。
 *
 * @author OtaShohei
 *
 */
public class PetAsobiBa extends SurfaceView implements SurfaceHolder.Callback,
		Runnable {
	/** Pet描画開始位置：X軸 */
	private int petCurrentX;
	/** Pet描画開始位置：Y軸 */
	private int petCurrentY;
	/** Pet移動距離：X軸 */
	private int petMoveX = 3;
	/** Pet移動距離：Y軸 */
	private int petMoveY = 5;

	/** 餌の数 */
	private int esaCnt = 0;
	/** 餌描画開始位置：X軸 */
	private int esaCurrentX = 5;
	/** 餌描画開始位置：Y軸 */
	private int esaCurrentY = 0;
	/** 餌移動距離：X軸 */
	private int esaMoveX = 0;
	/** 餌移動距離：Y軸 */
	private int esaMoveY = 5;

	/** 描画用スレッド */
	private Thread thread;
	/** ペット画像 */
	private Bitmap petPh;
	/** 餌画像 */
	private ArrayList<Bitmap> esaPhList;
	/** SurfaceHolderをメンバ変数として確保 */
	private SurfaceHolder holder;
	/** 画面サイズ。ペットなどの表示の機種ごとの差異を吸収するため。 */
	private int width;
	private int height;
	/** Logのタグを定数で確保 */
	private static final String TAG = "PetAsobiBa";

	// /** 初動加速の判断用 */
	// private boolean speedMove = false;

	/**
	 * 引数1つのコンストラクタ。 ここに記述すると例外発生する。SurfaceViewを全画面に用いてないからか?
	 *
	 * @param context
	 */
	public PetAsobiBa(Context context) {
		super(context);
	}

	/**
	 * 引数2つのコンストラクタ。 カスタムビューとしてXMLに定義するときに必要なコンストラクタ。
	 *
	 * @param context
	 * @param attrs
	 */
	public PetAsobiBa(Context context, AttributeSet attrs) {
		super(context, attrs);
		/** 画像の読み込み */
		Resources res = getResources();
		petPh = BitmapFactory.decodeResource(res, R.drawable.alpaca02);
		CamPePh camPePh = new CamPePh();
		esaPhList = camPePh.get(context, CamPeDb.getNowShotCnt(context));
		esaCnt = esaPhList.size();

		CameLog.setLog(TAG, "コンストラクタにて画像の読み込み完了。餌Phは" + esaCnt + "枚");
		/** SurfaceHolder の取得 */
		holder = getHolder();

		/** SurfaceHolder に コールバックを設定 */
		holder.addCallback(this);
		holder.setFixedSize(getWidth(), getHeight());

		/** フォーカスをあてる */
		setFocusable(true);

		initialize();
	}

	/**
	 * 引数3つのコンストラクタ。
	 *
	 * @param context
	 * @param attrs
	 * @param defStyle
	 */
	public PetAsobiBa(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	/**
	 * イニシャライズメソッド。
	 */
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
		CameLog.setLog(TAG, "surfaceCreated");
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
		CameLog.setLog(TAG, "surfaceChanged");
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		thread = null; // スレッド終了
		CameLog.setLog(TAG, "surfaceDestroyed");
	}

	@Override
	public void run() {

		/**
		 * どうしても安全にスレッドを停止できないのでtry-catchで囲みます…。
		 */
		try {
			/**
			 * ペットの表示サイズをここで調整。 ペットが正方形なのでともにwidth/3で設定
			 */
			petPh = Bitmap
					.createScaledBitmap(petPh, width / 3, width / 3, true);
			/** 餌画像の描画 */
			for (int i = 0; i < esaPhList.size(); i++) {
				Bitmap.createScaledBitmap(esaPhList.get(i), width / 10,
						width / 10, true);
			}

			while (thread != null) {
				/** ホルダーからキャンバスの取得 */
				Canvas canvas = getHolder().lockCanvas();

				/** 描画処理 */
				draw(canvas);

				/** 描画内容の確定 */
				getHolder().unlockCanvasAndPost(canvas);

				/** 移動処理 */
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
		/** 現在の状態を保存 */
		canvas.save();

		Paint paint = new Paint();

		/**
		 * Canvasの背景色を透明で塗る。 第二引数にPorterDuff.Mode.CLEARを付けないと透明にならないので注意。
		 */
		canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);

		/** 画像の描画 */
		canvas.drawBitmap(petPh, petCurrentX, petCurrentY, paint);
		/** 餌画像の描画 */
		for (int i = 0; i < esaPhList.size(); i++) {
			/**
			 * 画面幅の20分の1ずつ出現位置をずらす。
			 */
			canvas.drawBitmap(esaPhList.get(i), esaCurrentX *((i+1) * (width/20)), esaCurrentY, paint);
			CameLog.setLog(TAG, "餌画像の描画" + i + "枚目");
		}
		/** 現在の状態の変更 */
		canvas.restore();
//        holder.unlockCanvasAndPost(canvas);
	}

	@SuppressLint("ClickableViewAccessibility")
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		float x = event.getX(); // X座標を取得
		float y = event.getY(); // Y座標を取得
		// speedMove = true;
		getMoveSize(x, y);
		CameLog.setLog(TAG, "onTouchEvent");
		return true;
	}

	/**
	 * 移動量取得
	 */
	public void getMoveSize(float x, float y) {
		petMoveX = (int) (x / 100);
		petMoveY = (int) (y / 100);
		CameLog.setLog(TAG, "onTouchEvent");
	}

	/**
	 * 移動処理
	 */
	private void moveProc() {

		/**
		 * Pet移動処理
		 */
		// 画面端のチェック：X軸
		if (petCurrentX < 0 || getWidth() - petPh.getWidth() < petCurrentX) {
			petMoveX = -petMoveX;
		}
		// 画面端のチェック：Y軸
		if (petCurrentY < 0 || getHeight() - petPh.getHeight() < petCurrentY) {
			petMoveY = -petMoveY;
		}

		// 描画座標の更新
		petCurrentX += petMoveX;
		petCurrentY += petMoveY;

		/**
		 * 餌移動処理。
		 */
			// 画面端のチェック：X軸
			if (esaCurrentX < 0
					|| getWidth() - getWidth() / 10 /* petPh.getWidth() */< esaCurrentX) {
				esaMoveX = -esaMoveX;
			}
			// 画面端のチェック：Y軸
			if (esaCurrentY < 0
					|| getHeight() - getWidth() / 10 /* petPh.getWidth() */< esaCurrentY) {
				esaMoveY = -esaMoveY;
				CameLog.setLog(TAG, "餌移動処理Y軸");
			}

			// 描画座標の更新
			esaCurrentX += esaMoveX;
			esaCurrentY += esaMoveY;
	}
}