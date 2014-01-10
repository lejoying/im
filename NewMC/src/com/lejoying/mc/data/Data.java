package com.lejoying.mc.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import android.os.Bundle;

public class Data implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public User user = new User();
	public List<Circle> circles = new ArrayList<Circle>();
	public Map<String, Friend> friends = new Hashtable<String, Friend>();

	// Last messages list
	public List<String> lastChatFriends = new ArrayList<String>();

	// new friends
	public List<Friend> newFriends = new ArrayList<Friend>();

	// temp data
	public Friend nowChatFriend;
	public Bundle registerBundle;
	public Friend tempFriend;
}
