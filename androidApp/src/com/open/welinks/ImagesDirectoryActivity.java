package com.open.welinks;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.open.welinks.controller.ImagesDirectoryController;
import com.open.welinks.view.ImagesDirectoryView;

public class ImagesDirectoryActivity extends Activity {

	public String tag = "ImagesDirectoryActivity";

	public Context context;
	public Activity thisActivity;
	public ImagesDirectoryController thisController;
	public ImagesDirectoryView thisView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		linkViewController();
	}

	public void linkViewController() {
		this.thisActivity = this;
		this.context = this;
		this.thisView = new ImagesDirectoryView(thisActivity);
		this.thisController = new ImagesDirectoryController(thisActivity);
		this.thisView.thisController = this.thisController;
		this.thisController.thisView = this.thisView;

		thisView.initViews();

		thisController.setDate();
		thisController.initializeListeners();
		thisController.bindEvent();

		// thisView.initView();
		// thisController.onCreate();
		// thisController.initializeListeners();
		// thisController.bindEvent();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		thisController.onActivityResult(requestCode, resultCode, data);
	}
}
