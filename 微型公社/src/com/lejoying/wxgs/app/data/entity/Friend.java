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
	public String longitude;
	public String latitude;
	public List<Message> messages = new ArrayList<Message>();

	@Override
	public boolean equals(Object o) {
		boolean flag = false;
		if (o != null) {
			if (o instanceof Friend) {
				Friend f = (Friend) o;
				if (phone.equals(f.phone)) {
					flag = true;
				}
			} else if (o instanceof String) {
				String s = (String) o;
				if (phone.equals(s)) {
					flag = true;
				}
			}
		}
		return flag;
	}

}
