package com.open.welinks.service;

import com.open.welinks.model.DataHandlers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.util.Log;

public class ConnectionChangeReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		ConnectivityManager conManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (conManager.getActiveNetworkInfo() != null) {
			if (conManager.getActiveNetworkInfo().isAvailable()) {
				if (!PushService.isRunning) {
					PushService.instance.startLongPull();
					Log.e("ConnectionChangeReceiver", "onReceive-------------------------ok");
					DataHandlers dataHandlers = DataHandlers.getInstance();
					dataHandlers.sendShareMessage();
				}
			} else {
				PushService.isRunning = false;
			}
		} else {
			PushService.isRunning = false;
		}
	}

}