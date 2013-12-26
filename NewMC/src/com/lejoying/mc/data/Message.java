package com.lejoying.mc.data;

import java.io.Serializable;

public class Message implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	// type is receive or send
	public String type = "";
	public String content = "";
	public String time = "";
	public String messageType = "";

	@Override
	public boolean equals(Object o) {
		boolean flag = false;
		if (o != null) {
			try {
				Message m = (Message) o;
				if (type.equals(m.type) && content.equals(m.content)
						&& time.equals(m.time)
						&& messageType.equals(m.messageType)) {
					flag = true;
				}
			} catch (Exception e) {

			}
		}
		return flag;
	}
}
