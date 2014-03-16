package com.lejoying.wxgs.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public final class NetworkUtils {

	public static final int SEND_GET = 0xff01;
	public static final int SEND_POST = 0xff02;

	public static boolean hasNetwork(Context context) {
		return getActiveNetworkInfo(context) == null ? false : true;
	}

	public static NetworkInfo getActiveNetworkInfo(Context context) {
		NetworkInfo networkInfo = null;
		ConnectivityManager connectivityManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connectivityManager != null) {
			networkInfo = connectivityManager.getActiveNetworkInfo();
		}
		return networkInfo;
	}
}
