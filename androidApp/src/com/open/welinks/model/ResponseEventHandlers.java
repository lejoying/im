package com.open.welinks.model;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.lidroid.xutils.http.ResponseInfo;
import com.open.lib.HttpClient;
import com.open.lib.MyLog;
import com.open.welinks.model.Data.Event.EventMessage;
import com.open.welinks.model.Data.Messages.Message;
import com.open.welinks.model.Data.Relationship.Friend;
import com.open.welinks.model.Data.Relationship.Group;
import com.open.welinks.model.Data.UserInformation.User;
import com.open.welinks.utils.NotificationUtils;
import com.open.welinks.view.ViewManage;

public class ResponseEventHandlers {

	public Data data = Data.getInstance();

	public String tag = "ResponseEventHandlers";
	public MyLog log = new MyLog(tag, true);

	public ViewManage viewManage = ViewManage.getInstance();

	public static ResponseEventHandlers responseEventHandlers = ResponseEventHandlers.getInstance();

	public HttpClient httpClient = HttpClient.getInstance();

	public Gson gson = new Gson();

	public Parser parser = Parser.getInstance();

	public static ResponseEventHandlers getInstance() {
		if (responseEventHandlers == null) {
			responseEventHandlers = new ResponseEventHandlers();
		}
		return responseEventHandlers;
	}

	public void handleEvent(Message message) {
		parser.check();
		if (message == null) {
			return;
		}

		if (message.sendType.equals("event")) {
			String contentType = message.contentType;
			if ("message".equals(contentType)) {
				Message messageGson = gson.fromJson(message.content, Message.class);
				updateLocalMessage(messageGson);
				boolean isNotify = false;
				String sendType = messageGson.sendType;
				if ("point".equals(sendType)) {
					isNotify = data.relationship.friends.contains(messageGson.phone);
				} else if ("group".equals(sendType)) {
					isNotify = data.relationship.groups.contains(messageGson.gid);
				}

				if (isNotify) {
					if (NotificationUtils.isLeave(viewManage.mainView.context)) {
						NotificationUtils.showMessageNotification(viewManage.mainView.context, messageGson);
					} else {
						NotificationUtils.commonVibrate(viewManage.mainView.context);
					}
				}
				DataHandlers.getMessages(data.userInformation.currentUser.flag);
			} else {
				EventMessage eventMessage = gson.fromJson(message.content, EventMessage.class);
				if (data.event.userEventsMap == null) {
					data.event.userEventsMap = new HashMap<String, EventMessage>();
				}
				if (data.event.groupEventsMap == null) {
					data.event.groupEventsMap = new HashMap<String, EventMessage>();
				}
				if (data.event.userEvents == null) {
					data.event.userEvents = new ArrayList<String>();
				}
				if (data.event.groupEvents == null) {
					data.event.groupEvents = new ArrayList<String>();
				}
				if ("account_dataupdate".equals(contentType)) {
					handleAccountDataupdateEvent(eventMessage);
				} else if ("relation_newfriend".equals(contentType)) {
					handleRelationNewfriendEvent(eventMessage);
				} else if ("relation_addfriend".equals(contentType)) {
					handleRelationAddfriendEvent(eventMessage);
				} else if ("relation_friendaccept".equals(contentType)) {
					handleRelationFriendacceptEvent(eventMessage);
				} else if ("relation_deletefriend".equals(contentType)) {
					handleRelationDeletefriendEvent(eventMessage);
				} else if ("relation_blacklist".equals(contentType)) {
					handleRelationBlacklistEvent(eventMessage);
				} else if ("relation_dataupdate".equals(contentType)) {
					handleRelationDataupdateEvent(eventMessage);
				} else if ("relation_addcircle".equals(contentType)) {
					handleRelationAddCircleEvent(eventMessage);
				} else if ("relation_deletecircle".equals(contentType)) {
					handleRelationDeleteCircleEvent(eventMessage);
				} else if ("relation_updatecirclename".equals(contentType)) {
					handleRelationUpdateCircleNameEvent(eventMessage);
				} else if ("relation_circlesequence".equals(contentType)) {
					handleRelationCircleSequenceEvent(eventMessage);
				} else if ("relation_updatefriendcircle".equals(contentType)) {
					handleRelationUpdateFriendCircleEvent(eventMessage);
				} else if ("group_addmembers".equals(contentType)) {
					handleGroupAddmembersEvent(message, eventMessage);
				} else if ("group_removemembers".equals(contentType)) {
					handleGroupRemovemembersEvent(message, eventMessage);
				} else if ("group_dataupdate".equals(contentType)) {
					handleGroupDataupdateEvent(message, eventMessage);
				} else if ("group_create".equals(contentType)) {
					handleGroupCreateEvent(message, eventMessage);
				} else if ("group_addme".equals(contentType)) {
					handleGroupAddMeEvent(message, eventMessage);
				} else if ("group_removeme".equals(contentType)) {
					handleGroupRemoveMeEvent(message, eventMessage);
				} else if ("group_sequence".equals(contentType)) {
					handleGroupSequenceEvent(message, eventMessage);
				} else if ("group_newboard".equals(contentType)) {
					handleGroupNewBoard(message, eventMessage);
				} else if ("group_updateboard".equals(contentType)) {
					handleGroupUpdateBoard(message, eventMessage);
				} else if ("group_updateboardsequence".equals(contentType)) {
					handleGroupUpdateBoardSequence(message, eventMessage);
				} else if ("group_deleteboard".equals(contentType)) {
					handleGroupDeleteBoard(message, eventMessage);
				}
				data.event.isModified = true;
			}
		}
	}

