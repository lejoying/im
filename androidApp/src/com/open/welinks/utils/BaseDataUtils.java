package com.open.welinks.utils;

import java.lang.reflect.Field;

import com.open.welinks.R;
import com.open.welinks.model.Data;

import android.content.Context;
import android.app.Activity;
import android.util.DisplayMetrics;
import android.util.TypedValue;

public class BaseDataUtils {
	public static Data data = Data.getInstance();

	public static Context context;

	public static void initBaseData(Context context) {
		BaseDataUtils.context = context;
		data.baseData.metrics = new DisplayMetrics();
		((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(data.baseData.metrics);

		data.baseData.screenWidth = data.baseData.metrics.widthPixels;
		data.baseData.screenHeight = data.baseData.metrics.heightPixels;
		data.baseData.density = data.baseData.metrics.density;

		Class<?> c = null;
		Object obj = null;
		Field field = null;
		int x = 0, statusBarHeight = 0;
		try {
			c = Class.forName("com.android.internal.R$dimen");
			obj = c.newInstance();
			field = c.getField("status_bar_height");
			x = Integer.parseInt(field.get(obj).toString());
			statusBarHeight = context.getResources().getDimensionPixelSize(x);
		} catch (Exception e) {
			e.printStackTrace();
		}
		data.baseData.stateBar = statusBarHeight;
		data.baseData.appHeight = data.baseData.screenHeight - data.baseData.stateBar;

	}

	public static float dpToPx(int dp) {
		return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, data.baseData.metrics);
	}

	public static float pxToDp(int px) {
		return (int) (px / data.baseData.density);
	}

	public static float dpToPx(float dp) {
		return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, data.baseData.metrics);
	}

	public static float pxToDp(float px) {
		return (int) (px / data.baseData.density);
	}

	/**
	 * @param sex
	 * @return male return true , female return false
	 */
	public static boolean determineSex(String sex) {
		if ("男".equals(sex) || "male".equals(sex)) {
			return true;
		} else {
			return false;
		}

	}

	public static String generateMainBusiness(String type, String mainBusiness) {
		if ("".equals(mainBusiness)) {
			if ("point".equals(type)) {
				mainBusiness = context.getString(R.string.business_personal_not_business);
			} else if ("group".equals(type)) {
				mainBusiness = context.getString(R.string.business_room_not_business);
			}

		}
		return mainBusiness;
	}

	public static String generateUserName(String nickName, String alias) {
		String showName = "";
		if (alias != null && !"".equals(alias)) {
			showName = alias + "(" + nickName + ")";
		} else {
			showName = nickName;
		}

		return showName;
	}
}
