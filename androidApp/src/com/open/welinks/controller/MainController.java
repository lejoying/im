package com.open.welinks.controller;

import java.lang.reflect.Field;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.facebook.rebound.SimpleSpringListener;
import com.facebook.rebound.Spring;
import com.facebook.rebound.SpringUtil;
import com.google.gson.Gson;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.client.HttpRequest;
import com.open.lib.viewbody.ListBody1;
import com.open.welinks.LoginActivity;
import com.open.welinks.MainActivity;
import com.open.welinks.R;
import com.open.welinks.ScanQRCodeActivity;
import com.open.welinks.model.API;
import com.open.welinks.model.Data;
import com.open.welinks.model.Parser;
import com.open.welinks.model.Data.Relationship.Circle;
import com.open.welinks.model.Data.UserInformation.User;
import com.open.welinks.model.ResponseHandlers;
import com.open.welinks.service.PushService;
import com.open.welinks.utils.NetworkHandler;
import com.open.welinks.view.MainView;

public class MainController {

	public Data data = Data.getInstance();
	public Parser parser = Parser.getInstance();

	public String tag = "MainController";
	public MainView thisView;
	public Context context;
	public Activity thisActivity;

	public GestureDetector mGesture;
	public GestureDetector mListGesture;

	public OnClickListener mOnClickListener;
	public OnDownloadListener downloadListener;

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
		// thisView.showGroupMembers(thisView.groupMembersListContentView);

		data.tempData.statusBarHeight = getStatusBarHeight(thisActivity);

