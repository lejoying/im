package com.open.welinks.view;

import java.lang.reflect.Field;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.open.welinks.ChangePasswordActivity;
import com.open.welinks.DynamicListActivity;
import com.open.welinks.GroupListActivity;
import com.open.welinks.R;
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
	public NewChatView newChatView = null;
	public MessagesSubView messagesSubView = null;
	public ShareMessageDetailView shareMessageDetailView = null;

	public ShareSectionView shareSectionView = null;

	public CirclesManageView circlesManageView = null;

	public DynamicListActivity dynamicListActivity = null;
	public GroupListActivity groupListActivity = null;
	public SearchFriendActivity searchFriendActivity = null;
	public ChangePasswordActivity changePasswordActivity = null;

	public Activity thisActivity;
	public DisplayMetrics displayMetrics;

	public Handler handler = new Handler();

	public static ViewManage viewManager;

	public static ViewManage getInstance() {
		if (viewManager == null) {
			viewManager = new ViewManage();
		}
		return viewManager;
	}

	public DisplayImageOptions options;
	public DisplayImageOptions options30;
	public DisplayImageOptions options40;
	public DisplayImageOptions options52;
	public DisplayImageOptions options50;
	public DisplayImageOptions options45;
	public DisplayImageOptions options70;
	public DisplayImageOptions options60;
	public DisplayImageOptions options56;
	
	public int screenWidth;
	public int screenHeight;

	void initialize(Activity thisActivity) {
		this.thisActivity = thisActivity;
		displayMetrics = new DisplayMetrics();
		thisActivity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
		this.screenWidth = displayMetrics.widthPixels;
		this.screenHeight = displayMetrics.heightPixels;
		float density = displayMetrics.density / 1.5f;
		options = new DisplayImageOptions.Builder().cacheInMemory(false).cacheOnDisk(false).considerExifParams(true).bitmapConfig(Bitmap.Config.RGB_565).build();
		options30 = new DisplayImageOptions.Builder().cacheInMemory(false).cacheOnDisk(false).considerExifParams(true).bitmapConfig(Bitmap.Config.RGB_565).displayer(new RoundedBitmapDisplayer((int) (0 * density))).build();
		options40 = new DisplayImageOptions.Builder().cacheInMemory(true).cacheOnDisk(true).considerExifParams(true).showImageOnLoading(R.drawable.default_user_head).showImageForEmptyUri(R.drawable.default_user_head).showImageOnFail(R.drawable.default_user_head).displayer(new RoundedBitmapDisplayer((int) (40 * density))).build();
		options52 = new DisplayImageOptions.Builder().cacheInMemory(true).cacheOnDisk(true).considerExifParams(true).showImageOnLoading(R.drawable.default_user_head).showImageForEmptyUri(R.drawable.default_user_head).showImageOnFail(R.drawable.default_user_head).displayer(new RoundedBitmapDisplayer((int) (52 * density))).build();
		options50 = new DisplayImageOptions.Builder().cacheInMemory(true).cacheOnDisk(true).considerExifParams(true).showImageOnLoading(R.drawable.default_user_head).showImageForEmptyUri(R.drawable.default_user_head).showImageOnFail(R.drawable.default_user_head).displayer(new RoundedBitmapDisplayer((int) (50 * density))).build();
		options45 = new DisplayImageOptions.Builder().cacheInMemory(true).cacheOnDisk(true).considerExifParams(true).showImageOnLoading(R.drawable.default_user_head).showImageForEmptyUri(R.drawable.default_user_head).showImageOnFail(R.drawable.default_user_head).bitmapConfig(Bitmap.Config.RGB_565).displayer(new RoundedBitmapDisplayer((int) (45 * density))).build();
		options70 = new DisplayImageOptions.Builder().cacheInMemory(true).cacheOnDisk(true).considerExifParams(true).showImageOnLoading(R.drawable.default_user_head).showImageForEmptyUri(R.drawable.default_user_head).showImageOnFail(R.drawable.default_user_head).bitmapConfig(Bitmap.Config.RGB_565).displayer(new RoundedBitmapDisplayer((int) (70 * density))).build();
		options60 = new DisplayImageOptions.Builder().cacheInMemory(true).cacheOnDisk(true).considerExifParams(true).showImageOnLoading(R.drawable.default_user_head).showImageForEmptyUri(R.drawable.default_user_head).showImageOnFail(R.drawable.default_user_head).bitmapConfig(Bitmap.Config.RGB_565).displayer(new RoundedBitmapDisplayer((int) (60 * density))).build();
		options56 = new DisplayImageOptions.Builder().cacheInMemory(true).cacheOnDisk(true).considerExifParams(true).showImageOnLoading(R.drawable.default_user_head).showImageForEmptyUri(R.drawable.default_user_head).showImageOnFail(R.drawable.default_user_head).bitmapConfig(Bitmap.Config.RGB_565).displayer(new RoundedBitmapDisplayer((int) (56 * density))).build();
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
			if (chatView != null) {
				chatView.mChatAdapter.notifyDataSetChanged();
			} else if (newChatView != null) {
				newChatView.mChatAdapter.notifyDataSetChanged();
			}
		} else if (viewName.equals("CirclesManageView")) {
			if (circlesManageView != null) {
				if (circlesManageView.thisController.touchStatus.state == circlesManageView.thisController.touchStatus.NORMAL) {
					circlesManageView.showCircles();
				}
			}
		} else if (viewName.equals("ShareSectionNotifyShares")) {
			if (shareSectionView != null) {
				shareSectionView.showShareMessages();
			}
		}
	}

	public static String getErrorLineNumber() {
		StackTraceElement ste = new Throwable().getStackTrace()[1];
		return "line:" + ste.getLineNumber() + ",";
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