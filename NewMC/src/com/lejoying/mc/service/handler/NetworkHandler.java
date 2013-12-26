package com.lejoying.mc.service.handler;

import java.net.HttpURLConnection;
import java.util.Hashtable;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.lejoying.mc.R;
import com.lejoying.mc.api.API;
import com.lejoying.mc.data.App;
import com.lejoying.mc.data.User;
import com.lejoying.mc.service.BaseService.ServiceEvent;
import com.lejoying.mc.utils.MCDataTools;
import com.lejoying.mc.utils.MCHttpTools;
import com.lejoying.mc.utils.MCNetTools;
import com.lejoying.mc.utils.MCNetTools.ResponseListener;
import com.lejoying.utils.RSAUtils;

public class NetworkHandler {

	App app = App.getInstance();

	private Context mContext;
	private NetworkRemain mNetworkRemain;
	private ServiceEvent mServiceEvent;
	private Map<String, HttpURLConnection> mConnections;

	NetworkHandler(Context context, ServiceEvent serviceEvent) {
		this.mContext = context;
		this.mServiceEvent = serviceEvent;
		this.mNetworkRemain = new NetworkRemain(serviceEvent);
		this.mConnections = new Hashtable<String, HttpURLConnection>();
	}

	protected void process(final Intent intent) {
		String api = intent.getStringExtra("API");
		String apiClazz = API.getClazz(api);

		if (mConnections.get(api) != null) {
			mConnections.get(api).disconnect();
		}

		int SERVICE = intent.getIntExtra("SERVICE", -1);

		switch (SERVICE) {
		case MainServiceHandler.SERVICE_CANCELNETWORK:

			break;
		case MainServiceHandler.SERVICE_NETWORK:
			final Intent broadcast = new Intent();
			broadcast.putExtra("API", api);
			int permission = intent.getIntExtra("PERMISSION", -1);
			broadcast.putExtra("PERMISSION", permission);
			broadcast.setAction(MainServiceHandler.ACTION_STATUS);

			Bundle params = intent.getExtras();
			params.remove("API");
			params.remove("PERMISSION");
			params.remove("SERVICE");
			if (apiClazz.equals(API.ACCOUNT)) {
				if (api.equals(API.ACCOUNT_VERIFYPHONE)) {
					if (mNetworkRemain.isRemain(intent)) {
						sendNetworkBroadcast(broadcast,
								MainServiceHandler.STATUS_NETWORK_SUCCESS);
						return;
					}
				}
			} else if (apiClazz.equals(API.CIRCLE)) {

			} else if (apiClazz.equals(API.COMMUNITY)) {

			} else if (apiClazz.equals(API.IMAGE)) {

			} else if (apiClazz.equals(API.MESSAGE)) {

			} else if (apiClazz.equals(API.SESSION)) {

			} else if (apiClazz.equals(API.WEBCODE)) {

			} else if (apiClazz.equals(API.RELATION)) {

			}
			commonPOSTConnect(api, params, new ResponseAdapter(intent));
			break;

		default:
			break;
		}

	}

