package com.open.welinks;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;

import com.open.lib.MyLog;
import com.open.welinks.controller.ShareSectionController;
import com.open.welinks.model.Data;
import com.open.welinks.model.Parser;
import com.open.welinks.view.ShareSectionView;

public class ShareSectionActivity extends Activity {

	public Data data = Data.getInstance();
	public Parser parser = Parser.getInstance();
	public String tag = "ShareSectionActivity";
	public MyLog log = new MyLog(tag, true);

	public Context context;
	public ShareSectionView thisView;
	public ShareSectionController thisController;
	public Activity thisActivity;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		linkViewController();
	}

	public void linkViewController() {
		this.thisActivity = this;
		this.context = this;
		this.thisView = new ShareSectionView(thisActivity);
		this.thisController = new ShareSectionController(thisActivity);
		this.thisView.thisController = this.thisController;
		this.thisController.thisView = this.thisView;

		thisController.initializeListeners();
		thisView.initView();
		thisController.onCrate();
		thisController.bindEvent();
	}

	@Override
	protected void onResume() {
		super.onResume();
		thisController.onResume();
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		super.onTouchEvent(event);
		return thisController.onTouchEvent(event);
	}

	@Override
	public void finish() {
		super.finish();
		thisController.finish();
	}

	@Override
	public void onBackPressed() {
		thisController.onBackPressed();
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
		super.onActivityResult(requestCode, resultCode, intent);
		thisController.onActivityResult(requestCode, resultCode, intent);
	}
}
