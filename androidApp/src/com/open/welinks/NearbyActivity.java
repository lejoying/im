package com.open.welinks;

import com.open.welinks.controller.NearbyController;
import com.open.welinks.view.NearbyView;

import android.os.Bundle;
import android.app.Activity;

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
		thisController.onCreate();
		thisView.fillData();
	}

	@Override
	protected void onResume() {
		super.onResume();
		thisView.onResume();
	}
}
