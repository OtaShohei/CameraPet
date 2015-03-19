package jp.egaonohon.camerapet;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.widget.Toast;

/**
 * SNS投稿クラス。
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
	/** SNS連携用のメンバ変数 */
	private final static String[] sharePackages = { "com.facebook.katana",
			"com.twitter.android" };

	/**
	 * Facebook投稿メソッド。
	 *
	 * @param v
	 */
	public static void goFacebook(Context context) {
		if (isShareAppInstall(context, FACEBOOK_ID)) {
			Intent intent = new Intent();
			intent.setAction(Intent.ACTION_SEND);
			intent.setPackage(sharePackages[FACEBOOK_ID]);
			intent.setType("text/plain");
			/**
			 * 　URLは、CameraPetのダウンロードページに差し替えること!
			 */
			intent.putExtra(Intent.EXTRA_TEXT,
					"https://play.google.com/store/apps/");
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
			intent.setPackage(sharePackages[TWITTER_ID]);
			intent.setType("image/png");
			/**
			 * 　URLは、CameraPetのダウンロードページに差し替えること!
			 */
			intent.putExtra(Intent.EXTRA_TEXT,
					"Androidアプリ「CameraPet」で飼っているペットです！"
							+ "https://play.google.com/store/apps/");
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
			pm.getApplicationInfo(sharePackages[shareId],
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
		Uri uri = Uri.parse("market://details?id=" + sharePackages[shareId]);
		Intent intent = new Intent(Intent.ACTION_VIEW, uri);
		context.startActivity(intent);
	}
}
