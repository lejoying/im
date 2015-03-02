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

public class ScrollListBody {

	public String tag = "ScrollListBody";
	public MyLog log = new MyLog(tag, false);

	public ViewGroup containerView = null;
	public DisplayMetrics displayMetrics;

	public int width;

	public int containerHeight;
	public int containerWidth;

	public View initialize(DisplayMetrics displayMetrics, View containerView) {
		this.displayMetrics = displayMetrics;

		this.containerWidth = this.displayMetrics.widthPixels;

		this.containerView = (ViewGroup) containerView;

		return containerView;

	}

	public class ScrollListItemBody {

		public boolean isVisible = true;
		public View myListItemView = null;

		public float x = 0;
		public float y = 0;

		public float offset_x = 0;
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

	public List<String> listSequence = new ArrayList<String>();
	public Map<String, ScrollListItemBody> listSequenceMap = new HashMap<String, ScrollListItemBody>();

	public class TouchState {
		public int None = 0, Down = 1, Move = 2, Up = 0, LongPress = 3, Horizontal = 4, Vertical = 5;
		public int state = None;
	}

	public TouchState touchStatus = new TouchState();

	public class BodyStatus {
		public int FIXED = 0, DRAGGING = 1, FLINGHOMING = 2, ORDERING = 3, ORDERINGHOMING = 4, BOUNDARYHOMING = 5;
		public int state = FIXED;
	}

	public BodyStatus bodyStatus = new BodyStatus();

	public float touch_pre_x, touch_pre_y;
	public float x, y;

	public void onTouchDown(MotionEvent event) {
		if (touchStatus.state == touchStatus.None || touchStatus.state == touchStatus.Up) {
			touchStatus.state = touchStatus.Down;
			recordCurrentPosition();
		}
		touch_pre_x = event.getX();
		touch_pre_y = event.getY();
	}

	public void onTouchMove(MotionEvent event) {
		float x = event.getX();
		float y = event.getY();
		if (touchStatus.state == touchStatus.Down) {
			float deltaX = (x - touch_pre_x) * (x - touch_pre_x);
			float deltaY = (y - touch_pre_y) * (y - touch_pre_y);
			if (deltaY > 400 || deltaX > 400) {
				if (deltaY > deltaX) {
					if (touchStatus.state == touchStatus.Down) {
						// this.bodyStatus.state = this.bodyStatus.DRAGGING;
					}
					touchStatus.state = touchStatus.Vertical;
					touch_pre_y = y;
				} else {
					touchStatus.state = touchStatus.Horizontal;
					touch_pre_x = x;
				}
			}
		} else if (touchStatus.state == touchStatus.Vertical) {
			x = touch_pre_x;
		} else if (touchStatus.state == touchStatus.Horizontal) {
			if (bodyStatus.state == bodyStatus.DRAGGING) {
				this.setDeltaXY(x - touch_pre_x, 0);
				this.setChildrenPosition();
				touch_pre_x = x;
			}
		} else {
			// log.e(tag, "unkown status: touchMoveStatus.Up");
		}
	}

	private void setChildrenPosition() {
		for (int i = 0; i < this.listSequence.size(); i++) {
			// String key = this.listSequence.get(i);
			// ScrollListItemBody body = this.listSequenceMap.get(key);
			// float positionY = this.x + body.x - body.offset_x;
		}
	}

	private void setDeltaXY(float deltaX, float deltaY) {
		if (this.width < this.containerWidth) {
			return;
		}
		this.x += deltaX;
		if (this.x >= 0) {
			deltaX = 0;
		} else if (this.x < this.width - this.containerWidth) {
			this.x = this.width - this.containerWidth;
		}
		// if (this.x >= 0 && deltaX > 0) {
		// deltaY = deltaY / 4;
		// } else if (this.y <= -(this.width - containerWidth) && deltaY < 0) {
		// deltaY = deltaY / 4;
		// }
		// this.y = this.y + deltaY;
	}

	public void onTouchUp(MotionEvent event) {
		// TODO Auto-generated method stub

	}

	public void recordCurrentPosition() {
		for (int i = 0; i < listSequence.size(); i++) {
			String key = listSequence.get(i);
			ScrollListItemBody body = listSequenceMap.get(key);
			body.pre_position = body.x;
			body.next_position = body.x;
		}
	}
}
