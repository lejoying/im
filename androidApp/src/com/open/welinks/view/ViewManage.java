package com.open.welinks.view;

import com.open.welinks.DynamicListActivity;

import android.os.Handler;
import android.util.Log;

public class ViewManage {

	public String tag = "ViewManage";

	public Debug1View debug1View = null;
	public LoginView loginView = null;
	public MainView mainView = null;
	public MeSubView meSubView;
	public ChatView chatView = null;

	public DynamicListActivity dynamicListActivity = null;

	public DownloadOssFileView downloadOssFileView = null;

	public Handler handler = new Handler();

	public static ViewManage viewManager;

	public static ViewManage getInstance() {
		if (viewManager == null) {
			viewManager = new ViewManage();
		}
		return viewManager;
	}

	public void postNotifyView(final String viewName) {
		handler.post(new Runnable() {
			@Override
			public void run() {
				viewManager.notifyView(viewName);
			}
		});
	}

	public void notifyView(String viewName) {
		if (viewName.equals("UserIntimateView")) {
			if (mainView != null) {
				Log.d(tag, "notifyView:  UserIntimateView");
				mainView.friendsSubView.showCircles();
			}
		} else if (viewName.equals("DynamicListActivity")) {
			if (dynamicListActivity != null) {
				dynamicListActivity.userEventListAdapter.notifyDataSetChanged();
			}
		} else if (viewName.equals("MeSubView")) {
			if (meSubView != null) {
				meSubView.setUserData();
			}
		}
	}
}