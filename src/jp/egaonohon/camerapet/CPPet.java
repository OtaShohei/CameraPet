package jp.egaonohon.camerapet;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;

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

	/** Petの幅 */
	private int petWidth = 50;
	/** Petの高さ */
	private int petHeight = 50;

	/** 実行中ペット画像 */
	private Bitmap petPh;
	/** ペット元画像右向き */
	private Bitmap petPhR;
	/** ペット元画像左向き */
	private Bitmap petPhL;

	/** ペット画像のwidth初期値　 */
	private int petPhOriginWidth;
	/** ペット画像のHeigth初期値　 */
	private int petPhOriginHeight;
	/** PetPhのOriginalの幅とViweのサイズから割り出した理想の幅からX方向の拡大縮小率算出 */
	float scaleX = 1.0f;
	/** PetPhのOriginalの高さとViweのサイズから割り出した理想の高さからY方向の拡大縮小率算出 */
	float scaleY = 1.0f;
	/** PetPhの回転角度 */
	private float degree = 1.0f;

	/** Pet初期位置：X軸 eclipseの警告と異なり、使用しています。 */
	private int petDefaultX;
	/** Pet初期位置：Y軸 eclipseの警告と異なり、使用しています。 */
	private int petDefaultY;
	/** Pet移動距離：X軸 */
	private static int petMoveX = 3;
	/** Pet移動距離：Y軸 */
	private static int petMoveY = 5;
	/** Pet現在位置：X軸 */
	private int petCurrentX;
	/** Pet現在距離：Y軸 */
	private int petCurrentY;

	/** ペットの移動ベクトル */
	private CPVec _vec = new CPVec();
	/** 衝突判定用のRect */
	private RectF petRect;

	/** Logのタグを定数で確保 */
	private static final String TAG = "CPPet";

	/**
	 * ペットのコンストラクタ。
	 *
	 * @param bitmapR
	 *            ペット画像右向き
	 * @param viewWidth
	 *            ペットが存在するviewの幅
	 * @param viewHeight
	 *            ペットが存在するviewの高さ
	 */
	public CPPet(Bitmap bitmapR, Bitmap bitmapL, int viewWidth, int viewHeight) {
		super();

		this.petPh = bitmapR;
		this.petPhR = bitmapR;
		this.petPhL = bitmapL;
		this.viewWidth = viewWidth;
		this.viewHeight = viewHeight;

		CameLog.setLog(TAG, "CPPetがnewされた時点でのViewのWidthは" + this.viewWidth
				+ "。ViewのHeightは" + this.viewHeight);

		/** ペット画像のwidth初期値　 */
		petPhOriginWidth = petPh.getWidth();
		/** ペット画像のHeigth初期値　 */
		petPhOriginHeight = petPh.getHeight();
		/** PetPhのwidth初期値・Heigth初期値確認 */
		CameLog.setLog(TAG, "PetPhのwidth初期値は" + petPhOriginWidth
				+ "。Heigth初期値は" + petPhOriginHeight);

		/** ペットの幅はViewの幅の1/5 */
		this.petWidth = this.viewWidth / 5;
		/** ペットの高さはViewの幅の1/5 */
		this.petHeight = this.viewWidth / 5;

		CameLog.setLog(TAG, "CPPetがnewされた時点でのペットのWidthは" + this.petWidth
				+ "。Heightは" + this.petHeight);

		// /** PetPhのOriginalの幅とViweのサイズから割り出した理想の幅からX方向の拡大縮小率算出 */
		// scaleX = (petHeight / petPhOriginHeight);
		// /**
		// PetPhのOriginalの高さとViweのサイズから割り出した理想の高さからY方向の拡大縮小率算出。画像が正方形なのでxもyも同率
		// */
		// scaleY = (petHeight / petPhOriginHeight);

		/** テスト用に一度scaleを設定しなおしてみる。 */
		scaleX = 1.0f;
		scaleY = 1.0f;

		/** PetPhの拡大・縮小率設定 */
		matrix.postScale(scaleX, scaleY);
		// /** PetPhの拡大縮小率確認 */
		// CameLog.setLog(TAG, "PetPhの拡大縮小率はXが" + scaleX + "。Yは" + scaleY);

		/** PetPhの回転角設定 */
		matrix.postRotate(degree);

		/** 出現位置 */
		this.petDefaultX = 0;
		this.petDefaultY = 0;
		_vec._x = 3; // 移動ベクトルを下に向ける
		_vec._y = 5; // 移動ベクトルを下に向ける

		/** 衝突判定用Rectをインスタンス化 */
		petRect = new RectF();
	}

	/**
	 * PetAsobiBaから移動してきたペット移動処理メソッド。
	 */
	@Override
	public boolean onUpdate() {
		/**
		 * Pet移動処理
		 */
		// /**ディスプレイ情報を取得。1ピクセル×scaledDensity＝1dipとなります。 */
		// DisplayMetrics metrics = new DisplayMetrics();
		// // density (比率)を取得する
		// float density = metrics.scaledDensity;
		// // 縦のpx を dp に変換する ( pixel ÷ density + 0.5f（四捨五入) )
		// int higthdp = (int) (metrics.heightPixels / density + 0.5f);
		//
		// // 横の px を dp に変換する ( pixel ÷ density + 0.5f（四捨五入) )
		// int widhtdp = (int) (metrics.widthPixels / density + 0.5f);

		/** 画面端のチェック：X軸 */
		if (petCurrentX < 0 || this.viewWidth - petWidth < petCurrentX) {
			if (petPh.equals(petPhR)) {
				petPh = petPhL;
			}
			petMoveX = -petMoveX;
		}

		/** 画面端のチェック：Y軸 */
		if (petCurrentY < 0 || this.viewHeight - petHeight < petCurrentY) {
			if (petPh.equals(petPhL)) {
				petPh = petPhR;
			}
			petMoveY = -petMoveY;
		}

		// /** PetPhの位置と拡大縮小率確認 */
		// CameLog.setLog(TAG, "petNewCurrentXは" + petCurrentX + "。viewWidthは"
		// +viewWidth + "。petWidthは" + petWidth + "。petNewCurrentYは" +
		// petCurrentY + "。viewHeightは" + viewHeight + "。petHeightは" + petHeight
		// );

		// // 画面端のチェック：X軸
		// if(currentX < 0 || getWidth() - image.getWidth() < currentX) {
		// moveX = -moveX;
		// }
		// // 画面端のチェック：Y軸
		// if(currentY < 0 || getHeight() - image.getHeight() < currentY) {
		// moveY = -moveY;

		// /** 画面端のチェック：X軸 */
		// if (petCurrentX < 0 || (widhtdp *5) - petWidth < petCurrentX) {
		// petMoveX = -petMoveX;
		// CameLog.setLog(TAG, widhtdp + "");
		// }
		// /** 画面端のチェック：Y軸 */
		// if (petCurrentY < 0 || (higthdp * 5) - petHeight < petCurrentY) {
		// petMoveY = -petMoveY;
		// CameLog.setLog(TAG, higthdp + "");
		// }

		petCurrentX = petCurrentX + petMoveX;
		petCurrentY = petCurrentY + petMoveY;

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
		matrix.postTranslate(petCurrentX, petCurrentY);

		// /** matrixが存在しているか否かを確認 */
		// CameLog.setLog(TAG, "matrixのハッシュ値は" + matrix.hashCode());

		/** 衝突判定用Rectの更新 */
		petRect.set(petCurrentX, petCurrentY, petCurrentX + petWidth,
				petCurrentY + petHeight);

		// /** matrixが存在しているか否かを確認 */
		// CameLog.setLog(TAG, "petの現在地はXが" + petNewCurrentX +"。Yが" +
		// petNewCurrentY );
		// /** PetPhの位置と拡大縮小率確認 */
		// CameLog.setLog(TAG, "petNewCurrentXは" + petNewCurrentX +
		// "。viewWidthは" +viewWidth + "。petWidthは" + petWidth +
		// "。petNewCurrentYは" + petNewCurrentY + "。viewHeightは" + viewHeight +
		// "。petHeightは" + petHeight );

		return true;
	}

	@Override
	public void onDraw(Canvas c) {
		/**
		 * 描画処理。 canvasはSurfaceViewで使用する描画オブジェクト。 bitmapは生成済みBitmapオブジェクト。
		 */
		c.drawBitmap(petPh, matrix, _paint);
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
	 * Petが動いているviewの幅を取得させるゲッター。
	 *
	 * @return viewWidth
	 */
	public int getViewWidth() {
		return viewWidth;
	}

	/**
	 * Petが動いているviewの高さを取得させるゲッター。
	 *
	 * @return viewHeight
	 */
	public int getViewHeight() {
		return viewHeight;
	}

	/**
	 * 衝突判定用のRectを取得させるゲッター。
	 *
	 * @return
	 */
	public RectF getrRectF() {
		return petRect;
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
