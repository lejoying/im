package com.open.welinks.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.amap.api.cloud.model.AMapCloudException;
import com.amap.api.cloud.model.CloudItem;
import com.amap.api.cloud.model.CloudItemDetail;
import com.amap.api.cloud.search.CloudResult;
import com.amap.api.cloud.search.CloudSearch;
import com.amap.api.cloud.search.CloudSearch.OnCloudSearchListener;
import com.amap.api.cloud.search.CloudSearch.Query;
import com.amap.api.cloud.search.CloudSearch.SearchBound;
import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.location.LocationManagerProxy;
import com.amap.api.location.LocationProviderProxy;
import com.amap.api.maps.model.LatLng;
import com.facebook.rebound.SimpleSpringListener;
import com.facebook.rebound.Spring;
import com.facebook.rebound.SpringUtil;
import com.google.gson.Gson;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.client.HttpRequest;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.open.lib.MyLog;
import com.open.lib.viewbody.ListBody1;
import com.open.welinks.LoginActivity;
import com.open.welinks.R;
import com.open.welinks.ScanQRCodeActivity;
import com.open.welinks.customListener.OnDownloadListener;
import com.open.welinks.model.API;
import com.open.welinks.model.Constant;
import com.open.welinks.model.Data;
import com.open.welinks.model.Data.Relationship.Circle;
import com.open.welinks.model.Data.Relationship.Group;
import com.open.welinks.model.Data.UserInformation.User;
import com.open.welinks.model.DataHandler;
import com.open.welinks.model.Parser;
import com.open.welinks.model.ResponseHandlers;
import com.open.welinks.oss.DownloadFile;
import com.open.welinks.service.ConnectionChangeReceiver;
import com.open.welinks.service.PushService;
import com.open.welinks.view.MainView;
import com.open.welinks.view.ViewManage;

public class MainController {

	public Data data = Data.getInstance();
	public Parser parser = Parser.getInstance();

	public String tag = "MainController";
	public MyLog log = new MyLog(tag, true);

	public MainView thisView;
	public Context context;
	public Activity thisActivity;

	public GestureDetector mGesture;
	public GestureDetector mListGesture;

	public LocationManagerProxy mLocationManagerProxy;
	public AMapLocationListener mAMapLocationListener;
	public CloudSearch mCloudSearch;
	public ArrayList<CloudItem> mCloudItems;
	public Query mQuery;
	// public LatLng mLatLng;

	public OnCloudSearchListener mCloudSearchListener;
	public OnClickListener mOnClickListener;
	public OnDownloadListener downloadListener;

	public ListOnTouchListener listOnTouchListener;

	public SquareSubController squareSubController;
	public ShareSubController shareSubController;
	public MessagesSubController messagesSubController;
	public FriendsSubController friendsSubController;
	public MeSubController meSubController;

	Handler handler = new Handler();
	String url_userInfomation = "http://www.we-links.com/api2/account/getuserinfomation";
	String url_intimateFriends = "http://www.we-links.com/api2/relation/intimatefriends";

	Gson gson = new Gson();

	public String userPhone;

	public boolean isExit = false;

	public static final String CONNECTIVITY_ACTION = "android.net.conn.CONNECTIVITY_CHANGE";
	public ConnectionChangeReceiver connectionChangeReceiver;

	public MainController(Activity thisActivity) {
		this.context = thisActivity;
		this.thisActivity = thisActivity;

		thisController = this;

		connectionChangeReceiver = new ConnectionChangeReceiver();
		IntentFilter filter = new IntentFilter(CONNECTIVITY_ACTION);
		thisActivity.registerReceiver(connectionChangeReceiver, filter);
	}

	// public BaseSpringSystem mSpringSystem = SpringSystem.create();
	// public Spring mScaleSpring = mSpringSystem.createSpring();
	public ExampleSpringListener mSpringListener = new ExampleSpringListener();
	private MainController thisController;

