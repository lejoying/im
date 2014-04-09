package com.lejoying.wxgs.activity.utils;

import java.io.InputStream;
import java.net.HttpURLConnection;

import org.json.JSONException;
import org.json.JSONObject;

import com.lejoying.wxgs.R;
import com.lejoying.wxgs.activity.view.widget.Alert;
import com.lejoying.wxgs.app.MainApplication;
import com.lejoying.wxgs.app.handler.NetworkHandler.NetConnection;
import com.lejoying.wxgs.app.parser.StreamParser;
import com.lejoying.wxgs.utils.NetworkUtils;

public abstract class CommonNetConnection extends NetConnection {

	MainApplication app = MainApplication.getMainApplication();

	public abstract void success(JSONObject jData);

	@Override
	protected void success(InputStream is, HttpURLConnection httpURLConnection) {
		JSONObject jData = StreamParser.parseToJSONObject(is);
		httpURLConnection.disconnect();
		if (jData == null) {
			return;
		}
		try {
			jData.getString(app.getString(R.string.network_failed));
			unSuccess(jData);
			return;
		} catch (Exception e) {
		}
		success(jData);
	}

	@Override
	protected void failed(int failedType, int responseCode) {
		super.failed(failedType, responseCode);
		if (!NetworkUtils.hasNetwork(app)) {
			Alert.showMessage(app.getString(R.string.alert_text_nointernet));
		} else if (failedType == FAILED_TIMEOUT) {
			Alert.showMessage(app.getString(R.string.alert_text_nettimeout));
		} else {
			Alert.showMessage(app.getString(R.string.alert_text_neterror));
		}
		failed(failedType);
	}

	protected void failed(int failedType) {
		// TODO Auto-generated method stub

	}

	protected void unSuccess(JSONObject jData) {
		try {
			Alert.showMessage(jData.getString(app
					.getString(R.string.network_failed)));
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
