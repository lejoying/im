package com.open.welinks.model;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import com.google.gson.Gson;
import com.lidroid.xutils.http.ResponseInfo;
import com.open.lib.HttpClient;
import com.open.welinks.model.Data.Messages.Message;
import com.open.welinks.view.ViewManage;

public class ResponseEventHandlers {

	public Data data = Data.getInstance();

	public String tag = "ResponseEventHandlers";

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
		if (message.sendType.equals("event")) {
			String firstString = message.contentType.substring(0, message.contentType.indexOf("_"));
			if (firstString.equals("account") || firstString.equals("relation")) {
				data.event.userEvents.add(message);
			} else if (firstString.equals("group")) {
				data.event.groupEvents.add(message);
			}
		} else if (message.sendType.equals("point") || message.sendType.equals("group")) {
			updateLocalMessage(message);
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

	public class Event {
		public String 提示信息;
		public String event;
		public EventContent event_content;
	}

	public class EventContent {
		public List<String> message;
	}

	public void parseEvent(ResponseInfo<String> responseInfo) {
		Message message = gson.fromJson(responseInfo.result, Message.class);
		responseEventHandlers.handleEvent(message);
	}

	public void updateLocalMessage(Message message) {
		message.type = Constant.MESSAGE_TYPE_RECEIVE;
		if ("point".equals(message.sendType)) {
			ArrayList<Message> list = data.messages.friendMessageMap.get("p" + message.phone);
			if (list == null) {
				list = new ArrayList<Data.Messages.Message>();
				data.messages.friendMessageMap.put("p" + message.phone, list);
			}
			list.add(message);
		} else if ("group".equals(message.sendType)) {
			ArrayList<Message> list = data.messages.groupMessageMap.get("g" + message.gid);
			if (list == null) {
				list = new ArrayList<Data.Messages.Message>();
				data.messages.groupMessageMap.put("g" + message.gid, list);
			}
			list.add(message);
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
		} else {
			modifyMessagesSubView(message);
		}
	}

	public void modifyMessagesSubView(Message message) {
		if ("point".equals(message.sendType)) {
			data.relationship.friendsMap.get(message.phone).notReadMessagesCount++;
		} else if ("group".equals(message.sendType)) {
			data.relationship.groupsMap.get(message.gid).notReadMessagesCount++;
		}
		viewManage.mainView.messagesSubView.thisController.addMessageToSubView(message);
	}
}
