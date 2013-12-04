package com.lejoying.mc.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lejoying.mc.R;
import com.lejoying.mc.api.API;
import com.lejoying.mc.entity.Circle;
import com.lejoying.mc.entity.Friend;
import com.lejoying.mc.entity.User;
import com.lejoying.mc.listener.ResponseListener;
import com.lejoying.mc.utils.MCDataTools;
import com.lejoying.mc.utils.MCHttpTools;
import com.lejoying.mc.utils.MCNetTools;
import com.lejoying.mc.utils.MCStaticData;

public class NetworkService extends BaseService {

	public static final int SERVICE_NETWORK = 0x0101;
	public static final String ACTION_STATUS = "ACTION_STATUS";
	public static final String ACTION_REMAIN = "ACTION_REMAIN";

	public static final int SERVICE_DATA = 0x0102;
	public static final String ACTION_NOTIFY = "ACTION_NOTIFY";

	public static final int WHAT_CHATMESSAGE = 0x021;
	public static final int WHAT_MESSAGELIST = 0x022;
	public static final int WHAT_FRIEND = 0x023;

	public static final int NOTIFY_FRIEND = 0x02;
	public static final int NOTIFY_MESSAGE = 0x01;

	public static final String REMAIN_REGISTER = "REGISTERCODEREMAIN";
	public static final String REMAIN_LOGIN = "LOGINCODEREMAIN";

	public static final int STATUS_NOINTERNET = 0x011;
	public static final int STATUS_FAILED = 0x012;
	public static final int STATUS_SUCCESS = 0x013;
	public static final int STATUS_UNSUCCESS = 0x014;

	private String lastRegisterPhone;
	private int mRegCodeRemain;
	private int mLoginCodeRemain;
	private Timer mRegTimer;
	private Timer mLoginTimer;

	private LayoutInflater mInflater;

