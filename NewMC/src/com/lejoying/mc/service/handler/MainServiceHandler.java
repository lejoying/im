package com.lejoying.mc.service.handler;

import android.content.Context;
import android.content.Intent;

import com.lejoying.mc.service.BaseService.ServiceEvent;

public class MainServiceHandler {

	public static final int SERVICE_NETWORK = 0x011;
	public static final int SERVICE_CANCELNETWORK = 0x012;
	public static final String ACTION_STATUS = "ACTION_STATUS";
	public static final String ACTION_REMAIN = "ACTION_REMAIN";

	public static final int SERVICE_NOTIFYVIEW = 0x013;
	public static final String ACTION_NOTIFY = "ACTION_NOTIFY";

	public static final String REMAIN_REGISTER = "REGISTERCODEREMAIN";
	public static final String REMAIN_LOGIN = "LOGINCODEREMAIN";

	public static final int STATUS_NETWORK_NOINTERNET = 0x031;
	public static final int STATUS_NETWORK_FAILED = 0x032;
	public static final int STATUS_NETWORK_SUCCESS = 0x033;
	public static final int STATUS_NETWORK_UNSUCCESS = 0x034;

	public static final int NOTIFY_CHATMESSAGE = 0X041;
	public static final int NOTIFY_MESSAGELIST = 0X042;
	public static final int NOTIFY_FRIEND = 0X043;

	private ServiceEvent mServiceEvent;

	private NetworkHandler mNetworkHandler;

	public MainServiceHandler(Context context, ServiceEvent serviceEvent) {
		this.mServiceEvent = serviceEvent;
		this.mNetworkHandler = new NetworkHandler(context, serviceEvent);
		if (this.mServiceEvent == null) {
			this.mServiceEvent = new ServiceEvent() {
				@Override
				public void sendBroadcast(Intent broadcast) {

				}
			};
		}
	}

	public void process(final Intent intent) {
		if (intent == null) {
			return;
		}
		new Thread() {
			@Override
			public void run() {
				int SERVICE = intent.getIntExtra("SERVICE", -1);
				if (SERVICE == SERVICE_NETWORK
						|| SERVICE == SERVICE_CANCELNETWORK) {
					mNetworkHandler.process(intent);
				}
			}
		}.start();
	}
}
