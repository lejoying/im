package com.lejoying.mcutils;

import org.json.JSONException;
import org.json.JSONObject;

public class Circle {
	private int rid;
	private String name;
	
	public Circle() {
		super();
		// TODO Auto-generated constructor stub
	}
	public Circle(JSONObject jcircle){
		super();
		try {
			this.rid = jcircle.getInt("rid");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			this.name = jcircle.getString("name");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
	@Override
	public String toString() {
		return "Circle [rid=" + rid + ", name=" + name + "]";
	}
	
	
}
