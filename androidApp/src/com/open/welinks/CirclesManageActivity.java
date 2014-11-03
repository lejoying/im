package com.open.welinks;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import com.open.lib.MyLog;
import com.open.welinks.controller.CirclesManageController;
import com.open.welinks.model.Data;
import com.open.welinks.view.CirclesManageView;

public class CirclesManageActivity extends Activity {

	public Data data = Data.getInstance();

	public String tag = "CirclesManageActivity";
	public MyLog log = new MyLog(tag, true);

	public Context context;
	public CirclesManageView thisView;
	public CirclesManageController thisController;
	public Activity thisActivity;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		linkViewController();

	}

	public void linkViewController() {
		this.thisActivity = this;
		this.context = this;
		this.thisView = new CirclesManageView(thisActivity);
		this.thisController = new CirclesManageController(thisActivity);
		this.thisView.thisController = this.thisController;
		this.thisController.thisView = this.thisView;

		thisView.initView();
		// thisController.onCreate();
		// thisController.initializeListeners();
		// thisController.bindEvent();
	}
}
