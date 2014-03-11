package com.lejoying.wxgs.app.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.lejoying.wxgs.app.MainApplication;
import com.lejoying.wxgs.app.service.PushService;

public class InfoReceiver extends BroadcastReceiver {
	MainApplication main;

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		context.startService(new Intent(context, PushService.class));
	}

}
