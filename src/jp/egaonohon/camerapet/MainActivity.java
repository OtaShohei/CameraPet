package jp.egaonohon.camerapet;

import android.app.Activity;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

/**
 *
 * CameraPetのMainActivityのクラスです。 onClickFacebookBtnとonClickTwitterBtnボタンは要差し替え。
 *
 * @author 1107AND
 *
 */
public class MainActivity extends Activity implements LoaderCallbacks<String> {

	private final int FACEBOOK_ID = 0;
	private final int TWITTER_ID = 1;
	private Integer intShotCnt = 0;// 撮影回数の初期値
	/** 撮影回数保存用Preferences */
	private SharedPreferences pref;
	/** Preferencesへの書き込み用Editor */
	private Editor editor;
	/** BGM用変数 */
	private MediaPlayer mp, mp2;
	private boolean bgmOn = true;
	/** pet動き用のSurfaceView */
//	PetMove pet;
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


		/** プリファレンスの準備 */
		pref = this.getSharedPreferences("shotCnt", Context.MODE_PRIVATE);
		/** プリファレンスに書き込むためのEditorオブジェクト取得 */
		editor = pref.edit();

	}

	@Override
	protected void onResume() {
		// TODO 自動生成されたメソッド・スタブ
		super.onResume();

		/** BGMの制御 */
		if (bgmOn) {
			mp.start();
			bgmOn = true;
		} else {
			mp.pause();
			bgmOn = false;
		}

		/**
		 * プリファレンスから撮影回数を取り出す。
		 */
		intShotCnt = loadShotCnt(this);

		/**
		 * LoaderManagerを実行して撮影回数を読み込み。
		 * こうしてLoaderの初期化。LoaderManagerがローダーを識別するid0番を付けてる。
		 * 第2引数に情報を格納したbundle。onCreateLoader（LoaderCallbacksの3つのメソッドの一つ）へここから飛ぶ。
		 */
		getLoaderManager().initLoader(0, null, this);
		/*
		 * initLoaderの引数について。 第1引数:Loaderを識別するID値（onCreateLoaderメソッドの第1引数に渡される）
		 * 第2引数:パラメータ格納用（onCreateLoaderメソッド（このアクティビティにあるメソッド）の第2引数に渡される）
		 * 第3引数:LoaderCallbackインターフェースを実装したクラス（すなわちこのアクティビティ）
		 */
	}

	/** Cameraへの移動や他アプリへの遷移時にBGMの停止などを行う。 */
	@Override
	protected void onPause() {
		// TODO 自動生成されたメソッド・スタブ
		super.onPause();

		/** BGMの一時停止 */
		mp.pause();
		bgmOn = false;

		if (bgmOn) {
			mp.pause();
		} else {
		}

		/** 撮影回数を0にリセットする。 */
		editor.putInt("shotCnt", 0);
		editor.commit();
		Log.v("CAMERA", "MainActivityOnPause");

	}

	@Override
	public Loader<String> onCreateLoader(int id, Bundle args) {
		// TODO 自動生成されたメソッド・スタブ
		// intShotCnt = args.getInt("CntNum_Key");
		Log.v("CAMERA", "onCreateLoader");
		return new AsyncOnSave(this, intShotCnt);

	}

	@Override
	public void onLoadFinished(Loader<String> loader, String data) {
		// TODO 自動生成されたメソッド・スタブ
		Toast.makeText(this, data, Toast.LENGTH_LONG).show();
		Log.v("CAMERA", "onLoadFinished");

	}

	@Override
	public void onLoaderReset(Loader<String> loader) {
		// TODO 自動生成されたメソッド・スタブ
		Log.v("CAMERA", "onLoaderReset");
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
		}
		// 未実装機能です。
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

	/** プリファレンスから「撮影回数」を取り出す。登録されていなければ 0 を返す */
	public int loadShotCnt(Context context) {
		/** 撮影回数を取り出しておく */
		int beforeShotCnt = pref.getInt("shotCnt", 0);

		/** 取り出し終えたので撮影回数を0にリセットする。 */
		editor.putInt("shotCnt", 0);
		editor.commit();

		/** 取り出しておいた撮影回数を戻り値として戻す */
		return beforeShotCnt;
	}
}
