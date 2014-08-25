package com.open.welinks.view;

import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.open.lib.TouchView;
import com.open.lib.viewbody.BodyCallback;
import com.open.lib.viewbody.PagerBody;
import com.open.welinks.R;
import com.open.welinks.controller.DownloadFileList;
import com.open.welinks.controller.MainController;
import com.open.welinks.model.Data;

public class MainView {

	public Data data = Data.getInstance();

	public String tag = "MainView";

	public DisplayMetrics displayMetrics;

	public MainController thisController;
	public MainView thisView;
	public Context context;
	public Activity thisActivity;
	public Map<String, View> viewsMap = new HashMap<String, View>();

	public SquareSubView squareSubView;
	public ShareSubView shareSubView;
	public MessagesSubView messagesSubView;
	public FriendsSubView friendsSubView;
	public MeSubView meSubView;

	public LayoutInflater mInflater;

	public RelativeLayout friendsMenuView;
	public RelativeLayout messagesMenuView;
	public RelativeLayout meMenuView;

	public RelativeLayout squareMenuView;
	public RelativeLayout shareMenuView;
	public RelativeLayout messages_friends_me_menuView;

	public ImageView messages_friends_me_pager_indicator;
	public ImageView main_pager_indicator;

	public TouchView friendsView;
	public RelativeLayout messagesView;
	public RelativeLayout meView;

	public RelativeLayout messages_friends_me_View;
	public RelativeLayout shareView;
	public RelativeLayout squareView;

	public RelativeLayout title_messages_friends_me;
	public RelativeLayout title_share;
	public RelativeLayout title_square;

	public RelativeLayout main_container;

	public PagerBody messages_friends_me_PagerBody;

	public PagerBody mainPagerBody;

	public class ActivityStatus {
		public float SQUARE = 0, SHARE = 1, MESSAGES = 2.0f, FRIENDS = 2.1f, ME = 2.2f;
		public float subState = MESSAGES;
		public float state = SQUARE;
	}

	public ActivityStatus activityStatus = new ActivityStatus();

	public MainView(Activity thisActivity) {
		this.context = thisActivity;
		this.thisActivity = thisActivity;
		this.thisView = this;

	}

	public void initViews() {

		mInflater = thisActivity.getLayoutInflater();
		displayMetrics = new DisplayMetrics();

		MyBodyCallback myBodyCallback = new MyBodyCallback();

		thisActivity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

		thisActivity.setContentView(R.layout.activity_welinks);

		main_container = (RelativeLayout) thisActivity.findViewById(R.id.main_container);

		squareView = (RelativeLayout) mInflater.inflate(R.layout.view_main_square, null);
		shareView = (RelativeLayout) mInflater.inflate(R.layout.view_main_share, null);
		messages_friends_me_View = (RelativeLayout) mInflater.inflate(R.layout.view_main_messages_friends_me, null);

		title_square = (RelativeLayout) squareView.findViewById(R.id.title_square);
		title_share = (RelativeLayout) shareView.findViewById(R.id.title_share);
		title_messages_friends_me = (RelativeLayout) messages_friends_me_View.findViewById(R.id.title_messages_friends_me);

		main_pager_indicator = (ImageView) thisActivity.findViewById(R.id.main_pager_indicator);
		int main_pager_indicator_trip = (int) (44 * displayMetrics.density);

		mainPagerBody = new PagerBody();
		mainPagerBody.tag = "mainPagerBody";
		mainPagerBody.pager_indicator = main_pager_indicator;
		mainPagerBody.pager_indicator_trip = main_pager_indicator_trip;
		mainPagerBody.initialize(displayMetrics, myBodyCallback);

		main_container.addView(squareView);
		mainPagerBody.addChildView(squareView);
		mainPagerBody.setTitleView(title_square, 0);

		main_container.addView(shareView);
		mainPagerBody.addChildView(shareView);
		mainPagerBody.setTitleView(title_share, 1);

		main_container.addView(messages_friends_me_View);
		mainPagerBody.addChildView(messages_friends_me_View);
		mainPagerBody.setTitleView(title_messages_friends_me, 2);

		friendsMenuView = (RelativeLayout) messages_friends_me_View.findViewById(R.id.rl_intimatefriends);
		messagesMenuView = (RelativeLayout) messages_friends_me_View.findViewById(R.id.rl_chatMessagesList);
		meMenuView = (RelativeLayout) messages_friends_me_View.findViewById(R.id.rl_userInfomation);

		squareMenuView = (RelativeLayout) thisActivity.findViewById(R.id.square_menu_view);
		shareMenuView = (RelativeLayout) thisActivity.findViewById(R.id.share_menu_view);

		messages_friends_me_menuView = (RelativeLayout) thisActivity.findViewById(R.id.messages_friends_me_menu_view);

		messagesView = (RelativeLayout) messages_friends_me_View.findViewById(R.id.messagesContainer);

		friendsView = (TouchView) messages_friends_me_View.findViewById(R.id.friendsContainer);

		meView = (RelativeLayout) messages_friends_me_View.findViewById(R.id.meContainer);

		messages_friends_me_pager_indicator = (ImageView) messages_friends_me_View.findViewById(R.id.messages_friends_me_pager_indicator);
		int messages_friends_me_pager_indicator_trip = (int) (displayMetrics.widthPixels - (20 * displayMetrics.density)) / 3;
		ViewGroup.LayoutParams params2 = messages_friends_me_pager_indicator.getLayoutParams();
		params2.height = (int) (32 * displayMetrics.density);
		params2.width = messages_friends_me_pager_indicator_trip;
		messages_friends_me_pager_indicator.setLayoutParams(params2);

		messages_friends_me_PagerBody = new PagerBody();
		messages_friends_me_PagerBody.tag = "messages_friends_me_PagerBody";
		messages_friends_me_PagerBody.pager_indicator = messages_friends_me_pager_indicator;
		messages_friends_me_PagerBody.pager_indicator_trip = messages_friends_me_pager_indicator_trip;
		messages_friends_me_PagerBody.initialize(displayMetrics, myBodyCallback);

		messages_friends_me_PagerBody.addChildView(messagesView);
		messages_friends_me_PagerBody.addChildView(friendsView);
		messages_friends_me_PagerBody.addChildView(meView);
		messages_friends_me_PagerBody.inActive();

		
		this.thisView.squareSubView.initViews();
		this.thisView.shareSubView.initViews();
		this.thisView.messagesSubView.initViews();
		this.thisView.friendsSubView.initViews();
		this.thisView.meSubView.initViews();


	}

