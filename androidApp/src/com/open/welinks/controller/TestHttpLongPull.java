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

	public void test() {
		RequestParams params = new RequestParams();
		params.addQueryStringParameter("phone", "151");
		params.addQueryStringParameter("accessKey", "lejoying");

		HttpUtils httpUtils = new HttpUtils();
		httpUtils.configTimeout(3000);
		httpUtils.configRequestRetryCount(Integer.MAX_VALUE);
		String url = "http://www.we-links.com/api2/session/event";
		httpUtils.send(HttpMethod.POST, url, params, test);
	}

	public ResponseHandler<String> test = httpClient.new ResponseHandler<String>() {

		@Override
		public void onSuccess(ResponseInfo<String> responseInfo) {
			Log.e(tag, responseInfo.statusCode + "-------initUpload success");

		};
	};
}
