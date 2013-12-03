package com.lejoying.mc.utils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.StreamCorruptedException;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

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

// class DBManager {
// private DBHelper helper;
// private SQLiteDatabase db;
//
// public DBManager(Context context) {
// helper = new DBHelper(context);
// db = helper.getWritableDatabase();
// }
//
// public void addFriends(JSONArray friends) {
// deleteFriends();
// db.beginTransaction(); // 开始事务
// try {
// for (int i = 0; i < friends.length(); i++) {
// Circle circle = new Circle(friends.getJSONObject(i));
// if (circle.getRid() != 0) {
// db.execSQL("INSERT INTO circle VALUES(?, ?, ?)",
// new Object[] {
// circle.getRid(),
// circle.getName(),
// MCDataTools.getLoginedAccount(null)
// .getUid() });
// } else {
// db.execSQL("INSERT INTO circle VALUES(?, ?, ?)",
// new Object[] {
// -MCDataTools.getLoginedAccount(null)
// .getUid(),
// "没有分组",
// MCDataTools.getLoginedAccount(null)
// .getUid() });
// }
// JSONArray accounts = friends.getJSONObject(i).getJSONArray(
// "accounts");
// for (int j = 0; j < accounts.length(); j++) {
// Friend friend = new Friend(accounts.getJSONObject(j));
// db.execSQL(
// "INSERT INTO friend VALUES(?, ?, ?, ?, ?, ?)",
// new Object[] { friend.getUid(),
// friend.getNickName(), friend.getHead(),
// friend.getPhone(),
// friend.getMainBusiness(),
// friend.getFriendStatus() });
// if (circle.getRid() != 0) {
// db.execSQL(
// "INSERT INTO circlerelation VALUES(?,?)",
// new Object[] { circle.getRid(), friend.getUid() });
// } else {
// db.execSQL(
// "INSERT INTO circlerelation VALUES(?,?)",
// new Object[] {
// -MCDataTools.getLoginedAccount(null)
// .getUid(), friend.getUid() });
// }
// }
//
// }
// db.setTransactionSuccessful(); // 设置事务成功完成
// } catch (JSONException e) {
// // e.printStackTrace();
// } finally {
// db.endTransaction(); // 结束事务
// }
// }
//
// public void addCommunities(JSONArray communities) {
// db.beginTransaction(); // 开始事务
// try {
// db.execSQL("DELETE FROM communityrelation WHERE fuid=?",
// new Object[] { MCDataTools.getLoginedAccount(null) });
// for (int i = 0; i < communities.length(); i++) {
// Community community = new Community(
// communities.getJSONObject(i));
// db.execSQL(
// "INSERT OR REPLACE INTO community VALUES(?, ?, ?, ?)",
// new Object[] { community.getCid(), community.getName(),
// community.getDescription(),
// community.getAgent().getUid() });
// db.execSQL(
// "INSERT OR REPLACE INTO agent VALUES(?, ?, ?, ?, ?)",
// new Object[] { community.getAgent().getUid(),
// community.getAgent().getNickName(),
// community.getAgent().getHead(),
// community.getAgent().getPhone(),
// community.getAgent().getMainBusiness() });
// db.execSQL("INSERT INTO communityrelation VALUES(?, ?)",
// new Object[] { community.getCid(),
// MCDataTools.getLoginedAccount(null).getUid() });
//
// }
// db.setTransactionSuccessful(); // 设置事务成功完成
// } catch (JSONException e) {
// // e.printStackTrace();
// } finally {
// db.endTransaction(); // 结束事务
// }
// }
//
// public List<Community> queryCommunities(int fuid) {
// List<Community> communities = new ArrayList<Community>();
// Cursor c = queryCommunitiesCursor();
// while (c.moveToNext()) {
// Community community = new Community();
// community.setCid(c.getInt(c.getColumnIndex("cid")));
// community.setName(c.getString(c.getColumnIndex("name")));
// community.setDescription(c.getString(c
// .getColumnIndex("description")));
// Cursor ac = queryCommunityAgentCursor(c.getInt(c
// .getColumnIndex("aid")));
// Account account = new Account();
// if (ac.moveToNext()) {
// account.setUid(ac.getInt(ac.getColumnIndex("aid")));
// account.setNickName(ac.getString(ac.getColumnIndex("nickName")));
// account.setHead(ac.getString(ac.getColumnIndex("head")));
// account.setPhone(ac.getString(ac.getColumnIndex("phone")));
// account.setMainBusiness(ac.getString(ac
// .getColumnIndex("mainBusiness")));
// }
// community.setAgent(account);
// communities.add(community);
// }
// ;
// return communities;
//
// }
//
// private Cursor queryCommunityAgentCursor(int aid) {
// Cursor c = db
// .rawQuery(
// "SELECT aid,nickName,head,phone,mainBusiness FROM agent WHERE aid=?",
// new String[] { String.valueOf(aid) });
// return c;
// }
//
// private Cursor queryCommunitiesCursor() {
// Cursor c = db
// .rawQuery(
// "SELECT cid,name,description,aid FROM community WHERE cid IN(select cid from communityrelation where fuid=?)",
// new String[] { String.valueOf(MCDataTools
// .getLoginedAccount(null).getUid()) });
// return c;
// }
//
// public List<Circle> queryCircle() {
// List<Circle> circles = new ArrayList<Circle>();
// Cursor c = queryCircleCursor();
// while (c.moveToNext()) {
// Circle circle = new Circle();
// circle.setRid(c.getInt(c.getColumnIndex("rid")));
// circle.setName(c.getString(c.getColumnIndex("name")));
// circles.add(circle);
// }
// ;
// return circles;
//
// }
//
// private Cursor queryCircleCursor() {
// Cursor c = db.rawQuery(
// "SELECT rid,name FROM circle WHERE fuid = ?",
// new String[] { String.valueOf(MCDataTools.getLoginedAccount(
// null).getUid()) });
// return c;
// }
//
// public List<Friend> queryFriends(int rid) {
// List<Friend> friends = new ArrayList<Friend>();
// Cursor c = queryFriendsCursor(rid);
// while (c.moveToNext()) {
// Friend friend = new Friend();
// friend.setNickName(c.getString(c.getColumnIndex("nickName")));
// friend.setHead(c.getString(c.getColumnIndex("head")));
// friend.setPhone(c.getString(c.getColumnIndex("phone")));
// friend.setMainBusiness(c.getString(c.getColumnIndex("mainBusiness")));
// friend.setFriendStatus(c.getString(c.getColumnIndex("friendStatus")));
// friends.add(friend);
// }
// ;
// return friends;
// }
//
// private Cursor queryFriendsCursor(int rid) {
// Cursor c = db
// .rawQuery(
// "SELECT nickName,head,phone,mainBusiness,friendStatus FROM friend WHERE uid IN (select uid from circlerelation where rid=?)",
// new String[] { String.valueOf(rid) });
// return c;
// }
//
// private void deleteFriends() {
// List<Circle> circles = queryCircle();
// db.beginTransaction();
// try {
// for (Circle circle : circles) {
// db.execSQL("DELETE FROM friend",
// new Object[] { circle.getRid() });
// db.execSQL("DELETE FROM circlerelation",
// new Object[] { circle.getRid() });
// }
// db.execSQL("DELETE FROM circle", new Object[] { MCDataTools
// .getLoginedAccount(null).getUid() });
// db.setTransactionSuccessful();
// } finally {
// db.endTransaction();
// }
// }
//
// public void closeDB() {
// db.close();
// }
// }
