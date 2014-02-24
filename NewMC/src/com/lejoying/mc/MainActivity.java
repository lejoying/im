package com.lejoying.mc;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.baidu.location.BDLocation;
import com.lejoying.mc.data.App;
import com.lejoying.mc.data.handler.LocationHandler.LocationListener;
import com.lejoying.mc.fragment.FriendsFragment;
import com.lejoying.mc.network.API;
import com.lejoying.mc.service.PushService;
import com.lejoying.mc.utils.AjaxAdapter;
import com.lejoying.mc.utils.MCNetUtils;
import com.lejoying.mc.utils.MCNetUtils.Settings;

public class MainActivity extends BaseFragmentActivity {

	App app = App.getInstance();

	public static MainActivity instance;

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout._main);
		Intent service = new Intent(this, PushService.class);
		service.putExtra("objective", "start");
		startService(service);
		instance = this;

		app.locationHandler.requestLocation(new LocationListener() {

			@Override
			public void onReceivePoi(BDLocation poiLocation) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onReceiveLocation(final BDLocation location) {

				location.getLatitude();

				MCNetUtils.ajax(new AjaxAdapter() {

					@Override
					public void setParams(Settings settings) {
						try {
							settings.url = API.LBS_UPDATELOCATION;
							Bundle params = new Bundle();
							params.putString("phone", app.data.user.phone);
							params.putString("accessKey",
									app.data.user.accessKey);
							JSONObject jLocation = new JSONObject();
							jLocation.put("longitude",
									String.valueOf(location.getLongitude()));
							jLocation.put("latitude",
									String.valueOf(location.getLatitude()));
							params.putString("location", jLocation.toString());

							JSONObject jUser = new JSONObject();

							jUser.put("mainBusiness",
									app.data.user.mainBusiness);
							jUser.put("head", app.data.user.head);
							jUser.put("nickName", app.data.user.nickName);

							params.putString("account", jUser.toString());
							settings.params = params;

						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}

					@Override
					public void onSuccess(JSONObject jData) {
						System.out.println(jData);
					}

				});

			}
		});
	}

	@Override
	public Fragment setFirstPreview() {
		return new FriendsFragment();
	}

	@Override
	protected int setBackground() {
		// TODO Auto-generated method stub
		return R.drawable.cloudy_d_blur;
	}

	@Override
	protected void onPause() {
		super.onPause();
		System.out.println("pause");
		app.sDcardDataResolver.saveToSDcard();
	}

	@Override
	protected void onDestroy() {
		instance = null;
		super.onDestroy();
	}

	@Override
	public void finish() {
		Intent service = new Intent(this, PushService.class);
		service.putExtra("objective", "stop");
		startService(service);
		super.finish();
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	public void onConfigurationChanged(Configuration config) {
		super.onConfigurationChanged(config);
	}
}
