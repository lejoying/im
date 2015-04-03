package com.open.welinks.model;

import java.util.HashMap;
import java.util.Map;

import com.open.welinks.R;

public class Constant {
	
	public static void init() {
		DEFAULTFACEMAP = new HashMap<String, Integer>();
		for (int i = 0; i < DEFAULT_FACE_NAMES.length; i++) {
			DEFAULTFACEMAP.put(DEFAULT_FACE_NAMES[i], EMOJIS[i]);
		}
		FACE_RESOURCES_MAP = new HashMap<String, String[]>();
		FACE_RESOURCES_MAP.put("tosiji", TUSIJI_FACE_RESOURCES);
		FACE_RESOURCES_MAP.put("lengtu", LENGTU_FACE_RESOURCES);
		FACE_RESOURCES_MAP.put("ninimao", NINIMAO_FACE_RESOURCES);
		FACE_RESOURCES_MAP.put("feiniaobulu", FEINIAOBULU_FACE_RESOURCES);
		FACE_RESOURCES_MAP.put("donki", DONKI_FACE_RESOURCES);
		FACE_RESOURCES_MAP.put("xiaotumei", XIAOTUMEI_FACE_RESOURCES);
		FACE_RESOURCES_MAP.put("malimalihong", MALIMALIHONG_FACE_RESOURCES);
		FACE_RESOURCES_MAP.put("yangxiaojian", YANGXIAOJIAN_FACE_RESOURCES);
		FACE_RESOURCES_MAP.put("xiongnini", XIONGNINI_FACE_RESOURCES);
		FACE_RESOURCES_MAP.put("chouchoumao", CHOUCHOUMAO_FACE_RESOURCES);
		FACE_RESOURCES_MAP.put("mengleyuan", MENGLEYUAN_FACE_RESOURCES);
		FACE_RESOURCES_MAP.put("tudandan", TUDANDAN_FACE_RESOURCES);
		FACE_RESOURCES_MAP.put("oujisang", OUJISANG_FACE_RESOURCES);
		FACE_RESOURCES_MAP.put("xiaoan", XIAOAN_FACE_RESOURCES);
		FACE_RESOURCES_MAP.put("chouerguang", CHOUERGUANG_FACE_RESOURCES);
		FACE_NAMES_MAP = new HashMap<String, String[]>();
		FACE_NAMES_MAP.put("lengtu", LENGTU_FACE_NAMES);
		FACE_NAMES_MAP.put("ninimao", NINIMAO_FACE_NAMES);
		FACE_NAMES_MAP.put("feiniaobulu", FEINIAOBULU_FACE_NAMES);
		FACE_NAMES_MAP.put("donki", DONKI_FACE_NAMES);
		FACE_NAMES_MAP.put("xiaotumei", XIAOTUMEI_FACE_NAMES);
		FACE_NAMES_MAP.put("malimalihong", MALIMALIHONG_FACE_NAMES);
		FACE_NAMES_MAP.put("yangxiaojian", YANGXIAOJIAN_FACE_NAMES);
		FACE_NAMES_MAP.put("xiongnini", XIONGNINI_FACE_NAMES);
		FACE_NAMES_MAP.put("chouchoumao", CHOUCHOUMAO_FACE_NAMES);
		FACE_NAMES_MAP.put("mengleyuan", MENGLEYUAN_FACE_NAMES);
		FACE_NAMES_MAP.put("tudandan", TUDANDAN_FACE_NAMES);
		FACE_NAMES_MAP.put("oujisang", OUJISANG_FACE_NAMES);
		FACE_NAMES_MAP.put("xiaoan", XIAOAN_FACE_NAMES);
		FACE_NAMES_MAP.put("chouerguang", CHOUERGUANG_FACE_NAMES);
	}

	// message sendType
	public static int MESSAGE_TYPE_SEND = 0x01;
	public static int MESSAGE_TYPE_RECEIVE = 0x02;

