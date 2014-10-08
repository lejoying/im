package com.open.welinks.model;

import java.math.BigDecimal;
import java.util.ArrayList;

import android.util.Log;

import com.amap.api.maps2d.AMapUtils;
import com.amap.api.maps2d.model.LatLng;
import com.google.gson.Gson;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.open.lib.HttpClient;
import com.open.lib.MyLog;
import com.open.welinks.controller.MainController.LBSAccountData;
import com.open.welinks.controller.MainController.LBSGroupData;
import com.open.welinks.model.Data.Relationship.Group;
import com.open.welinks.model.Data.UserInformation.User;
import com.open.welinks.view.ViewManage;

public class LBSHandlers {

	Data data = Data.getInstance();
	Parser parser = Parser.getInstance();
	String tag = "LBSHandlers";
	MyLog log = new MyLog(tag, true);

	HttpClient httpClient = HttpClient.getInstance();

	Gson gson = new Gson();

	static LBSHandlers lbsHandlers;

	ViewManage viewManage = ViewManage.getInstance();

	public static LBSHandlers getInstance() {
		if (lbsHandlers == null) {
			lbsHandlers = new LBSHandlers();
		}
		return lbsHandlers;
	}

	public void uplodSquareLbsData(String gid) {
		parser.check();
		HttpUtils httpUtils = new HttpUtils();
		RequestParams params = new RequestParams();
		params.addQueryStringParameter("tableid", Constant.SQUARETABLEID);
		params.addQueryStringParameter("filter", "gid:" + gid);
		params.addQueryStringParameter("key", Constant.LBS_KSY);
		httpUtils.send(HttpMethod.GET, API.LBS_DATA_SEARCH, params, LBSSquareDataSearch);
	}

	public void uplodGroupLbsData(String gid) {
		parser.check();
		HttpUtils httpUtils = new HttpUtils();
		RequestParams params = new RequestParams();
		params.addQueryStringParameter("tableid", Constant.GROUPTABLEID);
		params.addQueryStringParameter("filter", "gid:" + gid);
		params.addQueryStringParameter("key", Constant.LBS_KSY);
		httpUtils.send(HttpMethod.GET, API.LBS_DATA_SEARCH, params, LBSGroupDataSearch);
	}

	public void uplodUserLbsData() {
		parser.check();
		User user = data.userInformation.currentUser;
		HttpUtils httpUtils = new HttpUtils();
		RequestParams params = new RequestParams();
		params.addQueryStringParameter("tableid", Constant.ACCOUNTTABLEID);
		params.addQueryStringParameter("filter", "phone:" + user.phone);
		params.addQueryStringParameter("key", Constant.LBS_KSY);
		httpUtils.send(HttpMethod.GET, API.LBS_DATA_SEARCH, params, LBSAccountDataSearch);
	}

	void modifyLBSData(String type, String id, String key) {
		HttpUtils httpUtils = new HttpUtils();
		RequestParams params = new RequestParams();
		if ("Account".equals(type)) {
			User user = data.userInformation.currentUser;
			LBSAccountData data = viewManage.mainView.thisController.new LBSAccountData();
			data._id = id;
			data._name = user.nickName;
			data._location = user.longitude + "," + user.latitude;
			data._address = viewManage.mainView.thisController.userAddress;
			data.phone = user.phone;
			data.sex = user.sex;
			data.head = user.head;
			data.mainBusiness = user.mainBusiness;
			data.lastlogintime = user.lastlogintime;
			params.addBodyParameter("data", gson.toJson(data));
			params.addBodyParameter("tableid", Constant.ACCOUNTTABLEID);
		} else if ("Group".equals(type) || "Square".equals(type)) {
			Group group = data.relationship.groupsMap.get(key);
			LBSGroupData data = viewManage.mainView.thisController.new LBSGroupData();
			data._id = id;
			data._name = group.name;
			// data._location = group.longitude + "," + group.latitude;
			// data._address = viewManage.mainView.thisController.userAddress;
			data.icon = group.icon;
			data.gid = group.gid + "";
			data.description = group.description;
			data.background = group.background;
			// data.gtype = "group";
			params.addBodyParameter("data", gson.toJson(data));
			if ("Group".equals(type)) {
				params.addBodyParameter("tableid", Constant.GROUPTABLEID);
			} else if ("Square".equals(type)) {
				params.addBodyParameter("tableid", Constant.SQUARETABLEID);
			}
		}

		params.addBodyParameter("key", Constant.LBS_KSY);
		params.addBodyParameter("loctype", "2");
		httpUtils.send(HttpMethod.POST, API.LBS_DATA_UPDATA, params, lbsDataUpdata);
	}

