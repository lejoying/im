package com.open.welinks.controller;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;

import com.open.lib.MyLog;
import com.open.welinks.model.Data;
import com.open.welinks.model.Data.Boards.ShareMessage;
import com.open.welinks.view.ShareDetailView;

public class ShareDetailController {

	public String tag = "ShareDetailController";
	public MyLog log = new MyLog(tag, true);

	public Data data = Data.getInstance();

	public Context context;
	public ShareDetailView thisView;
	public ShareDetailController thisController;
	public Activity thisActivity;

	public String gid;
	public String sid;
	public String gsid;

	public ShareDetailController(Activity thisActivity) {
		this.context = thisActivity;
		this.thisController = this;
		this.thisActivity = thisActivity;
	}

	public GestureDetector mGesture;

	public ShareMessage shareMessage;

	public void onCreate() {
		this.mGesture = new GestureDetector(thisActivity, new GestureListener());

		Intent intent = this.thisActivity.getIntent();
		this.gid = intent.getStringExtra("gid");
		this.sid = intent.getStringExtra("sid");
		this.gsid = intent.getStringExtra("gsid");

		if (this.sid == null || this.gsid == null) {
			throw new IllegalArgumentException("ShareDetailController:少传参数了");
		}
		this.shareMessage = data.boards.shareMessagesMap.get(this.gsid);
	}

	public class TouchStatus {
		public int None = 1, Down = 2, Horizontal = 3, Vertical = 4, Up = 1;
		public int state = None;
	}

	public TouchStatus status = new TouchStatus();

	public float touch_pre_x;
	public float touch_pre_y;

	public boolean onTouchEvent(MotionEvent event) {

		int motionEvent = event.getAction();

		float c_x = event.getX();
		float c_y = event.getY();

		if (motionEvent == MotionEvent.ACTION_DOWN) {
			log.e("ACTION_DOWN");
			if (status.state == status.None) {
				this.touch_pre_x = c_x;
				this.touch_pre_y = c_y;
				thisView.shareMessageDetailListBody.onTouchDown(event);
				status.state = status.Down;
			}
		} else if (motionEvent == MotionEvent.ACTION_MOVE) {

			float Δx = c_x - this.touch_pre_x;
			float Δy = c_y - this.touch_pre_y;

			if (status.state == status.Down) {
				log.e("Down");
				if (Δx * Δx + Δy * Δy > 400) {
					if (Δx * Δx > Δy * Δy) {
						status.state = status.Horizontal;
					} else {
						status.state = status.Vertical;
					}
					this.touch_pre_x = c_x;
					this.touch_pre_y = c_y;
				}
			} else if (status.state == status.Horizontal) {
				log.e("Horizontal");
			} else if (status.state == status.Vertical) {
				log.e("Vertical");
				thisView.shareMessageDetailListBody.onTouchMove(event);
			}
		} else if (motionEvent == MotionEvent.ACTION_UP) {
			status.state = status.Up;
			log.e("ACTION_UP");
			thisView.shareMessageDetailListBody.onTouchUp(event);
		}
		mGesture.onTouchEvent(event);
		return true;
	}

	class GestureListener extends SimpleOnGestureListener {
		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
			thisView.shareMessageDetailListBody.onFling(velocityX, velocityY);
			return true;
		}

		public void onLongPress(MotionEvent event) {

		}

		public boolean onDoubleTap(MotionEvent event) {
			return false;
		}

		public boolean onDoubleTapEvent(MotionEvent event) {
			return false;
		}

		public boolean onSingleTapUp(MotionEvent event) {
			return false;
		}

		@Override
		public boolean onSingleTapConfirmed(MotionEvent event) {

			return false;
		}

		public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {

			return false;
		}
	}
}
