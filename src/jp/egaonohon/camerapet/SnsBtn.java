package jp.egaonohon.camerapet;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Bitmap.CompressFormat;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.MediaStore.Images;
import android.text.format.DateFormat;
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
	
	/** ツイッター投稿時の画像ファイル名 */
	static String fileName;
	
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

	static Uri imageuri;
	protected final static Matrix matrix = new Matrix();

	/**
	 * Twitter投稿メソッド。
	 *
	 * @param v
	 */
	public static void goTwitter(Context context, String speciesName) {
		if (isShareAppInstall(context, TWITTER_ID)) {

			/** ペット画像取得 */
			Bitmap nowPetPh = getPetEncPh(context);
			/** 画像を保存 */
			imageuri = addImageAsApplication(context.getContentResolver(),
					nowPetPh);

			/** 投稿メッセージ生成 */
			makeMsg(context, speciesName);
			Intent intent = new Intent();
			intent.setAction(Intent.ACTION_SEND);
			intent.setPackage(SHAREPACKAGES[TWITTER_ID]);
			intent.setType("image/png");
			intent.putExtra(Intent.EXTRA_STREAM, imageuri);
			intent.putExtra(Intent.EXTRA_TEXT, snsTxt);
			context.startActivity(intent);
		} else {
			shareAppDl(context, TWITTER_ID);
		}
		
		/** ツイッター投稿写真削除用のインスタンスを生成 */
		TwitterPh nowTwitterPh = new TwitterPh(context);
		nowTwitterPh.deleteGalleryFile(context.getContentResolver(), PATH, fileName);
	}

	// twitter投稿時の画像保存
	private static final String TAG1 = "ImageManager";
	private static final String APPLICATION_NAME = "CameraPet";
	private static final Uri IMAGE_URI = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
	private static final String PATH = Environment
			.getExternalStorageDirectory().toString() + "/" + APPLICATION_NAME;

	public static Uri addImageAsApplication(ContentResolver cr, Bitmap bitmap) {
		long dateTaken = System.currentTimeMillis();
		fileName = createName(dateTaken) + ".jpg";
		return addImageAsApplication(cr, fileName, dateTaken, PATH, fileName, bitmap,
				null);
	}

	// twitter投稿時の画像ファイル名を生成
	private static String createName(long dateTaken) {
		return DateFormat.format("yyyy-MM-dd_kk.mm.ss", dateTaken).toString();
	}

	// twitter投稿時の画像をアルバムに保存しURI取得
	public static Uri addImageAsApplication(ContentResolver cr, String name,
			long dateTaken, String directory, String filename, Bitmap source,
			byte[] jpegData) {
		OutputStream outputStream = null;
		String filePath = directory + "/" + filename;
		try {
			File dir = new File(directory);
			if (!dir.exists()) {
				dir.mkdirs();

			}
			File file = new File(directory, filename);
			if (file.createNewFile()) {
				outputStream = new FileOutputStream(file);
				if (source != null) {
					source.compress(CompressFormat.JPEG, 75, outputStream);
				} else {
					outputStream.write(jpegData);
				}
			}

		} catch (FileNotFoundException ex) {

			return null;
		} catch (IOException ex) {

			return null;
		} finally {
			if (outputStream != null) {
				try {
					outputStream.close();
				} catch (Throwable t) {
				}
			}
		}
		ContentValues values = new ContentValues(7);
		values.put(Images.Media.TITLE, name);
		values.put(Images.Media.DISPLAY_NAME, filename);
		values.put(Images.Media.DATE_TAKEN, dateTaken);
		values.put(Images.Media.MIME_TYPE, "image/jpeg");
		values.put(Images.Media.DATA, filePath);
		return cr.insert(IMAGE_URI, values);
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
	 * アプリが無かったのでGooglePlayに飛ばすメソッド。
	 *
	 * @param shareId
	 */
	private static void shareAppDl(Context context, int shareId) {
		Uri uri = Uri.parse("market://details?id=" + SHAREPACKAGES[shareId]);
		Intent intent = new Intent(Intent.ACTION_VIEW, uri);
		context.startActivity(intent);
	}

	/** 投稿メッセージを生成するメソッド */
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

	/** 投稿画像を取得するメソッド */
	public static Bitmap getPetEncPh(Context context) {
		Bitmap petPh = BitmapFactory.decodeResource(context.getResources(),
				R.drawable.pet001a_enc);
		/** ペットの型番を取得 */
		String petModelNumberString = CamPePref
				.loadPetModelNumberForUnexpectedVersionUp(context);

		if (petModelNumberString.equals("Pet001A")) {
		} else if (petModelNumberString.equals("Pet002A")) {
			petPh = BitmapFactory.decodeResource(context.getResources(),
					R.drawable.pet002a_enc);
		} else if (petModelNumberString.equals("Pet003A")) {
			petPh = BitmapFactory.decodeResource(context.getResources(),
					R.drawable.pet003a_enc);
		}
		return petPh;
	}
	
	/** ツイッター投稿画像を削除するメソッド */
	public static boolean deleteGalleryFile(ContentResolver cr, String directory, String filename) {
//		if(isWrite()) {
			Cursor cursor = null;
			try{
				filename = directory + "/" + filename;
				cursor = cr.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, 
						new String[] {MediaStore.Images.Media._ID}, 
						MediaStore.Images.Media.DATA + " = ?", 
						new String[]{filename}, 
						null);
					
				if(cursor.getCount() != 0) {
					cursor.moveToFirst();
					
//					Uri uri = IMAGE_URI;
					Uri uri = ContentUris.appendId(
							MediaStore.Images.Media.EXTERNAL_CONTENT_URI.buildUpon(),
							cursor.getLong(cursor.getColumnIndex(MediaStore.Images.Media._ID))).build();
					cr.delete(uri, null, null);
						
					return true;
				}
			}finally {
				if(cursor != null)
					cursor.close();
			}
//		}	
		return false;
	}
}
