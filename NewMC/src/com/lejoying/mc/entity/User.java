package com.lejoying.mc.entity;

import org.json.JSONException;
import org.json.JSONObject;

public class User extends Account {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String accessKey;

	public User() {
		super();
	}

	public User(JSONObject jaccount) {
		super(jaccount);
		try {
			this.accessKey = jaccount.getString("accessKey");
		} catch (JSONException e) {
		}
	}

	public User(String phone, String head, String nickName,
			String mainBusiness, String status, String accessKey) {
		super(phone, head, nickName, mainBusiness, status);
		this.accessKey = accessKey;
	}

	public String getAccessKey() {
		return accessKey;
	}

	public void setAccessKey(String accessKey) {
		this.accessKey = accessKey;
	}
	
	

}
