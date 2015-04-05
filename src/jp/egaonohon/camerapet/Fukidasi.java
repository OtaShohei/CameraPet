package jp.egaonohon.camerapet;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;

/**
 * ゲーム画面に時折出現する吹き出しのクラス。
 *
 * @author OtaShohei
 *
 */
public class Fukidasi extends CamPeItem implements Runnable {

	/** 描画設定 */
	private Paint fukidasiPhPaint = new Paint();
	/**
	 * Petの画像拡大縮小用 拡大縮小に関しては、こちらのサイトを参照。
	 * http://blog.livedoor.jp/tmtlplus/archives/19016162.html
	 */
	final Matrix matrix = new Matrix();

	/** Viewの幅 */
	private int viewWidth;
	/** Viewの高さ */
	private int viewHeight;

	/** 吹き出し画像 */
	private Bitmap itemPh;

	/** 吹き出しセリフ文字 */
	private String fukidasiTxt;
	/** 吹き出しセリフ用のペイント */
	private Paint fukidasiMojiPaint;

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
	private int moveY = 1;

	/** 吹き出しが動くアニメーション効果用（歩数カウント） */
	private int cnt;
	/** 吹き出しが動くスピード（ペットの移動速度と同じくする） */
	private long speed = 60;
	/** 吹き出し移動加速度X軸 */
	private int petKasokudoX = viewWidth / 7;
	/** 吹き出し移動加速度Y軸 */
	private int petKasokudoY = viewWidth / 6;

	/** 吹き出し改行基準スケール */
	private int layoutScale = viewWidth/128;
	/** テキストの改行に必要なローカル変数 */
	private int lineBreakPoint;
	private int currentIndex = 0;
	/** 本文開始位置のY値 */
	private int linePointY;
	/** 吹き出し文字色のテーマカラーダークグレー(K80相当) */
	private static int txtColorDarkgray = Color.argb(255, 51, 51, 51);

	/** 吹き出し出現開始時間 */
	private long fukidasiStartTime;
	/** 現在時間 */
	private long fukidasiNowTime;
	/** 吹き出し終了時間 */
	private long fukidasiEndTime;
	/** 吹き出し削除可否判定用 */
	private boolean deleteOk = false;

	/** 吹き出し用のスレッド */
	private Thread fukidasiThread;
	// /** 衝突判定用のRectF */
	// private static RectF rectF;
	/** Logのタグを定数で確保 */
	private static final String TAG = "Fukidasi";

	/**
	 * 吹き出しのコンストラクタ。画像一枚バージョン。
	 * @param itemPh
	 * @param itemWidth
	 * @param itemHeight
	 * @param defaultX
	 * @param defaultY
	 * @param viewWidth
	 * @param viewHeight
	 * @param fukidasiTxt
	 */
	public Fukidasi(Bitmap itemPh, int itemWidth, int itemHeight, int defaultX,
			int defaultY, int viewWidth, int viewHeight, String fukidasiTxt) {
		super(itemWidth, itemHeight, defaultX, defaultY, viewWidth, viewHeight);

		this.itemPh = itemPh;
		this.itemWidth = itemWidth;
		this.itemHeight = itemHeight;
		/** Defaultの位置を現在地に代入する */
		this.nowX = defaultX;
		this.nowY = defaultY;
		this.viewWidth = viewWidth;
		this.viewHeight = viewHeight;
		this.fukidasiTxt = fukidasiTxt;

		init();
	}

	/**
	 * 吹き出しのコンストラクタ。このコンストラクタは左右画像が必要です。
	 * @param itemPhR
	 * @param itemPhL
	 * @param itemWidth
	 * @param itemHeight
	 * @param defaultX
	 * @param defaultY
	 * @param viewWidth
	 * @param viewHeight
	 */
	public Fukidasi(Bitmap itemPhR, Bitmap itemPhL, int itemWidth, int itemHeight,
			int defaultX, int defaultY, int viewWidth, int viewHeight) {
		super(itemWidth, itemHeight, defaultX, defaultY, viewWidth,
				viewHeight);
		CameLog.setLog(TAG, "吹き出しの画像は" + itemPhR + "吹き出しのWidthは" + itemWidth);

		this.itemPh = itemPhR;
		this.itemWidth = itemWidth;
		this.itemHeight = itemHeight;
		/** Defaultの位置を現在地に代入する */
		this.nowX = defaultX;
		this.nowY = defaultY;
		this.viewWidth = viewWidth;
		this.viewHeight = viewHeight;

		init();
	}

