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

public final class HttpTools {

	public static final int SEND_GET = 0xff01;
	public static final int SEND_POST = 0xff02;

	/**
	 * 判断网络是否可用,返回true时网络可用
	 * 
	 * @param context
	 * @return
	 */
	public static boolean hasNetwork(Context context) {
		ConnectivityManager connectivityManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
		if (networkInfo != null) {
			return true;
		}
		return false;
	}

	public static void sendGet(String path, int timeout,
			Map<String, String> params, HttpListener httpListener) {
		InputStream is = null;
		// 设置请求路径
		HttpURLConnection httpURLConnection = null;
		try {
			// 拼接请求参数
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
			// 创建请求链接
			httpURLConnection = (HttpURLConnection) url.openConnection();
			// 设置请求方式
			httpURLConnection.setRequestMethod("GET");
			// 设置超时
			httpURLConnection.setReadTimeout(timeout);
			httpURLConnection.setConnectTimeout(timeout);
			// 判断服务器响应

			if (httpURLConnection.getResponseCode() == 200) {
				is = httpURLConnection.getInputStream();
			}
		} catch (IOException e) {
			// e.printStackTrace();
		} finally {
			httpListener.handleInputStream(is, httpURLConnection);
			httpURLConnection.disconnect();
		}
	}

	public static void sendPost(String path, int timeout,
			Map<String, String> params, HttpListener httpListener) {
		InputStream is = null;
		HttpURLConnection httpURLConnection = null;
		try {
			// 拼接请求参数
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
			}
			// 请求参数不能为空
			if (paramData.length() == 0) {
				throw new NullPointerException("请求参数为空");
			}
			// 设置请求路径
			URL url = new URL(path);
			// 创建请求链接
			httpURLConnection = (HttpURLConnection) url.openConnection();
			// 设置请求方法
			httpURLConnection.setRequestMethod("POST");
			// 设置请求超时
			httpURLConnection.setReadTimeout(timeout);
			httpURLConnection.setConnectTimeout(timeout);

			httpURLConnection.setDoOutput(true);
			// 设置Content-Type
			httpURLConnection.setRequestProperty("Content-Type",
					"application/x-www-form-urlencoded");
			// 设置Content-Length
			httpURLConnection.setRequestProperty("Content-Length",
					paramData.length() + "");
			OutputStream os = httpURLConnection.getOutputStream();
			byte buffer[] = paramData.getBytes();
			os.write(buffer);
			os.flush();
			os.close();

			// 判断返回状态
			if (httpURLConnection.getResponseCode() == 200) {
				is = httpURLConnection.getInputStream();
			}
		} catch (IOException e) {
			// e.printStackTrace();
		} finally {
			httpListener.handleInputStream(is, httpURLConnection);
			httpURLConnection.disconnect();
		}
	}

	public interface HttpListener {
		public void handleInputStream(InputStream is,
				HttpURLConnection httpURLConnection);
	}
}
