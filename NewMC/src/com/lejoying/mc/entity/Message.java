package com.lejoying.mc.entity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.lejoying.mc.utils.MCDataTools;

public class Message {
	private int id;
	private Friend friend;
	private String type;
	private String content;
	private String time;
	private String messageType;
	private int isRead;

	public Message() {
		super();
		// TODO Auto-generated constructor stub
	}

	public Message(JSONObject message) {
		try {
			String phoneSend = message.getString("phone");
			String phoneReceive = new JSONArray(message.getString("phoneto"))
					.getString(0);
			friend = new Friend();
			if (phoneSend.equals(MCDataTools.getLoginedUser(null).getPhone())) {
				friend.setPhone(phoneReceive);
				this.type = "send";
			} else if (phoneReceive.equals(MCDataTools.getLoginedUser(null)
					.getPhone())) {
				friend.setPhone(phoneSend);
				this.type = "receive";
			}
		} catch (JSONException e) {
		}

		try {
			this.time = message.getString("time");
		} catch (JSONException e) {
		}

		try {
			this.messageType = message.getString("type");
		} catch (JSONException e) {
		}

		try {
			this.content = new JSONObject(message.getString("content"))
					.getString(messageType);
		} catch (JSONException e) {
		}
	}

	public Message(int mid, Friend friend, String type, String content,
			long time, String messageType) {
		super();
		this.id = mid;
		this.friend = friend;
		this.type = type;
		this.content = content;
		this.time = String.valueOf(time);
		this.messageType = messageType;
	}

	public Friend getFriend() {
		return friend;
	}

	public void setFriend(Friend friend) {
		this.friend = friend;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public void setTime(long time) {
		this.time = String.valueOf(time);
	}

	public String getMessageType() {
		return messageType;
	}

	public void setMessageType(String messageType) {
		this.messageType = messageType;
	}

	public int getIsRead() {
		return isRead;
	}

	public void setIsRead(int isRead) {
		this.isRead = isRead;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

}
