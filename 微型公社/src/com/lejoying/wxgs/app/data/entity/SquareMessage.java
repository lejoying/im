package com.lejoying.wxgs.app.data.entity;

import java.io.Serializable;
import java.util.ArrayList;

public class SquareMessage implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public String gmid = "";
	public String sendType = "";// "square"||"group"||"point"
	public String messageType = "";// "精华"||"活动"||"吐槽"
	public String contentType = "";// "text"||"image"||"voice"||"voiceandimage"||"textandimage"||"textandvoice"||"vit"
	public ArrayList<Content> content = new ArrayList<Content>();
	public String phone = "";
	public String nickName = "";
	public String head = "";
	public long time;
	public ArrayList<String> praiseusers = new ArrayList<String>();
	public int style;// "11"|| "12" || "21"

	public static Content obtainContent(String type, String details) {
		return new Content(type, details);
	}

}

class Content implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	String type = "";
	String details = "";

	public Content(String type, String details) {
		super();
		this.type = type;
		this.details = details;
	}

}
