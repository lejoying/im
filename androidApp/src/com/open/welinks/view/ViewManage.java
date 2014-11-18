package com.open.welinks.view;

import java.lang.reflect.Field;

import android.content.Context;
import android.os.Handler;
import android.util.Log;

import com.open.welinks.ChangePasswordActivity;
import com.open.welinks.DynamicListActivity;
import com.open.welinks.GroupListActivity;
import com.open.welinks.SearchFriendActivity;

public class ViewManage {

	public String tag = "ViewManage";

	public LoginView loginView = null;
	public MainView mainView = null;
	public MeSubView meSubView = null;
	public FriendsSubView friendsSubView = null;
	public SquareSubView squareSubView = null;
	public ShareSubView shareSubView = null;
	public ChatView chatView = null;
	public MessagesSubView messagesSubView = null;
	public ShareMessageDetailView shareMessageDetailView = null;

	public CirclesManageView circlesManageView = null;

	public DynamicListActivity dynamicListActivity = null;
	public GroupListActivity groupListActivity = null;
	public SearchFriendActivity searchFriendActivity = null;
	public ChangePasswordActivity changePasswordActivity = null;

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
		} else if (viewName.equals("ShareSubViewMessage")) {
			if (mainView.shareSubView != null) {
				mainView.shareSubView.showShareMessages();
			}
		} else if (viewName.equals("ShareSubViewConver")) {
			if (mainView.shareSubView != null) {
				mainView.shareSubView.setConver();
			}
		} else if (viewName.equals("SquareSubViewMessage")) {
			if (mainView.shareSubView != null) {
				mainView.squareSubView.showSquareMessages(true);
			}
		} else if (viewName.equals("ChangePasswordActivitySuccess")) {
			if (changePasswordActivity != null) {
				changePasswordActivity.modifySuccess();
			}
		} else if (viewName.equals("ChangePasswordActivityFailed")) {
			if (changePasswordActivity != null) {
				changePasswordActivity.modifyFailed();
			}
		} else if (viewName.equals("MessagesSubView")) {
			if (messagesSubView != null) {
				messagesSubView.showMessagesSequence();
			}
		} else if (viewName.equals("ChatMessage")) {
			if (messagesSubView != null) {
				chatView.mChatAdapter.notifyDataSetChanged();
			}
		} else if (viewName.equals("CirclesManageView")) {
			if (circlesManageView != null) {
				if (circlesManageView.thisController.touchStatus.state == circlesManageView.thisController.touchStatus.NORMAL) {
					circlesManageView.showCircles();
				}
			}
		}
	}
	public static int getStatusBarHeight(Context context) {
		Class<?> c = null;
		Object obj = null;
		Field field = null;
		int x = 0, statusBarHeight = 0;
		try {
			c = Class.forName("com.android.internal.R$dimen");
			obj = c.newInstance();
			field = c.getField("status_bar_height");
			x = Integer.parseInt(field.get(obj).toString());
			statusBarHeight = context.getResources().getDimensionPixelSize(x);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		return statusBarHeight;
	}
}