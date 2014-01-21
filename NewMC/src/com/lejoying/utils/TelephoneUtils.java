package com.lejoying.utils;

import android.content.Context;
import android.telephony.TelephonyManager;

public final class TelephoneUtils {

	public static String getPhoneNumber(Context context) {
		TelephonyManager telephonyManager = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);
		return telephonyManager.getLine1Number();
	}

}
