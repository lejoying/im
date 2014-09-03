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

import com.open.lib.OpenLooper;
import com.open.lib.OpenLooper.LoopCallback;

public class ListBody1 {
	public String tag = "ListBody";
	public DisplayMetrics displayMetrics;

	public View initialize(DisplayMetrics displayMetrics, View containerView) {
		this.displayMetrics = displayMetrics;

		this.containerView = (ViewGroup) containerView;
		openLooper = new OpenLooper();
		openLooper.createOpenLooper();
		loopCallback = new ListLoopCallback(openLooper);
		openLooper.loopCallback = loopCallback;

		return containerView;

	}

	public class MyListItemBody {
		public View myListItemView = null;

		public float x = 0;
		public float y = 0;

		public float offset_y = 0;

		public float pre_position = 0;
		public float next_position = 0;

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
		public int FIXED = 0, DRAGGING = 1, FLINGHOMING = 2, ORDERING = 3, ORDERINGHOMING = 4, BOUNDARYHOMING = 5;
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
			this.bodyStatus.state = this.bodyStatus.FIXED;
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
				this.setDeltaXY(0, y - touch_pre_y);
				this.setChildrenPosition();
				touch_pre_y = y;

			} else if (bodyStatus.state == bodyStatus.ORDERING) {

				// Log.i(tag, "this.y:  " + this.y);
				if (y < this.displayMetrics.heightPixels / 3 && (y - touch_pre_y) < 0) {
					if (this.y < 0) {
						this.OrderingMoveDirection = OrderingMoveUp;
						this.openLooper.start();
					}

				} else if (y > 2 * this.displayMetrics.heightPixels / 3 && (y - touch_pre_y) > 0) {
					if (this.y > -(this.height - containerHeight)) {
						this.OrderingMoveDirection = OrderingMoveDown;
						this.openLooper.start();
						
						Log.i(tag, "openLooper.start() " + this.y);
					}

				} else {
					this.OrderingMoveDirection = 0;
					this.openLooper.stop();
				}

				orderingItemBody.offset_y = orderingItemBody.offset_y - (y - touch_pre_y);
				orderingItemBody.myListItemView.setY(this.y + orderingItemBody.y - orderingItemBody.offset_y);
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

	public float bondary_offset = 0;

	public void onTouchUp(MotionEvent event) {
		if (isActive == false) {
			return;
		}
		if (this.bodyStatus.state == this.bodyStatus.DRAGGING) {
			if (this.y >= 0) {
				bondary_offset = this.y;
				this.bodyStatus.state = this.bodyStatus.BOUNDARYHOMING;
				boundarySpeedRatio = 3;
				this.openLooper.start();
			} else if (this.y <= -(this.height - containerHeight)) {
				bondary_offset = this.y + (this.height - containerHeight);
				this.bodyStatus.state = this.bodyStatus.BOUNDARYHOMING;
				boundarySpeedRatio = 3;
				this.openLooper.start();
			}

		} else if (this.bodyStatus.state == this.bodyStatus.ORDERING) {

			this.bodyStatus.state = this.bodyStatus.ORDERINGHOMING;
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
		if (this.bodyStatus.state == this.bodyStatus.FIXED) {
			this.orderingItemKey = key;
			this.touchStatus.state = this.touchStatus.LongPress;
			this.bodyStatus.state = this.bodyStatus.ORDERING;

			orderingItemBody = listItemBodiesMap.get(key);
			resolveNextPosition();
		} else {
			Log.i(tag, "this.bodyStatus.state:  " + this.bodyStatus.state);
		}
	}

	public void onStopOrdering() {
		if (this.bodyStatus.state == this.bodyStatus.ORDERING || this.bodyStatus.state == this.bodyStatus.ORDERINGHOMING) {
			this.touchStatus.state = this.touchStatus.Up;
			this.bodyStatus.state = this.bodyStatus.ORDERINGHOMING;

			this.orderingItemKey = null;
			this.orderingItemBody = null;
			this.resolveOffset();
		}
	}

	public float x = 0;
	public float y = 0;
	public float height = 0;

	public int containerHeight = 0;

	public void setDeltaXY(float deltaX, float deltaY) {
		if (this.y >= 0 && deltaY > 0) {
			deltaY = deltaY / 4;
		} else if (this.y <= -(this.height - containerHeight) && deltaY < 0) {
			deltaY = deltaY / 4;
		}

		this.y = this.y + deltaY;
	}

	// public void setChildrenDeltaXY(float deltaX, float deltaY) {
	// for (int i = 0; i < listItemsSequence.size(); i++) {
	// String key = listItemsSequence.get(i);
	// if (key.equals(orderingItemKey)) {
	// continue;
	// }
	// MyListItemBody myListItemBody = listItemBodiesMap.get(key);
	// myListItemBody.y = myListItemBody.y + deltaY;
	// }
	// }

	public void setChildrenDeltaNextPosition(float deltaX, float deltaY) {

		if (this.y >= 0 && deltaY > 0) {
			deltaY = deltaY / 4;
		} else if (this.y <= -(this.height - containerHeight) && deltaY < 0) {
			deltaY = deltaY / 4;
		}

		for (int i = 0; i < listItemsSequence.size(); i++) {
			String key = listItemsSequence.get(i);
			if (key.equals(orderingItemKey)) {
				continue;
			}
			MyListItemBody myListItemBody = listItemBodiesMap.get(key);
			myListItemBody.next_position = myListItemBody.next_position + deltaY;
		}
	}

	public void setChildrenPosition() {
		for (int i = 0; i < listItemsSequence.size(); i++) {
			String key = listItemsSequence.get(i);
			if (key.equals(orderingItemKey)) {
				continue;
			}
			MyListItemBody myListItemBody = listItemBodiesMap.get(key);
			myListItemBody.myListItemView.setY(this.y + myListItemBody.y - myListItemBody.offset_y);
		}
	}

	public void resolveNextPosition() {
		for (int i = 0; i < listItemsSequence.size(); i++) {
			String key = listItemsSequence.get(i);
			MyListItemBody myListItemBody = listItemBodiesMap.get(key);
			myListItemBody.next_position = myListItemBody.y;
			myListItemBody.pre_position = myListItemBody.y;
		}

	}

	public void resolveOffset() {
		for (int i = 0; i < listItemsSequence.size(); i++) {
			String key = listItemsSequence.get(i);
			MyListItemBody myListItemBody = listItemBodiesMap.get(key);
			myListItemBody.y = myListItemBody.y - myListItemBody.offset_y;
			myListItemBody.offset_y = 0;

		}
		setChildrenPosition();
		this.openLooper.start();
		Log.d(tag, "resolveOffset");

	}

	public ViewGroup containerView = null;

	public List<String> listItemsSequence = new ArrayList<String>();
	public Map<String, MyListItemBody> listItemBodiesMap = new HashMap<String, MyListItemBody>();

	public void sliding(float speedY) {
		Log.i(tag, "sliding:  " + speedY);
		this.dySpeed = speedY;
		bodyStatus.state = bodyStatus.FLINGHOMING;
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
			} else if (bodyStatus.state == bodyStatus.FLINGHOMING) {
				flingHoming((float) ellapsedMillis);
			} else if (bodyStatus.state == bodyStatus.ORDERINGHOMING) {
				orderingHoming(OrderingMoveDirection, (float) ellapsedMillis);
			} else if (bodyStatus.state == bodyStatus.BOUNDARYHOMING) {
				boundaryHoming(OrderingMoveDirection, (float) ellapsedMillis);
			}
		}
	}

