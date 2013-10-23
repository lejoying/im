package cn.buaa.myweixin.apiimpl;

import java.util.Map;

import android.app.Activity;

import cn.buaa.myweixin.api.AccountManager;
import cn.buaa.myweixin.apiutils.MCTools;
import cn.buaa.myweixin.listener.ResponseListener;
import cn.buaa.myweixin.utils.HttpTools;

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
	public void verifypass(Map<String, String> param,
			ResponseListener responseListener) {
		MCTools.ajax(activity, "/api2/account/verifypass", param, true,
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
	public void verifyloginphone(Map<String, String> param,
			ResponseListener responseListener) {
		MCTools.ajax(activity, "/api2/account/verifyloginphone", param, true,
				HttpTools.SEND_POST, 5000, responseListener);
	}

	@Override
	public void verifylogincode(Map<String, String> param,
			ResponseListener responseListener) {
		MCTools.ajax(activity, "/api2/account/verifylogincode", param, true,
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

}