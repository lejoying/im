package com.lejoying.mc.apiimpl;

import java.util.Map;

import android.content.Context;

import com.lejoying.mc.api.MessageManager;
import com.lejoying.mc.listener.ResponseListener;
import com.lejoying.mc.utils.MCTools;
import com.lejoying.utils.HttpTools;

public class MessageManagerImpl implements MessageManager {

	private Context context;

	public MessageManagerImpl(Context context) {
		this.context = context;
	}

	@Override
	public void send(Map<String, String> param,
			ResponseListener responseListener) {
		MCTools.ajax(context, "/api2/message/send", param, false,
				HttpTools.SEND_POST, 30000, responseListener);
	}

	@Override
	public void get(Map<String, String> param, ResponseListener responseListener) {
		MCTools.ajax(context, "/api2/message/get", param, false,
				HttpTools.SEND_POST, 30000, responseListener);
	}

}
