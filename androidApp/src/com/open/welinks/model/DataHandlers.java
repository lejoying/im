package com.open.welinks.model;

import java.util.List;

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.open.welinks.model.Data.Messages.Message;
import com.open.welinks.model.Data.UserInformation.User;

public class DataHandlers {

	public String tag = "DataUtil";
	public static Data data = Data.getInstance();
	public static Parser parser = Parser.getInstance();
	public static ResponseHandlers responseHandlers = ResponseHandlers.getInstance();

	public static void getIntimateFriends() {
		data = parser.check();
		RequestParams params = new RequestParams();
		User user = data.userInformation.currentUser;
		params.addBodyParameter("phone", user.phone);
		params.addBodyParameter("accessKey", user.accessKey);

		HttpUtils http = new HttpUtils();

		http.send(HttpMethod.POST, API.RELATION_GETINTIMATEFRIENDS, params, responseHandlers.getIntimateFriends);
	}

	public static void getMessages(String flag) {
		if (flag.equals("")) {
			flag = "none";
		}
		data = parser.check();
		RequestParams params = new RequestParams();
		User user = data.userInformation.currentUser;
		params.addBodyParameter("phone", user.phone);
		params.addBodyParameter("accessKey", user.accessKey);
		params.addBodyParameter("flag", flag);

		HttpUtils http = new HttpUtils();

		http.send(HttpMethod.POST, API.MESSAGE_GET, params, responseHandlers.getMessageCallBack);
	}

	public static void getUserCurrentGroupInfomation(String gid) {
		parser.check();
		RequestParams params = new RequestParams();
		HttpUtils httpUtils = new HttpUtils();
		User user = data.userInformation.currentUser;
		params.addBodyParameter("phone", user.phone);
		params.addBodyParameter("accessKey", user.accessKey);
		params.addBodyParameter("gid", gid + "");

		httpUtils.send(HttpMethod.POST, API.GROUP_GET, params, responseHandlers.getGroupInfomationCallBack);
	}

	public static void getUserCurrentGroupMembers(String gid, String type) {
		parser.check();
		RequestParams params = new RequestParams();
		HttpUtils httpUtils = new HttpUtils();
		User user = data.userInformation.currentUser;
		params.addBodyParameter("phone", user.phone);
		params.addBodyParameter("accessKey", user.accessKey);
		params.addBodyParameter("gid", gid);

		RequestCallBack<String> callBack = responseHandlers.getCurrentNewGroupMembersCallBack;
		if ("create".equals(type)) {
			callBack = responseHandlers.getCurrentNewGroupMembersCallBack;
		} else if ("removemembers".equals(type) || "addmembers".equals(type)) {
			callBack = responseHandlers.getCurrentGroupMembersCallBack;
		}

		httpUtils.send(HttpMethod.POST, API.GROUP_GETALLMEMBERS, params, callBack);
	}

	public static void getUserInfomation() {
		parser.check();
		RequestParams params = new RequestParams();
		HttpUtils httpUtils = new HttpUtils();
		User user = data.userInformation.currentUser;
		params.addBodyParameter("phone", user.phone);
		params.addBodyParameter("accessKey", user.accessKey);
		params.addBodyParameter("target", "[\"" + user.phone + "\"]");

		httpUtils.send(HttpMethod.POST, API.ACCOUNT_GET, params, responseHandlers.getUserInfomation);
	}

	public static void getUserCurrentAllGroup() {
		RequestParams params = new RequestParams();
		HttpUtils httpUtils = new HttpUtils();
		params.addBodyParameter("phone", data.userInformation.currentUser.phone);
		params.addBodyParameter("accessKey", data.userInformation.currentUser.accessKey);

		httpUtils.send(HttpMethod.POST, API.GROUP_GETGROUPMEMBERS, params, responseHandlers.getGroupMembersCallBack);
	}

	public static void clearData() {
		try {
			// data = null;
			data.userInformation.isModified = false;
			data.userInformation.currentUser.phone = "";
			data.userInformation.currentUser.accessKey = "";

			data.relationship.isModified = false;
			data.relationship.circles.clear();
			data.relationship.friends.clear();
			data.relationship.friendsMap.clear();
			data.relationship.circlesMap.clear();
			data.relationship.groups.clear();
			data.relationship.groupsMap.clear();
			data.relationship.squares.clear();

			data.messages.isModified = false;
			data.messages.friendMessageMap.clear();
			data.messages.groupMessageMap.clear();
			data.messages.messagesOrder.clear();

			data.shares.isModified = false;
			data.shares.shareMap.clear();

			data.event.isModified = false;
			data.event.groupEvents.clear();
			data.event.groupEventsMap.clear();
			data.event.userEvents.clear();
			data.event.userEventsMap.clear();

			data.localStatus.localData.currentSelectedGroup = "";
			data.localStatus.localData.currentSelectedSquare = "";
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static boolean contains(List<Message> list, Message message) {
		int s = list.size();
		if (message != null) {
			for (int i = 0; i < s; i++) {
				if (equalsMessage(message, list.get(i))) {
					return true;
				}
			}
		} else {
			for (int i = 0; i < s; i++) {
				if (list.get(i) == null) {
					return true;
				}
			}
		}
		return false;
	}

	public static boolean equalsMessage(Message m, Message message) {
		boolean flag = false;
		if (m != null) {
			try {
				if (!"".equals(m.gid) && m.gid != null) {
					if (message.gid.equals(m.gid) && message.phone.equals(m.phone) && message.time.equals(m.time) && message.content.equals(m.content) && message.contentType.equals(m.contentType) && message.sendType.equals(m.sendType)) {
						flag = true;
						// Log.e("Data", "聊天记录已存在group");
					}
				} else {
					if (message.phone.equals(m.phone) && message.phoneto.equals(m.phoneto) && message.time.equals(m.time) && message.content.equals(m.content) && message.contentType.equals(m.contentType) && message.sendType.equals(m.sendType)) {
						flag = true;
						// Log.e("Data", "聊天记录已存在point");
					}
				}
			} catch (Exception e) {
				flag = false;
			}
		}
		return flag;
	}
}
