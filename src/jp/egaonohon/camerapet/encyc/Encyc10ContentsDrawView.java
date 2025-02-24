package jp.egaonohon.camerapet.encyc;

import jp.egaonohon.camerapet.CamPePref;
import jp.egaonohon.camerapet.CameLog;
import jp.egaonohon.camerapet.R;
import jp.egaonohon.camerapet.R.drawable;
import jp.egaonohon.camerapet.R.string;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.View;

/**
 * ペットPet010A開発時には、petNameTxtなどのdummy部分を本番文字に差し替えてください。
 * @author OtaShohei
 *
 */
public class Encyc10ContentsDrawView extends View {

	/** ペットの詳細を記述するエリアの幅 */
	int contentsFieldWidth;
	/** ペットの詳細を記述するエリアの高さ */
	int contentsFieldHeight;
	/** アイテム配置の基準尺度 */
	int layoutScale;
	/** プリファレンス管理用のペット種別名 */
	private String petModelNumberTxt = "Pet010A";
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
	
//	/** 塗りのテーマカラー */
//	private static int themeColor = Color.argb(255, 224, 107, 98);

	/** Logのタグを定数で確保 */
	private static final String TAG = "Encyc10ContentsDrawView";

	public Encyc10ContentsDrawView(Context context) {
		super(context);
	}

	public Encyc10ContentsDrawView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public Encyc10ContentsDrawView(Context context, AttributeSet attrs,
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
		if (CamPePref.loadPetModelNumber(getContext(), petModelNumberTxt)
				.equals("getted")
				|| CamPePref.loadPetModelNumber(getContext(), petModelNumberTxt)
						.equals("now")) {

			CameLog.setLog(TAG, "以前ゲットしたペットか現在のペットであると判定");

			/** このペットが未取得の場合のペット名文字列を準備 */
			petNameTxt = res.getString(R.string.pet_dummy_name);
			/** このペットが未取得の場合のペット体重文字列を準備 */
			petWeightTxt = res.getString(R.string.pet_dummy_weight);
			/** このペットが未取得の場合のペット体長文字列を準備 */
			petLengthTxt = res.getString(R.string.pet_dummy_length);
			/** このペットが未取得の場合のペット好物文字列を準備 */
			petFavoriteTxt = res.getString(R.string.pet_dummy_favorite);
			/** このペットが未取得の場合のペット解説文字列を準備 */
			petCommentTxt = res.getString(R.string.pet_dummy_comment);

			/** ペット写真を取得 */
			petPh = BitmapFactory.decodeResource(getContext().getResources(),
					R.drawable.pet003a_r);

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
		
//		/** 「続く」文字描画 */
//		/** 文字色にテーマカラーを設定 */
//		paint.setColor(themeColor);
//		/** Aliasを設定 */
//		paint.setAntiAlias(true);
//
//		/** テキストサイズと書体を設定 */
//		paint.setTextSize(contentsFieldWidth / 26);
//		paint.setTypeface(Typeface.DEFAULT_BOLD);
//
//		/** テキスト中央揃えに設定 */
//		paint.setTextAlign(Paint.Align.CENTER);
//
//		/** ペット名表示 */
//		canvas.drawText(petNameTxt, layoutScale * 73, layoutScale * 77, paint);
	}
}
