package jp.egaonohon.camerapet;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.view.View;

public class CameraOvlView extends View {

	// コンストラクタはそのまま。
	public CameraOvlView(Context context) {
		super(context);
	}

	private int width, height;

	// CameraOvlViewクラスがnewされた時点でonSizeChangedメソッドが動いてwとhが取得される。
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		width = w;
		height = h;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		// 背景色をトランスペアレントに設定
		canvas.drawColor(Color.TRANSPARENT);

		// 描画するための線
		Paint paint = new Paint();
		paint.setARGB(255, 255, 0, 0);
		paint.setStyle(Paint.Style.STROKE);

		// 表示
		int radial = height / 10;
		canvas.drawCircle(width / 2, height / 2, radial * 5, paint);
		canvas.drawCircle(width / 2, height / 2, radial * 4, paint);
		canvas.drawCircle(width / 2, height / 2, radial * 3, paint);
		canvas.drawCircle(width / 2, height / 2, radial * 2, paint);
		canvas.drawCircle(width / 2, height / 2, radial * 1, paint);
		// 縦中央
		canvas.drawLine((float) width / 2, (float) 0.0, (float) width / 2,
				(float) height, paint);
		// 横中央
		canvas.drawLine((float) 0.0, (float) height / 2, (float) width,
				(float) height / 2, paint);
		// 文字表示
		// paint.setARGB(255, 255, 255, 255);
		// paint.setStyle(Paint.Style.FILL);
		// canvas.drawText("うまく撮ってね", 0, height - 30, paint);
		// エアータグに似て非なるもの
		// canvas.drawText("エアータグ?", width - 75, 17, paint);
		// paint.setARGB(125, 100, 100, 255);
		RectF rect = new RectF(width - 80, 0, width - 5, 30);
		canvas.drawRoundRect(rect, 10, 10, paint);

	}
}
