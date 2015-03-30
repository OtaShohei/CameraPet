package jp.egaonohon.camerapet;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;

public class Encyc01Activity extends Activity {

	/** BGM用変数 */
	private MediaPlayer encycBgm;
	/** BGM用変数 */
	private boolean bgmOn = true;

	/** Logのタグを定数で確保 */
	private static final String TAG = "Encyc01";

	/*
	 * (非 Javadoc)
	 * 
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO 自動生成されたメソッド・スタブ
		super.onCreate(savedInstanceState);
		setContentView(R.layout.encyclopedia_first);

		/** BGMインスタンス生成し準備 */
		encycBgm = MediaPlayer.create(this, R.raw.honwaka);

		/** BGMスタート */

//		mp.start(); // SEを鳴らす
		bgmOn = true;

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
		// TODO 自動生成されたメソッド・スタブ
		super.onResume();
		
		/** BGMの制御 */
		if (bgmOn) {
			encycBgm.start();
			encycBgm.setLooping(true);
			bgmOn = true;
		} else {
			encycBgm.pause();
			bgmOn = false;
		}
	}

	/*
	 * (非 Javadoc)
	 * 
	 * @see android.app.Activity#onPause()
	 */
	@Override
	protected void onPause() {
		// TODO 自動生成されたメソッド・スタブ
		super.onPause();

		/** BGMを停止 */
		encycBgm.stop();
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
		Intent intent = new Intent(Encyc01Activity.this, MainActivity.class);

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
		Intent intent = new Intent(Encyc01Activity.this, MainActivity.class);

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
	 * @param v
	 */
	 public void goforward(View v) {
	 /**
	 * 画面移動要求を格納したインテントを作成する。 第一引数に自身(this)を設定 第二引数に移動先のクラス名を指定
	 */
	 Intent intent = new Intent(Encyc01Activity.this, Encyc02Activity.class);
	
	 /**
	 * Activity.startActivity()の第一引数にインテントを指定することで画面移動が行われる。
	 */
	 startActivity(intent);
	 }

}
