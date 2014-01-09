package com.lejoying.mc.utils;

import java.net.HttpURLConnection;

import org.json.JSONObject;

import android.widget.Toast;

import com.lejoying.mc.data.App;
import com.lejoying.mc.utils.MCNetTools.AjaxInterface;
import com.lejoying.mc.utils.MCNetTools.Settings;

public abstract class AjaxAdapter implements AjaxInterface{
	App app = App.getInstance();

	public JSONObject jData;
	
	public abstract void setParams(Settings settings);

	public abstract void onSuccess(JSONObject jData);

	public void noInternet() {
		app.mUIThreadHandler.post(new Runnable() {
			public void run() {
				Toast.makeText(app.context, "没有网络连接，网络不给力呀~", Toast.LENGTH_SHORT).show();
			}
		});
	}

	public void failed() {
		app.mUIThreadHandler.post(new Runnable() {
			public void run() {
				Toast.makeText(app.context, "网络连接失败，网络不给力呀~", Toast.LENGTH_SHORT).show();
			}
		});
	}
	
	public void timeout() {
		app.mUIThreadHandler.post(new Runnable() {
			public void run() {
				Toast.makeText(app.context, "服务器长时间没有响应，网络不给力呀~", Toast.LENGTH_SHORT).show();
			}
		});
	}

	public void connectionCreated(HttpURLConnection httpURLConnection) {
	}

}