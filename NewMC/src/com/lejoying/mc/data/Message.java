package com.lejoying.mc.data;

import java.io.Serializable;

public class Message implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	// type is receive or send
	public String type;
	public String content;
	public String time;
	public String messageType;
}
