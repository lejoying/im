package com.lejoying.wxgs.app.data.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class SquareMessage implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public String gmid = "";
	public String sendType = "";// "square"||"group"||"point"
	public List<String> messageTypes = new ArrayList<String>();;// "精华"||"活动"||"吐槽"
	public String contentType = "";// "text"||"image"||"voice"||"voiceandimage"||"textandimage"||"textandvoice"||"vit"
	public String phone = "";
	public String nickName = "";
	public String head = "";
	public String cover = "none";// "none"|| "voice"|| "imageName"
	public long time;
	public ArrayList<String> praiseusers = new ArrayList<String>();
	public ArrayList<Comment> comments = new ArrayList<Comment>();

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

		private boolean containsAll(Content c) {
			boolean flag = false;
			if (text.equals(c.text) && images.containsAll(c.images)
					&& voices.containsAll(c.voices)) {
				flag = true;
			}
			return flag;
		}
	}

	@Override
	public boolean equals(Object o) {
		boolean flag = false;
		if (o != null) {
			try {
				SquareMessage m = (SquareMessage) o;

				if (content.containsAll(m.content) && time == (m.time)
						&& sendType.equals(m.sendType) && gmid.equals(m.gmid)
						&& contentType.equals(m.contentType)
						&& messageTypes.containsAll(m.messageTypes)
						&& phone.equals(m.phone) && nickName.equals(m.nickName)) {
					flag = true;
				}
			} catch (Exception e) {

			}
		}
		return flag;
	}
}
