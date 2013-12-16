package com.lejoying.mc.api;

public abstract class API {
	protected static final String SLASH = "/";

	public static final String ACCOUNT = "account";
	public static final String ACCOUNT_VERIFYPHONE = generate(ACCOUNT,
			"verifyphone");
	public static final String ACCOUNT_VERIFYCODE = generate(ACCOUNT,
			"verifycode");;
	public static final String ACCOUNT_AUTH = generate(ACCOUNT, "auth");;
	public static final String ACCOUNT_GET = generate(ACCOUNT, "get");;
	public static final String ACCOUNT_MODIFY = generate(ACCOUNT, "modify");;
	public static final String ACCOUNT_EXIT = generate(ACCOUNT, "exit");;

	public static final String CIRCLE = "circle";
	public static final String CIRCLE_MODIFY = generate(CIRCLE, "modify");
	public static final String CIRCLE_DELETE = generate(CIRCLE, "delete");
	public static final String CIRCLE_MOVEOROUT = generate(CIRCLE, "moveorout");
	public static final String CIRCLE_MOVEOUT = generate(CIRCLE, "moveout");
	public static final String CIRCLE_ADDCIRCLE = generate(CIRCLE, "addcircle");

	public static final String COMMUNITY = "community";
	public static final String COMMUNITY_ADD = generate(COMMUNITY, "add");
	public static final String COMMUNITY_FIND = generate(COMMUNITY, "find");
	public static final String COMMUNITY_JOIN = generate(COMMUNITY, "join");
	public static final String COMMUNITY_UNJOIN = generate(COMMUNITY, "unjoin");
	public static final String COMMUNITY_GETCOMMUNITIES = generate(COMMUNITY,
			"getcommunities");
	public static final String COMMUNITY_GETCOMMUNITYFRIENDS = generate(
			COMMUNITY, "getcommunityfriends");

	public static final String MESSAGE = "message";
	public static final String MESSAGE_SEND = generate(MESSAGE, "send");
	public static final String MESSAGE_GET = generate(MESSAGE, "get");

	public static final String RELATION = "relation";
	public static final String RELATION_ADDFRIEND = generate(RELATION,
			"addfriend");
	public static final String RELATION_DELETEFRIEND = generate(RELATION,
			"deletefriend");
	public static final String RELATION_BLACKLIST = generate(RELATION,
			"blacklist");
	public static final String RELATION_GETFRIENDS = generate(RELATION,
			"getfriends");
	public static final String RELATION_GETCIRCLESANDFRIENDS = generate(
			RELATION, "getcirclesandfriends");
	public static final String RELATION_GETASKFRIENDS = generate(RELATION,
			"getaskfriends");
	public static final String RELATION_ADDFRIENDAGREE = generate(RELATION,
			"addfriendagree");

	public static final String SESSION = "session";
	public static final String SESSION_EVENT = generate(SESSION, "event");

	public static final String WEBCODE = "webcode";
	public static final String WEBCODE_WEBCODELOGIN = generate(WEBCODE,
			"webcodelogin");

	public static final String IMAGE = "image";
	public static final String IMAGE_UPLOAD = generate(IMAGE, "upload");
	public static final String IMAGE_CHECK = generate(IMAGE, "check");

	public static String generate(String clazz, String method) {
		return clazz + SLASH + method;
	}

	public static String getClazz(String api) {
		return api.substring(0, api.indexOf(SLASH));
	}
}
