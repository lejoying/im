package com.lejoying.mc.apiimpl;

import android.content.Context;
import android.os.Bundle;

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
	public void modify(Bundle params,
			ResponseListener responseListener) {
		MCNetTools.ajax(context, "/api2/circle/modify", params, true,
				HttpTools.SEND_POST, 5000, responseListener);
	}

	@Override
	public void delete(Bundle params,
			ResponseListener responseListener) {
		MCNetTools.ajax(context, "/api2/circle/delete", params, true,
				HttpTools.SEND_POST, 5000, responseListener);
	}

}
