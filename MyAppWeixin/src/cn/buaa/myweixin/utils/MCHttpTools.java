package cn.buaa.myweixin.utils;

import java.io.IOException;
import java.util.Date;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import cn.buaa.myweixin.Login;

import android.app.Activity;
import android.os.Handler;
import android.widget.Toast;

public class MCHttpTools {

	public static final int NETWORK_NOINTERNET = 0x091;
	public static final int NETWORK_SUCCESS = 0x092;

	private static Map<String, String> lastParam;
	private static String lastUrl;
	private static long time;

	public static void postForJSON(Activity activity, final String url,
			final Map<String, String> param, boolean lock,
			final HttpStatusListener httpStatusListener) {
		postForJSON(activity, url, param, lock, false, httpStatusListener);
	}

	public static void postForJSON(Activity activity, final String url,
			final Map<String, String> param, boolean lock, boolean getLocation,
			final HttpStatusListener httpStatusListener) {
		if (new Date().getTime() - time < 500) {
			httpStatusListener.shortIntervalTime();
			return;
		}
		if (lock) {
			if (url.equals(lastUrl) && param.equals(lastParam)
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
			if (getLocation) {
				double[] location = LocationTools.getLocation(activity);
				double longitude = location[0];
				double latitude = location[1];
				param.put("longitude", String.valueOf(longitude));
				param.put("latitude", String.valueOf(latitude));
			}
			new Thread() {
				@Override
				public void run() {
					super.run();
					try {
						byte[] data = HttpTools.sendPost(url, param);
						JSONObject jo = new JSONObject(new String(data));
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

	public interface HttpStatusListener {
		public void shortIntervalTime();

		public void noInternet();

		public void getJSONSuccess(JSONObject data);
	}
}

class HttpStatusAdapter implements MCHttpTools.HttpStatusListener {

	@Override
	public void shortIntervalTime() {
		// TODO Auto-generated method stub

	}

	@Override
	public void noInternet() {
		// TODO Auto-generated method stub

	}

	@Override
	public void getJSONSuccess(JSONObject data) {
		// TODO Auto-generated method stub

	}
}
