package com.lejoying.mc.apiimpl;

import java.util.Map;

import android.content.Context;

import com.lejoying.mc.api.CircleManager;
import com.lejoying.mc.listener.ResponseListener;
import com.lejoying.mc.utils.MCNetTools;
import com.lejoying.utils.HttpTools;

public class CircleManagerImpl implements CircleManager {

	private Context context;

	public CircleManagerImpl(Context context) {
		super();
		this.context = context;
	}

	@Override
	public void modify(Map<String, String> param,
			ResponseListener responseListener) {
		MCNetTools.ajax(context, "/api2/circle/modify", param, true,
				HttpTools.SEND_POST, 5000, responseListener);
	}

	@Override
	public void delete(Map<String, String> param,
			ResponseListener responseListener) {
		MCNetTools.ajax(context, "/api2/circle/delete", param, true,
				HttpTools.SEND_POST, 5000, responseListener);
	}

}
