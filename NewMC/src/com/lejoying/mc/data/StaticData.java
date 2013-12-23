package com.lejoying.mc.data;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class StaticData implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public User user;
	public List<Circle> circles;
	public Map<String, Friend> friends;

	// Last messages list
	public List<String> lastChatFriends;

	// new friends
	public List<Friend> newFriends;
}
