package com.lejoying.data;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class StaticData implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public User mUser;
	public List<Circle> mCircles;
	public Map<String, Friend> mFriends;

}
