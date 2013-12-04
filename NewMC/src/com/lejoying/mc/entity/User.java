package com.lejoying.mc.entity;

import org.json.JSONObject;

public class User extends Account {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String accessKey;

	private String flag = "none";

	public User() {
		super();
	}

	public User(JSONObject jaccount) {
		super(jaccount);
	}

	public User(String phone, String head, String nickName, String mainBusiness) {
		super(phone, head, nickName, mainBusiness);
	}

	public String getAccessKey() {
		return accessKey;
	}

	public void setAccessKey(String accessKey) {
		this.accessKey = accessKey;
	}

	public String getFlag() {
		return flag;
	}

	public void setFlag(String flag) {
		this.flag = flag;
	}

}
