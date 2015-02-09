package com.open.welinks.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;

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
import com.amap.api.maps2d.AMapUtils;
import com.amap.api.maps2d.model.LatLng;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.open.lib.MyLog;
import com.open.welinks.NearbyActivity;
import com.open.welinks.R;
import com.open.welinks.customListener.OnDownloadListener;
import com.open.welinks.customView.ThreeChoicesView.OnItemClickListener;
import com.open.welinks.model.Constant;
import com.open.welinks.model.Data;
import com.open.welinks.model.Data.Relationship.Friend;
import com.open.welinks.model.Data.Relationship.Group;
import com.open.welinks.oss.DownloadFile;
import com.open.welinks.view.NearbyView;

public class NearbyController {
	public Data data = Data.getInstance();

	public String tag = "NearbyController";
	public MyLog log = new MyLog(tag, true);

	public NearbyView thisView;
	public NearbyController thisController;
	public NearbyActivity thisActivity;

	public LocationManagerProxy mLocationManagerProxy;
	public SearchBound bound;
	public Query mQuery;
	public Sortingrules mSortingrules;
	public CloudSearch mCloudSearch;
	public ArrayList<CloudItem> mCloudItems;
	public String type;

	public OnClickListener mOnClickListener;
	public OnCloudSearchListener mCloudSearchListener;
	public AMapLocationListener mAMapLocationListener;
	public OnItemClickListener mOnItemClickListener;
	public OnDownloadListener downloadListener;
	public OnScrollListener mOnScrollListener;

	public ImageLoader imageLoader = ImageLoader.getInstance();

	public String mTableId;
	public ArrayList<Map<String, Object>> mInfomations;
	public AMapLocation mAmapLocation;

	public Status status;

	public enum Status {
		account, group, square
	}

	public NearbyController(NearbyActivity thisActivity) {
		thisController = this;
		this.thisActivity = thisActivity;
	}

	public void onCreate() {
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
		} else if ("square".equals(type)) {
			status = Status.square;
			mTableId = Constant.SQUARETABLEID;
			thisView.NearbyLayoutID = R.layout.nearby_item_group;
			thisView.threeChoicesView.setDefaultItem(1);
		}

		mInfomations = new ArrayList<Map<String, Object>>();
		mCloudSearch = new CloudSearch(thisActivity);

		initializeListeners();
		bindEvent();

		mLocationManagerProxy = LocationManagerProxy.getInstance(thisActivity);
		mLocationManagerProxy.requestLocationData(LocationProviderProxy.AMapNetwork, -1, 15, mAMapLocationListener);
		mLocationManagerProxy.setGpsEnable(true);
	}

	public boolean loadfinish = true;

	public int nowpage = 0;

	public void initializeListeners() {
		mOnScrollListener = new OnScrollListener() {

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
				int lastItemid = thisView.nearbyListView.getLastVisiblePosition();
				if ((lastItemid + 1) == totalItemCount) {
					if (totalItemCount > 0) {
						if (loadfinish) {
							loadfinish = false;
							searchNearByPolygon(nowpage);
						}
					}
				}
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
				}
			}
		};

		mOnItemClickListener = thisView.threeChoicesView.new OnItemClickListener() {
			@Override
			public void onButtonCilck(int position) {
				if (position == 3) {
					status = Status.account;
					mTableId = Constant.ACCOUNTTABLEID;
					thisView.NearbyLayoutID = R.layout.nearby_item_account;
					nowpage = 0;
					loadfinish = true;
					thisView.nearbyListView.setSelection(0);
					searchNearByPolygon(nowpage);
				} else if (position == 2) {
					status = Status.group;
					mTableId = Constant.GROUPTABLEID;
					thisView.NearbyLayoutID = R.layout.nearby_item_group;
					nowpage = 0;
					loadfinish = true;
					thisView.nearbyListView.setSelection(0);
					searchNearByPolygon(nowpage);
				} else if (position == 1) {
					status = Status.square;
					mTableId = Constant.SQUARETABLEID;
					thisView.NearbyLayoutID = R.layout.nearby_item_group;
					nowpage = 0;
					loadfinish = true;
					thisView.nearbyListView.setSelection(0);
					searchNearByPolygon(nowpage);
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
							if (mAmapLocation == null) {
								return;
							}
							LatLng point = new LatLng(mAmapLocation.getLatitude(), mAmapLocation.getLongitude());
							if (nowpage == 0) {
								mInfomations.clear();
							}
							if (mCloudItems.size() > 0) {
								nowpage++;
								loadfinish = true;
								log.e(mCloudItems.size() + "---nowpage1:" + nowpage);
							} else {
								log.e(mCloudItems.size() + "---nowpage2:" + nowpage);
							}
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
								mInfomations.add(map);
							}
							thisView.nearbyAdapter.notifyDataSetChanged();
						}
					}
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
					// searchNearby(amapLocation);
					searchNearByPolygon(0);
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
	}

	public void bindEvent() {
		thisView.threeChoicesView.setOnItemClickListener(mOnItemClickListener);
		mCloudSearch.setOnCloudSearchListener(mCloudSearchListener);
		thisView.backView.setOnClickListener(mOnClickListener);
		thisView.nearbyListView.setOnScrollListener(mOnScrollListener);
	}

	public void searchNearby(AMapLocation amapLocation) {
		bound = new SearchBound(new LatLonPoint(amapLocation.getLatitude(), amapLocation.getLongitude()), 50000);
		try {
			mQuery = new Query(mTableId, "", bound);
		} catch (AMapCloudException e) {
			e.printStackTrace();
		}
		// if (status == Status.group) {
		// mQuery.addFilterString("gtype", "group");
		// } else if (status == Status.square) {
		// mQuery.addFilterString("gtype", "community");
		// }
		mQuery.setPageSize(50);
		// mQuery.setPageNum(1);
		mSortingrules = new Sortingrules(1);
		mQuery.setSortingrules(mSortingrules);

		mCloudSearch.searchCloudAsyn(mQuery);
	}

	public void searchNearByPolygon(int nowpage) {
		List<LatLonPoint> points = new ArrayList<LatLonPoint>();
		points.add(new LatLonPoint(5.965754, 70.136719));
		points.add(new LatLonPoint(56.170023, 140.097656));
		try {
			mQuery = new Query(mTableId, "", new SearchBound(points));
		} catch (AMapCloudException e) {
			e.printStackTrace();
		}
		mQuery.setPageSize(50);
		mQuery.setPageNum(nowpage);
		mSortingrules = new Sortingrules(1);// 0为权重降序排列，1为距离升序排列。
		mQuery.setSortingrules(mSortingrules);
		// mQuery.setBound(new SearchBound(points));
		mCloudSearch.searchCloudAsyn(mQuery);

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
}
