package com.lejoying.wxgs.app.handler;

import android.location.Location;
import android.os.Bundle;
import android.test.AndroidTestCase;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.location.LocationManagerProxy;
import com.amap.api.location.LocationProviderProxy;
import com.amap.api.maps.AMap;
import com.amap.api.maps.LocationSource;
import com.lejoying.wxgs.app.MainApplication;

public class AmapLocationHandler extends AndroidTestCase implements
		LocationSource, AMapLocationListener {
	MainApplication app;

	LocationListener mLocationListener;

	private LocationManagerProxy mLocationManagerProxy;
	private OnLocationChangedListener mListener;

	public void initialize(MainApplication app) {
		this.app = app;
		initAmapLocation();
	}

	private void initAmapLocation() {
		mLocationManagerProxy = LocationManagerProxy.getInstance(app);
	}

	public interface LocationListener {
		public void onLocationChangedListener(AMapLocation aMapLocation);
	}

	public void requestMapLocation(AMap aMap, LocationListener locationListener) {
		mLocationListener = locationListener;
		aMap.setLocationSource(this);// 设置定位监听
		aMap.getUiSettings().setMyLocationButtonEnabled(true);// 设置默认定位按钮是否显示
		aMap.setMyLocationEnabled(true);// 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false
		aMap.setMyLocationType(AMap.LOCATION_TYPE_LOCATE);
	}

	public void requestLocationInfomation(LocationListener locationListener) {
		mLocationListener = locationListener;
		mLocationManagerProxy.removeUpdates(this);
		mLocationManagerProxy.requestLocationData(
				LocationProviderProxy.AMapNetwork, 60 * 1000, 15, this);
	}

	@Override
	public void onLocationChanged(Location location) {
		// TODO abandoned

	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO abandoned

	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO abandoned

	}

	@Override
	public void onProviderDisabled(String provider) {
		// TODO abandoned

	}

	@Override
	public void onLocationChanged(AMapLocation amapLocation) {
		if (amapLocation != null
				&& amapLocation.getAMapException().getErrorCode() == 0) {
			if (mListener != null) {
				mListener.onLocationChanged(amapLocation);// 显示系统小蓝点
			}
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
}
