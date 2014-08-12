package com.open.welinks;

import com.open.welinks.controller.ImagesGridController;
import com.open.welinks.view.ImagesGridView;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

public class ImageGridActivity extends Activity {

	public String tag = "ImageGridActivity";

	public Context context;
	public Activity thisActivity;
	public ImagesGridController thisController;
	public ImagesGridView thisView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		linkViewController();
	}

	public void linkViewController() {
		this.thisActivity = this;
		this.context = this;
		this.thisView = new ImagesGridView(thisActivity);
		this.thisController = new ImagesGridController(thisActivity);
		this.thisView.thisController = this.thisController;
		this.thisController.thisView = this.thisView;

		thisController.oncreate();

		thisView.initViews();
		thisView.initData();
		
		thisController.initializeListeners();
		thisController.bindEvent();
	}
}
