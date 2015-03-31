package jp.egaonohon.camerapet;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Point;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

/**
 * カメラペットのゲーム画面Activity（=MainActivity）のクラス。
 * @author 1107AND
 *
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
	/** タブレットにおける不具合調査用にディスプレイのインスタンス生成 */
	private int windows_width;
	private int window_height;

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
//		mp.start(); // SEを鳴らす
		bgmOn = true;

		/** 画面のWidthを取得してみる（タブレットでの不具合対策） */
		Display display = getWindowManager().getDefaultDisplay();
		Point size = new Point();
		display.getSize(size);
		this.windows_width = size.x;    // width
		this.window_height = size.y;    // height
		CameLog.setLog(TAG, "Activityのwidthは" + windows_width + "Activityのheightは" +window_height);
	}


	@Override
	protected void onResume() { // アクティビティが動き始める時呼ばれる
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
	}

	@Override
	protected void onPause() { // アクティビティの動きが止まる時呼ばれる
		super.onPause();
		// AcSensor.Inst().onPause();// 中断時にセンサーを止める
		/** BGMの一時停止 */
		mp.pause();
		CameLog.setLog(TAG, "onPause");
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

		/** Facebookへ投稿実行 */
		SnsBtn.goFacebook(this);
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

	/**
	 * Twitter投稿メソッド。
	 *
	 * @param v
	 */
	public void onClickTwitterBtn(View v) {
		if (bgmOn) {
			mp2.start(); // SEを鳴らす
		}

		/** Twitterから戻ってくることを示す */
		returnTwitter = true;

		/** Twitterへ投稿実行 */
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

		/**
		 * 画面移動要求を格納したインテントを作成する。 第一引数に自身(this)を設定 第二引数に移動先のクラス名を指定
		 */
		Intent intent = new Intent(MainActivity.this, CameraActivity.class);

		/**
		 * Activity.startActivity()の第一引数にインテントを指定することで画面移動が行われる。
		 */
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
		} else {
			mp.start();
			bgmOn = true;
		}
	}

	/**
	 * ペット図鑑呼び出しメソッド。
	 *TutorialActivity
	 * @param v
	 */
	public void onClickEncyclopedia(View v) {
		if (bgmOn) {
			mp2.start(); // SEを鳴らす
		} else {
		}

		/**
		 * 画面移動要求を格納したインテントを作成する。 第一引数に自身(this)を設定 第二引数に移動先のクラス名を指定
		 */
		Intent intent = new Intent(MainActivity.this, Encyc01Activity.class);

		/**
		 * Activity.startActivity()の第一引数にインテントを指定することで画面移動が行われる。
		 */
		startActivity(intent);
	}

	/**
	 * チュートリアル呼び出しメソッド。
	 * @param v
	 */
	public void onClickTutorial(View v) {
		if (bgmOn) {
			mp2.start(); // SEを鳴らす
		} else {
		}

		/** Tutorialから戻ってくることを示す */
		returnTutorial = true;

		/**
		 * 画面移動要求を格納したインテントを作成する。 第一引数に自身(this)を設定 第二引数に移動先のクラス名を指定
		 */
		Intent intent = new Intent(MainActivity.this, TutorialFirstActivity.class);

		/**
		 * Activity.startActivity()の第一引数にインテントを指定することで画面移動が行われる。
		 */
		startActivity(intent);
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
}