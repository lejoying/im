package com.lejoying.mc.apiimpl;

import java.util.Map;

import android.content.Context;

import com.lejoying.mc.api.RelationManager;
import com.lejoying.mc.listener.ResponseListener;
import com.lejoying.mc.utils.MCTools;
import com.lejoying.utils.HttpTools;

public class RelationManagerImpl implements RelationManager {

	private Context context;

	public RelationManagerImpl(Context context) {
		super();
		this.context = context;
	}

	@Override
	public void addfriend(Map<String, String> param,
			ResponseListener responseListener) {
		MCTools.ajax(context, "/api2/relation/addfriend", param, true,
				HttpTools.SEND_POST, 5000, responseListener);
	}

	@Override
	public void getfriends(Map<String, String> param,
			ResponseListener responseListener) {
		MCTools.ajax(context, "/api2/relation/getfriends", param, true,
				HttpTools.SEND_POST, 5000, responseListener);
	}

	@Override
	public void getcommunities(Map<String, String> param,
			ResponseListener responseListener) {
		MCTools.ajax(context, "/api2/relation/getcommnities", param, true,
				HttpTools.SEND_POST, 5000, responseListener);
	}

	@Override
	public void addcircle(Map<String, String> param,
			ResponseListener responseListener) {
		MCTools.ajax(context, "/api2/relation/addcircle", param, true,
				HttpTools.SEND_POST, 5000, responseListener);
	}

	@Override
	public void getcirclesandfriends(Map<String, String> param,
			ResponseListener responseListener) {
		MCTools.ajax(context, "/api2/relation/getcirclesandfriends", param,
				true, HttpTools.SEND_POST, 5000, responseListener);
	}

	@Override
	public void getaskfriends(Map<String, String> param,
			ResponseListener responseListener) {
		MCTools.ajax(context, "/api2/relation/getaskfriends", param, true,
				HttpTools.SEND_POST, 5000, responseListener);
	}

	@Override
	public void addfriendagree(Map<String, String> param,
			ResponseListener responseListener) {
		MCTools.ajax(context, "/api2/relation/addfriendagree", param, true,
				HttpTools.SEND_POST, 5000, responseListener);
	}

}
