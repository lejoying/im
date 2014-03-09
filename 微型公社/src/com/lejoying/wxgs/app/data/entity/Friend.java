package com.lejoying.wxgs.app.data.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Friend implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public String phone = "";
	public int distance;
	public String nickName = "";
	public String mainBusiness = "";
	public String head = "";
	public String friendStatus = "";
	public String addMessage = "";
	public boolean temp;
	public int notReadMessagesCount;
	public List<Message> messages = new ArrayList<Message>();

	@Override
	public boolean equals(Object o) {
		boolean flag = false;
		if (o != null) {
			try {
				Friend f = (Friend) o;
				if (phone.equals(f.phone) && nickName.equals(f.nickName)
						&& mainBusiness.equals(f.mainBusiness)
						&& head.equals(f.head)
						&& friendStatus.equals(f.friendStatus)
						&& temp == f.temp
						&& notReadMessagesCount == f.notReadMessagesCount
						&& messages.containsAll(f.messages)) {
					flag = true;
				}
			} catch (Exception e) {

			}
		}
		return flag;
	}

}
