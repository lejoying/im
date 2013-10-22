package cn.buaa.myweixin.apiimpl;

import java.util.Map;

import android.app.Activity;

import cn.buaa.myweixin.api.Session;
import cn.buaa.myweixin.apiutils.MCTools;
import cn.buaa.myweixin.listener.ResponseListener;
import cn.buaa.myweixin.utils.HttpTools;

public class SessionImpl implements Session {

	private Activity activity;

	public SessionImpl(Activity activity) {
		this.activity = activity;
	}

	@Override
	public void event(Map<String, String> param,
			ResponseListener responseListener) {
		MCTools.ajax(activity, "/api2/session/event", param, true,
				HttpTools.SEND_POST, 30000, responseListener);
	}

}
