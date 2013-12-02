package com.lejoying.mc.apiimpl;

import android.content.Context;
import android.os.Bundle;

import com.lejoying.mc.api.RelationManager;
import com.lejoying.mc.listener.ResponseListener;
import com.lejoying.mc.utils.MCNetTools;
import com.lejoying.utils.HttpTools;

public class RelationManagerImpl implements RelationManager {

	private Context context;

	public RelationManagerImpl(Context context) {
		super();
		this.context = context;
	}

	@Override
	public void addfriend(Bundle params,
			ResponseListener responseListener) {
		MCNetTools.ajax(context, "/api2/relation/addfriend", params, true,
				HttpTools.SEND_POST, 5000, responseListener);
	}

	@Override
	public void getfriends(Bundle params,
			ResponseListener responseListener) {
		MCNetTools.ajax(context, "/api2/relation/getfriends", params, true,
				HttpTools.SEND_POST, 5000, responseListener);
	}

	@Override
	public void getcommunities(Bundle params,
			ResponseListener responseListener) {
		MCNetTools.ajax(context, "/api2/relation/getcommnities", params, true,
				HttpTools.SEND_POST, 5000, responseListener);
	}

	@Override
	public void addcircle(Bundle params,
			ResponseListener responseListener) {
		MCNetTools.ajax(context, "/api2/relation/addcircle", params, true,
				HttpTools.SEND_POST, 5000, responseListener);
	}

	@Override
	public void getcirclesandfriends(Bundle params,
			ResponseListener responseListener) {
		MCNetTools.ajax(context, "/api2/relation/getcirclesandfriends", params,
				true, HttpTools.SEND_POST, 5000, responseListener);
	}

	@Override
	public void getaskfriends(Bundle params,
			ResponseListener responseListener) {
		MCNetTools.ajax(context, "/api2/relation/getaskfriends", params, true,
				HttpTools.SEND_POST, 5000, responseListener);
	}

	@Override
	public void addfriendagree(Bundle params,
			ResponseListener responseListener) {
		MCNetTools.ajax(context, "/api2/relation/addfriendagree", params, true,
				HttpTools.SEND_POST, 5000, responseListener);
	}

}
