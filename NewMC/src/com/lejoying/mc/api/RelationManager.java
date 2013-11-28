package com.lejoying.mc.api;

import java.util.Map;

import com.lejoying.mc.listener.ResponseListener;

public interface RelationManager {
	
	public void addfriend(Map<String, String> param,
			ResponseListener responseListener);

	public void getfriends(Map<String, String> param,
			ResponseListener responseListener);

	public void addcircle(Map<String, String> param,
			ResponseListener responseListener);

	public void getcommunities(Map<String, String> param,
			ResponseListener responseListener);

	public void getcirclesandfriends(Map<String, String> param,
			ResponseListener responseListener);

	public void getaskfriends(Map<String, String> param,
			ResponseListener responseListener);

	public void addfriendagree(Map<String, String> param,
			ResponseListener responseListener);
}
