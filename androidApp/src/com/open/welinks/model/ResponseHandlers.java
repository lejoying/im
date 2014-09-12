package com.open.welinks.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.http.Header;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.open.lib.HttpClient;
import com.open.lib.HttpClient.ResponseHandler;
import com.open.lib.MyLog;
import com.open.welinks.controller.Debug1Controller;
import com.open.welinks.model.Data.Messages.Message;
import com.open.welinks.model.Data.Relationship;
import com.open.welinks.model.Data.Relationship.Friend;
import com.open.welinks.model.Data.Relationship.Group;
import com.open.welinks.model.Data.Shares.Share;
import com.open.welinks.model.Data.Shares.Share.ShareMessage;
import com.open.welinks.model.Data.UserInformation.User;
import com.open.welinks.utils.RSAUtils;
import com.open.welinks.view.ViewManage;

public class ResponseHandlers {

	public Data data = Data.getInstance();
	public Parser parser = Parser.getInstance();

	public String tag = "ResponseHandlers";
	public MyLog log = new MyLog(tag, true);

	public ViewManage viewManage = ViewManage.getInstance();

	public ResponseEventHandlers responseEventHandlers = ResponseEventHandlers.getInstance();

	public static ResponseHandlers responseHandlers;

	public static ResponseHandlers getInstance() {
		if (responseHandlers == null) {
			responseHandlers = new ResponseHandlers();
		}
		return responseHandlers;
	}

	public HttpClient httpClient = HttpClient.getInstance();

	public ResponseHandler<String> auth = httpClient.new ResponseHandler<String>() {

		@Override
		public void onSuccess(ResponseInfo<String> responseInfo) {

		}
	};

	public ResponseHandler<String> register = httpClient.new ResponseHandler<String>() {

		@Override
		public void onSuccess(ResponseInfo<String> responseInfo) {

		}
	};
	public ResponseHandler<String> getUserInfomation = httpClient.new ResponseHandler<String>() {

		class Response {
			public String 提示信息;
			public String 失败原因;
			public List<Friend> accounts;
		}

		public void onSuccess(ResponseInfo<String> responseInfo) {
			Response response = gson.fromJson(responseInfo.result, Response.class);
			if ("获取用户信息成功".equals(response.提示信息)) {
				Friend friend = response.accounts.get(0);
				if (friend != null) {
					parser.check();
					User user = data.userInformation.currentUser;
					user.userBackground = friend.userBackground;
					user.sex = friend.sex;
					user.id = friend.id;
					user.phone = friend.phone;
					user.nickName = friend.nickName;
					user.mainBusiness = friend.mainBusiness;
					user.head = friend.head;
					data.userInformation.isModified = true;
					viewManage.postNotifyView("MeSubView");
				}
			}
		};
	};

	Gson gson = new Gson();

	public ResponseHandler<String> getIntimateFriends = httpClient.new ResponseHandler<String>() {
		class Response {
			public String 提示信息;
			public String 失败原因;
			public Relationship relationship;

		}

		@Override
		public void onSuccess(ResponseInfo<String> responseInfo) {
			Response response = gson.fromJson(responseInfo.result, Response.class);
			if (response.提示信息.equals("获取密友圈成功")) {
				log.e(tag, "获取密友圈成功");
				data.relationship.circles = response.relationship.circles;
				data.relationship.circlesMap = response.relationship.circlesMap;
				data.relationship.friendsMap.putAll(response.relationship.friendsMap);

				data.relationship.isModified = true;
			}
			if (data.localStatus.debugMode.equals("NONE")) {
				// viewManage.postNotifyView("UserIntimateView");
				log.e(tag, "刷新好友分组");
				viewManage.mainView.friendsSubView.showCircles();
			}
		}
	};

	public ResponseHandler<String> upload = httpClient.new ResponseHandler<String>() {
		@Override
		public void onSuccess(ResponseInfo<String> responseInfo) {
			log.e(tag, responseInfo + "-------------");
			Header[] headers = responseInfo.getAllHeaders();
			for (int i = 0; i < headers.length; i++) {
				log.e(tag, "reply upload: " + headers[i]);
			}
		}
	};

