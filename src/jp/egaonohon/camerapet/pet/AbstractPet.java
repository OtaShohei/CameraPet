package jp.egaonohon.camerapet.pet;

import jp.egaonohon.camerapet.CamPeItem;
import jp.egaonohon.camerapet.CameLog;
import jp.egaonohon.camerapet.Fukidasi;
import jp.egaonohon.camerapet.R;
import jp.egaonohon.camerapet.R.drawable;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.view.View;

/**
 * ゲーム画面で動く各種ペットの抽象クラス
 *
 * @author OtaShohei
 *
 */
public abstract class AbstractPet extends CamPeItem implements Runnable {


	/** ペットが存在しているView */
	protected View petView;

	/** 描画設定 */
	protected Paint petPaint = new Paint();
	/**
	 * Petの画像拡大縮小用 拡大縮小に関しては、こちらのサイトを参照。
	 * http://blog.livedoor.jp/tmtlplus/archives/19016162.html
	 */
	protected final Matrix matrix = new Matrix();

	/** Viewの幅 */
	protected int viewWidth;
	/** Viewの高さ */
	protected int viewHeight;

	/** 実行中ペット画像 */
	protected Bitmap itemPh;
	/** ペット右向き画像 */
	protected Bitmap petPhR;
	/** ペット左向き画像 */
	protected Bitmap petPhL;

	/** 表示写真を左右入れ替えていいか否か */
	protected boolean changeItemPh;

	/** X方向の拡大縮小率算出 */
	protected float scaleX = 1.0f;
	/** Y方向の拡大縮小率算出 */
	protected float scaleY = 1.0f;
	/** Itemの回転角度 */
	protected float degree = 1.0f;
	/** Itemの幅 */
	protected int itemWidth;
	/** Itemの高さ */
	protected int itemHeight;
	/** Item初期位置：X軸 */
	protected int defaultX;
	/** Item初期位置：Y軸 */
	protected int defaultY;
	/** Item現在位置：X軸 */
	protected int nowX = 0;
	/** Item現在位置：Y軸 */
	protected int nowY = 0;
	/** Item移動距離：X軸 */
	protected int moveX = 1;
	/** Item移動距離：Y軸 */
	protected int moveY = 2;
	/** ペットの歩くアニメーション効果用（歩数カウント） */
	protected int cnt;
	/** ペットが動くスピード（移動および歩くアニメーションに影響） */
	protected long speed = 60;
	/** ペット移動加速度X軸 */
	protected int petKasokudoX = viewWidth / 7;
	/** ペット移動加速度Y軸 */
	protected int petKasokudoY = viewWidth / 6;

	/** 現在の吹き出し */
	protected Fukidasi nowFukidasi;
	/** 吹き出し座布団画像の参照 */
	protected Bitmap fukidasiPh;

	/** 吹き出しセリフ文字の参照 */
	protected String fukidasiTxt;
	/** 吹き出し改行基準スケール */
	protected int layoutScale = viewWidth / 128;
	/** テキストの改行に必要なローカル変数 */
	protected int lineBreakPoint;
	protected int currentIndex = 0;
	/** 本文開始位置のY値 */
	protected int linePointY;

	/** ペット用のスレッド */
	protected Thread petThread;
	/** ペットの種別名 */
	protected final String model_number = "AbstractPet";
	/** ペットの種名 */
	protected String petName;

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

		if (nowFukidasi.isVisible()) {

			// canvas.drawBitmap(fukidasiPh, fukidasiPhMatrix, petPaint);
			//
			canvas.drawBitmap(fukidasiPh, 0, itemHeight + (layoutScale * 4),
					petPaint);

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
			move();
			try {
				// /** userに触られた際には写真を入れ替えて震えて喜ぶ */
				// if (changeItemPh) {
				// if (itemPh.equals(petPhR)) {
				// Thread.sleep(30);
				// itemPh = petPhL;
				// Thread.sleep(30);
				// itemPh = petPhR;
				// } else if (itemPh.equals(petPhL)) {
				// Thread.sleep(30);
				// itemPh = petPhR;
				// Thread.sleep(30);
				// itemPh = petPhL;
				// }
				// changeItemPh = false;
				// }
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

		nowFukidasi = new Fukidasi();

		if (!nowFukidasi.isVisible()) {
			CameLog.setLog(TAG, "nowFukidasi.isVisibleは"
					+ nowFukidasi.isVisible());
			nowFukidasi.setVisible(true);
			CameLog.setLog(TAG, "nowFukidasi.isVisibleを"
					+ nowFukidasi.isVisible() + "にセット");
		}
		if (nowFukidasi.getTh() == null) {
			nowFukidasi.getTh().start();
			CameLog.setLog(TAG, "nowFukidasi.thをStart");
		}

		fukidasiTxt = nowFukidasi.getMsg(view, eventCode);
		/** fukidasiTxtの確認 */
		CameLog.setLog(TAG, "fukidasiTxtは" + fukidasiTxt);

		/** 吹き出し座布団画像取得 */
		fukidasiPh = BitmapFactory.decodeResource(view.getContext()
				.getResources(), R.drawable.fukidasi);

		/** 吹き出し座布団画像を端末の画面に合わせてリサイズ */
		Bitmap fukidasiPh02 = Bitmap.createScaledBitmap(fukidasiPh, ((view.getWidth()/128) * 59),
				((view.getWidth()/128) * 59), false);
		fukidasiPh = fukidasiPh02;

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

	/** 触られてペットが喜ぶメソッド */
	public abstract void pleased();

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

	public abstract String getPetModelNumber();

	/**
	 * @return petName
	 */
	public abstract String getPetName();

	/**
	 * @param itemPh
	 *            セットする itemPh
	 */
	public void setItemPh(Bitmap itemPh) {
		this.itemPh = itemPh;
	}

	/**
	 * @return petPhR
	 */
	public Bitmap getPetPhR() {
		return petPhR;
	}

	/**
	 * @return petPhL
	 */
	public Bitmap getPetPhL() {
		return petPhL;
	}
}