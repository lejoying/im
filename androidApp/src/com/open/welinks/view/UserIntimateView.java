package com.open.welinks.view;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.rebound.SpringConfig;
import com.open.lib.viewbody.BodyCallback;
import com.open.lib.viewbody.ListBody;
import com.open.lib.viewbody.ListBody.MyListItemBody;
import com.open.lib.viewbody.PagerBody;
import com.open.welinks.R;
import com.open.welinks.controller.UserIntimateController;
import com.open.welinks.model.Data;
import com.open.welinks.model.Data.Relationship.Circle;
import com.open.welinks.model.Data.Relationship.Friend;
import com.open.welinks.utils.MCImageUtils;

public class UserIntimateView {

	public Data data = Data.getInstance();

	public String tag = "UserIntimateView";

	public DisplayMetrics displayMetrics;

	public UserIntimateController thisController;
	public UserIntimateView thisView;
	public Context context;
	public Activity thisActivity;

	public LayoutInflater mInflater;

	public RelativeLayout friendsMenuView;
	public RelativeLayout messagesMenuView;
	public RelativeLayout meMenuView;

	public RelativeLayout squareMenuView;
	public RelativeLayout shareMenuView;
	public RelativeLayout messages_friends_me_menuView;

	public ImageView messages_friends_me_pager_indicator;
	public ImageView main_pager_indicator;

	public ImageView pager_indicator;

	public RelativeLayout friendsView;

	public RelativeLayout messagesView;
	public RelativeLayout meView;

	public RelativeLayout messages_friends_me_View;
	public RelativeLayout shareView;
	public RelativeLayout squareView;

	public RelativeLayout main_container;

	public ImageView userHeadImageView;
	public TextView userNickNameView;
	public TextView userBusinessView;

	public ImageView mAppIconToNameView;
	public View mRootView;

	public Map<String, CircleBody> viewsMap = new HashMap<String, CircleBody>();
	public ListBody friendListBody;

	public PagerBody messages_friends_me_PagerBody;

	public PagerBody mainPagerBody;

	public Map<String, CircleHolder> circleHolders = new Hashtable<String, CircleHolder>();

	public List<String> circles;
	public Map<String, Circle> circlesMap;
	public Map<String, Friend> friendsMap;

	public static final SpringConfig ORIGAMI_SPRING_CONFIG = SpringConfig.fromOrigamiTensionAndFriction(5, 7);

	public class ActivityStatus {
		public float SQUARE = 0, SHARE = 1, MESSAGES = 2.0f, FRIENDS = 2.1f, ME = 2.2f;
		public float state = MESSAGES;
	}

	public ActivityStatus activityStatus = new ActivityStatus();

	public UserIntimateView(Activity thisActivity) {
		this.context = thisActivity;
		this.thisActivity = thisActivity;
		this.thisView = this;
	}

	public void initData() {
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

		main_pager_indicator = (ImageView) thisActivity.findViewById(R.id.main_pager_indicator);
		int main_pager_indicator_trip = (int) (44 * displayMetrics.density);

		mainPagerBody = new PagerBody();
		mainPagerBody.tag = "mainPagerBody";
		mainPagerBody.pager_indicator = main_pager_indicator;
		mainPagerBody.pager_indicator_trip = main_pager_indicator_trip;
		mainPagerBody.initialize(displayMetrics, myBodyCallback);

		main_container.addView(squareView);
		mainPagerBody.addChildView(squareView);

		main_container.addView(shareView);
		mainPagerBody.addChildView(shareView);

		main_container.addView(messages_friends_me_View);
		mainPagerBody.addChildView(messages_friends_me_View);

		friendsMenuView = (RelativeLayout) messages_friends_me_View.findViewById(R.id.rl_intimatefriends);
		messagesMenuView = (RelativeLayout) messages_friends_me_View.findViewById(R.id.rl_chatMessagesList);
		meMenuView = (RelativeLayout) messages_friends_me_View.findViewById(R.id.rl_userInfomation);
		
		squareMenuView = (RelativeLayout) thisActivity.findViewById(R.id.square_menu_view);
		shareMenuView = (RelativeLayout) thisActivity.findViewById(R.id.share_menu_view);
		messages_friends_me_menuView = (RelativeLayout) thisActivity.findViewById(R.id.messages_friends_me_menu_view);

		messagesView = (RelativeLayout) messages_friends_me_View.findViewById(R.id.rl_chatMessagesContent);
		friendsView = (RelativeLayout) messages_friends_me_View.findViewById(R.id.rl_intimateFriendsContent);
		friendListBody = new ListBody();
		friendListBody.initialize(displayMetrics, friendsView);

		meView = (RelativeLayout) messages_friends_me_View.findViewById(R.id.rl_userInfomationContent);

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

		userHeadImageView = (ImageView) thisActivity.findViewById(R.id.iv_headImage);
		userNickNameView = (TextView) thisActivity.findViewById(R.id.tv_userNickname);
		userBusinessView = (TextView) thisActivity.findViewById(R.id.tv_userMainBusiness);

		mAppIconToNameView = (ImageView) messages_friends_me_View.findViewById(R.id.appIconToName);
		mRootView = mAppIconToNameView;

	}

