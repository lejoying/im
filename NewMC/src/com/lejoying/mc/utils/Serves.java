package com.lejoying.mc.utils;

import java.io.Serializable;

public class Serves implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String sid;
	private String name;
	public String getSid() {
		return sid;
	}
	public void setSid(String sid) {
		this.sid = sid;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
}
