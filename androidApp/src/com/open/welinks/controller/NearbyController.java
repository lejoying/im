package com.open.welinks.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
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
import android.widget.Toast;

import com.amap.api.cloud.model.AMapCloudException;
import com.amap.api.cloud.model.CloudItem;
import com.amap.api.cloud.model.CloudItemDetail;
import com.amap.api.cloud.model.LatLonPoint;
import com.amap.api.cloud.search.CloudResult;
import com.amap.api.cloud.search.CloudSearch;
import com.amap.api.cloud.search.CloudSearch.OnCloudSearchListener;
import com.amap.api.cloud.search.CloudSearch.Query;
import com.amap.api.cloud.search.CloudSearch.SearchBound;
import com.amap.api.cloud.search.CloudSearch.Sortingrules;
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
import com.amap.api.services.geocoder.GeocodeAddress;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.GeocodeSearch.OnGeocodeSearchListener;
import com.amap.api.services.geocoder.RegeocodeQuery;
import com.amap.api.services.geocoder.RegeocodeResult;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.open.lib.HttpClient;
import com.open.lib.MyLog;
import com.open.lib.HttpClient.ResponseHandler;
import com.open.welinks.NearbyActivity;
import com.open.welinks.R;
import com.open.welinks.ShareReleaseImageTextActivity;
import com.open.welinks.customListener.OnDownloadListener;
import com.open.welinks.customView.Alert;
import com.open.welinks.customView.Alert.AlertInputDialog;
import com.open.welinks.customView.Alert.AlertInputDialog.OnDialogClickListener;
import com.open.welinks.customView.ThreeChoicesView.OnItemClickListener;
import com.open.welinks.model.API;
import com.open.welinks.model.Constant;
import com.open.welinks.model.Data;
import com.open.welinks.model.SubData;
import com.open.welinks.model.Data.Boards.Score;
import com.open.welinks.model.Data.Boards.ShareMessage;
import com.open.welinks.model.ResponseHandlers;
import com.open.welinks.model.Data.Relationship.Friend;
import com.open.welinks.model.Data.Relationship.Group;
import com.open.welinks.model.Data.UserInformation.User;
import com.open.welinks.oss.DownloadFile;
import com.open.welinks.view.NearbyView;

public class NearbyController {

	public Data data = Data.getInstance();
	public SubData subData = SubData.getInstance();

	public String tag = "NearbyController";
	public MyLog log = new MyLog(tag, true);

	public NearbyView thisView;
	public NearbyController thisController;
	public NearbyActivity thisActivity;

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
	public OnTouchListener mOnTouchListener;

	public HttpClient httpClient = HttpClient.getInstance();
	public ResponseHandlers responseHandlers = ResponseHandlers.getInstance();
	public ImageLoader imageLoader = ImageLoader.getInstance();

	public Gson gson = new Gson();

	public String mTableId;
	public ArrayList<ShareMessage> mInfomations;
	public AMapLocation mAmapLocation;

	public double latitude, longitude;
	public int searchRadius = 50000;// 1500 10000 50000
	public int tempSearchRadius = 50000;// 5000 10000 50000
	public long searchTime = 0;// 3600000 86400000 259200000
	public long tempSearchTime = 0;// 3600000 86400000 259200000

	public int[] radius = { 1500, 5000, 10000, 50000 };
	public long[] times = { 3600000, 86400000, 259200000, 0 };

	public Status status, searchStatus;

	public enum Status {
		account, group, share, newest, hottest
	}

	public NearbyController(NearbyActivity thisActivity) {
		thisController = this;
		this.thisActivity = thisActivity;
	}

