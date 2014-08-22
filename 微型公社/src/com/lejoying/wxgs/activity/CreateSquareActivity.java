package com.lejoying.wxgs.activity;

import com.amap.api.maps.MapView;
import com.lejoying.wxgs.R;
import com.lejoying.wxgs.R.layout;
import com.lejoying.wxgs.app.MainApplication;
import com.lejoying.wxgs.app.handler.AmapLocationHandler.CreateLocationListener;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RelativeLayout;
import android.app.Activity;

public class CreateSquareActivity extends Activity implements OnClickListener {
	MainApplication app;
	private MapView mapView;
	RelativeLayout backView, confirm, cancel;

	@Override
	protected void onResume() {
		mapView.onResume();
		super.onResume();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_create_square);
		initLayout();
		initData();
		initEvent();
		mapView.onCreate(savedInstanceState);
	}

	private void initData() {
		app.amapLocationHandler.createLocation(mapView.getMap(),
				new CreateLocationListener() {
					@Override
					public void onCreateLocationChangedListener(
							double longitude, double latitude) {
						// TODO Auto-generated method stub

					}
				});

	}

	private void initEvent() {
		backView.setOnClickListener(this);
		confirm.setOnClickListener(this);
		cancel.setOnClickListener(this);

	}

	private void initLayout() {
		mapView = (MapView) findViewById(R.id.mapView);
		backView = (RelativeLayout) findViewById(R.id.backView);
		confirm = (RelativeLayout) findViewById(R.id.confirm);
		cancel = (RelativeLayout) findViewById(R.id.cancel);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		mapView.onSaveInstanceState(outState);
		super.onSaveInstanceState(outState);
	}

	@Override
	protected void onPause() {
		mapView.onPause();
		super.onPause();
	}

	@Override
	protected void onDestroy() {
		mapView.onDestroy();
		super.onDestroy();
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		if (id == R.id.backView) {
			mFinish();
		} else if (id == R.id.confirm) {

		} else if (id == R.id.cancel) {

		}

	}

	private void mFinish() {
		finish();
	}
}
