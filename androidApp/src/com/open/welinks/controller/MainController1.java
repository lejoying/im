package com.open.welinks.controller;

import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.provider.ContactsContract.PhoneLookup;
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

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.location.LocationManagerProxy;
import com.amap.api.location.LocationProviderProxy;
import com.google.gson.Gson;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.client.HttpRequest;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.open.lib.MyLog;
import com.open.lib.viewbody.ListBody1;
import com.open.welinks.R;
import com.open.welinks.LoginActivity;
import com.open.welinks.ScanQRCodeActivity;
import com.open.welinks.customListener.OnDownloadListener;
import com.open.welinks.model.API;
import com.open.welinks.model.Data;
import com.open.welinks.model.Data.UserInformation.User;
import com.open.welinks.model.DataHandler;
import com.open.welinks.model.Parser;
import com.open.welinks.model.ResponseHandlers;
import com.open.welinks.oss.DownloadFile;
import com.open.welinks.service.ConnectionChangeReceiver;
import com.open.welinks.service.PushService;
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

	public LocationManagerProxy mLocationManagerProxy;
	public AMapLocationListener mAMapLocationListener;

	public OnClickListener mOnClickListener;
	public OnDownloadListener downloadListener;
	public ListOnTouchListener listOnTouchListener;

	public ShareSubController1 shareSubController;

	public Handler handler = new Handler();

	public Gson gson = new Gson();

	public String userPhone;

	public boolean isExit = false;

	public static final String CONNECTIVITY_ACTION = "android.net.conn.CONNECTIVITY_CHANGE";
	public ConnectionChangeReceiver connectionChangeReceiver;

	public MainController1(Activity thisActivity) {
		this.context = thisActivity;
		this.thisActivity = thisActivity;

		thisController = this;

		connectionChangeReceiver = new ConnectionChangeReceiver();
		IntentFilter filter = new IntentFilter(CONNECTIVITY_ACTION);
		thisActivity.registerReceiver(connectionChangeReceiver, filter);
	}

	private MainController1 thisController;

	private DataHandler dataHandlers = DataHandler.getInstance();

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

		DataHandler.getUserInfomation();
		// DataHandlers.getUserCurrentAllGroup();
		getIntimatefriends();

		dataHandlers.sendShareMessage();

		requestLocation();

		getContacts();
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
		// thisActivity.unregisterReceiver(connectionChangeReceiver);
		mLocationManagerProxy.removeUpdates(mAMapLocationListener);
		mLocationManagerProxy.destroy();
		thisView.shareSubView.thisController.onDestroy();
	}

	public void getIntimatefriends() {
		log.e(tag, "刷新好友分组getIntimatefriends");
		data = parser.check();
		RequestParams params = new RequestParams();
		User currentUser = data.userInformation.currentUser;
		params.addBodyParameter("accessKey", currentUser.accessKey);
		params.addBodyParameter("phone", currentUser.phone);

		HttpUtils http = new HttpUtils();
		ResponseHandlers responseHandlers = ResponseHandlers.getInstance();

		http.send(HttpRequest.HttpMethod.POST, API.RELATION_GETINTIMATEFRIENDS, params, responseHandlers.getIntimateFriends);
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

		mAMapLocationListener = new AMapLocationListener() {

			@Override
			public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
			}

			@Override
			public void onProviderEnabled(String arg0) {
			}

			@Override
			public void onProviderDisabled(String arg0) {
			}

			@Override
			public void onLocationChanged(Location arg0) {
			}

			@Override
			public void onLocationChanged(AMapLocation aMapLocation) {
				mLocationManagerProxy.removeUpdates(mAMapLocationListener);
				mLocationManagerProxy.destroy();
				if (aMapLocation != null && aMapLocation.getAMapException().getErrorCode() == 0) {
					User currentUser = data.userInformation.currentUser;
					currentUser.address = aMapLocation.getAddress();
					currentUser.latitude = String.valueOf(aMapLocation.getLatitude());
					currentUser.longitude = String.valueOf(aMapLocation.getLongitude());
					// log.e(currentUser.longitude + "," + currentUser.latitude);
					modifyLocation();
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

	public void requestLocation() {
		mLocationManagerProxy = LocationManagerProxy.getInstance(thisActivity);
		mLocationManagerProxy.setGpsEnable(true);
		mLocationManagerProxy.requestLocationData(LocationProviderProxy.AMapNetwork, 60 * 1000 * 10, 1000, mAMapLocationListener);
	}

	public void modifyLocation() {
		data = parser.check();
		HttpUtils httpUtils = new HttpUtils();
		RequestParams params = new RequestParams();
		User currentUser = data.userInformation.currentUser;
		params.addBodyParameter("phone", currentUser.phone);
		params.addBodyParameter("accessKey", currentUser.accessKey);
		params.addBodyParameter("longitude", currentUser.longitude);
		params.addBodyParameter("latitude", currentUser.latitude);
		params.addBodyParameter("address", currentUser.address);
		ResponseHandlers responseHandlers = ResponseHandlers.getInstance();
		httpUtils.send(HttpMethod.POST, API.ACCOUNT_MODIFYLOCATION, params, responseHandlers.account_modifylocation);
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
				// listBody = thisView.shareSubView.shareMessageListBody;
				// if (thisView.shareSubView.isShowGroupDialog) {
				// listBody = thisView.shareSubView.groupListBody;
				// }
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
			exitApplication();
		} else if (requestCode == R.id.tag_second) {
		} else {
			shareSubController.onActivityResult(requestCode, resultCode, data);
		}

	}

	public void exitApplication() {
		data = parser.check();
		data.userInformation.currentUser.phone = "";
		data.userInformation.currentUser.accessKey = "";
		data.userInformation.isModified = true;
		parser.save();
		thisActivity.stopService(new Intent(thisActivity, PushService.class));
		if (this.connectionChangeReceiver != null) {
			thisActivity.unregisterReceiver(this.connectionChangeReceiver);
			connectionChangeReceiver = null;
		}
		DataHandler.clearData();
		thisActivity.finish();
		thisActivity.startActivity(new Intent(thisActivity, LoginActivity.class));
	}

	boolean isShowDialg = false;

	public void finish() {
		if (this.connectionChangeReceiver != null) {
			thisActivity.unregisterReceiver(this.connectionChangeReceiver);
			connectionChangeReceiver = null;
		}
	}

	class Contact {
		public String nickName;
		public String head;
	}

	Map<String, Contact> contacts = new HashMap<String, Contact>();

	public void getContacts() {
		new Thread(new Runnable() {

			@Override
			public void run() {
				ContentResolver contentResolver = thisActivity.getContentResolver();
				try {
					Cursor cursor = contentResolver.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
					while (cursor.moveToNext()) {

						int nameFieldColumnIndex = cursor.getColumnIndex(PhoneLookup.DISPLAY_NAME);
						String nickName = cursor.getString(nameFieldColumnIndex);
						String ContactId = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
						Cursor phone = contentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "=" + ContactId, null, null);

						while (phone.moveToNext()) {
							String phoneNumber = phone.getString(phone.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
							if (phoneNumber.indexOf("+86") == 0) {
								phoneNumber = phoneNumber.substring(3);
							}
							phoneNumber = phoneNumber.replaceAll(" ", "");
							if (phoneNumber.indexOf("1") == 0 && phoneNumber.length() == 11) {
								Contact contact = new Contact();
								contact.nickName = nickName;
								contact.head = "abc";
								contacts.put(phoneNumber, contact);
								// log.e(phoneNumber.length() + "---------------------------------" + phoneNumber);
							}
							// TODO contact photo
							// Uri uriNumber2Contacts = Uri.parse("content://com.android.contacts/" + "data/phones/filter/" + PhoneNumber);
							// final Cursor cursorCantacts = contentResolver.query(uriNumber2Contacts, null, null, null, null);
							// if (cursorCantacts.getCount() > 0) {
							// cursorCantacts.moveToFirst();
							// Long contactID = cursorCantacts.getLong(cursorCantacts.getColumnIndex("contact_id"));
							// final Uri uri = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, contactID);
							// final InputStream input = ContactsContract.Contacts.openContactPhotoInputStream(contentResolver, uri);
							// }
						}
						phone.close();
					}
					cursor.close();
					log.e("获取通讯录成功");
					if (contacts.size() > 0) {
						updateContactServer();
					}
				} catch (Exception e) {
					e.printStackTrace();
					Log.e("Exception", e.toString());
				}
			}
		}).start();
	}

	public ResponseHandlers responseHandlers = ResponseHandlers.getInstance();

	public void updateContactServer() {
		log.e("开始上传通讯录");
		String contactString = gson.toJson(contacts);
		RequestParams params = new RequestParams();
		HttpUtils httpUtils = new HttpUtils();
		User currentUser = data.userInformation.currentUser;
		params.addBodyParameter("phone", currentUser.phone);
		params.addBodyParameter("accessKey", currentUser.accessKey);
		params.addBodyParameter("contact", contactString);

		httpUtils.send(HttpMethod.POST, API.RELATION_UPDATECONTACT, params, responseHandlers.updateContactCallBack);
	}
}
