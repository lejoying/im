package com.lejoying.wxgs.app.handler;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

import com.lejoying.wxgs.utils.HttpUtils;
import com.lejoying.wxgs.utils.HttpUtils.Callback;

public class NetworkHandler {

	public static final int WORKTHREADCOUNT_MIN = 1;
	public static final int WORKTHREADCOUNT_MAX = 10;

	public int mWorkThreadCount;

	Queue<NetConnection> mNetConnections;

	Map<String, HttpURLConnection> mConnections;

	public NetworkHandler(int connectionCount) {
		mNetConnections = new LinkedList<NetConnection>();
		mConnections = new Hashtable<String, HttpURLConnection>();
		if (connectionCount > WORKTHREADCOUNT_MAX) {
			mWorkThreadCount = WORKTHREADCOUNT_MAX;
		} else if (connectionCount < WORKTHREADCOUNT_MIN) {
			mWorkThreadCount = WORKTHREADCOUNT_MIN;
		} else {
			mWorkThreadCount = connectionCount;
		}
		for (int i = 0; i < mWorkThreadCount; i++) {
			new NetworkHandlerWorkThread(i).start();
		}
	}

	public synchronized void connection(NetConnection netConnection) {
		mNetConnections.offer(netConnection);
		notify();
	}

	public void disConnection(String url) {
		if (url != null && !url.equals("")) {
			HttpURLConnection connection = mConnections.get(url);
			if (connection != null) {
				connection.disconnect();
			}
		}
	}

	public void disConnection(NetConnection netConnection) {
		if (netConnection != null) {
			netConnection.circulatingDo = false;
			disConnection(netConnection.settings.url);
		}
	}

	public synchronized NetConnection getExclude() throws InterruptedException {
		if (mNetConnections.size() == 0) {
			wait();
		}
		return mNetConnections.poll();
	}

	public static class Settings {
		public String url;
		public Map<String, String> params;
		public int timeout = 5000;
		public int method = HttpUtils.SEND_POST;
		public boolean disConnectionSameUrl = true;
	}

	public static abstract class NetConnection implements Callback, Runnable {

		Settings settings = new Settings();
		boolean circulatingDo;

		public abstract void settings(Settings settings);

		public abstract void success(InputStream is,
				HttpURLConnection httpURLConnection) throws IOException;

		@Override
		public void connectionCreated(HttpURLConnection httpURLConnection) {
			// TODO Auto-generated method stub

		}

		public boolean circulatingDo() {
			// TODO Auto-generated method stub

			return false;
		}

		@Override
		public void timeout() {
			// TODO Auto-generated method stub

		}

		@Override
		public void failed(int responseCode) {
			// TODO Auto-generated method stub

		}

		@Override
		public void error() {
			// TODO Auto-generated method stub

		}

		@Override
		public final void run() {
		}
	}

	class NetworkHandlerWorkThread extends Thread {

		public int id;

		boolean interrupt;

		public NetworkHandlerWorkThread(int id) {
			this.id = id;
		}

		@Override
		public void run() {
			while (!interrupt) {
				NetConnection netConnection = null;
				try {
					while ((netConnection = getExclude()) == null)
						;
				} catch (InterruptedException e) {
					return;
				}
				netConnection.settings(netConnection.settings);
				if (netConnection.settings.url != null
						&& !netConnection.settings.url.equals("")) {
					if (netConnection.settings.disConnectionSameUrl) {
						HttpURLConnection connection = mConnections
								.get(netConnection.settings.url);
						if (connection != null) {
							connection.disconnect();
						}
					}
					netConnection.circulatingDo = netConnection.circulatingDo();
					if (!netConnection.circulatingDo) {
						HttpUtils.connection(netConnection.settings.url,
								netConnection.settings.method,
								netConnection.settings.timeout,
								netConnection.settings.params, netConnection);
					} else {
						while (netConnection.circulatingDo) {
							HttpUtils.connection(netConnection.settings.url,
									netConnection.settings.method,
									netConnection.settings.timeout,
									netConnection.settings.params,
									netConnection);
						}
					}
				}
			}
		}
	}

}
