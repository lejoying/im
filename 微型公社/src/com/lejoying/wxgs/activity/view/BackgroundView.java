package com.lejoying.wxgs.activity.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

public class BackgroundView extends View {

	private Paint mPaint;

	private Bitmap mBackgroundBitmap;
	private Rect mSrc;
	private Rect mDst;

	public BackgroundView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mPaint = new Paint();
		mSrc = new Rect();
		mDst = new Rect();
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		if (mBackgroundBitmap != null) {
			canvas.drawBitmap(mBackgroundBitmap, mSrc, mDst, mPaint);
		}
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		if (oldw >= w && oldh >= h) {
			mDst.set(0, 0, oldw, oldh);
		} else {
			mDst.set(0, 0, w, h);
		}
		postInvalidate();
	}

	public void setBackground(int id) {
		BitmapFactory.Options options = new BitmapFactory.Options();
		mBackgroundBitmap = BitmapFactory.decodeResource(getResources(), id,
				options);
		mSrc.set(0, 0, mBackgroundBitmap.getWidth(),
				mBackgroundBitmap.getHeight());
		postInvalidate();
	}

}
