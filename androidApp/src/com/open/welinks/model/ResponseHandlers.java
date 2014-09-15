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
import com.open.welinks.model.Data.Relationship.Circle;
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
					if (user.circlesOrderString != null && friend.circlesOrderString != null) {

						if (!user.circlesOrderString.equals(friend.circlesOrderString)) {
							user.circlesOrderString = friend.circlesOrderString;
							try {
								data.relationship.circles = gson.fromJson(user.circlesOrderString, new TypeToken<List<String>>() {
								}.getType());
							} catch (JsonSyntaxException e) {
								e.printStackTrace();
							}
							if (data.localStatus.debugMode.equals("NONE")) {
								// viewManage.postNotifyView("UserIntimateView");
								log.e(tag, "刷新好友分组");
								viewManage.mainView.friendsSubView.showCircles();
							}
						}
					} else {
						if (user.circlesOrderString == null && friend.circlesOrderString != null) {
							user.circlesOrderString = friend.circlesOrderString;
							try {
								data.relationship.circles = gson.fromJson(user.circlesOrderString, new TypeToken<List<String>>() {
								}.getType());
							} catch (JsonSyntaxException e) {
								e.printStackTrace();
							}
							if (data.localStatus.debugMode.equals("NONE")) {
								// viewManage.postNotifyView("UserIntimateView");
								log.e(tag, "刷新好友分组");
								viewManage.mainView.friendsSubView.showCircles();
							}
						}
					}
					if (user.groupsSequenceString != null && friend.groupsSequenceString != null) {
						if (!user.groupsSequenceString.equals(friend.groupsSequenceString)) {
							user.groupsSequenceString = friend.groupsSequenceString;
							try {
								data.relationship.groups = gson.fromJson(user.groupsSequenceString, new TypeToken<List<String>>() {
								}.getType());
							} catch (JsonSyntaxException e) {
								e.printStackTrace();
							}
							viewManage.mainView.shareSubView.setGroupsDialogContent();
							viewManage.postNotifyView("GroupListActivity");
						}
					} else {
						if (user.groupsSequenceString == null && friend.groupsSequenceString != null) {
							user.groupsSequenceString = friend.groupsSequenceString;
							try {
								data.relationship.groups = gson.fromJson(user.groupsSequenceString, new TypeToken<List<String>>() {
								}.getType());
							} catch (JsonSyntaxException e) {
								e.printStackTrace();
							}
							viewManage.mainView.shareSubView.setGroupsDialogContent();
							viewManage.postNotifyView("GroupListActivity");
						}
					}
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
				data.relationship.groups = response.relationship.groups;
				data.relationship.groupsMap.putAll(response.relationship.groupsMap);
				data.relationship.friendsMap.putAll(response.relationship.friendsMap);
				data.relationship.isModified = true;
				// init current share
				String gid = "";
				if (response.relationship.groups.size() != 0) {
					gid = response.relationship.groups.get(0);
				} else {
					gid = "";
				}
				if (!data.localStatus.localData.currentSelectedGroup.equals("")) {
					if (data.relationship.groupsMap.get(data.localStatus.localData.currentSelectedGroup) == null) {
						data.localStatus.localData.currentSelectedGroup = gid;
					}
				} else {
					data.localStatus.localData.currentSelectedGroup = gid;
				}
				// Set the option group dialog content
				log.e(tag, data.relationship.groups.toString());
				if (!gid.equals("")) {
					viewManage.mainView.shareSubView.showShareMessages();
					viewManage.mainView.shareSubView.showGroupMembers();
					viewManage.mainView.shareSubView.getCurrentGroupShareMessages();
				}
				viewManage.mainView.shareSubView.setGroupsDialogContent();
				viewManage.postNotifyView("GroupListActivity");
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
			if (response.提示信息.equals("修改用户信息成功")) {
				if (viewManage.loginView != null) {
					viewManage.loginView.thisController.modifyUserPasswordCallBack();
				}
			} else {
				viewManage.loginView.thisController.loginFail(response.失败原因);
			}
		};

	};
	public ResponseHandler<String> account_modifypassword = httpClient.new ResponseHandler<String>() {
		class Response {
			public String 提示信息;
			public String 失败原因;
			public String uid;
			public String accessKey;
			public String PbKey;
		}

		public void onSuccess(ResponseInfo<String> responseInfo) {
			Response response = gson.fromJson(responseInfo.result, Response.class);
			if (response.提示信息.equals("修改用户密码成功")) {
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
	public ResponseHandler<String> account_verifycode = httpClient.new ResponseHandler<String>() {
		class Response {
			public String 提示信息;
			public String 失败原因;
		}

		public void onSuccess(ResponseInfo<String> responseInfo) {
			Response response = gson.fromJson(responseInfo.result, Response.class);
			if (response.提示信息.equals("验证成功")) {
				viewManage.loginView.thisController.requestUserAuthWithVerifyCodeCallBack();
			} else {
				viewManage.loginView.thisController.loginFail(response.失败原因);
			}
		};

	};
	public ResponseHandler<String> account_verifyphone = httpClient.new ResponseHandler<String>() {
		class Response {
			public String 提示信息;
			public String 失败原因;
		}

		public void onSuccess(ResponseInfo<String> responseInfo) {
			Response response = gson.fromJson(responseInfo.result, Response.class);
			if (response.提示信息.substring(response.提示信息.length()).equals("功")) {
				viewManage.loginView.thisController.requestUserVerifyCodeCallBack();
			} else {
				viewManage.loginView.thisController.loginFail(response.失败原因);
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
			public String 失败原因;
			public Group group;
		}

		public void onSuccess(ResponseInfo<String> responseInfo) {
			Response response = gson.fromJson(responseInfo.result, Response.class);
			if (response.提示信息.equals("修改群组信息成功")) {
				Group group = response.group;
				Group currentGroup = data.relationship.groupsMap.get(group.gid + "");
				currentGroup.icon = group.icon;
				currentGroup.name = group.name;
				currentGroup.longitude = group.longitude;
				currentGroup.latitude = group.latitude;
				currentGroup.description = group.description;
				currentGroup.background = group.background;
				data.relationship.isModified = true;
				viewManage.postNotifyView("ShareSubView");
				viewManage.postNotifyView("GroupListActivity");
				log.e(tag, "---------------------修改群组信息成功");
			} else {
				log.e(responseInfo.result);
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
				parser.check();
				String gid = response.gid;
				String gsid = response.gsid;
				String ogsid = response.ogsid;
				Share share = data.shares.shareMap.get(gid);
				ShareMessage shareMessage = share.shareMessagesMap.get(ogsid);
				shareMessage.gsid = response.gsid;
				shareMessage.time = response.time;
				shareMessage.status = "sent";
				int index = share.shareMessagesOrder.indexOf(ogsid);
				share.shareMessagesOrder.remove(index);
				share.shareMessagesOrder.add(index, gsid);
				share.shareMessagesMap.remove(ogsid);
				share.shareMessagesMap.put(shareMessage.gsid, shareMessage);
				data.shares.isModified = true;
				if (data.relationship.squares.contains(gid)) {
					viewManage.mainView.squareSubView.showSquareMessages();
				} else {
					viewManage.mainView.shareSubView.showShareMessages();
				}
				log.e(tag, "---------------------发送成功");
			}
		};
	};
	public ResponseHandler<String> share_getSharesCallBack = httpClient.new ResponseHandler<String>() {
		class Response {
			public String 提示信息;
			public String 失败原因;
			public String gid;
			public int nowpage;
			public Share shares;
		}

		@Override
		public void onSuccess(ResponseInfo<String> responseInfo) {
			// log.e(tag, responseInfo.result);
			try {
				Response response = gson.fromJson(responseInfo.result, Response.class);
				if (response.提示信息.equals("获取群分享成功")) {
					Share responsesShare = response.shares;
					parser.check();
					String gid = response.gid;
					Share share = data.shares.shareMap.get(gid);
					if (share == null) {
						share = data.shares.new Share();
						data.shares.shareMap.put(gid, share);
					}
					List<String> sharesOrder = responsesShare.shareMessagesOrder;
					if (response.nowpage == 0) {
						for (int i = sharesOrder.size() - 1; i >= 0; i--) {
							String key = sharesOrder.get(i);
							if (!share.shareMessagesOrder.contains(key)) {
								share.shareMessagesOrder.add(0, key);
							}
						}
					} else {
						for (int i = 0; i < sharesOrder.size(); i++) {
							String key = sharesOrder.get(i);
							if (!share.shareMessagesOrder.contains(key)) {
								share.shareMessagesOrder.add(key);
							}
						}
					}

					share.shareMessagesMap.putAll(responsesShare.shareMessagesMap);
					data.shares.isModified = true;
					if (data.relationship.groups.contains(gid)) {
						if (responsesShare.shareMessagesOrder.size() == 0) {
							viewManage.shareSubView.thisController.nowpage--;
						}
						viewManage.mainView.shareSubView.showShareMessages();
					} else {
						if (responsesShare.shareMessagesOrder.size() == 0) {
							viewManage.squareSubView.thisController.nowpage--;
						}
						viewManage.mainView.squareSubView.showSquareMessages();
					}
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
			parser.check();
			Response response = gson.fromJson(responseInfo.result, Response.class);
			// String phone = data.userInformation.currentUser.phone;
			if (response.提示信息.equals("点赞群分享成功")) {
				// Share share = data.shares.shareMap.get(response.gid);
				// ShareMessage shareMessage = share.shareMessagesMap.get(response.gsid);
				// if (shareMessage != null) {
				// if (shareMessage.praiseusers.contains(phone)) {
				// shareMessage.praiseusers.remove(phone);
				// } else {
				// shareMessage.praiseusers.add(phone);
				// }
				// }
				log.e(tag, "---------------------点赞群分享成功");
			} else if (response.提示信息.equals("点赞群分享失败")) {
				// Share share = data.shares.shareMap.get(response.gid);
				// ShareMessage shareMessage = share.shareMessagesMap.get(response.gsid);
				// if (shareMessage != null) {
				// if (shareMessage.praiseusers.contains(phone)) {
				// shareMessage.praiseusers.remove(phone);
				// } else {
				// shareMessage.praiseusers.add(phone);
				// }
				// }
				log.e(tag, "点赞群分享失败---------------------" + response.失败原因);
			}
			// data.shares.isModified = true;
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
				log.e(tag, response.gid + "---------------------评论群分享成功");
			} else {
				log.e(tag, "---------------------" + response.失败原因);
			}
		};
	};

	public ResponseHandler<String> share_delete = httpClient.new ResponseHandler<String>() {
		class Response {
			public String 提示信息;
			public String 失败原因;
		}

		@Override
		public void onSuccess(ResponseInfo<String> responseInfo) {
			Response response = gson.fromJson(responseInfo.result, Response.class);
			if (response.提示信息.equals("删除群分享成功")) {
				log.e("---------------------删除群分享成功");
			} else {
				log.e(tag, "---------------------" + response.失败原因);
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
			} else {
				log.e(tag, "---------------------" + response.失败原因);
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
			} else {
				log.e(tag, "---------------------" + response.失败原因);
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
			} else {
				log.e(tag, "---------------------" + response.失败原因);
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
				viewManage.mainView.friendsSubView.showCircles();
			} else {
				log.e(tag, "---------------------" + response.失败原因);
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
			} else {
				log.e(tag, "---------------------" + response.失败原因);
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
			} else {
				log.e(tag, "---------------------" + response.失败原因);
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
			} else {
				log.e(tag, "---------------------" + response.失败原因);
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
				} else {
					log.e(tag, "---------------------" + response.失败原因);
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
			} else {
				log.e(tag, "---------------------" + response.失败原因);
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
				viewManage.postNotifyView("ShareSubView");
				viewManage.postNotifyView("GroupListActivity");
				log.e(tag, "---------------------获取群组信息成功");
			} else {
				log.e(tag, "---------------------" + response.失败原因);
			}
		};
	};
	public RequestCallBack<String> getCurrentNewGroupMembersCallBack = httpClient.new ResponseHandler<String>() {
		class Response {
			public String 提示信息;
			public String 失败原因;
			public Group group;
			public List<String> members;
			public Map<String, Friend> membersMap;
		}

		public void onSuccess(ResponseInfo<String> responseInfo) {
			Response response = gson.fromJson(responseInfo.result, Response.class);
			if (response.提示信息.equals("获取群组成员成功")) {
				parser.check();
				List<String> members = response.members;
				Group group = response.group;
				Group currentGroup;
				String key0 = group.gid + "";
				if (data.relationship.groups.contains(key0)) {
					currentGroup = data.relationship.groupsMap.get(key0);
					currentGroup.icon = group.icon;
					currentGroup.name = group.name;
					currentGroup.longitude = group.longitude;
					currentGroup.latitude = group.latitude;
					currentGroup.description = group.description;
					currentGroup.background = group.background;
					currentGroup.members = members;
				} else {
					data.relationship.groups.add(key0);
					data.relationship.groupsMap.put(key0, group);
					group.members = members;
				}

				viewManage.postNotifyView("ShareSubView");
				viewManage.postNotifyView("GroupListActivity");

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
			} else {
				log.e(tag, "---------------------" + response.失败原因);
			}
		};
	};
	public RequestCallBack<String> getCurrentGroupMembersCallBack = httpClient.new ResponseHandler<String>() {
		class Response {
			public String 提示信息;
			public String 失败原因;
			public Group group;
			public List<String> members;
			public Map<String, Friend> membersMap;
		}

		public void onSuccess(ResponseInfo<String> responseInfo) {
			Response response = gson.fromJson(responseInfo.result, Response.class);
			if (response.提示信息.equals("获取群组成员成功")) {
				parser.check();
				Group group = data.relationship.groupsMap.get(response.group.gid + "");
				List<String> members = response.members;
				group.members = members;
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

				viewManage.postNotifyView("ShareSubView");
				viewManage.postNotifyView("GroupListActivity");

				log.e(tag, "---------------------获取群组成员成功");
			} else {
				log.e(tag, "---------------------" + response.失败原因);
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
					data.userInformation.currentUser.flag = response.flag;
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
				} else {
					log.e(tag, "---------------------" + response.失败原因);
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
			public String address;
			public Group group;
		}

		public void onSuccess(ResponseInfo<String> responseInfo) {
			Response response = gson.fromJson(responseInfo.result, Response.class);
			if (response.提示信息.equals("创建群组成功")) {
				data = parser.check();
				data.relationship.groups.remove(response.tempGid);
				Group tempGroup = data.relationship.groupsMap.remove(response.tempGid);
				String key = String.valueOf(response.group.gid);
				Group group = response.group;
				Group currentGroup = null;
				if (data.relationship.groups.contains(key)) {
					currentGroup = data.relationship.groupsMap.get(key);
					currentGroup.icon = group.icon;
					currentGroup.name = group.name;
					currentGroup.longitude = group.longitude;
					currentGroup.latitude = group.latitude;
					currentGroup.description = group.description;
					currentGroup.background = group.background;
				} else {
					data.relationship.groups.add(key);
					currentGroup = null;
					if (tempGroup != null) {
						currentGroup = tempGroup;
					} else {
						currentGroup = data.relationship.new Group();
					}
					currentGroup.gid = response.group.gid;
					currentGroup.icon = group.icon;
					currentGroup.name = group.name;
					currentGroup.longitude = group.longitude;
					currentGroup.latitude = group.latitude;
					currentGroup.description = group.description;
					currentGroup.background = group.background;
					data.relationship.groupsMap.put(key, currentGroup);
				}
				viewManage.mainView.thisController.creataLBSGroup(currentGroup, response.address);
				data.relationship.isModified = true;
				viewManage.shareSubView.setGroupsDialogContent();
			} else {
				log.d("创建群组失败===================" + response.失败原因);

			}
		};
	};
	public RequestCallBack<String> circle_modify = httpClient.new ResponseHandler<String>() {
		class Response {
			public String 提示信息;
			public String 失败原因;
		}

		public void onSuccess(ResponseInfo<String> responseInfo) {
			Response response = gson.fromJson(responseInfo.result, Response.class);
			if (response.提示信息.equals("修改成功")) {

			} else {
				log.d("修改失败===================" + response.失败原因);

			}
		};
	};
	public RequestCallBack<String> circle_delete = httpClient.new ResponseHandler<String>() {
		class Response {
			public String 提示信息;
			public String 失败原因;
		}

		public void onSuccess(ResponseInfo<String> responseInfo) {
			Response response = gson.fromJson(responseInfo.result, Response.class);
			if (response.提示信息.equals("删除成功")) {
				viewManage.mainView.friendsSubView.thisController.modifyGroupSequence(gson.toJson(data.relationship.circles));
			} else {
				log.d("删除失败===================" + response.失败原因);

			}
		};
	};
	public RequestCallBack<String> circle_addcircle = httpClient.new ResponseHandler<String>() {
		class Response {
			public String 提示信息;
			public String 失败原因;
			public Circle circle;
			public String rid;
		}

		public void onSuccess(ResponseInfo<String> responseInfo) {
			Response response = gson.fromJson(responseInfo.result, Response.class);
			if (response.提示信息.equals("添加成功")) {
				data = parser.check();
				data.relationship.circles.remove(response.rid);
				data.relationship.circles.add(String.valueOf(response.circle.rid));
				data.relationship.circlesMap.remove(response.rid);
				data.relationship.circlesMap.put(String.valueOf(response.circle.rid), response.circle);
				data.relationship.isModified = true;

				viewManage.mainView.friendsSubView.thisController.modifyGroupSequence(gson.toJson(data.relationship.circles));

			} else {
				log.d("添加失败===================" + response.失败原因);

			}
		};
	};

}
