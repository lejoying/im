package com.lejoying.mc.utils;

import java.io.InputStream;
import java.net.HttpURLConnection;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.widget.Toast;

import com.lejoying.mc.R;
import com.lejoying.mc.listener.ResponseListener;
import com.lejoying.mc.utils.MCHttpTools.HttpListener;
import com.lejoying.utils.HttpTools;
import com.lejoying.utils.StreamTools;

public class MCNetTools {

	public static Handler handler = new Handler();

	private static Toast toast;

	public static void ajax(final Context context, final String url,
			final Bundle param, final int method, final int timeout,
			final ResponseListener responseListener) {
		boolean hasNetwork = HttpTools.hasNetwork(context);

		if (!hasNetwork) {
			responseListener.noInternet();
		} else {
			new Thread() {
				private byte[] b = null;

				@Override
				public void run() {
					super.run();
					HttpListener httpListener = new HttpListener() {
						@Override
						public void handleInputStream(InputStream is,
								HttpURLConnection httpURLConnection) {
							responseListener
									.connectionCreated(httpURLConnection);
							b = StreamTools.isToData(is);
						}
					};
					if (method == HttpTools.SEND_GET) {
						MCHttpTools.sendGet(MCStaticData.DOMAIN + url, timeout,
								param, httpListener);
					}
					if (method == HttpTools.SEND_POST) {
						MCHttpTools.sendPost(MCStaticData.DOMAIN + url,
								timeout, param, httpListener);
					}
					try {
						if (b == null) {
							handler.post(new Runnable() {
								@Override
								public void run() {
									responseListener.failed();
								}
							});
						} else {
							final JSONObject data = new JSONObject(
									new String(b));
							if (data != null) {
								String info = data.getString(context
										.getString(R.string.app_notice));
								info = info.substring(info.length() - 2,
										info.length());

								if (info.equals(context
										.getString(R.string.app_success))) {
									handler.post(new Runnable() {
										@Override
										public void run() {
											responseListener.success(data);
										}
									});
								}
								if (info.equals(context
										.getString(R.string.app_unsuccess))) {
									handler.post(new Runnable() {
										@Override
										public void run() {
											responseListener.unsuccess(data);
										}
									});
								}
							}
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
			}.start();
		}
	}

	public static void showMsg(Context context, String text) {
		if (text == null || text.equals("")) {
			return;
		}
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
