package com.lejoying.mc.data;

import java.io.File;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Environment;
import android.os.Handler;
import android.view.LayoutInflater;

import com.lejoying.mc.R;
import com.lejoying.mc.data.handler.DataHandler;
import com.lejoying.mc.data.handler.EventHandler;
import com.lejoying.mc.data.handler.FileHandler;
import com.lejoying.mc.data.handler.JSONHandler;
import com.lejoying.mc.data.handler.LocationHandler;
import com.lejoying.mc.data.handler.SDcardDataResolver;
import com.lejoying.mc.data.handler.ServerHandler;
import com.lejoying.mc.data.handler.ViewHandler;
import com.lejoying.utils.SHA1;

public class App {
	private static App app;

	public String addFriendFragment = "addFriendFragment";
	public String businessCardFragment = "businessCardFragment";
	public String chatFragment = "chatFragment";
	public String friendNotFoundFragment = "friendNotFoundFragment";
	public String friendsFragment = "friendsFragment";
	public String loginUseCodeFragment = "loginUseCodeFragment";
	public String loginUsePassFragment = "loginUsePassFragment";
	public String modifyFragment = "modifyFragment";
	public String newFriendsFragment = "newFriendsFragment";
	public String registerCodeFragment = "registerCodeFragment";
	public String registerPassFragment = "registerPassFragment";
	public String registerPhoneFragment = "registerPhoneFragment";
	public String scanQRCodeFragment = "scanQRCodeFragment";
	public String searchFriendFragment = "searchFriendFragment";
	public String shareFragment = "shareFragment";
	public String squareFragment = "squareFragment";
	public String groupFragment = "groupFragment";

	public String mark = "";

	public String networkStatus = "none";// "WIFI"|"mobile"
	public String sdcardStatus = "none";// "exist"

	public SHA1 sha1;

	private App() {

	}

	private boolean isInitialized;

	public LayoutInflater inflater;

	public void initialize(Context applicationContext) {
		if (isInitialized) {
			return;
		} else {
			isInitialized = true;
		}
		context = applicationContext;
		inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		initData();
		initConfig();
		initHandler();
		initSDCard();
		initSound();
		sha1 = new SHA1();
	}

	public static App getInstance() {
		if (app == null) {
			app = new App();
		}
		return app;
	}

	public void initConfig() {
		config = new Config();
	}

	public Data data;

	public void initData() {
		data = new Data();
	}

	public DataHandler dataHandler;
	public EventHandler eventHandler;
	public JSONHandler mJSONHandler;
	public ServerHandler serverHandler;
	public FileHandler fileHandler;
	public SDcardDataResolver sDcardDataResolver;
	public ViewHandler viewHandler;
	public LocationHandler locationHandler;

	public Handler mUIThreadHandler;

	public void initHandler() {

		mUIThreadHandler = new Handler();
		dataHandler = new DataHandler();
		dataHandler.initialize(this);

		eventHandler = new EventHandler();
		eventHandler.initialize(this);

		mJSONHandler = new JSONHandler();
		mJSONHandler.initialize(this);

		serverHandler = new ServerHandler();
		serverHandler.initialize(this);

		fileHandler = new FileHandler();
		fileHandler.initialize(this);

		sDcardDataResolver = new SDcardDataResolver();
		sDcardDataResolver.initialize(this);

		viewHandler = new ViewHandler();
		viewHandler.initialize(this);

		locationHandler = new LocationHandler();
		locationHandler.initialize(this);
	}

	public File sdcardAppFolder;
	public File sdcardImageFolder;
	public File sdcardHeadImageFolder;

	public void initSDCard() {
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			sdcardStatus = "exist";
		} else {
			sdcardStatus = "none";
		}

		if (sdcardStatus == "exist") {
			sdcardAppFolder = new File(
					Environment.getExternalStorageDirectory(), "lejoying");
			if (!sdcardAppFolder.exists()) {
				sdcardAppFolder.mkdirs();
			}
			sdcardImageFolder = new File(sdcardAppFolder, "image");
			if (!sdcardImageFolder.exists()) {
				sdcardImageFolder.mkdirs();
			}
			sdcardHeadImageFolder = new File(sdcardImageFolder, "head");
			if (!sdcardHeadImageFolder.exists()) {
				sdcardHeadImageFolder.mkdir();
			}
		}
	}

	public SoundPool soundPool;
	public int SOUND_MESSAGE;

	public void initSound() {
		soundPool = new SoundPool(4, AudioManager.STREAM_MUSIC, 100);
		SOUND_MESSAGE = soundPool.load(context, R.raw.message, 1);
	}

	public void playSound(int sound) {
		AudioManager mgr = (AudioManager) context
				.getSystemService(Context.AUDIO_SERVICE);

		float streamVolumeCurrent = mgr
				.getStreamVolume(AudioManager.STREAM_MUSIC);

		float streamVolumeMax = mgr
				.getStreamMaxVolume(AudioManager.STREAM_MUSIC);

		float volume = streamVolumeCurrent / streamVolumeMax;

		soundPool.play(SOUND_MESSAGE, volume, volume, 1, 0, 1f);
	}

	public Context context;

	public boolean isDataChanged;

	public Config config;

	public final int SHOW_SELF = 1;
	public final int SHOW_FRIEND = 2;
	public final int SHOW_TEMPFRIEND = 3;
	public int businessCardStatus;
}
