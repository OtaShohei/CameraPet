package jp.egaonohon.camerapet.pet;

import jp.egaonohon.camerapet.CameLog;
import jp.egaonohon.camerapet.R;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.RectF;
import android.media.MediaPlayer;
import android.view.View;

/**
 * ゲーム画面で動くペットレベル1Aのクラス。
 *
 * @author OtaShohei
 *
 */
public class Pet003A extends AbstractPet implements Runnable {

	/** ペットの型番 */
	private static final String MODEL_NUMBER = "Pet003A";
	/** ペットの種名 */
	private String petName;
	/** くすぐったい時用サウンド */
	private MediaPlayer pleasedSE;
	/** Logのタグを定数で確保 */
	private static final String TAG = "Pet003A";

	/**
	 * スーパークラスからのコンストラクタ。
	 *
	 * @param itemPh
	 * @param width
	 * @param height
	 * @param defaultX
	 * @param defaultY
	 */
	public Pet003A(Bitmap itemPh, int width, int height, int defaultX,
			int defaultY, int viewWidth, int viewHeight) {
		super(itemPh, width, height, defaultX, defaultY, viewWidth, viewHeight);
	}

	/**
	 * ペットのコンストラクタ。左右画像が必要です。
	 *
	 * @param petPhR
	 * @param petPhL
	 * @param itemWidth
	 * @param itemHeight
	 * @param defaultX
	 * @param defaultY
	 * @param viewWidth
	 * @param viewHeight
	 */
	public Pet003A(View view, Bitmap petPhR, Bitmap petPhL, int itemWidth,
			int itemHeight, int defaultX, int defaultY, int viewWidth,
			int viewHeight) {
		super(petPhR, itemWidth, itemHeight, defaultX, defaultY, viewWidth,
				viewHeight);
		CameLog.setLog(TAG, "petPhRは" + petPhR + "petPhLは" + petPhL
				+ "itemWidthは" + itemWidth);

		this.petView = view;
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
		// nowFukidasi = new simpleFukidasi();

		/** petの名前を取得 */
		Resources res = petView.getResources();
		petName = res.getString(R.string.pet_03_name);

		/** 衝突判定用RectFをインスタンス化 */
		rectF = new RectF();

		CameLog.setLog(TAG, "Petがnewされた時点でのnowXは" + this.nowX + "。nowYは"
				+ this.nowY);

		CameLog.setLog(TAG, "Petがnewされた時点でのViewのWidthは" + this.viewWidth
				+ "。ViewのHeightは" + this.viewHeight);

		CameLog.setLog(TAG, "Petがnewされた時点でのペットのWidthは" + this.itemWidth
				+ "。Heightは" + this.itemHeight);

		/**
		 * PetPhの拡大・縮小率設定 ここでfloatにキャストしないと拡大率が小数点以下切り捨てられてしまうので要注意
		 */
		scaleX = (float) itemWidth / petPhR.getWidth();
		scaleY = (float) itemHeight / petPhR.getHeight();

		/** くすぐったい時用SEのインスタンス生成し準備 */
		pleasedSE = MediaPlayer.create(view.getContext(), R.raw.payohn);

		CameLog.setLog(TAG, "scaleXは" + scaleX + "scaleYは" + scaleY);

		matrix.setScale(scaleX, scaleY);
		// /** PetPhの拡大縮小率確認 */
		// CameLog.setLog(TAG, "PetPhの拡大縮小率はXが" + scaleX + "。Yは" + scaleY);

		/** PetPhの回転角設定 */
		// matrix.postRotate(degree);

		camPeItemThread = new Thread(this);
		camPeItemThread.start();
	}

	/** 触られてペットが喜ぶメソッド */
	public void pleased() {
		/** 鳴き声を上げる */
		pleasedSE.start();
		// /** 写真を入れ替えて震えてもいい */
		// changeItemPh = true;
	}

	public String getPetModelNumber() {
		return MODEL_NUMBER;
	}

	@Override
	public String getPetName() {
		return petName;
	}

}