	private void handleGroupDeleteBoard(Message message, EventMessage eventMessage) {
		String key = eventMessage.eid;
		if (!data.event.groupEvents.contains(key)) {
			data.event.groupEvents.add(key);
			data.event.groupEventsMap.put(key, eventMessage);
			data.event.groupNotReadMessage = true;
			String eventKey = "event_group";
			checkEventIsMessageOrder(eventKey);
			DataHandlers.getGroupBoards(eventMessage.gid);
		}

	}

	private void handleGroupUpdateBoardSequence(Message message, EventMessage eventMessage) {
		String key = eventMessage.eid;
		if (!data.event.groupEvents.contains(key)) {
			data.event.groupEvents.add(key);
			data.event.groupEventsMap.put(key, eventMessage);
			data.event.groupNotReadMessage = true;
			String eventKey = "event_group";
			checkEventIsMessageOrder(eventKey);
			DataHandlers.getGroupBoards(eventMessage.gid);
		}

	}

	private void handleGroupUpdateBoard(Message message, EventMessage eventMessage) {
		String key = eventMessage.eid;
		if (!data.event.groupEvents.contains(key)) {
			data.event.groupEvents.add(key);
			data.event.groupEventsMap.put(key, eventMessage);
			data.event.groupNotReadMessage = true;
			String eventKey = "event_group";
			checkEventIsMessageOrder(eventKey);
			DataHandlers.getGroupBoards(eventMessage.gid);
		}

	}

	private void handleGroupNewBoard(Message message, EventMessage eventMessage) {
		String key = eventMessage.eid;
		if (!data.event.groupEvents.contains(key)) {
			data.event.groupEvents.add(key);
			data.event.groupEventsMap.put(key, eventMessage);
			data.event.groupNotReadMessage = true;
			String eventKey = "event_group";
			checkEventIsMessageOrder(eventKey);
			DataHandlers.getGroupBoards(eventMessage.gid);
		}
	}

	private void handleGroupSequenceEvent(Message chatMessage, EventMessage message) {
		String key = message.eid;
		if (!data.event.groupEvents.contains(key)) {
			data.event.groupEvents.add(key);
			data.event.groupEventsMap.put(key, message);
			data.event.groupNotReadMessage = true;
			String eventKey = "event_group";
			checkEventIsMessageOrder(eventKey);
			DataHandlers.getUserInfomation();
		}
	}

	private void handleRelationUpdateCircleNameEvent(EventMessage eventMessage) {
		String key = eventMessage.eid;
		if (!data.event.userEvents.contains(key)) {
			data.event.userEvents.add(key);
			data.event.userEventsMap.put(key, eventMessage);
			data.event.userNotReadMessage = true;
			String eventKey = "event_user";
			checkEventIsMessageOrder(eventKey);
			DataHandlers.getUserInfomation();
			DataHandlers.getIntimateFriends();
		}
	}

