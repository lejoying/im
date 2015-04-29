package com.open.welinks;

import com.open.lib.MyLog;
import com.open.welinks.controller.BusinessCardController;
import com.open.welinks.view.BusinessCardView;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class BusinessCardActivity extends Activity {

	public String tag = "BusinessCardActivity";
	public MyLog log = new MyLog(tag, true);

	public BusinessCardController thisController;
	public BusinessCardView thisView;
	public BusinessCardActivity thisActivity;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		linkViewController();
	}

	public void linkViewController() {
		thisActivity = this;
		thisView = new BusinessCardView(this);
		thisController = new BusinessCardController(this);

		thisView.thisController = thisController;
		thisController.thisView = thisView;

		thisController.onCreate();
		thisController.initializeListeners();
		thisView.initView();
		thisController.bindEvent();
	}

	@Override
	protected void onResume() {
		thisController.onResume();
		super.onResume();
	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
		thisController.onWindowFocusChanged(hasFocus);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		thisController.onActivityResult(requestCode, resultCode, data);
	}
}
