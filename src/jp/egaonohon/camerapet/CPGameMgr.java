package jp.egaonohon.camerapet;

import java.util.ArrayList;
import java.util.LinkedList;

import android.R.integer;
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

	/** ペット画像右向き */
	private Bitmap petPhR;
	
	/** ペット画像右向き */
	private Bitmap petPhL;
	
	/** 取得した餌の数 */
	int esaCnt;
	
	/** 複数餌画像 */
	private ArrayList<Bitmap> esaPhList;
	/** 単一餌画像 */
	private Bitmap esaPh;
	/** 餌初期位置：X軸。*/
	private int esaDefaultX = 1;
	/** 餌初期位置：Y軸。*/
	private int esaDefaultY = 0;
	/** 餌インスタンスを格納するArrayList */
	ArrayList<CPEsa> esaAry = new ArrayList<CPEsa>();

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
		CameLog.setLog(TAG, "Viewの幅は" + viewWidth + "。Viewの高さは" + viewHeight);

		/** 直近撮影枚数を取得 */
		esaCnt = CamPeDb.getNowShotCnt(context);
		
		if (esaCnt ==0) {
			esaCnt = 3;
		}

		/** ペット写真取得 */
		petPhR = BitmapFactory.decodeResource(r, R.drawable.alpaca02);
		petPhL = BitmapFactory.decodeResource(r, R.drawable.alpaca_left);
		
		/** ペット作成 */
		taskList.add(new CPPet(petPhR, petPhL,viewWidth, viewHeight));
		CameLog.setLog(TAG, "ペット作成");

		// /**
		// * タイマークラスかAsyncTaskLoaderをつかって複数餌を一定時間ごとに呼び出す命令（書き途中）
		// */

		// /** 餌写真取得準備 */
		// CamPePh camPePh = new CamPePh();
		// petPh = camPePh.choicedOneGet(context, esaCnt);
		//
		// taskList.add(new CPEsa(petPh, viewWidth, viewHeight));

		try {
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
				taskList.add(new CPEsa(esaPhList.get(i), viewWidth, viewHeight,esaDefaultX,esaDefaultY));
			}
			CameLog.setLog(TAG, "複数写真使用で餌作成");
		} catch (Exception e) {
			/** 餌写真取得準備 */
			CamPePh camPePh = new CamPePh();
			esaPhList = new ArrayList<Bitmap>();

			CameLog.setLog(TAG, "例外発生のためデフォルト写真枚数5枚で餌写真取得");

			for (int i = 0; i < 5; i++) {
				/** 複数写真使用での餌インスタンス生成 */
				taskList.add(new CPEsa(esaPhList.get(i), viewWidth, viewHeight,esaDefaultX,esaDefaultY *i));
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