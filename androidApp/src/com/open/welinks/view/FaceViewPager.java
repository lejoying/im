package com.open.welinks.view;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;

public class FaceViewPager extends ViewPager {

	private boolean touchAble = true;

	public FaceViewPager(Context context) {
		super(context);
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent arg0) {
		if (!touchAble) {
			return false;
		} else {
			return super.onInterceptTouchEvent(arg0);
		}
	}

	@Override
	public boolean executeKeyEvent(KeyEvent event) {
		return !touchAble;
	}

	public FaceViewPager(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public void setTouchAble(boolean weather) {
		this.touchAble = weather;
	}
}
