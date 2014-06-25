package com.lejoying.wxgs.activity.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.EditText;

public class ReleaseEditText extends EditText {
	private Paint mPaint;

	public ReleaseEditText(Context context, AttributeSet attrs) {
		super(context, attrs);
		mPaint = new Paint();
		mPaint.setStyle(Paint.Style.STROKE);
		mPaint.setColor(Color.WHITE);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		canvas.drawLine(0, this.getHeight() - 1, this.getWidth() - 1,
				this.getHeight() - 1, mPaint);
		canvas.drawLine(this.getWidth() - 1, this.getHeight() - 50,
				this.getWidth() - 1, this.getHeight() - 1, mPaint);
		canvas.drawLine(0, this.getHeight() - 50, 0, this.getHeight() - 1,
				mPaint);
	}

}
