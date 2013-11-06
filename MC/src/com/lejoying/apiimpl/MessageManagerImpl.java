package com.lejoying.apiimpl;

import java.util.Map;

import android.app.Activity;

import com.lejoying.api.MessageManager;
import com.lejoying.listener.ResponseListener;
import com.lejoying.mcutils.MCTools;
import com.lejoying.utils.HttpTools;

public class MessageManagerImpl implements MessageManager {

	private Activity activity;

	public MessageManagerImpl(Activity activity) {
		this.activity = activity;
	}

	@Override
	public void send(Map<String, String> param,
			ResponseListener responseListener) {
		MCTools.ajax(activity, "/api2/message/send", param, false,
				HttpTools.SEND_POST, 30000, responseListener);
	}

	@Override
	public void get(Map<String, String> param, ResponseListener responseListener) {
		MCTools.ajax(activity, "/api2/message/get", param, false,
				HttpTools.SEND_POST, 30000, responseListener);
	}

}
