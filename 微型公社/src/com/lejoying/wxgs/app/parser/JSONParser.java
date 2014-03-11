package com.lejoying.wxgs.app.parser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.lejoying.wxgs.app.MainApplication;
import com.lejoying.wxgs.app.data.entity.Circle;
import com.lejoying.wxgs.app.data.entity.Event;
import com.lejoying.wxgs.app.data.entity.Friend;
import com.lejoying.wxgs.app.data.entity.Group;
import com.lejoying.wxgs.app.data.entity.Message;
import com.lejoying.wxgs.app.data.entity.User;

public class JSONParser {

	static MainApplication app = MainApplication.getMainApplication();

	public static User generateUserFromJSON(JSONObject jUser) {
		User user = new User();
		try {
			user.phone = jUser.getString("phone");
		} catch (JSONException e) {
		}
		try {
			user.head = jUser.getString("head");
		} catch (JSONException e) {
		}
		try {
			user.nickName = jUser.getString("nickName");
		} catch (JSONException e) {
		}
		try {
			user.mainBusiness = jUser.getString("mainBusiness");
		} catch (JSONException e) {
		}
		return user;
	}

	public static Friend generateFriendFromJSON(JSONObject jFriend) {
		Friend friend = new Friend();
		try {
			friend.phone = jFriend.getString("phone");
		} catch (JSONException e) {
		}
		try {
			friend.head = jFriend.getString("head");
		} catch (JSONException e) {
		}
		try {
			friend.nickName = jFriend.getString("nickName");
		} catch (JSONException e) {
		}
		try {
			friend.mainBusiness = jFriend.getString("mainBusiness");
		} catch (JSONException e) {
		}
		try {
			friend.friendStatus = jFriend.getString("friendStatus");
		} catch (JSONException e) {
		}
		try {
			friend.addMessage = jFriend.getString("message");
		} catch (JSONException e) {
		}
		try {
			friend.distance = jFriend.getInt("distance");
		} catch (JSONException e) {
		}
		return friend;
	}

