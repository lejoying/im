package com.open.welinks.controller;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.client.HttpRequest;
import com.open.welinks.model.Data;
import com.open.welinks.model.Data.Relationship;
import com.open.welinks.model.Data.UserInformation;
import com.open.welinks.model.ResponseHandlers;
import com.open.welinks.utils.CommonNetConnection;
import com.open.welinks.utils.NetworkHandler;
import com.open.welinks.utils.NetworkHandler.Settings;
import com.open.welinks.view.UserIntimateView;

public class UserIntimateController {

	public Data data = Data.getInstance();
	public String tag = "UserIntimateController";
	public UserIntimateView thisView;
	public Context context;
	public Activity thisActivity;

	public GestureDetector mGesture;

	public OnClickListener mOnClickListener;

	NetworkHandler mNetworkHandler = NetworkHandler.getInstance();
	Handler handler = new Handler();
	String url_userInfomation = "http://www.we-links.com/api2/account/getuserinfomation";
	String url_intimateFriends = "http://www.we-links.com/api2/relation/intimatefriends";

	Gson gson = new Gson();

	public String userPhone;

	public void oncreate() {
		String phone = thisActivity.getIntent().getStringExtra("phone");
		if (phone != null && !"".equals(phone)) {
			userPhone = phone;
		}
		mGesture = new GestureDetector(thisActivity, new GestureListener());
		
		
		thisView.showCircles();
//		this.test();
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
		mOnClickListener = new OnClickListener() {

			@Override
			public void onClick(View view) {
				if (view.equals(thisView.intimateFriendsMenuOptionView)) {
					thisView.changeMenuOptionSelected(thisView.intimateFriendsContentView, thisView.intimateFriendsMenuOptionStatusImage);
				} else if (view.equals(thisView.chatMessagesListMenuOptionView)) {
					thisView.changeMenuOptionSelected(thisView.chatMessagesListContentView, thisView.chatMessagesListMenuOptionStatusImage);
				} else if (view.equals(thisView.userInfomationMenuOptionView)) {
					thisView.changeMenuOptionSelected(thisView.userInfomationContentView, thisView.userInfomationMenuOptionStatusImage);
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

	public int touchMoveStatus_None = 4;
	public int touchMoveStatus_Down = 0;
	public int touchMoveStatus_Horizontal = 1;
	public int touchMoveStatus_Vertical = 2;
	public int touchMoveStatus_Up = 4;

	public int touchMoveStatus = touchMoveStatus_None;

	float Dy = 0;

	public boolean onTouchEvent(MotionEvent event) {

		if (!thisView.currentShowContentView.equals(thisView.intimateFriendsContentView)) {
			return true;
		}
		eventCount++;
		float x = event.getX();
		float y = event.getY();
		long currentMillis = System.currentTimeMillis();

		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			if (touchMoveStatus == touchMoveStatus_Up) {
				touchMoveStatus = touchMoveStatus_Down;
			}
			pre_x = x;
			pre_y = y;

			thisView.myListBody.recordChildrenPosition();
			thisView.myPagerBody.recordChildrenPosition();

			thisView.mSpring.setCurrentValue(0);
			thisView.mSpring.setEndValue(0);
			Log.e("onTouchEvent", "touch down");

			if (y > 520) {

			} else {

			}
		} else if (event.getAction() == MotionEvent.ACTION_MOVE) {
			if (touchMoveStatus == touchMoveStatus_Down) {
				if ((y - pre_y) * (y - pre_y) > 400 || (x - pre_x) * (x - pre_x) > 400) {
					if ((y - pre_y) * (y - pre_y) > (x - pre_x) * (x - pre_x)) {
						touchMoveStatus = touchMoveStatus_Vertical;
						Dy = pre_y - y;
						pre_y = y;
						Log.e("onTouchEvent", "开始纵向滑动:Dy=" + Dy);
					} else {
						touchMoveStatus = touchMoveStatus_Horizontal;
						pre_x = x;
						Log.e("onTouchEvent", "开始横向滑动");
					}
				}
			}
			if (touchMoveStatus == touchMoveStatus_Vertical) {
				x = pre_x;
				thisView.myListBody.setChildrenPosition(0, y - pre_y);
			} else if (touchMoveStatus == touchMoveStatus_Horizontal) {
				y = pre_y;
				thisView.myPagerBody.setChildrenPosition(x - pre_x, 0);
			} else {
				Log.e("onTouchEvent", "unkown status");
				x = pre_x;
				y = pre_y;
			}

		} else if (event.getAction() == MotionEvent.ACTION_UP) {
			Dy = pre_y - y;
			Log.e("onTouchEvent", "touch up:Dy=" + Dy);
			touchMoveStatus = touchMoveStatus_Up;
		}
		mGesture.onTouchEvent(event);
		return true;
	}

	class GestureListener extends SimpleOnGestureListener {
		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
			Log.i("GestureListener", "onFling:velocityX = " + velocityX + " velocityY" + velocityY);

			touchMoveStatus = touchMoveStatus_Up;
			thisView.myListBody.recordChildrenPosition();

			thisView.mSpring.setCurrentValue(0);
			if (velocityY > 0) {
				thisView.speedY = velocityY;
				if (velocityY > 5000) {
					thisView.speedY = 5000;
				}
			} else if (velocityY < 0) {
				thisView.speedY = velocityY;
				if (velocityY < -5000) {
					thisView.speedY = -5000;
				}
			}
			thisView.mSpring.setEndValue(1);

			return true;
		}

	}

	public void getUserInfomationData() {
		mNetworkHandler.connection(new CommonNetConnection() {

			@Override
			public void success(JSONObject jData) {
				generateTextView("获取个人信息成功...");
				try {
					data.userInformation = gson.fromJson(jData.getString("data"), UserInformation.class);
					getIntimateFriendsData(userPhone);
				} catch (JsonSyntaxException e) {
					e.printStackTrace();
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}

			@Override
			protected void settings(Settings settings) {
				generateTextView("正在获取个人信息...");
				settings.url = url_userInfomation;
				Map<String, String> params = new HashMap<String, String>();
				params.put("phone", userPhone);
				params.put("accessKey", "lejoying");
				settings.params = params;
			}

			@Override
			protected void unSuccess(JSONObject jData) {
				generateTextView("获取个人信息失败...");
				super.unSuccess(jData);
			}
		});
	}

	void getIntimateFriendsData(final String phone) {
		mNetworkHandler.connection(new CommonNetConnection() {

			@Override
			public void success(JSONObject jData) {
				generateTextView("获取密友成功...");
				try {
					data.relationship = gson.fromJson(jData.getString("data"), Relationship.class);
					generateTextView("准备初始化UI...");
					// Thread.currentThread().sleep(1000);
					handler.post(new Runnable() {

						@Override
						public void run() {
							thisView.notifyViews();
						}
					});
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}

			@Override
			protected void settings(Settings settings) {
				generateTextView("正在获取密友...");
				settings.url = url_intimateFriends;
				Map<String, String> params = new HashMap<String, String>();
				params.put("phone", phone);
				params.put("accessKey", "lejoying");
				settings.params = params;
			}

			@Override
			protected void unSuccess(JSONObject jData) {
				generateTextView("获取密友失败...");
				super.unSuccess(jData);
			}
		});
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
