package com.open.welinks.customView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.open.lib.MyLog;
import com.open.lib.OpenLooper;
import com.open.lib.OpenLooper.LoopCallback;
import com.open.lib.viewbody.BodyCallback;

public class ListBody2 {
	public String tag = "ListBody2";
	public MyLog log = new MyLog(tag, false);

	public DisplayMetrics displayMetrics;

	public View initialize(DisplayMetrics displayMetrics, View containerView) {
		this.displayMetrics = displayMetrics;

		this.containerView = (ViewGroup) containerView;
		openLooper = new OpenLooper();
		openLooper.createOpenLooper();
		loopCallback = new ListLoopCallback(openLooper);
		openLooper.loopCallback = loopCallback;

		refreshBaseHeight = displayMetrics.heightPixels / 16;

		return containerView;

	}

	public class MyListItemBody {

		public boolean isVisible = true;
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
		public int FIXED = 0, DRAGGING = 1, FLINGHOMING = 2,  BOUNDARYHOMING = 5;
		public int state = FIXED;
	}

	public BodyStatus bodyStatus = new BodyStatus();

	public class TouchStatus {
		public int None = 4, Down = 1, Horizontal = 2, Vertical = 3, Up = 4;
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
			this.bodyStatus.state = this.bodyStatus.FIXED;
		}
	}

	float touch_pre_x = 0;
	float touch_pre_y = 0;

	public void onTouchDown(MotionEvent event) {
		// log.i(tag, "bodyStatus:  " + this.bodyStatus.state +
		// "     touchStatus:  " + this.touchStatus.state);
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
			// log.e(tag, "unkown status: not touchMoveStatus.Up");
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
		if (touchStatus.state == touchStatus.Down) {
			float deltaX = (x - touch_pre_x) * (x - touch_pre_x);
			float deltaY = (y - touch_pre_y) * (y - touch_pre_y);
			if (deltaY > 400 || deltaX > 400) {
				if (deltaY > deltaX) {

					if (touchStatus.state == touchStatus.Down) {
						this.bodyStatus.state = this.bodyStatus.DRAGGING;
					}

					touchStatus.state = touchStatus.Vertical;
					touch_pre_y = y;

					// log.e(tag, "Vertical moving");
				} else {
					touchStatus.state = touchStatus.Horizontal;
					touch_pre_x = x;
					// log.e(tag, "Horizontal moving");
				}
			}
		} else if (touchStatus.state == touchStatus.Vertical) {
			x = touch_pre_x;
			if (bodyStatus.state == bodyStatus.DRAGGING) {
				this.setDeltaXY(0, y - touch_pre_y);
				this.setChildrenPosition();
				touch_pre_y = y;

			} 
		} else if (touchStatus.state == touchStatus.Horizontal) {
			y = touch_pre_y;
		} else {
			// log.e(tag, "unkown status: touchMoveStatus.Up");
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
			// log.i(tag, "bodyStatus error:" + this.bodyStatus.state);
		}
	}


	public BodyCallback bodyCallback = null;



	public float x = 0;
	public float y = 0;
	public float height = 0;

	public int containerHeight = 0;

	public void setDeltaXY(float deltaX, float deltaY) {

		if (this.height < this.containerHeight) {
			return;
		}
		if (this.y >= 0 && deltaY > 0) {
			deltaY = deltaY / 4;
		} else if (this.y <= -(this.height - containerHeight) && deltaY < 0) {
			deltaY = deltaY / 4;
		}
		float y1 = this.y;
		this.y = this.y + deltaY;
		if (y1 > this.y) {
			// 下拉 TODO
			if (this.y < 50) {
				// Log.e(tag, ">>>下拉" + this.y);
			}
		} else {
			// 上拉 TODO
			if (this.y + this.height < 50) {
				// Log.e(tag, ">>>上拉" + this.y);
			}
		}
	}

	public void setChildrenPosition() {
		for (int i = 0; i < listItemsSequence.size(); i++) {
			String key = listItemsSequence.get(i);
			MyListItemBody myListItemBody = listItemBodiesMap.get(key);
			float positionY = this.y + myListItemBody.y - myListItemBody.offset_y;

			if (myListItemBody.isVisible == false) {
				myListItemBody.isVisible = true;
				myListItemBody.myListItemView.setVisibility(View.VISIBLE);
			}
			myListItemBody.myListItemView.setY(positionY);
		}
	}

	public void hideOverScreenViews() {
		for (int i = 0; i < listItemsSequence.size(); i++) {
			String key = listItemsSequence.get(i);
			MyListItemBody myListItemBody = listItemBodiesMap.get(key);
			float positionY = this.y + myListItemBody.y - myListItemBody.offset_y;

			if (positionY > this.containerHeight + 50 || positionY + myListItemBody.itemHeight < -50) {
				if (myListItemBody.isVisible == true) {
					myListItemBody.isVisible = false;
					myListItemBody.myListItemView.setVisibility(View.GONE);
				}

			}
		}
	}


	public ViewGroup containerView = null;

	public List<String> listItemsSequence = new ArrayList<String>();
	public Map<String, MyListItemBody> listItemBodiesMap = new HashMap<String, MyListItemBody>();

	public void sliding(float speedY) {
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
			if (bodyStatus.state == bodyStatus.FLINGHOMING) {
				flingHoming((float) ellapsedMillis);
			} else if (bodyStatus.state == bodyStatus.BOUNDARYHOMING) {
				boundaryHoming(OrderingMoveDirection, (float) ellapsedMillis);
			}
		}
	}

	int OrderingMoveDirection = 0;
	int OrderingMoveUp = 1;
	int OrderingMoveDown = -1;

	int refreshBaseHeight;
	boolean isRefresh = false;

	public void boundaryHoming(int direction, float delta) {
		if (this.bondary_offset > 0) {

			if (delta * this.orderSpeed * this.boundarySpeedRatio > this.bondary_offset) {
				this.setDeltaXY(0, -this.bondary_offset);
				this.bondary_offset = 0;
				bodyStatus.state = bodyStatus.FIXED;
				hideOverScreenViews();
				this.openLooper.stop();
				if (isRefresh) {
					isRefresh = false;
					if (bodyCallback != null) {
						bodyCallback.onRefresh(1);
					}
				}
			} else {
				this.setDeltaXY(0, -delta * this.orderSpeed * this.boundarySpeedRatio);
				this.bondary_offset = this.bondary_offset - delta * this.orderSpeed * this.boundarySpeedRatio;
				if (bondary_offset > refreshBaseHeight) {
					isRefresh = true;
				}
				// Log.e(tag, bondary_offset + "--11-" + refreshBaseHeight);
			}
		}

		if (this.bondary_offset < 0) {
			if (-delta * this.orderSpeed * this.boundarySpeedRatio < this.bondary_offset) {
				this.setDeltaXY(0, -this.bondary_offset);
				this.bondary_offset = 0;
				bodyStatus.state = bodyStatus.FIXED;
				hideOverScreenViews();
				this.openLooper.stop();
				// Log.e(tag, -bondary_offset + "+++" + -refreshBaseHeight);
				if (isRefresh) {
					isRefresh = false;
					if (bodyCallback != null) {
						bodyCallback.onRefresh(-1);
					}
				}
			} else {
				this.setDeltaXY(0, delta * this.orderSpeed * this.boundarySpeedRatio);
				this.bondary_offset = this.bondary_offset + delta * this.orderSpeed * this.boundarySpeedRatio;
				// if (bondary_offset < -refreshBaseHeight) {
				isRefresh = true;
				// }
				// Log.e(tag, bondary_offset + "---" + -refreshBaseHeight);
			}
		}
		this.setChildrenPosition();
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
			hideOverScreenViews();
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
