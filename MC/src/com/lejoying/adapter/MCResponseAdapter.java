package com.lejoying.adapter;

import org.json.JSONObject;

import android.app.Activity;

import com.lejoying.listener.ResponseListener;

public class MCResponseAdapter implements ResponseListener {

	private Activity activity;
	
	public MCResponseAdapter(Activity activity) {
		super();
		this.activity = activity;
	}

	@Override
	public void noInternet() {
		
	}

	@Override
	public void success(JSONObject data) {
		
	}

	@Override
	public void unsuccess(JSONObject data) {
		
	}

	@Override
	public void failed() {
		
	}

}
