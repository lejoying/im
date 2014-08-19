package com.open.welinks;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.open.welinks.controller.DownloadOssFileController;
import com.open.welinks.model.Data;
import com.open.welinks.view.DownloadOssFileView;
import com.open.welinks.view.ViewManage;

public class DownloadOssFileActivity extends Activity {
	public Data data = Data.getInstance();
	public String tag = "DownloadOssFileActivity";

	public Context context;
	public DownloadOssFileView thisView;
	public DownloadOssFileController thisController;
	public Activity thisActivity;

	public ViewManage viewManager = ViewManage.getInstance();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		linkViewController();
	}

	void linkViewController() {
		this.thisActivity = this;
		this.context = this;
		this.thisView = new DownloadOssFileView(thisActivity);
		this.thisController = new DownloadOssFileController(thisActivity);
		this.thisView.thisController = this.thisController;
		this.thisController.thisView = this.thisView;

		viewManager.downloadOssFileView = this.thisView;

		thisController.onCreate();
		thisController.initializeListeners();
		thisView.initView();
		thisController.bindEvent();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu_debug_2, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		return thisController.onOptionsItemSelected(item);
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		return thisController.onKeyDown(keyCode, event);
	}

}
