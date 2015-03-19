package jp.egaonohon.camerapet;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

/**
 * CameraPetのMainActivityのクラスです。 onClickFacebookBtnとonClickTwitterBtnボタンは要差し替え。
 *
 * @author 1107AND
 *
 */
public class MainActivity extends Activity {

	private final int FACEBOOK_ID = 0;
	private final int TWITTER_ID = 1;
	/** 直近撮影回数 */
	private Integer intShotCnt = 0;
	/** BGM用変数 */
	private MediaPlayer mp, mp2;
	private boolean bgmOn = true;
	/** Logのタグを定数で確保 */
	private static final String TAG = "MainActivity";
	/** CameraActivityから戻ってきた直後を判定するBoolean */
	private boolean returnCam = false;
	private boolean returnFb = false;
	private boolean returnTw = false;
	/** SNS連携用のメンバ変数 */
	private final String[] sharePackages = { "com.facebook.katana",
			"com.twitter.android" };

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
		
//		getLoaderManager().initLoader(0, null, this);

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

		if (isShareAppInstall(FACEBOOK_ID)) {
			Intent intent = new Intent();
			intent.setAction(Intent.ACTION_SEND);
			intent.setPackage(sharePackages[FACEBOOK_ID]);
			intent.setType("text/plain");
			/**
			 * 　URLは、CameraPetのダウンロードページに差し替えること!
			 */
			intent.putExtra(Intent.EXTRA_TEXT,
					"https://play.google.com/store/apps/");
			startActivity(intent);
		} else {
			shareAppDl(FACEBOOK_ID);
		}
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

		if (isShareAppInstall(TWITTER_ID)) {
			Intent intent = new Intent();
			intent.setAction(Intent.ACTION_SEND);
			intent.setPackage(sharePackages[TWITTER_ID]);
			intent.setType("image/png");
			/**
			 * 　URLは、CameraPetのダウンロードページに差し替えること!
			 */
			intent.putExtra(Intent.EXTRA_TEXT,
					"Androidアプリ「CameraPet」で飼っているペットです！"
							+ "https://play.google.com/store/apps/");
			startActivity(intent);
		} else {
			shareAppDl(TWITTER_ID);
		}
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

	/**
	 * アプリがインストールされているかチェックするメソッド。
	 *
	 * @param shareId
	 * @return
	 */
	private Boolean isShareAppInstall(int shareId) {
		try {
			PackageManager pm = getPackageManager();
			pm.getApplicationInfo(sharePackages[shareId],
					PackageManager.GET_META_DATA);
			return true;
		} catch (NameNotFoundException e) {
			/**
			 * エラー発生時のトースト。
			 */
			Toast.makeText(this, "何らかのエラーが発生しました", Toast.LENGTH_SHORT).show();
			return false;
		}
	}

	/**
	 * アプリが無かったのでGooglePalyに飛ばすメソッド。
	 *
	 * @param shareId
	 */
	private void shareAppDl(int shareId) {
		Uri uri = Uri.parse("market://details?id=" + sharePackages[shareId]);
		Intent intent = new Intent(Intent.ACTION_VIEW, uri);
		startActivity(intent);
	}
}