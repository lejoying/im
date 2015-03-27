package com.open.welinks.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.provider.ContactsContract.PhoneLookup;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.MarginLayoutParams;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.Toast;

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
import com.amap.api.maps.AMap.OnCameraChangeListener;
import com.amap.api.maps.AMapUtils;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.LocationSource.OnLocationChangedListener;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.LatLng;
import com.amap.api.services.core.PoiItem;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.GeocodeSearch.OnGeocodeSearchListener;
import com.amap.api.services.geocoder.RegeocodeQuery;
import com.amap.api.services.geocoder.RegeocodeResult;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.open.lib.HttpClient;
import com.open.lib.MyLog;
import com.open.welinks.MainActivity;
import com.open.welinks.R;
import com.open.welinks.ShareMessageDetailActivity;
import com.open.welinks.ShareReleaseImageTextActivity;
import com.open.welinks.customListener.OnDownloadListener;
import com.open.welinks.customView.Alert;
import com.open.welinks.customView.Alert.AlertInputDialog;
import com.open.welinks.customView.Alert.AlertInputDialog.OnDialogClickListener;
import com.open.welinks.customView.ThreeChoicesView.OnItemClickListener;
import com.open.welinks.model.API;
import com.open.welinks.model.Constant;
import com.open.welinks.model.Data;
import com.open.welinks.model.Data.Boards.Score;
import com.open.welinks.model.Data.Boards.ShareMessage;
import com.open.welinks.model.Data.Relationship.Friend;
import com.open.welinks.model.Data.Relationship.Group;
import com.open.welinks.model.Data.UserInformation.User;
import com.open.welinks.model.LBSHandler;
import com.open.welinks.model.Parser;
import com.open.welinks.model.ResponseHandlers;
import com.open.welinks.model.SubData;
import com.open.welinks.model.TaskManageHolder;
import com.open.welinks.oss.DownloadFile;
import com.open.welinks.utils.Base64Coder;
import com.open.welinks.view.NearbyView;
import com.open.welinks.view.NearbyView.SendShare;

public class NearbyController {

	public Data data = Data.getInstance();
	public SubData subData = SubData.getInstance();
	public TaskManageHolder taskManageHolder = TaskManageHolder.getInstance();

	public String tag = "NearbyController";
	public MyLog log = new MyLog(tag, true);

	public NearbyView thisView;
	public NearbyController thisController;
	public Activity thisActivity;

	public LocationManagerProxy mLocationManagerProxy;
	public SearchBound bound;
	public Query mQuery;
	public CloudSearch mCloudSearch;
	public ArrayList<CloudItem> mCloudItems;
	public String type;

	public OnClickListener mOnClickListener;
	public OnCloudSearchListener mCloudSearchListener;
	public AMapLocationListener mAMapLocationListener;
	public OnItemClickListener mOnItemClickListener;
	public OnDownloadListener downloadListener;
	public OnScrollListener mOnScrollListener;
	public android.widget.AdapterView.OnItemClickListener mListOnItemClickListener;
	public OnTouchListener mOnTouchListener;

	public HttpClient httpClient = HttpClient.getInstance();
	public ResponseHandlers responseHandlers = ResponseHandlers.getInstance();
	public ImageLoader imageLoader = ImageLoader.getInstance();

	public Gson gson = new Gson();

	public String mTableId;
	public ArrayList<Object> mInfomations;
	public AMapLocation mAmapLocation;

	public double latitude, longitude;
	public int searchRadius = 50000;
	public int tempSearchRadius = 50000;
	public long searchTime = 0;
	public long tempSearchTime = 0;

	public int[] radius = { 1500, 5000, 10000, 50000 };
	public long[] times = { 3600000, 86400000, 259200000, 0 };

	public int RESULTCODE = 0x1;

	public Status status;

	public enum Status {
		account, group, newest, hottest
	}

	public NearbyController(Activity thisActivity) {
		thisController = this;
		this.thisActivity = thisActivity;

		getContacts();
	}

	public void onCreate() {
		thisView.viewManage.nearbyView = thisView;
		type = thisActivity.getIntent().getStringExtra("type");
		if ("account".equals(type)) {
			status = Status.account;
			mTableId = Constant.ACCOUNTTABLEID;
		} else if ("group".equals(type)) {
			status = Status.group;
			mTableId = Constant.GROUPTABLEID;
		} else if ("newest".equals(type)) {
			status = Status.newest;
			mTableId = Constant.SHARETABLEID;
		} else if ("hottest".equals(type)) {
			status = Status.hottest;
			mTableId = Constant.SHARETABLEID;
		}
		// thisView.threeChoicesView.setButtonTwoText("关注");

		if (data.localStatus.localData.currentSearchRadius != 0) {
			searchRadius = data.localStatus.localData.currentSearchRadius;
		}
		searchTime = data.localStatus.localData.currentSearchTime;

	}

	public Animation animationTop;
	public Animation animationBottom;
	public Animation animationShadowTop;
	public Animation animationShadowBottom;
	public Animation animationNearLeft;
	public Animation animationNearRight;

	public LocationSource mLocationSource;
	public OnLocationChangedListener mOnLocationChangedListener;
	public OnCameraChangeListener mOnCameraChangeListener;
	public GeocodeSearch mGeocodeSearch;
	public OnGeocodeSearchListener mOnGeocodeSearchListener;

	public void requestMyLocation() {

		thisView.mAMap.setOnCameraChangeListener(mOnCameraChangeListener);

		mGeocodeSearch = new GeocodeSearch(thisActivity);
		mGeocodeSearch.setOnGeocodeSearchListener(mOnGeocodeSearchListener);
	}

	public int nowpage = 0;

	public RegeocodeQuery mRegeocodeQuery;

	public boolean isScroll = false;

	public boolean isChangeAddress = false;

	public String address, title;

