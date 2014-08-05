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

import com.lejoying.wxgs.R;
import com.lejoying.wxgs.activity.MainActivity;
import com.lejoying.wxgs.activity.utils.DataUtil;
import com.lejoying.wxgs.app.MainApplication;
import com.lejoying.wxgs.app.data.API;
import com.lejoying.wxgs.app.data.Data;
import com.lejoying.wxgs.app.data.entity.Event;
import com.lejoying.wxgs.app.data.entity.SquareMessage;
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
		mPushHandler = new NetworkHandler(10);
		mResponseHandler = new ResponseHandler(2);
		super.onCreate();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		if (intent != null) {
			String operation = intent.getStringExtra("operation");
			if (operation != null && operation.equals("stop")) {
				stopLongPull();
			} else {
				startIMConnection();
				String gid = intent.getStringExtra("gid");
				String flag = intent.getStringExtra("flag");
				if (gid != null && !gid.equals("") && flag != null
						&& !flag.equals("")) {
					if (mSquareConnection != null) {
						mSquareConnection.disConnection();
					}
					mCurrentConnectionGid = gid;
					mCurrentFlag = flag;
					startSquareConnection(gid);
				}
			}
		}
		notifyWaitingForConnection();
		return super.onStartCommand(intent, flags, startId);
	}

	public void stopLongPull() {
		app.dataHandler.exclude(new Modification() {

			@Override
			public void modifyData(Data data) {
				data.user.accessKey = "";
			}

			@Override
			public void modifyUI() {
				DataUtil.saveData(PushService.this);
				isConnection = false;
				if (mIMConnection != null) {
					mIMConnection.disConnection();
					mIMConnection = null;
				}
				if (mSquareConnection != null) {
					mSquareConnection.disConnection();
					mSquareConnection = null;
				}
				notifyWaitingForConnection();
				sendBroadcast(new Intent(LONGPULL_FAILED));
			}
		});
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
			mIMConnection = createIMNetConnection();
			mPushHandler.connection(mIMConnection);
		}
	}

	void startSquareConnection(String gid) {
		if (mSquareConnection != null) {
			mSquareConnection.disConnection();
		}
		mSquareConnection = createSquareNetConnection(gid);
		mPushHandler.connection(mSquareConnection);

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
								// Alert.showMessage(jData
								// .getString(getString(R.string.network_failed)));
								jData.getString(getString(R.string.network_failed));
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
					try {
						if (!NetworkUtils.hasNetwork(PushService.this)) {
							waitForConnection();
						} else {
							waitForConnection(5000);
						}
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					break;
				}
			}

		};
		return netConnection;
	}

	NetConnection createSquareNetConnection(final String gid) {
		final Map<String, String> params = new HashMap<String, String>();
		params.put("phone", app.data.user.phone);
		params.put("accessKey", app.data.user.accessKey);
		params.put("gid", gid);
		params.put("flag", mCurrentFlag);
		NetConnection netConnection = new NetConnection() {
			@Override
			public void settings(Settings settings) {
				settings.url = API.DOMAIN + API.SQUARE_GETSQUAREMESSAGE;
				settings.timeout = 30000;
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
						// System.out.println("push-------"+getString(R.string.network_failed));
						jData.get(getString(R.string.network_failed));

						// disconnection long pull
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
						app.data.squareFlags.put(mCurrentConnectionGid,
								mCurrentFlag);
					} catch (JSONException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					params.put("flag", mCurrentFlag);
					reSetParams(params);
					app.dataHandler.exclude(new Modification() {
						@Override
						public void modifyData(Data data) {
							try {
								if (data.squareMessages
										.get(mCurrentConnectionGid) == null
										|| data.squareMessagesMap
												.get(mCurrentConnectionGid) == null) {
									data.squareMessages.put(
											mCurrentConnectionGid,
											new ArrayList<String>());
									data.squareMessagesClassify
											.put(mCurrentConnectionGid,
													new HashMap<String, List<String>>());
									data.squareMessagesMap
											.put(mCurrentConnectionGid,
													new HashMap<String, SquareMessage>());
									if (MainActivity.instance != null
											&& MainActivity.instance.mode
													.equals(MainActivity.MODE_MAIN)) {
										if (MainActivity.instance.mMainMode.mSquareFragment
												.isAdded()) {
											MainActivity.instance.mMainMode.mSquareFragment.squareContentView.justSetSquareMessageList(
													data.squareMessages
															.get(mCurrentConnectionGid),
													data.squareMessagesMap
															.get(mCurrentConnectionGid));
										}
									}

								}

								data.squareFlags.put(mCurrentConnectionGid,
										jData.getString("flag"));

								List<String> squareMessages = data.squareMessages
										.get(mCurrentConnectionGid);
								Map<String, List<String>> squareMessagesClassify = data.squareMessagesClassify
										.get(mCurrentConnectionGid);
								Map<String, SquareMessage> squareMessagesMap = data.squareMessagesMap
										.get(mCurrentConnectionGid);
								List<SquareMessage> newMessages = JSONParser
										.generateSquareMessagesFromJSON(jData
												.getJSONArray("messages"));
								for (SquareMessage message : newMessages) {
									if (!squareMessages.contains(message.gmid)) {
										squareMessages.add(message.gmid);
										squareMessagesMap.put(message.gmid,
												message);
									}
									for (int i = 0; i < message.messageTypes
											.size(); i++) {
										String messageType = message.messageTypes
												.get(i);
										if (squareMessagesClassify
												.get(messageType) == null) {
											List<String> list = new ArrayList<String>();
											list.add(message.gmid);
											squareMessagesClassify.put(
													messageType, list);
										} else {
											squareMessagesClassify.get(
													messageType).add(
													message.gmid);
										}
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
									MainActivity.instance.mMainMode.mSquareFragment
											.notifyViews();
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
					// try {
					// if (!NetworkUtils.hasNetwork(PushService.this)) {
					// waitForConnection();
					// } else {
					// waitForConnection(5000);
					// }
					// } catch (InterruptedException e) {
					// // TODO Auto-generated catch block
					// e.printStackTrace();
					// }
					break;
				}
			}

		};
		return netConnection;
	}

	public synchronized void waitForConnection() throws InterruptedException {
		wait();
	}

	public synchronized void waitForConnection(int time)
			throws InterruptedException {
		wait(time);
	}

	public synchronized void notifyWaitingForConnection() {
		notifyAll();
	}

}
