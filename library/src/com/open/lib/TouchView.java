package com.open.lib;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.FrameLayout;

public class TouchView extends FrameLayout {
	public boolean isIntercept = false;

	public TouchView(Context paramContext) {
		super(paramContext);
	}

	public TouchView(Context paramContext, AttributeSet paramAttributeSet) {
		super(paramContext, paramAttributeSet);
	}

	public TouchView(Context paramContext, AttributeSet paramAttributeSet, int paramInt) {
		super(paramContext, paramAttributeSet, paramInt);
	}

	public boolean onInterceptTouchEvent(MotionEvent paramMotionEvent) {
		return this.isIntercept;
	}

	public boolean onTouchEvent(MotionEvent paramMotionEvent) {
		// if (this.isIntercept) {
		// int i = getChildCount();
		// if (--i >= 0) {
		// View localView = getChildAt(i);
		// float f1 = paramMotionEvent.getX();
		// float f2 = paramMotionEvent.getY();
		// float f3 = paramMotionEvent.getX() - localView.getLeft();
		// float f4 = paramMotionEvent.getY() - localView.getTop();
		// if (((f4 >= 0F) && (f3 >= 0F)) || ((0xFF & paramMotionEvent.getAction()) != 0))
		// paramMotionEvent.setLocation(f3, f4);
		// try {
		// localView.dispatchTouchEvent(paramMotionEvent);
		// paramMotionEvent.setLocation(f1, f2);
		// } catch (ArrayIndexOutOfBoundsException localArrayIndexOutOfBoundsException) {
		// return false;
		// }
		// }
		// }
		super.onTouchEvent(paramMotionEvent);
		return this.isIntercept;
	}

	public void setInterceptTouches(boolean paramBoolean) {
		this.isIntercept = paramBoolean;
	}
}