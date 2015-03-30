package jp.egaonohon.camerapet;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;

/**
 * ゲーム画面で動くペットのクラス
 *
 * @author OtaShohei
 *
 */
public class Pet extends CamPeItem implements Runnable {

	/** 描画設定 */
	private Paint petPaint = new Paint();
	/**
	 * Petの画像拡大縮小用 拡大縮小に関しては、こちらのサイトを参照。
	 * http://blog.livedoor.jp/tmtlplus/archives/19016162.html
	 */
	final Matrix matrix = new Matrix();

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
	private int moveY = 3;
	/** ペットの歩くアニメーション効果用（歩数カウント） */
	private int cnt;
	/** ペットが動くスピード（移動および歩くアニメーションに影響） */
	private long speed = 60;

	/** ペット用のスレッド */
	private Thread petThread;
	// /** 衝突判定用のRectF */
	// private static RectF rectF;
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
	public Pet(Bitmap itemPh, int width, int height, int defaultX,
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
	public Pet(Bitmap petPhR, Bitmap petPhL, int itemWidth, int itemHeight,
			int defaultX, int defaultY, int viewWidth, int viewHeight) {
		super(petPhR, itemWidth, itemHeight, defaultX, defaultY, viewWidth,
				viewHeight);
		CameLog.setLog(TAG,"petPhRは" + petPhR +"petPhLは" +  petPhL +"itemWidthは" +  itemWidth);

		this.petPhR = petPhR;
		this.petPhL = petPhL;
		this.itemPh = petPhR;
		this.itemWidth = itemWidth;
		this.itemHeight = itemHeight;
		this.defaultX = defaultX;
		this.defaultY = defaultY;
		this.viewWidth = viewWidth;
		this.viewHeight = viewHeight;

		/** 衝突判定用RectFをインスタンス化 */
		rectF = new RectF();

		CameLog.setLog(TAG, "CPPetがnewされた時点でのViewのWidthは" + this.viewWidth
				+ "。ViewのHeightは" + this.viewHeight);

		CameLog.setLog(TAG, "CPPetがnewされた時点でのペットのWidthは" + this.itemWidth
				+ "。Heightは" + this.itemHeight);

		/** PetPhの拡大・縮小率設定
		 * ここでfloatにキャストしないと拡大率が小数点以下切り捨てられてしまうので要注意 */
		scaleX = (float)itemWidth / petPhR.getWidth();
		scaleY = (float)itemHeight / petPhR.getHeight();

		CameLog.setLog(TAG,"scaleXは" + scaleX +"scaleYは" +  scaleY);

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
	 * GameSurfaceViewから来たPetの移動量をセットする。 GameSurfaceView側での呼び出しがもちろん必要。
	 */
	public void setPetMoveSize(float x, float y) {
		moveX = (int) (x / (viewWidth / 8));
		moveY = (int) (y / (viewWidth / 8));
		// CameLog.setLog(TAG, "onTouchEventからペットに移動距離を設定");
	}

	/**
	 * Petの移動処理を司るメソッド。
	 */
	@Override
	public void move() {
		/** 歩数を数える */
		cnt++;

		/** 画面端のチェック：X軸。 (itemWidth/2)は、ペットの体の半分がはみ出ていたのでその調整 */
		if (nowX < 0 || this.viewWidth - itemWidth // + //(itemWidth / 2)

		< nowX) {
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
		if (nowY < 0 || this.viewHeight - (itemHeight// + (itemHeight / 2)
				) < nowY) {

			/** 次の行はそもそもXとYが食い違っているので動いているようだが妙な動きになってしまう。かつ、上下判定も変なことに */
			// moveY = -moveX;
			moveY *= -1;
			CameLog.setLog(TAG, "ペット移動範囲のwidthは" + viewWidth
					+ "ペット移動範囲のheightは" + viewHeight);
		}

		/** 移動させる */
		nowX = nowX + moveX;
		nowY = nowY + moveY;

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

		/** 衝突判定用RectFの更新 */
		rectF.set(nowX, nowY, nowX + itemWidth, nowY + itemHeight);

		 /** 衝突判定用RectFにセットした数値の確認 */
		 CameLog.setLog(TAG, "更新されたnowXは" + nowX + "。nowYは" + nowY
		 + "。nowX + itemWidthは" + (nowX + itemWidth)
		 + "。nowY + itemHeightは" + (nowY + itemHeight));
	}

	@Override
	public void draw(Canvas canvas) {
		/** Canvasの背景色を透明で塗る。 第二引数にPorterDuff.Mode.CLEARを付けないと透明にならないので注意。 */
		canvas.drawColor(Color.TRANSPARENT,
				android.graphics.PorterDuff.Mode.CLEAR);
		canvas.save();
		/** ここで描画位置を指定 */
		canvas.translate(nowX, nowY);
		canvas.drawBitmap(itemPh, matrix, petPaint);
		canvas.restore();
	}

	@Override
	public void run() {
		while (petThread != null) {
			move();
			try {
				Thread.sleep(1000 / speed);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
