package jp.egaonohon.camerapet;

import android.content.AsyncTaskLoader;
import android.content.ContentValues;
import android.content.Context;
//import android.content.SharedPreferences;
//import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.widget.Toast;


public class AsyncOnSave extends AsyncTaskLoader<String> {

	private SimpleDatabaseHelper helper = new SimpleDatabaseHelper(getContext());// このメンバ変数は、SimpleDatabaseHelperクラスの参照になっている。
	/** ユーザー名の初期値 */
	private String txtUser = "default";
	/** Petの種類の初期値 */
	private String txtPetType = "lv01";
	/** 撮影回数の初期値 */
	private Integer intShotCnt = null;
	/** 前回の撮影回数の初期値 */
	private Integer intBeforeShotCnt = null;
//	/** 撮影回数保存用Preferences */
//	private SharedPreferences pref;
//	/** Preferencesへの書き込み用Editor */
//	private Editor editor;

	public AsyncOnSave(Context context, Integer shotCnt) {
		super(context);
		intShotCnt = shotCnt;
		// TODO 自動生成されたコンストラクター・スタブ
	}

	@Override
	public void deliverResult(String data) {
		// TODO 自動生成されたメソッド・スタブ
		super.deliverResult(data);
	}

	@Override
	public String loadInBackground() {
		// データベース処理をここに記述。

		/**
		 * 前回の登録Dataから撮影回数を取得。
		 */
		// 読み込み用のデータベースオブジェクトを取得。SQLの読み込みは結果が戻ってきてそれを処理するので重い作業になる。
		SQLiteDatabase db = helper.getReadableDatabase();// getReadableDatabase()メソッドを使う。getWritableDatabaseもできるがあえて書き込み権限は不要なので今回は使わない。
		// db.queryの第二引数を作る
		// select文のカラムの指定する文字列をString型の配列に記述。ここは3つのカラムすべてということか…。
		String[] cols = { "user", "petType", "shotCnt" };
		// Select文の行を特定する（where句）文字列を取得（ISBNの指定）。ISBNを引っ張ってこいよ!て感じ。
		String[] params = { txtUser };

		// 実際にselect文に相当するメソッドを実行。以下、query()の引数。
		// 第一引数：テーブル名
		// 第二引数：項目名（カラム名）の指定（select　【＊】に相当）
		// 第三引数：Where句に相当する
		// 第四引数：Where句の指定データ
		// 第五引数：GropuBY句に相当。何かのカテゴリ単位で引っ張ってくるときに記述。
		// 第六引数：Having句に相当(GroupについてWhere句)
		// 第七引数：Ｏｒｄｅｒ　ＢＹ句に相当する
		// 第八引数：ＬＩＭＩＴ句に相当する。1000件中の10件だけを…などの指定。
		// 戻り値は、返却されたレコード群を示すCursor（カーソル）が返却される
		Cursor cs = db.query("pet", cols, "user = ?", params, null, null, null,
				null);// DBからの戻り値のCursorをこの後扱っていく。
		// データがあれば、データを取得する。なければ、無い！
		if (cs.moveToFirst()) {
			// データがあれば、それを取得する
			// txtTitle.setText(cs.getString(1));//
			// タイトルを引っ張ってくる。(1)は列を示す。一番左が0から始まる。0はisbnね。
			intBeforeShotCnt = cs.getInt(2);// 撮影回数を引っ張ってくる
			// String strPrice=cs.getString(2);
			// Toast.makeText(this, strPrice,
			// Toast.LENGTH_SHORT).show();
			// txtPrice.setText(Integer.toString(price));
		} else {
			// データがなかったので、その旨を表示する
			Toast.makeText(getContext(), "データがありません。", Toast.LENGTH_SHORT)
					.show();
		}

		Log.v("CAMERA", "前回の撮影回数取得に成功");

		/*
		 * 検索時にSQL文を直接送る場合はちょっと構文が違う。DELETEやINSERT時のようなexecSQLではない。 例えばこんな感じ。
		 * db.rawQuery("select * from books where",null);
		 */

		/*
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
		Log.v("CAMERA", "前回のユーザーデータ" + ct + "個の削除に成功");

		/**
		 * dbへの書き込み
		 */
		db = helper.getWritableDatabase();

		/**
		 * 合計撮影回数の算出。
		 */
		Integer totalShotCnt = intBeforeShotCnt + intShotCnt;

		// カラム（？　これだと行になるが列では？）名とデータの組わせで1レコードのデータを作成している
		// ContentValuesでは、キーが項目名になる。cvがレコードのフォーマットに合わせる入れ物。HashMapみたいなものかな？
		ContentValues cv = new ContentValues();
		cv.put("user", txtUser);// キーとデータの組み合せで入れていく（.put)
		cv.put("petType", txtPetType);
		cv.put("shotCnt", totalShotCnt);
		// レコードの追加を実施する
		// 第一引数：テーブル名
		// 第二引数：nullColumHack(項目がnullの場合の処理方法の指定)。通常はあまり考えられない。
		// 第三引数：ContentValues(レコードデータ)
		// 戻り値：long型　row　idが返却される
		long id = db.insert("pet", null, cv);// SQLのINSERTに相当するinsert()メソッドで追加される。引数は、テーブル名とレコードが入っているcv。
		String msg = "";
		if (id != -1) {// 戻り値を確認して成否を確認。戻り値-1の時は、テーブルがないなどの異常時。
			msg = "先ほどの撮影回数は" + intShotCnt + "回。今までの合計撮影回数は" + totalShotCnt
					+ "回です！";

		} else {
			msg = "データの登録に失敗しました。撮影回数は" + intShotCnt + "回";
		}

//		/** プリファレンスの準備 */
//		pref = this.getSharedPreferences("shotCnt", Context.MODE_PRIVATE);
//		/** プリファレンスに書き込むためのEditorオブジェクト取得 */
//		editor = pref.edit();
//		/** 撮影回数を0にリセットする。 */
//		editor.putInt("shotCnt", 0);
//		editor.commit();

		/**
		 * 餌クラスの元素材となるBitmap生成のために直近撮影済み枚数をプリファレンスにGetPh用に保存。
		 */
		TakenPhNum.save(getContext(), intShotCnt);
		return msg;
	}

	@Override
	protected void onStartLoading() {
		// TODO 自動生成されたメソッド・スタブ
		super.onStartLoading();
		forceLoad();
	}
}
