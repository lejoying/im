package com.lejoying.mc.apiimpl;

import android.content.Context;
import android.os.Bundle;

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
	public void find(Bundle params,
			ResponseListener responseListener) {
		MCNetTools.ajax(context, "/api2/community/find", params, true,
				HttpTools.SEND_GET, 5000, responseListener);
	}

	@Override
	public void getcommunityfriends(Bundle params,
			ResponseListener responseListener) {
		MCNetTools.ajax(context, "/api2/community/getcommunityfriends", params,
				true, HttpTools.SEND_GET, 5000, responseListener);
	}

	@Override
	public void getcommunities(Bundle params,
			ResponseListener responseListener) {
		MCNetTools.ajax(context, "/api2/community/getcommunities", params, true,
				HttpTools.SEND_GET, 5000, responseListener);
	}

	@Override
	public void join(Bundle params,
			ResponseListener responseListener) {
		MCNetTools.ajax(context, "/api2/community/join", params, true,
				HttpTools.SEND_GET, 5000, responseListener);
	}

	@Override
	public void unjoin(Bundle params,
			ResponseListener responseListener) {
		MCNetTools.ajax(context, "/api2/community/unjoin", params, true,
				HttpTools.SEND_GET, 5000, responseListener);
	}

}
