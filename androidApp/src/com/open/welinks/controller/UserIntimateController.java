package com.open.welinks.controller;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.open.welinks.model.Data;
import com.open.welinks.model.Data.Relationship;
import com.open.welinks.model.Data.UserInformation;
import com.open.welinks.utils.CommonNetConnection;
import com.open.welinks.utils.NetworkHandler;
import com.open.welinks.utils.NetworkHandler.Settings;
import com.open.welinks.view.UserIntimateView;

public class UserIntimateController {

	public Data data = Data.getInstance();

	public UserIntimateView thisView;
	public Context context;
	public Activity thisActivity;

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
					thisView.changeMenuOptionSelected(
							thisView.intimateFriendsContentView,
							thisView.intimateFriendsMenuOptionStatusImage);
				} else if (view.equals(thisView.chatMessagesListMenuOptionView)) {
					thisView.changeMenuOptionSelected(
							thisView.chatMessagesListContentView,
							thisView.chatMessagesListMenuOptionStatusImage);
				} else if (view.equals(thisView.userInfomationMenuOptionView)) {
					thisView.changeMenuOptionSelected(
							thisView.userInfomationContentView,
							thisView.userInfomationMenuOptionStatusImage);
				}

			}
		};
	}

	public void bindEvent() {
		thisView.intimateFriendsMenuOptionView
				.setOnClickListener(mOnClickListener);
		thisView.chatMessagesListMenuOptionView
				.setOnClickListener(mOnClickListener);
		thisView.userInfomationMenuOptionView
				.setOnClickListener(mOnClickListener);
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

	public boolean onTouchEvent(MotionEvent event) {

		if (!thisView.currentShowContentView
				.equals(thisView.intimateFriendsContentView)) {
			return true;
		}
		eventCount++;
		float x = event.getX();
		float y = event.getY();
		long currentMillis = System.currentTimeMillis();

		RelativeLayout intimateFriendsContentView = thisView.intimateFriendsContentView;

		if (event.getAction() == MotionEvent.ACTION_DOWN) {

			pre_x = x;
			pre_y = y;

			progress_test_x = intimateFriendsContentView.getX();
			progress_test_y = intimateFriendsContentView.getY();

			if (y > 520) {

			} else {

			}
		} else if (event.getAction() == MotionEvent.ACTION_MOVE) {
			if (lastMillis == 0) {
				lastMillis = currentMillis;
				return true;
			}

			// progress_test.setX(progress_test_x + x - pre_x);
			intimateFriendsContentView.setY(progress_test_y + y - pre_y);

			// progress_line1.setX(progress_line1_x + x - pre_x);

		} else if (event.getAction() == MotionEvent.ACTION_UP) {
			long delta = currentMillis - lastMillis;

			if (delta == 0 || x == pre_x || y == pre_y) {
				delta = currentMillis - pre_lastMillis;
				pre_x = pre_pre_x;
				pre_y = pre_pre_y;
			}

		}
		return true;
	}

	public void getUserInfomationData() {
		mNetworkHandler.connection(new CommonNetConnection() {

			@Override
			public void success(JSONObject jData) {
				generateTextView("获取个人信息成功...");
				try {
					data.userInformation = gson.fromJson(
							jData.getString("data"), UserInformation.class);
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
					data.relationship = gson.fromJson(jData.getString("data"),
							Relationship.class);
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
