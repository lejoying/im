package com.lejoying.wxgs.app.handler;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

public class NetworkHandler {

	public static final int WORKTHREADCOUNT_MIN = 1;
	public static final int WORKTHREADCOUNT_MAX = 10;
	public int mWorkThreadCount;

	Queue<NetConnection> mNetConnections;

	public NetworkHandler(int connectionCount) {
		mNetConnections = new LinkedList<NetConnection>();
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
		public int method = NetConnection.POST;
		public boolean circulatingDo = false;
	}

	public static abstract class NetConnection {

		Settings settings = new Settings();

		public static final int GET = 0xff01;
		public static final int POST = 0xff02;

		public static final int RESPONSECODE_DEFAULT = 0;
		public static final int FAILED_TIMEOUT = 1;
		public static final int FAILED_WRONGCODE = 2;
		public static final int FAILED_ERROR = 3;

		HttpURLConnection httpURLConnection;

		boolean isDisconnected = false;

		protected abstract void settings(Settings settings);

		protected abstract void success(InputStream is,
				HttpURLConnection httpURLConnection);

		protected void failed(int failedType, int responseCode) {
			// TODO Auto-generated method stub
		}

		protected void connectionCreated(HttpURLConnection httpURLConnection) {
			// TODO Auto-generated method stub
		}

		public void cancelCirculatingDo(boolean disconnectionImmediately) {
			if (settings.circulatingDo) {
				settings.circulatingDo = false;
			}
			if (disconnectionImmediately) {
				disConnection();
			}
		}

		public void disConnection() {
			isDisconnected = true;
			if (httpURLConnection != null) {
				httpURLConnection.disconnect();
				httpURLConnection = null;
			}
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
				try {
					NetConnection netConnection;
					while ((netConnection = getExclude()) == null)
						;
					if (!netConnection.isDisconnected) {
						netConnection.settings(netConnection.settings);
						if (netConnection.settings.url != null
								&& !netConnection.settings.url.equals("")) {
							if (!netConnection.settings.circulatingDo) {
								startConnection(netConnection);
							} else {
								while (netConnection.settings.circulatingDo) {
									startConnection(netConnection);
								}
							}
						}
					}
				} catch (InterruptedException e) {
					return;
				}
			}
		}
	}

	public void startConnection(NetConnection connection) {
		String url = connection.settings.url;
		int method = connection.settings.method;
		int timeout = connection.settings.timeout;
		Map<String, String> params = connection.settings.params;

		HttpURLConnection httpURLConnection = null;
		URL connectionURL = null;
		try {
			switch (method) {
			case NetConnection.GET:
				StringBuffer getPath = new StringBuffer(url);
				if (params != null) {
					Set<String> keys = params.keySet();
					if (keys != null) {
						getPath.append("?");
						for (String key : keys) {
							getPath.append(key + "=" + params.get(key) + "&");
						}
						if (getPath.length() != 0) {
							getPath.delete(getPath.length() - 1,
									getPath.length());
						}
					}
				}
				connectionURL = new URL(getPath.toString());
				httpURLConnection = (HttpURLConnection) connectionURL
						.openConnection();
				httpURLConnection.setRequestMethod("GET");

				// httpURLConnection.setReadTimeout(timeout);
				httpURLConnection.setConnectTimeout(timeout);
				break;
			case NetConnection.POST:
				StringBuffer paramData = new StringBuffer();
				if (params != null) {
					Set<String> keys = params.keySet();
					if (keys != null) {
						for (String key : keys) {
							paramData.append(key
									+ "="
									+ URLEncoder.encode(params.get(key),
											"UTF-8") + "&");
						}
						if (paramData.length() != 0) {
							paramData.delete(paramData.length() - 1,
									paramData.length());
						}
					}
				} else {
					paramData.append(" ");
				}
				connectionURL = new URL(url);
				httpURLConnection = (HttpURLConnection) connectionURL
						.openConnection();
				httpURLConnection.setRequestMethod("POST");

				httpURLConnection.setReadTimeout(timeout);
				httpURLConnection.setConnectTimeout(timeout);

				httpURLConnection.setDoOutput(true);
				httpURLConnection.setRequestProperty("Content-Type",
						"application/x-www-form-urlencoded");
				httpURLConnection.setRequestProperty("Content-Length",
						paramData.length() + "");
				OutputStream os = httpURLConnection.getOutputStream();
				byte buffer[] = paramData.toString().getBytes();
				os.write(buffer);
				os.flush();
				os.close();
				break;
			}
			connection.httpURLConnection = httpURLConnection;
			connection.connectionCreated(httpURLConnection);

			if (!connection.isDisconnected) {
				InputStream is = httpURLConnection.getInputStream();

				int requestCode = httpURLConnection.getResponseCode();
				if (requestCode == HttpURLConnection.HTTP_OK) {
					connection.success(is, httpURLConnection);
				} else {
					connection.failed(NetConnection.FAILED_WRONGCODE,
							requestCode);
					if (httpURLConnection != null) {
						httpURLConnection.disconnect();
					}
				}
			} else {
				connection.isDisconnected = false;
			}
		} catch (SocketTimeoutException e) {
			// TODO: handle exception
			connection.failed(NetConnection.FAILED_TIMEOUT,
					NetConnection.RESPONSECODE_DEFAULT);
			if (httpURLConnection != null) {
				httpURLConnection.disconnect();
			}
		} catch (IOException e) {
			connection.failed(NetConnection.FAILED_ERROR,
					NetConnection.RESPONSECODE_DEFAULT);
			if (httpURLConnection != null) {
				httpURLConnection.disconnect();
			}
		}
	}
}
