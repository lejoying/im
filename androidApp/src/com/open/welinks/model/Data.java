package com.open.welinks.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Data {

	public static Data data;

	public static Data getInstance() {
		if (data == null) {
			data = new Data();
		}
		return data;
	}

	public LocalStatus localStatus = new LocalStatus();

	public class LocalStatus {
		public String thisActivityName = "NONE";
		public String thisActivityStatus = "";
		
		public String debugMode="NONE";//NONE 
	}

	public UserInformation userInformation;
	public Relationship relationship;

	public class UserInformation {
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
}
