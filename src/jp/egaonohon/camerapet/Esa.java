package jp.egaonohon.camerapet;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;

/**
 * ホーム画面内のゲームエリアで降ってくるペットの餌（=UserPH)を司るクラス。
 *
 * @author OtaShohei
 *
 */
public class Esa implements Runnable {

	/** 餌の数 */
	private int esaCnt = 0;
	/** 餌画像 */
	private ArrayList<Bitmap> esaPhList;
	/** 餌の幅 */
	private int esaWidth;
	/** 餌の高さ */
	private int esaHeight;

	/** Viewの幅 */
	private int viewWidth;
	/** Viewの高さ */
	private int viewHeight;

	/** 餌描画開始位置：X軸 */
	private int esaCurrentX = 5;
	/** 餌描画開始位置：Y軸 */
	private int esaCurrentY = 0;
	/** 餌移動距離：X軸 */
	private int esaMoveX = 0;
	/** 餌移動距離：Y軸 */
	private int esaMoveY = 5;
	/** 餌の動くスピード（まだ使わない） */
	private long speed = 8;

	/** カウントアップ用整数 */
	int cnt;

	/** Logのタグを定数で確保 */
	private static final String TAG = "PetAsobiBa";

	/** 描画用スレッド */
	private Thread thread;

	/** 呼び出し元のContext */
	Context esaContext;

	// private int width;
	// private int height;
	// private int nowX;
	// private int nowY;
	// private int targetX;
	// private int targetY;

	private Paint paint = new Paint();
	private RectF rect;

	/**
	 * 餌のコンストラクタ。
	 *
	 * @param esaWidth
	 * @param esaHeight
	 * @param viewWidth
	 * @param viewHeight
	 * @param esaContext
	 */
	public Esa(int esaWidth, int esaHeight, int viewWidth, int viewHeight,
			Context esaContext) {
		super();
		this.esaWidth = esaWidth;
		this.esaHeight = esaHeight;
		this.viewWidth = viewWidth;
		this.viewHeight = viewHeight;
		this.esaContext = esaContext;
	}

	public void move() {
		// 画面端のチェック：X軸
		if (esaCurrentX < 0
				|| viewWidth - viewWidth / 10 /* petPh.getWidth() */< esaCurrentX) {
			esaMoveX = -esaMoveX;
		}
		// 画面端のチェック：Y軸
		if (esaCurrentY < 0
				|| viewHeight - viewHeight / 10 /* petPh.getWidth() */< esaCurrentY) {
			esaMoveY = -esaMoveY;
			CameLog.setLog(TAG, "餌移動処理Y軸");
		}

		// 描画座標の更新
		esaCurrentX += esaMoveX;
		esaCurrentY += esaMoveY;

		cnt++;
		rect.set(esaCurrentX, esaCurrentY, esaCurrentX + esaWidth, esaCurrentY
				+ esaHeight);
	}

	// public ArrayList<Bitmap> make(Context context) {
	//
	// // Bitmap bm = BitmapFactory.decodeResource(context.getResources(),
	// // R.drawable.droid_1);
	// // Bitmap bm2 = BitmapFactory.decodeResource(context.getResources(),
	// // R.drawable.droid_2);
	// // esaImgs
	// // .add(Bitmap.createScaledBitmap(bm, width / 5, width / 5, true));
	// // esaImgs.add(Bitmap
	// // .createScaledBitmap(bm2, width / 5, width / 5, true));
	// for (int i = 0; i < esaImgs.size(); i++) {
	// /**
	// * 餌はここで直近撮影写真枚数分作る。for文のesaImgs.size()を調整すれば何個でも作れる。
	// * だが、餌のbitmapは直近撮影写真枚数が格納されたArrayListであるesaImgsだけにしてメモリを節約。
	// */
	// esaAry.add(new Esa(esaImgs, esaWidth / 5, esaWidth / 5, 100, 100));
	// CameLog.setLog(TAG, "直近撮影写真枚数分餌を作った！");
	// }
	// for (int i = 0; i < esaImgs.size(); i++) {
	// esaAry.get(i).setSpeed(8 * (i + 1));
	// /**
	// * 以下、それぞれの餌の初期位置。
	// */
	// esaAry.get(i).setTargetX(0);
	// esaAry.get(i).setTargetY(esaWidth / 5 * i);// 身長分ずらした。
	// CameLog.setLog(TAG, "餌の初期位置セット！");
	// }
	// }

	public void draw(Canvas canvas) {
		/** 現在の状態を保存 */
		canvas.save();

		/**
		 * 餌用の直近撮影写真を取得。
		 */
		CamPePh camPePh = new CamPePh();
		esaPhList = camPePh.get(esaContext);
		esaCnt = esaPhList.size();

		CameLog.setLog(TAG, "コンストラクタにて画像の読み込み完了。餌Phは" + esaCnt + "枚");

		float degrees = (cnt % 2 == 0) ? 0 : -15f;

		/** 餌画像に回転を加える */
		canvas.rotate(degrees, esaCurrentX + esaWidth / 2, esaCurrentY
				+ esaHeight / 2);

		/** 餌画像の描画 */
		for (int i = 0; i < esaPhList.size(); i++) {
			/**
			 * 画面幅の20分の1ずつ出現位置をずらす。
			 */
			canvas.drawBitmap(esaPhList.get(i), esaCurrentX
					* ((i + 1) * (viewWidth / 20)), esaCurrentY, paint);
			CameLog.setLog(TAG, "餌画像の描画" + i + "枚目");
		}
		// canvas.scale(2, 2, nowX + width / 2, nowY + height / 2);
		/** 回転させた餌画像を元に戻す */
		canvas.restore();
	}

