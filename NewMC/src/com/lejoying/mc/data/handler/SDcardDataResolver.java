package com.lejoying.mc.data.handler;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.StreamCorruptedException;

import android.content.Context;

import com.lejoying.mc.data.App;
import com.lejoying.mc.data.Config;
import com.lejoying.mc.data.Data;
import com.lejoying.mc.data.handler.DataHandler.Modification;
import com.lejoying.mc.data.handler.DataHandler.UIModification;

public class SDcardDataResolver {

	App app;

	public void initialize(App app) {
		this.app = app;
	}

	InputStream inputStream = null;
	ObjectInputStream objectInputStream = null;
	OutputStream outputStream = null;
	ObjectOutputStream objectOutputStream = null;

	public void readConfig() {
		// read config
		try {
			inputStream = app.context.openFileInput("config");
			objectInputStream = new ObjectInputStream(inputStream);
			app.config = (Config) objectInputStream.readObject();

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

	public void readData(UIModification uiModification) {
		Data sdData = null;
		try {
			inputStream = app.context.openFileInput(app.config.lastLoginPhone);
			objectInputStream = new ObjectInputStream(inputStream);
			sdData = (Data) objectInputStream.readObject();
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
		final Data finalData = sdData;
		app.dataHandler.modifyData(new Modification() {
			public void modify(Data data) {
				if (finalData != null) {
					data.user = finalData.user;
					data.circles = finalData.circles;
					data.friends = finalData.friends;
					data.groups = finalData.groups;
					data.groupFriends = finalData.groupFriends;
					data.lastChatFriends = finalData.lastChatFriends;
					data.newFriends = finalData.newFriends;
				}
			}
		}, uiModification);
	}

	public void readLocalData(UIModification uiModification) {
		Data sdData = null;
		try {
			inputStream = app.context.openFileInput(app.data.user.phone);
			objectInputStream = new ObjectInputStream(inputStream);
			sdData = (Data) objectInputStream.readObject();
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
		final Data finalData = sdData;
		app.dataHandler.modifyData(new Modification() {
			public void modify(Data data) {
				if (finalData != null) {
					String accessKey = data.user.accessKey;
					data.user = finalData.user;
					data.user.accessKey = accessKey;
					data.circles = finalData.circles;
					data.friends = finalData.friends;
					data.groups = finalData.groups;
					data.groupFriends = finalData.groupFriends;
					data.lastChatFriends = finalData.lastChatFriends;
					data.newFriends = finalData.newFriends;
				}
			}
		}, uiModification);
	}

	public void saveToSDcard() {
		if (app.isDataChanged) {
			app.isDataChanged = false;
			// save data
			try {
				outputStream = app.context.openFileOutput(app.data.user.phone,
						Context.MODE_PRIVATE);
				app.config.lastLoginPhone = app.data.user.phone;
				objectOutputStream = new ObjectOutputStream(outputStream);
				Data saveData = new Data();
				saveData.circles = app.data.circles;
				saveData.friends = app.data.friends;
				saveData.groups = app.data.groups;
				saveData.groupFriends = app.data.groupFriends;
				saveData.lastChatFriends = app.data.lastChatFriends;
				saveData.newFriends = app.data.newFriends;
				saveData.user = app.data.user;
				objectOutputStream.writeObject(saveData);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				try {
					if (objectOutputStream != null) {
						objectOutputStream.close();
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				try {
					if (outputStream != null) {
						outputStream.close();
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			// save config
			try {
				outputStream = app.context.openFileOutput("config",
						Context.MODE_PRIVATE);
				objectOutputStream = new ObjectOutputStream(outputStream);
				objectOutputStream.writeObject(app.config);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				try {
					if (objectOutputStream != null) {
						objectOutputStream.close();
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				try {
					if (outputStream != null) {
						outputStream.close();
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
}