	public void initializeListeners() {

		mOnGeocodeSearchListener = new OnGeocodeSearchListener() {

			@Override
			public void onRegeocodeSearched(RegeocodeResult result, int rCode) {
				if (rCode == 0) {
					if (result != null && result.getRegeocodeAddress() != null && result.getRegeocodeAddress().getFormatAddress() != null) {
						address = result.getRegeocodeAddress().getFormatAddress();
						thisView.addressView.setText(address);
						isChangeAddress = false;
						thisView.ico_map_pin.startAnimation(animationTop);
						thisView.ico_map_pin_shadow2.startAnimation(animationShadowTop);
						List<PoiItem> pois = result.getRegeocodeAddress().getPois();
						if (pois != null && pois.size() > 0) {
							title = pois.get(0).getTitle();
						} else {
							title = result.getRegeocodeAddress().getStreetNumber().getStreet();
						}
					}
				}
			}

			@Override
			public void onGeocodeSearched(GeocodeResult result, int rCode) {
			}
		};
		mOnCameraChangeListener = new OnCameraChangeListener() {

			@Override
			public void onCameraChangeFinish(CameraPosition cameraPosition) {
				LatLng mLatLng = cameraPosition.target;
				latitude = mLatLng.latitude;
				longitude = mLatLng.longitude;
				com.amap.api.services.core.LatLonPoint latLonPoint = new com.amap.api.services.core.LatLonPoint(mLatLng.latitude, mLatLng.longitude);
				RegeocodeQuery mRegeocodeQuery = new RegeocodeQuery(latLonPoint, 200, GeocodeSearch.AMAP);
				mGeocodeSearch.getFromLocationAsyn(mRegeocodeQuery);
				thisView.openLooper.stop();
				thisView.currentPosition = 0;
				// thisView.nextPosition = 0;
				nowpage = 0;
				searchNearbyHttp(true);
			}

			@Override
			public void onCameraChange(CameraPosition cameraPosition) {
				LatLng mLatLng = cameraPosition.target;
				thisView.changeAmapCircle(mLatLng.longitude, mLatLng.latitude);
				next();
			}

			public void next() {
				if (isChangeAddress == false) {
					isChangeAddress = true;
					thisView.currentPosition = 0;
					thisView.nextPosition = 0;
					// thisView.openLooper.start();
					thisView.progressView.setTranslationX(thisView.currentPosition);
					if (thisView.img_btn_set_start.getVisibility() == View.VISIBLE) {
						thisView.img_btn_set_start.startAnimation(animationNearLeft);
					}
				}
			}
		};
		mLocationSource = new LocationSource() {

			@Override
			public void activate(OnLocationChangedListener listener) {
				mOnLocationChangedListener = listener;
				mLocationManagerProxy.removeUpdates(mAMapLocationListener);
				mLocationManagerProxy.setGpsEnable(true);
				mLocationManagerProxy.requestLocationData(LocationProviderProxy.AMapNetwork, -1, 10, mAMapLocationListener);
			}

			@Override
			public void deactivate() {
				if (mLocationManagerProxy != null) {
					mLocationManagerProxy.removeUpdates(mAMapLocationListener);
					mLocationManagerProxy.destroy();
				}
				mLocationManagerProxy = null;
			}

		};
		mOnScrollListener = new OnScrollListener() {

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

			}
		};

