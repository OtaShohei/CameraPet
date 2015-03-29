package jp.egaonohon.camerapet;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Toast;

/**
 * ゲーム画面の動作を司るSurfaceViewのクラス。
 *
 * @author OtaShohei
 *
 */
public class GameSurfaceView extends SurfaceView implements
		SurfaceHolder.Callback, Runnable {

	/** SurfaceViewを呼び出したContext */
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
	int viewHeight;
//	/** はてなマーク画像 */
//	private Bitmap hatena_btn;

	/** ペットの参照 */
	Pet myPet;
	/** ペット画像右向き */
	private Bitmap petPhR;
	/** ペット画像右向き */
	private Bitmap petPhL;

	/** 直近撮影枚数（=エサの数） */
	int esaCnt;
	/** 複数餌画像 */
	private ArrayList<Bitmap> esaPhList;
	/** 餌初期位置：X軸。 */
	private int esaDefaultX = 1;
	/** 餌初期位置：Y軸。 */
	private int esaDefaultY = 0;

	/** エサゲット時サウンド用変数 */
	private MediaPlayer bakubakuSE;
	/** エサゲット時カウント */
	private int esaGetCnt;

	/** ペット年齢 */
	private String petAge;
	/** 画面表示経験値 */
	private int gettedtotalEXP;
	/** 累積撮影回数文字 */
	private String expTxt;
	/** レーティングバー最大値 */
	private int ratingMax = 1;
	/** エサステイタス表示文字 */
	private String esaStatus;
	/** 画面文字や塗りのテーマカラー */
	private int themeColor =Color.argb(255, 224, 107, 98);
	/** その日最初の起動か否か */
	private boolean firstOfTheDay = true;

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
		viewHeight = getHeight();
		/** initialize時点でのViewの高さをLogで確認 */
		CameLog.setLog(TAG, "initialize()時点でのViewの幅は" + viewWidth + "Viewの高さは"
				+ viewHeight);

		/** SurfaceHolder の取得 */
		holder = getHolder();

		/** SurfaceHolder に コールバックを設定 */
		holder.setFixedSize(viewWidth, viewHeight);
		CameLog.setLog(TAG, "Viewの幅は" + viewWidth + "Viewの高さは" + viewHeight);

		/** 半透明を設定 */
		holder.setFormat(PixelFormat.TRANSLUCENT);
		/** フォーカスをあてる */
		setFocusable(true);

		/** サウンドエフェクトのインスタンス生成し準備 */
		bakubakuSE = MediaPlayer.create(context, R.raw.dog1b);

		/** このViewをトップにする */
		setZOrderOnTop(true);

		if (firstOfTheDay && !MainActivity.isReturnCam()) {
			/** 飼い主歓迎メッセージ表示 */
			Toast.makeText(getContext(),
					"「ワンワン!キュイーン!ワンワン!」\n（訳:飼い主さんに会えてうれしいワン!",
					Toast.LENGTH_LONG).show();
		}

		// CameLog.setLog(TAG, "CPGameSurfaceViewをコンストラクタから生成！");
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

				/** 衝突判定実行 */
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
						// CameLog.setLog(TAG,
						// "ペットleft" + myPet.getRectF().left + "ペットtop"
						// + myPet.getRectF().top + "ペットright"
						// + myPet.getRectF().right + "ペットbottom"
						// + myPet.getRectF().bottom);
						// CameLog.setLog(TAG, "エサleft"
						// + camPeItems.get(i).getRectF().left + "エサtop"
						// + camPeItems.get(i).getRectF().top + "エサright"
						// + camPeItems.get(i).getRectF().right
						// + "エサbottom"
						// + camPeItems.get(i).getRectF().bottom);

						if (RectF.intersects(myPet.getRectF(), camPeItems
								.get(i).getRectF())) {
							// CameLog.setLog(TAG, "myPetのrectは"
							// + myPet.getRectF().toString());
							// CameLog.setLog(TAG, "Esaのrectは"
							// + camPeItems.get(i).getRectF().toString());

							/**
							 * レーティングをUpする(今回のエサ総数に占める1個割合相当のfloat値分。10
							 * は表示している星の数） (1 / esaCnt) * 10
							 */
							// MainActivity.ratingUp((float) esaCnt / 10);
							esaGetCnt++;

							/** プリファレンスから累計エサ獲得成功回数を取得 */
							int gettedTotalEsaGetCnt = CamPePref
									.loadTotalEsaGetCnt(context);

							if (gettedTotalEsaGetCnt == -1) {
								gettedTotalEsaGetCnt = 0;
							}

							/** プリファレンスから累計経験値を取得 */
							gettedtotalEXP = CamPePref.loadTotalExp(context);

							/** 新たな累計エサ獲得成功回数の算出 */
							gettedTotalEsaGetCnt++;

							/** 新たな累計経験値の算出 */
							gettedtotalEXP++;

							try {
								if (gettedtotalEXP == -1) {
									gettedtotalEXP = 0;
								}
								expTxt = context.getString(R.string.exp_points) + " " + String.valueOf(gettedtotalEXP);
							} catch (Exception e) {
								CameLog.setLog(TAG,
										"プリファレンスからの経験値表示に失敗@onResume");
							}

							/** 新たな累計エサ獲得成功回数と新たな累計経験値をプリファレンスに保存 */
							CamPePref.saveTotalExpAndEsaGetCnt(context,
									gettedTotalEsaGetCnt, gettedtotalEXP);

							/** 今回のエサ獲得数がレイティング最大値になったら0にリセット…はやめて、ペット大喜び */
							if (ratingMax == esaGetCnt) {
								try {
									/**
									 * 飼い主歓迎メッセージ表示。
									 * ウェルカムメッセージ表示とコンフリクト時の例外を避けるためtry-catch
									 */
									Toast.makeText(getContext(),
											"「ウーワンワン！ウーワォン！」\n（訳:美味しかったワン!",
											Toast.LENGTH_LONG).show();
								} catch (Exception e) {
									// TODO 自動生成された catch ブロック
									e.printStackTrace();
								}
//									finally {
//									esaGetCnt = 0;
//								}
							}
							/** 獲得したエサを削除する */
							camPeItems.remove(i);
							CameLog.setLog(TAG, "現在の残数は" + camPeItems.size());
							bakubakuSE.start();
							// CameLog.setLog(TAG, "Petとエサの接触を検知しました!");
						}
					}
					/** SurfaceView上に、レーティングバー・経験値・ペット年齢・を描画するメソッドを呼び出す */
					textRectWrite(canvas);
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

	/**
	 * SurfaceView上に、レーティングバー・経験値・ペット年齢などを描画するメソッド。
	 * @param canvas
	 */
	private void textRectWrite(Canvas canvas) {
		/** レイティングバー風の記述 */
		Paint paint = new Paint();
		/** 文字にテーマカラー設定 */
		paint.setColor(themeColor);
		/** Aliasを設定 */
		paint.setAntiAlias(true);

		/** テキスト設定 sp*/
		paint.setTextSize(viewWidth / 26);
		paint.setTypeface(Typeface.DEFAULT_BOLD);
		/** テキスト右寄せ */
		paint.setTextAlign(Paint.Align.RIGHT);

		/** 経験値表示 */
		canvas.drawText(expTxt, (viewWidth / 40) * 39, (viewHeight / 80) * 4,
				paint);

		/** 誕生日表示 */
		canvas.drawText(petAge, (viewWidth / 40) * 39, (viewHeight / 80) * 7,
				paint);

		/** テキスト右寄せ */
		paint.setTextAlign(Paint.Align.LEFT);

		/** エサステイタス表示 */
		esaStatus = context.getString(R.string.esa_status) + "    " + esaGetCnt + "/" + ratingMax;
		canvas.drawText(esaStatus, viewWidth / 40, (viewHeight / 160) * 15,
				paint);

//		/** 経験値表示 */
//		canvas.drawText(expTxt, (viewWidth / 40) * 39, (viewHeight / 80) * 4,
//				paint);

		/** 進捗部分を塗る */
		if (ratingMax == -1) {
			/** レーティングが-1の場合（写真撮影なしでGame画面に現れた場合）は透明で塗りつぶす */
			paint.setColor(themeColor);
			Rect ratingRect = new Rect(viewWidth / 40, viewHeight / 40,
					((viewWidth / 3) / 1) * esaGetCnt, (viewHeight / 80) * 4);
			/** 進捗部分描画実行 */
			canvas.drawRect(ratingRect, paint);

		} else if (esaGetCnt != 0 || ratingMax > 1) {
			Rect ratingRect = new Rect(viewWidth / 40, viewHeight / 40,
					((viewWidth / 3) / ratingMax) * esaGetCnt,
					(viewHeight / 80) * 4);
			/** 進捗部分描画実行 */
			canvas.drawRect(ratingRect, paint);
		}
//		CameLog.setLog(TAG, "レーティング最大値は" + ratingMax + "。獲得済みエサ数は" + esaGetCnt);
		/** レイティングバー外枠にテーマカラー設定 */
		paint.setColor(themeColor);
		/** レイティングバー外枠設定 */
		paint.setStyle(Style.STROKE);
		/** レイティングバー外枠描画実行 */
		canvas.drawRect(viewWidth / 40, viewHeight / 40, viewWidth / 3,
				(viewHeight / 80) * 4, paint);

//		/**　はてなマーク表示 */
//		canvas.drawBitmap(hatena_btn, (viewWidth / 40) * 39, (viewHeight / 80) * 5, paint);
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		this.viewWidth = width;
		this.viewHeight = height;
		this.holder = holder;

		CameLog.setLog(TAG, "Viewの幅は" + width + "。Viewの高さは" + height);

		try {
			/** 直近撮影回数をプリファレンスから取得 */
			esaCnt = CamPePref.loadNowShotCnt(context);
			/** 直近撮影回数をレーティングバー最大値に設定 */
			ratingMax = esaCnt;
			if (ratingMax == 0) {
				/** 0での割り算による例外防止のため0になる場合は -1を入れる */
				ratingMax = -1;
			}
		} catch (Exception e1) {
			// TODO 自動生成された catch ブロック
			e1.printStackTrace();
			CameLog.setLog(TAG, "直近撮影回数をプリファレンスから取得時に例外が発生しました。");
		}
		// /** エサゲームレーティングのステップ幅算出のためエサ獲得数をMainActivityに伝える */
		// MainActivity.setGameRatingStep(esaCnt);
		CameLog.setLog(TAG, "プリファレンスから次の枚数を取り出した→" + esaCnt);

		/** その日最初の起動かつカメラやチュートリアルから戻ってきたのでなければ、初期値5を渡す */
		if (firstOfTheDay && (!MainActivity.isReturnCam() || !MainActivity.isReturnTut())) {
			CameLog.setLog(TAG, "プリファレンスから枚数を取り出せないので初期値を代入する→" + esaCnt);
			esaCnt = ratingMax = 5;
			firstOfTheDay = false;
		}

		CameLog.setLog(TAG, "直近撮影枚数を取得。枚数は" + esaCnt);

		/** ペット写真取得 */
		petPhR = BitmapFactory.decodeResource(context.getResources(),
				R.drawable.alpaca02);
		petPhL = BitmapFactory.decodeResource(context.getResources(),
				R.drawable.alpaca_left);

		/** ペット作成 */
		myPet = new Pet(petPhR, petPhL, viewWidth / 5, viewWidth / 5, 0,
				(viewWidth / 3) * 2, viewWidth, viewHeight);

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
						esaDefaultY, viewWidth, viewHeight, (int) (1 * (Math
								.random() * 9))));
			}

			CameLog.setLog(TAG, "複数写真使用で餌作成");
		} catch (Exception e) {
			/** 餌写真取得準備 */
			CamPePh camPePh = new CamPePh();
			esaPhList = new ArrayList<Bitmap>();

			CameLog.setLog(TAG, "例外発生のためデフォルト写真枚数5枚で餌写真取得");
			/** 直近撮影回数をレーティングバー最大値に設定&レーティングバー最大値にも5を設定 */
			ratingMax = 5;
			for (int i = 0; i < 5; i++) {
				/** 複数写真使用での餌インスタンス生成。こちらは、餌作成に成功しても直近撮影回数は0に戻さない */
				camPeItems.add(new Esa(esaPhList.get(i), viewWidth / 8,
						viewWidth / 8,
						(int) ((esaDefaultX * (Math.random() * 10)) * 90),
						esaDefaultY, viewWidth, viewHeight, (int) (1 * (Math
								.random() * 10))));
			}
		}

		/** ペット年齢取得 */
		petAge = Birthday.getAge(context);

		/** 経験値取得 */
		try {
			gettedtotalEXP = CamPePref.loadTotalExp(context);
		} catch (Exception e) {
			e.printStackTrace();
			CameLog.setLog(TAG, "プリファレンスから経験値取得に失敗@onCreate");
		}

		CameLog.setLog(TAG, "プリファレンスから経験値取得に成功@onCreate");
		try {
			if (gettedtotalEXP == -1) {
				gettedtotalEXP = 0;
			}
			expTxt = context.getString(R.string.exp_points) + " " + String.valueOf(gettedtotalEXP);
		} catch (Exception e) {
			CameLog.setLog(TAG, "プリファレンスからの経験値表示に失敗@onResume");
		}

//		/** はてなマーク取得 */
//		hatena_btn = BitmapFactory.decodeResource(context.getResources(),
//				R.drawable.hatena_btn);

		thread = new Thread(this);
		thread.start();
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		/** 餌作成に成功したら直近撮影回数を0に戻す */
		esaCnt = 0;
		CamPePref.saveNowShotCnt(getContext(), esaCnt);
		
		/** 起動済みの旨プリファレンスに情報を保存 */
		CamPePref.saveStartStatus(getContext());
		CameLog.setLog(TAG,"起動済みの旨プリファレンスに情報を保存");
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

	// public void rating() {
	// Paint paint = new Paint();
	// paint.setColor(Color.argb(130, 237, 118, 33));
	//
	// RectF ratingRect = new RectF(viewWidth / 40, viewHeight / 20,
	// viewWidth / 3, (viewHeight / 20) * 2);
	// canvas.drawRect(rectF, paint);
	// }
}