	public static List<Friend> generateFriendsFromJSON(JSONArray jFriends) {
		List<Friend> friends = new ArrayList<Friend>();
		for (int i = 0; i < jFriends.length(); i++) {
			try {
				JSONObject jFriend = jFriends.getJSONObject(i);
				Friend friend = generateFriendFromJSON(jFriend);
				if (friend.phone.equals(app.data.user.phone)) {
					continue;
				}
				friends.add(friend);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return friends;
	}

	private static Object[] generateCircleFromJSON(JSONObject jCircle) {
		Object[] circleAndFriends = new Object[2];
		try {
			Circle circle = new Circle();
			Map<String, Friend> friends = new HashMap<String, Friend>();

			circle.rid = jCircle.getInt("rid");
			circle.name = jCircle.getString("name");
			List<Friend> circleFriends = generateFriendsFromJSON(jCircle
					.getJSONArray("accounts"));
			List<String> phones = new ArrayList<String>();
			for (Friend friend : circleFriends) {
				String phone = friend.phone;
				phones.add(phone);
				friends.put(phone, friend);
			}
			circle.phones = phones;
			circleAndFriends[0] = circle;
			circleAndFriends[1] = friends;
		} catch (JSONException e) {
			circleAndFriends = null;
		}
		return circleAndFriends;
	}

	public static final class CirclesAndFriends {
		public List<Circle> circles = null;
		public Map<String, Friend> circleFriends = null;
	}

	@SuppressWarnings("unchecked")
	public static CirclesAndFriends generateCirclesFromJSON(JSONArray jCircles) {
		CirclesAndFriends circlesAndFriends = new CirclesAndFriends();
		List<Circle> circles = new ArrayList<Circle>();
		Map<String, Friend> circlefriends = new HashMap<String, Friend>();
		for (int i = 0; i < jCircles.length(); i++) {
			try {
				JSONObject jCircle = jCircles.getJSONObject(i);
				Object[] circleAndFriends = generateCircleFromJSON(jCircle);
				if (circleAndFriends != null) {
					circles.add((Circle) circleAndFriends[0]);
					circlefriends
							.putAll((Map<String, Friend>) circleAndFriends[1]);
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		circlesAndFriends.circles = circles;
		circlesAndFriends.circleFriends = circlefriends;
		return circlesAndFriends;
	}

	private static Object[] generateGroupFromJSON(JSONObject jGroup) {
		Object[] groupAndFriends = new Object[2];
		try {
			Group group = new Group();
			Map<String, Friend> friends = new HashMap<String, Friend>();
			group.gid = jGroup.getInt("gid");
			List<Friend> groupFriends = generateFriendsFromJSON(jGroup
					.getJSONArray("members"));
			List<String> phones = new ArrayList<String>();
			for (Friend friend : groupFriends) {
				String phone = friend.phone;
				phones.add(phone);
				friends.put(phone, friend);
			}
			group.members = phones;
			groupAndFriends[0] = group;
			groupAndFriends[1] = friends;
		} catch (JSONException e) {
			groupAndFriends = null;
		}

		return groupAndFriends;
	}

	public static final class GroupsAndFriends {
		public List<Group> groups = null;
		public Map<String, Friend> GroupFriends = null;
	}

	@SuppressWarnings("unchecked")
	public static GroupsAndFriends generateGroupsFromJSON(JSONArray jGroups) {
		GroupsAndFriends groupsAndFriends = new GroupsAndFriends();
		List<Group> groups = new ArrayList<Group>();
		Map<String, Friend> groupfriends = new HashMap<String, Friend>();
		for (int i = 0; i < jGroups.length(); i++) {
			try {
				JSONObject jGroup = jGroups.getJSONObject(i);
				Object[] groupAndFriends = generateGroupFromJSON(jGroup);
				if (groupAndFriends != null) {
					groups.add((Group) groupAndFriends[0]);
					groupfriends
							.putAll((Map<String, Friend>) groupAndFriends[1]);
				}
			} catch (JSONException e) {
			}
		}
		groupsAndFriends.groups = groups;
		groupsAndFriends.GroupFriends = groupfriends;
		return groupsAndFriends;
	}

	public static Message generateMessageFromJSON(JSONObject jMessage) {
		Message message = new Message();
		try {
			message.sendType = jMessage.getString("sendType");
			if (message.sendType.equals("point")) {
				String phoneSend = jMessage.getString("phone");
				String phoneReceive = new JSONArray(
						jMessage.getString("phoneto")).getString(0);

				String friendPhone = phoneSend;

				if (phoneSend.equals(app.data.user.phone)) {
					message.type = Message.MESSAGE_TYPE_SEND;
					friendPhone = phoneReceive;
				} else if (phoneReceive.equals(app.data.user.phone)) {
					message.type = Message.MESSAGE_TYPE_RECEIVE;
					friendPhone = phoneSend;
				}
				message.friendPhone = friendPhone;
			} else if (message.sendType.equals("group")
					|| message.sendType.equals("tempGroup")) {
				String phoneSend = jMessage.getString("phone");
				if (phoneSend.equals(app.data.user.phone)) {
					message.type = Message.MESSAGE_TYPE_SEND;
				} else {
					message.type = Message.MESSAGE_TYPE_RECEIVE;
				}
				message.gid = jMessage.getString("gid");
			}

			message.time = jMessage.getString("time");
			message.contentType = jMessage.getString("contentType");
			message.content = jMessage.getString("content");

			message.status = "sent";
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return message;
	}

	public static List<Message> generateMessagesFromJSON(JSONArray jMessages) {
		List<Message> messages = new ArrayList<Message>();
		if (jMessages.length() != 0) {
			for (int i = 0; i < jMessages.length(); i++) {
				try {
					JSONObject jMessage = new JSONObject(jMessages.getString(i));
					messages.add(generateMessageFromJSON(jMessage));
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}
		return messages;
	}

	public static Event generateEventFromJSON(JSONObject jEvnet) {
		Event event = new Event();
		try {
			event.event = jEvnet.getString("event");
			JSONObject jEventContent = jEvnet.getJSONObject("event_content");
			if (event.event.equals("message")) {
				event.eventContent = generateMessagesFromJSON(jEventContent
						.getJSONArray("message"));
			} else if (event.event.equals("newfriend")) {
				event.eventContent = null;
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return event;
	}

}
