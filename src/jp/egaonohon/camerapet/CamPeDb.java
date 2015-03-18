package jp.egaonohon.camerapet;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.widget.Toast;
import android.content.Context;

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
	private static Integer nonSavedNewShotCnt = null;
	/** DB保存ずみ直近の撮影回数 */
	private static Integer savedNewShot = null;
	/** DB保存済み累積撮影回数 */
	private static Integer intTotalShotCnt = null;
	/** DB保存済み直近撮影回数と未保存の直近撮影回数の差分 */
	private static Integer sabunNewShotCnt = null;
	/** 新たに保存する累積撮影回数 */
	private static Integer newTotalShotCnt = null;
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
			intTotalShotCnt = cs.getInt(3);// 撮影回数を引っ張ってくる
			// String strPrice=cs.getString(2);
			// Toast.makeText(this, strPrice,
			// Toast.LENGTH_SHORT).show();
			// txtPrice.setText(Integer.toString(price));
		} else {
			// データがなかったので、その旨を表示する
			Toast.makeText(context, "データがありません。", Toast.LENGTH_SHORT).show();
		}
		Toast.makeText(context, "これまでの撮影回数は" + intTotalShotCnt + "です",
				Toast.LENGTH_SHORT).show();
		/**
		 * 戻り値としてこれまでの累積撮影回数を渡す。
		 */
		return intTotalShotCnt;
	}

	/**
	 * データベースから直近撮影回数を取得するメソッド。
	 * 
	 * @param context
	 * @return
	 */
	public static int getNowShotCnt(Context context) {
		SQLiteDatabase db = helper.getReadableDatabase();// getReadableDatabase()メソッドを使う。getWritableDatabaseもできるがあえて書き込み権限は不要なので今回は使わない。
		// db.queryの第二引数を作る
		// select文のカラムの指定する文字列をString型の配列に記述。ここは3つのカラムすべてということか…。
		String[] cols = { "user", "petType", "nowShotCnt", "totalShotCnt" };
		// Select文の行を特定する（where句）文字列を取得（ISBNの指定）。ISBNを引っ張ってこいよ!て感じ。
		String[] params = { txtUser };

		// 実際にselect文に相当するメソッドを実行。
		Cursor cs = db.query("pet", cols, "user = ?", params, null, null, null,
				null);// DBからの戻り値のCursorをこの後扱っていく。
		// データがあれば、データを取得する。なければ、無い！
		if (cs.moveToFirst()) {
			// タイトルを引っ張ってくる。(1)は列を示す。一番左が0から始まる。
			nonSavedNewShotCnt = cs.getInt(2);// 撮影回数を引っ張ってくる
		} else {
			// データがなかったので、その旨を表示する
			Toast.makeText(context, "データがありません。", Toast.LENGTH_SHORT).show();
		}
		Toast.makeText(context, "先ほどの撮影回数は" + nonSavedNewShotCnt + "です",
				Toast.LENGTH_SHORT).show();
		/**
		 * 戻り値として直近撮影回数を渡す。
		 */
		return nonSavedNewShotCnt;
	}

	/**
	 * 直近写真撮影回数のみをデータベースに保存するメソッド。
	 * 
	 * @param cntNum
	 * @return
	 */
	public static String saveNowCount(Context context, int cntNum) {
		/** 未保存の撮影回数。 */
		nonSavedNewShotCnt = cntNum;

		/**
		 * SimpleDatabaseHelperを取得。
		 */
		helper = new SimpleDatabaseHelper(context);

		/**
		 * 前回の登録Dataから撮影回数を取得。
		 */
		// 読み込み用のデータベースオブジェクトを取得。SQLの読み込みは結果が戻ってきてそれを処理するので重い作業になる。
		SQLiteDatabase db = helper.getReadableDatabase();// getReadableDatabase()メソッドを使う。getWritableDatabaseもできるがあえて書き込み権限は不要なので今回は使わない。
		// db.queryの第二引数を作る
		// select文のカラムの指定する文字列をString型の配列に記述。ここは3つのカラムすべてということか…。
		String[] cols = { "user", "petType", "nowShotCnt", "totalShotCnt" };
		// Select文の行を特定する（where句）文字列を取得（ISBNの指定）。ISBNを引っ張ってこいよ!て感じ。
		String[] params = { txtUser };

		// 実際にselect文に相当するメソッドを実行。
		// 戻り値は、返却されたレコード群を示すCursor（カーソル）が返却される
		Cursor cs = db.query("pet", cols, "user = ?", params, null, null, null,
				null);// DBからの戻り値のCursorをこの後扱っていく。
		// データがあれば、データを取得する。なければ、無い！
		if (cs.moveToFirst()) {
			/** 直近撮影回数を引っ張ってくる */
			savedNewShot = cs.getInt(2);
			/** 累積撮影回数を引っ張ってくる */
			intTotalShotCnt = cs.getInt(3);
		} else {
			// データがなかったので、その旨を表示する
			Toast.makeText(context, "データがありません。", Toast.LENGTH_SHORT).show();
		}

		CameLog.setLog(TAG, "累計撮影回数取得に成功");

		/*
		 * 検索時にSQL文を直接送る場合はちょっと構文が違う。DELETEやINSERT時のようなexecSQLではない。 例えばこんな感じ。
		 * db.rawQuery("select * from books where",null);
		 * 
		 * String msg = ""; boolean eol = cs.moveToFirst(); while (eol) { msg +=
		 * cs.getString(1); eol = cs.moveToNext(); } Toast.makeText(this, msg,
		 * Toast.LENGTH_SHORT).show();
		 */

		/** 前回の登録Dataを削除。 */
		// 書き込み・読み込み用のデータベースオブジェクトを取得。newじゃないのか。
		db = helper.getWritableDatabase();
		// レコード削除する
		// 第一引数：テーブル名
		// 第二引数：Where句に相当する。検索条件。
		// 第三引数：Where句の指定データ
		// 戻り値は、影響を受けた行数。このパターンだとISBNの重複はないので1が戻り値。
		int ct = db.delete("pet", "user = ?", params);// 第1引数はテーブル名。第2引数は軸とするISBN。第三引数がisbnが入った配列。
		CameLog.setLog(TAG, "前回のユーザーデータ" + ct + "個の削除に成功");

		/**
		 * dbへの書き込み
		 */
		db = helper.getWritableDatabase();
		
		/**
		 * 合計撮影回数の算出。
		 */
		sabunNewShotCnt = nonSavedNewShotCnt - savedNewShot;
		newTotalShotCnt = sabunNewShotCnt + intTotalShotCnt;

		// カラム（？　これだと行になるが列では？）名とデータの組わせで1レコードのデータを作成している
		// ContentValuesでは、キーが項目名になる。cvがレコードのフォーマットに合わせる入れ物。HashMapみたいなものかな？
		ContentValues cv = new ContentValues();
		cv.put("user", txtUser);// キーとデータの組み合せで入れていく（.put)
		cv.put("petType", txtPetType);
		cv.put("nowShotCnt", nonSavedNewShotCnt);
		cv.put("totalShotCnt", newTotalShotCnt);
		// レコードの追加を実施する
		// 第一引数：テーブル名
		// 第二引数：nullColumHack(項目がnullの場合の処理方法の指定)。通常はあまり考えられない。
		// 第三引数：ContentValues(レコードデータ)
		// 戻り値：long型　row　idが返却される
		long id = db.insert("pet", null, cv);// SQLのINSERTに相当するinsert()メソッドで追加される。引数は、テーブル名とレコードが入っているcv。
		String msg = "";
		if (id != -1) {// 戻り値を確認して成否を確認。戻り値-1の時は、テーブルがないなどの異常時。
			msg = "先ほどの撮影回数は" + nonSavedNewShotCnt + "回。合計撮影回数は" + newTotalShotCnt + "です！";
		} else {
			msg = "データの登録に失敗しました。先ほどの撮影回数は" + nonSavedNewShotCnt + newTotalShotCnt + "回です。";
		}

		CameLog.setLog(TAG, msg);
		return msg;
	}

