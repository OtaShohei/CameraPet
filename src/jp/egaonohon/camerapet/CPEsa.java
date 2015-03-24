package jp.egaonohon.camerapet;

import java.util.ArrayList;

import android.R.integer;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;

public class CPEsa extends CPTask {
	/** 描画設定 */
	private Paint _paint = new Paint();
	/**
	 * Petの画像拡大縮小用 拡大縮小に関しては、こちらのサイトを参照。
	 *
	 * http://blog.livedoor.jp/tmtlplus/archives/19016162.html
	 *
	 */
	final Matrix matrix = new Matrix();

	/** Viewの幅 */
	private int viewWidth;
	/** Viewの高さ */
	private int viewHeight;

	/** 餌画像1枚 */
	private Bitmap esaPh;
	/** 餌画像複数枚 */
	private ArrayList<Bitmap> esaPhList;
	private int petPhOriginHeight;
	/** PetPhのOriginalの幅とViweのサイズから割り出した理想の幅からX方向の拡大縮小率算出 */
	float scaleX = 1.0f;
	/** PetPhのOriginalの高さとViweのサイズから割り出した理想の高さからY方向の拡大縮小率算出 */
	float scaleY = 1.0f;
	/** PetPhの回転角度 */
	private float degree = 1.0f;
	/** 餌の数 */
	int esaCnt = 1;
	/** 餌の幅 */
	private int esaWidth = 10;
	/** 餌の高さ */
	private int esaHeight = 10;

	/** 自機の移動ベクトル */
	private CPVec _vec = new CPVec();
	/** 餌初期位置：X軸。esaの個数分用意。まずは、8個分 */
	private int esaDefaultX, esaDefaultX1, esaDefaultX2, esaDefaultX3,
			esaDefaultX4, esaDefaultX5, esaDefaultX6, esaDefaultX7,
			esaDefaultX8 = 10;

	/** 餌初期位置：Y軸。esaの個数分用意。まずは、8個分 */
	private int esaDefaultY, esaDefaultY1, esaDefaultY2, esaDefaultY3,
			esaDefaultY4, esaDefaultY5, esaDefaultY6, esaDefaultY7,
			esaDefaultY8;

	/** 餌現在位置：X軸。esaの個数分用意。まずは、8個分 */
	private int esaCurrentX = 10;
	/** 餌現在距離：Y軸。esaの個数分用意。まずは、8個分 */
	private int esaCurrentY;

	/** 衝突判定用のRect。esaの個数分用意 */
	private RectF esaRect, esaRect1, esaRect2, esaRect3, esaRect4, esaRect5,
			esaRect6, esaRect7, esaRect8, esaRect9, esaRect10, esaRect11,
			esaRect12, esaRect13, esaRect14, esaRect15, esaRect16, esaRect17,
			esaRect18, esaRect19, esaRect20;

	/** Game開始時刻 **/
	long gameStartTime;
	/** Logのタグを定数で確保 */
	private static final String TAG = "CPEsa";

	// /** 複数写真使用のEsaコンストラクタ。 */
	// public CPEsa(ArrayList<Bitmap> esaPhList, int viewWidth, int viewHidth) {
	// for (int i = 0; i < esaPhList.size(); i++) {
	// this.esaPh = esaPhList.get(i);
	// /** 餌の幅はViewの幅の1/8 */
	// this.esaWidth = viewWidth / 8;
	// /** ペットの高さはViewの幅の1/8 */
	// this.esaHeight = viewWidth / 8;
	// /** 出現位置をX方向に10dpごとずらす */
	// this.esaCurrentX = 5 * (i * 5);
	// this.esaCurrentY = 0;
	// }
	// this.esaPhList = esaPhList;
	// _vec._y = 2; // 移動ベクトルを下に向ける
	// }

