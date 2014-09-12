package com.lejoying.wxgs.activity;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.amap.api.location.AMapLocation;
import com.amap.api.maps.MapView;
import com.lejoying.wxgs.R;
import com.lejoying.wxgs.activity.utils.CommonNetConnection;
import com.lejoying.wxgs.activity.view.widget.Alert;
import com.lejoying.wxgs.activity.view.widget.Alert.AlertInputDialog;
import com.lejoying.wxgs.activity.view.widget.Alert.AlertInputDialog.OnDialogClickListener;
import com.lejoying.wxgs.app.MainApplication;
import com.lejoying.wxgs.app.data.API;
import com.lejoying.wxgs.app.handler.AmapLocationHandler.CreateLocationListener;
import com.lejoying.wxgs.app.handler.AmapLocationHandler.LocationListener;
import com.lejoying.wxgs.app.handler.NetworkHandler.Settings;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RelativeLayout;
import android.app.Activity;

public class CreateSquareActivity extends Activity implements OnClickListener {
	MainApplication app = MainApplication.getMainApplication();;
	private MapView mapView;
	RelativeLayout backView, confirm, cancel;

	String squareName, squareDescription;
	public double squareLongitude = 0, squareLatitude = 0;

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
		app.amapLocationHandler.requestMapLocation(mapView.getMap(),
				new LocationListener() {

					@Override
					public void onLocationChangedListener(
							AMapLocation aMapLocation) {
						// TODO Auto-generated method stub

					}
				});
		app.amapLocationHandler.createLocation(mapView.getMap(),
				new CreateLocationListener() {
					@Override
					public void onCreateLocationChangedListener(
							double longitude, double latitude) {
						squareLongitude = longitude;
						squareLatitude = latitude;

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
			if (squareLatitude != 0 && squareLongitude != 0) {
				showDialog();
			} else {
				Alert.showMessage("您还没有选择位置");
			}
		} else if (id == R.id.cancel) {
			mFinish();
		}

	}

	private void showDialog() {
		Alert.createInputDialog(this).setInputHint("请输入广场名")
				.setOnConfirmClickListener(new OnDialogClickListener() {
					@Override
					public void onClick(AlertInputDialog dialog) {
						squareName = dialog.getInputText().toString();
						dialog = new AlertInputDialog(CreateSquareActivity.this);
						dialog.setInputText("")
								.setInputHint("请输入广场描述")
								.setOnConfirmClickListener(
										new OnDialogClickListener() {

											@Override
											public void onClick(
													AlertInputDialog dialog) {
												squareDescription = dialog
														.getInputText()
														.toString();
												createSquare();
											}

										}).show();
					}
				}).show();
	}

	private void createSquare() {
		app.networkHandler.connection(new CommonNetConnection() {
			@Override
			public void success(JSONObject jData) {
				mFinish();

			}

			@Override
			protected void settings(Settings settings) {
				settings.url = API.DOMAIN + API.GROUP_CREATE;
				Map<String, String> params = new HashMap<String, String>();
				params.put("phone", app.data.user.phone);
				params.put("accessKey", app.data.user.accessKey);
				params.put("type", "createGroup");
				params.put("name", squareName);
				JSONObject location = new JSONObject();
				try {
					location.put("longitude", String.valueOf(squareLongitude));
					location.put("latitude", String.valueOf(squareLatitude));
				} catch (JSONException e) {
					e.printStackTrace();
				}
				JSONArray members = new JSONArray();
				params.put("location", location.toString());
				params.put("gtype", "community");
				params.put("members", members.toString());
				params.put("description", squareDescription);
				params.put("address", "");
				settings.params = params;
			}

		});

	}

	private void mFinish() {
		finish();
	}
}
