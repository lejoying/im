package com.lejoying.utils;

import java.io.InputStream;
import java.net.HttpURLConnection;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;

import com.lejoying.autosendsms.R;
import com.lejoying.utils.MCHttpTools.HttpListener;

public class Ajax {

	public static Handler handler = new Handler();

	public static void ajax(final Context context, final String url,
			final Bundle param, final int method, final int timeout,
			final ResponseListener responseListener) {
		boolean hasNetwork = MCHttpTools.hasNetwork(context);

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
					if (method == MCHttpTools.SEND_GET) {
						MCHttpTools.sendGet(url, timeout,
								param, httpListener);
					}
					if (method == MCHttpTools.SEND_POST) {
						MCHttpTools.sendPost(url,
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

	public interface ResponseListener {
		public void connectionCreated(HttpURLConnection httpURLConnection);

		public void noInternet();

		public void success(JSONObject data);

		public void unsuccess(JSONObject data);

		public void failed();
	}

}
