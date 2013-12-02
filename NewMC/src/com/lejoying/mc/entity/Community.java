package com.lejoying.mc.entity;

import org.json.JSONException;
import org.json.JSONObject;


public class Community{
	
	private int cid;
	private String name;
	private String description;
	private Account agent;
	public Community() {
		super();
		// TODO Auto-generated constructor stub
	}
	public Community(int cid, String name, String description) {
		super();
		this.cid = cid;
		this.name = name;
		this.description = description;
	}
	
	public Community(JSONObject jcommunity){
		try {
			this.cid = jcommunity.getInt("cid");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			this.name = jcommunity.getString("name");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			this.description = jcommunity.getString("description");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			this.agent = new Account(jcommunity.getJSONObject("agent"));
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	public int getCid() {
		return cid;
	}
	public void setCid(int cid) {
		this.cid = cid;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public Account getAgent() {
		return agent;
	}
	public void setAgent(Account agent) {
		this.agent = agent;
	}
	@Override
	public String toString() {
		return "Community [cid=" + cid + ", name=" + name + ", description="
				+ description + ", agent=" + agent + "]";
	}
	
	
	
	
	
}
