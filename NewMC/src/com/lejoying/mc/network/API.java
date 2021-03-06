package com.lejoying.mc.network;

public abstract class API {
	protected static final String SLASH = "/";

	public static final String ACCOUNT = "account";
	public static final String ACCOUNT_VERIFYPHONE = "api2/account/verifyphone";
	public static final String ACCOUNT_VERIFYCODE = "api2/account/verifycode";
	public static final String ACCOUNT_AUTH = "api2/account/auth";
	public static final String ACCOUNT_GET = "api2/account/get";
	public static final String ACCOUNT_MODIFY = "api2/account/modify";
	public static final String ACCOUNT_EXIT = "api2/account/exit";

	public static final String CIRCLE = "circle";
	public static final String CIRCLE_MODIFY = "api2/circle/modify";
	public static final String CIRCLE_DELETE = "api2/circle/delete";
	public static final String CIRCLE_MOVEOROUT = "api2/circle/moveorout";
	public static final String CIRCLE_MOVEOUT = "api2/circle/moveout";
	public static final String CIRCLE_ADDCIRCLE = "api2/circle/addcircle";

	public static final String COMMUNITY = "community";
	public static final String COMMUNITY_ADD = "api2/community/add";
	public static final String COMMUNITY_FIND = "api2/community/find";
	public static final String COMMUNITY_JOIN = "api2/community/join";
	public static final String COMMUNITY_UNJOIN = "api2/community/unjoin";
	public static final String COMMUNITY_GETCOMMUNITIES = "api2/community/getcommunities";
	public static final String COMMUNITY_GETCOMMUNITYFRIENDS = "api2/community/getcommunityfriends";

	public static final String MESSAGE = "message";
	public static final String MESSAGE_SEND = "api2/message/send";
	public static final String MESSAGE_GET = "api2/message/get";

	public static final String RELATION = "relation";
	public static final String RELATION_ADDFRIEND = "api2/relation/addfriend";
	public static final String RELATION_DELETEFRIEND = "api2/relation/deletefriend";
	public static final String RELATION_BLACKLIST = "api2/relation/blacklist";
	public static final String RELATION_GETFRIENDS = "api2/relation/getfriends";
	public static final String RELATION_GETCIRCLESANDFRIENDS = "api2/relation/getcirclesandfriends";
	public static final String RELATION_GETASKFRIENDS = "api2/relation/getaskfriends";
	public static final String RELATION_ADDFRIENDAGREE = "api2/relation/addfriendagree";

	public static final String SESSION = "session";
	public static final String SESSION_EVENT = "api2/session/event";

	public static final String WEBCODE = "webcode";
	public static final String WEBCODE_WEBCODELOGIN = "api2/webcode/webcodelogin";

	public static final String IMAGE = "image";
	public static final String IMAGE_UPLOAD = "image/upload";
	public static final String IMAGE_CHECK = "image/check";

	public static final String GROUP = "group";
	public static final String GROUP_CREATE = "api2/group/create";
	public static final String GROUP_ADDMEMBERS = "api2/group/addmembers";
	public static final String GROUP_REMOVEMEMBERS = "api2/group/removemembers";
	public static final String GROUP_GETALLMEMBERS = "api2/group/getallmembers";
	public static final String GROUP_MODIFY = "api2/group/modify";
	public static final String GROUP_GETUSERGROUPS = "api2/group/getusergroups";
	public static final String GROUP_GET = "api2/group/get";
	public static final String GROUP_GETGROUPSANDMEMBERS = "api2/group/getgroupsandmembers";

	public static final String LBS = "lbs";
	public static final String LBS_UPDATELOCATION = "lbs/updatelocation";
	public static final String LBS_SETGROUPLOCATION = "lbs/setgrouplocation";
	public static final String LBS_NEARBYACCOUNTS = "lbs/nearbyaccounts";
	public static final String LBS_NEARBYGROUPS = "lbs/nearbygroups";

	public static final String SQUARE = "square";
	public static final String SQUARE_GETSQUAREMESSAGE = "api2/square/getsquaremessage";
	public static final String SQUARE_SENDSQUAREMESSAGE = "api2/square/sendsquaremessage";

	public static String getClazz(String api) {
		return api.substring(0, api.indexOf(SLASH));
	}
}
