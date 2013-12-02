package com.lejoying.mc.entity;

import org.json.JSONException;
import org.json.JSONObject;

public class Friend extends Account {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String friendStatus;
	
	public Friend() {
		super();
	}

	public Friend(JSONObject jaccount) {
		super(jaccount);
		try {
			this.friendStatus = jaccount.getString("friendStatus");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public Friend(String phone, String head, String nickName,
			String mainBusiness, String status, String accessKey) {
		super(phone, head, nickName, mainBusiness, status, accessKey);
	}

	public String getFriendStatus() {
		return friendStatus;
	}

	public void setFriendStatus(String friendStatus) {
		this.friendStatus = friendStatus;
	}

	
}
