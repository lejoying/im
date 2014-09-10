package com.open.welinks.controller;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Location;
import android.os.Bundle;
import android.os.Environment;
import android.sax.StartElementListener;
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
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.open.welinks.BusinessCardActivity;
import com.open.welinks.NearbyActivity;
import com.open.welinks.R;
import com.open.welinks.model.API;
import com.open.welinks.model.Constant;
import com.open.welinks.model.Data;
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

	public ImageLoader imageLoader = ImageLoader.getInstance();
	public DownloadFileList downloadFileList = DownloadFileList.getInstance();
	public DownloadFile downloadFile;
	public DisplayImageOptions options;
	public OnDownloadListener downloadListener;

	public String mTableId;
	public ArrayList<Map<String, Object>> mInfomations;

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
			mTableId = Constant.mAccountTableId;
			thisView.NearbyLayoutID = R.layout.nearby_item_account;
			thisView.titleContent.setText("附近的人");
		} else if ("group".equals(type)) {
			status = Status.group;
			mTableId = Constant.mGroupTableId;
			thisView.NearbyLayoutID = R.layout.nearby_item_group;
			thisView.titleContent.setText("附近的群组");
		} else if ("square".equals(type)) {
			status = Status.square;
			mTableId = Constant.mSquareTableId;
			thisView.NearbyLayoutID = R.layout.nearby_item_group;
			thisView.titleContent.setText("附近的广场");
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

			@Override
			public void onClick(View view) {
				if (view.getTag(R.id.tag_first) != null) {
					String type = (String) view.getTag(R.id.tag_first);
					String key = (String) view.getTag(R.id.tag_second);
					Intent intent = new Intent(thisActivity, BusinessCardActivity.class);
					intent.putExtra("type", type);
					intent.putExtra("key", key);
					thisActivity.startActivity(intent);
				} else if (view.equals(thisView.backView)) {
					thisActivity.finish();
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
							mInfomations.clear();
							for (CloudItem item : mCloudItems) {
								Map<String, Object> map = new HashMap<String, Object>();
								map.put("location", item.getLatLonPoint().toString());
								map.put("name", item.getTitle());
								map.put("address", item.getSnippet());
								map.put("distance", item.getDistance());
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
					searchNearby(amapLocation);
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
