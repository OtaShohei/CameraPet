package jp.egaonohon.camerapet;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Calendar;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.ShutterCallback;
import android.hardware.Camera.Size;
import android.net.Uri;
import android.provider.MediaStore.Images.ImageColumns;
import android.provider.MediaStore.Images.Media;
import android.provider.MediaStore.MediaColumns;
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
@SuppressLint("ClickableViewAccessibility")
public class CameraView extends SurfaceView {
	private Camera petCam;
	private static ContentResolver contentResolver = null;
	/** 写真保存完了まで次の写真撮影をさせないためのboolean値*/
	private boolean afStart = false;
	/** ボタン押下回数用 */
	private int cntNum = 0;
	/** Logのタグを定数で確保 */
	private static final String TAG = "CameraView";

	/**
	 * コンストラクタ3種ー＞オリジナルの部品XMLからの利用を可能にするため。
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
			@Override
			public void surfaceCreated(SurfaceHolder holder) {
				petCam = Camera.open(0);
				try {
					petCam.setPreviewDisplay(holder);
				} catch (IOException e) {
					e.printStackTrace();
				}
				CameLog.setLog(TAG, "surfaceCreated");
			}

			@Override
			public void surfaceChanged(SurfaceHolder holder, int format,
					int width, int height) {
				petCam.stopPreview();
				Parameters params = petCam.getParameters();

				// 縦画面の場合回転させる
				if (width < height) {
					// 縦画面
					petCam.setDisplayOrientation(90);
				} else {
					// 横画面
					petCam.setDisplayOrientation(0);
				}
				Size sz = params.getSupportedPreviewSizes().get(0);
				params.setPreviewSize(sz.width, sz.height);

				petCam.setParameters(params);
				setCameraParameters(petCam);
				petCam.startPreview();
				CameLog.setLog(TAG, "surfaceChanged");

			}

			@Override
			public void surfaceDestroyed(SurfaceHolder holder) {
				petCam.release();
				petCam = null;
				CameLog.setLog(TAG, "surfaceDestroyed");
				/**
				 * 撮影回数をDatabaseファイルに記録。
				 */
				CamPeDb.saveNowCount(getContext(), cntNum);
			}
		});
		// holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

		Toast.makeText(getContext(), "画面タップで撮影できます。", Toast.LENGTH_SHORT)
				.show();
		CameLog.setLog(TAG, "init");
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
			CameLog.setLog(TAG, "onTouchEvent");
			btnCount();
			/** AutoFoucusする要求を発行する */
			if (afStart == false)
				onAutoFocus();
			CameLog.setLog(TAG, "onTouchEvent");
		}
		return true;
	}

	public void onAutoFocus() {
		afStart = true;
		petCam.autoFocus(new AutoFocusCallback() {// c.autoFocusでオートフォーカスの指示を出す。
			@Override
			public void onAutoFocus(boolean success, Camera camera) {
				camera.takePicture(new ShutterCallback() {
					@Override
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
							saveDataToURI(data, dataName);
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

	/**
	 * コンテンツプロバイダ経由で保存するメソッド(ギャラリーに登録される)
	 *
	 * @param data
	 * @param dataName
	 */
	private void saveDataToURI(byte[] data, String dataName) {
		Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
		ContentValues values = new ContentValues();
		values.put(MediaColumns.DISPLAY_NAME, dataName);
		values.put(ImageColumns.DESCRIPTION, "taken with CameraPet");
		values.put(MediaColumns.MIME_TYPE, "image/jpeg");
		values.put(ImageColumns.DATE_TAKEN, System.currentTimeMillis());
		Uri uri = contentResolver.insert(Media.EXTERNAL_CONTENT_URI, values);
		try {
			OutputStream outStream = contentResolver.openOutputStream(uri);
			bitmap.compress(Bitmap.CompressFormat.JPEG, 90, outStream);
			outStream.close();
		} catch (Exception e) {
		}
		CameLog.setLog(TAG, "saveDataToURI");
	}

	/**
	 * ボタン押下回数カウントを行うメソッド。
	 */
	void btnCount() {
		cntNum++;

		/**
		 * 撮影回数を表示。
		 */
		Toast.makeText(getContext(), "撮影回数" + cntNum, Toast.LENGTH_SHORT)
				.show();
		CameLog.setLog(TAG, "btnCount");
	}
}