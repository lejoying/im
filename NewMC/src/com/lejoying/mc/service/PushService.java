package com.lejoying.mc.service;

import java.net.HttpURLConnection;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;

import com.lejoying.mc.R;
import com.lejoying.mc.data.App;
import com.lejoying.mc.network.API;
import com.lejoying.mc.utils.MCNetTools;
import com.lejoying.mc.utils.MCNetTools.AjaxInterface;
import com.lejoying.mc.utils.MCNetTools.Settings;
import com.lejoying.utils.HttpTools;

public class PushService extends Service {
	App app = App.getInstance();
	HttpURLConnection longAjaxConnection;
	public boolean isStart;

	long failTime;
	int failCount;

	@Override
	public void onCreate() {
		super.onCreate();
	}

	public void startLongAjax(final Bundle params) {
		isStart = true;

		MCNetTools.ajaxAPI(new AjaxInterface() {
			public void setParams(Settings settings) {
				settings.url = API.SESSION_EVENT;
				settings.params = params;
				settings.timeout = 30000;
			}

			public void onSuccess(JSONObject jData) {
				if (isStart) {
					startLongAjax(params);
				}
				try {
					jData.getString(getString(R.string.app_reason));
					// TODO how to resolve when server returns fail message?
				} catch (JSONException e) {
				}
				System.out.println(jData);

				app.eventHandler.handleEvent(jData);
			}

			@Override
			public void failed() {
				if (isStart) {
					startLongAjax(params);
					System.out.println("long ajax failed");
				}
			}
		});

		// MCNetTools.ajax(this, API.SESSION_EVENT, params, HttpTools.SEND_POST,
		// 30000, new ResponseListener() {
		//
		// @Override
		// public void success(JSONObject data) {
		//
		//
		// }
		//
		// @Override
		// public void noInternet() {
		// System.out.println("noInternet");
		// }
		//
		// @Override
		// public void failed() {
		// // if (isStart) {
		// // startLongAjax(params);
		// // } else {
		// // if (MainActivity.instance != null) {
		// // Intent intent = new Intent(
		// // MainActivity.instance,
		// // LoginActivity.class);
		// // MainActivity.instance.startActivity(intent);
		// // MainActivity.instance.finish();
		// // MainActivity.instance = null;
		// // }
		// // }
		// long nowTime = new Date().getTime();
		// if (nowTime - failTime < 5000) {
		// failCount++;
		// }
		// failTime = nowTime;
		// if (failCount > 5) {
		// isStart = false;
		// failCount = 0;
		// }
		// System.out.println("failed");
		// }
		//
		// @Override
		// public void connectionCreated(
		// HttpURLConnection httpURLConnection) {
		// System.out.println("create long ajax");
		// longAjaxConnection = httpURLConnection;
		// }
		// });
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		if (intent == null) {
			// todo to be discussed
			return super.onStartCommand(intent, flags, startId);
		}
		String objective = intent.getStringExtra("objective");
		if (objective.equals("start")) {
			Bundle params = new Bundle();
			params.putString("phone", app.data.user.phone);
			params.putString("accessKey", app.data.user.accessKey);
			if (!isStart) {
				startLongAjax(params);
			}
		} else if (objective.equals("stop")) {
			isStart = false;
			longAjaxConnection.disconnect();
		}
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

}
