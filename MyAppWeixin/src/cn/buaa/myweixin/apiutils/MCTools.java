package cn.buaa.myweixin.apiutils;

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
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Handler;
import cn.buaa.myweixin.listener.ResponseListener;
import cn.buaa.myweixin.utils.HttpTools;
import cn.buaa.myweixin.utils.HttpTools.HttpListener;
import cn.buaa.myweixin.utils.LocationTools;
import cn.buaa.myweixin.utils.StreamTools;

public class MCTools {

	private static Account nowAccount;

	private static Handler MCHandler = new Handler();

	private static final String DOMAIN = "http://192.168.0.102:8071";

	private static String lasturl;
	private static Map<String, String> lastparam;
	private static long lasttime;

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
							// TODO Auto-generated method stub
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
											MCHandler.post(new Runnable() {
												@Override
												public void run() {
													responseListener
															.success(data);
												}
											});
										}
										if (info.equals("失败")) {
											MCHandler.post(new Runnable() {
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
									MCHandler.post(new Runnable() {
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

	public static Map<String, String> getLocationParam(Activity activity) {
		double[] location = LocationTools.getLocation(activity);
		Map<String, String> map = new HashMap<String, String>();
		map.put("latitude", String.valueOf(location[1]));
		map.put("longitude", String.valueOf(location[0]));
		return map;
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
		return account;
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
	
	public static void saveFriends(Activity activity){
		
	}
}

class DBHelper extends SQLiteOpenHelper {  
	  
    private static final String DATABASE_NAME = "test.db";  
    private static final int DATABASE_VERSION = 1;  
      
    public DBHelper(Context context) {  
        super(context, DATABASE_NAME, null, DATABASE_VERSION);  
    }  
  
    @Override  
    public void onCreate(SQLiteDatabase db) {  
        db.execSQL("CREATE TABLE IF NOT EXISTS mc" +  
                "(_id INTEGER PRIMARY KEY AUTOINCREMENT, uid INTEGER, name VARCHAR, age INTEGER, info TEXT)");  
    }  
  
    @Override  
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {  
        db.execSQL("ALTER TABLE person ADD COLUMN other STRING");  
    }  
}  

class DBManager {  
    private DBHelper helper;  
    private SQLiteDatabase db;  
      
    public DBManager(Context context) {  
        helper = new DBHelper(context); 
        db = helper.getWritableDatabase();  
    }  
      
    /** 
     * add persons 
     * @param persons 
     */  
    public void add(JSONArray friends) {  
        db.beginTransaction();  //开始事务  
        try {  
//            for (Person person : persons) {  
//                db.execSQL("INSERT INTO person VALUES(null, ?, ?, ?)", new Object[]{person.name, person.age, person.info});  
//            }  
            db.setTransactionSuccessful();  //设置事务成功完成  
        } finally {  
            db.endTransaction();    //结束事务  
        }  
    }  
      
    /** 
     * update person's age 
     * @param person 
     */  
//    public void updateAge(Person person) {  
//        ContentValues cv = new ContentValues();  
//        cv.put("age", person.age);  
//        db.update("person", cv, "name = ?", new String[]{person.name});  
//    }  
//      
//    /** 
//     * delete old person 
//     * @param person 
//     */  
//    public void deleteOldPerson(Person person) {  
//        db.delete("person", "age >= ?", new String[]{String.valueOf(person.age)});  
//    }  
//      
//    /** 
//     * query all persons, return list 
//     * @return List<Person> 
//     */  
//    public List<Person> query() {  
//        ArrayList<Person> persons = new ArrayList<Person>();  
//        Cursor c = queryTheCursor();  
//        while (c.moveToNext()) {  
//            Person person = new Person();  
//            person._id = c.getInt(c.getColumnIndex("_id"));  
//            person.name = c.getString(c.getColumnIndex("name"));  
//            person.age = c.getInt(c.getColumnIndex("age"));  
//            person.info = c.getString(c.getColumnIndex("info"));  
//            persons.add(person);  
//        }  
//        c.close();  
//        return persons;  
//    }  
      
    /** 
     * query all persons, return cursor 
     * @return  Cursor 
     */  
    public Cursor queryTheCursor() {  
        Cursor c = db.rawQuery("SELECT * FROM person", null);  
        return c;  
    }  
      
    /** 
     * close database 
     */  
    public void closeDB() {  
        db.close();  
    }  
}  
