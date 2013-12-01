package com.lejoying.mc.service;

import org.json.JSONObject;

import android.os.Handler;
import android.os.Message;

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

	public static NetworkService instance;

	private AccountManager mAccountManager;
	private CircleManager mCircleManager;
	private CommunityManager mCommunityManager;
	private MessageManager mMessageManager;
	private RelationManager mRelationManager;
	private Session mSession;

	@Override
	public void onCreate() {
		super.onCreate();
		mAccountManager = new AccountManagerImpl(this);
		mCircleManager = new CircleManagerImpl(this);
		mCommunityManager = new CommunityManagerImpl(this);
		mMessageManager = new MessageManagerImpl(this);
		mRelationManager = new RelationManagerImpl(this);
		mSession = new SessionImpl(this);
		instance = this;
	}

	public class NetworkHandler extends Handler {

		@Override
		public void handleMessage(Message msg) {

		}
	}

}
