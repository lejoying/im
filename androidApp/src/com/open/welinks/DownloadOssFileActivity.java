package com.open.welinks;

import java.io.File;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.open.lib.HttpClient;
import com.open.welinks.controller.DownloadOssFileController;
import com.open.welinks.view.DownloadOssFileView;
import com.open.welinks.view.ViewManage;

public class DownloadOssFileActivity extends Activity {

	public String tag = "DownloadOssFileActivity";

	public Context context;
	public DownloadOssFileView thisView;
	public DownloadOssFileController thisController;
	public Activity thisActivity;

	public ViewManage viewManage = ViewManage.getInstance();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		linkViewController();
	}

	public void linkViewController() {
		this.thisActivity = this;
		this.context = this;
		this.thisView = new DownloadOssFileView(thisActivity);
		this.thisController = new DownloadOssFileController(thisActivity);
		this.thisView.thisController = this.thisController;
		this.thisController.thisView = this.thisView;

		viewManage.downloadOssFileView = this.thisView;

		thisView.initView();
		test();

		// thisView.initView();
		// thisController.onCreate();
		// thisController.initializeListeners();
		// thisController.bindEvent();
	}

	public class ResponseHandler0 extends RequestCallBack<File> {

		@Override
		public void onStart() {
			Log.e(tag, "-----start-----");

		}

		@Override
		public void onLoading(long total, long current, boolean isUploading) {
			Log.e(tag, current + "-----" + total + "-----" + isUploading);
		}

		@Override
		public void onSuccess(ResponseInfo<File> responseInfo) {
			Log.e(tag, "-----success-----" + responseInfo.statusCode);
		}

		@Override
		public void onFailure(HttpException error, String msg) {
			Log.d(tag, "onFailure: -----" + msg);
		}
	};

	HttpClient httpClient = HttpClient.getInstance();
	public ResponseHandler0 download = new ResponseHandler0() {

	};

	public void test() {
//		RequestParams params = new RequestParams();
		HttpUtils httpUtils = new HttpUtils();
		String requestUri = "http://images5.we-links.com/mutilpart/20F5B7FCE06E5CC24B0B875389A55AF11023DC4D.jpg";
		httpUtils.download(requestUri, Environment
				.getExternalStorageDirectory().getAbsolutePath() + "/song.jpg",
				download);
	}
}
