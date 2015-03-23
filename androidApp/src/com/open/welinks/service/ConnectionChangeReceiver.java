package com.open.welinks.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;

import com.open.lib.MyLog;
import com.open.welinks.model.DataHandler;

public class ConnectionChangeReceiver extends BroadcastReceiver {

	public String tag = "ConnectionChangeReceiver";
	public MyLog log = new MyLog(tag, true);

	@Override
	public void onReceive(Context context, Intent intent) {
		 try {
		ConnectivityManager conManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (conManager.getActiveNetworkInfo() != null) {
			if (conManager.getActiveNetworkInfo().isAvailable()) {
				if (!PushService.isRunning && PushService.instance != null) {
					PushService.instance.startLongPull();
					log.e("onReceive-------------------------ok");
					DataHandler dataHandlers = DataHandler.getInstance();
					dataHandlers.sendShareMessage();
				}
			} else {
				PushService.isRunning = false;
				log.e("网络已断开1");
			}
		} else {
			log.e("网络已断开2");
			PushService.isRunning = false;
		}
		} catch (Exception e) {
			e.printStackTrace();
			log.e(e.toString());
		}
	}
}