package com.open.welinks;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;

import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.open.welinks.controller.TestListController;
import com.open.welinks.model.Data;
import com.open.welinks.view.TestListView;
import com.open.welinks.view.ViewManage;

public class TestListActivity extends Activity {

	public Data data = Data.getInstance();
	public String tag = "TestListActivity";

	public Context context;
	public TestListView thisView;
	public TestListController thisController;
	public Activity thisActivity;

	public ViewManage viewManager = ViewManage.getInstance();

	@SuppressWarnings("unused")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (Config.DEVELOPER_MODE && Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
			StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectAll().penaltyDialog().build());
			StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder().detectAll().penaltyDeath().build());
		}
		initImageLoader(getApplicationContext());
		linkViewController();
	}

	public static void initImageLoader(Context context) {
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context).threadPriority(Thread.NORM_PRIORITY - 2).denyCacheImageMultipleSizesInMemory().diskCacheFileNameGenerator(new Md5FileNameGenerator()).diskCacheSize(50 * 1024 * 1024).tasksProcessingOrder(QueueProcessingType.LIFO).writeDebugLogs().build();
		ImageLoader.getInstance().init(config);
	}

	public static class Config {
		public static final boolean DEVELOPER_MODE = false;
	}

	void linkViewController() {
		this.thisActivity = this;
		this.context = this;
		this.thisView = new TestListView(thisActivity);
		this.thisController = new TestListController(thisActivity);
		this.thisView.thisController = this.thisController;
		this.thisController.thisView = this.thisView;

		thisController.initializeListeners();
		thisView.initViews();
		thisController.oncreate();
		thisController.bindEvent();
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
			startActivity(new Intent(TestListActivity.this, Debug1Activity.class));
		} else if (item.getItemId() == R.id.debug1_0) {
			Log.d(tag, "debug1.1");
			startActivity(new Intent(TestListActivity.this, Debug1Activity.class));
		}
		return true;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		super.onTouchEvent(event);
		return thisController.onTouchEvent(event);
	}

	@Override
	protected void onResume() {
		super.onResume();
		thisController.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
		thisController.onPause();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		thisController.onActivityResult(requestCode, resultCode, data);
		super.onActivityResult(requestCode, resultCode, data);
	}
}
