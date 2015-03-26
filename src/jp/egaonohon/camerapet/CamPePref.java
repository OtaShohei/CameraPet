package jp.egaonohon.camerapet;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class CamPePref {
	
	/** Logのタグを定数で確保 */
	private static final String TAG = "CamPePref";
	
	/** 直近撮影回数と累計撮影回数をプリファレンスにも保存する */
	public static void saveNowAndTotalShotCnt(Context context, int nowShotCnt, int totalShotCnt) {
		// プリファレンスの準備 //
		SharedPreferences pref = context.getSharedPreferences("ShotCnt",
				Context.MODE_PRIVATE);

		// プリファレンスに書き込むためのEditorオブジェクト取得 //
		Editor editor = pref.edit();

		// "nowShotCnt" というキーで回数を登録
		editor.putInt("nowShotCnt", nowShotCnt);

		// "nowShotCnt" というキーで回数を登録
		editor.putInt("totalShotCnt", totalShotCnt);
		
		CameLog.setLog(TAG, "直近撮影回数"+ nowShotCnt +"回と累計撮影回数" + totalShotCnt +"をプリファレンスにも保存");
		
		// 書き込みの確定（実際にファイルに書き込む）
		editor.commit();
	}

	/** 累計撮影回数のみをプリファレンスに保存する */
	public static void saveTotalShotCnt(Context context, int totalShotCnt) {
		// プリファレンスの準備 //
		SharedPreferences pref = context.getSharedPreferences("ShotCnt",
				Context.MODE_PRIVATE);

		// プリファレンスに書き込むためのEditorオブジェクト取得 //
		Editor editor = pref.edit();

		// "nowShotCnt" というキーで回数を登録
		editor.putInt("totalShotCnt", totalShotCnt);
		
		CameLog.setLog(TAG, "直近撮影回数"+ totalShotCnt +"回をプリファレンスにも保存");
		
		// 書き込みの確定（実際にファイルに書き込む）
		editor.commit();
	}
	
	/** 直近撮影回数のみをプリファレンスに保存する */
	public static void saveNowShotCnt(Context context, int nowShotCnt) {
		// プリファレンスの準備 //
		SharedPreferences pref = context.getSharedPreferences("ShotCnt",
				Context.MODE_PRIVATE);

		// プリファレンスに書き込むためのEditorオブジェクト取得 //
		Editor editor = pref.edit();

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
}
