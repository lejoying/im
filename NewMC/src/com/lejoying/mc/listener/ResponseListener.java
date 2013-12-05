package com.lejoying.mc.listener;

import java.net.HttpURLConnection;

import org.json.JSONObject;

public interface ResponseListener {
	public void connectionCreated(HttpURLConnection httpURLConnection);

	public void noInternet();

	public void success(JSONObject data);

	public void unsuccess(JSONObject data);

	public void failed();
}
