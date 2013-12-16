package com.lejoying.mc.service.handler;

import java.util.Timer;
import java.util.TimerTask;

import android.content.Intent;
import android.os.Bundle;

import com.lejoying.mc.service.MainService;
import com.lejoying.mc.service.handler.MainServiceHandler.ServiceEvent;

public class NetworkRemain {

	private String lastRegisterPhone;
	private int mRegCodeRemain;
	private int mLoginCodeRemain;
	private Timer mRegTimer;
	private Timer mLoginTimer;

	private ServiceEvent mServiceEvent;

	NetworkRemain(ServiceEvent serviceEvent) {
		this.mServiceEvent = serviceEvent;
	}

	protected boolean isRemain(Intent intent) {
		Bundle params = intent.getExtras();
		String usage = params.getString("usage");
		String phone = params.getString("phone");
		if (usage != null && phone != null) {
			if (usage.equals("register")) {
				if (phone.equals(lastRegisterPhone) && mRegCodeRemain != 0) {
					return true;
				}
				if (!phone.equals(lastRegisterPhone)) {
					lastRegisterPhone = phone;
					mRegCodeRemain = 0;
					if (mRegTimer != null) {
						mRegTimer.cancel();
					}
				}
			} else if (usage.equals("login")) {
				if (mLoginCodeRemain != 0) {
					return true;
				}
			}
		}
		return false;
	}

	protected void startReamin(Intent intent) {
		Bundle params = intent.getExtras();
		String usage = params.getString("usage");
		String phone = params.getString("phone");
		if (usage != null && phone != null) {
			if (usage.equals("register")) {
				if (mRegCodeRemain == 0) {
					mRegCodeRemain = 60;
					mRegTimer = new Timer();
					mRegTimer.schedule(new TimerTask() {
						@Override
						public void run() {
							mRegCodeRemain--;
							if (mRegCodeRemain >= 0) {
								Intent broadcast = new Intent();
								broadcast.putExtra(MainService.REMAIN_REGISTER,
										mRegCodeRemain);
								broadcast.setAction(MainService.ACTION_REMAIN);
								mServiceEvent.sendBroadcast(broadcast);
							}
							if (mRegCodeRemain <= 0) {
								mRegCodeRemain = 0;
								mRegTimer.cancel();
							}
						}
					}, 0, 1000);
				}
			} else if (usage.equals("login")) {
				if (mLoginCodeRemain == 0) {
					mLoginCodeRemain = 60;
					mLoginTimer = new Timer();
					mLoginTimer.schedule(new TimerTask() {
						@Override
						public void run() {
							mLoginCodeRemain--;
							if (mLoginCodeRemain >= 0) {
								Intent broadcast = new Intent();
								broadcast.putExtra(MainService.REMAIN_LOGIN,
										mLoginCodeRemain);
								broadcast.setAction(MainService.ACTION_REMAIN);
								mServiceEvent.sendBroadcast(broadcast);
							}
							if (mLoginCodeRemain <= 0) {
								mLoginCodeRemain = 0;
								mLoginTimer.cancel();
							}
						}
					}, 0, 1000);
				}
			}
		}
	}

	protected void cancelRemain() {
		if (mRegTimer != null) {
			mRegTimer.cancel();
		}
		if (mLoginTimer != null) {
			mLoginTimer.cancel();
		}
		lastRegisterPhone = "";
		mLoginCodeRemain = 0;
		mRegCodeRemain = 0;

	}
	
	public interface RemainListener {
		public String setRemainType();

		public void remain(int remain);
	}

}
