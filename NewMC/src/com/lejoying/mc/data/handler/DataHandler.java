package com.lejoying.mc.data.handler;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.StreamCorruptedException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.lejoying.mc.data.App;
import com.lejoying.mc.data.Circle;
import com.lejoying.mc.data.Friend;
import com.lejoying.mc.data.StaticConfig;
import com.lejoying.mc.data.StaticData;
import com.lejoying.mc.data.User;
import com.lejoying.mc.fragment.ChatFragment;
import com.lejoying.mc.fragment.FriendsFragment;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

public class DataHandler {

	public final int DOSYNC = 0x31;

	App app;
	public final int DATA_HANDLER_USER = 0x01;
	public final int DATA_HANDLER_CIRCLE = 0x02;
	public final int DATA_HANDLER_MESSAGE = 0x03;
	public final int DATA_HANDLER_NEWFRIEND = 0x04;

	public final int DATA_HANDLER_UPDATEUSER = 0x05;
	public final int DATA_HANDLER_UPDATEFRIEND = 0x06;

	public final int DATA_HANDLER_GETCONFIGANDDATA = 0x11;
	public final int DATA_HANDLER_SAVECONFIGANDDATA = 0x12;
	public final int DATA_HANDLER_GETUSERDATA = 0x13;

	public final int DATA_HANDLER_CLEANDATA = 0x21;

	List<Message> mQueue = new ArrayList<Message>();

	boolean isHandle;

	public Handler handler = new Handler();

	public DataHandler(App app) {
		this.app = app;
	}

	public void handleData(Message message) {
		mQueue.add(message);
		handleData();
	}

	public void sendMessage(int what, Object obj) {
		Message msg = new Message();
		msg.what = what;
		msg.obj = obj;
		handleData(msg);
	}

	public void sendMessage(int what, int arg1, Object obj) {
		Message msg = new Message();
		msg.what = what;
		msg.obj = obj;
		msg.arg1 = arg1;
		handleData(msg);
	}

	public void sendEmptyMessage(int what) {
		Message msg = new Message();
		msg.what = what;
		handleData(msg);
	}

	private void handleData() {
		if (mQueue.size() == 0 || isHandle) {
			return;
		}
		isHandle = true;
		final Message msg = mQueue.get(0);
		mQueue.remove(0);
		if (msg.arg1 == DOSYNC) {
			handle(msg);
			isHandle = false;
			handleData();
		} else {
			new Thread() {
				public void run() {
					handle(msg);
					isHandle = false;
					handleData();
				}
			}.start();
		}
	}