	public ResponseHandler<String> checkFile = httpClient.new ResponseHandler<String>() {
		class Response {
			public String 提示信息;
			public String 失败原因;
			public String filename;
			public boolean exists;
			public String signature;
			public long expires;
			public String OSSAccessKeyId;

		}

		@Override
		public void onSuccess(ResponseInfo<String> responseInfo) {
			Response response = gson.fromJson(responseInfo.result, Response.class);
			log.e(tag, responseInfo.result);
			if (response.提示信息.equals("查找成功")) {
				if (response.exists) {

				} else if (!response.exists) {
					log.e(tag, response.signature + "---" + response.filename + "---" + response.expires + "---" + response.OSSAccessKeyId);
					Debug1Controller.uploadImageWithInputStreamUploadEntity(response.signature, response.filename, response.expires, response.OSSAccessKeyId);
				}
			}
		}
	};

	public ResponseHandler<String> message_sendMessageCallBack = httpClient.new ResponseHandler<String>() {
		class Response {
			public String 提示信息;
			// public String 失败原因;
			public long time;
			// public String sendType;
			// public String gid;
			// public String phoneto;
		}

		@Override
		public void onSuccess(ResponseInfo<String> responseInfo) {
			Response response = gson.fromJson(responseInfo.result, Response.class);
			if (response.提示信息.equals("发送成功")) {
				// if ("point".equals(response.sendType)) {
				// //
				// data.messages.friendMessageMap.get(response.phoneto).get(0).time
				// = String.valueOf(response.time);
				// } else if ("group".equals(response.sendType)) {
				// //
				// data.messages.groupMessageMap.get(response.gid).get(0).time =
				// String.valueOf(response.time);
				// }
			}
		};
	};

	// All groups of the current user
	public ResponseHandler<String> getGroupMembersCallBack = httpClient.new ResponseHandler<String>() {
		class Response {
			public String 提示信息;
			public String 失败原因;
			public Relationship relationship;
		}

		@Override
		public void onSuccess(ResponseInfo<String> responseInfo) {
			Response response = gson.fromJson(responseInfo.result, Response.class);
			if (response.提示信息.equals("获取群组成员成功")) {
				if (response.relationship.groups.size() != 0) {
					data.relationship.groups = response.relationship.groups;
					data.relationship.groupsMap = response.relationship.groupsMap;
					data.relationship.friendsMap.putAll(response.relationship.friendsMap);
					data.relationship.isModified = true;
					// init current share
					if (!data.localStatus.localData.currentSelectedGroup.equals("")) {
						if (data.relationship.groupsMap.get(data.localStatus.localData.currentSelectedGroup) == null) {
							data.localStatus.localData.currentSelectedGroup = response.relationship.groups.get(0);
						}
					} else {
						data.localStatus.localData.currentSelectedGroup = response.relationship.groups.get(0);
					}
					// Set the option group dialog content
					log.e(tag, data.relationship.groups.toString());
					viewManage.mainView.shareSubView.setGroupsDialogContent();
					viewManage.mainView.shareSubView.showShareMessages();
					viewManage.mainView.shareSubView.showGroupMembers();
					viewManage.mainView.shareSubView.getCurrentGroupShareMessages();
				}
			}
		};
	};

