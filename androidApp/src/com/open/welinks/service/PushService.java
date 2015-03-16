package com.open.welinks.service;

import java.util.Random;

import android.app.Service;
import android.content.Intent;
import android.net.ConnectivityManager;
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
import com.open.lib.HttpClient.ResponseHandler;
import com.open.lib.MyLog;
import com.open.welinks.LoginActivity;
import com.open.welinks.MainActivity;
import com.open.welinks.model.API;
import com.open.welinks.model.Data;
import com.open.welinks.model.Parser;
import com.open.welinks.model.ResponseEventHandler.ResponseInfoHandler;
import com.open.welinks.utils.MyGson;

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

	public static PushService instance;

	public static boolean isRunning = false;

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
		instance = this;
		super.onCreate();
		log.e("service onCreate");
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
		log.e("service onStartCommand");
		return super.onStartCommand(intent, flags, startId);
	}

	public void startLongPull() {
		if (!isRunning) {
			connect();
			isRunning = true;
		}
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
		startLongPull();
	}

	public void stopLongPull() {
		Intent intent = new Intent(PushService.this, LoginActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(intent);
	}

	HttpHandler<String> httpHandler;

	public void connect() {
		if (operation) {
			HttpUtils httpUtils = new HttpUtils();
			httpHandler = httpUtils.send(HttpMethod.GET, API.SESSION_EVENT + "/?i=" + i, params, longPull);
			Log.e(tag, "Long Service Count：" + i);
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
			if (httpHandler != null) {
				httpHandler.cancel(true);
			}
			MyGson myGson = new MyGson();
			Response response = myGson.fromJson(responseInfo.result, Response.class);
			// Response response = gson.fromJson(responseInfo.result, Response.class);
			if (response == null) {
				connect();
				return;
			}
			if (response.失败原因 != null) {
				if ("请求失败".equals(response.提示信息)) {
					// stopLongPull();
					// parser.check();
					// data.userInformation.currentUser.accessKey = "";
					// data.userInformation.isModified = true;
					log.e(data.userInformation.currentUser.accessKey + ":::::" + data.userInformation.currentUser.phone);
					log.e("accessKey 无效,自动退出...");
					if (MainActivity.instance != null) {
						MainActivity.instance.thisController.exitApplication();
					}
				} else {
				}
			} else {
				connect();
				mResponseInfoHandler.exclude(responseInfo);
			}
		}

		@Override
		public void onFailure(HttpException error, String msg) {
			log.e("fail---------------------");
			checkConnect();
			// connect();
		};
	};

	public void checkConnect() {
		ConnectivityManager conManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
		if (conManager.getActiveNetworkInfo() != null) {
			if (conManager.getActiveNetworkInfo().isAvailable()) {
				connect();
			} else {
				// connect();
				isRunning = false;
			}
		} else {
			isRunning = false;
		}
	}
}
