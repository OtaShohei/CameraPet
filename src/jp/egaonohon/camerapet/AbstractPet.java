package jp.egaonohon.camerapet;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;

/**
 * ゲーム画面で動く各種ペットの抽象クラス
 * @author OtaShohei
 *
 */
public abstract class AbstractPet extends CamPeItem implements Runnable {

	/** 描画設定 */
	private Paint petPaint = new Paint();
	/**
	 * Petの画像拡大縮小用 拡大縮小に関しては、こちらのサイトを参照。
	 * http://blog.livedoor.jp/tmtlplus/archives/19016162.html
	 */
	final Matrix matrix = new Matrix();

	/** Viewの幅 */
	private int viewWidth;
	/** Viewの高さ */
	private int viewHeight;

	/** 実行中ペット画像 */
	private Bitmap itemPh;
	/** ペット右向き画像 */
	private Bitmap petPhR;
	/** ペット左向き画像 */
	private Bitmap petPhL;

	/** X方向の拡大縮小率算出 */
	float scaleX = 1.0f;
	/** Y方向の拡大縮小率算出 */
	float scaleY = 1.0f;
	/** Itemの回転角度 */
	private float degree = 1.0f;
	/** Itemの幅 */
	private int itemWidth;
	/** Itemの高さ */
	private int itemHeight;
	/** Item初期位置：X軸 */
	private int defaultX;
	/** Item初期位置：Y軸 */
	private int defaultY;
	/** Item現在位置：X軸 */
	private int nowX = 0;
	/** Item現在位置：Y軸 */
	private int nowY = 0;
	/** Item移動距離：X軸 */
	private int moveX;
	/** Item移動距離：Y軸 */
	private int moveY;
	/** ペットの歩くアニメーション効果用（歩数カウント） */
	private int cnt;
	/** ペットが動くスピード（移動および歩くアニメーションに影響） */
	private long speed = 60;
	/** ペット移動加速度X軸 */
	private int petKasokudoX;
	/** ペット移動加速度Y軸 */
	private int petKasokudoY ;

	/** ペット用のスレッド */
	private Thread petThread;

	/** Logのタグを定数で確保 */
	private static final String TAG = "Pet";

	/**
	 * スーパークラスからのコンストラクタ。
	 *
	 * @param itemPh
	 * @param width
	 * @param height
	 * @param defaultX
	 * @param defaultY
	 */
	public AbstractPet(Bitmap itemPh, int width, int height, int defaultX,
			int defaultY, int viewWidth, int viewHeight) {
		super(width, height, defaultX, defaultY, viewWidth, viewHeight);
	}

	/**
	 * ペットのコンストラクタ。左右画像が必要です。
	 *
	 * @param itemWidth
	 * @param itemHeight
	 * @param defaultX
	 * @param defaultY
	 * @param viewWidth
	 * @param viewHeight
	 */
	public AbstractPet(Bitmap petPhR, Bitmap petPhL, int itemWidth, int itemHeight,
			int defaultX, int defaultY, int viewWidth, int viewHeight) {
		super(itemWidth, itemHeight, defaultX, defaultY, viewWidth,
				viewHeight);
		CameLog.setLog(TAG, "petPhRは" + petPhR + "petPhLは" + petPhL
				+ "itemWidthは" + itemWidth);

		this.petPhR = petPhR;
		this.petPhL = petPhL;
		this.itemPh = petPhR;
		this.itemWidth = itemWidth;
		this.itemHeight = itemHeight;
		/** Defaultの位置を現在地に代入する */
		this.nowX = defaultX;
		this.nowY = defaultY;
		this.viewWidth = viewWidth;
		this.viewHeight = viewHeight;

		/** 衝突判定用RectFをインスタンス化 */
		rectF = new RectF();

		/**
		 * PetPhの拡大・縮小率設定 ここでfloatにキャストしないと拡大率が小数点以下切り捨てられてしまうので要注意
		 */
		scaleX = (float) itemWidth / petPhR.getWidth();
		scaleY = (float) itemHeight / petPhR.getHeight();

		CameLog.setLog(TAG, "scaleXは" + scaleX + "scaleYは" + scaleY);

		matrix.setScale(scaleX, scaleY);

		/** PetPhの回転角設定 */
		// matrix.postRotate(degree);

		petThread = new Thread(this);
		petThread.start();
	}

	/**
	 * GameSurfaceViewでのオンタッチイベントでやって来たPetの移動量をセットする抽象メソッド。
	 * GameSurfaceViewから呼び出す。
	 */
	public abstract void setPetMoveSize(float x, float y);

	/** エサと衝突した際にペットに起こるイベントを定義する抽象メソッド。GameSurfaceViewから呼び出す */
	public abstract void returnEsaKrush();

	/**
	 * Petの通常移動処理を司るメソッド。
	 */
	@Override
	public void move() {
		/** 歩数を数える */
		cnt++;

		/** 画面端のチェック：X軸。 */
		if (nowX < 0 || this.viewWidth - itemWidth < nowX) {
			if (itemPh.equals(petPhR)) {
				itemPh = petPhL;
			} else if (itemPh.equals(petPhL)) {
				itemPh = petPhR;
			}
			moveX *= -1;
		}

		/** 画面端のチェック：Y軸。 */
		if (nowY < 0 || this.viewHeight - itemHeight < nowY) {
			moveY *= -1;
		}

		/** 移動させる */
		nowX = nowX + moveX;
		nowY = nowY + moveY;

		/** 衝突判定用RectFの更新 */
		rectF.set(nowX, nowY, nowX + itemWidth, nowY + itemHeight);
	}

	@Override
	public void draw(Canvas canvas) {
		/** Canvasの背景色を透明で塗る。 第二引数にPorterDuff.Mode.CLEARを付けないと透明にならないので注意。 */
		canvas.drawColor(Color.TRANSPARENT,
				android.graphics.PorterDuff.Mode.CLEAR);
		canvas.save();
		/** ここで描画位置を指定 */
		canvas.translate(nowX, nowY);
		canvas.drawBitmap(itemPh, matrix, petPaint);
		canvas.restore();
	}

	@Override
	public void run() {
		while (petThread != null) {
			move();
			try {
				Thread.sleep(1000 / speed);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	public int getNowX() {
		return nowX;
	}

	public void setNowX(int nowX) {
		this.nowX = nowX;
	}

	public int getNowY() {
		return nowY;
	}

	public void setNowY(int nowY) {
		this.nowY = nowY;
	}
}
