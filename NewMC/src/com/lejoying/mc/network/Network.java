package com.lejoying.mc.network;

import java.net.HttpURLConnection;

import org.json.JSONObject;

import android.content.Context;
import android.os.Bundle;

import com.lejoying.mc.utils.MCNetTools;
import com.lejoying.mc.utils.MCNetTools.ResponseListener;
import com.lejoying.utils.HttpTools;

public class Network {

	public void sendGet(Context context, String api, Bundle params, int timeout) {
		
	}

	public void sendPost(Context context, String api, Bundle params, int timeout) {
		MCNetTools.ajax(context, api, params, HttpTools.SEND_POST, timeout,
				new ResponseListener() {

					@Override
					public void success(JSONObject data) {
						// TODO Auto-generated method stub

					}

					@Override
					public void noInternet() {
						// TODO Auto-generated method stub

					}

					@Override
					public void failed() {
						// TODO Auto-generated method stub

					}

					@Override
					public void connectionCreated(
							HttpURLConnection httpURLConnection) {
						// TODO Auto-generated method stub

					}
				});
	}
}
