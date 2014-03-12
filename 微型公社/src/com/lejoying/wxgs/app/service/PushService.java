package com.lejoying.wxgs.app.service;

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

	public static final int LONGPULLSTATE_CONNECTION = 1;
	public static final int LONGPULLSTATE_WAITFORCONNECTION = 2;

	int mStatus;

	static PushService mPushService;

	MainApplication app = MainApplication.getMainApplication();;

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
		mStatus = LONGPULLSTATE_WAITFORCONNECTION;
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
		mIMLongPullConnection = null;
		mSquareLongPullConnection = null;
		super.onDestroy();
	}

	static NetConnection mIMLongPullConnection;
	static NetConnection mSquareLongPullConnection;

	public synchronized static void startIMLongPull(final String phone,
			final String accessKey) {
		if (mPushService != null
				&& mPushService.mStatus == LONGPULLSTATE_WAITFORCONNECTION) {
			mIMLongPullConnection = createIMNetConnection(phone, accessKey);
			mPushService.startIMLongPull();
		} else if (mPushService == null) {
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
				settings.circulating = true;
			}

			@Override
			public void success(InputStream is,
					HttpURLConnection httpURLConnection) {
				// TODO Auto-generated method stub

			}

		};
		return netConnection;
	}

	public synchronized static void startSquareLongPull(String phone,
			String accessKey, String gid, String flag) {
		if (mPushService != null) {
			mSquareLongPullConnection = createSquareNetConnection(phone,
					accessKey, gid, flag);
			mPushService.startSquareLongPull();
		} else {
			mSquareLongPullConnection = createSquareNetConnection(phone,
					accessKey, gid, flag);
			MainApplication.getMainApplication().startService(
					new Intent(MainApplication.getMainApplication(),
							PushService.class));
		}
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
				settings.circulating = true;
			}

			@Override
			public void success(InputStream is,
					HttpURLConnection httpURLConnection) {
				// TODO Auto-generated method stub
			}

		};
		return netConnection;
	}

	public synchronized void startIMLongPull() {
		if (mIMLongPullConnection != null) {
			mPushHandler.connection(mIMLongPullConnection);
		}
	}

	public synchronized void startSquareLongPull() {
		if (mSquareLongPullConnection != null) {
			mPushHandler.connection(mSquareLongPullConnection);
		}
	}
}
