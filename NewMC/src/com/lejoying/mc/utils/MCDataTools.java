package com.lejoying.mc.utils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.StreamCorruptedException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.lejoying.mc.entity.Circle;
import com.lejoying.mc.entity.Friend;
import com.lejoying.mc.entity.Message;
import com.lejoying.mc.entity.User;

public class MCDataTools {

	public static void saveUser(Context context, User user) {

		MCStaticData.mUser = user;

		try {
			OutputStream os = context.openFileOutput("user",
					Context.MODE_PRIVATE);
			ObjectOutputStream oos = new ObjectOutputStream(os);
			oos.writeObject(user);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static User getLoginedUser(Context context) {
		User user = MCStaticData.mUser;
		if (user != null) {
			return user;
		}
		if (context != null) {
			try {
				InputStream is = context.openFileInput("user");
				ObjectInputStream ois = new ObjectInputStream(is);
				user = (User) ois.readObject();
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
		return user;
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

	public static List<Message> getMessages(Context context, int from,
			String phone) {
		List<Message> messages = new ArrayList<Message>();
		DBManager manager = new DBManager(context);
		messages = manager.queryMessages(from, phone);
		manager.closeDB();
		return messages;
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
				+ "(rid INTEGER, fphone VARCHAR)");
		db.execSQL("CREATE TABLE IF NOT EXISTS circle"
				+ "(rid INTEGER PRIMARY KEY, name VARCHAR, phone VARCHAR)");
		db.execSQL("CREATE TABLE IF NOT EXISTS friend"
				+ "(fphone VARCHAR PRIMARY KEY, nickName VARCHAR, head VARCHAR, mainBusiness TEXT,friendStatus VARCHAR)");
		db.execSQL("CREATE TABLE IF NOT EXISTS message"
				+ "(mid INTEGER PRIMARY KEY AUTOINCREMENT,phone VARCHAR, fphone VARCHAR, messageType VARCHAR,type VARCHAR,content TEXT,time VARCHAR,isRead INTEGER)");
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

	public DBManager(Context context) {
		db = new DBHelper(context).getWritableDatabase();
	}

	public void addCircles(JSONArray circles) {
		db.beginTransaction();
		try {
			for (int i = 0; i < circles.length(); i++) {
				Circle circle = new Circle(circles.getJSONObject(i));
				if (circle.getRid() != 0) {
					db.execSQL(
							"INSERT OR REPLACE INTO circle VALUES(?, ?, ?)",
							new Object[] { circle.getRid(), circle.getName(),
									MCDataTools.getLoginedUser(null).getPhone() });
				} else {
					db.execSQL(
							"INSERT OR REPLACE INTO circle VALUES(?, ?, ?)",
							new Object[] {
									-Integer.valueOf(
											MCDataTools.getLoginedUser(null)
													.getPhone()).intValue(),
									"没有分组",
									MCDataTools.getLoginedUser(null).getPhone() });
				}
				List<Friend> friends = circle.getFriends();
				for (Friend friend : friends) {
					db.execSQL(
							"INSERT OR REPLACE INTO friend VALUES(?, ?, ?, ?, ?)",
							new Object[] { friend.getPhone(),
									friend.getNickName(), friend.getHead(),
									friend.getMainBusiness(),
									friend.getFriendStatus() });
					if (circle.getRid() != 0) {
						db.execSQL(
								"INSERT OR REPLACE INTO circlerelation VALUES(?,?)",
								new Object[] { circle.getRid(),
										friend.getPhone() });
					} else {
						db.execSQL(
								"INSERT OR REPLACE INTO circlerelation VALUES(?,?)",
								new Object[] {
										-Integer.valueOf(friend.getPhone())
												.intValue(), friend.getPhone() });
					}
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
		Cursor c = db.rawQuery("SELECT rid,name FROM circle WHERE phone = ?",
				new String[] { MCDataTools.getLoginedUser(null).getPhone() });
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
			friend.setFriendStatus(c.getString(c.getColumnIndex("friendStatus")));
			friends.add(friend);
		}
		return friends;
	}

	private Cursor queryFriendsCursor(int rid) {
		Cursor c = db
				.rawQuery(
						"SELECT fphone,nickName,head,mainBusiness,friendStatus FROM friend WHERE fphone IN (select fphone from circlerelation where rid=?)",
						new String[] { String.valueOf(rid) });
		return c;
	}

	public void addMessages(JSONArray messages) {
		db.beginTransaction();
		try {
			for (int i = 0; i < messages.length(); i++) {
				Message message = new Message(new JSONObject(
						messages.getString(i)));
				db.execSQL(
						"INSERT OR REPLACE INTO message VALUES(null,?, ?, ?, ?, ?, ?, ?)",
						new Object[] {
								MCDataTools.getLoginedUser(null).getPhone(),
								message.getPhone(), message.getMessageType(),
								message.getType(), message.getContent(),
								message.getTime(), message.getIsRead() });
			}
			db.setTransactionSuccessful();
		} catch (JSONException e) {

		} finally {
			db.endTransaction();
		}
	}

	public List<Message> queryMessages(int from, String phone) {
		List<Message> messages = new ArrayList<Message>();
		Cursor c = queryMessagesCursor(from, phone);
		while (c.moveToNext()) {
			Message message = new Message();
			message.setId(c.getInt(c.getColumnIndex("mid")));
			message.setPhone(c.getString(c.getColumnIndex("fphone")));
			message.setMessageType(c.getString(c.getColumnIndex("messageType")));
			message.setType(c.getString(c.getColumnIndex("type")));
			message.setContent(c.getString(c.getColumnIndex("content")));
			message.setTime(c.getString(c.getColumnIndex("time")));
			message.setIsRead(c.getInt(c.getColumnIndex("isRead")));
			messages.add(message);
		}
		return messages;
	}

	private Cursor queryMessagesCursor(int from, String phone) {
		Cursor c = db
				.rawQuery(
						"SELECT mid,fphone,messageType,type,content,time,isRead FROM message WHERE fphone=? order by time desc LIMIT "
								+ from + ",10", new String[] { phone });
		return c;
	}

	public void closeDB() {
		db.close();
	}
}
