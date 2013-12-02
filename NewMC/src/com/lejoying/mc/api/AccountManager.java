package com.lejoying.mc.api;

import android.os.Bundle;

import com.lejoying.mc.listener.ResponseListener;

public interface AccountManager {
	public void verifyphone(Bundle params,
			ResponseListener responseListener);

	public void verifycode(Bundle params,
			ResponseListener responseListener);

	public void auth(Bundle params,
			ResponseListener responseListener);

	public void exit(Bundle params,
			ResponseListener responseListener);
	
	public void verifywebcode(Bundle params,
			ResponseListener responseListener);
	
	public void verifywebcodelogin(Bundle params,
			ResponseListener responseListener);
	
	public void getaccount(Bundle params,
			ResponseListener responseListener);

	public void modify(Bundle params,
			ResponseListener responseListener);
	
}
