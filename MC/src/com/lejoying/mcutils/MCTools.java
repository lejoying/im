package com.lejoying.mcutils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.StreamCorruptedException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.os.Handler;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.lejoying.listener.ResponseListener;
import com.lejoying.utils.HttpTools;
import com.lejoying.utils.HttpTools.HttpListener;
import com.lejoying.utils.LocationTools;
import com.lejoying.utils.StreamTools;

public class MCTools {

	private static Account nowAccount;

	public static boolean INNEWCOMMUNITY = false;

	private static Community NOWCOMMUNITY;

	private static List<Friend> CHAT_FRIENDS;

	private static List<Friend> newFriends;

	public static Handler handler = new Handler();

	private static final String DOMAIN = "http://192.168.0.104:8071";

	private static String lasturl;
	private static Map<String, String> lastparam;
	private static long lasttime;
	
	private final static int QRCODE_SIZE = 300;

	public static void ajax(Activity activity, final String url,
			final Map<String, String> param, boolean lock, final int method,
			final int timeout, final ResponseListener responseListener) {
		boolean hasNetwork = HttpTools.hasNetwork(activity);

		if (lock) {
			if ((url.equals(lasturl) && param.equals(lastparam))
					&& new Date().getTime() - lasttime < 5000) {
				return;
			}
		}
		lasturl = url;
		lastparam = param;
		lasttime = new Date().getTime();

		if (!hasNetwork) {
			responseListener.noInternet();
		} else {
			new Thread() {
				@Override
				public void run() {
					super.run();
					HttpListener httpListener = new HttpListener() {

						@Override
						public void handleInputStream(InputStream is) {
							try {
								if (is != null) {
									byte[] b = StreamTools.isToData(is);
									final JSONObject data = new JSONObject(
											new String(b));
									if (data != null) {
										String info = data.getString("提示信息");
										info = info.substring(
												info.length() - 2,
												info.length());

										if (info.equals("成功")) {
											handler.post(new Runnable() {
												@Override
												public void run() {
													responseListener
															.success(data);
												}
											});
										}
										if (info.equals("失败")) {
											handler.post(new Runnable() {
												@Override
												public void run() {
													responseListener
															.unsuccess(data);
												}
											});
										}
									}
								}
								if (is == null) {
									handler.post(new Runnable() {
										@Override
										public void run() {
											responseListener.failed();
										}
									});
								}
							} catch (JSONException e) {
								e.printStackTrace();
							}
						}
					};
					if (method == HttpTools.SEND_GET) {
						HttpTools.sendGet(DOMAIN + url, timeout, param,
								httpListener);
					}
					if (method == HttpTools.SEND_POST) {
						HttpTools.sendPost(DOMAIN + url, timeout, param,
								httpListener);
					}
				}
			}.start();
		}
	}

	public static Map<String, String> getParamsWithLocation(Activity activity) {
		double[] location = LocationTools.getLocation(activity);
		Map<String, String> map = new HashMap<String, String>();
		map.put("latitude", String.valueOf(location[1]));
		map.put("longitude", String.valueOf(location[0]));
		return map;
	}

	public static List<Friend> getNewFriends() {
		return newFriends;
	}

	public static Community getNOWCOMMUNITY() {
		return NOWCOMMUNITY;
	}

	public static void setNOWCOMMUNITY(Community nOWCOMMUNITY) {
		NOWCOMMUNITY = nOWCOMMUNITY;
	}

	public static List<Friend> getCHAT_FRIENDS() {
		return CHAT_FRIENDS;
	}

	public static void setCHAT_FRIENDS(List<Friend> cHAT_FRIENDS) {
		CHAT_FRIENDS = cHAT_FRIENDS;
	}

	public static void setNewFriends(JSONArray accounts) {
		List<Friend> newFriends = new ArrayList<Friend>();
		for (int i = 0; i < accounts.length(); i++) {
			try {
				Friend friend = new Friend(accounts.getJSONObject(i));
				newFriends.add(friend);
			} catch (JSONException e) {
				// e.printStackTrace();
			}
		}
		MCTools.newFriends = newFriends;
	}

