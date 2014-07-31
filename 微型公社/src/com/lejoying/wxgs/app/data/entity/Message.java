package com.lejoying.wxgs.app.data.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Message implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public static final int MESSAGE_TYPE_SEND = 0x01;
	public static final int MESSAGE_TYPE_RECEIVE = 0x02;

	// type is receive or send
	public int type;
	// public String content="";
	public ArrayList<String> content = new ArrayList<String>();
	public String time = "";
	public String sendType = "";
	public String gid = "";
	public String contentType = "";
	public String status = "";
	public String phone = "";
	public String nickName = "";

	@Override
	public String toString() {
		return "Message [type=" + type + ", content=" + content + ", time="
				+ time + ", sendType=" + sendType + ", gid=" + gid
				+ ", contentType=" + contentType + ", status=" + status
				+ ", phone=" + phone + ", nickName=" + nickName + "]";
	}

	@Override
	public boolean equals(Object o) {
		boolean flag = false;
		if (o != null) {
			try {
				Message m = (Message) o;

				if (type == m.type && content.containsAll(m.content)
						&& time.equals(m.time) && sendType.equals(m.sendType)
						&& gid.equals(m.gid)
						&& contentType.equals(m.contentType)
						&& status.equals(m.status) && phone.equals(m.phone)
						&& nickName.equals(m.nickName)) {
					flag = true;
				}
			} catch (Exception e) {

			}
		}
		return flag;
	}
}
