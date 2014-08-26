package com.open.lib.viewbody;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.facebook.rebound.SimpleSpringListener;
import com.facebook.rebound.Spring;
import com.facebook.rebound.SpringConfig;
import com.facebook.rebound.SpringSystem;
import com.open.lib.OpenLooper;
import com.open.lib.OpenLooper.LoopCallback;

public class ListBody {
	public String tag = "ListBody";
	public DisplayMetrics displayMetrics;
	public Spring mSpring;
	public PagerSpringListener mPagerSpringListener;

	public SpringConfig ORIGAMI_SPRING_CONFIG = SpringConfig.fromOrigamiTensionAndFriction(8, 7);

	public View initialize(DisplayMetrics displayMetrics, View containerView) {
		this.displayMetrics = displayMetrics;

		SpringSystem mSpringSystem = SpringSystem.create();
		mSpring = mSpringSystem.createSpring().setSpringConfig(ORIGAMI_SPRING_CONFIG);

		mPagerSpringListener = new PagerSpringListener();
		mSpring.addListener(mPagerSpringListener);

		this.containerView = (ViewGroup) containerView;

		openLooper = new OpenLooper();
		openLooper.createOpenLooper();
		loopCallback = new ListLoopCallback(openLooper);
		openLooper.loopCallback = loopCallback;

		return containerView;

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

		public float x = 0;
		public float y = 0;

		public float pre_x = 0;
		public float pre_y = 0;

		public View initialize(View myListItemView) {
			this.myListItemView = myListItemView;
			return this.myListItemView;
		}
	}

	public boolean isActive = true;

	public void active() {
		isActive = true;
	}

	public void inActive() {
		isActive = false;
	}

	public class BodyStatus {
		public int FIXED = 0, DRAGGING = 1, INERTIAMOVING = 2, ORDERING = 3;
		public int state = FIXED;
	}

	public BodyStatus bodyStatus = new BodyStatus();

	public class TouchStatus {
		public int None = 4, Down = 1, Horizontal = 2, Vertical = 3, Up = 4, LongPress = 5;
		public int state = None;
	}

	public TouchStatus touchStatus = new TouchStatus();

	public float speedY = 0;
	public float ratio = 0.00008f;

	public void render() {
		if (isActive == false) {
			return;
		}
		double value = mSpring.getCurrentValue();

		if (this.touchStatus.state == this.touchStatus.Up) {
			this.setChildrenPosition(0, (float) (value * displayMetrics.heightPixels));
		} else {
			Log.d(tag, "render skip");
		}
	}

