package com.lejoying.mc.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class BaseService extends Service {
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	public interface ServiceEvent {
		public void sendBroadcast(Intent broadcast);
	}
}
