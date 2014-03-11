package com.lejoying.wxgs.app.service;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.HashMap;
import java.util.Map;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.lejoying.wxgs.app.MainApplication;
import com.lejoying.wxgs.app.data.API;
import com.lejoying.wxgs.app.handler.NetworkHandler;
import com.lejoying.wxgs.app.handler.NetworkHandler.NetConnection;
import com.lejoying.wxgs.app.handler.NetworkHandler.Settings;

public class PushService extends Service {

	static PushService mPushService;

	MainApplication app;

	NetworkHandler mPushHandler;

	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		mPushService = this;
		mPushHandler = new NetworkHandler(2);
		super.onCreate();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		startIMLongPull();
		startSquareLongPull();
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		mPushService = null;
		super.onDestroy();
	}

	boolean isConnected = false;
	static NetConnection mIMLongPullConnection;
	static NetConnection mSquareLongPullConnection;

	public static void startIMLongPull(final String phone,
			final String accessKey) {
		if (mPushService != null) {
			if (!mPushService.isConnected) {
				mPushService.isConnected = true;
				mIMLongPullConnection = createIMNetConnection(phone, accessKey);
				mPushService.startIMLongPull();
			}
		} else {
			mIMLongPullConnection = createIMNetConnection(phone, accessKey);
			MainApplication.getMainApplication().startService(
					new Intent(MainApplication.getMainApplication(),
							PushService.class));
		}
	}

	static NetConnection createIMNetConnection(final String phone,
			final String accessKey) {
		NetConnection netConnection = new NetConnection() {

			@Override
			public void settings(Settings settings) {
				settings.url = API.DOMAIN + API.SESSION_EVENT;
				settings.timeout = 30000;
				Map<String, String> params = new HashMap<String, String>();
				params.put("phone", phone);
				params.put("accessKey", accessKey);
				settings.params = params;
			}

			@Override
			public void success(InputStream is,
					HttpURLConnection httpURLConnection) throws IOException {
				// TODO Auto-generated method stub

			}

			@Override
			public void failed(int responseCode) {
				// TODO Auto-generated method stub
				super.failed(responseCode);
			}

			@Override
			public void error() {
				// TODO Auto-generated method stub
				super.error();
			}

			@Override
			public void timeout() {
				// TODO Auto-generated method stub
				super.timeout();
			}

			@Override
			public boolean circulatingDo() {
				// TODO Auto-generated method stub
				return true;
			}
		};
		return netConnection;
	}

	public static void startSquareLongPull(String phone, String accessKey,
			String gid, String flag) {

	}

	static NetConnection createSquareNetConnection(final String phone,
			final String accessKey, final String gid, final String flag) {
		NetConnection netConnection = new NetConnection() {

			@Override
			public void settings(Settings settings) {
				settings.url = API.DOMAIN + API.SESSION_EVENT;
				settings.timeout = 30000;
				Map<String, String> params = new HashMap<String, String>();
				params.put("phone", phone);
				params.put("accessKey", accessKey);
				params.put("gid", gid);
				params.put("flag", flag);
				settings.params = params;
			}

			@Override
			public void success(InputStream is,
					HttpURLConnection httpURLConnection) throws IOException {
				// TODO Auto-generated method stub

			}

			@Override
			public boolean circulatingDo() {
				// TODO Auto-generated method stub
				return true;
			}
		};
		return netConnection;
	}

	public void startIMLongPull() {
		if (mIMLongPullConnection != null) {
			mPushHandler.connection(mIMLongPullConnection);
		}
	}

	public void startSquareLongPull() {
		if (mSquareLongPullConnection != null) {
			mPushHandler.connection(mSquareLongPullConnection);
		}
	}
}
