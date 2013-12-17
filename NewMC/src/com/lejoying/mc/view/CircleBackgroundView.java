package com.lejoying.mc.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

public class CircleBackgroundView extends View {

	private Paint mPaint;

	public CircleBackgroundView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mPaint = new Paint();
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		mPaint.setColor(Color.argb(150, 0, 0, 0));
		canvas.drawCircle(getWidth() / 2, getHeight() / 2, getWidth() / 2 - 2,
				mPaint);
	}

}
