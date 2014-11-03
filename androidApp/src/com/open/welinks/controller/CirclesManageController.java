package com.open.welinks.controller;

import com.open.lib.MyLog;
import com.open.welinks.model.Data;
import com.open.welinks.view.CirclesManageView;

import android.app.Activity;
import android.content.Context;

public class CirclesManageController {
	
	public Data data = Data.getInstance();

	public String tag = "CirclesManageActivity";
	public MyLog log = new MyLog(tag, true);

	public Context context;
	public CirclesManageView thisView;
	public CirclesManageController thisController;
	public Activity thisActivity;

	public CirclesManageController(Activity thisActivity) {
		this.context = thisActivity;
		this.thisActivity = thisActivity;
		this.thisController = this;
	}
}
