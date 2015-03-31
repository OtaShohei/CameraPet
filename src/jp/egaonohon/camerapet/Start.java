package jp.egaonohon.camerapet;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

/**
 * 本アプリインストール後の初回起動か否かを判定するクラス。
 * 初回起動時以外もアプリの最初に呼び出される。その際にlogoを表示する。
 * @author OtaShohei
 *
 */
public class Start extends Activity {

	/** Logのタグを定数で確保 */
	private static final String TAG = "Start";

	/* (非 Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		/** フルスクリーンに設定 */
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);//
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		setContentView(R.layout.start);
		Judge();
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

	/**
	 * 初回起動か否かを判断するメソッド。
	 */
	public void Judge() {
		String startStatus = CamPePref.loadStartStatus(this);
		CameLog.setLog(TAG,startStatus);

		/**
		 * 初めての起動でないならばMainActivityへ飛ばす。
		 * 最初、Stringの比較を == でやっていたので判定がうまくいっていなかった…。
		 *  */
		if (startStatus.equals("notFirst") ) {
			/**
			 * 画面移動要求を格納したインテントを作成する。 第一引数に自身(this)を設定 第二引数に移動先のクラス名を指定
			 */
			Intent intent = new Intent(Start.this, MainActivity.class);

			/** 処理を1000ミリ秒待つ */
//			sleep(1000);
			CameLog.setLog(TAG,"初回起動ではないと判断。");

			/**
			 * Activity.startActivity()の第一引数にインテントを指定することで画面移動が行われる。
			 */
			startActivity(intent);

			/** 初めての起動ならばチュートリアルへ飛ばす */
		} else if (!startStatus.equals("notFirst")) {
			/**
			 * 画面移動要求を格納したインテントを作成する。 第一引数に自身(this)を設定 第二引数に移動先のクラス名を指定
			 */
			Intent intent = new Intent(Start.this, TutorialFirstActivity.class);

			/** 処理を1000ミリ秒待つ */
//			sleep(1000);
			CameLog.setLog(TAG,"初回起動と判断。");

			/**
			 * Activity.startActivity()の第一引数にインテントを指定することで画面移動が行われる。
			 */
			startActivity(intent);
		}
	}

	/** シングルスレッドにおいて指定ミリ秒実行を止めるメソッド */
	public synchronized void sleep(long msec) {
		try {
			wait(msec);
			CameLog.setLog(TAG,"500ミリ秒実行を停止。");
		} catch (InterruptedException e) {
			CameLog.setLog(TAG,"500ミリ秒実行停止に失敗。");
		}
	}
}
