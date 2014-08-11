package com.open.welinks.utils;

import java.io.InputStream;
import java.net.HttpURLConnection;

import org.json.JSONException;
import org.json.JSONObject;

import com.open.welinks.utils.NetworkHandler.NetConnection;



public abstract class CommonNetConnection extends NetConnection {

//	MainApplication app = MainApplication.getMainApplication();

	public abstract void success(JSONObject jData);

	@Override
	protected void success(InputStream is, HttpURLConnection httpURLConnection) {
		JSONObject jData = StreamParser.parseToJSONObject(is);
		httpURLConnection.disconnect();
		if (jData == null) {
			return;
		}
		try {
			jData.getString("失败原因");
			unSuccess(jData);
			return;
		} catch (Exception e) {
		}
		success(jData);
	}

	@Override
	protected void failed(int failedType, int responseCode) {
		super.failed(failedType, responseCode);
		// if (!NetworkUtils.hasNetwork(app)) {
		// Alert.showMessage(app.getString(R.string.alert_text_nointernet));
		// } else if (failedType == FAILED_TIMEOUT) {
		// Alert.showMessage(app.getString(R.string.alert_text_nettimeout));
		// } else {
		// Alert.showMessage(app.getString(R.string.alert_text_neterror));
		// }
		failed(failedType);
	}

	protected void failed(int failedType) {
		// TODO Auto-generated method stub
	}

	protected void unSuccess(JSONObject jData) {
	}
}
