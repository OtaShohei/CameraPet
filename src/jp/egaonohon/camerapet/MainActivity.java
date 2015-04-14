package jp.egaonohon.camerapet;

import java.util.Locale;

import jp.basicinc.gamefeat.android.sdk.controller.GameFeatAppController;
import jp.egaonohon.camerapet.encyc.Encyc01Activity;
import jp.egaonohon.camerapet.tutorial.TutorialFirstActivity;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

/**
 * カメラペットのゲーム画面Activity（=MainActivity）のクラス。
 *
 * @author OtaShohei
 */
public class MainActivity extends Activity {

	/** SurfaceView の参照 */
	GameSurfaceView msv;
	/** BGM用変数 */
	private MediaPlayer mp, mp2;
	private boolean bgmOn = true;
	/** 直近撮影枚数（=エサの数） */
	static int esaCnt;
	/** エサ食べさせ成功回数 */
	private int gettedEsaCnt;

	/** Logのタグを定数で確保 */
	private static final String TAG = "MainActivity";
	// /** タブレットにおける不具合調査用にディスプレイのインスタンス生成 */
	// private int windows_width;
	// private int window_height;

	/** CameraActivityから戻ってきた直後を判定するBoolean */
	private static boolean returnCam = false;
	/** Facebookから戻ってきた直後を判定するBoolean */
	private static boolean returnFb = false;
	/** Twitterから戻ってきた直後を判定するBoolean */
	private static boolean returnTwitter = false;
	/** Tutorialから戻ってきた直後を判定するBoolean */
	private static boolean returnTutorial = false;
	/** 図鑑から戻ってきた直後を判定するBoolean */
	private static boolean returnEncyc = false;

	/** 言語設定ごとの広告表示のための言語設定確認 */
	private String locale;

