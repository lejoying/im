package com.open.welinks.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.open.welinks.model.Data.Boards.ShareMessage;
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

		public Map<String, ShareMessage> tempShareMessageMap = new HashMap<String, ShareMessage>();

		public ArrayList<String> selectedImageList;

		public ArrayList<String> praiseusersList;

		public List<ShareMessage> tempShares;

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

		public String sendBug = "TRUE";// "NONE"

		public LocalData localData;

		public class LocalData {
			public boolean isModified = true;
			public ArrayList<ImageBean> prepareUploadImagesList = new ArrayList<ImageBean>();
			public ArrayList<ImageBean> prepareDownloadImagesList = new ArrayList<ImageBean>();

			public ArrayList<String> shareReleaseSequece = new ArrayList<String>();
			public Map<String, ShareDraft> shareReleaseSequeceMap = new HashMap<String, ShareDraft>();

			public String currentSelectedGroup = "";
			public String currentSelectedGroupBoard = "";
			public String currentSelectedSquare = "";
			public String currentSelectedSquareBoard = "";

			public Map<String, String> notSentMessagesMap = new HashMap<String, String>();
			public Map<String, ShareDraft> notSendShareMessagesMap = new HashMap<String, ShareDraft>();

			public class ShareDraft {
				public String gid;// not must
				public String sid;
				public String gsid;// not must
				public String gtype;
				public String content;
				public String imagesContent;
			}

			public Map<String, Boolean> newMessagePowerMap = new HashMap<String, Boolean>();

			public String addFriendMessage = "";
		}
	}

	public UserInformation userInformation;
	public Relationship relationship;
	public Boards boards;
	public Messages messages;
	public Event event;

	public class UserInformation {
		public boolean isModified = false;

		public User currentUser;

		public class User {
			public String userBackground = "Back";
			public String sex = "";
			public int id;
			public String age;
			public String phone = "";
			public String nickName = "";
			public String mainBusiness = "";
			public String head = "Head";
			public String accessKey = "";
			public String flag = "none";
			public String createTime;

			public String lastLoginTime;
			public String longitude;
			public String latitude;

			public String groupsSequenceString;
			public String circlesOrderString;

			public List<String> blackList;
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

		public List<String> friends = new ArrayList<String>();
		public Map<String, Friend> friendsMap = new HashMap<String, Friend>();

		public List<String> circles = new ArrayList<String>();
		public Map<String, Circle> circlesMap = new HashMap<String, Circle>();

		public List<String> groups = new ArrayList<String>();
		public Map<String, Group> groupsMap = new HashMap<String, Group>();

		public List<String> squares = new ArrayList<String>();

		public class Friend {
			public int id;
			public String sex = "";
			public int age;
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
			public String lastLoginTime;
			public String createTime;

			public String groupsSequenceString;
			public String circlesOrderString;
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
			public String createTime;
			public String cover;
			public String permission;

			public String currentBoard = "";

			public List<String> boards;
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

	public class Boards {
		public boolean isModified = false;

		public Map<String, Board> boardsMap = new HashMap<String, Board>();
		public Map<String, ShareMessage> shareMessagesMap = new HashMap<String, ShareMessage>();

		public class Board {
			public String sid;
			public String name;
			public String cover;
			public String head;
			public String description;
			public String gid;

			public long updateTime;
			public List<String> shareMessagesOrder = new ArrayList<String>();
		}

		public class ShareMessage {
			public int MAXTYPE_COUNT = 3;
			public int MESSAGE_TYPE_IMAGETEXT = 0x01;
			public int MESSAGE_TYPE_VOICETEXT = 0x02;
			public int MESSAGE_TYPE_VOTE = 0x03;

			public int mType;// MESSAGE_TYPE
			public String gid;// only sharelist In the use of
			public String gsid;
			public String sid;
			public String type;// imagetext voicetext vote
			public String phone;
			public String nickName;
			public String head;
			public long time;
			public ArrayList<String> praiseusers = new ArrayList<String>();
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

	public class Event {

		public boolean isModified = false;

		public boolean groupNotReadMessage = false;
		public List<String> groupEvents = new ArrayList<String>();

		public Map<String, EventMessage> groupEventsMap = new HashMap<String, EventMessage>();

		public boolean userNotReadMessage = false;
		public List<String> userEvents = new ArrayList<String>();

		public Map<String, EventMessage> userEventsMap = new HashMap<String, EventMessage>();

		public class EventMessage {
			public String eid;
			public String gid;
			public String rid;
			public String type;
			public String phone;
			public String phoneTo;
			public String name;
			public String time;
			public String status;// waiting success
			public String content;
		}
	}
}
