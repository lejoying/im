package com.open.welinks.controller;

import android.app.Activity;
import android.content.Context;

import com.open.welinks.view.DownloadOssFileView;

public class DownloadOssFileController {

	public String tag = "DownloadOssFileController";
	public Context context;
	public DownloadOssFileView thisView;
	public DownloadOssFileController thisController;
	public Activity thisActivity;

	public DownloadOssFileController(Activity thisActivity) {
		context = thisActivity;
		this.thisActivity = thisActivity;
	}
}
