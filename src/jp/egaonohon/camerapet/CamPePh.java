package jp.egaonohon.camerapet;

import java.util.ArrayList;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;

/**
 * 直近撮影写真をBitmapで管理するクラス。
 *
 * @author OtaShohei
 *
 */
public class CamPePh {
	/**
	 * メンバ変数。直近撮影枚数を取得。
	 */
	int takenPhNum;
	private Context context;
	/** Logのタグを定数で確保 */
	private static final String TAG = "CamPePH";

	/**
	 * 直近撮影写真を1枚のみ取得するメソッド。 
	 *
	 * @param context
	 * @return
	 */
	public Bitmap getOne(Context context) {
		this.context = context;
		takenPhNum = 1;
		ArrayList<Bitmap> list = load(takenPhNum);
		Bitmap oneCPP = list.get(0);
		CameLog.setLog(TAG, "直近撮影済み写真を取得。" + list.size() + "枚です！");
		return oneCPP;
	}
	
	/**
	 * 直近撮影写真をArrayList<Bitmap>で取得するメソッド。 2015-03-18時点では未完成です。
	 *
	 * @param context
	 * @return
	 */
	public  ArrayList<Bitmap> get(Context context, int takenPhNum) {
		this.context = context;
		this.takenPhNum = takenPhNum;
//		/**
//		 * 直近撮影済み枚数を取得。0枚の場合は1を返す仕様です。
//		 */
//			if (takenPhNum == 0) {
//				takenPhNum = 1;
//				CameLog.setLog(TAG, "直近画像0枚だったので1枚にリセット");
//			}

		CameLog.setLog(TAG, "直近撮影済み枚数を取得。" + takenPhNum + "枚です！");
		ArrayList<Bitmap> list = load(takenPhNum);

		CameLog.setLog(TAG, "直近撮影済み写真を取得。" + list.size() + "枚です！");
		return list;
	}

	public ArrayList<Bitmap> load(int takenPhNum) {
		ArrayList<Bitmap> list = new ArrayList<Bitmap>();
		ContentResolver cr = context.getContentResolver();
		Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
		Cursor c = context.getContentResolver().query(uri, null, null, null,
				null);
		c.moveToFirst();
		for (int i = 0; i < c.getCount(); i++) {
			// for (int i = 0; i < CursorNum; i++) {
			long id = c.getLong(c.getColumnIndexOrThrow("_id"));
			/**
			 * 直近撮影写真のみをBitmapにしてlistにadd。それ以外はaddしません。
			 */
			if (i >= (c.getCount() - takenPhNum)) {
				Bitmap bmp = MediaStore.Images.Thumbnails.getThumbnail(cr, id,
						MediaStore.Images.Thumbnails.MICRO_KIND, null);
				list.add(bmp);
			}
			c.moveToNext();
		}
		return list;
	}
}