	protected void process(Intent intent, final JSONObject data) {
		String api = intent.getStringExtra("API");
		String apiClazz = API.getClazz(api);

		Bundle params = intent.getExtras();
		params.remove("API");
		params.remove("PERMISSION");
		params.remove("SERVICE");

		if (apiClazz.equals(API.ACCOUNT)) {
			if (api.equals(API.ACCOUNT_VERIFYPHONE)) {
				String usage = params.getString("usage");
				if (usage.equals("register")) {
					MCDataTools.app.registerBundle = params;
					try {
						MCDataTools.app.registerBundle.putString("code",
								data.getString("code"));
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
				mNetworkRemain.startReamin(intent);
			} else if (api.equals(API.ACCOUNT_VERIFYCODE)) {
				if (MCDataTools.app.registerBundle != null) {
					System.out.println(data);
					try {
						MCDataTools.app.registerBundle.putString(
								"accessKey",
								RSAUtils.decrypt(app.config.pbKey0,
										data.getString("accessKey")));
						MCDataTools.app.registerBundle.putString("PbKey",
								data.getString("PbKey"));
					} catch (JSONException e) {
						e.printStackTrace();
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} else {
					String accessKey = "";
					String pbKey = "";
					try {
						accessKey = RSAUtils.decrypt(app.config.pbKey0,
								data.getString("accessKey"));
						pbKey = data.getString("PbKey");
					} catch (JSONException e) {
						e.printStackTrace();
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					saveLoginUser(params.getString("phone"), accessKey, pbKey);
				}
				mNetworkRemain.cancelRemain();
			} else if (api.equals(API.ACCOUNT_AUTH)) {
				String accessKey = "";
				String pbKey = "";
				try {
					accessKey = data.getString("accessKey");
					pbKey = data.getString("PbKey");
				} catch (JSONException e) {
					e.printStackTrace();
				}
				saveLoginUser(params.getString("phone"), accessKey, pbKey);
			} else if (api.equals(API.ACCOUNT_MODIFY)) {
				if (MCDataTools.app.registerBundle != null) {
					saveLoginUser(
							MCDataTools.app.registerBundle.getString("phone"),
							MCDataTools.app.registerBundle
									.getString("accessKey"),
							MCDataTools.app.registerBundle.getString("PbKey"));
				}
			} else if (api.equals(API.ACCOUNT_GET)) {
				if (params.getString("phone")
						.equals(params.getString("target"))) {
					try {
						MCDataTools.updateUser(data.getJSONObject("account"));
					} catch (JSONException e) {

					}
				}
			} else if (api.equals(API.ACCOUNT_EXIT)) {

			}
		} else if (apiClazz.equals(API.CIRCLE)) {

		} else if (apiClazz.equals(API.COMMUNITY)) {

		} else if (apiClazz.equals(API.IMAGE)) {

		} else if (apiClazz.equals(API.MESSAGE)) {
			if (api.equals(API.MESSAGE_GET)) {
				try {
					MCDataTools.saveMessages(data.getJSONArray("messages"));
					app.data.user.flag = String.valueOf(data.getInt("flag"));
				} catch (JSONException e) {
				}
			}
		} else if (apiClazz.equals(API.SESSION)) {

		} else if (apiClazz.equals(API.WEBCODE)) {

		} else if (apiClazz.equals(API.RELATION)) {
			if (api.equals(API.RELATION_GETCIRCLESANDFRIENDS)) {
				try {
					MCDataTools.saveCircles(data.getJSONArray("circles"));
				} catch (JSONException e) {
				}

			}
		}

	}

	protected void sendNetworkBroadcast(Intent broadcast, int status) {
		broadcast.putExtra("STATUS", status);
		mServiceEvent.sendBroadcast(broadcast);
	}

	protected void commonGETConnect(String api, Bundle params,
			ResponseListener responseListener) {
		MCNetTools.ajax(mContext, api, params, MCHttpTools.SEND_GET, 5000,
				responseListener);
	}

	protected void commonPOSTConnect(String api, Bundle params,
			ResponseListener responseListener) {
		MCNetTools.ajax(mContext, api, params, MCHttpTools.SEND_POST, 5000,
				responseListener);
	}

	protected void connect(String api, Bundle params, int method, int timeout,
			ResponseListener responseListener) {
		MCNetTools.ajax(mContext, api, params, method, timeout,
				responseListener);
	}

	private void saveLoginUser(String phone, String accessKey, String pbKey) {
		User user = app.data.user;
		user.phone = phone;
		try {
			user.accessKey = RSAUtils.decrypt(pbKey, accessKey);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	class ResponseAdapter implements ResponseListener {

		private String api;
		private Intent intent;
		private Intent broadcast;

		public ResponseAdapter(Intent intent) {
			this.intent = intent;
			api = intent.getStringExtra("API");
			this.broadcast = new Intent();
			broadcast.putExtra("API", api);
			int permission = intent.getIntExtra("PERMISSION", -1);
			broadcast.putExtra("PERMISSION", permission);
			broadcast.setAction(MainServiceHandler.ACTION_STATUS);

		}

		@Override
		public void connectionCreated(HttpURLConnection httpURLConnection) {
			mConnections.put(api, httpURLConnection);
		}

		@Override
		public void success(JSONObject data) {
			if (data != null) {
				String info = null;
				try {
					info = data.getString(mContext
							.getString(R.string.app_notice));
				} catch (JSONException e) {
					e.printStackTrace();
					sendNetworkBroadcast(broadcast,
							MainServiceHandler.STATUS_NETWORK_FAILED);
					return;
				}
				if (info == null) {
					sendNetworkBroadcast(broadcast,
							MainServiceHandler.STATUS_NETWORK_FAILED);
					return;
				}
				info = info.substring(info.length() - 2, info.length());

				if (info.equals(mContext.getString(R.string.app_success))) {
					process(intent, data);
					sendNetworkBroadcast(broadcast,
							MainServiceHandler.STATUS_NETWORK_SUCCESS);
				}
				if (info.equals(mContext.getString(R.string.app_unsuccess))) {
					try {
						broadcast.putExtra("LOG", data.getString(mContext
								.getString(R.string.app_reason)));
					} catch (JSONException e) {
						broadcast.putExtra("LOG",
								mContext.getString(R.string.app_timeout));
					}
					sendNetworkBroadcast(broadcast,
							MainServiceHandler.STATUS_NETWORK_UNSUCCESS);
				}
			}
		}

		@Override
		public void noInternet() {
			sendNetworkBroadcast(broadcast,
					MainServiceHandler.STATUS_NETWORK_NOINTERNET);
		}

		@Override
		public void failed() {
			sendNetworkBroadcast(broadcast,
					MainServiceHandler.STATUS_NETWORK_FAILED);
		}
	}

	public interface NetworkStatusListener {
		public void onReceive(int STATUS, String log);
	}
}
