package jp.egaonohon.camerapet;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.Button;

public class CameraActivity extends Activity {

	/** メンバ変数 */
	private Button back;
	/** Logのタグを定数で確保 */
	private static final String TAG = "CameraActivity";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		try {
			new CameraView(this);
		} catch (Exception e) {

			e.printStackTrace();

			/**
			 * ここでデフォルトのカメラアプリを呼び出す?　→ちょっと難易度高そうで保留。
			 * 参考　http://y-anz-m.blogspot.jp/2014/04/1chooser.html
			 */
			// /** インテントのインスタンス生成 */
			// Intent intent = new Intent();
			// /**
			// * ファイル名を指定 File型変数picture 第一引数にストレージのディレクトリを指定します。
			// * 第二引数にはカメラで撮影してできた画像ファイル名を指定。
			// */
			// File picture = new
			// File(Environment.getExternalStorageDirectory(),
			// getPicFileName());
			// /** カメラで撮影した画像をキャプチャー。カメラで撮影するアクションをセット */
			// intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
			// /** intentにカテゴリーを追加 */
			// intent.addCategory(Intent.CATEGORY_DEFAULT);
			// /**
			// * MediaStore.EXTRA_OUTPUT コンテンツリゾルバURIを表すのに使うintent-extraの名前。
			// * コンテンツリゾルバURI リクエストされたビデオや画像をおいておくためにつかわれる。fromFile
			// * ファイルからURIを作るメソッド
			// */
			// intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(picture));
			// /** インテントを開始　カメラアプリが起動 */
			// startActivityForResult(intent, 333);
		}

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
		finish();
	}

	public void makeCameraView() throws Exception {
		new CameraView(this);
	}

	/**
	 * CameraActivityからホームに戻るボタンのメソッド。
	 *
	 * @param v
	 */
	public void onClickGoHomeBtn(View v) {
		/**
		 * 画面移動要求を格納したインテントを作成する。 第一引数に自身(this)を設定 第二引数に移動先のクラス名を指定。
		 * インテントにはCameraActivityから戻ってきたことを示す文字を念のために格納。
		 */
		Intent intent = new Intent(CameraActivity.this, MainActivity.class);
		intent.putExtra("returnCam", "true");
		CameLog.setLog(TAG, "CameraActivityからputExtra完了");

		/**
		 * Activity.startActivity()の第一引数にインテントを指定することで画面移動が行われる。
		 */
		startActivity(intent);
	}

	/*
	 * (非 Javadoc)
	 * 
	 * @see android.app.Activity#onStart()
	 */
	@Override
	protected void onStart() {
		super.onStart();
		/** Google Analytics用の記述 */
		Tracker t = ((App) getApplication())
				.getTracker(App.TrackerName.APP_TRACKER);
		t.setScreenName(this.getClass().getSimpleName());
		t.send(new HitBuilders.AppViewBuilder().build());
	}
}