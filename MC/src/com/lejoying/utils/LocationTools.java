package com.lejoying.utils;

import android.app.Activity;
import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.widget.Toast;

public class LocationTools {

	public static double[] getLocation(Activity activity) {
		LocationManager lm = (LocationManager) activity
				.getSystemService(Context.LOCATION_SERVICE);
		// 返回所有已知的位置提供者的名称列表，包括未获准访问或调用活动目前已停用的。
		//List<String> lp = lm.getAllProviders();
		// for (String item : lp) {
		// System.out.println("可用位置服务：" + item);
		// }
		Criteria criteria = new Criteria();
		criteria.setCostAllowed(false);
		// 设置位置服务免费
		criteria.setAccuracy(Criteria.ACCURACY_COARSE); // 设置水平位置精度
		// getBestProvider 只有允许访问调用活动的位置供应商将被返回
		String providerName = lm.getBestProvider(criteria, true);
		//System.out.println("------位置服务：" + providerName);

		if (providerName != null) {
			Location location = lm.getLastKnownLocation(providerName);
			// 获取维度信息
			double latitude = location.getLatitude();
			// 获取经度信息
			double longitude = location.getLongitude();
			return new double[] { longitude, latitude };
		} else {
			Toast.makeText(activity, "1.请检查网络连接 \n2.请打开我的位置",
					Toast.LENGTH_SHORT).show();
		}

		return null;
	}
}
