package jp.egaonohon.camerapet;

import java.util.List;

import jp.ogwork.camerafragment.camera.CameraFragment;
import jp.ogwork.camerafragment.camera.CameraSurfaceView.OnPictureSizeChangeListener;
import jp.ogwork.camerafragment.camera.CameraSurfaceView.OnPreviewSizeChangeListener;
import jp.ogwork.camerafragment.camera.CameraSurfaceView.OnTakePictureListener;
import android.content.Intent;
import android.graphics.Bitmap;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.Size;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.Button;
import android.widget.FrameLayout;

public class ShutterActivity extends FragmentActivity {

	/**
	 * メンバ変数
	 */
	protected static final String TAG = ShutterActivity.class.getName();
	protected static final String TAG_CAMERA_FRAGMENT = "camera";
	private FrameLayout cameraFl;
	private CameraFragment cameraFragment;
	private Button back;
	private Button autofocusBtn;
	private Button shotBtn;
	private Button btn_change_camera_direction;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_shutter);

		findViewById();

		if (savedInstanceState == null) {
			/** カメラサイズを決定するため、Viewのサイズを取る */
			cameraFl.getViewTreeObserver().addOnGlobalLayoutListener(
					new OnGlobalLayoutListener() {

						@SuppressWarnings("deprecation")
						@Override
						public void onGlobalLayout() {
							cameraFl.getViewTreeObserver()
									.removeGlobalOnLayoutListener(this);
							addCameraFragment(cameraFl.getWidth(),
									cameraFl.getHeight(), R.id.cameraFl);
						}
					});
		}

		autofocusBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				autoFocus();
			}
		});

		shotBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				cameraFragment.enableShutterSound(false);
				takePicture();
			}
		});

		btn_change_camera_direction = (Button) findViewById(R.id.btn_change_camera_direction);
		btn_change_camera_direction.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				changeCameraDirection();
			}
		});

		/**
		 * スクリーンが自動でオフになるのを防ぐ。
		 */
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
	}

	/***
	 * CameraFragmentをviewに追加
	 * 
	 * @param viewWidth
	 * @param viewHeight
	 */
	public void addCameraFragment(final int viewWidth, final int viewHeight,
			int containerViewId) {
		cameraFragment = new CameraFragment();
		Bundle args = new Bundle();
		args.putInt(CameraFragment.BUNDLE_KEY_CAMERA_FACING,
				Camera.CameraInfo.CAMERA_FACING_BACK);
		cameraFragment.setArguments(args);

		/** プレビューサイズリスナの設定 */
		cameraFragment
				.setOnPreviewSizeChangeListener(new OnPreviewSizeChangeListener() {

					/** サイズ変更前 */
					@Override
					public Size onPreviewSizeChange(
							List<Size> supportedPreviewSizeList) {
						return cameraFragment.choosePreviewSize(
								supportedPreviewSizeList, 0, 0, viewWidth,
								viewHeight);
					}

					/** サイズ変更後 */
					@Override
					public void onPreviewSizeChanged(Size previewSize) {

						float viewAspectRatio = (float) viewHeight
								/ previewSize.width;
						int height = viewHeight;
						int width = (int) (viewAspectRatio * previewSize.height);

						/** 縦横どちらでfixさせるか */
						if (width < viewWidth) {
							/** 縦Fixだと幅が足りないと判断 */
							/** 横fixさせる */
							width = viewWidth;
							height = (int) (viewAspectRatio * previewSize.width);
						}
						/** cameraSurfaceViewのサイズ変更 */
						cameraFragment.setLayoutBounds(width, height);
						return;
					}
				});

		/** カメラ保存サイズリスナの設定 */
		cameraFragment
				.setOnPictureSizeChangeListener(new OnPictureSizeChangeListener() {
					@Override
					public Size onPictureSizeChange(
							List<Size> supportedPictureSizeList) {
						/** 画面横幅以下の中で最大サイズを選ぶ */
						return cameraFragment.choosePictureSize(
								supportedPictureSizeList, 0, 0, viewWidth,
								viewHeight);
					}
				});

		getSupportFragmentManager().beginTransaction()
				.add(containerViewId, cameraFragment, TAG_CAMERA_FRAGMENT)
				.commit();
	}

	/***
	 * 撮影・保存
	 */
	private void takePicture() {
		cameraFragment.takePicture(true, new OnTakePictureListener() {

			@Override
			public void onShutter() {

			}

			@Override
			public void onPictureTaken(Bitmap bitmap, Camera camera) {
				String path = Environment.getExternalStorageDirectory()
						.toString() + "/CameraPet";
				cameraFragment.setSavePictureDir(path);
				cameraFragment.saveBitmap(bitmap);
			}
		});
	}

	/**
	 * オートフォーカス
	 * */
	private void autoFocus() {
		cameraFragment.autoFocus();
	}

	/**
	 * リアカメラ・フロントカメラ切り替え
	 * */
	private void changeCameraDirection() {
		int cameraDirection = 0;
		if (cameraFragment.getCameraDirection() == CameraInfo.CAMERA_FACING_BACK) {
			cameraDirection = CameraInfo.CAMERA_FACING_FRONT;
		} else {
			cameraDirection = CameraInfo.CAMERA_FACING_BACK;

		}
		cameraFragment.setCameraDirection(cameraDirection);
	}

	/**
	 * CameraActivityからホームに戻るボタンのメソッド。
	 *
	 * @param v
	 */
	public void onClickGoHomeBtn(View v) {
		/**
		 * 画面移動要求を格納したインテントを作成する。 第一引数に自身(this)を設定 第二引数に移動先のクラス名を指定
		 */
		Intent intent = new Intent(ShutterActivity.this, MainActivity.class);

		/**
		 * Activity.startActivity()の第一引数にインテントを指定することで画面移動が行われる。
		 */
		startActivity(intent);
	}

	public void findViewById() {
		cameraFl = (FrameLayout) findViewById(R.id.cameraFl);
		autofocusBtn = (Button) findViewById(R.id.autofocusBtn);
		shotBtn = (Button) findViewById(R.id.shotBtn);
		back = (Button) findViewById(R.id.returnBtn);// 戻るボタンの参照を引っ張ってくる
	}

	@Override
	protected void onPause() {
		super.onPause();
		/**
		 * スクリーンが自動でオフになるのを防いでいたフラグをきちんとオフにする。画面から抜けるときにはOFFにしないと大変なことに。
		 */
		getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
	}
}
