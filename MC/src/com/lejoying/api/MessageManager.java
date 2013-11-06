package com.lejoying.api;

import java.util.Map;

import com.lejoying.listener.ResponseListener;

public interface MessageManager {
	public void send(Map<String, String> param,
			ResponseListener responseListener);

	public void get(Map<String, String> param,
			ResponseListener responseListener);
}
