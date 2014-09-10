package com.open.welinks.service;

import java.util.Random;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.google.gson.Gson;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.HttpHandler;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.open.lib.HttpClient;
import com.open.lib.MyLog;
import com.open.lib.HttpClient.ResponseHandler;
import com.open.welinks.model.API;
import com.open.welinks.model.Data;
import com.open.welinks.model.Parser;
import com.open.welinks.model.ResponseEventHandlers.ResponseInfoHandler;

public class PushService extends Service {

	public Data data = Data.getInstance();
	public Parser parser = Parser.getInstance();
	public String tag = "PushService";

	public MyLog log = new MyLog(tag, true);

	public static final String LONGPULL_STOP = "com.open.welinks.service.longpull_stop";

	public boolean operation = false;

	public HttpClient httpClient = HttpClient.getInstance();

	public RequestParams params;
	public HttpUtils httpUtils;

	public ResponseInfoHandler mResponseInfoHandler;

	public Random random;
	public int i;

	public Gson gson = new Gson();

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
		if (intent != null) {
			operation = intent.getBooleanExtra("operation", false);
			if (operation) {
				i = Math.abs(random.nextInt()) % 1000;
				String phone = intent.getStringExtra("phone");
				String accessKey = intent.getStringExtra("accessKey");
				startIMLongPull(phone, accessKey);
			} else {
				stopLongPull();
			}
		}
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public void onDestroy() {
		operation = false;
		parser.save();
		log.e("push service destroy");
		super.onDestroy();
	}

	public void startIMLongPull(String phone, String accessKey) {
		params.addQueryStringParameter("phone", phone);
		params.addQueryStringParameter("accessKey", accessKey);
		connect();
	}

	public void stopLongPull() {
		// stopSelf();
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
		class Response {
			public String 提示信息;
			public String 失败原因;
		}

		@Override
		public void onSuccess(ResponseInfo<String> responseInfo) {
			System.out.println("success-----------------");
			if (httpHandler != null) {
				httpHandler.cancel(true);
			}
			try {
				Response response = gson.fromJson(responseInfo.result, Response.class);
				if ("请求失败".equals(response.提示信息.substring(response.提示信息.length() - 2))) {
					stopLongPull();
				}
			} catch (Exception e) {
				connect();
				mResponseInfoHandler.exclude(responseInfo);
			}
		}

		@Override
		public void onFailure(HttpException error, String msg) {
			System.out.println("fail---------------------");
			connect();
		};
	};
}
