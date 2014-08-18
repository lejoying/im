package com.open.welinks.view;

import android.app.Activity;
import android.content.Context;

import com.open.welinks.R;
import com.open.welinks.controller.DownloadOssFileController;

public class DownloadOssFileView {
	public String tag = "DownloadOssFileView";
	public Context context;
	public DownloadOssFileView thisView;
	public DownloadOssFileController thisController;
	public Activity thisActivity;

	public DownloadOssFileView(Activity thisActivity) {
		context = thisActivity;
		this.thisActivity = thisActivity;
	}

	public void initView() {
		thisActivity.setContentView(R.layout.activity_downloadossfiles);
	}
}
