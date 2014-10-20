package com.open.welinks;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.open.welinks.controller.GroupInfoController;
import com.open.welinks.model.Data;
import com.open.welinks.view.GroupInfoView;

public class GroupInfoActivity extends Activity {

	public Data data = Data.getInstance();
	public String tag = "GroupInfoActivity";

	public Context context;
	public GroupInfoView thisView;
	public GroupInfoController thisController;
	public Activity thisActivity;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		linkViewController();
	}

	public void linkViewController() {
		this.thisActivity = this;
		this.context = this;
		this.thisView = new GroupInfoView(thisActivity);
		this.thisController = new GroupInfoController(thisActivity);
		this.thisView.thisController = this.thisController;
		this.thisController.thisView = this.thisView;

		thisView.initView();
		thisController.onCreate();
		thisController.initializeListeners();
		thisController.bindEvent();
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		// this.thisController.onBackPressed();
	}

	@Override
	protected void onResume() {
		// this.thisController.onResume();
		super.onResume();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		this.thisController.onActivityResult(requestCode, resultCode, data);
		super.onActivityResult(requestCode, resultCode, data);
	}
}
