package com.lejoying.mc.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Friend implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public String phone;
	public String nickName;
	public String mainBusiness;
	public String head;
	public String friendStatus;
	public int notReadMessagesCount;
	public List<Message> messages = new ArrayList<Message>();
}
