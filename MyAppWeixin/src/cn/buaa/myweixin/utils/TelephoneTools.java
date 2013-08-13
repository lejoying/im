package cn.buaa.myweixin.utils;

import android.content.Context;
import android.telephony.TelephonyManager;

public final class TelephoneTools {
	
	/**
	 * 获取手机号码，如果运营商未在sim卡中写入号码或用户自己修改则获取号码错误
	 * @param context
	 * @return
	 */
	public static String getPhoneNumber(Context context){
		TelephonyManager telephonyManager = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);
		return telephonyManager.getLine1Number();
	}  
	
}  
