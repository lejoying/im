package com.open.welinks;

import android.app.Activity;
import android.os.Bundle;

import com.open.lib.MyLog;
import com.open.welinks.controller.GroupListController;
import com.open.welinks.view.GroupListView;

public class GroupListActivity extends Activity {

	public GroupListView thisView;
	public GroupListController thisController;

	public String tag = "GroupListActivity";
	public MyLog log = new MyLog(tag, true);

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		thisView = new GroupListView(this);
		thisController = new GroupListController(this);
		thisView.thisController = thisController;
		thisController.thisView = thisView;

		thisController.onCreate();
		thisView.initViews();
		thisController.initializeListeners();
		thisController.initData();
	}

	@Override
	protected void onResume() {
		thisController.onResume();
		super.onResume();
	}

	@Override
	protected void onDestroy() {
		thisController.onDestroy();
		super.onDestroy();
	}

	@Override
	public void onBackPressed() {
		thisController.onBackPressed();
	}

	@Override
	public void finish() {
		thisController.finish();
		super.finish();
	}

}
