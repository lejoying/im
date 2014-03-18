package com.lejoying.wxgs.activity.view;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class CommonViewPager extends ViewPager {

	public CommonViewPager(final Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent arg0) {
		// TODO Auto-generated method stub
		super.onInterceptTouchEvent(arg0);
		return false;
	}
}
