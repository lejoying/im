package com.lejoying.mc.data.handler;

import org.json.JSONException;
import org.json.JSONObject;

import com.lejoying.mc.data.App;
import com.lejoying.mc.data.StaticData;
import com.lejoying.mc.data.User;

public class DataHandler2 {
	App app = App.getInstance();
	
	public int updateUser(JSONObject jUser, StaticData data) {
		app.isDataChanged = true;
		int count = 0;
		User user = generateUserFromJSON(jUser);
		if (user.head != null && !user.head.equals("")) {
			data.user.head = user.head;
			count++;
		}
		if (user.mainBusiness != null && !user.mainBusiness.equals("")) {
			data.user.mainBusiness = user.mainBusiness;
			count++;
		}
		if (user.nickName != null && !user.nickName.equals("")) {
			data.user.nickName = user.nickName;
			count++;
		}
		return count;
	}
	
	public User generateUserFromJSON(JSONObject jUser) {
		User user = new User();
		try {
			user.phone = jUser.getString("phone");
		} catch (JSONException e) {
		}
		try {
			user.head = jUser.getString("head");
		} catch (JSONException e) {
		}
		try {
			user.nickName = jUser.getString("nickName");
		} catch (JSONException e) {
		}
		try {
			user.mainBusiness = jUser.getString("mainBusiness");
		} catch (JSONException e) {
		}
		return user;
	}
}
