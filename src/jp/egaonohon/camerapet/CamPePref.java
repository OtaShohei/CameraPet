package jp.egaonohon.camerapet;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class CamPePref {
	
	/** Logのタグを定数で確保 */
	private static final String TAG = "CamPePref";
	
	/**Gameでの累計エサ獲得成功回数と累計経験値をプリファレンスに保存する */
	public static void saveTotalExpAndEsaGetCnt(Context context, int totalEsaGetCnt, int totalExp) {
		
		/** プリファレンスに書き込むためのEditorオブジェクト取得 */
		Editor editor =getExpSaveEditor(context);

		/** "EsaGetCnt" というキーで累計エサ獲得成功回数を登録*/
		editor.putInt("EsaGetCnt", totalEsaGetCnt);

		/** "eXP" というキーで累計経験値を登録*/
		editor.putInt("eXP", totalExp);
		
		CameLog.setLog(TAG, "累計エサ獲得成功回数"+ totalEsaGetCnt +"回と累計経験値" + totalExp +"をプリファレンスにも保存");
		
		/** 書き込みの確定（実際にファイルに書き込む）*/
		editor.commit();
	}
	
	/** 累計経験値をプリファレンスに保存する */
	public static void saveTotalExp(Context context, int totalExp) {
		
		/** プリファレンスに書き込むためのEditorオブジェクト取得 */
		Editor editor =getExpSaveEditor(context);

		/** "eXP" というキーで累計経験値を登録*/
		editor.putInt("eXP", totalExp);
		
		CameLog.setLog(TAG, "累計経験値" + totalExp +"をプリファレンスに保存");
		
		/** 書き込みの確定（実際にファイルに書き込む）*/
		editor.commit();
	}
	/** プリファレンスから累計エサ獲得成功回数を取り出す。登録されていなければ -1 を返す */
	public static int loadTotalEsaGetCnt(Context context) {
		/** プリファレンスの準備 */
		SharedPreferences pref = context.getSharedPreferences("expEry",
				Context.MODE_PRIVATE);

		/** "EsaGetCnt" というキーで保存されている値を読み出す */
		int gettedTotalEsaGetCnt =pref.getInt("EsaGetCnt", -1);
		
		CameLog.setLog(TAG, "累計撮影回数"+ gettedTotalEsaGetCnt +"をプリファレンスから取得");
		
		/** 取得した累計エサ獲得成功回数を戻す */
		return gettedTotalEsaGetCnt;
	}
	
	/** プリファレンスから累計経験値を取り出す。登録されていなければ -1 を返す */
	public static int loadTotalExp(Context context) {
		/** プリファレンスの準備 */
		SharedPreferences pref = context.getSharedPreferences("expEry",
				Context.MODE_PRIVATE);
		
		/** "eXP" というキーで保存されている値を読み出す */
		int gettedtotalEXP =pref.getInt("eXP", -1);
		
		CameLog.setLog(TAG, "累計撮影回数"+ gettedtotalEXP +"をプリファレンスから取得");
		
		/** 取得し累計経験値を戻す */
		return gettedtotalEXP;
	}
	
	/** 直近撮影回数と累計撮影回数をプリファレンスにも保存する */
	public static void saveNowAndTotalShotCnt(Context context, int nowShotCnt, int totalShotCnt) {
		
		/** プリファレンスに書き込むためのEditorオブジェクト取得 */
		Editor editor =getShotCntSaveEditor(context);

		// "nowShotCnt" というキーで回数を登録
		editor.putInt("nowShotCnt", nowShotCnt);

		// "totalShotCnt" というキーで回数を登録
		editor.putInt("totalShotCnt", totalShotCnt);
		
		CameLog.setLog(TAG, "直近撮影回数"+ nowShotCnt +"回と累計撮影回数" + totalShotCnt +"をプリファレンスにも保存");
		
		// 書き込みの確定（実際にファイルに書き込む）
		editor.commit();
	}

	/** 【累計】撮影回数のみをプリファレンスに保存する */
	public static void saveTotalShotCnt(Context context, int totalShotCnt) {
		
		/** プリファレンスに書き込むためのEditorオブジェクト取得 */
		Editor editor =getShotCntSaveEditor(context);

		// "nowShotCnt" というキーで回数を登録
		editor.putInt("totalShotCnt", totalShotCnt);
		
		CameLog.setLog(TAG, "直近撮影回数"+ totalShotCnt +"回をプリファレンスにも保存");
		
		// 書き込みの確定（実際にファイルに書き込む）
		editor.commit();
	}
	
	/** 【直近】撮影回数のみをプリファレンスに保存する */
	public static void saveNowShotCnt(Context context, int nowShotCnt) {
		
		/** プリファレンスに書き込むためのEditorオブジェクト取得 */
		Editor editor =getShotCntSaveEditor(context);

		// "nowShotCnt" というキーで回数を登録
		editor.putInt("nowShotCnt", nowShotCnt);
		
		CameLog.setLog(TAG, "直近撮影回数"+ nowShotCnt +"回をプリファレンスにも保存");
		
		// 書き込みの確定（実際にファイルに書き込む）
		editor.commit();
	}
	
	/** プリファレンスから直近撮影回数を取り出す。登録されていなければ -1 を返す */
	public static int loadNowShotCnt(Context context) {
		/** プリファレンスの準備 */
		SharedPreferences pref = context.getSharedPreferences("ShotCnt",
				Context.MODE_PRIVATE);
		
		int gettedNowShotCnt =pref.getInt("nowShotCnt", -1);
		
		CameLog.setLog(TAG, "直近撮影回数"+ gettedNowShotCnt +"をプリファレンスから取得");
		
		/** "NowShotCnt" というキーで保存されている値を読み出す */
		return gettedNowShotCnt;
	}
	
	/** プリファレンスから累計撮影回数を取り出す。登録されていなければ -1 を返す */
	public static int loadTotalShotCnt(Context context) {
		/** プリファレンスの準備 */
		SharedPreferences pref = context.getSharedPreferences("ShotCnt",
				Context.MODE_PRIVATE);

		int gettedtotalShotCnt =pref.getInt("totalShotCnt", -1);
		
		CameLog.setLog(TAG, "累計撮影回数"+ gettedtotalShotCnt +"をプリファレンスから取得");
		
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
