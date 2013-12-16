package com.lejoying.mc.entity;

import java.io.Serializable;

import org.json.JSONException;
import org.json.JSONObject;

class Account implements Serializable {

	private static final long serialVersionUID = 1L;
	private String phone;
	private String nickName;
	private String mainBusiness;
	private String head;

	public Account() {
		super();
	}

	public Account(JSONObject jaccount) {

		try {
			this.phone = jaccount.getString("phone");
		} catch (JSONException e) {
		}
		try {
			this.head = jaccount.getString("head");
		} catch (JSONException e) {
		}
		try {
			this.nickName = jaccount.getString("nickName");
		} catch (JSONException e) {
		}
		try {
			this.mainBusiness = jaccount.getString("mainBusiness");
		} catch (JSONException e) {
		}
	}

	public Account(String phone, String head, String nickName,
			String mainBusiness) {
		super();
		this.phone = phone;
		this.head = head;
		this.nickName = nickName;
		this.mainBusiness = mainBusiness;
	}

	public void append(Account account) {
		if (account.getHead() != null && !account.getHead().equals("")) {
			this.head = account.getHead();
		}
		if (account.getMainBusiness() != null
				&& !account.getMainBusiness().equals("")) {
			this.mainBusiness = account.getMainBusiness();
		}
		if (account.getNickName() != null && !account.getNickName().equals("")) {
			this.nickName = account.getNickName();
		}
		if (account.getPhone() != null && !account.getPhone().equals("")) {
			this.phone = account.getPhone();
		}
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getHead() {
		return head;
	}

	public void setHead(String head) {
		this.head = head;
	}

	public String getNickName() {
		return nickName;
	}

	public void setNickName(String nickName) {
		this.nickName = nickName;
	}

	public String getMainBusiness() {
		return mainBusiness;
	}

	public void setMainBusiness(String mainBusiness) {
		this.mainBusiness = mainBusiness;
	}

	@Override
	public boolean equals(Object o) {
		boolean flag = false;
		if (o != null) {
			try {
				if (this == o) {
					flag = true;
				} else {
					Account a = (Account) o;
					if (head.equals(a.head)
							&& mainBusiness.equals(a.mainBusiness)
							&& nickName.equals(a.nickName)
							&& phone.equals(a.phone)) {
						flag = true;
					}
				}
			} catch (Exception e) {
				flag = false;
			}
		}
		return flag;
	}

}
