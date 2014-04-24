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
	public String phone = "";
	public String nickName = "";
	public String head = "";
	public String cover = "voice";// voice|| "imageName"
	public long time;
	public ArrayList<String> praiseusers = new ArrayList<String>();
	public ArrayList<String> comments = new ArrayList<String>();

	public Content content = new Content();

	public int style;// "11"|| "12" || "21"

	public static class Content implements Serializable {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		public ArrayList<String> images = new ArrayList<String>();
		public ArrayList<String> voices = new ArrayList<String>();
		public String text = "";

		public void addImage(String imageName) {
			this.images.add(imageName);
		}

		public void addVoice(String voiceName) {
			this.voices.add(voiceName);
		}
	}
}
