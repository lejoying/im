package com.open.lib;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.RelativeLayout;

public class TouchRelativeLayoutView extends RelativeLayout {
	public boolean isIntercept = false;

	public TouchRelativeLayoutView(Context paramContext) {
		super(paramContext);
	}

	public TouchRelativeLayoutView(Context paramContext, AttributeSet paramAttributeSet) {
		super(paramContext, paramAttributeSet);
	}

	public TouchRelativeLayoutView(Context paramContext, AttributeSet paramAttributeSet, int paramInt) {
		super(paramContext, paramAttributeSet, paramInt);
	}

	public boolean onInterceptTouchEvent(MotionEvent paramMotionEvent) {
		return this.isIntercept;
	}

	public boolean onTouchEvent(MotionEvent paramMotionEvent) {
		super.onTouchEvent(paramMotionEvent);
		return this.isIntercept;
	}

	public void setInterceptTouches(boolean paramBoolean) {
		this.isIntercept = paramBoolean;
	}
}