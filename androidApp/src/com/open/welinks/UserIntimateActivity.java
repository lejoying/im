package com.open.welinks;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.MotionEvent;

import com.open.welinks.controller.UserIntimateController;
import com.open.welinks.model.Data;
import com.open.welinks.view.UserIntimateView;

public class UserIntimateActivity extends Activity {

	public Data data = Data.getInstance();
	public String tag = "UserIntimateActivity";

	public Context context;
	public UserIntimateView thisView;
	public UserIntimateController thisController;
	public Activity thisActivity;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		linkViewController();
	}

	void linkViewController() {
		this.thisActivity = this;
		this.context = this;
		this.thisView = new UserIntimateView(thisActivity);
		this.thisController = new UserIntimateController(thisActivity);
		this.thisView.thisController = this.thisController;
		this.thisController.thisView = this.thisView;

		thisView.initViews();
		thisView.initData();
		thisController.initializeListeners();
		thisController.bindEvent();
		thisController.oncreate();
		thisController.getUserInfomationData();
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		super.onTouchEvent(event);
		return thisController.onTouchEvent(event);
	}
}
