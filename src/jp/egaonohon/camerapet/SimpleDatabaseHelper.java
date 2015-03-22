package jp.egaonohon.camerapet;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * CameraPetで使用するデータベースを構築するためにSQLiteOpenHelperを継承して作ったクラス。
 * @author OtaShohei
 *
 */
public class SimpleDatabaseHelper extends SQLiteOpenHelper {
	/**
	 *  データベースファイル名をメンバ変数として宣言。
	 *  命名に制限事項はありません。名前は何でもいいし拡張子 *.sqlite があってもいい。今回はCameraPetUserにしている。
	 */
	static final private String DBNAME = "CameraPetUser";//
//	/** 初回起動日時保存用Preferencesのメンバ変数確保 */
//	SharedPreferences pref;
//	/** Preferencesへの書き込み用Editor */
//	Editor editor;
	
//	/** インストール日時 */
//	long birthDay;
	/** Logのタグを定数で確保 */
	private static final String TAG = "SimpleDatabaseHelper";
	/**
	 * 以下の定数はフレームワーク側で利用されています。バージョンとはSQLiteOpenHelperが見ている数字。
	 * データベースを改修（レコードを追加するなど）するときにこのバージョン番号を上げるとデータベースを作りなおしてくれる。しかも自動で。
	 * ただし、バージョンナンバーは下げられない。上げる。下げるとエラー。
	 */
	static final private int VERSION = 1;

	public SimpleDatabaseHelper(Context context) {
		// SQLiteOpenHelperのコンストラクタ呼出し。この辺はお決まりの呪文みたいなもの。
		super(context, DBNAME, null, VERSION);
	}

	/**
	 * このスマホに初めてアプリを入れたら
	 */
	@Override
	public void onOpen(SQLiteDatabase db) {
		super.onOpen(db);
//		/** インストール日時を取得 */
//		birthDay = System.currentTimeMillis();
	}

	/**
	 * アクティビティ側でgetWritableDatabase();か、getReadableDatabase();でデータベースを呼ばれたら
	 * 初めて呼ばれたタイミングで、DBがないときにこのonCreateが呼び出される!!　特にアクティビティ側で呼び出すような記述をしなくていい。
	 * それがSQLiteOpenhelperの役目。
	 * ただし、一度テーブルを作ると二度とこのメソッドは呼び出されない。
	 */
	@Override
	public void onCreate(SQLiteDatabase db) {

		try {
//			db.execSQL("CREATE TABLE pet ("
//					+ "user TEXT PRIMARY KEY, petType TEXT, nowShotCnt INTEGER, totalShotCnt INTEGER, birthDay INTEGER)");//isbnをキーにして、タイトルとプライスを入れる。
//			db.execSQL("INSERT INTO pet(user, petType, nowshotCnt, totalShotCnt, birthDay,)"
//					+ " VALUES('default', 'lv01', 0, 0, birthDay)");//ここでは初期データも同時に突っ込んでいる以下同様。
			db.execSQL("CREATE TABLE pet ("
					+ "user TEXT PRIMARY KEY, petType TEXT, nowShotCnt INTEGER, totalShotCnt INTEGER)");//isbnをキーにして、タイトルとプライスを入れる。
			db.execSQL("INSERT INTO pet(user, petType, nowshotCnt, totalShotCnt)"
					+ " VALUES('default', 'lv01', 0, 0)");//ここでは初期データも同時に突っ込んでいる以下同様。
		} catch (Exception e) {
			// TODO: handle exception
			CameLog.setLog(TAG, "DB作成に失敗");
		}

	}

	/**
	 *  このメソッドonUpgradeはコンストラクタでバージョンが変更されているときにだけ自動的に呼び出される。
	 *  ただ、事故の元なのでこのメソッドは慎重に使うべき。
	 *  バージョン1→バージョン5など。バージョン1つずつ上げてもらえるとは限らない。
	 *
	 *  したがって、最初のDBのテーブルを作るときに、予備の列を用意しておくなど安全策をとっておく方法も。
	 */
	@Override
	public void onUpgrade(SQLiteDatabase db, int old_v, int new_v) { //第2引数が古いバージョン番号。第3引数が新しいバージョン番号。
		// 単純に古いデータベースファイルを削除しただけ
		db.execSQL("DROP TABLE IF EXISTS pet");
		// 本当はバックアップ等をする必要がある!!　要注意!!
		onCreate(db);
		// 本当はこの後で、バックアップしたデータを戻す作業をする必要がある！！！
	}
}
