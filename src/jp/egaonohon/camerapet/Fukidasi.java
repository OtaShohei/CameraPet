package jp.egaonohon.camerapet;

import android.view.View;

public class Fukidasi implements Runnable {

	private boolean isVisible;
	private Thread th;
	protected static final int fukidasiHyojiTime = 10000;
	/** Logのタグを定数で確保 */
	private static final String TAG = "Fukidasi";

	/**
	 * コンストラクタ。
	 */
	public Fukidasi() {
		super();
		setVisible(true);
		CameLog.setLog(TAG, "isVisibleは" + isVisible());
		setTh(new Thread(this));
		getTh().start();
		CameLog.setLog(TAG, "吹き出しスレッドを開始");
	}

	@Override
	public void run() {
		while (getTh() != null) {
			try {
				CameLog.setLog(TAG, "吹き出しスレッドを10秒停止。10秒吹き出しを掲出。");
				Thread.sleep(fukidasiHyojiTime);
				setVisible(false);
				stop();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	/** 吹き出しスレッドを停止 */
	public void stop() {
		CameLog.setLog(TAG, "吹き出しスレッドを停止");
		setTh(null);
	}

	public String getMsg(View view, int eventCode) {
		CameLog.setLog(TAG, "飼い主歓迎メッセージを呼び出し。イベントコードは" + eventCode);
		return FukidasiTxt.make(view, eventCode);
	}

	public boolean isVisible() {
		return isVisible;
	}

	public void setVisible(boolean isVisible) {
		this.isVisible = isVisible;
	}

	public Thread getTh() {
		return th;
	}

	public void setTh(Thread th) {
		this.th = th;
	}

	/** 吹き出しのThreadを停止するメソッド */
	public void stopThread() {
		th = null;
	}

}
