package cn.buaa.myweixin.apiimpl;

import java.util.Map;

import android.app.Activity;
import cn.buaa.myweixin.api.CircleManager;
import cn.buaa.myweixin.apiutils.MCTools;
import cn.buaa.myweixin.listener.ResponseListener;
import cn.buaa.myweixin.utils.HttpTools;

public class CircleManagerImpl implements CircleManager {

	private Activity activity;

	public CircleManagerImpl(Activity activity) {
		super();
		this.activity = activity;
	}
	
	@Override
	public void modify(Map<String, String> param,
			ResponseListener responseListener) {
		MCTools.ajax(activity, "/api2/circle/modify", param, true,
				HttpTools.SEND_POST, 5000, responseListener);
	}

	@Override
	public void delete(Map<String, String> param,
			ResponseListener responseListener) {
		MCTools.ajax(activity, "/api2/circle/delete", param, true,
				HttpTools.SEND_POST, 5000, responseListener);		
	}

}
