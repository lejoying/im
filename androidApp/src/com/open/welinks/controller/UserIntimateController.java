package com.open.welinks.controller;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.ImageView;
import android.widget.Toast;

import com.facebook.rebound.SimpleSpringListener;
import com.facebook.rebound.Spring;
import com.facebook.rebound.SpringUtil;
import com.google.gson.Gson;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.client.HttpRequest;
import com.open.welinks.SettingActivity;
import com.open.welinks.controller.DownloadFile.DownloadListener;
import com.open.welinks.model.Data;
import com.open.welinks.model.ResponseHandlers;
import com.open.welinks.utils.NetworkHandler;
import com.open.welinks.view.UserIntimateView;
import com.open.welinks.view.UserIntimateView.SharesMessageBody;

public class UserIntimateController {

	public Data data = Data.getInstance();
	public String tag = "UserIntimateController";
	public UserIntimateView thisView;
	public Context context;
	public Activity thisActivity;

	public GestureDetector mGesture;

	public OnClickListener mOnClickListener;
	public OnTouchListener onTouchListener;
	public DownloadListener downloadListener;

	NetworkHandler mNetworkHandler = NetworkHandler.getInstance();
	Handler handler = new Handler();
	String url_userInfomation = "http://www.we-links.com/api2/account/getuserinfomation";
	String url_intimateFriends = "http://www.we-links.com/api2/relation/intimatefriends";

	Gson gson = new Gson();

	public String userPhone;

	// public BaseSpringSystem mSpringSystem = SpringSystem.create();
	// public Spring mScaleSpring = mSpringSystem.createSpring();
	public ExampleSpringListener mSpringListener = new ExampleSpringListener();

	public void oncreate() {
		String phone = thisActivity.getIntent().getStringExtra("phone");
		if (phone != null && !"".equals(phone)) {
			userPhone = phone;
		}
		mGesture = new GestureDetector(thisActivity, new GestureListener());

		thisView.showCircles();
		// this.test();
		// initChatMessages();
		thisView.showMessages();

		thisView.showShareMessages();

		// thisView.showGroupMembers(thisView.groupMembersListContentView);
	}

	public void onResume() {
		thisView.mMePageAppIconScaleSpring.addListener(mSpringListener);
	}