	/**
	 * CPEsaコンストラクタ。
	 * 
	 * @param esaPh
	 *            餌PH
	 * @param viewWidth
	 *            餌が動くViewの幅
	 * @param viewHeight
	 *            餌が動くViewの高さ
	 * @param esaNum
	 *            餌の数
	 */
	public CPEsa(Bitmap esaPh, int viewWidth, int viewHeight, int esaDefaultX,
			int esaDefaultY) {
		super();
		this.esaPh = esaPh;
		this.viewWidth = viewWidth;
		this.viewHeight = viewHeight;
		// this.esaCnt = esaNum;

		/** 餌の幅はViewの幅の1/8 */
		this.esaWidth = viewWidth / 8;
		/** ペットの高さはViewの幅の1/8 */
		this.esaHeight = viewWidth / 8;

		CameLog.setLog(TAG, "CPEsaがnewされた時点でのViewのWidthは" + viewWidth
				+ "。ViewのHeightは" + viewHeight);

		/** テスト用に一度scaleを設定しなおしてみる。 */
		scaleX = 1.0f;
		scaleY = 1.0f;

		/** EsaPhの拡大・縮小率設定 */
		matrix.postScale(scaleX, scaleY);
		// /** PetPhの拡大縮小率確認 */
		// CameLog.setLog(TAG, "PetPhの拡大縮小率はXが" + scaleX + "。Yは" + scaleY);

		/** EsaPhの回転角設定 */
		matrix.postRotate(degree);

		// for (int i = 1; i < esaNum; i++) {
		/** 初期出現位置X */
		this.esaDefaultX = (int) ((esaDefaultX * (Math.random() * 10)) * 90);
		/** 初期出現位置Y */
		this.esaDefaultY = 0;

		_vec._y = (float) (1 * (Math.random() * 10)); // 移動ベクトルを下に向ける

		/** 衝突判定用Rectをインスタンス化 */
		esaRect = new RectF();
		// switch (i) {
		// case 1:
		// esaRect1 = new RectF();
		// break;
		// case 2:
		// esaRect2 = new RectF();
		// break;
		// case 3:
		// esaRect3 = new RectF();
		// break;
		// case 4:
		// esaRect4 = new RectF();
		// break;
		// case 5:
		// esaRect5 = new RectF();
		// break;
		// case 6:
		// esaRect6 = new RectF();
		// break;
		// case 7:
		// esaRect7 = new RectF();
		// break;
		// case 8:
		// esaRect8 = new RectF();
		// case 9:
		// esaRect9 = new RectF();
		// break;
		// case 10:
		// esaRect10 = new RectF();
		// break;
		// case 11:
		// esaRect11 = new RectF();
		// break;
		// case 12:
		// esaRect12 = new RectF();
		// break;
		// case 13:
		// esaRect13 = new RectF();
		// break;
		// case 14:
		// esaRect14 = new RectF();
		// break;
		// case 15:
		// esaRect15 = new RectF();
		// break;
		// case 16:
		// esaRect16 = new RectF();
		// break;
		// case 17:
		// esaRect17 = new RectF();
		// break;
		// case 18:
		// esaRect18 = new RectF();
		// break;
		// case 19:
		// esaRect19 = new RectF();
		// break;
		// case 20:
		// esaRect20 = new RectF();
		// break;
		// default:
		// break;
		// }
		// }
		// CamPePh cpp = new CamPePh();
		// esaPh = cpp.getOne(context);// esaPhを取得する。写真を1枚ずつ取得するメソッドを新たに作りたい。
		// _paint.setColor(Color.BLUE); // 色を青に設定
		// _paint.setAntiAlias(true); // エイリアスをオン

	}

	@Override
	public boolean onUpdate() {
		/** 画面端のチェック：X軸 */
		if (esaCurrentX < 0 || this.viewWidth - esaWidth < esaCurrentX) {
			_vec._x = -_vec._x;
		}

		/** 画面端のチェック：Y軸 */
		if (esaCurrentY < 0 || this.viewHeight - esaHeight < esaCurrentY) {
			_vec._y = -_vec._y;
		}

		/** 移動ベクトル_vecが指す方向に移動させる */
		esaCurrentX = (int) (esaDefaultX + _vec._x);
		esaCurrentY = (int) (esaCurrentY + _vec._y);

		// esaCurrentX += _vec._x;
		// esaCurrentY += _vec._y;

		/** esaPhの位置と拡大縮小率確認 */
		CameLog.setLog(TAG, "esaNewCurrentXは" + esaCurrentX + "。viewWidthは"
				+ viewWidth + "。esaWidthは" + esaWidth + "。esaNewCurrentYは"
				+ esaCurrentY + "。viewHeightは" + viewHeight + "。esaHeightは"
				+ esaHeight);

		/**
		 * 描画座標の更新。表示する座標を設定する。移動ベクトル_vecが指す方向に移動させる petCurrentX += petMoveX
		 * petCurrentY += petMoveY とするとペットが一瞬で消えてしまう。
		 * 
		 * ループで囲んで使う場合は積算で処理される為　２ループ目は 回転　⇒　移動　⇒　回転　⇒　移動　...
		 * と回転と移動を混在して積算していくので、予想外の動きをします。
		 * ただし、これは移動や回転を相対値で指定＆画像表示のループで使用した場合です。
		 * 回転値、移動値　を絶対値、絶対座標　として　別変数で保持計算し
		 * 、回転・移動値を設定する前に.set○○　か、.reset　メソッドといっしょに設定するとうまく動きます。
		 * 
		 * http://javadroid.blog.fc2.com/blog-entry-83.html
		 * */
		matrix.reset();
		matrix.postTranslate(esaCurrentX, esaCurrentY);

		// /**
		// * 衝突判定用Rectの更新
		// * 例外発生につき一旦コメントアウト
		// */
		// esaRect.set(esaCurrentX, esaCurrentY, esaCurrentX + esaWidth,
		// esaCurrentY + esaHeight);

		return true;
	}

	@Override
	public void onDraw(Canvas c) {
		c.drawBitmap(esaPh, matrix, _paint);
	}

}