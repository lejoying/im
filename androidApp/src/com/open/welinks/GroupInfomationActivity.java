package com.open.welinks;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import com.open.welinks.controller.GroupInfomationController;
import com.open.welinks.model.Data;
import com.open.welinks.view.GroupInfomationView;

public class GroupInfomationActivity extends Activity {

	public Data data = Data.getInstance();
	public String tag = "GroupInfomationActivity";

	public Context context;
	public GroupInfomationView thisView;
	public GroupInfomationController thisController;
	public Activity thisActivity;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		linkViewController();
	}

	public void linkViewController() {
		this.thisActivity = this;
		this.context = this;
		this.thisView = new GroupInfomationView(thisActivity);
		this.thisController = new GroupInfomationController(thisActivity);
		this.thisView.thisController = this.thisController;
		this.thisController.thisView = this.thisView;

		thisView.initView();
		 thisController.onCreate();
		thisController.initializeListeners();
		thisController.bindEvent();
	}
}
