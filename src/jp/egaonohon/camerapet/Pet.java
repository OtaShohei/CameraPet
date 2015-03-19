package jp.egaonohon.camerapet;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;

/**
 * 画面上で動かすペットのクラス。 2015-03-19時点では未完成です。
 * 
 * @author 1107AND
 *
 */
public class Pet {

	/**
	 * フィールド。姿を現す「画像」と「大きさ」。そして「動かすときの位置（座標）」は最低限必要なので用意する。
	 */
	private Bitmap bitmap;
	private int width;
	private int height;
	private int nowX;// 現在地x。初期値としてコンストラクタで渡す。
	private int nowY;// 現在地y
	private int targetX;// どこへ行くかの目的地x。setterで渡す。
	private int targetY;// どこへ行くかの目的地y

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

}
