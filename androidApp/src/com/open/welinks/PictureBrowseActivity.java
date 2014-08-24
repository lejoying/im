package com.open.welinks;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import com.open.welinks.controller.PictureBrowseController;
import com.open.welinks.model.Data;
import com.open.welinks.view.PictureBrowseView;

public class PictureBrowseActivity extends Activity {

	public Data data = Data.getInstance();
	public String tag = "PictureBrowseActivity";

	public Context context;
	public PictureBrowseView thisView;
	public PictureBrowseController thisController;
	public Activity thisActivity;

	public Bundle savedInstanceState;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.savedInstanceState = savedInstanceState;
		linkViewController();
	}

	public void linkViewController() {
		this.thisActivity = this;
		this.context = this;
		this.thisView = new PictureBrowseView(thisActivity);
		this.thisController = new PictureBrowseController(thisActivity);
		this.thisView.thisController = this.thisController;
		this.thisController.thisView = this.thisView;

		thisController.onCreate(savedInstanceState);
		thisView.initView();
		// thisController.initializeListeners();
		// thisController.bindEvent();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		thisController.onSaveInstanceState(outState);
		super.onSaveInstanceState(outState);
	}
}
