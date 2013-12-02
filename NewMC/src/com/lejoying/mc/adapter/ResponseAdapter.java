package com.lejoying.mc.adapter;

import org.json.JSONObject;

import com.lejoying.mc.listener.ResponseListener;

public abstract class ResponseAdapter implements ResponseListener {

	@Override
	public void noInternet() {
		// TODO Auto-generated method stub

	}

	@Override
	public abstract void success(JSONObject data);

	@Override
	public void unsuccess(JSONObject data) {
		// TODO Auto-generated method stub

	}

	@Override
	public void failed() {
		// TODO Auto-generated method stub

	}

}
