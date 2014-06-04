package com.lejoying.wxgs.app.data.entity;

import java.io.Serializable;

public class Comment implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public String phone;
	public String nickName;
	public String head;
	public String phoneTo;
	public String nickNameTo;
	public String headTo;
	public String contentType = "";// text||image||voice
	public String content;
	public long time;
}
