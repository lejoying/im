package com.open.welinks.view;

import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;

import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.open.lib.TouchView;
import com.open.welinks.R;
import com.open.welinks.controller.DownloadFileList;
import com.open.welinks.controller.TestListController;
import com.open.welinks.model.Data;

public class TestListView {

	public Data data = Data.getInstance();

	public String tag = "MainView";

	public DisplayMetrics displayMetrics;

	public TestListController thisController;
	public TestListView thisView;
	public Context context;
	public Activity thisActivity;
	public Map<String, View> viewsMap = new HashMap<String, View>();

	public LayoutInflater mInflater;

	public TouchView friendsView;

	public RelativeLayout title_messages_friends_me;

	public RelativeLayout main_container;

	public class ActivityStatus {
		public float SQUARE = 0, SHARE = 1, MESSAGES = 2.0f, FRIENDS = 2.1f, ME = 2.2f;
		public float subState = MESSAGES;
		public float state = SQUARE;
	}

	public ActivityStatus activityStatus = new ActivityStatus();

	public TestListView(Activity thisActivity) {
		this.context = thisActivity;
		this.thisActivity = thisActivity;
		this.thisView = this;

	}

	public void initViews() {

		mInflater = thisActivity.getLayoutInflater();
		displayMetrics = new DisplayMetrics();

		thisActivity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

		thisActivity.setContentView(R.layout.activity_test_list_view);

		main_container = (RelativeLayout) thisActivity.findViewById(R.id.main_container);

		friendsView = (TouchView) thisActivity.findViewById(R.id.friendsContainer);

	}

	public ImageLoader imageLoader = ImageLoader.getInstance();
	public DownloadFileList downloadFileList = DownloadFileList.getInstance();
	public Gson gson = new Gson();

}
