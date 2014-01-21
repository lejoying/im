package com.lejoying.mc.service;

import java.net.HttpURLConnection;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;

import com.lejoying.mc.LoginActivity;
import com.lejoying.mc.MainActivity;
import com.lejoying.mc.R;
import com.lejoying.mc.data.App;
import com.lejoying.mc.network.API;
import com.lejoying.mc.utils.AjaxAdapter;
import com.lejoying.mc.utils.MCNetUtils;
import com.lejoying.mc.utils.MCNetUtils.Settings;

public class PushService extends Service {
	App app = App.getInstance();
	HttpURLConnection longAjaxConnection;
	public boolean isStart;

	long failTime;
	int failCount;

	public boolean waitForInternet;

	@Override
	public void onCreate() {
		super.onCreate();
	}

	public void startLongAjax(final Bundle params) {
		isStart = true;

		MCNetUtils.ajax(new AjaxAdapter() {
			public void setParams(Settings settings) {
				settings.url = API.SESSION_EVENT;
				settings.params = params;
				settings.timeout = 30000;
			}

			public void onSuccess(JSONObject jData) {
				try {
					jData.getString(getString(R.string.app_reason));
					if (MainActivity.instance != null) {
						Intent intent = new Intent(MainActivity.instance,
								LoginActivity.class);
						MainActivity.instance.startActivity(intent);
						MainActivity.instance.finish();
					}
					return;
				} catch (JSONException e) {
				}
				if (isStart) {
					startLongAjax(params);
				}
				app.eventHandler.handleEvent(jData);
			}

			@Override
			public void noInternet() {
				waitForInternet = true;
			}

			@Override
			public void failed() {
				if (isStart) {
					if (!waitForInternet) {
						try {
							Thread.sleep(10000);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						startLongAjax(params);
					}
				}
			}

			@Override
			public void timeout() {
				if (isStart) {
					startLongAjax(params);
					System.out.println("long ajax failed");
				}
			}

			@Override
			public void connectionCreated(HttpURLConnection httpURLConnection) {
				longAjaxConnection = httpURLConnection;
				System.out.println("long ajax is created");
			}
		});
	}

	public void stopLongAjax() {
		if (isStart) {
			isStart = false;
			waitForInternet = false;
			if (longAjaxConnection != null) {
				longAjaxConnection.disconnect();
			}
		}
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		if (intent == null) {
			// todo to be discussed
			return super.onStartCommand(intent, flags, startId);
		}
		String objective = intent.getStringExtra("objective");
		if (objective == null) {
			return super.onStartCommand(intent, flags, startId);
		}
		Bundle params = new Bundle();
		params.putString("phone", app.data.user.phone);
		params.putString("accessKey", app.data.user.accessKey);
		if (objective.equals("start")) {
			if (!isStart) {
				startLongAjax(params);
			}
		} else if (objective.equals("network")) {
			if (isStart && waitForInternet) {
				waitForInternet = false;
				startLongAjax(params);
			}
		} else if (objective.equals("stop")) {
			stopLongAjax();
		}
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

}
