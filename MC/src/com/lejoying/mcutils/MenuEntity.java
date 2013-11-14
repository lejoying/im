package com.lejoying.mcutils;

public class MenuEntity {
	private int imageID;
	private String text;

	public MenuEntity() {
		super();
		// TODO Auto-generated constructor stub
	}

	public MenuEntity(int imageID, String text) {
		super();
		this.imageID = imageID;
		this.text = text;
	}

	public int getImageID() {
		return imageID;
	}

	public void setImageID(int imageID) {
		this.imageID = imageID;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

}
