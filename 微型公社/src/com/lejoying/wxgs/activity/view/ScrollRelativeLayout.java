package com.lejoying.wxgs.activity.view;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.RelativeLayout;

public class ScrollRelativeLayout extends RelativeLayout {

	public ScrollRelativeLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onDraw(Canvas canvas) {
		System.out.println("onDraw");
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		return listener != null ? listener.onTouch(this, event) : false;
	}

	OnTouchListener listener;

	@Override
	public void setOnTouchListener(OnTouchListener l) {
		listener = l;
	}

	boolean interecept;

	public void interceptTouchEvent() {
		interecept = true;
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		if (ev.getAction() == MotionEvent.ACTION_DOWN) {
			interecept = false;
		}
		onTouchEvent(ev);
		return interecept;
	}

}
