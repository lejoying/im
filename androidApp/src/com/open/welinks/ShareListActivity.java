package com.open.welinks;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.open.lib.MyLog;
import com.open.welinks.controller.ShareListController;
import com.open.welinks.model.Data;
import com.open.welinks.view.ShareListView;

public class ShareListActivity extends Activity {
	public Data data = Data.getInstance();
	public String tag = "ShareListActivity";
	public MyLog log = new MyLog(tag, true);

	public Context context;
	public ShareListView thisView;
	public ShareListController thisController;
	public Activity thisActivity;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		linkViewController();
	}

	public void linkViewController() {
		this.thisActivity = this;
		this.context = this;
		this.thisView = new ShareListView(thisActivity);
		this.thisController = new ShareListController(thisActivity);
		this.thisView.thisController = this.thisController;
		this.thisController.thisView = this.thisView;

		thisView.initView();
		thisController.onCrate();
		thisController.initializeListeners();
		thisController.bindEvent();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		thisController.onActivityResult(requestCode, resultCode, data);
	}
}
