package com.lejoying.api;

import java.util.Map;

import com.lejoying.listener.ResponseListener;

public interface AccountManager {
	public void verifyphone(Map<String, String> param,
			ResponseListener responseListener);

	public void verifycode(Map<String, String> param,
			ResponseListener responseListener);

	public void verifyloginphone(Map<String, String> param,
			ResponseListener responseListener);
	
	public void verifylogincode(Map<String, String> param,
			ResponseListener responseListener);
	
	public void verifypass(Map<String, String> param,
			ResponseListener responseListener);

	public void auth(Map<String, String> param,
			ResponseListener responseListener);

	public void exit(Map<String, String> param,
			ResponseListener responseListener);
	
	public void verifywebcode(Map<String, String> param,
			ResponseListener responseListener);
	
	public void verifywebcodelogin(Map<String, String> param,
			ResponseListener responseListener);
	
	public void getaccount(Map<String, String> param,
			ResponseListener responseListener);

	public void modify(Map<String, String> param,
			ResponseListener responseListener);
	
}
