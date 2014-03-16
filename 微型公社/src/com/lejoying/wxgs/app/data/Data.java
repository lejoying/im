package com.lejoying.wxgs.app.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.lejoying.wxgs.app.data.entity.Circle;
import com.lejoying.wxgs.app.data.entity.Friend;
import com.lejoying.wxgs.app.data.entity.Group;
import com.lejoying.wxgs.app.data.entity.Message;
import com.lejoying.wxgs.app.data.entity.User;

public class Data implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public User user = new User();
	public List<Circle> circles = new ArrayList<Circle>();
	public List<Group> groups = new ArrayList<Group>();

	public Map<String, Friend> groupFriends = new HashMap<String, Friend>();
	public Map<String, Friend> friends = new HashMap<String, Friend>();

	public Map<String, List<Message>> squareMessages = new HashMap<String, List<Message>>();

	// Last messages list
	public List<String> lastChatFriends = new ArrayList<String>();

	// new friends
	public List<Friend> newFriends = new ArrayList<Friend>();

	// temp data
	public List<Friend> nearByFriends = new ArrayList<Friend>();
	public List<Group> nearByGroups = new ArrayList<Group>();
	public Friend nowChatFriend;
	public Friend tempFriend;

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
		lastChatFriends.clear();
		newFriends.clear();
		nearByFriends.clear();
		nearByGroups.clear();
		nowChatFriend = null;
		tempFriend = null;
	}
}
