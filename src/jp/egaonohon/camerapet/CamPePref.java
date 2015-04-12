package jp.egaonohon.camerapet;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

/**
 * アプリで扱うプリファレンスへの書き込み読み込みすべてを司るクラス。
 *
 * @author 1107AND
 *
 */
public class CamPePref {

	/** Logのタグを定数で確保 */
	private static final String TAG = "CamPePref";

	/** 初回起動か否かを確認するステイタス情報をプリファレンスに保存する */
	public static void saveStartStatus(Context context) {
		/** プリファレンスの準備 */
		SharedPreferences pref = context.getSharedPreferences("startStatus",
				Context.MODE_PRIVATE);

		/** プリファレンスに書き込むためのEditorオブジェクト取得 */
		Editor editor = pref.edit();

		/** "startStatus" というキーでnotFirstを登録 */
		editor.putString("startStatus", "notFirst");

		/** 書き込みの確定（実際にファイルに書き込む） */
		editor.commit();
		CameLog.setLog(TAG, "saveStartStatusにて" + "startStatus" + "キーで"
				+ "notFirst" + "を登録");
	}

	/** 現在起動中である旨プリファレンスに保存する */
	public static void saveOperationStatus(Context context) {
		/** プリファレンスの準備 */
		SharedPreferences pref = context.getSharedPreferences("WorkStatus",
				Context.MODE_PRIVATE);

		/** プリファレンスに書き込むためのEditorオブジェクト取得 */
		Editor editor = pref.edit();

		/** "startStatus" というキーでnotFirstを登録 */
		editor.putString("WorkStatus", "operation");

		/** 書き込みの確定（実際にファイルに書き込む） */
		editor.commit();
		CameLog.setLog(TAG, "saveWorkStatusにて" + "WorkStatus" + "キーで"
				+ "operation" + "を登録");
	}

	/** 現在起動していない旨、Activityが破棄される直前にプリファレンスに保存する */
	public static void saveNotWorkStatus(Context context) {
		/** プリファレンスの準備 */
		SharedPreferences pref = context.getSharedPreferences("WorkStatus",
				Context.MODE_PRIVATE);

		/** プリファレンスに書き込むためのEditorオブジェクト取得 */
		Editor editor = pref.edit();

		/** "startStatus" というキーでnotFirstを登録 */
		editor.putString("WorkStatus", "notWork");

		/** 書き込みの確定（実際にファイルに書き込む） */
		editor.commit();
		CameLog.setLog(TAG, "saveNotWorkStatusにて" + "WorkStatus" + "キーで"
				+ "notWork" + "を登録");
	}

	/** 現在起動しているか否かを確認するステイタス情報をプリファレンスから取り出す。登録されていなければnotFoundを返す */
	public static String loadWorkStatus(Context context) {

		/** プリファレンスの準備 */
		SharedPreferences pref = context.getSharedPreferences("WorkStatus",
				Context.MODE_PRIVATE);

		String workStatus = pref.getString("WorkStatus", "notFound");

		CameLog.setLog(TAG, "在起動しているか否かを確認するステイタス情報として" + workStatus + "を取得");

		/** "startStatus" というキーで保存されている値を読み出す */
		return workStatus;
	}

	/** Notificationを掲出した時間をプリファレンスに保存する */
	public static void saveNotificationTime(Context context) {

		/** Notificationを掲出した時間 */
		long notificationSubmitTime = System.currentTimeMillis();

		/** プリファレンスの準備 */
		SharedPreferences pref = context.getSharedPreferences(
				"notificationSubmitTime", Context.MODE_PRIVATE);

		/** プリファレンスに書き込むためのEditorオブジェクト取得 */
		Editor editor = pref.edit();

		/** "notificationSubmitTime" というキーでnotificationSubmitTimeを登録 */
		editor.putLong("notificationSubmitTime", notificationSubmitTime);

		CameLog.setLog(TAG, "Notificationを掲出した時間" + notificationSubmitTime + "を登録しました");

		/** 書き込みの確定（実際にファイルに書き込む） */
		editor.commit();
	}

	/** Notificationを前回掲出した時間を取り出す。登録されていなければ -1 を返す */
	public static long loadNotificationTime(Context context) {

		/** プリファレンスの準備 */
		SharedPreferences pref = context.getSharedPreferences(
				"notificationSubmitTime", Context.MODE_PRIVATE);

		/** "threeHoursEatCnt" というキーで保存されている値を読み出す */
		long notificationSubmitTime = pref.getLong("notificationSubmitTime",
				(long) -1);

		CameLog.setLog(TAG, "Notificationを前回掲出した時間" + notificationSubmitTime
				+ "をプリファレンスから取得");

		/** 3時間以内に食べたエサの数を戻す */
		return notificationSubmitTime;
	}

