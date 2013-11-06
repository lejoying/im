package com.lejoying.apiimpl;

import java.util.Map;

import android.app.Activity;

import com.lejoying.api.CommunityManager;
import com.lejoying.listener.ResponseListener;
import com.lejoying.mcutils.MCTools;
import com.lejoying.utils.HttpTools;

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

	@Override
	public void join(Map<String, String> param,
			ResponseListener responseListener) {
		MCTools.ajax(activity, "/api2/community/join", param,
				true, HttpTools.SEND_GET, 5000, responseListener);
	}

	@Override
	public void unjoin(Map<String, String> param,
			ResponseListener responseListener) {
		MCTools.ajax(activity, "/api2/community/unjoin", param,
				true, HttpTools.SEND_GET, 5000, responseListener);
	}

}