		mOnClickListener = new OnClickListener() {

			@Override
			public void onClick(View view) {
				if (view.getTag(R.id.tag_class) != null) {
					String type = (String) view.getTag(R.id.tag_class);
					final int position = (Integer) view.getTag(R.id.tag_first);
					// + thisView.nearbyListView.getFirstVisiblePosition();
					if ("IncrementView".equals(type)) {
						ShareMessage shareMessage = (ShareMessage) thisController.mInfomations.get(position);
						if (shareMessage.scores == null) {
							shareMessage.scores = new HashMap<String, Data.Boards.Score>();
						}
						Score score = shareMessage.scores.get(data.userInformation.currentUser.phone);
						if (score == null) {
							score = data.boards.new Score();
						} else {
							if (score.remainNumber == 0) {
								Toast.makeText(thisActivity, "对不起,你只能评分一次", Toast.LENGTH_SHORT).show();
								return;
							}
						}
						shareMessage.totalScore = shareMessage.totalScore + 1;
						score.phone = data.userInformation.currentUser.phone;
						score.time = new Date().getTime();
						score.positive = 1;
						score.remainNumber = 0;
						shareMessage.scores.put(score.phone, score);
						data.boards.isModified = true;
						thisView.notifyData();
						thisView.modifyPraiseusersToMessage(true, shareMessage.gsid);
					} else if ("DecrementView".equals(type)) {
						final ShareMessage shareMessage = (ShareMessage) thisController.mInfomations.get(position);
						if (shareMessage.scores == null) {
							shareMessage.scores = new HashMap<String, Data.Boards.Score>();
						}
						Score score1 = shareMessage.scores.get(data.userInformation.currentUser.phone);
						if (score1 == null) {
							score1 = data.boards.new Score();
						} else {
							if (score1.remainNumber == 0) {
								Toast.makeText(thisActivity, "对不起,你只能评分一次", Toast.LENGTH_SHORT).show();
								return;
							}
						}
						final Score score = score1;
						if (shareMessage.totalScore == -4) {
							Alert.createDialog(thisActivity).setTitle("帖子分数少于-5将被删除.").setOnConfirmClickListener(new AlertInputDialog.OnDialogClickListener() {
								@Override
								public void onClick(AlertInputDialog dialog) {
									shareMessage.totalScore = shareMessage.totalScore - 1;
									score.phone = data.userInformation.currentUser.phone;
									score.time = new Date().getTime();
									score.negative = 1;
									score.remainNumber = 0;
									shareMessage.scores.put(score.phone, score);
									data.boards.isModified = true;
									thisView.notifyData();
									thisView.modifyPraiseusersToMessage(false, shareMessage.gsid);
									thisController.mInfomations.remove(position);
									thisView.notifyData();
								}
							}).show();
						} else {
							shareMessage.totalScore = shareMessage.totalScore - 1;
							score.phone = data.userInformation.currentUser.phone;
							score.time = new Date().getTime();
							score.negative = 1;
							score.remainNumber = 0;
							shareMessage.scores.put(score.phone, score);
							data.boards.isModified = true;
							thisView.notifyData();
							thisView.modifyPraiseusersToMessage(false, shareMessage.gsid);
						}
					}
				} else if (view.getTag(R.id.tag_first) != null) {
					String type = (String) view.getTag(R.id.tag_first);
					String key = (String) view.getTag(R.id.tag_second);
					if ("square".equals(type)) {
						thisView.businessCardPopView.cardView.setSmallBusinessCardContent(thisView.businessCardPopView.cardView.TYPE_SQUARE, key);
						thisView.businessCardPopView.cardView.setMenu(false);
						thisView.businessCardPopView.showUserCardDialogView();
					} else if ("group".equals(type)) {
						thisView.businessCardPopView.cardView.setSmallBusinessCardContent(thisView.businessCardPopView.cardView.TYPE_GROUP, key);
						thisView.businessCardPopView.cardView.setMenu(false);
						thisView.businessCardPopView.showUserCardDialogView();
					} else if ("point".equals(type)) {
						thisView.businessCardPopView.cardView.setSmallBusinessCardContent(thisView.businessCardPopView.cardView.TYPE_POINT, key);
						thisView.businessCardPopView.cardView.setMenu(false);
						thisView.businessCardPopView.showUserCardDialogView();
					}
				} else if (view.equals(thisView.backView)) {
					if (status == Status.account || status == Status.group) {
						thisActivity.finish();
					} else {
						Intent intent = new Intent(thisActivity, MainActivity.class);
						thisActivity.startActivity(intent);
					}
				} else if (view.equals(thisView.positionView)) {
					MarginLayoutParams params = (MarginLayoutParams) thisView.nearbyListView.getLayoutParams();
					int topMarigin = params.topMargin;
					if (topMarigin == (int) (84 * thisView.metrics.density)) {
						params.topMargin = (int) (304 * thisView.metrics.density);
					} else {
						params.topMargin = (int) (84 * thisView.metrics.density);
					}
					thisView.nearbyListView.setLayoutParams(params);
				} else if (view.equals(thisView.sortView)) {
					thisView.changePopupWindow(false);
				} else if (view.equals(thisView.searChView)) {
					Toast.makeText(thisActivity, "搜索", Toast.LENGTH_SHORT).show();
				} else if (view.equals(thisView.locationView)) {
					mLocationManagerProxy.requestLocationData(LocationProviderProxy.AMapNetwork, -1, 15, mAMapLocationListener);
				} else if (view.equals(thisView.ico_map_pin2)) {
					if (thisView.img_btn_set_start.getVisibility() == View.VISIBLE) {
						thisView.img_btn_set_start.startAnimation(animationNearLeft);
					} else {
						thisView.img_btn_set_start.setVisibility(View.VISIBLE);
						thisView.img_btn_set_start.startAnimation(animationNearRight);
					}
				} else if (view.equals(thisView.background)) {
					thisView.changePopupWindow(false);
				} else if (view.equals(thisView.screen) || view.equals(thisView.screenBackground) || view.equals(thisView.screenCancel)) {
					tempSearchRadius = searchRadius;
					tempSearchTime = searchTime;
					thisView.changeScreenPopupWindow();
				} else if (view.equals(thisView.screenConfirm)) {
					searchRadius = tempSearchRadius;
					searchTime = tempSearchTime;
					searchNearbyHttp(false);
					thisView.changeScreenPopupWindow();
					thisView.changeAmapCircle(longitude, latitude);
				} else if (view.equals(thisView.scopeOne)) {
					tempSearchRadius = radius[0];
					thisView.changeScreenText();
				} else if (view.equals(thisView.scopeTwo)) {
					tempSearchRadius = radius[1];
					thisView.changeScreenText();
				} else if (view.equals(thisView.scopeThree)) {
					tempSearchRadius = radius[2];
					thisView.changeScreenText();
				} else if (view.equals(thisView.scopeFour)) {
					tempSearchRadius = radius[3];
					thisView.changeScreenText();
				} else if (view.equals(thisView.timeOne)) {
					tempSearchTime = times[0];
					thisView.changeScreenText();
				} else if (view.equals(thisView.timeTwo)) {
					tempSearchTime = times[1];
					thisView.changeScreenText();
				} else if (view.equals(thisView.timeThree)) {
					tempSearchTime = times[2];
					thisView.changeScreenText();
				} else if (view.equals(thisView.timeFour)) {
					tempSearchTime = times[3];
					thisView.changeScreenText();
				} else if (view.equals(thisView.singleButton)) {
					thisView.changePopupWindow(false);
					thisView.img_btn_set_start.setVisibility(View.VISIBLE);
					thisView.img_btn_set_start.startAnimation(animationNearRight);
				} else if (view.equals(thisView.releationMenuImage)) {

				} else if (view.equals(thisView.img_btn_set_start)) {
					Alert.createInputDialog(thisActivity).setTitle("请输入地址备注信息").setInputText(title).setInputHint("请输入地址备注").setDescription("当前位置：" + address).setOnConfirmClickListener(new OnDialogClickListener() {

						@Override
						public void onClick(AlertInputDialog dialog) {
							com.open.welinks.model.Data.UserInformation.User.Location location = data.userInformation.currentUser.new Location();
							location.address = address;
							location.latitude = latitude;
							location.longitude = longitude;
							location.remark = dialog.getInputText().trim();
							if (data.userInformation.currentUser.commonUsedLocations == null)
								data.userInformation.currentUser.commonUsedLocations = new ArrayList<Data.UserInformation.User.Location>();
							data.userInformation.currentUser.commonUsedLocations.add(location);
							thisView.dialogAdapter.notifyDataSetChanged();
							data.userInformation.isModified = true;
							modifyUserCommonUsedLocations();
						}

					}).show();
				} else if (view.equals(thisView.shareMenuImage)) {
					Intent intent = new Intent(thisActivity, ShareReleaseImageTextActivity.class);
					intent.putExtra("mode", "NearbyView");
					intent.putExtra("gtype", "share");
					intent.putExtra("type", "imagetext");
					intent.putExtra("gid", Constant.SQUARE_SID);
					intent.putExtra("address", address);
					intent.putExtra("latitude", latitude);
					intent.putExtra("longitude", longitude);
					intent.putExtra("sid", Constant.SQUARE_SID);
					intent.putExtra("source", 2);
					thisActivity.startActivityForResult(intent, RESULTCODE);
				}
			}
		};
		mOnTouchListener = new OnTouchListener() {

			@Override
			public boolean onTouch(View view, MotionEvent event) {
				if (view.equals(thisView.nearbyListView)) {

					int id = event.getAction();
					if (id == MotionEvent.ACTION_DOWN) {
						thisView.status.state = thisView.status.Down;
						thisView.openLooper.stop();
						thisView.touch_pre_x = event.getX();
						thisView.touch_pre_y = event.getY();
						thisView.percent = 0;
						thisView.currentPosition = -thisView.viewManage.screenWidth;
						thisView.nextPosition = -thisView.viewManage.screenWidth;
						MarginLayoutParams params = (MarginLayoutParams) thisView.nearbyListView.getLayoutParams();
						int topMarigin = params.topMargin;
						if (topMarigin == (int) (304 * thisView.metrics.density)) {
							params.topMargin = (int) (84 * thisView.metrics.density);
							thisView.nearbyListView.setLayoutParams(params);
							thisView.touch_pre_y += 220 * thisView.metrics.density;
						}
					} else if (id == MotionEvent.ACTION_MOVE) {
						float x = event.getX();
						float y = event.getY();
						if (thisView.status.state == thisView.status.Down) {
							View firstView = thisView.nearbyListView.getChildAt(0);
							if (firstView == null) {
								return false;
							}
							int firstVisiblePosition = thisView.nearbyListView.getFirstVisiblePosition();
							int top = firstView.getTop();
							int firstViewHeight = firstView.getHeight();
							int topDistance = -top + firstVisiblePosition * firstViewHeight;
							int buttomDistance = topDistance + thisView.nearbyListView.getHeight();
							int totalHeight = firstViewHeight * thisView.nearbyListView.getCount();
							MarginLayoutParams params = (MarginLayoutParams) thisView.nearbyListView.getLayoutParams();
							int topMarigin = params.topMargin;
							int error = 2;
							if (topMarigin == (int) (84 * thisView.metrics.density)) {
								error = 7;
							}
							// log.e(buttomDistance + "-----" + totalHeight);
							if (topDistance == 0) {
								thisView.status.state = thisView.status.T;
							} else if (buttomDistance >= totalHeight - error) {
								thisView.status.state = thisView.status.B;
							}
						} else if (thisView.status.state == thisView.status.T || thisView.status.state == thisView.status.B) {
							float Δy = y - thisView.touch_pre_y;
							thisView.touch_pre_x = x;
							thisView.touch_pre_y = y;
							thisView.isTranslate = false;// true
							thisView.percent += Δy;
							if (thisView.status.state == thisView.status.T && thisView.percent < 0) {
								thisView.status.state = thisView.status.Down;
								thisView.percent = 0;
								thisView.isTranslate = false;
							}
							if (thisView.status.state == thisView.status.B && thisView.percent > 0) {
								thisView.status.state = thisView.status.Down;
								thisView.percent = 0;
								thisView.isTranslate = false;
							}
							thisView.currentPosition = (float) (-thisView.viewManage.screenWidth + Math.abs(thisView.percent) * 2);
							if (thisView.currentPosition >= 0) {
								thisView.currentPosition = 0;
							}
							thisView.progressView.setTranslationX(thisView.currentPosition);
						}
					} else if (id == MotionEvent.ACTION_UP) {
						float distance = Math.abs(thisView.percent) * 2;
						if (distance > thisView.viewManage.screenWidth / 2) {
							thisView.nextPosition = 0;
						} else {
							thisView.nextPosition = -thisView.viewManage.screenWidth;
						}
						thisView.isTranslate = false;
						thisView.openLooper.start();
						thisView.loopCallback.state = thisView.status.state;
						thisView.status.state = thisView.status.Up;
					}
					return thisView.isTranslate;
				}
				return false;
			}
		};
		mOnItemClickListener = thisView.threeChoicesView.new OnItemClickListener() {
			@Override
			public void onButtonCilck(int position) {
				if (position == 3) {
					if (status == Status.newest) {
						status = Status.hottest;
					} else if (status == Status.group) {
						status = Status.account;
						mTableId = Constant.ACCOUNTTABLEID;
					}
					nowpage = 0;
					thisView.openLooper.stop();
					thisView.currentPosition = -thisView.viewManage.screenWidth;
					thisView.nextPosition = -thisView.viewManage.screenWidth;
					searchNearbyHttp(false);
				} else if (position == 2) {
				} else if (position == 1) {
					if (status == Status.hottest) {
						status = Status.newest;
					} else if (status == Status.account) {
						status = Status.group;
						mTableId = Constant.GROUPTABLEID;
					}
					nowpage = 0;
					thisView.openLooper.stop();
					thisView.currentPosition = -thisView.viewManage.screenWidth;
					thisView.nextPosition = -thisView.viewManage.screenWidth;
					searchNearbyHttp(false);
				}
			}
		};
		mCloudSearchListener = new OnCloudSearchListener() {

			@SuppressWarnings("rawtypes")
			@Override
			public void onCloudSearched(CloudResult result, int rCode) {
				if (rCode == 0) {
					if (result != null && result.getQuery() != null) {
						if (result.getQuery().equals(mQuery)) {
							mCloudItems = result.getClouds();
							LatLng point = new LatLng(latitude, longitude);
							if (nowpage == 0) {
								mInfomations.clear();
							}
							if (mCloudItems.size() > 0) {
								nowpage++;
							} else {
							}
							long now = System.currentTimeMillis();
							for (CloudItem item : mCloudItems) {
								Map<String, Object> map = new HashMap<String, Object>();
								LatLng point2 = new LatLng(item.getLatLonPoint().getLatitude(), item.getLatLonPoint().getLongitude());
								map.put("location", item.getLatLonPoint().toString());
								map.put("name", item.getTitle());
								map.put("address", item.getSnippet());
								map.put("distance", item.getDistance() == -1 ? (int) AMapUtils.calculateLineDistance(point, point2) : item.getDistance());
								Iterator iter = item.getCustomfield().entrySet().iterator();
								while (iter.hasNext()) {
									Map.Entry entry = (Map.Entry) iter.next();
									map.put(entry.getKey().toString(), entry.getValue());
								}
								long time = Long.valueOf((String) map.get("time"));
								if (searchTime == times[times.length - 1] || (now - time) < searchTime) {
									// mInfomations.add(processingData(map));
								}
							}
							thisView.notifyData();
						}
					}
				} else {
					log.e(rCode + "::::::::::::::" + result);
				}
			}

			@Override
			public void onCloudItemDetailSearched(CloudItemDetail detail, int rCode) {
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
			public void onLocationChanged(Location location) {
			}

			@Override
			public void onLocationChanged(AMapLocation amapLocation) {
				mLocationManagerProxy.removeUpdates(mAMapLocationListener);
				mLocationManagerProxy.destroy();
				if (amapLocation != null && amapLocation.getAMapException().getErrorCode() == 0) {
					mAmapLocation = amapLocation;
					if ("".equals(address)) {
						User currentUser = data.userInformation.currentUser;
						currentUser.address = mAmapLocation.getAddress();
						currentUser.latitude = mAmapLocation.getLatitude() + "";
						currentUser.longitude = mAmapLocation.getLongitude() + "";
						modifyLocation();
					}
					address = mAmapLocation.getAddress();
					latitude = amapLocation.getLatitude();
					longitude = amapLocation.getLongitude();
					// thisView.addressView.setText(address);
					// mOnLocationChangedListener.onLocationChanged(amapLocation);
					LatLng mLatLng = new LatLng(amapLocation.getLatitude(), amapLocation.getLongitude());
					// thisView.mAMap.moveCamera(CameraUpdateFactory.changeLatLng(mLatLng));
					thisView.mAMap.animateCamera(CameraUpdateFactory.changeLatLng(mLatLng), 500, null);
					thisView.changeAmapCircle(longitude, latitude);
					thisView.changeScreenText();
					// searchNearby(amapLocation);
					// searchNearByPolygon(0);
				} else {

				}
			}
		};
		downloadListener = new OnDownloadListener() {
			@Override
			public void onFailure(DownloadFile instance, int status) {
				super.onFailure(instance, status);
			}

			@Override
			public void onSuccess(DownloadFile instance, int status) {
				super.onSuccess(instance, status);
				imageLoader.displayImage("file://" + instance.path, (ImageView) instance.view);
			}
		};
		mListOnItemClickListener = new android.widget.AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				if (status == Status.newest || status == Status.hottest) {
					ShareMessage shareMessage = (ShareMessage) thisController.mInfomations.get(position);
					data.boards.shareMessagesMap.put(shareMessage.gsid, shareMessage);
					Intent intent = new Intent(thisActivity, ShareMessageDetailActivity.class);
					intent.putExtra("gid", shareMessage.gid);
					intent.putExtra("sid", shareMessage.sid);
					intent.putExtra("gsid", shareMessage.gsid);
					thisActivity.startActivity(intent);
				} else if (status == Status.group) {
					Group group = (Group) thisController.mInfomations.get(position);
					thisView.businessCardPopView.cardView.setSmallBusinessCardContent(thisView.businessCardPopView.cardView.TYPE_GROUP, String.valueOf(group.gid));
					thisView.businessCardPopView.showUserCardDialogView();
				} else if (status == Status.account) {
					Friend friend = (Friend) thisController.mInfomations.get(position);
					thisView.businessCardPopView.cardView.setSmallBusinessCardContent(thisView.businessCardPopView.cardView.TYPE_POINT, friend.phone);
					thisView.businessCardPopView.cardView.setMenu(false);
					thisView.businessCardPopView.showUserCardDialogView();
				}
			}
		};
	}

	public void modifyLocation() {
		data = Parser.getInstance().check();
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

	public boolean isTouchDown = false;
	public View onTouchDownView;

	public void bindEvent() {
		thisView.threeChoicesView.setOnItemClickListener(mOnItemClickListener);
		mCloudSearch.setOnCloudSearchListener(mCloudSearchListener);
		thisView.backView.setOnClickListener(mOnClickListener);
		thisView.nearbyListView.setOnScrollListener(mOnScrollListener);
		thisView.positionView.setOnClickListener(this.mOnClickListener);
		thisView.sortView.setOnClickListener(this.mOnClickListener);
		thisView.searChView.setOnClickListener(this.mOnClickListener);
		thisView.locationView.setOnClickListener(this.mOnClickListener);
		thisView.img_btn_set_start.setOnClickListener(this.mOnClickListener);
		thisView.ico_map_pin2.setOnClickListener(this.mOnClickListener);
		thisView.background.setOnClickListener(this.mOnClickListener);
		thisView.dialogContainer.setOnClickListener(this.mOnClickListener);
		thisView.screen.setOnClickListener(this.mOnClickListener);
		thisView.screenBackground.setOnClickListener(this.mOnClickListener);
		thisView.screenCancel.setOnClickListener(this.mOnClickListener);
		thisView.screenConfirm.setOnClickListener(this.mOnClickListener);
		thisView.scopeOne.setOnClickListener(this.mOnClickListener);
		thisView.scopeTwo.setOnClickListener(this.mOnClickListener);
		thisView.scopeThree.setOnClickListener(this.mOnClickListener);
		thisView.scopeFour.setOnClickListener(this.mOnClickListener);
		thisView.timeOne.setOnClickListener(this.mOnClickListener);
		thisView.timeTwo.setOnClickListener(this.mOnClickListener);
		thisView.timeThree.setOnClickListener(this.mOnClickListener);
		thisView.timeFour.setOnClickListener(this.mOnClickListener);
		thisView.nearbyListView.setOnItemClickListener(mListOnItemClickListener);
		thisView.nearbyListView.setOnTouchListener(mOnTouchListener);
		if (thisView.shareMenuImage != null)
			thisView.shareMenuImage.setOnClickListener(this.mOnClickListener);
		if (thisView.releationMenuImage != null)
			thisView.releationMenuImage.setOnClickListener(this.mOnClickListener);
		if (thisView.singleButton != null) {
			thisView.singleButton.setOnClickListener(mOnClickListener);
		}
		mGeocodeSearch.setOnGeocodeSearchListener(mOnGeocodeSearchListener);
		thisView.mAMap.setOnCameraChangeListener(mOnCameraChangeListener);
	}

	boolean isRun = false;

	public Handler handler = new Handler();

	public void searchNearbyHttp(final boolean isAnimation) {
		if (isAnimation) {
			thisView.transleteSpeed = 0.4f;
			thisView.loopCallback.state = thisView.status.None;
			thisView.currentPosition = 0;
			thisView.nextPosition = thisView.viewManage.screenWidth / 2f;
			handler.postDelayed(new Runnable() {
				public void run() {
					isRun = true;
					thisView.openLooper.start();
				}
			}, 500);
		}

		long now = System.currentTimeMillis();
		RequestParams params = new RequestParams();
		HttpUtils httpUtils = new HttpUtils();
		params.addQueryStringParameter("key", Constant.LBS_SAVE_KSY);
		params.addQueryStringParameter("tableid", mTableId);
		params.addQueryStringParameter("center", longitude + "," + latitude);
		params.addQueryStringParameter("radius", String.valueOf(searchRadius));
		params.addQueryStringParameter("limit", String.valueOf(20));
		params.addQueryStringParameter("page", String.valueOf(nowpage + 1));
		params.addQueryStringParameter("time", String.valueOf(now));
		if (status == Status.account || status == Status.group) {
			params.addQueryStringParameter("sortrule", "_distance:1");
			if (status == Status.account && searchTime != times[times.length - 1]) {
				params.addQueryStringParameter("filter", "lastlogintime:[" + (now - searchTime) + "," + now + "]");
			}
		} else {
			if (status == Status.newest) {
				params.addQueryStringParameter("sortrule", "time:0");
			} else if (status == Status.hottest) {
				params.addQueryStringParameter("sortrule", "totalScore:0");
			}
			if (searchTime != times[times.length - 1]) {
				params.addQueryStringParameter("filter", "time:[" + (now - searchTime) + "," + now + "]");
			}
		}
		httpUtils.send(HttpMethod.GET, API.LBS_DATASEARCH_AROUND, params, httpClient.new ResponseHandler<String>() {

			@Override
			public void onSuccess(ResponseInfo<String> responseInfo) {
				if (isAnimation) {
					thisView.loopCallback.state = thisView.status.None;
					thisView.transleteSpeed = 0.6f;
					if (!isRun && thisView.nextPosition == thisView.viewManage.screenWidth / 2f) {
						thisView.nextPosition = thisView.viewManage.screenWidth / 3f * 2;
						thisView.openLooper.start();
					} else {
						thisView.nextPosition = thisView.viewManage.screenWidth / 3f * 2;
					}
				}
				Response response = gson.fromJson(responseInfo.result, Response.class);
				if ("OK".equals(response.info)) {
					boolean isMerge = false;
					if (nowpage == 0) {
						mInfomations.clear();
						isMerge = true;
					}
					if (response.count > 0) {
						nowpage++;
					}

					for (PoiData poiData : response.datas) {
						if (status == Status.account && poiData.phone != null) {
							processingAccountData(poiData);
						} else if (status == Status.group && poiData.gid != null) {
							processingGroupData(poiData);
						} else if (status == Status.newest || status == Status.hottest) {
							processingShareData(poiData);
						}
					}
					if (isAnimation) {
						thisView.loopCallback.state = thisView.status.None;
						thisView.transleteSpeed = 1f;
						thisView.nextPosition = thisView.viewManage.screenWidth;
						thisView.openLooper.start();
						if (!isRun && thisView.nextPosition == thisView.viewManage.screenWidth / 3f * 2) {
							thisView.nextPosition = thisView.viewManage.screenWidth;
							thisView.openLooper.start();
						} else {
							thisView.nextPosition = thisView.viewManage.screenWidth;
						}
						isRun = false;
					}
					if (status == Status.account) {
						thisView.notifyData();
					} else if (status == Status.group) {
						thisView.notifyData();
					} else if (status == Status.newest) {
						if (isMerge) {
							getTableListData();
						} else {
							thisView.notifyData();
						}
					} else if (status == Status.hottest) {
						addSendingShareMessage();
						thisView.notifyData();
					}
				} else {
					log.e(response.info + "::::::" + response.status);
				}
			}
		});
	}

	public void addSendingShareMessage() {
		boolean isReflash = false;
		for (int i = thisView.sendingSequence.size() - 1; i >= 0; i--) {
			String key = thisView.sendingSequence.get(i);
			SendShare sendShare = thisView.sendingShareMessage.get(key);
			if (sendShare != null) {
				thisController.mInfomations.add(0, sendShare.shareMessage);
				isReflash = true;
			}
		}
		if (isReflash) {
			thisView.nearbyShareAdapter.notifyDataSetChanged();
		}
	}

	class Response {
		public int count;
		public String info;
		public int status;
		public ArrayList<PoiData> datas;
	}

	public void getTableListData() {

		// long time = System.currentTimeMillis();
		RequestParams params = new RequestParams();
		HttpUtils httpUtils = new HttpUtils();
		// params.addHeader("Cookie", "sid=f6839c7b168da4bc3bf0d2c5d13a4cf1b0829cab3047fa5ce45801a27e3bd0c60ea69cb9fc8bd465");
		// params.addQueryStringParameter("num", "50");
		// params.addQueryStringParameter("page", "1");
		// params.addQueryStringParameter("order", "_id:0");
		// params.addQueryStringParameter("_", time + "");
		// params.addQueryStringParameter("v", "1");
		// params.addQueryStringParameter("cid", "1");
		httpUtils.send(HttpMethod.GET, "http://yuntuapi.amap.com/datamanage/data/list?key=0cd819a62c50d40b75a73f66cb14aa06&tableid=54f520e3e4b0ff22e1fc52d3&sortrule=time:0", params, httpClient.new ResponseHandler<String>() {

			@Override
			public void onSuccess(ResponseInfo<String> responseInfo) {
				// log.e(responseInfo.result);
				Response response = gson.fromJson(responseInfo.result, Response.class);
				// log.e(response.datas.size() + "------------");
				ArrayList<PoiData> list = response.datas;
				ArrayList<ShareMessage> needList = new ArrayList<ShareMessage>();
				ShareMessage shareMessage = null;
				if (mInfomations.size() > 0) {
					shareMessage = (ShareMessage) mInfomations.get(0);
				}
				for (int i = 0; i < list.size(); i++) {
					PoiData poiEntity = list.get(i);
					ShareMessage currentShareMessage = processingShareMessage(poiEntity);
					if (shareMessage != null) {
						if (shareMessage.time < currentShareMessage.time && searchRadius >= currentShareMessage.distance) {
							currentShareMessage.getStatus = "synchronous";
							needList.add(currentShareMessage);
						}
					} else {
						if (searchRadius >= currentShareMessage.distance) {
							currentShareMessage.getStatus = "synchronous";
							needList.add(shareMessage);
						}
					}
				}
				mInfomations.addAll(needList);
				sortMInformations();
				addSendingShareMessage();
				thisView.notifyData();
			}

			@Override
			public void onFailure(HttpException error, String msg) {
				super.onFailure(error, msg);
				thisView.notifyData();
			}
		});
	}

	// 冒泡排序
	public void sortMInformations() {
		for (int i = 0; i < mInfomations.size() - 1; i++) {
			for (int j = 0; j < mInfomations.size() - i - 1; j++) {
				ShareMessage m1 = (ShareMessage) mInfomations.get(j);
				ShareMessage m2 = (ShareMessage) mInfomations.get(j + 1);
				if (m1 != null && m2 != null) {
					if (m1.time < m2.time) {
						ShareMessage temp = m1;
						mInfomations.set(j, m2);
						mInfomations.set(j + 1, temp);
					}
				}
			}
		}
	}

	public ShareMessage processingShareMessage(PoiData poiData) {
		ShareMessage shareMessage = data.boards.new ShareMessage();
		String content = poiData.content;
		content = new String(Base64Coder.decode(content));
		if (content.lastIndexOf("@") == content.length() - 1) {
			content = content.substring(0, content.length() - 1);
		}
		shareMessage.content = content;
		shareMessage.head = poiData.head;
		shareMessage.gsid = String.valueOf(poiData.gsid);
		shareMessage.sid = String.valueOf(poiData.sid);
		shareMessage.phone = poiData.phone;
		shareMessage.totalScore = poiData.totalScore;
		shareMessage.type = poiData.type;
		shareMessage.time = poiData.time;
		shareMessage.nickName = poiData._name;
		shareMessage.status = "sent";
		String location[] = poiData._location.split(",");
		shareMessage.distance = 1000 * Double.valueOf(LBSHandler.getInstance().pointDistance(longitude + "", latitude + "", location[0], location[1]));
		String scores = poiData.scores;
		if (scores != null && !"".equals(scores)) {
			scores = scores.substring(0, scores.length() - 1);
			shareMessage.scores = gson.fromJson(scores, new TypeToken<HashMap<String, Score>>() {
			}.getType());
		}
		// log.e("processingShareMessage:" + message.sid);
		return shareMessage;
	}

	public void processingShareData(PoiData poiData) {

		ShareMessage shareMessage = data.boards.new ShareMessage();
		String content = poiData.content;
		content = new String(Base64Coder.decode(content));
		if (content.lastIndexOf("@") == content.length() - 1) {
			content = content.substring(0, content.length() - 1);
		}
		shareMessage.content = content;
		shareMessage.head = poiData.head;
		shareMessage.gsid = String.valueOf(poiData.gsid);
		shareMessage.sid = String.valueOf(poiData.sid);
		shareMessage.phone = poiData.phone;
		shareMessage.totalScore = poiData.totalScore;
		shareMessage.type = poiData.type;
		shareMessage.time = poiData.time;
		shareMessage.nickName = poiData._name;
		shareMessage.status = "sent";
		shareMessage.distance = Integer.valueOf(poiData._distance);
		String scores = poiData.scores;
		if (scores != null && !"".equals(scores)) {
			scores = scores.substring(0, scores.length() - 1);
			shareMessage.scores = gson.fromJson(scores, new TypeToken<HashMap<String, Score>>() {
			}.getType());
		}
		mInfomations.add(shareMessage);
		// log.e("processingShareData:" + message.sid);

		// TODO error code
		data.boards.shareMessagesMap.put(shareMessage.sid, shareMessage);
	}

	public void processingAccountData(PoiData poiData) {
		Friend friend = data.relationship.new Friend();
		friend.nickName = poiData._name;
		friend.sex = poiData.sex;
		friend.mainBusiness = poiData.mainBusiness;
		friend.head = poiData.head;
		friend.phone = poiData.phone;
		friend.age = poiData.age;
		String[] location = poiData._location.split(",");
		friend.longitude = location[0];
		friend.latitude = location[1];
		friend.lastLoginTime = String.valueOf(poiData.lastlogintime);
		friend.distance = Integer.valueOf(poiData._distance);

		if (!data.relationship.friendsMap.containsKey(friend.phone)) {
			data.relationship.friendsMap.put(friend.phone, friend);
		}
		mInfomations.add(friend);
	}

	public void processingGroupData(PoiData poiData) {
		Group group = data.relationship.new Group();
		group.name = poiData._name;
		group.icon = poiData.icon;
		group.gid = Integer.valueOf(poiData.gid);
		group.description = poiData.description;
		group.cover = poiData.cover;
		group.createTime = String.valueOf(poiData.createTime);
		group.distance = Integer.valueOf(poiData._distance);
		String[] location = poiData._location.split(",");
		group.longitude = location[0];
		group.latitude = location[1];
		if (!data.relationship.groupsMap.containsKey(poiData.gid)) {
			data.relationship.groupsMap.put(poiData.gid, group);
		}
		mInfomations.add(group);
	}

	public boolean judgeTempRelation(Map<String, Object> infomation) {
		boolean isTemp = true;
		if (infomation.get("gid") == null) {
			for (String circles : data.relationship.circles) {
				if (data.relationship.circlesMap.get(circles).friends.contains(infomation.get("phone"))) {
					isTemp = false;
					break;
				}
			}
			if (isTemp) {
				Friend tempFriend = data.relationship.new Friend();
				tempFriend.phone = (String) infomation.get("phone");
				tempFriend.head = (String) infomation.get("head");
				tempFriend.nickName = (String) infomation.get("name");
				tempFriend.mainBusiness = (String) infomation.get("mainBusiness");
				data.tempData.tempFriend = tempFriend;
			}
		} else {
			if (data.relationship.groups.contains(infomation.get("gid"))) {
				isTemp = false;
			}
			if (isTemp) {
				Group tempGroup = data.relationship.new Group();
				tempGroup.gid = Integer.valueOf((String) infomation.get("gid"));
				tempGroup.icon = (String) infomation.get("icon");
				tempGroup.name = (String) infomation.get("name");
				tempGroup.description = (String) infomation.get("description");
				data.tempData.tempGroup = tempGroup;
			}

		}
		return isTemp;
	}

	public void getUserCommonUsedLocations() {
		RequestParams params = new RequestParams();
		HttpUtils httpUtils = new HttpUtils();
		User currentUser = data.userInformation.currentUser;
		params.addBodyParameter("phone", currentUser.phone);
		params.addBodyParameter("accessKey", currentUser.accessKey);
		httpUtils.send(HttpMethod.POST, API.ACCOUNT_GETCOMMONUSEDLOCATION, params, responseHandlers.account_getcommonusedlocation);
	}

	public void modifyUserCommonUsedLocations() {
		RequestParams params = new RequestParams();
		HttpUtils httpUtils = new HttpUtils();
		User currentUser = data.userInformation.currentUser;
		params.addBodyParameter("phone", currentUser.phone);
		params.addBodyParameter("accessKey", currentUser.accessKey);
		params.addBodyParameter("commonusedlocation", gson.toJson(currentUser.commonUsedLocations));

		httpUtils.send(HttpMethod.POST, API.ACCOUNT_MODIFYCOMMONUSEDLOCATION, params, httpClient.new ResponseHandler<String>() {

			@Override
			public void onSuccess(ResponseInfo<String> responseInfo) {
				super.onSuccess(responseInfo);
			}
		});
	}

	public void onActivityResult(int requestCode, int resultCode2, Intent data2) {
		if (resultCode2 == RESULTCODE) {
			nowpage = 0;
			thisView.openLooper.stop();
			thisView.currentPosition = -thisView.viewManage.screenWidth;
			thisView.nextPosition = -thisView.viewManage.screenWidth;
			searchNearbyHttp(false);
		}
	}

	public void initData() {
		initializeListeners();
		mInfomations = new ArrayList<Object>();
		mCloudSearch = new CloudSearch(thisActivity);

		mLocationManagerProxy = LocationManagerProxy.getInstance(thisActivity);
		mLocationManagerProxy.setGpsEnable(true);

		thisView.mAMap.setLocationSource(mLocationSource);
		thisView.mAMap.getUiSettings().setMyLocationButtonEnabled(false);
		thisView.mAMap.getUiSettings().setZoomControlsEnabled(false);
		// thisView.mAMap.getUiSettings().setScaleControlsEnabled(false);
		thisView.mAMap.setMyLocationEnabled(true);

		thisView.mAMap.getUiSettings().setRotateGesturesEnabled(false);
		thisView.mAMap.getUiSettings().setTiltGesturesEnabled(false);

		mGeocodeSearch = new GeocodeSearch(thisActivity);

		// requestMyLocation();
		animationTop = AnimationUtils.loadAnimation(thisActivity, R.anim.animation_point_beat_top);
		animationBottom = AnimationUtils.loadAnimation(thisActivity, R.anim.animation_point_beat_bottom);
		animationTop.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {
			}

			@Override
			public void onAnimationRepeat(Animation animation) {
			}

			@Override
			public void onAnimationEnd(Animation animation) {
				thisView.ico_map_pin.startAnimation(animationBottom);
			}
		});
		animationShadowTop = AnimationUtils.loadAnimation(thisActivity, R.anim.animation_point_shadow2_beat_top);
		animationShadowBottom = AnimationUtils.loadAnimation(thisActivity, R.anim.animation_point_shadow2_beat_bottom);
		animationShadowTop.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {
			}

			@Override
			public void onAnimationRepeat(Animation animation) {
			}

			@Override
			public void onAnimationEnd(Animation animation) {
				thisView.ico_map_pin_shadow2.startAnimation(animationShadowBottom);
			}
		});
		animationNearLeft = AnimationUtils.loadAnimation(thisActivity, R.anim.animation_nearby_address_left);
		animationNearRight = AnimationUtils.loadAnimation(thisActivity, R.anim.animation_nearby_address_right);
		animationNearLeft.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {
			}

			@Override
			public void onAnimationRepeat(Animation animation) {
			}

			@Override
			public void onAnimationEnd(Animation animation) {
				thisView.img_btn_set_start.setVisibility(View.INVISIBLE);
			}
		});
		bindEvent();
		mLocationManagerProxy.requestLocationData(LocationProviderProxy.AMapNetwork, -1, 15, mAMapLocationListener);
		getUserCommonUsedLocations();
	}

	class PoiData {
		public String _id, _location, _name, _address, _distance;
		public String content, scores, phone, type, head;
		public String sex, mainBusiness;
		public String icon, gid, description, cover;
		public int sid, gsid, totalScore, age;
		public long createTime, time, lastlogintime;
	}

	boolean isShowDialg = false;
	public boolean isExit = false;

	public boolean onKeyUp(int keyCode, KeyEvent event) {
		if (status == Status.account || status == Status.group) {
			thisActivity.finish();
		} else {
			if (keyCode == KeyEvent.KEYCODE_BACK) {
				if (isExit) {
					thisActivity.finish();
				} else {
					if (!isShowDialg) {
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
		}
		return true;
	}

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

	class Contact {
		public String nickName;
		public String head;
	}

	Map<String, Contact> contacts = new HashMap<String, Contact>();

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
