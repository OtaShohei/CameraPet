package jp.egaonohon.camerapet;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class GameSurfaceView extends SurfaceView implements
		SurfaceHolder.Callback, Runnable {

	/** SurfaceViewのスレッド */
	Context context;
	ArrayList<CamPeItem> camPeItems = new ArrayList<CamPeItem>();
	public static final long FPS = 1000 / 30;

	/** SurfaceViewのスレッド */
	Thread thread;
	/** SurfaceViewのホルダー */
	SurfaceHolder holder;
	/** Viewの幅 */
	int viewWidth;
	/** Viewの高さ */
	int viweHeight;
	/** ペットの参照 */
	Pet myPet;
	/** ペット画像右向き */
	private Bitmap petPhR;
	/** ペット画像右向き */
	private Bitmap petPhL;

	/** 取得した餌の数 */
	int esaCnt;
	/** 複数餌画像 */
	private ArrayList<Bitmap> esaPhList;
	/** 餌初期位置：X軸。 */
	private int esaDefaultX = 1;
	/** 餌初期位置：Y軸。 */
	private int esaDefaultY = 0;

	/** Logのタグを定数で確保 */
	private static final String TAG = "GameSurfaceView";

	OnFcsChangeListener listener;

	public GameSurfaceView(Context context) {
		super(context);
		this.context = context;
		getHolder().addCallback(this);
		initialize();
	}

	public GameSurfaceView(Context context, AttributeSet attrs) {
		super(context, attrs);

		this.context = context;
		getHolder().addCallback(this);
		initialize();
	}

	public GameSurfaceView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initialize();
	}

	/**
	 * いずれのコンストラクタが呼ばれてもこのメソッドが動作する。
	 */
	private void initialize() {

		/** Viewの幅。ペットなどの表示の機種ごとの差異を吸収するため。 */
		viewWidth = getWidth();
		/** Viewの高さ。ペットなどの表示の機種ごとの差異を吸収するため。 */
		viweHeight = getHeight();
		/** initialize時点でのViewの高さをLogで確認 */
		CameLog.setLog(TAG, "initialize()時点でのViewの幅は" + viewWidth + "Viewの高さは"
				+ viweHeight);

		/** SurfaceHolder の取得 */
		holder = getHolder();

		/** SurfaceHolder に コールバックを設定 */
		holder.setFixedSize(viewWidth, viweHeight);
		CameLog.setLog(TAG, "Viewの幅は" + viewWidth + "Viewの高さは" + viweHeight);

		/** 半透明を設定 */
		holder.setFormat(PixelFormat.TRANSLUCENT);
		/** フォーカスをあてる */
		setFocusable(true);

		/** このViewをトップにする */
		setZOrderOnTop(true);

		CameLog.setLog(TAG, "CPGameSurfaceViewをコンストラクタから生成！");
	}

	@Override
	public void run() {
		Canvas canvas = null;
		while (thread != null) {
			long time = System.currentTimeMillis();
			canvas = holder.lockCanvas();
			if (canvas != null) {
				for (int i = 0; i < camPeItems.size(); i++) {
					camPeItems.get(i).draw(canvas);
				}

				for (int i = 0; i < camPeItems.size(); i++) {
					if (!camPeItems.get(i).equals(myPet)) {
						// if (camPeItems == null) {
						// CameLog.setLog(TAG, "camPeItemsがnull");
						//
						// }
						// if (camPeItems.get(i) == null) {
						// CameLog.setLog(TAG, "camPeItems.get(i)がnull");
						// }
						// if (camPeItems.get(i).getRectF() == null) {
						// CameLog.setLog(TAG,
						// "camPeItems.get(i).getRectF()がnull");
						// }
						if (RectF.intersects(myPet.getRectF(), camPeItems
								.get(i).getRectF())) {
							// CameLog.setLog(TAG, "myPetのrectは"
							// + myPet.getRectF().toString());
							// CameLog.setLog(TAG, "Esaのrectは"
							// + camPeItems.get(i).getRectF().toString());

							// camPeItems.remove(i);
							// CameLog.setLog(TAG, "Petとエサの接触を検知しました!");
						}
					}
				}
				holder.unlockCanvasAndPost(canvas);
			}

			time = System.currentTimeMillis() - time;

			if (time < FPS) {
				try {
					Thread.sleep(FPS - time);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		this.viewWidth = width;
		this.viweHeight = height;
		this.holder = holder;

		CameLog.setLog(TAG, "Viewの幅は" + width + "。Viewの高さは" + height);

		/** 直近撮影枚数をプリファレンスから取得 */
		esaCnt = CamPePref.loadNowShotCnt(context);
		CameLog.setLog(TAG, "プリファレンスから次の枚数を取り出した→" + esaCnt);
		/** プリファレンスからも取り出せない時は、初期値3を渡す */
		if (esaCnt == -1) {
			CameLog.setLog(TAG, "プリファレンスから枚数を取り出せないので初期値を代入する→" + esaCnt);
			esaCnt = 3;
		}

		CameLog.setLog(TAG, "直近撮影枚数を取得。枚数は" + esaCnt);
		/** ペット写真取得 */
		petPhR = BitmapFactory.decodeResource(context.getResources(),
				R.drawable.alpaca02);
		petPhL = BitmapFactory.decodeResource(context.getResources(),
				R.drawable.alpaca_left);

		/** ペット作成 */
		myPet = new Pet(petPhR, petPhL, viewWidth / 5, viewWidth / 5, 0, 0,
				viewWidth, viweHeight);

		camPeItems.add(myPet);
		CameLog.setLog(TAG, "ペット作成");

		try {
			/** 餌写真取得準備 */
			CamPePh camPePh = new CamPePh();
			/**
			 * 直近撮影写真が複数枚の時 複数枚写真取得
			 */
			esaPhList = new ArrayList<Bitmap>();
			esaPhList = camPePh.get(context, esaCnt);
			CameLog.setLog(TAG, "コンストラクタにて画像の読み込み完了。餌Phは" + esaPhList.size()
					+ "枚");

			for (int i = 0; i < esaPhList.size(); i++) {
				/**
				 * 複数写真使用での餌インスタンス生成
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
				 */
				camPeItems.add(new Esa(esaPhList.get(i), viewWidth / 8,
						viewWidth / 8,
						(int) ((esaDefaultX * (Math.random() * 10)) * 90),
						esaDefaultY, viewWidth, viweHeight, (int) (1 * (Math
								.random() * 10))));
			}
			CameLog.setLog(TAG, "複数写真使用で餌作成");
		} catch (Exception e) {
			/** 餌写真取得準備 */
			CamPePh camPePh = new CamPePh();
			esaPhList = new ArrayList<Bitmap>();

			CameLog.setLog(TAG, "例外発生のためデフォルト写真枚数5枚で餌写真取得");

			for (int i = 0; i < 5; i++) {
				/** 複数写真使用での餌インスタンス生成 */
				camPeItems.add(new Esa(esaPhList.get(i), viewWidth / 8,
						viewWidth / 8,
						(int) ((esaDefaultX * (Math.random() * 10)) * 90),
						esaDefaultY, viewWidth, viweHeight, (int) (1 * (Math
								.random() * 10))));
			}
		}
		thread = new Thread(this);
		thread.start();
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		thread = null;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		float x = event.getX(); // X座標を取得
		float y = event.getY(); // Y座標を取得
		// speedMove = true;
		myPet.setPetMoveSize(x, y);
		CameLog.setLog(TAG, "onTouchEvent");
		// listener.onFcsChange(i);
		return true;
	}
}
