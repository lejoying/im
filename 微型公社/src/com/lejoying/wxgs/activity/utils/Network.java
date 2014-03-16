package com.lejoying.wxgs.activity.utils;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.HashMap;
import java.util.Map;

import com.lejoying.wxgs.app.MainApplication;
import com.lejoying.wxgs.app.data.API;
import com.lejoying.wxgs.app.handler.NetworkHandler.NetConnection;
import com.lejoying.wxgs.app.handler.NetworkHandler.Settings;
import com.lejoying.wxgs.app.parser.StreamParser;

public class Network {

	public interface NetListener {
		public void success();

		public void unSuccess();

		public void failed(int failedType);
	}

	public static class CommonNetConnection extends NetConnection {

		@Override
		protected void settings(Settings settings) {
			// TODO Auto-generated method stub

		}

		@Override
		protected void success(InputStream is,
				HttpURLConnection httpURLConnection) {
			// TODO Auto-generated method stub

		}
	};

	public static void getUser(final MainApplication app,
			final NetListener listener) {
		NetConnection netConnection = new NetConnection() {

			@Override
			protected void success(InputStream is,
					HttpURLConnection httpURLConnection) {
				System.out.println(StreamParser.parseToJSONObject(is)
						.toString());
			}

			@Override
			protected void settings(Settings settings) {
				settings.url = API.DOMAIN + API.ACCOUNT_GET;
				Map<String, String> params = new HashMap<String, String>();
				params.put("phone", app.data.user.phone);
				params.put("accessKey", app.data.user.accessKey);
				params.put("target", "[\"" + app.data.user.phone + "\"]");
				settings.params = params;
			}
		};
		app.networkHandler.connection(netConnection);
	}
}
