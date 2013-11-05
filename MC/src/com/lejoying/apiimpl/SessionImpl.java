package com.lejoying.apiimpl;

import java.util.Map;

import android.app.Activity;

import com.lejoying.api.Session;
import com.lejoying.listener.ResponseListener;
import com.lejoying.mcutils.MCTools;
import com.lejoying.utils.HttpTools;

public class SessionImpl implements Session {

	private Activity activity;

	public SessionImpl(Activity activity) {
		this.activity = activity;
	}

	@Override
	public void event(Map<String, String> param,
			ResponseListener responseListener) {
		MCTools.ajax(activity, "/api2/session/event", param, false,
				HttpTools.SEND_POST, 30000, responseListener);
	}

	@Override
	public void eventweb(Map<String, String> param,
			ResponseListener responseListener) {
		MCTools.ajax(activity, "/api2/session/eventweb", param, false,
				HttpTools.SEND_POST, 30000, responseListener);
	}

}
