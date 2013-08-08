package cn.buaa.myweixin.utils;

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

public class HttpTools {

	/**
	 * 判断网络是否可用,返回true时网络可用
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

	/**
	 * 用get方法发送params到地址为path的服务器，并返回服务器响应的byte[]
	 * @param path
	 * @param params
	 * @return
	 * @throws IOException
	 */
	public static byte[] sendGet(String path, Map<String, String> params)
			throws IOException {
		byte data[] = null;
		InputStream is = sendGetForInputStream(path, params);
		if(is!=null){
			data = StreamTools.isToData(is);
		}
		return data;
	}
 
	public static InputStream sendGetForInputStream(String path, Map<String, String> params)
			throws IOException {
		InputStream is = null;
		// 拼接请求参数
		if (params != null) {
			Set<String> keys = params.keySet();
			if (keys != null) {
				path += "?";
				for (String key : keys) {
					path += key + "="
							+ URLEncoder.encode(params.get(key), "UTF-8") + "&";
				}
				path = path.substring(0, path.length() - 2);
			}
		}
		// 设置请求路径
		URL url = new URL(path);
		// 创建请求链接
		HttpURLConnection httpURLConnection = (HttpURLConnection) url
				.openConnection();
		// 设置请求方式
		httpURLConnection.setRequestMethod("GET");
		// 设置超时
		httpURLConnection.setConnectTimeout(5000);
		// 判断服务器响应
		byte[] data = null;
		if (httpURLConnection.getResponseCode() == 200) {
			is = httpURLConnection.getInputStream();
		}
		httpURLConnection.disconnect();
		return is;
	}
	
	public static byte[] sendPost(String path, Map<String, String> params)
			throws IOException {
		//拼接请求参数
		String paramData = "";
		if (params != null) {
			Set<String> keys = params.keySet();
			if (keys != null) {
				for (String key : keys) {
					paramData += key + "=" + params.get(key) + "&";
				}
				paramData = paramData.substring(0, paramData.length() - 1);
			}
		}
		//请求参数不能为空
		if (paramData.length()==0) {
			throw new NullPointerException("请求参数为空");
		}
		//设置请求路径
		URL url = new URL(path);
		//创建请求链接
		HttpURLConnection httpURLConnection = (HttpURLConnection) url
				.openConnection();
		//设置请求方法
		httpURLConnection.setRequestMethod("POST");
		//设置请求超时
		httpURLConnection.setConnectTimeout(5000);
		httpURLConnection.setDoOutput(true);
		// 设置Content-Type
		httpURLConnection.setRequestProperty("Content-Type",
				"application/x-www-form-urlencoded");
		// 设置Content-Length
		httpURLConnection.setRequestProperty("Content-Length", paramData.length()
				+ "");
		OutputStream os = httpURLConnection.getOutputStream();
		byte buffer[] = paramData.getBytes();
		os.write(buffer);
		os.flush();
		os.close();
		byte data[] = null;
		//判断返回状态
		if (httpURLConnection.getResponseCode() == 200) {
			InputStream is = httpURLConnection.getInputStream();
			data = StreamTools.isToData(is);
		}
		httpURLConnection.disconnect();
		return data;
	}
}
