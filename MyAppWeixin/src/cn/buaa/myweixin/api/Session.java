package cn.buaa.myweixin.api;

import java.util.Map;

import cn.buaa.myweixin.listener.ResponseListener;

public interface Session {
	public void event(Map<String, String> param,
			ResponseListener responseListener);
	
}
