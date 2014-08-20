package com.open.welinks.controller;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Toast;

import com.facebook.rebound.BaseSpringSystem;
import com.facebook.rebound.SimpleSpringListener;
import com.facebook.rebound.Spring;
import com.facebook.rebound.SpringSystem;
import com.facebook.rebound.SpringUtil;
import com.google.gson.Gson;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.client.HttpRequest;
import com.open.welinks.model.Data;
import com.open.welinks.model.ResponseHandlers;
import com.open.welinks.utils.NetworkHandler;
import com.open.welinks.view.UserIntimateView;

public class UserIntimateController {

	public Data data = Data.getInstance();
	public String tag = "UserIntimateController";
	public UserIntimateView thisView;
	public Context context;
	public Activity thisActivity;

	public GestureDetector mGesture;

	public OnClickListener mOnClickListener;
	public OnTouchListener onTouchListener;

	NetworkHandler mNetworkHandler = NetworkHandler.getInstance();
	Handler handler = new Handler();
	String url_userInfomation = "http://www.we-links.com/api2/account/getuserinfomation";
	String url_intimateFriends = "http://www.we-links.com/api2/relation/intimatefriends";

	Gson gson = new Gson();

	public String userPhone;

	public BaseSpringSystem mSpringSystem = SpringSystem.create();
	public ExampleSpringListener mSpringListener = new ExampleSpringListener();
	public Spring mScaleSpring = mSpringSystem.createSpring();

	public void oncreate() {
		String phone = thisActivity.getIntent().getStringExtra("phone");
		if (phone != null && !"".equals(phone)) {
			userPhone = phone;
		}
		mGesture = new GestureDetector(thisActivity, new GestureListener());

		thisView.showCircles();
		// this.test();
	}

	public void onResume() {
		mScaleSpring.addListener(mSpringListener);
	}

	public void onPause() {
		mScaleSpring.removeListener(mSpringListener);
	}

	private class ExampleSpringListener extends SimpleSpringListener {
		@Override
		public void onSpringUpdate(Spring spring) {
			float mappedValue = (float) SpringUtil.mapValueFromRangeToRange(spring.getCurrentValue(), 0, 1, 1, 0.5);
			thisView.mAppIconToNameView.setScaleX(mappedValue);
			thisView.mAppIconToNameView.setScaleY(mappedValue);
		}
	}

	public void test() {

		RequestParams params = new RequestParams();
		params.addBodyParameter("accessKey", "lejoying");
		params.addBodyParameter("phone", "15120088197");

		HttpUtils http = new HttpUtils();
		ResponseHandlers responseHandlers = ResponseHandlers.getInstance();

		String url2 = "http://192.168.1.91/api2/relation/intimatefriends";
		http.send(HttpRequest.HttpMethod.POST, url2, params, responseHandlers.getIntimateFriends);
	}

	public UserIntimateController(Activity thisActivity) {
		this.context = thisActivity;
		this.thisActivity = thisActivity;

	}

	public void initializeListeners() {
		onTouchListener = new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				int action = event.getAction();
				if (action == MotionEvent.ACTION_DOWN) {
					mScaleSpring.setEndValue(1);
				} else if (action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_CANCEL) {
					mScaleSpring.setEndValue(0);
				}
				return true;
			}
		};
		mOnClickListener = new OnClickListener() {

			@Override
			public void onClick(View view) {
				if (view.equals(thisView.chatMessagesListMenuOptionView)) {
					thisView.myPagerBody.flipTo(0);
				} else if (view.equals(thisView.intimateFriendsMenuOptionView)) {
					thisView.myPagerBody.flipTo(1);
				} else if (view.equals(thisView.userInfomationMenuOptionView)) {
					thisView.myPagerBody.flipTo(2);
				} else if (view.getTag() != null) {
					Log.d(tag, (String) view.getTag());
				}
			}
		};
	}

	public void bindEvent() {
		thisView.intimateFriendsMenuOptionView.setOnClickListener(mOnClickListener);
		thisView.chatMessagesListMenuOptionView.setOnClickListener(mOnClickListener);
		thisView.userInfomationMenuOptionView.setOnClickListener(mOnClickListener);
		thisView.mRootView.setOnTouchListener(onTouchListener);
	}

	public class TouchStatus {
		public int None = 4, Down = 1, Horizontal = 2, Vertical = 3, Up = 4;
		public int state = None;
	}

	public TouchStatus touchStatus = new TouchStatus();

	public boolean onTouchEvent(MotionEvent event) {

		if (!thisView.currentShowContentView.equals(thisView.intimateFriendsContentView)) {
			return true;
		}

		int motionEvent = event.getAction();
		if (motionEvent == MotionEvent.ACTION_DOWN) {
			thisView.myPagerBody.onTouchDown(event);
			thisView.myListBody.onTouchDown(event);
		} else if (motionEvent == MotionEvent.ACTION_MOVE) {
			thisView.myPagerBody.onTouchMove(event);
			thisView.myListBody.onTouchMove(event);
		} else if (motionEvent == MotionEvent.ACTION_UP) {
			thisView.myPagerBody.onTouchUp(event);
			thisView.myListBody.onTouchUp(event);
		}
		mGesture.onTouchEvent(event);
		return true;
	}

	class GestureListener extends SimpleOnGestureListener {
		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
			Log.i("GestureListener", "onFling:velocityX = " + velocityX + " velocityY" + velocityY);

			if (thisView.myListBody.bodyStatus.state == thisView.myListBody.bodyStatus.DRAGGING) {
				thisView.myListBody.onFling(velocityX, velocityY);
			}
			if (thisView.myPagerBody.bodyStatus.state == thisView.myPagerBody.bodyStatus.HOMING) {
				thisView.myPagerBody.onFling(velocityX, velocityY);
			}
			return true;
		}

	}

	void generateTextView(final String message) {
		handler.post(new Runnable() {

			@Override
			public void run() {
				Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
			}
		});
	}

}
