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

	public class VoiceMessageContent {
		public String time = "";
		public String fileName = "";
		public String recordReadSize = "";
	}

	public class LocationMessageContent {
		public String latitude = "";
		public String longitude = "";
		public String imageFileName = "";
	}

	public class CardMessageContent {
		public String type = "";
		public String key = "";
		public String name = "";
		public String mainBusiness = "";
		public String head = "";
	}

	public class SpecialGifMessageContent {
		public String phone = "";
		public String content = "";
	}
}