	public ResponseHandler<String> account_modify = httpClient.new ResponseHandler<String>() {
		class Response {
			public String 提示信息;
			public String 失败原因;
		}

		public void onSuccess(ResponseInfo<String> responseInfo) {
			Response response = gson.fromJson(responseInfo.result, Response.class);
			if (response.提示信息.equals("修改用户位置信息成功") || response.提示信息.equals("创建用户位置信息成功")) {
				log.e(tag, "---------------------修改用户信息成功");
			}
		};

	};
	public ResponseHandler<String> account_modifylocation = httpClient.new ResponseHandler<String>() {
		class Response {
			public String 提示信息;
			public User account;
		}

		public void onSuccess(ResponseInfo<String> responseInfo) {
			Response response = gson.fromJson(responseInfo.result, Response.class);
			if (response.提示信息.equals("修改用户信息成功")) {
				data = parser.check();
				User user = data.userInformation.currentUser;
				user.latitude = response.account.latitude;
				user.longitude = response.account.longitude;
				user.lastlogintime = response.account.lastlogintime;
				data.userInformation.isModified = true;
				viewManage.mainView.thisController.chackLBSAccount();
			}
		};

	};
	public ResponseHandler<String> account_auth = httpClient.new ResponseHandler<String>() {
		class Response {
			public String 提示信息;
			public String 失败原因;
			public String uid;
			public String accessKey;
			public String PbKey;
		}

		public void onSuccess(ResponseInfo<String> responseInfo) {
			Response response = gson.fromJson(responseInfo.result, Response.class);
			if (response.提示信息.equals("普通鉴权成功")) {
				String accessKey = "", phone = "";
				try {
					accessKey = RSAUtils.decrypt(response.PbKey, response.accessKey);
					phone = RSAUtils.decrypt(response.PbKey, response.uid);
				} catch (Exception e) {
					e.printStackTrace();
				}
				data.userInformation.currentUser.phone = phone;
				data.userInformation.currentUser.accessKey = accessKey;
				data.userInformation.isModified = true;
				HttpUtils httpUtils = new HttpUtils();
				RequestParams params = new RequestParams();
				params.addBodyParameter("phone", phone);
				params.addBodyParameter("accessKey", accessKey);
				params.addBodyParameter("target", "[\"" + phone + "\"]");
				ResponseHandlers responseHandlers = getInstance();
				httpUtils.send(HttpMethod.POST, API.ACCOUNT_GET, params, responseHandlers.account_get);
				viewManage.loginView.thisController.loginSuccessful(phone);
			} else {
				viewManage.loginView.thisController.loginFail(response.失败原因);
			}
		};

	};
	public ResponseHandler<String> account_get = httpClient.new ResponseHandler<String>() {
		class Response {
			public String 提示信息;
			public String 失败原因;
			public List<Account> accounts;
		}

		class Account {
			public int ID;
			public String phone;
			public String nickName;
			public String mainBusiness;
			public String head;
			public String sex;
			public String byPhone;
			public String userBackground;
		}

		public void onSuccess(ResponseInfo<String> responseInfo) {
			Response response = gson.fromJson(responseInfo.result, Response.class);
			if ("获取用户信息成功".equals(response.提示信息)) {
				User user = data.userInformation.currentUser;
				Account account = response.accounts.get(0);
				user.id = account.ID;
				user.head = account.head;
				user.mainBusiness = account.mainBusiness;
				user.nickName = account.nickName;
				user.sex = account.sex;
				user.userBackground = account.userBackground;
				data.userInformation.isModified = true;
			}
		};
	};
	public ResponseHandler<String> group_addmembers = httpClient.new ResponseHandler<String>() {
		class Response {
			public String 提示信息;
		}

		public void onSuccess(ResponseInfo<String> responseInfo) {
			Response response = gson.fromJson(responseInfo.result, Response.class);
			if (response.提示信息.equals("加入群组成功")) {
				log.e(tag, "---------------------加入群组成功");
			}
		};

	};

	public ResponseHandler<String> group_modify = httpClient.new ResponseHandler<String>() {
		class Response {
			public String 提示信息;
			public Group group;
		}

		public void onSuccess(ResponseInfo<String> responseInfo) {
			log.e(responseInfo.result);
			Response response = gson.fromJson(responseInfo.result, Response.class);
			if (response.提示信息.equals("修改群组信息成功")) {
				data.relationship.groupsMap.put(String.valueOf(response.group.gid), response.group);
				log.e(tag, "---------------------修改群组信息成功");
			}
		};

	};

