package com.open.welinks;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;

import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.open.welinks.controller.FriendsSubController;
import com.open.welinks.controller.MainController;
import com.open.welinks.controller.MeSubController;
import com.open.welinks.controller.MessagesSubController;
import com.open.welinks.controller.ShareSubController;
import com.open.welinks.controller.SquareSubController;
import com.open.welinks.model.Data;
import com.open.welinks.model.Parser;
import com.open.welinks.service.PushService;
import com.open.welinks.view.FriendsSubView;
import com.open.welinks.view.MainView;
import com.open.welinks.view.MeSubView;
import com.open.welinks.view.MessagesSubView;
import com.open.welinks.view.ShareSubView;
import com.open.welinks.view.SquareSubView;
import com.open.welinks.view.ViewManage;

public class MainActivity extends Activity {

	public Data data = Data.getInstance();
	public String tag = "UserIntimateActivity";

	public Context context;
	public MainView thisView;
	public MainController thisController;
	public Activity thisActivity;

	public ViewManage viewManager = ViewManage.getInstance();

	public Parser parser = Parser.getInstance();

	public boolean islinked = false;

	@SuppressWarnings("unused")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.thisActivity = this;
		if (Config.DEVELOPER_MODE && Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
			StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectAll().penaltyDialog().build());
			StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder().detectAll().penaltyDeath().build());
		}
		initImageLoader(getApplicationContext());
		startPushService();
		thisActivity.setContentView(R.layout.activity_welinks);
	}

	public void startPushService() {
		Intent service = new Intent(thisActivity, PushService.class);
		Log.e(tag, "* startPushService *check data");
		parser.initialize(thisActivity);
		data = parser.check();
		service.putExtra("phone", data.userInformation.currentUser.phone);
		service.putExtra("accessKey", data.userInformation.currentUser.accessKey);
		service.putExtra("operation", true);
		startService(service);
	}

	public static void initImageLoader(Context context) {
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context).threadPriority(Thread.NORM_PRIORITY - 2).denyCacheImageMultipleSizesInMemory().diskCacheFileNameGenerator(new Md5FileNameGenerator()).diskCacheSize(50 * 1024 * 1024).tasksProcessingOrder(QueueProcessingType.LIFO).writeDebugLogs().build();
		ImageLoader.getInstance().init(config);
	}

	public static class Config {
		public static final boolean DEVELOPER_MODE = false;
	}

	void linkViewController() {
		if (this.islinked) {
			return;
		}
		parser.initialize(context);
		parser.check();

		this.context = this;
		this.thisView = new MainView(thisActivity);
		this.thisController = new MainController(thisActivity);
		this.thisView.thisController = this.thisController;
		this.thisController.thisView = this.thisView;

		linkSubViewController();

		viewManager.mainView = this.thisView;

		thisController.initializeListeners();
		thisView.initViews();
		thisController.oncreate();
		thisController.bindEvent();

		this.islinked = true;
	}

	void linkSubViewController() {

		this.thisView.squareSubView = new SquareSubView(this.thisView);
		this.thisView.shareSubView = new ShareSubView(this.thisView);
		this.thisView.messagesSubView = new MessagesSubView(this.thisView);
		this.thisView.friendsSubView = new FriendsSubView(this.thisView);
		this.thisView.meSubView = new MeSubView(this.thisView);

		this.thisController.squareSubController = new SquareSubController(this.thisController);
		this.thisController.shareSubController = new ShareSubController(this.thisController);
		this.thisController.messagesSubController = new MessagesSubController(this.thisController);
		this.thisController.friendsSubController = new FriendsSubController(this.thisController);
		this.thisController.meSubController = new MeSubController(this.thisController);

		this.thisController.squareSubController.thisView = this.thisView.squareSubView;
		this.thisController.shareSubController.thisView = this.thisView.shareSubView;
		this.thisController.messagesSubController.thisView = this.thisView.messagesSubView;
		this.thisController.friendsSubController.thisView = this.thisView.friendsSubView;
		this.thisController.meSubController.thisView = this.thisView.meSubView;

		this.thisView.squareSubView.thisController = this.thisController.squareSubController;
		this.thisView.shareSubView.thisController = this.thisController.shareSubController;
		this.thisView.messagesSubView.thisController = this.thisController.messagesSubController;
		this.thisView.friendsSubView.thisController = this.thisController.friendsSubController;
		this.thisView.meSubView.thisController = this.thisController.meSubController;

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu_debug_1, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.debug1_1) {
			Log.d(tag, "debug1.1");
			startActivity(new Intent(MainActivity.this, Debug1Activity.class));
		} else if (item.getItemId() == R.id.debug1_0) {
			Log.d(tag, "debug1.1");
			startActivity(new Intent(MainActivity.this, Debug1Activity.class));
		}
		return true;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		super.onTouchEvent(event);
		return thisController.onTouchEvent(event);
	}

	public Handler mHandler;
	public Runnable runnable;

	@Override
	protected void onResume() {
		super.onResume();

		mHandler = new Handler();
		runnable = new Runnable() {
			@Override
			public void run() {
				linkViewController();
				thisController.onResume();
			}
		};
		mHandler.post(runnable);
		// link();

	}

	public void link() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				linkViewController();
				thisController.onResume();
			}
		}).start();
	}

	@Override
	protected void onPause() {
		parser = Parser.getInstance();
		parser.save();
		super.onPause();
		if (thisController != null) {
			thisController.onPause();
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		thisController.onDestroy();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		boolean flag = thisController.onKeyDown(keyCode, event);
		if (!flag) {
			return flag;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public void onBackPressed() {
		thisController.onBackPressed();
		super.onBackPressed();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		thisController.onActivityResult(requestCode, resultCode, data);
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public void finish() {
		// parser.saveDataToLocal();
		super.finish();
	}

}
