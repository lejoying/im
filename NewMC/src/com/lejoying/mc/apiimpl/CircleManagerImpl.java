package com.lejoying.mc.apiimpl;

import java.util.Map;

import android.app.Activity;

import com.lejoying.mc.api.CircleManager;
import com.lejoying.mc.listener.ResponseListener;
import com.lejoying.mc.utils.MCTools;
import com.lejoying.utils.HttpTools;

public class CircleManagerImpl implements CircleManager {

	private Activity activity;

	public CircleManagerImpl(Activity activity) {
		super();
		this.activity = activity;
	}
	
	@Override
	public void modify(Map<String, String> param,
			ResponseListener responseListener) {
		MCTools.ajax(activity, "/api2/circle/modify", param, true,
				HttpTools.SEND_POST, 5000, responseListener);
	}

	@Override
	public void delete(Map<String, String> param,
			ResponseListener responseListener) {
		MCTools.ajax(activity, "/api2/circle/delete", param, true,
				HttpTools.SEND_POST, 5000, responseListener);		
	}

}
