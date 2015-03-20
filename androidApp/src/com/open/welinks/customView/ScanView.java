package com.open.welinks.customView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.view.View;

import com.open.welink.R;

public class ScanView extends View {

	private final Paint paint;
	private Rect framingRect;

	static int SPEEN_DISTANCE = 8;
	int slideTop;
	boolean isFirst = false;

	public ScanView(Context context, AttributeSet attrs) {
		super(context, attrs);
		paint = new Paint(Paint.ANTI_ALIAS_FLAG);
		// scannerAlpha = 0;
	}

	public void setFramingRect(Rect framingRect) {
		this.framingRect = framingRect;
	}

	@SuppressLint("DrawAllocation")
	@Override
	protected void onDraw(Canvas canvas) {
		if (framingRect == null) {
			return;
		}
		if (!isFirst) {
			isFirst = true;
			slideTop = framingRect.top;
		}
		int width = canvas.getWidth() > canvas.getHeight() ? canvas.getHeight()
				: canvas.getWidth();
		int height = canvas.getWidth() < canvas.getHeight() ? canvas
				.getHeight() : canvas.getWidth();

		int framingRectSide = framingRect.bottom - framingRect.top;

		paint.setColor(Color.argb(100, 50, 50, 50));
		canvas.drawRect(0, 0, width, framingRect.top, paint);
		canvas.drawRect(0, framingRect.top, framingRect.left,
				framingRect.bottom, paint);
		canvas.drawRect(0, framingRect.bottom, width, height, paint);
		canvas.drawRect(framingRect.right, framingRect.top, width,
				framingRect.bottom, paint);

		int cornerWidth = framingRectSide / 9;
		int cornerHeight = framingRectSide / 38;

		float textSize = (framingRectSide + 2 * cornerHeight) / 15f;
		paint.setTextSize(textSize);
		paint.setColor(Color.rgb(210, 210, 210));
		canvas.drawText("", framingRect.left - cornerHeight, framingRect.top
				- (int) (1.5 * textSize), paint);

		paint.setColor(Color.rgb(255, 255, 255));
		canvas.drawRect(framingRect.left - cornerHeight, framingRect.top
				- cornerHeight, framingRect.left, framingRect.top
				- cornerHeight + cornerWidth, paint);
		canvas.drawRect(framingRect.left - cornerHeight, framingRect.top
				- cornerHeight, framingRect.left - cornerHeight + cornerWidth,
				framingRect.top, paint);
		canvas.drawRect(framingRect.right - cornerWidth + cornerHeight,
				framingRect.top - cornerHeight, framingRect.right
						+ cornerHeight, framingRect.top, paint);
		canvas.drawRect(framingRect.right, framingRect.top - cornerHeight,
				framingRect.right + cornerHeight, framingRect.top + cornerWidth
						- cornerHeight, paint);
		canvas.drawRect(framingRect.left - cornerHeight, framingRect.bottom
				- cornerWidth + cornerHeight, framingRect.left,
				framingRect.bottom + cornerHeight, paint);
		canvas.drawRect(framingRect.left - cornerHeight, framingRect.bottom,
				framingRect.left + cornerWidth - cornerHeight,
				framingRect.bottom + cornerHeight, paint);
		canvas.drawRect(framingRect.right - cornerWidth + cornerHeight,
				framingRect.bottom, framingRect.right + cornerHeight,
				framingRect.bottom + cornerHeight, paint);
		canvas.drawRect(framingRect.right, framingRect.bottom - cornerWidth
				+ cornerHeight, framingRect.right + cornerHeight,
				framingRect.bottom + cornerHeight, paint);
		slideTop += SPEEN_DISTANCE;
		if (slideTop >= framingRect.bottom) {
			slideTop = framingRect.top;
		}

		// canvas.drawRect(framingRect.left + 2, slideTop, framingRect.right
		// + cornerHeight, slideTop + 2, paint);

		Rect lineRect = new Rect();
		lineRect.left = framingRect.left;
		lineRect.right = framingRect.right + cornerHeight;
		lineRect.top = slideTop;
		lineRect.bottom = slideTop + 10;
		canvas.drawBitmap(((BitmapDrawable) (getResources()
				.getDrawable(R.drawable.qrcode_scan_line))).getBitmap(), null,
				lineRect, paint);

		postInvalidateDelayed(1L, framingRect.left, framingRect.top,
				framingRect.right, framingRect.bottom + cornerHeight);
	}
}
