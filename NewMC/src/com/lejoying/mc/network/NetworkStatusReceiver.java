package com.lejoying.mc.network;

import com.lejoying.mc.data.App;
import com.lejoying.utils.HttpTools;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class NetworkStatusReceiver extends BroadcastReceiver {
	public App app = App.getInstance();

	@Override
	public void onReceive(Context context, Intent intent) {
		if (HttpTools.hasNetwork(context)) {
			app.networkStatus = HttpTools.getActiveNetworkInfo(context).getTypeName();
			System.out.println("network type:" + app.networkStatus);
		} else {
			app.networkStatus = "none";
			System.out.println("network status is offline");
		}
	}
}
