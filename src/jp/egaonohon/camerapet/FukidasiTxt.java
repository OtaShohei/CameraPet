package jp.egaonohon.camerapet;

import java.util.Random;

import android.content.res.Resources;
import android.view.View;

/**
 * ペットがゲーム中につぶやくセリフ文字を生成するクラス。 ゆくゆくは、WebAPI化してそちらから取得するクラスにしたい。
 * 
 * @author OtaShohei
 *
 */
public class FukidasiTxt {

	/** Logのタグを定数で確保 */
	private static final String TAG = "FukidasiTxt";

	/** イベント発生時の吹き出し文字 */
	static int[] eventMsg = { R.string.pet_message_welcome,
			R.string.pet_message_satiety, R.string.pet_message_esa_zero,
			R.string.pet_message_levelup, R.string.pet_message_thanksSNS };

	/** 雑談時の吹き出し文字 */
	static int[] chatMsg = { R.string.pet_message_generic001,
			R.string.pet_message_generic002, R.string.pet_message_generic003,
			R.string.pet_message_generic004, R.string.pet_message_generic005,
			R.string.pet_message_generic006, R.string.pet_message_generic007,
			R.string.pet_message_generic008, R.string.pet_message_generic009,
			R.string.pet_message_generic010 };

	/** 吹き出し発行時のイベント:ペットが触られた時。 */
	static int[] touchedMsg = { R.string.pet_message_touch001,
			R.string.pet_message_touch002, R.string.pet_message_touch003,
			R.string.pet_message_touch004, R.string.pet_message_touch005,
			R.string.pet_message_touch006, R.string.pet_message_touch007,
			R.string.pet_message_touch008, R.string.pet_message_touch009,
			R.string.pet_message_touch010, R.string.pet_message_touch011,
			R.string.pet_message_touch012 };

	/**
	 * ペットがゲーム中につぶやくセリフを生成するメソッド。
	 * 
	 * @param view
	 * @param eventCode
	 * @return
	 */
	public static String make(View view, int eventCode) {
		Resources res = view.getResources();

		CameLog.setLog(TAG, "飼い主歓迎メッセージを呼び出し。イベントコードは" + eventCode);

		String msg = "";
		/** 雑談時は乱数を発生してそれによりセリフを選択 */
		if (eventCode == 6) {
			int selectGenericMessageNum = new Random().nextInt(chatMsg.length);
			msg = res.getString(chatMsg[selectGenericMessageNum]);

			/** ペットが触られた時は乱数を発生してそれによりセリフを選択 */
		} else if (eventCode == 7) {
			int selectGenericMessageNum = new Random()
					.nextInt(touchedMsg.length);
			msg = res.getString(touchedMsg[selectGenericMessageNum]);

			/** イベント時は、イベントコードに応じて正規のセリフを選択 */
		} else {
			msg = res.getString(eventMsg[eventCode - 1]);
			CameLog.setLog(TAG, "飼い主歓迎メッセージを取得。内容は:" + msg);
		}
		return msg;
	}
}