	// faceRegx
	public static String FACEREGX = "[\\[,<]{1}[\u4E00-\u9FFF]{1,5}[\\],>]{1}|[\\[,<]{1}[a-zA-Z0-9]{1,5}[\\],>]{1}";
	// tableId
	public static String ACCOUNTTABLEID = "53eacbe4e4b0693fbf5fd13b";
	public static String GROUPTABLEID = "53eacbb9e4b0693fbf5fd0f6";
	public static String SQUARETABLEID = "54101cade4b0dfd37f863ace";
	public static String SHARETABLEID = "54f520e3e4b0ff22e1fc52d3";
	// lbs key
//	public static String LBS_KSY = "7b7b0483c25df5414ba05d81957dac5a";// old key ： 32b48639b260edd1916960614151eec3
	public static String LBS_SAVE_KSY = "0cd819a62c50d40b75a73f66cb14aa06";

	// wechat id
	public static String WECHAT_ADDID = "wx801364850e8a09dc";// "wx801364850e8a09dc" || "wxbbf27e2f87ef9083";
	// square sid
	public static String SQUARE_SID = "5359";
	/**
	 * defaultCircleId = 8888888
	 */
	public static long DEFAULTCIRCLEID = 8888888;
	/**
	 * defaultContactId = 9999999
	 */
	public static long defaultContactId = 9999999;

	public static String DEFAULTGROUPCIRCLE = "8888888";

