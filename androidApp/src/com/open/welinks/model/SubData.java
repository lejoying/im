package com.open.welinks.model;

public class SubData {
	public static SubData subData;

	public static SubData getInstance() {
		if (subData == null) {
			subData = new SubData();
		}
		return subData;
	}

	public class MessageShareContent {
		public String gid;
		public String gsid;
		public String image;
		public String text;
	}
}
