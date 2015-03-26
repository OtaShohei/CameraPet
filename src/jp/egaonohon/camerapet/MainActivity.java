package jp.egaonohon.camerapet;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RatingBar;
import android.widget.TextView;

public class MainActivity extends Activity {

	// /**
	// * @param savedTotalShotCnt セットする savedTotalShotCnt
	// */
	// public void setSavedTotalShotCnt(int savedTotalShotCnt) {
	// this.savedTotalShotCnt = savedTotalShotCnt;
	// }

	/** SurfaceView の参照 */
	GameSurfaceView msv;
	/** BGM用変数 */
	private MediaPlayer mp, mp2;
	private boolean bgmOn = true;
	/** 直近撮影枚数（=エサの数） */
	static int esaCnt;
	/** エサ食べさせ成功回数 */
	private int gettedEsaCnt;
	/** エサ食べさせ成功レイティング */
	private static RatingBar esaGettedRating;
	/** 累積撮影回数 */
	private int savedTotalShotCnt;
	/** 累積撮影回数表示 */
	private TextView savedTotalShotCntTV;
	/** 生後日数 */
	private int petAge;
	/** 生後日数画面表示 */
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

		/** フルスクリーンに設定 */
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);//
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		setContentView(R.layout.activity_main);
		/** SurfaceViewの生成 */
		msv = (GameSurfaceView) findViewById(R.id.petSpace);
		/** RatingBarの生成 */
		esaGettedRating = new RatingBar(this);

		/** TextViewとRatingBarもidを付けて参照 */
		savedTotalShotCntTV = (TextView) findViewById(R.id.savedTotalShotCntTV);
		petAgeTV = (TextView) findViewById(R.id.petAgeTV);
		esaGettedRating = (RatingBar) findViewById(R.id.ratingBar1);

		/** BGMインスタンス生成し準備 */
		mp = MediaPlayer.create(MainActivity.this, R.raw.honwaka);
		mp2 = MediaPlayer.create(MainActivity.this, R.raw.poka);

		/** BGMスタート */
		mp.setLooping(true);
		mp.start(); // SEを鳴らす
		bgmOn = true;

	}



	@Override
	protected void onResume() { // アクティビティが動き始める時呼ばれる
		super.onResume();

		/** BGMの制御 */
		if (bgmOn) {
			mp.start();
			bgmOn = true;
		} else {
			mp.pause();
			bgmOn = false;
		}

		try {
			savedTotalShotCnt = CamPePref.loadTotalShotCnt(this);
		} catch (Exception e) {
			e.printStackTrace();
			CameLog.setLog(TAG, "プリファレンスから累積撮影回数取得に失敗@onCreate");
		}

		CameLog.setLog(TAG, "プリファレンスから累積撮影回数取得に成功@onCreate");

		/** 累積撮影回数表示 */
		try {
			if (savedTotalShotCnt == -1) {
				savedTotalShotCnt = 0;
			}
			savedTotalShotCntTV.setText(String.valueOf(savedTotalShotCnt)
					+ "Pt");
		} catch (Exception e) {
			CameLog.setLog(TAG, "プリファレンスからの累積撮影回数表示に失敗@onResume");
		}
		CameLog.setLog(TAG, "プリファレンスからの累積撮影回数" + savedTotalShotCnt + "の表示に成功@onResume");

		/** 誕生日表示 */
		petAgeTV.setText(Birthday.getAge(this));

//		/** レーティングの最大値をセットする */
//		esaGettedRating.setMax(CamPePref.loadNowShotCnt(this));

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

//	/** エサゲームのレーティングのステップ幅をセットする */
//	public static void setGameRatingStep(int rating) {
//		/** 直近撮影枚数（=エサの数） */
//		esaCnt = rating;
//		esaGettedRating.setStepSize(rating/10);
//		 CameLog.setLog(TAG, "セットしたレーティングのステップ幅は" + esaGettedRating.getStepSize());
//	}
	/** エサゲームのエサ獲得ごとにレーティングに反映する */
	public static void ratingUp(float rating) {
		 CameLog.setLog(TAG, "レーティングをUp。Upする値は" + rating);
		esaGettedRating.setRating(rating);
		 CameLog.setLog(TAG, "レーティングをUp。現在のレーティングは" + esaGettedRating.getRating());
		/** エサゲームのエサ獲得が星の数10と等しくなったら0に戻す */
		if (esaGettedRating.getRating() >= 10) {
			esaGettedRating.setRating(0);
		}
	}

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
		 */
		Intent intent = new Intent(MainActivity.this, GetPh.class);

		/**
		 * Activity.startActivity()の第一引数にインテントを指定することで画面移動が行われる。
		 */
		startActivity(intent);
	}
}