package com.lejoying.mc.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.widget.AbsListView;
import android.widget.ListView;

public class FriendListView extends ListView {

	GestureDetector mGestureDetector;

	public FriendListView(Context context) {
		super(context);
	}

	public FriendListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mGestureDetector = new GestureDetector(context, new YScrollDetector());
		this.setOnScrollListener(new OnScrollListener() {

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				// TODO Auto-generated method stub
				switch (scrollState) {
				case SCROLL_STATE_FLING:
					isFling = true;
					break;

				default:
					isFling = false;
					break;
				}
			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				// TODO Auto-generated method stub

			}
		});
	}

	public FriendListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	boolean isFling;

	

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		// TODO Auto-generated method stub
		if (ev.getAction() == MotionEvent.ACTION_DOWN
				|| ev.getAction() == MotionEvent.ACTION_UP) {
			state = 0;
		}
		boolean flag = mGestureDetector.onTouchEvent(ev);
		boolean flagSuper = super.onInterceptTouchEvent(ev);

		return (flagSuper && flag) || isFling;
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