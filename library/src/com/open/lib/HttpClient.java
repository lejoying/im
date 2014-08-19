package com.open.lib;

import android.util.Log;

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;

public class HttpClient {
	public String tag = "TestHttp";

	public static HttpClient httpClient;

	public static HttpClient getInstance() {
		if (httpClient == null) {
			httpClient = new HttpClient();
		}
		return httpClient;
	}

	public class TimeLine {

		public long start = 0; // 0
		public long startConnect = 0; // 1

		public long startSend = 0; // 2
		public long sent = 0; // 3

		public long startReceive = 0; // 4
		public long received = 0; // 5
	}

	public class ResponseHandler<T> extends RequestCallBack<T> {

		TimeLine timeLine = new TimeLine();

		@Override
		public void onStart() {
			timeLine.start = System.currentTimeMillis();
			timeLine.received = System.currentTimeMillis();
			Log.d(tag, "timeline: " + (timeLine.received - timeLine.start)
					+ "ms  onStart");
		}

		@Override
		public void onLoading(long total, long current, boolean isUploading) {
			timeLine.received = System.currentTimeMillis();
			if (isUploading) {
				Log.d(tag, "timeline: " + (timeLine.received - timeLine.start)
						+ "ms  onLoading upload: " + current + "/" + total);
			} else {
				Log.d(tag, "timeline: " + (timeLine.received - timeLine.start)
						+ "ms  onLoading reply: " + current + "/" + total);
			}
		}

		@Override
		public void onSuccess(ResponseInfo<T> responseInfo) {
			timeLine.received = System.currentTimeMillis();
			Log.d(tag, "timeline: " + (timeLine.received - timeLine.start)
					+ "ms   onSuccess: " + responseInfo.result);
		}

		@Override
		public void onFailure(HttpException error, String msg) {
			Log.d(tag, "onFailure: " + msg);
		}
	};

	public void test1() {

		RequestParams params = new RequestParams();
		// params.addBodyParameter("target", "[\"15120088197\"]");
		params.addBodyParameter("accessKey", "lejoying");
		params.addBodyParameter("phone", "151");

		// params.addBodyParameter("msg", "测试");
		HttpUtils http = new HttpUtils();
		String url1 = "http://192.168.1.92/api2/account/get?p=13";

		String url2 = "http://192.168.1.92/api2/relation/intimatefriends";

		final TimeLine timeLine = new TimeLine();
		ResponseHandler<String> requestCallBack = new ResponseHandler<String>() {

			@Override
			public void onLoading(long total, long current, boolean isUploading) {
				timeLine.received = System.currentTimeMillis();
				if (isUploading) {
					Log.d(tag, "timeline: "
							+ (timeLine.received - timeLine.start)
							+ "ms  upload: " + current + "/" + total);
				} else {
					Log.d(tag, "timeline: "
							+ (timeLine.received - timeLine.start)
							+ "ms  reply: " + current + "/" + total);
				}
			}

			@Override
			public void onSuccess(ResponseInfo<String> responseInfo) {
				timeLine.received = System.currentTimeMillis();
				Log.d(tag, "timeline: " + (timeLine.received - timeLine.start)
						+ "ms   reply: " + responseInfo.result);
				super.onSuccess(responseInfo);
			}

		};

		timeLine.start = System.currentTimeMillis();
		http.send(HttpRequest.HttpMethod.POST, url2, params, requestCallBack);

	}
}
