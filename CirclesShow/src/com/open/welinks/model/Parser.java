package com.open.welinks.model;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.open.welinks.model.Data.Relationship;
import com.open.welinks.model.Data.UserInformation;

public class Parser {
	String tag = "Parser";
	
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

	public void parse() {
		Data data = Data.getInstance();
		Gson gson = new Gson();

		String userInformationStr = getFromAssets("userInformation.js");
		data.userInformation = gson.fromJson(userInformationStr, UserInformation.class);
		
		String userInformationStr_debug = gson.toJson(data.userInformation);
		Log.d(tag, userInformationStr_debug);
		
		String relationshipStr = getFromAssets("relationship.js");
		data.relationship = gson.fromJson(relationshipStr, Relationship.class);
		
		int i = 1;
		i = i + 68;
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
}
