package com.lejoying.mc.view;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ViewParent;

public class FriendViewPager extends ViewPager {

	GestureDetector mGestureDetector;

	public FriendViewPager(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
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
					boolean isDisallowAllInteracept = false;

					@Override
					public boolean onScroll(MotionEvent arg0, MotionEvent arg1,
							float arg2, float arg3) {
						if (Math.abs(arg3) < Math.abs(arg2)) {
							if (!isDisallowAllInteracept) {
								ViewParent viewParent = FriendViewPager.this;
								while ((viewParent = viewParent.getParent()) != null) {
									viewParent
											.requestDisallowInterceptTouchEvent(true);
								}
								isDisallowAllInteracept = true;
							}
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
						// TODO Auto-generated method stub
						flag = false;
						isDisallowAllInteracept = false;
						return true;
					}

				});
	}

	@Override
	public boolean onTouchEvent(MotionEvent arg0) {
		super.onTouchEvent(arg0);
		if(arg0.getAction()==MotionEvent.ACTION_DOWN){
			ViewParent viewParent = FriendViewPager.this;
			while ((viewParent = viewParent.getParent()) != null) {
				viewParent
						.requestDisallowInterceptTouchEvent(true);
			}
		}
		return true;
	}

}
