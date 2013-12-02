package com.lejoying.mc.apiimpl;

import android.content.Context;
import android.os.Bundle;

import com.lejoying.mc.api.Session;
import com.lejoying.mc.listener.ResponseListener;
import com.lejoying.mc.utils.MCNetTools;
import com.lejoying.utils.HttpTools;

public class SessionImpl implements Session {

	private Context context;

	public SessionImpl(Context context) {
		this.context = context;
	}

	@Override
	public void event(Bundle params,
			ResponseListener responseListener) {
		MCNetTools.ajax(context, "/api2/session/event", params, false,
				HttpTools.SEND_POST, 30000, responseListener);
	}

	@Override
	public void eventweb(Bundle params,
			ResponseListener responseListener) {
		MCNetTools.ajax(context, "/api2/session/eventweb", params, false,
				HttpTools.SEND_POST, 30000, responseListener);
	}

}
