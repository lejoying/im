package cn.buaa.myweixin.utils;

import java.io.IOException;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.os.Handler;
import android.widget.Toast;

public class MCHttpTools {

	public static final int NETWORK_NOINTERNET = 0x091;
	public static final int NETWORK_SUCCESS = 0x092;

	public static void postForJSON(Activity activity, final String url,
			final Map<String, String> param, Handler handler,
			final HttpStatusListener httpStatusListener) {
		boolean hasNetwork = HttpTools.hasNetwork(activity);
		if (!hasNetwork) {
			httpStatusListener.noInternet();
		} else {
			new Thread() {
				@Override
				public void run() {
					// TODO Auto-generated method stub
					super.run();
					try {
						byte[] data = HttpTools.sendPost(url, param);
						JSONObject jo = new JSONObject(new String(data));
						httpStatusListener.getJSONSuccess(jo);
					} catch (IOException e) {
						e.printStackTrace();
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}.start();
		}
	}

	public interface HttpStatusListener {
		public void noInternet();
		public void getJSONSuccess(JSONObject data);
	}

	public class HttpStatusAdapter implements HttpStatusListener {
		@Override
		public void noInternet() {
		}
		@Override
		public void getJSONSuccess(JSONObject data) {
		}

	}
}
