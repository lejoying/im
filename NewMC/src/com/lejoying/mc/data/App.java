package com.lejoying.mc.data;

import java.io.File;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Environment;
import android.os.Handler;
import android.view.LayoutInflater;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.lejoying.mc.R;
import com.lejoying.mc.data.handler.DataHandler;
import com.lejoying.mc.data.handler.EventHandler;
import com.lejoying.mc.data.handler.FileHandler;
import com.lejoying.mc.data.handler.JSONHandler;
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
		initBaiduLocation();
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

	// baidu location
	public LocationClient mLocationClient = null;
	public BDLocationListener myListener = new MyLocationListener();

	public void initBaiduLocation() {
		mLocationClient = new LocationClient(context); // 声明LocationClient类
		mLocationClient.setAK("WcjShod0qos0j8pQExf7fMxh");
		mLocationClient.registerLocationListener(myListener);

		LocationClientOption option = new LocationClientOption();
		option.setOpenGps(true);
		option.setAddrType("all");// 返回的定位结果包含地址信息
		option.setCoorType("bd09ll");// 返回的定位结果是百度经纬度,默认值gcj02
		option.setScanSpan(10 * 1000 * 60);// 设置发起定位请求的间隔时间为10min
		option.disableCache(true);// 禁止启用缓存定位
		option.setPoiNumber(5); // 最多返回POI个数
		option.setPoiDistance(1000); // poi查询距离
		option.setPoiExtraInfo(true); // 是否需要POI的电话和地址等详细信息
		mLocationClient.setLocOption(option);
	}

	public class MyLocationListener implements BDLocationListener {
		@Override
		public void onReceiveLocation(BDLocation location) {
			if (location == null)
				return;
			StringBuffer sb = new StringBuffer(256);
			sb.append("time : ");
			sb.append(location.getTime());
			sb.append("\nerror code : ");
			sb.append(location.getLocType());
			sb.append("\nlatitude : ");
			sb.append(location.getLatitude());
			sb.append("\nlontitude : ");
			sb.append(location.getLongitude());
			sb.append("\nradius : ");
			sb.append(location.getRadius());
			if (location.getLocType() == BDLocation.TypeGpsLocation) {
				sb.append("\nspeed : ");
				sb.append(location.getSpeed());
				sb.append("\nsatellite : ");
				sb.append(location.getSatelliteNumber());
			} else if (location.getLocType() == BDLocation.TypeNetWorkLocation) {
				sb.append("\naddr : ");
				sb.append(location.getAddrStr());
			}

			System.out.println(sb.toString());
		}

		public void onReceivePoi(BDLocation poiLocation) {
			if (poiLocation == null) {
				return;
			}
			StringBuffer sb = new StringBuffer(256);
			sb.append("Poi time : ");
			sb.append(poiLocation.getTime());
			sb.append("\nerror code : ");
			sb.append(poiLocation.getLocType());
			sb.append("\nlatitude : ");
			sb.append(poiLocation.getLatitude());
			sb.append("\nlontitude : ");
			sb.append(poiLocation.getLongitude());
			sb.append("\nradius : ");
			sb.append(poiLocation.getRadius());
			if (poiLocation.getLocType() == BDLocation.TypeNetWorkLocation) {
				sb.append("\naddr : ");
				sb.append(poiLocation.getAddrStr());
			}
			if (poiLocation.hasPoi()) {
				sb.append("\nPoi:");
				sb.append(poiLocation.getPoi());
			} else {
				sb.append("noPoi information");
			}
			System.out.println(sb.toString());
		}
	}

	public Context context;

	public boolean isDataChanged;

	public Config config;

	public final int SHOW_SELF = 1;
	public final int SHOW_FRIEND = 2;
	public final int SHOW_TEMPFRIEND = 3;
	public int businessCardStatus;
}
