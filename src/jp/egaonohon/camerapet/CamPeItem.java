package jp.egaonohon.camerapet;

import android.graphics.Canvas;
import android.graphics.RectF;

/**
 * アプリのゲーム画面に登場するアイテムすべての抽象クラス。
 * @author OtaShohei
 *
 */
public abstract class CamPeItem {

	/** Viewの幅 */
	protected int viewWidth;
	/** Viewの高さ */
	protected int viewHeight;

	/** Itemの幅 */
	protected int itemWidth;
	/** Itemの高さ */
	protected int itemHeight;

	/** Item初期位置：X軸 */
	protected int defaultX;
	/** Item初期位置：Y軸 */
	protected int defaultY;
	/** Item現在位置：X軸 */
	protected int nowX;
	/** Item現在位置：Y軸 */
	protected int nowY;

	/** CamPeItemのスレッド */
	protected Thread camPeItemThread;

	/** 衝突判定用のRectF。ここをstaticにするとペットかエサどちらか先にRectに数値をセットした方が確保してしまうので要注意。 */
	protected RectF rectF;

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

	public float getNowX() {
		return nowX;
	}

	public float getNowY() {
		return nowY;
	}

	/** RectFをGameSurfaceViewに渡すメソッド */
	public RectF getRectF() {
		return rectF;
	}

	/** CamPeItemのThreadを停止するメソッド */
	public void stopCamPeItemThread() {
		camPeItemThread = null;
	}
}
