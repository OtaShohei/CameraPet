package jp.egaonohon.camerapet;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.Button;

public class CameraActivity extends Activity {
	Button shot;
	Button back;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		new CameraView(this);
		/** カメラビューをsetContentView */
		setContentView(R.layout.camera);
		LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT);
		/**
		 * addContentViewした時点で、CameraOvlViewクラスのonSizeChanged()メソッドが動いてwとhが取得される
		 * 。 プレビューの上にオーバーレイさせる。
		 */
		addContentView(new CameraOvlView(this), params);
		/** スクリーンが自動でオフになるのを防ぐ。 */
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		/** 戻るボタンの参照を取得。 */
		back = (Button) findViewById(R.id.returnBtn);
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
		/** スクリーンが自動でオフになるのを防いでいたフラグをきちんとオフにする。画面から抜けるときにはOFFにしないと大変なことに。 */
		getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
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