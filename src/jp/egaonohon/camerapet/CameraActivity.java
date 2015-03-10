package jp.egaonohon.camerapet;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.Button;

public class CameraActivity extends Activity {
	private CameraView cameraView;
	private boolean afStart = false;
	Button shot;
	Button back;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		cameraView = new CameraView(this);
		setContentView(R.layout.camera);// カメラビューをsetContentView
		LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT);
		// addContentViewした時点で、CameraOvlViewクラスのonSizeChanged()メソッドが動いてwとhが取得される。
		addContentView(new CameraOvlView(this), params);// プレビューの上にオーバーレイさせる。
		/**
		 * スクリーンが自動でオフになるのを防ぐ。
		 */
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		findViewById();
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
		/**
		 * スクリーンが自動でオフになるのを防いでいたフラグをきちんとオフにする。画面から抜けるときにはOFFにしないと大変なことに。
		 */
		getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

	}

	public void findViewById() {
		shot = (Button) findViewById(R.id.shotBtn);// 撮影ボタンの参照を引っ張ってくる
		back = (Button) findViewById(R.id.returnBtn);// 戻るボタンの参照を引っ張ってくる
	}

	/**
	 * CameraActivityのシャッターボタンのメソッド。
	 *
	 * @param v
	 */
	public void onClickTakePicBtn(View v) {

		if (afStart == false) {
			cameraView.onAutoFocus();
		}
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
		Intent intent = new Intent(CameraActivity.this, MainActivity.class);

		/**
		 * Activity.startActivity()の第一引数にインテントを指定することで画面移動が行われる。
		 */
		startActivity(intent);
	}
}