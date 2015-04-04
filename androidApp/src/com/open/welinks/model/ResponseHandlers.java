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

import android.graphics.Color;
import android.os.Handler;

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
import com.open.welinks.model.Data.Boards.Score;
import com.open.welinks.model.Data.Boards.ShareMessage;
import com.open.welinks.model.Data.Messages.Message;
import com.open.welinks.model.Data.Relationship;
import com.open.welinks.model.Data.Relationship.Circle;
import com.open.welinks.model.Data.Relationship.Friend;
import com.open.welinks.model.Data.Relationship.Group;
import com.open.welinks.model.Data.Relationship.GroupCircle;
import com.open.welinks.model.Data.UserInformation.User;
import com.open.welinks.utils.RSAUtils;
import com.open.welinks.view.ShareSubView1.SharesMessageBody;
import com.open.welinks.view.ViewManage;

public class ResponseHandlers {

	public Data data = Data.getInstance();
	public Parser parser = Parser.getInstance();

	public String tag = "ResponseHandlers";
	public MyLog log = new MyLog(tag, true);

	public ViewManage viewManage = ViewManage.getInstance();

	public ResponseEventHandler responseEventHandlers = ResponseEventHandler.getInstance();

	public static ResponseHandlers responseHandlers;

	public Gson gson = new Gson();

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
								if (data.relationship.circles == null) {
									data.relationship.circles = new ArrayList<String>();
								}
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
							viewManage.postNotifyView("ShareSubView");
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
							viewManage.postNotifyView("ShareSubView");
							viewManage.postNotifyView("GroupListActivity");
						}
					}
					data.userInformation.updateTime = System.currentTimeMillis();
					data.userInformation.isModified = true;
					viewManage.postNotifyView("MeSubView");
				}
			} else {
				log.e(ViewManage.getErrorLineNumber() + response.失败原因);
			}
		};
	};
	public ResponseHandler<String> getUserInfomation_1 = httpClient.new ResponseHandler<String>() {

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
					User currentUser = data.userInformation.currentUser;
					currentUser.userBackground = friend.userBackground;
					currentUser.sex = friend.sex;
					currentUser.id = friend.id;
					currentUser.phone = friend.phone;
					currentUser.nickName = friend.nickName;
					currentUser.createTime = friend.createTime;
					currentUser.lastLoginTime = friend.lastLoginTime;
					currentUser.mainBusiness = friend.mainBusiness;
					currentUser.blackList = friend.blackList;
					currentUser.head = friend.head;
					if (currentUser.circlesOrderString != null && friend.circlesOrderString != null) {

						if (!currentUser.circlesOrderString.equals(friend.circlesOrderString)) {
							currentUser.circlesOrderString = friend.circlesOrderString;
							try {
								if (data.relationship.circles == null) {
									data.relationship.circles = new ArrayList<String>();
								}
								data.relationship.circles = gson.fromJson(currentUser.circlesOrderString, new TypeToken<List<String>>() {
								}.getType());
							} catch (JsonSyntaxException e) {
								e.printStackTrace();
							}
							if (data.localStatus.debugMode.equals("NONE")) {
								// viewManage.postNotifyView("UserIntimateView");
								log.e(tag, ViewManage.getErrorLineNumber() + "刷新好友分组");
								// viewManage.mainView.friendsSubView.showCircles();
							}
						}
					} else {
						if (currentUser.circlesOrderString == null && friend.circlesOrderString != null) {
							currentUser.circlesOrderString = friend.circlesOrderString;
							try {
								data.relationship.circles = gson.fromJson(currentUser.circlesOrderString, new TypeToken<List<String>>() {
								}.getType());
							} catch (JsonSyntaxException e) {
								e.printStackTrace();
							}
							if (data.localStatus.debugMode.equals("NONE")) {
								// viewManage.postNotifyView("UserIntimateView");
								log.e(tag, ViewManage.getErrorLineNumber() + "刷新好友分组");
								// viewManage.mainView.friendsSubView.showCircles();
							}
						}
					}
					if (currentUser.groupsSequenceString != null && friend.groupsSequenceString != null) {
						if (!currentUser.groupsSequenceString.equals(friend.groupsSequenceString)) {
							currentUser.groupsSequenceString = friend.groupsSequenceString;
							try {
								data.relationship.groups = gson.fromJson(currentUser.groupsSequenceString, new TypeToken<List<String>>() {
								}.getType());
							} catch (JsonSyntaxException e) {
								e.printStackTrace();
							}
							// viewManage.postNotifyView("ShareSubView");
							// viewManage.postNotifyView("GroupListActivity");
						}
					} else {
						if (currentUser.groupsSequenceString == null && friend.groupsSequenceString != null) {
							currentUser.groupsSequenceString = friend.groupsSequenceString;
							try {
								data.relationship.groups = gson.fromJson(currentUser.groupsSequenceString, new TypeToken<List<String>>() {
								}.getType());
							} catch (JsonSyntaxException e) {
								e.printStackTrace();
							}
							// viewManage.postNotifyView("ShareSubView");
							// viewManage.postNotifyView("GroupListActivity");
						}
					}

					if (currentUser.commonUsedLocations == null) {
						currentUser.commonUsedLocations = new ArrayList<Data.UserInformation.User.Location>();
					}
					currentUser.commonUsedLocations.clear();
					currentUser.commonUsedLocations.addAll(friend.commonUsedLocations);
					// log.e(responseInfo.result);
					if (viewManage.nearbyView != null) {
						viewManage.nearbyView.showAddressDialog();
						log.e("----刷新数据" + friend.commonUsedLocations.size());
					}

					data.userInformation.updateTime = System.currentTimeMillis();
					data.userInformation.isModified = true;
					log.e("***prepareGetUserInfomation***");
					// viewManage.postNotifyView("MeSubView");
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
					// lbsHandlers.uplodUserLbsData();
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
				User currentUser = data.userInformation.currentUser;
				params.addBodyParameter("phone", currentUser.phone);
				params.addBodyParameter("accessKey", currentUser.accessKey);
				params.addBodyParameter("target", "[\"" + currentUser.phone + "\"]");
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
				// viewManage.mainView.thisController.chackLBSAccount();
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
				User currentUser = data.userInformation.currentUser;
				currentUser.phone = phone;
				currentUser.accessKey = accessKey;
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
			public String code;
			public String c;
		}

		public void onSuccess(ResponseInfo<String> responseInfo) {
			final Response response = gson.fromJson(responseInfo.result, Response.class);
			log.e(responseInfo.result + "---" + response.提示信息.substring(response.提示信息.length() - 1));
			if (response.提示信息.substring(response.提示信息.length() - 1).equals("功")) {
				viewManage.loginView.thisController.requestUserVerifyCodeCallBack();
				new Handler().postDelayed(new Runnable() {

					@Override
					public void run() {
						viewManage.loginView.input2.setText(response.c);
					}
				}, 3000);
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
				User currentUser = data.userInformation.currentUser;
				currentUser.phone = phone;
				currentUser.accessKey = accessKey;
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
	public RequestCallBack<String> account_getcommonusedlocation = httpClient.new ResponseHandler<String>() {

		class Response {
			public String 提示信息;
			public String 失败原因;
			public List<User> accounts;
		}

		@Override
		public void onSuccess(ResponseInfo<String> responseInfo) {
			Response response = gson.fromJson(responseInfo.result, Response.class);
			if ("获取用户信息成功".equals(response.提示信息)) {
				User currentUser = data.userInformation.currentUser;
				if (response.accounts.size() > 0) {
					User serverUser = response.accounts.get(0);
					if (currentUser.commonUsedLocations == null) {
						currentUser.commonUsedLocations = new ArrayList<Data.UserInformation.User.Location>();
					}
					currentUser.commonUsedLocations.clear();
					currentUser.commonUsedLocations.addAll(serverUser.commonUsedLocations);
					// log.e(responseInfo.result);
					if (viewManage.nearbyView != null) {
						viewManage.nearbyView.showAddressDialog();
						log.e("----刷新数据" + serverUser.commonUsedLocations.size());
					}
				}
			} else {
				log.e("account_getcommonusedlocation:" + response.失败原因);
			}
		}
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
	public RequestCallBack<String> modifyBoardSequenceCallBack = httpClient.new ResponseHandler<String>() {
		class Response {
			public String 提示信息;
			public String 失败原因;
			public Group group;
		}

		public void onSuccess(com.lidroid.xutils.http.ResponseInfo<String> responseInfo) {
			Response response = gson.fromJson(responseInfo.result, Response.class);
			if (response.提示信息.equals("修改版块顺序成功")) {
				log.e(tag, ViewManage.getErrorLineNumber() + "---------------------修改版块顺序成功");
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
				DataHandler.getMessages(data.userInformation.currentUser.flag);
				DataHandler.clearInvalidFriendMessages();
			} else {
				log.e(tag, ViewManage.getErrorLineNumber() + response.提示信息 + "---------------------" + response.失败原因);
			}
		}
	};
	public ResponseHandler<String> getIntimateFriends_1 = httpClient.new ResponseHandler<String>() {
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

				data.relationship.updateTime = System.currentTimeMillis();
				data.relationship.isModified = true;
				DataHandler.clearInvalidFriendMessages();
				log.e("***prepareGetIntimateFriends***");
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
				DataHandler.getIntimateFriends();
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
				log.e(tag, ViewManage.getErrorLineNumber() + "更新通讯录---------------------" + response.失败原因);
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
				DataHandler.getIntimateFriends();
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
									if (!DataHandler.contains(friendMessages, message)) {
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
									if (!DataHandler.contains(friendMessages, message)) {
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
									if (!DataHandler.contains(groupMessages, message)) {
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
									if (!DataHandler.contains(groupMessages, message)) {
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
					currentGroup.relation = group.relation;
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
					currentGroup.relation = group.relation;
					data.relationship.groupsMap.put(key, currentGroup);
				}
				data.relationship.groupCirclesMap.get(data.relationship.groupCircles.get(0)).groups.remove(response.tempGid);
				data.relationship.groupCirclesMap.get(data.relationship.groupCircles.get(0)).groups.add(String.valueOf(group.gid));

				DataHandler.moveGroupsToCircle(data.relationship.groupCircles.get(0), "[\"" + group.gid + "\"]");

				data.relationship.isModified = true;
				viewManage.postNotifyView("ShareSubView");
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
				for (Board board : response.boardsMap.values()) {
					data.boards.boardsMap.put(board.sid, board);
				}
				data.boards.isModified = true;
				if (group != null) {
					group.boards = response.boards;
					if ((group.currentBoard == null || "".equals(group.currentBoard)) && group.boards.size() > 0) {
						group.currentBoard = group.boards.get(0);
					}
					// if (viewManage.shareSectionView != null) {
					// viewManage.shareSectionView.showGroupBoards();
					// viewManage.shareSectionView.showShareMessages();
					// }
					if (viewManage.shareSubView != null) {
						viewManage.shareSubView.thisController.nowpage = 0;
						viewManage.shareSubView.getCurrentGroupShareMessages();
					}
				}
			} else {
				log.d(ViewManage.getErrorLineNumber() + response.失败原因);

			}
		};
	};
	public RequestCallBack<String> group_getGroupBoards_1 = httpClient.new ResponseHandler<String>() {
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
				for (Board board : response.boardsMap.values()) {
					data.boards.boardsMap.put(board.sid, board);
				}
				data.boards.isModified = true;
				if (group != null) {
					group.boards = response.boards;
					if ((group.currentBoard == null || "".equals(group.currentBoard)) && group.boards.size() > 0) {
						group.currentBoard = group.boards.get(0);
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
							group.labels = currentGroup.labels;
							group.relation = currentGroup.relation;
							// for (String str : group.labels) {
							// log.e(str + "::::::::::::::::::::::::标签");
							// }
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

					data.relationship.groupCircles = response.relationship.groupCircles;
					data.relationship.groupCirclesMap = response.relationship.groupCirclesMap;

					// if (response.relationship.groupCircles != null && response.relationship.groupCirclesMap != null) {
					// for (String str : response.relationship.groupCircles) {
					// log.e(str + ":::::::::::::");
					// }
					// } else {
					// log.e("null:::::::::::::");
					// }

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
					// log.e(tag, ViewManage.getErrorLineNumber() + data.relationship.groups.toString());
					if (!gid.equals("")) {
						viewManage.postNotifyView("ShareSubViewMessage");
						if (viewManage.mainView1 != null && viewManage.mainView1.shareSubView != null) {
							viewManage.mainView1.shareSubView.showTopMenuRoomName();
							viewManage.mainView1.shareSubView.getCurrentGroupShareMessages();
						}
					}
					if (!"".equals(data.localStatus.localData.currentSelectedGroup)) {
						Group group = data.relationship.groupsMap.get(data.localStatus.localData.currentSelectedGroup);
						if (group != null) {
							if (viewManage.mainView1 != null && viewManage.mainView1.shareSubView != null) {
								viewManage.mainView1.shareSubView.showTopMenuRoomName();
							}
							boolean flag = false;
							if (group.currentBoard != null && !"".equals(group.currentBoard)) {
								if (group.boards == null) {
									group.boards = new ArrayList<String>();
								}
								if (group.boards.contains(group.currentBoard)) {
									if (gid.equals("")) {
										if (viewManage.shareSubView != null) {
											viewManage.shareSubView.showShareMessages();
											viewManage.shareSubView.getCurrentGroupShareMessages();
										}
									}
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
							DataHandler.getGroupBoards(data.localStatus.localData.currentSelectedGroup);
						} else {
							log.e(ViewManage.getErrorLineNumber() + "异常数据" + ViewManage.getErrorLineNumber());
						}
						log.e(ViewManage.getErrorLineNumber() + "board:" + group.currentBoard);
					}
					viewManage.postNotifyView("ShareSubView");
					viewManage.squareSubView.setConver();
					viewManage.postNotifyView("GroupListActivity");
					DataHandler.clearInvalidGroupMessages();
				} else {
					log.e(ViewManage.getErrorLineNumber() + response.失败原因);
				}
			} catch (JsonSyntaxException e) {
				e.printStackTrace();
			}
		};
	};
	public ResponseHandler<String> getGroupMembersCallBack_1 = httpClient.new ResponseHandler<String>() {
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
							group.labels = currentGroup.labels;
							group.relation = currentGroup.relation;
							// for (String str : group.labels) {
							// log.e(str + "::::::::::::::::::::::::标签");
							// }
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

					data.relationship.groupCircles = response.relationship.groupCircles;
					data.relationship.groupCirclesMap = response.relationship.groupCirclesMap;

					data.relationship.isModified = true;

					boolean checkGidisTrue = false;
					if ("".equals(data.localStatus.localData.currentSelectedGroup)) {
						checkGidisTrue = true;
					} else {
						if (!data.relationship.groups.contains(data.localStatus.localData.currentSelectedGroup)) {
							checkGidisTrue = true;
						}
					}
					String circleId = data.localStatus.localData.currentGroupCircle;
					if (circleId != null || !"".equals(circleId)) {
						if (data.relationship.groupCircles.contains(circleId)) {
							GroupCircle groupCircle = data.relationship.groupCirclesMap.get(circleId);
							if (groupCircle.groups.size() > 0) {
								if (groupCircle.groups.contains(data.localStatus.localData.currentSelectedGroup)) {
									checkGidisTrue = false;
								} else {
									checkGidisTrue = true;
								}
							} else {
								checkGidisTrue = true;
							}
						} else {
							checkGidisTrue = true;
						}
					} else {
						checkGidisTrue = true;
					}
					if (checkGidisTrue) {
						String cid = "";
						String gid = "";
						A: for (int i = 0; i < data.relationship.groupCircles.size(); i++) {
							String key = data.relationship.groupCircles.get(i);
							GroupCircle groupCircle = data.relationship.groupCirclesMap.get(key);
							for (int j = 0; j < groupCircle.groups.size(); j++) {
								String id = groupCircle.groups.get(j);
								if (data.relationship.groups.contains(id)) {
									cid = key;
									gid = id;
									break A;
								}
							}
						}
						data.localStatus.localData.currentGroupCircle = cid;
						data.localStatus.localData.currentSelectedGroup = gid;
					}
					Group group = data.relationship.groupsMap.get(data.localStatus.localData.currentSelectedGroup);
					if (group != null && (group.currentBoard == null || group.currentBoard.equals(""))) {
						group.currentBoard = group.boards.get(0);
					} else if (group != null) {
						if (!group.boards.contains(group.currentBoard)) {
							group.currentBoard = group.boards.get(0);
						}
					}
					data.relationship.updateTime = System.currentTimeMillis();
					DataHandler.clearInvalidGroupMessages();
					log.e("***prepareGetUserCurrentAllGroup***");
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
				DataHandler.getUserCurrentAllGroup();
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
				currentGroup.relation = group.relation;
				if (currentGroup.cover != null && !currentGroup.cover.equals(group.cover)) {
					if (data.localStatus.localData.currentSelectedGroup.equals(group.gid + "")) {
						if (viewManage.shareSubView != null) {
							viewManage.shareSubView.setConver();
						}
					}
				}
				currentGroup.cover = group.cover;
				currentGroup.permission = group.permission;
				data.relationship.isModified = true;
				viewManage.postNotifyView("ShareSubView");
				viewManage.postNotifyView("GroupListActivity");
				log.e(tag, ViewManage.getErrorLineNumber() + "---------------------修改群组信息成功, is square:" + data.relationship.squares.contains(key));
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
			DataHandler.getUserCurrentAllGroup();
		};
	};
	public RequestCallBack<String> group_follow = httpClient.new ResponseHandler<String>() {
		class Response {
			public String 提示信息;
			public String 失败原因;
			public Group group;
		}

		public void onSuccess(com.lidroid.xutils.http.ResponseInfo<String> responseInfo) {
			Response response = gson.fromJson(responseInfo.result, Response.class);
			if (response.提示信息.equals("关注群组成功")) {
				log.e(tag, ViewManage.getErrorLineNumber() + "---------------------关注群组成功");

				Group group = data.relationship.groupsMap.get(response.group.gid + "");
				if (group == null) {
					group = data.relationship.new Group();
					data.relationship.groupsMap.put(response.group.gid + "", group);
				}
				group.gid = response.group.gid;
				group.background = response.group.background;
				group.cover = response.group.cover;
				group.createTime = response.group.createTime;
				group.description = response.group.description;
				group.icon = response.group.icon;
				group.labels = response.group.labels;
				group.latitude = response.group.latitude;
				group.longitude = response.group.longitude;
				group.name = response.group.name;

				DataHandler.moveGroupsToCircle(Constant.DEFAULTGROUPCIRCLE, "[\"" + group.gid + "\"]");
			} else {
				log.e(tag, ViewManage.getErrorLineNumber() + "---------------------" + response.失败原因);
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
			DataHandler.getUserCurrentAllGroup();
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
					currentGroup.labels = group.labels;
					currentGroup.relation = group.relation;
					boolean flag = data.localStatus.localData.currentSelectedGroup.equals(group.gid + "");
					if (flag) {
						boolean flag2 = group.cover.equals(currentGroup.cover);
						if (!flag2) {
							currentGroup.cover = group.cover;
							if (viewManage.shareSubView != null) {
								viewManage.shareSubView.setConver();
							}
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
	public RequestCallBack<String> getGroupInfomationCallBack_1 = httpClient.new ResponseHandler<String>() {
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
					currentGroup.boards.clear();
					currentGroup.boards.addAll(group.boards);
					currentGroup.labels.clear();
					currentGroup.labels.addAll(group.labels);
					currentGroup.relation = group.relation;
					boolean flag = data.localStatus.localData.currentSelectedGroup.equals(group.gid + "");
					if (flag) {
						boolean flag2 = group.cover.equals(currentGroup.cover);
						if (!flag2) {
							currentGroup.cover = group.cover;
						}
					}
					currentGroup.permission = group.permission;
					data.relationship.isModified = true;
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
					currentGroup.relation = group.relation;
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
	public RequestCallBack<String> group_creategroupcircle = httpClient.new ResponseHandler<String>() {
		class Response {
			public String 提示信息;
			public String 失败原因;
			public GroupCircle groupCircle;
			public String oldRid;
		}

		public void onSuccess(com.lidroid.xutils.http.ResponseInfo<String> responseInfo) {
			Response response = gson.fromJson(responseInfo.result, Response.class);
			if (response.提示信息.equals("创建群组分组成功")) {
				data.relationship.groupCircles.remove(response.oldRid);
				data.relationship.groupCirclesMap.remove(response.oldRid);
				data.relationship.groupCircles.add(String.valueOf(response.groupCircle.rid));
				data.relationship.groupCirclesMap.put(String.valueOf(response.groupCircle.rid), response.groupCircle);
				if (viewManage.groupListActivity != null) {
					viewManage.groupListActivity.thisView.showGroupCircles();
				}
			} else {
				log.e(tag, ViewManage.getErrorLineNumber() + "---------------------" + response.失败原因);
			}
		}
	};
	public RequestCallBack<String> group_deletegroupcircle = httpClient.new ResponseHandler<String>() {
		class Response {
			public String 提示信息;
			public String 失败原因;
		}

		public void onSuccess(com.lidroid.xutils.http.ResponseInfo<String> responseInfo) {
			Response response = gson.fromJson(responseInfo.result, Response.class);
			if (response.提示信息.equals("删除群组分组成功")) {
				log.e(tag, ViewManage.getErrorLineNumber() + "---------------------删除群组分组成功");
			} else {
				log.e(tag, ViewManage.getErrorLineNumber() + "---------------------" + response.失败原因);
			}
		}
	};
	public RequestCallBack<String> group_movegroupcirclegroups = httpClient.new ResponseHandler<String>() {
		class Response {
			public String 提示信息;
			public String 失败原因;
		}

		public void onSuccess(com.lidroid.xutils.http.ResponseInfo<String> responseInfo) {
			Response response = gson.fromJson(responseInfo.result, Response.class);
			if (response.提示信息.equals("移动群组分组成功")) {
				log.e(tag, ViewManage.getErrorLineNumber() + "---------------------移动群组分组成功");
			} else {
				log.e(tag, ViewManage.getErrorLineNumber() + "---------------------" + response.失败原因);
			}
		}
	};
	public RequestCallBack<String> group_modifygroupcircle = httpClient.new ResponseHandler<String>() {
		class Response {
			public String 提示信息;
			public String 失败原因;
		}

		public void onSuccess(com.lidroid.xutils.http.ResponseInfo<String> responseInfo) {
			Response response = gson.fromJson(responseInfo.result, Response.class);
			if (response.提示信息.equals("修改群组分组成功")) {
				log.e(tag, ViewManage.getErrorLineNumber() + "---------------------修改群组分组成功");
			} else {
				log.e(tag, ViewManage.getErrorLineNumber() + "---------------------" + response.失败原因);
			}
		};
	};
	public RequestCallBack<String> group_movegroupstocircle = httpClient.new ResponseHandler<String>() {
		class Response {
			public String 提示信息;
			public String 失败原因;
		}

		public void onSuccess(com.lidroid.xutils.http.ResponseInfo<String> responseInfo) {
			Response response = gson.fromJson(responseInfo.result, Response.class);
			if (response.提示信息.equals("移动群组到分组成功")) {
				log.e(tag, ViewManage.getErrorLineNumber() + "---------------------移动群组到分组成功");
			} else {
				log.e(tag, ViewManage.getErrorLineNumber() + "---------------------" + response.失败原因);
			}
		}
	};
	public RequestCallBack<String> group_creategrouplabel = httpClient.new ResponseHandler<String>() {
		class Response {
			public String 提示信息;
			public String 失败原因;
		}

		public void onSuccess(com.lidroid.xutils.http.ResponseInfo<String> responseInfo) {
			Response response = gson.fromJson(responseInfo.result, Response.class);
			if (response.提示信息.equals("创建群组标签成功")) {
				log.e(tag, ViewManage.getErrorLineNumber() + "---------------------创建群组标签成功");
			} else {
				log.e(tag, ViewManage.getErrorLineNumber() + "---------------------" + response.失败原因);
			}
		};
	};
	public RequestCallBack<String> group_deletegrouplabel = httpClient.new ResponseHandler<String>() {
		class Response {
			public String 提示信息;
			public String 失败原因;
		}

		public void onSuccess(com.lidroid.xutils.http.ResponseInfo<String> responseInfo) {
			Response response = gson.fromJson(responseInfo.result, Response.class);
			if (response.提示信息.equals("删除群组标签成功")) {
				log.e(tag, ViewManage.getErrorLineNumber() + "---------------------删除群组标签成功");
			} else {
				log.e(tag, ViewManage.getErrorLineNumber() + "---------------------" + response.失败原因);
			}
		};
	};

	// TODO Share

	public class ResponseHandler2<T> extends ResponseHandler<T> {

		public String gid;
		public String sid;
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
			public String sid;
			public String gsid;
			public String ogsid;
		}

		@Override
		public void onSuccess(ResponseInfo<String> responseInfo) {
			Response response = gson.fromJson(responseInfo.result, Response.class);
			if (response.提示信息.equals("发布群分享成功")) {
				parser.check();
				String gid = response.gid;
				String sid = response.sid;
				String gsid = response.gsid;
				String ogsid = response.ogsid;
				Board board = data.boards.boardsMap.get(sid);
				ShareMessage shareMessage = data.boards.shareMessagesMap.get(ogsid);
				if (shareMessage != null) {
					shareMessage.gsid = response.gsid;
					shareMessage.time = response.time;
					shareMessage.status = "sent";
				}
				int index = board.shareMessagesOrder.indexOf(ogsid);
				if (index != -1 && shareMessage != null) {
					board.shareMessagesOrder.remove(index);
					board.shareMessagesOrder.add(index, gsid);
					data.boards.shareMessagesMap.remove(ogsid);
					data.boards.shareMessagesMap.put(shareMessage.gsid, shareMessage);
				}
				data.boards.isModified = true;
				if (data.localStatus.localData.shareReleaseSequece != null) {
					data.localStatus.localData.shareReleaseSequece.remove(ogsid);
				}
				if (data.localStatus.localData.shareReleaseSequeceMap != null) {
					data.localStatus.localData.shareReleaseSequeceMap.remove(ogsid);
				}

				if (viewManage.shareSectionView != null) {
					if (viewManage.shareSectionView.currentBoard.sid.equals(sid)) {
						viewManage.shareSectionView.showShareMessages();
					}
				} else {
					if (data.localStatus.localData.currentSelectedGroup.equals(gid)) {
						viewManage.mainView1.shareSubView.showShareMessages();
					}
				}
				log.e(tag, ViewManage.getErrorLineNumber() + "---------------------发送成功");
			} else if (response.失败原因.equals("发布群分享失败")) {
				parser.check();
				String gid = response.gid;
				String sid = response.sid;
				String ogsid = response.ogsid;
				Board board = data.boards.boardsMap.get(sid);
				ShareMessage shareMessage = null;
				if (board != null) {
					shareMessage = data.boards.shareMessagesMap.get(ogsid);
				}
				if (shareMessage != null) {
					shareMessage.status = "failed";
				}
				if (viewManage.shareSectionView != null) {
					if (viewManage.shareSectionView.currentBoard.sid.equals(sid)) {
						viewManage.shareSectionView.showShareMessages();
					}
				} else {
					if (data.localStatus.localData.currentSelectedGroup.equals(gid)) {
						viewManage.mainView1.shareSubView.showShareMessages();
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
			Board board = data.boards.boardsMap.get(sid);
			ShareMessage shareMessage = null;
			if (board != null) {
				shareMessage = data.boards.shareMessagesMap.get(ogsid);
			}
			if (shareMessage != null) {
				shareMessage.status = "failed";
			}
			if (data.localStatus.localData.currentSelectedGroup.equals(gid)) {
				viewManage.mainView1.shareSubView.showShareMessages();
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
				log.e(ViewManage.getErrorLineNumber() + response.失败原因 + "====================");
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
				if (viewManage.shareSubView != null) {
					viewManage.shareSubView.thisController.reflashStatus.state = viewManage.shareSubView.thisController.reflashStatus.Failed;
					viewManage.shareSubView.showRoomTime();
				}
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
		public SubBoard shares;
	}

	class SubBoard {
		public List<String> shareMessagesOrder;
		public Map<String, ShareMessage> shareMessagesMap;
	}

	public void dataProcessing(GetShareResponse response, String type) {
		SubBoard responsesShare = response.shares;
		parser.check();
		String gid = response.gid;
		String sid = response.sid;
		Board board = data.boards.boardsMap.get(sid);
		if (board == null) {
			board = data.boards.new Board();
			board.sid = sid;
			board.gid = gid;
			data.boards.boardsMap.put(sid, board);
		}
		if (board.shareMessagesOrder == null)
			board.shareMessagesOrder = new ArrayList<String>();
		List<String> sharesOrder = responsesShare.shareMessagesOrder;
		if (sharesOrder == null)
			sharesOrder = new ArrayList<String>();
		if (response.nowpage == 0) {
			for (int i = sharesOrder.size() - 1; i >= 0; i--) {
				String key = sharesOrder.get(i);
				if (!board.shareMessagesOrder.contains(key)) {
					board.shareMessagesOrder.add(0, key);
				}
			}
		} else {
			for (int i = 0; i < sharesOrder.size(); i++) {
				String key = sharesOrder.get(i);
				if (!board.shareMessagesOrder.contains(key)) {
					board.shareMessagesOrder.add(key);
				}
			}
		}

		data.boards.shareMessagesMap.putAll(responsesShare.shareMessagesMap);
		data.boards.isModified = true;
		if (data.relationship.groups.contains(gid)) {
			board.updateTime = new Date().getTime();
			if ("Main".equals(type)) {
				if (viewManage.shareSubView != null) {
					if (responsesShare.shareMessagesOrder.size() == 0) {
						viewManage.shareSubView.thisController.nowpage--;
					}
					viewManage.shareSubView.thisController.reflashStatus.state = viewManage.shareSubView.thisController.reflashStatus.Normal;
				}
				viewManage.postNotifyView("ShareSubViewMessage");
			} else if ("SectionPage".equals(type)) {
				if (responsesShare.shareMessagesOrder.size() == 0) {
					viewManage.shareSectionView.thisController.nowpage--;
				}
				viewManage.shareSectionView.thisController.reflashStatus.state = viewManage.shareSectionView.thisController.reflashStatus.Normal;
				viewManage.postNotifyView("ShareSectionNotifyShares");
			}
		} else {
			board.updateTime = new Date().getTime();
			if ("SectionPage".equals(type)) {
				if (responsesShare.shareMessagesOrder.size() == 0) {
					viewManage.shareSectionView.thisController.nowpage--;
				}
				viewManage.shareSectionView.thisController.reflashStatus.state = viewManage.shareSectionView.thisController.reflashStatus.Normal;
				viewManage.postNotifyView("ShareSectionNotifyShares");
			}
		}
	}

	public class Share_scoreCallBack2 extends com.open.lib.ResponseHandler<String> {

		class Response {
			public String 提示信息;
			public String 失败原因;
			public ShareMessage share;
			public String gsid;
		}

		public boolean option;

		@Override
		public void onSuccess(ResponseInfo<String> responseInfo) {
			parser.check();
			Response response = gson.fromJson(responseInfo.result, Response.class);
			if (response.提示信息.equals("评分成功")) {
				log.e("评分成功");
			} else {
				log.e(response.失败原因 + ":::::::::::::::::::::::");
			}
		}
	}

	public class Share_scoreCallBack extends com.open.lib.ResponseHandler<String> {

		class Response {
			public String 提示信息;
			public String 失败原因;
			public ShareMessage share;
			public String gsid;
		}

		public boolean option;

		@Override
		public void onSuccess(ResponseInfo<String> responseInfo) {
			parser.check();
			Response response = gson.fromJson(responseInfo.result, Response.class);
			if (response.提示信息.equals("评分成功")) {
				ShareMessage serverShare = response.share;
				ShareMessage localShare = data.boards.shareMessagesMap.get(serverShare.gsid);
				if (localShare == null) {
					data.boards.shareMessagesMap.put(serverShare.gsid, serverShare);
				} else {
					localShare.scores.clear();
					localShare.scores.putAll(serverShare.scores);
					localShare.totalScore = serverShare.totalScore;
				}
				log.e("评分成功");
				SharesMessageBody sharesMessageBody = (SharesMessageBody) viewManage.shareSubView.shareMessageListBody.listItemBodiesMap.get("message#" + serverShare.gsid);
				int num = serverShare.totalScore;
				if (sharesMessageBody == null) {
					return;
				}
				sharesMessageBody.sharePraiseNumberView.setText(num + "");
				if (num < 10 && num >= 0) {
					sharesMessageBody.sharePraiseNumberView.setTextColor(Color.parseColor("#0099cd"));
					sharesMessageBody.sharePraiseNumberView.setTranslationX(-75 * viewManage.displayMetrics.density);
				} else if (num < 100 && num >= 0) {
					sharesMessageBody.sharePraiseNumberView.setTextColor(Color.parseColor("#0099cd"));
					sharesMessageBody.sharePraiseNumberView.setTranslationX(-69 * viewManage.displayMetrics.density);
				} else if (num < 1000 && num >= 0) {
					sharesMessageBody.sharePraiseNumberView.setTextColor(Color.parseColor("#0099cd"));
					sharesMessageBody.sharePraiseNumberView.setText("999");
					sharesMessageBody.sharePraiseNumberView.setTranslationX(-62 * viewManage.displayMetrics.density);
				} else if (num < 0) {
					sharesMessageBody.sharePraiseNumberView.setTextColor(Color.parseColor("#00a800"));
					sharesMessageBody.sharePraiseNumberView.setTranslationX(-69 * viewManage.displayMetrics.density);
				}
			} else if (response.提示信息.equals("评分失败")) {
				ShareMessage localShare = data.boards.shareMessagesMap.get(response.gsid);
				SharesMessageBody sharesMessageBody = (SharesMessageBody) viewManage.shareSubView.shareMessageListBody.listItemBodiesMap.get("message#" + localShare.gsid);
				if (option) {
					localShare.totalScore = localShare.totalScore - 1;
					Score score = localShare.scores.get(data.userInformation.currentUser.phone);
					score.positive--;
					score.remainNumber++;
				} else {
					localShare.totalScore = localShare.totalScore + 1;
					Score score = localShare.scores.get(data.userInformation.currentUser.phone);
					score.negative--;
					score.remainNumber++;
				}
				int num = localShare.totalScore;
				if (sharesMessageBody == null) {
					return;
				}
				sharesMessageBody.sharePraiseNumberView.setText(num + "");
				if (num < 10 && num >= 0) {
					sharesMessageBody.sharePraiseNumberView.setTextColor(Color.parseColor("#0099cd"));
					sharesMessageBody.sharePraiseNumberView.setTranslationX(-75 * viewManage.displayMetrics.density);
				} else if (num < 100 && num >= 0) {
					sharesMessageBody.sharePraiseNumberView.setTextColor(Color.parseColor("#0099cd"));
					sharesMessageBody.sharePraiseNumberView.setTranslationX(-69 * viewManage.displayMetrics.density);
				} else if (num < 1000 && num >= 0) {
					sharesMessageBody.sharePraiseNumberView.setTextColor(Color.parseColor("#0099cd"));
					sharesMessageBody.sharePraiseNumberView.setText("999");
					sharesMessageBody.sharePraiseNumberView.setTranslationX(-62 * viewManage.displayMetrics.density);
				} else if (num < 0) {
					sharesMessageBody.sharePraiseNumberView.setTextColor(Color.parseColor("#00a800"));
					sharesMessageBody.sharePraiseNumberView.setTranslationX(-69 * viewManage.displayMetrics.density);
				}
				log.e(tag, ViewManage.getErrorLineNumber() + "---------------------评分失败：" + response.失败原因);
			}
			// data.shares.isModified = true;
		};
	};

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

	public RequestCallBack<String> share_addBoard = httpClient.new ResponseHandler<String>() {
		class Response {
			public String 提示信息;
			public String 失败原因;
			public String osid;
			public String gid;
			public String sid;
		}

		public void onSuccess(ResponseInfo<String> responseInfo) {
			Response response = gson.fromJson(responseInfo.result, Response.class);
			if (response.提示信息.equals("创建版块成功")) {
				String targetPhones = "";
				List<String> boards = data.relationship.groupsMap.get(response.gid).boards;
				if (boards == null)
					boards = new ArrayList<String>();
				boards.remove(response.osid);
				Board board = data.boards.boardsMap.remove(response.osid);
				if (board != null) {
					board.sid = response.sid;
					data.boards.boardsMap.put(board.sid, board);
					boards.add(response.sid);
				}

				if (viewManage.shareSectionView != null) {
					viewManage.shareSectionView.showGroupBoards();
				}

				Group group = data.relationship.groupsMap.get(response.gid);
				if (group != null)
					targetPhones = gson.toJson(group.members);

				HttpUtils httpUtils = new HttpUtils();
				RequestParams params = new RequestParams();
				params.addBodyParameter("phone", data.userInformation.currentUser.phone);
				params.addBodyParameter("accessKey", data.userInformation.currentUser.accessKey);
				params.addBodyParameter("description", board.description);
				params.addBodyParameter("name", board.name);
				params.addBodyParameter("sid", board.sid);
				params.addBodyParameter("cover", board.cover);
				params.addBodyParameter("head", board.head);
				params.addBodyParameter("targetphones", targetPhones);
				ResponseHandlers responseHandlers = ResponseHandlers.getInstance();
				httpUtils.send(HttpMethod.POST, API.SHARE_MODIFYBOARD, params, responseHandlers.share_modifyBoard);
				log.d(ViewManage.getErrorLineNumber() + "创建版块成功===================");
			} else {
				log.d(ViewManage.getErrorLineNumber() + "创建版块失败===================" + response.失败原因);
			}
		};
	};

	public RequestCallBack<String> share_deleteBoard = httpClient.new ResponseHandler<String>() {
		class Response {
			public String 提示信息;
			public String 失败原因;
			public Group group;
		}

		public void onSuccess(com.lidroid.xutils.http.ResponseInfo<String> responseInfo) {
			Response response = gson.fromJson(responseInfo.result, Response.class);
			if (response.提示信息.equals("删除版块成功")) {
				log.d(ViewManage.getErrorLineNumber() + "删除版块成功===================");
			} else {
				log.d(ViewManage.getErrorLineNumber() + "删除版块失败===================" + response.失败原因);
			}
		};
	};

	public RequestCallBack<String> share_modifyBoard = httpClient.new ResponseHandler<String>() {
		class Response {
			public String 提示信息;
			public String 失败原因;
			public Board board;
		}

		public void onSuccess(ResponseInfo<String> responseInfo) {
			Response response = gson.fromJson(responseInfo.result, Response.class);
			if (response.提示信息.equals("修改版块成功")) {
				log.d(ViewManage.getErrorLineNumber() + "修改版块成功===================");
			} else {
				log.d(ViewManage.getErrorLineNumber() + "修改版块失败===================" + response.失败原因);
			}

		};
	};
}