	public void stopChange() {
		if (isActive == false) {
			return;
		}

		if (this.bodyStatus.state == this.bodyStatus.DRAGGING) {
			Log.d(tag, "stopChange bodyStatus.FIXED");
			this.bodyStatus.state = this.bodyStatus.FIXED;
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

	public long lastMillis;

	public void onTouchMove(MotionEvent event) {
		if (isActive == false) {
			return;
		}
		float x = event.getX();
		float y = event.getY();
		if (touchStatus.state == touchStatus.Down || touchStatus.state == touchStatus.LongPress) {
			float deltaX = (x - touch_pre_x) * (x - touch_pre_x);
			float deltaY = (y - touch_pre_y) * (y - touch_pre_y);
			if (deltaY > 400 || deltaX > 400) {
				if (deltaY > deltaX) {
					touchStatus.state = touchStatus.Vertical;
					touch_pre_y = y;

					if (touchStatus.state == touchStatus.Down) {
						this.bodyStatus.state = this.bodyStatus.DRAGGING;
					}
					this.recordChildrenPosition();

					Log.e(tag, "Vertical moving");
				} else {
					touchStatus.state = touchStatus.Horizontal;
					touch_pre_x = x;
					Log.e(tag, "Horizontal moving");
				}
			}
		} else if (touchStatus.state == touchStatus.Vertical) {
			x = touch_pre_x;
			if (bodyStatus.state == bodyStatus.DRAGGING) {
				this.setChildrenDeltaPosition(0, y - touch_pre_y);
			} else if (bodyStatus.state == bodyStatus.ORDERING) {

				if (y < this.displayMetrics.heightPixels / 3) {
					this.OrderingMoveDirection = OrderingMoveUp;
					this.openLooper.start();
				} else if (y > 2 * this.displayMetrics.heightPixels / 3) {
					this.OrderingMoveDirection = OrderingMoveDown;
					this.openLooper.start();
				} else {
					this.OrderingMoveDirection = 0;
					this.openLooper.stop();
				}

				orderingItemBody.myListItemView.setY(orderingItem_pre_y + y - touch_pre_y);

			}
		} else if (touchStatus.state == touchStatus.Horizontal) {
			y = touch_pre_y;
		} else {
			// Log.e(tag, "unkown status: touchMoveStatus.Up");
		}
	}

	public void onTouchUp(MotionEvent event) {
		if (isActive == false) {
			return;
		}
		if (this.bodyStatus.state == this.bodyStatus.DRAGGING || this.bodyStatus.state == this.bodyStatus.FIXED) {
			sliding(0);
		}
		touchStatus.state = touchStatus.Up;
	}

	public void onFling(float velocityX, float velocityY) {
		if (isActive == false) {
			return;
		}
		if (this.bodyStatus.state == this.bodyStatus.DRAGGING) {

			if (velocityY > 0) {
				this.speedY = velocityY;
				if (velocityY > 5000) {
					this.speedY = 5000;
				}
			} else if (velocityY < 0) {
				this.speedY = velocityY;
				if (velocityY < -5000) {
					this.speedY = -5000;
				}
			}
			sliding(speedY);
		}
	}

	public MyListItemBody orderingItemBody;
	public String orderingItemKey;
	public float orderingItem_pre_y = 0;

	public void onOrdering(String key) {
		orderingItemKey = key;
		this.touchStatus.state = this.touchStatus.LongPress;
		this.bodyStatus.state = this.bodyStatus.ORDERING;

		orderingItemBody = listItemBodiesMap.get(key);
		orderingItem_pre_y = orderingItemBody.y;
	}

	public float pre_x = 0;
	public float x = 0;
	public float pre_y = 0;
	public float y = 0;
	public float height = 0;

	public void setChildrenDeltaPosition(float deltaX, float deltaY) {
		this.x = this.pre_x + deltaX;
		this.y = this.pre_y + deltaY;

		for (int i = 0; i < listItemsSequence.size(); i++) {
			String key = listItemsSequence.get(i);
			if (key.equals(orderingItemKey)) {
				continue;
			}
			MyListItemBody myListItemBody = listItemBodiesMap.get(key);
			myListItemBody.myListItemView.setX(myListItemBody.pre_x + deltaX);
			myListItemBody.myListItemView.setY(myListItemBody.pre_y + deltaY);
		}
	}

	public ViewGroup containerView = null;

	public List<String> listItemsSequence = new ArrayList<String>();
	public Map<String, MyListItemBody> listItemBodiesMap = new HashMap<String, MyListItemBody>();

	public void recordChildrenPosition() {

		this.pre_x = this.x;
		this.pre_y = this.y;

		for (int i = 0; i < listItemsSequence.size(); i++) {
			MyListItemBody myListItemBody = listItemBodiesMap.get(listItemsSequence.get(i));
			myListItemBody.pre_x = myListItemBody.myListItemView.getX();
			myListItemBody.pre_y = myListItemBody.myListItemView.getY();
		}
	}

	public void setChildrenPosition(float x, float y) {
		this.x = x;
		this.y = y;
		for (int i = 0; i < listItemsSequence.size(); i++) {
			MyListItemBody myListItemBody = listItemBodiesMap.get(listItemsSequence.get(i));
			// myListItemBody.myListItemView.setX(myListItemBody.x + x);
			myListItemBody.myListItemView.setY(myListItemBody.y + y);
		}
	}

	public void sliding(float speedY) {

		double currentValue = this.y / displayMetrics.heightPixels;
		double endValue = 0;
		if (speedY > 0) {
			endValue = currentValue + this.ratio * speedY * speedY / displayMetrics.heightPixels;
		} else {
			endValue = currentValue - this.ratio * speedY * speedY / displayMetrics.heightPixels;
		}
		if (endValue > 0) {
			endValue = 0;
		}

		if (endValue < -(this.height - displayMetrics.heightPixels + 110 * displayMetrics.density) / displayMetrics.heightPixels) {
			endValue = -(this.height - displayMetrics.heightPixels + 110 * displayMetrics.density) / displayMetrics.heightPixels;
		}

		this.mSpring.setCurrentValue(currentValue);
		this.mSpring.setEndValue(endValue);
	}

	OpenLooper openLooper = null;
	LoopCallback loopCallback = null;

	public class ListLoopCallback extends LoopCallback {
		public ListLoopCallback(OpenLooper openLooper) {
			openLooper.super();
		}

		@Override
		public void loop(double ellapsedMillis) {
			orderingMove(OrderingMoveDirection, (float) ellapsedMillis);
		}
	}

	int OrderingMoveDirection = 0;
	int OrderingMoveUp = 1;
	int OrderingMoveDown = -1;

	public void orderingMove(int direction, float delta) {
		if (direction == 0) {
			return;
		}

		if (direction == OrderingMoveUp) {
			setChildrenDeltaPosition(0, delta * this.orderSpeed);

		} else if (direction == OrderingMoveDown) {
			setChildrenDeltaPosition(0, -delta * this.orderSpeed);
		}
		recordChildrenPosition();
	}

	float dxSpeed = 0;
	float dySpeed = 0;

	float orderSpeed = 0.66f;

	public void dampenSpeed(long deltaMillis) {
		if (dxSpeed != 0.0f) {
			dxSpeed *= (1.0f - 0.001f * deltaMillis);
			if (Math.abs(dxSpeed) < 0.001f)
				dxSpeed = 0.0f;
		}

		if (dySpeed != 0.0f) {
			dySpeed *= (1.0f - 0.001f * deltaMillis);
			if (Math.abs(dySpeed) < 0.001f)
				dySpeed = 0.0f;
		}
	}
}
