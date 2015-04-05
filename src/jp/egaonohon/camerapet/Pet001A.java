package jp.egaonohon.camerapet;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.view.View;

/**
 * ゲーム画面で動くペットレベル1Aのクラス。
 *
 * @author OtaShohei
 *
 */
public class Pet001A extends AbstractPet implements Runnable {

	/** ペットが存在しているView */
	private View petView;

	/** 描画設定 */
	private Paint petPaint = new Paint();
	/**
	 * Petの画像拡大縮小用 拡大縮小に関しては、こちらのサイトを参照。
	 * http://blog.livedoor.jp/tmtlplus/archives/19016162.html
	 */
	final Matrix matrix = new Matrix();

	/** ペットの型番 */
	private static final String MODEL_NUMBER = "Pet001A";

	/** ペットの種名 */
	private String petName;

	/** Viewの幅 */
	private int viewWidth;
	/** Viewの高さ */
	private int viewHeight;

	/** 実行中ペット画像 */
	private Bitmap itemPh;
	/** ペット右向き画像 */
	private Bitmap petPhR;
	/** ペット左向き画像 */
	private Bitmap petPhL;

	/** X方向の拡大縮小率算出 */
	float scaleX = 1.0f;
	/** Y方向の拡大縮小率算出 */
	float scaleY = 1.0f;
	/** Itemの回転角度 */
	private float degree = 1.0f;
	/** Itemの幅 */
	private int itemWidth;
	/** Itemの高さ */
	private int itemHeight;
	/** Item初期位置：X軸 */
	private int defaultX;
	/** Item初期位置：Y軸 */
	private int defaultY;
	/** Item現在位置：X軸 */
	private int nowX = 0;
	/** Item現在位置：Y軸 */
	private int nowY = 0;
	/** Item移動距離：X軸 */
	private int moveX = 1;
	/** Item移動距離：Y軸 */
	private int moveY = 2;
	/** ペットの歩くアニメーション効果用（歩数カウント） */
	private int cnt;
	/** ペットが動くスピード（移動および歩くアニメーションに影響） */
	private long speed = 60;
	/** ペット移動加速度X軸 */
	private int petKasokudoX = viewWidth / 7;
	/** ペット移動加速度Y軸 */
	private int petKasokudoY = viewWidth / 6;

	/** 現在の吹き出し */
	private simpleFukidasi nowFukidasi;
	/** 吹き出し座布団画像の参照 */
	private Bitmap fukidasiPh;
	/** 吹き出しセリフ文字の参照 */
	private String fukidasiTxt;
	/** 吹き出し改行基準スケール */
	private int layoutScale = viewWidth / 128;
	/** テキストの改行に必要なローカル変数 */
	private int lineBreakPoint;
	private int currentIndex = 0;
	/** 本文開始位置のY値 */
	private int linePointY;

	/** ペット用のスレッド */
	private Thread petThread;
	// /** 衝突判定用のRectF */
	// private static RectF rectF;
	/** Logのタグを定数で確保 */
	private static final String TAG = "Pet001A";

