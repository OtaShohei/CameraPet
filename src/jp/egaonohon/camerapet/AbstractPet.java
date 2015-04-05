package jp.egaonohon.camerapet;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.view.View;

/**
 * ゲーム画面で動く各種ペットの抽象クラス
 *
 * @author OtaShohei
 *
 */
public abstract class AbstractPet extends CamPeItem implements Runnable {

	/** ペットの種別名 */
	protected final String model_number = "AbstractPet";
	/** ペットの種名 */
	protected String petName;

	/** Item現在位置：X軸 */
	private int nowX = 0;
	/** Item現在位置：Y軸 */
	private int nowY = 0;

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
	public AbstractPet(View view, Bitmap petPhR, Bitmap petPhL, int itemWidth,
			int itemHeight, int defaultX, int defaultY, int viewWidth,
			int viewHeight) {
		super(itemWidth, itemHeight, defaultX, defaultY, viewWidth, viewHeight);
		CameLog.setLog(TAG, "petPhRは" + petPhR + "petPhLは" + petPhL
				+ "itemWidthは" + itemWidth);
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
	public abstract void move();

	@Override
	public abstract void draw(Canvas canvas);

	@Override
	public abstract void run();

	/** ペットの喋らせるメソッド。 */
	public abstract void talk(View view,int eventCode
//			Context context, View view, int eventCode,int fukidasiDefaultX, int fukidasiDefaultY
			);

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

	public String getPetModelNumber() {
		return model_number;
	}

	/**
	 * @return petName
	 */
	public String getPetName() {
		return petName;
	}
}
