package com.open.welinks.model;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import com.google.gson.Gson;
import com.open.lib.MyLog;
import com.open.welinks.model.Data.LocalStatus;
import com.open.welinks.model.Data.Messages;
import com.open.welinks.model.Data.Relationship;
import com.open.welinks.model.Data.Shares;
import com.open.welinks.model.Data.Squares;
import com.open.welinks.model.Data.TempData;
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

	public void initialize(Context context) {
		this.context = context;
		int i = 1;
		i = i + 66;
	}

	public Data parse() {
		Data data = Data.getInstance();
		Gson gson = new Gson();
		try {
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

			String squareContent = getFromAssets("square.js");
			data.squares = gson.fromJson(squareContent, Squares.class);
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
		}
		return result;
	}

	public Data check() {
		Data data = Data.getInstance();
		Gson gson = new Gson();

		try {
			if (data.userInformation == null) {
				String userInformationStr = getFromAssets("userInformation.js");
				data.userInformation = gson.fromJson(userInformationStr, UserInformation.class);
			}
			if (data.relationship == null) {
				String relationshipStr = getFromAssets("relationship.js");
				data.relationship = gson.fromJson(relationshipStr, Relationship.class);
			}
			if (data.messages == null) {
				String messageContent = getFromAssets("message.js");
				data.messages = gson.fromJson(messageContent, Messages.class);
			}
			if (data.shares == null) {
				String shareContent = getFromAssets("share.js");
				data.shares = gson.fromJson(shareContent, Shares.class);
			}
			if (data.squares == null) {
				String squareContent = getFromAssets("square.js");
				data.squares = gson.fromJson(squareContent, Squares.class);
			}
		} catch (Exception e) {
			log.e(tag, "**************Gson parse error!**************");
			data = null;
		}

		return data;
	}

	public void readSdFileToData() {
		final Data data = Data.getInstance();
		File sdFile = Environment.getExternalStorageDirectory();
		data.userInformation = data.new UserInformation();
		data.userInformation.currentUser = data.userInformation.new User();
		data.userInformation.currentUser.phone = "152";
		final File currentUserFile = new File(sdFile, "welinks/" + data.userInformation.currentUser.phone);
		if (!currentUserFile.exists()) {
			return;
		}
		new Thread(new Runnable() {

			@Override
			public void run() {
				Gson gson = new Gson();
				// read userInformation
				try {
					File userInformationFile = new File(currentUserFile, "userInformation.js");
					if (!userInformationFile.exists()) {
						return;
					}
					FileInputStream fileInputStream = new FileInputStream(userInformationFile);
					byte[] bytes = StreamParser.parseToByteArray(fileInputStream);
					String content = new String(bytes);// String.valueOf(bytes)
					data.userInformation = gson.fromJson(content, UserInformation.class);
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}
			}
		}).start();
		new Thread(new Runnable() {

			@Override
			public void run() {
				Gson gson = new Gson();
				// read relationship
				try {
					File relationshipFile = new File(currentUserFile, "relationship.js");
					if (!relationshipFile.exists()) {
						return;
					}
					FileInputStream fileInputStream = new FileInputStream(relationshipFile);
					byte[] bytes = StreamParser.parseToByteArray(fileInputStream);
					String content = new String(bytes);// String.valueOf(bytes)
					data.relationship = gson.fromJson(content, Relationship.class);
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}
			}
		}).start();
		new Thread(new Runnable() {

			@Override
			public void run() {
				Gson gson = new Gson();
				// read shares
				try {
					File sharesFile = new File(currentUserFile, "shares.js");
					if (!sharesFile.exists()) {
						return;
					}
					FileInputStream fileInputStream = new FileInputStream(sharesFile);
					byte[] bytes = StreamParser.parseToByteArray(fileInputStream);
					String content = new String(bytes);// String.valueOf(bytes)
					data.shares = gson.fromJson(content, Shares.class);
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}
			}
		}).start();
		new Thread(new Runnable() {

			@Override
			public void run() {
				Gson gson = new Gson();
				// save squares
				try {
					File squaresFile = new File(currentUserFile, "squares.js");
					if (!squaresFile.exists()) {
						return;
					}
					FileInputStream fileInputStream = new FileInputStream(squaresFile);
					byte[] bytes = StreamParser.parseToByteArray(fileInputStream);
					String content = new String(bytes);// String.valueOf(bytes)
					data.squares = gson.fromJson(content, Squares.class);
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}
			}
		}).start();
		new Thread(new Runnable() {

			@Override
			public void run() {
				Gson gson = new Gson();
				// read localStatus
				try {
					File localStatusFile = new File(currentUserFile, "localStatus.js");
					if (!localStatusFile.exists()) {
						return;
					}
					FileInputStream fileInputStream = new FileInputStream(localStatusFile);
					byte[] bytes = StreamParser.parseToByteArray(fileInputStream);
					String content = new String(bytes);// String.valueOf(bytes)
					data.localStatus = gson.fromJson(content, LocalStatus.class);
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}
			}
		}).start();
		new Thread(new Runnable() {

			@Override
			public void run() {
				Gson gson = new Gson();
				// read tempData
				try {
					File tempDataFile = new File(currentUserFile, "tempData.js");
					if (!tempDataFile.exists()) {
						return;
					}
					FileInputStream fileInputStream = new FileInputStream(tempDataFile);
					byte[] bytes = StreamParser.parseToByteArray(fileInputStream);
					String content = new String(bytes);// String.valueOf(bytes)
					data.tempData = gson.fromJson(content, TempData.class);
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}
			}
		}).start();
	}

	public void saveDataToLocal() {
		final Data data = Data.getInstance();
		File sdFile = Environment.getExternalStorageDirectory();
		final File currentUserFile = new File(sdFile, "welinks/" + data.userInformation.currentUser.phone);
		if (!currentUserFile.exists()) {
			currentUserFile.mkdirs();
		}
		new Thread(new Runnable() {

			@Override
			public void run() {
				Gson gson = new Gson();
				// save userInformation
				try {
					String userInformationString = gson.toJson(data.userInformation);
					Log.e(tag, userInformationString.length() + "---userInformation");
					File userInformationFile = new File(currentUserFile, "userInformation.js");
					FileOutputStream userInformationFileOutputStream = new FileOutputStream(userInformationFile);
					byte[] buffer = userInformationString.getBytes();
					userInformationFileOutputStream.write(buffer);
					userInformationFileOutputStream.flush();
					userInformationFileOutputStream.close();
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}).start();
		new Thread(new Runnable() {

			@Override
			public void run() {
				Gson gson = new Gson();
				// save relationship
				try {
					String relationshipString = gson.toJson(data.relationship);
					Log.e(tag, relationshipString.length() + "---relationship");
					File relationshipFile = new File(currentUserFile, "relationship.js");
					FileOutputStream relationshipFileOutputStream = new FileOutputStream(relationshipFile);
					byte[] buffer = relationshipString.getBytes();
					relationshipFileOutputStream.write(buffer);
					relationshipFileOutputStream.flush();
					relationshipFileOutputStream.close();
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}).start();
		new Thread(new Runnable() {

			@Override
			public void run() {
				Gson gson = new Gson();
				// save shares
				try {
					String sharesString = gson.toJson(data.shares);
					Log.e(tag, sharesString.length() + "---shares");
					File sharesFile = new File(currentUserFile, "shares.js");
					FileOutputStream sharesFileOutputStream = new FileOutputStream(sharesFile);
					byte[] buffer = sharesString.getBytes();
					sharesFileOutputStream.write(buffer);
					sharesFileOutputStream.flush();
					sharesFileOutputStream.close();
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}).start();
		new Thread(new Runnable() {

			@Override
			public void run() {
				Gson gson = new Gson();
				// save squares
				try {
					String squaresString = gson.toJson(data.squares);
					Log.e(tag, squaresString.length() + "---squares");
					File squaresFile = new File(currentUserFile, "squares.js");
					FileOutputStream squaresFileOutputStream = new FileOutputStream(squaresFile);
					byte[] buffer = squaresString.getBytes();
					squaresFileOutputStream.write(buffer);
					squaresFileOutputStream.flush();
					squaresFileOutputStream.close();
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}).start();
		new Thread(new Runnable() {

			@Override
			public void run() {
				Gson gson = new Gson();
				// save localStatus
				try {
					Log.e(tag, "---localStatus  ：" + data.localStatus);
					String localStatusString = gson.toJson(data.localStatus);
					Log.e(tag, localStatusString.length() + "---localStatus");
					File localStatusFile = new File(currentUserFile, "localStatus.js");
					FileOutputStream localStatusFileOutputStream = new FileOutputStream(localStatusFile);
					byte[] buffer = localStatusString.getBytes();
					localStatusFileOutputStream.write(buffer);
					localStatusFileOutputStream.flush();
					localStatusFileOutputStream.close();
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}).start();
		new Thread(new Runnable() {

			@Override
			public void run() {
				Gson gson = new Gson();
				// save tempData
				try {
					Log.e(tag, "---tempData  ：" + data.tempData);
					String tempDataString = gson.toJson(data.tempData);
					Log.e(tag, tempDataString.length() + "---tempData");
					File tempDataFile = new File(currentUserFile, "tempData.js");
					FileOutputStream tempDataFileOutputStream = new FileOutputStream(tempDataFile);
					byte[] buffer = tempDataString.getBytes();
					tempDataFileOutputStream.write(buffer);
					tempDataFileOutputStream.flush();
					tempDataFileOutputStream.close();
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}).start();
	}
}