	RequestCallBack<String> lbsDataUpdata = httpClient.new ResponseHandler<String>() {
		class Response {
			public int status;
			public String info;
		}

		public void onSuccess(ResponseInfo<String> responseInfo) {
			Response response = gson.fromJson(responseInfo.result, Response.class);
			if (response.status == 1) {
				log.e(tag, "updata lbs success");

			} else {
				log.e(tag, "updata*" + response.info);
			}
		};
	};

	RequestCallBack<String> LBSAccountDataSearch = httpClient.new ResponseHandler<String>() {
		class Response {
			public int status;
			// public String info;
			public int count;
			public ArrayList<data> datas;

			class data {
				public String _id;
				public String phone;
			}
		}

		public void onSuccess(ResponseInfo<String> responseInfo) {
			Log.e(tag, responseInfo.result);
			try {

				Response response = gson.fromJson(responseInfo.result, Response.class);
				if (response.status == 1) {
					if (response.count == 0) {
					} else if (response.count == 1) {
						modifyLBSData("Account", response.datas.get(0)._id, response.datas.get(0).phone);
					}
				}
			} catch (Exception e) {
			}
		};
	};
	RequestCallBack<String> LBSGroupDataSearch = httpClient.new ResponseHandler<String>() {
		class Response {
			public int status;
			// public String info;
			public int count;
			public ArrayList<data> datas;

			class data {
				public String _id;
				public String gid;
			}
		}

		public void onSuccess(ResponseInfo<String> responseInfo) {
			Log.e(tag, responseInfo.result);
			try {

				Response response = gson.fromJson(responseInfo.result, Response.class);
				if (response.status == 1) {
					if (response.count == 0) {
					} else if (response.count == 1) {
						modifyLBSData("Group", response.datas.get(0)._id, response.datas.get(0).gid);
					}
				}
			} catch (Exception e) {
			}
		};
	};
	RequestCallBack<String> LBSSquareDataSearch = httpClient.new ResponseHandler<String>() {
		class Response {
			public int status;
			// public String info;
			public int count;
			public ArrayList<data> datas;

			class data {
				public String _id;
				public String gid;
			}
		}

		public void onSuccess(ResponseInfo<String> responseInfo) {
			Log.e(tag, responseInfo.result);
			try {

				Response response = gson.fromJson(responseInfo.result, Response.class);
				if (response.status == 1) {
					if (response.count == 0) {
					} else if (response.count == 1) {
						modifyLBSData("Square", response.datas.get(0)._id, response.datas.get(0).gid);
					}
				}
			} catch (Exception e) {
			}
		};
	};
	static double DEF_PI = 3.14159265359; // PI
	static double DEF_2PI = 6.28318530712; // 2*PI
	static double DEF_PI180 = 0.01745329252; // PI/180.0
	static double DEF_R = 6370693.5; // radius of earth

	public double GetLongDistance(double lon1, double lat1, double lon2, double lat2) {
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

	public String pointDistance(String longitude, String latitude, String longitude2, String latitude2) {
		String distance = GetLongDistance(checkNumber(longitude), checkNumber(latitude), checkNumber(longitude2), checkNumber(latitude2)) + "";
		LatLng latLng1 = new LatLng(checkNumber(longitude), checkNumber(latitude));
		LatLng latLng2 = new LatLng(checkNumber(longitude2), checkNumber(latitude2));
		String distance0 = AMapUtils.calculateLineDistance(latLng1, latLng2) + "";
		Log.e(tag, "distance0:      " + distance0);
		Log.e(tag, "distance:      " + distance);
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
