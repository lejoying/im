package com.open.welinks.model;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.open.lib.MyLog;
import com.open.welinks.model.Data.Messages;
import com.open.welinks.model.Data.Relationship;
import com.open.welinks.model.Data.Shares;
import com.open.welinks.model.Data.Squares;
import com.open.welinks.model.Data.UserInformation;

public class Parser {
	String tag = "Parser";
	public MyLog log = new MyLog(tag, false);

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
}
