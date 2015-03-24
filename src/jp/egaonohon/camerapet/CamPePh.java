package jp.egaonohon.camerapet;

import java.util.ArrayList;

import android.R.integer;
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
	 * メンバ変数。
	 */
	/** 直近撮影枚数 */
	int takenPhNum;
	/** 欲しい1枚写真の昇順番号 */
	int targetPhNum;
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
	 * 指定した1枚の写真をBitmapで取得するメソッド。 
	 *
	 * @param context
	 * @return
	 */
	public  Bitmap choicedOneGet(Context context, int targetPhNum) {
		this.context = context;
		this.targetPhNum = targetPhNum;

		CameLog.setLog(TAG, "choicedOneGet()で" + targetPhNum + "番目の写真を探します！");
		ArrayList<Bitmap> list = load(targetPhNum);
		Bitmap choicedPh =list.get(0);
		CameLog.setLog(TAG, "choicedOneGet()で指定写真1枚を取得しました。！");
		return choicedPh;
	}

	public ArrayList<Bitmap> choicedload(int targetPhNum) {
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
			if (i == (c.getCount() - targetPhNum)) {
				Bitmap bmp = MediaStore.Images.Thumbnails.getThumbnail(cr, id,
						MediaStore.Images.Thumbnails.MICRO_KIND, null);
				list.add(bmp);
			}
			c.moveToNext();
		}
		return list;
	}

	/**
	 * 直近撮影写真を複数まとめてArrayList<Bitmap>で取得するメソッド。 
	 * メモリ負担が大きいので使用は慎重に。
	 *
	 * @param context
	 * @return
	 */
	public  ArrayList<Bitmap> get(Context context, int takenPhNum) {
		this.context = context;
		this.takenPhNum = takenPhNum;

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
	}}

