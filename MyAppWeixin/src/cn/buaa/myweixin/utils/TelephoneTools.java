package cn.buaa.myweixin.utils;

import android.content.Context;
import android.telephony.TelephonyManager;

public class TelephoneTools {
	
	public static String getPhoneNumber(Context context){
		TelephonyManager telephonyManager = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);
		return telephonyManager.getLine1Number();
	}
}
