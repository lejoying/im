package com.lejoying.wxgs.activity.utils;

import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;

import com.lejoying.wxgs.app.MainApplication;
import com.lejoying.wxgs.app.data.API;
import com.lejoying.wxgs.app.data.Configuration;
import com.lejoying.wxgs.app.data.Data;
import com.lejoying.wxgs.app.data.entity.Circle;
import com.lejoying.wxgs.app.data.entity.Friend;
import com.lejoying.wxgs.app.data.entity.Group;
import com.lejoying.wxgs.app.data.entity.Message;
import com.lejoying.wxgs.app.data.entity.User;
import com.lejoying.wxgs.app.handler.DataHandler.Modification;
import com.lejoying.wxgs.app.handler.NetworkHandler.NetConnection;
import com.lejoying.wxgs.app.handler.NetworkHandler.Settings;
import com.lejoying.wxgs.app.parser.JSONParser;
import com.lejoying.wxgs.app.parser.JSONParser.CirclesAndFriends;
import com.lejoying.wxgs.app.parser.JSONParser.GroupsAndFriends;
import com.lejoying.wxgs.app.parser.StreamParser;

public class DataUtil {

	static MainApplication app = MainApplication.getMainApplication();

	public static abstract class GetDataListener {
		public abstract void getSuccess();

		public void getFailed() {
		};
	}

