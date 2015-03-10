package jp.egaonohon.android.camerapet;

import android.app.Activity;
import android.os.Bundle;
import android.view.ViewGroup.LayoutParams;

public class CameraActivity extends Activity {
	private static CameraView cameraView;
	private static LayoutParams params;

	public static void takeShot() {
		params = new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		cameraView = new CameraView(this);
		setContentView(cameraView);// カメラビューをsetContentView
		addContentView(new CameraOvlView(this), params);// プレビューの上にオーバーレイさせる。
	}

	@Override
	protected void onResume() {
		super.onResume();
		// cameraView.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
		// cameraView.onPause();
	}
}