	class MyBodyCallback extends BodyCallback {
		@Override
		public void onStart(String bodyTag, float variable) {
			if (bodyTag.equals("messages_friends_me_PagerBody")) {

				thisView.friendsSubView.friendListBody.inActive();
				if (variable == 2) {
					thisView.meSubView.mMePageAppIconScaleSpring.setEndValue(1);
				}
			}
		}

		int lastOnFlipingIndex = -1;

		@Override
		public void onFlipping(String bodyTag, float variable) {
			if (bodyTag.equals("messages_friends_me_PagerBody")) {
				if (variable == 0) {
				} else if (variable == 1) {
				} else if (variable == 2) {
				}
				lastOnFlipingIndex = -1;
			} else if (bodyTag.equals("mainPagerBody")) {
				if (variable == 0) {
					lastOnFlipingIndex = -1;
				} else if (variable == 1) {
					lastOnFlipingIndex = 1;
				} else if (variable == 2) {
					if (lastOnFlipingIndex == 1) {
						Log.d(tag, "bodyTag onFliping:" + variable + "  lastOnFlipingIndex: " + lastOnFlipingIndex);
						thisView.messages_friends_me_PagerBody.active();
						thisView.mainPagerBody.inActive();
					}
					lastOnFlipingIndex = -1;
				}

			}
		}

		@Override
		public void onFixed(String bodyTag, float variable) {
			if (bodyTag.equals("messages_friends_me_PagerBody")) {
				if (variable == 0) {
					thisView.activityStatus.state = thisView.activityStatus.MESSAGES;
					thisView.activityStatus.subState = thisView.activityStatus.MESSAGES;
					thisView.messagesSubView.messageListBody.active();
				} else if (variable == 1) {
					thisView.activityStatus.state = thisView.activityStatus.FRIENDS;
					thisView.activityStatus.subState = thisView.activityStatus.FRIENDS;
					thisView.friendsSubView.friendListBody.active();
				} else if (variable == 2) {
					thisView.activityStatus.state = thisView.activityStatus.ME;
					thisView.activityStatus.subState = thisView.activityStatus.ME;
				}
			} else if (bodyTag.equals("mainPagerBody")) {
				if (variable == 0) {
					thisView.activityStatus.state = thisView.activityStatus.SQUARE;
				} else if (variable == 1) {
					thisView.activityStatus.state = thisView.activityStatus.SHARE;
					thisView.shareSubView.shareMessageListBody.active();
				} else if (variable == 2) {
					thisView.activityStatus.state = thisView.activityStatus.subState;
					thisView.mainPagerBody.inActive();
					thisView.messages_friends_me_PagerBody.active();
					if (thisView.activityStatus.state == thisView.activityStatus.FRIENDS) {
						thisView.friendsSubView.friendListBody.active();
					} else if (thisView.activityStatus.state == thisView.activityStatus.ME) {
						thisView.meSubView.mMePageAppIconScaleSpring.setEndValue(0);
					}

				}
			}
		}

		@Override
		public boolean onOverRange(String bodyTag, float variable) {
			if (bodyTag.equals("messages_friends_me_PagerBody")) {
				if (variable == -1) {
					Log.d(tag, "messages_friends_me_PagerBody onOverRange");

					thisView.mainPagerBody.active();
					thisView.messages_friends_me_PagerBody.inActive();
					return true;
				}
			}
			return false;
		}

	}

	public ImageLoader imageLoader = ImageLoader.getInstance();
	public DownloadFileList downloadFileList = DownloadFileList.getInstance();
	public Gson gson = new Gson();

}
