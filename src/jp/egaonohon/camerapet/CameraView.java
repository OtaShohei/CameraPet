package jp.egaonohon.camerapet;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Calendar;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.ShutterCallback;
import android.hardware.Camera.Size;
import android.net.Uri;
import android.provider.MediaStore.Images.ImageColumns;
import android.provider.MediaStore.Images.Media;
import android.provider.MediaStore.MediaColumns;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.WindowManager;
import android.widget.Toast;

/**
 * カメラを司るクラス。AFを使うようにしている。
 *
 * @author OtaShohei
 *
 */
@SuppressLint("ClickableViewAccessibility")
public class CameraView extends SurfaceView {
	private Camera petCam;
	private static ContentResolver contentResolver = null;
	/** 写真保存完了まで次の写真撮影をさせないためのboolean値 */
	private boolean afStart = false;
	/** ボタン押下回数用 */
	private int cntNum = 0;
	/** 写真撮影でインクリメントする経験値 */
	private int gettedtotalEXP;
	/** Logのタグを定数で確保 */
	private static final String TAG = "CameraView";
	/**
	 * プレビューのサイズ。詳しくは、 http://labs.techfirm.co.jp/android/cho/1647
	 * http://d.hatena.ne.jp/TAC/20101214/1292347298
	 */
	private Size previewSize;
	private Size pictureSize;

	/**
	 * コンストラクタ3種ー＞オリジナルの部品XMLからの利用を可能にするため。
	 *
	 * @throws Exception
	 */
	public CameraView(Context context, AttributeSet attrs, int defStyle)
			throws Exception {
		super(context, attrs, defStyle);
		init(context);
	}

	public CameraView(Context context, AttributeSet attrs) throws Exception {
		super(context, attrs);
		init(context);
	}

	public CameraView(Context context) throws Exception {
		super(context);
		init(context);
	}

