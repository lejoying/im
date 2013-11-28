package com.lejoying.mc.apiimpl;

import java.util.Map;

import android.app.Activity;

import com.lejoying.mc.api.AccountManager;
import com.lejoying.mc.listener.ResponseListener;
import com.lejoying.mc.utils.MCTools;
import com.lejoying.utils.HttpTools;

public class AccountManagerImpl implements AccountManager {

	private Activity activity;

	public AccountManagerImpl(Activity activity) {
		super();
		this.activity = activity;
	}

	@Override
	public void verifyphone(Map<String, String> param,
			final ResponseListener responseListener) {
		MCTools.ajax(activity, "/api2/account/verifyphone", param, true,
				HttpTools.SEND_POST, 5000, responseListener);
	}

	@Override
	public void verifycode(Map<String, String> param,
			ResponseListener responseListener) {
		MCTools.ajax(activity, "/api2/account/verifycode", param, true,
				HttpTools.SEND_POST, 5000, responseListener);
	}

	@Override
	public void auth(Map<String, String> param,
			ResponseListener responseListener) {
		MCTools.ajax(activity, "/api2/account/auth", param, true,
				HttpTools.SEND_POST, 5000, responseListener);
	}

	@Override
	public void exit(Map<String, String> param,
			ResponseListener responseListener) {
		MCTools.ajax(activity, "/api2/account/exit", param, true,
				HttpTools.SEND_POST, 5000, responseListener);
	}

	@Override
	public void verifywebcode(Map<String, String> param,
			ResponseListener responseListener) {
		MCTools.ajax(activity, "/api2/account/verifywebcode", param, true,
				HttpTools.SEND_POST, 5000, responseListener);
	}

	@Override
	public void verifywebcodelogin(Map<String, String> param,
			ResponseListener responseListener) {
		MCTools.ajax(activity, "/api2/account/verifywebcodelogin", param, true,
				HttpTools.SEND_POST, 5000, responseListener);
	}

	@Override
	public void modify(Map<String, String> param,
			ResponseListener responseListener) {
		MCTools.ajax(activity, "/api2/account/modify", param, true,
				HttpTools.SEND_POST, 5000, responseListener);
	}

	@Override
	public void getaccount(Map<String, String> param,
			ResponseListener responseListener) {
		MCTools.ajax(activity, "/api2/account/getaccount", param, true,
				HttpTools.SEND_POST, 5000, responseListener);
	}

}
