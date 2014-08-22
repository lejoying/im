package com.lejoying.wxgs.app.handler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.test.AndroidTestCase;
import android.view.View;
import android.widget.Toast;

import com.amap.api.cloud.model.AMapCloudException;
import com.amap.api.cloud.model.CloudItem;
import com.amap.api.cloud.model.CloudItemDetail;
import com.amap.api.cloud.model.LatLonPoint;
import com.amap.api.cloud.search.CloudResult;
import com.amap.api.cloud.search.CloudSearch;
import com.amap.api.cloud.search.CloudSearch.OnCloudSearchListener;
import com.amap.api.cloud.search.CloudSearch.SearchBound;
import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.location.LocationManagerProxy;
import com.amap.api.location.LocationProviderProxy;
import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.AMap.InfoWindowAdapter;
import com.amap.api.maps.AMap.OnInfoWindowClickListener;
import com.amap.api.maps.AMap.OnMapLongClickListener;
import com.amap.api.maps.AMap.OnMarkerClickListener;
import com.amap.api.maps.AMap.OnMarkerDragListener;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.Circle;
import com.amap.api.maps.model.CircleOptions;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.lejoying.wxgs.app.MainApplication;

public class AmapLocationHandler extends AndroidTestCase implements
		LocationSource, AMapLocationListener, OnCloudSearchListener,
		OnMarkerClickListener, OnInfoWindowClickListener, InfoWindowAdapter,
		OnMarkerDragListener, OnMapLongClickListener {
	MainApplication app;
	LocationListener mLocationListener;
	CreateLocationListener mCreateLocationListener;

	private LocationManagerProxy mLocationManagerProxy;
	private OnLocationChangedListener mListener;
	private AMap mAMap;
	private AMapLocation amapLocation;
	private CloudSearch mCloudSearch;
	private CloudSearch.Query mQuery;
	private List<CloudItem> mCloudItems;
	private Marker createMarker;

	public String mAccountTableId = "53eacbe4e4b0693fbf5fd13b";
	public String mGroupTableId = "53eacbb9e4b0693fbf5fd0f6";
	public int TPYE_ACCOUNT = 0x1, TPYE_GROUP = 0x2, TPYE_SQUARE = 0x3;
	private String createAddress = "";
	private int searchType;
	private ArrayList<Marker> mPoiMarks = new ArrayList<Marker>();
	private ArrayList<Circle> mPoiCircles = new ArrayList<Circle>();
	private List<Map<String, Object>> mInfomations = new ArrayList<Map<String, Object>>();

	public void initialize(MainApplication app) {
		this.app = app;
		initAmapLocation();
	}

	private void initAmapLocation() {

	}

	public interface LocationListener {
		public void onLocationChangedListener(AMapLocation aMapLocation);
	}

	public interface CreateLocationListener {
		public void onCreateLocationChangedListener(double longitude,
				double latitude);
	}

	public void requestMapLocation(AMap aMap, LocationListener locationListener) {
		mLocationManagerProxy = LocationManagerProxy.getInstance(app);
		mLocationListener = locationListener;
		mAMap = aMap;
		aMap.setLocationSource(this);// 设置定位监听
		aMap.getUiSettings().setMyLocationButtonEnabled(true);// 设置默认定位按钮是否显示
		aMap.setMyLocationEnabled(true);// 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false
		aMap.setMyLocationType(AMap.LOCATION_TYPE_MAP_FOLLOW);
	}

	public void requestLocationInfomation(LocationListener locationListener) {
		mLocationManagerProxy = LocationManagerProxy.getInstance(app);
		mLocationListener = locationListener;
		mLocationManagerProxy.removeUpdates(this);
		mLocationManagerProxy.requestLocationData(
				LocationProviderProxy.AMapNetwork, 60 * 1000, 15, this);
	}

	public void searchAccountsByBound(AMap mAMap) {
		searchType = TPYE_ACCOUNT;
		this.mAMap = mAMap;
		SearchBound bound = new SearchBound(new LatLonPoint(
				amapLocation.getLatitude(), amapLocation.getLongitude()), 10000);
		try {
			mQuery = new CloudSearch.Query(mAccountTableId, "", bound);
		} catch (AMapCloudException e) {
			e.printStackTrace();
		}
		mQuery.addFilterString("online", "1");
		mQuery.addFilterString("phone!", app.data.user.phone);
		searchByBound();
	}

	public void searchGroupsByBound(AMap mAMap) {
		searchType = TPYE_GROUP;
		this.mAMap = mAMap;
		SearchBound bound = new SearchBound(new LatLonPoint(
				amapLocation.getLatitude(), amapLocation.getLongitude()), 10000);
		try {
			mQuery = new CloudSearch.Query(mGroupTableId, "", bound);
		} catch (AMapCloudException e) {
			e.printStackTrace();
		}
		mQuery.addFilterString("gtype", "group");
		mQuery.setPageSize(30);
		searchByBound();
	}

	public void searchSquaresByBound(AMap mAMap) {
		searchType = TPYE_SQUARE;
		this.mAMap = mAMap;
		SearchBound bound = new SearchBound(new LatLonPoint(
				amapLocation.getLatitude(), amapLocation.getLongitude()), 10000);
		try {
			mQuery = new CloudSearch.Query(mGroupTableId, "", bound);
		} catch (AMapCloudException e) {
			e.printStackTrace();
		}
		mQuery.addFilterString("gtype", "community");
		mQuery.setPageSize(30);
		searchByBound();
	}

	private void searchByBound() {
		mCloudSearch = new CloudSearch(app);
		mCloudSearch.setOnCloudSearchListener(this);
		mAMap.setOnMarkerClickListener(this);
		mAMap.setOnInfoWindowClickListener(this);
		mAMap.setInfoWindowAdapter(this);
		CloudSearch.Sortingrules sorting = new CloudSearch.Sortingrules("_id",
				false);
		mQuery.setSortingrules(sorting);
		mCloudSearch.searchCloudAsyn(mQuery);
	}

	public void createLocation(AMap mAMap,
			CreateLocationListener createLocationListener) {
		mCreateLocationListener = createLocationListener;
		mAMap.setOnMapLongClickListener(this);
		mAMap.setOnMarkerDragListener(this);
		createMarker = null;
	}

	@SuppressWarnings("rawtypes")
	private void parsingInfomation(List<CloudItem> mCloudItems) {
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
	}

	private void addToMap() {
		if (searchType == TPYE_ACCOUNT) {
			for (int i = 0; i < mCloudItems.size(); i++) {
				Marker marker = mAMap.addMarker(getMarkerOptions(mCloudItems
						.get(i)));
				marker.setObject(i);
				mPoiMarks.add(marker);
			}
		} else if (searchType == TPYE_GROUP || searchType == TPYE_SQUARE) {
			for (int i = 0; i < mCloudItems.size(); i++) {
				Circle circle = mAMap.addCircle(getCircleOptions(mCloudItems
						.get(i)));
				mPoiCircles.add(circle);
			}
		}
	}

	private void removeFromMap() {
		for (Marker mark : mPoiMarks) {
			mark.remove();
		}
		for (Circle circle : mPoiCircles) {
			circle.remove();
		}
	}

	private MarkerOptions getMarkerOptions(CloudItem mCloudItem) {
		return new MarkerOptions()
				.position(
						new LatLng(mCloudItem.getLatLonPoint().getLatitude(),
								mCloudItem.getLatLonPoint().getLongitude()))
				.title(mCloudItem.getTitle()).snippet(mCloudItem.getSnippet())
				.draggable(false);
	}

	private CircleOptions getCircleOptions(CloudItem mCloudItem) {
		return new CircleOptions()
				.center(new LatLng(mCloudItem.getLatLonPoint().getLatitude(),
						mCloudItem.getLatLonPoint().getLongitude()))
				.radius(2000).strokeColor(Color.argb(50, 1, 1, 1))
				.fillColor(Color.argb(50, 1, 1, 1)).strokeWidth(25);
	}

	private View createInfoWindow(Map<String, Object> info) {
		// TODO
		return null;
	}

	@Override
	public void onLocationChanged(Location location) {
		// abandoned

	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// abandoned

	}

	@Override
	public void onProviderEnabled(String provider) {
		// abandoned

	}

	@Override
	public void onProviderDisabled(String provider) {
		// abandoned

	}

	@Override
	public void onLocationChanged(AMapLocation amapLocation) {
		if (amapLocation != null
				&& amapLocation.getAMapException().getErrorCode() == 0) {
			if (mListener != null) {
				mListener.onLocationChanged(amapLocation);// 显示系统小蓝点
				// mAMap.animateCamera(CameraUpdateFactory
				// .newCameraPosition(new CameraPosition(new LatLng(
				// amapLocation.getLongitude(), amapLocation
				// .getLatitude()), 18, 0, 0)));
			}
			this.amapLocation = amapLocation;
			mLocationListener.onLocationChangedListener(amapLocation);
		}
	}

	@Override
	public void activate(OnLocationChangedListener listener) {
		mListener = listener;
		mLocationManagerProxy.removeUpdates(this);
		mLocationManagerProxy.requestLocationData(
				LocationProviderProxy.AMapNetwork, 60 * 1000, 10, this);

	}

	@Override
	public void deactivate() {
		mListener = null;
		if (mLocationManagerProxy != null) {
			mLocationManagerProxy.removeUpdates(this);
			mLocationManagerProxy.destroy();
		}
		mLocationManagerProxy = null;
	}

	@Override
	public void onCloudItemDetailSearched(CloudItemDetail cloudItemResult,
			int rCode) {
		// abandoned
	}

	@Override
	public void onCloudSearched(CloudResult cloudResult, int rCode) {
		if (rCode == 0) {
			if (cloudResult != null && cloudResult.getQuery() != null) {
				if (cloudResult.getQuery().equals(mQuery)) {
					mCloudItems = cloudResult.getClouds();
					if (mCloudItems != null && mCloudItems.size() > 0) {
						parsingInfomation(mCloudItems);
						removeFromMap();
						addToMap();
					}
				} else {
					Toast.makeText(app, "对不起，没有搜索到相关数据！", Toast.LENGTH_LONG)
							.show();
				}
			} else {
				Toast.makeText(app, "对不起，没有搜索到相关数据！", Toast.LENGTH_LONG).show();
			}
		} else {
			Toast.makeText(app, "搜索失败,请检查网络连接！" + rCode, Toast.LENGTH_LONG)
					.show();
		}
	}

	@Override
	public void onInfoWindowClick(Marker marker) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean onMarkerClick(Marker marker) {
		if (marker.isInfoWindowShown()) {
			marker.hideInfoWindow();
		} else {
			marker.showInfoWindow();
		}
		return true;
	}

	@Override
	public View getInfoContents(Marker marker) {
		Map<String, Object> info = new HashMap<String, Object>();
		if (marker.equals(createMarker)) {
			info.put("address", marker.getSnippet());
		} else {
			info = mInfomations.get((Integer) marker.getObject());
		}
		return createInfoWindow(info);
	}

	@Override
	public View getInfoWindow(Marker marker) {
		Map<String, Object> info = new HashMap<String, Object>();
		if (marker.equals(createMarker)) {
			info.put("address", marker.getSnippet());
		} else {
			info = mInfomations.get((Integer) marker.getObject());
		}
		return createInfoWindow(info);
	}

	@Override
	public void onMapLongClick(LatLng point) {
		if (createMarker == null) {
			createMarker = mAMap.addMarker(new MarkerOptions().position(point)
					.draggable(true).title(""));
		}
		mCreateLocationListener.onCreateLocationChangedListener(
				point.longitude, point.latitude);
	}

	@Override
	public void onMarkerDrag(Marker marker) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onMarkerDragEnd(Marker marker) {
		marker.hideInfoWindow();
		mCreateLocationListener.onCreateLocationChangedListener(
				marker.getPosition().longitude, marker.getPosition().latitude);
	}

	@Override
	public void onMarkerDragStart(Marker marker) {
		marker.showInfoWindow();

	}
}
