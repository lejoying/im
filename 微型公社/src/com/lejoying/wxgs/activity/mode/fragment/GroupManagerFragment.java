package com.lejoying.wxgs.activity.mode.fragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lejoying.wxgs.R;
import com.lejoying.wxgs.activity.mode.MainModeManager;
import com.lejoying.wxgs.activity.view.ScrollRelativeLayout;
import com.lejoying.wxgs.activity.view.widget.CircleMenu;
import com.lejoying.wxgs.app.MainApplication;
import com.lejoying.wxgs.app.data.entity.Circle;
import com.lejoying.wxgs.app.data.entity.Friend;
import com.lejoying.wxgs.app.data.entity.Group;
import com.lejoying.wxgs.app.handler.FileHandler.FileResult;

public class GroupManagerFragment extends BaseFragment {

	MainApplication app = MainApplication.getMainApplication();
	MainModeManager mMainModeManager;
	LayoutInflater mInflater;

	Group mCurrentManagerGroup;

	ScrollRelativeLayout circlesViewContenter;

	View mContentView;

	View editControl;
	View nextBar;
	View buttonList;
	View selectFriend;

	public static final String MODE_MANAGER = "manager";
	public static final String MODE_NEWGROUP = "new";
	public String status;

	public void setMode(MainModeManager mainMode) {
		mMainModeManager = mainMode;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		screenWidth = getScreenWidth();
		currentEditPosition = 0;
		mInflater = inflater;
		mContentView = inflater.inflate(R.layout.fragment_group_manager, null);
		circlesViewContenter = (ScrollRelativeLayout) mContentView.findViewById(R.id.circlesViewContainer);
		editControl = mContentView.findViewById(R.id.editControl);
		nextBar = mContentView.findViewById(R.id.nextBar);
		buttonList = mContentView.findViewById(R.id.buttonList);
		selectFriend = mContentView.findViewById(R.id.selectFriend);
		if (status.equals(MODE_MANAGER)) {
			RelativeLayout.LayoutParams params = (android.widget.RelativeLayout.LayoutParams) editControl.getLayoutParams();
			params.height = (int) dp2px(80);
			editControl.setLayoutParams(params);
			selectFriend.setVisibility(View.GONE);
			buttonList.setVisibility(View.VISIBLE);
			nextBar.setVisibility(View.GONE);
			notifyViews();
		} else if (status.equals(MODE_NEWGROUP)) {
			RelativeLayout.LayoutParams params = (android.widget.RelativeLayout.LayoutParams) editControl.getLayoutParams();
			params.height = (int) dp2px(160);
			editControl.setLayoutParams(params);
			selectFriend.setVisibility(View.VISIBLE);
			buttonList.setVisibility(View.GONE);
			nextBar.setVisibility(View.VISIBLE);
			notifyCircles();
		}
		return mContentView;
	}

	int screenWidth;
	int currentEditPosition = 0;
	Map<String, View> views = new HashMap<String, View>();
	List<String> circles = new ArrayList<String>();

	private void notifyCircles() {
		int marginTop = (int) dp2px(20);

		circlesViewContenter.setGravity(Gravity.LEFT | Gravity.TOP);

		circles.clear();
		// generate circles
		for (int i = 0; i < app.data.circles.size(); i++) {
			Circle circle = app.data.circles.get(i);
			if (views.get("group#" + circle.rid) == null) {
				CircleHolder circleHolder = new CircleHolder();
				circleHolders.put("group#" + circle.rid, circleHolder);
				View view = generateCircleView(circle, circleHolder);
				views.put("group#" + circle.rid, view);
				view.setTag(272);
			}
			circles.add("group#" + circle.rid);
		}

		int marginLeft = 0;

		for (int i = 0; i < circles.size(); i++) {
			String group = circles.get(i);
			View view = views.get(group);
			RelativeLayout.LayoutParams layoutParams3 = new RelativeLayout.LayoutParams(screenWidth, LayoutParams.WRAP_CONTENT);
			layoutParams3.setMargins(marginLeft, marginTop, -Integer.MAX_VALUE, 0);
			view.setLayoutParams(layoutParams3);
			TextView manager = (TextView) view.findViewById(R.id.panel_right_button);
			manager.setText("添加成员");
			manager.setVisibility(View.VISIBLE);
			view.findViewById(R.id.bottomBar).setVisibility(View.VISIBLE);
			if (view.getParent() != null) {
				((ViewGroup) view.getParent()).removeView(view);
			}
			circlesViewContenter.addView(view);
			marginLeft += screenWidth;
		}
	}

