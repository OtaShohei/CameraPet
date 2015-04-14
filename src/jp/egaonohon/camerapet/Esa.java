package jp.egaonohon.camerapet;

import java.util.Random;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;

public class Esa extends CamPeItem implements Runnable {

	/** Esa描画設定 */
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
	/** エサの現在位置：X軸 */
	private float nowX = 10;
	/** エサの初期位置：X軸 */
	private int defaultX;
	/** エサの現在距離：Y軸 */
	private double nowY;
	/** エサ移動距離：X軸 */
	private double moveX;
	/** エサ移動距離：Y軸 */
	private double moveY;
	/** エサが移動するアニメーション効果用（歩数カウント） */
	private int cnt = 1;
	/** エサが動くスピード（移動および歩くアニメーションに影響） */
	private long speed = 60;
	/** X軸の進行方向を変更した時間 */
	private long moveChangeTime;
	/** Y方向に移動する加速度 */
	private float esaKasokudoX = 1.3f;
	/** Y方向餌移動乱数最大値 */
	private int esaKasokudoXMax = 4;

	/** 1度でも何かに衝突したか否か */
	private boolean oneTimeKrush = false;

	/** エサの数 */
	int esaCnt = 1;

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
			int defaultY, int viewWidth, int viewHeight, float moveY) {
		super(width, height, defaultX, defaultY, viewWidth, viewHeight);
		this.itemPh = itemPh;
		this.itemWidth = width;
		this.itemHeight = height;
		// this.defaultX = defaultX;
		this.nowX = defaultX;
		// /** エサのY座標の初期位置をマイナスに設定して画面表示直後でのペットとの接触をさける */
		// this.nowY = defaultY - (viewWidth / 5);
		this.nowY = defaultY;
		this.viewWidth = viewWidth;
		this.viewHeight = viewHeight;
		this.moveY = moveY;
		this.moveX = esaKasokudoX
				* ((new Random().nextInt(esaKasokudoXMax+1)) - (esaKasokudoXMax/2));

		/** 衝突判定用RectFをインスタンス化 */
		rectF = new RectF();

		CameLog.setLog(TAG, "Esaがnewされた時点でのViewのWidthは" + this.viewWidth
				+ "。ViewのHeightは" + this.viewHeight + "defaultXは" + defaultX);

		 /** EsaPhの拡大縮小率確認 */
		 CameLog.setLog(TAG, "EsaPhのitemWidthは" + itemWidth );
		 CameLog.setLog(TAG, "EsaPhのWidthは" + itemPh.getWidth() );

		/**
		 * PetPhの拡大・縮小率設定 ここでfloatにキャストしないと拡大率が小数点以下切り捨てられてしまうので要注意
		 */
		scaleX = (float) itemWidth / itemPh.getWidth();
		scaleY = (float) itemHeight / itemPh.getHeight();

		/** EsaPhの拡大・縮小率設定 */
		matrix.postScale(scaleX, scaleY);
		// /** EsaPhの拡大縮小率確認 */
		// CameLog.setLog(TAG, "EsaPhの拡大縮小率はXが" + scaleX + "。Yは" + scaleY);

		// /** EsaPhの回転角設定 */
		// degree = (cnt % 2 == 0) ? 0 : -15f;
		// matrix.postRotate(degree);

		// // 移動ベクトルを下に向ける
		// moveY = (int) (1 * (Math.random() * 10));

		// /** 最初に進行方向を決めた時間を記録（X方向をランダムに動かすため） */
		// moveChangeTime = System.currentTimeMillis();

		// /** 最初の進む方向を決める */
		// moveX = new Random().nextInt((viewWidth / 128) * 2) + 1;

		camPeItemThread = new Thread(this);
		camPeItemThread.start();
	}

	@Override
	public void move() {
		/** 歩数を数える */
		cnt++;

//		if (nowY < (viewHeight/3)) {
//			/** Y方向に動くペースを変化させる。 */
//			moveY = moveY * (1 + (cnt / 800));
//		}

		/** 画面端のチェック：X軸 */
		if (nowX < 0 || this.viewWidth - itemWidth < nowX) {
			moveX *= -1;
		}

		/** 画面端のチェック：Y軸 は画面上部のみ跳ね返るようにする */
		if (nowY < 0) {
			moveY *= -1;
		}

		// /** ふらふらと舞う横方向の限界点チェック：X軸 */
		// if (nowX < (defaultX - ((viewWidth/128)*40) ) || nowX > (defaultX +
		// ((viewWidth/128)*40) )) {
		// moveX *= -1;
		// }
		//
		// /** 画面端のチェック：Y軸 は行わずに落下したら画面から出て行くように変更するので次のブロックはコメントアウトする */
		// if (nowY < 0 || this.viewHeight - itemHeight < nowY) {
		// /** 次の行はそもそもXとYが食い違っているので動いているようだが妙な動きになってしまう。かつ、上下判定も変なことに */
		// // moveY = -moveX;
		// moveY *= -1;
		// }
		//
		// /** */
		// long now = System.currentTimeMillis();
		// if (now > moveChangeTime +1000) {
		// moveX = new Random().nextInt(viewWidth/128) + 1;
		// moveChangeTime = now;
		// }

		/** 移動させる */
		nowX = (float) (nowX + moveX);
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
		// matrix.reset();
		// matrix.postTranslate(nowX, nowY);

		/** 衝突判定用RectFの更新 */
		rectF.set(nowX, (float) nowY, nowX + itemWidth, (float) nowY
				+ itemHeight);

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
		/** 一度衝突したエサは赤く変わる。キャンバスをリストアしたあとにこの記述を書かないと反映されないので要注意。 */
		if (oneTimeKrush) {
			/** Esa描画設定 */
			Paint krushPaint = new Paint();
			krushPaint.setARGB(125, 100, 0, 0);
			canvas.drawRect(rectF, krushPaint);
		}
	}

	@Override
	public void run() {
		while (camPeItemThread != null) {
			move();
			try {
				Thread.sleep(1000 / speed);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void returnAfterKrush() {
		// CameLog.setLog(TAG,"returnAfterKrush");
		oneTimeKrush = true;
		moveY *= -1;
	}

}
