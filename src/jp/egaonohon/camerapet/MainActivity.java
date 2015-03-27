package jp.egaonohon.camerapet;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
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
	/** CameraActivityから戻ってきた直後を判定するBoolean */
	private static boolean returnCam = false;
	private static boolean returnFb = false;
	private static boolean returnTw = false;

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
		mp.setLooping(true);
		mp.start(); // SEを鳴らす
		bgmOn = true;

	}


	@Override
	protected void onResume() { // アクティビティが動き始める時呼ばれる
		super.onResume();

		/** BGMの制御 */
		if (bgmOn) {
			mp.start();
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
		returnTw = true;

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
	 *
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
		Intent intent = new Intent(MainActivity.this, GetPh.class);

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
	public static boolean isReturnTw() {
		return returnTw;
	}
}