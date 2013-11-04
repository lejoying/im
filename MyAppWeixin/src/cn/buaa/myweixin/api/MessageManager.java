package cn.buaa.myweixin.api;

import java.util.Map;

import cn.buaa.myweixin.listener.ResponseListener;

public interface MessageManager {
	public void send(Map<String, String> param,
			ResponseListener responseListener);

	public void get(Map<String, String> param,
			ResponseListener responseListener);
}
