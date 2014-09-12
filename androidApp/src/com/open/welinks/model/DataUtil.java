package com.open.welinks.model;

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.open.welinks.model.Data.UserInformation.User;

public class DataUtil {

	public String tag = "DataUtil";
	public static Data data = Data.getInstance();
	public static Parser parser = Parser.getInstance();
	public static ResponseHandlers responseHandlers = ResponseHandlers.getInstance();

	public static void getIntimateFriends() {
		data = parser.check();
		RequestParams params = new RequestParams();
		User user = data.userInformation.currentUser;
		params.addBodyParameter("accessKey", user.accessKey);
		params.addBodyParameter("phone", user.phone);

		HttpUtils http = new HttpUtils();

		http.send(HttpRequest.HttpMethod.POST, API.RELATION_GETINTIMATEFRIENDS, params, responseHandlers.getIntimateFriends);
	}

	public static void getMessages(String flag) {
		data = parser.check();
		RequestParams params = new RequestParams();
		User user = data.userInformation.currentUser;
		params.addBodyParameter("accessKey", user.accessKey);
		params.addBodyParameter("flag", flag);

		HttpUtils http = new HttpUtils();

		http.send(HttpRequest.HttpMethod.POST, API.RELATION_GETINTIMATEFRIENDS, params, responseHandlers.getMessageCallBack);
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
}
