package jp.egaonohon.camerapet;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.widget.Toast;

/**
 * このCameraPetアプリの様々なデータを保存取得するためのクラスです。
 * 
 * @author OtaShohei
 *
 */
public class CamPeDb {
	/**
	 * フィールド各種。
	 */
	/** このメンバ変数は、SimpleDatabaseHelperクラスの参照になっている。 */
	private static SimpleDatabaseHelper helper;
	/** ユーザー名 */
	private static String txtUser = "default";
	/** Petの種類 */
	private static String txtPetType = "lv01";
	/** 未保存の直近撮影回数 */
	private static Integer preferenceNewShotCnt = 0;
	/** DB保存済み累積撮影回数 */
	private static Integer preferenceTotalShotCnt = 0;
	/** 新たに保存する累積撮影回数 */
	private static Integer newTotalShotCnt = 0;
	/** CameraPetインストール日時 */
	private static Long birthDay = null;
	/** Logのタグを定数で確保 */
	private static final String TAG = "CamPeDb";

	/**
	 * データベースから累積撮影回数を取得するメソッド。
	 *
	 * @param context
	 * @return
	 */
	public static int getTotalShotCnt(Context context) {
		// 読み込み用のデータベースオブジェクトを取得。SQLの読み込みは結果が戻ってきてそれを処理するので重い作業になる。
		SQLiteDatabase db = helper.getReadableDatabase();// getReadableDatabase()メソッドを使う。getWritableDatabaseもできるがあえて書き込み権限は不要なので今回は使わない。
		// db.queryの第二引数を作る
		// select文のカラムの指定する文字列をString型の配列に記述。ここは3つのカラムすべてということか…。
		String[] cols = { "user", "petType", "nowShotCnt", "totalShotCnt" };
		// Select文の行を特定する（where句）文字列を取得（ISBNの指定）。ISBNを引っ張ってこいよ!て感じ。
		String[] params = { txtUser };

		// 実際にselect文に相当するメソッドを実行。以下、query()の引数。
		// 第一引数：テーブル名
		// 第二引数：項目名（カラム名）の指定（select　【＊】に相当）
		// 第三引数：Where句に相当する
		// 第四引数：Where句の指定データ
		// 第五引数：GropuBY句に相当。何かのカテゴリ単位で引っ張ってくるときに記述。
		// 第六引数：Having句に相当(GroupについてWhere句)
		// 第七引数：Order BY句に相当する
		// 第八引数：LIMIT句に相当する。1000件中の10件だけを…などの指定。
		// 戻り値は、返却されたレコード群を示すCursor（カーソル）が返却される
		Cursor cs = db.query("pet", cols, "user = ?", params, null, null, null,
				null);// DBからの戻り値のCursorをこの後扱っていく。
		// データがあれば、データを取得する。なければ、無い！
		if (cs.moveToFirst()) {
			// データがあれば、それを取得する
			// txtTitle.setText(cs.getString(1));//
			// タイトルを引っ張ってくる。(1)は列を示す。一番左が0から始まる。
			preferenceTotalShotCnt = cs.getInt(3);// 撮影回数を引っ張ってくる
			// String strPrice=cs.getString(2);
			// Toast.makeText(this, strPrice,
			// Toast.LENGTH_SHORT).show();
			// txtPrice.setText(Integer.toString(price));
		} else {
			// データがなかったので、その旨を表示する
			Toast.makeText(context, "データがありません。", Toast.LENGTH_SHORT).show();
		}
		Toast.makeText(context, "これまでの撮影回数は" + preferenceTotalShotCnt + "です",
				Toast.LENGTH_SHORT).show();
		/**
		 * 戻り値としてこれまでの累積撮影回数を渡す。
		 */
		return preferenceTotalShotCnt;
	}

	/**
	 * データベースから直近撮影回数を取得するメソッド。
	 *
	 * @param context
	 * @return
	 */
	public static int getNowShotCnt(Context context) {
		try {
			SQLiteDatabase db = helper.getReadableDatabase();// getReadableDatabase()メソッドを使う。getWritableDatabaseもできるがあえて書き込み権限は不要なので今回は使わない。
			// db.queryの第二引数を作る
			// select文のカラムの指定する文字列をString型の配列に記述。ここは3つのカラムすべてということか…。
			String[] cols = { "user", "petType", "nowShotCnt", "totalShotCnt" };
			// Select文の行を特定する（where句）文字列を取得（ユーザー名の指定）。ユーザーを引っ張ってこいよ!て感じ。
			String[] params = { txtUser };

			// 実際にselect文に相当するメソッドを実行。
			Cursor cs = db.query("pet", cols, "user = ?", params, null, null,
					null, null);// DBからの戻り値のCursorをこの後扱っていく。
			// データがあれば、データを取得する。なければ、無い！
			if (cs.moveToFirst()) {
				// 撮影回数を引っ張ってくる(2)は列を示す。一番左が0から始まる。
				preferenceNewShotCnt = cs.getInt(2);
			} else {
				// // データがなかったので、その旨を表示する
				// Toast.makeText(context, "データがありません。",
				// Toast.LENGTH_SHORT).show();
				CameLog.setLog(TAG, "直近撮影枚数データがありません。");
			}
			// Toast.makeText(context, "先ほどの撮影回数は" + nonSavedNewShotCnt + "です",
			// Toast.LENGTH_SHORT).show();
			CameLog.setLog(TAG, "先ほどの撮影回数は" + preferenceNewShotCnt + "です");
			/**
			 * 戻り値として直近撮影回数を渡す。
			 */
			return preferenceNewShotCnt;
		} catch (Exception e) {
			/**
			 * 戻り値として直近撮影回数を渡す。
			 */
			e.printStackTrace();
			CameLog.setLog(TAG, "直近撮影回数取得時に例外発生。デフォルト値の-1を戻します。");
			return -1;
		}
	}

