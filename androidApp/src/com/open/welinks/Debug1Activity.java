package com.open.welinks;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.open.welinks.controller.Debug1Controller;
import com.open.welinks.model.Data;
import com.open.welinks.view.Debug1View;
import com.open.welinks.view.Debug1View.Status;
import com.open.welinks.view.ViewManage;

public class Debug1Activity extends Activity {
	public Data data = Data.getInstance();
	public String tag = "LoginActivity";

	public Context context;
	public Debug1View thisView;
	public Debug1Controller thisController;
	public Activity thisActivity;

	public ViewManage viewManager = ViewManage.getInstance();

	public boolean isInit;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		isInit = false;
		linkViewController();

		if (thisView.status == Status.welcome) {
			thisView.status = Status.welcome;
		} else {
			thisView.status = Status.start;
		}
	}

	void linkViewController() {
		this.thisActivity = this;
		this.context = this;
		this.thisView = new Debug1View(thisActivity);
		this.thisController = new Debug1Controller(thisActivity);
		this.thisView.thisController = this.thisController;
		this.thisController.thisView = this.thisView;

		viewManager.debug1View = this.thisView;

		thisController.onCreate();
		thisController.initializeListeners();
		thisView.initView();
		thisController.bindEvent();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu_debug_2, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		return thisController.onOptionsItemSelected(item);
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (isInit) {
			thisController.onCreate();
			thisController.initializeListeners();
			thisView.initView();
			thisController.bindEvent();
		}
		isInit = true;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		return thisController.onKeyDown(keyCode, event);
	}

}
