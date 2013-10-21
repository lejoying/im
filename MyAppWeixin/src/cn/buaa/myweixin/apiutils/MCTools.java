package cn.buaa.myweixin.apiutils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.StreamCorruptedException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import cn.buaa.myweixin.listener.ResponseListener;
import cn.buaa.myweixin.utils.HttpTools;
import cn.buaa.myweixin.utils.LocationTools;

import android.app.Activity;
import android.content.Context;
import android.os.Looper;

public class MCTools {

	private static final String DOMAIN = "http://192.168.2.43:8071";

	private static String lasturl;
	private static Map<String, String> lastparam;
	private static long lasttime;

	public static void sendForJSON(Activity activity, final String url,
			final Map<String, String> param, boolean lock, final int method,
			final ResponseListener responseListener) {
//		if (new Date().getTime() - lasttime < 500) {
//			httpStatusListener.shortIntervalTime();
//			return;
//		}
		if (lock) {
			if ((url.equals(lasturl) && param.equals(lastparam))
					&& new Date().getTime() - lasttime < 5000) {
				return;
			}
		}
		lasturl = url;
		lastparam = param;
		lasttime = new Date().getTime();
		boolean hasNetwork = HttpTools.hasNetwork(activity);
		if (!hasNetwork) {
			responseListener.noInternet();
		} else {
			new Thread() {
				@Override
				public void run() {
					super.run();
					try {
						JSONObject data = HttpTools.sendForJSONObject(
								MCTools.DOMAIN + url, param, method);
						if (data != null) {
							String info = data.getString("提示信息");
							info = info.substring(info.length() - 2,
									info.length());
							Looper.prepare();
							if (info.equals("成功")) {
								responseListener.success(data);
							}
							if (info.equals("失败")) {
								responseListener.failed(data);
							}
							Looper.loop();
						} 
					} catch (IOException e) {
						e.printStackTrace();
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
			}.start();
		}
	}

	public static Map<String, String> getLocationParam(Activity activity) {
		double[] location = LocationTools.getLocation(activity);
		Map<String, String> map = new HashMap<String, String>();
		map.put("latitude", String.valueOf(location[1]));
		map.put("longitude", String.valueOf(location[0]));
		return map;
	}

	public static void saveAccount(Activity activity, Account account) {
		try {
			OutputStream os = activity.openFileOutput("account",
					Context.MODE_PRIVATE);
			ObjectOutputStream oos = new ObjectOutputStream(os);
			oos.writeObject(account);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static Account getLoginedAccount(Activity activity) {
		Account account = null;
		try {
			InputStream is = activity.openFileInput("account");
			ObjectInputStream ois = new ObjectInputStream(is);
			account = (Account) ois.readObject();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (StreamCorruptedException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return account;
	}

}
