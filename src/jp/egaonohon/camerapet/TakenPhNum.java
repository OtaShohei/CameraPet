package jp.egaonohon.camerapet;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

/**
 * 直近撮影済み写真枚数を保存&読み出すクラス。
 * @author OtaShohei
 *
 */
public class TakenPhNum {

	/**
	 * フィールド
	 */
	/** 撮影回数保存用Preferences */
	private static SharedPreferences pref;
	/** Preferencesへの書き込み用Editor */
	private static Editor editor;
	/** Logのタグを定数で確保 */
	private static final String TAG = "TakenPhNum";


	/**
	 * GetPh()で使用するCursorNum（直近撮影済み枚数）を取得するメソッド。
	 *
	 * @param context
	 * @param intShotCnt
	 */
	public static void save(Context context, Integer intShotCnt) {
		/**
		 * 撮影回数保存用Preferencesのインスタンス生成
		 */
		pref = context.getSharedPreferences("GetPhNum", Context.MODE_PRIVATE);
		/**
		 * プリファレンスに書き込むためのEditorオブジェクト取得
		 */
		editor = pref.edit();

		/**
		 * "shotCnt" というキーで撮影回数を登録
		 */
		editor.putInt("GetPhNum", intShotCnt);
		/**
		 * 書き込みの確定（実際にファイルに書き込む）
		 */
		editor.commit();
		CameLog.setLog(TAG, "CursorNum" + intShotCnt+"枚");

	}

	/** プリファレンスから「撮影回数」を取り出す。登録されていなければ 0 を返す */
	public static int load(Context context) {
		/** 撮影回数を取り出しておく */
		int beforeShotCnt = pref.getInt("GetPhNum", 0);

		/** 取り出し終えたので撮影回数を0にリセットする。 */
		editor.putInt("GetPhNum", 0);
		editor.commit();

		/** 取り出しておいた撮影回数を戻り値として戻す */
		return beforeShotCnt;
	}

}
