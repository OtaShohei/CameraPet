package jp.egaonohon.camerapet;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.widget.Toast;

/**
 * SNS投稿クラス。 TWEETTXTとoFACEBOOKTXTは要差し替え。
 * 
 * @author OtaShohei
 *
 */
public class SnsBtn {
	/**
	 * メンバ変数
	 */
	private final static int FACEBOOK_ID = 0;
	private final static int TWITTER_ID = 1;
	private static final String TWEETTXT = "Androidアプリ「CameraPet」で飼っているペットです！ https://play.google.com/store/apps/";
	private static final String FACEBOOKTXT = "https://play.google.com/store/apps/";
	/** SNS連携用のメンバ変数 */
	private final static String[] SHAREPACKAGES = { "com.facebook.katana",
			"com.twitter.android" };
	/** Logのタグを定数で確保 */
	private static final String TAG = "SnsBtn";

	/**
	 * Facebook投稿メソッド。
	 *
	 * @param v
	 */
	public static void goFacebook(Context context) {
		if (isShareAppInstall(context, FACEBOOK_ID)) {
			Intent intent = new Intent();
			intent.setAction(Intent.ACTION_SEND);
			intent.setPackage(SHAREPACKAGES[FACEBOOK_ID]);
			intent.setType("text/plain");
			intent.putExtra(Intent.EXTRA_TEXT, FACEBOOKTXT);
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
	public static void goTwitter(Context context) {
		if (isShareAppInstall(context, TWITTER_ID)) {
			Intent intent = new Intent();
			intent.setAction(Intent.ACTION_SEND);
			intent.setPackage(SHAREPACKAGES[TWITTER_ID]);
			intent.setType("image/png");
			intent.putExtra(Intent.EXTRA_TEXT, TWEETTXT);
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
			/**
			 * エラー発生時のトースト。
			 */
			Toast.makeText(context, "何らかのエラーが発生しました", Toast.LENGTH_SHORT)
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
}
