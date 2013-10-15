package cn.buaa.myweixin.utils;

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
import java.util.Set;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.os.Looper;

public class MCTools {

	private static String domain = "http://192.168.0.19:8071";
	private static Map<String, String> lastParam;
	private static String lastUrl;
	private static long time;

	private static Map<String, Map<String, String>> lastRequests = new HashMap<String, Map<String,String>>();
	private static long lastTime;

	public interface HttpStatusListener {

		public void shortIntervalTime();

		public void noInternet();

		public void getJSONSuccess(JSONObject data);
	}

	public static void postForJSON(Activity activity, final String url,
			final Map<String, String> param, boolean lock,
			final HttpStatusListener httpStatusListener) {
		if (new Date().getTime() - time < 500) {
			httpStatusListener.shortIntervalTime();
			return;
		}
		if (lock) {
			if ((url.equals(lastUrl) && param.equals(lastParam))
					&& new Date().getTime() - time < 5000) {
				return;
			}
		}
		lastParam = param;
		lastUrl = url;
		time = new Date().getTime();
		boolean hasNetwork = HttpTools.hasNetwork(activity);
		if (!hasNetwork) {
			httpStatusListener.noInternet();
		} else {
			new Thread() {
				@Override
				public void run() {
					super.run();
					try {
						byte[] data = HttpTools.sendPost(MCTools.domain + url,
								param);
						JSONObject jo = new JSONObject(new String(data));
						time = new Date().getTime();
						Looper.prepare();
						httpStatusListener.getJSONSuccess(jo);
						Looper.loop();
					} catch (IOException e) {
						e.printStackTrace();
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
			}.start();
		}
	}

	public static void postForJSON(Activity activity,
			final Map<String, Map<String, String>> requests, boolean lock,
			final HttpStatusListener httpStatusListener) {
		if (new Date().getTime() - time < 500) {
			httpStatusListener.shortIntervalTime();
			return;
		}
		if (lock) {
			if ((lastRequests.equals(requests))
					&& new Date().getTime() - lastTime < 5000) {
				return;
			}
		}
		lastRequests = requests;
		lastTime = new Date().getTime();
		boolean hasNetwork = HttpTools.hasNetwork(activity);
		if (!hasNetwork) {
			httpStatusListener.noInternet();
		} else {
			Set<String> apis = requests.keySet();
			for (final String api : apis) {
				new Thread() {
					@Override
					public void run() {
						super.run();
						try {
							JSONObject data = HttpTools.sendForJSONObject(MCTools.domain
									+ api, requests.get(api),HttpTools.SEND_POST);
							JSONObject jo = new JSONObject("{"+api+":"+data.toString()+"}");
							httpStatusListener.getJSONSuccess(jo);
						} catch (IOException e) {
							e.printStackTrace();
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}
				}.start();
			}
		}
	}

	public static void saveAccount(Activity activity, Account account) {
		try {
			OutputStream os = activity.openFileOutput("account",
					Context.MODE_PRIVATE);
			ObjectOutputStream oos = new ObjectOutputStream(os);
			oos.writeObject(account);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
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
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (StreamCorruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return account;
	}

}