	public static void getUser(final GetDataListener listener) {
		NetConnection netConnection = new CommonNetConnection() {
			@Override
			public void success(final JSONObject jData) {
				app.dataHandler.exclude(new Modification() {
					@Override
					public void modifyData(Data data) {
						try {
							User user = JSONParser.generateUserFromJSON(jData
									.getJSONArray("accounts").getJSONObject(0));
							data.user.userBackground = user.userBackground;
							data.user.head = user.head;
							data.user.nickName = user.nickName;
							data.user.mainBusiness = user.mainBusiness;
							data.user.id = user.id;
							data.user.sex = user.sex;
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}

					@Override
					public void modifyUI() {
						if (listener != null)
							listener.getSuccess();
						super.modifyUI();
					}
				});

			}

			@Override
			protected void unSuccess(JSONObject jData) {
				if (listener != null)
					listener.getFailed();
				super.unSuccess(jData);
			}

			@Override
			protected void settings(Settings settings) {
				settings.url = API.DOMAIN + API.ACCOUNT_GET;
				Map<String, String> params = new HashMap<String, String>();
				params.put("phone", app.data.user.phone);
				params.put("accessKey", app.data.user.accessKey);
				params.put("target", "[\"" + app.data.user.phone + "\"]");
				settings.params = params;
			}
		};
		app.networkHandler.connection(netConnection);
	}

	public static void getCircles(final GetDataListener listener) {
		NetConnection netConnection = new CommonNetConnection() {
			@Override
			public void success(final JSONObject jData) {
				app.dataHandler.exclude(new Modification() {
					@Override
					public void modifyData(Data data) {
						try {
							CirclesAndFriends circlesAndFriends = JSONParser
									.generateCirclesFromJSON(jData
											.getJSONArray("circles"));
							data.circles.clear();
							for (Circle circle : circlesAndFriends.circles) {
								data.circles.add(String.valueOf(circle.rid));
								data.circlesMap.put(String.valueOf(circle.rid),
										circle);
							}
							Map<String, Friend> friends = circlesAndFriends.circleFriends;
							Set<String> phones = circlesAndFriends.circleFriends
									.keySet();
							for (String phone : phones) {
								Friend friend = friends.get(phone);
								updateFriend(friend, data);
							}
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}

					@Override
					public void modifyUI() {
						if (listener != null)
							listener.getSuccess();
						super.modifyUI();
					}
				});
			}

			@Override
			protected void unSuccess(JSONObject jData) {
				if (listener != null)
					listener.getFailed();
				super.unSuccess(jData);
			}

			@Override
			protected void settings(Settings settings) {
				settings.url = API.DOMAIN + API.RELATION_GETCIRCLESANDFRIENDS;
				Map<String, String> params = new HashMap<String, String>();
				params.put("phone", app.data.user.phone);
				params.put("accessKey", app.data.user.accessKey);
				settings.params = params;
			}
		};
		app.networkHandler.connection(netConnection);
	}

	public static void updateFriend(Friend friend, Data data) {
		Friend updateFriend = data.friends.get(friend.phone);
		if (updateFriend != null) {
			if (friend.head != null && !friend.head.equals("")) {
				updateFriend.head = friend.head;
			}
			if (friend.nickName != null && !friend.nickName.equals("")) {
				updateFriend.nickName = friend.nickName;
			}
			if (friend.mainBusiness != null && !friend.mainBusiness.equals("")) {
				updateFriend.mainBusiness = friend.mainBusiness;
			}
			if (friend.friendStatus != null && !friend.friendStatus.equals("")) {
				updateFriend.friendStatus = friend.friendStatus;
			}
		} else {
			data.friends.put(friend.phone, friend);
		}
	}

	public static void getGroups(final GetDataListener listener) {
		NetConnection netConnection = new CommonNetConnection() {
			@Override
			public void success(final JSONObject jData) {
				app.dataHandler.exclude(new Modification() {
					@Override
					public void modifyData(Data data) {
						try {
							GroupsAndFriends groupsAndFriends = JSONParser
									.generateGroupsFromJSON(jData
											.getJSONArray("groups"));
							data.groups.clear();
							for (Group group : groupsAndFriends.groups) {
								data.groups.add(String.valueOf(group.gid));
								updateGroup(group, data);
							}
							data.groupFriends = groupsAndFriends.groupFriends;
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}

					@Override
					public void modifyUI() {
						if (listener != null)
							listener.getSuccess();
						super.modifyUI();
					}
				});
			}

			@Override
			protected void unSuccess(JSONObject jData) {
				if (listener != null)
					listener.getFailed();
				super.unSuccess(jData);
			}

			@Override
			protected void settings(Settings settings) {
				settings.url = API.DOMAIN + API.GROUP_GETGROUPSANDMEMBERS;
				Map<String, String> params = new HashMap<String, String>();
				params.put("phone", app.data.user.phone);
				params.put("accessKey", app.data.user.accessKey);
				settings.params = params;
			}
		};
		app.networkHandler.connection(netConnection);
	}

	public static void updateGroup(Group group, Data data) {
		Group updateGroup = data.groupsMap.get(String.valueOf(group.gid));
		if (updateGroup != null) {
			if (updateGroup.name != null && !updateGroup.equals("")) {
				updateGroup.name = group.name;
			}
			if (updateGroup.icon != null && !updateGroup.equals("")) {
				updateGroup.icon = group.icon;
			}
			if (updateGroup.description != null && !updateGroup.equals("")) {
				updateGroup.description = group.description;
			}
			if (updateGroup.members != null) {
				updateGroup.members = group.members;
			}
		} else {
			data.groupsMap.put(String.valueOf(group.gid), group);
		}
	}

	public static void getAskFriends(final GetDataListener listener) {
		NetConnection netConnection = new CommonNetConnection() {
			@Override
			public void success(final JSONObject jData) {
				app.dataHandler.exclude(new Modification() {
					@Override
					public void modifyData(Data data) {
						try {
							List<Friend> friends = JSONParser
									.generateFriendsFromJSON(jData
											.getJSONArray("accounts"));
							for (Friend friend : friends) {
								if (!data.newFriends.contains(friend)) {
									data.newFriends.add(0, friend);
								}
							}
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}

					@Override
					public void modifyUI() {
						if (listener != null)
							listener.getSuccess();
						super.modifyUI();
					}
				});
			}

			@Override
			protected void unSuccess(JSONObject jData) {
				if (listener != null)
					listener.getFailed();
				super.unSuccess(jData);
			}

			@Override
			protected void settings(Settings settings) {
				settings.url = API.DOMAIN + API.RELATION_GETASKFRIENDS;
				Map<String, String> params = new HashMap<String, String>();
				params.put("phone", app.data.user.phone);
				params.put("accessKey", app.data.user.accessKey);
				settings.params = params;
			}
		};
		app.networkHandler.connection(netConnection);

	}

	public static void getMessages(final GetDataListener listener) {
		NetConnection netConnection = new CommonNetConnection() {
			@Override
			public void success(final JSONObject jData) {
				app.dataHandler.exclude(new Modification() {
					@Override
					public void modifyData(Data data) {
						try {
							data.user.flag = jData.getString("flag");
						} catch (JSONException e1) {
						}
						try {
							List<Message> messages = JSONParser
									.generateMessagesFromJSON(jData
											.getJSONArray("messages"));
							for (final Message message : messages) {
								if (message.sendType.equals("point")) {
									Friend friend = data.friends
											.get(message.phone);
									if (friend != null
											&& !friend.messages
													.contains(message)) {
										data.lastChatFriends.remove("f"
												+ friend.phone);
										data.lastChatFriends.add(0, "f"
												+ friend.phone);
										friend.messages.add(message);
										friend.notReadMessagesCount++;
									}
								} else if (message.sendType.equals("group")) {
									Group group = data.groupsMap.get(String
											.valueOf(message.gid));
									if (group != null
											&& !group.messages
													.contains(message)) {
										data.lastChatFriends.remove("g"
												+ group.gid);
										data.lastChatFriends.add(0, "g"
												+ group.gid);
										group.messages.add(message);
										group.notReadMessagesCount++;
									} else {
										getGroups(new GetDataListener() {
											@Override
											public void getSuccess() {
												app.dataHandler
														.exclude(new Modification() {

															@Override
															public void modifyData(
																	Data data) {
																Group group = data.groupsMap
																		.get(String
																				.valueOf(message.gid));
																if (group != null
																		&& !group.messages
																				.contains(message)) {
																	group.messages
																			.add(message);
																	group.notReadMessagesCount++;
																}
															}

															@Override
															public void modifyUI() {
																if (listener != null)
																	listener.getSuccess();
																super.modifyUI();
															}
														});
											}

										});
									}
								}
							}
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}

					@Override
					public void modifyUI() {
						if (listener != null)
							listener.getSuccess();
						super.modifyUI();
					}
				});
			}

			@Override
			protected void unSuccess(JSONObject jData) {
				if (listener != null)
					listener.getFailed();
				super.unSuccess(jData);
			}

			@Override
			protected void settings(Settings settings) {
				settings.url = API.DOMAIN + API.MESSAGE_GET;
				Map<String, String> params = new HashMap<String, String>();
				params.put("phone", app.data.user.phone);
				params.put("accessKey", app.data.user.accessKey);
				params.put("flag", app.data.user.flag);
				settings.params = params;
			}
		};
		app.networkHandler.connection(netConnection);
	}

	public static void saveData(Context context) {
		if (app.data.isChanged) {
			app.data.isChanged = false;
			try {
				Data saveData = new Data();
				saveData.user = app.data.user;
				saveData.circles = app.data.circles;
				saveData.circlesMap = app.data.circlesMap;
				saveData.friends = app.data.friends;
				saveData.groups = app.data.groups;
				saveData.squareFlags = app.data.squareFlags;
				saveData.squareMessages = app.data.squareMessages;
				saveData.groupsMap = app.data.groupsMap;
				saveData.groupFriends = app.data.groupFriends;
				saveData.lastChatFriends = app.data.lastChatFriends;
				saveData.newFriends = app.data.newFriends;
				StreamParser.parseToObjectFile(context.openFileOutput(
						app.data.user.phone, Context.MODE_PRIVATE), saveData);

				Configuration config = new Configuration();
				config.lastLoginPhone = app.data.user.phone;
				StreamParser.parseToObjectFile(
						context.openFileOutput("config", Context.MODE_PRIVATE),
						config);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}