	// faces
	public static String[] FACES = { "tosiji", "lengtu", "ninimao", "feiniaobulu", "donki", "xiaotumei", "chouerguang", "xiaoan", "oujisang", "tudandan", "mengleyuan", "chouchoumao", "malimalihong", "yangxiaojian", "xiongnini" };
	public static String[] DEFAULT_FACE_NAMES = { "[呲牙]", "[微笑]", "[大笑]", "[墨镜]", "[捂嘴]", "[亲亲]", "[抠鼻]", "[蔑视]", "[惊讶]", "[疑问]", "[可怜]", "[害羞]", "[出汗]", "[撇嘴]", "[无奈]", "[鼓掌]", "[生气]", "[大哭]", "[快哭了]", "[闭嘴]", "[嘘]", "[再见]", "[叹气]", "[吐舌]", "[挥手]", "[板砖]", "[困]", "[哈欠]", "[色]", "[吐]", "[吐血]", "[爆汗]", "[奋斗]", "[咒骂]", "[可爱]", "[鄙视]", "[钱]", "[吃]", "[迷糊]", "[无聊]", "[嘚瑟]", "[左哼哼]", "[右哼哼]", "[委屈]", "[撅嘴]", "[发狂]", "[阴险]", "[坏笑]", "[OK]", "[胜利]", "[过来]", "[赞]", "[踩]", "[握手]", "[路过]", "[大便]", "[魔鬼]", "[狂怒]", "[天使]", "[幽灵]", "[爱心]", "[心碎]", "[汽车]", "[蛋糕]", "[礼物]", "[月亮]", "[玫瑰]", "[太阳]", "[炸弹]", "[酒]", "[食物]", "[面条]", "[猪]", "[嘴]", "[拥抱]", "[抱拳]" };
	public static Integer[] EMOJIS = { R.drawable.e0, R.drawable.e1, R.drawable.e2, R.drawable.e3, R.drawable.e4, R.drawable.e5, R.drawable.e6, R.drawable.e7, R.drawable.e8, R.drawable.e9, R.drawable.e10, R.drawable.e11, R.drawable.e12, R.drawable.e13, R.drawable.e14, R.drawable.e15, R.drawable.e16, R.drawable.e17, R.drawable.e18, R.drawable.e19, R.drawable.e20, R.drawable.e21, R.drawable.e22, R.drawable.e23, R.drawable.e24, R.drawable.e25, R.drawable.e26, R.drawable.e27, R.drawable.e28, R.drawable.e29, R.drawable.e30, R.drawable.e31, R.drawable.e32, R.drawable.e33, R.drawable.e34, R.drawable.e35, R.drawable.e36, R.drawable.e37, R.drawable.e38, R.drawable.e39, R.drawable.e40, R.drawable.e41, R.drawable.e42, R.drawable.e43, R.drawable.e44, R.drawable.e45, R.drawable.e46, R.drawable.e47,
			R.drawable.e48, R.drawable.e49, R.drawable.e50, R.drawable.e51, R.drawable.e52, R.drawable.e53, R.drawable.e54, R.drawable.e55, R.drawable.e56, R.drawable.e57, R.drawable.e58, R.drawable.e59, R.drawable.e60, R.drawable.e61, R.drawable.e62, R.drawable.e63, R.drawable.e64, R.drawable.e65, R.drawable.e66, R.drawable.e67, R.drawable.e68, R.drawable.e69, R.drawable.e70, R.drawable.e71, R.drawable.e72, R.drawable.e73, R.drawable.e74, R.drawable.e75 };
	// public static Integer[] BIG_EMOJIS = { R.drawable.e0_big, R.drawable.e1_big, R.drawable.e2_big, R.drawable.e3_big, R.drawable.e4_big, R.drawable.e5_big, R.drawable.e6_big, R.drawable.e7_big, R.drawable.e8_big, R.drawable.e9_big, R.drawable.e10_big, R.drawable.e11_big, R.drawable.e12_big, R.drawable.e13_big, R.drawable.e14_big, R.drawable.e15_big, R.drawable.e16_big, R.drawable.e17_big, R.drawable.e18_big, R.drawable.e19_big, R.drawable.e20_big, R.drawable.e21_big, R.drawable.e22_big, R.drawable.e23_big, R.drawable.e24_big, R.drawable.e25_big, R.drawable.e26_big, R.drawable.e27_big, R.drawable.e28_big, R.drawable.e29_big, R.drawable.e30_big, R.drawable.e31_big, R.drawable.e32_big, R.drawable.e33_big, R.drawable.e34_big, R.drawable.e35_big, R.drawable.e36_big, R.drawable.e37_big,
	// R.drawable.e38_big, R.drawable.e39_big, R.drawable.e40_big, R.drawable.e41_big, R.drawable.e42_big, R.drawable.e43_big, R.drawable.e44_big, R.drawable.e45_big, R.drawable.e46_big, R.drawable.e47_big, R.drawable.e48_big, R.drawable.e49_big, R.drawable.e50_big, R.drawable.e51_big, R.drawable.e52_big, R.drawable.e53_big, R.drawable.e54_big, R.drawable.e55_big, R.drawable.e56_big, R.drawable.e57_big, R.drawable.e58_big, R.drawable.e59_big, R.drawable.e60_big, R.drawable.e61_big, R.drawable.e62_big, R.drawable.e63_big, R.drawable.e64_big, R.drawable.e65_big, R.drawable.e66_big, R.drawable.e67_big, R.drawable.e68_big, R.drawable.e69_big, R.drawable.e70_big, R.drawable.e71_big, R.drawable.e72_big, R.drawable.e73_big, R.drawable.e74_big, R.drawable.e75_big };
	public static String[] TUSIJI_FACE_RESOURCES = { "tusiji_1.osg", "tusiji_2.osg", "tusiji_3.osg", "tusiji_4.osg", "tusiji_5.osg", "tusiji_6.osg", "tusiji_7.osg", "tusiji_8.osg", "tusiji_9.osg", "tusiji_10.osg", "tusiji_11.osg", "tusiji_12.osg", "tusiji_13.osg", "tusiji_14.osg", "tusiji_15.osg", "tusiji_16.osg" };
	public static String[] LENGTU_FACE_RESOURCES = { "lengtu_1.osg", "lengtu_2.osg", "lengtu_3.osg", "lengtu_4.osg", "lengtu_5.osg", "lengtu_6.osg", "lengtu_7.osg", "lengtu_8.osg", "lengtu_9.osg", "lengtu_10.osg", "lengtu_11.osg", "lengtu_12.osg", "lengtu_13.osg", "lengtu_14.osg", "lengtu_15.osg", "lengtu_16.osg", "lengtu_17.osg", "lengtu_18.osg", "lengtu_19.osg", "lengtu_20.osg", "lengtu_21.osg", "lengtu_22.osg", "lengtu_23.osg", "lengtu_24.osg", "lengtu_25.osg", "lengtu_26.osg", "lengtu_27.osg", "lengtu_28.osg", "lengtu_29.osg", "lengtu_30.osg" };
	public static String[] NINIMAO_FACE_RESOURCES = { "ninimao_1.osg", "ninimao_2.osg", "ninimao_3.osg", "ninimao_4.osg", "ninimao_5.osg", "ninimao_6.osg", "ninimao_7.osg", "ninimao_8.osg", "ninimao_9.osg", "ninimao_10.osg", "ninimao_11.osg", "ninimao_12.osg", "ninimao_13.osg", "ninimao_14.osg", "ninimao_15.osg", "ninimao_16.osg", "ninimao_17.osg", "ninimao_18.osg", "ninimao_19.osg", "ninimao_20.osg", "ninimao_21.osg", "ninimao_22.osg", "ninimao_23.osg", "ninimao_24.osg", "ninimao_25.osg", "ninimao_26.osg", "ninimao_27.osg", "ninimao_28.osg", "ninimao_29.osg", "ninimao_30.osg", "ninimao_31.osg", "ninimao_32.osg" };
	public static String[] FEINIAOBULU_FACE_RESOURCES = { "feiniaobulu_1.osg", "feiniaobulu_2.osg", "feiniaobulu_3.osg", "feiniaobulu_4.osg", "feiniaobulu_5.osg", "feiniaobulu_6.osg", "feiniaobulu_7.osg", "feiniaobulu_8.osg", "feiniaobulu_9.osg", "feiniaobulu_10.osg", "feiniaobulu_11.osg", "feiniaobulu_12.osg", "feiniaobulu_13.osg", "feiniaobulu_14.osg", "feiniaobulu_15.osg", "feiniaobulu_16.osg", "feiniaobulu_17.osg", "feiniaobulu_18.osg", "feiniaobulu_19.osg", "feiniaobulu_20.osg", "feiniaobulu_21.osg", "feiniaobulu_22.osg", "feiniaobulu_23.osg", "feiniaobulu_24.osg", "feiniaobulu_25.osg", "feiniaobulu_26.osg", "feiniaobulu_27.osg", "feiniaobulu_28.osg", "feiniaobulu_29.osg", "feiniaobulu_30.osg", "feiniaobulu_31.osg", "feiniaobulu_32.osg" };
	public static String[] DONKI_FACE_RESOURCES = { "donki_1.osg", "donki_2.osg", "donki_3.osg", "donki_4.osg", "donki_5.osg", "donki_6.osg", "donki_7.osg", "donki_8.osg", "donki_9.osg", "donki_10.osg", "donki_11.osg", "donki_12.osg", "donki_13.osg", "donki_14.osg", "donki_15.osg", "donki_16.osg", "donki_17.osg", "donki_18.osg", "donki_19.osg", "donki_20.osg", "donki_21.osg", "donki_22.osg", "donki_23.osg", "donki_24.osg", "donki_25.osg", "donki_26.osg", "donki_27.osg", "donki_28.osg", "donki_29.osg", "donki_30.osg" };
	public static String[] XIAOTUMEI_FACE_RESOURCES = { "xiaotumei_1.osg", "xiaotumei_2.osg", "xiaotumei_3.osg", "xiaotumei_4.osg", "xiaotumei_5.osg", "xiaotumei_6.osg", "xiaotumei_7.osg", "xiaotumei_8.osg", "xiaotumei_9.osg", "xiaotumei_10.osg", "xiaotumei_11.osg", "xiaotumei_12.osg", "xiaotumei_13.osg", "xiaotumei_14.osg", "xiaotumei_15.osg", "xiaotumei_16.osg", "xiaotumei_17.osg", "xiaotumei_18.osg", "xiaotumei_19.osg", "xiaotumei_20.osg", "xiaotumei_21.osg", "xiaotumei_22.osg", "xiaotumei_23.osg", "xiaotumei_24.osg", "xiaotumei_25.osg", "xiaotumei_26.osg", "xiaotumei_27.osg", "xiaotumei_28.osg", "xiaotumei_29.osg", "xiaotumei_30.osg" };
	public static String[] MALIMALIHONG_FACE_RESOURCES = { "malimalihong_1.osg", "malimalihong_2.osg", "malimalihong_3.osg", "malimalihong_4.osg", "malimalihong_5.osg", "malimalihong_6.osg", "malimalihong_7.osg", "malimalihong_8.osg", "malimalihong_9.osg", "malimalihong_10.osg", "malimalihong_11.osg", "malimalihong_12.osg", "malimalihong_13.osg", "malimalihong_14.osg", "malimalihong_15.osg", "malimalihong_16.osg", "malimalihong_17.osg", "malimalihong_18.osg", "malimalihong_19.osg", "malimalihong_20.osg", "malimalihong_21.osg", "malimalihong_22.osg", "malimalihong_23.osg", "malimalihong_24.osg", "malimalihong_25.osg", "malimalihong_26.osg", "malimalihong_27.osg", "malimalihong_28.osg", "malimalihong_29.osg", "malimalihong_30.osg" };
	public static String[] YANGXIAOJIAN_FACE_RESOURCES = { "yangxiaojian_1.osg", "yangxiaojian_2.osg", "yangxiaojian_3.osg", "yangxiaojian_4.osg", "yangxiaojian_5.osg", "yangxiaojian_6.osg", "yangxiaojian_7.osg", "yangxiaojian_8.osg", "yangxiaojian_9.osg", "yangxiaojian_10.osg", "yangxiaojian_11.osg", "yangxiaojian_12.osg", "yangxiaojian_13.osg", "yangxiaojian_14.osg", "yangxiaojian_15.osg", "yangxiaojian_16.osg", "yangxiaojian_17.osg", "yangxiaojian_18.osg", "yangxiaojian_19.osg", "yangxiaojian_20.osg", "yangxiaojian_21.osg", "yangxiaojian_22.osg", "yangxiaojian_23.osg", "yangxiaojian_24.osg", "yangxiaojian_25.osg", "yangxiaojian_26.osg", "yangxiaojian_27.osg", "yangxiaojian_28.osg", "yangxiaojian_29.osg", "yangxiaojian_30.osg" };
	public static String[] XIONGNINI_FACE_RESOURCES = { "xiongnini_1.osg", "xiongnini_2.osg", "xiongnini_3.osg", "xiongnini_4.osg", "xiongnini_5.osg", "xiongnini_6.osg", "xiongnini_7.osg", "xiongnini_8.osg", "xiongnini_9.osg", "xiongnini_10.osg", "xiongnini_11.osg", "xiongnini_12.osg", "xiongnini_13.osg", "xiongnini_14.osg", "xiongnini_15.osg", "xiongnini_16.osg", "xiongnini_17.osg", "xiongnini_18.osg", "xiongnini_19.osg", "xiongnini_20.osg", "xiongnini_21.osg", "xiongnini_22.osg", "xiongnini_23.osg", "xiongnini_24.osg", "xiongnini_25.osg", "xiongnini_26.osg", "xiongnini_27.osg" };
	public static String[] CHOUCHOUMAO_FACE_RESOURCES = { "chouchoumao_1.osg", "chouchoumao_2.osg", "chouchoumao_3.osg", "chouchoumao_4.osg", "chouchoumao_5.osg", "chouchoumao_6.osg", "chouchoumao_7.osg", "chouchoumao_8.osg", "chouchoumao_9.osg", "chouchoumao_10.osg", "chouchoumao_11.osg", "chouchoumao_12.osg", "chouchoumao_13.osg", "chouchoumao_14.osg", "chouchoumao_15.osg", "chouchoumao_16.osg", "chouchoumao_17.osg", "chouchoumao_18.osg", "chouchoumao_19.osg", "chouchoumao_20.osg", "chouchoumao_21.osg", "chouchoumao_22.osg", "chouchoumao_23.osg", "chouchoumao_24.osg", "chouchoumao_25.osg" };
	public static String[] MENGLEYUAN_FACE_RESOURCES = { "mengleyuan_1.osg", "mengleyuan_2.osg", "mengleyuan_3.osg", "mengleyuan_4.osg", "mengleyuan_5.osg", "mengleyuan_6.osg", "mengleyuan_7.osg", "mengleyuan_8.osg", "mengleyuan_9.osg", "mengleyuan_10.osg", "mengleyuan_11.osg", "mengleyuan_12.osg", "mengleyuan_13.osg", "mengleyuan_14.osg", "mengleyuan_15.osg", "mengleyuan_16.osg", "mengleyuan_17.osg", "mengleyuan_18.osg", "mengleyuan_19.osg", "mengleyuan_20.osg", "mengleyuan_21.osg", "mengleyuan_22.osg" };
	public static String[] TUDANDAN_FACE_RESOURCES = { "tudandan_1.osg", "tudandan_2.osg", "tudandan_3.osg", "tudandan_4.osg", "tudandan_5.osg", "tudandan_6.osg", "tudandan_7.osg", "tudandan_8.osg", "tudandan_9.osg", "tudandan_10.osg", "tudandan_11.osg", "tudandan_12.osg", "tudandan_13.osg", "tudandan_14.osg", "tudandan_15.osg", "tudandan_16.osg", "tudandan_17.osg", "tudandan_18.osg", "tudandan_19.osg", "tudandan_20.osg" };
	public static String[] OUJISANG_FACE_RESOURCES = { "oujisang_1.osg", "oujisang_2.osg", "oujisang_3.osg", "oujisang_4.osg", "oujisang_5.osg", "oujisang_6.osg", "oujisang_7.osg", "oujisang_8.osg", "oujisang_9.osg", "oujisang_10.osg", "oujisang_11.osg", "oujisang_12.osg", "oujisang_13.osg", "oujisang_14.osg", "oujisang_15.osg" };
	public static String[] XIAOAN_FACE_RESOURCES = { "xiaoan_1.osg", "xiaoan_2.osg", "xiaoan_3.osg", "xiaoan_4.osg", "xiaoan_5.osg", "xiaoan_6.osg", "xiaoan_7.osg", "xiaoan_8.osg", "xiaoan_9.osg", "xiaoan_10.osg", "xiaoan_11.osg", "xiaoan_12.osg", "xiaoan_13.osg" };
	public static String[] CHOUERGUANG_FACE_RESOURCES = { "chouerguang_1.osg", "chouerguang_2.osg", "chouerguang_3.osg", "chouerguang_4.osg", "chouerguang_5.osg", "chouerguang_6.osg", "chouerguang_7.osg", "chouerguang_8.osg", "chouerguang_9.osg", "chouerguang_10.osg", "chouerguang_11.osg", "chouerguang_12.osg", "chouerguang_13.osg", "chouerguang_14.osg", "chouerguang_15.osg", "chouerguang_16.osg", "chouerguang_17.osg", "chouerguang_18.osg", "chouerguang_19.osg", "chouerguang_20.osg", "chouerguang_21.osg" };
	public static String[] LENGTU_FACE_NAMES = { "打狗", "吃饭", "不爽", "抽烟", "吃零食", "打酱油", "DJ", "放假", "负分滚出", "给跪", "鬼鬼祟祟", "呵呵呵", "滑冰", "疯了", "画圈圈", "火钳刘", "点头", "开饭了", "哭", "抛媚眼", "潜水", "敲手鼓", "撒花", "跳舞", "正步走", "调戏", "震惊", "悠闲", "沙发", "无语" };
	public static String[] NINIMAO_FACE_NAMES = { "害羞", "拖走", "感动", "依靠", "催眠", "抓狂", "哈哈", "画圈圈", "嘚瑟", "今天星期五", "结冰", "纠结", "好乖", "惊讶", "流鼻血", "求妹纸", "冷", "无语", "脸红", "流汗", "跳", "委屈", "痛哭", "不", "我打", "我的天啊", "想哭", "晕", "可怜", "我捏", "装酷", "拜拜" };
	public static String[] FEINIAOBULU_FACE_NAMES = { "出来聊天", "请多关照", "记下罪名", "别闹", "新人报三围", "新人发照", "楼上流氓", "楼下流氓", "弹到死", "此人猥琐", "给跪了", "不忍直视", "关灯", "捡肥皂", "雅蠛蝶", "蜀黍查身体", "抱大腿", "踩群主", "群主发福利", "群主跳舞", "吹牛", "怒赞", "你们在聊神马", "去年买了个表", "留下肿么看", "中箭无数", "推到", "晚安", "围观", "笑cry", "天王盖地虎", "小鸡炖蘑菇" };
	public static String[] DONKI_FACE_NAMES = { "闭嘴", "吃了", "大哭", "打喷嚏", "发呆", "发烧", "尴尬", "好开心", "好可怕", "好色", "坏巫婆", "就是高兴", "可爱僵尸", "酷", "狂喜", "冷汗", "撇嘴", "翘屁股", "去喘吁吁", "睡觉", "狂怒", "太伤心", "吐", "玩疯了", "微笑", "小宝宝", "跳舞", "痒啊痒", "晕了", "抓狂" };
	public static String[] XIAOTUMEI_FACE_NAMES = { "骑马", "唱歌", "吃饱", "逗逗", "烦躁", "鬼脸", "害羞", "画圈", "挥手", "奸笑", "骄傲", "惊呆了", "拉便便", "泪崩", "骂人", "拍手", "切菜", "亲亲", "撒花", "示爱", "睡觉", "偷听", "偷笑", "吐舌", "挖鼻", "委屈", "星星", "鸭梨大", "招财", "走你" };
	public static String[] MALIMALIHONG_FACE_NAMES = { "抱头痛哭", "本大爷", "不想起床", "不想上班", "差劲", "累觉不爱", "打滚", "嘚瑟", "抖腿", "放屁", "尴尬", "哈哈", "害怕", "好无聊", "我来了", "急", "惊讶", "沮丧", "快干活", "快回话", "楼下禽兽", "没人鸟我", "你不爱我", "求桃花", "生气", "委屈", "我萌吗", "掀桌", "悠闲", "咒骂" };
	public static String[] YANGXIAOJIAN_FACE_NAMES = { "淡定喝茶", "抖腿", "吃", "动起来", "发抖", "钢琴家", "勾引", "孤独寂寞冷", "回眸一笑", "去他的", "失落", "来捡肥皂", "鼓掌", "讨厌", "戳便便", "晃一晃", "路过", "我要掐死你", "中箭", "哭", "撞墙", "惊倒", "捅楼上", "起不来", "跳绳", "吓一跳", "疑问", "撞晕了", "无聊", "我顶" };
	public static String[] XIONGNINI_FACE_NAMES = { "爱你", "安慰", "不满", "不想洗澡", "吃饭", "抽丫的", "勾引", "鼓掌", "嗨起来", "好多作业", "可怜兮兮", "哭", "领队", "粘脚了", "生病", "失眠", "天使哭", "投降", "玩电脑", "围观", "无聊", "笑", "再见", "抓狂", "休息", "震惊", "赞" };
	public static String[] CHOUCHOUMAO_FACE_NAMES = { "扯裤裤", "淡定", "蛋疼", "道别", "发呆", "超赞", "嘎嘎", "功夫", "鬼脸", "汗颜", "坏笑", "惊呆", "开心", "抠鼻", "哭泣", "你好", "呕吐", "欧耶", "亲亲", "乞讨", "色色", "生气", "歪脖", "兴奋", "疑问" };
	public static String[] MENGLEYUAN_FACE_NAMES = { "憨笑", "不可原谅", "赞同", "大王息怒", "卖瓜", "发呆", "不满", "拒绝", "离家出走", "热屎了", "开饭喽", "你太嫩", "亲亲", "挖坑埋你", "吃西瓜", "老大有请", "摇屁屁", "完败", "有种PK", "上厕所", "找帅哥", "生病" };
	public static String[] TUDANDAN_FACE_NAMES = { "白眼", "该吃药了", "捶胸", "打击", "放屁", "给我票", "鬼脸", "花痴", "惊吓", "鞠躬", "瞌睡", "哭泣", "求票", "圣诞快乐", "圣诞礼物", "示爱", "思考", "吐血", "晕", "装可怜" };
	public static String[] OUJISANG_FACE_NAMES = { "开心", "可怜", "跑", "蹭", "痛哭", "跳", "蹦", "扭腰", "笑", "弹吉他", "求打包", "摇头", "中箭", "哭", "勒裤衩" };
	public static String[] XIAOAN_FACE_NAMES = { "打招呼", "变脸", "草裙舞", "吃货", "恭喜", "喝奶", "惊愕", "酷", "潜水", "撒娇", "委屈", "无聊", "心碎" };
	public static String[] CHOUERGUANG_FACE_NAMES = { "别想回家", "美图说个P", "抽丫的", "改图", "滚犊子", "啊西吧", "不装会死", "扔你嘚瑟", "让你偷懒", "装13没朋友", "你能怎样我", "为何这么吊", "打你怎么滴", "去死吧", "别告老师啊", "我让你吊", "你告老师啊", "闭嘴", "嫩娘", "山炮", "biang的" };

	public static String[] LABELS = { "吃货", "唱歌", "自拍", "读书", "音乐", "电影", "奢侈品", "美妆", "瑜伽", "游泳", "健身", "星座", "游戏", "动漫", "LOL", "纹身", "汪星人", "喵星人", "文艺", "书法", "摄影", "绘画", "Dota", "DIY", "军事", "篮球", "足球", "羽毛球", "乒乓球", "跑步", "户外" };

	public static Map<String, String[]> FACE_RESOURCES_MAP;
	public static Map<String, String[]> FACE_NAMES_MAP;
	public static Map<String, Integer> DEFAULTFACEMAP;
}
