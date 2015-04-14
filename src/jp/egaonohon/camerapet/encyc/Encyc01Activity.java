package jp.egaonohon.camerapet.encyc;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import jp.basicinc.gamefeat.android.sdk.controller.GameFeatAppController;
import jp.egaonohon.camerapet.App;
import jp.egaonohon.camerapet.CamPePref;
import jp.egaonohon.camerapet.CameLog;
import jp.egaonohon.camerapet.MainActivity;
import jp.egaonohon.camerapet.PetAlarmBroadcastReceiver;
import jp.egaonohon.camerapet.R;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

public class Encyc01Activity extends Activity {

	/** BGM用変数 */
	private MediaPlayer encycBgm;
	/** BGM用変数 */
	private boolean bgmOn = true;
	
//	【教室公開用コメントアウト】
//	/** GameFeat用のインスタンス */
//	private GameFeatAppController gfAppController;
//	protected getImageData getImageData;

	/** Logのタグを定数で確保 */
	private static final String TAG = "Encyc01";

	/*
	 * (非 Javadoc)
	 * 
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.encyclopedia_first);

		/** BGMインスタンス生成し準備 */
		encycBgm = MediaPlayer.create(this, R.raw.honwaka);

		/** getStringExtraで状況を判断して、BGMスタートさせる */
		Intent intent = getIntent();
		String keyword = intent.getStringExtra("bgmOn");
		if (keyword.equals("true")) {
			bgmOn = true;
			encycBgm.start();
			CameLog.setLog(TAG, "bgmOnが" + bgmOn + "なのでBGM開始");
		} else {
			bgmOn = false;
			CameLog.setLog(TAG, "bgmOnが" + bgmOn + "なのでBGMは鳴らさない");
		}

		/** NotificationManager用に、現在起動中である旨、プリファレンスに登録 */
		CamPePref.saveOther01ActivityOperationStatus(this);

		/** 属性 android:id="@+id/adMobSpace" が与えられているものとしてLinearLayout をルックアップする */
		LinearLayout layout = (LinearLayout) findViewById(R.id.adMobSpace);

//		【教室公開用コメントアウト】
//		// GFコントローラ
//		gfAppController = new GameFeatAppController();
//		gfAppController.init(Encyc01Activity.this);
//
//		// LinearLayout incLayout = null;
//		RelativeLayout incLayout = null;
//		LinearLayout mainLayout = (LinearLayout) findViewById(R.id.adMobSpace);
//
//		// カスタム広告のデータを取得
//		ArrayList<HashMap<String, String>> customArrayList = gfAppController
//				.getCustomAds();
//
//		for (final HashMap<String, String> map : customArrayList) {
//			// LayoutInflaterの準備
//			LayoutInflater inflater = (LayoutInflater) getApplicationContext()
//					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//			incLayout = (RelativeLayout) inflater.inflate(R.layout.custom_row,
//					null);
//
//			// アイコン画像の読み込み
//			ImageView appIcon = (ImageView) incLayout
//					.findViewById(R.id.app_icon);
//			getImageData = new getImageData(map.get("appIconUrl"), appIcon);
//			getImageData.execute();
//
//			// タイトルの設定
//			TextView title = (TextView) incLayout.findViewById(R.id.title);
//			title.setText(map.get("appTitle"));
//
//			// タイトルの設定
//			TextView description = (TextView) incLayout
//					.findViewById(R.id.description);
//			description.setText(map.get("appDescription"));
//
//			// レビューボタンの設定
//			Button btnReview = (Button) incLayout.findViewById(R.id.btn_review);
//			if (map.get("hasReview") == "0") {
//				btnReview.setVisibility(View.GONE);
//			}
//			btnReview.setOnClickListener(new OnClickListener() {
//				@Override
//				public void onClick(View v) {
//					// レビューへ
//					gfAppController.onAdReviewClick(map);
//				}
//			});
//
//			// DLボタンの設定
//			Button btnStore = (Button) incLayout.findViewById(R.id.btn_store);
//			btnStore.setOnClickListener(new OnClickListener() {
//				@Override
//				public void onClick(View v) {
//					// AppStoreへ
//					gfAppController.onAdStoreClick(map);
//				}
//			});
//
//			mainLayout.addView(incLayout);
//		}
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

		/** BGMを停止 */
		encycBgm.stop();

		/**
		 * Activityを明示的に終了させる。 ただし注意点あり。
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
	 *
	 * @param v
	 */
	public void goforward(View v) {
		/**
		 * 画面移動要求を格納したインテントを作成する。 第一引数に自身(this)を設定 第二引数に移動先のクラス名を指定
		 */
		Intent intent = new Intent(Encyc01Activity.this, Encyc02Activity.class);

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

	/**
	 * 画像の非同期読み込み
	 *
	 */
	class getImageData extends AsyncTask<String, Integer, Bitmap> {

		private ImageView imageView;
		private String imageUrl;

		public getImageData(String imageUrl, ImageView imageView) {
			super();
			this.imageView = imageView;
			this.imageUrl = imageUrl;
		}

		@Override
		protected Bitmap doInBackground(String... param) {
			Bitmap bitmap;

			try {
				URL url = new URL(this.imageUrl);
				InputStream inputStream = url.openStream();
				bitmap = BitmapFactory.decodeStream(inputStream);
				return bitmap;
			} catch (IOException ex) {
				Logger.getLogger(Encyc01Activity.class.getName()).log(
						Level.SEVERE, null, ex);
			}

			return null;
		}

		@Override
		protected void onPostExecute(Bitmap result) {
			imageView.setImageBitmap(result);
		}
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
}
