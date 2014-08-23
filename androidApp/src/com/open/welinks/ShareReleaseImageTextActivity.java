package com.open.welinks;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;

import com.open.welinks.controller.ShareReleaseImageTextController;
import com.open.welinks.model.Data;
import com.open.welinks.view.ShareReleaseImageTextView;

public class ShareReleaseImageTextActivity extends Activity {

	public Data data = Data.getInstance();
	public String tag = "ShareReleaseImageTextActivity";

	public Context context;
	public ShareReleaseImageTextView thisView;
	public ShareReleaseImageTextController thisController;
	public Activity thisActivity;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		linkViewController();
	}

	public void linkViewController() {
		this.thisActivity = this;
		this.context = this;
		this.thisView = new ShareReleaseImageTextView(thisActivity);
		this.thisController = new ShareReleaseImageTextController(thisActivity);
		this.thisView.thisController = this.thisController;
		this.thisController.thisView = this.thisView;

		thisView.initView();
		thisController.initializeListeners();
		thisController.bindEvent();
	}
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		thisController.onTouchEvent(event);
		return super.onTouchEvent(event);
	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		thisController.onActivityResult(requestCode, resultCode, data);
	}
	@Override
	public void finish() {
		thisController.finish();
		super.finish();
	}
}
