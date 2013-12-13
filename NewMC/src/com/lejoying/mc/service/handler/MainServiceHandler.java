package com.lejoying.mc.service.handler;

import android.content.Context;
import android.content.Intent;

import com.lejoying.mc.service.MainService;

public class MainServiceHandler {

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
				if (SERVICE == MainService.SERVICE_NETWORK
						|| SERVICE == MainService.SERVICE_CANCELNETWORK) {
					mNetworkHandler.process(intent);
				}
			}
		}.start();
	}

	public interface ServiceEvent {
		public void sendBroadcast(Intent broadcast);
	}
}
