package com.lejoying.mc.service;

import android.content.Intent;

import com.lejoying.mc.service.BaseService.ServiceEvent;
import com.lejoying.mc.service.handler.MainServiceHandler;

public class MainService extends BaseService implements ServiceEvent {

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
