package com.open.welinks.controller;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Location;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

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
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.open.welinks.BusinessCardActivity;
import com.open.welinks.NearbyActivity;
import com.open.welinks.R;
import com.open.welinks.customListener.OnDownloadListener;
import com.open.welinks.customView.ThreeChoicesView.OnItemClickListener;
import com.open.welinks.model.API;
import com.open.welinks.model.Constant;
import com.open.welinks.model.Data;
import com.open.welinks.model.Data.Relationship.Friend;
import com.open.welinks.model.Data.Relationship.Group;
import com.open.welinks.view.NearbyView;

public class NearbyController {
	public Data data = Data.getInstance();

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

	public ImageLoader imageLoader = ImageLoader.getInstance();
	public DownloadFileList downloadFileList = DownloadFileList.getInstance();
	public DownloadFile downloadFile;
	public DisplayImageOptions options;
	public OnDownloadListener downloadListener;

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
		options = new DisplayImageOptions.Builder().showImageOnLoading(R.drawable.ic_stub).showImageForEmptyUri(R.drawable.ic_empty).showImageOnFail(R.drawable.ic_error).cacheInMemory(true).cacheOnDisk(true).considerExifParams(true).bitmapConfig(Bitmap.Config.RGB_565).displayer(new RoundedBitmapDisplayer(40)).build();
		mCloudSearch = new CloudSearch(thisActivity);

		initializeListeners();
		bindEvent();

		mLocationManagerProxy = LocationManagerProxy.getInstance(thisActivity);
		mLocationManagerProxy.requestLocationData(LocationProviderProxy.AMapNetwork, -1, 15, mAMapLocationListener);
		mLocationManagerProxy.setGpsEnable(true);
	}

	public void initializeListeners() {

		mOnClickListener = new OnClickListener() {

			@SuppressWarnings("unchecked")
			@Override
			public void onClick(View view) {
				if (view.getTag(R.id.tag_first) != null) {
					boolean isTemp = judgeTempRelation((Map<String, Object>) view.getTag(R.id.tag_third));
					String type = (String) view.getTag(R.id.tag_first);
					String key = (String) view.getTag(R.id.tag_second);
					Intent intent = new Intent(thisActivity, BusinessCardActivity.class);
					intent.putExtra("type", type);
					intent.putExtra("key", key);
					intent.putExtra("isTemp", isTemp);
					thisActivity.startActivity(intent);
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
					searchNearByPolygon();
				} else if (position == 2) {
					status = Status.group;
					mTableId = Constant.GROUPTABLEID;
					thisView.NearbyLayoutID = R.layout.nearby_item_group;
					searchNearByPolygon();
				} else if (position == 1) {
					status = Status.square;
					mTableId = Constant.SQUARETABLEID;
					thisView.NearbyLayoutID = R.layout.nearby_item_group;
					searchNearByPolygon();
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
							mInfomations.clear();
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
				// unused

			}
		};
		mAMapLocationListener = new AMapLocationListener() {

			@Override
			public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
				// unused

			}

			@Override
			public void onProviderEnabled(String arg0) {
				// unused

			}

			@Override
			public void onProviderDisabled(String arg0) {
				// unused

			}

			@Override
			public void onLocationChanged(Location location) {
				// unused
			}

			@Override
			public void onLocationChanged(AMapLocation amapLocation) {
				mLocationManagerProxy.removeUpdates(mAMapLocationListener);
				mLocationManagerProxy.destroy();
				if (amapLocation != null && amapLocation.getAMapException().getErrorCode() == 0) {
					mAmapLocation = amapLocation;
					// searchNearby(amapLocation);
					searchNearByPolygon();
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

	public void searchNearByPolygon() {
		List<LatLonPoint> points = new ArrayList<LatLonPoint>();
		points.add(new LatLonPoint(5.965754, 70.136719));
		points.add(new LatLonPoint(56.170023, 140.097656));
		try {
			mQuery = new Query(mTableId, "", new SearchBound(points));
		} catch (AMapCloudException e) {
			e.printStackTrace();
		}
		mQuery.setPageSize(50);
		// mQuery.setPageNum(1);
		mSortingrules = new Sortingrules(1);
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

	public void setImageOnView(String fileName, ImageView view) {
		File sdFile = Environment.getExternalStorageDirectory();
		File file = new File(sdFile, "welinks/heads/" + fileName);
		final String url = API.DOMAIN_COMMONIMAGE + "heads/" + fileName;
		final String path = file.getAbsolutePath();
		imageLoader.displayImage("file://" + file.getAbsolutePath(), view, options, new SimpleImageLoadingListener() {
			@Override
			public void onLoadingStarted(String imageUri, View view) {
			}

			@Override
			public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
				downloadFile = new DownloadFile(url, path);
				downloadFile.view = view;
				downloadFile.setDownloadFileListener(thisController.downloadListener);
				downloadFileList.addDownloadFile(downloadFile);
			}

			@Override
			public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {

			}
		});
	}
}
