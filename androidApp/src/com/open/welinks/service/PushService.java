package com.open.welinks.service;

import java.util.Random;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.HttpHandler;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.open.lib.HttpClient;
import com.open.lib.HttpClient.ResponseHandler;
import com.open.welinks.model.API;
import com.open.welinks.model.ResponseEventHandlers.ResponseInfoHandler;

public class PushService extends Service {

	public String tag = "PushService";

	public boolean operation;

	public HttpClient httpClient = HttpClient.getInstance();

	public RequestParams params;
	public HttpUtils httpUtils;

	public ResponseInfoHandler mResponseInfoHandler;

	public Random random;
	public int i;

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onCreate() {
		params = new RequestParams();
		httpUtils = new HttpUtils();
		mResponseInfoHandler = new ResponseInfoHandler(10);
		random = new Random();
		super.onCreate();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		operation = true;
		i = Math.abs(random.nextInt()) % 1000;
		String phone = intent.getStringExtra("phone");
		String accessKey = intent.getStringExtra("accessKey");
		startIMLongPull(phone, accessKey);
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public void onDestroy() {
		operation = false;
		// TODO Auto-generated method stub
		super.onDestroy();
	}

	public void startIMLongPull(String phone, String accessKey) {
		params.addQueryStringParameter("phone", phone);
		params.addQueryStringParameter("accessKey", accessKey);
		connect();
	}

	HttpHandler<String> httpHandler;

	public void connect() {
		if (operation) {
			HttpUtils httpUtils = new HttpUtils();
			httpHandler = httpUtils.send(HttpMethod.GET, API.SESSION_EVENT + "/?i=" + i, params, longPull);
			Log.e(tag, i + "-----------");
		}
		i++;
	}

	public ResponseHandler<String> longPull = httpClient.new ResponseHandler<String>() {

		@Override
		public void onSuccess(ResponseInfo<String> responseInfo) {
			System.out.println("success-----------------");
			if (httpHandler != null) {
				httpHandler.cancel(true);
			}
			connect();
			mResponseInfoHandler.exclude(responseInfo);
		}

		@Override
		public void onFailure(HttpException error, String msg) {
			System.out.println("fail---------------------");
			connect();
		};
	};
}
