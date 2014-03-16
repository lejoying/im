package com.lejoying.wxgs.activity.utils;

import java.io.InputStream;
import java.net.HttpURLConnection;

import org.json.JSONObject;

import com.lejoying.wxgs.R;
import com.lejoying.wxgs.activity.view.widget.Alert;
import com.lejoying.wxgs.app.MainApplication;
import com.lejoying.wxgs.app.handler.NetworkHandler.NetConnection;
import com.lejoying.wxgs.app.parser.StreamParser;
import com.lejoying.wxgs.utils.NetworkUtils;

public abstract class CommonNetConnection extends NetConnection {

	MainApplication app = MainApplication.getMainApplication();

	protected abstract void success(JSONObject jData);

	protected abstract void failed();

	@Override
	protected void success(InputStream is, HttpURLConnection httpURLConnection) {
		JSONObject jData = StreamParser.parseToJSONObject(is);
		httpURLConnection.disconnect();
		if (jData == null) {
			return;
		}
		try {
			Alert.showMessage(jData.getString(app
					.getString(R.string.network_failed)));
			failed();
			return;
		} catch (Exception e) {
		}
		success(jData);
	}

	@Override
	protected void failed(int failedType, int responseCode) {
		if (!NetworkUtils.hasNetwork(app)) {
			Alert.showMessage(app.getString(R.string.alert_text_nointernet));
		} else if (failedType == FAILED_TIMEOUT) {
			Alert.showMessage(app.getString(R.string.alert_text_nettimeout));
		} else {
			Alert.showMessage(app.getString(R.string.alert_text_neterror));
		}
		failed();
	}

}
