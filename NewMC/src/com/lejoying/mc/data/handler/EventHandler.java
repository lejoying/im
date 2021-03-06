package com.lejoying.mc.data.handler;

import java.util.List;

import org.json.JSONObject;

import com.lejoying.mc.data.App;
import com.lejoying.mc.data.Data;
import com.lejoying.mc.data.Event;
import com.lejoying.mc.data.Message;
import com.lejoying.mc.data.handler.DataHandler.Modification;
import com.lejoying.mc.data.handler.DataHandler.UIModification;
import com.lejoying.mc.fragment.ChatFragment;
import com.lejoying.mc.fragment.FriendsFragment;

public class EventHandler {

	App app;

	public void initialize(App app) {
		this.app = app;
	}

	public void handleEvent(final JSONObject jEvnet) {
		app.dataHandler.modifyData(new Modification() {
			public void modify(Data data) {
				Event event = app.mJSONHandler.generateEventFromJSON(jEvnet);
				if (event.event.equals("message")) {
					try {
						@SuppressWarnings("unchecked")
						List<Message> messages = (List<Message>) event.eventContent;
						handleMessage(data, messages);
					} catch (Exception e) {

					}
				} else if (event.event.equals("newfriend")) {
					app.serverHandler.getAskFriends(null, null);
				}
			}
		}, new UIModification() {
			public void modifyUI() {
				if (ChatFragment.instance != null) {
					ChatFragment.instance.mAdapter.notifyDataSetChanged();
				}
				if (FriendsFragment.instance != null) {
				}
			}
		});
	}

	public void handleMessage(Data data, List<Message> messages) {
		// for (Message message : messages) {
		// data.friends.get(message.friendPhone).messages.add(message);
		// if (message.type == Message.MESSAGE_TYPE_RECEIVE) {
		// if (ChatFragment.instance == null
		// || !data.nowChatFriend.phone
		// .equals(message.friendPhone)) {
		// data.friends.get(message.friendPhone).notReadMessagesCount++;
		// }
		// } else {
		// data.friends.get(message.friendPhone).notReadMessagesCount = 0;
		// }
		//
		// data.lastChatFriends.remove(message.friendPhone);
		// data.lastChatFriends.add(0, message.friendPhone);
		// }
		app.serverHandler.getMessages(null, null);
	}
}
