package com.lejoying.utils;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.Date;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.os.Bundle;

import com.lejoying.utils.HttpTools.HttpListener;

public class Ajax {

	public static class Settings {
		public String url = null;
		public int method = HttpTools.SEND_POST;
		public int timeout = 5000;
		public Bundle params = null;
	};

	public interface AjaxInterface {
		public void setParams(Settings settings);

		public void onSuccess(JSONObject jData);

		public void failed();

		public void noInternet();

		public void timeout();

		public void connectionCreated(HttpURLConnection httpURLConnection);
	}

	public static void ajax(Context context, final AjaxInterface ajaxInterface) {

		final Settings settings = new Settings();
		final long startTime = new Date().getTime();
		ajaxInterface.setParams(settings);
		if (!HttpTools.hasNetwork(context)) {
			ajaxInterface.noInternet();
		} else {
			new Thread() {
				private byte[] b = null;

				@Override
				public void run() {
					super.run();
					HttpListener httpListener = new HttpListener() {
						@Override
						public void handleInputStream(InputStream is) {
							b = StreamTools.isToData(is);
						}

						@Override
						public void connectionCreated(
								final HttpURLConnection httpURLConnection) {
							ajaxInterface.connectionCreated(httpURLConnection);
						}
					};
					if (settings.method == HttpTools.SEND_GET) {
						HttpTools
								.sendGetUseBundle(settings.url,
										settings.timeout, settings.params,
										httpListener);
					} else if (settings.method == HttpTools.SEND_POST) {
						HttpTools
								.sendPostUseBundle(settings.url,
										settings.timeout, settings.params,
										httpListener);
					}
					try {
						if (b == null) {
							long endTime = new Date().getTime();
							if (endTime - startTime < settings.timeout) {
								ajaxInterface.failed();
							} else {
								ajaxInterface.timeout();
							}
						} else {
							final JSONObject jData = new JSONObject(new String(
									b));
							if (jData != null) {
								ajaxInterface.onSuccess(jData);
							}
						}
					} catch (JSONException e) {
					}
				}
			}.start();
		}
	}

}
