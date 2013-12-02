package com.lejoying.mc.apiimpl;

import java.util.Map;

import android.content.Context;

import com.lejoying.mc.api.CommunityManager;
import com.lejoying.mc.listener.ResponseListener;
import com.lejoying.mc.utils.MCNetTools;
import com.lejoying.utils.HttpTools;

public class CommunityManagerImpl implements CommunityManager {

	private Context context;

	public CommunityManagerImpl(Context context) {
		super();
		this.context = context;
	}

	@Override
	public void find(Map<String, String> param,
			ResponseListener responseListener) {
		MCNetTools.ajax(context, "/api2/community/find", param, true,
				HttpTools.SEND_GET, 5000, responseListener);
	}

	@Override
	public void getcommunityfriends(Map<String, String> param,
			ResponseListener responseListener) {
		MCNetTools.ajax(context, "/api2/community/getcommunityfriends", param,
				true, HttpTools.SEND_GET, 5000, responseListener);
	}

	@Override
	public void getcommunities(Map<String, String> param,
			ResponseListener responseListener) {
		MCNetTools.ajax(context, "/api2/community/getcommunities", param, true,
				HttpTools.SEND_GET, 5000, responseListener);
	}

	@Override
	public void join(Map<String, String> param,
			ResponseListener responseListener) {
		MCNetTools.ajax(context, "/api2/community/join", param, true,
				HttpTools.SEND_GET, 5000, responseListener);
	}

	@Override
	public void unjoin(Map<String, String> param,
			ResponseListener responseListener) {
		MCNetTools.ajax(context, "/api2/community/unjoin", param, true,
				HttpTools.SEND_GET, 5000, responseListener);
	}

}
