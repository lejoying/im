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
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.open.welinks.R;
import com.open.welinks.controller.UserIntimateController;
import com.open.welinks.model.Data;
import com.open.welinks.model.Data.Relationship.Circle;
import com.open.welinks.model.Data.Relationship.Friend;
import com.open.welinks.utils.MCImageUtils;

public class UserIntimateView {

	public UserIntimateController thisController;
	public Context context;
	public Activity thisActivity;

	public Data data = Data.getInstance();
	public LayoutInflater mInflater;

	public int screenHeight, screenWidth, screenDip;
	public float screenDensity = 1.0f;

	public ImageView currentMenuOptionSelectedStatusImage;
	public RelativeLayout currentShowContentView;

	public RelativeLayout intimateFriendsMenuOptionView;
	public RelativeLayout chatMessagesListMenuOptionView;
	public RelativeLayout userInfomationMenuOptionView;
	public ImageView intimateFriendsMenuOptionStatusImage;
	public ImageView chatMessagesListMenuOptionStatusImage;
	public ImageView userInfomationMenuOptionStatusImage;

	public RelativeLayout intimateFriendsContentView;
	public RelativeLayout chatMessagesListContentView;
	public RelativeLayout userInfomationContentView;

	public ImageView userHeadImageView;
	public TextView userNickNameView;
	public TextView userBusinessView;

	public Map<String, View> views = new HashMap<String, View>();
	public List<String> normalShow = new ArrayList<String>();

	public Map<String, CircleHolder> circleHolders = new Hashtable<String, CircleHolder>();

	public List<String> circles;
	public Map<String, Circle> circlesMap;
	public Map<String, Friend> friendsMap;

	public UserIntimateView(Activity thisActivity) {
		this.context = thisActivity;
		this.thisActivity = thisActivity;
	}

	public void initData() {
		mInflater = thisActivity.getLayoutInflater();
		DisplayMetrics displayMetrics = new DisplayMetrics();
		thisActivity.getWindowManager().getDefaultDisplay()
				.getMetrics(displayMetrics);
		screenDensity = displayMetrics.density;
		screenDip = (int) (40 * screenDensity + 0.5f);
		screenHeight = displayMetrics.heightPixels;
		screenWidth = displayMetrics.widthPixels;

		baseLeft = (int) (screenWidth - (dp2px(20) * 2) - (dp2px(55) * 4)) / 8;
		vWidth = (int) (screenWidth - (dp2px(20) * 2));
		headSpace = baseLeft * 2;
		head = (int) dp2px(55f);

		currentMenuOptionSelectedStatusImage = intimateFriendsMenuOptionStatusImage;
		currentShowContentView = intimateFriendsContentView;

	}

	public void initViews() {
		thisActivity.setContentView(R.layout.activity_userintimate);
		intimateFriendsMenuOptionView = (RelativeLayout) thisActivity
				.findViewById(R.id.rl_intimatefriends);
		chatMessagesListMenuOptionView = (RelativeLayout) thisActivity
				.findViewById(R.id.rl_chatMessagesList);
		userInfomationMenuOptionView = (RelativeLayout) thisActivity
				.findViewById(R.id.rl_userInfomation);
		intimateFriendsMenuOptionStatusImage = (ImageView) thisActivity
				.findViewById(R.id.iv_intimatefriends_status);
		chatMessagesListMenuOptionStatusImage = (ImageView) thisActivity
				.findViewById(R.id.iv_chatMessagesList_status);
		userInfomationMenuOptionStatusImage = (ImageView) thisActivity
				.findViewById(R.id.iv_userInfomation_status);
		intimateFriendsContentView = (RelativeLayout) thisActivity
				.findViewById(R.id.rl_intimateFriendsContent);
		chatMessagesListContentView = (RelativeLayout) thisActivity
				.findViewById(R.id.rl_chatMessagesContent);
		userInfomationContentView = (RelativeLayout) thisActivity
				.findViewById(R.id.rl_userInfomationContent);

		userHeadImageView = (ImageView) thisActivity
				.findViewById(R.id.iv_headImage);
		userNickNameView = (TextView) thisActivity
				.findViewById(R.id.tv_userNickname);
		userBusinessView = (TextView) thisActivity
				.findViewById(R.id.tv_userMainBusiness);
	}

