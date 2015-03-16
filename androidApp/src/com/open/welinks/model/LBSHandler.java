package com.open.welinks.model;

import java.math.BigDecimal;

import com.google.gson.Gson;
import com.open.lib.HttpClient;
import com.open.lib.MyLog;
import com.open.welinks.view.ViewManage;

public class LBSHandler {

	Data data = Data.getInstance();
	Parser parser = Parser.getInstance();
	String tag = "LBSHandlers";
	MyLog log = new MyLog(tag, true);

	HttpClient httpClient = HttpClient.getInstance();

	Gson gson = new Gson();

	static LBSHandler lbsHandlers;

	ViewManage viewManage = ViewManage.getInstance();

	public static LBSHandler getInstance() {
		if (lbsHandlers == null) {
			lbsHandlers = new LBSHandler();
		}
		return lbsHandlers;
	}

	static double DEF_PI = 3.14159265359; // PI
	static double DEF_2PI = 6.28318530712; // 2*PI
	static double DEF_PI180 = 0.01745329252; // PI/180.0
	static double DEF_R = 6370693.5; // radius of earth

	public double GetLongDistance(double lon1, double lat1, double lon2, double lat2) {
		// log.e(lon1 + ":" + lon2 + ":" + lat1 + ":" + lat2);
		if (lon1 == 0d || lon2 == 0d || lat1 == 0d || lat2 == 0d) {
			return 0d;
		} else {
			double ew1, ns1, ew2, ns2;
			double distance;
			ew1 = lon1 * DEF_PI180;
			ns1 = lat1 * DEF_PI180;
			ew2 = lon2 * DEF_PI180;
			ns2 = lat2 * DEF_PI180;
			distance = Math.sin(ns1) * Math.sin(ns2) + Math.cos(ns1) * Math.cos(ns2) * Math.cos(ew1 - ew2);
			if (distance > 1.0)
				distance = 1.0;
			else if (distance < -1.0)
				distance = -1.0;
			distance = DEF_R * Math.acos(distance);
			return distance / 1000;
		}
	}

	public String pointDistance(String longitude, String latitude, String longitude2, String latitude2) {
		String distance = GetLongDistance(checkNumber(longitude), checkNumber(latitude), checkNumber(longitude2), checkNumber(latitude2)) + "";
		// LatLng latLng1 = new LatLng(checkNumber(longitude), checkNumber(latitude));
		// LatLng latLng2 = new LatLng(checkNumber(longitude2), checkNumber(latitude2));
		// String distance0 = AMapUtils.calculateLineDistance(latLng1, latLng2) + "";
		// Log.e(tag, "distance0:      " + distance0);
		// Log.e(tag, "distance:      " + distance);
		if (distance.indexOf(".") != -1) {
			if (distance.substring(distance.indexOf(".") + 1).length() > 2) {
				distance = distance.substring(0, distance.indexOf(".") + 3);
			}
		}
		return distance;
	}

	String formatNumber(float num) {
		BigDecimal b = new BigDecimal(num);
		double f1 = b.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
		return f1 + "";
	}

	double checkNumber(String num) {
		if (num == null || num.equals("")) {
			num = "0";
		}
		return Double.valueOf(num);
	}

	public static String getDistance(int distance) {
		if (distance < 1000) {
			return distance + "m";
		} else {
			return Math.round(distance / 1000 / 1.0) + "km";
		}
	}
}
