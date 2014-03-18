package com.lejoying.wxgs.activity.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.RelativeLayout;

public class ScrollRelativeLayout extends RelativeLayout {

	public ScrollRelativeLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		return listener.onTouch(this, event);
	}

	OnTouchListener listener;

	@Override
	public void setOnTouchListener(OnTouchListener l) {
		listener = l;
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		System.out.println("intercept");
		onTouchEvent(ev);
		return false;
	}

}
