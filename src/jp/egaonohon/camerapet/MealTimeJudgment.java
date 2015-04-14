package jp.egaonohon.camerapet;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 現在時刻から、朝食・昼ごはん・おやつ・晩御飯を判定するクラス。
 * @author OtaShohei
 *
 */
public class MealTimeJudgment {
	/** 戻り値「Dinner」などを格納する */
	private static String MealTime;
	/** Logのタグを定数で確保 */
	private static final String TAG = "MealTimeJudgment";

	/**
	 * 現在時刻から、朝食・昼ごはん・おやつ・晩御飯を判定するメソッド。
	 * @return
	 */
	public static String get() {
		/** 現在時間を取得 */
		String nowTime = getNowDate();
//		CameLog.setLog(TAG,"取得した現在時間文字列は" + nowTime);

		/** 現在時間をint型に変換 */
		int nowTimeint = Integer.parseInt(nowTime);
//		CameLog.setLog(TAG,"変換したnowTimeintは" + nowTimeint);

		/** 時間帯判定実行。4～10時ならば朝食 */
		if (nowTimeint >= 4 && nowTimeint <= 10) {
			MealTime = "Morning";
//			CameLog.setLog(TAG,"朝食と判定");
			return MealTime;
			/** 時間帯判定実行。11～14時ならば昼食 */
		} else if (nowTimeint >= 11 && nowTimeint <= 14) {
			MealTime = "Lunch";
//			CameLog.setLog(TAG,"昼食と判定");
			return MealTime;
			/** 時間帯判定実行。15～17時ならばおやつ */
		} else if (nowTimeint >= 15 && nowTimeint <= 17) {
			MealTime = "Snack";
//			CameLog.setLog(TAG,"おやつと判定");
			return MealTime;
			/** 時間帯判定実行。18時以降ならば晩御飯 */
		} else if (nowTimeint >= 18) {
			MealTime = "Dinner";
//			CameLog.setLog(TAG,"晩御飯と判定");
			return MealTime;
			/** 時間帯判定実行。3時以前ならば夜食 */
		} else if (nowTimeint <= 3) {
			MealTime = "bedtimeSnack";
//			CameLog.setLog(TAG,"夜食と判定");
			return MealTime;
		}
		return nowTime;
	}

	/** 現在「時間」のみをStringで取得するメソッド。 */
	public static String getNowDate() {
		final DateFormat df = new SimpleDateFormat("HH");
		final Date date = new Date(System.currentTimeMillis());
		return df.format(date);
	}

}
