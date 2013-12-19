package com.lejoying.mc.utils;

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

import android.content.Context;

import com.lejoying.data.App;
import com.lejoying.data.Circle;
import com.lejoying.data.Friend;
import com.lejoying.data.Message;
import com.lejoying.data.StaticConfig;
import com.lejoying.data.StaticData;
import com.lejoying.data.User;

public class MCDataTools {

	public static void saveData(Context context) {
		OutputStream outputStream = null;
		ObjectOutputStream objectOutputStream = null;
		// save data
		try {
			outputStream = context.openFileOutput(getLoginUser().phone,
					Context.MODE_PRIVATE);
			getConfig().lastLoginPhone = getLoginUser().phone;
			objectOutputStream = new ObjectOutputStream(outputStream);
			objectOutputStream.writeObject(App.getInstance().data);
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
			objectOutputStream.writeObject(getConfig());
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

	public static void getData(Context context) {
		InputStream inputStream = null;
		ObjectInputStream objectInputStream = null;

		// read config

		try {
			inputStream = context.openFileInput("config");
			objectInputStream = new ObjectInputStream(inputStream);
			App.getInstance().config = (StaticConfig) objectInputStream
					.readObject();
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
			inputStream = context.openFileInput(getConfig().lastLoginPhone);
			objectInputStream = new ObjectInputStream(inputStream);
			App.getInstance().data = (StaticData) objectInputStream
					.readObject();
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

	public static User getLoginUser() {
		return App.getInstance().data.user;
	}

	public static int appendToUser(JSONObject jUser) {
		int count = 0;
		User user = generateUserFromJSON(jUser);
		if (user.head != null && !user.head.equals("")) {
			getLoginUser().head = user.head;
			count++;
		}
		if (user.mainBusiness != null && !user.mainBusiness.equals("")) {
			getLoginUser().mainBusiness = user.mainBusiness;
			count++;
		}
		if (user.nickName != null && !user.nickName.equals("")) {
			getLoginUser().nickName = user.nickName;
			count++;
		}
		if (user.phone != null && !user.phone.equals("")) {
			getLoginUser().phone = user.phone;
			count++;
		}
		return count;
	}

	public static List<Circle> getCircles() {
		return App.getInstance().data.circles;
	}

	public static Map<String, Friend> getFriends() {
		return App.getInstance().data.friends;
	}

	public static StaticConfig getConfig() {
		return App.getInstance().config;
	}

	public static void saveCircles(JSONArray jCircles) {
		Map<String, Friend> friends = new Hashtable<String, Friend>();
		List<Circle> circles = new ArrayList<Circle>();
		for (int i = 0; i < jCircles.length(); i++) {
			try {
				JSONObject jCircle = jCircles.getJSONObject(i);
				Circle circle = new Circle();
				circle.rid = jCircle.getInt("rid");
				circle.name = jCircle.getString("name");
				JSONArray jFriends = jCircle.getJSONArray("accounts");
				List<String> phones = new ArrayList<String>();
				for (int j = 0; j < jFriends.length(); j++) {
					JSONObject jFriend = jFriends.getJSONObject(j);
					String phone = jFriend.getString("phone");
					phones.add(phone);
					if (friends.get(phone) == null) {
						Friend friend = new Friend();
						friend.phone = jFriend.getString("phone");
						friend.head = jFriend.getString("head");
						friend.nickName = jFriend.getString("nickName");
						friend.mainBusiness = jFriend.getString("mainBusiness");
						friend.friendStatus = jFriend.getString("friendStatus");
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
		App.getInstance().data.circles = circles;
		App.getInstance().data.friends = friends;
	}

	public static void saveMessages(JSONArray jMessages) {
		for (int i = 0; i < jMessages.length(); i++) {
			try {
				JSONObject jMessage = jMessages.getJSONObject(i);
				Message message = new Message();

				String phoneSend = jMessage.getString("phone");
				String phoneReceive = new JSONArray(
						jMessage.getString("phoneto")).getString(0);

				String friendPhone = phoneSend;

				if (phoneSend.equals(getLoginUser().phone)) {
					message.type = "send";
					friendPhone = phoneReceive;
				} else if (phoneReceive.equals(getLoginUser().phone)) {
					message.type = "receive";
					friendPhone = phoneSend;
				}

				message.time = jMessage.getString("time");

				message.messageType = jMessage.getString("type");

				message.content = new JSONObject(jMessage.getString("content"))
						.getString(message.messageType);

				getFriends().get(friendPhone).messages.add(message);

				Integer notReadCount = App.getInstance().data.notReadCountMap
						.get(friendPhone);

				if (notReadCount == null) {
					App.getInstance().data.lastChatFriends.add(0, friendPhone);
					App.getInstance().data.notReadCountMap.put(friendPhone, 1);
				} else {
					App.getInstance().data.lastChatFriends.remove(friendPhone);
					App.getInstance().data.lastChatFriends.add(0, friendPhone);
					App.getInstance().data.notReadCountMap.put(friendPhone,
							notReadCount += 1);
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

}