package com.open.welinks.model;

import java.util.HashMap;
import java.util.Map;

import com.open.welinks.R;

public class Constant {
	public static void init() {
		DEFAULTFACEMAP = new HashMap<String, Integer>();
		for (int i = 0; i < DEFAULT_FACE_NAMES.length; i++) {
			DEFAULTFACEMAP.put(DEFAULT_FACE_NAMES[i], BIG_EMOJIS[i]);
		}
		FACE_NAMES_MAP = new HashMap<String, String[]>();
		FACE_NAMES_MAP.put("tosiji", TUSIJI_FACE_NAMES);
	}

	// message sendType
	public static int MESSAGE_TYPE_SEND = 0x01;
	public static int MESSAGE_TYPE_RECEIVE = 0x02;
	// handler
	public static final int HANDLER_CHAT_NOTIFY = 0x11;
	public static final int HANDLER_CHAT_HIDEVOICEPOP = 0x12;
	public static final int HANDLER_CHAT_STARTPLAY = 0x13;
	public static final int HANDLER_CHAT_STOPPLAY = 0x14;
	// requestCode
	public static final int REQUESTCODE_ABLUM = 0x21;
	public static final int REQUESTCODE_TAKEPHONE = 0x22;
	public static final int REQUESTCODE_TAKEPHOTO = 0x23;
	// faceRegx
	public static String FACEREGX = "[\\[,<]{1}[\u4E00-\u9FFF]{1,5}[\\],>]{1}|[\\[,<]{1}[a-zA-Z0-9]{1,5}[\\],>]{1}";
	// tableId
	public static String ACCOUNTTABLEID = "53eacbe4e4b0693fbf5fd13b";
	public static String GROUPTABLEID = "53eacbb9e4b0693fbf5fd0f6";
	public static String SQUARETABLEID = "54101cade4b0dfd37f863ace";
	// lbs key
	public static String LBS_KSY = "7b7b0483c25df5414ba05d81957dac5a";// old key ： 32b48639b260edd1916960614151eec3
	public static String LBS_SAVE_KSY = "0cd819a62c50d40b75a73f66cb14aa06";

	// wechat id
	public static String WECHAT_ADDID = "wx801364850e8a09dc";// "wxbbf27e2f87ef9083";

	/**
	 * defaultCircleId = 8888888
	 */
	public static long DEFAULTCIRCLEID = 8888888;
	/**
	 * defaultContactId = 9999999
	 */
	public static long defaultContactId = 9999999;