	public static void saveAccount(Activity activity, Account account) {

		MCTools.nowAccount = account;

		try {
			OutputStream os = activity.openFileOutput("account",
					Context.MODE_PRIVATE);
			ObjectOutputStream oos = new ObjectOutputStream(os);
			oos.writeObject(account);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static Account getLoginedAccount(Activity activity) {
		Account account = MCTools.nowAccount;
		if (account != null) {
			return account;
		}
		if (activity != null) {
			try {
				InputStream is = activity.openFileInput("account");
				ObjectInputStream ois = new ObjectInputStream(is);
				account = (Account) ois.readObject();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (StreamCorruptedException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
		return account;
	}

	public static Bitmap createBitmap(String content) {
		// 用于设置QR二维码参数
		Hashtable<EncodeHintType, Object> qrParam = new Hashtable<EncodeHintType, Object>();
		// 设置QR二维码的纠错级别——这里选择最高H级别
		qrParam.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
		// 设置编码方式
		qrParam.put(EncodeHintType.CHARACTER_SET, "UTF-8");

		// 生成QR二维码数据——这里只是得到一个由true和false组成的数组
		// 参数顺序分别为：编码内容，编码类型，生成图片宽度，生成图片高度，设置参数
		try {
			BitMatrix bitMatrix = new MultiFormatWriter().encode(content,
					BarcodeFormat.QR_CODE, QRCODE_SIZE, QRCODE_SIZE, qrParam);

			// 开始利用二维码数据创建Bitmap图片，分别设为黑白两色
			int w = bitMatrix.getWidth();
			int h = bitMatrix.getHeight();
			int[] data = new int[w * h];

			for (int y = 0; y < h; y++) {
				for (int x = 0; x < w; x++) {
					if (bitMatrix.get(x, y))
						data[y * w + x] = 0xff000000;// 黑色
					else
						data[y * w + x] = -1;// -1 相当于0xffffffff 白色
				}
			}
			// 创建一张bitmap图片，采用最高的效果显示
			Bitmap bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
			// 将上面的二维码颜色数组传入，生成图片颜色
			bitmap.setPixels(data, 0, w, 0, 0, w, h);
			return bitmap;
		} catch (WriterException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	private void createQRCodeBitmapWithPortrait(Bitmap qr, Bitmap portrait) {  
	    // 头像图片的大小  
	    int portrait_W = portrait.getWidth();  
	    int portrait_H = portrait.getHeight();  
	  
	    // 设置头像要显示的位置，即居中显示  
	    int left = (QRCODE_SIZE - portrait_W) / 2;  
	    int top = (QRCODE_SIZE - portrait_H) / 2;  
	    int right = left + portrait_W;  
	    int bottom = top + portrait_H;  
	    Rect rect1 = new Rect(left, top, right, bottom);  
	  
	    // 取得qr二维码图片上的画笔，即要在二维码图片上绘制我们的头像  
	    Canvas canvas = new Canvas(qr);  
	  
	    // 设置我们要绘制的范围大小，也就是头像的大小范围  
	    Rect rect2 = new Rect(0, 0, portrait_W, portrait_H);  
	    // 开始绘制  
	    canvas.drawBitmap(portrait, rect2, rect1, null);
	}  

	public static String createAccessKey() {

		String[] strs = { "a", "b", "c", "d", "e", "f", "g", "h", "i", "j",
				"k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v",
				"w", "x", "y", "z", "A", "B", "C", "D", "E", "F", "G", "H",
				"I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T",
				"U", "V", "W", "X", "Y", "Z", "0", "1", "2", "3", "4", "5",
				"6", "7", "8", "9" };
		int count = 20;
		String str = "";
		for (int i = 0; i < 9; i++) {
			str += strs[(int) Math.floor(Math.random() * strs.length)];
		}
		str += "a";
		for (int i = 0; i < count - 10; i++) {
			str += strs[(int) Math.floor(Math.random() * strs.length)];
		}
		return str;
	}

	public static void saveFriends(Activity activity, JSONArray friends) {
		DBManager dbManager = new DBManager(activity);
		dbManager.addFriends(friends);
		dbManager.closeDB();
	}

	public static List<Circle> getCircles(Activity activity) {
		List<Circle> circles = new ArrayList<Circle>();
		DBManager dbManager = new DBManager(activity);
		circles = dbManager.queryCircle();
		dbManager.closeDB();
		return circles;
	}

	public static List<Friend> getFriends(Activity activity, int rid) {
		List<Friend> accounts = new ArrayList<Friend>();
		DBManager dbManager = new DBManager(activity);
		accounts = dbManager.queryFriends(rid);
		dbManager.closeDB();
		return accounts;
	}

	public static void saveCommunities(Activity activity, JSONArray communities) {
		DBManager dbManager = new DBManager(activity);
		dbManager.addCommunities(communities);
		dbManager.closeDB();
	}

	public static List<Community> getCommunities(Activity activity) {
		List<Community> community = new ArrayList<Community>();
		DBManager dbManager = new DBManager(activity);
		community = dbManager.queryCommunities(MCTools.getLoginedAccount(null)
				.getUid());
		dbManager.closeDB();
		return community;
	}

}

class DBHelper extends SQLiteOpenHelper {

	private static final String DATABASE_NAME = "mc.db";
	private static final int DATABASE_VERSION = 1;

	public DBHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("CREATE TABLE IF NOT EXISTS circlerelation"
				+ "(rid INTEGER, uid INTEGER)");
		db.execSQL("CREATE TABLE IF NOT EXISTS circle"
				+ "(rid INTEGER PRIMARY KEY, name VARCHAR, fuid INTEGER)");
		db.execSQL("CREATE TABLE IF NOT EXISTS friend"
				+ "(uid INTEGER PRIMARY KEY, nickName VARCHAR, head VARCHAR, phone VARCHAR, mainBusiness TEXT,friendStatus VARCHAR)");
		db.execSQL("CREATE TABLE IF NOT EXISTS agent"
				+ "(aid INTEGER PRIMARY KEY, nickName VARCHAR, head VARCHAR, phone VARCHAR, mainBusiness TEXT)");
		db.execSQL("CREATE TABLE IF NOT EXISTS community"
				+ "(cid INTEGER PRIMARY KEY, name VARCHAR, description TEXT , aid INTEGER)");
		db.execSQL("CREATE TABLE IF NOT EXISTS communityrelation"
				+ "(cid INTEGER, fuid INTEGER)");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("ALTER TABLE circle ADD COLUMN other STRING");
		db.execSQL("ALTER TABLE friend ADD COLUMN other STRING");
		db.execSQL("ALTER TABLE circlerelation ADD COLUMN other STRING");
		db.execSQL("ALTER TABLE community ADD COLUMN other STRING");
		db.execSQL("ALTER TABLE communityrelation ADD COLUMN other STRING");
	}
}

class DBManager {
	private DBHelper helper;
	private SQLiteDatabase db;

	public DBManager(Context context) {
		helper = new DBHelper(context);
		db = helper.getWritableDatabase();
	}

	public void addFriends(JSONArray friends) {
		deleteFriends();
		db.beginTransaction(); // 开始事务
		try {
			for (int i = 0; i < friends.length(); i++) {
				Circle circle = new Circle(friends.getJSONObject(i));
				if (circle.getRid() != 0) {
					db.execSQL("INSERT INTO circle VALUES(?, ?, ?)",
							new Object[] { circle.getRid(), circle.getName(),
									MCTools.getLoginedAccount(null).getUid() });
				} else {
					db.execSQL("INSERT INTO circle VALUES(?, ?, ?)",
							new Object[] {
									-MCTools.getLoginedAccount(null).getUid(),
									"没有分组",
									MCTools.getLoginedAccount(null).getUid() });
				}
				JSONArray accounts = friends.getJSONObject(i).getJSONArray(
						"accounts");
				for (int j = 0; j < accounts.length(); j++) {
					Friend friend = new Friend(accounts.getJSONObject(j));
					db.execSQL(
							"INSERT INTO friend VALUES(?, ?, ?, ?, ?, ?)",
							new Object[] { friend.getUid(),
									friend.getNickName(), friend.getHead(),
									friend.getPhone(),
									friend.getMainBusiness(),
									friend.getFriendStatus() });
					if (circle.getRid() != 0) {
						db.execSQL(
								"INSERT INTO circlerelation VALUES(?,?)",
								new Object[] { circle.getRid(), friend.getUid() });
					} else {
						db.execSQL("INSERT INTO circlerelation VALUES(?,?)",
								new Object[] {
										-MCTools.getLoginedAccount(null)
												.getUid(), friend.getUid() });
					}
				}

			}
			db.setTransactionSuccessful(); // 设置事务成功完成
		} catch (JSONException e) {
			// e.printStackTrace();
		} finally {
			db.endTransaction(); // 结束事务
		}
	}

	public void addCommunities(JSONArray communities) {
		db.beginTransaction(); // 开始事务
		try {
			db.execSQL("DELETE FROM communityrelation WHERE fuid=?",
					new Object[] { MCTools.getLoginedAccount(null) });
			for (int i = 0; i < communities.length(); i++) {
				Community community = new Community(
						communities.getJSONObject(i));
				db.execSQL(
						"INSERT OR REPLACE INTO community VALUES(?, ?, ?, ?)",
						new Object[] { community.getCid(), community.getName(),
								community.getDescription(),
								community.getAgent().getUid() });
				db.execSQL(
						"INSERT OR REPLACE INTO agent VALUES(?, ?, ?, ?, ?)",
						new Object[] { community.getAgent().getUid(),
								community.getAgent().getNickName(),
								community.getAgent().getHead(),
								community.getAgent().getPhone(),
								community.getAgent().getMainBusiness() });
				db.execSQL("INSERT INTO communityrelation VALUES(?, ?)",
						new Object[] { community.getCid(),
								MCTools.getLoginedAccount(null).getUid() });

			}
			db.setTransactionSuccessful(); // 设置事务成功完成
		} catch (JSONException e) {
			// e.printStackTrace();
		} finally {
			db.endTransaction(); // 结束事务
		}
	}

	public List<Community> queryCommunities(int fuid) {
		List<Community> communities = new ArrayList<Community>();
		Cursor c = queryCommunitiesCursor();
		while (c.moveToNext()) {
			Community community = new Community();
			community.setCid(c.getInt(c.getColumnIndex("cid")));
			community.setName(c.getString(c.getColumnIndex("name")));
			community.setDescription(c.getString(c
					.getColumnIndex("description")));
			Cursor ac = queryCommunityAgentCursor(c.getInt(c
					.getColumnIndex("aid")));
			Account account = new Account();
			if (ac.moveToNext()) {
				account.setUid(ac.getInt(ac.getColumnIndex("aid")));
				account.setNickName(ac.getString(ac.getColumnIndex("nickName")));
				account.setHead(ac.getString(ac.getColumnIndex("head")));
				account.setPhone(ac.getString(ac.getColumnIndex("phone")));
				account.setMainBusiness(ac.getString(ac
						.getColumnIndex("mainBusiness")));
			}
			community.setAgent(account);
			communities.add(community);
		}
		;
		return communities;

	}

	private Cursor queryCommunityAgentCursor(int aid) {
		Cursor c = db
				.rawQuery(
						"SELECT aid,nickName,head,phone,mainBusiness FROM agent WHERE aid=?",
						new String[] { String.valueOf(aid) });
		return c;
	}

	private Cursor queryCommunitiesCursor() {
		Cursor c = db
				.rawQuery(
						"SELECT cid,name,description,aid FROM community WHERE cid IN(select cid from communityrelation where fuid=?)",
						new String[] { String.valueOf(MCTools
								.getLoginedAccount(null).getUid()) });
		return c;
	}

	public List<Circle> queryCircle() {
		List<Circle> circles = new ArrayList<Circle>();
		Cursor c = queryCircleCursor();
		while (c.moveToNext()) {
			Circle circle = new Circle();
			circle.setRid(c.getInt(c.getColumnIndex("rid")));
			circle.setName(c.getString(c.getColumnIndex("name")));
			circles.add(circle);
		}
		;
		return circles;

	}

	private Cursor queryCircleCursor() {
		Cursor c = db.rawQuery("SELECT rid,name FROM circle WHERE fuid = ?",
				new String[] { String.valueOf(MCTools.getLoginedAccount(null)
						.getUid()) });
		return c;
	}

	public List<Friend> queryFriends(int rid) {
		List<Friend> friends = new ArrayList<Friend>();
		Cursor c = queryFriendsCursor(rid);
		while (c.moveToNext()) {
			Friend friend = new Friend();
			friend.setNickName(c.getString(c.getColumnIndex("nickName")));
			friend.setHead(c.getString(c.getColumnIndex("head")));
			friend.setPhone(c.getString(c.getColumnIndex("phone")));
			friend.setMainBusiness(c.getString(c.getColumnIndex("mainBusiness")));
			friend.setFriendStatus(c.getString(c.getColumnIndex("friendStatus")));
			friends.add(friend);
		}
		;
		return friends;
	}

	private Cursor queryFriendsCursor(int rid) {
		Cursor c = db
				.rawQuery(
						"SELECT nickName,head,phone,mainBusiness,friendStatus FROM friend WHERE uid IN (select uid from circlerelation where rid=?)",
						new String[] { String.valueOf(rid) });
		return c;
	}

	private void deleteFriends() {
		List<Circle> circles = queryCircle();
		db.beginTransaction();
		try {
			for (Circle circle : circles) {
				db.execSQL("DELETE FROM friend",
						new Object[] { circle.getRid() });
				db.execSQL("DELETE FROM circlerelation",
						new Object[] { circle.getRid() });
			}
			db.execSQL("DELETE FROM circle", new Object[] { MCTools
					.getLoginedAccount(null).getUid() });
			db.setTransactionSuccessful();
		} finally {
			db.endTransaction();
		}
	}

	public void closeDB() {
		db.close();
	}
}
