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

		thisView.initView();
		thisView.mapView.onCreate(savedInstanceState);
		thisController.onCreate();
		thisView.fillData2();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		thisController.onActivityResult(requestCode, resultCode, data);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		thisView.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
		thisView.onPause();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		thisView.onSaveInstanceState(outState);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		thisView.onDestroy();
	}
}
