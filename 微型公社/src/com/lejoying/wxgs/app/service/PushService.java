package com.lejoying.wxgs.app.service;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.widget.Toast;

import com.lejoying.wxgs.R;
import com.lejoying.wxgs.activity.MainActivity;
import com.lejoying.wxgs.activity.utils.DataUtil;
import com.lejoying.wxgs.app.MainApplication;
import com.lejoying.wxgs.app.data.API;
import com.lejoying.wxgs.app.data.Data;
import com.lejoying.wxgs.app.data.entity.Event;
import com.lejoying.wxgs.app.data.entity.Message;
import com.lejoying.wxgs.app.handler.DataHandler.Modification;
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
	String mCurrentFlag = "0";

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
				mCurrentFlag = flag;
				startSquareConnection(gid);
			}
			String operation = intent.getStringExtra("operation");

			if (operation != null && operation.equals("stop")) {
				stopLongPull();
			}
		}
		notifyWaitingForConnection();
		return super.onStartCommand(intent, flags, startId);
	}

	public void stopLongPull() {
		app.data.user.accessKey = "";
		DataUtil.saveData(this);
		isConnection = false;
		if (mIMConnection != null) {
			mIMConnection.disConnection();
			mIMConnection = null;
		}
		if (mSquareConnection != null) {
			mSquareConnection.disConnection();
			mSquareConnection = null;
		}
		sendBroadcast(new Intent(LONGPULL_FAILED));
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

	void startSquareConnection(String gid) {
		if (!mIMConnection.isDisconnected()
				&& (mSquareConnection == null || mSquareConnection
						.isDisconnected())) {
			mPushHandler
					.connection(mSquareConnection = createSquareNetConnection(gid));
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
						JSONObject jData = StreamParser.parseToJSONObject(is);
						httpURLConnection.disconnect();
						if (jData != null) {
							try {
								System.out
										.println(jData
												.get(getString(R.string.network_failed)));

								// disconnection long pull
								stopLongPull();
								return;
							} catch (JSONException e) {
								e.printStackTrace();
							}
							Event event = JSONParser
									.generateEventFromJSON(jData);
							app.eventHandler.handleEvent(event);
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

	NetConnection createSquareNetConnection(final String gid) {
		NetConnection netConnection = new NetConnection() {
			@Override
			public void settings(Settings settings) {
				settings.url = API.DOMAIN + API.SQUARE_GETSQUAREMESSAGE;
				settings.timeout = 30000;
				Map<String, String> params = new HashMap<String, String>();
				params.put("phone", app.data.user.phone);
				params.put("accessKey", app.data.user.accessKey);
				params.put("gid", gid);
				params.put("flag", mCurrentFlag);
				settings.params = params;
				settings.circulating = true;
			}

			@Override
			public void success(InputStream is,
					final HttpURLConnection httpURLConnection) {
				final JSONObject jData = StreamParser.parseToJSONObject(is);
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
					try {
						mCurrentFlag = jData.getString("flag");
					} catch (JSONException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					reSettings();
					app.dataHandler.exclude(new Modification() {
						@Override
						public void modifyData(Data data) {
							try {
								if (data.squareMessages
										.get(mCurrentConnectionGid) == null) {
									data.squareMessages.put(
											mCurrentConnectionGid,
											new ArrayList<Message>());
								}

								data.squareFlags.put(mCurrentConnectionGid,
										jData.getString("flag"));

								List<Message> squareMessages = data.squareMessages
										.get(mCurrentConnectionGid);
								List<Message> newMessages = JSONParser
										.generateMessagesFromJSON(jData
												.getJSONArray("messages"));
								for (Message message : newMessages) {
									if (!squareMessages.contains(message)) {
										squareMessages.add(message);
									}
								}
							} catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}

						@Override
						public void modifyUI() {
							if (MainActivity.instance != null
									&& MainActivity.instance.mode
											.equals(MainActivity.MODE_MAIN)) {
								if (MainActivity.instance.mMainMode.mSquareFragment
										.isAdded()) {
									MainActivity.instance.mMainMode.mSquareFragment.mAdapter
											.notifyDataSetChanged();
								}
							}
						}
					});
				}

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
