package com.lejoying.mc.service;

import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;

import com.lejoying.mc.R;
import com.lejoying.mc.api.API;
import com.lejoying.mc.entity.User;
import com.lejoying.mc.listener.ResponseListener;
import com.lejoying.mc.utils.MCDataTools;
import com.lejoying.mc.utils.MCHttpTools;
import com.lejoying.mc.utils.MCNetTools;
import com.lejoying.mc.utils.MCStaticData;

public class NetworkService extends BaseService {

	public static final String ACTION = "NETWORKSERVICE";
	public static final String ACTION_REMAIN = "NETWORKSERVICE_REMAIN";

	public static final String REMAIN_REGISTER = "REGISTERCODEREMAIN";
	public static final String REMAIN_LOGIN = "LOGINCODEREMAIN";

	public static final int STATUS_NOINTERNET = 0x01;
	public static final int STATUS_FAILED = 0x02;
	public static final int STATUS_SUCCESS = 0x03;
	public static final int STATUS_UNSUCCESS = 0x04;

	private String lastRegisterPhone;
	private int mRegCodeRemain;
	private int mLoginCodeRemain;
	private Timer mRegTimer;
	private Timer mLoginTimer;

	@Override
	public void onCreate() {
		super.onCreate();

	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		handlerNetworkAsk(intent);
		return super.onStartCommand(intent, flags, startId);
	}

	private void handlerNetworkAsk(Intent intent) {
		String api = intent.getStringExtra("API");
		Bundle params = intent.getExtras();
		params.remove("API");
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
			broadcast.setAction(ACTION);
			params = intent.getExtras();
			params.remove("API");
		}

		@Override
		public void noInternet() {
			sendBroadcast(broadcast, STATUS_NOINTERNET);
		}

		@Override
		public void success(JSONObject data) {
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

			} else if (apiClazz.equals(API.SESSION)) {

			} else if (apiClazz.equals(API.WEBCODE)) {

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
					broadcast.setAction(ACTION);
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
