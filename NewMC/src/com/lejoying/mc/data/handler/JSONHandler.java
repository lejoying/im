package com.lejoying.mc.data.handler;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.lejoying.mc.data.App;
import com.lejoying.mc.data.Circle;
import com.lejoying.mc.data.Data;
import com.lejoying.mc.data.Event;
import com.lejoying.mc.data.Friend;
import com.lejoying.mc.data.Message;
import com.lejoying.mc.data.User;

public class JSONHandler {
	App app;

	public void initailize(App app) {
		this.app = app;
	}

	public void updateUser(JSONObject jUser, Data data) {
		User user = generateUserFromJSON(jUser);
		if (user.head != null && !user.head.equals("")) {
			data.user.head = user.head;
		}
		if (user.mainBusiness != null && !user.mainBusiness.equals("")) {
			data.user.mainBusiness = user.mainBusiness;
		}
		if (user.nickName != null && !user.nickName.equals("")) {
			data.user.nickName = user.nickName;
		}
	}

	public void updateFriend(JSONObject jFriend, Data data) {
		Friend friend = generateFriendFromJSON(jFriend);
		Friend updateFriend = data.friends.get(friend.phone);
		if (updateFriend != null) {
			if (friend.head != null && !friend.head.equals("")) {
				updateFriend.head = friend.head;
			}
			if (friend.nickName != null && !friend.nickName.equals("")) {
				updateFriend.nickName = friend.nickName;
			}
			if (friend.mainBusiness != null && !friend.mainBusiness.equals("")) {
				updateFriend.mainBusiness = friend.mainBusiness;
			}
			if (friend.friendStatus != null && !friend.friendStatus.equals("")) {
				updateFriend.friendStatus = friend.friendStatus;
			}
		}
	}

	public void saveCircles(JSONArray jCircles, Data data) {
		Map<String, Friend> friends = new Hashtable<String, Friend>();
		friends.putAll(app.data.friends);
		List<Circle> circles = new ArrayList<Circle>();
		for (int i = 0; i < jCircles.length(); i++) {
			try {
				JSONObject jCircle = jCircles.getJSONObject(i);
				Circle circle = new Circle();
				try {
					circle.rid = jCircle.getInt("rid");
				} catch (JSONException e) {

				}
				circle.name = jCircle.getString("name");
				JSONArray jFriends = jCircle.getJSONArray("accounts");
				List<String> phones = new ArrayList<String>();
				for (int j = 0; j < jFriends.length(); j++) {
					JSONObject jFriend = jFriends.getJSONObject(j);
					String phone = jFriend.getString("phone");
					phones.add(phone);
					if (friends.get(phone) == null) {
						Friend friend = generateFriendFromJSON(jFriend);
						friends.put(phone, friend);
					} else {
						updateFriend(jFriend, data);
					}
				}
				circle.phones = phones;
				circles.add(circle);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		data.circles = circles;
		data.friends = friends;
	}

	public void saveMessages(JSONArray jMessages, Data data) {
		List<Message> messages = generateMessagesFromJSON(jMessages);
		for (Message message : messages) {
			if (message.sendType.equals("point")) {
				List<Message> friendMessages = data.friends
						.get(message.friendPhone).messages;
				if (!friendMessages.contains(message)) {
					friendMessages.add(message);
					if (message.type == Message.MESSAGE_TYPE_RECEIVE) {
						if (data.nowChatFriend == null
								|| data.nowChatFriend.phone != message.friendPhone
								|| !app.mark.equals(app.chatFragment)) {
							data.friends.get(message.friendPhone).notReadMessagesCount++;
						}
					}
				}
				data.lastChatFriends.remove(message.friendPhone);
				data.lastChatFriends.add(0, message.friendPhone);
			} else if (message.sendType.equals("group")) {

			} else if (message.sendType.equals("tempGroup")) {

			}
		}
	}

	public void saveNewFriends(JSONArray jFriends, Data data) {
		for (int i = 0; i < jFriends.length(); i++) {
			try {
				JSONObject jFriend = jFriends.getJSONObject(i);
				Friend friend = generateFriendFromJSON(jFriend);
				if (!data.newFriends.contains(friend)) {
					data.newFriends.add(0, friend);
				} else {
					data.newFriends.remove(friend);
					data.newFriends.add(0, friend);
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public User generateUserFromJSON(JSONObject jUser) {
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

	public List<Message> generateMessagesFromJSON(JSONArray jMessages) {
		List<Message> messages = new ArrayList<Message>();
		if (jMessages.length() != 0) {
			for (int i = 0; i < jMessages.length(); i++) {
				try {
					JSONObject jMessage = new JSONObject(jMessages.getString(i));
					Message message = new Message();
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
					messages.add(message);
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}
		return messages;
	}

	public Event generateEventFromJSON(JSONObject jEvnet) {
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

	public List<Friend> generateFriendsFromJSON(JSONArray jFriends) {
		List<Friend> friends = new ArrayList<Friend>();
		for (int i = 0; i < jFriends.length(); i++) {
			try {
				JSONObject jFriend = jFriends.getJSONObject(i);
				Friend friend = generateFriendFromJSON(jFriend);
				friends.add(friend);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return friends;
	}

	public Friend generateFriendFromJSON(JSONObject jFriend) {
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
		return friend;
	}

}
