package com.lejoying.mc.utils;

import java.io.InputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.os.Handler;
import android.view.Gravity;
import android.widget.Toast;

import com.lejoying.mc.listener.ResponseListener;
import com.lejoying.utils.HttpTools;
import com.lejoying.utils.HttpTools.HttpListener;
import com.lejoying.utils.LocationTools;
import com.lejoying.utils.StreamTools;

public class MCNetTools {

	public static Handler handler = new Handler();

	private static String lasturl;
	private static Map<String, String> lastparam;
	private static long lasttime;

	private static Toast toast;

	public static void ajax(Context context, final String url,
			final Map<String, String> param, boolean lock, final int method,
			final int timeout, final ResponseListener responseListener) {
		boolean hasNetwork = HttpTools.hasNetwork(context);

		if (lock) {
			if ((url.equals(lasturl) && param.equals(lastparam))
					&& new Date().getTime() - lasttime < 5000) {
				return;
			}
		}
		lasturl = url;
		lastparam = param;
		lasttime = new Date().getTime();

		if (!hasNetwork) {
			responseListener.noInternet();
		} else {
			new Thread() {
				@Override
				public void run() {
					super.run();
					HttpListener httpListener = new HttpListener() {

						@Override
						public void handleInputStream(InputStream is) {
							try {
								if (is != null) {
									byte[] b = StreamTools.isToData(is);
									final JSONObject data = new JSONObject(
											new String(b));
									if (data != null) {
										String info = data.getString("提示信息");
										info = info.substring(
												info.length() - 2,
												info.length());

										if (info.equals("成功")) {
											handler.post(new Runnable() {
												@Override
												public void run() {
													responseListener
															.success(data);
												}
											});
										}
										if (info.equals("失败")) {
											handler.post(new Runnable() {
												@Override
												public void run() {
													responseListener
															.unsuccess(data);
												}
											});
										}
									}
								}
								if (is == null) {
									handler.post(new Runnable() {
										@Override
										public void run() {
											responseListener.failed();
										}
									});
								}
							} catch (JSONException e) {
								e.printStackTrace();
							}
						}
					};
					if (method == HttpTools.SEND_GET) {
						HttpTools.sendGet(MCStaticData.DOMAIN + url, timeout,
								param, httpListener);
					}
					if (method == HttpTools.SEND_POST) {
						HttpTools.sendPost(MCStaticData.DOMAIN + url, timeout,
								param, httpListener);
					}
				}
			}.start();
		}
	}

	public static Map<String, String> getParamsWithLocation(Context context) {
		double[] location = LocationTools.getLocation(context);
		Map<String, String> map = new HashMap<String, String>();
		map.put("latitude", String.valueOf(location[1]));
		map.put("longitude", String.valueOf(location[0]));
		return map;
	}

	public static void showMsg(Context context, String text) {
		if (toast != null) {
			toast.cancel();
		}
		toast = Toast.makeText(context, text, Toast.LENGTH_LONG);
		toast.setGravity(Gravity.CENTER, 0, 0);
		toast.show();
	}

	public static void cleanMsg() {
		if (toast != null) {
			toast.cancel();
		}
	}
}
