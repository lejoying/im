package com.open.welinks;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;

import com.open.welinks.controller.NearbyController;
import com.open.welinks.view.NearbyView;

public class NearbyActivity extends Activity {

	public NearbyView thisView;
	public NearbyController thisController;
	public NearbyActivity thisActivity;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		thisActivity = this;
		thisView = new NearbyView(thisActivity);
		thisController = new NearbyController(thisActivity);

		thisView.thisController = thisController;
		thisController.thisView = thisView;

		thisController.onCreate();
		thisView.initView();
		thisView.mapView.onCreate(savedInstanceState);
		thisController.initData();
		thisView.fillData();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		thisController.onActivityResult(requestCode, resultCode, data);
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	protected void onResume() {
		thisView.onResume();
		super.onResume();
	}

	@Override
	protected void onPause() {
		thisView.onPause();
		super.onPause();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		thisView.onSaveInstanceState(outState);
		super.onSaveInstanceState(outState);
	}

	@Override
	protected void onDestroy() {
		thisView.onDestroy();
		super.onDestroy();
	}
}
