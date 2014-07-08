package com.lejoying.wxgs.app.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.lejoying.wxgs.app.data.entity.Circle;
import com.lejoying.wxgs.app.data.entity.Friend;
import com.lejoying.wxgs.app.data.entity.Group;
import com.lejoying.wxgs.app.data.entity.SquareMessage;
import com.lejoying.wxgs.app.data.entity.User;

public class Data implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public User user = new User();
	public List<String> circles = new ArrayList<String>();
	public Map<String, Circle> circlesMap = new HashMap<String, Circle>();
	public List<String> groups = new ArrayList<String>();
	public Map<String, Group> groupsMap = new HashMap<String, Group>();

	public Map<String, Friend> groupFriends = new HashMap<String, Friend>();
	public Map<String, Friend> friends = new HashMap<String, Friend>();

	public Map<String, List<String>> squareMessages = new HashMap<String, List<String>>();
	public Map<String, Map<String, SquareMessage>> squareMessagesMap = new HashMap<String, Map<String, SquareMessage>>();
	public Map<String, Map<String, List<String>>> squareMessagesClassify = new HashMap<String, Map<String, List<String>>>();
	public Map<String, String> squareFlags = new HashMap<String, String>();
	public Map<String, List<String>> squareCollects = new HashMap<String, List<String>>();
	public String currentSquare = "";
	public String currentGroup = "";

	// Last messages list
	public List<String> lastChatFriends = new ArrayList<String>();

	// new friends
	public List<Friend> newFriends = new ArrayList<Friend>();

	// temp data
	public List<Friend> nearByFriends = new ArrayList<Friend>();
	public List<Group> nearByGroups = new ArrayList<Group>();

	public boolean isClear;
	public boolean isChanged;

	public void clear() {
		isClear = true;
		user = new User();
		circles.clear();
		groups.clear();
		groupFriends.clear();
		friends.clear();
		squareMessages.clear();
		squareMessagesClassify.clear();
		squareMessagesMap.clear();
		squareFlags.clear();
		lastChatFriends.clear();
		newFriends.clear();
		nearByFriends.clear();
		nearByGroups.clear();
		squareCollects.clear();
		currentSquare = "";
		currentGroup = "";
	}
}
