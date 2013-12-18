package com.lejoying.data;

import java.io.Serializable;

public class Message implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public String type;
	public String content;
	public String time;
	public String messageType;
	public int isRead;
}
