package jp.egaonohon.camerapet;

import android.R.integer;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.DisplayMetrics;
import android.widget.Toast;

//import android.graphics.RectF;

/**
 * 画面上で動かすペットのクラス。
 *
 * @author OtaShohei
 *
 */
public class CPPet extends CPTask {
	/**
	 * フィールド。姿を現す「画像」と「大きさ」。そして「動かすときの位置（座標）」は最低限必要なので用意する。
	 */
	/** 描画設定 */
	private Paint _paint = new Paint();
	/** ペット画像 */
	private Bitmap petPh;

	/** Viewの幅 */
	private int viewWidth;
	/** Viewの高さ */
	private int viewHeight;

	/** Petの幅 */
	private int petWidth = 50;
	/** Petの高さ */
	private int petHeight = 50;

	/** Pet現在位置：X軸 */
	private int petCurrentX;
	/** Pet現在位置：Y軸 */
	private int petCurrentY;
	/** Pet移動距離：X軸 */
	private static int petMoveX = 3;
	/** Pet移動距離：Y軸 */
	private static int petMoveY = 5;

	/** ペットの移動ベクトル */
	private CPVec _vec = new CPVec();

	/** Logのタグを定数で確保 */
	private static final String TAG = "CPPet";

	/**
	 * ペットのコンストラクタ。
	 *
	 * @param bitmap
	 *            ペット画像
	 * @param viewWidth
	 *            ペットが存在するviewの幅
	 * @param viewHeight
	 *            ペットが存在するviewの高さ
	 */
	public CPPet(Bitmap bitmap, int viewWidth, int viewHeight) {
		super();
		this.petPh = bitmap;
		this.viewWidth = viewWidth;
		this.viewHeight = viewHeight;
		/** ペットの幅はViewの幅の1/5 */
		this.petWidth = this.viewWidth / 5;
		/** ペットの高さはViewの幅の1/5 */
		this.petHeight = this.petWidth;
		// this.petCurrentX = x;
		// this.petCurrentY = y;

		/** 出現位置 */
		this.petCurrentX = 0;
		this.petCurrentY = 0;
		_vec._x = 3; // 移動ベクトルを下に向ける
		_vec._y = 5; // 移動ベクトルを下に向ける
	}

	/**
	 * PetAsobiBaから移動してきたペット移動処理メソッド。
	 */
	@Override
	public boolean onUpdate() {

		/**
		 * Pet移動処理
		 */
//		 /**ディスプレイ情報を取得。1ピクセル×scaledDensity＝1dipとなります。 */
//		 DisplayMetrics metrics = new DisplayMetrics();		 
//		// density (比率)を取得する
//		 float density = metrics.scaledDensity;
//		 // 縦のpx を dp に変換する ( pixel ÷ density + 0.5f（四捨五入) )
//		 int higthdp = (int) (metrics.heightPixels / density + 0.5f);
//
//		 // 横の px を dp に変換する ( pixel ÷ density + 0.5f（四捨五入) )
//		 int widhtdp = (int) (metrics.widthPixels / density + 0.5f);
		 
		//
		 // 画面端のチェック：X軸
		 if (petCurrentX < 0 || 780 - petWidth < petCurrentX) {
		 petMoveX = -petMoveX;
		 }
//			CameLog.setLog(TAG, "Viewの幅は" + viewWidth);
		 
		 // 画面端のチェック：Y軸
		 if (petCurrentY < 0 || 1000 - petHeight < petCurrentY) {
		 petMoveY = -petMoveY;
		 }
//			CameLog.setLog(TAG, "Viewの高さは" + viewHeight);

//		/** 画面端のチェック：X軸 */
//		if (petCurrentX < 0 || (widhtdp *5) - petWidth < petCurrentX) {
//			petMoveX = -petMoveX;
//			CameLog.setLog(TAG, widhtdp + "");
//		}
//		/** 画面端のチェック：Y軸 */
//		if (petCurrentY < 0 || (higthdp * 5) - petHeight < petCurrentY) {
//			petMoveY = -petMoveY;
//			CameLog.setLog(TAG, higthdp + "");
//		}

		/** 描画座標の更新 */
		petCurrentX += petMoveX; // 移動ベクトル_vecが指す方向に移動させる
		petCurrentY += petMoveY;
		return true;
	}

	@Override
	public void onDraw(Canvas c) {
		c.drawBitmap(petPh, petCurrentX, petCurrentY, _paint);
	}

	/**
	 * CPGameSurfaceViewから来たPetの移動量をセットする。
	 */
	public static void setPetMoveSize(float x, float y) {
		petMoveX = (int) (x / 100);
		petMoveY = (int) (y / 100);
		CameLog.setLog(TAG, "onTouchEvent");
	}

	/**
	 * Petが動いているviewの幅を取得するゲッター。
	 * 
	 * @return viewWidth
	 */
	public int getViewWidth() {
		return viewWidth;
	}

	/**
	 * Petが動いているviewの高さを取得するゲッター。
	 * 
	 * @return viewHeight
	 */
	public int getViewHeight() {
		return viewHeight;
	}

	// /**
	// * @param petMoveX セットする petMoveX
	// */
	// public void setPetMoveX(int petMoveX) {
	// this.petMoveX = petMoveX;
	// }
	//
	// /**
	// * @param petMoveY セットする petMoveY
	// */
	// public void setPetMoveY(int petMoveY) {
	// this.petMoveY = petMoveY;
	// }
}
