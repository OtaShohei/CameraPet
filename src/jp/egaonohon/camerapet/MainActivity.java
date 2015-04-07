package jp.egaonohon.camerapet;

import jp.egaonohon.camerapet.encyc.Encyc01Activity;
import jp.egaonohon.camerapet.tutorial.TutorialFirstActivity;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Point;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

/**
 * カメラペットのゲーム画面Activity（=MainActivity）のクラス。
 *
 * @author OtaShohei
 */
public class MainActivity extends Activity {

	/** SurfaceView の参照 */
	GameSurfaceView msv;
	/** BGM用変数 */
	private MediaPlayer mp, mp2;
	private boolean bgmOn = true;
	/** 直近撮影枚数（=エサの数） */
	static int esaCnt;
	/** エサ食べさせ成功回数 */
	private int gettedEsaCnt;

	/** Logのタグを定数で確保 */
	private static final String TAG = "MainActivity";
	/** タブレットにおける不具合調査用にディスプレイのインスタンス生成 */
	private int windows_width;
	private int window_height;

	/** ゲーム時のトースト表示用のリソース変数 */
	static Context context;
	private static Resources res;

	/** CameraActivityから戻ってきた直後を判定するBoolean */
	private static boolean returnCam = false;
	/** Facebookから戻ってきた直後を判定するBoolean */
	private static boolean returnFb = false;
	/** Twitterから戻ってきた直後を判定するBoolean */
	private static boolean returnTwitter = false;
	/** Tutorialから戻ってきた直後を判定するBoolean */
	private static boolean returnTutorial = false;
	/** 図鑑から戻ってきた直後を判定するBoolean */
	private static boolean returnEncyc = false;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		/** フルスクリーンに設定 */
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);//
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		setContentView(R.layout.activity_main);
		/** SurfaceViewの生成 */
		msv = (GameSurfaceView) findViewById(R.id.petSpace);

		/** BGMインスタンス生成し準備 */
		mp = MediaPlayer.create(MainActivity.this, R.raw.honwaka);
		mp2 = MediaPlayer.create(MainActivity.this, R.raw.poka);

		/** BGMスタート */
		// mp.start(); // SEを鳴らす
		bgmOn = true;

		/** ゲーム時のトースト表示用のリソースインスタンス取得 */
		res = getResources();

		/** 画面のWidthを取得してみる（タブレットでの不具合対策） */
		Display display = getWindowManager().getDefaultDisplay();
		Point size = new Point();
		display.getSize(size);
		this.windows_width = size.x; // width
		this.window_height = size.y; // height
		CameLog.setLog(TAG, "Activityのwidthは" + windows_width
				+ "Activityのheightは" + window_height);
	}

	@Override
	protected void onResume() { // アクティビティが動き始める時呼ばれる
		super.onResume();

		// setfirstPetAlarmBroadcastReceiver();

		/** BGMの制御 */
		if (bgmOn) {
			mp.start();
			mp.setLooping(true);
			bgmOn = true;
		} else {
			mp.pause();
			bgmOn = false;
		}
	}

	@Override
	protected void onPause() { // アクティビティの動きが止まる時呼ばれる
		super.onPause();
		// AcSensor.Inst().onPause();// 中断時にセンサーを止める
		/** BGMの一時停止 */
		mp.pause();
		CameLog.setLog(TAG, "onPause");
	}

	@Override
	protected void onStop() {
		// TODO 自動生成されたメソッド・スタブ
		super.onStop();
		// /** インストール後初の起動かどうかを確認 */
		// String startStatus = CamPePref.loadStartStatus(this);
		// /** インストール後初の起動でないならば */
		// if (startStatus.equals("notFirst")) {
		// /** 前回のAlarmManager & NotificationManagerをキャンセルする動かすメソッドを動かす */
		// CameLog.setLog(TAG, "インストール後初の起動でないので「おなかがすいた！」通知をキャンセルする。");
		// canceltPetAlarmBroadcastReceiver();
		// }
		/** AlarmManager & NotificationManagerを動かすメソッドを呼び出す */
		setPetAlarmBroadcastReceiver();
		CameLog.setLog(TAG, "onStop");
	}

	/**
	 * Activityの最後に呼び出される。
	 */
	@Override
	protected void onDestroy() {
		super.onDestroy();

		CameLog.setLog(TAG, "onDestroy");
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

		/** 現在のペットの種類名を取得して、Facebookへ投稿実行 */
		SnsBtn.goFacebook(this, GameSurfaceView.getSpeciesName());
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
		returnTwitter = true;

		/** 現在のペットの種類名を取得して、Twitterへ投稿実行 */
		SnsBtn.goTwitter(this, GameSurfaceView.getSpeciesName());
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
	 * ペット図鑑呼び出しメソッド。 TutorialActivity
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
		Intent intent = new Intent(MainActivity.this, Encyc01Activity.class);

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
	 * チュートリアル呼び出しメソッド。
	 *
	 * @param v
	 */
	public void onClickTutorial(View v) {
		if (bgmOn) {
			mp2.start(); // SEを鳴らす
		} else {
		}

		/** Tutorialから戻ってくることを示す */
		returnTutorial = true;

		/**
		 * 画面移動要求を格納したインテントを作成する。 第一引数に自身(this)を設定 第二引数に移動先のクラス名を指定
		 */
		Intent intent = new Intent(MainActivity.this,
				TutorialFirstActivity.class);

		/**
		 * Activity.startActivity()の第一引数にインテントを指定することで画面移動が行われる。
		 */
		startActivity(intent);
	}

	/** SNSポイントを付与するメソッド */
	public void giveSNSPoints() {
		/** 現在の経験値を取得 */
		int gettedtotalEXP = CamPePref.loadTotalExp(this);
		/** ポイントを付与する */
		int newTotalExp = 8 + gettedtotalEXP;
		/** 加算したポイントをプリファレンスに保存する */
		CamPePref.saveTotalExp(this, newTotalExp);
	}

	/**
	 * アプリをしばらく起動していないと、「おなかがすいた！」とペットがユーザーに4日後に通知を出すメソッド。
	 * PetAlarmBroadcastReceiverクラスと連携する。
	 */
	public void setPetAlarmBroadcastReceiver() {

//		TimePicker tPicker;
		int notificationId = 0;
		PendingIntent alarmIntent;

		/** 現在時間を取得 */
		long now = System.currentTimeMillis();
		/** 10秒後にalert */
		long fourDayAfter = now + (30 * 1000);
		// /** 4日後を割り出す */
		// long fourDayAfter = now + 345600000;

		/** 4日後の20時台を割り出すコードは一時保留 */
		// /** 4日後を持つDateインスタンス生成 */
		// Date fourDayAfterDate = new Date(fourDayAfter);
		// /** Dateクラスから日時のString型文字列生成 */
		// SimpleDateFormat format = new SimpleDateFormat("MM_dd__HH__mm");
		// String s = format.format(fourDayAfterDate);
		// String kiridasi = s.substring(6,8);
		// /** 候補時間をint型に変換 */
		// int kouhoTime = new Integer(kiridasi).intValue();
		// String henkanTimeStr = s.replaceAll("__HH__","sasuke");

		Intent bootIntent = new Intent(MainActivity.this,
				AlarmBroadcastReceiver.class);
		bootIntent.putExtra("notificationId", notificationId);
		alarmIntent = PendingIntent.getBroadcast(MainActivity.this, 0,
				bootIntent, PendingIntent.FLAG_CANCEL_CURRENT);

		AlarmManager alarm = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

		alarm.set(AlarmManager.RTC_WAKEUP, fourDayAfter, alarmIntent);
		CameLog.setLog(TAG, "1分後" + fourDayAfter + "に通知セット完了");
	}

	/** Cameraから戻ってきたかどうかの判定を他のクラスに与えるメソッド */
	public static boolean isReturnCam() {
		return returnCam;
	}

	/** facebookから戻ってきたかどうかの判定を他のクラスに与えるメソッド */
	public static boolean isReturnFb() {
		return returnFb;
	}

	/** Twitterから戻ってきたかどうかの判定を他のクラスに与えるメソッド */
	public static boolean isReturnTwitter() {
		return returnTwitter;
	}

	/** チュートリアルから戻ってきたかどうかの判定を他のクラスに与えるメソッド */
	public static boolean isReturnTutorial() {
		return returnTutorial;
	}

	/** facebookから戻ってきたかどうかの判定を他のクラスから変更するメソッド */
	public static void setReturnFb(boolean returnFb) {
		MainActivity.returnFb = returnFb;
	}

	/** Twitterから戻ってきたかどうかの判定を他のクラスから変更するメソッド */
	public static void setReturnTwitter(boolean returnTwitter) {
		MainActivity.returnTwitter = returnTwitter;
	}

	/** チュートリアルから戻ってきたかどうかの判定を他のクラスから変更するメソッド */
	public static void setReturnTut(boolean returnTut) {
		MainActivity.returnTutorial = returnTut;
	}
}