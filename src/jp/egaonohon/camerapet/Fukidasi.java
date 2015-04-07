package jp.egaonohon.camerapet;

import android.content.res.Resources;
import android.view.View;

public class Fukidasi implements Runnable {

	protected boolean isVisible;
	protected Thread th;
	protected static final int fukidasiHyojiTime = 10000;
	/** Logのタグを定数で確保 */
	private static final String TAG = "simpleFukidasi";

	/**
	 * コンストラクタ。
	 */
	public Fukidasi() {
		super();
		isVisible = true;
		CameLog.setLog(TAG, "isVisibleは" + isVisible);
		th = new Thread(this);
		th.start();
		CameLog.setLog(TAG, "吹き出しスレッドを開始");
	}

	@Override
	public void run() {
		while (th != null) {
			try {
				CameLog.setLog(TAG, "吹き出しスレッドを10秒停止。10秒吹き出しを掲出。");
				Thread.sleep(fukidasiHyojiTime);
				isVisible = false;
				stop();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	/** 吹き出しスレッドを停止 */
	public void stop() {
		CameLog.setLog(TAG, "吹き出しスレッドを停止");
		th = null;
	}

	public String getMsg(View view, int eventCode) {

		Resources res = view.getResources();

		switch (eventCode) {

		/** その日最初のアプリ起動時 */
		case 1:
			return res.getString(R.string.pet_message_welcome);

			/** お腹いっぱいになったとき */
		case 2:
			return res.getString(R.string.pet_message_satiety);

			/** 降ってくるエサの残数が0になったとき */
		case 3:
			return res.getString(R.string.pet_message_esa_zero);

			/** レベルアップした時 */
		case 4:
			return res.getString(R.string.pet_message_levelup);

			/** SNS投稿で成長日記を投稿してくれた時 */
		case 5:
			return res.getString(R.string.pet_message_thanksSNS);

		default:
			/** いずれにも当てはまらない時 */
			return res.getString(R.string.pet_message_generic);
		}
	}

}
