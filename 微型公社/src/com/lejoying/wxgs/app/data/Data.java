package com.lejoying.wxgs.app.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.os.Bundle;

import com.lejoying.wxgs.app.data.entity.Circle;
import com.lejoying.wxgs.app.data.entity.Friend;
import com.lejoying.wxgs.app.data.entity.Group;
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

	// Last messages list
	public List<String> lastChatFriends = new ArrayList<String>();

	// new friends
	public List<Friend> newFriends = new ArrayList<Friend>();

	// temp data
	public List<Friend> nearByFriends = new ArrayList<Friend>();
	public List<Group> nearByGroups = new ArrayList<Group>();
	public Friend nowChatFriend;
	public Bundle registerBundle;
	public Friend tempFriend;
}