	public void notifyViews() {

		intimateFriendsContentView.removeAllViews();

		circles = data.relationship.circles;
		circlesMap = data.relationship.circlesMap;
		friendsMap = data.relationship.friendsMap;

		userHeadImageView.setImageBitmap(MCImageUtils.getCircleBitmap(
				BitmapFactory.decodeResource(thisActivity.getResources(),
						R.drawable.face_man), true, 5, Color.WHITE));
		userNickNameView.setText(data.userInformation.currentUser.nickName);
		userBusinessView.setText(data.userInformation.currentUser.mainBusiness);
		// generate circle page views
		generateViews();
		// init circle page views position
		int top = 0;
		for (int i = 0; i < normalShow.size(); i++) {
			View v = views.get(normalShow.get(i));
			if (v.getParent() == null) {
				intimateFriendsContentView.addView(v, i);
			}
			int height = (int) dp2px(((Integer) v.getTag()).floatValue());
			RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
					LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
			if (i == normalShow.size() - 1) {
				layoutParams.setMargins(layoutParams.leftMargin, top + 10,
						layoutParams.rightMargin, 50);
			} else {
				layoutParams.setMargins(layoutParams.leftMargin, top,
						layoutParams.rightMargin, -Integer.MAX_VALUE);
			}
			if (i < 2) {
				top = top + height + 10;
			} else {
				top = top + height;
			}
			// v.setTranslationY(top);
			// v.setY(top);
			v.setLayoutParams(layoutParams);
		}
	}

	public void generateViews() {
		normalShow.clear();
		if (views.get("button#findmore") == null) {
			View findMoreFriendButtonView = generateFindMoreFriendButtonView();
			findMoreFriendButtonView.setTag(46);
			views.put("button#findmore", findMoreFriendButtonView);
		}
		normalShow.add("button#findmore");

		View newFriendButtonView = views.get("button#newfriend");
		if (newFriendButtonView == null) {
			newFriendButtonView = generateNewFriendButtonView();
			newFriendButtonView.setTag(46);
			views.put("button#newfriend", newFriendButtonView);
		}

		int newFriendsCount = 1;
		notifyNewFriendButtonView(newFriendButtonView, newFriendsCount);
		normalShow.add("button#newfriend");

		// circles.clear();
		// generate circles
		for (int i = 0; i < circles.size(); i++) {
			Circle circle = circlesMap.get(circles.get(i));

			View circleView = views.get("group#" + circle.rid);
			if (circleView == null) {
				CircleHolder circleHolder = new CircleHolder();
				circleHolders.put("group#" + circle.rid, circleHolder);
				circleView = generateCircleView();
				views.put("group#" + circle.rid, circleView);
				circleView.setTag(265);// 262
			}
			notifyCircleView(circleView, circle,
					circleHolders.get("group#" + circle.rid));

			normalShow.add("group#" + circle.rid);
			// circles.add("group#" + circle.rid);
		}
	}

