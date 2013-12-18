package com.lejoying.mc.utils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.StreamCorruptedException;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;

import com.lejoying.data.App;
import com.lejoying.data.Circle;
import com.lejoying.data.Friend;
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
		return App.getInstance().data.mUser;
	}

	public static int appendToUser(JSONObject account) {
		int count = 0;
		account.getString("");
		return count;
	}

	public static List<Circle> getCircles() {
		return App.getInstance().data.mCircles;
	}

	public static Map<String, Friend> getFriends() {
		return App.getInstance().data.mFriends;
	}

	public static StaticConfig getConfig() {
		return App.getInstance().config;
	}

	public static void saveCircles(JSONArray circles) {

	}

	public static void saveMessages(JSONArray messages) {

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