package com.open.welinks;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
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
import com.open.welinks.model.UpdateManager;
import com.open.welinks.service.PushService;
import com.open.welinks.utils.NotificationUtils;
import com.open.welinks.view.FriendsSubView;
import com.open.welinks.view.MainView;
import com.open.welinks.view.MeSubView;
import com.open.welinks.view.MessagesSubView;
import com.open.welinks.view.ShareSubView;
import com.open.welinks.view.SquareSubView;
import com.open.welinks.view.ViewManage;

public class MainActivity extends Activity {

	public Data data = Data.getInstance();
	public String tag = "MainActivity";

	public Context context;
	public MainView thisView;
	public MainController thisController;
	public Activity thisActivity;

	public ViewManage viewManager = ViewManage.getInstance();

	public Parser parser = Parser.getInstance();

	public boolean islinked = false;

	public static MainActivity instance;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.thisActivity = this;
		// Context context = instance.instance.context;
		instance = this;
		initImageLoader(getApplicationContext());

		startPushService();

		thisActivity.setContentView(R.layout.activity_welinks);

//		try {
			UpdateManager manager = new UpdateManager(MainActivity.this);
			manager.checkUpdate();
//		} catch (Exception e) {
//			e.printStackTrace();
//		}

	}

	public void startPushService() {
		Intent service = new Intent(thisActivity, PushService.class);
		PushService.isRunning = false;
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
		if (thisController != null) {
			super.onTouchEvent(event);
			return thisController.onTouchEvent(event);
		} else {
			return super.onTouchEvent(event);
		}
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
		thisController.onDestroy();
		super.onDestroy();
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
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		return thisController.onKeyUp(keyCode, event);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (thisController != null) {
			thisController.onActivityResult(requestCode, resultCode, data);
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public void finish() {
		thisController.finish();
		// parser.saveDataToLocal();
		super.finish();
	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		if ("chatFriend".equals(NotificationUtils.showFragment)) {
			Intent intent = new Intent(MainActivity.this, ChatActivity.class);
			intent.putExtra("id", NotificationUtils.message.phone);
			intent.putExtra("type", "point");
			startActivityForResult(intent, R.id.tag_second);
		} else if ("chatGroup".equals(NotificationUtils.showFragment)) {
			Intent intent = new Intent(MainActivity.this, ChatActivity.class);
			intent.putExtra("id", NotificationUtils.message.gid);
			intent.putExtra("type", "group");
			startActivityForResult(intent, R.id.tag_second);
		} else if ("chatList".equals(NotificationUtils.showFragment)) {
			thisView.mainPagerBody.active();
			thisView.messages_friends_me_PagerBody.inActive();
			thisView.mainPagerBody.flipTo(2);
			thisView.messages_friends_me_PagerBody.flipTo(0);
		}
		// Log.e(tag, thisView.shareSubView.groupCoverView.getHeight() + ":height");
		// Log.e(tag, thisView.shareSubView.groupCoverView.getWidth() + ":width");
		NotificationUtils.cancelNotification(MainActivity.this);
		super.onWindowFocusChanged(hasFocus);
	}
}
