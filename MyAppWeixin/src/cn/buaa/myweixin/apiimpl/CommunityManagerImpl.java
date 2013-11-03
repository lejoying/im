package cn.buaa.myweixin.apiimpl;

import java.util.Map;

import android.app.Activity;

import cn.buaa.myweixin.api.CommunityManager;
import cn.buaa.myweixin.apiutils.MCTools;
import cn.buaa.myweixin.listener.ResponseListener;
import cn.buaa.myweixin.utils.HttpTools;

public class CommunityManagerImpl implements CommunityManager {

	private Activity activity;

	public CommunityManagerImpl(Activity activity) {
		super();
		this.activity = activity;
	}

	@Override
	public void find(Map<String, String> param,
			ResponseListener responseListener) {
		MCTools.ajax(activity, "/api2/community/find", param, true,
				HttpTools.SEND_GET, 5000, responseListener);
	}

	@Override
	public void getcommunityfriends(Map<String, String> param,
			ResponseListener responseListener) {
		MCTools.ajax(activity, "/api2/community/getcommunityfriends", param,
				true, HttpTools.SEND_GET, 5000, responseListener);
	}

	@Override
	public void getcommunities(Map<String, String> param,
			ResponseListener responseListener) {
		MCTools.ajax(activity, "/api2/community/getcommunities", param,
				true, HttpTools.SEND_GET, 5000, responseListener);
	}

}