	@Override
	public void onCreate() {
		super.onCreate();
		mInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public int onStartCommand(final Intent intent, int flags, int startId) {
		int SERVICE = intent.getIntExtra("SERVICE", -1);
		intent.removeExtra("SERVICE");
		if (SERVICE == SERVICE_DATA) {
			new Thread() {
				public void run() {
					handlerData(intent);
				};
			}.start();
		} else if (SERVICE == SERVICE_NETWORK) {
			new Thread() {
				public void run() {
					handlerNetworkAsk(intent);
				};
			}.start();
		}
		return super.onStartCommand(intent, flags, startId);
	}

	private void handlerData(Intent intent) {
		final Intent broadcast = new Intent();
		broadcast.setAction(ACTION_NOTIFY);
		switch (intent.getIntExtra("WHAT", -1)) {
		case WHAT_CHATMESSAGE:
			new Thread() {
				public void run() {

				};
			}.start();
			break;
		case WHAT_MESSAGELIST:
			new Thread() {
				public void run() {
					final List<View> messagesViewList = new ArrayList<View>();
					messagesViewList.add(mInflater.inflate(R.layout.f_margin,
							null));
					for (int i = 0; i < 100; i++) {
						View v = mInflater.inflate(R.layout.f_messages_item,
								null);
						messagesViewList.add(v);
					}

					messagesViewList.add(mInflater.inflate(R.layout.f_margin,
							null));
					MCStaticData.messages = messagesViewList;
					sendBroadcast(broadcast);
				};
			}.start();
			break;
		case WHAT_FRIEND:
			new Thread() {
				public void run() {
					List<View> circlesViewList = new ArrayList<View>();
					List<Circle> circles = MCDataTools
							.getCircles(NetworkService.this);
					if (circles != null) {
						for (Circle circle : circles) {
							View group = mInflater.inflate(
									R.layout.f_group_panel, null);
							TextView tv_groupname = (TextView) group
									.findViewById(R.id.tv_groupname);
							tv_groupname.setText(circle.getName());
							final List<Friend> friends = circle.getFriends();
							final int pagecount = friends.size() % 6 == 0 ? friends
									.size() / 6 : friends.size() / 6 + 1;
							final List<View> pageviews = new ArrayList<View>();
							for (int i = 0; i < pagecount; i++) {
								final int a = i;
								BaseAdapter gridpageAdapter = new BaseAdapter() {
									@Override
									public View getView(int position,
											View convertView,
											final ViewGroup parent) {
										View gridpage_item = (RelativeLayout) mInflater
												.inflate(
														R.layout.f_group_panelitem_gridpageitem_user,
														null);
										ImageView iv_head = (ImageView) gridpage_item
												.findViewById(R.id.iv_head);
										TextView tv_nickname = (TextView) gridpage_item
												.findViewById(R.id.tv_nickname);

										iv_head.setImageBitmap(null);
										tv_nickname
												.setText(friends.get(
														a * 6 + position)
														.getNickName());

										return gridpage_item;
									}

									@Override
									public long getItemId(int position) {
										return position;
									}

									@Override
									public Object getItem(int position) {
										return friends.get(a * 6 + position);
									}

									@Override
									public int getCount() {
										int nowcount = 0;
										if (a < pagecount - 1) {
											nowcount = 6;
										} else {
											nowcount = friends.size() - a * 6;
										}
										return nowcount;
									}
								};
								GridView gridpage = (GridView) mInflater
										.inflate(
												R.layout.f_group_panelitem_gridpage,
												null);
								gridpage.setAdapter(gridpageAdapter);
								pageviews.add(gridpage);
							}

							circlesViewList.add(group);
						}

					}
				};
			}.start();
			break;

		default:
			break;
		}
	}

	private void handlerNetworkAsk(Intent intent) {
		String api = intent.getStringExtra("API");
		Bundle params = intent.getExtras();
		params.remove("API");
		params.remove("PERMISSION");
		String apiClazz = API.getClazz(api);
		if (apiClazz.equals(API.ACCOUNT)) {
			if (api.equals(API.ACCOUNT_VERIFYPHONE)) {
				sendCode(intent);
				return;
			}
		} else if (apiClazz.equals(API.CIRCLE)) {

		} else if (apiClazz.equals(API.COMMUNITY)) {

		} else if (apiClazz.equals(API.IMAGE)) {

		} else if (apiClazz.equals(API.MESSAGE)) {

		} else if (apiClazz.equals(API.SESSION)) {

		} else if (apiClazz.equals(API.WEBCODE)) {

		}
		commonPOSTConnect(api, params, new NetworkResponseAdapter(intent));
	}

	private class NetworkResponseAdapter implements ResponseListener {
		Intent broadcast;
		Bundle params;
		String api;
		String apiClazz;

		public NetworkResponseAdapter(Intent intent) {
			api = intent.getStringExtra("API");
			apiClazz = API.getClazz(api);
			broadcast = new Intent();
			broadcast.putExtra("API", api);
			int permission = intent.getIntExtra("PERMISSION", -1);
			broadcast.putExtra("PERMISSION", permission);
			broadcast.setAction(ACTION_STATUS);
			// broadcast.putExtra("PERMISSION",
			// intent.getIntExtra("PERMISSION", -1));
			params = intent.getExtras();
			params.remove("API");
		}

		@Override
		public void noInternet() {
			sendBroadcast(broadcast, STATUS_NOINTERNET);
		}

		@Override
		public void success(final JSONObject data) {
			if (apiClazz.equals(API.ACCOUNT)) {
				if (api.equals(API.ACCOUNT_VERIFYPHONE)) {
					String usage = params.getString("usage");
					if (usage.equals("register")) {
						try {
							MCStaticData.registerBundle.putString("code",
									data.getString("code"));
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}
				} else if (api.equals(API.ACCOUNT_VERIFYCODE)) {
					if (MCStaticData.registerBundle != null) {
						try {
							MCStaticData.registerBundle.putString("accessKey",
									data.getString("accessKey"));
						} catch (JSONException e) {
							e.printStackTrace();
						}
					} else {
						String accessKey = "";
						try {
							accessKey = data.getString("accessKey");
						} catch (JSONException e) {
							e.printStackTrace();
						}
						saveLoginUser(params.getString("phone"), accessKey);
					}
					lastRegisterPhone = "";
					mRegCodeRemain = 0;
					mLoginCodeRemain = 0;
				} else if (api.equals(API.ACCOUNT_AUTH)) {
					String accessKey = "";
					try {
						accessKey = data.getString("accessKey");
					} catch (JSONException e) {
						e.printStackTrace();
					}
					saveLoginUser(params.getString("phone"), accessKey);
				} else if (api.equals(API.ACCOUNT_MODIFY)) {
					if (MCStaticData.registerBundle != null) {
						saveLoginUser(
								MCStaticData.registerBundle.getString("phone"),
								MCStaticData.registerBundle
										.getString("accessKey"));
					}
				} else if (api.equals(API.ACCOUNT_GET)) {
					if (params.getString("phone").equals(
							params.getString("target"))) {
						User user = null;
						try {
							user = new User(data.getJSONObject("account"));
							user.setAccessKey(MCDataTools.getLoginedUser(
									NetworkService.this).getAccessKey());
						} catch (JSONException e) {

						}
						MCDataTools.saveUser(NetworkService.this, user);
					}
				}
			} else if (apiClazz.equals(API.CIRCLE)) {

			} else if (apiClazz.equals(API.COMMUNITY)) {

			} else if (apiClazz.equals(API.IMAGE)) {

			} else if (apiClazz.equals(API.MESSAGE)) {
				if (api.equals(API.MESSAGE_GET)) {
					new Thread() {
						@Override
						public void run() {
							try {
								MCDataTools.saveMessages(NetworkService.this,
										data.getJSONArray("messages"));
								User user = MCDataTools
										.getLoginedUser(NetworkService.this);
								user.setFlag(data.getString("flag"));
								MCDataTools.saveUser(NetworkService.this, user);
							} catch (JSONException e) {
							}
						}
					}.start();
				}
			} else if (apiClazz.equals(API.SESSION)) {

			} else if (apiClazz.equals(API.WEBCODE)) {

			} else if (apiClazz.equals(API.RELATION)) {
				if (api.equals(API.RELATION_GETCIRCLESANDFRIENDS)) {
					new Thread() {
						public void run() {
							try {
								MCDataTools.saveCircles(NetworkService.this,
										data.getJSONArray("circles"));
							} catch (JSONException e) {
							}
						};
					}.start();
				}
			}
			sendBroadcast(broadcast, STATUS_SUCCESS);
		}

		@Override
		public void unsuccess(JSONObject data) {
			try {
				broadcast.putExtra("LOG",
						data.getString(getString(R.string.app_reason)));
			} catch (JSONException e) {
				broadcast.putExtra("LOG", getString(R.string.app_timeout));
			}
			sendBroadcast(broadcast, STATUS_UNSUCCESS);
		}

		@Override
		public void failed() {
			sendBroadcast(broadcast, STATUS_FAILED);
		}

	}

	public void sendBroadcast(Intent broadcast, int status) {
		broadcast.putExtra("STATUS", status);
		sendBroadcast(broadcast);
	}

	public void sendCode(Intent intent) {
		String api = intent.getStringExtra("API");
		Bundle params = intent.getExtras();
		params.remove("API");
		String usage = params.getString("usage");
		String phone = params.getString("phone");
		if (usage != null && phone != null) {
			if (usage.equals("register")) {
				MCStaticData.registerBundle = params;
				if (phone.equals(lastRegisterPhone) && mRegCodeRemain != 0) {
					lastRegisterPhone = phone;
					Intent broadcast = new Intent();
					broadcast.putExtra("API", api);
					broadcast.putExtra("STATUS", STATUS_SUCCESS);
					broadcast.setAction(ACTION_STATUS);
					sendBroadcast(broadcast);
					return;
				}
				if (!phone.equals(lastRegisterPhone)) {
					lastRegisterPhone = phone;
					mRegCodeRemain = 0;
					if (mRegTimer != null) {
						mRegTimer.cancel();
					}
				}
				if (mRegCodeRemain == 0) {
					mRegCodeRemain = 60;
					commonPOSTConnect(api, params, new NetworkResponseAdapter(
							intent) {
						@Override
						public void unsuccess(JSONObject data) {
							mRegCodeRemain = 0;
							super.unsuccess(data);
						}

						@Override
						public void noInternet() {
							mRegCodeRemain = 0;
							super.noInternet();
						}

						@Override
						public void failed() {
							mRegCodeRemain = 0;
							super.failed();
						}

						@Override
						public void success(JSONObject data) {
							super.success(data);
							mRegTimer = new Timer();
							mRegTimer.schedule(new TimerTask() {
								@Override
								public void run() {
									mRegCodeRemain--;
									Intent broadcast = new Intent();
									broadcast.putExtra(REMAIN_REGISTER,
											mRegCodeRemain);
									broadcast.setAction(ACTION_REMAIN);
									sendBroadcast(

									broadcast);
									if (mRegCodeRemain <= 0) {
										mRegCodeRemain = 0;
										mRegTimer.cancel();
									}
								}
							}, 0, 1000);
						}
					});
				}
			} else if (usage.equals("login")) {
				MCStaticData.registerBundle = null;
				if (mLoginCodeRemain == 0) {
					mLoginCodeRemain = 60;
					commonPOSTConnect(api, params, new NetworkResponseAdapter(
							intent) {
						@Override
						public void unsuccess(JSONObject data) {
							mLoginCodeRemain = 0;
							super.unsuccess(data);
						}

						@Override
						public void noInternet() {
							mLoginCodeRemain = 0;
							super.noInternet();
						}

						@Override
						public void failed() {
							mLoginCodeRemain = 0;
							super.failed();
						}

						@Override
						public void success(JSONObject data) {
							super.success(data);
							mLoginTimer = new Timer();
							mLoginTimer.schedule(new TimerTask() {
								@Override
								public void run() {
									mLoginCodeRemain--;
									Intent broadcast = new Intent();
									broadcast.putExtra(REMAIN_LOGIN,
											mLoginCodeRemain);
									broadcast.setAction(ACTION_REMAIN);
									sendBroadcast(

									broadcast);
									if (mLoginCodeRemain <= 0) {
										mLoginCodeRemain = 0;
										mLoginTimer.cancel();
									}
								}
							}, 0, 1000);
						}
					});

				}
			}
		}
	}

	public void commonGETConnect(String api, Bundle params,
			ResponseListener responseListener) {
		MCNetTools.ajax(this, api, params, MCHttpTools.SEND_GET, 5000,
				responseListener);
	}

	public void commonPOSTConnect(String api, Bundle params,
			ResponseListener responseListener) {
		MCNetTools.ajax(this, api, params, MCHttpTools.SEND_POST, 5000,
				responseListener);
	}

	public void connect(String api, Bundle params, int method, int timeout,
			ResponseListener responseListener) {
		MCNetTools.ajax(this, api, params, method, timeout, responseListener);
	}

	private void saveLoginUser(String phone, String accessKey) {
		User user = new User();
		user.setPhone(phone);
		user.setAccessKey(accessKey);
		MCDataTools.saveUser(NetworkService.this, user);
	}

}
