package cn.buaa.myweixin.api;

import java.util.Map;

import cn.buaa.myweixin.listener.ResponseListener;

public interface CircleManager {
	public void modify(Map<String, String> param,
			ResponseListener responseListener);
	public void delete(Map<String, String> param,
			ResponseListener responseListener);
}
