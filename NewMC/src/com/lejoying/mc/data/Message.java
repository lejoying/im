package com.lejoying.mc.data;

import java.io.Serializable;

public class Message implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public static final int MESSAGE_TYPE_SEND = 0x01;
	public static final int MESSAGE_TYPE_RECEIVE = 0x02;

	// type is receive or send
	public int type;
	public String content = "";
	public String time = "";
	public String messageType = "";
	public String status;
	public String friendPhone;

	@Override
	public boolean equals(Object o) {
		boolean flag = false;
		if (o != null) {
			try {
				Message m = (Message) o;
				if (type == m.type && content.equals(m.content) && time.equals(m.time) && messageType.equals(m.messageType) && status.equals(m.status) && friendPhone.equals(m.status)) {
					flag = true;
				}
			} catch (Exception e) {

			}
		}
		return flag;
	}
}