	public static String[] DEFAULT_FACE_NAMES = { "[呲牙]", "[微笑]", "[大笑]", "[墨镜]", "[捂嘴]", "[亲亲]", "[抠鼻]", "[蔑视]", "[惊讶]", "[疑问]", "[可怜]", "[害羞]", "[出汗]", "[撇嘴]", "[无奈]", "[鼓掌]", "[生气]", "[大哭]", "[快哭了]", "[闭嘴]", "[嘘]", "[再见]", "[叹气]", "[吐舌]", "[挥手]", "[板砖]", "[困]", "[哈欠]", "[色]", "[吐]", "[吐血]", "[爆汗]", "[奋斗]", "[咒骂]", "[可爱]", "[鄙视]", "[钱]", "[吃]", "[迷糊]", "[无聊]", "[嘚瑟]", "[左哼哼]", "[右哼哼]", "[委屈]", "[撅嘴]", "[发狂]", "[阴险]", "[坏笑]", "[OK]", "[胜利]", "[过来]", "[赞]", "[踩]", "[握手]", "[路过]", "[大便]", "[魔鬼]", "[狂怒]", "[天使]", "[幽灵]", "[爱心]", "[心碎]", "[汽车]", "[蛋糕]", "[礼物]", "[月亮]", "[玫瑰]", "[太阳]", "[炸弹]", "[酒]", "[食物]", "[面条]", "[猪]", "[嘴]", "[拥抱]", "[抱拳]" };
	public static Integer[] EMOJIS = { R.drawable.e0, R.drawable.e1, R.drawable.e2, R.drawable.e3, R.drawable.e4, R.drawable.e5, R.drawable.e6, R.drawable.e7, R.drawable.e8, R.drawable.e9, R.drawable.e10, R.drawable.e11, R.drawable.e12, R.drawable.e13, R.drawable.e14, R.drawable.e15, R.drawable.e16, R.drawable.e17, R.drawable.e18, R.drawable.e19, R.drawable.e20, R.drawable.e21, R.drawable.e22, R.drawable.e23, R.drawable.e24, R.drawable.e25, R.drawable.e26, R.drawable.e27, R.drawable.e28, R.drawable.e29, R.drawable.e30, R.drawable.e31, R.drawable.e32, R.drawable.e33, R.drawable.e34, R.drawable.e35, R.drawable.e36, R.drawable.e37, R.drawable.e38, R.drawable.e39, R.drawable.e40, R.drawable.e41, R.drawable.e42, R.drawable.e43, R.drawable.e44, R.drawable.e45, R.drawable.e46, R.drawable.e47,
			R.drawable.e48, R.drawable.e49, R.drawable.e50, R.drawable.e51, R.drawable.e52, R.drawable.e53, R.drawable.e54, R.drawable.e55, R.drawable.e56, R.drawable.e57, R.drawable.e58, R.drawable.e59, R.drawable.e60, R.drawable.e61, R.drawable.e62, R.drawable.e63, R.drawable.e64, R.drawable.e65, R.drawable.e66, R.drawable.e67, R.drawable.e68, R.drawable.e69, R.drawable.e70, R.drawable.e71, R.drawable.e72, R.drawable.e73, R.drawable.e74, R.drawable.e75 };
	public static Integer[] BIG_EMOJIS = { R.drawable.e0_big, R.drawable.e1_big, R.drawable.e2_big, R.drawable.e3_big, R.drawable.e4_big, R.drawable.e5_big, R.drawable.e6_big, R.drawable.e7_big, R.drawable.e8_big, R.drawable.e9_big, R.drawable.e10_big, R.drawable.e11_big, R.drawable.e12_big, R.drawable.e13_big, R.drawable.e14_big, R.drawable.e15_big, R.drawable.e16_big, R.drawable.e17_big, R.drawable.e18_big, R.drawable.e19_big, R.drawable.e20_big, R.drawable.e21_big, R.drawable.e22_big, R.drawable.e23_big, R.drawable.e24_big, R.drawable.e25_big, R.drawable.e26_big, R.drawable.e27_big, R.drawable.e28_big, R.drawable.e29_big, R.drawable.e30_big, R.drawable.e31_big, R.drawable.e32_big, R.drawable.e33_big, R.drawable.e34_big, R.drawable.e35_big, R.drawable.e36_big, R.drawable.e37_big,
			R.drawable.e38_big, R.drawable.e39_big, R.drawable.e40_big, R.drawable.e41_big, R.drawable.e42_big, R.drawable.e43_big, R.drawable.e44_big, R.drawable.e45_big, R.drawable.e46_big, R.drawable.e47_big, R.drawable.e48_big, R.drawable.e49_big, R.drawable.e50_big, R.drawable.e51_big, R.drawable.e52_big, R.drawable.e53_big, R.drawable.e54_big, R.drawable.e55_big, R.drawable.e56_big, R.drawable.e57_big, R.drawable.e58_big, R.drawable.e59_big, R.drawable.e60_big, R.drawable.e61_big, R.drawable.e62_big, R.drawable.e63_big, R.drawable.e64_big, R.drawable.e65_big, R.drawable.e66_big, R.drawable.e67_big, R.drawable.e68_big, R.drawable.e69_big, R.drawable.e70_big, R.drawable.e71_big, R.drawable.e72_big, R.drawable.e73_big, R.drawable.e74_big, R.drawable.e75_big };
	public static String[] TUSIJI_FACE_NAMES = { "tusiji_1.osg", "tusiji_2.osg", "tusiji_3.osg", "tusiji_4.osg", "tusiji_5.osg", "tusiji_6.osg", "tusiji_7.osg", "tusiji_8.osg", "tusiji_9.osg", "tusiji_10.osg", "tusiji_11.osg", "tusiji_12.osg", "tusiji_13.osg", "tusiji_14.osg", "tusiji_15.osg", "tusiji_16.osg" };
	public static Map<String, String[]> FACE_NAMES_MAP;
	public static Map<String, Integer> DEFAULTFACEMAP;
}