	public void notifyCircleView(final View circleView, Circle circle,
			CircleHolder circleHolder) {
		TextView groupName = (TextView) circleView
				.findViewById(R.id.panel_name);
		groupName.setText(circle.name + "( " + circle.friends.size() + " )");
		RelativeLayout friendsPanelView = (RelativeLayout) circleView
				.findViewById(R.id.rl_friendsPanel);
		for (int i = 0; i < circle.friends.size(); i++) {
			final Friend friend = friendsMap.get(circle.friends.get(i));
			FriendHolder friendHolder = new FriendHolder();
			friendHolder.phone = friend.phone;
			int index = circleHolder.friendHolders.indexOf(friendHolder);
			friendHolder = (index != -1 ? circleHolder.friendHolders
					.remove(index) : null);
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

	public int baseLeft;// 26
	public int headSpace;// 48
	public int head;
	public int vWidth;

	public Position switchPosition(int i) {
		Position position = new Position();
		int baseX = (int) dp2px(i / 8 * 326);
		if ((i + 1) % 8 == 1) {
			position.y = (int) dp2px(11);
			position.x = (int) (baseLeft + baseX);
		} else if ((i + 1) % 8 == 2) {
			position.y = (int) dp2px(11);
			position.x = (int) (baseLeft + head + headSpace + baseX);
		} else if ((i + 1) % 8 == 3) {
			position.y = (int) dp2px(11);
			position.x = (int) (baseLeft + head + headSpace + head + headSpace + baseX);
		} else if ((i + 1) % 8 == 4) {
			position.y = (int) dp2px(11);
			position.x = (int) (baseLeft + head + headSpace + head + headSpace
					+ head + headSpace + baseX);
		} else if ((i + 1) % 8 == 5) {
			position.y = (int) dp2px(11 + 73 + 27);
			position.x = (int) (baseLeft + baseX);
		} else if ((i + 1) % 8 == 6) {
			position.y = (int) dp2px(11 + 73 + 27);
			position.x = (int) (baseLeft + head + headSpace + baseX);
		} else if ((i + 1) % 8 == 7) {
			position.y = (int) dp2px(11 + 73 + 27);
			position.x = (int) (baseLeft + head + headSpace + head + headSpace + baseX);
		} else if ((i + 1) % 8 == 0) {
			position.y = (int) dp2px(11 + 73 + 27);
			position.x = (int) (baseLeft + head + headSpace + head + headSpace
					+ head + headSpace + baseX);
		}
		return position;
	}

	public void resolveFriendsPositions(CircleHolder circleHolder) {
		for (int i = 0; i < circleHolder.friendHolders.size(); i++) {
			FriendHolder friendHolder = circleHolder.friendHolders.get(i);
			friendHolder.position = switchPosition(i);
			friendHolder.index = i;
		}
	}

	public void setFriendsPositions(CircleHolder circleHolder) {
		for (int i = 0; i < circleHolder.friendHolders.size(); i++) {
			FriendHolder friendHolder = circleHolder.friendHolders.get(i);

			RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
					(int) dp2px(55f), RelativeLayout.LayoutParams.WRAP_CONTENT);
			params.rightMargin = -Integer.MAX_VALUE;
			params.topMargin = friendHolder.position.y;
			params.leftMargin = friendHolder.position.x;
			friendHolder.view.setLayoutParams(params);
		}
	}

	View generateFriendView(Friend friend) {
		View convertView = mInflater.inflate(R.layout.circles_gridpage_item,
				null);
		final ImageView head = (ImageView) convertView
				.findViewById(R.id.iv_head);
		TextView nickname = (TextView) convertView
				.findViewById(R.id.tv_nickname);
		if (!friend.alias.equals("") && friend.alias != null) {
			nickname.setText(friend.alias);
		} else {
			nickname.setText(friend.nickName);
		}
		head.setImageBitmap(MCImageUtils.getCircleBitmap(BitmapFactory
				.decodeResource(thisActivity.getResources(),
						R.drawable.face_man), true, 5, Color.WHITE));
		return convertView;
	}

	View generateCircleView() {
		final View circleView = mInflater.inflate(R.layout.circle_panel, null);
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

		@Override
		public boolean equals(Object o) {
			boolean flag = false;
			if (o != null) {
				if (o instanceof FriendHolder) {
					FriendHolder h = (FriendHolder) o;
					if (phone.equals(h.phone)) {
						flag = true;
					}
				} else if (o instanceof String) {
					String s = (String) o;
					if (phone.equals(s)) {
						flag = true;
					}
				}
			}
			return flag;
		}
	}

	public float dp2px(float px) {
		float dp = screenDensity * px + 0.5f;
		return dp;
	}

	public void changeMenuOptionSelected(RelativeLayout contentView,
			ImageView statusImage) {
		if (statusImage.getVisibility() == View.GONE) {
			currentMenuOptionSelectedStatusImage.setVisibility(View.GONE);
			statusImage.setVisibility(View.VISIBLE);
			currentShowContentView.setVisibility(View.GONE);
			contentView.setVisibility(View.VISIBLE);
		}
		currentShowContentView = contentView;
		currentMenuOptionSelectedStatusImage = statusImage;
	}

	void notifyNewFriendButtonView(View newFriendButtonView, int newFriendsCount) {
		TextView newFriendButton = (TextView) newFriendButtonView
				.findViewById(R.id.tv_type);
		ImageView findMoreFriendIcon = (ImageView) newFriendButtonView
				.findViewById(R.id.iv_icon);
		findMoreFriendIcon.setImageResource(R.drawable.header);
		if (newFriendsCount != 0) {
			newFriendButton.setText("新的好友(" + newFriendsCount + ")");
		} else {
			newFriendButton.setText("新的好友");
		}
	}

	View generateNewFriendButtonView() {
		View newFriendButtonView = mInflater.inflate(
				R.layout.circle_item_button_layout, null);
		return newFriendButtonView;
	}

	View generateFindMoreFriendButtonView() {
		View findMoreFriendButtonView = mInflater.inflate(
				R.layout.circle_item_button_layout, null);
		TextView findMoreFriendButton = (TextView) findMoreFriendButtonView
				.findViewById(R.id.tv_type);
		ImageView findMoreFriendIcon = (ImageView) findMoreFriendButtonView
				.findViewById(R.id.iv_icon);
		findMoreFriendIcon.setImageResource(R.drawable.dialog_search);
		findMoreFriendButton.setText("搜索好友");

		return findMoreFriendButtonView;
	}
}
