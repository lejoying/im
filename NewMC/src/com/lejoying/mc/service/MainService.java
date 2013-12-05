package com.lejoying.mc.service;

import android.content.Intent;

import com.lejoying.mc.service.handler.MainServiceHandler;
import com.lejoying.mc.service.handler.MainServiceHandler.ServiceEvent;

public class MainService extends BaseService implements ServiceEvent {

	public static final int SERVICE_NETWORK = 0x011;
	public static final int SERVICE_CANCELNETWORK = 0x012;
	public static final String ACTION_STATUS = "ACTION_STATUS";
	public static final String ACTION_REMAIN = "ACTION_REMAIN";

	public static final int SERVICE_NOTIFYDATA = 0x013;
	public static final int SERVICE_NOTIFYVIEW = 0x014;
	public static final String ACTION_NOTIFY = "ACTION_NOTIFY";

	public static final int WHAT_CHATMESSAGE = 0x021;
	public static final int WHAT_MESSAGELIST = 0x022;
	public static final int WHAT_FRIEND = 0x023;

	public static final String REMAIN_REGISTER = "REGISTERCODEREMAIN";
	public static final String REMAIN_LOGIN = "LOGINCODEREMAIN";

	public static final int STATUS_NETWORK_NOINTERNET = 0x031;
	public static final int STATUS_NETWORK_FAILED = 0x032;
	public static final int STATUS_NETWORK_SUCCESS = 0x033;
	public static final int STATUS_NETWORK_UNSUCCESS = 0x034;

	public static final int NOTIFY_CHATMESSAGE = 0X041;
	public static final int NOTIFY_MESSAGELIST = 0X042;
	public static final int NOTIFY_FRIEND = 0X043;

	private MainServiceHandler mMainServiceHandler;

	@Override
	public void onCreate() {
		super.onCreate();
		mMainServiceHandler = new MainServiceHandler(this, this);
	}

	@Override
	public int onStartCommand(final Intent intent, int flags, int startId) {
		mMainServiceHandler.process(intent);
		return super.onStartCommand(intent, flags, startId);
	}

}
