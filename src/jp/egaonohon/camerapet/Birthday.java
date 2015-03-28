package jp.egaonohon.camerapet;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class Birthday {

	/** ペットの誕生日時（=インストール日時） */
	private static long birthday;
	/** ペットの誕生経過ミリ秒 */
	private static long pastMillis;
	/** Logのタグを定数で確保 */
	private static final String TAG = "Birthday";
	/** ペットの年齢 */
	private static String petAgeString;

	public static String getAge(Context context) {
		/** 誕生日時取得 */
		birthday = loadBirthday(context);

		/** 誕生日時取得できない（データが存在しない場合）は現在時間を誕生日時に設定。再取得。 */
		if (birthday == -1) {
			saveBirthday(context);
			birthday = loadBirthday(context);
		}

		/** 現在時間取得 */
		long now = System.currentTimeMillis();

		/** ペットの誕生経過ミリ秒取得 */
		if (now > birthday) {
			pastMillis = now - birthday;
			/** 時間に変換 */
			long petAgeHours = ((pastMillis/1000)/60)/60;
			/** 24時間以下ならそのまま返却 */
			if (petAgeHours <= 24) {
				petAgeString = context.getString(R.string.pet_age) + " " + petAgeHours + context.getString(R.string.age_times);
				return petAgeString;
			/** 1ヶ月（=744)以下なら日数で返却 */
			}else if (petAgeHours <= 744) {
				petAgeString = context.getString(R.string.pet_age) + " " + petAgeHours /24 + context.getString(R.string.age_days);
				return petAgeString;
			}else {
				petAgeString = context.getString(R.string.pet_age) + " " + petAgeHours /744 + context.getString(R.string.age_months);
			}
		}
		return petAgeString;
	}

	/**
	 * 誕生日時を保存する。
	 * @param context
	 */
	public static void saveBirthday(Context context) {

		/** 現在時間取得 */
		birthday = System.currentTimeMillis();

		/** プリファレンスの準備 */
		SharedPreferences pref = context.getSharedPreferences("birthday",
				Context.MODE_PRIVATE);

		/** プリファレンスに書き込むためのEditorオブジェクト取得 */
		Editor editor = pref.edit();

		/** "nowShotCnt" というキーで回数を登録 */
		editor.putLong("birthday", birthday);

		CameLog.setLog(TAG, "誕生時間を" + birthday + "で記録しました！");

		/** 書き込みの確定（実際にファイルに書き込む） */
		editor.commit();
	}

	/**
	 * プリファレンスから誕生日時を取り出す。登録されていなければ -1 を返す
	 * @param context
	 * @return
	 */
	public static long loadBirthday(Context context) {
		/** プリファレンスの準備 */
		SharedPreferences pref = context.getSharedPreferences("birthday",
				Context.MODE_PRIVATE);

		long gettedBirthday =pref.getLong("birthday", -1);

		CameLog.setLog(TAG, "誕生時間"+ gettedBirthday +"をプリファレンスから取得");

		/** "NowShotCnt" というキーで保存されている値を読み出す */
		return gettedBirthday;
	}
}
