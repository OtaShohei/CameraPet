package jp.egaonohon.camerapet;

import android.graphics.Bitmap;
import android.graphics.Canvas;
//import android.graphics.Matrix;
//import android.graphics.Paint;
import android.graphics.RectF;

/**
 * アプリのゲーム画面に登場するアイテムすべての抽象クラス。
 * @author OtaShohei
 *
 */
public abstract class CamPeItem {

//	/** 描画設定 */
//	private Paint paint = new Paint();
//	/**
//	 * Petの画像拡大縮小用 拡大縮小に関しては、こちらのサイトを参照。
//	 * http://blog.livedoor.jp/tmtlplus/archives/19016162.html
//	 */
//	final Matrix matrix = new Matrix();

	/** Viewの幅 */
	private int viewWidth;
	/** Viewの高さ */
	private int viewHeight;

	/** アイテム画像 */
	private Bitmap itemPh;

//	/** X方向の拡大縮小率算出 */
//	float scaleX = 1.0f;
//	/** Y方向の拡大縮小率算出 */
//	float scaleY = 1.0f;
//	/** Itemの回転角度 */
//	private float degree = 1.0f;

	/** Itemの幅 */
	private int itemWidth;
	/** Itemの高さ */
	private int itemHeight;

	/** Item初期位置：X軸 */
	private int defaultX;
	/** Item初期位置：Y軸 */
	private int defaultY;
	/** Item現在位置：X軸 */
	private int nowX;
	/** Item現在位置：Y軸 */
	private int nowY;
//	/** Item移動距離：X軸 */
//	private static int moveX;
//	/** Item移動距離：Y軸 */
//	private static int moveY;

	/** 衝突判定用のRectF。ここをstaticにするとペットかエサどちらか先にRectに数値をセットした方が確保してしまうので要注意。 */
	public  RectF rectF;

	/**
	 * ゲーム登場Item抽象クラスのコンストラクタ。
	 *
	 * @param itemPh
	 * @param width
	 * @param height
	 * @param defaultX
	 * @param defaultY
	 */
	public CamPeItem( int width, int height, int defaultX,
			int defaultY, int viewWidth, int viewHeight) {
		super();
		this.itemWidth = width;
		this.itemHeight = height;
		this.defaultX = defaultX;
		this.defaultY = defaultY;
		this.viewWidth = viewWidth;
		this.viewHeight = viewHeight;
	}

	/** Itemの動作を司る抽象クラス */
	public abstract void move();

	/** ItemのCanvasへの描き込みを司る抽象クラス */
	public abstract void draw(Canvas canvas);

	public int getWidth() {
		return itemWidth;
	}


	public int getHeight() {
		return itemHeight;
	}


	public int getNowX() {
		return nowX;
	}


	public int getNowY() {
		return nowY;
	}

	/** RectFをGameSurfaceViewに渡すメソッド */
	public RectF getRectF() {
		return rectF;
	}
}