	public void init() {
//		/** 衝突判定用RectFをインスタンス化 */
//		rectF = new RectF();

		CameLog.setLog(TAG, "Fukidasiがnewされた時点でのViewのWidthは" + this.viewWidth
				+ "。ViewのHeightは" + this.viewHeight);

		CameLog.setLog(TAG, "Fukidasiがnewされた時点での吹き出しのWidthは" + this.itemWidth
				+ "。Heightは" + this.itemHeight);

		CameLog.setLog(TAG, "Fukidasiがnewされた時点での吹き出しのnowXは" + this.nowX + "。nowYは" + this.nowY
				+ "。Heightは" + this.itemHeight);

		/**
		 * FukidasiPhの拡大・縮小率設定 ここでfloatにキャストしないと拡大率が小数点以下切り捨てられてしまうので要注意
		 */
		scaleX = (float) itemWidth / itemPh.getWidth();
		scaleY = (float) itemHeight / itemPh.getHeight();

		CameLog.setLog(TAG, "scaleXは" + scaleX + "scaleYは" + scaleY);

		matrix.setScale(scaleX, scaleY);
		// matrix.postScale(scaleX, scaleY);
		// /** PetPhの拡大縮小率確認 */
		// CameLog.setLog(TAG, "PetPhの拡大縮小率はXが" + scaleX + "。Yは" + scaleY);

		/** 吹き出しの回転角設定 */
		// matrix.postRotate(degree);

		/** 吹き出し文字の描画準備を行う */
		prepareMakeText();
		
		/** 吹き出し出現時間を保存 */
		fukidasiStartTime = System.currentTimeMillis();
		CameLog.setLog(TAG, "吹き出し出現時間を" + fukidasiStartTime + "に設定");
		/** 吹き出し終了時間を算出 */
		fukidasiEndTime = fukidasiStartTime + 5000;
		CameLog.setLog(TAG, "吹き出し終了時間を" + fukidasiEndTime + "に設定");

		fukidasiThread = new Thread(this);
		fukidasiThread.start();
	}

	
	/**
	 * GameSurfaceViewでのオンタッチイベントでやって来たfukidasiの移動量をセットする。
	 * GameSurfaceView側での呼び出しがもちろん必要。
	 */
	public void setMoveSize(float x, float y) {
		moveX = (int) (x / (viewWidth / 7));
		moveY = (int) (y / (viewWidth / 5));
		// CameLog.setLog(TAG, "onTouchEventからペットに移動距離を設定");
	}

//	/** エサと衝突したらFukidasiを反転させるメソッド。GameSurfaceViewから呼び出す */
//	public void returnEsaKrush() {
//		moveX = 1;
//		moveY = 0;
//		if (itemPh.equals(petPhR)) {
//			itemPh = petPhL;
//		} else if (itemPh.equals(petPhL)) {
//			itemPh = petPhR;
//		}
//		// moveX *= -1;
//		// moveY *= -1;
//	}

	/**
	 * 吹き出しの移動処理を司るメソッド。
	 */
	@Override
	public void move() {
		/** 移動量を数える */
		cnt++;

		/** 5秒経過が確認されたらBoolean値をtrueに変更 */
		fukidasiNowTime = System.currentTimeMillis();
		if (fukidasiNowTime == fukidasiEndTime) {
			deleteOk = true;
			CameLog.setLog(TAG, "吹き出し出現から5秒経過を確認。deleteOkを" + deleteOk + "に変更");
		}


		/** 画面端のチェック：X軸。 */
		if (nowX < 0 || this.viewWidth - itemWidth < nowX) {
////			if (itemPh.equals(petPhR)) {
////				itemPh = petPhL;
////			} else if (itemPh.equals(petPhL)) {
////				itemPh = petPhR;
////			}

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

		CameLog.setLog(TAG, "吹き出しのnowXは" + this.nowX + "。nowYは" + this.nowY);

		// CameLog.setLog(TAG, "move時点でのViewのWidthは" + this.viewWidth
		// + "。ViewのHeightは" + this.viewHeight);

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

//		/** 衝突判定用RectFの更新 */
//		rectF.set(nowX, nowY, nowX + itemWidth, nowY + itemHeight);

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
		canvas.drawBitmap(itemPh, matrix, fukidasiPhPaint);

		/** テキストの改行を行いつつ文字表示 */
		while (lineBreakPoint != 0) {
			String mesureString = fukidasiTxt.substring(currentIndex);
			lineBreakPoint = fukidasiMojiPaint.breakText(mesureString, true,
					(layoutScale * 86), null);
			if (lineBreakPoint != 0) {
				String line = fukidasiTxt.substring(currentIndex,
						currentIndex + lineBreakPoint);
				/**　第2引数がセリフ描画の左端  */
				canvas.drawText(line, nowX - layoutScale, linePointY, fukidasiMojiPaint);
				linePointY += (viewWidth / 20);
				currentIndex += lineBreakPoint;
			}
		}


		canvas.restore();
	}

	/** 吹き出し文字の描画準備を行う */
	public void prepareMakeText() {

		/** 吹き出し用のペイントを準備 */
		fukidasiMojiPaint = new Paint();

		/** 文字色にダークグレーを設定 */
		fukidasiMojiPaint.setColor(Color.argb(255, 51, 51, 51));

		/** テキスト左寄せに変更 */
		fukidasiMojiPaint.setTextAlign(Paint.Align.LEFT);

		/** 吹き出し改行基準スケールをここで定義 */
		layoutScale = viewWidth/128;

		/** ペット解説用テキストサイズと書体を変更 */
		fukidasiMojiPaint.setTextSize(viewWidth / 28);

		/** テキストの改行に必要なローカル変数 */
		lineBreakPoint = Integer.MAX_VALUE;
		currentIndex = 0;

		/** 本文開始位置のY値 */
		linePointY = nowY + (viewWidth/60);
	}

	@Override
	public void run() {
		while (fukidasiThread != null) {
			move();
			try {
				Thread.sleep(1000 / speed);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

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

	public boolean isDeleteOk() {
		return deleteOk;
	}

}
