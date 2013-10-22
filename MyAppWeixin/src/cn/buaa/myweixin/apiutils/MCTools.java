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

	private static Account nowAccount;

	private static final String DOMAIN = "http://192.168.2.43:8071";

	private static String lasturl;
	private static Map<String, String> lastparam;
	private static long lasttime;

	public static void ajax(Activity activity, final String url,
			final Map<String, String> param, boolean lock, final int method,
			final int timeout, final ResponseListener responseListener) {
		boolean hasNetwork = HttpTools.hasNetwork(activity);

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
					try {
						byte[] b = null;
						if (method == HttpTools.SEND_GET) {
							b = HttpTools.sendGet(DOMAIN + url, timeout, param);
						}
						if (method == HttpTools.SEND_POST) {
							b = HttpTools
									.sendPost(DOMAIN + url, timeout, param);
						}

						Looper.prepare();
						if (b != null) {
							JSONObject data = new JSONObject(new String(b));
							if (data != null) {
								String info = data.getString("提示信息");
								info = info.substring(info.length() - 2,
										info.length());

								if (info.equals("成功")) {
									responseListener.success(data);
								}
								if (info.equals("失败")) {
									responseListener.unsuccess(data);
								}
							}
						}
						
					} catch (IOException e) {
						responseListener.failed();
						e.printStackTrace();
					} catch (JSONException e) {
						e.printStackTrace();
					}
					Looper.loop();
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

		MCTools.nowAccount = account;

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
		Account account = MCTools.nowAccount;
		if (account != null) {
			return account;
		}
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

	public static String createAccessKey() {

		String[] strs = { "a", "b", "c", "d", "e", "f", "g", "h", "i", "j",
				"k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v",
				"w", "x", "y", "z", "A", "B", "C", "D", "E", "F", "G", "H",
				"I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T",
				"U", "V", "W", "X", "Y", "Z", "0", "1", "2", "3", "4", "5",
				"6", "7", "8", "9" };
		int count = 20;
		String str = "";
		for (int i = 0; i < 9; i++) {
			str += strs[(int) Math.floor(Math.random() * strs.length)];
		}
		str += "a";
		for (int i = 0; i < count - 10; i++) {
			str += strs[(int) Math.floor(Math.random() * strs.length)];
		}
		return str;
	}

}
