package jp.egaonohon.camerapet;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

/**
 * ツイッター投稿時に作成した写真を、Twitterから戻ってきたときに削除するためのクラス。
 * MainActivityのBoolean値を確認して、戻ってきたことを確認したらrunメソッドの中で削除を実行する。
 * 
 * @author OtaShohei
 *
 */
public class TwitterPh implements Runnable {

	/** twitterPhのスレッド */
	protected Thread twitterPhThread;
	/** twitterPhのContentResolver */
	protected ContentResolver twitterPhCr;
	/** twitterPhが存在しているディレクトリ */
	protected String twitterPhDirectory;
	/** twitterPhのファイル名 */
	protected String twitterPhFilename;
	/** 写真を削除していいかどうか */
	protected boolean deleteNg = false;
	/** Logのタグを定数で確保 */
	private static final String TAG = "TwitterPh";
	/** Logのタグを定数で確保 */
	protected static Context twitterPhContext;

	/** TwitterPhクラスのコンストラクタ */
	public TwitterPh(Context context) {
		twitterPhContext = context;
		twitterPhThread = new Thread(this);
		twitterPhThread.start();
	}

	/** ツイッター投稿画像を削除するメソッド */
	public boolean deleteGalleryFile(ContentResolver cr, String directory,
			String filename) {
		twitterPhCr = cr;
		twitterPhDirectory = directory;
		twitterPhFilename = filename;
		return false;
	}

	@Override
	public void run() {
		CameLog.setLog(TAG, "TwitterPhのrun動いてます");
		while (twitterPhThread != null) {

			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			/** 削除の可否を確認 */
			deleteNg = MainActivity.isReturnTwitter();

//			/** 現在起動しているか否かを確認する */
//			String mainActivityworkStatus = CamPePref
//					.loadMainActivityWorkStatus(twitterPhContext);
//			String other01ActivityworkStatus = CamPePref
//					.loadOther01ActivityWorkStatus(twitterPhContext);
//			String other02ActivityworkStatus = CamPePref
//					.loadOther02ActivityWorkStatus(twitterPhContext);

			/**
			 * ツイッター投稿を終えたか、アプリのいずれかのActivityが現在起動中でないならば、削除開始…
			 * と修正しようとしましたがこの条件文では意味が無い
			 * （結局、Twitter遷移時にCameraPetを終了されると同じこと）のでコメントアウト。
			 */
			if (!deleteNg
			// || ((!deleteNg && (mainActivityworkStatus.equals("notWork") ||
			// mainActivityworkStatus
			// .equals("notFound")))
			// && (other01ActivityworkStatus.equals("notWork") ||
			// other01ActivityworkStatus
			// .equals("notFound")) && (other02ActivityworkStatus
			// .equals("notWork") || other02ActivityworkStatus
			// .equals("notFound")))
			) {

				CameLog.setLog(TAG, "10秒経過したので削除開始");
				Cursor cursor = null;
				try {
					twitterPhFilename = twitterPhDirectory + "/"
							+ twitterPhFilename;
					cursor = twitterPhCr.query(
							MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
							new String[] { MediaStore.Images.Media._ID },
							MediaStore.Images.Media.DATA + " = ?",
							new String[] { twitterPhFilename }, null);

					if (cursor.getCount() != 0) {
						cursor.moveToFirst();

						// Uri uri = IMAGE_URI;
						Uri uri = ContentUris
								.appendId(
										MediaStore.Images.Media.EXTERNAL_CONTENT_URI
												.buildUpon(),
										cursor.getLong(cursor
												.getColumnIndex(MediaStore.Images.Media._ID)))
								.build();
						twitterPhCr.delete(uri, null, null);
						CameLog.setLog(TAG, "ツイッター投稿写真削除完了");
						// /** ツイッター写真削除可否を元に戻す */
						// CamPePref.saveTwitterPhDeleteOK(twitterPhContext,
						// false);
					}
				} finally {
					if (cursor != null) {
						cursor.close();
						twitterPhThread = null;
					}
				}
			}
		}
	}
}