	/**
	 * スーパークラスからのコンストラクタ。
	 *
	 * @param itemPh
	 * @param width
	 * @param height
	 * @param defaultX
	 * @param defaultY
	 */
	public Pet001A(Bitmap itemPh, int width, int height, int defaultX,
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
	public Pet001A(View view, Bitmap petPhR, Bitmap petPhL, int itemWidth,
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
		petName = res.getString(R.string.pet_01_name);

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

		CameLog.setLog(TAG, "scaleXは" + scaleX + "scaleYは" + scaleY);

		matrix.setScale(scaleX, scaleY);
		// matrix.postScale(scaleX, scaleY);
		// /** PetPhの拡大縮小率確認 */
		// CameLog.setLog(TAG, "PetPhの拡大縮小率はXが" + scaleX + "。Yは" + scaleY);

		/** PetPhの回転角設定 */
		// matrix.postRotate(degree);

		petThread = new Thread(this);
		petThread.start();
	}

	/**
	 * GameSurfaceViewでのオンタッチイベントでやって来たPetの移動量をセットする。
	 * GameSurfaceView側での呼び出しがもちろん必要。
	 */
	public void setPetMoveSize(float x, float y) {
		moveX = (int) (x / (viewWidth / 7));
		moveY = (int) (y / (viewWidth / 5));
		// CameLog.setLog(TAG, "onTouchEventからペットに移動距離を設定");
	}

	/** エサと衝突したらPetを反転させるメソッド。GameSurfaceViewから呼び出す */
	public void returnEsaKrush() {
		moveX = 1;
		moveY = 0;
		if (itemPh.equals(petPhR)) {
			itemPh = petPhL;
		} else if (itemPh.equals(petPhL)) {
			itemPh = petPhR;
		}
		// moveX *= -1;
		// moveY *= -1;
	}

	/**
	 * Petの移動処理を司るメソッド。
	 */
	@Override
	public void move() {
		/** 歩数を数える */
		cnt++;

		/** 画面端のチェック：X軸。 */
		if (nowX < 0 || this.viewWidth - itemWidth < nowX) {
			if (itemPh.equals(petPhR)) {
				itemPh = petPhL;
			} else if (itemPh.equals(petPhL)) {
				itemPh = petPhR;
			}

			/** 次の行のようにすると、左端に行った時も左へ向かってしまう */
			// moveX = -moveX;
			moveX *= -1;
		}

		/** 画面端のチェック：Y軸。(itemHeight/2)は、ペットの体の半分がはみ出ていたのでその調整 */
		if (nowY < 0 || this.viewHeight - itemHeight < nowY) {

			/** 次の行はそもそもXとYが食い違っているので動いているようだが妙な動きになってしまう。かつ、上下判定も変なことに */
			// moveY = -moveX;
			moveY *= -1;
			// CameLog.setLog(TAG, "ペット移動範囲のwidthは" + viewWidth
			// + "ペット移動範囲のheightは" + viewHeight);
		}

		/** 移動させる */
		nowX = nowX + moveX;
		nowY = nowY + moveY;

		// CameLog.setLog(TAG, "ペットのnowXは" + nowX
		// + "。ペットのnowYは" + nowY);

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
		// matrix.reset();
		// matrix.setTranslate(nowX, nowY);

		// /** matrixが存在しているか否かを確認 */
		// CameLog.setLog(TAG, "matrixのハッシュ値は" + matrix.hashCode());

		/** 衝突判定用RectFの更新 */
		rectF.set(nowX, nowY, nowX + itemWidth, nowY + itemHeight);

		// /** 衝突判定用RectFにセットした数値の確認 */
		// CameLog.setLog(TAG, "更新されたnowXは" + nowX + "。nowYは" + nowY
		// + "。nowX + itemWidthは" + (nowX + itemWidth)
		// + "。nowY + itemHeightは" + (nowY + itemHeight));
	}

	@Override
	public void draw(Canvas canvas) {
		/** Canvasの背景色を透明で塗る。 第二引数にPorterDuff.Mode.CLEARを付けないと透明にならないので注意。 */
		canvas.drawColor(Color.TRANSPARENT,
				android.graphics.PorterDuff.Mode.CLEAR);
		canvas.save();
		/** ここで描画位置を指定 */
		canvas.translate(nowX, nowY);

		if (nowFukidasi.isVisible) {

			canvas.drawBitmap(fukidasiPh, 0, itemHeight + (layoutScale * 4),
					petPaint);
			// canvas.drawText(fukidasiTxt, 0, itemHeight + (layoutScale * 20),
			// petPaint);

			int maxWidth = itemHeight;// 150pxで改行する。
			int lineBreakPoint = Integer.MAX_VALUE;// 仮に、最大値を入れておく
			int currentIndex = 0;// 現在、原文の何文字目まで改行が入るか確認したかを保持する
			int linePointY = (viewWidth / 22);// 文字を描画するY位置。改行の度にインクリメントする。

			while (lineBreakPoint != 0) {
				String mesureString = fukidasiTxt.substring(currentIndex);
				lineBreakPoint = petPaint.breakText(mesureString, true,
						maxWidth, null);
				if (lineBreakPoint != 0) {
					String line = fukidasiTxt.substring(currentIndex,
							currentIndex + lineBreakPoint);
					canvas.drawText(line, (layoutScale * 7),
							((itemHeight + (layoutScale * 16)) + linePointY),
							petPaint);
					linePointY = linePointY + (viewWidth / 22);
					currentIndex += lineBreakPoint;
				}
			}
		}

		canvas.drawBitmap(itemPh, matrix, petPaint);

		canvas.restore();
	}

	@Override
	public void run() {
		while (petThread != null) {
			// if (nowFukidasi.th == null) {
			// nowFukidasi =null;
			// }
			move();
			try {
				Thread.sleep(1000 / speed);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	/** ペットの喋らせるメソッド。 */
	public void talk(View view, int eventCode
	// Context context, View view, int eventCode,
	// int fukidasiDefaultX, int fukidasiDefaultY
	) {

		nowFukidasi = new simpleFukidasi();

		if (!nowFukidasi.isVisible) {
			CameLog.setLog(TAG, "nowFukidasi.isVisibleは"
					+ nowFukidasi.isVisible);
			nowFukidasi.isVisible = true;
			CameLog.setLog(TAG, "nowFukidasi.isVisibleを"
					+ nowFukidasi.isVisible + "にセット");
		}
		if (nowFukidasi.th == null) {
			nowFukidasi.th.start();
			CameLog.setLog(TAG, "nowFukidasi.thをStart");
		}

		fukidasiTxt = nowFukidasi.getMsg(view, eventCode);
		/** fukidasiTxtの確認 */
		CameLog.setLog(TAG, "fukidasiTxtは" + fukidasiTxt);

		/** 吹き出し座布団画像取得 */
		fukidasiPh = BitmapFactory.decodeResource(view.getContext()
				.getResources(), R.drawable.fukidasi);

		/** 吹き出し用のペイントを準備 */
		petPaint = new Paint();

		/** 文字色にダークグレーを設定 */
		petPaint.setColor(Color.argb(255, 51, 51, 51));

		/** テキスト左寄せに変更 */
		petPaint.setTextAlign(Paint.Align.LEFT);

		/** 吹き出し改行基準スケールをここで定義 */
		layoutScale = viewWidth / 128;

		/** ペット解説用テキストサイズと書体を変更 */
		petPaint.setTextSize(viewWidth / 26);

		/** テキストの改行に必要なローカル変数 */
		lineBreakPoint = Integer.MAX_VALUE;
		currentIndex = 0;

		/** 本文開始位置のY値 */
		linePointY = viewWidth / 60;

	}

	// public void talkStop() {
	// nowFukidasi = null;
	// }

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
		return MODEL_NUMBER;
	}

	/**
	 * @return petName
	 */
	public String getPetName() {
		return petName;
	}
}
