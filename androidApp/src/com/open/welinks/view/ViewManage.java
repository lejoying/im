package com.open.welinks.view;

import android.os.Handler;
import android.util.Log;

public class ViewManage {

	public String tag = "ViewManage";

	public Debug1View debug1View = null;
	public LoginView loginView = null;
	public UserIntimateView userIntimateView = null;

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
			if (userIntimateView != null) {
				Log.d(tag, "notifyView:  UserIntimateView");
				userIntimateView.showCircles();
			}
		}
	}
}