	/**
	 * 直近写真撮影回数とそれを加えた累計撮影回数をデータベースに保存するメソッド。
	 *
	 * @param cntNum
	 * @return
	 */
	public static String saveNowCount(Context context) {
		/** 未保存の撮影回数。 */
		preferenceNewShotCnt = CamPePref.loadNowShotCnt(context);
		
		/** プリファレンスから累計撮影回数を取得 */
		preferenceTotalShotCnt = CamPePref.loadTotalShotCnt(context);

		/** SimpleDatabaseHelperを取得 */
		helper = new SimpleDatabaseHelper(context);

		/** 書き込み用のデータベースオブジェクトを取得。SQLの読み込みは結果が戻ってきてそれを処理するので重い作業になる。*/
		SQLiteDatabase db = helper.getWritableDatabase();

		// カラム（？　これだと行になるが列では？）名とデータの組わせで1レコードのデータを作成している
		// ContentValuesでは、キーが項目名になる。cvがレコードのフォーマットに合わせる入れ物。HashMapみたいなものかな？
		ContentValues cv = new ContentValues();
		cv.put("user", txtUser);// キーとデータの組み合せで入れていく（.put)
		cv.put("petType", txtPetType);
		cv.put("nowShotCnt", preferenceNewShotCnt);
		cv.put("totalShotCnt", preferenceTotalShotCnt);
		
		// 誕生日記録失敗コメントアウト
		// cv.put("birthDay", birthDay);
		
		/**
		 * レコードの更新を実施する 第一引数：テーブル名 第二引数：ContentValueオブジェクト。
		 * 第三引数：whereClauseには、更新するデータのWHERE条件を指定します
		 * 。この値をnullに指定すると、すべての行が更新対象となります。
		 * 第四引数:更新するデータのwhere条件を「?」を使ってパラメータで指定した場合のパラメータ値をString配列で指定。
		 * WHERE条件に「?」パラメータが無い場合は、nullを指定。 戻り値：更新した行数が返却される
		 */
		long id = db.update("pet", cv, null, null);// SQLのupdateに相当するupdate()メソッドで上書きされる。引数は、テーブル名とレコードが入っているcv。
		String msg = "";
		if (id != -1) {// 戻り値を確認して成否を確認。戻り値-1の時は、テーブルがないなどの異常時。
			msg = "先ほどの撮影回数は" + preferenceNewShotCnt + "回。合計撮影回数は"
					+ preferenceTotalShotCnt + "です！";

		} else {
			msg = "データの登録に失敗しました。先ほどの撮影回数は" + preferenceNewShotCnt
					+ preferenceTotalShotCnt + "回です。";
		}
		CameLog.setLog(TAG, msg);
		

		
		return msg;
	}
}

// /**
// * 以下、SQLiteTIPSメモ
// *
// * 検索時にSQL文を直接送る場合はちょっと構文が違う。DELETEやINSERT時のようなexecSQLではない。 例えばこんな感じ。
// * db.rawQuery("select * from books where",null);
// *
// * String msg = ""; boolean eol = cs.moveToFirst(); while (eol) { msg +=
// * cs.getString(1); eol = cs.moveToNext(); } Toast.makeText(this, msg,
// * Toast.LENGTH_SHORT).show();
// */
//
// /** 前回の登録Dataを削除。 */
// /** 書き込み・読み込み用のデータベースオブジェクトを取得。newじゃないのか。 */
// db = helper.getWritableDatabase();
// /**
// * レコード削除する
// * 第一引数：テーブル名
// * 第二引数：Where句に相当する。検索条件。
// * 第三引数：Where句の指定データ
// * 戻り値は、影響を受けた行数。このパターンだとISBNの重複はないので1が戻り値。
// */
// int ct = db.delete("pet", "user = ?", params);//
// 第1引数はテーブル名。第2引数は軸とするISBN。第三引数がisbnが入った配列。
// CameLog.setLog(TAG, "前回のユーザーデータ" + ct + "個の削除に成功");