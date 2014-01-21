package com.lejoying.mc.utils;

import java.net.HttpURLConnection;

import org.json.JSONObject;

import android.os.Bundle;
import android.widget.Toast;

import com.lejoying.mc.data.App;
import com.lejoying.mc.utils.MCNetUtils.AjaxInterface;
import com.lejoying.mc.utils.MCNetUtils.Settings;

public abstract class AjaxAdapter implements AjaxInterface {
	App app = App.getInstance();

	public JSONObject jData;

	public Bundle generateParams() {
		Bundle params = new Bundle();
		params.putString("phone", app.data.user.phone);
		params.putString("accessKey", app.data.user.accessKey);
		return params;
	}

	public abstract void setParams(Settings settings);

	public abstract void onSuccess(JSONObject jData);

	public void noInternet() {
		app.mUIThreadHandler.post(new Runnable() {
			public void run() {
				Toast.makeText(app.context, "没有网络连接，网络不给力呀~",
						Toast.LENGTH_SHORT).show();
			}
		});
	}

	public void failed() {

	}

	public void timeout() {

	}

	public void connectionCreated(HttpURLConnection httpURLConnection) {
	}

}