	@Override
	public void onResume() {
		CircleMenu.showBack();
		super.onResume();
	}

	private void notifyViews() {

		int marginTop = (int) dp2px(20);

		circlesViewContenter.removeAllViews();

		groupHolder = new CircleHolder();
		View v = generateGroup(groupHolder);
		circlesViewContenter.setGravity(Gravity.LEFT | Gravity.TOP);
		RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		layoutParams.setMargins(0, marginTop, layoutParams.rightMargin, -Integer.MAX_VALUE);
		v.setLayoutParams(layoutParams);
		circlesViewContenter.addView(v);

		circles.clear();
		// generate circles
		for (int i = 0; i < app.data.circles.size(); i++) {
			Circle circle = app.data.circles.get(i);
			if (views.get("group#" + circle.rid) == null) {
				CircleHolder circleHolder = new CircleHolder();
				circleHolders.put("group#" + circle.rid, circleHolder);
				View view = generateCircleView(circle, circleHolder);
				views.put("group#" + circle.rid, view);
				view.setTag(272);
			}
			circles.add("group#" + circle.rid);
		}

		for (int i = 0; i < circles.size(); i++) {
			String group = circles.get(i);
			View view = views.get(group);
			RelativeLayout.LayoutParams layoutParams3 = new RelativeLayout.LayoutParams(screenWidth, LayoutParams.WRAP_CONTENT);
			layoutParams3.setMargins((i + 1) * screenWidth, marginTop, -Integer.MAX_VALUE, 0);
			view.setLayoutParams(layoutParams3);
			TextView manager = (TextView) view.findViewById(R.id.panel_right_button);
			manager.setText("添加成员");
			manager.setVisibility(View.VISIBLE);
			view.findViewById(R.id.bottomBar).setVisibility(View.VISIBLE);
			if (view.getParent() != null) {
				((ViewGroup) view.getParent()).removeView(view);
			}
			circlesViewContenter.addView(view);
		}

	}

	public int getScreenWidth() {
		return getActivity().getResources().getDisplayMetrics().widthPixels;
	}

	public int getScreenHeight() {
		return getActivity().getResources().getDisplayMetrics().heightPixels;
	}

	public static int sp2px(Context context, float spValue) {
		final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
		return (int) (spValue * fontScale + 0.5f);
	}

	public float dp2px(float px) {
		float dp = getActivity().getResources().getDisplayMetrics().density * px + 0.5f;
		return dp;
	}

	class Position {
		int x = 0;
		int y = 0;
	}

	Position switchPosition(int i) {
		Position position = new Position();
		if ((i + 1) % 6 == 1) {
			position.y = (int) dp2px(11);
			position.x = (int) dp2px(26 + i / 6 * 326);
		} else if ((i + 1) % 6 == 2) {
			position.y = (int) dp2px(11);
			position.x = (int) dp2px(26 + 55 + 48 + i / 6 * 326);
		} else if ((i + 1) % 6 == 3) {
			position.y = (int) dp2px(11);
			position.x = (int) dp2px(26 + 55 + 48 + 55 + 48 + i / 6 * 326);
		} else if ((i + 1) % 6 == 4) {
			position.y = (int) dp2px(11 + 73 + 27);
			position.x = (int) dp2px(26 + i / 6 * 326);
		} else if ((i + 1) % 6 == 5) {
			position.y = (int) dp2px(11 + 73 + 27);
			position.x = (int) dp2px(26 + 55 + 48 + i / 6 * 326);
		} else if ((i + 1) % 6 == 0) {
			position.y = (int) dp2px(11 + 73 + 27);
			position.x = (int) dp2px(26 + 55 + 48 + 55 + 48 + i / 6 * 326);
		}
		return position;
	}

	class FriendHolder {
		Position position;
		View view;
		int index;
	}

