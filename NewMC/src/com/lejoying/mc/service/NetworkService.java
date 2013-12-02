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
		handlerNetworkAsk(intent);
		return super.onStartCommand(intent, flags, startId);
	}

	private void handlerNetworkAsk(Intent intent) {
		final Intent broadcast = new Intent();
		broadcast.putExtra("API", intent.getIntExtra("API", -1));
		broadcast.setAction(ACTION);
		Bundle params = intent.getExtras();
		params.remove("API");
		switch (intent.getIntExtra("API", -1)) {
		case API.ACCOUNT_AUTH:
			mAccountManager.auth(params, new NetworkResponseAdapter(broadcast));
			break;
		case API.ACCOUNT_EXIT:
			mAccountManager.exit(params, new NetworkResponseAdapter(broadcast));
			break;
		case API.ACCOUNT_GETACCOUNT:
			mAccountManager.getaccount(params, new NetworkResponseAdapter(
					broadcast));
			break;
		case API.ACCOUNT_VERIFYCODE:
			mAccountManager.verifycode(params, new NetworkResponseAdapter(
					broadcast));
			break;
		case API.ACCOUNT_VERIFYPHONE:
			if (intent.getStringExtra("usage") != null
					&& intent.getStringExtra("phone") != null) {
				if (intent.getStringExtra("usage").equals("register")) {
					if (intent.getStringExtra("phone")
							.equals(lastRegisterPhone) && mRegCodeRemain != 0) {
						lastRegisterPhone = intent.getStringExtra("phone");
						broadcast.putExtra("STATUS", STATUS_SUCCESS);
						sendBroadcast(broadcast);
						break;
					}
					if (!intent.getStringExtra("phone").equals(
							lastRegisterPhone)) {
						lastRegisterPhone = intent.getStringExtra("phone");
						mRegCodeRemain = 0;
						if (mRegTimer != null) {
							mRegTimer.cancel();
						}
					}
					if (mRegCodeRemain == 0) {
						mRegCodeRemain = 60;
						mAccountManager.verifyphone(params,
								new NetworkResponseAdapter(broadcast) {
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
												sendBroadcast(broadcast);
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
								new NetworkResponseAdapter(broadcast));
					}
				}
			}
			break;
		case API.ACCOUNT_VERIFYWEBCODE:
			mAccountManager.verifywebcode(params, new NetworkResponseAdapter(
					broadcast));
			break;
		case API.ACCOUNT_VERIFYWEBCODELOGIN:
			mAccountManager.verifywebcodelogin(params,
					new NetworkResponseAdapter(broadcast));
			break;

		case API.CIRCLE_DELETE:
			mCircleManager
					.delete(params, new NetworkResponseAdapter(broadcast));
			break;
		case API.CIRCLE_MODIFY:
			mCircleManager
					.modify(params, new NetworkResponseAdapter(broadcast));
			break;

		case API.COMMUNITY_FIND:
			mCommunityManager.find(params,
					new NetworkResponseAdapter(broadcast));
			break;
		case API.COMMUNITY_GETCOMMUNITIES:
			mCommunityManager.getcommunities(params,
					new NetworkResponseAdapter(broadcast));
			break;
		case API.COMMUNITY_GETCOMMUNITYFRIENDS:
			mCommunityManager.getcommunityfriends(params,
					new NetworkResponseAdapter(broadcast));
			break;
		case API.COMMUNITY_JOIN:
			mCommunityManager.join(params,
					new NetworkResponseAdapter(broadcast));
			break;
		case API.COMMUNITY_UNJOIN:
			mCommunityManager.unjoin(params, new NetworkResponseAdapter(
					broadcast));
			break;

		case API.MESSAGE_GET:
			mMessageManager.get(params, new NetworkResponseAdapter(broadcast));
			break;
		case API.MESSAGE_SEND:
			mMessageManager.send(params, new NetworkResponseAdapter(broadcast));
			break;

		case API.RELATION_ADDCIRCLE:
			mRelationManager.addcircle(params, new NetworkResponseAdapter(
					broadcast));
			break;
		case API.RELATION_ADDFRIEND:
			mRelationManager.addfriend(params, new NetworkResponseAdapter(
					broadcast));
			break;
		case API.RELATION_ADDFRIENDAGREE:
			mRelationManager.addfriendagree(params, new NetworkResponseAdapter(
					broadcast));
			break;
		case API.RELATION_GETASKFRIENDS:
			mRelationManager.getaskfriends(params, new NetworkResponseAdapter(
					broadcast));
			break;
		case API.RELATION_GETCIRCLESANDFRIENDS:
			mRelationManager.getcirclesandfriends(params,
					new NetworkResponseAdapter(broadcast));
			break;
		case API.RELATION_GETCOMMUNITIES:
			mRelationManager.getcommunities(params, new NetworkResponseAdapter(
					broadcast));
			break;
		case API.RELATION_GETFRIENDS:
			mRelationManager.getfriends(params, new NetworkResponseAdapter(
					broadcast));
			break;

		case API.SESSION_EVENT:
			mSession.event(params, new NetworkResponseAdapter(broadcast));
			break;
		case API.SESSION_EVENTWEB:
			mSession.eventweb(params, new NetworkResponseAdapter(broadcast));
			break;

		default:
			break;
		}
	}

	private class NetworkResponseAdapter implements ResponseListener {
		Intent intent;

		public NetworkResponseAdapter(Intent intent) {
			this.intent = intent;
		}

		@Override
		public void noInternet() {
			intent.putExtra("STATUS", STATUS_NOINTERNET);
			sendBroadcast(intent);
		}

		@Override
		public void success(JSONObject data) {
			intent.putExtra("STATUS", STATUS_SUCCESS);
			sendBroadcast(intent);
		}

		@Override
		public void unsuccess(JSONObject data) {
			intent.putExtra("STATUS", STATUS_UNSUCCESS);
			try {
				intent.putExtra("LOG",
						data.getString(getString(R.string.app_reason)));
			} catch (JSONException e) {
				intent.putExtra("LOG", getString(R.string.app_timeout));
			}
			sendBroadcast(intent);
		}

		@Override
		public void failed() {
			intent.putExtra("STATUS", STATUS_FAILED);
			sendBroadcast(intent);
		}
	}

}
