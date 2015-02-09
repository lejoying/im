package com.open.welinks;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import com.open.lib.MyLog;
import com.open.welinks.controller.SharePraiseusersController;
import com.open.welinks.model.Data;
import com.open.welinks.view.SharePraiseusersView;

public class SharePraiseusersActivity extends Activity {

	public String tag = "SharePraiseusersActivity";
	public MyLog log = new MyLog(tag, true);

	public Data data = Data.getInstance();

	public Context context;
	public SharePraiseusersView thisView;
	public SharePraiseusersController thisController;
	public Activity thisActivity;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		linkViewController();
	}

	public void linkViewController() {
		this.thisActivity = this;
		this.context = this;
		this.thisView = new SharePraiseusersView(thisActivity);
		this.thisController = new SharePraiseusersController(thisActivity);
		this.thisView.thisController = this.thisController;
		this.thisController.thisView = this.thisView;

		thisController.initializeListeners();
		thisController.onCreate();
		thisView.initView();
		thisController.bindEvent();
	}

	@Override
	protected void onResume() {
		if (thisController != null) {
			thisController.onResume();
		}
		super.onResume();
	}
}
