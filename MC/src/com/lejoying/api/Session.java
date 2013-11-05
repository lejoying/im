package com.lejoying.api;

import java.util.Map;

import com.lejoying.listener.ResponseListener;

public interface Session {
	public void eventweb(Map<String, String> param,
			ResponseListener responseListener);
	public void event(Map<String, String> param,
			ResponseListener responseListener);
	
}
