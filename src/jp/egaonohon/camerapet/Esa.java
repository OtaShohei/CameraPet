package jp.egaonohon.camerapet;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;

public class Esa extends CamPeItem implements Runnable {

	/** 描画設定 */
	private Paint paint = new Paint();
	/**
	 * Petの画像拡大縮小用 拡大縮小に関しては、こちらのサイトを参照。
	 * http://blog.livedoor.jp/tmtlplus/archives/19016162.html
	 */
	final Matrix matrix = new Matrix();

	/** Viewの幅 */
	private int viewWidth;
	/** Viewの高さ */
	private int viewHeight;

	/** 餌画像1枚 */
	private Bitmap itemPh;
	// /** 餌画像複数枚 */
	// private ArrayList<Bitmap> itemPhList;

	/** X方向の拡大縮小率算出 */
	float scaleX = 1.0f;
	/** Y方向の拡大縮小率算出 */
	float scaleY = 1.0f;
	/** エサの回転角度 */
	private float degree = 1.0f;
	/** エサの幅 */
	private int itemWidth = 10;
	/** エサの高さ */
	private int itemHeight = 10;
//	/** エサの初期位置：X軸 */
//	private int defaultX;
//	/** エサの初期位置：Y軸 */
//	private int defaultY;
	/** エサの現在位置：X軸 */
	private int nowX = 10;
	/** エサの現在距離：Y軸 */
	private double nowY;
	/** エサ移動距離：X軸 */
	private int moveX;
	/** エサ移動距離：Y軸 */
	private double moveY;
	/** エサが移動するアニメーション効果用（歩数カウント） */
	private int cnt;
	/** エサが動くスピード（移動および歩くアニメーションに影響） */
	private long speed = 60;

	/** エサの数 */
	int esaCnt = 1;

	/** エサ用のスレッド */
	private Thread esaThread;
	// /** 衝突判定用のRectF */
	// private static RectF esaRectF;
	/** Logのタグを定数で確保 */
	private static final String TAG = "Esa";

	/**
	 * エサのコンストラクタ。
	 *
	 * @param itemPh
	 *            エサ写真
	 * @param width
	 *            エサ写真の幅
	 * @param height
	 *            エサ写真の高さ
	 * @param defaultX
	 *            エサの初期位置X
	 * @param defaultY
	 *            エサの初期位置Y
	 * @param viewWidth
	 *            エサが動くViewの幅
	 * @param viewHeight
	 *            エサが動くViewの高さ
	 * @param moveY
	 *            エサが落下するY方向dp値
	 */
	public Esa(Bitmap itemPh, int width, int height, int defaultX,
			int defaultY, int viewWidth, int viewHeight, Double moveY) {
		super(width, height, defaultX, defaultY, viewWidth, viewHeight);
		this.itemPh = itemPh;
		this.itemWidth = width;
		this.itemHeight = height;
		this.nowX = defaultX;
		/** エサのY座標の初期位置をマイナスに設定して画面表示直後でのペットとの接触をさける */
		this.nowY = defaultY - (viewWidth/4);
		this.viewWidth = viewWidth;
		this.viewHeight = viewHeight;
		this.moveY = moveY;

		/** 衝突判定用RectFをインスタンス化 */
		rectF = new RectF();

		CameLog.setLog(TAG, "Esaがnewされた時点でのViewのWidthは" + this.viewWidth
				+ "。ViewのHeightは" + this.viewHeight + "defaultXは" + defaultX);

		/** PetPhの拡大・縮小率設定
		 * ここでfloatにキャストしないと拡大率が小数点以下切り捨てられてしまうので要注意 */
		scaleX = (float)itemWidth / itemPh.getWidth();
		scaleY = (float)itemHeight / itemPh.getHeight();

		/** EsaPhの拡大・縮小率設定 */
		matrix.postScale(scaleX, scaleY);
		// /** EsaPhの拡大縮小率確認 */
		// CameLog.setLog(TAG, "EsaPhの拡大縮小率はXが" + scaleX + "。Yは" + scaleY);

		/** EsaPhの回転角設定 */
		degree = (cnt % 2 == 0) ? 0 : -15f;
		matrix.postRotate(degree);

		// // 移動ベクトルを下に向ける
		// moveY = (int) (1 * (Math.random() * 10));

		esaThread = new Thread(this);
		esaThread.start();
	}

	@Override
	public void move() {
		/** 歩数を数える */
		cnt++;

		/** 画面端のチェック：X軸 */
		if (nowX < 0 || this.viewWidth - itemWidth < nowX) {
			/** 次の行のようにすると、左端に行った時も左へ向かってしまう */
			// moveX = -moveX;
			moveX *= -1;
		}

		/** 画面端のチェック：Y軸 は行わずに落下したら画面から出て行くように変更するので次のブロックはコメントアウトする */
//		if (nowY < 0 || this.viewHeight - itemHeight < nowY) {
//			/** 次の行はそもそもXとYが食い違っているので動いているようだが妙な動きになってしまう。かつ、上下判定も変なことに */
//			// moveY = -moveX;
//			moveY *= -1;
//		}

		/** 移動させる */
		nowX = nowX + moveX;
		nowY = nowY + moveY;

		// /** esaPhの位置と拡大縮小率確認 */
		// CameLog.setLog(TAG, "nowXは" + nowX + "。viewWidthは"
		// + viewWidth + "。widthは" + width + "。nowYは"
		// + nowY + "。viewHeightは" + viewHeight + "。Heightは"
		// + height);

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
		 *
		 * 次の2行もコメントアウト。設定した拡大縮小率がmatrix.reset()ですぐになかったことになってしまうから。
		 * */
//		matrix.reset();
//		matrix.postTranslate(nowX, nowY);

		/** 衝突判定用RectFの更新 */
		rectF.set(nowX, (float) nowY, nowX + itemWidth, (float)nowY + itemHeight);

		// /** 衝突判定用RectFにセットした数値の確認 */
		// CameLog.setLog(TAG, "更新されたnowXは" + nowX + "。nowYは" + nowY
		// + "。nowX + itemWidthは" + (nowX + itemWidth)
		// + "。nowY + itemHeightは" + (nowY + itemHeight));

		// CameLog.setLog(TAG, "onUpdateでesaRectの中身は空っぽ?" + esaRect.isEmpty());

	}

	@Override
	public void draw(Canvas canvas) {
		canvas.save();
		/** ここで描画位置を指定 */
		canvas.translate(nowX, (float) nowY);
		canvas.drawBitmap(itemPh, matrix, paint);
		canvas.restore();
	}

	@Override
	public void run() {
		while (esaThread != null) {
			move();
			try {
				Thread.sleep(1000 / speed);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
