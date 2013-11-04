package cn.buaa.myweixin.apiimpl;

import java.util.Map;

import android.app.Activity;

import cn.buaa.myweixin.api.MessageManager;
import cn.buaa.myweixin.apiutils.MCTools;
import cn.buaa.myweixin.listener.ResponseListener;
import cn.buaa.myweixin.utils.HttpTools;

public class MessageManagerImpl implements MessageManager {

	private Activity activity;

	public MessageManagerImpl(Activity activity) {
		this.activity = activity;
	}

	@Override
	public void send(Map<String, String> param,
			ResponseListener responseListener) {
		MCTools.ajax(activity, "/api2/message/send", param, false,
				HttpTools.SEND_POST, 30000, responseListener);
	}

	@Override
	public void get(Map<String, String> param, ResponseListener responseListener) {
		MCTools.ajax(activity, "/api2/message/get", param, false,
				HttpTools.SEND_POST, 30000, responseListener);
	}

}
