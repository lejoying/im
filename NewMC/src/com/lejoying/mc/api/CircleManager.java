package com.lejoying.mc.api;

import java.util.Map;

import com.lejoying.mc.listener.ResponseListener;

public interface CircleManager {
	public void modify(Map<String, String> param,
			ResponseListener responseListener);
	public void delete(Map<String, String> param,
			ResponseListener responseListener);
}
