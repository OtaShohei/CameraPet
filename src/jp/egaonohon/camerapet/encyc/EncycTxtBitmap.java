package jp.egaonohon.camerapet.encyc;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.Paint.Style;

public class EncycTxtBitmap {

	/** 画面文字や塗りのテーマカラー */
	private static int themeColor = Color.argb(255, 224, 107, 98);
	/**
	 * ペット図鑑に、ペット近影・ペット解説文などを描画するメソッド。
	 *
	 * @param canvas
	 */
	private static void draw(Canvas canvas) {
		/** プログレスバーの記述 */
		Paint paint = new Paint();
		/** 文字にテーマカラー設定 */
		paint.setColor(themeColor);
		/** Aliasを設定 */
		paint.setAntiAlias(true);

		/** テキスト設定 sp */
//		paint.setTextSize(viewWidth / 26);
		paint.setTypeface(Typeface.DEFAULT_BOLD);
		/** テキスト右寄せ */
		paint.setTextAlign(Paint.Align.RIGHT);

		/** 経験値表示 */
//		canvas.drawText(expTxt, (viewWidth / 40) * 39, (viewHeight / 80) * 4,
//				paint);

		/** 誕生日表示 */
//		canvas.drawText(petAge, (viewWidth / 40) * 39, (viewHeight / 80) * 7,
//				paint);

		/** テキスト右寄せ */
		paint.setTextAlign(Paint.Align.LEFT);
		// CameLog.setLog(TAG, "獲得済みエサの数@textRectWriteは" + esaGetCnt);

		/** エサステイタス表示のため朝昼晩判定を行うメソッドを呼び出す。 */
//		gameTimeJudge();

		/** 上記に基づき、エサステイタス表示 */
//		canvas.drawText(esaStatus + "    " + esaGetCnt + "/" + progressMax,
//				viewWidth / 40, (viewHeight / 160) * 15, paint);

		/** 残りエサ数表示 */
//		canvas.drawText(foodCntThisTime + "    "
//				+ (nowFalldownEsaCnt - throwedEsa), viewWidth / 40,
//				(viewHeight / 160) * 21, paint);

		/**
		 * 進捗部分を塗る
		 *
		 * 第3引数は、外枠のdrawRect開始位置（外枠のdrawRectの第1引数）分を足さないと外枠とずれてしまう。ここでは、
		 * viewWidth / 40がそれに相当する。 また、
		 * 第3引数はみなfloatに明示的にキャストしておかないと小さい差異が結果的に大きい差になってプログレスバーが最後まで塗れないので要注意。
		 */
//		RectF ratingRect = new RectF(
//				(float) viewWidth / 40,
//				(float) viewHeight / 40,
//				(float) ((((float) ((float) (viewWidth / 2) - (float) (viewWidth / 40)) / (float) progressMax) * (float) esaGetCnt) + (float) (viewWidth / 40)),
//				(float) (viewHeight / 80) * 4);

		// CameLog.setLog(
		// TAG,
		// "塗りつぶした横幅は"
		// + ((((viewWidth / 2) - (viewWidth / 40)) / progressMax) *
		// esaGetCnt));
		// CameLog.setLog(TAG, "プログレスバー最大値は" + progressMax + "エサ獲得数は" +
		// esaGetCnt);
		/** 進捗部分描画実行 */
//		canvas.drawRect(ratingRect, paint);
		// }
		// CameLog.setLog(TAG, "レーティング最大値は" + ratingMax + "。獲得済みエサ数は" +
		// esaGetCnt);
		/** 進捗バー外枠にテーマカラー設定 */
//		paint.setColor(themeColor);
		/** 進捗バー外枠設定 */
//		paint.setStyle(Style.STROKE);
		/**
		 * 進捗バー外枠描画実行 外枠の第3引数は開始位置分を引かないとずれる。ここではviewWidth / 40
		 * */
//		canvas.drawRect((float) viewWidth / 40, (float) viewHeight / 40,
//				(float) (viewWidth / 2), (float) (viewHeight / 80) * 4, paint);
		// CameLog.setLog(TAG, "進捗バー外枠横幅は" + ((viewWidth / 2) - (viewWidth /
		// 40)));
	}

}
