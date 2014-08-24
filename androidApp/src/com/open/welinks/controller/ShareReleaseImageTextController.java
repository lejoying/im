package com.open.welinks.controller;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;

import com.open.welinks.ImagesDirectoryActivity;
import com.open.welinks.model.Data;
import com.open.welinks.view.ShareReleaseImageTextView;

public class ShareReleaseImageTextController {
	public Data data = Data.getInstance();
	public String tag = "ShareReleaseImageTextController";

	public Context context;
	public ShareReleaseImageTextView thisView;
	public ShareReleaseImageTextController thisController;
	public Activity thisActivity;

	public OnClickListener monClickListener;
	public OnTouchListener monOnTouchListener;

	public int RESULT_REQUESTCODE_SELECTIMAGE = 0x01;

	public ShareReleaseImageTextController(Activity thisActivity) {
		this.context = thisActivity;
		this.thisActivity = thisActivity;
	}

	public void initializeListeners() {
		monOnTouchListener = new OnTouchListener() {

			@Override
			public boolean onTouch(View view, MotionEvent event) {
				int motionEvent = event.getAction();
				if (motionEvent == MotionEvent.ACTION_DOWN) {
					view.setBackgroundColor(Color.argb(143, 0, 0, 0));
				} else if (motionEvent == MotionEvent.ACTION_UP) {
					view.setBackgroundColor(Color.parseColor("#00000000"));
				}
				return false;
			}
		};
		monClickListener = new OnClickListener() {

			@Override
			public void onClick(View view) {
				if (view == thisView.mCancleButtonView) {
					thisActivity.finish();
				} else if (view == thisView.mConfirmButtonView) {
					thisActivity.finish();
				} else if (view == thisView.mSelectImageButtonView) {
					Intent intent = new Intent(thisActivity, ImagesDirectoryActivity.class);
					thisActivity.startActivityForResult(intent, RESULT_REQUESTCODE_SELECTIMAGE);
				}
			}
		};
	}

	public void bindEvent() {
		thisView.mCancleButtonView.setOnClickListener(monClickListener);
		thisView.mConfirmButtonView.setOnClickListener(monClickListener);
		thisView.mSelectImageButtonView.setOnClickListener(monClickListener);
		thisView.mCancleButtonView.setOnTouchListener(monOnTouchListener);
		thisView.mConfirmButtonView.setOnTouchListener(monOnTouchListener);
		thisView.mSelectImageButtonView.setOnTouchListener(monOnTouchListener);

	}

	public void onActivityResult(int requestCode, int resultCode, Intent data2) {
		if (requestCode == RESULT_REQUESTCODE_SELECTIMAGE && resultCode == Activity.RESULT_OK) {
			Log.e(tag, data.tempData.selectedImageList.size() + "---------------selected image size");
			thisView.showSelectedImages();
		}
	}

	public void finish() {
		data.tempData.selectedImageList = null;
	}

	public long eventCount = 0;

	public int preTouchTimes = 5;
	public float pre_x = 0;
	public float pre_y = 0;
	long lastMillis = 0;

	public float pre_pre_x = 0;
	public float pre_pre_y = 0;
	long pre_lastMillis = 0;

	public float progress_test_x = 0;
	public float progress_test_y = 0;

	public float progress_line1_x = 0;

	public void onTouchEvent(MotionEvent event) {
		eventCount++;
		float x = event.getX();
		float y = event.getY();
		long currentMillis = System.currentTimeMillis();

		// RelativeLayout intimateFriendsContentView =
		// thisView.intimateFriendsContentView;

		if (event.getAction() == MotionEvent.ACTION_DOWN) {

			pre_x = x;
			pre_y = y;

			thisView.myScrollImageBody.recordChildrenPosition();
			// progress_test_x = intimateFriendsContentView.getX();
			// progress_test_y = intimateFriendsContentView.getY();

			if (y > 520) {

			} else {

			}
		} else if (event.getAction() == MotionEvent.ACTION_MOVE) {
			if (lastMillis == 0) {
				lastMillis = currentMillis;
			}

			// progress_test.setX(progress_test_x + x - pre_x);
			// intimateFriendsContentView.setY(progress_test_y + y - pre_y);

			// progress_line1.setX(progress_line1_x + x - pre_x);
//			Log.e(tag, (x - pre_x) + "-----------------x");
			thisView.myScrollImageBody.setChildrenPosition(x - pre_x, 0);
			// thisView.myScrollImageBody.setChildrenPosition(0, y - pre_y);

		} else if (event.getAction() == MotionEvent.ACTION_UP) {
			long delta = currentMillis - lastMillis;

			if (delta == 0 || x == pre_x || y == pre_y) {
				delta = currentMillis - pre_lastMillis;
				pre_x = pre_pre_x;
				pre_y = pre_pre_y;
			}
		}
	}
}
