package cn.buaa.myweixin.apiutils;

import java.io.Serializable;

import org.json.JSONException;
import org.json.JSONObject;

public class Account implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int uid;
	private String phone;
	private String head;
	private String nickName;
	private String mainBusiness;
	private String status;
	private String accessKey;

	public Account() {
		super();
	}

	public Account(JSONObject jaccount) {

		try {
			this.uid = jaccount.getInt("uid");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		try {
			this.phone = jaccount.getString("phone");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		try {
			this.head = jaccount.getString("head");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		try {
			this.nickName = jaccount.getString("nickName");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		try {
			this.mainBusiness = jaccount.getString("mainBusiness");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		try {
			this.status = jaccount.getString("status");
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	public Account(String phone, String head, String nickName,
			String mainBusiness, String status, String accessKey) {
		super();
		this.phone = phone;
		this.head = head;
		this.nickName = nickName;
		this.mainBusiness = mainBusiness;
		this.status = status;
		this.accessKey = accessKey;
	}

	public int getUid() {
		return uid;
	}

	public void setUid(int uid) {
		this.uid = uid;
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

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getAccessKey() {
		return accessKey;
	}

	public void setAccessKey(String accessKey) {
		this.accessKey = accessKey;
	}
}