	public ResponseHandler<String> share_sendShareCallBack = httpClient.new ResponseHandler<String>() {
		class Response {
			public String 提示信息;
			public String 失败原因;
			public long time;
			public String gid;
			public String gsid;
			public String ogsid;
		}

		@Override
		public void onSuccess(ResponseInfo<String> responseInfo) {
			Response response = gson.fromJson(responseInfo.result, Response.class);
			if (response.提示信息.equals("发布群分享成功")) {
				Share share = data.shares.shareMap.get(response.gid);
				ShareMessage shareMessage = share.shareMessagesMap.get(response.ogsid);
				shareMessage.gsid = response.gsid;
				shareMessage.time = response.time;
				shareMessage.status = "sent";
				int index = share.shareMessagesOrder.indexOf(response.ogsid);
				share.shareMessagesOrder.remove(index);
				share.shareMessagesOrder.add(0, response.gsid);
				share.shareMessagesMap.remove(response.ogsid);
				share.shareMessagesMap.put(shareMessage.gsid, shareMessage);
				viewManage.mainView.shareSubView.showShareMessages();
				log.e(tag, "---------------------发送成功");
			}
		};
	};
	public ResponseHandler<String> share_getSharesCallBack = httpClient.new ResponseHandler<String>() {
		class Response {
			public String 提示信息;
			public String 失败原因;
			public String gid;
			public Share shares;
		}

		@Override
		public void onSuccess(ResponseInfo<String> responseInfo) {
			// log.e(tag, responseInfo.result);
			try {
				Response response = gson.fromJson(responseInfo.result, Response.class);
				if (response.提示信息.equals("获取群分享成功")) {
					Share share = data.shares.shareMap.get(response.gid);
					if (share == null) {
						share = data.shares.new Share();
						data.shares.shareMap.put(response.gid, share);
					}
					List<String> sharesOrder = response.shares.shareMessagesOrder;
					for (int i = sharesOrder.size() - 1; i >= 0; i--) {
						String key = sharesOrder.get(i);
						if (!share.shareMessagesOrder.contains(key)) {
							share.shareMessagesOrder.add(0, key);
						}
					}
					share.shareMessagesMap.putAll(response.shares.shareMessagesMap);
					data.shares.isModified = true;
					viewManage.mainView.shareSubView.showShareMessages();
				}
			} catch (JsonSyntaxException e) {
				e.printStackTrace();
			}
		};
	};
	public ResponseHandler<String> share_modifyPraiseusersCallBack = httpClient.new ResponseHandler<String>() {
		class Response {
			public String 提示信息;
			public String 失败原因;
			public String gid;
			public String gsid;
		}

		@Override
		public void onSuccess(ResponseInfo<String> responseInfo) {
			Response response = gson.fromJson(responseInfo.result, Response.class);
			if (response.提示信息.equals("点赞群分享成功")) {
				log.e(tag, "---------------------点赞群分享成功");
			} else if (response.提示信息.equals("点赞群分享失败")) {
				Share share = data.shares.shareMap.get(response.gid);
				ShareMessage shareMessage = share.shareMessagesMap.get(response.gsid);
				shareMessage.praiseusers.remove(data.userInformation.currentUser.phone);
			}
		};
	};
	public ResponseHandler<String> share_addCommentCallBack = httpClient.new ResponseHandler<String>() {
		class Response {
			public String 提示信息;
			public String 失败原因;
			public String gid;
			public String gsid;
		}

		@Override
		public void onSuccess(ResponseInfo<String> responseInfo) {
			Response response = gson.fromJson(responseInfo.result, Response.class);
			if (response.提示信息.equals("评论群分享成功")) {
				log.e(tag, "---------------------评论群分享成功");
			} else if (response.提示信息.equals("评论群分享失败")) {

			}
		};
	};
	public ResponseHandler<String> relation_modifyAlias = httpClient.new ResponseHandler<String>() {
		class Response {
			public String 提示信息;
			public String 失败原因;
		}

		public void onSuccess(ResponseInfo<String> responseInfo) {
			Response response = gson.fromJson(responseInfo.result, Response.class);
			if (response.提示信息.equals("修改备注成功")) {
				log.e(tag, "---------------------修改备注成功");
			}
		};

	};
	public ResponseHandler<String> relation_deletefriend = httpClient.new ResponseHandler<String>() {
		class Response {
			public String 提示信息;
			public String 失败原因;
		}

		public void onSuccess(ResponseInfo<String> responseInfo) {
			Response response = gson.fromJson(responseInfo.result, Response.class);
			if (response.提示信息.equals("删除成功")) {
				log.e(tag, "---------------------删除成功");
			}
		};
	};
	public ResponseHandler<String> relation_addfriend = httpClient.new ResponseHandler<String>() {
		class Response {
			public String 提示信息;
			public String 失败原因;
		}

		public void onSuccess(ResponseInfo<String> responseInfo) {
			Response response = gson.fromJson(responseInfo.result, Response.class);
			if (response.提示信息.equals("发送请求成功")) {
				log.e(tag, "---------------------发送请求成功");
			}
		};
	};

	public RequestCallBack<String> modifyCircleSequenceCallBack = httpClient.new ResponseHandler<String>() {
		class Response {
			public String 提示信息;
			public String 失败原因;
		}

		public void onSuccess(ResponseInfo<String> responseInfo) {
			Response response = gson.fromJson(responseInfo.result, Response.class);
			if (response.提示信息.equals("修改分组顺序成功")) {
				log.e(tag, "---------------------修改分组顺序成功");
			}
		};
	};

	public RequestCallBack<String> modifyGroupSequenceCallBack = httpClient.new ResponseHandler<String>() {
		class Response {
			public String 提示信息;
			public String 失败原因;
		}

		public void onSuccess(ResponseInfo<String> responseInfo) {
			Response response = gson.fromJson(responseInfo.result, Response.class);
			if (response.提示信息.equals("修改群组顺序成功")) {
				log.e(tag, "---------------------修改群组顺序成功");
			}
		};
	};

