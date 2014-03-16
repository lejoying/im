package com.lejoying.wxgs.app.service;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.widget.Toast;

import com.lejoying.wxgs.R;
import com.lejoying.wxgs.app.MainApplication;
import com.lejoying.wxgs.app.data.API;
import com.lejoying.wxgs.app.data.entity.Event;
import com.lejoying.wxgs.app.handler.NetworkHandler;
import com.lejoying.wxgs.app.handler.NetworkHandler.NetConnection;
import com.lejoying.wxgs.app.handler.NetworkHandler.Response;
import com.lejoying.wxgs.app.handler.NetworkHandler.ResponseHandler;
import com.lejoying.wxgs.app.handler.NetworkHandler.Settings;
import com.lejoying.wxgs.app.parser.JSONParser;
import com.lejoying.wxgs.app.parser.StreamParser;
import com.lejoying.wxgs.utils.NetworkUtils;

public class PushService extends Service {

	public static final String LONGPULL_FAILED = "com.lejoying.wxgs.app.service.longpull_failed";

	MainApplication app = MainApplication.getMainApplication();;

	NetworkHandler mPushHandler;
	ResponseHandler mResponseHandler;

	String mCurrentConnectionGid = "";

	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		mPushHandler = new NetworkHandler(2);
		mResponseHandler = new ResponseHandler(2);
		super.onCreate();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		if (intent != null) {
			startIMConnection();
			String gid = intent.getStringExtra("gid");
			String flag = intent.getStringExtra("flag");
			if (gid != null && !gid.equals("") && flag != null
					&& !flag.equals("")) {
				if (!mCurrentConnectionGid.equals(gid)) {
					if (mSquareConnection != null
							&& !mSquareConnection.isDisconnected()) {
						mSquareConnection.disConnection();
					}
				}
				mCurrentConnectionGid = gid;
				startSquareConnection(gid, flag);
			}
		}
		notifyWaitingForConnection();
		return super.onStartCommand(intent, flags, startId);
	}

	public static void startIMLongPull(Context context) {
		context.startService(new Intent(context, PushService.class));
	}

	public static void startSquareLongPull(Context context, String gid,
			String flag) {
		if (gid != null && !gid.equals("") && flag != null && !flag.equals("")) {
			Intent service = new Intent(context, PushService.class);
			service.putExtra("gid", gid);
			service.putExtra("flag", flag);
			context.startService(service);
		}
	}

	NetConnection mIMConnection;
	NetConnection mSquareConnection;
	boolean isConnection;

	void startIMConnection() {
		if (app.data.user.phone.equals("")
				|| app.data.user.accessKey.equals("")) {
			return;
		}
		if (mIMConnection == null || mIMConnection.isDisconnected()) {
			isConnection = true;
			mPushHandler.connection(mIMConnection = createIMNetConnection());
		}
	}

	void startSquareConnection(String gid, String flag) {
		if (!mIMConnection.isDisconnected()
				&& (mSquareConnection == null || mSquareConnection
						.isDisconnected())) {
			mPushHandler
					.connection(mSquareConnection = createSquareNetConnection(
							gid, flag));
		}
	}

	NetConnection createIMNetConnection() {
		NetConnection netConnection = new NetConnection() {

			@Override
			public void settings(Settings settings) {
				settings.url = API.DOMAIN + API.SESSION_EVENT;
				settings.timeout = 30000;
				Map<String, String> params = new HashMap<String, String>();
				params.put("phone", app.data.user.phone);
				params.put("accessKey", app.data.user.accessKey);
				settings.params = params;
				settings.circulating = true;
			}

			@Override
			public void success(InputStream is,
					final HttpURLConnection httpURLConnection) {
				Response response = new Response(is) {
					@Override
					public void handleResponse(InputStream is) {
						JSONObject jObject = StreamParser.parseToJSONObject(is);
						httpURLConnection.disconnect();
						System.out.println("IMSuccess");
						if (jObject != null) {
							try {
								System.out
										.println(jObject
												.get(getString(R.string.network_failed)));

								// disconnection long pull
								isConnection = false;
								if (mIMConnection != null) {
									mIMConnection.disConnection();
									mIMConnection = null;
								}
								if (mSquareConnection != null) {
									mSquareConnection.disConnection();
									mSquareConnection = null;
								}
								app.data.user.accessKey = "";
								sendBroadcast(new Intent(LONGPULL_FAILED));
								return;
							} catch (JSONException e) {
								e.printStackTrace();
							}
							Event event = JSONParser
									.generateEventFromJSON(jObject);
							System.out.println(event);
						}
					}
				};
				mResponseHandler.exclude(response);
			}

			@Override
			protected void failed(int failedType, int responseCode) {
				switch (failedType) {
				case FAILED_TIMEOUT:
					break;
				default:
					synchronized (this) {
						try {
							if (!NetworkUtils.hasNetwork(PushService.this)) {
								waitForConnection();
							} else {
								wait(5000);
							}
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					break;
				}
			}

		};
		return netConnection;
	}

	NetConnection createSquareNetConnection(final String gid, final String flag) {
		NetConnection netConnection = new NetConnection() {
			@Override
			public void settings(Settings settings) {
				settings.url = API.DOMAIN + API.SQUARE_GETSQUAREMESSAGE;
				settings.timeout = 30000;
				Map<String, String> params = new HashMap<String, String>();
				params.put("phone", app.data.user.phone);
				params.put("accessKey", app.data.user.accessKey);
				params.put("gid", gid);
				params.put("flag", flag);
				settings.params = params;
				settings.circulating = true;
			}

			@Override
			public void success(InputStream is,
					final HttpURLConnection httpURLConnection) {
				Response response = new Response(is) {
					@Override
					public void handleResponse(InputStream is) {
						JSONObject jData = StreamParser.parseToJSONObject(is);
						httpURLConnection.disconnect();
						if (jData != null) {
							try {
								jData.get(getString(R.string.network_failed));
								// disconnection long pull
								Toast.makeText(PushService.this, "连接到广场失败",
										Toast.LENGTH_LONG).show();
								if (mSquareConnection != null) {
									mSquareConnection.disConnection();
									mSquareConnection = null;
								}
								return;
							} catch (JSONException e) {
								e.printStackTrace();
							}
							System.out.println(jData.toString());
						}
					}
				};
				mResponseHandler.exclude(response);
			}

			@Override
			protected void failed(int failedType, int responseCode) {
				switch (failedType) {
				case FAILED_TIMEOUT:
					break;
				default:
					synchronized (this) {
						try {
							if (!NetworkUtils.hasNetwork(PushService.this)) {
								waitForConnection();
							} else {
								wait(5000);
							}
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					break;
				}
			}

		};
		return netConnection;
	}

	public synchronized void waitForConnection() throws InterruptedException {
		wait();
	}

	public synchronized void notifyWaitingForConnection() {
		notifyAll();
	}

}
