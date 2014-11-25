package com.open.welinks.model;

import com.open.lib.MyLog;

public class UserIntentQueue {

	public String tag = "UserIntentQueue";
	public MyLog log = new MyLog(tag, true);

	public static UserIntentQueue userIntentQueue;

	public static UserIntentQueue getInstance() {
		if (userIntentQueue == null) {
			userIntentQueue = new UserIntentQueue();
		}
		return userIntentQueue;
	}

}