	public RequestCallBack<String> addMembersCallBack = httpClient.new ResponseHandler<String>() {
		class Response {
			public String 提示信息;
			public String 失败原因;
		}

		public void onSuccess(ResponseInfo<String> responseInfo) {
			Response response = gson.fromJson(responseInfo.result, Response.class);
			if (response.提示信息.equals("加入群组成功")) {
				log.e(tag, "---------------------加入群组成功");
			}
		};
	};

	public RequestCallBack<String> removeMembersCallBack = httpClient.new ResponseHandler<String>() {
		class Response {
			public String 提示信息;
			public String 失败原因;
		}

		public void onSuccess(ResponseInfo<String> responseInfo) {
			Response response = gson.fromJson(responseInfo.result, Response.class);
			if (response.提示信息.equals("退出群组成功")) {
				log.e(tag, "---------------------退出群组成功");
			}
		};
	};

	public RequestCallBack<String> getaskfriendsCallBack = httpClient.new ResponseHandler<String>() {
		class Response {
			public String 提示信息;
			public String 失败原因;
			public List<Friend> accounts;
		}

		public void onSuccess(ResponseInfo<String> responseInfo) {
			try {
				Response response = gson.fromJson(responseInfo.result, Response.class);
				if (response.提示信息.equals("获取好友请求成功")) {
					if (response.accounts.size() > 0) {
						viewManage.postNotifyView("DynamicListActivity");
					}
					log.e(tag, response.accounts.size() + "---------------------获取好友请求成功");
				}
			} catch (JsonSyntaxException e) {
				e.printStackTrace();
			}
		};
	};
	public RequestCallBack<String> addFriendAgreeCallBack = httpClient.new ResponseHandler<String>() {
		class Response {
			public String 提示信息;
			public String 失败原因;
		}

		public void onSuccess(ResponseInfo<String> responseInfo) {
			Response response = gson.fromJson(responseInfo.result, Response.class);
			if (response.提示信息.equals("添加成功")) {
				log.e(tag, "---------------------添加好友成功");
				DataUtil.getIntimateFriends();
			}
		};
	};
	public RequestCallBack<String> getGroupInfomationCallBack = httpClient.new ResponseHandler<String>() {
		class Response {
			public String 提示信息;
			public String 失败原因;
			public Group group;
		}

		public void onSuccess(ResponseInfo<String> responseInfo) {
			Response response = gson.fromJson(responseInfo.result, Response.class);
			if (response.提示信息.equals("获取群组信息成功")) {
				Group group = response.group;
				parser.check();
				Group currentGroup = data.relationship.groupsMap.get(group.gid + "");
				currentGroup.icon = group.icon;
				currentGroup.name = group.name;
				currentGroup.longitude = group.longitude;
				currentGroup.latitude = group.latitude;
				currentGroup.description = group.description;
				currentGroup.background = group.background;
				data.relationship.isModified = true;
				log.e(tag, "---------------------获取群组信息成功");
			}
		};
	};
	public RequestCallBack<String> getCurrentGroupMembersCallBack = httpClient.new ResponseHandler<String>() {
		class Response {
			public String 提示信息;
			public String 失败原因;
			public String gid;
			public List<String> members;
			public Map<String, Friend> membersMap;
		}

		public void onSuccess(ResponseInfo<String> responseInfo) {
			Response response = gson.fromJson(responseInfo.result, Response.class);
			if (response.提示信息.equals("获取群组成员成功")) {
				parser.check();
				Group group = data.relationship.groupsMap.get(response.gid);
				List<String> members = response.members;
				group.members = response.members;
				Map<String, Friend> membersMap = response.membersMap;

				for (int i = 0; i < members.size(); i++) {
					String key = members.get(i);
					Friend friend = data.relationship.friendsMap.get(key);
					Friend serverFriend = membersMap.get(key);
					if (friend == null) {
						friend = serverFriend;
						data.relationship.friendsMap.put(key, friend);
					} else {
						friend.sex = serverFriend.sex;
						friend.nickName = serverFriend.nickName;
						friend.mainBusiness = serverFriend.mainBusiness;
						friend.head = serverFriend.head;
						friend.longitude = serverFriend.longitude;
						friend.latitude = serverFriend.latitude;
						friend.userBackground = serverFriend.userBackground;
						friend.lastlogintime = serverFriend.lastlogintime;
					}
				}
				data.relationship.isModified = true;

				log.e(tag, "---------------------获取群组成员成功");
			}
		};
	};

