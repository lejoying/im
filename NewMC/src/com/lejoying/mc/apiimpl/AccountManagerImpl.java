package com.lejoying.mc.apiimpl;

import android.content.Context;
import android.os.Bundle;

import com.lejoying.mc.api.AccountManager;
import com.lejoying.mc.listener.ResponseListener;
import com.lejoying.mc.utils.MCNetTools;
import com.lejoying.utils.HttpTools;

public class AccountManagerImpl implements AccountManager {

	private Context context;

	public AccountManagerImpl(Context context) {
		super();
		this.context = context;
	}

	@Override
	public void verifyphone(Bundle params,
			final ResponseListener responseListener) {
		MCNetTools.ajax(context, "/api2/account/verifyphone", params, true,
				HttpTools.SEND_POST, 5000, responseListener);
	}

	@Override
	public void verifycode(Bundle params,
			ResponseListener responseListener) {
		MCNetTools.ajax(context, "/api2/account/verifycode", params, true,
				HttpTools.SEND_POST, 5000, responseListener);
	}

	@Override
	public void auth(Bundle params,
			ResponseListener responseListener) {
		MCNetTools.ajax(context, "/api2/account/auth", params, true,
				HttpTools.SEND_POST, 5000, responseListener);
	}

	@Override
	public void exit(Bundle params,
			ResponseListener responseListener) {
		MCNetTools.ajax(context, "/api2/account/exit", params, true,
				HttpTools.SEND_POST, 5000, responseListener);
	}

	@Override
	public void verifywebcode(Bundle params,
			ResponseListener responseListener) {
		MCNetTools.ajax(context, "/api2/account/verifywebcode", params, true,
				HttpTools.SEND_POST, 5000, responseListener);
	}

	@Override
	public void verifywebcodelogin(Bundle params,
			ResponseListener responseListener) {
		MCNetTools.ajax(context, "/api2/account/verifywebcodelogin", params, true,
				HttpTools.SEND_POST, 5000, responseListener);
	}

	@Override
	public void modify(Bundle params,
			ResponseListener responseListener) {
		MCNetTools.ajax(context, "/api2/account/modify", params, true,
				HttpTools.SEND_POST, 5000, responseListener);
	}

	@Override
	public void getaccount(Bundle params,
			ResponseListener responseListener) {
		MCNetTools.ajax(context, "/api2/account/getaccount", params, true,
				HttpTools.SEND_POST, 5000, responseListener);
	}

}
