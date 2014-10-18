package com.open.welinks.utils;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonSyntaxException;
import com.open.lib.MyLog;

public class MyGson {

	public String tag = "MyGson";
	public MyLog log = new MyLog(tag, true);

	public Gson gson = new Gson();

	public <T> T fromJson(String json, Class<T> classOfT) {
		T t;
		try {
			t = gson.fromJson(json, classOfT);
		} catch (JsonSyntaxException e) {
			e.printStackTrace();
			log.e(e.toString());
			t = null;
		}
		return t;
	}

	public String toJson(JsonElement jsonElement) {
		String result = null;
		try {
			result = gson.toJson(jsonElement);
		} catch (Exception e) {
			e.printStackTrace();
			log.e(e.toString());
			result = null;
		}
		return result;
	}
}
