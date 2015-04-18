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
			R.string.pet_message_levelup, R.string.pet_message_thanksSNS,
			R.string.pet_message_thanksEncyc,
			R.string.pet_message_thanksTutorial,
			R.string.pet_message_thanksCam, };

	/** 4～10時の朝時間帯雑談吹き出し文字 */
	static int[] morningChatMsg = { R.string.pet_message_generic001,
			R.string.pet_message_generic002, R.string.pet_message_generic003,
			R.string.pet_message_generic004, R.string.pet_message_generic005,
			R.string.pet_message_generic006, R.string.pet_message_generic007,
			R.string.pet_message_generic008, R.string.pet_message_generic009,
			R.string.pet_message_generic011, R.string.pet_message_generic012,
			R.string.pet_message_generic013, R.string.pet_message_generic014,
			R.string.pet_message_generic015, R.string.pet_message_generic016,
			R.string.pet_message_generic018, R.string.pet_message_generic019,
			R.string.pet_message_generic021, R.string.pet_message_generic022,
			R.string.pet_message_generic023, R.string.pet_message_generic024,
			R.string.pet_message_generic025, R.string.pet_message_generic026,
			R.string.pet_message_generic027, R.string.pet_message_generic028,
			R.string.pet_message_generic029, R.string.pet_message_generic030,
			R.string.pet_message_generic031, R.string.pet_message_generic032,
			R.string.pet_message_generic033,
			R.string.pet_message_morningtime001,
			R.string.pet_message_morningtime002,
			R.string.pet_message_morningtime003,
			R.string.pet_message_morningtime004,
			R.string.pet_message_morningtime005,
			R.string.pet_message_morningtime006, };

	/** 11～14時の昼時間帯雑談吹き出し文字 */
	static int[] lunchChatMsg = { R.string.pet_message_generic001,
			R.string.pet_message_generic002, R.string.pet_message_generic004,
			R.string.pet_message_generic006, R.string.pet_message_generic007,
			R.string.pet_message_generic008, R.string.pet_message_generic009,
			R.string.pet_message_generic010, R.string.pet_message_generic011,
			R.string.pet_message_generic012, R.string.pet_message_generic013,
			R.string.pet_message_generic014, R.string.pet_message_generic015,
			R.string.pet_message_generic016, R.string.pet_message_generic018,
			R.string.pet_message_generic019, R.string.pet_message_generic020,
			R.string.pet_message_generic021, R.string.pet_message_lunchtime001,
			R.string.pet_message_lunchtime002,
			R.string.pet_message_lunchtime003,
			R.string.pet_message_lunchtime004, };

	/** 15～17時のおやつ時間帯雑談吹き出し文字 */
	static int[] snackChatMsg = { R.string.pet_message_generic001,
			R.string.pet_message_generic002, R.string.pet_message_generic004,
			R.string.pet_message_generic006, R.string.pet_message_generic007,
			R.string.pet_message_generic008, R.string.pet_message_generic009,
			R.string.pet_message_generic010, R.string.pet_message_generic011,
			R.string.pet_message_generic012, R.string.pet_message_generic013,
			R.string.pet_message_generic014, R.string.pet_message_generic015,
			R.string.pet_message_generic016, R.string.pet_message_generic018,
			R.string.pet_message_generic019, R.string.pet_message_generic020,
			R.string.pet_message_generic021, R.string.pet_message_generic022,
			R.string.pet_message_generic023, R.string.pet_message_generic024,
			R.string.pet_message_generic025, R.string.pet_message_generic026,
			R.string.pet_message_generic027, R.string.pet_message_generic028,
			R.string.pet_message_generic029, R.string.pet_message_generic030,
			R.string.pet_message_generic031, R.string.pet_message_generic032,
			R.string.pet_message_generic033, R.string.pet_message_snacktime001,
			R.string.pet_message_snacktime002,
			R.string.pet_message_snacktime003,
			R.string.pet_message_snacktime004, };

	/** 18〜23時の夜時間帯雑談吹き出し文字 */
	static int[] dinnerChatMsg = { R.string.pet_message_generic001,
			R.string.pet_message_generic002, R.string.pet_message_generic004,
			R.string.pet_message_generic006, R.string.pet_message_generic007,
			R.string.pet_message_generic008, R.string.pet_message_generic009,
			R.string.pet_message_generic011, R.string.pet_message_generic012,
			R.string.pet_message_generic013, R.string.pet_message_generic014,
			R.string.pet_message_generic015, R.string.pet_message_generic016,
			R.string.pet_message_generic017, R.string.pet_message_generic018,
			R.string.pet_message_generic019, R.string.pet_message_generic020,
			R.string.pet_message_generic021, R.string.pet_message_generic022,
			R.string.pet_message_generic023, R.string.pet_message_generic024,
			R.string.pet_message_generic025, R.string.pet_message_generic026,
			R.string.pet_message_generic027, R.string.pet_message_generic028,
			R.string.pet_message_generic029, R.string.pet_message_generic030,
			R.string.pet_message_generic031, R.string.pet_message_generic032,
			R.string.pet_message_generic033,
			R.string.pet_message_dinnertime001,
			R.string.pet_message_dinnertime002,
			R.string.pet_message_dinnertime003,
			R.string.pet_message_dinnertime004,
			R.string.pet_message_dinnertime005, };

	/** 0～3時の深夜時間帯雑談吹き出し文字 */
	static int[] bedtimeChatMsg = { R.string.pet_message_generic001,
			R.string.pet_message_generic002, R.string.pet_message_generic003,
			R.string.pet_message_generic004, R.string.pet_message_generic006,
			R.string.pet_message_generic007, R.string.pet_message_generic008,
			R.string.pet_message_generic009, R.string.pet_message_generic011,
			R.string.pet_message_generic012, R.string.pet_message_generic013,
			R.string.pet_message_generic014, R.string.pet_message_generic015,
			R.string.pet_message_generic016, R.string.pet_message_generic017,
			R.string.pet_message_generic018, R.string.pet_message_generic019,
			R.string.pet_message_generic020, R.string.pet_message_generic021,
			R.string.pet_message_generic022, R.string.pet_message_generic023,
			R.string.pet_message_generic024, R.string.pet_message_generic025,
			R.string.pet_message_generic026, R.string.pet_message_generic027,
			R.string.pet_message_generic028, R.string.pet_message_generic029,
			R.string.pet_message_generic030, R.string.pet_message_generic031,
			R.string.pet_message_generic032, R.string.pet_message_generic033,
			R.string.pet_message_midnight001 };

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
		/** 雑談時は乱数を発生してそれによりセリフを選択。時間帯別の振り分けをさらに行う。 */
		if (eventCode == 9) {
			/** 時間判定メソッドを呼び出す */
			String MealTime = MealTimeJudgment.get();

			if (MealTime.equals("Morning")) {
				/** 朝時間帯なら朝用セリフを準備 */
				int selectGenericMessageNum = new Random()
						.nextInt(morningChatMsg.length);
				msg = res.getString(morningChatMsg[selectGenericMessageNum]);
			}

			if (MealTime.equals("Lunch")) {
				/** 昼時間帯なら昼用セリフを準備 */
				int selectGenericMessageNum = new Random()
						.nextInt(lunchChatMsg.length);
				msg = res.getString(lunchChatMsg[selectGenericMessageNum]);
			}
			if (MealTime.equals("Snack")) {
				/** おやつ時間帯ならおやつ時間帯用セリフを準備 */
				int selectGenericMessageNum = new Random()
						.nextInt(snackChatMsg.length);
				msg = res.getString(snackChatMsg[selectGenericMessageNum]);
			}
			if (MealTime.equals("Dinner")) {
				/** 夜時間帯なら晩用セリフを準備 */
				int selectGenericMessageNum = new Random()
						.nextInt(dinnerChatMsg.length);
				msg = res.getString(dinnerChatMsg[selectGenericMessageNum]);
			}
			if (MealTime.equals("bedtimeSnack")) {
				/** 夜食時間帯なら深夜用セリフを準備 */
				int selectGenericMessageNum = new Random()
						.nextInt(bedtimeChatMsg.length);
				msg = res.getString(bedtimeChatMsg[selectGenericMessageNum]);
			}
		} else if (eventCode == 10) {
			/** ペットが触られた時は乱数を発生してそれによりセリフを選択 */
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
