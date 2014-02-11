package com.lejoying.mc.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.ListView;

public class FriendListView extends ListView {
	private static final int MAX_Y_OVERSCROLL_DISTANCE = 200;

	private Context mContext;
	private int mMaxYOverscrollDistance;

	GestureDetector mGestureDetector;

	public FriendListView(Context context) {
		super(context);
		mContext = context;
		initBounceListView();
	}

	public FriendListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
		initBounceListView();

		mGestureDetector = new GestureDetector(context,
				new GestureDetector.OnGestureListener() {

					@Override
					public boolean onSingleTapUp(MotionEvent arg0) {
						// TODO Auto-generated method stub
						return false;
					}

					@Override
					public void onShowPress(MotionEvent arg0) {
						// TODO Auto-generated method stub

					}

					boolean flag = false;

					@Override
					public boolean onScroll(MotionEvent arg0, MotionEvent arg1,
							float arg2, float arg3) {
						if (Math.abs(arg3) > Math.abs(arg2)) {
							flag = true;
						}
						return flag;
					}

					@Override
					public void onLongPress(MotionEvent arg0) {
						// TODO Auto-generated method stub

					}

					@Override
					public boolean onFling(MotionEvent arg0, MotionEvent arg1,
							float arg2, float arg3) {
						// TODO Auto-generated method stub
						return false;
					}

					@Override
					public boolean onDown(MotionEvent arg0) {
						flag = false;
						return true;
					}

				});
	}

	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		super.onTouchEvent(ev);
		return true;
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		// TODO Auto-generated method stub
		super.onInterceptTouchEvent(ev);
		System.out.println("intercept");
		return true;
	}

	private int pointToPosition(float x, float y) {
		// TODO Auto-generated method stub
		return pointToPosition((int) x, (int) y);
	}

	public FriendListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		mContext = context;
		initBounceListView();
	}

	private void initBounceListView() {
		// get the density of the screen and do some maths with it on the max
		// overscroll distance
		// variable so that you get similar behaviors no matter what the screen
		// size

		final DisplayMetrics metrics = mContext.getResources()
				.getDisplayMetrics();
		final float density = metrics.density;

		mMaxYOverscrollDistance = (int) (density * MAX_Y_OVERSCROLL_DISTANCE);
	}

	// @SuppressLint("NewApi")
	// @Override
	// protected boolean overScrollBy(int deltaX, int deltaY, int scrollX,
	// int scrollY, int scrollRangeX, int scrollRangeY,
	// int maxOverScrollX, int maxOverScrollY, boolean isTouchEvent) {
	// // This is where the magic happens, we have replaced the incoming
	// // maxOverScrollY with our own custom variable mMaxYOverscrollDistance;
	// return super.overScrollBy(deltaX, deltaY, scrollX, scrollY,
	// scrollRangeX, scrollRangeY, maxOverScrollX,
	// mMaxYOverscrollDistance, isTouchEvent);
	// }

}