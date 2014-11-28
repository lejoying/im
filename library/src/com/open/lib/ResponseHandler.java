package com.open.lib;

import android.util.Log;

import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;

public class ResponseHandler<T> extends RequestCallBack<T> {
	public String tag = "ResponseHandler";
	public class TimeLine {

		public long start = 0; // 0
		public long startConnect = 0; // 1

		public long startSend = 0; // 2
		public long sent = 0; // 3

		public long startReceive = 0; // 4
		public long received = 0; // 5
	}
	TimeLine timeLine = new TimeLine();

	@Override
	public void onStart() {
		timeLine.start = System.currentTimeMillis();
		timeLine.received = System.currentTimeMillis();
		Log.d(tag, "timeline: " + (timeLine.received - timeLine.start) + "ms  onStart");
	}

	@Override
	public void onLoading(long total, long current, boolean isUploading) {
		timeLine.received = System.currentTimeMillis();
		if (isUploading) {
			Log.d(tag, "timeline: " + (timeLine.received - timeLine.start) + "ms  onLoading upload: " + current + "/" + total);
		} else {
			Log.d(tag, "timeline: " + (timeLine.received - timeLine.start) + "ms  onLoading reply: " + current + "/" + total);
		}
	}

	@Override
	public void onSuccess(ResponseInfo<T> responseInfo) {
		timeLine.received = System.currentTimeMillis();
		Log.d(tag, "timeline: " + (timeLine.received - timeLine.start) + "ms   onSuccess: " + responseInfo.result);
	}
	
	public void onSuccess(String results) {
		timeLine.received = System.currentTimeMillis();
		Log.d(tag, "timeline: " + (timeLine.received - timeLine.start) + "ms   onSuccess: " + results);
	}

	@Override
	public void onFailure(HttpException error, String msg) {
		Log.d(tag, "onFailure: " + msg);
	}
};

