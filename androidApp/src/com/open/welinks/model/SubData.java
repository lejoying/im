package com.open.welinks.model;

import java.util.ArrayList;
import java.util.List;

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
		public String sid;
		public String gsid;
		public String image;
		public String text;
	}

	public class SendShareMessage {
		public String type;// imagetext voicetext vote
		public String content;
	}

	public class ShareContent {
		public List<ShareContentItem> shareContentItems = new ArrayList<ShareContentItem>();

		public class ShareContentItem {
			public String type;// text image
			public String detail;
		}
	}
}