	public void onPause() {
		thisView.mMePageAppIconScaleSpring.removeListener(mSpringListener);
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
		downloadListener = new DownloadListener() {

			@Override
			public void loading(DownloadFile instance, int precent, int status) {
				// TODO Auto-generated method stub

			}

			@Override
			public void success(DownloadFile instance, int status) {
				thisView.imageLoader.displayImage("file://" + instance.path, (ImageView) instance.view, thisView.options);
			}
		};
		onTouchListener = new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				int action = event.getAction();
				if (action == MotionEvent.ACTION_DOWN) {
					thisView.mMePageAppIconScaleSpring.setEndValue(1);
				} else if (action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_CANCEL) {
					thisView.mMePageAppIconScaleSpring.setEndValue(0);
				}
				return true;
			}
		};
		mOnClickListener = new OnClickListener() {

			@Override
			public void onClick(View view) {
				if (view.equals(thisView.messagesMenuView)) {
					thisView.messages_friends_me_PagerBody.flipTo(0);
				} else if (view.equals(thisView.friendsMenuView)) {
					thisView.messages_friends_me_PagerBody.flipTo(1);
				} else if (view.equals(thisView.meMenuView)) {
					thisView.messages_friends_me_PagerBody.flipTo(2);
				}

				else if (view.equals(thisView.squareMenuView)) {
					thisView.mainPagerBody.active();
					thisView.messages_friends_me_PagerBody.inActive();
					thisView.mainPagerBody.flipTo(0);
				} else if (view.equals(thisView.shareMenuView)) {
					thisView.mainPagerBody.active();
					thisView.messages_friends_me_PagerBody.inActive();
					thisView.mainPagerBody.flipTo(1);
				} else if (view.equals(thisView.messages_friends_me_menuView)) {
					thisView.mainPagerBody.active();
					thisView.messages_friends_me_PagerBody.inActive();
					thisView.mainPagerBody.flipTo(2);
				}

				else if (view.equals(thisView.shareTopMenuGroupNameParent)) {
					thisView.showGroupsDialog();
				} else if (view.equals(thisView.groupDialogView)) {
					thisView.dismissGroupDialog();
				}

				else if (view.equals(thisView.me_setting_view)) {
					Intent intent = new Intent(thisActivity, SettingActivity.class);
					thisActivity.startActivity(intent);
				}

				else if (view.getTag() != null) {
					Log.d(tag, (String) view.getTag());
				}
			}
		};
	}

	public void bindEvent() {
		thisView.friendsMenuView.setOnClickListener(mOnClickListener);
		thisView.messagesMenuView.setOnClickListener(mOnClickListener);
		thisView.meMenuView.setOnClickListener(mOnClickListener);

		thisView.squareMenuView.setOnClickListener(mOnClickListener);
		thisView.shareMenuView.setOnClickListener(mOnClickListener);
		thisView.messages_friends_me_menuView.setOnClickListener(mOnClickListener);

		thisView.mRootView.setOnTouchListener(onTouchListener);

		thisView.shareTopMenuGroupNameParent.setOnClickListener(mOnClickListener);
		thisView.groupDialogView.setOnClickListener(mOnClickListener);

		List<String> listItemsSqquece = thisView.shareMessageListBody.listItemsSequence;
		for (int i = 0; i < listItemsSqquece.size(); i++) {
			String key = listItemsSqquece.get(i);
			SharesMessageBody sharesMessageBody = (SharesMessageBody) thisView.shareMessageListBody.listItemBodiesMap.get(key);
			if (sharesMessageBody.downloadFile != null) {
				sharesMessageBody.downloadFile.setDownloadFileListener(downloadListener);
			}
		}

		thisView.me_setting_view.setOnClickListener(mOnClickListener);
	}

	public class TouchStatus {
		public int None = 4, Down = 1, Horizontal = 2, Vertical = 3, Up = 4;
		public int state = None;
	}

	public TouchStatus touchStatus = new TouchStatus();

	public boolean onTouchEvent(MotionEvent event) {

		int motionEvent = event.getAction();
		if (motionEvent == MotionEvent.ACTION_DOWN) {
			thisView.messages_friends_me_PagerBody.onTouchDown(event);
			thisView.mainPagerBody.onTouchDown(event);
			// thisView.friendListBody.onTouchDown(event);
			// thisView.chatMessageListBody.onTouchDown(event);
			thisView.shareMessageListBody.onTouchDown(event);
		} else if (motionEvent == MotionEvent.ACTION_MOVE) {
			thisView.messages_friends_me_PagerBody.onTouchMove(event);
			thisView.mainPagerBody.onTouchMove(event);
			// thisView.friendListBody.onTouchMove(event);
			// thisView.chatMessageListBody.onTouchMove(event);
			thisView.shareMessageListBody.onTouchMove(event);
		} else if (motionEvent == MotionEvent.ACTION_UP) {
			thisView.messages_friends_me_PagerBody.onTouchUp(event);
			thisView.mainPagerBody.onTouchUp(event);
			// thisView.friendListBody.onTouchUp(event);
			// thisView.chatMessageListBody.onTouchUp(event);
			thisView.shareMessageListBody.onTouchUp(event);
		}
		mGesture.onTouchEvent(event);
		return true;
	}

	class GestureListener extends SimpleOnGestureListener {
		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
			Log.i("GestureListener", "onFling:velocityX = " + velocityX + " velocityY" + velocityY);

			// if (thisView.friendListBody.bodyStatus.state ==
			// thisView.friendListBody.bodyStatus.DRAGGING) {
			// thisView.friendListBody.onFling(velocityX, velocityY);
			// }
			if (thisView.messages_friends_me_PagerBody.bodyStatus.state == thisView.messages_friends_me_PagerBody.bodyStatus.HOMING) {
				thisView.messages_friends_me_PagerBody.onFling(velocityX, velocityY);
			}
			if (thisView.mainPagerBody.bodyStatus.state == thisView.mainPagerBody.bodyStatus.HOMING) {
				thisView.mainPagerBody.onFling(velocityX, velocityY);
			}
			// if (thisView.chatMessageListBody.bodyStatus.state ==
			// thisView.chatMessageListBody.bodyStatus.DRAGGING) {
			// thisView.chatMessageListBody.onFling(velocityX, velocityY);
			// }
			if (thisView.shareMessageListBody.bodyStatus.state == thisView.shareMessageListBody.bodyStatus.DRAGGING) {
				thisView.shareMessageListBody.onFling(velocityX, velocityY);
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
