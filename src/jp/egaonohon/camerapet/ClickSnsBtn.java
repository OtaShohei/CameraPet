package jp.egaonohon.camerapet;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.widget.Toast;

/**
 * SNS投稿クラス。
 * 
 * @author OtaShohei
 *
 */
public class ClickSnsBtn {
	/**
	 * メンバ変数
	 */
	private final int FACEBOOK_ID = 0;
	private final static int TWITTER_ID = 1;
	/** SNS連携用のメンバ変数 */
	private final String[] sharePackages = { "com.facebook.katana",
			"com.twitter.android" };

	public Intent onClickFacebook() {
		Intent intent = new Intent();
//		if (isShareAppInstall(FACEBOOK_ID)) {
			intent.setAction(Intent.ACTION_SEND);
			intent.setPackage(sharePackages[FACEBOOK_ID]);
			intent.setType("text/plain");
			/**
			 * 　URLは、CameraPetのダウンロードページに差し替えること!
			 */
			intent.putExtra(Intent.EXTRA_TEXT,
					"https://play.google.com/store/apps/");
			return intent;
//		} else {
//			shareAppDl(FACEBOOK_ID);
		}
//		return intent;
//	}

	public Intent onClickTwitter() {
		Intent intent = new Intent();
//		if (isShareAppInstall(TWITTER_ID)) {
			intent.setAction(Intent.ACTION_SEND);
			intent.setPackage(sharePackages[TWITTER_ID]);
			intent.setType("image/png");
			/**
			 * 　URLは、CameraPetのダウンロードページに差し替えること!
			 */
			intent.putExtra(Intent.EXTRA_TEXT,
					"Androidアプリ「CameraPet」で飼っているペットです！"
							+ "https://play.google.com/store/apps/");
			return intent;
//		} else {
//			shareAppDl(TWITTER_ID);
		}
//		return intent;
//	}

	/**
	 * アプリがインストールされているかチェックするメソッド。
	 *
	 * @param shareId
	 * @return
	 */
	public Boolean isShareAppInstall(int shareId,PackageManager pm) {
		try {
			pm.getApplicationInfo(sharePackages[shareId],
					PackageManager.GET_META_DATA);
			return true;
		} catch (NameNotFoundException e) {
			/**
			 * エラー発生時のトースト。
			 */
//			Toast.makeText(getContext(), "何らかのエラーが発生しました", Toast.LENGTH_SHORT)
//					.show();
			return false;
		}
	}

	/**
	 * アプリが無かったのでGooglePalyに飛ばすメソッド。
	 *
	 * @param shareId
	 */
	public static void shareAppDl(int shareId) {
//		Uri uri = Uri.parse("market://details?id=" + sharePackages[shareId]);
//		Intent intent = new Intent(Intent.ACTION_VIEW, uri);
//		startActivity(intent);
	}

}
