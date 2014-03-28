package com.lejoying.wxgs.activity.utils;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

import com.baidu.location.BDLocation;
import com.lejoying.wxgs.app.MainApplication;
import com.lejoying.wxgs.app.data.API;
import com.lejoying.wxgs.app.handler.LocationHandler.LocationListener;
import com.lejoying.wxgs.app.handler.NetworkHandler.Settings;

public class LocationUtils {

	static MainApplication app = MainApplication.getMainApplication();

	static long updateTime;

	public static void updateLocation() {
		app.locationHandler.requestLocation(new LocationListener() {
			@Override
			public void onReceiveLocation(final BDLocation location) {
				updata(location.getLongitude(), location.getLatitude());
			}
		});
	}

	public static void updateLocation(double longitude, double latitude) {
		updata(longitude, latitude);
	}

	static void updata(final double longitude, final double latitude) {
		long currentTime = System.currentTimeMillis();
		if (currentTime - updateTime < 3600000) {
			return;
		}
		updateTime = currentTime;
		app.networkHandler.connection(new CommonNetConnection() {

			@Override
			protected void settings(Settings settings) {
				settings.url = API.DOMAIN + API.LBS_UPDATELOCATION;
				Map<String, String> params = new HashMap<String, String>();
				params.put("phone", app.data.user.phone);
				params.put("accessKey", app.data.user.accessKey);
				params.put("account", "{\"mainBusiness\":\""
						+ app.data.user.mainBusiness + "\",\"head\":\""
						+ app.data.user.head + "\",\"nickName\":\""
						+ app.data.user.nickName + "\"}");
				params.put("location", "{\"longitude\":\"" + longitude
						+ "\",\"latitude\":\"" + latitude + "\"}");
				settings.params = params;
			}

			@Override
			public void success(JSONObject jData) {
				// TODO Auto-generated method stub

			}
		});
	}
}
