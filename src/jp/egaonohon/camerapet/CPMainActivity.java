package jp.egaonohon.camerapet;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

public class CPMainActivity extends Activity {

//	/**
//	 * @param savedTotalShotCnt セットする savedTotalShotCnt
//	 */
//	public void setSavedTotalShotCnt(int savedTotalShotCnt) {
//		this.savedTotalShotCnt = savedTotalShotCnt;
//	}

	/** SurfaceView の参照 */
	CPGameSurfaceView cpGameview;
	/** BGM用変数 */
	private MediaPlayer mp, mp2;
	private boolean bgmOn = true;
	/** 累積撮影回数 */
	int savedTotalShotCnt;
	/** 累積撮影回数表示用 */
	private TextView savedTotalShotCntTV;
	/** 生後日数 */
	int petAge;
	/** 生後日数表示用 */
	private TextView petAgeTV;
	/** Logのタグを定数で確保 */
	private static final String TAG = "MainActivity";
	/** CameraActivityから戻ってきた直後を判定するBoolean */
	private boolean returnCam = false;
	private boolean returnFb = false;
	private boolean returnTw = false;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);// フルスクリーンに設定
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		try {
			savedTotalShotCnt = CamPeDb.getTotalShotCnt(this);
		} catch (Exception e) {
			e.printStackTrace();
			CameLog.setLog(TAG, "累積撮影回数取得に失敗@onCreate");
		}

		CameLog.setLog(TAG, "累積撮影回数取得に成功@onCreate");
		
		/** SurfaceViewの生成 */
		cpGameview = new CPGameSurfaceView(this);

//		/** listenerをセット。無名クラス。 */
//		cpGameview.setOnFcsChangeListener(new OnFcsChangeListener() {
//			@Override
//			public void onFcsChange(float x, float y) {// オンフォーカスChangelistener。自作。フォーカスが変わったら行うことを記述。buttonに対してクリックlistenerをつけていた時と同じ感じ。
//				CPPet.setPetMoveX(x);
//				CPPet.setPetMoveX(y);
//				}
//		});

		setContentView(R.layout.cp_activity_main);
		savedTotalShotCntTV = (TextView) findViewById(R.id.savedTotalShotCntTV); // TextViewにもidを付けて参照
		petAgeTV = (TextView) findViewById(R.id.petAgeTV); // TextViewにもidを付けて参照
		
		// AcSensor.Inst().onCreate(this); // センサー初期化
		/** BGMインスタンス生成し準備 */
		mp = MediaPlayer.create(CPMainActivity.this, R.raw.honwaka);
		mp2 = MediaPlayer.create(CPMainActivity.this, R.raw.poka);

		/** BGMスタート */
		mp.setLooping(true);
		mp.start(); // SEを鳴らす
		bgmOn = true;
		

	}

	@Override
	protected void onResume() { // アクティビティが動き始める時呼ばれる
		super.onResume();
		// AcSensor.Inst().onResume();// 開始時にセンサーを動かし始める
		/** BGMの制御 */
		if (bgmOn) {
			mp.start();
			bgmOn = true;
		} else {
			mp.pause();
			bgmOn = false;
		}
		
		/** 累積撮影回数表示 */
		try {
			savedTotalShotCntTV.setText(String.valueOf(savedTotalShotCnt) + "Pt");
		} catch (Exception e) {
			CameLog.setLog(TAG, "累積撮影回数表示に失敗@onResume");
		}
		CameLog.setLog(TAG, "累積撮影回数表示に成功@onResume");
	}

	@Override
	protected void onPause() { // アクティビティの動きが止まる時呼ばれる
		super.onPause();
		// AcSensor.Inst().onPause();// 中断時にセンサーを止める
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
		Intent intent = new Intent(CPMainActivity.this, CameraActivity.class);

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
		 */
		Intent intent = new Intent(CPMainActivity.this, GetPh.class);

		/**
		 * Activity.startActivity()の第一引数にインテントを指定することで画面移動が行われる。
		 */
		startActivity(intent);
	}
	
//	cpGameview.setOnFcsChangeListener(new OnFcsChangeListener() {//listenerをセット。無名クラス。
//		@Override
//		public void onFcsChange(int num) {//オンフォーカスChangelistener。自作。フォーカスが変わったら行うことを記述。buttonに対してクリックlistenerをつけていた時と同じ感じ。
//			tv.setText("ドロイド" + (num+1) + "号が選択されたよ");
//		}
//
//	@Override
//	public void onFcsChange(float x, float y) {
//		// TODO 自動生成されたメソッド・スタブ
//		
//	}
//	});
}