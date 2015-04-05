package jp.egaonohon.camerapet.encyc;

import jp.egaonohon.camerapet.CameLog;
import jp.egaonohon.camerapet.MainActivity;
import jp.egaonohon.camerapet.R;
import jp.egaonohon.camerapet.R.layout;
import jp.egaonohon.camerapet.R.raw;
import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;

public class Encyc09Activity extends Activity {

	/** BGM用変数 */
	private MediaPlayer encycBgm;
	/** BGM用変数 */
	private boolean bgmOn = true;

	/** Logのタグを定数で確保 */
	private static final String TAG = "Encyc09";

	/*
	 * (非 Javadoc)
	 *
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.encyclopedia_ninth);

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
		super.onPause();

		/** BGMを停止 */
		encycBgm.stop();

		/**
		 * Activityを明示的に終了させる。
		 * ただし注意点あり。
		 * http://d.hatena.ne.jp/adsaria/20110428/1303966837
		 * http://www.android-navi.com/archives/android_1/finish_activity/
		 * */
		finish();
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
		Intent intent = new Intent(Encyc09Activity.this, Encyc08Activity.class);

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
		Intent intent = new Intent(Encyc09Activity.this, MainActivity.class);

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
	 Intent intent = new Intent(Encyc09Activity.this, Encyc10Activity.class);

	 /**
	 * Activity.startActivity()の第一引数にインテントを指定することで画面移動が行われる。
	 */
	 startActivity(intent);
	 }

}