		getIntimatefriends();
	}

	public void onResume() {
		data.localStatus.thisActivityName = "MainActivity";
		thisView.meSubView.mMePageAppIconScaleSpring.addListener(mSpringListener);
		// thisView.shareSubView.onResume();
		// thisView.messagesSubView.onResume();
	}

	public void onPause() {
		thisView.meSubView.mMePageAppIconScaleSpring.removeListener(mSpringListener);
	}

	public void onDestroy() {
		thisView.messagesSubView.onDestroy();

	}

	private class ExampleSpringListener extends SimpleSpringListener {
		@Override
		public void onSpringUpdate(Spring spring) {
			float mappedValue = (float) SpringUtil.mapValueFromRangeToRange(spring.getCurrentValue(), 0, 1, 1, 0.5);
			thisView.meSubView.mAppIconToNameView.setScaleX(mappedValue);
			thisView.meSubView.mAppIconToNameView.setScaleY(mappedValue);
		}
	}

	public void getIntimatefriends() {
		Log.e(tag, "刷新好友分组getIntimatefriends");
		RequestParams params = new RequestParams();
		data = parser.check();
		User user = data.userInformation.currentUser;
		params.addBodyParameter("accessKey", user.accessKey);
		params.addBodyParameter("phone", user.phone);

		HttpUtils http = new HttpUtils();
		ResponseHandlers responseHandlers = ResponseHandlers.getInstance();

		http.send(HttpRequest.HttpMethod.POST, API.RELATION_GETINTIMATEFRIENDS, params, responseHandlers.getIntimateFriends);
	}

	public MainController(Activity thisActivity) {
		this.context = thisActivity;
		this.thisActivity = thisActivity;

		thisController = this;
	}

	public void initializeListeners() {

		downloadListener = new OnDownloadListener() {

			@Override
			public void loading(DownloadFile instance, int precent, int status) {
			}

			@Override
			public void onSuccess(DownloadFile instance, int status) {
				thisView.imageLoader.displayImage("file://" + instance.path, (ImageView) instance.view, thisView.shareSubView.options);
			}

			@Override
			public void onFailure(DownloadFile instance, int status) {
				// TODO Auto-generated method stub

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
					EditText editText = ((EditText) (view.getTag(R.id.tag_first)));
					String inputContent = editText.getText().toString().trim();
					Circle circle = (Circle) view.getTag(R.id.tag_second);
					if (thisView.activityStatus.state == thisView.activityStatus.FRIENDS) {
						friendsSubController.onConfirmButton(inputContent, circle);
					}

					thisView.friendsSubView.dismissCircleSettingDialog();
				} else if (view.equals(thisView.scannerCodeView)) {
					Intent intent = new Intent(thisActivity, ScanQRCodeActivity.class);
					thisActivity.startActivity(intent);
				} else if (view.getTag() != null) {

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

		thisView.scannerCodeView.setOnClickListener(mOnClickListener);

		thisView.friendsMenuView.setOnClickListener(mOnClickListener);
		thisView.messagesMenuView.setOnClickListener(mOnClickListener);
		thisView.meMenuView.setOnClickListener(mOnClickListener);

		thisView.squareMenuView.setOnClickListener(mOnClickListener);
		thisView.shareMenuView.setOnClickListener(mOnClickListener);
		thisView.messages_friends_me_menuView.setOnClickListener(mOnClickListener);

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
			if (thisView.activityStatus.state == thisView.activityStatus.MESSAGES) {
				thisView.messagesSubView.messageListBody.onTouchDown(event);
			} else if (thisView.activityStatus.state == thisView.activityStatus.FRIENDS) {
				thisView.friendsSubView.friendListBody.onTouchDown(event);
			} else if (thisView.activityStatus.state == thisView.activityStatus.SHARE) {
				thisView.shareSubView.shareMessageListBody.onTouchDown(event);
				thisView.shareSubView.groupListBody.onTouchDown(event);
			} else if (thisView.activityStatus.state == thisView.activityStatus.SQUARE) {
				thisView.squareSubView.squareListBody.onTouchDown(event);
			}

		} else if (motionEvent == MotionEvent.ACTION_MOVE) {
			thisView.messages_friends_me_PagerBody.onTouchMove(event);
			thisView.mainPagerBody.onTouchMove(event);

			if (thisView.activityStatus.state == thisView.activityStatus.MESSAGES) {
				thisView.messagesSubView.messageListBody.onTouchMove(event);
			} else if (thisView.activityStatus.state == thisView.activityStatus.FRIENDS) {
				thisView.friendsSubView.friendListBody.onTouchMove(event);
			} else if (thisView.activityStatus.state == thisView.activityStatus.SHARE) {
				thisView.shareSubView.shareMessageListBody.onTouchMove(event);
				thisView.shareSubView.groupListBody.onTouchMove(event);
			} else if (thisView.activityStatus.state == thisView.activityStatus.SQUARE) {
				thisView.squareSubView.squareListBody.onTouchMove(event);
			}
		} else if (motionEvent == MotionEvent.ACTION_UP) {
			thisView.messages_friends_me_PagerBody.onTouchUp(event);
			thisView.mainPagerBody.onTouchUp(event);

			if (thisView.activityStatus.state == thisView.activityStatus.MESSAGES) {
				thisView.messagesSubView.messageListBody.onTouchUp(event);
			} else if (thisView.activityStatus.state == thisView.activityStatus.FRIENDS) {
				friendsSubController.onSingleTapUp(event);
				thisView.friendsSubView.friendListBody.onTouchUp(event);
			} else if (thisView.activityStatus.state == thisView.activityStatus.SHARE) {
				shareSubController.onSingleTapUp(event);
				thisView.shareSubView.shareMessageListBody.onTouchUp(event);
				thisView.shareSubView.groupListBody.onTouchUp(event);
			} else if (thisView.activityStatus.state == thisView.activityStatus.SQUARE) {
				thisView.squareSubView.squareListBody.onTouchUp(event);
			}
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
			ListBody1 listBody = null;

			if (thisView.activityStatus.state == thisView.activityStatus.MESSAGES) {

				listBody = thisView.messagesSubView.messageListBody;
			} else if (thisView.activityStatus.state == thisView.activityStatus.FRIENDS) {
				listBody = thisView.friendsSubView.friendListBody;
			} else if (thisView.activityStatus.state == thisView.activityStatus.SHARE) {
				listBody = thisView.shareSubView.shareMessageListBody;
				// listBody = thisView.shareSubView.shareMessageListBody;
			} else if (thisView.activityStatus.state == thisView.activityStatus.SQUARE) {
				listBody = thisView.squareSubView.squareListBody;
			}
			if (listBody == null) {
				return true;
			}

			if (listBody.bodyStatus.state == listBody.bodyStatus.DRAGGING) {
				listBody.onFling(velocityX, velocityY);
			} else if (listBody.bodyStatus.state == listBody.bodyStatus.FIXED) {
				listBody.onFling(velocityX, velocityY);
			} else {
				Log.i(tag, "bodyStatus error:" + listBody.bodyStatus.state);
			}

			if (thisView.messages_friends_me_PagerBody.bodyStatus.state == thisView.messages_friends_me_PagerBody.bodyStatus.HOMING) {
				thisView.messages_friends_me_PagerBody.onFling(velocityX, velocityY);
			}
			if (thisView.mainPagerBody.bodyStatus.state == thisView.mainPagerBody.bodyStatus.HOMING) {
				thisView.mainPagerBody.onFling(velocityX, velocityY);
			}

			return true;
		}

		public void onLongPress(MotionEvent event) {
			if (thisView.activityStatus.state == thisView.activityStatus.FRIENDS) {
				friendsSubController.onLongPress(event);
			}
			if (thisView.activityStatus.state == thisView.activityStatus.SHARE) {
				shareSubController.onLongPress(event);
			}

		}

		public boolean onDoubleTap(MotionEvent event) {

			return false;
		}

		public boolean onDoubleTapEvent(MotionEvent event) {
			if (thisView.activityStatus.state == thisView.activityStatus.FRIENDS) {
				friendsSubController.onDoubleTapEvent(event);
			}
			return false;
		}

		public boolean onSingleTapUp(MotionEvent event) {
			if (thisView.activityStatus.state == thisView.activityStatus.MESSAGES) {
				messagesSubController.onSingleTapUp(event);
			}
			return false;
		}

		public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
			if (thisView.activityStatus.state == thisView.activityStatus.FRIENDS) {
				friendsSubController.onScroll();
			} else if (thisView.activityStatus.state == thisView.activityStatus.SHARE) {
				shareSubController.onScroll();
			}

			return false;
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

	public void onActivityResult(int requestCode, int resultCode, Intent data2) {
		if (requestCode == R.id.tag_first && resultCode == Activity.RESULT_OK) {
			thisActivity.stopService(new Intent(thisActivity, PushService.class));
			parser = Parser.getInstance();
			parser.save();
			thisActivity.startActivity(new Intent(thisActivity, LoginActivity.class));
			thisActivity.finish();
		} else {
			shareSubController.onActivityResult(requestCode, resultCode, data);
		}

	}

	public int getStatusBarHeight(Context context) {
		Class<?> c = null;
		Object obj = null;
		Field field = null;
		int x = 0, statusBarHeight = 0;
		try {
			c = Class.forName("com.android.internal.R$dimen");
			obj = c.newInstance();
			field = c.getField("status_bar_height");
			x = Integer.parseInt(field.get(obj).toString());
			statusBarHeight = context.getResources().getDimensionPixelSize(x);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		return statusBarHeight;
	}

	public void onBackPressed() {
		shareSubController.onBackPressed();
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		return shareSubController.onKeyDown(keyCode, event);
	}
}
