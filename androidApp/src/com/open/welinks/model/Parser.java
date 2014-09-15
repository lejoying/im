package com.open.welinks.model;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import com.google.gson.Gson;
import com.open.lib.MyLog;
import com.open.welinks.model.Data.Event;
import com.open.welinks.model.Data.LocalStatus.LocalData;
import com.open.welinks.model.Data.Messages;
import com.open.welinks.model.Data.Relationship;
import com.open.welinks.model.Data.Shares;
import com.open.welinks.model.Data.UserInformation;
import com.open.welinks.utils.StreamParser;

public class Parser {
	String tag = "Parser";
	public MyLog log = new MyLog(tag, true);

	public static Parser parser;

	public static Parser getInstance() {
		if (parser == null) {
			parser = new Parser();
		}
		return parser;

	}

	public Context context;
	public Gson gson;

	public void initialize(Context context) {
		this.context = context;
		if (gson == null) {
			this.gson = new Gson();
		}
	}

	public Data parse() {
		Data data = Data.getInstance();
		if (gson == null) {
			this.gson = new Gson();
		}
		try {
			String localDataStr = getFromAssets("localData.js");
			data.localStatus.localData = gson.fromJson(localDataStr, LocalData.class);

			String userInformationStr = getFromAssets("userInformation.js");
			data.userInformation = gson.fromJson(userInformationStr, UserInformation.class);

			String userInformationStr_debug = gson.toJson(data.userInformation);
			Log.d(tag, userInformationStr_debug);

			String relationshipStr = getFromAssets("relationship.js");
			data.relationship = gson.fromJson(relationshipStr, Relationship.class);

			String messageContent = getFromAssets("message.js");
			data.messages = gson.fromJson(messageContent, Messages.class);

			String shareContent = getFromAssets("share.js");
			data.shares = gson.fromJson(shareContent, Shares.class);

			String eventContent = getFromAssets("event.js");
			data.event = gson.fromJson(eventContent, Event.class);
		} catch (Exception e) {
			log.e(tag, "**************Gson parse error!**************");
			data = null;
		}

		return data;
	}

	public String getFromAssets(String fileName) {
		String result = null;
		try {
			InputStreamReader inputReader = new InputStreamReader(context.getResources().getAssets().open(fileName));
			BufferedReader bufReader = new BufferedReader(inputReader);
			String line = "";
			result = "";
			while ((line = bufReader.readLine()) != null) {
				result += line;
			}
			return result;
		} catch (Exception e) {
			e.printStackTrace();
			result = null;
		}
		return result;
	}