	private void handleRelationDeleteCircleEvent(EventMessage eventMessage) {
		String key = eventMessage.eid;
		if (!data.event.userEvents.contains(key)) {
			data.event.userEvents.add(key);
			data.event.userEventsMap.put(key, eventMessage);
			data.event.userNotReadMessage = true;
			String eventKey = "event_user";
			checkEventIsMessageOrder(eventKey);
			DataHandlers.getUserInfomation();
			DataHandlers.getIntimateFriends();
		}
	}

	private void handleRelationUpdateFriendCircleEvent(EventMessage eventMessage) {
		// String key = eventMessage.eid;
		// if (!data.event.userEvents.contains(key)) {
		// data.event.userEvents.add(key);
		// data.event.userEventsMap.put(key, eventMessage);
		// data.event.userNotReadMessage = true;
		// String eventKey = "event_user";
		// checkEventIsMessageOrder(eventKey);
		// }
		DataHandlers.getIntimateFriends();
	}

	private void handleRelationCircleSequenceEvent(EventMessage eventMessage) {
		// String key = eventMessage.eid;
		// if (!data.event.userEvents.contains(key)) {
		// data.event.userEvents.add(key);
		// data.event.userEventsMap.put(key, eventMessage);
		// data.event.userNotReadMessage = true;
		// String eventKey = "event_user";
		// checkEventIsMessageOrder(eventKey);
		// }
		DataHandlers.getUserInfomation();
		DataHandlers.getIntimateFriends();
	}

	private void handleRelationAddCircleEvent(EventMessage eventMessage) {
		String key = eventMessage.eid;
		if (!data.event.userEvents.contains(key)) {
			data.event.userEvents.add(key);
			data.event.userEventsMap.put(key, eventMessage);
			data.event.userNotReadMessage = true;
			String eventKey = "event_user";
			checkEventIsMessageOrder(eventKey);
			DataHandlers.getUserInfomation();
			DataHandlers.getIntimateFriends();
		}
	}

	public void handleRelationDataupdateEvent(EventMessage eventMessage) {
		DataHandlers.getUserInfomation();
		DataHandlers.getIntimateFriends();
	}

	public void checkEventIsMessageOrder(String key) {
		if (data.messages.messagesOrder.contains(key)) {
			data.messages.messagesOrder.remove(key);
		}
		data.messages.messagesOrder.add(0, key);
		data.messages.isModified = true;
	}

	// OK
	private void handleGroupRemoveMeEvent(Message chatMessage, EventMessage message) {
		String key = message.eid;
		if (!data.event.groupEvents.contains(key)) {
			data.event.groupEvents.add(key);
			data.event.groupEventsMap.put(key, message);
			data.event.groupNotReadMessage = true;
			String eventKey = "event_group";
			checkEventIsMessageOrder(eventKey);
			addEventToChatMessages(chatMessage, message);
			viewManage.postNotifyView("DynamicListActivity");
			viewManage.postNotifyView("MessagesSubView");
			DataHandlers.getUserCurrentAllGroup();
		}
	}

	// OK
	private void handleGroupAddMeEvent(Message chatMessage, EventMessage message) {
		String key = message.eid;
		if (!data.event.groupEvents.contains(key)) {
			data.event.groupEvents.add(key);
			data.event.groupEventsMap.put(key, message);
			data.event.groupNotReadMessage = true;
			String eventKey = "event_group";
			checkEventIsMessageOrder(eventKey);
			addEventToChatMessages(chatMessage, message);
			viewManage.postNotifyView("DynamicListActivity");
			viewManage.postNotifyView("MessagesSubView");
			DataHandlers.getUserCurrentGroupMembers(message.gid, "create");
		}
	}

	// OK
	public void handleGroupCreateEvent(Message chatMessage, EventMessage message) {
		String key = message.eid;
		if (!data.event.groupEvents.contains(key)) {
			data.event.groupEvents.add(key);
			data.event.groupEventsMap.put(key, message);
			data.event.groupNotReadMessage = true;
			String eventKey = "event_group";
			checkEventIsMessageOrder(eventKey);
			addEventToChatMessages(chatMessage, message);
			viewManage.postNotifyView("DynamicListActivity");
			viewManage.postNotifyView("MessagesSubView");
			DataHandlers.getUserCurrentGroupMembers(message.gid, "create");
		}
	}

