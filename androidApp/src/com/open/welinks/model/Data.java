package com.open.welinks.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.open.welinks.model.Data.Messages.Message;
import com.open.welinks.model.Data.Relationship.Friend;
import com.open.welinks.model.Data.Relationship.Group;
import com.open.welinks.model.Data.TempData.ImageBean;

public class Data {

	public static Data data;

	public static Data getInstance() {
		if (data == null) {
			data = new Data();
		}
		return data;
	}

	public TempData tempData = new TempData();

	public class TempData {
		public int statusBarHeight;

		public ArrayList<ImageBean> prepareUploadImages;

		public ArrayList<String> prepareDownloadImages;

		public ArrayList<String> selectedImageList;

		public List<String> praiseusersList;

		public Friend tempFriend;

		public Group tempGroup;

		public class ImageBean {

			public String parentName;
			public String path;

			public String contentType;
			public long size = 0;

			// public UploadMultipart multipart;
			//
			// public DownloadFile downloadFile;
		}
	}

	public LocalStatus localStatus = new LocalStatus();

	public class LocalStatus {
		public String thisActivityName = "NONE";
		public String thisActivityStatus = "";

		public String debugMode = "NONE";// NONE

		public LocalData localData = new LocalData();

		public class LocalData {
			public ArrayList<ImageBean> prepareUploadImagesList = new ArrayList<ImageBean>();
			public ArrayList<ImageBean> prepareDownloadImagesList = new ArrayList<ImageBean>();

			public String currentSelectedGroup = "";
		}
	}

	public class ShareContent {
		public List<ShareContentItem> shareContentItems = new ArrayList<ShareContentItem>();

		public class ShareContentItem {
			public String type;// text image
			public String detail;
		}
	}

	public UserInformation userInformation;
	public Relationship relationship;
	public Messages messages;
	public Shares shares;
	public Squares squares;
	public Event event;

	public class UserInformation {
		public boolean isModified = false;

		public User currentUser;

		public class User {
			public String userBackground = "Back";
			public String sex = "";
			public int id;
			public String phone = "";
			public String nickName = "";
			public String mainBusiness = "";
			public String head = "Head";
			public String accessKey = "";
			public String flag = "none";

			public String lastlogintime;
			public String longitude;
			public String latitude;
		}

		public LocalConfig localConfig;

		public class LocalConfig {
			public String deviceid = "";
			public String line1Number = "";
			public String imei = "";
			public String imsi = "";
		}

		public ServerConfig serverConfig;

		public class ServerConfig {

		}

	}

	public class Relationship {
		public boolean isModified = false;

		public Map<String, Friend> friendsMap = new HashMap<String, Friend>();

		public List<String> circles = new ArrayList<String>();
		public Map<String, Circle> circlesMap = new HashMap<String, Circle>();

		public List<String> groups = new ArrayList<String>();
		public Map<String, Group> groupsMap = new HashMap<String, Group>();

		public class Friend {
			public int id;
			public String sex = "";
			public String phone = "";
			public int distance;
			public String nickName = "";
			public String mainBusiness = "";
			public String head = "Head";
			public String friendStatus = "";
			public String addMessage = "";
			public boolean temp;
			public int notReadMessagesCount;
			public String longitude;
			public String latitude;
			public String userBackground = "Back";
			public String alias = "";
			public String lastlogintime;
		}

		public class Circle {
			public int rid;
			public String name = "";
			public List<String> friends = new ArrayList<String>();
		}

		public class Group {
			public int gid;
			public String icon = "";
			public String name = "";
			public int notReadMessagesCount;
			public int distance;
			public String longitude;
			public String latitude;
			public String description;
			public String background;

			public List<String> members = new ArrayList<String>();
		}
	}

	public class Messages {
		public boolean isModified = false;

		public Map<String, ArrayList<Message>> friendMessageMap = new HashMap<String, ArrayList<Message>>();

		public Map<String, ArrayList<Message>> groupMessageMap = new HashMap<String, ArrayList<Message>>();

		public List<String> messagesOrder = new ArrayList<String>();

		public class Message {

			public int type;
			public String time;
			public String sendType;
			public String gid;
			public String status;
			public String phone;
			public String nickName;
			public String contentType;
			public String content;
			public String phoneto;
		}
	}

	public class Shares {
		public boolean isModified = false;

		public Map<String, Share> shareMap = new HashMap<String, Share>();

		public class Share {

			public List<String> shareMessagesOrder = new ArrayList<String>();

			public Map<String, ShareMessage> shareMessagesMap = new HashMap<String, ShareMessage>();

			public class ShareMessage {
				public int MAXTYPE_COUNT = 3;
				public int MESSAGE_TYPE_IMAGETEXT = 0x01;
				public int MESSAGE_TYPE_VOICETEXT = 0x02;
				public int MESSAGE_TYPE_VOTE = 0x03;

				public int mType;// MESSAGE_TYPE
				public String gsid;
				public String type;// imagetext voicetext vote
				public String phone;
				public long time;
				public List<String> praiseusers = new ArrayList<String>();
				public List<Comment> comments = new ArrayList<Comment>();
				public String content;
				public String status;// sending sent failed
			}

			public class Comment {
				public String phone;
				public String nickName;
				public String head;
				public String phoneTo;
				public String nickNameTo;
				public String headTo;
				public String contentType;// "text"
				public String content;
				public long time;
			}
		}
	}

	public class Squares {
		public boolean isModified = false;

		public Map<String, Square> squareMap = new HashMap<String, Square>();

		public class Square {

			public List<String> squareMessagesOrder = new ArrayList<String>();

			public Map<String, SquareMessage> squareMessagesMap = new HashMap<String, SquareMessage>();

			public class SquareMessage {
				public int MAXTYPE_COUNT = 3;
				public int MESSAGE_TYPE_IMAGETEXT = 0x01;
				public int MESSAGE_TYPE_VOICETEXT = 0x02;
				public int MESSAGE_TYPE_VOTE = 0x03;

				public int mType;// MESSAGE_TYPE
				public String gsid;
				public String type;// imagetext voicetext vote
				public String phone;
				public String nickName;
				public String head;
				public long time;
				public List<String> praiseusers = new ArrayList<String>();
				public List<Comment> comments = new ArrayList<Comment>();
				public String content;
				public String status;// sending sent failed
			}

			public class Comment {
				public String phone;
				public String nickName;
				public String head;
				public String phoneTo;
				public String nickNameTo;
				public String headTo;
				public String contentType;// "text"
				public String content;
				public long time;
			}
		}
	}

	public class Event {

		public boolean isModified = false;

		public List<Message> groupEvents = new ArrayList<Message>();

		public Map<String, Message> groupEventsMap = new HashMap<String, Message>();

		public List<Message> userEvents = new ArrayList<Message>();

		public Map<String, Message> userEventsMap = new HashMap<String, Message>();
	}
}