	int OrderingMoveDirection = 0;
	int OrderingMoveUp = 1;
	int OrderingMoveDown = -1;

	public void boundaryHoming(int direction, float delta) {
		if (this.bondary_offset > 0) {
			if (delta * this.orderSpeed * this.boundarySpeedRatio > this.bondary_offset) {
				this.setDeltaXY(0, -this.bondary_offset);
				this.bondary_offset = 0;
				bodyStatus.state = bodyStatus.FIXED;
				this.openLooper.stop();
			} else {
				this.setDeltaXY(0, -delta * this.orderSpeed * this.boundarySpeedRatio);
				this.bondary_offset = this.bondary_offset - delta * this.orderSpeed * this.boundarySpeedRatio;
			}
		}

		if (this.bondary_offset < 0) {
			if (-delta * this.orderSpeed * this.boundarySpeedRatio < this.bondary_offset) {
				this.setDeltaXY(0, -this.bondary_offset);
				this.bondary_offset = 0;
				bodyStatus.state = bodyStatus.FIXED;
				this.openLooper.stop();
			} else {
				this.setDeltaXY(0, delta * this.orderSpeed * this.boundarySpeedRatio);
				this.bondary_offset = this.bondary_offset + delta * this.orderSpeed * this.boundarySpeedRatio;
			}
		}
		this.setChildrenPosition();
	}

	public void orderingMove(int direction, float delta) {
		if (direction == 0) {
			return;
		}

		float distance = (float) direction * delta * this.orderSpeed;

		if (this.y + distance >= 0) {
			distance = -this.y;
			this.openLooper.stop();
		} else if (this.y + distance <= -(this.height - containerHeight)) {
			distance = -this.y - (this.height - containerHeight);
			this.openLooper.stop();
		}
		setDeltaXY(0, distance);
		orderingItemBody.offset_y = orderingItemBody.offset_y + distance;

		computeOffset();
		this.setChildrenPosition();

	}

