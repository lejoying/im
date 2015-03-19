package com.open.welinks;

import me.imid.swipebacklayout.lib.SwipeBackLayout;
import me.imid.swipebacklayout.lib.app.SwipeBackActivity;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;

import com.open.welinks.controller.ShareMessageDetailController;
import com.open.welinks.model.Data;
import com.open.welinks.utils.PreferenceUtils;
import com.open.welinks.view.ShareMessageDetailView;

public class ShareMessageDetailActivity extends SwipeBackActivity {

	public Data data = Data.getInstance();
	public String tag = "ShareMessageDetailActivity";

	public Context context;
	public ShareMessageDetailView thisView;
	public ShareMessageDetailController thisController;
	public Activity thisActivity;

	private SwipeBackLayout mSwipeBackLayout;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//
		mSwipeBackLayout = getSwipeBackLayout();
		int edgeFlag;
		edgeFlag = SwipeBackLayout.EDGE_LEFT;
		mSwipeBackLayout.setEdgeTrackingEnabled(edgeFlag);
		saveTrackingMode(edgeFlag);
		//

		linkViewController();
	}

	private void saveTrackingMode(int flag) {
		PreferenceUtils.setPrefInt(getApplicationContext(), "key_tracking_mode", flag);
	}

	public void linkViewController() {
		this.thisActivity = this;
		this.context = this;
		this.thisView = new ShareMessageDetailView(thisActivity);
		this.thisController = new ShareMessageDetailController(thisActivity);
		this.thisView.thisController = this.thisController;
		this.thisController.thisView = this.thisView;

		thisController.initData();
		thisController.initializeListeners();
		thisView.initView();
		thisController.bindEvent();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		thisController.onActivityResult(requestCode, resultCode, data);
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public void onBackPressed() {
		thisController.onBackPressed();
		super.onBackPressed();
	}
	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		Log.e(tag, "aaaaaaaaaaaaaaaaaaaa");
		return super.onKeyUp(keyCode, event);
	}

	@Override
	protected void onResume() {
		thisController.onResume();
		super.onResume();
	}

	@Override
	public void finish() {
		thisController.finish();
		super.finish();
	}
}
