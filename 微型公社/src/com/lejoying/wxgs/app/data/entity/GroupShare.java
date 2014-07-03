package com.lejoying.wxgs.app.data.entity;

import java.io.Serializable;
import java.util.ArrayList;

public class GroupShare implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public String gsid;
	public String type;// "imagetext" || "voicetext" || "vote"
	public String phone;
	public long time;
	public ArrayList<String> praiseusers = new ArrayList<String>();
	public ArrayList<Comment> comments = new ArrayList<Comment>();
	public ShareContent content = new ShareContent();

	public static class ShareContent implements Serializable {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		// "imagetext" || "voicetext"
		public ArrayList<String> images = new ArrayList<String>();
		public ArrayList<String> voices = new ArrayList<String>();
		public String text = "";

		// "vote"
		public String title;
		public ArrayList<String> voteoptions = new ArrayList<String>();

		public void addVoteOption(String option) {
			this.voteoptions.add(option);
		}

		public void addImage(String imageName) {
			this.images.add(imageName);
		}

		public void addVoice(String voiceName) {
			this.voices.add(voiceName);
		}
	}

	@Override
	public boolean equals(Object o) {
		boolean flag = false;
		if (o != null) {
			try {
				GroupShare gs = (GroupShare) o;
				if (gsid.equals(gs.gsid) && type.equals(gs.type)
						&& phone.equals(gs.phone) && time == gs.time
						&& praiseusers.containsAll(gs.praiseusers)
						&& comments.containsAll(gs.comments)) {
					flag = true;
				}
			} catch (Exception e) {
				flag = false;
			}
		}
		return flag;
	}
}
