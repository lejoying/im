package com.lejoying.mc.network;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.lejoying.mc.data.App;
import com.lejoying.mc.service.PushService;
import com.lejoying.utils.HttpUtils;

public class NetworkStatusReceiver extends BroadcastReceiver {
	public App app = App.getInstance();

	@Override
	public void onReceive(Context context, Intent intent) {
		if (HttpUtils.hasNetwork(context)) {
			app.networkStatus = HttpUtils.getActiveNetworkInfo(context)
					.getTypeName();
			Intent service = new Intent(context, PushService.class);
			service.putExtra("objective", "network");
			context.startService(service);
			app.serverHandler.getAllData();
			System.out.println("network type:" + app.networkStatus);
		} else {
			app.networkStatus = "none";
			System.out.println("network status is offline");
		}
	}
}
