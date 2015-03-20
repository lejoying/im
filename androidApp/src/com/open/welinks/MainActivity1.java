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

import com.open.lib.MyLog;
import com.open.welink.R;
import com.open.welinks.controller.MainController1;
import com.open.welinks.controller.ShareSubController1;
import com.open.welinks.model.Data;
import com.open.welinks.model.Parser;
import com.open.welinks.model.TaskManageHolder;
import com.open.welinks.utils.NotificationUtils;
import com.open.welinks.view.MainView1;
import com.open.welinks.view.ShareSubView1;
import com.open.welinks.view.ViewManage;

public class MainActivity1 extends Activity {

	public Data data = Data.getInstance();

	public String tag = "MainActivity";
	public MyLog log = new MyLog(tag, true);

	public Context context;
	public MainView1 thisView;
	public MainController1 thisController;
	public Activity thisActivity;

	public ViewManage viewManager = ViewManage.getInstance();

	public Parser parser = Parser.getInstance();

	public boolean islinked = false;

	public static MainActivity1 instance;

	public TaskManageHolder taskManageHolder;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.thisActivity = this;
		parser.initialize(this);

		// Context context = instance.instance.context;
		instance = this;
		// initImageLoader(getApplicationContext());
		// BaseDataUtils.initBaseData(this);
		// Constant.init();
		// startPushService();

		taskManageHolder = TaskManageHolder.getInstance();
		taskManageHolder.initialize();

		thisActivity.setContentView(R.layout.activity_welinks);

		// UpdateManager manager = new UpdateManager(MainActivity1.this);
		// manager.checkUpdate();
	}

	void linkViewController() {
		if (this.islinked) {
			return;
		}
		parser.initialize(context);
		parser.check();

		this.context = this;
		this.thisView = new MainView1(thisActivity);
		this.thisController = new MainController1(thisActivity);
		this.thisView.thisController = this.thisController;
		this.thisController.thisView = this.thisView;

		linkSubViewController();

		viewManager.mainView1 = this.thisView;

		thisController.initializeListeners();
		thisView.initViews();
		thisController.oncreate();
		thisController.bindEvent();

		this.islinked = true;
	}

	void linkSubViewController() {

		this.thisView.shareSubView = new ShareSubView1(this.thisView);

		this.thisController.shareSubController = new ShareSubController1(this.thisController);

		this.thisController.shareSubController.thisView = this.thisView.shareSubView;

		this.thisView.shareSubView.thisController = this.thisController.shareSubController;

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
			// startActivity(new Intent(MainActivity.this, Debug1Activity.class));
		} else if (item.getItemId() == R.id.debug1_0) {
			Log.d(tag, "debug1.1");
			// startActivity(new Intent(MainActivity.this, Debug1Activity.class));
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
			Intent intent = new Intent(MainActivity1.this, ChatActivity.class);
			intent.putExtra("id", NotificationUtils.message.phone);
			intent.putExtra("type", "point");
			startActivityForResult(intent, R.id.tag_second);
		} else if ("chatGroup".equals(NotificationUtils.showFragment)) {
			Intent intent = new Intent(MainActivity1.this, ChatActivity.class);
			intent.putExtra("id", NotificationUtils.message.gid);
			intent.putExtra("type", "group");
			startActivityForResult(intent, R.id.tag_second);
		} else if ("chatList".equals(NotificationUtils.showFragment)) {
			thisView.mainPagerBody.active();
			thisView.messages_friends_me_PagerBody.inActive();
			thisView.mainPagerBody.flipTo(2);
			thisView.messages_friends_me_PagerBody.flipTo(0);
		}
		NotificationUtils.cancelNotification(MainActivity1.this);
		super.onWindowFocusChanged(hasFocus);
	}
}
