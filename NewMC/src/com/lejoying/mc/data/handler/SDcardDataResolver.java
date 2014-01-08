package com.lejoying.mc.data.handler;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.StreamCorruptedException;

import android.content.Context;

import com.lejoying.mc.data.App;
import com.lejoying.mc.data.StaticConfig;
import com.lejoying.mc.data.StaticData;
import com.lejoying.mc.data.handler.DataHandler1.Modification;

public class SDcardDataResolver {

	public App app = App.getInstance();

	public void initailize() {

	}

	InputStream inputStream = null;
	ObjectInputStream objectInputStream = null;

	void readConfig(Context context) {
		// read config
		try {
			inputStream = context.openFileInput("config");
			objectInputStream = new ObjectInputStream(inputStream);
			app.config = (StaticConfig) objectInputStream.readObject();

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
				if (objectInputStream != null) {
					objectInputStream.close();
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			try {
				if (inputStream != null) {
					inputStream.close();
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}

	void readStaticData(Context context) {
		try {
			inputStream = context.openFileInput(app.config.lastLoginPhone);
			objectInputStream = new ObjectInputStream(inputStream);
			final StaticData sdData = (StaticData) objectInputStream.readObject();

			app.dataHandler1.modifyData(new Modification() {
				public void modify(StaticData data) {
					data.user = sdData.user;
					data.circles = sdData.circles;
					data.friends = sdData.friends;
					data.lastChatFriends = sdData.lastChatFriends;
					data.newFriends = sdData.newFriends;
				}
			});

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
				if (objectInputStream != null) {
					objectInputStream.close();
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			try {
				if (inputStream != null) {
					inputStream.close();
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}
}
