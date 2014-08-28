package com.open.welinks.controller;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.open.lib.HttpClient;
import com.open.lib.HttpClient.ResponseHandler;

public class TestHttpLongPull extends Activity {

	public String tag = "TestHttpLongPull";

	public static HttpClient httpClient = HttpClient.getInstance();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		test();

	}

	RequestParams params = new RequestParams();
	HttpUtils httpUtils = new HttpUtils();
	String url = "http://www.we-links.com/api2/session/event";

	public void test() {
		params.addQueryStringParameter("phone", "151");
		params.addQueryStringParameter("accessKey", "lejoying");

		// params.addHeader("Timeout", 30000 + "");

		httpUtils.configRequestRetryCount(5);
		httpUtils.send(HttpMethod.GET, url, params, test);
	}

	public ResponseHandler<String> test = httpClient.new ResponseHandler<String>() {

		@Override
		public void onSuccess(ResponseInfo<String> responseInfo) {
			Log.e(tag, responseInfo.statusCode + "------- success");
			httpUtils.send(HttpMethod.GET, url, params, test);
			try {
				Thread.currentThread().sleep(50000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		};

		@Override
		public void onFailure(com.lidroid.xutils.exception.HttpException error, String msg) {
			Log.e(tag, error + "------- onFailure");
			httpUtils.send(HttpMethod.GET, url, params, test);
			try {
				Thread.currentThread().sleep(70000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Log.e(tag, error + "------- onFailure2");
		};
	};
}
