package com.open.welinks.view;

import android.os.Handler;
import android.util.Log;

public class ViewManager {

	public String tag = "ViewManager";

	public Debug1View debug1View = null;
	public LoginView loginView = null;
	public UserIntimateView userIntimateView = null;

	public Handler handler = new Handler();

	public static ViewManager viewManager;

	public static ViewManager getIntance() {
		if (viewManager == null) {
			viewManager = new ViewManager();
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
			}
		}
	}
}
