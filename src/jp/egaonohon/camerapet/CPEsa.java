package jp.egaonohon.camerapet;

import java.util.ArrayList;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;

public class CPEsa extends CPTask {

	/** 餌画像1枚 */
	private Bitmap esaPh;
	/** 餌画像複数枚 */
	private ArrayList<Bitmap> esaPhList;
	/** 餌の幅 */
	private int esaWidth = 10;
	/** 餌の高さ */
	private int esaHeight = 10;
	/** 描画設定 */
	private Paint _paint = new Paint();
	/** 自機の移動ベクトル */
	private CPVec _vec = new CPVec();
	/** 餌現在位置：X軸 */
	private int esaCurrentX;
	/** 餌現在位置：Y軸 */
	private int esaCurrentY;
	/** Logのタグを定数で確保 */
	private static final String TAG = "CPEsa";

//	/** 複数写真使用のEsaコンストラクタ。 */
//	public CPEsa(ArrayList<Bitmap> esaPhList, int viewWidth, int viewHidth) {
//		for (int i = 0; i < esaPhList.size(); i++) {
//			this.esaPh = esaPhList.get(i);
//			/** 餌の幅はViewの幅の1/8 */
//			this.esaWidth = viewWidth / 8;
//			/** ペットの高さはViewの幅の1/8 */
//			this.esaHeight = viewWidth / 8;
//			/** 出現位置をX方向に10dpごとずらす */
//			this.esaCurrentX = 5 * (i * 5);
//			this.esaCurrentY = 0;
//		}
//		this.esaPhList = esaPhList;
//		_vec._y = 2; // 移動ベクトルを下に向ける
//	}

	/**
	 * Esaコンストラクタ。
	 * @param esaPh			餌PH
	 * @param viewWidth		餌が動くViewの幅
	 * @param viewHeight	餌が動くViewの高さ
	 * @param esaCurrentX	餌の初期位置X
	 * @param esaCurrentY	餌の初期位置Y
	 */
	public CPEsa(Bitmap esaPh, int viewWidth, int viewHeight, int esaCurrentX, int esaCurrentY) {
		this.esaPh = esaPh;
		/** 餌の幅はViewの幅の1/8 */
		this.esaWidth = viewWidth / 8;
		/** ペットの高さはViewの幅の1/8 */
		this.esaHeight = viewWidth / 8;
		/** 出現位置 */
		this.esaCurrentX = esaCurrentX;
		this.esaCurrentY = esaCurrentY;
		// CamPePh cpp = new CamPePh();
		// esaPh = cpp.getOne(context);// esaPhを取得する。写真を1枚ずつ取得するメソッドを新たに作りたい。
		// _paint.setColor(Color.BLUE); // 色を青に設定
		// _paint.setAntiAlias(true); // エイリアスをオン
		_vec._y = 2; // 移動ベクトルを下に向ける
	}

	@Override
	public boolean onUpdate() {
		// 画面両端チェックは後ほど実装
		// // 画面端のチェック：X軸
		// if (esaCurrentX < 0
		// || getWidth() - getWidth() / 10 /* petPh.getWidth() */< esaCurrentX)
		// {
		// esaMoveX = -esaMoveX;
		// }
		// // 画面端のチェック：Y軸
		// if (esaCurrentY < 0
		// || getHeight() - getWidth() / 10 /* petPh.getWidth() */< esaCurrentY)
		// {
		// esaMoveY = -esaMoveY;
		// CameLog.setLog(TAG, "餌移動処理Y軸");
		// }

		esaCurrentX += _vec._x; // 移動ベクトル_vecが指す方向に移動させる
		esaCurrentY += _vec._y;
		return true;
	}

	@Override
	public void onDraw(Canvas c) {
		c.drawBitmap(esaPh, esaCurrentX, esaCurrentY, _paint);
	}

}