	public void orderingHoming(int direction, float delta) {

		if (bodyStatus.state != bodyStatus.ORDERINGHOMING) {
			return;
		}
		boolean ifStop = true;
		for (int i = 0; i < listItemsSequence.size(); i++) {
			String key = listItemsSequence.get(i);
			MyListItemBody myListItemBody = listItemBodiesMap.get(key);
			if (Math.abs(myListItemBody.y - myListItemBody.next_position) < delta * this.orderSpeed) {
				myListItemBody.y = myListItemBody.next_position;
				continue;
			}

			if (myListItemBody.y < myListItemBody.next_position) {
				myListItemBody.y = myListItemBody.y + delta * this.orderSpeed;
				// myListItemBody.next_position = myListItemBody.next_position - delta * this.orderSpeed / 2;
			} else {
				myListItemBody.y = myListItemBody.y - delta * this.orderSpeed;
				// myListItemBody.next_position = myListItemBody.next_position + delta * this.orderSpeed / 2;
			}
			ifStop = false;
		}
		this.setChildrenPosition();

		if (ifStop) {
			bodyStatus.state = bodyStatus.FIXED;
			this.openLooper.stop();
		}
	}

	public void computeOffset() {
		float ordering_itemHeight = orderingItemBody.itemHeight;

		float ordering_itemTop = orderingItemBody.y - orderingItemBody.offset_y;
		float ordering_itemBottom = ordering_itemTop + orderingItemBody.itemHeight;

		float ordering_itemTop_pre = orderingItemBody.pre_position;
		float ordering_itemBottom_pre = ordering_itemTop_pre + orderingItemBody.itemHeight;

		for (int i = 0; i < listItemsSequence.size(); i++) {
			String key = listItemsSequence.get(i);
			if (key.equals(orderingItemKey)) {
				continue;
			}
			MyListItemBody myListItemBody = listItemBodiesMap.get(key);

			float itemTop = myListItemBody.y;
			float itemBottom = itemTop + myListItemBody.itemHeight;

			float itemHeight = myListItemBody.itemHeight;

			if (myListItemBody.next_position == 0) {
				myListItemBody.next_position = myListItemBody.y;
			}

			float ratio = 0;

			if (itemTop < ordering_itemTop && ordering_itemTop < itemBottom && myListItemBody.offset_y <= 0) {
				ratio = (ordering_itemTop - itemBottom) / itemHeight;
			} else if (itemTop < ordering_itemBottom && ordering_itemBottom < itemBottom && myListItemBody.offset_y >= 0) {
				ratio = (ordering_itemBottom - itemTop) / itemHeight;
			} else {
				if (ordering_itemTop_pre > itemTop && ordering_itemTop > itemTop) {
					ratio = 0;
				} else if (ordering_itemTop_pre > itemTop && ordering_itemTop < itemTop) {
					ratio = -1;
				} else if (ordering_itemTop_pre < itemTop && ordering_itemTop < itemTop) {
					ratio = 0;
				} else if (ordering_itemTop_pre < itemTop && ordering_itemTop > itemTop) {
					ratio = 1;
				}
				myListItemBody.offset_y = ratio * ordering_itemHeight;
				continue;
			}

			if (ratio > 1) {
				ratio = 1;
			}
			if (ratio < -1) {
				ratio = -1;
			}

			if (Math.abs(ratio) < 0.5) {
				if (ratio > 0) {
					myListItemBody.next_position = itemTop;
					orderingItemBody.next_position = itemTop - ordering_itemHeight;
				} else {
					myListItemBody.next_position = itemTop;
					orderingItemBody.next_position = itemBottom;
				}

			} else {
				if (ratio > 0) {
					myListItemBody.next_position = myListItemBody.y - ordering_itemHeight;
					orderingItemBody.next_position = itemTop;
				} else {
					myListItemBody.next_position = myListItemBody.y + ordering_itemHeight;
					orderingItemBody.next_position = itemTop;
				}

			}

			myListItemBody.offset_y = ratio * ordering_itemHeight;
		}

	}

	float dxSpeed = 0;
	float dySpeed = 0;

	float orderSpeed = 0.46f;
	float boundarySpeedRatio = 3;
	int outOfBandaryTimes = 0;

	public void flingHoming(float delta1) {
		long currentMillis = System.currentTimeMillis();

		if (lastMillis != 0) {
			long delta = currentMillis - lastMillis;
			dampenSpeed(delta);
			setDeltaXY(0, this.ratio * delta * this.dySpeed);
			this.setChildrenPosition();
		}

		if (this.y >= 0) {
			outOfBandaryTimes++;
		} else if (this.y <= -(this.height - containerHeight)) {
			outOfBandaryTimes++;
		}
		if (outOfBandaryTimes > 3) {
			if (this.y >= 0) {
				bondary_offset = this.y;
			} else if (this.y <= -(this.height - containerHeight)) {
				bondary_offset = this.y + (this.height - containerHeight);
			}
			boundarySpeedRatio = Math.abs(bondary_offset) / 60;
			this.bodyStatus.state = this.bodyStatus.BOUNDARYHOMING;

			outOfBandaryTimes = 0;
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
