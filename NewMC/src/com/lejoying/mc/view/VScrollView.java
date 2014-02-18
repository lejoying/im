package com.lejoying.mc.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.widget.ScrollView;

public class VScrollView extends ScrollView {

	GestureDetector mGestureDetector;

	public VScrollView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		mGestureDetector = new GestureDetector(context, new YScrollDetector());
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		// TODO Auto-generated method stub

		if (ev.getAction() == MotionEvent.ACTION_DOWN
				|| ev.getAction() == MotionEvent.ACTION_UP) {
			state = 0;
		}
		boolean flag = mGestureDetector.onTouchEvent(ev);
		boolean flagSuper = super.onInterceptTouchEvent(ev);
		return flagSuper && flag;
	}

	int state;

	final int STATE_SELF = 1;
	final int STATE_CHILD = 2;

	class YScrollDetector extends SimpleOnGestureListener {
		@Override
		public boolean onScroll(MotionEvent e1, MotionEvent e2,
				float distanceX, float distanceY) {
			boolean flag = false;
			switch (state) {
			case 0:
				if (Math.abs(distanceY) >= Math.abs(distanceX)) {
					state = STATE_SELF;
				} else if (Math.abs(distanceY) < Math.abs(distanceX)) {
					state = STATE_CHILD;
				}
			case STATE_SELF:
				flag = true;
				break;
			case STATE_CHILD:
				flag = false;
				break;
			}
			return flag;
		}

	}

}
