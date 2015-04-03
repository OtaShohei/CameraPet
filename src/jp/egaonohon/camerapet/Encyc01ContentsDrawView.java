package jp.egaonohon.camerapet;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

public class Encyc01ContentsDrawView extends View {

	/** ペットの詳細を記述するエリアの幅 */
	int contentsFieldWidth;
	/** ペットの詳細を記述するエリアの高さ */
	int contentsFieldHeight;
	/** アイテム配置の基準尺度 */
	int layoutScale;

	/** プリファレンス管理用のペット種別名 */
	private String petSpeciesNameNameTxt = "Pet001A";
	/** ペット近影 */
	private Bitmap petPh;
	/** ペット名文字 */
	private String petNameTxt;
	/** ペット体重文字 */
	private String petWeightTxt;
	/** ペット体長文字 */
	private String petLengthTxt;
	/** ペット好物文字 */
	private String petFavoriteTxt;
	/** ペット解説文字 */
	private String petCommentTxt;

	/** Logのタグを定数で確保 */
	private static final String TAG = "Encyc01ContentsDrawView";

	public Encyc01ContentsDrawView(Context context) {
		super(context);
	}

	public Encyc01ContentsDrawView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public Encyc01ContentsDrawView(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
	}

	/*
	 * (非 Javadoc)
	 * 
	 * @see android.view.View#onSizeChanged(int, int, int, int)
	 */
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);

		contentsFieldWidth = w;
		contentsFieldHeight = h;

	}

	/*
	 * (非 Javadoc)
	 * 
	 * @see android.view.View#onDraw(android.graphics.Canvas)
	 */
	@SuppressLint("DrawAllocation")
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		/** 表示文字列取得のためのリソースを準備 */
		Resources res = getResources();

		/** 以前ゲットしたペットか現在のペットであるならばならば */
		if (CamPePref.loadPetStatus(getContext(), petSpeciesNameNameTxt)
				.equals("getted")
				|| CamPePref.loadPetStatus(getContext(), petSpeciesNameNameTxt)
						.equals("now")) {

			CameLog.setLog(TAG, "以前ゲットしたペットか現在のペットであると判定");

			/** このペットが未取得の場合のペット名文字列を準備 */
			petNameTxt = res.getString(R.string.pet_01_name);
			/** このペットが未取得の場合のペット体重文字列を準備 */
			petWeightTxt = res.getString(R.string.pet_01_weight);
			/** このペットが未取得の場合のペット体長文字列を準備 */
			petLengthTxt = res.getString(R.string.pet_01_length);
			/** このペットが未取得の場合のペット好物文字列を準備 */
			petFavoriteTxt = res.getString(R.string.pet_01_favorite);
			/** このペットが未取得の場合のペット解説文字列を準備 */
			petCommentTxt = res.getString(R.string.pet_01_comment);

			/** ペット写真を取得 */
			petPh = BitmapFactory.decodeResource(getContext().getResources(),
					R.drawable.pet001a_r);

			/** 現在も以前もゲットしていないペットであるならば */
		} else {

			CameLog.setLog(TAG, "現在も以前もゲットしていないペットであると判定");

			/** このペットが未取得の場合のペット名文字列を準備 */
			petNameTxt = res.getString(R.string.pet_unknown_name);
			/** このペットが未取得の場合のペット体重文字列を準備 */
			petWeightTxt = res.getString(R.string.pet_unknown_weight);
			/** このペットが未取得の場合のペット体長文字列を準備 */
			petLengthTxt = res.getString(R.string.pet_unknown_length);
			/** このペットが未取得の場合のペット好物文字列を準備 */
			petFavoriteTxt = res.getString(R.string.pet_unknown_favorite);
			/** このペットが未取得の場合のペット解説文字列を準備 */
			petCommentTxt = res.getString(R.string.pet_unknown_comment);

			/** ペット写真を取得 */
			petPh = BitmapFactory.decodeResource(getContext().getResources(),
					R.drawable.nazono_inu);
		}

		/** Paint準備 */
		Paint paint = new Paint();
		layoutScale = contentsFieldWidth / 128;

		/** Text類描画実行 */
		EncycCommonTextRect encycCommonTextRect = new EncycCommonTextRect();
		encycCommonTextRect.Draw(getContext(), canvas, contentsFieldWidth,
				contentsFieldHeight, petPh, layoutScale, paint, petNameTxt,
				petWeightTxt, petLengthTxt, petFavoriteTxt, petCommentTxt);
	}
}
