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

import com.lejoying.data.App;
import com.lejoying.mc.utils.MCHttpTools.HttpListener;
import com.lejoying.utils.StreamTools;

public class MCNetTools {

	static App app = App.getInstance();

	public static Handler handler = new Handler();

	private static Toast toast;

	public static void ajax(final Context context, final String url,
			final Bundle params, final int method, final int timeout,
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
						MCHttpTools.sendGet(app.config.DOMAIN + url, timeout,
								params, httpListener);
					}
					if (method == MCHttpTools.SEND_POST) {
						MCHttpTools.sendPost(app.config.DOMAIN + url, timeout,
								params, httpListener);
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
								handler.post(new Runnable() {
									@Override
									public void run() {
										responseListener.success(data);
									}
								});
							}
						}
					} catch (JSONException e) {
						e.printStackTrace();
						handler.post(new Runnable() {
							@Override
							public void run() {
								responseListener.failed();
							}
						});
					}
				}
			}.start();
		}
	}

	public static void showMsg(final Context context, final String text) {
		handler.post(new Runnable() {
			@Override
			public void run() {
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
		});
	}

	public static void cleanMsg() {
		if (toast != null) {
			toast.cancel();
		}
	}

	public interface ResponseListener {
		public void connectionCreated(HttpURLConnection httpURLConnection);

		public void noInternet();

		public void success(JSONObject data);

		public void failed();
	}

}
