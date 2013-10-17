package cn.buaa.myweixin.api;

import java.util.Map;

import cn.buaa.myweixin.listener.ResponseListener;

public interface AccountManager {
	public void verifyphone(Map<String, String> param,
			ResponseListener responseListener);

	public void verifycode(Map<String, String> param,
			ResponseListener responseListener);

	public void verifypass(Map<String, String> param,
			ResponseListener responseListener);

	public void auth(Map<String, String> param,
			ResponseListener responseListener);

	public void exit(Map<String, String> param,
			ResponseListener responseListener);
}
