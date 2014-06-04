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
import com.lejoying.wxgs.app.data.entity.Comment;
import com.lejoying.wxgs.app.data.entity.Event;
import com.lejoying.wxgs.app.data.entity.Friend;
import com.lejoying.wxgs.app.data.entity.Group;
import com.lejoying.wxgs.app.data.entity.Message;
import com.lejoying.wxgs.app.data.entity.SquareMessage;
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
			user.id = jUser.getInt("ID");
		} catch (JSONException e) {
		}
		try {
			user.sex = jUser.getString("sex");
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
		try {
			user.userBackground = jUser.getString("userBackground");
		} catch (JSONException e) {
			// TODO: handle exception
		}
		return user;
	}

	public static Friend generateFriendFromJSON(JSONObject jFriend) {
		Friend friend = new Friend();
		try {
			friend.id = jFriend.getInt("ID");
		} catch (JSONException e) {
		}
		try {
			friend.sex = jFriend.getString("sex");
		} catch (JSONException e) {
		}
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
			friend.userBackground = jFriend.getString("userBackground");
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
		try {
			friend.alias = jFriend.getString("alias");
		} catch (JSONException e) {
		}
		try {
			JSONObject jLocation = jFriend.getJSONObject("location");
			friend.longitude = jLocation.getString("longitude");
			friend.latitude = jLocation.getString("latitude");
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
				friends.add(friend);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return friends;
	}

	private static Object[] generateCircleFromJSON(JSONObject jCircle) {
		Object[] circleAndFriends = new Object[2];
		int rid = -1;
		String name = "";
		try {
			name = jCircle.getString("name");
		} catch (JSONException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		try {
			rid = jCircle.getInt("rid");
		} catch (JSONException e) {
		}
		try {
			Circle circle = new Circle();
			Map<String, Friend> friends = new HashMap<String, Friend>();
			circle.rid = rid;
			circle.name = name;
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
		Group group = new Group();
		Map<String, Friend> friends = new HashMap<String, Friend>();
		try {
			group.gid = jGroup.getInt("gid");
			try {
				group.icon = jGroup.getString("icon");
			} catch (JSONException e) {
			}
			try {
				group.name = jGroup.getString("name");
			} catch (JSONException e) {
			}
			try {
				group.description = jGroup.getString("description");
			} catch (JSONException e) {
			}
			try {
				List<Friend> groupFriends = generateFriendsFromJSON(jGroup
						.getJSONArray("members"));

				List<String> phones = new ArrayList<String>();
				for (Friend friend : groupFriends) {
					String phone = friend.phone;
					phones.add(phone);
					friends.put(phone, friend);
				}
				group.members = phones;
			} catch (JSONException e) {
			}

			try {
				group.distance = jGroup.getInt("distance");
			} catch (JSONException e) {
			}
			try {
				JSONObject jLocation = jGroup.getJSONObject("location");
				group.latitude = jLocation.getString("latitude");
				group.longitude = jLocation.getString("longitude");
			} catch (JSONException e) {
			}
		} catch (JSONException e) {
		}
		groupAndFriends[0] = group;
		groupAndFriends[1] = friends;
		return groupAndFriends;
	}

	public static final class GroupsAndFriends {
		public List<Group> groups = null;
		public Map<String, Friend> groupFriends = null;
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
		groupsAndFriends.groupFriends = groupfriends;
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

				String phone = phoneSend;

				if (phoneSend.equals(app.data.user.phone)) {
					message.type = Message.MESSAGE_TYPE_SEND;
					phone = phoneReceive;
				} else if (phoneReceive.equals(app.data.user.phone)) {
					message.type = Message.MESSAGE_TYPE_RECEIVE;
					phone = phoneSend;
				}
				message.phone = phone;
			} else if (message.sendType.equals("group")
					|| message.sendType.equals("tempGroup")) {
				String phone = jMessage.getString("phone");
				if (phone.equals(app.data.user.phone)) {
					message.type = Message.MESSAGE_TYPE_SEND;
				} else {
					message.type = Message.MESSAGE_TYPE_RECEIVE;
				}
				message.phone = phone;
				message.gid = jMessage.getString("gid");
			} else if (message.sendType.equals("square")) {
				message.gid = jMessage.getString("gid");
				message.phone = jMessage.getString("phone");
				message.nickName = jMessage.getString("nickName");
			}

			message.time = jMessage.getString("time");
			message.contentType = jMessage.getString("contentType");
			List<String> content = new ArrayList<String>();
			for (int i = 0; i < jMessage.getJSONArray("content").length(); i++) {
				content.add(jMessage.getJSONArray("content").get(i).toString());
			}
			message.content = content;

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

	public static Event generateEventFromJSON(JSONObject jEvent) {
		Event event = new Event();
		try {
			event.event = jEvent.getString("event");
			JSONObject jEventContent = jEvent.getJSONObject("event_content");
			if (event.event.equals("message")) {
				event.eventContent = generateMessagesFromJSON(jEventContent
						.getJSONArray("message"));
			} else if (event.event.equals("newfriend")) {
				event.eventContent = null;
			} else if (event.event.equals("friendaccept")) {
				event.eventContent = jEventContent.getString("phone");
			} else if (event.event.equals("groupinformationchanged")) {
				event.eventContent = jEventContent.getString("gid");
			} else if (event.event.equals("groupmemberchanged")) {
				event.eventContent = jEventContent.getString("gid");
			} else if (event.event.equals("groupstatuschanged")) {
				event.eventContent = jEventContent.getString("gid");
				event.operation = jEventContent.getBoolean("operation");
			} else if (event.event.equals("friendstatuschanged")) {
				event.eventContent = jEventContent.getString("phone");
				event.operation = jEventContent.getString("operation");
			} else if (event.event.equals("userinformationchanged")) {
				event.eventContent = jEventContent.getString("phone");
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return event;
	}

	public static List<SquareMessage> generateSquareMessagesFromJSON(
			JSONArray jMessages) {
		List<SquareMessage> squareMessages = new ArrayList<SquareMessage>();
		if (jMessages.length() != 0) {
			for (int i = 0; i < jMessages.length(); i++) {
				try {
					JSONObject jMessage = new JSONObject(jMessages.getString(i));
					squareMessages.add(generateSquareMessageFromJSON(jMessage));
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}
		return squareMessages;
	}

	private static SquareMessage generateSquareMessageFromJSON(
			JSONObject jMessage) {
		SquareMessage squareMessage = new SquareMessage();
		try {
			squareMessage.gmid = jMessage.getString("gmid");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		try {
			squareMessage.sendType = jMessage.getString("sendType");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		try {
			JSONArray typeJsonArray = jMessage.getJSONArray("messageType");
			ArrayList<String> messageTypes = new ArrayList<String>();
			for (int i = 0; i < typeJsonArray.length(); i++) {
				String messageType = typeJsonArray.getString(i);
				if (!"".equals(messageType)) {
					messageTypes.add(messageType);
				}
			}
			squareMessage.messageTypes = messageTypes;
		} catch (JSONException e) {
			try {
				ArrayList<String> messageTypes = new ArrayList<String>();
				String messageType = jMessage.getString("messageType");
				messageTypes.add(messageType);
				squareMessage.messageTypes = messageTypes;
			} catch (JSONException e1) {
				e.printStackTrace();
				e1.printStackTrace();
			}
		}
		try {
			squareMessage.contentType = jMessage.getString("contentType");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		try {
			squareMessage.cover = jMessage.getString("cover");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		try {
			JSONArray contents = jMessage.getJSONArray("content");
			for (int i = 0; i < contents.length(); i++) {
				// Object content = contents.get(i);
				JSONObject content = new JSONObject(contents.getString(i));
				if (content.getString("type").equals("image")) {
					squareMessage.content
							.addImage(content.getString("details"));
				} else if (content.getString("type").equals("voice")) {
					squareMessage.content
							.addVoice(content.getString("details"));
				} else {
					squareMessage.content.text = content.getString("details");
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		try {
			squareMessage.phone = jMessage.getString("phone");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		try {
			squareMessage.nickName = jMessage.getString("nickName");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		try {
			squareMessage.head = jMessage.getString("head");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		try {
			squareMessage.time = jMessage.getLong("time");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		try {
			ArrayList<String> users = new ArrayList<String>();
			JSONArray praiseusers = jMessage.getJSONArray("praiseusers");
			for (int i = 0; i < praiseusers.length(); i++) {
				users.add(praiseusers.getString(i));
			}
			squareMessage.praiseusers = users;
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return squareMessage;
	}

	public static List<Comment> generateCommentsFromJSON(JSONArray jComments) {
		List<Comment> comments = new ArrayList<Comment>();
		for (int i = 0; i < jComments.length(); i++) {
			try {
				JSONObject jComment = jComments.getJSONObject(i);
				comments.add(generateCommentOnlyFromJSON(jComment));
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

		return comments;
	}

	public static Comment generateCommentOnlyFromJSON(JSONObject jsonObject) {

		Comment comment = new Comment();
		try {
			comment.phone = jsonObject.getString("phone");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		try {
			comment.nickName = jsonObject.getString("nickName");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		try {
			comment.head = jsonObject.getString("head");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		try {
			comment.phoneTo = jsonObject.getString("phoneTo");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		try {
			comment.nickNameTo = jsonObject.getString("nickNameTo");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		try {
			comment.headTo = jsonObject.getString("headTo");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		try {
			comment.contentType = jsonObject.getString("contentType");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		try {
			comment.content = jsonObject.getString("content");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		try {
			comment.time = jsonObject.getLong("time");
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return comment;
	}
}
