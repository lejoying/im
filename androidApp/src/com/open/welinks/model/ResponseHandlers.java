package com.open.welinks.model;

import android.util.Log;

import com.google.gson.Gson;
import com.lidroid.xutils.http.ResponseInfo;
import com.open.lib.HttpClient;
import com.open.lib.HttpClient.ResponseHandler;
import com.open.welinks.model.Data.Relationship;
import com.open.welinks.view.ViewManager;

public class ResponseHandlers {

	public Data data = Data.getInstance();

	public String tag = "ResponseHandlers";

	public ViewManager viewManager = ViewManager.getIntance();

	public static ResponseHandlers responseHandlers;

	public static ResponseHandlers getInstance() {
		if (responseHandlers == null) {
			responseHandlers = new ResponseHandlers();
		}
		return responseHandlers;
	}

	public HttpClient httpClient = HttpClient.getInstance();

	public ResponseHandler auth = httpClient.new ResponseHandler() {

		@Override
		public void onSuccess(ResponseInfo<String> responseInfo) {

		}
	};

	public ResponseHandler register = httpClient.new ResponseHandler() {

		@Override
		public void onSuccess(ResponseInfo<String> responseInfo) {

		}
	};

	Gson gson = new Gson();

	public ResponseHandler getIntimateFriends = httpClient.new ResponseHandler() {
		class Response {
			public String 提示信息;
			public String 失败原因;
			public Relationship relationship;

		}

		@Override
		public void onSuccess(ResponseInfo<String> responseInfo) {
			Response response = gson.fromJson(responseInfo.result,
					Response.class);
			if (response.提示信息.equals("获取密友圈成功")) {
				data.relationship.circles = response.relationship.circles;
				data.relationship.circlesMap = response.relationship.circlesMap;
				data.relationship.friendsMap
						.putAll(response.relationship.friendsMap);
			}
			int i = 1;
			i = i + 2;
			if (data.localStatus.debugMode.equals("NONE")) {
				viewManager.postNotifyView("UserIntimateView");
			}
		}
	};

	public ResponseHandler upload = httpClient.new ResponseHandler() {

		@Override
		public void onStart() {
			System.out.println("start upload");
		}

		@Override
		public void onLoading(long total, long current, boolean isUploading) {
			System.out.println("loading" + total + "--" + current + "--"
					+ isUploading);
		}

		@Override
		public void onSuccess(ResponseInfo<String> responseInfo) {
			System.out.println(responseInfo.result + "---"
					+ responseInfo.statusCode);
		}
	};
}
