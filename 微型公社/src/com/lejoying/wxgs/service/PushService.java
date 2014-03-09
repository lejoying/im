package com.lejoying.wxgs.service;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.lejoying.wxgs.activity.LoginActivity;
import com.lejoying.wxgs.app.MainApplication;
import com.lejoying.wxgs.utils.HttpUtils;
import com.lejoying.wxgs.utils.HttpUtils.Callback;
import com.lejoying.wxgs.utils.StreamUtils;

public class PushService extends Service {

	MainApplication app;

	LongConnectionThread mLongConnectionThread;
	HandleConnectionResultThread mHandleStreamThread;

	Queue<LongConnectionResponse> mStreamQueue;

	static Map<String, String> mLongConnectionParams;
	static PushService mPushService;

	public static boolean isConnected;

	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onCreate() {
		mPushService = this;
		callback = new LongConnectionCallback();
		mLongConnectionParams = new HashMap<String, String>();
		app = MainApplication.getMainApplication();
		mLongConnectionThread = new LongConnectionThread();
		mHandleStreamThread = new HandleConnectionResultThread();
		mStreamQueue = new LinkedList<LongConnectionResponse>();
		mLongConnectionParams = new Hashtable<String, String>();
		mLongConnectionThread.start();
		mHandleStreamThread.start();
		super.onCreate();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		if (intent != null) {
			String phone = intent.getStringExtra("phone");
			String accessKey = intent.getStringExtra("accessKey");
			if (phone != null && !phone.equals("") && accessKey != null
					&& !accessKey.equals("")) {
				startLongConnection(phone, accessKey);
			}
		}
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		mPushService = null;
		super.onDestroy();
	}

	public void handleResponse(LongConnectionResponse response) {
		mStreamQueue.offer(response);
		synchronized (mHandleStreamThread) {
			mHandleStreamThread.notify();
		}
	}

	public LongConnectionResponse getResponse() throws InterruptedException {
		if (mStreamQueue.size() == 0) {
			synchronized (mHandleStreamThread) {
				mHandleStreamThread.wait();
			}
		}
		return mStreamQueue.poll();
	}

	public void waitForConnection() throws InterruptedException {
		synchronized (mLongConnectionThread) {
			mLongConnectionThread.wait();
		}
	}

	public static void startLongConnection(String phone, String accessKey) {
		if (!isConnected) {
			isConnected = true;
			mLongConnectionParams.put("phone", phone);
			mLongConnectionParams.put("accessKey", accessKey);
			if (mPushService != null) {
				synchronized (mPushService.mLongConnectionThread) {
					System.out.println("notify");
					mPushService.mLongConnectionThread.notify();
				}
			} else {
				Intent service = new Intent(MainApplication.currentActivity,
						PushService.class);
				service.putExtra("phone", phone);
				service.putExtra("accessKey", accessKey);
				MainApplication.currentActivity.startActivity(service);
			}
		}
	}

	public static void stopLongConnection() {
		if (mPushService != null && isConnected) {
			isConnected = false;
			mLongConnectionParams.clear();
			if (mPushService.mCurrentConnection != null) {
				mPushService.mCurrentConnection.disconnect();
			}
		}
	}

	public class LongConnectionResponse {
		InputStream is;
		HttpURLConnection httpURLConnection;

		public LongConnectionResponse() {
			// TODO Auto-generated constructor stub
		}

		public LongConnectionResponse(InputStream is,
				HttpURLConnection httpURLConnection) {
			this.is = is;
			this.httpURLConnection = httpURLConnection;
		}
	}

	public final class HandleConnectionResultThread extends Thread {
		boolean interrupt = false;

		@Override
		public void run() {
			while (!interrupt) {
				LongConnectionResponse response = null;
				try {
					while ((response = getResponse()) == null
							|| response.is != null
							|| response.httpURLConnection != null)
						;
				} catch (InterruptedException e) {
					return;
				}

				String s = new String(
						StreamUtils.getByteArrayFromInputStream(response.is));

				System.out.println(s);

				response.httpURLConnection.disconnect();

			}
		}
	}

	LongConnectionCallback callback;
	HttpURLConnection mCurrentConnection;

	public class LongConnectionCallback implements Callback {

		@Override
		public void success(InputStream is, HttpURLConnection httpURLConnection) {
			// TODO Auto-generated method stub
			System.out.println("success");
			handleResponse(new LongConnectionResponse(is, httpURLConnection));
		}

		@Override
		public void error() {
			if (!isConnected) {
				MainApplication.currentActivity.startActivity(new Intent(
						MainApplication.currentActivity, LoginActivity.class));
			} else {
				try {
					Thread.sleep(2000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			System.out.println("error");
		}

		@Override
		public void timeout() {
			// TODO Auto-generated method stub
			System.out.println("timeout");
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		@Override
		public void connectionCreated(HttpURLConnection httpURLConnection) {
			mCurrentConnection = httpURLConnection;
			System.out.println("connection");
		}

	}

	public final class LongConnectionThread extends Thread {
		boolean interrupt = false;

		@Override
		public void run() {
			while (!interrupt) {
				if (isConnected) {
					System.out.println("1");
					HttpUtils.connection("http://qq", HttpUtils.SEND_POST,
							30000, mLongConnectionParams, callback);
				} else {
					System.out.println("2");
					try {
						waitForConnection();
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}
	}

}
