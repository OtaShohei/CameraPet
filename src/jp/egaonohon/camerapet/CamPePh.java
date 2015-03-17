package jp.egaonohon.camerapet;

import java.util.ArrayList;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;

public class CamPePh {
	/**
	 * メンバ変数。直近撮影枚数を取得。
	 */
	int takenPhNum;
	private Context context;
	/** Logのタグを定数で確保 */
	private static final String TAG = "CamPePH";

	public ArrayList<Bitmap> get(Context context) {
		// TODO 自動生成されたコンストラクター・スタブ

		this.context = context;
		/**
		 * 直近撮影済み枚数を取得
		 */
		takenPhNum = TakenPhNum.load(context);
		ArrayList<Bitmap> list = load();
		return list;
	}

	private ArrayList<Bitmap> load() {
		ArrayList<Bitmap> list = new ArrayList<Bitmap>();
		ContentResolver cr = context.getContentResolver();
		Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
		Cursor c = context.getContentResolver().query(uri, null, null, null, null);
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
