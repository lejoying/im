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
		// TODO Auto-generated constructor stub
	}

	public Friend(JSONObject jaccount) {
		super(jaccount);
		try {
			this.friendStatus = jaccount.getString("friendStatus");
		} catch (JSONException e) {
		}
	}

	public Friend(String phone, String head, String nickName,
			String mainBusiness, String friendStatus) {
		super(phone, head, nickName, mainBusiness);
		this.friendStatus = friendStatus;
	}

	public String getFriendStatus() {
		return friendStatus;
	}

	public void setFriendStatus(String friendStatus) {
		this.friendStatus = friendStatus;
	}

	@Override
	public boolean equals(Object o) {
		boolean flag = false;
		if (o != null) {
			try {
				Friend f = (Friend) o;
				if (friendStatus.equals(f.friendStatus)) {
					flag = true;
				}
			} catch (Exception e) {
				flag = false;
			}
		}
		return flag && super.equals(o);
	}

}