	// OK
	public void handleGroupDataupdateEvent(Message chatMessage, EventMessage message) {
		String key = message.eid;
		if (!data.event.groupEvents.contains(key)) {
			data.event.groupEvents.add(key);
			data.event.groupEventsMap.put(key, message);
			data.event.groupNotReadMessage = true;
			String eventKey = "event_group";
			checkEventIsMessageOrder(eventKey);
			addEventToChatMessages(chatMessage, message);
			viewManage.postNotifyView("MessagesSubView");
			viewManage.postNotifyView("DynamicListActivity");
			DataHandlers.getUserCurrentGroupInfomation(message.gid);
		}
	}

	public void addEventToChatMessages(Message chatMessage, EventMessage message) {
		parser.check();
		// User user = data.userInformation.currentUser;
		String key = message.gid;
		String messageKey = "g" + message.gid;
		ArrayList<Message> groupMessages = data.messages.groupMessageMap.get(messageKey);
		if (groupMessages == null) {
			groupMessages = new ArrayList<Message>();
			data.messages.groupMessageMap.put(messageKey, groupMessages);
		}
		if (!data.messages.messagesOrder.contains(messageKey)) {
			if (data.relationship.groups.contains(key)) {
				data.messages.messagesOrder.add(messageKey);
				groupMessages.add(chatMessage);
				Group group = data.relationship.groupsMap.get(key);
				if (group != null) {
					group.notReadMessagesCount++;
				}
			}
		} else {
			if (data.relationship.groups.contains(key)) {
				data.messages.messagesOrder.remove(messageKey);
				data.messages.messagesOrder.add(0, messageKey);
				groupMessages.add(chatMessage);
				Group group = data.relationship.groupsMap.get(key);
				if (group != null) {
					group.notReadMessagesCount++;
				}
			}
		}
		data.messages.isModified = true;
	}

	// OK
	public void handleGroupRemovemembersEvent(Message chatMessage, EventMessage message) {
		String key = message.eid;
		if (!data.event.groupEvents.contains(key)) {
			data.event.groupEvents.add(key);
			data.event.groupEventsMap.put(key, message);
			data.event.groupNotReadMessage = true;
			String eventKey = "event_group";
			checkEventIsMessageOrder(eventKey);
			addEventToChatMessages(chatMessage, message);
			viewManage.postNotifyView("MessagesSubView");
			viewManage.postNotifyView("DynamicListActivity");
			DataHandlers.getUserCurrentGroupMembers(message.gid, "removemembers");
		}
	}

	// OK
	public void handleGroupAddmembersEvent(Message chatMessage, EventMessage message) {
		String key = message.eid;
		if (!data.event.groupEvents.contains(key)) {
			data.event.groupEvents.add(key);
			data.event.groupEventsMap.put(key, message);
			data.event.groupNotReadMessage = true;
			String eventKey = "event_group";
			checkEventIsMessageOrder(eventKey);
			addEventToChatMessages(chatMessage, message);
			viewManage.postNotifyView("DynamicListActivity");
			viewManage.postNotifyView("MessagesSubView");
			DataHandlers.getUserCurrentGroupMembers(message.gid, "addmembers");
		}
	}

	public void handleRelationBlacklistEvent(EventMessage message) {
		String key = message.eid;
		if (!data.event.userEvents.contains(key)) {
			data.event.userEvents.add(key);
			data.event.userEventsMap.put(key, message);
			data.event.userNotReadMessage = true;
			String eventKey = "event_user";
			checkEventIsMessageOrder(eventKey);
			// viewManage.postNotifyView("MessagesSubView");
			DataHandlers.getIntimateFriends();
		}
	}

	public void handleRelationDeletefriendEvent(EventMessage message) {
		// String key = message.eid;
		// if (!data.event.userEvents.contains(key)) {
		// data.event.userEvents.add(key);
		// data.event.userEventsMap.put(key, message);
		// data.event.userNotReadMessage = true;
		// String eventKey = "event_user";
		// checkEventIsMessageOrder(eventKey);
		// viewManage.postNotifyView("MessagesSubView");
		String key = message.phone;
		parser.check();
		data.relationship.friends.remove(key);
		Friend friend = data.relationship.friendsMap.get(key);
		friend.friendStatus = "delete";
		data.relationship.isModified = true;
		DataHandlers.getIntimateFriends();
		DataHandlers.getUserInfomation();
		// }
	}

