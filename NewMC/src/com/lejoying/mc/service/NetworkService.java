package com.lejoying.mc.service;

import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;

import com.lejoying.mc.R;
import com.lejoying.mc.api.API;
import com.lejoying.mc.api.AccountManager;
import com.lejoying.mc.api.CircleManager;
import com.lejoying.mc.api.CommunityManager;
import com.lejoying.mc.api.MessageManager;
import com.lejoying.mc.api.RelationManager;
import com.lejoying.mc.api.Session;
import com.lejoying.mc.apiimpl.AccountManagerImpl;
import com.lejoying.mc.apiimpl.CircleManagerImpl;
import com.lejoying.mc.apiimpl.CommunityManagerImpl;
import com.lejoying.mc.apiimpl.MessageManagerImpl;
import com.lejoying.mc.apiimpl.RelationManagerImpl;
import com.lejoying.mc.apiimpl.SessionImpl;
import com.lejoying.mc.listener.ResponseListener;
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

	protected AccountManager mAccountManager;
	protected CircleManager mCircleManager;
	protected CommunityManager mCommunityManager;
	protected MessageManager mMessageManager;
	protected RelationManager mRelationManager;
	protected Session mSession;

	private String lastRegisterPhone;
	private String lastLoginPhone;
	private int mRegCodeRemain;
	private int mLoginCodeRemain;
	private Timer mRegTimer;
	private Timer mLoginTimer;

	@Override
	public void onCreate() {
		super.onCreate();
		mAccountManager = new AccountManagerImpl(this);
		mCircleManager = new CircleManagerImpl(this);
		mCommunityManager = new CommunityManagerImpl(this);
		mMessageManager = new MessageManagerImpl(this);
		mRelationManager = new RelationManagerImpl(this);
		mSession = new SessionImpl(this);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		System.out.println(intent.getExtras()+":::::::::::");
		handlerNetworkAsk(intent);
		return super.onStartCommand(intent, flags, startId);
	}

	private void handlerNetworkAsk(Intent intent) {
		Bundle params = intent.getExtras();
		params.remove("API");
		switch (intent.getIntExtra("API", -1)) {
		case API.ACCOUNT_AUTH:
			mAccountManager.auth(params, new NetworkResponseAdapter(intent));
			break;
		case API.ACCOUNT_EXIT:
			mAccountManager.exit(params, new NetworkResponseAdapter(intent));
			break;
		case API.ACCOUNT_GETACCOUNT:
			mAccountManager.getaccount(params, new NetworkResponseAdapter(
					intent));
			break;
		case API.ACCOUNT_VERIFYCODE:
			mAccountManager.verifycode(params, new NetworkResponseAdapter(
					intent));
			break;
		case API.ACCOUNT_VERIFYPHONE:
			if (params.getString("usage") != null
					&& params.getString("phone") != null) {
				if (params.getString("usage").equals("register")) {
					if (params.getString("phone").equals(lastRegisterPhone)
							&& mRegCodeRemain != 0) {
						lastRegisterPhone = params.getString("phone");
						Intent broadcast = new Intent();
						broadcast.putExtra("API", API.ACCOUNT_VERIFYPHONE);
						broadcast.putExtra("STATUS", STATUS_SUCCESS);
						broadcast.setAction(ACTION);
						sendBroadcast(broadcast);
						break;
					}
					if (!params.getString("phone").equals(lastRegisterPhone)) {
						lastRegisterPhone = params.getString("phone");
						mRegCodeRemain = 0;
						if (mRegTimer != null) {
							mRegTimer.cancel();
						}
					}
					if (mRegCodeRemain == 0) {
						mRegCodeRemain = 60;
						mAccountManager.verifyphone(params,
								new NetworkResponseAdapter(intent) {
									@Override
									public void success(JSONObject data) {
										super.success(data);
										mRegTimer = new Timer();
										mRegTimer.schedule(new TimerTask() {
											@Override
											public void run() {
												mRegCodeRemain--;
												Intent broadcast = new Intent();
												broadcast.putExtra(
														REMAIN_REGISTER,
														mRegCodeRemain);
												broadcast
														.setAction(ACTION_REMAIN);
												sendBroadcast(

												broadcast);
												if (mRegCodeRemain == 0) {
													mRegTimer.cancel();
												}
											}
										}, 0, 1000);
									}
								});
					}
				} else if (intent.getStringExtra("usage").equals("login")) {
					if (mLoginCodeRemain == 0) {
						mLoginCodeRemain = 60;
						mAccountManager.verifyphone(params,
								new NetworkResponseAdapter(intent));
					}
				}
			}
			break;
		case API.ACCOUNT_VERIFYWEBCODE:
			mAccountManager.verifywebcode(params, new NetworkResponseAdapter(
					intent));
			break;
		case API.ACCOUNT_VERIFYWEBCODELOGIN:
			mAccountManager.verifywebcodelogin(params,
					new NetworkResponseAdapter(intent));
			break;

		case API.CIRCLE_DELETE:
			mCircleManager.delete(params, new NetworkResponseAdapter(intent));
			break;
		case API.CIRCLE_MODIFY:
			mCircleManager.modify(params, new NetworkResponseAdapter(intent));
			break;

		case API.COMMUNITY_FIND:
			mCommunityManager.find(params, new NetworkResponseAdapter(intent));
			break;
		case API.COMMUNITY_GETCOMMUNITIES:
			mCommunityManager.getcommunities(params,
					new NetworkResponseAdapter(intent));
			break;
		case API.COMMUNITY_GETCOMMUNITYFRIENDS:
			mCommunityManager.getcommunityfriends(params,
					new NetworkResponseAdapter(intent));
			break;
		case API.COMMUNITY_JOIN:
			mCommunityManager.join(params, new NetworkResponseAdapter(intent));
			break;
		case API.COMMUNITY_UNJOIN:
			mCommunityManager
					.unjoin(params, new NetworkResponseAdapter(intent));
			break;

		case API.MESSAGE_GET:
			mMessageManager.get(params, new NetworkResponseAdapter(intent));
			break;
		case API.MESSAGE_SEND:
			mMessageManager.send(params, new NetworkResponseAdapter(intent));
			break;

		case API.RELATION_ADDCIRCLE:
			mRelationManager.addcircle(params, new NetworkResponseAdapter(
					intent));
			break;
		case API.RELATION_ADDFRIEND:
			mRelationManager.addfriend(params, new NetworkResponseAdapter(
					intent));
			break;
		case API.RELATION_ADDFRIENDAGREE:
			mRelationManager.addfriendagree(params, new NetworkResponseAdapter(
					intent));
			break;
		case API.RELATION_GETASKFRIENDS:
			mRelationManager.getaskfriends(params, new NetworkResponseAdapter(
					intent));
			break;
		case API.RELATION_GETCIRCLESANDFRIENDS:
			mRelationManager.getcirclesandfriends(params,
					new NetworkResponseAdapter(intent));
			break;
		case API.RELATION_GETCOMMUNITIES:
			mRelationManager.getcommunities(params, new NetworkResponseAdapter(
					intent));
			break;
		case API.RELATION_GETFRIENDS:
			mRelationManager.getfriends(params, new NetworkResponseAdapter(
					intent));
			break;

		case API.SESSION_EVENT:
			mSession.event(params, new NetworkResponseAdapter(intent));
			break;
		case API.SESSION_EVENTWEB:
			mSession.eventweb(params, new NetworkResponseAdapter(intent));
			break;

		default:
			break;
		}
	}

	private class NetworkResponseAdapter implements ResponseListener {
		Intent intent;
		Intent broadcast;
		Bundle params;

		public NetworkResponseAdapter(Intent intent) {
			this.intent = intent;
			broadcast = new Intent();
			broadcast.putExtra("API", intent.getIntExtra("API", -1));
			broadcast.setAction(ACTION);
			params = intent.getExtras();
			params.remove("API");
		}

		@Override
		public void noInternet() {
			broadcast.putExtra("STATUS", STATUS_NOINTERNET);
			sendBroadcast(broadcast);
		}

		@Override
		public void success(JSONObject data) {
			switch (intent.getIntExtra("API", -1)) {
			case API.ACCOUNT_AUTH:
				break;
			case API.ACCOUNT_EXIT:
				break;
			case API.ACCOUNT_GETACCOUNT:
				break;
			case API.ACCOUNT_VERIFYCODE:

				break;
			case API.ACCOUNT_VERIFYPHONE:
				if (params.getString("usage").equals("register")) {
					MCStaticData.registerBundle = params;
					MCStaticData.registerBundle.remove("usage");
					try {
						MCStaticData.registerBundle.putString("code",
								data.getString("code"));
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
				break;
			case API.ACCOUNT_VERIFYWEBCODE:
				break;
			case API.ACCOUNT_VERIFYWEBCODELOGIN:
				break;
			case API.CIRCLE_DELETE:
				break;
			case API.CIRCLE_MODIFY:
				break;
			case API.COMMUNITY_FIND:
				break;
			case API.COMMUNITY_GETCOMMUNITIES:
				break;
			case API.COMMUNITY_GETCOMMUNITYFRIENDS:
				break;
			case API.COMMUNITY_JOIN:
				break;
			case API.COMMUNITY_UNJOIN:
				break;
			case API.MESSAGE_GET:
				break;
			case API.MESSAGE_SEND:
				break;
			case API.RELATION_ADDCIRCLE:
				break;
			case API.RELATION_ADDFRIEND:
				break;
			case API.RELATION_ADDFRIENDAGREE:
				break;
			case API.RELATION_GETASKFRIENDS:
				break;
			case API.RELATION_GETCIRCLESANDFRIENDS:
				break;
			case API.RELATION_GETCOMMUNITIES:
				break;
			case API.RELATION_GETFRIENDS:
				break;
			case API.SESSION_EVENT:
				break;
			case API.SESSION_EVENTWEB:
				break;
			default:
				break;
			}
			broadcast.putExtra("STATUS", STATUS_SUCCESS);
			sendBroadcast(broadcast);
		}

		@Override
		public void unsuccess(JSONObject data) {
			broadcast.putExtra("STATUS", STATUS_UNSUCCESS);
			try {
				broadcast.putExtra("LOG",
						data.getString(getString(R.string.app_reason)));
			} catch (JSONException e) {
				broadcast.putExtra("LOG", getString(R.string.app_timeout));
			}
			sendBroadcast(broadcast);
		}

		@Override
		public void failed() {
			broadcast.putExtra("STATUS", STATUS_FAILED);
			sendBroadcast(broadcast);
		}
	}

}