	/** 初回起動か否かを確認するステイタス情報をプリファレンスから取り出す。登録されていなければ空の文字列を返す */
	public static String loadStartStatus(Context context) {

		/** プリファレンスの準備 */
		SharedPreferences pref = context.getSharedPreferences("startStatus",
				Context.MODE_PRIVATE);

		String startStatus = pref.getString("startStatus", "");

		CameLog.setLog(TAG, "初回起動か否かのステイタス" + startStatus + "を取得");

		/** "startStatus" というキーで保存されている値を読み出す */
		return startStatus;
	}

	/** Viewの幅をプリファレンスに保存する */
	public static void saveViewWidth(Context context, int viewWidth) {
		/** プリファレンスの準備 */
		SharedPreferences pref = context.getSharedPreferences("viewWidth",
				Context.MODE_PRIVATE);

		/** プリファレンスに書き込むためのEditorオブジェクト取得 */
		Editor editor = pref.edit();

		/** "NotificationID" というキーでNotificationIdNumを登録 */
		editor.putInt("viewWidth", viewWidth);

		/** 書き込みの確定（実際にファイルに書き込む） */
		editor.commit();
		CameLog.setLog(TAG, "viewWidthへ" + "viewWidth" + "キーで" + viewWidth
				+ "を登録");
	}

	/** Viewの幅をプリファレンスから取り出す。登録されていなければ空の文字列を返す */
	public static int loadViewWidth(Context context) {
		/** プリファレンスの準備 */
		SharedPreferences pref = null;
		pref = context.getSharedPreferences("viewWidth", Context.MODE_PRIVATE);

		int viewWidth = pref.getInt("viewWidth", 0);

		CameLog.setLog(TAG, "viewWidth" + viewWidth + "を取得");

		/** "PetSpeciesName" というキーで保存されている値を読み出す */
		return viewWidth;
	}

	/** ペット種別名をプリファレンスに保存する */
	public static void savePetSpeciesName(Context context, String PetSpeciesName) {
		/** プリファレンスの準備 */
		SharedPreferences pref = context.getSharedPreferences("PetSpeciesName",
				Context.MODE_PRIVATE);

		/** プリファレンスに書き込むためのEditorオブジェクト取得 */
		Editor editor = pref.edit();

		/** "PetSpeciesName" というキーでnotFirstを登録 */
		editor.putString("PetSpeciesName", PetSpeciesName);

		/** 書き込みの確定（実際にファイルに書き込む） */
		editor.commit();
		CameLog.setLog(TAG, "PetSpeciesNameにて" + "PetSpeciesName" + "キーで"
				+ PetSpeciesName + "を登録");
	}

	/** ペット種別名をプリファレンスから取り出す。登録されていなければ空の文字列を返す */
	public static String loadPetSpeciesName(Context context) {
		/** プリファレンスの準備 */
		SharedPreferences pref = context.getSharedPreferences("PetSpeciesName",
				Context.MODE_PRIVATE);

		String PetSpeciesName = pref.getString("PetSpeciesName", "");

		CameLog.setLog(TAG, "ペット種別名" + PetSpeciesName + "を取得");

		/** "PetSpeciesName" というキーで保存されている値を読み出す */
		return PetSpeciesName;
	}

	/**
	 * ペットがレベルアップする直前、現在のペットのステイタスnowをgettedに書き換える。次に、
	 * レベルアップ後のペットをnowとペットステイタス情報を管理するプリファレンスに保存する
	 */
	public static void savePetModelNumber(Context context,
			String beforePetModelNumber, String updatedPetModelNumber) {

		/** プリファレンスの準備 */
		SharedPreferences pref = context.getSharedPreferences("petStatus",
				Context.MODE_PRIVATE);

		/** プリファレンスに書き込むためのEditorオブジェクト取得 */
		Editor editor = pref.edit();

		if (!(updatedPetModelNumber.equals("Pet001A"))) {
			/** "petSpecies" というキーで現在そのペットであることを示すnowを登録 */
			editor.putString(beforePetModelNumber, "getted");
		}

		if (beforePetModelNumber.equals("Pet001A")) {
			editor.putString(beforePetModelNumber, "getted");
		}
		editor.putString(updatedPetModelNumber, "now");

		/** 書き込みの確定（実際にファイルに書き込む） */
		editor.commit();
		CameLog.setLog(TAG, "savePetStatusにて、" + beforePetModelNumber + "キーで"
				+ "gettedを、" + updatedPetModelNumber + "キーで" + "nowを" + "登録");
	}

