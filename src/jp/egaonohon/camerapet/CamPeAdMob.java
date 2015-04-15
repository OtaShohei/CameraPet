package jp.egaonohon.camerapet;

import android.app.Activity;
import android.content.Context;
import android.widget.LinearLayout;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;


public class CamPeAdMob {
	
	/**
	 * コンストラクタ
	 */
	public CamPeAdMob() {
		super();
	}

	/** 言語設定ごとの広告表示のための言語設定確認 */
	private String locale;
	/** Admob用のインスタンス */
	private AdView adView;

	public AdView workAtOnCreate(Activity activity) {
		
		// ////////
		// 以下、Admob用記述
		// http://skys.co.jp/archives/1579
		// を参考にEclipseへのメモリ割り当てを増やさないとEclipseがフリーズするので要注意。
		// ////////

		/** adView を作成する */
		adView = new AdView(activity);
		adView.setAdUnitId("ca-app-pub-1135628131797223/1945135213");
		adView.setAdSize(AdSize.SMART_BANNER);

		/**
		 * 属性 android:id="@+id/adMobSpace" が与えられているものとしてLinearLayout
		 * をルックアップする
		 */
		LinearLayout layout = (LinearLayout) activity.findViewById(R.id.adMobSpace);

		/** adView を追加する */
		layout.addView(adView);

		// /** 一般的なリクエストを行う */
		// AdRequest adRequest = new AdRequest.Builder().build();

		/** 【教室公開用コメントアウト】 */
		/** テスト用のリクエストを行う */
		AdRequest adRequest = new AdRequest.Builder()
		/** エミュレータ */
		.addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
		/** Nexus */
		.addTestDevice("9F62663AFEF7E4EC5B3F231A4AB93A9C")
		/** Motorola */
		.addTestDevice("F8ACFF2BF5F49E1CD65848BA4BC6E0AD")
		/** 広告対象を女性に */
		.setGender(AdRequest.GENDER_FEMALE).build();

		/** 広告リクエストを行って adView を読み込む */
		adView.loadAd(adRequest);
		
		/** adView を返却する */
		return adView;
	}

}
