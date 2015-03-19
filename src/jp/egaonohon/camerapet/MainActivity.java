package jp.egaonohon.camerapet;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;

/**
 * CameraPetのMainActivityのクラスです。 
 *
 * @author 1107AND
 *
 */
public class MainActivity extends Activity {

	/** BGM用変数 */
	private MediaPlayer mp, mp2;
	private boolean bgmOn = true;
	/** Logのタグを定数で確保 */
	private static final String TAG = "MainActivity";
	/** CameraActivityから戻ってきた直後を判定するBoolean */
	private boolean returnCam = false;
	private boolean returnFb = false;
	private boolean returnTw = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		/** BGMインスタンス生成し準備 */
		mp = MediaPlayer.create(MainActivity.this, R.raw.honwaka);
		mp2 = MediaPlayer.create(MainActivity.this, R.raw.poka);

		/** BGMスタート */
		mp.setLooping(true);
		mp.start(); // SEを鳴らす
		bgmOn = true;
	}

	@Override
	protected void onResume() {
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

	/** Cameraへの移動や他アプリへの遷移時にBGMの停止などを行う。 */
	@Override
	protected void onPause() {
		super.onPause();

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
		 * 撮影枚数をintentに詰め込んでGetPhクラスでその分を取得するように修正する。
		 */
		Intent intent = new Intent(MainActivity.this, GetPh.class);

		/**
		 * Activity.startActivity()の第一引数にインテントを指定することで画面移動が行われる。
		 */
		startActivity(intent);
	}
}