	void handle(Message msg) {
		InputStream inputStream = null;
		ObjectInputStream objectInputStream = null;
		switch (msg.what) {
		case DATA_HANDLER_CLEANDATA:
			app.initData();
			break;
		case DATA_HANDLER_GETUSERDATA:
			// read data
			try {
				inputStream = ((Context) msg.obj)
						.openFileInput(app.data.user.phone);
				objectInputStream = new ObjectInputStream(inputStream);
				StaticData data = (StaticData) objectInputStream.readObject();
				app.data.user.flag = data.user.flag;
				app.data.circles = data.circles;
				app.data.friends = data.friends;
				app.data.lastChatFriends = data.lastChatFriends;
				app.data.newFriends = data.newFriends;
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (StreamCorruptedException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} finally {
				try {
					if (objectInputStream != null) {
						objectInputStream.close();
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				try {
					if (inputStream != null) {
						inputStream.close();
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}

			break;
		case DATA_HANDLER_SAVECONFIGANDDATA:
			OutputStream outputStream = null;
			ObjectOutputStream objectOutputStream = null;
			if (app.isDataChanged) {
				app.isDataChanged = false;
				// save data
				try {
					outputStream = ((Context) msg.obj).openFileOutput(
							app.data.user.phone, Context.MODE_PRIVATE);
					app.config.lastLoginPhone = app.data.user.phone;
					objectOutputStream = new ObjectOutputStream(outputStream);
					objectOutputStream.writeObject(app.data);
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				} finally {
					try {
						if (objectOutputStream != null) {
							objectOutputStream.close();
						}
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					try {
						if (outputStream != null) {
							outputStream.close();
						}
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				// save config
				try {
					outputStream = ((Context) msg.obj).openFileOutput("config",
							Context.MODE_PRIVATE);
					objectOutputStream = new ObjectOutputStream(outputStream);
					objectOutputStream.writeObject(app.config);
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				} finally {
					try {
						if (objectOutputStream != null) {
							objectOutputStream.close();
						}
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					try {
						if (outputStream != null) {
							outputStream.close();
						}
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
			break;
		case DATA_HANDLER_GETCONFIGANDDATA:

			// read config

			try {
				inputStream = ((Context) msg.obj).openFileInput("config");
				objectInputStream = new ObjectInputStream(inputStream);
				app.config = (StaticConfig) objectInputStream.readObject();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (StreamCorruptedException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} finally {
				try {
					if (objectInputStream != null) {
						objectInputStream.close();
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				try {
					if (inputStream != null) {
						inputStream.close();
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
			// read data

			try {
				inputStream = ((Context) msg.obj)
						.openFileInput(app.config.lastLoginPhone);
				objectInputStream = new ObjectInputStream(inputStream);
				app.data = (StaticData) objectInputStream.readObject();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (StreamCorruptedException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} finally {
				try {
					if (objectInputStream != null) {
						objectInputStream.close();
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				try {
					if (inputStream != null) {
						inputStream.close();
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
			break;
		case DATA_HANDLER_USER:
			updateUser((JSONObject) msg.obj);
			break;
		case DATA_HANDLER_CIRCLE:
			JSONArray jCircles = (JSONArray) msg.obj;
			app.isDataChanged = true;
			Map<String, Friend> friends = new Hashtable<String, Friend>();
			friends.putAll(app.data.friends);
			List<Circle> circles = new ArrayList<Circle>();
			for (int i = 0; i < jCircles.length(); i++) {
				try {
					JSONObject jCircle = jCircles.getJSONObject(i);
					Circle circle = new Circle();
					try {
						circle.rid = jCircle.getInt("rid");
					} catch (JSONException e) {

					}
					circle.name = jCircle.getString("name");
					JSONArray jFriends = jCircle.getJSONArray("accounts");
					List<String> phones = new ArrayList<String>();
					for (int j = 0; j < jFriends.length(); j++) {
						JSONObject jFriend = jFriends.getJSONObject(j);
						String phone = jFriend.getString("phone");
						phones.add(phone);
						if (friends.get(phone) == null) {
							Friend friend = generateFriendFromJSON(jFriend);
							friends.put(phone, friend);
						} else {
							updateFriend(jFriend);
						}
					}
					circle.phones = phones;
					circles.add(circle);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			app.data.circles = circles;
			app.data.friends = friends;
			if (FriendsFragment.instance != null) {
				FriendsFragment.instance.mFriendsHandler
						.sendEmptyMessage(FriendsFragment.instance.NOTIFYDATASETCHANGED);
			}
			break;
		case DATA_HANDLER_MESSAGE:
			JSONArray jMessages = (JSONArray) msg.obj;
			if (jMessages.length() != 0) {
				app.isDataChanged = true;
				for (int i = 0; i < jMessages.length(); i++) {
					try {
						JSONObject jMessage = new JSONObject(
								jMessages.getString(i));
						com.lejoying.mc.data.Message message = new com.lejoying.mc.data.Message();

						String phoneSend = jMessage.getString("phone");
						String phoneReceive = new JSONArray(
								jMessage.getString("phoneto")).getString(0);

						String friendPhone = phoneSend;

						if (phoneSend.equals(app.data.user.phone)) {
							message.type = com.lejoying.mc.data.Message.MESSAGE_TYPE_SEND;
							friendPhone = phoneReceive;
						} else if (phoneReceive.equals(app.data.user.phone)) {
							message.type = com.lejoying.mc.data.Message.MESSAGE_TYPE_RECEIVE;
							friendPhone = phoneSend;
						}

						message.time = jMessage.getString("time");

						message.messageType = jMessage.getString("type");

						message.content = jMessage.getString("content");

						message.status = "sent";

						app.data.friends.get(friendPhone).messages.add(message);
						if (message.type == com.lejoying.mc.data.Message.MESSAGE_TYPE_RECEIVE) {
							if (ChatFragment.instance == null
									|| !app.nowChatFriend.phone
											.equals(friendPhone)) {
								app.data.friends.get(friendPhone).notReadMessagesCount++;
							}
						} else {
							app.data.friends.get(friendPhone).notReadMessagesCount = 0;
						}

						app.data.lastChatFriends.remove(friendPhone);
						app.data.lastChatFriends.add(0, friendPhone);

						handler.post(new Runnable() {
							@Override
							public void run() {
								if (ChatFragment.instance != null) {
									ChatFragment.instance.mAdapter
											.notifyDataSetChanged();
								}
								if (FriendsFragment.instance != null) {
									FriendsFragment.instance.mFriendsHandler
											.sendEmptyMessage(FriendsFragment.instance.NOTIFYDATASETCHANGED);
								}
							}
						});

					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
			}
			if (FriendsFragment.instance != null) {
				FriendsFragment.instance.mFriendsHandler
						.sendEmptyMessage(FriendsFragment.instance.NOTIFYDATASETCHANGED);
			}
			break;
		case DATA_HANDLER_NEWFRIEND:
			JSONArray jFriends = (JSONArray) msg.obj;
			for (int i = 0; i < jFriends.length(); i++) {
				try {
					JSONObject jFriend = jFriends.getJSONObject(i);
					Friend friend = generateFriendFromJSON(jFriend);
					if (app.data.newFriends == null) {
						app.data.newFriends = new ArrayList<Friend>();
					}
					if (!app.data.newFriends.contains(friend)) {
						app.data.newFriends.add(0, friend);
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			if (FriendsFragment.instance != null) {
				FriendsFragment.instance.mFriendsHandler
						.sendEmptyMessage(FriendsFragment.instance.NOTIFYDATASETCHANGED);
			}
			break;
		case DATA_HANDLER_UPDATEUSER:
			updateUser((JSONObject) msg.obj);
			break;
		case DATA_HANDLER_UPDATEFRIEND:
			updateFriend((JSONObject) msg.obj);
			break;
		default:
			break;
		}

	}

	int updateUser(JSONObject jUser) {
		app.isDataChanged = true;
		int count = 0;
		User user = generateUserFromJSON(jUser);
		if (user.head != null && !user.head.equals("")) {
			app.data.user.head = user.head;
			count++;
		}
		if (user.mainBusiness != null && !user.mainBusiness.equals("")) {
			app.data.user.mainBusiness = user.mainBusiness;
			count++;
		}
		if (user.nickName != null && !user.nickName.equals("")) {
			app.data.user.nickName = user.nickName;
			count++;
		}
		return count;
	}

	int updateFriend(JSONObject jFriend) {
		app.isDataChanged = true;
		int count = 0;
		Friend friend = generateFriendFromJSON(jFriend);
		Friend updateFriend = app.data.friends.get(friend.phone);
		if (updateFriend != null) {
			if (friend.head != null && !friend.head.equals("")) {
				updateFriend.head = friend.head;
				count++;
			}
			if (friend.nickName != null && !friend.nickName.equals("")) {
				updateFriend.nickName = friend.nickName;
				count++;
			}
			if (friend.mainBusiness != null && !friend.mainBusiness.equals("")) {
				updateFriend.mainBusiness = friend.mainBusiness;
				count++;
			}
			if (friend.friendStatus != null && !friend.friendStatus.equals("")) {
				updateFriend.friendStatus = friend.friendStatus;
				count++;
			}
		}
		return count;
	}

	User generateUserFromJSON(JSONObject jUser) {
		User user = new User();
		try {
			user.phone = jUser.getString("phone");
		} catch (JSONException e) {
		}
		try {
			user.head = jUser.getString("head");
		} catch (JSONException e) {
		}
		try {
			user.nickName = jUser.getString("nickName");
		} catch (JSONException e) {
		}
		try {
			user.mainBusiness = jUser.getString("mainBusiness");
		} catch (JSONException e) {
		}
		return user;
	}

	Friend generateFriendFromJSON(JSONObject jFriend) {
		Friend friend = new Friend();
		try {
			friend.phone = jFriend.getString("phone");
		} catch (JSONException e) {
		}
		try {
			friend.head = jFriend.getString("head");
		} catch (JSONException e) {
		}
		try {
			friend.nickName = jFriend.getString("nickName");
		} catch (JSONException e) {
		}
		try {
			friend.mainBusiness = jFriend.getString("mainBusiness");
		} catch (JSONException e) {
		}
		try {
			friend.friendStatus = jFriend.getString("friendStatus");
		} catch (JSONException e) {
		}
		try {
			friend.addMessage = jFriend.getString("message");
		} catch (JSONException e) {
		}
		return friend;
	}

}
