package com.lejoying.mc.api;

public interface API {
	public static final int ACCOUNT_VERIFYPHONE = 0x001;
	public static final int ACCOUNT_VERIFYCODE = 0x002;
	public static final int ACCOUNT_AUTH = 0x003;
	public static final int ACCOUNT_GET = 0x004;
	public static final int ACCOUNT_MODIFY = 0x005;
	public static final int ACCOUNT_EXIT = 0x006;

	public static final int CIRCLE_MODIFY = 0x101;
	public static final int CIRCLE_DELETE = 0x102;

	public static final int COMMUNITY_FIND = 0x201;
	public static final int COMMUNITY_JOIN = 0x202;
	public static final int COMMUNITY_UNJOIN = 0x203;
	public static final int COMMUNITY_GETCOMMUNITIES = 0x204;
	public static final int COMMUNITY_GETCOMMUNITYFRIENDS = 0x205;

	public static final int MESSAGE_SEND = 0x301;
	public static final int MESSAGE_GET = 0x302;

	public static final int RELATION_ADDFRIEND = 0x401;
	public static final int RELATION_GETFRIENDS = 0x402;
	public static final int RELATION_ADDCIRCLE = 0x403;
	public static final int RELATION_GETCOMMUNITIES = 0x404;
	public static final int RELATION_GETCIRCLESANDFRIENDS = 0x405;
	public static final int RELATION_GETASKFRIENDS = 0x406;
	public static final int RELATION_ADDFRIENDAGREE = 0x407;

	public static final int SESSION_EVENTWEB = 0x501;
	public static final int SESSION_EVENT = 0x502;
	
	public static final int WEBCODE_WEBCODELOGIN = 0x6
}
