package jp.egaonohon.camerapet;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.net.Uri;
import android.widget.Toast;

/**
 * SNS投稿クラス。 TWEETTXTとoFACEBOOKTXTは要差し替え。
 * 
 * @author OtaShohei
 */
public class SnsBtn {
	/**
	 * メンバ変数
	 */
	private static final int FACEBOOK_ID = 0;
	private static final int TWITTER_ID = 1;
	/** 画面表示ペット種別名 */
	private String speciesNameforSns;
	/** ペット年齢 */
	private static String petAgeforSns;
	/** 画面表示経験値 */
	private static int gettedtotalEXPforSns;
	/** SNSに投稿するメッセージ */
	private static String snsTxt;
	// private static String TweetTxt =
	// "スマホアプリ「CameraPet」で飼っているペットです！ https://play.google.com/store/apps/";
	// private static String FacebookTxt =
	// "https://play.google.com/store/apps/";
	/** SNS連携用のメンバ変数 */
	private static final String[] SHAREPACKAGES = { "com.facebook.katana",
			"com.twitter.android" };
	/** Logのタグを定数で確保 */
	private static final String TAG = "SnsBtn";

	/**
	 * Facebook投稿メソッド。
	 *
	 * @param v
	 */
	public static void goFacebook(Context context, String speciesName) {
		if (isShareAppInstall(context, FACEBOOK_ID)) {
			/** 投稿メッセージ生成 */
			makeMsg(context, speciesName);
			Intent intent = new Intent();
			intent.setAction(Intent.ACTION_SEND);
			intent.setPackage(SHAREPACKAGES[FACEBOOK_ID]);
			intent.setType("text/plain");
			intent.putExtra(Intent.EXTRA_TEXT, snsTxt);
			context.startActivity(intent);
		} else {
			shareAppDl(context, FACEBOOK_ID);
		}
	}

	/**
	 * Twitter投稿メソッド。
	 *
	 * @param v
	 */
	public static void goTwitter(Context context, String speciesName) {
		if (isShareAppInstall(context, TWITTER_ID)) {
			/** 投稿メッセージ生成 */
			makeMsg(context, speciesName);
			Intent intent = new Intent();
			intent.setAction(Intent.ACTION_SEND);
			intent.setPackage(SHAREPACKAGES[TWITTER_ID]);
			intent.setType("image/png");
			intent.putExtra(Intent.EXTRA_TEXT, snsTxt);
			context.startActivity(intent);
		} else {
			shareAppDl(context, TWITTER_ID);
		}
	}

	/**
	 * アプリがインストールされているかチェックするメソッド。
	 *
	 * @param shareId
	 * @return
	 */
	private static Boolean isShareAppInstall(Context context, int shareId) {
		try {
			PackageManager pm = context.getPackageManager();
			pm.getApplicationInfo(SHAREPACKAGES[shareId],
					PackageManager.GET_META_DATA);
			return true;
		} catch (NameNotFoundException e) {
			/** Toast表示用の文字列を取得 */
			Resources res = context.getResources();
			String petWelcomMessage = res.getString(R.string.download_sns_app);
			/**
			 * Twitterかfacebookアプリがインストールされていない時のトースト。
			 */
			Toast.makeText(context, petWelcomMessage, Toast.LENGTH_SHORT)
					.show();
			return false;
		}
	}

	/**
	 * アプリが無かったのでGooglePalyに飛ばすメソッド。
	 *
	 * @param shareId
	 */
	private static void shareAppDl(Context context, int shareId) {
		Uri uri = Uri.parse("market://details?id=" + SHAREPACKAGES[shareId]);
		Intent intent = new Intent(Intent.ACTION_VIEW, uri);
		context.startActivity(intent);
	}

	public static String makeMsg(Context context, String speciesName) {

		/** プリファレンスから累計経験値を取得 */
		gettedtotalEXPforSns = CamPePref.loadTotalExp(context);
		/** ペットの年齢を取得 */
		petAgeforSns = "" + Birthday.getAge(context);

		Resources res = context.getResources();

		snsTxt = res.getString(R.string.pet_sns_report_age) + " "
				+ petAgeforSns + res.getString(R.string.pet_sns_report_exp)
				+ " " + gettedtotalEXPforSns
				+ res.getString(R.string.pet_sns_report_species_name)
				+ speciesName + res.getString(R.string.pet_sns_report_finish)
				+ " " + "https://play.google.com/store/apps/";
		return snsTxt;
	}
}