	/** あるペットのステイタス情報をプリファレンスから取り出す。登録されていなければ空の文字列を返す */
	public static String loadPetModelNumber(Context context,
			String petModelNumber) {
		/** プリファレンスの準備 */
		SharedPreferences pref = context.getSharedPreferences("petStatus",
				Context.MODE_PRIVATE);

		String petStatus = pref.getString(petModelNumber, "");

		CameLog.setLog(TAG, "ペット" + petModelNumber + "のステイタス" + petStatus
				+ "を取得");

		/** "startStatus" というキーで保存されている値を読み出す */
		return petStatus;
	}

	/**
	 * 写真撮影やSNS投稿によるペットのバージョンアップに備えて、 写真撮影やSNS投稿直前のペットModelNumberをプリファレンスに保存する
	 */
	public static void savePetModelNumberForUnexpectedVersionUp(
			Context context, String beforePetModelNumber) {

		/** プリファレンスの準備 */
		SharedPreferences pref = context.getSharedPreferences(
				"PetModelNumberForUnexpectedVersionUp", Context.MODE_PRIVATE);

		/** プリファレンスに書き込むためのEditorオブジェクト取得 */
		Editor editor = pref.edit();

		/** "petSpecies" というキーで現在そのペットであることを示すnowを登録 */
		editor.putString("PetModelNumberForUnexpectedVersionUp",
				beforePetModelNumber);

		/** 書き込みの確定（実際にファイルに書き込む） */
		editor.commit();
		CameLog.setLog(
				TAG,
				"savePetModelNumberForUnexpectedVersionUpにて、PetModelNumberForUnexpectedVersionUpキーで"
						+ beforePetModelNumber + "を登録");
	}

	/** 写真撮影やSNS投稿直前のペットModelNumberをプリファレンスから取り出す。 */
	public static String loadPetModelNumberForUnexpectedVersionUp(
			Context context) {
		/** プリファレンスの準備 */
		SharedPreferences pref = context.getSharedPreferences(
				"PetModelNumberForUnexpectedVersionUp", Context.MODE_PRIVATE);

		String beforePetModelNumber = pref.getString(
				"PetModelNumberForUnexpectedVersionUp", "");

		CameLog.setLog(
				TAG,
				"loadPetModelNumberForUnexpectedVersionUpにて、PetModelNumberForUnexpectedVersionUpキーで"
						+ beforePetModelNumber + "を取得");

		/** "startStatus" というキーで保存されている値を読み出す */
		return beforePetModelNumber;
	}

	/** Gameでの累計エサ獲得成功回数と累計経験値をプリファレンスに保存する */
	public static void saveTotalExpAndEsaGetCnt(Context context,
			int totalEsaGetCnt, int totalExp) {

		/** プリファレンスに書き込むためのEditorオブジェクト取得 */
		Editor editor = getExpSaveEditor(context);

		/** "EsaGetCnt" というキーで累計エサ獲得成功回数を登録 */
		editor.putInt("EsaGetCnt", totalEsaGetCnt);

		/** "eXP" というキーで累計経験値を登録 */
		editor.putInt("eXP", totalExp);

		CameLog.setLog(TAG, "累計エサ獲得成功回数" + totalEsaGetCnt + "回と累計経験値"
				+ totalExp + "をプリファレンスにも保存");

		/** 書き込みの確定（実際にファイルに書き込む） */
		editor.commit();
	}
	
//	/** ツイッター用の写真を削除していいかどうかをプリファレンスに保存する */
//	public static void saveTwitterPhDeleteOK(Context context, Boolean twitterPhDeleteOk) {
//
//		/** プリファレンスの準備 */
//		SharedPreferences pref = context.getSharedPreferences(
//				"twitterPhDeleteOK", Context.MODE_PRIVATE);
//
//		/** プリファレンスに書き込むためのEditorオブジェクト取得 */
//		Editor editor = pref.edit();
//
//		/** "twitterPhDeleteOK" というキーでtwitterPhDeleteOkを登録 */
//		editor.putBoolean("twitterPhDeleteOK", twitterPhDeleteOk);
//
//		/** 書き込みの確定（実際にファイルに書き込む） */
//		editor.commit();
//	}
//
//	/** ツイッター用の写真を削除していいかどうかをプリファレンスから取り出す */
//	public static boolean loadTwitterPhDeleteOK(Context context) {
//
//		CameLog.setLog(TAG, "ツイッター用の写真を削除していいかどうかで用いているcontextの状態は" + context);
//		
//		/** プリファレンスの準備 */
//		SharedPreferences pref = context.getSharedPreferences(
//				"twitterPhDeleteOK", Context.MODE_PRIVATE);
//
//		/** "twitterPhDeleteOK" というキーで保存されている値を読み出す */
//		boolean twitterPhDeleteOK = pref.getBoolean("twitterPhDeleteOK", false);
//
//		CameLog.setLog(TAG, "ツイッター用の写真を削除していいかどうか" + twitterPhDeleteOK
//				+ "をプリファレンスから取得");
//
//		/** 3時間以内に食べたエサの数をを戻す */
//		return twitterPhDeleteOK;
//	}

