package jp.egaonohon.camerapet;

import android.app.Activity;
import android.content.ContentResolver;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.GridView;
import java.util.ArrayList;

/**
 * 直近撮影済み写真を取得するクラス。 参考↓
 * http://dev.classmethod.jp/smartphone/basic-android-component-04-gridview/
 * 
 * @author OtaShohei
 *
 */
public class GetPh extends Activity {

	/**
	 * メンバ変数。直近撮影枚数を取得。
	 */
	int takenPhNum;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		/**
		 * 直近撮影済み枚数を取得
		 */
		takenPhNum = TakenPhNum.load(this);

		super.onCreate(savedInstanceState);
		setContentView(R.layout.encyclopedia);

		ArrayList<Bitmap> list = load();
		BitmapAdapter adapter = new BitmapAdapter(getApplicationContext(),
				R.layout.list_item, list);

		GridView gridView = (GridView) findViewById(R.id.gridView1);
		gridView.setAdapter(adapter);

	}

	private ArrayList<Bitmap> load() {
		ArrayList<Bitmap> list = new ArrayList<Bitmap>();
		ContentResolver cr = getContentResolver();
		Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
		Cursor c = managedQuery(uri, null, null, null, null);
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
// GridView mGrid;
// private int CursorNum = 0;
//
// /**
// * Called when the activity is first created.
// * 参考サイト:http://techbooster.org/android/application/6282/
// * 撮影枚数を取得したあとなので、while文で回しているところを枚数分のif文に書き換えてメモリ不足を防ぐ。
// */
// @SuppressLint("UseSparseArrays")
// @Override
// public void onCreate(Bundle savedInstanceState) {
// super.onCreate(savedInstanceState);
// setContentView(R.layout.encyclopedia);
//
// /**
// * 直近撮影済み枚数を取得
// */
// CursorNum = TakenPhNum.load(this);
// Log.d("CAMERA", "CursorNum" + CursorNum+"枚");
//
// // レコードの取得
// @SuppressWarnings("deprecation")
// Cursor cursor = managedQuery(
// MediaStore.Images.Media.EXTERNAL_CONTENT_URI, null, null, null,
// null);
//
// cursor.moveToFirst();
//
// // init for loop
// int fieldIndex;
// Long id;
// int cnt = 0, VolMax = 0;
// HashMap<Integer, Uri> uriMap = new HashMap<Integer, Uri>(); // URIをMapで管理する
//
// for (int i = 1; i < CursorNum+1; i++) {
// // int j = FOCUSED_STATE_SET[i];
// // カラムIDの取得
// fieldIndex = cursor.getColumnIndex(MediaStore.Images.Media._ID);
// id = cursor.getLong(fieldIndex);
//
// // IDからURIを取得
// Uri bmpUri = ContentUris.withAppendedId(
// MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id);
// uriMap.put(cnt, bmpUri);
// cnt++;
// cursor.moveToNext();
// }
// cursor.close();
//
// VolMax = --cnt;
// cnt = 0;
//
// /* Setting GridView */
// mGrid = (GridView) findViewById(R.id.myGrid);
// mGrid.setAdapter(new myAdapter(getContentResolver(), uriMap, VolMax));
// }
//
// // GridView用のCustomAdapter
// //
// public class myAdapter extends BaseAdapter {
// private ContentResolver cr;
// private HashMap<Integer, Uri> hm;
// private int MAX;
// private Bitmap tmpBmp;
// ImageView imageView;
//
// public myAdapter(ContentResolver _cr, HashMap<Integer, Uri> _hm, int max) {
// cr = _cr;
// hm = _hm;
// // MAX = max;
// MAX = 30;
// }
//
// public View getView(int position, View convertView, ViewGroup parent) {
// if (convertView == null) {
// imageView = new ImageView(GetPh.this);
// imageView.setLayoutParams(new GridView.LayoutParams(100, 100));
// } else {
// imageView = (ImageView) convertView;
// }
//
// try {
// tmpBmp = MediaStore.Images.Media
// .getBitmap(cr, hm.get(position));//ここで例外発生……
// } catch (FileNotFoundException e) {
// e.printStackTrace();
// } catch (IOException e) {
// e.printStackTrace();
// }
// /** ここでbmpを貼りこみ */
// imageView.setImageBitmap(tmpBmp);
//
// return imageView;
// }
//
// public final int getCount() {
// return MAX;
// }
//
// public final Object getItem(int position) {
// return position;
// }
//
// public final long getItemId(int position) {
// return position;
// }
// }

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
