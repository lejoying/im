package com.open.welinks;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.open.welinks.model.Constant;
import com.open.welinks.model.Data;
import com.open.welinks.model.Data.UserInformation;
import com.open.welinks.model.Data.UserInformation.User;
import com.open.welinks.model.Parser;
import com.open.welinks.model.TaskManageHolder;
import com.open.welinks.utils.BaseDataUtils;

public class LaunchActivity extends Activity {

	public boolean isDebug = false;
	public Data data = Data.getInstance();

	public String tag = "MainActivity";
	public Context context;

	public TaskManageHolder taskManageHolder;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		context = this;
		Log.d(tag, "hello world!");

		taskManageHolder = TaskManageHolder.getInstance();
		taskManageHolder.initialize(this);

		initImageLoader(getApplicationContext());

		data = Data.getInstance();

		Parser parser = Parser.getInstance();
		parser.initialize(context);

		BaseDataUtils.initBaseData(this);

		Constant.init();

		// getLocalInformation();
		try {
			if (data.userInformation == null) {
				String userInformationStr = parser.getFromRootForder("userInformation.js");
				data.userInformation = parser.gson.fromJson(userInformationStr, UserInformation.class);
			}
			User currentUser = data.userInformation.currentUser;
			if (!"".equals(currentUser.phone) && !"".equals(currentUser.accessKey)) {
				Intent intent = new Intent(LaunchActivity.this, NearbyActivity.class);
				intent.putExtra("type", "newest");
				startActivity(intent);
				finish();
			} else {
				Intent intent = new Intent(LaunchActivity.this, LoadingActivity.class);
				startActivity(intent);
				finish();
			}
		} catch (Exception e) {
			Intent intent = new Intent(LaunchActivity.this, LoadingActivity.class);
			startActivity(intent);
			finish();
		}
	}

	public static void initImageLoader(Context context) {
		if (!ImageLoader.getInstance().isInited()) {
			ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context).threadPriority(Thread.NORM_PRIORITY - 2).denyCacheImageMultipleSizesInMemory().diskCacheFileNameGenerator(new Md5FileNameGenerator()).diskCacheSize(50 * 1024 * 1024).tasksProcessingOrder(QueueProcessingType.LIFO).writeDebugLogs().build();
			ImageLoader.getInstance().init(config);
		}
	}

	public static class Config {
		public static final boolean DEVELOPER_MODE = false;
	}

	public class LocalConfig {
		public String deviceid = "";
		public String line1Number = "";
		public String imei = "";
		public String imsi = "";
	}

	public void getLocalInformation() {
		// LocalConfig localConfig = data.userInformation.localConfig;
		try {
			LocalConfig localConfig = new LocalConfig();
			TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
			localConfig.deviceid = telephonyManager.getDeviceId();
			localConfig.line1Number = telephonyManager.getLine1Number();
			localConfig.imei = telephonyManager.getSimSerialNumber();
			localConfig.imsi = telephonyManager.getSubscriberId();
			Log.e(tag, "deviceid:" + localConfig.deviceid + "\nline1Number:" + localConfig.line1Number + "\nimei:" + localConfig.imei + "\nimsi:" + localConfig.imsi);
		} catch (Exception e) {
			e.printStackTrace();
			Log.e("LaunchActivity", e.toString(), e);
		}
	}
}
