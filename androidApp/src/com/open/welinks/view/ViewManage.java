package com.open.welinks.view;

import com.open.welinks.DynamicListActivity;
import com.open.welinks.GroupListActivity;
import com.open.welinks.SearchFriendActivity;

import android.os.Handler;
import android.util.Log;

public class ViewManage {

	public String tag = "ViewManage";

	public Debug1View debug1View = null;
	public LoginView loginView = null;
	public MainView mainView = null;
	public MeSubView meSubView = null;
	public SquareSubView squareSubView = null;
	public ShareSubView shareSubView = null;
	public ChatView chatView = null;
	public MessagesSubView messagesSubView = null;
	public ShareMessageDetailView shareMessageDetailView = null;

	public DynamicListActivity dynamicListActivity = null;
	public GroupListActivity groupListActivity = null;
	public SearchFriendActivity searchFriendActivity = null;

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
				if (dynamicListActivity.userEventListAdapter != null) {
					dynamicListActivity.userEventListAdapter.notifyDataSetChanged();
				}
				if (dynamicListActivity.groupEventListAdapter != null) {
					dynamicListActivity.groupEventListAdapter.notifyDataSetChanged();
				}
			}
		} else if (viewName.equals("MeSubView")) {
			if (meSubView != null) {
				meSubView.setUserData();
			}
		} else if (viewName.equals("GroupListActivity")) {
			if (groupListActivity != null) {
				groupListActivity.groupListAdapter.notifyDataSetChanged();
			}
		} else if (viewName.equals("ShareSubView")) {
			if (mainView.shareSubView != null) {
				mainView.shareSubView.setGroupsDialogContent();
			}
		}
	}
}