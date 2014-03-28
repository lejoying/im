package com.lejoying.wxgs.app.handler;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.MKGeneralListener;
import com.lejoying.wxgs.app.MainApplication;

public class LocationHandler {
	MainApplication app;

	public void initialize(MainApplication app) {
		this.app = app;
		initBaiduLocation();
		initMap();
	}

	public interface LocationListener {
		public void onReceiveLocation(BDLocation location);
	}

	public interface PointListener {
		public void onReceivePoi(BDLocation poiLocation);
	}

	LocationListener locationListener;
	PointListener pointListener;

	public int requestLocation(LocationListener locationListener) {
		if (!mLocationClient.isStarted()) {
			mLocationClient.start();
		}
		this.locationListener = locationListener;
		return mLocationClient.requestLocation();
	}

	public int requestPoint(PointListener pointListener) {
		if (!mLocationClient.isStarted()) {
			mLocationClient.start();
		}
		this.pointListener = pointListener;
		return mLocationClient.requestPoi();
	}

	// baidu location
	LocationClient mLocationClient = null;
	BDLocationListener myListener = new MyLocationListener();

	void initBaiduLocation() {
		mLocationClient = new LocationClient(app); // 声明LocationClient类
		mLocationClient.setAK("ceDtWxAk6GYKosK2ieQWGSrI");
		mLocationClient.registerLocationListener(myListener);

		LocationClientOption option = new LocationClientOption();
		option.setOpenGps(true);
		option.setAddrType("all");// 返回的定位结果包含地址信息
		option.setCoorType("bd09ll");// 返回的定位结果是百度经纬度,默认值gcj02
		option.disableCache(true);// 禁止启用缓存定位
		option.setPoiNumber(5); // 最多返回POI个数
		option.setPoiDistance(1000); // poi查询距离
		option.setPoiExtraInfo(true); // 是否需要POI的电话和地址等详细信息
		mLocationClient.setLocOption(option);
	}

	public BMapManager manager;

	void initMap() {
		manager = new BMapManager(app);
		manager.init("ceDtWxAk6GYKosK2ieQWGSrI", new MKGeneralListener() {

			@Override
			public void onGetNetworkState(int iError) {
				// if (iError == MKEvent.ERROR_NETWORK_CONNECT) {
				// Toast.makeText(app, "您的网络出错啦！", Toast.LENGTH_LONG).show();
				// } else if (iError == MKEvent.ERROR_NETWORK_DATA) {
				// Toast.makeText(app, "输入正确的检索条件！", Toast.LENGTH_LONG).show();
				// }
				// ...
			}

			@Override
			public void onGetPermissionState(int iError) {
				// // 非零值表示key验证未通过
				// if (iError != 0) {
				// // 授权Key错误：
				// Toast.makeText(
				// app,
				// "请在 DemoApplication.java文件输入正确的授权Key,并检查您的网络连接是否正常！error: "
				// + iError, Toast.LENGTH_LONG).show();
				// } else {
				// Toast.makeText(app, "key认证成功", Toast.LENGTH_LONG).show();
				// }
			}
		});
	}

	class MyLocationListener implements BDLocationListener {
		@Override
		public void onReceiveLocation(BDLocation location) {
			if (location == null) {
				return;
			}
			if (locationListener != null) {
				locationListener.onReceiveLocation(location);
			}
			// StringBuffer sb = new StringBuffer(256);
			// sb.append("time : ");
			// sb.append(location.getTime());
			// sb.append("\nerror code : ");
			// sb.append(location.getLocType());
			// sb.append("\nlatitude : ");
			// sb.append(location.getLatitude());
			// sb.append("\nlontitude : ");
			// sb.append(location.getLongitude());
			// sb.append("\nradius : ");
			// sb.append(location.getRadius());
			// if (location.getLocType() == BDLocation.TypeGpsLocation) {
			// sb.append("\nspeed : ");
			// sb.append(location.getSpeed());
			// sb.append("\nsatellite : ");
			// sb.append(location.getSatelliteNumber());
			// } else if (location.getLocType() ==
			// BDLocation.TypeNetWorkLocation) {
			// sb.append("\naddr : ");
			// sb.append(location.getAddrStr());
			// }
			//
			// System.out.println(sb.toString());
		}

		public void onReceivePoi(BDLocation poiLocation) {
			if (poiLocation == null) {
				return;
			}
			if (pointListener != null) {
				pointListener.onReceivePoi(poiLocation);
			}
			// StringBuffer sb = new StringBuffer(256);
			// sb.append("Poi time : ");
			// sb.append(poiLocation.getTime());
			// sb.append("\nerror code : ");
			// sb.append(poiLocation.getLocType());
			// sb.append("\nlatitude : ");
			// sb.append(poiLocation.getLatitude());
			// sb.append("\nlontitude : ");
			// sb.append(poiLocation.getLongitude());
			// sb.append("\nradius : ");
			// sb.append(poiLocation.getRadius());
			// if (poiLocation.getLocType() == BDLocation.TypeNetWorkLocation) {
			// sb.append("\naddr : ");
			// sb.append(poiLocation.getAddrStr());
			// }
			// if (poiLocation.hasPoi()) {
			// sb.append("\nPoi:");
			// sb.append(poiLocation.getPoi());
			// } else {
			// sb.append("noPoi information");
			// }
			// System.out.println(sb.toString());
		}
	}
}
