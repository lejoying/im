package com.open.welinks;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.open.welinks.controller.GroupMemberManageController;
import com.open.welinks.model.Data;
import com.open.welinks.view.GroupMemberManageView;

public class GroupMemberManageActivity extends Activity {

	public Data data = Data.getInstance();
	public String tag = "GroupMemberManageActivity";

	public Context context;
	public GroupMemberManageView thisView;
	public GroupMemberManageController thisController;
	public Activity thisActivity;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		linkViewController();
	}

	public void linkViewController() {
		this.thisActivity = this;
		this.context = this;
		this.thisView = new GroupMemberManageView(thisActivity);
		this.thisController = new GroupMemberManageController(thisActivity);
		this.thisView.thisController = this.thisController;
		this.thisController.thisView = this.thisView;

		thisView.initView();
		thisController.onCreate();
		thisController.initializeListeners();
		thisController.bindEvent();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		thisController.onActivityResult(requestCode, resultCode, data);
		super.onActivityResult(requestCode, resultCode, data);
	}
}
