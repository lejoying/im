package com.open.welinks;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.MotionEvent;

import com.open.lib.MyLog;
import com.open.welinks.controller.ShareDetailController;
import com.open.welinks.model.Data;
import com.open.welinks.view.ShareDetailView;

public class ShareDetailActivity extends Activity {

	public String tag = "ShareMessageDetailActivity";
	public MyLog log = new MyLog(tag, true);

	public Data data = Data.getInstance();

	public Context context;
	public ShareDetailView thisView;
	public ShareDetailController thisController;
	public Activity thisActivity;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		linkViewController();
	}

	public void linkViewController() {
		this.thisActivity = this;
		this.context = this;
		this.thisView = new ShareDetailView(thisActivity);
		this.thisController = new ShareDetailController(thisActivity);
		this.thisView.thisController = this.thisController;
		this.thisController.thisView = this.thisView;

		thisController.onCreate();
		thisView.initView();
		// thisController.initData();
		// thisController.initializeListeners();
		// thisView.initView();
		// thisController.bindEvent();
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		super.onTouchEvent(event);
		return thisController.onTouchEvent(event);
	}
}
