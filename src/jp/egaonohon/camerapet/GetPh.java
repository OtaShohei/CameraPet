package jp.egaonohon.camerapet;

import java.util.ArrayList;

import android.app.Activity;
import android.content.ContentResolver;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.GridView;

/**
 * 直近撮影済み写真を取得するクラス。 参考↓
 * http://dev.classmethod.jp/smartphone/basic-android-component-04-gridview/
 * CamPePHクラスが実装されたら不要となるクラスです。
 * @author OtaShohei
 *
 */
public class GetPh extends Activity {

	/** メンバ変数。直近撮影枚数。 */
	int takenPhNum = 0;
	/** これまでの撮影回数 */
	private static int intTotalShotCnt = 0;
	/** Logのタグを定数で確保 */
	private static final String TAG = "GetPh";

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		/**
		 * 直近撮影済み枚数を取得
		 */
		try {
			takenPhNum = CamPeDb.getNowShotCnt(this);
		} catch (Exception e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}

		super.onCreate(savedInstanceState);
		setContentView(R.layout.encyclopedia);

		ArrayList<Bitmap> list = load();
		BitmapAdapter adapter = new BitmapAdapter(getApplicationContext(),
				R.layout.list_item, list);

		GridView gridView = (GridView) findViewById(R.id.gridView1);
		gridView.setAdapter(adapter);
		CameLog.setLog(TAG, "onCreate");
	}

	private ArrayList<Bitmap> load() {
		ArrayList<Bitmap> list = new ArrayList<Bitmap>();
		ContentResolver cr = getContentResolver();
		Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
		Cursor c = getContentResolver().query(uri, null, null, null, null);
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
		CameLog.setLog(TAG, "load");
		return list;
	}
}

/**
 * 画像を縮小するメソッドは以下のURLに記載の下記のコードを参考に。
 * http://qiita.com/exilias/items/38075e08ca45d223cf92
 */
// InputStream inputStream =
// getContentResolver().openInputStream(data.getData());
//
// // 画像サイズ情報を取得する
// BitmapFactory.Options imageOptions = new BitmapFactory.Options();
// imageOptions.inJustDecodeBounds = true;
// BitmapFactory.decodeStream(inputStream, null, imageOptions);
// Log.v("image", "Original Image Size: " + imageOptions.outWidth + " x " +
// imageOptions.outHeight);
//
// inputStream.close();
//
// // もし、画像が大きかったら縮小して読み込む
// // 今回はimageSizeMaxの大きさに合わせる
// Bitmap bitmap;
// int imageSizeMax = 500;
// inputStream = getContentResolver().openInputStream(data.getData());
// float imageScaleWidth = (float)imageOptions.outWidth / imageSizeMax;
// float imageScaleHeight = (float)imageOptions.outHeight / imageSizeMax;
//
// // もしも、縮小できるサイズならば、縮小して読み込む
// if (imageScaleWidth > 2 && imageScaleHeight > 2) {
// BitmapFactory.Options imageOptions2 = new BitmapFactory.Options();
//
// // 縦横、小さい方に縮小するスケールを合わせる
// int imageScale = (int)Math.floor((imageScaleWidth > imageScaleHeight ?
// imageScaleHeight : imageScaleWidth));
//
// // inSampleSizeには2のべき上が入るべきなので、imageScaleに最も近く、かつそれ以下の2のべき上の数を探す
// for (int i = 2; i <= imageScale; i *= 2) {
// imageOptions2.inSampleSize = i;
// }
//
// bitmap = BitmapFactory.decodeStream(inputStream, null, imageOptions2);
// Log.v("image", "Sample Size: 1/" + imageOptions2.inSampleSize);
// } else {
// bitmap = BitmapFactory.decodeStream(inputStream);
// }
//
// inputStream.close();
