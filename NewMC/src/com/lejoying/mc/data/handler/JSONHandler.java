package com.lejoying.mc.data.handler;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.lejoying.mc.data.App;
import com.lejoying.mc.data.Event;
import com.lejoying.mc.data.Friend;
import com.lejoying.mc.data.Message;
import com.lejoying.mc.data.StaticData;
import com.lejoying.mc.data.User;

public class JSONHandler {
	App app;

	public void initailize(App app) {
		this.app = app;
	}

	public int updateUser(JSONObject jUser, StaticData data) {
		app.isDataChanged = true;
		int count = 0;
		User user = generateUserFromJSON(jUser);
		if (user.head != null && !user.head.equals("")) {
			data.user.head = user.head;
			count++;
		}
		if (user.mainBusiness != null && !user.mainBusiness.equals("")) {
			data.user.mainBusiness = user.mainBusiness;
			count++;
		}
		if (user.nickName != null && !user.nickName.equals("")) {
			data.user.nickName = user.nickName;
			count++;
		}
		return count;
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

					String phoneSend = jMessage.getString("phone");
					String phoneReceive = new JSONArray(jMessage.getString("phoneto")).getString(0);

					String friendPhone = phoneSend;

					if (phoneSend.equals(app.data.user.phone)) {
						message.type = Message.MESSAGE_TYPE_SEND;
						friendPhone = phoneReceive;
					} else if (phoneReceive.equals(app.data.user.phone)) {
						message.type = Message.MESSAGE_TYPE_RECEIVE;
						friendPhone = phoneSend;
					}

					message.time = jMessage.getString("time");
					message.messageType = jMessage.getString("type");
					message.content = jMessage.getString("content");

					message.friendPhone = friendPhone;

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
				event.eventContent = generateMessagesFromJSON(jEventContent.getJSONArray("message"));
			}else if(event.event.equals("newfriend")){
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