	class MyBodyCallback extends BodyCallback {
		@Override
		public void onStart(String bodyTag, float variable) {
			if (bodyTag.equals("messages_friends_me_PagerBody")) {
				thisView.friendListBody.inActive();
				if (variable == 2) {
					thisController.mScaleSpring.setEndValue(1);
				}
			}
		}

		@Override
		public void onFixed(String bodyTag, float variable) {
			if (bodyTag.equals("messages_friends_me_PagerBody")) {
				if (variable == 0) {
					thisView.activityStatus.state = thisView.activityStatus.MESSAGES;
				} else if (variable == 1) {
					thisView.activityStatus.state = thisView.activityStatus.FRIENDS;
					thisView.friendListBody.active();
				} else if (variable == 2) {
					thisView.activityStatus.state = thisView.activityStatus.ME;
					thisController.mScaleSpring.setEndValue(0);
				}
			}

		}
	}

	public float speedY = 0;
	public float ratio = 0.00008f;

	public void showCircles() {

		circles = data.relationship.circles;
		circlesMap = data.relationship.circlesMap;
		friendsMap = data.relationship.friendsMap;

		this.friendListBody.listItemsSequence.clear();

		for (int i = 0; i < circles.size(); i++) {
			Circle circle = circlesMap.get(circles.get(i));

			CircleBody circleBody = null;
			circleBody = new CircleBody(this.friendListBody);
			circleBody.initialize();
			circleBody.setContent(circle);

			this.friendListBody.listItemsSequence.add("circle#" + circle.rid);
			this.friendListBody.listItemBodiesMap.put("circle#" + circle.rid, circleBody);

			RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, (int) (260 * displayMetrics.density));
			circleBody.y = 270 * displayMetrics.density * i + 2 * displayMetrics.density;
			circleBody.cardView.setY(circleBody.y);
			circleBody.cardView.setX(0);

			this.friendListBody.containerView.addView(circleBody.cardView, layoutParams);
			this.friendListBody.height = this.friendListBody.height + 270 * displayMetrics.density;
			Log.d(tag, "addView");

		}
	}

	public class CircleBody extends MyListItemBody {

		CircleBody(ListBody listBody) {
			listBody.super();
		}

		public List<String> friendsSequence = new ArrayList<String>();
		public Map<String, FriendBody> friendBodiesMap = new HashMap<String, FriendBody>();

		public View cardView = null;
		public TextView leftTopText = null;

		public View initialize() {

			this.cardView = mInflater.inflate(R.layout.view_control_circle_card, null);
			this.leftTopText = (TextView) this.cardView.findViewById(R.id.leftTopText);

			this.leftTopText.setOnClickListener(thisController.mOnClickListener);

			super.initialize(cardView);
			return cardView;
		}

		public void setContent(Circle circle) {
			this.leftTopText.setText(circle.name);
			this.leftTopText.setTag(circle.name);

			this.friendsSequence.clear();
			for (int i = 0; i < circle.friends.size(); i++) {
				String phone = circle.friends.get(i);
				Friend friend = friendsMap.get(phone);
				if (this.friendBodiesMap.get(phone) == null) {

				}
			}
		}
	}

	public class FriendBody {
		public View cardView = null;

		public ImageView headView;
		public TextView nickNameView;

		public View Initialize() {
			cardView = mInflater.inflate(R.layout.circles_gridpage_item, null);

			return cardView;
		}

		public void setData(Friend friend) {

		}
	}

	public void generateViews() {
		// normalShow.clear();
		// if (viewsMap.get("button#findmore") == null) {
		// View findMoreFriendButtonView = generateFindMoreFriendButtonView();
		// findMoreFriendButtonView.setTag(46);
		// viewsMap.put("button#findmore", findMoreFriendButtonView);
		// }
		// normalShow.add("button#findmore");
		//
		// View newFriendButtonView = viewsMap.get("button#newfriend");
		// if (newFriendButtonView == null) {
		// newFriendButtonView = generateNewFriendButtonView();
		// newFriendButtonView.setTag(46);
		// viewsMap.put("button#newfriend", newFriendButtonView);
		// }
		//
		// int newFriendsCount = 1;
		// notifyNewFriendButtonView(newFriendButtonView, newFriendsCount);
		// normalShow.add("button#newfriend");
		//
		// // circles.clear();
		// // generate circles
		// for (int i = 0; i < circles.size(); i++) {
		// Circle circle = circlesMap.get(circles.get(i));
		//
		// View circleView = viewsMap.get("group#" + circle.rid);
		// if (circleView == null) {
		// CircleHolder circleHolder = new CircleHolder();
		// circleHolders.put("group#" + circle.rid, circleHolder);
		// circleView = generateCircleView();
		// viewsMap.put("group#" + circle.rid, circleView);
		// circleView.setTag(265);// 262
		// }
		// notifyCircleView(circleView, circle, circleHolders.get("group#" +
		// circle.rid));
		//
		// normalShow.add("group#" + circle.rid);
		// // circles.add("group#" + circle.rid);
		// }
	}

	public void notifyCircleView(final View circleView, Circle circle, CircleHolder circleHolder) {
		TextView groupName = (TextView) circleView.findViewById(R.id.panel_name);
		groupName.setText(circle.name + "( " + circle.friends.size() + " )");
		RelativeLayout friendsPanelView = (RelativeLayout) circleView.findViewById(R.id.rl_friendsPanel);
		for (int i = 0; i < circle.friends.size(); i++) {
			final Friend friend = friendsMap.get(circle.friends.get(i));
			FriendHolder friendHolder = new FriendHolder();
			friendHolder.phone = friend.phone;
			int index = circleHolder.friendHolders.indexOf(friendHolder);
			friendHolder = (index != -1 ? circleHolder.friendHolders.remove(index) : null);
			View convertView = null;
			if (friendHolder == null) {
				convertView = generateFriendView(friend);
				friendHolder = new FriendHolder();
				friendHolder.phone = friend.phone;
				friendHolder.view = convertView;
			}
			friendsPanelView.addView(convertView);
			circleHolder.friendHolders.add(i, friendHolder);
		}
		resolveFriendsPositions(circleHolder);
		setFriendsPositions(circleHolder);
	}

	public void resolveFriendsPositions(CircleHolder circleHolder) {
		for (int i = 0; i < circleHolder.friendHolders.size(); i++) {
			FriendHolder friendHolder = circleHolder.friendHolders.get(i);
			friendHolder.index = i;
		}
	}

	public void setFriendsPositions(CircleHolder circleHolder) {
		for (int i = 0; i < circleHolder.friendHolders.size(); i++) {
			FriendHolder friendHolder = circleHolder.friendHolders.get(i);

			RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams((int) (displayMetrics.density * 55f), RelativeLayout.LayoutParams.WRAP_CONTENT);
			params.rightMargin = -Integer.MAX_VALUE;
			params.topMargin = friendHolder.position.y;
			params.leftMargin = friendHolder.position.x;
			friendHolder.view.setLayoutParams(params);
		}
	}

	View generateFriendView(Friend friend) {
		View convertView = mInflater.inflate(R.layout.circles_gridpage_item, null);
		final ImageView head = (ImageView) convertView.findViewById(R.id.iv_head);
		TextView nickname = (TextView) convertView.findViewById(R.id.tv_nickname);
		if (!friend.alias.equals("") && friend.alias != null) {
			nickname.setText(friend.alias);
		} else {
			nickname.setText(friend.nickName);
		}
		head.setImageBitmap(MCImageUtils.getCircleBitmap(BitmapFactory.decodeResource(thisActivity.getResources(), R.drawable.face_man), true, 5, Color.WHITE));
		return convertView;
	}

	public View generateCircleView() {
		View circleView = mInflater.inflate(R.layout.view_control_circle_card, null);

		return circleView;
	}

	class CircleHolder {
		public List<FriendHolder> friendHolders = new ArrayList<FriendHolder>();
	}

	class Position {
		int x = 0;
		int y = 0;
	}

	class FriendHolder {
		Position position;
		View view;
		String phone = "";
		int index;

	}

	void notifyNewFriendButtonView(View newFriendButtonView, int newFriendsCount) {
		TextView newFriendButton = (TextView) newFriendButtonView.findViewById(R.id.tv_type);
		ImageView findMoreFriendIcon = (ImageView) newFriendButtonView.findViewById(R.id.iv_icon);
		findMoreFriendIcon.setImageResource(R.drawable.header);
		if (newFriendsCount != 0) {
			newFriendButton.setText("新的好友(" + newFriendsCount + ")");
		} else {
			newFriendButton.setText("新的好友");
		}
	}

	View generateNewFriendButtonView() {
		View newFriendButtonView = mInflater.inflate(R.layout.circle_item_button_layout, null);
		return newFriendButtonView;
	}

	View generateFindMoreFriendButtonView() {
		View findMoreFriendButtonView = mInflater.inflate(R.layout.circle_item_button_layout, null);
		TextView findMoreFriendButton = (TextView) findMoreFriendButtonView.findViewById(R.id.tv_type);
		ImageView findMoreFriendIcon = (ImageView) findMoreFriendButtonView.findViewById(R.id.iv_icon);
		findMoreFriendIcon.setImageResource(R.drawable.dialog_search);
		findMoreFriendButton.setText("搜索好友");

		return findMoreFriendButtonView;
	}
}