//	/**
//	 * 累計写真撮影回数を算出してデータベースに保存するメソッド。
//	 * 
//	 * @param cntNum
//	 * @return
//	 */
//	public static Integer saveTotalCount(Context context) {
//		/**
//		 * SimpleDatabaseHelperを取得。
//		 */
//		helper = new SimpleDatabaseHelper(context);
//
//		/**
//		 * ローカル変数を宣言
//		 */
//
//		int nowShot = 0;
//		int oldTotalShot = 0;
//
//		/**
//		 * 前回の登録Dataから撮影回数を取得。
//		 */
//		SQLiteDatabase db = helper.getReadableDatabase();// getReadableDatabase()メソッドを使う。getWritableDatabaseもできるがあえて書き込み権限は不要なので今回は使わない。
//		// db.queryの第二引数を作る
//		// select文のカラムの指定する文字列をString型の配列に記述。ここは3つのカラムすべてということか…。
//		String[] cols = { "user", "petType", "nowShotCnt", "totalShotCnt" };
//		// Select文の行を特定する（where句）文字列を取得（ISBNの指定）。ISBNを引っ張ってこいよ!て感じ。
//		String[] params = { txtUser };
//
//		// 実際にselect文に相当するメソッドを実行。
//		// 戻り値は、返却されたレコード群を示すCursor（カーソル）が返却される
//		Cursor cs = db.query("pet", cols, "user = ?", params, null, null, null,
//				null);// DBからの戻り値のCursorをこの後扱っていく。
//		// データがあれば、データを取得する。なければ、無い！
//		if (cs.moveToFirst()) {
//			/** 直近撮影回数を引っ張ってくる */
//			nowShot = cs.getInt(2);
//			/** 累積撮影回数を引っ張ってくる */
//			oldTotalShot = cs.getInt(3);
//		} else {
//			// データがなかったので、その旨を表示する
//			Toast.makeText(context, "データがありません。", Toast.LENGTH_SHORT).show();
//		}
//
//		CameLog.setLog(TAG, "前回の撮影回数取得に成功");
//
//		/** 前回の登録Dataを削除。 */
//		// 書き込み・読み込み用のデータベースオブジェクトを取得。
//		db = helper.getWritableDatabase();
//		// レコード削除する
//		// 第一引数：テーブル名
//		// 第二引数：Where句に相当する。検索条件。
//		// 第三引数：Where句の指定データ
//		// 戻り値は、影響を受けた行数。このパターンだとISBNの重複はないので1が戻り値。
//		int ct = db.delete("pet", "user = ?", params);// 第1引数はテーブル名。第2引数は軸とするISBN。第三引数がユーザー名が入った配列。
//		CameLog.setLog(TAG, "前回のユーザーデータ" + ct + "個の削除に成功");
//
//		/**
//		 * dbへの書き込み
//		 */
//		db = helper.getWritableDatabase();
//
//		/**
//		 * 合計撮影回数の算出。
//		 */
//		Integer newTotalShotCnt = nowShot + oldTotalShot;
//
//		// カラム（？　これだと行になるが列では？）名とデータの組わせで1レコードのデータを作成している
//		// ContentValuesでは、キーが項目名になる。cvがレコードのフォーマットに合わせる入れ物。HashMapみたいなものかな？
//		ContentValues cv = new ContentValues();
//		cv.put("user", txtUser);// キーとデータの組み合せで入れていく（.put)
//		cv.put("petType", txtPetType);
//		cv.put("nowShotCnt", nowShot);
//		cv.put("totalShotCnt", newTotalShotCnt);
//		// レコードの追加を実施する
//		// 第一引数：テーブル名
//		// 第二引数：nullColumHack(項目がnullの場合の処理方法の指定)。通常はあまり考えられない。
//		// 第三引数：ContentValues(レコードデータ)
//		// 戻り値：long型　row　idが返却される
//		long id = db.insert("pet", null, cv);// SQLのINSERTに相当するinsert()メソッドで追加される。引数は、テーブル名とレコードが入っているcv。
//		String msg = "";
//		if (id != -1) {// 戻り値を確認して成否を確認。戻り値-1の時は、テーブルがないなどの異常時。
//			msg = "先ほどの撮影回数は" + nonSavedNewShotCnt + "回。今までの合計撮影回数は" + newTotalShotCnt
//					+ "回です！";
//		} else {
//			msg = "データの登録に失敗しました。先ほどの撮影回数は" + nonSavedNewShotCnt + "回です。";
//		}
//
//		CameLog.setLog(TAG, msg);
//		return newTotalShotCnt;
//	}
}
