package cn.buaa.myweixin.apiutils;

import java.io.Serializable;
import java.util.List;

public class Account implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String phone;
	private String head;
	private String nickName;
	private String mainBusiness;
	private String status;
	private String accessKey;
	private List<Account> friends;
	private List<Community> myCommunitys;

	public Account() {
		super();
		// TODO Auto-generated constructor stub
	}

	public Account(String phone, String head, String nickName,
			String mainBusiness, String status, String accessKey,
			List<Account> friends, List<Community> myCommunitys) {
		super();
		this.phone = phone;
		this.head = head;
		this.nickName = nickName;
		this.mainBusiness = mainBusiness;
		this.status = status;
		this.accessKey = accessKey;
		this.friends = friends;
		this.myCommunitys = myCommunitys;
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

	public List<Account> getFriends() {
		return friends;
	}

	public void setFriends(List<Account> friends) {
		this.friends = friends;
	}

	public List<Community> getMyCommunitys() {
		return myCommunitys;
	}

	public void setMyCommunitys(List<Community> myCommunitys) {
		this.myCommunitys = myCommunitys;
	}

}
