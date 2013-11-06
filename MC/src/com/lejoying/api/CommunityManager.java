package com.lejoying.api;

import java.util.Map;

import com.lejoying.listener.ResponseListener;

public interface CommunityManager {
	public void find(Map<String, String> param,
			ResponseListener responseListener);

	public void join(Map<String, String> param,
			ResponseListener responseListener);

	public void unjoin(Map<String, String> param,
			ResponseListener responseListener);

	public void getcommunities(Map<String, String> param,
			ResponseListener responseListener);

	public void getcommunityfriends(Map<String, String> param,
			ResponseListener responseListener);

}
