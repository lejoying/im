package com.lejoying.mc.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.StreamCorruptedException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.lejoying.mc.R;
import com.lejoying.mc.entity.Circle;
import com.lejoying.mc.entity.Friend;
import com.lejoying.mc.entity.Message;
import com.lejoying.mc.entity.User;

public class MCDataTools {

	static String DATABASE_NAME;

	public static void saveUser(Context context, User user) {
		MCStaticData.mUser = user;
		DATABASE_NAME = MCDataTools.generateDBName(user);
		OutputStream os = null;
		ObjectOutputStream oos = null;
		try {
			os = context.openFileOutput(generateUserFileName(user),
					Context.MODE_PRIVATE);
			oos = new ObjectOutputStream(os);
			oos.writeObject(user);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (oos != null) {
					oos.close();
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				if (os != null) {
					os.close();
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public static User getLoginedUser(Context context) {
		User user = MCStaticData.mUser;
		if (user != null) {
			DATABASE_NAME = MCDataTools.generateDBName(user);
			return user;
		}
		if (context != null) {
			InputStream is = null;
			ObjectInputStream ois = null;
			File files = context.getFilesDir();
			for (File file : files.listFiles()) {
				try {
					is = context.openFileInput(file.getName());
					ois = new ObjectInputStream(is);
					user = (User) ois.readObject();
					if (user.isNow()) {
						break;
					}
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (StreamCorruptedException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				} finally {
					try {
						if (ois != null) {
							ois.close();
						}
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

					try {
						if (is != null) {
							is.close();
						}
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}
			}
		}
		return user;
	}

	public static void cleanAllData(Context context, boolean quit) {
		if (quit) {
			User user = MCDataTools.getLoginedUser(null);
			if (user != null) {
				user.setAccessKey("");
				user.setNow(false);
				MCDataTools.saveUser(context, user);
			}
			MCStaticData.mUser = null;
		}
		MCStaticData.loginCodeBundle = null;
		MCStaticData.registerBundle = null;
		MCStaticData.messagesViewList.clear();
		MCStaticData.circlesViewList.clear();
	}

	static String generateDBName(User user) {
		return user.getPhone() + ".db";
	}

	static String generateUserFileName(User user) {
		return user.getPhone();
	}

	public static void saveCircles(Context context, JSONArray circles) {
		DBManager manager = new DBManager(context);
		manager.addCircles(circles);
		manager.closeDB();
	}

	public static List<Circle> getCircles(Context context) {
		List<Circle> circles = new ArrayList<Circle>();
		DBManager manager = new DBManager(context);
		circles = manager.queryCircle();
		manager.closeDB();
		return circles;
	}

	public static void saveMessages(Context context, JSONArray messages) {
		DBManager manager = new DBManager(context);
		manager.addMessages(messages);
		manager.closeDB();
	}

	public static List<Message> getMessages(Context context, int count) {
		List<Message> messages = new ArrayList<Message>();
		DBManager manager = new DBManager(context);
		messages = manager.queryMessages(count);
		manager.closeDB();
		return messages;
	}

	public static List<Message> getMessages(Context context, String fphone,
			int count) {
		List<Message> messages = new ArrayList<Message>();
		DBManager manager = new DBManager(context);
		messages = manager.queryMessages(fphone, count);
		manager.closeDB();
		return messages;
	}

	public static Map<String, Integer> getNotReadMessages(Context context) {
		Map<String, Integer> notReadMessages = new Hashtable<String, Integer>();
		DBManager manager = new DBManager(context);
		notReadMessages = manager.queryNotReadMessages();
		manager.closeDB();
		return notReadMessages;
	}
}

class DBHelper extends SQLiteOpenHelper {

	private final static int DATABASE_VERSION = 1;

	public DBHelper(Context context) {
		super(context, MCDataTools.DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("CREATE TABLE IF NOT EXISTS circlerelation"
				+ "(rid INTEGER, fphone VARCHAR)");
		db.execSQL("CREATE TABLE IF NOT EXISTS circle"
				+ "(rid INTEGER PRIMARY KEY, name VARCHAR)");
		db.execSQL("CREATE TABLE IF NOT EXISTS friend"
				+ "(fphone VARCHAR PRIMARY KEY, nickName VARCHAR, head VARCHAR, mainBusiness TEXT,friendStatus VARCHAR)");
		db.execSQL("CREATE TABLE IF NOT EXISTS message"
				+ "(mid INTEGER PRIMARY KEY AUTOINCREMENT, fphone VARCHAR, messageType VARCHAR,type VARCHAR,content TEXT,time VARCHAR,isRead INTEGER)");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("ALTER TABLE circlerelation ADD COLUMN other STRING");
		db.execSQL("ALTER TABLE circle ADD COLUMN other STRING");
		db.execSQL("ALTER TABLE friend ADD COLUMN other STRING");
		db.execSQL("ALTER TABLE message ADD COLUMN other STRING");
	}
}

class DBManager {
	private SQLiteDatabase db;
	private Context mContext;

	public DBManager(Context context) {
		db = new DBHelper(context).getWritableDatabase();
		this.mContext = context;
	}

	public void addCircles(JSONArray circles) {
		db.beginTransaction();
		try {
			db.execSQL("DELETE FROM circlerelation");
			for (int i = 0; i < circles.length(); i++) {
				Circle circle = new Circle(circles.getJSONObject(i));
				if (circle.getRid() != 0) {
					db.execSQL("INSERT OR REPLACE INTO circle VALUES(?, ?)",
							new Object[] { circle.getRid(), circle.getName() });
				} else {
					db.execSQL(
							"INSERT OR REPLACE INTO circle VALUES(?, ?)",
							new Object[] {
									-Integer.valueOf(
											MCDataTools.getLoginedUser(null)
													.getPhone()).intValue(),
									mContext.getString(R.string.app_nogroup) });
				}
				List<Friend> friends = circle.getFriends();
				for (Friend friend : friends) {
					db.execSQL(
							"INSERT OR REPLACE INTO friend VALUES(?, ?, ?, ?, ?)",
							new Object[] { friend.getPhone(),
									friend.getNickName(), friend.getHead(),
									friend.getMainBusiness(),
									friend.getFriendStatus() });
					int rid = circle.getRid();
					if (circle.getRid() == 0) {
						rid = -Integer.valueOf(friend.getPhone());
					}
					db.execSQL("INSERT INTO circlerelation VALUES(?,?)",
							new Object[] { rid, friend.getPhone() });

				}

			}
			db.setTransactionSuccessful();
		} catch (JSONException e) {

		} finally {
			db.endTransaction();
		}
	}

	public List<Circle> queryCircle() {
		List<Circle> circles = new ArrayList<Circle>();
		Cursor c = queryCircleCursor();
		while (c.moveToNext()) {
			Circle circle = new Circle();
			circle.setRid(c.getInt(c.getColumnIndex("rid")));
			circle.setName(c.getString(c.getColumnIndex("name")));
			circle.setFriends(queryFriends(circle.getRid()));
			circles.add(circle);
		}
		return circles;

	}

	private Cursor queryCircleCursor() {
		Cursor c = db.rawQuery("SELECT rid,name FROM circle", null);
		return c;
	}

	public Friend queryFriend(String phone) {
		Friend friend = new Friend();
		Cursor c = queryFriendCursor(phone);
		while (c.moveToNext()) {
			friend.setPhone(c.getString(c.getColumnIndex("fphone")));
			friend.setNickName(c.getString(c.getColumnIndex("nickName")));
			friend.setHead(c.getString(c.getColumnIndex("head")));
			friend.setMainBusiness(c.getString(c.getColumnIndex("mainBusiness")));
		}
		return friend;
	}

	private Cursor queryFriendCursor(String phone) {
		Cursor c = db
				.rawQuery(
						"SELECT fphone,nickName,head,mainBusiness FROM friend WHERE fphone=?",
						new String[] { phone });
		return c;
	}

	private List<Friend> queryFriends(int rid) {
		List<Friend> friends = new ArrayList<Friend>();
		Cursor c = queryFriendsCursor(rid);
		while (c.moveToNext()) {
			Friend friend = new Friend();
			friend.setPhone(c.getString(c.getColumnIndex("fphone")));
			friend.setNickName(c.getString(c.getColumnIndex("nickName")));
			friend.setHead(c.getString(c.getColumnIndex("head")));
			friend.setMainBusiness(c.getString(c.getColumnIndex("mainBusiness")));
			friends.add(friend);
		}
		return friends;
	}

	private Cursor queryFriendsCursor(int rid) {
		Cursor c = db
				.rawQuery(
						"SELECT fphone,nickName,head,mainBusiness FROM friend WHERE"
								+ " fphone IN (select fphone from circlerelation where rid=?)"
								+ " and friendStatus='success'",
						new String[] { String.valueOf(rid) });
		return c;
	}

	public void addMessages(JSONArray messages) {
		db.beginTransaction();
		db.execSQL("DELETE FROM message");
		try {
			for (int i = 0; i < messages.length(); i++) {
				Message message = new Message(new JSONObject(
						messages.getString(i)));
				db.execSQL(
						"INSERT OR REPLACE INTO message VALUES(null, ?, ?, ?, ?, ?, ?)",
						new Object[] { message.getFriend().getPhone(),
								message.getMessageType(), message.getType(),
								message.getContent(), message.getTime(),
								message.getIsRead() });
			}
			db.setTransactionSuccessful();
		} catch (JSONException e) {

		} finally {
			db.endTransaction();
		}
	}

	public List<Message> queryMessages(int count) {
		List<Message> messages = new ArrayList<Message>();
		Cursor c = queryMessagesCursor(count);
		while (c.moveToNext()) {
			Message message = new Message();
			message.setId(c.getInt(c.getColumnIndex("mid")));
			message.setFriend(queryFriend(c.getString(c
					.getColumnIndex("fphone"))));
			message.setMessageType(c.getString(c.getColumnIndex("messageType")));
			message.setType(c.getString(c.getColumnIndex("type")));
			message.setContent(c.getString(c.getColumnIndex("content")));
			message.setTime(c.getString(c.getColumnIndex("time")));
			message.setIsRead(c.getInt(c.getColumnIndex("isRead")));
			messages.add(message);
		}
		return messages;

	}

	private Cursor queryMessagesCursor(int count) {
		Cursor c = db
				.rawQuery(
						"SELECT mid,fphone,messageType,type,content,time,isRead"
								+ " FROM message GROUP BY fphone ORDER BY time DESC LIMIT ?",
						new String[] { String.valueOf(count) });
		return c;
	}

	public List<Message> queryMessages(String fphone, int count) {
		List<Message> messages = new ArrayList<Message>();
		Cursor c = queryMessagesCursor(fphone, count);
		while (c.moveToNext()) {
			Message message = new Message();
			message.setId(c.getInt(c.getColumnIndex("mid")));
			message.setFriend(queryFriend(c.getString(c
					.getColumnIndex("fphone"))));
			message.setMessageType(c.getString(c.getColumnIndex("messageType")));
			message.setType(c.getString(c.getColumnIndex("type")));
			message.setContent(c.getString(c.getColumnIndex("content")));
			message.setTime(c.getString(c.getColumnIndex("time")));
			message.setIsRead(c.getInt(c.getColumnIndex("isRead")));
			messages.add(message);
		}
		return messages;
	}

	private Cursor queryMessagesCursor(String fphone, int count) {
		Cursor c = db
				.rawQuery(
						"SELECT mid,fphone,messageType,type,content,time,isRead FROM message WHERE fphone=? LIMIT ?",
						new String[] { fphone, String.valueOf(count) });
		return c;
	}

	public Map<String, Integer> queryNotReadMessages() {
		Map<String, Integer> map = new Hashtable<String, Integer>();
		Cursor c = queryNotReadMessagesCursor();
		while (c.moveToNext()) {
			String fphone = c.getString(c.getColumnIndex("fphone"));
			Integer i = map.get(fphone);
			if (map.get(fphone) == null) {
				map.put(fphone, 1);
			} else {
				map.put(fphone, i += 1);
			}
		}
		return map;
	}

	private Cursor queryNotReadMessagesCursor() {
		Cursor c = db.rawQuery("SELECT fphone FROM message where isRead=0",
				null);
		return c;
	}

	public void closeDB() {
		db.close();
	}
}