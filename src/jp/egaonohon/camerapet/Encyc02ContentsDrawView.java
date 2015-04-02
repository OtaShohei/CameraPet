package jp.egaonohon.camerapet;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.View;

public class Encyc02ContentsDrawView extends View {

	/** ペットの詳細を記述するエリアの幅 */
	int contentsFieldWidth;
	/** ペットの詳細を記述するエリアの高さ */
	int contentsFieldHeight;
	/** アイテム配置の基準尺度 */
	int layoutScale;
	/** 塗りのテーマカラー */
	private static int themeColor = Color.argb(255, 224, 107, 98);
	/** 文字色のテーマカラーダークグレー(K80相当) */
	private static int txtColorDarkgray = Color.argb(255, 51, 51, 51);
	/** 文字色のテーマカラーダークグレー(K80相当) */
	private static int txtColorWhite = Color.argb(255, 255, 255, 255);
	/** プリファレンス管理用のペット種別名 */
	private String petSpeciesNameNameTxt = "Pet002A";
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
	private static final String TAG = "Encyc02ContentsDrawView";

	public Encyc02ContentsDrawView(Context context) {
		super(context);
	}

	public Encyc02ContentsDrawView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public Encyc02ContentsDrawView(Context context, AttributeSet attrs,
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
			petNameTxt = res.getString(R.string.pet_02_name);
			/** このペットが未取得の場合のペット体重文字列を準備 */
			petWeightTxt = res.getString(R.string.pet_02_weight);
			/** このペットが未取得の場合のペット体長文字列を準備 */
			petLengthTxt = res.getString(R.string.pet_02_length);
			/** このペットが未取得の場合のペット好物文字列を準備 */
			petFavoriteTxt = res.getString(R.string.pet_02_favorite);
			/** このペットが未取得の場合のペット解説文字列を準備 */
			petCommentTxt = res.getString(R.string.pet_02_comment);

			/** ペット写真を取得 */
			petPh = BitmapFactory.decodeResource(getContext().getResources(),
					R.drawable.pet002a_r);

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

		/** 背景の古書風画像を取得 */
		Bitmap jimonPh = BitmapFactory.decodeResource(getContext()
				.getResources(), R.drawable.encyclopedia_jimon);

		/** jimonPhのサイズを取得 */
		int jimonPhWidth = jimonPh.getWidth();
		int jimonPhHeight = jimonPh.getHeight();

		layoutScale = contentsFieldWidth / 128;

		/**
		 * jimonPh描画 drawBitmapの引数のうちRectが関わる第3第4引数については…
		 * http://blogs.yahoo.co.jp/magoapp/1265371.html …を参照。
		 * */
		canvas.drawBitmap(jimonPh, new Rect(0, 0, jimonPhWidth, jimonPhHeight),
				new Rect(0, 0, contentsFieldWidth, contentsFieldHeight), paint);

		/** petPhのサイズを取得 */
		int phWidth = petPh.getWidth();
		int phHeight = petPh.getHeight();

		/**
		 * petPh描画 drawBitmapの引数のうちRectが関わる第3第4引数については…
		 * http://blogs.yahoo.co.jp/magoapp/1265371.html …を参照。
		 * */
		canvas.drawBitmap(petPh, new Rect(0, 0, phWidth, phHeight), new Rect(
				layoutScale * 42, layoutScale * 12, layoutScale * 94,
				layoutScale * 64), paint);

		/** テーマカラー設定 */
		paint.setColor(themeColor);

		/** 見出し座布団を塗る */
		Rect midashiRect = new Rect(layoutScale * 28, layoutScale * 64,
				layoutScale * 116, layoutScale * 74);

		/** 見出し座布団描画実行 */
		canvas.drawRect(midashiRect, paint);

		/** 文字色に白を設定 */
		paint.setColor(txtColorWhite);
		/** Aliasを設定 */
		paint.setAntiAlias(true);

		/** テキストサイズと書体を設定 */
		paint.setTextSize(contentsFieldWidth / 26);
		paint.setTypeface(Typeface.DEFAULT_BOLD);

		/** テキスト中央揃えに設定 */
		paint.setTextAlign(Paint.Align.CENTER);

		/** ペット名表示 */
		canvas.drawText(petNameTxt, layoutScale * 68, layoutScale * 71, paint);

		/** 文字色にダークグレーを設定 */
		paint.setColor(txtColorDarkgray);

		/** テキストサイズと書体を変更 */
		paint.setTextSize(contentsFieldWidth / 32);

		/** テキスト左寄せに変更 */
		paint.setTextAlign(Paint.Align.LEFT);

		/** ペット体重文字表示 */
		canvas.drawText(petWeightTxt, layoutScale * 30, layoutScale * 82, paint);

		/** ペット身長文字表示 */
		canvas.drawText(petLengthTxt, layoutScale * 30, layoutScale * 88, paint);

		/** ペット好物文字表示 */
		canvas.drawText(petFavoriteTxt, layoutScale * 30, layoutScale * 94,
				paint);

		/** テキストの改行に必要なローカル変数 */
		int lineBreakPoint = Integer.MAX_VALUE;
		int currentIndex = 0;
		/** 本文開始位置のY値 */
		int linePointY = layoutScale * 102;

		/** テキストの改行を行いつつ文字表示 */
		while (lineBreakPoint != 0) {
			String mesureString = petCommentTxt.substring(currentIndex);
			lineBreakPoint = paint.breakText(mesureString, true,
					(layoutScale * 86), null);
			if (lineBreakPoint != 0) {
				String line = petCommentTxt.substring(currentIndex,
						currentIndex + lineBreakPoint);
				canvas.drawText(line, layoutScale * 30, linePointY, paint);
				linePointY += (contentsFieldWidth / 32);
				currentIndex += lineBreakPoint;
			}
		}
	}
}