	/** 3時間以内に食べたエサの数を現在時間とともにプリファレンスに保存する */
	public static void save3hoursEatCnt(Context context, int nowEatCnt) {

		/** エサを食べた時間 */
		long esaGetTime = System.currentTimeMillis();

		/** プリファレンスの準備 */
		SharedPreferences pref = context.getSharedPreferences(
				"threeHoursEatCnt", Context.MODE_PRIVATE);

		/** プリファレンスに書き込むためのEditorオブジェクト取得 */
		Editor editor = pref.edit();

		/** "threeHoursEatCnt" というキーでnowEatCntを登録 */
		editor.putInt("threeHoursEatCnt", nowEatCnt);

		/** "esaEatTime" というキーでesaGetTimeを登録 */
		editor.putLong("esaEatTime", esaGetTime);

		/** 書き込みの確定（実際にファイルに書き込む） */
		editor.commit();
	}

	/** プリファレンスから3時間以内に食べたエサの数を取り出す。登録されていなければ -1 を返す */
	public static int load3hoursEatCnt(Context context) {

		/** 現在時間 */
		long nowTime = System.currentTimeMillis();

		/** エサを食べた時間 */
		long esaLastGetTime;

		/** プリファレンスの準備 */
		SharedPreferences pref = context.getSharedPreferences(
				"threeHoursEatCnt", Context.MODE_PRIVATE);

		/** "threeHoursEatCnt" というキーで保存されている値を読み出す */
		int gettedThreeHoursEatCnt = pref.getInt("threeHoursEatCnt", -1);

		/** "threeHoursEatCnt" というキーで保存されている値を読み出す。からの時は-1を戻す */
		esaLastGetTime = pref.getLong("esaEatTime", (long) -1.0f);

		/** 前回の食事時間から3時間を経過しているなら0を代入 */
		if (nowTime > (esaLastGetTime + 10800000L)) {
			gettedThreeHoursEatCnt = 0;
		}

		CameLog.setLog(TAG, "3時間以内に食べたエサの数" + gettedThreeHoursEatCnt
				+ "をプリファレンスから取得");

		/** 3時間以内に食べたエサの数をを戻す */
		return gettedThreeHoursEatCnt;
	}

	/** 累計経験値をプリファレンスに保存する */
	public static void saveTotalExp(Context context, int totalExp) {

		/** プリファレンスに書き込むためのEditorオブジェクト取得 */
		Editor editor = getExpSaveEditor(context);

		/** "eXP" というキーで累計経験値を登録 */
		editor.putInt("eXP", totalExp);

		CameLog.setLog(TAG, "累計経験値" + totalExp + "をプリファレンスに保存");

		/** 書き込みの確定（実際にファイルに書き込む） */
		editor.commit();
	}

	/** プリファレンスから累計エサ獲得成功回数を取り出す。登録されていなければ -1 を返す */
	public static int loadTotalEsaGetCnt(Context context) {
		/** プリファレンスの準備 */
		SharedPreferences pref = context.getSharedPreferences("expEry",
				Context.MODE_PRIVATE);

		/** "EsaGetCnt" というキーで保存されている値を読み出す */
		int gettedTotalEsaGetCnt = pref.getInt("EsaGetCnt", -1);

		// CameLog.setLog(TAG, "累計撮影回数" + gettedTotalEsaGetCnt +
		// "をプリファレンスから取得");

		/** 取得した累計エサ獲得成功回数を戻す */
		return gettedTotalEsaGetCnt;
	}

