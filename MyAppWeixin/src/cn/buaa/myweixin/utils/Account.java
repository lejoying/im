package cn.buaa.myweixin.utils;

import java.io.Serializable;
import java.util.List;

public class Account implements Serializable {
	private String phone;
	private String head;
	private String nickName;
	private String mainBusiness;
	private String status; 
	private List<Account> friends;
	private List<Community> myCommunitys;
	
	public Account() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	

	public Account(String phone, String head, String nickName,
			String mainBusiness, String status, List<Account> friends,
			List<Community> myCommunitys) {
		super();
		this.phone = phone;
		this.head = head;
		this.nickName = nickName;
		this.mainBusiness = mainBusiness;
		this.status = status;
		this.friends = friends;
		this.myCommunitys = myCommunitys;
	}



	@Override
	public String toString() {
		return "Account [phone=" + phone + ", head=" + head + ", nickName="
				+ nickName + ", mainBusiness=" + mainBusiness + ", status="
				+ status + ", friends=" + friends + ", myCommunitys="
				+ myCommunitys + "]";
	}



	public String getStatus() {
		return status;
	}



	public void setStatus(String status) {
		this.status = status;
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
