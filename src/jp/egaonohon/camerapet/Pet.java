package jp.egaonohon.camerapet;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;

/**
 * 画面上で動かすペットのクラス。 2015-03-19時点では未完成です。
 *
 * @author 1107AND
 *
 */
public class Pet implements Runnable{

	/**
	 * フィールド。姿を現す「画像」と「大きさ」。そして「動かすときの位置（座標）」は最低限必要なので用意する。
	 */
	/** Petの表示画像*/
	private Bitmap bitmap;
	private int width;
	private int height;
	/** 現在地x。初期値としてコンストラクタで渡す。*/
	private int nowX;
	/** 現在地y。初期値としてコンストラクタで渡す。 */
	private int nowY;
	/** どこへ行くかの目的地x。setterで渡す。 */
	private int targetX;
	/** どこへ行くかの目的地y。setterで渡す。 */
	private int targetY;// どこへ行くかの目的地y
	/** Petの動作speed*/
	private long speed = 8;
	/** Petを動かすスレッド*/
	private Thread thread;
	/** 当たり判定用。餌クラスだけでいいかも。これは。*/
	private RectF rect;
	/** 無限カウントアップ。*/
	int cnt;



	/**
	 * ペットのコンストラクタ。
	 *
	 * @param bitmap
	 * @param width
	 * @param height
	 * @param x
	 * @param y
	 */
	public Pet(Bitmap bitmap, int width, int height, int x, int y) {
		super();
		this.bitmap = bitmap;
		this.width = width;
		this.height = height;
		this.nowX = x;
		this.nowY = y;
	}

	/**
	 * 目的地を与えるsetter
	 *
	 * @param targetX
	 */
	public void setTargetX(int targetX) {
		this.targetX = targetX;
	}

	public void setTargetY(int targetY) {
		this.targetY = targetY;
	}

	/**
	 * 現在地を取得するgetter
	 *
	 * @param targetX
	 */
	public int getNowX() {
		return nowX;
	}

	public int getNowY() {
		return nowY;
	}

	/**
	 *
	 * @return
	 */
	public Bitmap getBitmap() {
		return bitmap;
	}

	/**
	 *
	 * @return
	 */
	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	/**
	 * メソッド。ペットは何をするのかを記述するメソッド。 現在地と目的地を確認して動いていく。
	 *
	 *
	 */
	public void move() {
		/**
		 * マイナスになることがあるので注意。
		 */
		int distanceX = targetX - nowX;

		if (distanceX > 0) {
			nowX++;
		} else {
			nowX--;// 差分がマイナスになる時の動き方はこれ。
		}

		int distanceY = targetY - nowY;

		if (distanceY > 0) {

			nowY++;

		} else {
			nowY--;
		}
	}

	Paint paint;

	/**
	 * SurfaceView上にペットを描画するメソッドをSurfaceViewのクラスではなくこちらがわに書くのがすっきりするし主流なので行ってみる。
	 *
	 * @param canvas
	 */
	public void draw(Canvas canvas) {
		canvas.drawBitmap(bitmap, nowX, nowY, paint);
	}

	@Override
	public void run() {
		// TODO 自動生成されたメソッド・スタブ

	}

}