	/** プリファレンスから累計経験値を取り出す。登録されていなければ -1 を返す */
	public static int loadTotalExp(Context context) {
		/** プリファレンスの準備 */
		SharedPreferences pref = context.getSharedPreferences("expEry",
				Context.MODE_PRIVATE);

		/** "eXP" というキーで保存されている値を読み出す */
		int gettedtotalEXP = pref.getInt("eXP", -1);

		// CameLog.setLog(TAG, "累計撮影回数" + gettedtotalEXP + "をプリファレンスから取得");

		/** 取得し累計経験値を戻す */
		return gettedtotalEXP;
	}

	/** 直近撮影回数と累計撮影回数をプリファレンスにも保存する */
	public static void saveNowAndTotalShotCnt(Context context, int nowShotCnt,
			int totalShotCnt) {

		/** プリファレンスに書き込むためのEditorオブジェクト取得 */
		Editor editor = getShotCntSaveEditor(context);

		// "nowShotCnt" というキーで回数を登録
		editor.putInt("nowShotCnt", nowShotCnt);

		// "totalShotCnt" というキーで回数を登録
		editor.putInt("totalShotCnt", totalShotCnt);

		CameLog.setLog(TAG, "直近撮影回数" + nowShotCnt + "回と累計撮影回数" + totalShotCnt
				+ "をプリファレンスにも保存");

		// 書き込みの確定（実際にファイルに書き込む）
		editor.commit();
	}

	/** 【累計】撮影回数のみをプリファレンスに保存する */
	public static void saveTotalShotCnt(Context context, int totalShotCnt) {

		/** プリファレンスに書き込むためのEditorオブジェクト取得 */
		Editor editor = getShotCntSaveEditor(context);

		// "nowShotCnt" というキーで回数を登録
		editor.putInt("totalShotCnt", totalShotCnt);

		CameLog.setLog(TAG, "直近撮影回数" + totalShotCnt + "回をプリファレンスにも保存");

		// 書き込みの確定（実際にファイルに書き込む）
		editor.commit();
	}

	/** 【直近】撮影回数のみをプリファレンスに保存する */
	public static void saveNowShotCnt(Context context, int nowShotCnt) {

		/** プリファレンスに書き込むためのEditorオブジェクト取得 */
		Editor editor = getShotCntSaveEditor(context);

		// "nowShotCnt" というキーで回数を登録
		editor.putInt("nowShotCnt", nowShotCnt);

		CameLog.setLog(TAG, "直近撮影回数" + nowShotCnt + "回をプリファレンスにも保存");

		// 書き込みの確定（実際にファイルに書き込む）
		editor.commit();
	}

	/** プリファレンスから直近撮影回数を取り出す。登録されていなければ -1 を返す */
	public static int loadNowShotCnt(Context context) {
		/** プリファレンスの準備 */
		SharedPreferences pref = context.getSharedPreferences("ShotCnt",
				Context.MODE_PRIVATE);

		int gettedNowShotCnt = pref.getInt("nowShotCnt", -1);

		CameLog.setLog(TAG, "直近撮影回数" + gettedNowShotCnt + "をプリファレンスから取得");

		/** "NowShotCnt" というキーで保存されている値を読み出す */
		return gettedNowShotCnt;
	}

	/** プリファレンスから累計撮影回数を取り出す。登録されていなければ -1 を返す */
	public static int loadTotalShotCnt(Context context) {
		/** プリファレンスの準備 */
		SharedPreferences pref = context.getSharedPreferences("ShotCnt",
				Context.MODE_PRIVATE);

		int gettedtotalShotCnt = pref.getInt("totalShotCnt", -1);

		CameLog.setLog(TAG, "累計撮影回数" + gettedtotalShotCnt + "をプリファレンスから取得");

		/** "TotalShotCnt" というキーで保存されている値を読み出す */
		return gettedtotalShotCnt;
	}

	/** プリファレンスに撮影回数を保存するためのEditorオブジェクトを取得するメソッド */
	public static Editor getShotCntSaveEditor(Context context) {
		/** プリファレンスの準備 */
		SharedPreferences pref = context.getSharedPreferences("ShotCnt",
				Context.MODE_PRIVATE);

		/** プリファレンスに書き込むためのEditorオブジェクト取得 */
		return pref.edit();
	}

	/** プリファレンスにエサ獲得成功回数やSNSポイント、経験値を保存するためのEditorオブジェクトを取得するメソッド */
	public static Editor getExpSaveEditor(Context context) {
		/** プリファレンスの準備 */
		SharedPreferences pref = context.getSharedPreferences("expEry",
				Context.MODE_PRIVATE);

		/** プリファレンスに書き込むためのEditorオブジェクト取得 */
		return pref.edit();
	}
}
