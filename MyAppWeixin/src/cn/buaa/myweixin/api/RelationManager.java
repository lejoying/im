package cn.buaa.myweixin.api;

import java.util.Map;

import cn.buaa.myweixin.listener.ResponseListener;

public interface RelationManager {
	public void join(Map<String, String> param,
			ResponseListener responseListener);

	public void addfriend(Map<String, String> param,
			ResponseListener responseListener);

	public void getfriends(Map<String, String> param,
			ResponseListener responseListener);

	public void getcommunities(Map<String, String> param,
			ResponseListener responseListener);
}
