package com.lejoying.utils;

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

public final class HttpTools {

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

	public static void sendGet(String path, int timeout,
			Map<String, String> params, HttpListener httpListener) {
		InputStream is = null;
		HttpURLConnection httpURLConnection = null;
		try {
			if (params != null) {
				Set<String> keys = params.keySet();
				if (keys != null) {
					path += "?";
					for (String key : keys) {
						path += key + "=" + params.get(key) + "&";
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
				is = httpURLConnection.getInputStream();
			}
			if (is != null) {
				httpListener.handleInputStream(is);
			} else {
				httpListener.failed();
			}
		} catch (IOException e) {
			// e.printStackTrace();
		} finally {
			if (httpURLConnection != null) {
				httpURLConnection.disconnect();
			}
		}
	}

	public static void sendPost(String path, int timeout,
			Map<String, String> params, HttpListener httpListener) {
		InputStream is = null;
		HttpURLConnection httpURLConnection = null;
		try {
			String paramData = "";
			if (params != null) {
				Set<String> keys = params.keySet();
				if (keys != null) {
					for (String key : keys) {
						paramData += key + "="
								+ URLEncoder.encode(params.get(key), "UTF-8")
								+ "&";
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
				is = httpURLConnection.getInputStream();
			}
			if (is != null) {
				httpListener.handleInputStream(is);
			} else {
				httpListener.failed();
			}
		} catch (IOException e) {
			// e.printStackTrace();
		} finally {
			if (httpURLConnection != null) {
				httpURLConnection.disconnect();
			}
		}
	}

	public static void sendGetUseBundle(String path, int timeout,
			Bundle params, HttpListener httpListener) {
		InputStream is = null;
		HttpURLConnection httpURLConnection = null;
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
				is = httpURLConnection.getInputStream();
			}
			if (is != null) {
				httpListener.handleInputStream(is);
			} else {
				httpListener.failed();
			}
		} catch (IOException e) {
			// e.printStackTrace();
		} finally {
			if (httpURLConnection != null) {
				httpURLConnection.disconnect();
			}
		}
	}

	public static void sendPostUseBundle(String path, int timeout,
			Bundle params, HttpListener httpListener) {
		InputStream is = null;
		HttpURLConnection httpURLConnection = null;
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
				is = httpURLConnection.getInputStream();
			}
			if (is != null) {
				httpListener.handleInputStream(is);
			} else {
				httpListener.failed();
			}
		} catch (IOException e) {
			// e.printStackTrace();
		} finally {
			if (httpURLConnection != null) {
				httpURLConnection.disconnect();
			}
		}
	}

	public interface HttpListener {
		public void handleInputStream(InputStream is);

		public void failed();

		public void connectionCreated(HttpURLConnection httpURLConnection);
	}
}