	class CircleHolder {
		public List<FriendHolder> friendHolders = new ArrayList<FriendHolder>();
	}

	public Map<String, CircleHolder> circleHolders = new Hashtable<String, CircleHolder>();
	public Map<String, FriendHolder> tempFriendHolders = new Hashtable<String, FriendHolder>();
	public CircleHolder groupHolder;

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

			RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams((int) dp2px(55f), android.widget.RelativeLayout.LayoutParams.WRAP_CONTENT);
			params.rightMargin = -Integer.MAX_VALUE;

			params.topMargin = friendHolder.position.y;
			params.leftMargin = friendHolder.position.x;
			friendHolder.view.setLayoutParams(params);
		}
	}

	View generateGroup(CircleHolder circleHolder) {
		View circleView = mInflater.inflate(R.layout.fragment_panel, null);

		TextView groupName = (TextView) circleView.findViewById(R.id.panel_name);
		groupName.setText(mCurrentManagerGroup.name);
		TextView rightGroupName = (TextView) circleView.findViewById(R.id.panel_right_button);
		rightGroupName.setText("群组管理");
		rightGroupName.setVisibility(View.VISIBLE);
		final RelativeLayout friendContainer = (RelativeLayout) circleView.findViewById(R.id.viewContainer);

		List<String> phones = mCurrentManagerGroup.members;
		Map<String, Friend> friends = app.data.groupFriends;
		// int pagecount = phones.size() % 6 == 0 ? phones.size() / 6 :
		// phones.size() / 6 + 1;

		for (int i = 0; i < phones.size(); i++) {
			final Friend friend = friends.get(phones.get(i));

			View convertView = generateFriendView(friend);

			final FriendHolder friendHolder = new FriendHolder();
			friendHolder.view = convertView;

			circleHolder.friendHolders.add(friendHolder);

			friendContainer.addView(convertView);
		}

		resolveFriendsPositions(circleHolder);
		setFriendsPositions(circleHolder);

		final GestureDetector detector = new GestureDetector(getActivity(), new SimpleOnGestureListener() {
			float x0 = 0;
			float dx = 0;

			@Override
			public boolean onDown(MotionEvent e) {
				x0 = e.getRawX();
				return true;
			}

			@Override
			public void onLongPress(MotionEvent e) {
			}

			@Override
			public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
				dx = e2.getRawX() - x0;
				friendContainer.scrollBy(-(int) (dx), 0);
				x0 = e2.getRawX();
				return true;
			}
		});

		friendContainer.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				return detector.onTouchEvent(event);
			}
		});

		return circleView;
	}

	View generateCircleView(Circle circle, CircleHolder circleHolder) {
		final View circleView = mInflater.inflate(R.layout.fragment_panel, null);

		View buttonPreviousGroup = circleView.findViewById(R.id.buttonPreviousGroup);
		buttonPreviousGroup.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (currentEditPosition > 0) {
					View currentView = views.get(circles.get(currentEditPosition));
					currentEditPosition--;
					View previousView = views.get(circles.get(currentEditPosition));

					TranslateAnimation animation = new TranslateAnimation(-screenWidth, 0, 0, 0);
					animation.setDuration(300);

					circlesViewContenter.scrollTo(currentEditPosition * screenWidth, 0);

					currentView.startAnimation(animation);
					previousView.startAnimation(animation);
				}
			}
		});

		View buttonNextGroup = circleView.findViewById(R.id.buttonNextGroup);
		buttonNextGroup.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (currentEditPosition < circles.size() - 1) {
					View currentView = views.get(circles.get(currentEditPosition));
					currentEditPosition++;
					View nextView = views.get(circles.get(currentEditPosition));

					TranslateAnimation animation = new TranslateAnimation(screenWidth, 0, 0, 0);
					animation.setDuration(300);

					circlesViewContenter.scrollTo(currentEditPosition * screenWidth, 0);
					currentView.startAnimation(animation);
					nextView.startAnimation(animation);
				}
			}
		});

		TextView groupName = (TextView) circleView.findViewById(R.id.panel_name);
		groupName.setText(circle.name);
		final RelativeLayout friendContainer = (RelativeLayout) circleView.findViewById(R.id.viewContainer);

		List<String> phones = circle.phones;
		Map<String, Friend> friends = app.data.friends;
		// int pagecount = phones.size() % 6 == 0 ? phones.size() / 6 :
		// phones.size() / 6 + 1;

		for (int i = 0; i < phones.size(); i++) {
			final Friend friend = friends.get(phones.get(i));

			View convertView = generateFriendView(friend);

			final FriendHolder friendHolder = new FriendHolder();
			friendHolder.view = convertView;

			circleHolder.friendHolders.add(friendHolder);

			friendContainer.addView(convertView);
		}

		resolveFriendsPositions(circleHolder);
		setFriendsPositions(circleHolder);

		final GestureDetector detector = new GestureDetector(getActivity(), new SimpleOnGestureListener() {
			float x0 = 0;
			float dx = 0;

			@Override
			public boolean onDown(MotionEvent e) {
				x0 = e.getRawX();
				return true;
			}

			@Override
			public void onLongPress(MotionEvent e) {
			}

			@Override
			public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
				dx = e2.getRawX() - x0;
				friendContainer.scrollBy(-(int) (dx), 0);
				x0 = e2.getRawX();
				return true;
			}
		});

		friendContainer.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				return detector.onTouchEvent(event);
			}
		});

		return circleView;
	}

	TranslateAnimation friendToLeftAnimation;
	TranslateAnimation friendToRightAnimation;

	TranslateAnimation friendToNextLineAnimation;
	TranslateAnimation friendToPreLineAnimation;
	TranslateAnimation allTempFriendMoveToLeft;
	TranslateAnimation allTempFriendMoveToRight;

	TranslateAnimation lastFriendToLeftAnimation;
	TranslateAnimation lastFriendToRightAnimation;

	void circleViewCommonAnimation() {
		lastFriendToLeftAnimation = new TranslateAnimation(dp2px(103), 0, 0, 0);
		lastFriendToLeftAnimation.setStartOffset(150);
		lastFriendToLeftAnimation.setDuration(120);

		lastFriendToRightAnimation = new TranslateAnimation(dp2px(-118), 0, dp2px(100), dp2px(100));
		lastFriendToRightAnimation.setStartOffset(150);
		lastFriendToRightAnimation.setDuration(120);

		friendToLeftAnimation = new TranslateAnimation(dp2px(103), 0, 0, 0);
		friendToLeftAnimation.setStartOffset(150);
		friendToLeftAnimation.setDuration(120);

		friendToRightAnimation = new TranslateAnimation(dp2px(-103), 0, 0, 0);

		friendToRightAnimation.setStartOffset(150);
		friendToRightAnimation.setDuration(120);

		friendToNextLineAnimation = new TranslateAnimation(dp2px(206), 0, dp2px(-100), 0);

		friendToNextLineAnimation.setStartOffset(150);
		friendToNextLineAnimation.setDuration(120);

		friendToPreLineAnimation = new TranslateAnimation(dp2px(-206), 0, dp2px(100), 0);

		friendToPreLineAnimation.setStartOffset(150);
		friendToPreLineAnimation.setDuration(120);

		allTempFriendMoveToLeft = new TranslateAnimation(dp2px(75), 0, 0, 0);
		allTempFriendMoveToLeft.setStartOffset(150);
		allTempFriendMoveToLeft.setDuration(120);

		allTempFriendMoveToRight = new TranslateAnimation(-dp2px(75), 0, 0, 0);
		allTempFriendMoveToRight.setStartOffset(150);
		allTempFriendMoveToRight.setDuration(120);
	}

	View generateFriendView(Friend friend) {
		View convertView = mInflater.inflate(R.layout.fragment_circles_gridpage_item, null);
		final ImageView head = (ImageView) convertView.findViewById(R.id.iv_head);
		TextView nickname = (TextView) convertView.findViewById(R.id.tv_nickname);
		nickname.setText(friend.nickName);
		final String headFileName = friend.head;
		app.fileHandler.getHeadImage(headFileName, new FileResult() {
			@Override
			public void onResult(String where) {
				head.setImageBitmap(app.fileHandler.bitmaps.get(headFileName));
			}
		});
		return convertView;
	}

}
