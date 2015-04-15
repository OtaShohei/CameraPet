package jp.egaonohon.camerapet.encyc;

import java.util.Locale;

import com.google.android.gms.ads.AdView;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import jp.egaonohon.camerapet.App;
import jp.egaonohon.camerapet.CamPeAdMob;
import jp.egaonohon.camerapet.CamPeGameFeat;
import jp.egaonohon.camerapet.CamPePref;
import jp.egaonohon.camerapet.CameLog;
import jp.egaonohon.camerapet.MainActivity;
import jp.egaonohon.camerapet.PetAlarmBroadcastReceiver;
import jp.egaonohon.camerapet.R;
import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;

public class Encyc07Activity extends Activity {

	/** BGM用変数 */
	private MediaPlayer encycBgm;
	/** BGM用変数 */
	private boolean bgmOn = true;

	/** 【教室公開用コメントアウト】 */
	/** 言語設定ごとの広告表示のための言語設定確認 */
	private String locale;
	/** AdMob用のインスタンス */
	private AdView adView;

	/** Logのタグを定数で確保 */
	private static final String TAG = "Encyc07";

	/*
	 * (非 Javadoc)
	 * 
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.encyclopedia_seventh);

		/** BGMインスタンス生成し準備 */
		encycBgm = MediaPlayer.create(this, R.raw.honwaka);

		/** getStringExtraで状況を判断して、BGMスタートさせる */
		Intent intent = getIntent();
		String keyword = intent.getStringExtra("bgmOn");
		if (keyword.equals("true")) {
			bgmOn = true;
			encycBgm.start();
		} else {
			bgmOn = false;
		}

		/** NotificationManager用に、現在起動中である旨、プリファレンスに登録 */
		CamPePref.saveOther01ActivityOperationStatus(this);

		/** 広告表示設定のため、ユーザーの設定言語を取得 */
		locale = Locale.getDefault().toString();

		if (!locale.equals("ja_JP")) {
			/** AdMob呼び出し */
			CamPeAdMob encyc07CamPeAdMob = new CamPeAdMob();
			adView = encyc07CamPeAdMob.workAtOnCreate(this);
		} else {
			 /** GameFeat呼び出し */
			 CamPeGameFeat encyc07CamPeGameFeat = new CamPeGameFeat();
			 encyc07CamPeGameFeat.workAtOnCreate(this);
		}
		
		/** 起動したクラスをLogで確認 */
		CameLog.setLog(TAG, "onCreate");
	}

	/*
	 * (非 Javadoc)
	 * 
	 * @see android.app.Activity#onResume()
	 */
	@Override
	protected void onResume() {
		super.onResume();
		
		if (!locale.equals("ja_JP")) {
			/** 日本語以外の場合はAdMobの一時停止 */
			adView.resume();
		}
	}

	/*
	 * (非 Javadoc)
	 * 
	 * @see android.app.Activity#onPause()
	 */
	@Override
	protected void onPause() {
		super.onPause();

		/** BGMを停止 */
		encycBgm.stop();

		/** 日本語以外の場合は */
		if (!locale.equals("ja_JP")) {
			/** Admobの破棄 */
			adView.destroy();
		}

		/**
		 * Activityを明示的に終了させる。 ただし注意点あり。
		 * http://d.hatena.ne.jp/adsaria/20110428/1303966837
		 * http://www.android-navi.com/archives/android_1/finish_activity/
		 * */
		finish();
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
	}

	/*
	 * (非 Javadoc)
	 * 
	 * @see android.app.Activity#onStop()
	 */
	@Override
	protected void onStop() {
		super.onStop();
		/** NotificationManager用に、現在起動中でない旨、プリファレンスに登録 */
		CamPePref.saveOther01ActivityNotWorkStatus(this);
		
		/** AlarmManager & NotificationManagerを動かすメソッドを呼び出す */
		PetAlarmBroadcastReceiver.set(this);
	}

	// //////////////////////////////////////////////////////////////////////////////////
	// 以下、非オーバーライド系メソッド。
	// //////////////////////////////////////////////////////////////////////////////////
	/**
	 * 戻るボタンメソッド。
	 *
	 * @param v
	 */
	public void goBack(View v) {

		/**
		 * 画面移動要求を格納したインテントを作成する。 第一引数に自身(this)を設定 第二引数に移動先のクラス名を指定
		 */
		Intent intent = new Intent(Encyc07Activity.this, Encyc06Activity.class);

		/** BGMオンオフ状態を保持してインテントを出せるようにputextra */
		if (bgmOn) {
			intent.putExtra("bgmOn", "true");
		} else {
			intent.putExtra("bgmOn", "false");
		}

		/**
		 * Activity.startActivity()の第一引数にインテントを指定することで画面移動が行われる。
		 */
		startActivity(intent);
	}

	/**
	 * チュートリアルを終えるボタンメソッド。
	 *
	 * @param v
	 */
	public void goHome(View v) {

		/**
		 * 画面移動要求を格納したインテントを作成する。 第一引数に自身(this)を設定 第二引数に移動先のクラス名を指定
		 */
		Intent intent = new Intent(Encyc07Activity.this, MainActivity.class);

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
			encycBgm.pause();
			bgmOn = false;
		} else {
			encycBgm.start();
			bgmOn = true;
		}
	}

	/**
	 * 次へボタンメソッド。
	 * 
	 * @param v
	 */
	public void goforward(View v) {
		/**
		 * 画面移動要求を格納したインテントを作成する。 第一引数に自身(this)を設定 第二引数に移動先のクラス名を指定
		 */
		Intent intent = new Intent(Encyc07Activity.this, Encyc08Activity.class);

		/** BGMオンオフ状態を保持してインテントを出せるようにputextra */
		if (bgmOn) {
			intent.putExtra("bgmOn", "true");
		} else {
			intent.putExtra("bgmOn", "false");
		}

		/**
		 * Activity.startActivity()の第一引数にインテントを指定することで画面移動が行われる。
		 */
		startActivity(intent);
	}
}
