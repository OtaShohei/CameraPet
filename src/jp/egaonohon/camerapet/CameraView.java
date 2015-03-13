package jp.egaonohon.camerapet;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Calendar;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.ShutterCallback;
import android.hardware.Camera.Size;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.MediaStore.Images.Media;
import android.text.format.DateFormat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Toast;

/**
 * CameraViewは、CameraBasicほぼ、そのまま。 ただ、AFを使うようにしているのが違う。
 *
 * @author 1107AND
 *
 */
@SuppressLint("WorldReadableFiles") public class CameraView extends SurfaceView {
	private Camera petCam;
	private static final String SD_CARD = "/sdcard/";
	private static ContentResolver contentResolver = null;
	private boolean afStart = false;

	/*
	 * コンストラクタ3種ー＞オリジナルの部品XMLからの利用を可能にするため
	 */
	public CameraView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}

	public CameraView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public CameraView(Context context) {
		super(context);
		init(context);
	}

	public void init(Context context) {
		contentResolver = context.getContentResolver();

		SurfaceHolder holder = getHolder();
		holder.addCallback(new SurfaceHolder.Callback() {
			public void surfaceCreated(SurfaceHolder holder) {
				petCam = Camera.open(0);
				try {
					petCam.setPreviewDisplay(holder);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			public void surfaceChanged(SurfaceHolder holder, int format,
					int width, int height) {
				petCam.stopPreview();
				Parameters params = petCam.getParameters();

				Size sz = params.getSupportedPreviewSizes().get(0);
				params.setPreviewSize(sz.width, sz.height);

				petCam.setParameters(params);
				setCameraParameters(petCam);
				petCam.startPreview();

			}

			public void surfaceDestroyed(SurfaceHolder holder) {
				petCam.release();
				petCam = null;
			}
		});
		// holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
	}

	// カメラに対して画像のサイズの指定をしている。
	private void setCameraParameters(Camera camera) {
		// 元の設定を読んでおく
		Parameters parameters = camera.getParameters();
		// 変更するところだけ変更する。
		parameters.setPictureSize(480, 320); // Default:2048x1536
		// 変更内容を保存する。
		camera.setParameters(parameters);
	}

	/**
	 * onAutoFocus()メソッドを呼び出すことを onTouchのタイミングで実は行っている。 したがって、やや時間がかかっている。
	 */
	@Override
	public boolean onTouchEvent(MotionEvent e) {
		if (e.getAction() == MotionEvent.ACTION_DOWN) {
			// Log.v("CAMERA", "ontouch");
			// AutoFoucusする要求を発行！
			if (afStart == false)
				onAutoFocus();
		}
		return true;
	}

	public void onAutoFocus() {
		afStart = true;
		petCam.autoFocus(new AutoFocusCallback() {// c.autoFocusでオートフォーカスの指示を出す。
			@Override
			public void onAutoFocus(boolean success, Camera camera) {
				camera.takePicture(new ShutterCallback() {
					public void onShutter() {
					}
				}, null, new Camera.PictureCallback() {
					@Override
					public void onPictureTaken(byte[] data, Camera camera) {
						try {
							String dataName = "photo_"
									+ String.valueOf(Calendar.getInstance()
											.getTimeInMillis()) + ".jpg";
							/**
							 * saveDataToURIはギャラリーに直接書き込むメソッド。
							 * SDカード経由の時に用いた、Environment
							 * .getExternalStorageState()を使うかどうかはどちらでもいい。
							 */
							saveFile(data, dataName);
							camera.startPreview();// プレビューを再度表示開始。
							afStart = false;// オートフォーカスをオフ&この動作まで再び写真撮影をさせない。
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				});
			}
		});
	}

	// コンテンツプロバイダ経由で保存するメソッド(ギャラリーに登録される)
	// private void saveDataToURI(byte[] data, String dataName) {
	// Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
	// ContentValues values = new ContentValues();
	// values.put(Media.DISPLAY_NAME, dataName);
	// values.put(Media.DESCRIPTION, "taken with G1");
	// values.put(Media.MIME_TYPE, "image/jpeg");
	// values.put(Media.DATE_TAKEN, System.currentTimeMillis());

	// Uri uri = contentResolver.insert(Media.EXTERNAL_CONTENT_URI, values);
	// try {
	// OutputStream outStream = contentResolver.openOutputStream(uri);
	// bitmap.compress(Bitmap.CompressFormat.JPEG, 90, outStream);
	// outStream.close();
	// } catch (Exception e) {
	//
	// }
	//
	// }

	// ギャラリーに画像を保存する
	public void saveFile(byte[] data, String dataName) {
		Bitmap myBitmap = null;
		Canvas bitmapCanvas = null;

		Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
		ContentValues values = new ContentValues();
		values.put(Media.DISPLAY_NAME, dataName);
		values.put(Media.DESCRIPTION, "taken with G1");
		values.put(Media.MIME_TYPE, "image/jpeg");
		values.put(Media.DATE_TAKEN, System.currentTimeMillis());

		// SDカードが利用可能か確認
		String status = Environment.getExternalStorageState();

		File sdcardDir;
		if (status.equals(Environment.MEDIA_MOUNTED)) {
			String sdcardDirPath = Environment.getExternalStorageDirectory()
					.getAbsolutePath() + "/CameraPet";
			sdcardDir = new File(sdcardDirPath);
			// DIRのチェックと作成
			if (!sdcardDir.exists()) {
				sdcardDir.mkdir();
			}

		} else {
			Toast.makeText(getContext(), "SDカードにアクセスできません", Toast.LENGTH_SHORT)
					.show();
			return;
		}
		/**
		 * ファイル名の作成
		 */
		long msec = System.currentTimeMillis();
		String fname = DateFormat.format("yyyy-MM-dd_kk.mm.ss", msec)
				.toString();
		fname = sdcardDir.getAbsolutePath() + "/" + fname + "png";
		/**
		 * ファイルへ画像オブジェクトを書き込む。 openOutputStreamの引数がおかしい問題が未解決。
		 */

		/**
		 * フルパスからuri変換
		 */
//		Uri uri = Uri.fromFile(sdcardDir);
		try {			
//			OutputStream outStream = contentResolver.openOutputStream(uri);
//			bitmap.compress(Bitmap.CompressFormat.JPEG, 90, outStream);
//			outStream.flush();
//			outStream.close();
			 FileOutputStream outstream = new FileOutputStream(fname);
			 myBitmap.compress(CompressFormat.JPEG, 100, outstream);
			 outstream.flush();
			 outstream.close();
		} catch (Exception e) {
			Toast.makeText(getContext(), "ファイルアクセスできません", Toast.LENGTH_SHORT)
					.show();
		}
		// ギャラリーに登録
		ContentResolver contentResolver = getContext().getContentResolver();
		ContentValues contentValues = new ContentValues();
		contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "image/png");
		contentValues.put(MediaStore.MediaColumns.DATA, fname);
		contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
				contentValues);

	}
}
