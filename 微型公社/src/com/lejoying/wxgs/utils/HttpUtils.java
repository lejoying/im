package com.lejoying.wxgs.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Map;
import java.util.Set;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

public final class HttpUtils {

	public static final int SEND_GET = 0xff01;
	public static final int SEND_POST = 0xff02;

	public static boolean hasNetwork(Context context) {
		return getActiveNetworkInfo(context) == null ? false : true;
	}

	public static NetworkInfo getActiveNetworkInfo(Context context) {
		NetworkInfo networkInfo = null;
		ConnectivityManager connectivityManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connectivityManager != null) {
			networkInfo = connectivityManager.getActiveNetworkInfo();
		}
		return networkInfo;
	}

	public static void connection(String url, int method, int timeout,
			Map<String, String> params, Callback callback) {
		HttpURLConnection httpURLConnection = null;
		URL connectionURL = null;
		try {
			switch (method) {
			case SEND_GET:
				StringBuffer getPath = new StringBuffer(url);
				if (params != null) {
					Set<String> keys = params.keySet();
					if (keys != null) {
						getPath.append("?");
						for (String key : keys) {
							getPath.append(key + "=" + params.get(key) + "&");
						}
						if (getPath.length() != 0) {
							getPath.delete(getPath.length() - 1,
									getPath.length());
						}
					}
				}
				connectionURL = new URL(getPath.toString());
				httpURLConnection = (HttpURLConnection) connectionURL
						.openConnection();
				httpURLConnection.setRequestMethod("GET");
				httpURLConnection.setReadTimeout(timeout);
				httpURLConnection.setConnectTimeout(timeout);
				break;
			case SEND_POST:
				StringBuffer paramData = new StringBuffer();
				if (params != null) {
					Set<String> keys = params.keySet();
					if (keys != null) {
						for (String key : keys) {
							paramData.append(key
									+ "="
									+ URLEncoder.encode(params.get(key),
											"UTF-8") + "&");
						}
						if (paramData.length() != 0) {
							paramData.delete(paramData.length() - 1,
									paramData.length());
						}
					}
				} else {
					paramData.append(" ");
				}
				connectionURL = new URL(url);
				httpURLConnection = (HttpURLConnection) connectionURL
						.openConnection();
				httpURLConnection.setRequestMethod("POST");
				httpURLConnection.setReadTimeout(timeout);
				httpURLConnection.setConnectTimeout(timeout);

				httpURLConnection.setDoOutput(true);
				httpURLConnection.setRequestProperty("Content-Type",
						"application/x-www-form-urlencoded");
				httpURLConnection.setRequestProperty("Content-Length",
						paramData.length() + "");
				OutputStream os = httpURLConnection.getOutputStream();
				byte buffer[] = paramData.toString().getBytes();
				os.write(buffer);
				os.flush();
				os.close();
				break;
			}
			if (httpURLConnection != null) {
				callback.connectionCreated(httpURLConnection);
				if (httpURLConnection.getResponseCode() == 200) {
					callback.success(httpURLConnection.getInputStream(),
							httpURLConnection);
				} else {
					callback.failed(httpURLConnection.getResponseCode());

					callback.timeout();

					if (httpURLConnection != null) {
						httpURLConnection.disconnect();
					}
				}
			}
		} catch (IOException e) {
			callback.error();
			if (httpURLConnection != null) {
				httpURLConnection.disconnect();
			}
		}
	}

	public interface Callback {
		public abstract void success(InputStream is,
				HttpURLConnection httpURLConnection) throws IOException;

		public void error();

		public void failed(int responseCode);

		public void timeout();

		public void connectionCreated(HttpURLConnection httpURLConnection);
	}

	public static void sendGetUseBundle(String path, int timeout,
			Bundle params, HttpListener httpListener) {
		InputStream is = null;
		HttpURLConnection httpURLConnection = null;
		boolean isTimeout = true;
		try {
			if (params != null) {
				Set<String> keys = params.keySet();
				if (keys != null) {
					path += "?";
					for (String key : keys) {
						path += key + "=" + String.valueOf(params.get(key))
								+ "&";
					}
					path = path.substring(0, path.length() - 1);
				}
			}
			URL url = new URL(path);
			httpURLConnection = (HttpURLConnection) url.openConnection();
			httpURLConnection.setRequestMethod("GET");
			httpURLConnection.setReadTimeout(timeout);
			httpURLConnection.setConnectTimeout(timeout);

			httpListener.connectionCreated(httpURLConnection);
			if (httpURLConnection.getResponseCode() == 200) {
				isTimeout = false;
				is = httpURLConnection.getInputStream();
			}

		} catch (IOException e) {
			// e.printStackTrace();
		} finally {
			httpListener.handleInputStream(is, isTimeout);
			if (httpURLConnection != null) {
				httpURLConnection.disconnect();
			}
		}
	}

	public static void sendPostUseBundle(String path, int timeout,
			Bundle params, HttpListener httpListener) {
		InputStream is = null;
		HttpURLConnection httpURLConnection = null;
		boolean isTimeout = true;
		try {
			String paramData = "";
			if (params != null) {
				Set<String> keys = params.keySet();
				if (keys != null) {
					for (String key : keys) {
						paramData += key
								+ "="
								+ URLEncoder.encode(
										String.valueOf(params.get(key)),
										"UTF-8") + "&";
					}
					paramData = paramData.substring(0, paramData.length() - 1);
				}
			} else {
				paramData = " ";
			}
			URL url = new URL(path);
			httpURLConnection = (HttpURLConnection) url.openConnection();
			httpURLConnection.setRequestMethod("POST");
			httpURLConnection.setReadTimeout(timeout);
			httpURLConnection.setConnectTimeout(timeout);

			httpURLConnection.setDoOutput(true);
			httpURLConnection.setRequestProperty("Content-Type",
					"application/x-www-form-urlencoded");
			httpURLConnection.setRequestProperty("Content-Length",
					paramData.length() + "");
			OutputStream os = httpURLConnection.getOutputStream();
			byte buffer[] = paramData.getBytes();
			os.write(buffer);
			os.flush();
			os.close();

			httpListener.connectionCreated(httpURLConnection);
			if (httpURLConnection.getResponseCode() == 200) {
				isTimeout = false;
				is = httpURLConnection.getInputStream();
			}
		} catch (IOException e) {
			// e.printStackTrace();
		} finally {
			httpListener.handleInputStream(is, isTimeout);
			if (httpURLConnection != null) {
				httpURLConnection.disconnect();
			}
		}
	}

	public interface HttpListener {
		public void handleInputStream(InputStream is, boolean isTimeout);

		public void connectionCreated(HttpURLConnection httpURLConnection);
	}
}
