package com.lejoying.mc.service;

import java.util.Timer;
import java.util.TimerTask;

import android.content.Intent;

public class SMSService extends BaseService {

	public static final int ACTION_CLOGIN = 0x01;
	public static final int ACTION_REGISTER = 0x02;
	public static final int FLAG_BEFORE = 0x03;
	public static final int FLAG_NEW = 0x04;

	private int mCLoginRemain;
	private int mRegisterRemain;

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
	}

	private Timer mRegisterTimer;

	@Override
	public int onStartCommand(final Intent intent, int flags, int startId) {
		int action = intent.getIntExtra("action", 0);
		int flag = intent.getIntExtra("flag", 0);
		switch (action) {
		case ACTION_CLOGIN:
			break;
		case ACTION_REGISTER:
			if (mRegisterRemain == 0) {
				mRegisterRemain = 60;
				if (flag != FLAG_BEFORE) {
					mRegisterTimer = new Timer();
					final Intent sendIntent = new Intent();
					mRegisterTimer.schedule(new TimerTask() {
						@Override
						public void run() {
							mRegisterRemain--;
							if (mRegisterRemain == 0) {
								mRegisterTimer.cancel();
							}
							sendIntent.putExtra("remain", mRegisterRemain);
							sendIntent.setAction("com.action.MAIN");
							sendBroadcast(sendIntent);
						}
					}, 0, 1000);
				}
			}
			break;
		default:
			break;
		}
		return super.onStartCommand(intent, flags, startId);
	}

}
