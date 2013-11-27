package com.lejoying.view;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.lejoying.mc.R;

public class CircleMenuView extends View {

	private final Paint mPaint;

	public CircleMenuView(Context context) {
		this(context, null);
	}

	public CircleMenuView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.mPaint = new Paint();
		this.mPaint.setColor(Color.rgb(255, 255, 255));
		this.mPaint.setAntiAlias(true);
		this.mPaint.setDither(true);
	}
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		canvas.drawBitmap(BitmapFactory.decodeResource(getResources(),
				R.drawable.control_disk), 0, 0, mPaint);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		return super.onTouchEvent(event);
	}

}
