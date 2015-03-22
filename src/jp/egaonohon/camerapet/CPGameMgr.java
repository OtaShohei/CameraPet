package jp.egaonohon.camerapet;

import java.util.ArrayList;
import java.util.LinkedList;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;

public class CPGameMgr {

	/** タスクリスト */
	private LinkedList<CPTask> taskList = new LinkedList<CPTask>(); //
	/** Logのタグを定数で確保 */
	private static final String TAG = "CPGameMgr";
	Context context;
	/** Viewの幅 */
	int viewWidth;
	/** Viewの高さ */
	int viewHeight;

	/** ペット画像 */
	private Bitmap petPh;
	/** 複数餌画像 */
	private ArrayList<Bitmap> esaPhList;
	/** 単一餌画像 */
	private Bitmap esaPh;

	/**
	 * ゲームに登場するインスタンスをココに追加。
	 * 
	 * @param context
	 *            Viewが属するcontext
	 * @param viewWidth
	 *            viewの幅
	 * @param viewHeight
	 *            viewの高さ
	 */
	CPGameMgr(Context context, int viewWidth, int viewHeight) {

		Resources r = context.getResources();
		taskList.add(new CPFpsController());
		this.context = context;
		this.viewWidth = viewWidth;
		this.viewHeight = viewHeight;
		CameLog.setLog(TAG, "Viewの幅は" + viewWidth + "Viewの高さは" + viewHeight);

		/** ペット写真取得 */
		petPh = BitmapFactory.decodeResource(r, R.drawable.alpaca02);

		/** ペット作成 */
		taskList.add(new CPPet(petPh, viewWidth, viewHeight));
		CameLog.setLog(TAG, "ペット作成");

		/** 直近撮影枚数を取得 */
		int esaCnt = 5;

		try {
			esaCnt = CamPeDb.getNowShotCnt(context);
			/** 餌写真取得準備 */
			CamPePh camPePh = new CamPePh();
			/**
			 * 直近撮影写真が複数枚の時 複数枚写真取得
			 */
			esaPhList = new ArrayList<Bitmap>();
			esaPhList = camPePh.get(context, esaCnt);
			CameLog.setLog(TAG, "コンストラクタにて画像の読み込み完了。餌Phは" + esaPhList.size()
					+ "枚");

			for (int i = 0; i < esaPhList.size(); i++) {
				/** 複数写真使用での餌インスタンス生成 */
				taskList.add(new CPEsa(esaPhList.get(i), viewWidth, viewHeight,
						(viewWidth / 5) * i, 0));
			}
			CameLog.setLog(TAG, "複数写真使用で餌作成");
		} catch (Exception e) {
			/** 餌写真取得準備 */
			CamPePh camPePh = new CamPePh();
			esaPhList = new ArrayList<Bitmap>();

			CameLog.setLog(TAG, "例外発生のためデフォルト写真枚数5枚で餌写真取得");

			for (int i = 0; i < 5; i++) {
				/** 複数写真使用での餌インスタンス生成 */
				taskList.add(new CPEsa(esaPhList.get(i), viewWidth, viewHeight,
						5 + ((viewWidth / 5) * i), 0));
			}
		}
	}

	/**
	 * Taskを実行。
	 * 
	 * @return
	 */
	public boolean onUpdate() {
		for (int i = 0; i < taskList.size(); i++) {
			if (taskList.get(i).onUpdate() == false) { // 更新失敗なら
				taskList.remove(i); // そのタスクを消す
				i--;
			}
		}
		return true;
	}

	/**
	 * 描画を行う。
	 * 
	 * @param c
	 */
	@SuppressLint("WrongCall")
	public void onDraw(Canvas c) {
		/** 現在の状態を保存 */
		c.save();

		/** Canvasの背景色を透明で塗る。 第二引数にPorterDuff.Mode.CLEARを付けないと透明にならないので注意。 */
		c.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);

		/** taskList内の描画を実行する */
		for (CPTask task : taskList) {
			task.onDraw(c);
		}
		/** 現在の状態の変更 */
		c.restore();
	}

}