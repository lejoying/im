package com.open.welinks.controller;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.rebound.SimpleSpringListener;
import com.facebook.rebound.Spring;
import com.facebook.rebound.SpringUtil;
import com.google.gson.Gson;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.client.HttpRequest;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.open.welinks.ShareReleaseImageTextActivity;
import com.open.welinks.controller.DownloadFile.DownloadListener;
import com.open.welinks.model.Data;
import com.open.welinks.model.Data.Relationship.Circle;
import com.open.welinks.model.ResponseHandlers;
import com.open.welinks.utils.NetworkHandler;
import com.open.welinks.view.MainView;

public class MainController {

	public Data data = Data.getInstance();
	public String tag = "UserIntimateController";
	public MainView thisView;
	public Context context;
	public Activity thisActivity;

	public ResponseHandlers responseHandlers = ResponseHandlers.getInstance();

	public GestureDetector mGesture;
	public GestureDetector mListGesture;

	public OnClickListener mOnClickListener;
	public OnTouchListener onTouchListener;
	public OnTouchListener onTouchBackColorListener;
	public DownloadListener downloadListener;
	public OnLongClickListener onLongClickListener;
	public ListOnTouchListener listOnTouchListener;

	public SquareSubController squareSubController;
	public ShareSubController shareSubController;
	public MessagesSubController messagesSubController;
	public FriendsSubController friendsSubController;
	public MeSubController meSubController;

	NetworkHandler mNetworkHandler = NetworkHandler.getInstance();
	Handler handler = new Handler();
	String url_userInfomation = "http://www.we-links.com/api2/account/getuserinfomation";
	String url_intimateFriends = "http://www.we-links.com/api2/relation/intimatefriends";

	Gson gson = new Gson();

	public String userPhone;

	// public BaseSpringSystem mSpringSystem = SpringSystem.create();
	// public Spring mScaleSpring = mSpringSystem.createSpring();
	public ExampleSpringListener mSpringListener = new ExampleSpringListener();
	private MainController thisController;

	public void oncreate() {
		String phone = thisActivity.getIntent().getStringExtra("phone");
		if (phone != null && !"".equals(phone)) {
			userPhone = phone;
		}
		mGesture = new GestureDetector(thisActivity, new GestureListener());
		mListGesture = new GestureDetector(thisActivity, new GestureListener());

		thisView.friendsSubView.showCircles();
		thisView.messagesSubView.showMessages();

		thisView.shareSubView.showShareMessages();

		this.getUserCurrentAllGroup();

		// thisView.showGroupMembers(thisView.groupMembersListContentView);
	}

	public void onResume() {
		thisView.meSubView.mMePageAppIconScaleSpring.addListener(mSpringListener);
	}

	public void onPause() {
		thisView.meSubView.mMePageAppIconScaleSpring.removeListener(mSpringListener);
	}

