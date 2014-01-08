package com.lejoying.mc.network;

import com.lejoying.utils.HttpTools;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class NetworkStatusReceiver extends BroadcastReceiver {
	@Override
	public void onReceive(Context context, Intent intent) {
		if (HttpTools.hasNetwork(context)) {
			System.out.println("network type:"
					+ HttpTools.getActiveNetworkInfo(context).getTypeName());
		} else {
			System.out.println("network status is offline");
		}
	}
}
