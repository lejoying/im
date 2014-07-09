package com.lejoying.wxgs.app.data.entity;

import java.io.Serializable;
import java.util.ArrayList;

public class GroupShare implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public static final int MAXTYPE_COUNT = 3;
	public static final int MESSAGE_TYPE_IMAGETEXT = 0x01;
	public static final int MESSAGE_TYPE_VOICETEXT = 0x02;
	public static final int MESSAGE_TYPE_VOTE = 0x03;

	// type is imagetext or voicetext or vote
	public int mType;

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
		public ArrayList<VoiceContent> voices = new ArrayList<VoiceContent>();
		public String text = "";

		// "vote"
		public String title;
		public ArrayList<VoteContent> voteoptions = new ArrayList<VoteContent>();

		public void addImage(String imageName) {
			this.images.add(imageName);
		}

		public void addVoice(VoiceContent voiceContent) {
			this.voices.add(voiceContent);
		}
	}

	public static class VoteContent implements Serializable {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		public String content = "";
		public ArrayList<String> voteUsers = new ArrayList<String>();

		public void addVoteUser(String phone) {
			voteUsers.add(phone);
		}
	}

	public static class VoiceContent implements Serializable {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		public String fileName = "";
		public long time = 0;
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
