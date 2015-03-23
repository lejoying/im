package com.open.welinks.controller;

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
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.gson.Gson;
import com.open.lib.MyLog;
import com.open.lib.viewbody.ListBody1;
import com.open.welinks.R;
import com.open.welinks.ScanQRCodeActivity;
import com.open.welinks.customListener.OnDownloadListener;
import com.open.welinks.model.Data;
import com.open.welinks.model.Parser;
import com.open.welinks.oss.DownloadFile;
import com.open.welinks.view.MainView1;
import com.open.welinks.view.ViewManage;

public class MainController1 {

	public Data data = Data.getInstance();
	public Parser parser = Parser.getInstance();

	public String tag = "MainController";
	public MyLog log = new MyLog(tag, true);

	public MainView1 thisView;
	public Context context;
	public Activity thisActivity;

	public GestureDetector mGesture;
	public GestureDetector mListGesture;

	public OnClickListener mOnClickListener;
	public OnDownloadListener downloadListener;
	public ListOnTouchListener listOnTouchListener;

	public ShareSubController1 shareSubController;

	public Handler handler = new Handler();

	public Gson gson = new Gson();

	public String userPhone;

	public boolean isExit = false;

	public MainController1(Activity thisActivity) {
		this.context = thisActivity;
		this.thisActivity = thisActivity;

		thisController = this;
	}

	private MainController1 thisController;

	public void oncreate() {
		String phone = thisActivity.getIntent().getStringExtra("phone");
		if (phone != null && !"".equals(phone)) {
			userPhone = phone;
		}
		mGesture = new GestureDetector(thisActivity, new GestureListener());
		mListGesture = new GestureDetector(thisActivity, new GestureListener());
		parser.check();

		thisView.shareSubView.showShareMessages();
		// thisView.showGroupMembers(thisView.groupMembersListContentView);

		data.tempData.statusBarHeight = ViewManage.getStatusBarHeight(thisActivity);
	}

	public void onResume() {
		data.localStatus.thisActivityName = "MainActivity";
		thisView.shareSubView.dismissGroupDialog();
		thisView.shareSubView.dismissReleaseShareDialogView();
		thisView.shareSubView.businessCardPopView.dismissUserCardDialogView();
		// thisView.shareSubView.onResume();
		// thisView.messagesSubView.onResume();

		data = thisController.parser.check();
		thisView.userTopbarNameView.setText(data.userInformation.currentUser.nickName);
		int length = data.userInformation.currentUser.nickName.length();
		int left = (int) (thisView.textSize * length);
		RelativeLayout.LayoutParams params = (android.widget.RelativeLayout.LayoutParams) thisView.botton.getLayoutParams();
		params.leftMargin = left;
	}

	public void onPause() {
		thisView.shareSubView.thisController.onPause();
	}

	public void onDestroy() {
		thisView.shareSubView.thisController.onDestroy();
	}

	public void initializeListeners() {

		downloadListener = new OnDownloadListener() {

			@Override
			public void onLoading(DownloadFile instance, int precent, int status) {
			}

			@Override
			public void onSuccess(DownloadFile instance, int status) {
				thisView.imageLoader.displayImage("file://" + instance.path, (ImageView) instance.view, thisView.shareSubView.options);
			}

			@Override
			public void onFailure(DownloadFile instance, int status) {
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
					thisView.mainPagerBody.flipTo(1);
				} else if (view.equals(thisView.shareMenuView)) {
					thisView.mainPagerBody.active();
					thisView.messages_friends_me_PagerBody.inActive();
					thisView.mainPagerBody.flipTo(0);
				} else if (view.equals(thisView.messages_friends_me_menuView)) {
					thisView.mainPagerBody.active();
					thisView.messages_friends_me_PagerBody.inActive();
					thisView.mainPagerBody.flipTo(2);
				}

				if (view.equals(thisView.scannerCodeView)) {
					Intent intent = new Intent(thisActivity, ScanQRCodeActivity.class);
					thisActivity.startActivity(intent);
				} else if (view.getTag() != null) {

					Log.d(tag, (String) view.getTag());
				}
			}
		};

		listOnTouchListener = new ListOnTouchListener();

