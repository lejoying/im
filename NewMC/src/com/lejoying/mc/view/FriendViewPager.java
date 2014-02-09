package com.lejoying.mc.view;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.ListView;

public class FriendViewPager extends ViewPager {

	ListView mContentListView;

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

					@Override
					public boolean onScroll(MotionEvent arg0, MotionEvent arg1,
							float arg2, float arg3) {
						System.out.println(arg2+":::"+arg3);
						return true;
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
						return true;
					}
				});
	}

	public void setContentListView(ListView listView) {
		this.mContentListView = listView;
	}

	@Override
	public boolean onTouchEvent(MotionEvent e) {		
		return mGestureDetector.onTouchEvent(e);
	}

}