	/** Admob用のインスタンス */
	private AdView adView;
	/** GameFeat用のインスタンス */
	private GameFeatAppController gfAppController;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		/** フルスクリーンに設定 */
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);//
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);

		/** SurfaceViewの生成 */
		msv = (GameSurfaceView) findViewById(R.id.petSpace);

		/** BGMインスタンス生成し準備 */
		mp = MediaPlayer.create(MainActivity.this, R.raw.honwaka);
		mp2 = MediaPlayer.create(MainActivity.this, R.raw.poka);
		/** BGMスタート */
		bgmOn = true;

		// /** 画面のWidthを取得してみる（タブレットでの不具合対策） */
		// Display display = getWindowManager().getDefaultDisplay();
		// Point size = new Point();
		// display.getSize(size);
		// this.windows_width = size.x; // width
		// this.window_height = size.y; // height
		// CameLog.setLog(TAG, "Activityのwidthは" + windows_width
		// + "Activityのheightは" + window_height);

		/** 広告表示設定のため、ユーザーの設定言語を取得 */
		locale = Locale.getDefault().toString();

		/** 日本語設定でないならば */
		if (!locale.equals("ja_JP")) {
			// ////////
			// 以下、Admob用記述
			// http://skys.co.jp/archives/1579
			// を参考にEclipseへのメモリ割り当てを増やさないとEclipseがフリーズするので要注意。
			// ////////

			/** adView を作成する */
			adView = new AdView(this);
			adView.setAdUnitId("ca-app-pub-1135628131797223/1945135213");
			adView.setAdSize(AdSize.SMART_BANNER);

			/**
			 * 属性 android:id="@+id/adMobSpace" が与えられているものとしてLinearLayout
			 * をルックアップする
			 */
			LinearLayout layout = (LinearLayout) findViewById(R.id.adMobSpace);

			/** adView を追加する */
			layout.addView(adView);

			// /** 一般的なリクエストを行う */
			// AdRequest adRequest = new AdRequest.Builder().build();

			/** 【教室公開用コメントアウト】 */
			/** テスト用のリクエストを行う */
			AdRequest adRequest = new AdRequest.Builder()
			/** エミュレータ */
			.addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
			/** Nexus */
			.addTestDevice("9F62663AFEF7E4EC5B3F231A4AB93A9C")
			/** Motorola */
			.addTestDevice("F8ACFF2BF5F49E1CD65848BA4BC6E0AD")
			/** 広告対象を女性に */
			.setGender(AdRequest.GENDER_FEMALE).build();

			/** 広告リクエストを行って adView を読み込む */
			adView.loadAd(adRequest);
		} else {
			// //////
			// 以下、gamefeat用記述
			// //////
			/** GFコントローラ */
			gfAppController = new GameFeatAppController();
		}
	}

	/**
	 * アクティビティが動き始める時呼ばれる。ここで以前のデータを呼び出すのがいいかも
	 */
	@Override
	protected void onResume() { //
		super.onResume();

		/** BGMの制御 */
		if (bgmOn) {
			mp.start();
			mp.setLooping(true);
			bgmOn = true;
		} else {
			mp.pause();
			bgmOn = false;
		}
		/** NotificationManager用に、現在起動中である旨、プリファレンスに登録 */
		CamPePref.saveMainActivityOperationStatus(this);

		/** 日本語以外の場合は */
		if (!locale.equals("ja_JP")) {
			/** Admobの一時停止 */
			adView.resume();
		}
	}

	/**
	 * アクティビティの動きが止まる時呼ばれる。ここで、ゲーム中のデータをすべて保存した方がいいかも。onDestroyだと…
	 * 途中で電話がかかってきた場合にはonStopまでしか来ない（onDestroyにこない）。
	 * 電話終了後はonStartに戻ってくるのでデータが一回前のデータしか保存されないので大変なことになってしまう。
	 * 保存と読み込みは、onResumeとonPause。あるいは、onStartとonStop。対でやるようにしておくとトラブらない。
	 * */
	@Override
	protected void onPause() {
		super.onPause();
		/** BGMの一時停止 */
		mp.pause();

		/** 日本語以外の場合は */
		if (!locale.equals("ja_JP")) {
			/** Admobの一時停止 */
			adView.pause();
		}

		CameLog.setLog(TAG, "onPause");
	}

	/*
	 * (非 Javadoc)
	 *
	 * @see android.app.Activity#onStart()
	 */
	@Override
	protected void onStart() {
		super.onStart();
		/** Google Analytics用の記述 */
		Tracker t = ((App) getApplication())
				.getTracker(App.TrackerName.APP_TRACKER);
		t.setScreenName(this.getClass().getSimpleName());
		t.send(new HitBuilders.AppViewBuilder().build());

		if (locale.equals("ja_JP")) {
			/** 【教室公開用コメントアウト】 */
			/**
			 * GAME FEAT広告設定初期化 初期化コードの引数は次の通り。 activateGF(【Activity名】.this,
			 * カスタム広告の使用, アイコン広告の使用, 全画面広告の使用);
			 */
			gfAppController.activateGF(MainActivity.this, true, false, false);
		}
	}

	@Override
	protected void onStop() {
		super.onStop();
		/** NotificationManager用に、現在起動中でない旨、プリファレンスに登録 */
		CamPePref.saveMainActivityNotWorkStatus(this);

		/** AlarmManager & NotificationManagerを動かすメソッドを呼び出す */
		PetAlarmBroadcastReceiver.set(this);
		CameLog.setLog(TAG, "onStop");
	}

	/**
	 * Activityの最後に呼び出される。
	 */
	@Override
	protected void onDestroy() {
		super.onDestroy();

		/** 日本語以外の場合は */
		if (!locale.equals("ja_JP")) {
			/** Admobの破棄 */
			adView.destroy();
		}

		CameLog.setLog(TAG, "onDestroy");
		/** Activityをしっかり消去 */
		finish();
	}

	// //////////////////////////////////////////////////////////////////////////////////
	// 以下、非オーバーライド系メソッド。
	// //////////////////////////////////////////////////////////////////////////////////

	/**
	 * Facebook投稿メソッド。
	 *
	 * @param v
	 */
	public void onClickFacebookBtn(View v) {
		if (bgmOn) {
			mp2.start(); // SEを鳴らす
		}
		/** Facebookから戻ってくることを示す */
		returnFb = true;
		/** 現在のペットの種類名を取得して、Facebookへ投稿実行 */
		SnsBtn.goFacebook(this);
	}

	/**
	 * Twitter投稿メソッド。
	 *
	 * @param v
	 */
	public void onClickTwitterBtn(View v) {
		if (bgmOn) {
			/** SEを鳴らす */
			mp2.start();
		}
		/** Twitterから戻ってくることを示す */
		returnTwitter = true;
		/** ツイッター投稿メソッドを呼び出し */
		SnsBtn.goTwitter(this);
	}

	/**
	 * カメラ機能呼び出しメソッド。
	 *
	 * @param v
	 */
	public void onClickGoCamBtn(View v) {
		if (bgmOn) {
			mp2.start(); // SEを鳴らす
		}
		/** CameraActivityから戻ってくることを示す */
		returnCam = true;
		/** 画面移動要求を格納したインテントを作成する。 第一引数に自身(this)を設定 第二引数に移動先のクラス名を指定 */
		Intent intent = new Intent(MainActivity.this, CameraActivity.class);
		/** Activity.startActivity()の第一引数にインテントを指定することで画面移動が行われる。 */
		startActivity(intent);
	}

	/**
	 * BGMオン・オフメソッド。
	 *
	 * @param v
	 */
	public void onClickSoundBtn(View v) {
		if (bgmOn) {
			mp2.start(); // SEを鳴らす
			mp.pause();
			bgmOn = false;
			/** GAMEのSE音もならないようにする */
			GameSurfaceView.setSeOn(false);
		} else {
			mp.start();
			bgmOn = true;
			/** GAMEのSE音もなるようにする */
			GameSurfaceView.setSeOn(true);
		}
	}

	/**
	 * ペット図鑑呼び出しメソッド。 TutorialActivity
	 *
	 * @param v
	 */
	public void onClickEncyclopedia(View v) {
		if (bgmOn) {
			/** SEを鳴らす */
			mp2.start();
		} else {
		}
		/** 図鑑から戻ってくることを示す */
		returnEncyc = true;
		/** 画面移動要求を格納したインテントを作成する。 第一引数に自身(this)を設定 第二引数に移動先のクラス名を指定 */
		Intent intent = new Intent(MainActivity.this, Encyc01Activity.class);
		/** BGMオンオフ状態を保持してインテントを出せるようにputextra */
		if (bgmOn) {
			intent.putExtra("bgmOn", "true");
		} else {
			intent.putExtra("bgmOn", "false");
		}
		/** Activity.startActivity()の第一引数にインテントを指定することで画面移動が行われる。 */
		startActivity(intent);
	}

	/**
	 * チュートリアル呼び出しメソッド。
	 *
	 * @param v
	 */
	public void onClickTutorial(View v) {
		if (bgmOn) {
			mp2.start(); // SEを鳴らす
		} else {
		}
		/** Tutorialから戻ってくることを示す */
		returnTutorial = true;
		/** 画面移動要求を格納したインテントを作成する。 第一引数に自身(this)を設定 第二引数に移動先のクラス名を指定 */
		Intent intent = new Intent(MainActivity.this,
				TutorialFirstActivity.class);
		/** Activity.startActivity()の第一引数にインテントを指定することで画面移動が行われる。 */
		startActivity(intent);
	}

	/** SNSポイントを付与するメソッド */
	public void giveSNSPoints() {
		/** 現在の経験値を取得 */
		int gettedtotalEXP = CamPePref.loadTotalExp(this);
		/** ポイントを付与する */
		int newTotalExp = 8 + gettedtotalEXP;
		/** 加算したポイントをプリファレンスに保存する */
		CamPePref.saveTotalExp(this, newTotalExp);
	}

	public Bitmap getScreenBitmap(View view) {
		return getViewBitmap(view.getRootView());
	}

	public Bitmap getViewBitmap(View view) {
		view.setDrawingCacheEnabled(true);
		Bitmap cache = view.getDrawingCache();
		if (cache == null) {
			return null;
		}
		Bitmap bitmap = Bitmap.createBitmap(cache);
		view.setDrawingCacheEnabled(false);
		return bitmap;
	}

	/** Cameraから戻ってきたかどうかの判定を他のクラスに与えるメソッド */
	public static boolean isReturnCam() {
		return returnCam;
	}

	/** facebookから戻ってきたかどうかの判定を他のクラスに与えるメソッド */
	public static boolean isReturnFb() {
		return returnFb;
	}

	/** Twitterから戻ってきたかどうかの判定を他のクラスに与えるメソッド */
	public static boolean isReturnTwitter() {
		return returnTwitter;
	}

	/** チュートリアルから戻ってきたかどうかの判定を他のクラスに与えるメソッド */
	public static boolean isReturnTutorial() {
		return returnTutorial;
	}

	/** facebookから戻ってきたかどうかの判定を他のクラスから変更するメソッド */
	public static void setReturnFb(boolean returnFb) {
		MainActivity.returnFb = returnFb;
	}

	/** Twitterから戻ってきたかどうかの判定を他のクラスから変更するメソッド */
	public static void setReturnTwitter(boolean returnTwitter) {
		MainActivity.returnTwitter = returnTwitter;
	}

	/** チュートリアルから戻ってきたかどうかの判定を他のクラスから変更するメソッド */
	public static void setReturnTut(boolean returnTut) {
		MainActivity.returnTutorial = returnTut;
	}

	/**
	 * @return returnEncyc
	 */
	public static boolean isReturnEncyc() {
		return returnEncyc;
	}

	/**
	 * @param returnEncyc
	 *            セットする returnEncyc
	 */
	public static void setReturnEncyc(boolean returnEncyc) {
		MainActivity.returnEncyc = returnEncyc;
	}

	/**
	 * @param returnCam
	 *            セットする returnCam
	 */
	public static void setReturnCam(boolean returnCam) {
		MainActivity.returnCam = returnCam;
	}

	/**
	 * @param returnTutorial
	 *            セットする returnTutorial
	 */
	public static void setReturnTutorial(boolean returnTutorial) {
		MainActivity.returnTutorial = returnTutorial;
	}

	public boolean isBgmOn() {
		return bgmOn;
	}

	public void setBgmOn(boolean bgmOn) {
		this.bgmOn = bgmOn;
	}

}