	@Override
	public void run() {
		while (true) {
			move();
			try {
				Thread.sleep(1000 / speed);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * RectFのgetter
	 *
	 * @return
	 */
	public RectF getrRectF() {
		return rect;
	}

	/**
	 * speedのsetter
	 *
	 * @param speed
	 */
	public void setSpeed(long speed) {
		this.speed = speed;
	}

	public int getWidth() {
		return esaWidth;
	}

	public int getHeight() {
		return esaHeight;
	}

	public int getNowX() {
		return esaCurrentX;
	}

	public int getNowY() {
		return esaCurrentY;
	}

	// public void setTargetX(int targetX) {
	// this.targetX = targetX;
	// }

	// /**
	// * フィールド。姿を現す「画像」と「大きさ」。そして「動かすときの位置（座標）」は最低限必要なので用意する。
	// */
	// /** Petの表示画像が入ったArrayList */
	// ArrayList<Bitmap> bmAry = new ArrayList<Bitmap>();
	// /** 餌の幅 */
	// private int width;
	// /** 餌の高さ */
	// private int height;
	// /** 現在地x。初期値としてコンストラクタで渡す。 */
	// private int nowX;
	// /** 現在地y。初期値としてコンストラクタで渡す。 */
	// private int nowY;
	// /** どこへ行くかの目的地x。setterで渡す。 */
	// private int targetX;
	// /** どこへ行くかの目的地y。setterで渡す。 */
	// private int targetY;
	// /** Petの動作speed。ドロイド君それぞれの動く速度に変化をつける。 */
	// private long speed = 8;
	// /** Petを動かすスレッド。Runnableを実装するにあたってスレッドを用意する。 */
	// private Thread thread;
	// /** 当たり判定用。餌クラスだけでいいかも。これは。 */
	// private RectF rect;
	// /** 無限カウントアップ。 */
	// int cnt;
	// /** Logのタグを定数で確保 */
	// private static final String TAG = "Esa";
	//
	// Paint paint = new Paint();
	//
	// /**
	// * 幅のゲッター。
	// *
	// * @return
	// */
	// public int getWidth() {
	// return width;
	// }
	//
	// /**
	// * 高さのゲッター。
	// *
	// * @return
	// */
	// public int getHeight() {
	// return height;
	// }
	//
	// public int getNowX() {
	// return nowX;
	// }
	//
	// public int getNowY() {
	// return nowY;
	// }
	//
	// public RectF getrRectF() {
	// return rect;
	// }
	//
	// /**
	// * 移動目標のY座標
	// *
	// * @param targetY
	// */
	// public void setTargetY(int targetY) {
	// this.targetY = targetY;
	// }
	//
	// /**
	// * 移動目標のX座標
	// *
	// * @param targetX
	// */
	// public void setTargetX(int targetX) {
	// this.targetX = targetX;
	// }
	//
	// public void setSpeed(long speed) {
	// this.speed = speed;
	// }
	//
	// /**
	// * ドロイド君は複数いるのでArrayListに格納。
	// * ただ、Bitmapで展開しているのは一体分の画像しか行っていない。そうすることでメモリにやさしい設計になる。 一体分の参照を渡しているだけ。
	// * ドロイド君が100体になっても1つぶんで済む。
	// * Backgroundクラスはその作り方をしていないので、100枚分のインスタンスが必要となってしまう。
	// *
	// * bitmapインスタンスは小さく作っておいて、描画するときに拡大して表示するとさらにメモリを節約できる。
	// *
	// * @param bmAry
	// * @param width
	// * @param height
	// * @param x
	// * @param y
	// */
	//
	// public Esa(ArrayList<Bitmap> bmAry, int width, int height, int x, int y)
	// {
	// super();
	// this.bmAry = bmAry;
	// this.width = width;
	// this.height = height;
	// this.nowX = x;
	// this.nowY = y;
	// /** 存在位置検知のため用意。 */
	// rect = new RectF();
	// thread = new Thread(this);
	// thread.start();
	// }
	//
	// /**
	// * 餌の移動させるメソッド。
	// */
	// public void move() {
	// cnt++;
	//
	// // nowX = 100;
	// // nowY = 100;
	//
	// if (Math.abs(targetX - nowX) < height / 5) {
	// nowX = targetX;
	// } else {
	// int distanceX = targetX - nowX;
	// if (distanceX > 0) {
	// nowX += height / 5;
	// } else {
	// nowX -= height / 5;
	// }
	// }
	// if (Math.abs(targetY - nowY) < height / 5) {
	// nowY = targetY;
	// } else {
	// int distanceY = targetY - nowY;
	// if (distanceY > 0) {
	// nowY += height / 5;
	// } else {
	// nowY -= height / 5;
	// }
	// }
	// rect.set(nowX, nowY, nowX + width, nowY + height);
	// CameLog.setLog(TAG, "move");
	// }
	//
	// /**
	// * 餌を生成するメソッド。
	// */
	// public void draw(Canvas canvas) {
	// for (int i = 0; i < bmAry.size(); i++) {
	// canvas.drawBitmap(bmAry.get(i), nowX, nowY, paint);
	//
	// }
	// CameLog.setLog(TAG, "draw");
	// }
	//
	// @Override
	// public void run() {
	// while (thread != null) {
	// move();
	// try {
	// Thread.sleep(1000/8);
	// } catch (InterruptedException e) {
	// e.printStackTrace();
	// }
	// CameLog.setLog(TAG, "run");
	// }
	// }
}
