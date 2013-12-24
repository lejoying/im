package com.lejoying.mc.utils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.StreamCorruptedException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;

import com.lejoying.mc.data.App;
import com.lejoying.mc.data.Circle;
import com.lejoying.mc.data.Friend;
import com.lejoying.mc.data.Message;
import com.lejoying.mc.data.StaticConfig;
import com.lejoying.mc.data.StaticData;
import com.lejoying.mc.data.User;

public class MCDataTools {

	public static App app = App.getInstance();

	public static void saveData(Context context) {
		OutputStream outputStream = null;
		ObjectOutputStream objectOutputStream = null;
		if (app.isDataChanged) {
			app.isDataChanged = false;
			// save data
			try {
				outputStream = context.openFileOutput(app.data.user.phone,
						Context.MODE_PRIVATE);
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
				outputStream = context.openFileOutput("config",
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

	}

	public static void getData(Context context) {
		InputStream inputStream = null;
		ObjectInputStream objectInputStream = null;

		// read config

		try {
			inputStream = context.openFileInput("config");
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
			inputStream = context.openFileInput(app.config.lastLoginPhone);
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
	}

	public static void getUserData(Context context, User user) {
		InputStream inputStream = null;
		ObjectInputStream objectInputStream = null;
		// read data
		try {
			inputStream = context.openFileInput(user.phone);
			objectInputStream = new ObjectInputStream(inputStream);
			StaticData data = (StaticData) objectInputStream.readObject();
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
	}

	public static int updateUser(JSONObject jUser) {
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
		if (user.phone != null && !user.phone.equals("")) {
			app.data.user.phone = user.phone;
			count++;
		}
		return count;
	}

	public static void saveCircles(JSONArray jCircles) {
		app.isDataChanged = true;
		Map<String, Friend> friends = app.data.friends;
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
	}

	public static void saveMessages(JSONArray jMessages) {
		if (jMessages.length() != 0) {
			app.isDataChanged = true;
			for (int i = 0; i < jMessages.length(); i++) {
				try {
					JSONObject jMessage = new JSONObject(jMessages.getString(i));
					Message message = new Message();

					String phoneSend = jMessage.getString("phone");
					String phoneReceive = new JSONArray(
							jMessage.getString("phoneto")).getString(0);

					String friendPhone = phoneSend;

					if (phoneSend.equals(app.data.user.phone)) {
						message.type = "send";
						friendPhone = phoneReceive;
					} else if (phoneReceive.equals(app.data.user.phone)) {
						message.type = "receive";
						friendPhone = phoneSend;
					}

					message.time = jMessage.getString("time");

					message.messageType = jMessage.getString("type");

					message.content = new JSONObject(
							jMessage.getString("content"))
							.getString(message.messageType);

					app.data.friends.get(friendPhone).messages.add(message);
					app.data.friends.get(friendPhone).notReadMessagesCount++;

					app.data.lastChatFriends.remove(friendPhone);
					app.data.lastChatFriends.add(0, friendPhone);

				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public static void saveNewFriends(JSONArray jFriends) {
		for (int i = 0; i < jFriends.length(); i++) {
			try {
				JSONObject jFriend = jFriends.getJSONObject(i);
				Friend friend = generateFriendFromJSON(jFriend);
				if (app.data.newFriends == null) {
					app.data.newFriends = new ArrayList<Friend>();
				}
				if (!app.data.newFriends.contains(friend)) {
					app.data.newFriends.add(friend);
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public static User generateUserFromJSON(JSONObject jUser) {
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

	public static Friend generateFriendFromJSON(JSONObject jFriend) {
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
		return friend;
	}

}