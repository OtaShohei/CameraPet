package jp.egaonohon.camerapet.tutorial;

import jp.egaonohon.camerapet.App;
import jp.egaonohon.camerapet.CamPePref;
import jp.egaonohon.camerapet.CameLog;
import jp.egaonohon.camerapet.MainActivity;
import jp.egaonohon.camerapet.PetAlarmBroadcastReceiver;
import jp.egaonohon.camerapet.R;
import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

public class SpecialThanksActivity extends Activity {

	/** BGM用変数 */
	private static MediaPlayer tutorialBgm;
	/** Web遷移用変数 */
	private static final int MY_INTENT_BROWSER = 0;

	/** Logのタグを定数で確保 */
	private static final String TAG = "SpecialThanksActivity";

	/*
	 * (非 Javadoc)
	 *
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.special_thanks);

		/** NotificationManager用に、現在起動中である旨、プリファレンスに登録 */
		CamPePref.saveOther02ActivityOperationStatus(this);

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
	}

	/*
	 * (非 Javadoc)
	 *
	 * @see android.app.Activity#onPause()
	 */
	@Override
	protected void onPause() {
		super.onPause();
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
		CamPePref.saveOther02ActivityNotWorkStatus(this);

		/** AlarmManager & NotificationManagerを動かすメソッドを呼び出す */
		PetAlarmBroadcastReceiver.set(this);

	}

	/* (非 Javadoc)
	 * @see android.app.Activity#onDestroy()
	 */
	@Override
	protected void onDestroy() {
		super.onDestroy();
		/** finishはonStopなどだとtwitterから戻ってきた時にこのActivityが存在しないことになってしまう */
		finish();
	}

	// //////////////////////////////////////////////////////////////////////////////////
	// 以下、非オーバーライド系メソッド。
	// //////////////////////////////////////////////////////////////////////////////////
	public void onClickOtaFb(View v) {
		Intent intent = new Intent(Intent.ACTION_VIEW,
				Uri.parse("https://www.facebook.com/egaonoota"));

		startActivityForResult(intent, MY_INTENT_BROWSER);
	}

	public void onClickOtaTw(View v) {
		Intent intent = new Intent(Intent.ACTION_VIEW,
				Uri.parse("https://twitter.com/egaota"));

		startActivityForResult(intent, MY_INTENT_BROWSER);
	}

	public void onClickOtaBlog(View v) {
		Intent intent = new Intent(Intent.ACTION_VIEW,
				Uri.parse("http://sho.hateblo.jp/"));

		startActivityForResult(intent, MY_INTENT_BROWSER);
	}

	/**
	 * 戻るボタンメソッド。
	 *
	 * @param v
	 */
	public void goBack(View v) {
		/**
		 * 画面移動要求を格納したインテントを作成する。 第一引数に自身(this)を設定 第二引数に移動先のクラス名を指定
		 */
		Intent intent = new Intent(SpecialThanksActivity.this,
				TutorialSeventhActivity.class);

		/**
		 * Activity.startActivity()の第一引数にインテントを指定することで画面移動が行われる。
		 */
		startActivity(intent);
	}

	/**
	 * ゲーム画面へ移動するメソッド。
	 *
	 * @param v
	 */
	public void goHome(View v) {
		/**
		 * 画面移動要求を格納したインテントを作成する。 第一引数に自身(this)を設定 第二引数に移動先のクラス名を指定
		 */
		Intent intent = new Intent(SpecialThanksActivity.this,
				MainActivity.class);

		/**
		 * Activity.startActivity()の第一引数にインテントを指定することで画面移動が行われる。
		 */
		startActivity(intent);
	}
}
