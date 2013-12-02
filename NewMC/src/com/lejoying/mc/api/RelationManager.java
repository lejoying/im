package com.lejoying.mc.api;

import android.os.Bundle;

import com.lejoying.mc.listener.ResponseListener;

public interface RelationManager {
	
	public void addfriend(Bundle params,
			ResponseListener responseListener);

	public void getfriends(Bundle params,
			ResponseListener responseListener);

	public void addcircle(Bundle params,
			ResponseListener responseListener);

	public void getcommunities(Bundle params,
			ResponseListener responseListener);

	public void getcirclesandfriends(Bundle params,
			ResponseListener responseListener);

	public void getaskfriends(Bundle params,
			ResponseListener responseListener);

	public void addfriendagree(Bundle params,
			ResponseListener responseListener);
}
