package jp.egaonohon.camerapet.tutorial;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import jp.egaonohon.camerapet.App;
import jp.egaonohon.camerapet.CamPePref;
import jp.egaonohon.camerapet.CameLog;
import jp.egaonohon.camerapet.MainActivity;
import jp.egaonohon.camerapet.PetAlarmBroadcastReceiver;
import jp.egaonohon.camerapet.R;
import jp.egaonohon.camerapet.R.layout;
import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;

public class TutorialFifthActivity extends Activity {

	/** BGM用変数 */
	private static MediaPlayer tutorialBgm;
	
	/** Logのタグを定数で確保 */
	private static final String TAG = "TutorialFifthActivity";
	
	/* (非 Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tutorial_fifth);
		
		/** NotificationManager用に、現在起動中である旨、プリファレンスに登録 */
		CamPePref.saveOther01ActivityOperationStatus(this);
		
		/** 起動したクラスをLogで確認 */
		CameLog.setLog(TAG, "onCreate");
	}

	/* (非 Javadoc)
	 * @see android.app.Activity#onResume()
	 */
	@Override
	protected void onResume() {
		// TODO 自動生成されたメソッド・スタブ
		super.onResume();
	}

	/* (非 Javadoc)
	 * @see android.app.Activity#onPause()
	 */
	@Override
	protected void onPause() {
		// TODO 自動生成されたメソッド・スタブ
		super.onPause();
//		/** BGMを停止 */
//		tutorialBgm.stop();
		finish();
	}

	// //////////////////////////////////////////////////////////////////////////////////
	// 以下、非オーバーライド系メソッド。
	// //////////////////////////////////////////////////////////////////////////////////

	/**
	 * 戻るボタンメソッド。
	 * @param v
	 */
	public void goBack(View v) {
		/**
		 * 画面移動要求を格納したインテントを作成する。 第一引数に自身(this)を設定 第二引数に移動先のクラス名を指定
		 */
		Intent intent = new Intent(TutorialFifthActivity.this, TutorialFourthActivity.class);

		/**
		 * Activity.startActivity()の第一引数にインテントを指定することで画面移動が行われる。
		 */
		startActivity(intent);
	}

	/**
	 * 次へボタンメソッド。
	 * @param v
	 */
	public void goNext(View v) {
		/**
		 * 画面移動要求を格納したインテントを作成する。 第一引数に自身(this)を設定 第二引数に移動先のクラス名を指定
		 */
		Intent intent = new Intent(TutorialFifthActivity.this, TutorialSixthActivity.class);

		/**
		 * Activity.startActivity()の第一引数にインテントを指定することで画面移動が行われる。
		 */
		startActivity(intent);
	}	
	
	/**
	 * チュートリアルを終えるボタンメソッド。
	 * @param v
	 */
	public void goHome(View v) {
		
		/**
		 * 画面移動要求を格納したインテントを作成する。 第一引数に自身(this)を設定 第二引数に移動先のクラス名を指定
		 */
		Intent intent = new Intent(TutorialFifthActivity.this, MainActivity.class);

		/**
		 * Activity.startActivity()の第一引数にインテントを指定することで画面移動が行われる。
		 */
		startActivity(intent);
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

	/* (非 Javadoc)
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
}
