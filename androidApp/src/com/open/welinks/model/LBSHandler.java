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

	public String pointDistance1(String longitude, String latitude, String longitude2, String latitude2) {
		double distance = GetLongDistance1(checkNumber(longitude), checkNumber(latitude), checkNumber(longitude2), checkNumber(latitude2));
		String distanceStr = "";
		if (distance * 1000 <= Constant.DEFAULMINDISTANCE) {
			return Constant.DEFAULMINDISTANCESTRING;
		}
		distanceStr = String.valueOf(distance);
		if (distanceStr.indexOf(".") != -1) {
			if (distanceStr.substring(distanceStr.indexOf(".") + 1).length() > 3) {
				distanceStr = distanceStr.substring(0, distanceStr.indexOf(".") + 4);
			}
		}
		distanceStr += "km";
		return distanceStr;
	}

	double earthRadius = 6378137;

	public double GetLongDistance1(double lon1, double lat1, double lon2, double lat2) {
		// log.e(lon1 + ":" + lon2 + ":" + lat1 + ":" + lat2);
		if (lon1 == 0d || lon2 == 0d || lat1 == 0d || lat2 == 0d) {
			return 0d;
		} else {
			double lon_1, lat_1, lon_2, lat_2;
			double distance = 0;
			lon_1 = lon1 * Math.PI / 180.0;
			lon_2 = lon2 * Math.PI / 180.0;
			lat_1 = lat1 * Math.PI / 180.0;
			lat_2 = lat2 * Math.PI / 180.0;
			double differenceLatitude = lat_1 - lat_2;
			double differenceLongitude = lon_1 - lon_2;

			distance = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(differenceLatitude / 2), 2) + Math.cos(lat_1) * Math.cos(lat_2) * Math.pow(Math.sin(differenceLongitude / 2), 2)));
			distance = distance * earthRadius;
			distance = Math.round(distance * 10000) / 10000;

			return distance / 1000;
		}
	}

	static double DEF_R = 6378137; // radius of earth

	public double GetLongDistance(double lon1, double lat1, double lon2, double lat2) {
		// log.e(lon1 + ":" + lon2 + ":" + lat1 + ":" + lat2);
		if (lon1 == 0d || lon2 == 0d || lat1 == 0d || lat2 == 0d) {
			return 0d;
		} else {
			double ew1, ns1, ew2, ns2;
			double distance;
			ew1 = lon1 * Math.PI / 180.0;
			ns1 = lat1 * Math.PI / 180.0;
			ew2 = lon2 * Math.PI / 180.0;
			ns2 = lat2 * Math.PI / 180.0;
			distance = Math.sin(ns1) * Math.sin(ns2) + Math.cos(ns1) * Math.cos(ns2) * Math.cos(ew1 - ew2);
			if (distance > 1.0)
				distance = 1.0;
			else if (distance < -1.0)
				distance = -1.0;
			distance = DEF_R * Math.acos(distance);
			distance = Math.round(distance);
			return distance / 1000;
		}
	}

	public String pointDistance(String longitude, String latitude, String longitude2, String latitude2) {
		double distance = GetLongDistance(checkNumber(longitude), checkNumber(latitude), checkNumber(longitude2), checkNumber(latitude2));
		String distanceStr = "";
		if (distance * 1000 <= Constant.DEFAULMINDISTANCE) {
			return Constant.DEFAULMINDISTANCESTRING;
		}
		distanceStr = String.valueOf(distance);
		if (distanceStr.indexOf(".") != -1) {
			if (distanceStr.substring(distanceStr.indexOf(".") + 1).length() > 3) {
				distanceStr = distanceStr.substring(0, distanceStr.indexOf(".") + 4);
			}
		}
		distanceStr += "km";
		return distanceStr;
	}

	String formatNumber(float num) {
		BigDecimal b = new BigDecimal(num);
		double f1 = b.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
		return f1 + "";
	}

	double checkNumber(String num) {
		if (num == null || num.equals("")) {
			log.e("checkNumber null" + num);
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