	private class ExampleSpringListener extends SimpleSpringListener {
		@Override
		public void onSpringUpdate(Spring spring) {
			float mappedValue = (float) SpringUtil.mapValueFromRangeToRange(spring.getCurrentValue(), 0, 1, 1, 0.5);
			thisView.meSubView.mAppIconToNameView.setScaleX(mappedValue);
			thisView.meSubView.mAppIconToNameView.setScaleY(mappedValue);
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

	public MainController(Activity thisActivity) {
		this.context = thisActivity;
		this.thisActivity = thisActivity;
		
		thisController=this;
	}

	public void initializeListeners() {
		friendsSubController.initializeListeners();
		onTouchBackColorListener = new OnTouchListener() {

			@Override
			public boolean onTouch(View view, MotionEvent event) {
				if (view == thisView.shareSubView.releaseImageTextButton) {
					int motionEvent = event.getAction();
					if (motionEvent == MotionEvent.ACTION_DOWN) {
						view.setBackgroundColor(Color.argb(143, 0, 0, 0));
					} else if (motionEvent == MotionEvent.ACTION_UP) {
						view.setBackgroundColor(Color.parseColor("#00000000"));
					}
				}
				return false;
			}
		};
		onLongClickListener = new OnLongClickListener() {

			@Override
			public boolean onLongClick(View view) {
				thisView.friendsSubView.showCircleSettingDialog(view);
				return true;
			}
		};
		downloadListener = new DownloadListener() {

			@Override
			public void loading(DownloadFile instance, int precent, int status) {
			}

			@Override
			public void success(DownloadFile instance, int status) {
				thisView.imageLoader.displayImage("file://" + instance.path, (ImageView) instance.view, thisView.shareSubView.options);
			}
		};
		onTouchListener = new OnTouchListener() {

			@Override
			public boolean onTouch(View view, MotionEvent event) {
				int action = event.getAction();
				if (action == MotionEvent.ACTION_DOWN) {
					thisView.meSubView.mMePageAppIconScaleSpring.setEndValue(1);
				} else if (action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_CANCEL) {
					thisView.meSubView.mMePageAppIconScaleSpring.setEndValue(0);
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

				else if (view.equals(thisView.shareSubView.shareTopMenuGroupNameParent)) {
					thisView.shareSubView.showGroupsDialog();
				} else if (view.equals(thisView.shareSubView.groupDialogView)) {
					thisView.shareSubView.dismissGroupDialog();
				}

				else if (view.equals(thisView.friendsSubView.modifyCircleNameView)) {
					if (thisView.friendsSubView.currentStatus == thisView.friendsSubView.SHOW_DIALOG) {
						thisView.friendsSubView.dialogSpring.removeListener(thisView.friendsSubView.dialogSpringListener);
						thisView.friendsSubView.currentStatus = thisView.friendsSubView.DIALOG_SWITCH;
						thisView.friendsSubView.dialogOutSpring.addListener(thisView.friendsSubView.dialogSpringListener);
						thisView.friendsSubView.dialogOutSpring.setCurrentValue(1.0);
						thisView.friendsSubView.dialogOutSpring.setEndValue(0);
						thisView.friendsSubView.dialogInSpring.addListener(thisView.friendsSubView.dialogSpringListener);
						thisView.friendsSubView.inputDialigView.setVisibility(View.VISIBLE);
						thisView.friendsSubView.dialogInSpring.setCurrentValue(1);
						thisView.friendsSubView.dialogInSpring.setEndValue(0);
					}
				} else if (view.equals(thisView.friendsSubView.cancleButton)) {
					thisView.friendsSubView.dismissCircleSettingDialog();
				} else if (view.equals(thisView.friendsSubView.confirmButton)) {
					EditText editText = ((EditText) (view.getTag()));
					String inputContent = editText.getText().toString().trim();
					if ("".equals(inputContent)) {
						generateTextView("组名不能为空");
						return;
					}
					TextView circleNameView = (TextView) editText.getTag();
					circleNameView.setText(inputContent);
					thisView.friendsSubView.dismissCircleSettingDialog();
					Circle circle = (Circle) circleNameView.getTag();
					circle.name = inputContent;
				} else if (view.equals(thisView.shareSubView.releaseShareView)) {
					thisView.shareSubView.showReleaseShareDialogView();
				} else if (view.equals(thisView.shareSubView.releaseShareDialogView)) {
					thisView.shareSubView.dismissReleaseShareDialogView();
				} else if (view.equals(thisView.shareSubView.releaseImageTextButton)) {
					Intent intent = new Intent(thisActivity, ShareReleaseImageTextActivity.class);
					thisActivity.startActivity(intent);
					thisView.shareSubView.dismissReleaseShareDialogView();
				}
				// else if (view.equals(thisView.meSubView.me_setting_view)) {
				// Intent intent = new Intent(thisActivity,
				// SettingActivity.class);
				// thisActivity.startActivity(intent);
				// }

				else if (view.getTag() != null) {
					Log.d(tag, (String) view.getTag());
				}
			}
		};
		listOnTouchListener = new ListOnTouchListener();

		this.thisController.squareSubController.initializeListeners();
		this.thisController.shareSubController.initializeListeners();
		this.thisController.messagesSubController.initializeListeners();
		this.thisController.friendsSubController.initializeListeners();
		this.thisController.meSubController.initializeListeners();
	}

	public void bindEvent() {
		friendsSubController.bindEvent();

		thisView.friendsMenuView.setOnClickListener(mOnClickListener);
		thisView.messagesMenuView.setOnClickListener(mOnClickListener);
		thisView.meMenuView.setOnClickListener(mOnClickListener);

		thisView.squareMenuView.setOnClickListener(mOnClickListener);
		thisView.shareMenuView.setOnClickListener(mOnClickListener);
		thisView.messages_friends_me_menuView.setOnClickListener(mOnClickListener);

		thisView.meSubView.mRootView.setOnTouchListener(onTouchListener);

		thisView.shareSubView.shareTopMenuGroupNameParent.setOnClickListener(mOnClickListener);
		thisView.shareSubView.groupDialogView.setOnClickListener(mOnClickListener);

		// thisView.friendsSubView.friendsView.setOnTouchListener(listOnTouchListener);

		// List<String> listItemsSqquece =
		// thisView.shareSubView.shareMessageListBody.listItemsSequence;
		// for (int i = 0; i < listItemsSqquece.size(); i++) {
		// String key = listItemsSqquece.get(i);
		// SharesMessageBody sharesMessageBody = (SharesMessageBody)
		// thisView.shareSubView.shareMessageListBody.listItemBodiesMap.get(key);
		// if (sharesMessageBody.downloadFile != null) {
		// sharesMessageBody.downloadFile.setDownloadFileListener(downloadListener);
		// }
		// }

		// thisView.me_setting_view.setOnClickListener(mOnClickListener);
		
		this.thisController.squareSubController.bindEvent();
		this.thisController.shareSubController.bindEvent();
		this.thisController.messagesSubController.bindEvent();
		this.thisController.friendsSubController.bindEvent();
		this.thisController.meSubController.bindEvent();
	}

	public class TouchStatus {
		public int None = 4, Down = 1, Horizontal = 2, Vertical = 3, Up = 4;
		public int state = None;
	}

	public TouchStatus touchStatus = new TouchStatus();

	public boolean onTouchEvent(MotionEvent event) {

		int motionEvent = event.getAction();
		if (motionEvent == MotionEvent.ACTION_DOWN) {
			Log.d(tag, "Activity on touch down");
			thisView.messages_friends_me_PagerBody.onTouchDown(event);
			thisView.mainPagerBody.onTouchDown(event);
			thisView.friendsSubView.friendListBody.onTouchDown(event);
			// thisView.chatMessageListBody.onTouchDown(event);
			thisView.shareSubView.shareMessageListBody.onTouchDown(event);
		} else if (motionEvent == MotionEvent.ACTION_MOVE) {
			thisView.messages_friends_me_PagerBody.onTouchMove(event);
			thisView.mainPagerBody.onTouchMove(event);
			thisView.friendsSubView.friendListBody.onTouchMove(event);
			// thisView.chatMessageListBody.onTouchMove(event);
			thisView.shareSubView.shareMessageListBody.onTouchMove(event);
		} else if (motionEvent == MotionEvent.ACTION_UP) {
			thisView.messages_friends_me_PagerBody.onTouchUp(event);
			thisView.mainPagerBody.onTouchUp(event);
			thisView.friendsSubView.friendListBody.onTouchUp(event);
			// thisView.chatMessageListBody.onTouchUp(event);
			thisView.shareSubView.shareMessageListBody.onTouchUp(event);
		}
		mGesture.onTouchEvent(event);
		return true;
	}

	class ListOnTouchListener implements OnTouchListener {

		@Override
		public boolean onTouch(View v, MotionEvent event) {

			int motionEvent = event.getAction();
			if (motionEvent == MotionEvent.ACTION_DOWN) {
				Log.d(tag, "List on touch down");
				thisView.friendsSubView.friendListBody.onTouchDown(event);
			} else if (motionEvent == MotionEvent.ACTION_MOVE) {
				Log.d(tag, "List on touch move");
				thisView.friendsSubView.friendListBody.onTouchMove(event);
			} else if (motionEvent == MotionEvent.ACTION_UP) {
				Log.d(tag, "List on touch up");
				thisView.friendsSubView.friendListBody.onTouchUp(event);
			}
			mListGesture.onTouchEvent(event);
			return true;
		}

	}

	class GestureListener extends SimpleOnGestureListener {
		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
			Log.i("GestureListener", "onFling:velocityX = " + velocityX + " velocityY" + velocityY);

			if (thisView.friendsSubView.friendListBody.bodyStatus.state == thisView.friendsSubView.friendListBody.bodyStatus.DRAGGING) {
				thisView.friendsSubView.friendListBody.onFling(velocityX, velocityY);
			}
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
			if (thisView.shareSubView.shareMessageListBody.bodyStatus.state == thisView.shareSubView.shareMessageListBody.bodyStatus.DRAGGING) {
				thisView.shareSubView.shareMessageListBody.onFling(velocityX, velocityY);
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

	public void getUserCurrentAllGroup() {
		RequestParams params = new RequestParams();
		HttpUtils httpUtils = new HttpUtils();
		params.addBodyParameter("phone", data.userInformation.currentUser.phone);
		params.addBodyParameter("accessKey", "lejoying");

		httpUtils.send(HttpMethod.POST, "http://www.we-links.com/api2/group/getgroupmembers", params, responseHandlers.getGroupMembers);
	}
}