	// OK
	public void handleRelationFriendacceptEvent(EventMessage message) {
		String key = message.eid;
		parser.check();
		EventMessage event = data.event.userEventsMap.get(key);
		if (message != null && event != null) {
			event.status = "success";
			data.event.isModified = true;
		}
		data.event.userNotReadMessage = true;
		String eventKey = "event_user";
		checkEventIsMessageOrder(eventKey);
		DataHandlers.getIntimateFriends();
		viewManage.postNotifyView("DynamicListActivity");
		viewManage.postNotifyView("MessagesSubView");
		// data.event.userEventsMap.put(message.gid, message);
	}

	// OK
	public void handleRelationNewfriendEvent(EventMessage message) {
		List<String> userEvents = data.event.userEvents;
		EventMessage dealMessage = null;
		for (int i = userEvents.size() - 1; i >= 0; i--) {
			String key = userEvents.get(i);
			EventMessage message0 = data.event.userEventsMap.get(key);
			if ("relation_newfriend".equals(message0.type)) {
				try {
					if (message.phone.equals(message0.phone) && message.phoneTo.equals(message0.phoneTo)) {
						dealMessage = message0;
						break;
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		if (dealMessage != null) {
			data.event.userEvents.remove(dealMessage.eid);
			data.event.userEventsMap.remove(dealMessage.eid);
		}
		String key = message.eid;
		if (!data.event.userEvents.contains(key)) {
			data.event.userEvents.add(key);
			data.event.userEventsMap.put(key, message);
			data.event.userNotReadMessage = true;
			String eventKey = "event_user";
			checkEventIsMessageOrder(eventKey);
			viewManage.postNotifyView("DynamicListActivity");
			viewManage.postNotifyView("MessagesSubView");
		}
	}

	// OK
	private void handleRelationAddfriendEvent(EventMessage message) {
		List<String> userEvents = data.event.userEvents;
		EventMessage dealMessage = null;
		for (int i = userEvents.size() - 1; i >= 0; i--) {
			if (userEvents.size() == 0) {
				break;
			}
			String key = userEvents.get(i);
			if (key == null) {
				continue;
			}
			EventMessage message0 = data.event.userEventsMap.get(key);
			if ("relation_addfriend".equals(message0.type)) {
				if (message.phone.equals(message0.phone) && message.phoneTo.equals(message0.phoneTo)) {
					dealMessage = message0;
					break;
				}
			}
		}
		if (dealMessage != null) {
			data.event.userEvents.remove(dealMessage.eid);
			data.event.userEventsMap.remove(dealMessage.eid);
		}
		String key = message.eid;
		if (!data.event.userEvents.contains(key)) {
			data.event.userEvents.add(key);
			data.event.userEventsMap.put(key, message);
			data.event.userNotReadMessage = true;
			String eventKey = "event_user";
			checkEventIsMessageOrder(eventKey);
			viewManage.postNotifyView("DynamicListActivity");
			viewManage.postNotifyView("MessagesSubView");
		}
	}

	// OK
	public void handleAccountDataupdateEvent(EventMessage message) {
		String key = message.eid;
		if (!data.event.userEvents.contains(key)) {
			parser.check();
			data.event.userEvents.add(key);
			data.event.userEventsMap.put(key, message);
			data.event.userNotReadMessage = true;
			String eventKey = "event_user";
			checkEventIsMessageOrder(eventKey);
			viewManage.postNotifyView("DynamicListActivity");
			viewManage.postNotifyView("MessagesSubView");
			DataHandlers.getUserInfomation();
		}
	}

	public static abstract class Response<T> {
		ResponseInfo<T> responseInfo;

		public Response(ResponseInfo<T> responseInfo) {
			this.responseInfo = responseInfo;
		}

		public abstract void handleResponse(InputStream is);
	}

	public static final class ResponseInfoHandler {

		public static final int WORKTHREADCOUNT_MIN = 1;
		public static final int WORKTHREADCOUNT_MAX = 50;
		public int mWorkThreadCount;

		Queue<ResponseInfo<String>> mResponseQueue;

		public ResponseInfoHandler(int concurrenceCount) {
			mResponseQueue = new LinkedList<ResponseInfo<String>>();
			if (concurrenceCount > WORKTHREADCOUNT_MAX) {
				mWorkThreadCount = WORKTHREADCOUNT_MAX;
			} else if (concurrenceCount < WORKTHREADCOUNT_MIN) {
				mWorkThreadCount = WORKTHREADCOUNT_MIN;
			} else {
				mWorkThreadCount = concurrenceCount;
			}
			for (int i = 0; i < mWorkThreadCount; i++) {
				new ResponseHandlerWorkThread(i).start();
			}
		}

		public synchronized void exclude(ResponseInfo<String> responseInfo) {
			mResponseQueue.offer(responseInfo);
			notify();
		}

		synchronized ResponseInfo<String> getExcude() throws InterruptedException {
			if (mResponseQueue.size() == 0) {
				wait();
			}
			return mResponseQueue.poll();
		}

		class ResponseHandlerWorkThread extends Thread {

			public int id;

			boolean interrupt;

			public ResponseHandlerWorkThread(int id) {
				this.id = id;
			}

			@Override
			public void run() {
				while (!interrupt) {
					ResponseInfo<String> responseInfo = null;
					try {
						while ((responseInfo = getExcude()) == null)
							;
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					responseEventHandlers.parseEvent(responseInfo);
				}
			}
		}
	}

	public void parseEvent(ResponseInfo<String> responseInfo) {
		Log.e(tag, responseInfo.result);
		Message message = null;
		try {
			message = gson.fromJson(responseInfo.result, Message.class);
			if (message.sendType != null) {
				responseEventHandlers.handleEvent(message);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void updateLocalMessage(Message message) {
		parser.check();
		message.type = Constant.MESSAGE_TYPE_RECEIVE;
		String sendType = message.sendType;
		if ("point".equals(sendType)) {
			String key = "";
			User user = data.userInformation.currentUser;
			if (message.phone.equals(user.phone)) {
				try {
					List<String> phoneTos = gson.fromJson(message.phoneto, new TypeToken<List<String>>() {
					}.getType());
					key = "p" + phoneTos.get(0);
				} catch (JsonSyntaxException e) {
					e.printStackTrace();
				}
			} else {
				key = "p" + message.phone;
			}
			ArrayList<Message> messageList = data.messages.friendMessageMap.get(key);
			if (messageList == null) {
				messageList = new ArrayList<Message>();
				data.messages.friendMessageMap.put(key, messageList);
			}
			if (!messageList.contains(message)) {
				messageList.add(message);
			}
		} else if ("group".equals(sendType)) {
			String key = "g" + message.gid;
			ArrayList<Message> groupList = data.messages.groupMessageMap.get(key);
			if (groupList == null) {
				groupList = new ArrayList<Message>();
				data.messages.groupMessageMap.put(key, groupList);
			}
			if (!groupList.contains(message)) {
				groupList.add(message);
			}
		}
		data.messages.isModified = true;
		if (viewManage.chatView != null) {
			String key = viewManage.chatView.thisController.key;
			if (key.equals(message.phone) || key.equals(message.gid)) {
				viewManage.chatView.thisController.handler.post(new Runnable() {

					@Override
					public void run() {
						viewManage.chatView.mChatAdapter.notifyDataSetChanged();
					}
				});
			} else {
				modifyMessagesSubView(message);
			}
		} else if (viewManage.newChatView != null) {
			String key = viewManage.newChatView.thisController.key;
			if (key.equals(message.phone) || key.equals(message.gid)) {
				viewManage.newChatView.thisController.handler.post(new Runnable() {
					@Override
					public void run() {
						viewManage.newChatView.mChatAdapter.notifyDataSetChanged();
					}
				});
			} else {
				modifyMessagesSubView(message);
			}
		}
		{
			modifyMessagesSubView(message);
		}
	}

	public void modifyMessagesSubView(Message message) {
		if ("point".equals(message.sendType)) {
			if (viewManage.newChatView != null) {
				String key = viewManage.newChatView.thisController.key;
				if (!key.equals(message.phone)) {
					data.relationship.friendsMap.get(message.phone).notReadMessagesCount++;
				}
			} else {
				data.relationship.friendsMap.get(message.phone).notReadMessagesCount++;
			}
		} else if ("group".equals(message.sendType)) {
			if (viewManage.newChatView != null) {
				String key = viewManage.newChatView.thisController.key;
				if (!key.equals(message.gid)) {
					data.relationship.groupsMap.get(message.gid).notReadMessagesCount++;
				}
			} else {
				data.relationship.groupsMap.get(message.gid).notReadMessagesCount++;
			}
		}
		viewManage.mainView.messagesSubView.thisController.addMessageToSubView(message);
	}
}
