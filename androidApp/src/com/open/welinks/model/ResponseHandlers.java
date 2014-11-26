package com.open.welinks.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.http.Header;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.open.lib.HttpClient;
import com.open.lib.HttpClient.ResponseHandler;
import com.open.lib.MyLog;
import com.open.welinks.controller.FriendsSubController.Circle2;
import com.open.welinks.model.Data.Boards.Board;
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

	public Gson gson = new Gson();

	public LBSHandlers lbsHandlers = LBSHandlers.getInstance();

	public static ResponseHandlers getInstance() {
		if (responseHandlers == null) {
			responseHandlers = new ResponseHandlers();
		}
		return responseHandlers;
	}

	public HttpClient httpClient = HttpClient.getInstance();

	// TODO Account

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
			public List<User> accounts;
		}

		public void onSuccess(ResponseInfo<String> responseInfo) {
			Response response = gson.fromJson(responseInfo.result, Response.class);
			if ("获取用户信息成功".equals(response.提示信息)) {
				User friend = response.accounts.get(0);
				if (friend != null) {
					parser.check();
					User user = data.userInformation.currentUser;
					user.userBackground = friend.userBackground;
					user.sex = friend.sex;
					user.id = friend.id;
					user.phone = friend.phone;
					user.nickName = friend.nickName;
					user.createTime = friend.createTime;
					user.lastLoginTime = friend.lastLoginTime;
					user.mainBusiness = friend.mainBusiness;
					user.blackList = friend.blackList;
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
								log.e(tag, ViewManage.getErrorLineNumber() + "刷新好友分组");
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
								log.e(tag, ViewManage.getErrorLineNumber() + "刷新好友分组");
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
			} else {
				log.e(ViewManage.getErrorLineNumber() + response.失败原因);
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
				} else {
					lbsHandlers.uplodUserLbsData();
				}
				viewManage.postNotifyView("ChangePasswordActivitySuccess");
				log.e(ViewManage.getErrorLineNumber() + response.提示信息);
			} else {
				log.e(ViewManage.getErrorLineNumber() + response.失败原因);
				viewManage.postNotifyView("ChangePasswordActivityFailed");
				if (viewManage.loginView != null) {
					viewManage.loginView.thisController.loginFail(response.失败原因);
				}
			}
		};

	};
	public ResponseHandler<String> account_modifypassword = httpClient.new ResponseHandler<String>() {
		class Response {
			public String 提示信息;
			public String 失败原因;
		}

		public void onSuccess(ResponseInfo<String> responseInfo) {
			Response response = gson.fromJson(responseInfo.result, Response.class);
			if (response.提示信息.equals("修改用户信息成功")) {
				HttpUtils httpUtils = new HttpUtils();
				RequestParams params = new RequestParams();
				params.addBodyParameter("phone", data.userInformation.currentUser.phone);
				params.addBodyParameter("accessKey", data.userInformation.currentUser.accessKey);
				params.addBodyParameter("target", "[\"" + data.userInformation.currentUser.phone + "\"]");
				httpUtils.send(HttpMethod.POST, API.ACCOUNT_GET, params, httpClient.new ResponseHandler<String>() {
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
						public String age;
						// public String byPhone;
						public String createTime;
						public String lastLoginTime;
						public String userBackground;
					}

					public void onSuccess(ResponseInfo<String> responseInfo) {
						Response response = gson.fromJson(responseInfo.result, Response.class);
						if ("获取用户信息成功".equals(response.提示信息)) {
							User user = data.userInformation.currentUser;
							if (response.accounts.size() > 0) {
								Account account = response.accounts.get(0);
								if (user.phone.equals(account.phone)) {
									user.id = account.ID;
									user.head = account.head;
									user.mainBusiness = account.mainBusiness;
									user.nickName = account.nickName;
									user.sex = account.sex;
									user.age = account.age;
									user.createTime = account.createTime;
									user.lastLoginTime = account.lastLoginTime;
									user.userBackground = account.userBackground;
									data.userInformation.isModified = true;
									viewManage.loginView.thisController.loginSuccessful(data.userInformation.currentUser.phone);
								}
							}
						} else {
							log.e(ViewManage.getErrorLineNumber() + "获取用户信息失败---" + response.失败原因);
						}
					};
				});
			} else {
				viewManage.loginView.thisController.loginFail(response.失败原因);
			}
		};

	};
	public ResponseHandler<String> account_modifylocation = httpClient.new ResponseHandler<String>() {
		class Response {
			public String 提示信息;
			public String 失败原因;
			public User account;
		}

		public void onSuccess(ResponseInfo<String> responseInfo) {
			Response response = gson.fromJson(responseInfo.result, Response.class);
			if (response.提示信息.equals("修改用户信息成功")) {
				log.e(ViewManage.getErrorLineNumber() + "修改用户信息成功");
				data = parser.check();
				User user = data.userInformation.currentUser;
				user.latitude = response.account.latitude;
				user.longitude = response.account.longitude;
				user.lastLoginTime = response.account.lastLoginTime;
				data.userInformation.isModified = true;
				viewManage.mainView.thisController.chackLBSAccount();
			} else {
				log.e(ViewManage.getErrorLineNumber() + "修改用户信息失败---" + response.失败原因);
			}
		};

	};
	public ResponseHandler<String> account_verifycode = httpClient.new ResponseHandler<String>() {
		class Response {
			public String 提示信息;
			public String 失败原因;
			public String uid;
			public String accessKey;
			public String PbKey;
		}

		public void onSuccess(ResponseInfo<String> responseInfo) {
			Response response = gson.fromJson(responseInfo.result, Response.class);
			if (response.提示信息.equals("验证成功")) {
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
			public String age;
			// public String byPhone;
			public String createTime;
			public String lastLoginTime;
			public String userBackground;
			public List<String> blackList;
			// public String friendStatus;
		}

		public void onSuccess(ResponseInfo<String> responseInfo) {
			Response response = gson.fromJson(responseInfo.result, Response.class);
			if ("获取用户信息成功".equals(response.提示信息)) {
				User user = data.userInformation.currentUser;
				if (response.accounts.size() > 0) {
					Account account = response.accounts.get(0);
					if (user.phone.equals(account.phone)) {
						user.id = account.ID;
						user.head = account.head;
						user.mainBusiness = account.mainBusiness;
						user.nickName = account.nickName;
						user.sex = account.sex;
						user.age = account.age;
						user.createTime = account.createTime;
						user.lastLoginTime = account.lastLoginTime;
						user.userBackground = account.userBackground;
						user.blackList = account.blackList;
						data.userInformation.isModified = true;
					} else {
						boolean isTemp = true;
						List<String> circles = data.relationship.circles;
						for (String circle : circles) {
							List<String> friends = data.relationship.circlesMap.get(circle).friends;
							if (friends.contains(account.phone)) {
								isTemp = false;
								break;
							}
						}
						if (data.relationship.circlesMap.get("8888888") != null) {
							if (data.relationship.circlesMap.get("8888888").friends.contains(account.phone)) {
								isTemp = false;
							}
						}
						if (isTemp) {
							Friend friend = data.relationship.new Friend();
							friend.phone = account.phone;
							friend.head = account.head;
							friend.nickName = account.nickName;
							friend.mainBusiness = account.mainBusiness;
							friend.sex = account.sex;
							friend.age = Integer.valueOf(account.age);
							friend.createTime = account.createTime;
							friend.lastLoginTime = account.lastLoginTime;
							friend.userBackground = account.userBackground;
							friend.id = account.ID;
							// friend.friendStatus = account.friendStatus;

							data.tempData.tempFriend = friend;

							data.relationship.friendsMap.put(friend.phone, friend);
						} else {
							Friend friend = data.relationship.friendsMap.get(account.phone);
							if (friend != null) {
								friend.head = account.head;
								friend.nickName = account.nickName;
								friend.mainBusiness = account.mainBusiness;
								friend.sex = account.sex;
								friend.age = Integer.valueOf(account.age);
								friend.createTime = account.createTime;
								friend.lastLoginTime = account.lastLoginTime;
								friend.userBackground = account.userBackground;
								// friend.friendStatus = account.friendStatus;
							}
						}
						if (viewManage.searchFriendActivity != null) {
							viewManage.searchFriendActivity.searchCallBack(account.phone, isTemp);
						}
					}
					viewManage.postNotifyView("MeSubView");
				}
			} else {
				if ("获取用户信息失败".equals(response.提示信息) && "用户不存在".equals(response.失败原因)) {
					viewManage.searchFriendActivity.searchCallBack("", false);
				}
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
				log.e(tag, ViewManage.getErrorLineNumber() + "---------------------修改分组顺序成功");
				viewManage.mainView.friendsSubView.showCircles();
			} else {
				log.e(tag, ViewManage.getErrorLineNumber() + "---------------------" + response.失败原因);
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
				log.e(tag, ViewManage.getErrorLineNumber() + "---------------------修改群组顺序成功");
			} else {
				log.e(tag, ViewManage.getErrorLineNumber() + "---------------------" + response.失败原因);
			}
		};
	};

	// TODO Relationship

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
				log.e(tag, ViewManage.getErrorLineNumber() + "获取密友圈成功");
				List<String> circles = response.relationship.circles;
				String defaultCircleName = null;
				if (data.relationship.circlesMap != null) {
					Circle circle = data.relationship.circlesMap.get("8888888");
					if (circle != null) {
						defaultCircleName = circle.name;
					}
				}
				data.relationship.circles = circles;
				Map<String, Circle> circlesMap = response.relationship.circlesMap;
				data.relationship.circlesMap = circlesMap;
				Map<String, Friend> friendsMap = response.relationship.friendsMap;
				Iterator<Entry<String, Friend>> iterator = friendsMap.entrySet().iterator();
				List<String> singleDeleteFriendList = new ArrayList<String>();
				if (data.relationship.friendsMap != null && data.relationship.friendsMap.size() != 0) {
					while (iterator.hasNext()) {
						Map.Entry<String, Friend> entry = iterator.next();
						String key = entry.getKey();
						Friend friend = entry.getValue();
						if (data.relationship.friendsMap.get(key) != null) {
							Friend oldFriend = data.relationship.friendsMap.get(key);
							oldFriend.phone = friend.phone;
							oldFriend.head = friend.head;
							oldFriend.nickName = friend.nickName;
							oldFriend.mainBusiness = friend.mainBusiness;
							oldFriend.sex = friend.sex;
							oldFriend.age = Integer.valueOf(friend.age);
							oldFriend.createTime = friend.createTime;
							oldFriend.lastLoginTime = friend.lastLoginTime;
							oldFriend.userBackground = friend.userBackground;
							oldFriend.id = friend.id;
							oldFriend.friendStatus = friend.friendStatus;
							if ("delete".equals(friend.friendStatus)) {
								singleDeleteFriendList.add(friend.phone);
							}
						} else {
							data.relationship.friendsMap.put(key, friend);
						}
					}
				} else {
					data.relationship.friendsMap.putAll(response.relationship.friendsMap);
				}

				Set<String> set = new LinkedHashSet<String>();
				set.clear();
				set.addAll(singleDeleteFriendList);
				singleDeleteFriendList.clear();
				singleDeleteFriendList.addAll(set);
				if (data.relationship.friends == null) {
					data.relationship.friends = new ArrayList<String>();
				} else {
					data.relationship.friends.clear();
				}
				for (int i = 0; i < circles.size(); i++) {
					Circle circle = circlesMap.get(circles.get(i));
					if (circle.rid == Constant.DEFAULTCIRCLEID) {
						if (defaultCircleName != null) {
							circle.name = defaultCircleName;
						}
					}
					data.relationship.friends.addAll(circle.friends);
				}

				set.clear();
				set.addAll(data.relationship.friends);
				data.relationship.friends.clear();
				data.relationship.friends.addAll(set);

				data.relationship.friends.removeAll(singleDeleteFriendList);

				data.relationship.isModified = true;
				if (data.localStatus.debugMode.equals("NONE")) {
					// viewManage.postNotifyView("UserIntimateView");
					viewManage.postNotifyView("DynamicListActivity");
					viewManage.postNotifyView("MessagesSubView");
					log.e(tag, ViewManage.getErrorLineNumber() + "刷新好友分组");
					viewManage.mainView.friendsSubView.showCircles();
					viewManage.postNotifyView("CirclesManageView");
				}
				DataHandlers.getMessages(data.userInformation.currentUser.flag);
				DataHandlers.clearInvalidFriendMessages();
			} else {
				log.e(tag, ViewManage.getErrorLineNumber() + response.提示信息 + "---------------------" + response.失败原因);
			}
		}
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
					log.e(tag, ViewManage.getErrorLineNumber() + response.accounts.size() + "---------------------获取好友请求成功");
				} else {
					log.e(tag, ViewManage.getErrorLineNumber() + "---------------------" + response.失败原因);
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
				log.e(tag, ViewManage.getErrorLineNumber() + "---------------------添加好友成功");
				DataHandlers.getIntimateFriends();
			} else {
				log.e(tag, ViewManage.getErrorLineNumber() + "---------------------" + response.失败原因);
			}
		};
	};
	public RequestCallBack<String> updateContactCallBack = httpClient.new ResponseHandler<String>() {
		class Response {
			public String 提示信息;
			public String 失败原因;
		}

		public void onSuccess(ResponseInfo<String> responseInfo) {
			Response response = gson.fromJson(responseInfo.result, Response.class);
			if (response.提示信息.equals("更新通讯录成功")) {
				log.e(tag, ViewManage.getErrorLineNumber() + "---------------------更新通讯录成功");
			} else {
				log.e(tag, ViewManage.getErrorLineNumber() + "---------------------" + response.失败原因);
			}
		};
	};
	public RequestCallBack<String> modifyCircleCallBack = httpClient.new ResponseHandler<String>() {
		class Response {
			public String 提示信息;
			public String 失败原因;
		}

		public void onSuccess(ResponseInfo<String> responseInfo) {
			Response response = gson.fromJson(responseInfo.result, Response.class);
			if (response.提示信息.equals("修改成功")) {
				log.e(tag, ViewManage.getErrorLineNumber() + "modifyCircleCallBack---------------------修改好友分组成功");
			} else {
				log.e(tag, ViewManage.getErrorLineNumber() + "modifyCircleCallBack---------------------" + response.失败原因);
			}
		};
	};
	// TODO Upload

	public ResponseHandler<String> upload = httpClient.new ResponseHandler<String>() {
		@Override
		public void onSuccess(ResponseInfo<String> responseInfo) {
			log.e(tag, ViewManage.getErrorLineNumber() + responseInfo + "-------------");
			Header[] headers = responseInfo.getAllHeaders();
			for (int i = 0; i < headers.length; i++) {
				log.e(tag, ViewManage.getErrorLineNumber() + "reply upload: " + headers[i]);
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
			log.e(tag, ViewManage.getErrorLineNumber() + responseInfo.result);
			if (response.提示信息.equals("查找成功")) {
				if (response.exists) {

				} else if (!response.exists) {
					log.e(tag, ViewManage.getErrorLineNumber() + response.signature + "---" + response.filename + "---" + response.expires + "---" + response.OSSAccessKeyId);
					// Debug1Controller.uploadImageWithInputStreamUploadEntity(response.signature, response.filename, response.expires, response.OSSAccessKeyId);
				}
			} else {
				log.e(ViewManage.getErrorLineNumber() + response.失败原因);
			}
		}
	};
	public ResponseHandler<String> relation_modifyAlias = httpClient.new ResponseHandler<String>() {
		class Response {
			public String 提示信息;
			public String 失败原因;
		}

		public void onSuccess(ResponseInfo<String> responseInfo) {
			Response response = gson.fromJson(responseInfo.result, Response.class);
			if (response.提示信息.equals("修改备注成功")) {
				log.e(tag, ViewManage.getErrorLineNumber() + "---------------------修改备注成功");
			} else {
				log.e(tag, ViewManage.getErrorLineNumber() + "---------------------" + response.失败原因);
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
			if (response.提示信息.equals("删除好友成功")) {
				log.e(tag, ViewManage.getErrorLineNumber() + "---------------------删除成功");
			} else {
				log.e(tag, ViewManage.getErrorLineNumber() + "---------------------" + response.失败原因);
			}
		};
	};
	public ResponseHandler<String> relation_blackList = httpClient.new ResponseHandler<String>() {
		class Response {
			public String 提示信息;
			public String 失败原因;
		}

		public void onSuccess(ResponseInfo<String> responseInfo) {
			Response response = gson.fromJson(responseInfo.result, Response.class);
			if (response.提示信息.equals("更新黑名单成功")) {
				log.e(tag, ViewManage.getErrorLineNumber() + "relation_blackList:更新黑名单成功");
			} else {
				log.e(tag, ViewManage.getErrorLineNumber() + "relation_blackList:" + response.失败原因);
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
				log.e(tag, ViewManage.getErrorLineNumber() + "---------------------发送请求成功");
			} else if (response.提示信息.equals("添加好友成功")) {
				DataHandlers.getIntimateFriends();
			} else {
				log.e(tag, ViewManage.getErrorLineNumber() + "---------------------" + response.失败原因);
			}
		};
	};
	// TODO Message

	public ResponseHandler<String> message_sendMessageCallBack = httpClient.new ResponseHandler<String>() {
		class Response {
			public String 提示信息;
			public String 失败原因;
			public String time;
			public String oldTime;
			public String sendType;
			public String gid;
			public String phoneTo;
		}

		@Override
		public void onSuccess(ResponseInfo<String> responseInfo) {
			Response response = gson.fromJson(responseInfo.result, Response.class);
			if (response.提示信息.equals("发送成功")) {
				// log.e(responseInfo.result);
				parser.check();
				if (response.sendType != null) {
					if ("point".equals(response.sendType)) {
						// TODO
						List<String> phones = gson.fromJson(response.phoneTo, new TypeToken<List<String>>() {
						}.getType());
						String key = phones.get(0);
						ArrayList<Message> messages = data.messages.friendMessageMap.get("p" + key);
						if (messages != null) {
							Message message0 = null;
							for (int i = 0; i < messages.size(); i++) {
								Message message = messages.get(i);
								if (message.time.equals(response.oldTime)) {
									message0 = message;
									break;
								}
							}
							if (message0 != null) {
								log.e(ViewManage.getErrorLineNumber() + "修改聊天数据成功point");
								if (message0.status.equals("sending")) {
									message0.time = response.time;
								}
								message0.status = "sent";
								viewManage.postNotifyView("ChatMessage");
							} else {
								log.e(ViewManage.getErrorLineNumber() + "修改聊天数据失败point");
							}
						}
					} else if ("group".equals(response.sendType)) {
						ArrayList<Message> messages = data.messages.groupMessageMap.get("g" + response.gid);
						if (messages != null) {
							Message message0 = null;
							for (int i = 0; i < messages.size(); i++) {
								Message message = messages.get(i);
								if ("event".equals(message.sendType)) {
								} else {
									if (message.time.equals(response.oldTime)) {
										message0 = message;
										break;
									}
								}
							}
							if (message0 != null) {
								log.e(ViewManage.getErrorLineNumber() + "修改发送数据成功group");
								if (message0.status.equals("sending")) {
									message0.time = response.time;
								}
								message0.status = "sent";
								viewManage.postNotifyView("ChatMessage");
							} else {
								log.e(ViewManage.getErrorLineNumber() + "修改发送数据失败group");
							}
						}
					}
				}

				// TODO modify local data

				// if ("point".equals(response.sendType)) {
				// //
				// data.messages.friendMessageMap.get(response.phoneto).get(0).time = String.valueOf(response.time);
				// } else if ("group".equals(response.sendType)) {
				// //
				// data.messages.groupMessageMap.get(response.gid).get(0).time = String.valueOf(response.time);
				// }
			} else if (response.提示信息.equals("发送失败")) {
				if (response.sendType != null) {
					if ("point".equals(response.sendType)) {

					} else if ("group".equals(response.sendType)) {

					}
				}
				log.e(tag, ViewManage.getErrorLineNumber() + response.提示信息 + "---------------------" + response.失败原因);
			} else {
				log.e(tag, ViewManage.getErrorLineNumber() + response.提示信息 + "---------------------" + response.失败原因);
			}
		};
	};
	public RequestCallBack<String> getMessageCallBack = httpClient.new ResponseHandler<String>() {
		class Response {
			public String 提示信息;
			public String 失败原因;
			public String flag;
			public List<String> messages;
		}

		public void onSuccess(ResponseInfo<String> responseInfo) {
			try {
				Response response = gson.fromJson(responseInfo.result, Response.class);
				if (response.提示信息.equals("获取成功")) {
					log.e(tag, ViewManage.getErrorLineNumber() + response.提示信息 + "---------------------获取消息成功" + response.flag);
					List<String> messages = response.messages;
					parser.check();
					User user = data.userInformation.currentUser;
					user.flag = response.flag;
					data.userInformation.isModified = true;
					data.messages.isModified = true;
					log.e(ViewManage.getErrorLineNumber() + "message size:" + messages.size());
					for (int i = 0; i < messages.size(); i++) {
						Message message = null;
						try {
							message = gson.fromJson(messages.get(i), Message.class);
						} catch (Exception e) {
							e.printStackTrace();
							log.e(tag, ViewManage.getErrorLineNumber() + "gson message Exception");
							continue;
						}
						String sendType = message.sendType;
						if ("event".equals(sendType)) {
							responseEventHandlers.handleEvent(message);

						} else if ("point".equals(sendType)) {
							String key = message.phone;
							message.type = Constant.MESSAGE_TYPE_RECEIVE;
							if (key.equals(user.phone)) {
								List<String> phones = gson.fromJson(message.phoneto, new TypeToken<List<String>>() {
								}.getType());
								key = phones.get(0);
								message.type = Constant.MESSAGE_TYPE_SEND;
							}
							String messageKey = "p" + key;
							ArrayList<Message> friendMessages = data.messages.friendMessageMap.get(messageKey);
							if (friendMessages == null) {
								friendMessages = new ArrayList<Message>();
								data.messages.friendMessageMap.put(messageKey, friendMessages);
							}
							if (!data.messages.messagesOrder.contains(messageKey)) {
								if (data.relationship.friends.contains(key)) {
									data.messages.messagesOrder.add(0, messageKey);
									if (!DataHandlers.contains(friendMessages, message)) {
										friendMessages.add(message);
										Friend friend = data.relationship.friendsMap.get(key);
										if (friend != null) {
											friend.notReadMessagesCount++;
										}
									}
								}
							} else {
								if (data.relationship.friends.contains(key)) {
									data.messages.messagesOrder.remove(messageKey);
									data.messages.messagesOrder.add(0, messageKey);
									if (!DataHandlers.contains(friendMessages, message)) {
										friendMessages.add(message);
										Friend friend = data.relationship.friendsMap.get(key);
										if (friend != null) {
											friend.notReadMessagesCount++;
										}
									}
								}
							}
						} else if ("group".equals(sendType)) {
							String key = message.gid;
							String messageKey = "g" + message.gid;
							if (message.phone.equals(user.phone)) {
								message.type = Constant.MESSAGE_TYPE_SEND;
							} else {
								message.type = Constant.MESSAGE_TYPE_RECEIVE;
							}
							ArrayList<Message> groupMessages = data.messages.groupMessageMap.get(messageKey);
							if (groupMessages == null) {
								groupMessages = new ArrayList<Message>();
								data.messages.groupMessageMap.put(messageKey, groupMessages);
							}
							if (!data.messages.messagesOrder.contains(messageKey)) {
								if (data.relationship.groups.contains(key)) {
									data.messages.messagesOrder.add(messageKey);
									if (!DataHandlers.contains(groupMessages, message)) {
										groupMessages.add(message);
										Group group = data.relationship.groupsMap.get(key);
										if (group != null) {
											group.notReadMessagesCount++;
										}
									}
								}
							} else {
								if (data.relationship.groups.contains(key)) {
									data.messages.messagesOrder.remove(messageKey);
									data.messages.messagesOrder.add(0, messageKey);
									if (!DataHandlers.contains(groupMessages, message)) {
										groupMessages.add(message);
										Group group = data.relationship.groupsMap.get(key);
										if (group != null) {
											group.notReadMessagesCount++;
										}
									}
								}
							}
						}
					}
					data.event.isModified = true;
					data.messages.isModified = true;
					viewManage.messagesSubView.showMessagesSequence();
				} else {
					log.e(tag, ViewManage.getErrorLineNumber() + response.提示信息 + "---------------------" + response.失败原因);
				}
			} catch (JsonSyntaxException e) {
				e.printStackTrace();
				log.e(tag, ViewManage.getErrorLineNumber() + e.toString() + "");
			}
		};
	};
	// TODO Group
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
					currentGroup.createTime = group.createTime;
					currentGroup.background = group.background;
					currentGroup.cover = group.cover;
					currentGroup.permission = group.permission;
					currentGroup.boards = group.boards;
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
					currentGroup.createTime = group.createTime;
					currentGroup.background = group.background;
					currentGroup.cover = group.cover;
					currentGroup.permission = group.permission;
					currentGroup.boards = group.boards;
					data.relationship.groupsMap.put(key, currentGroup);
				}
				viewManage.mainView.thisController.creataLBSGroup(currentGroup, response.address);
				data.relationship.isModified = true;
				viewManage.shareSubView.setGroupsDialogContent();
			} else {
				log.d(ViewManage.getErrorLineNumber() + "创建群组失败===================" + response.失败原因);

			}
		};
	};
	public RequestCallBack<String> group_getGroupBoards = httpClient.new ResponseHandler<String>() {
		class Response {
			public String 提示信息;
			public String 失败原因;
			public String gid;
			public List<String> boards;
			public Map<String, Board> boardsMap;
		}

		public void onSuccess(ResponseInfo<String> responseInfo) {
			Response response = gson.fromJson(responseInfo.result, Response.class);
			if (response.提示信息.equals("获取版块成功")) {
				data = parser.check();
				Group group = data.relationship.groupsMap.get(response.gid);
				if (group != null) {
					group.boards = response.boards;
					if (data.relationship.squares.contains(response.gid)) {
						viewManage.squareSubView.thisController.getCurrentSquareShareMessages();
					} else {
						viewManage.shareSubView.getCurrentGroupShareMessages();
					}
				}
			} else {
				log.d(ViewManage.getErrorLineNumber() + response.失败原因);

			}
		};
	};
	public ResponseHandler<String> getGroupMembersCallBack = httpClient.new ResponseHandler<String>() {
		class Response {
			public String 提示信息;
			public String 失败原因;
			public Relationship relationship;
		}

		@Override
		public void onSuccess(ResponseInfo<String> responseInfo) {
			try {
				Response response = gson.fromJson(responseInfo.result, Response.class);
				if (response.提示信息.equals("获取群组成员成功")) {
					parser.check();
					data.relationship.groups = response.relationship.groups;
					Map<String, Group> groupsMap = response.relationship.groupsMap;
					for (Entry<String, Group> entity : groupsMap.entrySet()) {
						String key = entity.getKey();
						Group currentGroup = entity.getValue();
						if (data.relationship.groupsMap.containsKey(key)) {
							Group group = data.relationship.groupsMap.get(key);
							group.icon = currentGroup.icon;
							group.name = currentGroup.name;
							group.distance = currentGroup.distance;
							group.longitude = currentGroup.longitude;
							group.latitude = currentGroup.latitude;
							group.description = currentGroup.description;
							group.background = currentGroup.background;
							group.cover = currentGroup.cover;
							group.permission = currentGroup.permission;
							group.members = currentGroup.members;
							group.boards = currentGroup.boards;
						} else {
							data.relationship.groupsMap.put(key, currentGroup);
						}
					}

					Map<String, Friend> friendsMap = response.relationship.friendsMap;
					Iterator<Entry<String, Friend>> iterator = friendsMap.entrySet().iterator();
					if (data.relationship.friendsMap != null && data.relationship.friendsMap.size() != 0) {
						while (iterator.hasNext()) {
							Map.Entry<String, Friend> entry = iterator.next();
							String key = entry.getKey();
							Friend friend = entry.getValue();
							if (data.relationship.friendsMap.get(key) != null) {
								Friend oldFriend = data.relationship.friendsMap.get(key);
								oldFriend.phone = friend.phone;
								oldFriend.head = friend.head;
								oldFriend.nickName = friend.nickName;
								oldFriend.mainBusiness = friend.mainBusiness;
								oldFriend.sex = friend.sex;
								oldFriend.age = Integer.valueOf(friend.age);
								oldFriend.createTime = friend.createTime;
								oldFriend.lastLoginTime = friend.lastLoginTime;
								oldFriend.userBackground = friend.userBackground;
								oldFriend.id = friend.id;
								// oldFriend.friendStatus = friend.friendStatus;
							} else {
								data.relationship.friendsMap.put(key, friend);
							}
						}
					} else {
						data.relationship.friendsMap.putAll(response.relationship.friendsMap);
					}

					data.relationship.isModified = true;
					// init current share
					String gid = "";
					if (response.relationship.groups.size() != 0) {
						gid = response.relationship.groups.get(0);
					} else {
						gid = "";
						data.localStatus.localData.currentSelectedGroup = gid;
					}
					if (!data.localStatus.localData.currentSelectedGroup.equals("")) {
						if (data.relationship.groupsMap.get(data.localStatus.localData.currentSelectedGroup) == null) {
							data.localStatus.localData.currentSelectedGroup = gid;
							if (response.relationship.groups.size() != 0) {
								gid = response.relationship.groups.get(0);
							}
						}
					} else {
						data.localStatus.localData.currentSelectedGroup = gid;
					}
					// Set the option group dialog content
					log.e(tag, ViewManage.getErrorLineNumber() + data.relationship.groups.toString());
					if (!gid.equals("")) {
						viewManage.mainView.shareSubView.showShareMessages();
						viewManage.mainView.shareSubView.showTopMenuRoomName();
						viewManage.mainView.shareSubView.getCurrentGroupShareMessages();
					}
					if (!"".equals(data.localStatus.localData.currentSelectedGroup)) {
						Group group = data.relationship.groupsMap.get(data.localStatus.localData.currentSelectedGroup);
						if (group != null) {
							viewManage.mainView.shareSubView.showTopMenuRoomName();
							boolean flag = false;
							if (group.currentBoard != null && !"".equals(group.currentBoard)) {
								if (group.boards == null) {
									group.boards = new ArrayList<String>();
								}
								if (group.boards.contains(group.currentBoard)) {
									viewManage.shareSubView.showShareMessages();
									viewManage.shareSubView.getCurrentGroupShareMessages();
								} else {
									flag = true;
								}
							} else {
								flag = true;
							}
							if (flag) {
								if (group.boards.size() > 0) {
									group.currentBoard = group.boards.get(0);
									viewManage.shareSubView.showShareMessages();
									viewManage.shareSubView.getCurrentGroupShareMessages();
								} else {
									log.e(ViewManage.getErrorLineNumber() + "异常数据" + ViewManage.getErrorLineNumber());
								}
							}
							DataHandlers.getGroupBoards(data.localStatus.localData.currentSelectedGroup);
						} else {
							log.e(ViewManage.getErrorLineNumber() + "异常数据" + ViewManage.getErrorLineNumber());
						}
						log.e(ViewManage.getErrorLineNumber() + "board:" + group.currentBoard);
					}
					viewManage.mainView.shareSubView.setGroupsDialogContent();
					viewManage.postNotifyView("GroupListActivity");
					DataHandlers.clearInvalidGroupMessages();
				} else {
					log.e(ViewManage.getErrorLineNumber() + response.失败原因);
				}
			} catch (JsonSyntaxException e) {
				e.printStackTrace();
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
				parser.check();
				DataHandlers.getUserCurrentAllGroup();
				log.e(tag, ViewManage.getErrorLineNumber() + "---------------------加入群组成功");
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
				log.e(ViewManage.getErrorLineNumber() + "nullPointException:" + currentGroup);
				String key = currentGroup.gid + "";
				currentGroup.icon = group.icon;
				currentGroup.name = group.name;
				currentGroup.longitude = group.longitude;
				currentGroup.latitude = group.latitude;
				currentGroup.createTime = group.createTime;
				currentGroup.description = group.description;
				currentGroup.background = group.background;
				currentGroup.boards = group.boards;
				if (currentGroup.cover != null && !currentGroup.cover.equals(group.cover)) {
					if (data.localStatus.localData.currentSelectedGroup.equals(group.gid + "")) {
						viewManage.shareSubView.setConver();
					}
				}
				currentGroup.cover = group.cover;
				currentGroup.permission = group.permission;
				data.relationship.isModified = true;
				viewManage.postNotifyView("ShareSubView");
				viewManage.postNotifyView("GroupListActivity");
				log.e(tag, ViewManage.getErrorLineNumber() + "---------------------修改群组信息成功, is square:" + data.relationship.squares.contains(key));
				if (data.relationship.squares.contains(key)) {
					lbsHandlers.uplodSquareLbsData(key);
				} else if (data.relationship.groups.contains(key)) {
					lbsHandlers.uplodGroupLbsData(key);
				}
			} else {
				log.e(ViewManage.getErrorLineNumber() + responseInfo.result);
				log.e(ViewManage.getErrorLineNumber() + response.失败原因);
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
				log.e(tag, ViewManage.getErrorLineNumber() + "---------------------加入群组成功");
			} else {
				log.e(tag, ViewManage.getErrorLineNumber() + "---------------------" + response.失败原因);
			}
			DataHandlers.getUserCurrentAllGroup();
		};
	};

	public RequestCallBack<String> removeMembersCallBack = httpClient.new ResponseHandler<String>() {
		class Response {
			public String 提示信息;
			public String 失败原因;
		}

		public void onSuccess(ResponseInfo<String> responseInfo) {
			Response response = gson.fromJson(responseInfo.result, Response.class);
			DataHandlers.getUserCurrentAllGroup();
			if (response.提示信息.equals("退出群组成功")) {
				log.e(tag, ViewManage.getErrorLineNumber() + "---------------------退出群组成功");
			} else {
				log.e(tag, ViewManage.getErrorLineNumber() + "---------------------" + response.失败原因);
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
				if (currentGroup != null) {
					currentGroup.icon = group.icon;
					currentGroup.name = group.name;
					currentGroup.longitude = group.longitude;
					currentGroup.latitude = group.latitude;
					currentGroup.description = group.description;
					currentGroup.createTime = group.createTime;
					currentGroup.background = group.background;
					currentGroup.boards = group.boards;
					boolean flag = data.localStatus.localData.currentSelectedGroup.equals(group.gid + "");
					if (flag) {
						boolean flag2 = group.cover.equals(currentGroup.cover);
						if (!flag2) {
							currentGroup.cover = group.cover;
							viewManage.shareSubView.setConver();
						}
					}
					currentGroup.permission = group.permission;
					data.relationship.isModified = true;
					viewManage.postNotifyView("ShareSubView");
					viewManage.postNotifyView("GroupListActivity");
				}

				log.e(tag, ViewManage.getErrorLineNumber() + "---------------------获取群组信息成功");
			} else {
				log.e(tag, ViewManage.getErrorLineNumber() + "---------------------" + response.失败原因);
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
					currentGroup.createTime = group.createTime;
					currentGroup.background = group.background;
					currentGroup.cover = group.cover;
					currentGroup.permission = group.permission;
					currentGroup.members = members;
					currentGroup.boards = group.boards;
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
						friend.createTime = serverFriend.createTime;
						friend.userBackground = serverFriend.userBackground;
						friend.lastLoginTime = serverFriend.lastLoginTime;
						// friend.friendStatus = serverFriend.friendStatus;
					}
				}
				data.relationship.isModified = true;

				log.e(tag, ViewManage.getErrorLineNumber() + "---------------------获取群组成员成功");
			} else {
				log.e(tag, ViewManage.getErrorLineNumber() + "---------------------" + response.失败原因);
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
				if (group != null) {
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
							friend.createTime = serverFriend.createTime;
							friend.userBackground = serverFriend.userBackground;
							friend.lastLoginTime = serverFriend.lastLoginTime;
							// friend.friendStatus = serverFriend.friendStatus;
						}
					}
					data.relationship.isModified = true;

					viewManage.postNotifyView("ShareSubView");
					viewManage.postNotifyView("GroupListActivity");
				}
				log.e(tag, ViewManage.getErrorLineNumber() + "---------------------获取群组成员成功");
			} else {
				log.e(tag, ViewManage.getErrorLineNumber() + "---------------------" + response.失败原因);
			}
		};
	};

	// TODO Share

	public class ResponseHandler2<T> extends ResponseHandler<T> {

		public String gid;
		public String ogsid;

		ResponseHandler2(HttpClient httpClient) {
			httpClient.super();
		}
	};

	public ResponseHandler2<String> share_sendShareCallBack = new ResponseHandler2<String>(httpClient) {
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
				if (shareMessage != null) {
					shareMessage.gsid = response.gsid;
					shareMessage.time = response.time;
					shareMessage.status = "sent";
				}
				int index = share.shareMessagesOrder.indexOf(ogsid);
				if (index != -1 && shareMessage != null) {
					share.shareMessagesOrder.remove(index);
					share.shareMessagesOrder.add(index, gsid);
					share.shareMessagesMap.remove(ogsid);
					share.shareMessagesMap.put(shareMessage.gsid, shareMessage);
				}
				data.shares.isModified = true;
				if (data.localStatus.localData.shareReleaseSequece != null) {
					data.localStatus.localData.shareReleaseSequece.remove(ogsid);
				}
				if (data.localStatus.localData.shareReleaseSequeceMap != null) {
					data.localStatus.localData.shareReleaseSequeceMap.remove(ogsid);
				}

				if (data.relationship.squares.contains(gid)) {
					if (data.localStatus.localData.currentSelectedSquare.equals(gid)) {

						viewManage.mainView.squareSubView.showSquareMessages(true);
					}
				} else {
					if (data.localStatus.localData.currentSelectedGroup.equals(gid)) {
						viewManage.mainView.shareSubView.showShareMessages();
					}
				}
				log.e(tag, ViewManage.getErrorLineNumber() + "---------------------发送成功");
			} else if (response.失败原因.equals("发布群分享失败")) {
				parser.check();
				String gid = response.gid;
				String ogsid = response.ogsid;
				Share share = data.shares.shareMap.get(gid);
				ShareMessage shareMessage = null;
				if (share != null) {
					shareMessage = share.shareMessagesMap.get(ogsid);
				}
				if (shareMessage != null) {
					shareMessage.status = "failed";
				}
				if (data.relationship.squares.contains(gid)) {
					if (data.localStatus.localData.currentSelectedSquare.equals(gid)) {
						viewManage.mainView.squareSubView.showSquareMessages(true);
					}
				} else {
					if (data.localStatus.localData.currentSelectedGroup.equals(gid)) {
						viewManage.mainView.shareSubView.showShareMessages();
					}
				}
				log.e(ViewManage.getErrorLineNumber() + response.失败原因);
			} else {
				log.e(ViewManage.getErrorLineNumber() + response.失败原因);
			}
		};

		@Override
		public void onFailure(HttpException error, String msg) {
			parser.check();
			Share share = data.shares.shareMap.get(gid);
			ShareMessage shareMessage = null;
			if (share != null) {
				shareMessage = share.shareMessagesMap.get(ogsid);
			}
			if (shareMessage != null) {
				shareMessage.status = "failed";
			}
			if (data.relationship.squares.contains(gid)) {
				if (data.localStatus.localData.currentSelectedSquare.equals(gid)) {
					viewManage.mainView.squareSubView.showSquareMessages(true);
				}
			} else {
				if (data.localStatus.localData.currentSelectedGroup.equals(gid)) {
					viewManage.mainView.shareSubView.showShareMessages();
				}
			}
			log.e(ViewManage.getErrorLineNumber() + msg);
		};
	};
	public ResponseHandler<String> share_get = httpClient.new ResponseHandler<String>() {
		class Response {
			public String 提示信息;
			public String 失败原因;
			public List<ShareMessage> shareMessages;
		}

		public void onSuccess(ResponseInfo<String> responseInfo) {
			Response response = gson.fromJson(responseInfo.result, Response.class);
			if (response.提示信息.equals("获取群分享成功")) {
				try {
					ShareMessage shareMessages = response.shareMessages.get(0);
					parser.check();
					data.tempData.tempShareMessageMap.put(shareMessages.gsid, shareMessages);
					if (viewManage.shareMessageDetailView != null) {
						viewManage.shareMessageDetailView.thisController.showTempShare();
					}
				} catch (Exception e) {

				}
			} else {
				System.out.println(response.失败原因 + "====================");
			}
		};

	};
	public ResponseHandler<String> share_getSharesCallBack = httpClient.new ResponseHandler<String>() {

		@Override
		public void onSuccess(ResponseInfo<String> responseInfo) {
			try {
				GetShareResponse response = gson.fromJson(responseInfo.result, GetShareResponse.class);
				if (response.提示信息.equals("获取群分享成功")) {
					dataProcessing(response, "Main");
				} else {
					log.e(ViewManage.getErrorLineNumber() + response.失败原因 + "--获取群分享失败");
				}
			} catch (JsonSyntaxException e) {
				e.printStackTrace();
				log.e(ViewManage.getErrorLineNumber() + "失败" + e.toString());
			}
		};

		public void onFailure(HttpException error, String msg) {
			if (viewManage.mainView.activityStatus.state == viewManage.mainView.activityStatus.SHARE) {
				viewManage.shareSubView.thisController.reflashStatus.state = viewManage.shareSubView.thisController.reflashStatus.Failed;
				viewManage.shareSubView.showRoomTime();
			} else if (viewManage.mainView.activityStatus.state == viewManage.mainView.activityStatus.SQUARE) {
				viewManage.squareSubView.thisController.reflashStatus.state = viewManage.squareSubView.thisController.reflashStatus.Failed;
				viewManage.squareSubView.showRoomTime();
			}
		};
	};

	public ResponseHandler<String> share_getSharesCallBack2 = httpClient.new ResponseHandler<String>() {

		@Override
		public void onSuccess(ResponseInfo<String> responseInfo) {
			try {
				GetShareResponse response = gson.fromJson(responseInfo.result, GetShareResponse.class);
				if (response.提示信息.equals("获取群分享成功")) {
					dataProcessing(response, "SectionPage");
				} else {
					log.e(ViewManage.getErrorLineNumber() + response.失败原因);
				}
			} catch (JsonSyntaxException e) {
				e.printStackTrace();
				log.e(ViewManage.getErrorLineNumber() + "失败" + e.toString());
			}
		};

		@Override
		public void onFailure(HttpException error, String msg) {
			if (viewManage.shareSectionView != null) {
				viewManage.shareSectionView.thisController.reflashStatus.state = viewManage.shareSectionView.thisController.reflashStatus.Failed;
				viewManage.shareSectionView.showRoomTime();
			}
		};
	};

	class GetShareResponse {
		public String 提示信息;
		public String 失败原因;
		public String gid;
		public String sid;
		public int nowpage;
		public Share shares;
	}

	public void dataProcessing(GetShareResponse response, String type) {
		Share responsesShare = response.shares;
		parser.check();
		String gid = response.gid;
		String sid = response.sid;
		Share share = data.shares.shareMap.get(sid);
		if (share == null) {
			share = data.shares.new Share();
			data.shares.shareMap.put(sid, share);
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
			share.updateTime = new Date().getTime();
			if ("Main".equals(type)) {
				if (responsesShare.shareMessagesOrder.size() == 0) {
					viewManage.shareSubView.thisController.nowpage--;
				}
				viewManage.shareSubView.thisController.reflashStatus.state = viewManage.shareSubView.thisController.reflashStatus.Normal;
				viewManage.postNotifyView("ShareSubViewMessage");
			} else if ("SectionPage".equals(type)) {
				if (responsesShare.shareMessagesOrder.size() == 0) {
					viewManage.shareSectionView.thisController.nowpage--;
				}
				viewManage.shareSectionView.thisController.reflashStatus.state = viewManage.shareSectionView.thisController.reflashStatus.Normal;
				viewManage.postNotifyView("ShareSectionNotifyShares");
			}
		} else {
			share.updateTime = new Date().getTime();
			if ("Main".equals(type)) {
				if (responsesShare.shareMessagesOrder.size() == 0) {
					viewManage.squareSubView.thisController.nowpage--;
				}
				viewManage.squareSubView.thisController.reflashStatus.state = viewManage.squareSubView.thisController.reflashStatus.Normal;
				if (response.nowpage == 0) {
					viewManage.squareSubView.showSquareMessages(true);
				} else {
					viewManage.squareSubView.showSquareMessages(false);
				}
			} else if ("SectionPage".equals(type)) {
				if (responsesShare.shareMessagesOrder.size() == 0) {
					viewManage.shareSectionView.thisController.nowpage--;
				}
				viewManage.shareSectionView.thisController.reflashStatus.state = viewManage.shareSectionView.thisController.reflashStatus.Normal;
				viewManage.postNotifyView("ShareSectionNotifyShares");
			}
		}
	}

	public ResponseHandler<String> share_modifyPraiseusersCallBack = httpClient.new ResponseHandler<String>() {
		class Response {
			public String 提示信息;
			public String 失败原因;
			// public String gid;
			// public String gsid;
		}

		@Override
		public void onSuccess(ResponseInfo<String> responseInfo) {
			parser.check();
			Response response = gson.fromJson(responseInfo.result, Response.class);
			// String phone = data.userInformation.currentUser.phone;
			if (response.提示信息.equals("点赞群分享成功")) {
				// Share share = data.shares.shareMap.get(response.gid);
				// ShareMessage shareMessage =
				// share.shareMessagesMap.get(response.gsid);
				// if (shareMessage != null) {
				// if (shareMessage.praiseusers.contains(phone)) {
				// shareMessage.praiseusers.remove(phone);
				// } else {
				// shareMessage.praiseusers.add(phone);
				// }
				// }
				log.e(tag, ViewManage.getErrorLineNumber() + "---------------------点赞群分享成功");
			} else if (response.提示信息.equals("点赞群分享失败")) {
				// Share share = data.shares.shareMap.get(response.gid);
				// ShareMessage shareMessage =
				// share.shareMessagesMap.get(response.gsid);
				// if (shareMessage != null) {
				// if (shareMessage.praiseusers.contains(phone)) {
				// shareMessage.praiseusers.remove(phone);
				// } else {
				// shareMessage.praiseusers.add(phone);
				// }
				// }
				log.e(tag, ViewManage.getErrorLineNumber() + "点赞群分享失败---------------------" + response.失败原因);
			}
			// data.shares.isModified = true;
		};
	};
	public ResponseHandler<String> share_addCommentCallBack = httpClient.new ResponseHandler<String>() {
		class Response {
			public String 提示信息;
			public String 失败原因;
			public String gid;
			// public String gsid;
		}

		@Override
		public void onSuccess(ResponseInfo<String> responseInfo) {
			Response response = gson.fromJson(responseInfo.result, Response.class);
			if (response.提示信息.equals("评论群分享成功")) {
				log.e(tag, ViewManage.getErrorLineNumber() + response.gid + "---------------------评论群分享成功");
			} else {
				log.e(tag, ViewManage.getErrorLineNumber() + "---------------------" + response.失败原因);
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
				log.e(ViewManage.getErrorLineNumber() + "---------------------删除群分享成功");
			} else {
				log.e(tag, ViewManage.getErrorLineNumber() + "---------------------" + response.失败原因);
			}
		};
	};

	// TODO LBS

	public RequestCallBack<String> lbsdata_create = httpClient.new ResponseHandler<String>() {
		class Response {
			public int status;
			public String info;
		}

		public void onSuccess(ResponseInfo<String> responseInfo) {
			Response response = gson.fromJson(responseInfo.result, Response.class);
			if (response.status == 1) {
				log.e(tag, ViewManage.getErrorLineNumber() + "create lbs success");

			} else {
				log.e(tag, ViewManage.getErrorLineNumber() + "create*" + response.info);
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
				log.e(tag, ViewManage.getErrorLineNumber() + "updata lbs success");

			} else {
				log.e(tag, ViewManage.getErrorLineNumber() + "updata*" + response.info);
			}
		};
	};
	public RequestCallBack<String> lbsdata_search = httpClient.new ResponseHandler<String>() {
		class Response {
			public int status;
			// public String info;
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

	// TODO Circle

	public RequestCallBack<String> circle_modify = httpClient.new ResponseHandler<String>() {
		class Response {
			public String 提示信息;
			public String 失败原因;
		}

		public void onSuccess(ResponseInfo<String> responseInfo) {
			Response response = gson.fromJson(responseInfo.result, Response.class);
			if (response.提示信息.equals("修改成功")) {
				viewManage.postNotifyView("UserIntimateView");
				viewManage.postNotifyView("CirclesManageView");
				log.e(ViewManage.getErrorLineNumber() + "修改分组名称成功");
			} else {
				log.d(ViewManage.getErrorLineNumber() + "修改失败===================" + response.失败原因);

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
				List<Circle2> list = new ArrayList<Circle2>();
				for (int i = 0; i < data.relationship.circles.size(); i++) {
					String key = data.relationship.circles.get(i);
					Circle circleData = data.relationship.circlesMap.get(key);
					if (circleData != null) {
						list.add(viewManage.friendsSubView.thisController.new Circle2(circleData.rid + "", circleData.name));
					}
				}
				viewManage.mainView.friendsSubView.thisController.modifyGroupSequence(gson.toJson(list));
			} else {
				log.d(ViewManage.getErrorLineNumber() + "删除失败===================" + response.失败原因);

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

				viewManage.postNotifyView("UserIntimateView");
				viewManage.postNotifyView("CirclesManageView");

				// viewManage.mainView.friendsSubView.thisController.modifyGroupSequence(gson.toJson(data.relationship.circles));

			} else {
				log.d(ViewManage.getErrorLineNumber() + "添加失败===================" + response.失败原因);

			}
		};
	};
}
