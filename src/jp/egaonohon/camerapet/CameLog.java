package jp.egaonohon.camerapet;

import android.util.Log;

/**
 * CameraPetでLogを取得を管理するクラス。 Google Playリリース時には、メソッド内をコメントアウトして脆弱性対策を行うこと。
 *
 * @author 1107AND
 *
 */
public class CameLog {

	/**
	 * CameraPetでLogを取得するためのメソッド。
	 * tagは、メンバ変数として各クラス名を定数で確保してください。
	 * （例）
	 * private static final String TAG = "GetPh";
	 *
	 */
	public static void setLog(String tag, String msg) {
		/**
		 * Logを消すときは次の行をコメントアウトするだけでいい。
		 * クラスがたくさんあるプロジェクトの場合はこのメソッドをクラス化してそれぞれのクラスから呼び出すようにしたほうがすっきりする。
		 */
		Log.v(tag, msg);
	}

}
