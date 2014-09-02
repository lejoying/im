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

public class ListBody1 {
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

		public float offset_y = 0;

		public float itemWidth = 0;
		public float itemHeight = 0;

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
		public int FIXED = 0, DRAGGING = 1, HOMINGMOVING = 2, ORDERING = 3;
		public int state = FIXED;
	}

	public BodyStatus bodyStatus = new BodyStatus();

	public class TouchStatus {
		public int None = 4, Down = 1, Horizontal = 2, Vertical = 3, Up = 4, LongPress = 5;
		public int state = None;
	}

	public TouchStatus touchStatus = new TouchStatus();

	public float speedY = 0;
	public float ratio = 0.0008f;

	public void render() {
		if (isActive == false) {
			return;
		}
		double value = mSpring.getCurrentValue();

		if (this.touchStatus.state == this.touchStatus.Up) {
			// this.setChildrenPosition(0, (float) (value * displayMetrics.heightPixels));
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
		Log.i(tag, "bodyStatus:  " + this.bodyStatus.state + "     touchStatus:  " + this.touchStatus.state);
		if (isActive == false) {
			return;
		}
		float x = event.getX();
		float y = event.getY();

		if (touchStatus.state == touchStatus.Up) {
			touchStatus.state = touchStatus.Down;
			this.openLooper.stop();
		} else {
			// Log.e(tag, "unkown status: not touchMoveStatus.Up");
		}

		touch_pre_x = x;
		touch_pre_y = y;
	}

	public long lastMillis = 0;

	public void onTouchMove(MotionEvent event) {
		if (isActive == false) {
			return;
		}
		float x = event.getX();
		float y = event.getY();
		// Log.i(tag, "this.bodyStatus.state:  " + this.bodyStatus.state);
		// Log.i(tag, "this.touchStatus.state:  " + this.touchStatus.state);
		if (touchStatus.state == touchStatus.Down || touchStatus.state == touchStatus.LongPress) {
			float deltaX = (x - touch_pre_x) * (x - touch_pre_x);
			float deltaY = (y - touch_pre_y) * (y - touch_pre_y);
			if (deltaY > 400 || deltaX > 400) {
				if (deltaY > deltaX) {

					if (touchStatus.state == touchStatus.Down) {
						Log.i(tag, "this.bodyStatus.state:  this.bodyStatus.DRAGGING" + this.bodyStatus.state);
						this.bodyStatus.state = this.bodyStatus.DRAGGING;
					}

					touchStatus.state = touchStatus.Vertical;
					touch_pre_y = y;

					// this.recordChildrenPosition();

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
				this.setChildrenDeltaXY(0, y - touch_pre_y);
				this.setChildrenPosition();
				touch_pre_y = y;

			} else if (bodyStatus.state == bodyStatus.ORDERING) {

				// Log.i(tag, "this.y:  " + this.y);
				if (y < this.displayMetrics.heightPixels / 3) {
					if (this.y < 0) {
						this.OrderingMoveDirection = OrderingMoveUp;
						this.openLooper.start();
					}

				} else if (y > 2 * this.displayMetrics.heightPixels / 3) {
					if (this.y > -(this.height - 2 * 260 * displayMetrics.density)) {
						this.OrderingMoveDirection = OrderingMoveDown;
						this.openLooper.start();
					}

				} else {
					this.OrderingMoveDirection = 0;
					this.openLooper.stop();
				}

				orderingItemBody.offset_y = orderingItemBody.offset_y - (y - touch_pre_y);
				orderingItemBody.myListItemView.setY(orderingItemBody.y - orderingItemBody.offset_y);
				touch_pre_y = y;

				computeOffset();
				setChildrenPosition();

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
			// sliding(0);
		} else if (this.bodyStatus.state == this.bodyStatus.ORDERING) {

			this.bodyStatus.state = this.bodyStatus.FIXED;
		}
		touchStatus.state = touchStatus.Up;
	}

	public void onFling(float velocityX, float velocityY) {
		if (isActive == false) {
			return;
		}
		if (this.bodyStatus.state == this.bodyStatus.DRAGGING || this.bodyStatus.state == this.bodyStatus.FIXED) {

			if (velocityY > 0) {
				this.speedY = velocityY;
				if (velocityY > 10000) {
					this.speedY = 10000;
				}
			} else if (velocityY < 0) {
				this.speedY = velocityY;
				if (velocityY < -10000) {
					this.speedY = -10000;
				}
			}
			sliding(speedY);
		} else {
			Log.i(tag, "bodyStatus error:" + this.bodyStatus.state);
		}
	}

	public MyListItemBody orderingItemBody;
	public String orderingItemKey;

	public void onOrdering(String key) {
		Log.e(tag, "LongPress");
		this.orderingItemKey = key;
		this.touchStatus.state = this.touchStatus.LongPress;
		this.bodyStatus.state = this.bodyStatus.ORDERING;

		orderingItemBody = listItemBodiesMap.get(key);
	}

	public void onStopOrdering() {
		this.touchStatus.state = this.touchStatus.Up;
		this.bodyStatus.state = this.bodyStatus.FIXED;
		this.resolveOffset();
		this.orderingItemKey = null;
		this.orderingItemBody = null;
	}

	public float x = 0;
	public float y = 0;
	public float height = 0;

	public void setChildrenDeltaXY(float deltaX, float deltaY) {
		this.x = this.x + deltaX;
		this.y = this.y + deltaY;

		for (int i = 0; i < listItemsSequence.size(); i++) {
			String key = listItemsSequence.get(i);
			if (key.equals(orderingItemKey)) {
				continue;
			}
			MyListItemBody myListItemBody = listItemBodiesMap.get(key);
			myListItemBody.y = myListItemBody.y + deltaY;
		}
	}

	public void setChildrenPosition() {
		for (int i = 0; i < listItemsSequence.size(); i++) {
			String key = listItemsSequence.get(i);
			if (key.equals(orderingItemKey)) {
				continue;
			}
			MyListItemBody myListItemBody = listItemBodiesMap.get(key);
			myListItemBody.myListItemView.setY(myListItemBody.y - myListItemBody.offset_y);
		}
	}

	public void resolveOffset() {
		for (int i = 0; i < listItemsSequence.size(); i++) {
			String key = listItemsSequence.get(i);
			MyListItemBody myListItemBody = listItemBodiesMap.get(key);
			myListItemBody.y = myListItemBody.y - myListItemBody.offset_y;
			myListItemBody.offset_y = 0;
		}
	}

	public ViewGroup containerView = null;

	public List<String> listItemsSequence = new ArrayList<String>();
	public Map<String, MyListItemBody> listItemBodiesMap = new HashMap<String, MyListItemBody>();


	public void sliding(float speedY) {
		Log.i(tag, "sliding:  " + speedY);
		this.dySpeed = speedY;
		bodyStatus.state = bodyStatus.HOMINGMOVING;
		lastMillis = System.currentTimeMillis();
		this.openLooper.start();
	}

	OpenLooper openLooper = null;
	LoopCallback loopCallback = null;

	public class ListLoopCallback extends LoopCallback {
		public ListLoopCallback(OpenLooper openLooper) {
			openLooper.super();
		}

		@Override
		public void loop(double ellapsedMillis) {
			if (bodyStatus.state == bodyStatus.ORDERING) {
				orderingMove(OrderingMoveDirection, (float) ellapsedMillis);
			} else if (bodyStatus.state == bodyStatus.HOMINGMOVING) {
				homingMove((float) ellapsedMillis);
			}
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
			setChildrenDeltaXY(0, delta * this.orderSpeed);

		} else if (direction == OrderingMoveDown) {
			setChildrenDeltaXY(0, -delta * this.orderSpeed);
		}
		if (this.y > 0 || this.y < -(this.height - 2 * 260 * displayMetrics.density)) {
			this.openLooper.stop();
		}
		computeOffset();
		this.setChildrenPosition();
	}

	public void computeOffset() {
		float y = orderingItemBody.y - orderingItemBody.offset_y;
		float itemHeight = orderingItemBody.itemHeight;

		for (int i = 0; i < listItemsSequence.size(); i++) {
			String key = listItemsSequence.get(i);
			MyListItemBody myListItemBody = listItemBodiesMap.get(key);
			if (orderingItemBody.offset_y > 0 && myListItemBody.y < y && myListItemBody.y + myListItemBody.itemHeight > y) {
				float ratio = (y - (myListItemBody.y + myListItemBody.itemHeight)) / myListItemBody.itemHeight;
				if (ratio > 1) {
					ratio = 1;
				}
				if (ratio < -1) {
					ratio = -1;
				}

				myListItemBody.offset_y = ratio * itemHeight;
				// Log.d(tag, "myListItemBody.offset_y:  " + myListItemBody.offset_y);

			} else if (orderingItemBody.offset_y < 0 && myListItemBody.y + myListItemBody.itemHeight - itemHeight > y && y + itemHeight > myListItemBody.y) {
				float ratio = (y + itemHeight - myListItemBody.y) / myListItemBody.itemHeight;
				if (ratio > 1) {
					ratio = 1;
				}
				if (ratio < -1) {
					ratio = -1;
				}
				// Log.i(tag, "ratio:  " + ratio);

				myListItemBody.offset_y = ratio * itemHeight;
				// Log.d(tag, "myListItemBody.offset_y:  " + myListItemBody.offset_y);
			}
		}

	}

	float dxSpeed = 0;
	float dySpeed = 0;

	float orderSpeed = 0.66f;

	public void homingMove(float delta1) {
		long currentMillis = System.currentTimeMillis();

		if (lastMillis != 0) {
			long delta = currentMillis - lastMillis;
			dampenSpeed(delta);
			setChildrenDeltaXY(0, this.ratio * delta * this.dySpeed);
			this.setChildrenPosition();
		}

		lastMillis = currentMillis;

		if (this.dySpeed == 0) {
			bodyStatus.state = bodyStatus.FIXED;
			this.openLooper.stop();
		}
	}

	public void dampenSpeed(long deltaMillis) {

		if (dySpeed != 0.0f) {
			dySpeed *= (1.0f - 0.002f * deltaMillis);
			if (Math.abs(dySpeed) < 50f)
				dySpeed = 0.0f;
		}
	}
}
