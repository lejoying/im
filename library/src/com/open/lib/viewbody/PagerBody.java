package com.open.lib.viewbody;

import java.util.ArrayList;
import java.util.List;

import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import com.facebook.rebound.SimpleSpringListener;
import com.facebook.rebound.Spring;
import com.facebook.rebound.SpringConfig;
import com.facebook.rebound.SpringSystem;

public class PagerBody {
	public String tag = "PagerBody";
	public DisplayMetrics displayMetrics;
	public Spring mSpring;
	public PagerSpringListener mPagerSpringListener;

	public SpringConfig ORIGAMI_SPRING_CONFIG = SpringConfig.fromOrigamiTensionAndFriction(200, 15);

	BodyCallback bodyCallback;

	public View initialize(DisplayMetrics displayMetrics, BodyCallback bodyCallback) {

		this.bodyCallback = bodyCallback;
		this.displayMetrics = displayMetrics;

		SpringSystem mSpringSystem = SpringSystem.create();
		mSpring = mSpringSystem.createSpring().setSpringConfig(ORIGAMI_SPRING_CONFIG);

		mPagerSpringListener = new PagerSpringListener();
		mSpring.addListener(mPagerSpringListener);

		return null;
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

	public class MyPagerItemBody {
		public View myPagerItemView = null;
		public View myPagerTitleView = null;

		public float x;
		public float y;

		public float pre_x = 0;
		public float pre_y = 0;

		public View initialize(View myPagerItemView) {
			this.myPagerItemView = myPagerItemView;
			return this.myPagerItemView;
		}
	}

	public boolean isActive = true;

	public void active() {
		Log.d(tag, "active");
		isActive = true;
		this.bodyStatus.state = this.bodyStatus.FIXED;
		this.touchStatus.state = this.touchStatus.None;
	}

	public void inActive() {
		Log.d(tag, "inActive");
		this.isActive = false;
		this.touchStatus.state = this.touchStatus.None;
	}

	public class BodyStatus {
		public int FIXED = 0, DRAGGING = 1, HOMING = 2;
		public int state = FIXED;
	}

	public BodyStatus bodyStatus = new BodyStatus();

	public class TouchStatus {
		public int None = 4, Down = 1, Horizontal = 2, Vertical = 3, Up = 4;
		public int state = None;
	}

	public TouchStatus touchStatus = new TouchStatus();

	public void render() {
		// if (isActive == false) {
		// return;
		// }
		double value = mSpring.getCurrentValue();

		if (this.touchStatus.state == this.touchStatus.Up) {
			if (this.bodyStatus.state == this.bodyStatus.HOMING) {

				double deltaX = -displayMetrics.widthPixels * value;
				this.setChildrenPosition((float) deltaX, 0);
			}
		} else {
			Log.d(tag, "render skip");
		}
	}

	public void stopChange() {
		if (isActive == false) {
			return;
		}
		if (this.bodyStatus.state == this.bodyStatus.HOMING) {
			Log.d(tag, "stopChange myPagerBody.status.FIXED");
			this.bodyStatus.state = this.bodyStatus.FIXED;
			this.pageIndex = this.nextPageIndex;

			this.bodyCallback.onFixed(tag, this.pageIndex);
		}
	}

	float touch_pre_x = 0;
	float touch_pre_y = 0;

	public void onTouchDown(MotionEvent event) {
		if (isActive == false) {
			return;
		}
		float x = event.getX();
		float y = event.getY();
		if (touchStatus.state == touchStatus.Up) {
			touchStatus.state = touchStatus.Down;
		} else {
			// Log.e(tag, "unkown status: not touchMoveStatus.Up");
		}
		touch_pre_x = x;
		touch_pre_y = y;
	}

	public void onTouchMove(MotionEvent event) {
		if (isActive == false) {
			return;
		}
		float x = event.getX();
		float y = event.getY();
		if (touchStatus.state == touchStatus.Down) {
			float deltaX = (x - touch_pre_x) * (x - touch_pre_x);
			float deltaY = (y - touch_pre_y) * (y - touch_pre_y);
			if (deltaY > 400 || deltaX > 400) {
				if (deltaY > deltaX) {
					touchStatus.state = touchStatus.Vertical;
					touch_pre_y = y;
					Log.e(tag, "Vertical moving");
				} else {
					touchStatus.state = touchStatus.Horizontal;
					if (this.bodyStatus.state == this.bodyStatus.FIXED || this.bodyStatus.state == this.bodyStatus.HOMING) {
						if (this.nextPageIndex == 0 && x - touch_pre_x > 0) {
							if (this.bodyCallback.onOverRange(tag, -1) == true) {
								return;
							}
						} else if (this.nextPageIndex == 3 && x - touch_pre_x < 0) {
							if (this.bodyCallback.onOverRange(tag, -1) == true) {
								return;
							}
						}
						this.recordChildrenPosition();
						this.bodyStatus.state = this.bodyStatus.DRAGGING;
						this.bodyCallback.onStart(tag, this.pageIndex);
					} else {
						Log.e(tag, "thisView.myPagerBody.status error: " + this.bodyStatus.state);
					}
					touch_pre_x = x;
					Log.e(tag, "Horizontal moving");
				}
			}
		} else if (touchStatus.state == touchStatus.Horizontal) {
			y = touch_pre_y;
			this.setChildrenDeltaPosition(x - touch_pre_x, 0);
		} else {
			this.onTouchDown(event);
			// Log.e(tag, "unkown status: touchMoveStatus.Up");
		}
	}

	public void onTouchUp(MotionEvent event) {
		if (isActive == false) {
			return;
		}
		this.homing();

		touchStatus.state = touchStatus.Up;
	}

	public void onFling(float velocityX, float velocityY) {
		if (isActive == false) {
			return;
		}
		if (this.bodyStatus.state == this.bodyStatus.HOMING) {
			if (velocityX * velocityX > 1000000) {
				if (velocityX > 0) {
					this.flip(-1);
				} else {
					this.flip(1);
				}
			}
		}
	}

	public List<MyPagerItemBody> childrenBodys = new ArrayList<MyPagerItemBody>();
	public ImageView pager_indicator;
	public int pager_indicator_trip = 0;

	public int pageIndex = 0;
	public int nextPageIndex = 0;
	public float pre_x = 0;
	public float x = 0;

	public float deltaX = 0;

	public void addChildView(View childView) {
		int index = childrenBodys.size();

		MyPagerItemBody childBody = new MyPagerItemBody();
		childBody.initialize(childView);
		childBody.x = index * displayMetrics.widthPixels;

		childView.setX(childBody.x);
		childView.setVisibility(View.VISIBLE);

		childrenBodys.add(childBody);
	}

	public void setTitleView(View myPagerTitleView, int index) {
		MyPagerItemBody childBody = childrenBodys.get(index);
		childBody.myPagerTitleView = myPagerTitleView;
	}

	public void recordChildrenPosition() {
		pre_x = x;
		for (MyPagerItemBody childBody : this.childrenBodys) {
			childBody.pre_x = childBody.myPagerItemView.getX();
		}
	}

	public void setChildrenDeltaPosition(float deltaX, float deltaY) {
		this.x = this.pre_x + deltaX;

		float pager_indicator_position = -(this.pre_x + deltaX) * (float) pager_indicator_trip / (float) displayMetrics.widthPixels;
		pager_indicator.setX(pager_indicator_position);

		for (MyPagerItemBody childBody : this.childrenBodys) {
			childBody.myPagerItemView.setX(childBody.pre_x + deltaX);

		}
		setTitleViewAlpha();
	}

	public void setTitleViewAlpha() {

		for (int index = 0; index < this.childrenBodys.size(); index++) {
			MyPagerItemBody childBody = this.childrenBodys.get(index);

			if (childBody.myPagerTitleView != null) {
				float alpha = (-this.x) / (float) displayMetrics.widthPixels + 0.33f - (float) index;
				childBody.myPagerTitleView.setAlpha(3.3f * alpha);
			}
		}
	}

	public void setChildrenPosition(float x, float y) {
		this.x = x;

		float pager_indicator_position = -(x) * (float) pager_indicator_trip / (float) displayMetrics.widthPixels;
		pager_indicator.setX(pager_indicator_position);

		for (MyPagerItemBody childBody : this.childrenBodys) {
			childBody.myPagerItemView.setX(childBody.x + x);
		}
		setTitleViewAlpha();
	}

	public void homing() {
		if (bodyStatus.state == bodyStatus.DRAGGING) {

			bodyStatus.state = bodyStatus.HOMING;
			nextPageIndex = Math.round(-x / displayMetrics.widthPixels);

			mSpring.setCurrentValue(-x / displayMetrics.widthPixels);

			int size = childrenBodys.size();
			if (nextPageIndex > size - 1) {
				nextPageIndex = size - 1;
			}
			if (nextPageIndex < 0) {
				nextPageIndex = 0;
			}

			mSpring.setEndValue(nextPageIndex);
			this.bodyCallback.onFlipping(tag, this.nextPageIndex);
			Log.d(tag, "homing to " + this.nextPageIndex);
		}
	}

	public void flip(int step) {
		mSpring.setCurrentValue(-x / displayMetrics.widthPixels);
		this.nextPageIndex = this.nextPageIndex + step;
		int size = childrenBodys.size();
		if (nextPageIndex > size - 1) {
			nextPageIndex = size - 1;
		}
		if (nextPageIndex < 0) {
			nextPageIndex = 0;
		}
		mSpring.setEndValue(nextPageIndex);
		this.bodyCallback.onFlipping(tag, this.nextPageIndex);

		Log.d(tag, "flip to(step) " + this.nextPageIndex);
	}

	public void flipTo(int toIndex) {
		if (toIndex != this.nextPageIndex) {
			Log.d(tag, "flipTo: " + toIndex);
			bodyStatus.state = bodyStatus.HOMING;
			mSpring.setCurrentValue(-x / displayMetrics.widthPixels);
			this.nextPageIndex = toIndex;
			int size = childrenBodys.size();
			if (nextPageIndex > size - 1) {
				nextPageIndex = size - 1;
			}
			if (nextPageIndex < 0) {
				nextPageIndex = 0;
			}
			mSpring.setEndValue(nextPageIndex);
			this.bodyCallback.onFlipping(tag, this.nextPageIndex);

			Log.d(tag, "flip to " + this.nextPageIndex);
		}
	}

	public interface OnPagerBodyChangeListener {
		public void onPageChange();
	}

	public OnPagerBodyChangeListener onPagerBodyChangeListener;

	public void setOnPagerBodyChangeListener(OnPagerBodyChangeListener onPagerBodyChangeListener) {
		this.onPagerBodyChangeListener = onPagerBodyChangeListener;
	}
}
