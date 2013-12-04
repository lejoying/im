package com.lejoying.mc.entity;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Circle {
	private int rid;
	private String name;
	private List<Friend> friends;

	public Circle() {
		super();
		// TODO Auto-generated constructor stub
	}

	public Circle(JSONObject jcircle) {
		super();
		friends = new ArrayList<Friend>();
		try {
			this.rid = jcircle.getInt("rid");
		} catch (JSONException e) {
		}
		try {
			this.name = jcircle.getString("name");
		} catch (JSONException e) {
		}
		try {
			friends = new ArrayList<Friend>();
			JSONArray accounts = jcircle.getJSONArray("accounts");
			for (int i = 0; i < accounts.length(); i++) {
				Friend friend = new Friend(accounts.getJSONObject(i));
				friends.add(friend);
			}
		} catch (JSONException e) {
		}
	}

	public Circle(int rid, String name) {
		super();
		this.rid = rid;
		this.name = name;
	}

	public int getRid() {
		return rid;
	}

	public void setRid(int rid) {
		this.rid = rid;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<Friend> getFriends() {
		return friends;
	}

	public void setFriends(List<Friend> friends) {
		this.friends = friends;
	}
	
	
}
