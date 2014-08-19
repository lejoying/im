package com.open.lib.viewbody;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.facebook.rebound.SimpleSpringListener;
import com.facebook.rebound.Spring;
import com.facebook.rebound.SpringConfig;
import com.facebook.rebound.SpringSystem;

import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

public class ListBody {
	public String tag = "ListBody";
	public DisplayMetrics displayMetrics;
	public Spring mSpring;
	public PagerSpringListener mPagerSpringListener;

	public SpringConfig ORIGAMI_SPRING_CONFIG = SpringConfig.fromOrigamiTensionAndFriction(5, 7);

	public View initialize(DisplayMetrics displayMetrics, View container) {
		this.displayMetrics = displayMetrics;

		SpringSystem mSpringSystem = SpringSystem.create();
		mSpring = mSpringSystem.createSpring().setSpringConfig(ORIGAMI_SPRING_CONFIG);

		mPagerSpringListener = new PagerSpringListener();
		mSpring.addListener(mPagerSpringListener);
		
		intimateFriendsContentView = (RelativeLayout) container;
		return intimateFriendsContentView;

	}

	public class PagerSpringListener extends SimpleSpringListener {
		@Override
		public void onSpringUpdate(Spring spring) {
			render();
		}

		@Override
		public void onSpringAtRest(Spring spring) {
			stopChange();
		}
	}

	public class MyListItemBody {
		public View myListItemView = null;

		public float x;
		public float y;

		public float pre_x = 0;
		public float pre_y = 0;

		public View initialize(View myListItemView) {
			this.myListItemView = myListItemView;
			return this.myListItemView;
		}
	}

	public class TouchStatus {
		public int None = 4, Down = 1, Horizontal = 2, Vertical = 3, Up = 4;
		public int state = None;
	}

	public TouchStatus touchStatus = new TouchStatus();

	public void render() {

		double value = mSpring.getCurrentValue();

		if (this.touchStatus.state == this.touchStatus.Up) {
		} else {
			Log.d(tag, "render skip");
		}
	}

	public void stopChange() {

	}

	float touch_pre_x = 0;
	float touch_pre_y = 0;

	public void onTouchDown(MotionEvent event) {
		float x = event.getX();
		float y = event.getY();
		if (touchStatus.state == touchStatus.Up) {
			touchStatus.state = touchStatus.Down;
		} else {
			Log.e("onTouchEvent", "unkown status: not touchMoveStatus.Up");
		}
		touch_pre_x = x;
		touch_pre_y = y;
	}

	public void onTouchMove(MotionEvent event) {
		float x = event.getX();
		float y = event.getY();
		if (touchStatus.state == touchStatus.Down) {
			if ((y - touch_pre_y) * (y - touch_pre_y) > 400 || (x - touch_pre_x) * (x - touch_pre_x) > 400) {
				if ((y - touch_pre_y) * (y - touch_pre_y) > (x - touch_pre_x) * (x - touch_pre_x)) {
					touchStatus.state = touchStatus.Vertical;
					touch_pre_y = y;
					Log.e("onTouchEvent", "开始纵向滑动");
				} else {
					touchStatus.state = touchStatus.Horizontal;
					touch_pre_x = x;
					Log.e("onTouchEvent", "开始横向滑动");
				}
			}
		} else if (touchStatus.state == touchStatus.Horizontal) {
			y = touch_pre_y;
			this.setChildrenDeltaPosition(x - touch_pre_x, 0);
		} else {
			Log.e("onTouchEvent", "unkown status: touchMoveStatus.Up");
			x = pre_x;
			y = touch_pre_y;
		}
	}

	public void onTouchUp(MotionEvent event) {
		if (touchStatus.state == touchStatus.Horizontal && (this.status.state == this.status.DRAGGING || this.status.state == this.status.FIXED)) {
		}

		touchStatus.state = touchStatus.Up;
	}

	public void onFling(float velocityX, float velocityY) {
		if (this.status.state == this.status.DRAGGING) {
		}
	}

	public List<MyListItemBody> childrenBodys = new ArrayList<MyListItemBody>();

	public float pre_x = 0;
	public float x = 0;

	public float deltaX = 0;

	public void addChildView(View childView) {
		int index = childrenBodys.size();

		MyListItemBody childBody = new MyListItemBody();
		childBody.initialize(childView);
		childBody.x = index * displayMetrics.widthPixels;

		childView.setX(childBody.x);
		childView.setVisibility(View.VISIBLE);

		childrenBodys.add(childBody);
	}

	public void setChildrenDeltaPosition(float deltaX, float deltaY) {
		this.x = this.pre_x + deltaX;

		for (MyListItemBody childBody : this.childrenBodys) {
			childBody.myListItemView.setX(childBody.pre_x + deltaX);
		}
	}

	public RelativeLayout intimateFriendsContentView = null;

	public List<String> listItemsSequence = new ArrayList<String>();
	public Map<String, MyListItemBody> listItemBodiesMap = new HashMap<String, MyListItemBody>();

	public class Status {
		public int FIXED = 0, DRAGGING = 1, INERTIAMOVING = 2;
		public int state = FIXED;
	}

	public Status status = new Status();

	public void recordChildrenPosition() {
		for (int i = 0; i < listItemsSequence.size(); i++) {
			MyListItemBody myListItemBody = listItemBodiesMap.get(listItemsSequence.get(i));
			myListItemBody.x = myListItemBody.myListItemView.getX();
			myListItemBody.y = myListItemBody.myListItemView.getY();
		}
	}

	public void setChildrenPosition(float deltaX, float deltaY) {
		for (int i = 0; i < listItemsSequence.size(); i++) {
			MyListItemBody myListItemBody = listItemBodiesMap.get(listItemsSequence.get(i));
			myListItemBody.myListItemView.setX(myListItemBody.x + deltaX);
			myListItemBody.myListItemView.setY(myListItemBody.y + deltaY);
		}
	}
}