	public RequestCallBack<String> getMessageCallBack = httpClient.new ResponseHandler<String>() {
		class Response {
			public String 提示信息;
			public String 失败原因;
			public String flag;
			public List<Message> messages;
		}

		public void onSuccess(ResponseInfo<String> responseInfo) {
			try {
				Response response = gson.fromJson(responseInfo.result, Response.class);
				if (response.提示信息.equals("获取成功")) {
					log.e(tag, "---------------------获取消息成功");
					List<Message> messages = response.messages;
					parser.check();
					data.messages.isModified = true;
					for (int i = 0; i < messages.size(); i++) {
						Message message = messages.get(i);
						String sendType = message.sendType;
						if ("event".equals(sendType)) {
							if (!data.event.userEvents.contains(message)) {
								responseEventHandlers.handleEvent(message);
							}
						} else if ("point".equals(sendType)) {
							List<String> phones = gson.fromJson(message.phoneto, new TypeToken<List<String>>() {
							}.getType());
							ArrayList<Message> friendMessages = data.messages.friendMessageMap.get(phones.get(0));
							if (friendMessages == null) {
								friendMessages = new ArrayList<Message>();
								data.messages.friendMessageMap.put(phones.get(0), friendMessages);
							}
							if (!friendMessages.contains(message)) {
								friendMessages.add(message);
							}
						} else if ("group".equals(sendType)) {
							ArrayList<Message> groupMessages = data.messages.friendMessageMap.get(message.gid);
							if (groupMessages == null) {
								groupMessages = new ArrayList<Message>();
								data.messages.friendMessageMap.put(message.gid, groupMessages);
							}
							if (!groupMessages.contains(message)) {
								groupMessages.add(message);
							}
						}
					}
				}
			} catch (JsonSyntaxException e) {
				e.printStackTrace();
			}
		};
	};
	public RequestCallBack<String> lbsdata_create = httpClient.new ResponseHandler<String>() {
		class Response {
			public int status;
			public String info;
		}

		public void onSuccess(ResponseInfo<String> responseInfo) {
			Response response = gson.fromJson(responseInfo.result, Response.class);
			if (response.status == 1) {
				log.e(tag, "create lbs success");

			} else {
				log.e(tag, "create*" + response.info);
			}
		};
	};
	public RequestCallBack<String> lbsdata_updata = httpClient.new ResponseHandler<String>() {
		class Response {
			public int status;
			public String info;
		}

		public void onSuccess(ResponseInfo<String> responseInfo) {
			Response response = gson.fromJson(responseInfo.result, Response.class);
			if (response.status == 1) {
				log.e(tag, "updata lbs success");

			} else {
				log.e(tag, "updata*" + response.info);
			}
		};
	};
	public RequestCallBack<String> lbsdata_search = httpClient.new ResponseHandler<String>() {
		class Response {
			public int status;
			public String info;
			public int count;
			public ArrayList<data> datas;

			class data {
				public String _id;
			}
		}

		public void onSuccess(ResponseInfo<String> responseInfo) {
			try {

				Response response = gson.fromJson(responseInfo.result, Response.class);
				if (response.status == 1) {
					if (response.count == 0) {
						viewManage.mainView.thisController.creataLBSAccount();
					} else {
						viewManage.mainView.thisController.modifyLBSAccount(response.datas.get(0)._id);
					}
				}
			} catch (Exception e) {

			}
		};
	};
	public RequestCallBack<String> group_create = httpClient.new ResponseHandler<String>() {
		class Response {
			public String 提示信息;
			public String 失败原因;
			public String tempGid;
			public Group group;
		}

		public void onSuccess(ResponseInfo<String> responseInfo) {
			Response response = gson.fromJson(responseInfo.result, Response.class);
			if (response.提示信息.equals("创建群组成功")) {
				data = parser.check();
				data.relationship.groups.remove(response.tempGid);
				data.relationship.groupsMap.remove(response.tempGid);
				data.relationship.groups.add(String.valueOf(response.group.gid));
				data.relationship.groupsMap.put(String.valueOf(response.group.gid), response.group);
				data.relationship.isModified = true;
				log.d("创建群组成功===================");
			} else {
				log.d("创建群组失败===================" + response.失败原因);

			}
		};
	};
}
