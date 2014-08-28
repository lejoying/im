package com.open.welinks.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.open.lib.HttpClient;
import com.open.lib.HttpClient.ResponseHandler;
import com.open.welinks.model.API;
import com.open.welinks.model.ResponseEventHandlers.ResponseInfoHandler;

public class PushService extends Service {

	public String tag = "PushService";

	public HttpClient httpClient = HttpClient.getInstance();

	public RequestParams params = new RequestParams();
	public HttpUtils httpUtils = new HttpUtils();

	public ResponseInfoHandler mResponseInfoHandler = new ResponseInfoHandler(10);

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	public void startIMLongPull() {
		params.addQueryStringParameter("phone", "151");
		params.addQueryStringParameter("accessKey", "lejoying");
		connect();
	}

	public void connect() {
		httpUtils.send(HttpMethod.GET, API.SESSION_EVENT, params, longPull);
	}

	public ResponseHandler<String> longPull = httpClient.new ResponseHandler<String>() {

		@Override
		public void onSuccess(ResponseInfo<String> responseInfo) {
			connect();
			mResponseInfoHandler.exclude(responseInfo);
		}

		@Override
		public void onFailure(HttpException error, String msg) {
			connect();
		};
	};
}
