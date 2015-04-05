package jp.egaonohon.camerapet.encyc;

import jp.egaonohon.camerapet.R;
import jp.egaonohon.camerapet.R.drawable;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;

/**
 * ペット図鑑各ページでの描画の共通部分を担当するクラス。
 * @author OtaShohei
 *
 */
public class EncycCommonTextRect {

	/** 塗りのテーマカラー */
	private static int themeColor = Color.argb(255, 224, 107, 98);
	/** 文字色のテーマカラーダークグレー(K80相当) */
	private static int txtColorDarkgray = Color.argb(255, 51, 51, 51);
	/** 文字色のテーマカラーダークグレー(K80相当) */
	private static int txtColorWhite = Color.argb(255, 255, 255, 255);

	/**
	 * ペット図鑑各ページでの描画の共通部分を担当するメソッド。
	 */
	public void Draw(Context context, Canvas canvas, int contentsFieldWidth,
			int contentsFieldHeight, Bitmap petPh, int layoutScale,
			Paint paint, String petNameTxt, String petWeightTxt,
			String petLengthTxt, String petFavoriteTxt, String petCommentTxt) {
		/** 背景の古書風画像を取得 */
		Bitmap jimonPh = BitmapFactory.decodeResource(context.getResources(),
				R.drawable.encyclopedia_jimon);

		/** jimonPhのサイズを取得 */
		int jimonPhWidth = jimonPh.getWidth();
		int jimonPhHeight = jimonPh.getHeight();

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
				layoutScale * 39, layoutScale * 18, layoutScale * 97,
				layoutScale * 70), paint);

		/** テーマカラー設定 */
		paint.setColor(themeColor);

		/** 見出し座布団を塗る */
		Rect midashiRect = new Rect(layoutScale * 28, layoutScale * 70,
				layoutScale * 116, layoutScale * 80);

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
		canvas.drawText(petNameTxt, layoutScale * 68, layoutScale * 77, paint);

		/** 文字色にダークグレーを設定 */
		paint.setColor(txtColorDarkgray);

		/** ペットスペック用テキストサイズと書体を変更 */
		paint.setTextSize(contentsFieldWidth / 32);

		/** テキスト左寄せに変更 */
		paint.setTextAlign(Paint.Align.LEFT);

		/** ペット体重文字表示 */
		canvas.drawText(petWeightTxt, layoutScale * 30, layoutScale * 88, paint);

		/** ペット身長文字表示 */
		canvas.drawText(petLengthTxt, layoutScale * 30, layoutScale * 94, paint);

		/** ペット好物文字表示 */
		canvas.drawText(petFavoriteTxt, layoutScale * 30, layoutScale * 100,
				paint);

		/** ペットスペック用テキストサイズと書体を変更 */
		paint.setTextSize(contentsFieldWidth / 28);

		/** テキストの改行に必要なローカル変数 */
		int lineBreakPoint = Integer.MAX_VALUE;
		int currentIndex = 0;
		/** 本文開始位置のY値 */
		int linePointY = layoutScale * 108;

		/** テキストの改行を行いつつ文字表示 */
		while (lineBreakPoint != 0) {
			String mesureString = petCommentTxt.substring(currentIndex);
			lineBreakPoint = paint.breakText(mesureString, true,
					(layoutScale * 86), null);
			if (lineBreakPoint != 0) {
				String line = petCommentTxt.substring(currentIndex,
						currentIndex + lineBreakPoint);
				canvas.drawText(line, layoutScale * 30, linePointY, paint);
				linePointY += (contentsFieldWidth / 20);
				currentIndex += lineBreakPoint;
			}
		}
	}

}