	public void init(final Context context) throws Exception {
		contentResolver = context.getContentResolver();

		SurfaceHolder holder = getHolder();
		holder.addCallback(new SurfaceHolder.Callback() {
			@Override
			public void surfaceCreated(SurfaceHolder holder) {
				petCam = Camera.open(0);
			}

			@SuppressWarnings("deprecation")
			@Override
			public void surfaceChanged(SurfaceHolder holder, int format,
					int width, int height) {

				petCam.stopPreview();

				Camera.Parameters params = petCam.getParameters();

				/**
				 * 端末がサポートするサイズを取得する。
				 * これにより、機種によって発生する例外を防止する。
				 */
				List<Size> supportedPictureSizes = SupportedSizesReflect
						.getSupportedPictureSizes(params);
				List<Size> supportedPreviewSizes = SupportedSizesReflect
						.getSupportedPreviewSizes(params);

				if (supportedPictureSizes != null
						&& supportedPreviewSizes != null
						&& supportedPictureSizes.size() > 0
						&& supportedPreviewSizes.size() > 0) {

					/** 2.xの場合 */

					/** 撮影サイズを設定する */
					pictureSize = supportedPictureSizes.get(0);

					/** 画像サイズを必要に応じて制限する(おまけ) */
					int maxSize = 1280;
					if (maxSize > 0) {
						for (Size size : supportedPictureSizes) {
							if (maxSize >= Math.max(size.width, size.height)) {
								pictureSize = size;
								break;
							}
						}
					}

					/** ディスプレイサイズを取得する。引数で与えられるサイズはたまにおかしいので、DisplayMetricsで取得する */
					WindowManager windowManager = (WindowManager) context
							.getSystemService(Context.WINDOW_SERVICE);
					Display display = windowManager.getDefaultDisplay();
					DisplayMetrics displayMetrics = new DisplayMetrics();
					display.getMetrics(displayMetrics);

					previewSize = getOptimalPreviewSize(supportedPreviewSizes,
							display.getWidth(), display.getHeight());

					params.setPictureSize(pictureSize.width, pictureSize.height);
					params.setPreviewSize(previewSize.width, previewSize.height);

				}
				petCam.setParameters(params);
				try {
					petCam.setPreviewDisplay(holder);
				} catch (IOException e) {
					e.printStackTrace();
				}
				petCam.startPreview();

				/** 縦画面の場合回転させる */
				if (width < height) {
					/** 縦画面 */
					petCam.setDisplayOrientation(90);
				} else {
					/** 横画面 */
					petCam.setDisplayOrientation(0);
				}
			}

			@Override
			public void surfaceDestroyed(SurfaceHolder holder) {
				petCam.release();
				petCam = null;
				CameLog.setLog(TAG, "surfaceDestroyed");
				/**
				 * 撮影回数をDatabaseファイルに記録。
				 */
				CamPeDb.saveNowCount(getContext());
				CameLog.setLog(TAG, "撮影回数をDatabaseファイルに記録");
			}
		});
		// holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

		/** 撮影ガイダンスのToast表示文字列を取得 */
		Resources res = getResources();
		String shotGuidance = res.getString(R.string.how_to_shot);
		/** 撮影ガイダンスのToast表示 */
		Toast.makeText(getContext(), shotGuidance, Toast.LENGTH_SHORT).show();
		CameLog.setLog(TAG, "init");
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

	/**
	 * ApiDemoでよく使うgetOptimalPreviewSize 詳しくはこちら。
	 * http://qiita.com/zaburo/items/b5d3815d3ec45b0daf4f
	 *
	 */
	private Camera.Size getOptimalPreviewSize(List<Camera.Size> sizes, int w,
			int h) {

		final double ASPECT_TOLERANCE = 0.1;
		double targetRatio = (double) h / w;

		if (sizes == null)
			return null;

		Camera.Size optimalSize = null;
		double minDiff = Double.MAX_VALUE;

		int targetHeight = h;

		for (Camera.Size size : sizes) {
			double ratio = (double) size.width / size.height;
			if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE)
				continue;
			if (Math.abs(size.height - targetHeight) < minDiff) {
				optimalSize = size;
				minDiff = Math.abs(size.height - targetHeight);
			}
		}

		if (optimalSize == null) {
			minDiff = Double.MAX_VALUE;
			for (Camera.Size size : sizes) {
				if (Math.abs(size.height - targetHeight) < minDiff) {
					optimalSize = size;
					minDiff = Math.abs(size.height - targetHeight);
				}
			}
		}
		return optimalSize;
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
		/** プリファレンスから前回の累計撮影回数を取得 */
		int totalShotCnt = CamPePref.loadTotalShotCnt(getContext());

		/** プリファレンスから累計経験値を取得 */
		gettedtotalEXP = CamPePref.loadTotalExp(getContext());

		/** 累積経験値が-1の場合は0にリセット */
		if (gettedtotalEXP == -1) {
			gettedtotalEXP = 0;
		}

		/** 合計撮影回数の算出 */
		totalShotCnt++;

		/** 新たな累計経験値の算出 */
		gettedtotalEXP = gettedtotalEXP + 10;

		/** プリファレンスに直近撮影回数と累計撮影回数を保存 */
		CamPePref.saveNowAndTotalShotCnt(getContext(), cntNum, totalShotCnt);

		/** プリファレンスに新たな累計経験値を保存 */
		CamPePref.saveTotalExp(getContext(), gettedtotalEXP);

		/** 撮影回数を表示 */
		Resources res = this.getResources();
		Toast.makeText(
				getContext(),
				res.getString(R.string.number_shooting) + " " + cntNum + " "
						+ res.getString(R.string.exp_increased_01) + " "
						+ (cntNum * 10)
						+ res.getString(R.string.exp_increased_02),
				Toast.LENGTH_SHORT).show();
		CameLog.setLog(TAG, "btnCount");
	}
}