	public void saveToSD(File forder, String fileName, String content) {
		try {
			File file = new File(forder, fileName);
			FileOutputStream userInformationFileOutputStream = new FileOutputStream(file);
			byte[] buffer = content.getBytes();
			userInformationFileOutputStream.write(buffer);
			userInformationFileOutputStream.flush();
			userInformationFileOutputStream.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void saveToUserForder(String phone, String fileName, String content) {
		File sdFile = Environment.getExternalStorageDirectory();
		File userForder = new File(sdFile, "welinks/" + phone);

		if (!userForder.exists()) {
			userForder.mkdirs();
		}

		saveToSD(userForder, fileName, content);
	}

	public void saveToRootForder(String fileName, String content) {
		File sdFile = Environment.getExternalStorageDirectory();
		File rootForder = new File(sdFile, "welinks/");

		if (!rootForder.exists()) {
			rootForder.mkdirs();
		}

		saveToSD(rootForder, fileName, content);
	}

	public String getFromSD(File forder, String fileName) {

		String result = null;
		try {
			File file = new File(forder, fileName);
			if (!file.exists()) {
				return null;
			}
			FileInputStream fileInputStream = new FileInputStream(file);
			byte[] bytes = StreamParser.parseToByteArray(fileInputStream);
			result = new String(bytes);

			return result;
		} catch (Exception e) {
			e.printStackTrace();
			result = null;
		}
		return result;
	}

	public String getFromUserForder(String phone, String fileName) {
		String result = null;
		File sdFile = Environment.getExternalStorageDirectory();
		File userForder = new File(sdFile, "welinks/" + phone);

		if (!userForder.exists()) {
			userForder.mkdirs();
		}

		result = getFromSD(userForder, fileName);

		if (result == null) {
			result = getFromAssets(fileName);
		}

		return result;
	}

	public String getFromRootForder(String fileName) {
		String result = null;
		File sdFile = Environment.getExternalStorageDirectory();
		File rootForder = new File(sdFile, "welinks/");

		result = getFromSD(rootForder, fileName);

		if (result == null) {
			result = getFromAssets(fileName);
		}

		return result;
	}

	public Data check() {
		Data data = Data.getInstance();
		if (gson == null) {
			this.gson = new Gson();
		}
		String phone = "none";
		try {
			log.e(tag, "**check data");
			try {
				if (data.localStatus.localData == null) {
					String localDataStr = getFromRootForder("localData.js");
					data.localStatus.localData = gson.fromJson(localDataStr, LocalData.class);
				}
			} catch (Exception e) {
				throw e;
			}
			try {
				if (data.userInformation == null) {
					log.e(tag, "**data.userInformation is null");
					String userInformationStr = getFromRootForder("userInformation.js");
					data.userInformation = gson.fromJson(userInformationStr, UserInformation.class);
				}
			} catch (Exception e) {
				throw e;
			}
			try {
				if (!"".equals(data.userInformation.currentUser.phone) && !"".equals(data.userInformation.currentUser.accessKey)) {
					phone = data.userInformation.currentUser.phone;
				}
			} catch (Exception e) {
				throw e;
			}
			try {
				if (data.relationship == null) {
					log.e(tag, "**data.relationship is null");
					String relationshipStr = getFromUserForder(phone, "relationship.js");
					data.relationship = gson.fromJson(relationshipStr, Relationship.class);
				}
			} catch (Exception e) {
				throw e;
			}
			try {
				if (data.messages == null) {
					String messageContent = getFromUserForder(phone, "message.js");
					data.messages = gson.fromJson(messageContent, Messages.class);
				}
			} catch (Exception e) {
				throw e;
			}
			try {
				if (data.shares == null) {
					String shareContent = getFromUserForder(phone, "share.js");
					data.shares = gson.fromJson(shareContent, Shares.class);
				}
			} catch (Exception e) {
				throw e;
			}
			try {
				if (data.event == null) {
					String eventContent = getFromUserForder(phone, "event.js");
					data.event = gson.fromJson(eventContent, Event.class);
				}
			} catch (Exception e) {
				// throw e;

			}
		} catch (Exception e) {
			log.e(tag, "**************Gson parse error!**************");
			e.printStackTrace();
			data = null;
		}

		return data;
	}

	public void save() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				log.e(tag, "**************saveDataToSD!**************");
				saveDataToSD();
			}
		}).start();
	}

	public void saveDataToSD() {
		Data data = Data.getInstance();
		String phone = data.userInformation.currentUser.phone;

		String localDataStr = gson.toJson(data.localStatus.localData);
		saveToRootForder("localData.js", localDataStr);

		if (data.userInformation.isModified) {
			data.userInformation.isModified = false;
			String userInformationStr = gson.toJson(data.userInformation);
			saveToRootForder("userInformation.js", userInformationStr);
		}

		if (data.relationship.isModified) {
			data.relationship.isModified = false;

			String relationshipStr = gson.toJson(data.relationship);
			saveToUserForder(phone, "relationship.js", relationshipStr);
		}

		if (data.shares.isModified) {
			data.shares.isModified = false;
			String sharesStr = gson.toJson(data.shares);
			saveToUserForder(phone, "share.js", sharesStr);
		}

		if (data.messages.isModified) {
			data.messages.isModified = false;

			String messagesStr = gson.toJson(data.messages);
			saveToUserForder(phone, "message.js", messagesStr);
		}

		if (data.event.isModified) {
			data.event.isModified = false;

			String eventStr = gson.toJson(data.event);
			saveToUserForder(phone, "event.js", eventStr);
		}
	}

}
