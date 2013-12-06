package com.lejoying.mc.entity;

import org.json.JSONObject;

public class User extends Account {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String accessKey;

	private String pbKey;

	private boolean now;

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

	public boolean isNow() {
		return now;
	}

	public void setNow(boolean now) {
		this.now = now;
	}

	public String getPbKey() {
		return pbKey;
	}

	public void setPbKey(String pbKey) {
		this.pbKey = pbKey;
	}

}
