package com.lejoying.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

public class ScanView extends View {

	private static final int[] SCANNER_ALPHA = { 0, 64, 128, 192, 255, 192,
			128, 64 };
	private static final long ANIMATION_DELAY = 80L;
	private static final int POINT_SIZE = 6;

	private final Paint paint;
	private Rect framingRect;

	private int scannerAlpha;

	public ScanView(Context context, AttributeSet attrs) {
		super(context, attrs);
		paint = new Paint(Paint.ANTI_ALIAS_FLAG);
		scannerAlpha = 0;
	}

	public void setFramingRect(Rect framingRect) {
		this.framingRect = framingRect;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		if (framingRect == null) {
			return;
		}
		int width = canvas.getWidth();
		int height = canvas.getHeight();

		int framingRectSide = framingRect.bottom - framingRect.top;

		paint.setColor(Color.argb(160, 50, 50, 50));
		canvas.drawRect(0, 0, width, framingRect.top, paint);
		canvas.drawRect(0, framingRect.top, framingRect.left,
				framingRect.bottom, paint);
		canvas.drawRect(0, framingRect.bottom, width, height, paint);
		canvas.drawRect(framingRect.right, framingRect.top, width,
				framingRect.bottom, paint);
		float textSize = framingRectSide / 15.5f;
		paint.setTextSize(textSize);
		paint.setColor(Color.rgb(140, 140, 140));
		canvas.drawText("请将二维码放入框内,即可自动扫描", framingRect.left, framingRect.top
				- textSize, paint);

		paint.setColor(Color.rgb(110, 210, 15));
		int cornerWidth = framingRectSide / 10;
		int cornerHeight = framingRectSide / 50;
		canvas.drawRect(framingRect.left, framingRect.top, framingRect.left
				+ cornerWidth, framingRect.top + cornerHeight, paint);
		canvas.drawRect(framingRect.left, framingRect.top, framingRect.left
				+ cornerHeight, framingRect.top + cornerWidth, paint);
		canvas.drawRect(framingRect.right - cornerWidth, framingRect.top,
				framingRect.right, framingRect.top + cornerHeight, paint);
		canvas.drawRect(framingRect.right - cornerHeight, framingRect.top,
				framingRect.right, framingRect.top + cornerWidth, paint);
		canvas.drawRect(framingRect.left, framingRect.bottom - cornerHeight,
				framingRect.left + cornerWidth, framingRect.bottom, paint);
		canvas.drawRect(framingRect.left, framingRect.bottom - cornerWidth,
				framingRect.left + cornerHeight, framingRect.bottom, paint);
		canvas.drawRect(framingRect.right - cornerHeight, framingRect.bottom
				- cornerWidth, framingRect.right, framingRect.bottom, paint);
		canvas.drawRect(framingRect.right - cornerWidth, framingRect.bottom
				- cornerHeight, framingRect.right, framingRect.bottom, paint);

		// Draw a red "laser scanner" line through the middle to show
		// decoding is active

		paint.setAlpha(SCANNER_ALPHA[scannerAlpha]);
		scannerAlpha = (scannerAlpha + 1) % SCANNER_ALPHA.length;
		int middle = framingRect.height() / 2 + framingRect.top;
		canvas.drawRect(framingRect.left + 2, middle - 1,
				framingRect.right - 1, middle + 2, paint);
		postInvalidateDelayed(ANIMATION_DELAY, framingRect.left - POINT_SIZE,
				framingRect.top - POINT_SIZE, framingRect.right + POINT_SIZE,
				framingRect.bottom + POINT_SIZE);

	}

}
