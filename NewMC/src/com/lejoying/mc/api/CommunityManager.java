package com.lejoying.mc.api;

import android.os.Bundle;

import com.lejoying.mc.listener.ResponseListener;

public interface CommunityManager {
	public void find(Bundle params,
			ResponseListener responseListener);

	public void join(Bundle params,
			ResponseListener responseListener);

	public void unjoin(Bundle params,
			ResponseListener responseListener);

	public void getcommunities(Bundle params,
			ResponseListener responseListener);

	public void getcommunityfriends(Bundle params,
			ResponseListener responseListener);

}
