package jp.egaonohon.camerapet;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * Game画面のSurfaceView。
 * @author OtaShohei
 *
 */
@SuppressLint("WrongCall")
class CPGameSurfaceView extends SurfaceView implements SurfaceHolder.Callback,
		Runnable {
	/** SurfaceHolderをメンバ変数として確保 */
	private SurfaceHolder holder;
	private Context context = getContext();
	/** Viewの幅。ペットなどの表示の機種ごとの差異を吸収するため。 */
	int viweWidth = getWidth();
	/** Viewの高さ。ペットなどの表示の機種ごとの差異を吸収するため。 */
	int viewHeight = getHeight();
	/** Logのタグを定数で確保 */
	private static final String TAG = "CPGameSurfaceView";

	private CPGameMgr gameMgr;
	private Thread thread;


	/**
	 * 引数1つのコンストラクタ
	 *
	 *
	 * @param context
	 */
	public CPGameSurfaceView(Context context) {
		super(context);
		initialize();
	}

	/**
	 * 引数2つのコンストラクタ。
	 * 2回initializeが動いているようなので一旦コメントアウト。
	 * @param context
	 * @param attrs
	 */
	public CPGameSurfaceView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initialize();
	}

	/**
	 * いずれのコンストラクタが呼ばれてもこのメソッドが動作する。
	 */
	private void initialize() {

		/** Viewの幅。ペットなどの表示の機種ごとの差異を吸収するため。 */
		int viweWidth = getWidth();
		/** Viewの高さ。ペットなどの表示の機種ごとの差異を吸収するため。 */
		int viewHeight = getHeight();
		/** initialize時点でのViewの高さをLogで確認 */
		CameLog.setLog(TAG, "initialize()時点でのViewの幅は" + viweWidth + "Viewの高さは" + viewHeight);

		/** SurfaceHolder の取得 */
		holder = getHolder();

		/** SurfaceHolder に コールバックを設定 */
		holder.addCallback(this);
		holder.setFixedSize(viweWidth, viewHeight);
		CameLog.setLog(TAG, "Viewの幅は" + viweWidth + "Viewの高さは" + viewHeight);

		/** 半透明を設定 */
		holder.setFormat(PixelFormat.TRANSLUCENT);
		/** フォーカスをあてる */
		setFocusable(true);

		/** このViewをトップにする */
		setZOrderOnTop(true);

		CameLog.setLog(TAG, "CPGameSurfaceViewをコンストラクタから生成！");
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		// 解像度情報変更通知
		this.holder = holder;
		this.viweWidth = width;
		this.viewHeight = height;

		/** CPGameMgrインスタンスを生成。フィールドから移動した */
		gameMgr = new CPGameMgr(context, viweWidth, viewHeight);
		CameLog.setLog(TAG, "GameSurfaceViewからCPGameSurfaceViewをnewした時点のviewの幅は" + viweWidth + "viewの高さは" + viewHeight);

		thread = new Thread(this); // 別スレッドでメインループを作る
		thread.start();
		CameLog.setLog(TAG, "surfaceChanged");
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		CameLog.setLog(TAG, "surfaceCreated");
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		thread = null;
		CameLog.setLog(TAG, "surfaceDestroyed");
	}

	@SuppressLint("WrongCall")
	@Override
	public void run() {
		while (thread != null) { // メインループ
			gameMgr.onUpdate();
			onDraw(getHolder());
		}
	}

	private void onDraw(SurfaceHolder holder) {
		Canvas c = holder.lockCanvas();
		if (c == null) {
			return;
		}
		gameMgr.onDraw(c);
		holder.unlockCanvasAndPost(c);
	}

	/**
	 * タッチイベントを取得して、ペットに動作を伝える。
	 */
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		float x = event.getX(); // X座標を取得
		float y = event.getY(); // Y座標を取得
		// speedMove = true;
		CPPet.setPetMoveSize(x, y);
		CameLog.setLog(TAG, "onTouchEvent");
		// listener.onFcsChange(i);
		return true;
	}
}