		this.thisController.shareSubController.initializeListeners();
	}

	public void bindEvent() {

		thisView.scannerCodeView.setOnClickListener(mOnClickListener);

		thisView.friendsMenuView.setOnClickListener(mOnClickListener);
		thisView.messagesMenuView.setOnClickListener(mOnClickListener);
		thisView.meMenuView.setOnClickListener(mOnClickListener);

		thisView.squareMenuView.setOnClickListener(mOnClickListener);
		thisView.shareMenuView.setOnClickListener(mOnClickListener);
		thisView.messages_friends_me_menuView.setOnClickListener(mOnClickListener);

		this.thisController.shareSubController.bindEvent();
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
			} else if (thisView.activityStatus.state == thisView.activityStatus.FRIENDS) {
			} else if (thisView.activityStatus.state == thisView.activityStatus.SHARE) {
				thisView.shareSubView.shareMessageListBody.onTouchDown(event);
				thisView.shareSubView.groupListBody.onTouchDown(event);
				thisView.shareSubView.releaseChannelListBody.onTouchDown(event);
			} else if (thisView.activityStatus.state == thisView.activityStatus.SQUARE) {
			}

		} else if (motionEvent == MotionEvent.ACTION_MOVE) {
			thisView.messages_friends_me_PagerBody.onTouchMove(event);
			thisView.mainPagerBody.onTouchMove(event);

			if (thisView.activityStatus.state == thisView.activityStatus.MESSAGES) {
			} else if (thisView.activityStatus.state == thisView.activityStatus.FRIENDS) {
			} else if (thisView.activityStatus.state == thisView.activityStatus.SHARE) {
				thisView.shareSubView.shareMessageListBody.onTouchMove(event);
				thisView.shareSubView.groupListBody.onTouchMove(event);
				thisView.shareSubView.releaseChannelListBody.onTouchMove(event);
			} else if (thisView.activityStatus.state == thisView.activityStatus.SQUARE) {
			}
		} else if (motionEvent == MotionEvent.ACTION_UP) {
			thisView.messages_friends_me_PagerBody.onTouchUp(event);
			thisView.mainPagerBody.onTouchUp(event);

			if (thisView.activityStatus.state == thisView.activityStatus.MESSAGES) {
			} else if (thisView.activityStatus.state == thisView.activityStatus.FRIENDS) {
			} else if (thisView.activityStatus.state == thisView.activityStatus.SHARE) {
				shareSubController.onSingleTapUp(event);
				thisView.shareSubView.shareMessageListBody.onTouchUp(event);
				thisView.shareSubView.groupListBody.onTouchUp(event);
				thisView.shareSubView.releaseChannelListBody.onTouchUp(event);
			} else if (thisView.activityStatus.state == thisView.activityStatus.ME) {
			} else if (thisView.activityStatus.state == thisView.activityStatus.SQUARE) {
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
			} else if (motionEvent == MotionEvent.ACTION_MOVE) {
				Log.d(tag, "List on touch move");
			} else if (motionEvent == MotionEvent.ACTION_UP) {
				Log.d(tag, "List on touch up");

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
			} else if (thisView.activityStatus.state == thisView.activityStatus.FRIENDS) {
			} else if (thisView.activityStatus.state == thisView.activityStatus.SHARE) {
				listBody = thisView.shareSubView.shareMessageListBody;
			} else if (thisView.activityStatus.state == thisView.activityStatus.SQUARE) {
			}
			if (listBody != null) {
				if (listBody.bodyStatus.state == listBody.bodyStatus.DRAGGING) {
					listBody.onFling(velocityX, velocityY);
				} else if (listBody.bodyStatus.state == listBody.bodyStatus.FIXED) {
					listBody.onFling(velocityX, velocityY);
				} else {
					Log.i(tag, "bodyStatus error:" + listBody.bodyStatus.state);
				}
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
			}
			if (thisView.activityStatus.state == thisView.activityStatus.SHARE) {
				shareSubController.onLongPress(event);
			}
		}

		public boolean onDoubleTap(MotionEvent event) {
			// log.e("onDoubleTap");
			return false;
		}

		public boolean onDoubleTapEvent(MotionEvent event) {
			if (thisView.activityStatus.state == thisView.activityStatus.FRIENDS) {
			}
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
			if (thisView.activityStatus.state == thisView.activityStatus.FRIENDS) {
			} else if (thisView.activityStatus.state == thisView.activityStatus.SHARE) {
				shareSubController.onScroll();
			} else if (thisView.activityStatus.state == thisView.activityStatus.SQUARE) {
			} else if (thisView.activityStatus.state == thisView.activityStatus.ME) {
			} else if (thisView.activityStatus.state == thisView.activityStatus.MESSAGES) {
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

	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == R.id.tag_first && resultCode == Activity.RESULT_OK) {
			// exitApplication();
		} else if (requestCode == R.id.tag_second) {
		} else {
			shareSubController.onActivityResult(requestCode, resultCode, data);
		}

	}

	boolean isShowDialg = false;

	public void finish() {
	}
}