	private DataHandler dataHandlers = DataHandler.getInstance();

	public void oncreate() {
		String phone = thisActivity.getIntent().getStringExtra("phone");
		if (phone != null && !"".equals(phone)) {
			userPhone = phone;
		}
		mGesture = new GestureDetector(thisActivity, new GestureListener());
		mListGesture = new GestureDetector(thisActivity, new GestureListener());
		parser.check();

		thisView.friendsSubView.showCircles();
		thisView.messagesSubView.showMessagesSequence();

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
		thisView.meSubView.mMePageAppIconScaleSpring.addListener(mSpringListener);
		thisView.shareSubView.dismissGroupDialog();
		thisView.shareSubView.dismissReleaseShareDialogView();
		thisView.friendsSubView.businessCardPopView.dismissUserCardDialogView();
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
		thisView.meSubView.mMePageAppIconScaleSpring.removeListener(mSpringListener);
	}

	public void onDestroy() {
		mLocationManagerProxy.removeUpdates(mAMapLocationListener);
		mLocationManagerProxy.destroy();
		thisView.messagesSubView.onDestroy();
		thisView.squareSubView.thisController.onDestroy();

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
		log.e(tag, "刷新好友分组getIntimatefriends");
		RequestParams params = new RequestParams();
		data = parser.check();
		User user = data.userInformation.currentUser;
		params.addBodyParameter("accessKey", user.accessKey);
		params.addBodyParameter("phone", user.phone);

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
				} else if (view.equals(thisView.friendsSubView.deleteCircleView)) {
					thisView.friendsSubView.thisController.deleteCircle(String.valueOf((Integer) (view.getTag(R.id.tag_first))));
					thisView.friendsSubView.dismissCircleSettingDialog();
				} else if (view.equals(thisView.friendsSubView.createCircleView)) {
					thisView.friendsSubView.thisController.createCircle();
					thisView.friendsSubView.dismissCircleSettingDialog();
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
				if (aMapLocation != null && aMapLocation.getAMapException().getErrorCode() == 0) {
					data.userInformation.currentUser.address = aMapLocation.getAddress();
					data.userInformation.currentUser.latitude = String.valueOf(aMapLocation.getLatitude());
					data.userInformation.currentUser.longitude = String.valueOf(aMapLocation.getLongitude());
					modifyLocation();
				}

			}
		};
		mCloudSearchListener = new OnCloudSearchListener() {

			@Override
			public void onCloudSearched(CloudResult result, int rCode) {
				if (rCode == 0) {
					if (result != null && result.getQuery() != null) {
						if (result.getQuery().equals(mQuery)) {
							mCloudItems = result.getClouds();
							parser.check();
							if (data.relationship != null) {
								if (data.relationship.squares == null) {
									data.relationship.squares = new ArrayList<String>();
								}
								for (CloudItem item : mCloudItems) {
									Map<String, Object> map = new HashMap<String, Object>();
									LatLng point2 = new LatLng(item.getLatLonPoint().getLatitude(), item.getLatLonPoint().getLongitude());
									map.put("location", item.getLatLonPoint());
									map.put("name", item.getTitle());
									map.put("address", item.getSnippet());
									// map.put("distance", (int) AMapUtils.calculateLineDistance(mLatLng, point2));
									Iterator<?> iter = item.getCustomfield().entrySet().iterator();
									while (iter.hasNext()) {
										Entry<?, ?> entry = (Entry<?, ?>) iter.next();
										map.put(entry.getKey().toString(), entry.getValue());
									}
									addSquare(map);
								}
								// thisView.squareSubView.thisController.setCurrentSquare();
							}
						}
					}
				}

			}

			@Override
			public void onCloudItemDetailSearched(CloudItemDetail detail, int rCode) {
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

		this.thisController.squareSubController.bindEvent();
		this.thisController.shareSubController.bindEvent();
		this.thisController.messagesSubController.bindEvent();
		this.thisController.friendsSubController.bindEvent();
		this.thisController.meSubController.bindEvent();
	}

	public void requestLocation() {
		mLocationManagerProxy = LocationManagerProxy.getInstance(thisActivity);
		mLocationManagerProxy.setGpsEnable(true);
		mLocationManagerProxy.requestLocationData(LocationProviderProxy.AMapNetwork, 60 * 1000 * 10, 1000, mAMapLocationListener);
	}

	public void searchNearBySquare() {
		mCloudSearch = new CloudSearch(thisActivity);
		mCloudSearch.setOnCloudSearchListener(mCloudSearchListener);

		List<com.amap.api.cloud.model.LatLonPoint> points = new ArrayList<com.amap.api.cloud.model.LatLonPoint>();
		points.add(new com.amap.api.cloud.model.LatLonPoint(5.965754, 70.136719));
		points.add(new com.amap.api.cloud.model.LatLonPoint(56.170023, 140.097656));
		try {
			mQuery = new Query(Constant.SQUARETABLEID, "", new SearchBound(points));
		} catch (AMapCloudException e) {
			e.printStackTrace();
		}
		mQuery.setPageSize(20);
		mCloudSearch.searchCloudAsyn(mQuery);

	}

	public void addSquare(Map<String, Object> map) {
		data = parser.check();
		Group group = data.relationship.new Group();

		group.gid = Integer.valueOf((String) map.get("gid"));
		group.name = (String) map.get("name");
		group.icon = (String) map.get("icon");
		group.description = (String) map.get("description");
		group.distance = (Integer) map.get("distance");
		if (map.get("conver") != null) {
			group.cover = (String) map.get("conver");
		}

		if (!data.relationship.squares.contains(group.gid + "")) {
			group.currentBoard = (String) map.get("sid");
			data.relationship.squares.add(group.gid + "");
			data.relationship.groupsMap.put(group.gid + "", group);
		} else {
			Group group2 = data.relationship.groupsMap.get(group.gid + "");
			group2.name = (String) map.get("name");
			group2.icon = (String) map.get("icon");
			group2.description = (String) map.get("description");
			group2.distance = (Integer) map.get("distance");
			group2.currentBoard = (String) map.get("sid");
			if (map.get("conver") != null) {
				group2.cover = (String) map.get("conver");
				// if (data.localStatus.localData.currentSelectedSquare.equals(group2.gid + "")) {
				// // thisView.squareSubView.setConver();
				// }
			}
		}

		data.relationship.isModified = true;
	}

	public void modifyLocation() {
		data = parser.check();
		HttpUtils httpUtils = new HttpUtils();
		RequestParams params = new RequestParams();
		params.addBodyParameter("phone", data.userInformation.currentUser.phone);
		params.addBodyParameter("accessKey", data.userInformation.currentUser.accessKey);
		params.addBodyParameter("longitude", data.userInformation.currentUser.longitude);
		params.addBodyParameter("latitude", data.userInformation.currentUser.latitude);
		params.addBodyParameter("address", data.userInformation.currentUser.address);
		ResponseHandlers responseHandlers = ResponseHandlers.getInstance();
		httpUtils.send(HttpMethod.POST, API.ACCOUNT_MODIFYLOCATION, params, responseHandlers.account_modifylocation);
	}

	public void chackLBSAccount() {
		HttpUtils httpUtils = new HttpUtils();
		RequestParams params = new RequestParams();
		params.addQueryStringParameter("tableid", Constant.ACCOUNTTABLEID);
		params.addQueryStringParameter("filter", "phone:" + data.userInformation.currentUser.phone);
		params.addQueryStringParameter("key", Constant.LBS_SAVE_KSY);
		ResponseHandlers responseHandlers = ResponseHandlers.getInstance();
		httpUtils.send(HttpMethod.GET, API.LBS_DATA_SEARCH, params, responseHandlers.lbsdata_search);
	}

	public void creataLBSAccount() {
		final User user = data.userInformation.currentUser;
		LBSAccountData data = new LBSAccountData();
		data._name = user.nickName;
		data._location = user.longitude + "," + user.latitude;
		data._address = this.data.userInformation.currentUser.address;
		data.phone = user.phone;
		data.sex = user.sex;
		data.head = user.head;
		data.mainBusiness = user.mainBusiness;
		data.lastlogintime = user.lastLoginTime;
		HttpUtils httpUtils = new HttpUtils();
		RequestParams params = new RequestParams();
		params.addBodyParameter("key", Constant.LBS_SAVE_KSY);
		params.addBodyParameter("tableid", Constant.ACCOUNTTABLEID);
		params.addBodyParameter("loctype", "2");
		params.addBodyParameter("data", gson.toJson(data));
		ResponseHandlers responseHandlers = ResponseHandlers.getInstance();
		httpUtils.send(HttpMethod.POST, API.LBS_DATA_CREATE, params, responseHandlers.lbsdata_create);

	}

	public void modifyLBSAccount(final String id) {
		final User user = data.userInformation.currentUser;
		LBSAccountData data = new LBSAccountData();
		data._id = id;
		data._name = user.nickName;
		data._location = user.longitude + "," + user.latitude;
		data._address = this.data.userInformation.currentUser.address;
		data.phone = user.phone;
		data.sex = user.sex;
		if (user.age != null && !"".equals(user.age)) {
			Pattern pattern = Pattern.compile("[0-9]*");
			Matcher isNum = pattern.matcher(user.age);
			if (isNum.matches()) {
				data.age = user.age;
			} else {
				data.age = "20";
			}
		} else {
			data.age = "20";
		}
		data.head = user.head;
		data.mainBusiness = user.mainBusiness;
		data.lastlogintime = user.lastLoginTime;

		HttpUtils httpUtils = new HttpUtils();
		RequestParams params = new RequestParams();
		params.addBodyParameter("key", Constant.LBS_SAVE_KSY);
		params.addBodyParameter("tableid", Constant.ACCOUNTTABLEID);
		params.addBodyParameter("loctype", "2");
		params.addBodyParameter("data", gson.toJson(data));
		ResponseHandlers responseHandlers = ResponseHandlers.getInstance();
		httpUtils.send(HttpMethod.POST, API.LBS_DATA_UPDATA, params, responseHandlers.lbsdata_updata);
	}

	public void creataLBSGroup(Group group, String address) {
		LBSGroupData data = new LBSGroupData();
		data._name = group.name;
		data._location = group.longitude + "," + group.latitude;
		data._address = address;
		data.icon = group.icon;
		data.gid = String.valueOf(group.gid);
		data.description = group.description;
		data.background = group.background;
		data.gtype = "group";
		HttpUtils httpUtils = new HttpUtils();
		RequestParams params = new RequestParams();
		params.addBodyParameter("key", Constant.LBS_SAVE_KSY);
		params.addBodyParameter("tableid", Constant.GROUPTABLEID);
		params.addBodyParameter("loctype", "2");
		params.addBodyParameter("data", gson.toJson(data));
		ResponseHandlers responseHandlers = ResponseHandlers.getInstance();
		httpUtils.send(HttpMethod.POST, API.LBS_DATA_CREATE, params, responseHandlers.lbsdata_create);
	}

	public class LBSAccountData {
		public String _id;
		public String _name;
		public String _location;
		public String _address;
		public String phone;
		public String sex;
		public String age;
		public String head;
		public String mainBusiness;
		public String lastlogintime;
	}

	public class LBSGroupData {
		public String _id;
		public String _name;
		public String _location;
		public String _address;
		public String icon;
		public String gid;
		public String description;
		public String gtype;
		public String background;
		public String conver;

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
				thisView.shareSubView.releaseChannelListBody.onTouchDown(event);
			} else if (thisView.activityStatus.state == thisView.activityStatus.SQUARE) {
				thisView.squareSubView.locationListBody.onTouchDown(event);
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
				thisView.shareSubView.releaseChannelListBody.onTouchMove(event);
			} else if (thisView.activityStatus.state == thisView.activityStatus.SQUARE) {
				thisView.squareSubView.locationListBody.onTouchMove(event);
			}
		} else if (motionEvent == MotionEvent.ACTION_UP) {
			thisView.messages_friends_me_PagerBody.onTouchUp(event);
			thisView.mainPagerBody.onTouchUp(event);

			if (thisView.activityStatus.state == thisView.activityStatus.MESSAGES) {
				messagesSubController.onSingleTapUp(event);
				thisView.messagesSubView.messageListBody.onTouchUp(event);
			} else if (thisView.activityStatus.state == thisView.activityStatus.FRIENDS) {
				friendsSubController.onSingleTapUp(event);
				thisView.friendsSubView.friendListBody.onTouchUp(event);
			} else if (thisView.activityStatus.state == thisView.activityStatus.SHARE) {
				shareSubController.onSingleTapUp(event);
				thisView.shareSubView.shareMessageListBody.onTouchUp(event);
				thisView.shareSubView.groupListBody.onTouchUp(event);
				thisView.shareSubView.releaseChannelListBody.onTouchUp(event);
			} else if (thisView.activityStatus.state == thisView.activityStatus.ME) {
				meSubController.onSingleTapUp(event);
			} else if (thisView.activityStatus.state == thisView.activityStatus.SQUARE) {
				thisView.squareSubView.locationListBody.onTouchUp(event);
				squareSubController.onSingleTapUp(event);
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
				// if (thisView.shareSubView.isShowGroupDialog) {
				// listBody = thisView.shareSubView.groupListBody;
				// }
			} else if (thisView.activityStatus.state == thisView.activityStatus.SQUARE) {
				listBody = thisView.squareSubView.locationListBody;
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
				friendsSubController.onLongPress(event);
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
				friendsSubController.onDoubleTapEvent(event);
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
				friendsSubController.onScroll();
			} else if (thisView.activityStatus.state == thisView.activityStatus.SHARE) {
				shareSubController.onScroll();
			} else if (thisView.activityStatus.state == thisView.activityStatus.SQUARE) {
				squareSubController.onScroll();
			} else if (thisView.activityStatus.state == thisView.activityStatus.ME) {
				meSubController.onScroll();
			} else if (thisView.activityStatus.state == thisView.activityStatus.MESSAGES) {
				messagesSubController.onScroll();
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
			exit();
		} else if (requestCode == R.id.tag_second) {
			messagesSubController.onActivityResult(requestCode, resultCode, data2);
		} else {
			shareSubController.onActivityResult(requestCode, resultCode, data2);
		}

	}

	public void exit() {
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

	public boolean onKeyUp(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (isExit) {
				thisActivity.finish();
			} else {
				if (!shareSubController.thisView.isShowGroupDialog && !isShowDialg) {
					Toast.makeText(thisActivity, "再按一次退出程序", Toast.LENGTH_SHORT).show();
					isExit = true;
					new Thread() {
						@Override
						public void run() {
							try {
								sleep(2000);
								isExit = false;
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
							super.run();
						}
					}.start();
				}
			}
		}
		return true;
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		isShowDialg = false;
		boolean flag = shareSubController.onKeyDown(keyCode, event);
		// if (flag) {
		// flag = squareSubController.onKeyDown(keyCode, event);
		// }
		if (!flag) {
			isShowDialg = true;
		}
		return flag;
	}

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
		params.addBodyParameter("phone", data.userInformation.currentUser.phone);
		params.addBodyParameter("accessKey", data.userInformation.currentUser.accessKey);
		params.addBodyParameter("contact", contactString);

		httpUtils.send(HttpMethod.POST, API.RELATION_UPDATECONTACT, params, responseHandlers.updateContactCallBack);
	}
}