	public void onCreate() {
		thisView.viewManage.nearbyView = thisView;
		type = thisActivity.getIntent().getStringExtra("type");
		if ("account".equals(type)) {
			status = Status.account;
			mTableId = Constant.ACCOUNTTABLEID;
			thisView.NearbyLayoutID = R.layout.nearby_item_account;
			thisView.threeChoicesView.setDefaultItem(3);
		} else if ("group".equals(type)) {
			status = Status.group;
			mTableId = Constant.GROUPTABLEID;
			thisView.NearbyLayoutID = R.layout.nearby_item_group;
			thisView.threeChoicesView.setDefaultItem(2);
		} else if ("share".equals(type)) {
			status = Status.share;
			searchStatus = Status.newest;
			mTableId = Constant.SHARETABLEID;
			thisView.NearbyLayoutID = R.layout.nearby_item_group;
			thisView.threeChoicesView.setDefaultItem(1);
		}
		thisView.threeChoicesView.setButtonOneText("最新");
		// thisView.threeChoicesView.setButtonTwoText("关注");
		thisView.threeChoicesView.setButtonThreeText("最热");
		thisView.threeChoicesView.setTwoChoice();
		mInfomations = new ArrayList<ShareMessage>();
		mCloudSearch = new CloudSearch(thisActivity);

		initializeListeners();
		bindEvent();

		thisView.mAMap = thisView.mapView.getMap();

		mLocationManagerProxy = LocationManagerProxy.getInstance(thisActivity);
		mLocationManagerProxy.setGpsEnable(true);

		thisView.mAMap.setLocationSource(mLocationSource);
		thisView.mAMap.getUiSettings().setMyLocationButtonEnabled(false);
		thisView.mAMap.getUiSettings().setZoomControlsEnabled(false);
		// thisView.mAMap.getUiSettings().setScaleControlsEnabled(false);
		thisView.mAMap.setMyLocationEnabled(true);

		thisView.mAMap.getUiSettings().setRotateGesturesEnabled(false);
		thisView.mAMap.getUiSettings().setTiltGesturesEnabled(false);

		thisView.mAMap.setOnCameraChangeListener(mOnCameraChangeListener);

		mGeocodeSearch = new GeocodeSearch(thisActivity);
		mGeocodeSearch.setOnGeocodeSearchListener(mOnGeocodeSearchListener);

		mLocationManagerProxy.requestLocationData(LocationProviderProxy.AMapNetwork, -1, 15, mAMapLocationListener);
		getUserCommonUsedLocations();

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
		animationShadowBottom.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {
			}

			@Override
			public void onAnimationRepeat(Animation animation) {
			}

			@Override
			public void onAnimationEnd(Animation animation) {
				// thisView.img_btn_set_start.setVisibility(View.VISIBLE);
				// thisView.img_btn_set_start.startAnimation(animationNearRight);
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

	public boolean loadFinish = true;

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
						// log.e("onRegeocodeSearched:" + address);
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
				if (rCode == 0) {
					if (result != null && result.getGeocodeAddressList() != null && result.getGeocodeAddressList().size() > 0) {
						GeocodeAddress point = result.getGeocodeAddressList().get(0);
						LatLng mLatLng = new LatLng(point.getLatLonPoint().getLatitude(), point.getLatLonPoint().getLongitude());
						thisView.mAMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mLatLng, 13));
						String address = point.getFormatAddress();
						// log.e("onGeocodeSearched:" + address);
						// locationView.setText("当前地址：" + address);
					}
				}
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
				searchNearby();
			}

			@Override
			public void onCameraChange(CameraPosition cameraPosition) {
				next();
			}

			public void next() {
				if (isChangeAddress == false) {
					isChangeAddress = true;
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
				// if (positioned) {
				// LatLng mLatLng = new LatLng(Double.valueOf(latitude), Double.valueOf(longitude));
				// thisView.mAMap.moveCamera(CameraUpdateFactory.changeLatLng(mLatLng));
				// locationView.setText("当前地址：" + address);
				// positioned = false;
				// } else {
				mLocationManagerProxy.requestLocationData(LocationProviderProxy.AMapNetwork, -1, 10, mAMapLocationListener);
				// }
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
				int lastItemid = thisView.nearbyListView.getLastVisiblePosition();
				if ((lastItemid + 1) == totalItemCount) {
					if (totalItemCount > 0) {
						if (loadFinish) {
							loadFinish = false;
							// searchNearby();
						}
					}
				}
				// log.e("first:::" + firstVisibleItem);
				// if (firstVisibleItem != 0) {
				// isScroll = true;
				// }
				// if (firstVisibleItem == 0 && isScroll) {
				// MarginLayoutParams params = (MarginLayoutParams) thisView.nearbyListView.getLayoutParams();
				// int topMarigin = params.topMargin;
				// if (topMarigin == (int) (88 * thisView.metrics.density)) {
				// params.topMargin = (int) (288 * thisView.metrics.density);
				// } else {
				// params.topMargin = (int) (88 * thisView.metrics.density);
				// }
				// thisView.nearbyListView.setLayoutParams(params);
				// }
			}
		};

		mOnClickListener = new OnClickListener() {

			@Override
			public void onClick(View view) {
				if (view.getTag(R.id.tag_first) != null) {
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
					thisActivity.finish();
				} else if (view.equals(thisView.positionView)) {
					MarginLayoutParams params = (MarginLayoutParams) thisView.nearbyListView.getLayoutParams();
					int topMarigin = params.topMargin;
					if (topMarigin == (int) (84 * thisView.metrics.density)) {
						params.topMargin = (int) (304 * thisView.metrics.density);
						// thisView.lineView.setVisibility(View.GONE);
					} else {
						params.topMargin = (int) (84 * thisView.metrics.density);
						// thisView.lineView.setVisibility(View.VISIBLE);
					}
					thisView.nearbyListView.setLayoutParams(params);
					// if (thisView.lbsMapView.getVisibility() == View.VISIBLE) {
					// // thisView.lbsMapView.setVisibility(View.INVISIBLE);
					// params.topMargin = (int) (88 * thisView.metrics.density);
					// thisView.lbsMapView.setLayoutParams(params);
					// } else {
					// // thisView.lbsMapView.setVisibility(View.VISIBLE);
					// MarginLayoutParams params = (MarginLayoutParams) thisView.nearbyListView.getLayoutParams();
					// params.topMargin = (int) (288 * thisView.metrics.density);
					// }
				} else if (view.equals(thisView.sortView)) {
					// Toast.makeText(thisActivity, "排序筛选", Toast.LENGTH_SHORT).show();
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
					searchNearby();
					thisView.changeScreenPopupWindow();
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
				} else if (view.equals(thisView.menuImage)) {
					Intent intent = new Intent(thisActivity, ShareReleaseImageTextActivity.class);
					intent.putExtra("mode", "NearbyView");
					intent.putExtra("gtype", "share");
					intent.putExtra("type", "imagetext");
					intent.putExtra("gid", Constant.SQUARE_SID);
					intent.putExtra("address", address);
					intent.putExtra("latitude", latitude);
					intent.putExtra("longitude", longitude);
					intent.putExtra("sid", Constant.SQUARE_SID);
					thisActivity.startActivity(intent);
				}
			}
		};

		mOnItemClickListener = thisView.threeChoicesView.new OnItemClickListener() {
			@Override
			public void onButtonCilck(int position) {
				if (position == 3) {
					searchStatus = Status.hottest;
					nowpage = 0;
					loadFinish = true;
					searchNearby();
				} else if (position == 2) {
				} else if (position == 1) {
					searchStatus = Status.newest;
					nowpage = 0;
					loadFinish = true;
					searchNearby();
				}
			}
		};
		mCloudSearchListener = new OnCloudSearchListener() {

			@SuppressWarnings("rawtypes")
			@Override
			public void onCloudSearched(CloudResult result, int rCode) {
				log.e("result::::::::::::::::::");
				if (rCode == 0) {
					log.e("result1::::::::::::::::::");
					if (result != null && result.getQuery() != null) {
						log.e("result2::::::::::::::::::");
						if (result.getQuery().equals(mQuery)) {
							log.e("result3::::::::::::::::::");
							mCloudItems = result.getClouds();
							LatLng point = new LatLng(latitude, longitude);
							if (nowpage == 0) {
								mInfomations.clear();
							}
							if (mCloudItems.size() > 0) {
								log.e("result4::::::::::::::::::");
								nowpage++;
								loadFinish = true;
							} else {
								log.e("result0::::::::::::::::::");
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
									log.e(entry.getKey().toString() + ":::::::::" + entry.getValue());
									map.put(entry.getKey().toString(), entry.getValue());
								}
								long time = Long.valueOf((String) map.get("time"));
								if (searchTime == times[times.length - 1] || (now - time) < searchTime) {
									mInfomations.add(processingData(map));
								}
							}
							thisView.nearbyAdapter.notifyDataSetChanged();
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
					address = mAmapLocation.getAddress();
					latitude = amapLocation.getLatitude();
					longitude = amapLocation.getLongitude();
					// thisView.addressView.setText(address);
					// mOnLocationChangedListener.onLocationChanged(amapLocation);
					LatLng mLatLng = new LatLng(amapLocation.getLatitude(), amapLocation.getLongitude());
					// thisView.mAMap.moveCamera(CameraUpdateFactory.changeLatLng(mLatLng));
					thisView.mAMap.animateCamera(CameraUpdateFactory.changeLatLng(mLatLng), 500, null);
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
		};
		mOnTouchListener = new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				System.out.println("touch:::::::::");
				return false;
			}
		};
	}

	public void bindEvent() {
		thisView.threeChoicesView.setOnItemClickListener(mOnItemClickListener);
		mCloudSearch.setOnCloudSearchListener(mCloudSearchListener);
		thisView.backView.setOnClickListener(mOnClickListener);
		thisView.nearbyListView.setOnScrollListener(mOnScrollListener);
		thisView.menuImage.setOnClickListener(this.mOnClickListener);
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

		if (thisView.singleButton != null) {
			thisView.singleButton.setOnClickListener(mOnClickListener);
		}
	}

	public void searchNearby() {
		bound = new SearchBound(new LatLonPoint(latitude, longitude), searchRadius);
		try {
			mQuery = new Query(mTableId, "", bound);
		} catch (AMapCloudException e) {
			e.printStackTrace();
		}
		mQuery.setPageSize(20);
		mQuery.setPageNum(nowpage);
		CloudSearch.Sortingrules sorting = null;
		if (searchStatus == Status.newest) {
			sorting = new CloudSearch.Sortingrules("time", false);
		} else if (searchStatus == Status.hottest) {
			sorting = new CloudSearch.Sortingrules("totalScore", false);
		}
		mQuery.setSortingrules(sorting);
		mCloudSearch.searchCloudAsyn(mQuery);
	}

	public ShareMessage processingData(Map<String, Object> map) {
		ShareMessage message = data.boards.new ShareMessage();
		message.content = (String) map.get("content");
		message.head = (String) map.get("head");
		message.gsid = (String) map.get("gsid");
		message.sid = (String) map.get("sid");
		message.phone = (String) map.get("phone");
		message.totalScore = Integer.valueOf((String) map.get("totalScore"));
		message.type = (String) map.get("type");
		message.time = Long.valueOf((String) map.get("time"));
		message.nickName = (String) map.get("name");
		message.distance = (Integer) map.get("distance");
		String scores = (String) map.get("scores");
		if (scores != null && !"".equals(scores)) {
			scores = scores.substring(0, scores.length() - 1);
			log.e(scores);
			message.scores = gson.fromJson(scores, new TypeToken<HashMap<String, Score>>() {
			}.getType());
		}
		return data.boards.shareMessagesMap.put(message.sid, message);
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
}
