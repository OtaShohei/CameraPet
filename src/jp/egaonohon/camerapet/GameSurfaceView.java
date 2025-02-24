package jp.egaonohon.camerapet;

import java.util.ArrayList;
import java.util.Random;

import jp.egaonohon.camerapet.pet.AbstractPet;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.PixelFormat;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * ゲーム画面の動作を司るSurfaceViewのクラス。
 *
 * @author OtaShohei
 */
public class GameSurfaceView extends SurfaceView implements
		SurfaceHolder.Callback, Runnable, OnGestureListener {

	/** SurfaceViewを呼び出したContext */
	Context context;
	private ArrayList<CamPeItem> camPeItems = new ArrayList<CamPeItem>();
	public static final long FPS = 1000 / 30;

	/** userが指を触れたX座標 */
	private float usertouchedX;
	/** userが指を触れたX座標 */
	private float usertouchedY;

	/** SurfaceViewのスレッド */
	private Thread thread;
	/** SurfaceViewのホルダー */
	private SurfaceHolder holder;
	/** Viewの幅 */
	private int viewWidth;
	/** Viewの高さ */
	private int viewHeight;

	/** Defaultペットの参照 */
	private AbstractPet myPet;
	/** レベルアップ後のペットの参照 */
	private AbstractPet updatedMyPet;
	/** レベルアップ前のペット型番 */
	String beforePetModelNumber;
	/** レベルアップ後のペット型番 */
	String updatedPetModelNumber;

	/** 直近撮影枚数（=エサの数） */
	private int esaCnt;
	/** 複数餌画像 */
	private ArrayList<Bitmap> esaPhList;
	/** 餌初期位置：X軸。 */
	private int esaDefaultX = 1;
	/** 餌初期位置：Y軸。 */
	private int esaDefaultY = 0;
	/** 餌移動加速度基準値 */
	private float esaKasokudo = 1.5f;
	/** 餌移動加速度乱数最大値 */
	private int esaKasokudoMax = 4;
	/** インストール直後エサ生成数初期値 */
	private int esaDefaultMakeCnt = 5;
	/** 前回エサを作った時間 */
	private long lastEsaMakeTime;
	/** まだ生成していない残りエサ数（次回画面表示時用） */
	private int nextMakeEsaCnt;

	/** プリファレンスから取得した3時間以内エサ獲得成功数 */
	private int gettedThreeHoursEatCnt;

	/** 1ゲームでエサを落下させた数 */
	private int throwedEsa = 0;

	/** エサゲット時サウンド用変数 */
	private MediaPlayer bakubakuSE;
	/** レベルアップ時サウンド用変数 */
	private MediaPlayer levelUpSE;
	/** エサゲット時カウント */
	private int esaGetCnt = 0;
	/** SEを鳴らすか否かの変数 */
	private static boolean seOn = true;

	/** ペット年齢 */
	private String petAge;
	/** 画面表示ペット種別名 */
	private static String speciesName;
	/** 画面表示経験値 */
	private int gettedtotalEXP;
	/** 累積撮影回数文字 */
	private String expTxt;
	/** エサ落下予定表示文字 */
	private String foodCntThisTime;
	/** エサ取得プログレスバー最大値 */
	private int progressMax = 100;
	/** 今回降ってくる予定のエサの数。直近撮影回数の10倍 */
	private int nowFalldownEsaCnt;
	/** エサステイタス表示文字 */
	private String esaStatus;
	/** 画面文字や塗りのテーマカラー */
	private int themeColor = Color.argb(255, 224, 107, 98);
	/** その日最初の起動か否か */
	private boolean firstOfTheDay = true;
	/** セリフでのえさ告知回数 */
	private int EsakokutiCnt = 0;
	// /** セリフでの満腹告知回数 */
	// private int ManpukuCnt = 0;
	/** 世間話を行っていいかどうか */
	private boolean sekenBanasi = false;
	/** ペットにユーザーが触れてよろこぶ仕草をペットがするかどうかを判定するためのタッチ位置X座標 */
	private float petAmuseX;
	/** ペットにユーザーが触れてよろこぶ仕草をペットがするかどうかを判定するためのタッチ位置Y座標 */
	private float petAmuseY;
	/** フリック検出をPetに伝えていいか否か */
	private static boolean flickOk = true;

	/** エサの残数がゼロになった時間。世間話を開始する基準時間となる */
	private long esaZeroTime = 0;

	/** フリック動作を拾うためのGestureDetector */
	private GestureDetector mGestureDetector;

	/** 満腹時のペット喜びの声 */
	String petWelcomMessage;

	/** 吹き出し発行時のイベント:その日最初のアプリ起動時 */
	private int pet_message_welcome = 1;
	/** 吹き出し発行時のイベント:お腹いっぱいになった時 */
	private int pet_message_satiety = 2;
	/** 吹き出し発行時のイベント:降ってくるエサの残数が0になった時 */
	private int pet_message_esa_zero = 3;
	/** 吹き出し発行時のイベント:レベルアップした時 */
	private int pet_message_levelup = 4;
	/** 吹き出し発行時のイベント:SNS投稿で成長日記を投稿してくれた時 */
	private int pet_message_thanksSNS = 5;
	/** 吹き出し発行時のイベント:ペット図鑑を見て帰ってきた時。 */
	private int pet_message_thanksEncyc = 6;
	/** 吹き出し発行時のイベント:チュートリアルを見て帰ってきた時。 */
	private int pet_message_thanksTutorial = 7;
	/** 吹き出し発行時のイベント:写真撮影を終えて帰ってきた時。 */
	private int pet_message_thanksCam = 8;

	/** 吹き出し発行時のイベント:エサが落ちてこなくてペットが暇な時。 */
	private int pet_message_generic = 9;
	/** 吹き出し発行時のイベント:ペットが触られた時。 */
	private int pet_message_touch = 10;

	/** Logのタグを定数で確保 */
	private static final String TAG = "GameSurfaceView";

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

		mGestureDetector = new GestureDetector(context, this);

		/** サウンドエフェクトのインスタンス生成し準備 */
		bakubakuSE = MediaPlayer.create(context, R.raw.dog1b);

		/** レベルアップ時SEのインスタンス生成し準備 */
		levelUpSE = MediaPlayer.create(context, R.raw.level_up_se);

		/** このViewをトップにする */
		setZOrderOnTop(true);
	}

	@Override
	public void run() {
		Canvas canvas = null;
		while (thread != null) {
			long nowTime = System.currentTimeMillis();
			canvas = holder.lockCanvas();
			if (canvas != null) {
				for (int i = 0; i < camPeItems.size(); i++) {
					camPeItems.get(i).draw(canvas);
				}

				/** 衝突判定実行 */
				for (int i = 0; i < camPeItems.size(); i++) {
					if (!camPeItems.get(i).equals(myPet)) {
						judgeCollision(i);
					}

					/** ペットのレベル判定を行なう。レベルアップ可能ならば、現在のペットを削除して新しいペットに差し替える。 */
					else if (camPeItems.get(i).equals(myPet)) {

						/** ユーザーの指がペットに触れているか判定 */
						if (myPet.rectF.contains(petAmuseX, petAmuseY)) {
							CameLog.setLog(
									TAG,
									"ユーザーの指がペットに触れているか判定"
											+ myPet.rectF.contains(petAmuseX,
													petAmuseY));
							if (seOn) {
								/** SE鳴らしてもいいならばペットが喜びの声をあげる */
								myPet.pleased(this, pet_message_touch);
							}
						}

						/** ペットに触れていようがいまいが同じ位置で2回目は震えないように判定位置を画面の外にセットし直す */
						petAmuseX = viewWidth + 5;
						/** ペットに触れていようがいまいが同じ位置で2回目は震えないように判定位置を画面の外にセットし直す */
						petAmuseY = viewHeight + 5;

						/** 現在のステータスでレベルアップが可能と判定されたら、ペットをレベルアップする。 */
						if (PetLevel.judge(context, myPet)) {
							/** 現在のペットの位置を取得する */
							float nowX = camPeItems.get(i).getNowX();
							float nowY = camPeItems.get(i).getNowY();
							CameLog.setLog(TAG, "レベルアップ可能と判定");

							/** ペットステイタスの書き換えに備えてレベルアップ前のペット種別名を取得し確保しておく */
							beforePetModelNumber = myPet.getPetModelNumber();
							CameLog.setLog(TAG, "レベルアップ前のペット種別名"
									+ beforePetModelNumber + "を取得");

							/** レベル判定に基づき新レベルのペット作成 */
							updatedMyPet = PetLevel.up(context, this,
									viewWidth / 3, viewWidth / 3, 0,
									(viewWidth / 3) * 2, viewWidth, viewHeight);
							/** 新レベルのペットに現在地をセット */
							updatedMyPet.setNowX(nowX);
							updatedMyPet.setNowY(nowY);

							/** 新しいペットを作り終えたので古いペットを削除する */
							camPeItems.remove(i);
						}
					}

					/** SurfaceView上に、レーティングバー・経験値・ペット年齢・を描画するメソッドを呼び出す */
					textRectWrite(canvas);
				}

				/** レベルアップ処理を行って削除したため、ArrayListにペットが入っていないならば… */
				if (!camPeItems.contains(myPet)) {
					/** 新しいペットを作り終えたので古いペットを削除する */
					myPet = updatedMyPet;
					/**
					 * スレッドのArrayListの中身をすべて削除（バージョンUp後に、エサが消えるのに透明なエサと衝突するバグ回避策
					 * ）
					 */
					camPeItems.clear();
					/** スレッドのArrayListに新レベルのペットを追加 */
					camPeItems.add(myPet);

					if (seOn) {
						/** レベルアップ時のSEを鳴らす */
						levelUpSE.start();
					}

					/** レベルアップ後のペット型番を取得し確保しておく */
					updatedPetModelNumber = myPet.getPetModelNumber();
					CameLog.setLog(TAG, "レベルアップ後のペット種別名"
							+ updatedPetModelNumber + "を取得");
					/** レベルアップ前後のペット種別名ごとにペットステイタスを司るプレファレンスを変更しておく */
					CamPePref.savePetModelNumber(context, beforePetModelNumber,
							updatedPetModelNumber);
					/** SNS投稿や写真撮影によるレベルアップに備えて現在のペット型番を取得しプリファレンスに保存 */
					CamPePref.savePetModelNumberForUnexpectedVersionUp(context,
							myPet.getPetModelNumber());
					/** レベルアップ後のペット種別名を取得し確保しておく */
					speciesName = myPet.getPetName();
					/** SNS投稿や写真撮影によるレベルアップに備えて現在のペット種別名を取得しプリファレンスに保存 */
					CamPePref.savePetSpeciesName(context, speciesName);
					myPet.talk(this, pet_message_levelup);
					CameLog.setLog(TAG, "ペットがレベルアップ!!");
				}

				/** 画面に表示されているエサの残数が1または0になったら新たなエサを1つ生成する */
				if (camPeItems.size() <= 3) {
					/** かつ、前回エサを作った時間から1秒以上経過しているならエサを作る */
					if (nowTime > (lastEsaMakeTime + 3250)) {
						makeEsa(1);
						lastEsaMakeTime = nowTime;
					}
				}

				/** 残りエサ数が0になったらエサの獲得方法告知 */
				if ((nowFalldownEsaCnt - throwedEsa) == 0) {

					EsakokutiCnt++;

					/** 1回だけ表示 */
					if (EsakokutiCnt <= 1) {
						myPet.talk(this, pet_message_esa_zero);
						/** 世間話を開始する基準時間をセット。エサゼロになった時間から21～44秒後 */
						esaZeroTime = (System.currentTimeMillis() + ((new Random()
								.nextInt(25) + 21) * 1000));
					}

					/** 残りエサ数が0になってエサゼロになった時間から15～60秒後になっていたら世間話をできるようにする */
					if (nowTime > esaZeroTime) {
						sekenBanasi = true;
					}
				}

				if (sekenBanasi) {
					myPet.talk(this, pet_message_generic);
					/** 今、喋ったので次に備えて基準時間を再設定する。ランダムに設定。21～44秒。 */
					esaZeroTime = (nowTime + ((new Random().nextInt(25) + 21) * 1000));
					sekenBanasi = false;
				}
				holder.unlockCanvasAndPost(canvas);
			}

			nowTime = System.currentTimeMillis() - nowTime;

			if (nowTime < FPS) {
				try {
					Thread.sleep(FPS - nowTime);
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
		this.viewHeight = height;
		this.holder = holder;

		/** もし、初回起動ならば初期レベルのペットステイタスをプレファレンスに保存しておく */
		CamPePref.savePetModelNumber(context, "Pet001A", "Pet001A");

		CameLog.setLog(TAG, "Viewの幅は" + width + "。Viewの高さは" + height);

		try {
			/** 直近撮影回数をプリファレンスから取得 */
			esaCnt = CamPePref.loadNowShotCnt(context);

			// if (esaCnt == 0) {
			/** 次回用として保存されたエサ数の取得し */
			nextMakeEsaCnt = CamPePref.loadNextMakeEsaCnt(context);
			// }
			/** 直近撮影回数を取り出し終えたので直近撮影回数を0にリセット */
			CamPePref.saveNowShotCnt(context, 0);

		} catch (Exception e1) {
			e1.printStackTrace();
			CameLog.setLog(TAG, "直近撮影回数をプリファレンスから取得時に例外が発生しました。");
		}
		CameLog.setLog(TAG, "プリファレンスから次の枚数を取り出した→" + esaCnt);

		/** もしインストール直後などで直近撮影枚数がない場合、あるいは、次回用として保存されたエサ数が0ならばデフォルト値5枚をセット。 */
		if (esaCnt == -1 || (esaCnt == 0 && nextMakeEsaCnt == 0)) {
			nowFalldownEsaCnt = esaDefaultMakeCnt;
		} else if (esaCnt == 0 && nextMakeEsaCnt != 0) {
			/** 次回用として保存されたエサ数があるならばその数だけエサを生成する */
			nowFalldownEsaCnt = nextMakeEsaCnt;
		} else if (esaCnt != 0 && nextMakeEsaCnt != 0) {
			/** 次回用として保存されたエサ数があり、直近撮影枚数もあるならば、それらの合計枚数のエサを生成する */
			nowFalldownEsaCnt = (10 * esaCnt) + nextMakeEsaCnt;
		} else {
			/** それ以外の時は、落下予定のエサの数を直近撮影回数を元に設定 */
			nowFalldownEsaCnt = 10 * esaCnt;
		}

		/** さらに、Facebookやツイッターから戻ってきた時にはエサ生成数を更に20増やす */
		if (MainActivity.isReturnFb() || MainActivity.isReturnTwitter()) {
			nowFalldownEsaCnt += 20;
		}
		// CameLog.setLog(TAG, "直近撮影枚数を取得。枚数は" + esaCnt);

		/** レベル判定に基づきペット作成 */
		myPet = PetLevel.up(context, this, viewWidth / 3, viewWidth / 3, 0,
				(viewWidth / 3) * 2, viewWidth, viewHeight);
		camPeItems.add(myPet);

		/** 突然SNS投稿する場合に備えて、現在のペットの種別名をプリファレンスに保存 */
		CamPePref.savePetSpeciesName(context, myPet.getPetName());

		/** 突然SNS投稿する場合に備えて、現在のペットの型番をプリファレンスに保存 */
		CamPePref.savePetModelNumberForUnexpectedVersionUp(context,
				myPet.getPetModelNumber());

		/** 画面表示時ペットメッセージ */
		if (MainActivity.isReturnEncyc()) {
			/** 図鑑を見てくれてありがとう */
			myPet.talk(this, pet_message_thanksEncyc);
			/** お礼を言い終えたので、Booleanを初期状態に戻す */
			MainActivity.setReturnEncyc(false);
		} else if (MainActivity.isReturnFb() || MainActivity.isReturnTwitter()) {
			/** SNS投稿ありがとう。経験値を上げ終えるまではこのBoolean値はfalseにはしない！ */
			myPet.talk(this, pet_message_thanksSNS);
		} else if (MainActivity.isReturnTutorial()) {
			/** チュートリアルを見てくれてありがとう */
			myPet.talk(this, pet_message_thanksTutorial);
			/** お礼を言い終えたので、Booleanを初期状態に戻す */
			MainActivity.setReturnTutorial(false);
		} else if (MainActivity.isReturnCam()) {
			/** 写真を撮ってくれてありがとう */
			myPet.talk(this, pet_message_thanksCam);
			/** お礼を言い終えたので、Booleanを初期状態に戻す */
			MainActivity.setReturnCam(false);
		} else {
			/** それ以外のときは、飼い主歓迎メッセージを表示 */
			myPet.talk(this, pet_message_welcome);
		}

		CameLog.setLog(TAG, "飼い主歓迎メッセージを呼び出し。イベントコードは" + pet_message_welcome);

		/** エサを生成するメソッド呼び出し。初期値は1枚を設定。エサを作った時間も記録 */
		makeEsa(1);
		lastEsaMakeTime = System.currentTimeMillis();

		/** エサ落下予定表示文字取得 */
		foodCntThisTime = context.getString(R.string.number_of_food_this_time);

		/** ペット年齢取得 */
		petAge = Birthday.getAge(context);

		/** ペット種別名取得 */
		speciesName = myPet.getPetName();

		/** 突然SNS投稿する場合に備えて、現在のペットの種別名をプリファレンスに保存 */
		CamPePref.savePetSpeciesName(context, speciesName);

		/** プレイ中以外にペットがレベルアップした場合に対応するために、現在のペット型番名を取得してステイタスと比較する */
		String petModelNumber = myPet.getPetModelNumber();
		String preferenceSavedPetModelNumber = CamPePref.loadPetModelNumber(
				context, petModelNumber);
		/** 現在のステータスがnowで無いならば */
		if (!preferenceSavedPetModelNumber.equals("now")) {
			/** 直前のペットModelNumberを取得 */
			String beforePetModelNumber = CamPePref
					.loadPetModelNumberForUnexpectedVersionUp(context);
			/** 直前のペットModelNumberがインストール直後などで空でないならば */
			if (!beforePetModelNumber.equals("")) {
				/** ステイタスを保存し直す */
				CamPePref.savePetModelNumber(context, beforePetModelNumber,
						petModelNumber);
				CameLog.setLog(TAG,
						"ペットのModelNumberステイタスを保存し直した@surfaceChanged");
			}
		}

		/** 経験値取得 */
		try {
			gettedtotalEXP = CamPePref.loadTotalExp(context);
		} catch (Exception e) {
			e.printStackTrace();
			CameLog.setLog(TAG, "プリファレンスから経験値取得に失敗@onCreate");
		}

		/** facebookから戻ってきた直後なら経験値を20Up */
		if (MainActivity.isReturnFb()) {
			gettedtotalEXP += 20;
			CamPePref.saveTotalExp(context, gettedtotalEXP);
			// CameLog.setLog(TAG, "facebookから戻ってきたので経験値を次の数値にアップ→" +
			// gettedtotalEXP);
			/** エサ告知カウントを0にリセット */
			EsakokutiCnt = 0;
			/** 経験値をアップし終えたので、Booleanを初期状態に戻す */
			MainActivity.setReturnFb(false);
		}

		/** Twitterから戻ってきた直後なら経験値を20Up */
		if (MainActivity.isReturnTwitter()) {
			gettedtotalEXP += 20;
			CamPePref.saveTotalExp(context, gettedtotalEXP);
			// CameLog.setLog(TAG, "Twitterから戻ってきたので経験値を次の数値にアップ→" +
			// gettedtotalEXP);
			/** エサ告知カウントを0にリセット */
			EsakokutiCnt = 0;
			/** 経験値をアップし終えたので、Booleanを初期状態に戻す */
			MainActivity.setReturnTwitter(false);
			// /** ツイッター用写真の削除許可を与える */
			// CamPePref.saveTwitterPhDeleteOK(context, true);
		}

		CameLog.setLog(TAG, "プリファレンスから経験値取得に成功@onCreate");
		try {
			if (gettedtotalEXP == -1) {
				gettedtotalEXP = 0;
			}
			expTxt = context.getString(R.string.exp_points) + " "
					+ String.valueOf(gettedtotalEXP);
		} catch (Exception e) {
			CameLog.setLog(TAG, "プリファレンスからの経験値表示に失敗@onResume");
		}

		/**
		 * プリファレンスから3時間以内エサ獲得成功数を取得。
		 * 2015年4月7日まで、誤って累計エサ獲得数を取得していたため、ゲーム画面表示直後にはプログレスバーがフル状態となっていた模様。
		 */
		gettedThreeHoursEatCnt = CamPePref.load3hoursEatCnt(context);

		if (gettedThreeHoursEatCnt == -1) {
			gettedThreeHoursEatCnt = 0;
		}

		esaGetCnt = gettedThreeHoursEatCnt;

		/** プログレスバー最大値を3時間以内取得数が超えていた場合は取得数を最大値に設定し直す */
		if (esaGetCnt > progressMax) {
			esaGetCnt = progressMax;
		}

		thread = new Thread(this);
		thread.start();
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {

		/** 起動していたペット種別名をプリファレンスに保存 */
		CamPePref.savePetSpeciesName(context, speciesName);

		/** ペットステイタスの書き換えに備えてレベルアップ前のペット種別名を取得しプリファレンスに保存しておく */
		beforePetModelNumber = myPet.getPetModelNumber();
		CameLog.setLog(TAG, "レベルアップ前のペット種別名" + beforePetModelNumber + "を取得");
		CamPePref.savePetModelNumberForUnexpectedVersionUp(context,
				beforePetModelNumber);

		/** 落下させたエサ数を初期化する */
		throwedEsa = 0;
		/** 落下予定のエサ数も初期化する */
		nowFalldownEsaCnt = 0;
		/** エサゼロ時間を極大にしてエサが出なくする（初期化？） */
		esaZeroTime = 2145888061L;
		/** 世間話をできなくする */
		sekenBanasi = false;

		/** 起動ステイタスを取得する */
		String startStatus = CamPePref.loadStartStatus(context);
		/**
		 * 初めての起動でないならばViewの幅をPreferenceに保存する。
		 * */
		if (!startStatus.equals("notFirst")) {
			CamPePref.saveViewWidth(context, viewWidth);
			/** 起動済みの旨プリファレンスに情報を保存 */
			CamPePref.saveStartStatus(getContext());
			CameLog.setLog(TAG, "起動済みの旨プリファレンスに情報を保存");
		}

		/**
		 * ペットやエサが入ったArrayListを空にする。これにより、
		 * SNS投稿などから戻ってきた時にエサが無いのに何かに衝突して経験値がUpするバグを防げるはず。
		 */
		camPeItems.clear();
		CameLog.setLog(TAG, "ペットやエサが入ったArrayListを空にしたので数は" + camPeItems.size());

		/** camPeItemsの全Threadを停止する。 */
		for (int j = 0; j < camPeItems.size(); j++) {
			camPeItems.get(j).stopCamPeItemThread();
		}

		/** このThreadを停止する */
		thread = null;

	}

	/**
	 * SurfaceView上に、レーティングバー・経験値・ペット年齢などを描画するメソッド。
	 *
	 * @param canvas
	 */
	private void textRectWrite(Canvas canvas) {
		/** プログレスバーの記述 */
		Paint paint = new Paint();
		/** 文字にテーマカラー設定 */
		paint.setColor(themeColor);
		/** Aliasを設定 */
		paint.setAntiAlias(true);

		/** テキスト設定 sp */
		paint.setTextSize(viewWidth / 26);
		paint.setTypeface(Typeface.DEFAULT_BOLD);
		/** テキスト右寄せ */
		paint.setTextAlign(Paint.Align.RIGHT);

		/** 経験値表示 */
		canvas.drawText(expTxt, (viewWidth / 40) * 39, (viewHeight / 80) * 4,
				paint);

		/** エサステイタス表示のため朝昼晩判定を行うメソッドを呼び出す。 */
		gameTimeJudge();

		/** 残りエサ数表示 */
		canvas.drawText(foodCntThisTime + "    "
				+ (nowFalldownEsaCnt - throwedEsa), (viewWidth / 40) * 39,
				(viewHeight / 160) * 15, paint);

		/** ペット名表示表示 */
		Resources res = this.getResources();
		canvas.drawText(res.getString(R.string.pet_species_name_title) + " "
				+ speciesName, (viewWidth / 40) * 39,
				(viewHeight - (viewWidth / 40)), paint);

		/** テキスト右寄せ */
		paint.setTextAlign(Paint.Align.LEFT);
		// CameLog.setLog(TAG, "獲得済みエサの数@textRectWriteは" + esaGetCnt);

		/** 誕生日表示 */
		canvas.drawText(petAge, (viewWidth / 40),
				(viewHeight - (viewWidth / 40)), paint);

		/** 上記に基づき、エサステイタス表示 */
		canvas.drawText(esaStatus + "    " + esaGetCnt + "/" + progressMax,
				viewWidth / 40, (viewHeight / 160) * 15, paint);

		/**
		 * 進捗部分を塗る
		 *
		 * 第3引数は、外枠のdrawRect開始位置（外枠のdrawRectの第1引数）分を足さないと外枠とずれてしまう。ここでは、
		 * viewWidth / 40がそれに相当する。 また、
		 * 第3引数はみなfloatに明示的にキャストしておかないと小さい差異が結果的に大きい差になってプログレスバーが最後まで塗れないので要注意。
		 */
		RectF ratingRect = new RectF(
				(float) viewWidth / 40,
				(float) viewHeight / 40,
				(float) ((((float) ((float) (viewWidth / 2) - (float) (viewWidth / 40)) / (float) progressMax) * (float) esaGetCnt) + (float) (viewWidth / 40)),
				(float) (viewHeight / 80) * 4);

		// CameLog.setLog(
		// TAG,
		// "塗りつぶした横幅は"
		// + ((((viewWidth / 2) - (viewWidth / 40)) / progressMax) *
		// esaGetCnt));
		// CameLog.setLog(TAG, "プログレスバー最大値は" + progressMax + "エサ獲得数は" +
		// esaGetCnt);
		/** 進捗部分描画実行 */
		canvas.drawRect(ratingRect, paint);
		// }
		// CameLog.setLog(TAG, "レーティング最大値は" + ratingMax + "。獲得済みエサ数は" +
		// esaGetCnt);
		/** 進捗バー外枠にテーマカラー設定 */
		paint.setColor(themeColor);
		/** 進捗バー外枠設定 */
		paint.setStyle(Style.STROKE);
		/**
		 * 進捗バー外枠描画実行 外枠の第3引数は開始位置分を引かないとずれる。ここではviewWidth / 40
		 * */
		canvas.drawRect((float) viewWidth / 40, (float) viewHeight / 40,
				(float) (viewWidth / 2), (float) (viewHeight / 80) * 4, paint);
		// CameLog.setLog(TAG, "進捗バー外枠横幅は" + ((viewWidth / 2) - (viewWidth /
		// 40)));
	}

	/**
	 * エサステイタス表示のため朝昼晩判定を行うメソッド。 画面描画の度に判定を行うかは悩ましいところ…
	 */
	public void gameTimeJudge() {
		/** 食事時間判定メソッドを呼び出す */
		String MealTime = MealTimeJudgment.get();

		if (MealTime.equals("Morning")) {
			/** 朝時間帯なら朝食表示文字を準備 */
			esaStatus = context.getString(R.string.esa_status_morning);
		}

		if (MealTime.equals("Lunch")) {
			/** 昼時間帯なら昼食表示文字を準備 */
			esaStatus = context.getString(R.string.esa_status_lunch);
		}
		if (MealTime.equals("Snack")) {
			/** おやつ時間帯ならおやつ食表示文字を準備 */
			esaStatus = context.getString(R.string.esa_status_snack);
		}
		if (MealTime.equals("Dinner")) {
			/** 夜時間帯なら晩御飯表示文字を準備 */
			esaStatus = context.getString(R.string.esa_status_dinner);
		}
		if (MealTime.equals("bedtimeSnack")) {
			/** 夜食時間帯なら夜食表示文字を準備 */
			esaStatus = context.getString(R.string.esa_status_bedtimeSnack);
		}
	}

	/** 衝突判定を実行するメソッド */
	public void judgeCollision(int i) {
		// CameLog.setLog(TAG, "myPetのrectは"
		// + myPet.getRectF().toString());
		// CameLog.setLog(TAG, "Esaのrectは"
		// + camPeItems.get(i).getRectF().toString());
		if (RectF.intersects(myPet.getRectF(), camPeItems.get(i).getRectF())) {
			int hitCnt;
			hitCnt = camPeItems.get(i).getHitCnt();
			hitCnt++;
			camPeItems.get(i).setHitCnt(hitCnt);
			camPeItems.get(i).returnAfterKrush();

			if (hitCnt >= 2) {
				/** プリファレンスから3時間以内エサ獲得成功数を取得 */
				gettedThreeHoursEatCnt = CamPePref.load3hoursEatCnt(context);
				if (gettedThreeHoursEatCnt == -1) {
					gettedThreeHoursEatCnt = 0;
				}
				esaGetCnt = gettedThreeHoursEatCnt;
				/**
				 * プログレスバー最大値になるまで3時間以内エサ獲得成功数をUpする。
				 */
				if (esaGetCnt < progressMax) {
					esaGetCnt++;
				}
				/** 新たな累計エサ獲得成功回数と新たな累計経験値をプリファレンスに保存 */
				CamPePref.save3hoursEatCnt(context, esaGetCnt);
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
				/** インストール直後で累積経験値が-1の場合は0に初期化 */
				try {
					if (gettedtotalEXP == -1) {
						gettedtotalEXP = 0;
					}

					/** エサゲットに伴い新たな累計経験値の算出 */
					gettedtotalEXP++;

				} catch (Exception e) {
					CameLog.setLog(TAG, "プリファレンスからの経験値表示に失敗@onResume");
				}
				/** 今回のエサ獲得数がプログレスバー最大値になったら… */
				if (progressMax == esaGetCnt) {
					/** 前回、満腹度100による経験値Upを実行した時間を取り出す */
					long manpukuTime = CamPePref.loadManpukuTime(context);
					// ManpukuCnt++;
					/** 前回、経験値Upを実行した時間から3時間が経過しているならば… */
					if (System.currentTimeMillis() > (manpukuTime + 10800000L)) {
						myPet.talk(this, pet_message_satiety);
						/** お腹いっぱいご褒美として経験値も20Up */
						gettedtotalEXP += 20;
						/** 満腹となった時間を新たに現在時間で登録する。 */
						CamPePref.saveManpukuTime(context);
					}
				}

				/** エサゲット後の経験値画面表示更新 */
				expTxt = context.getString(R.string.exp_points) + " "
						+ String.valueOf(gettedtotalEXP);

				/** 新たな累計エサ獲得成功回数と新たな累計経験値をプリファレンスに保存 */
				CamPePref.saveTotalExpAndEsaGetCnt(context,
						gettedTotalEsaGetCnt, gettedtotalEXP);

				CameLog.setLog(TAG, "エサの現在位置は"
						+ camPeItems.get(i).getRectF().top);
				/** 獲得したエサを削除する */
				camPeItems.remove(i);
			}

			/** エサに衝突したらペットを反転させる */
			myPet.returnAfterKrush();
			CameLog.setLog(TAG, "現在の残数は" + camPeItems.size());
			if (seOn) {
				bakubakuSE.start();
			}
			// CameLog.setLog(TAG, "Petとエサの接触を検知しました!");
		} else if (camPeItems.get(i).getRectF().top > viewHeight) {
			/** 画面外に出て行ったエサを削除する */
			camPeItems.remove(i);
		}
	}

	/** エサを生成するメソッド */
	public void makeEsa(int makeEsaCnt) {

		/** 餌写真取得準備 */
		CamPePh camPePh = new CamPePh();
		/**
		 * エサ写真を格納するArrayListを用意しておく
		 */
		esaPhList = new ArrayList<Bitmap>();

		/** 落下させたエサの数が今回落下予定のエサの数未満ならばエサを作る */
		if (throwedEsa <= (nowFalldownEsaCnt - 1)) {

			if (esaCnt <= 0) {
				esaCnt = 1;
			}

			/** 引数でエサを生成 */
			esaPhList = camPePh.get(context, makeEsaCnt, esaCnt);

			CameLog.setLog(TAG, "コンストラクタにて画像の読み込み完了。餌Phは" + esaPhList.size()
					+ "枚");

			for (int i = 0; i < esaPhList.size(); i++) {
				/** エサのX初期位置を生成 */
				int defaultX = (int) ((esaDefaultX * ((Math.random() * 10) + 1)) * ((viewWidth / 128) * 10));

				/** 画面幅を超えた場合の調整 */
				if (defaultX > viewWidth) {
					defaultX = (int) ((defaultX - viewWidth) + ((viewWidth / 128) * ((Math
							.random() * 5) + 1)));
					CameLog.setLog(TAG, "defaultXが画面を超えたので位置修正。修正位置は"
							+ defaultX);
				}

				if (defaultX < (viewWidth / 5)) {
					defaultX = (int) (defaultX + (viewWidth / 128) * 32);
					CameLog.setLog(TAG, "defaultXが画面左に寄り過ぎたので位置修正。修正位置は"
							+ defaultX);
				}

				CameLog.setLog(TAG, "Esaのコンストラクタに渡すdefaultXは" + defaultX);

				camPeItems.add(new Esa(esaPhList.get(i), viewWidth / 13,
						viewWidth / 13, defaultX, esaDefaultY, viewWidth,
						viewHeight, esaKasokudo
								* (new Random().nextInt(esaKasokudoMax) + 1)));
				/** 落下させたエサの数をインクリメント */
				throwedEsa++;

				/** 未生成残りエサ数を保存（次回用） */
				nextMakeEsaCnt = nowFalldownEsaCnt - throwedEsa;
				// CameLog.setLog(TAG, "終了直前の今回降ってくる予定のエサ総数は" +
				// nowFalldownEsaCnt +"。1ゲームですでに落下させたエサ数は" + throwedEsa);
				CamPePref.saveNextMakeEsaCnt(context, nextMakeEsaCnt);
				CameLog.setLog(TAG, "未生成残りエサ数" + nextMakeEsaCnt + "を次回用に保存");
			}
			CameLog.setLog(TAG, "複数写真使用で餌作成");
		}
	}

	/**
	 * 現在のペットの名前を取得するメソッド。
	 *
	 * @return speciesName
	 */
	public static String getSpeciesName() {
		return speciesName;
	}

	@Override
	public boolean onDown(MotionEvent e) {
		return false;
	}

	@Override
	public void onShowPress(MotionEvent e) {
	}

	@Override
	public boolean onSingleTapUp(MotionEvent e) {
		petAmuseY = e.getY();
		petAmuseX = e.getX();
		return true;
	}

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
			float distanceY) {
		return false;
	}

	@Override
	public void onLongPress(MotionEvent e) {
	}

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
		// CameLog.setLog(TAG, "onFlingの引数はe1が" + e1 + "e2が" + e2 + "velocityXが"
		// + velocityX + "velocityYが" + velocityY);

		/** フリックから移動量を取り出す。引数最後の数字を小さくすると動きが速くなる */
		usertouchedX = (e2.getX() - e1.getX())
				/ (float) ((viewWidth / 128) * 11);
		usertouchedY = (e2.getY() - e1.getY())
				/ (float) ((viewWidth / 128) * 11);

		// CameLog.setLog(TAG, "ペットがフリックで移動するX軸距離を" + usertouchedX + "にセット");
		// CameLog.setLog(TAG, "ペットがフリックで移動するY軸距離を" + usertouchedY + "にセット");

		if (flickOk) {
			/** petに移動量をセット */
			myPet.setPetMoveSize(usertouchedX, usertouchedY);
		}
		return true;
	}

	/**
	 * 以下のサイトを参考に、フリックごとにきちんと処理するように変更する。
	 *
	 * http://lostlinksearch.net/blog/2011/03/android-%E3%83%95%E3%83%AA%E3%83%
	 * 83 %E3%82%AF%E6%93%8D%E4%BD%9C%E3%81%AE%E5%AE%9F%E8%A3%85%E6%96%B9%E6%B3%
	 * 95/
	 */
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		/** GestureDetectorにすべてを任せる */
		mGestureDetector.onTouchEvent(event);
		// CameLog.setLog(TAG, "onTouchEventでのMotionEventのxは" + event.getRawX()
		// + "onTouchEventでのMotionEventのyは" + event.getRawY());
		return true;
	}

	/**
	 * @param flickOk
	 *            セットする flickOk
	 */
	public static void setFlickOk(boolean flickOk) {
		GameSurfaceView.flickOk = flickOk;
	}

	public static boolean isSeOn() {
		return seOn;
	}

	public static void setSeOn(boolean seOn) {
		GameSurfaceView.seOn = seOn;
	}

}
