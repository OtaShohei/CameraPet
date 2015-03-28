package jp.egaonohon.camerapet;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;

public class TutorialSecondActivity extends Activity {
	
	/** BGM用変数 */
	private static MediaPlayer tutorialBgm;

	/** Logのタグを定数で確保 */
	private static final String TAG = "TutorialSecondActivity";
	
	/* (非 Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO 自動生成されたメソッド・スタブ
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tutorial_second);
		
//		/** BGMインスタンス生成し準備 */
//		tutorialBgm = MediaPlayer.create(this, R.raw.honwaka);
//		
//		/** BGMスタート */
//		tutorialBgm.setLooping(true);
//		tutorialBgm.start(); // SEを鳴らす
		
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
		Intent intent = new Intent(TutorialSecondActivity.this, TutorialFirstActivity.class);

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
		Intent intent = new Intent(TutorialSecondActivity.this, TutorialThirdActivity.class);

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
		Intent intent = new Intent(TutorialSecondActivity.this, MainActivity.class);

		/**
		 * Activity.startActivity()の第一引数にインテントを指定することで画面移動が行われる。
		 */
		startActivity(intent);
	}	

}
