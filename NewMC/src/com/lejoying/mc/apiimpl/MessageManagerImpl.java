package com.lejoying.mc.apiimpl;

import android.content.Context;
import android.os.Bundle;

import com.lejoying.mc.api.MessageManager;
import com.lejoying.mc.listener.ResponseListener;
import com.lejoying.mc.utils.MCNetTools;
import com.lejoying.utils.HttpTools;

public class MessageManagerImpl implements MessageManager {

	private Context context;

	public MessageManagerImpl(Context context) {
		this.context = context;
	}

	@Override
	public void send(Bundle params,
			ResponseListener responseListener) {
		MCNetTools.ajax(context, "/api2/message/send", params, false,
				HttpTools.SEND_POST, 30000, responseListener);
	}

	@Override
	public void get(Bundle params, ResponseListener responseListener) {
		MCNetTools.ajax(context, "/api2/message/get", params, false,
				HttpTools.SEND_POST, 30000, responseListener);
	}

}
