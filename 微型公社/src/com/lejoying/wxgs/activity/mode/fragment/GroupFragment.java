package com.lejoying.wxgs.activity.mode.fragment;

import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lejoying.wxgs.R;
import com.lejoying.wxgs.activity.mode.MainModeManager;
import com.lejoying.wxgs.activity.view.ScrollRelativeLayout;
import com.lejoying.wxgs.activity.view.widget.CircleMenu;
import com.lejoying.wxgs.app.MainApplication;
import com.lejoying.wxgs.app.data.entity.Friend;
import com.lejoying.wxgs.app.data.entity.Group;
import com.lejoying.wxgs.app.handler.FileHandler.FileResult;

public class GroupFragment extends BaseFragment {

	MainApplication app = MainApplication.getMainApplication();
	MainModeManager mMainModeManager;

	LayoutInflater mInflater;

	float density = 1f;
	int screenHeight = 0;
	int screenWidth = 0;
	int groupPanelWidth = 0;
	int groupPanelHeight = 0;
	int groupScrollSpaceWidth = 0;
	int groupItemWidth = 0;

	int headSize = 0;
	int headMargin = 0;

	View mContentView;
	ScrollRelativeLayout groupViewContainer;

	public void setMode(MainModeManager mainMode) {
		mMainModeManager = mainMode;
	}

	static class TouchEvnetStatus {
		public static int STATIC = 0;
		public static int MOVING_X = 1;
		public static int MOVING_Y = 2;
		public static int END = 3;
	}

	public int touchEvnetStatus = TouchEvnetStatus.STATIC;

	public View onCreateView(android.view.LayoutInflater inflater, android.view.ViewGroup container, android.os.Bundle savedInstanceState) {
		mInflater = inflater;
		density = getActivity().getResources().getDisplayMetrics().density;
		screenHeight = getScreenHeight();
		screenWidth = getScreenWidth();
		groupPanelWidth = screenWidth - (int) dp2px(20);
		groupPanelHeight = (int) (dp2px(10 + 1 + 220 + 37) + sp2px(getActivity(), 18));
		groupScrollSpaceWidth = screenWidth - (int) dp2px(40);
		groupItemWidth = groupScrollSpaceWidth / 2 - (int) dp2px(20);
		headSize = (int) dp2px(22);
		headMargin = (int) ((groupItemWidth - dp2px(10) - headSize * 5) / 6);

		mContentView = inflater.inflate(R.layout.fragment_group, null);
		groupViewContainer = (ScrollRelativeLayout) mContentView.findViewById(R.id.groupViewContainer);

		System.out.println(density);

		initEvent();

		notifyView();

		return mContentView;
	}

	void initEvent() {
		groupViewContainer.setOnTouchListener(new OnTouchListener() {

			public float x0 = 0;
			public float y0 = 0;

			public float x0_0 = 0;
			public float y0_0 = 0;

			public float vx = 0;
			public float vy = 0;

			public float dy = 0;
			public float dx = 0;

			public int preTouchTimes = 5;

			long pre_lastMillis = 0;
			long lastMillis = 0;

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// eventCount++;
				float x = event.getRawX();
				float y = event.getRawY();

				long currentMillis = System.currentTimeMillis();

				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					if (touchEvnetStatus == TouchEvnetStatus.END) {
						touchEvnetStatus = TouchEvnetStatus.STATIC;
					}
					x0 = x;
					y0 = y;
				} else if (event.getAction() == MotionEvent.ACTION_MOVE) {
					if (lastMillis == 0) {
						lastMillis = currentMillis;
						return true;
					}
					dy = y - y0;
					dx = x - x0;
					if (touchEvnetStatus == TouchEvnetStatus.MOVING_Y) {
						groupViewContainer.scrollBy(0, -(int) (dy));
						y0 = y;
					} else if (touchEvnetStatus == TouchEvnetStatus.STATIC) {
						if (dy * dy + dx * dx > 400) {
							if (dy * dy > dx * dx) {
								touchEvnetStatus = TouchEvnetStatus.MOVING_Y;
							} else {
								touchEvnetStatus = TouchEvnetStatus.MOVING_X;
							}
						}
					}

					if (preTouchTimes < 0) {
						preTouchTimes = 2;
						x0_0 = x0;
						y0_0 = y0;
						pre_lastMillis = lastMillis;

						x0 = x;
						y0 = y;

						lastMillis = currentMillis;
					}
					preTouchTimes--;

				} else if (event.getAction() == MotionEvent.ACTION_UP) {

					long delta = currentMillis - lastMillis;

					if (delta == 0 || x == x0 || y == y0) {
						delta = currentMillis - pre_lastMillis;
						x0 = x0_0;
						y0 = y0_0;
					}

					vx = (x - x0) / delta;
					vy = (y - y0) / delta;

					// System.out.println("vx:    " + vx + "     ----vy:    " +
					// vy);

					// FrictionAnimation decelerationAnimation = new
					// FrictionAnimation(vx, vy);
					// circlesViewContenter.startAnimation(decelerationAnimation);
					if (touchEvnetStatus == TouchEvnetStatus.MOVING_Y || touchEvnetStatus == TouchEvnetStatus.MOVING_X) {
						touchEvnetStatus = TouchEvnetStatus.END;
					}
				}

				return true;
			}
		});

	}

	void notifyView() {
		int top = (int) dp2px(20);
		groupViewContainer.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL);

		View myGroup = generateMyGroup();
		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
		params.topMargin = top;
		params.bottomMargin = -Integer.MAX_VALUE;
		myGroup.setLayoutParams(params);
		groupViewContainer.addView(myGroup);

		top += (groupPanelHeight + (int) dp2px(25));

		View attentionGroup = generateAttentionGroup();
		RelativeLayout.LayoutParams params2 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
		params2.topMargin = top;
		params2.bottomMargin = -Integer.MAX_VALUE;
		attentionGroup.setLayoutParams(params2);
		groupViewContainer.addView(attentionGroup);

		top += (groupPanelHeight + (int) dp2px(25));

		View nearByGroup = generateNearByGroup();
		RelativeLayout.LayoutParams params3 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
		params3.topMargin = top;
		params3.bottomMargin = -Integer.MAX_VALUE;
		nearByGroup.setLayoutParams(params3);
		groupViewContainer.addView(nearByGroup);

		top += (groupPanelHeight + (int) dp2px(25));

		View tempGroup = generateTempGroup();
		RelativeLayout.LayoutParams params4 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
		params4.topMargin = top;
		params4.bottomMargin = -Integer.MAX_VALUE;
		tempGroup.setLayoutParams(params4);
		groupViewContainer.addView(tempGroup);

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
		float dp = density * px + 0.5f;
		return dp;
	}

	Map<String, View> groups = new HashMap<String, View>();

	class Position {
		int x = 0;
		int y = 0;
	}

	Position switchPosition(int i) {
		Position position = new Position();
		if ((i + 1) % 4 == 1) {
			position.y = (int) dp2px(20);
			position.x = (int) dp2px(10) + i / 4 * groupScrollSpaceWidth;
		} else if ((i + 1) % 4 == 2) {
			position.y = (int) dp2px(20);
			position.x = groupItemWidth + (int) dp2px(30) + i / 4 * groupScrollSpaceWidth;
		} else if ((i + 1) % 4 == 3) {
			position.y = (int) dp2px(115);
			position.x = (int) dp2px(10) + i / 4 * groupScrollSpaceWidth;
		} else if ((i + 1) % 4 == 0) {
			position.y = (int) dp2px(115);
			position.x = groupItemWidth + (int) dp2px(30) + i / 4 * groupScrollSpaceWidth;
		}
		return position;
	}

	View generateMyGroup() {
		View groupView = mInflater.inflate(R.layout.fragment_panel, null);
		TextView panelName = (TextView) groupView.findViewById(R.id.panel_name);
		panelName.setText("我加入的群组");
		final ScrollRelativeLayout viewContainer = (ScrollRelativeLayout) groupView.findViewById(R.id.viewContainer);

		View bottomBar = groupView.findViewById(R.id.bottomBar);
		bottomBar.findViewById(R.id.buttonPreviousGroup).setVisibility(View.GONE);
		TextView buttonManager = (TextView) bottomBar.findViewById(R.id.buttonNextGroup);
		buttonManager.setText("群组设置");
		bottomBar.setVisibility(View.VISIBLE);

		for (int i = 0; i < app.data.groups.size(); i++) {
			final Group group = app.data.groups.get(i);
			View groupItemView = mInflater.inflate(R.layout.fragment_group_item, null);
			TextView groupName = (TextView) groupItemView.findViewById(R.id.groupName);
			TextView memberCount = (TextView) groupItemView.findViewById(R.id.memberCount);
			groupName.setText(group.name);
			memberCount.setText("(" + String.valueOf(group.members.size()) + ")");

			LinearLayout members = (LinearLayout) groupItemView.findViewById(R.id.members);

			for (int j = 0; j < group.members.size(); j++) {
				if (j == 5) {
					break;
				}
				Friend groupFriend = app.data.groupFriends.get(group.members.get(j));
				final ImageView head = new ImageView(getActivity());
				android.widget.LinearLayout.LayoutParams params = new android.widget.LinearLayout.LayoutParams(headSize, headSize);
				params.leftMargin = headMargin;
				head.setLayoutParams(params);
				final String fileName = groupFriend.head;
				app.fileHandler.getHeadImage(fileName, new FileResult() {
					@Override
					public void onResult(String where) {
						head.setImageBitmap(app.fileHandler.bitmaps.get(fileName));
					}
				});
				members.addView(head);
			}

			RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(groupItemWidth, RelativeLayout.LayoutParams.WRAP_CONTENT);
			Position position = switchPosition(i);
			params.rightMargin = -Integer.MAX_VALUE;
			params.leftMargin = position.x;
			params.topMargin = position.y;
			groupItemView.setLayoutParams(params);

			groupItemView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					if (touchEvnetStatus != TouchEvnetStatus.STATIC) {
						return;
					}
					mMainModeManager.mChatFragment.mStatus = ChatFragment.CHAT_GROUP;
					mMainModeManager.mChatFragment.mNowChatGroup = group;
					mMainModeManager.showNext(mMainModeManager.mChatFragment);
				}
			});

			viewContainer.addView(groupItemView);
		}

		View groupItemView = mInflater.inflate(R.layout.fragment_group_item_add, null);

		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(groupItemWidth, RelativeLayout.LayoutParams.WRAP_CONTENT);
		Position position = switchPosition(viewContainer.getChildCount());
		params.rightMargin = -Integer.MAX_VALUE;
		params.leftMargin = position.x;
		params.topMargin = position.y;
		groupItemView.setLayoutParams(params);

		groupItemView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mMainModeManager.mGroupManagerFragment.status = GroupManagerFragment.MODE_NEWGROUP;
				mMainModeManager.showNext(mMainModeManager.mGroupManagerFragment);
			}
		});

		viewContainer.addView(groupItemView);

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
				if (touchEvnetStatus == TouchEvnetStatus.MOVING_X) {
					viewContainer.scrollBy(-(int) (dx), 0);
					x0 = e2.getRawX();
				}
				return true;
			}
		});

		viewContainer.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				return detector.onTouchEvent(event);
			}
		});

		return groupView;
	}

	View generateAttentionGroup() {
		View groupView = mInflater.inflate(R.layout.fragment_panel, null);
		TextView panelName = (TextView) groupView.findViewById(R.id.panel_name);
		panelName.setText("我关注的群组");
		final ScrollRelativeLayout viewContainer = (ScrollRelativeLayout) groupView.findViewById(R.id.viewContainer);

		View bottomBar = groupView.findViewById(R.id.bottomBar);
		bottomBar.findViewById(R.id.buttonPreviousGroup).setVisibility(View.GONE);
		TextView buttonManager = (TextView) bottomBar.findViewById(R.id.buttonNextGroup);
		buttonManager.setText("查看群组分享");
		bottomBar.setVisibility(View.VISIBLE);

		View groupItemView = mInflater.inflate(R.layout.fragment_group_item_add, null);
		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(groupItemWidth, RelativeLayout.LayoutParams.WRAP_CONTENT);
		Position position = switchPosition(viewContainer.getChildCount());
		params.rightMargin = -Integer.MAX_VALUE;
		params.leftMargin = position.x;
		params.topMargin = position.y;
		groupItemView.setLayoutParams(params);
		TextView text = (TextView) groupItemView.findViewById(R.id.newGroupText);
		text.setText("找到更多群组");
		viewContainer.addView(groupItemView);

		return groupView;
	}

	View generateNearByGroup() {
		View groupView = mInflater.inflate(R.layout.fragment_panel, null);
		TextView panelName = (TextView) groupView.findViewById(R.id.panel_name);
		panelName.setText("附近活跃群组");
		final ScrollRelativeLayout viewContainer = (ScrollRelativeLayout) groupView.findViewById(R.id.viewContainer);

		View bottomBar = groupView.findViewById(R.id.bottomBar);
		bottomBar.findViewById(R.id.buttonPreviousGroup).setVisibility(View.GONE);
		TextView buttonManager = (TextView) bottomBar.findViewById(R.id.buttonNextGroup);
		buttonManager.setText("亦庄站");
		bottomBar.setVisibility(View.VISIBLE);

		View groupItemView = mInflater.inflate(R.layout.fragment_group_item_add, null);
		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(groupItemWidth, RelativeLayout.LayoutParams.WRAP_CONTENT);
		Position position = switchPosition(viewContainer.getChildCount());
		params.rightMargin = -Integer.MAX_VALUE;
		params.leftMargin = position.x;
		params.topMargin = position.y;
		groupItemView.setLayoutParams(params);
		TextView text = (TextView) groupItemView.findViewById(R.id.newGroupText);
		text.setText("找到更多群组");
		viewContainer.addView(groupItemView);

		return groupView;
	}

	View generateTempGroup() {
		View groupView = mInflater.inflate(R.layout.fragment_panel, null);
		TextView panelName = (TextView) groupView.findViewById(R.id.panel_name);
		panelName.setText("临时群组");
		final ScrollRelativeLayout viewContainer = (ScrollRelativeLayout) groupView.findViewById(R.id.viewContainer);

		View bottomBar = groupView.findViewById(R.id.bottomBar);
		bottomBar.findViewById(R.id.buttonPreviousGroup).setVisibility(View.GONE);
		TextView buttonManager = (TextView) bottomBar.findViewById(R.id.buttonNextGroup);
		buttonManager.setText("临时的群组");
		bottomBar.setVisibility(View.VISIBLE);

		View groupItemView = mInflater.inflate(R.layout.fragment_group_item_add, null);
		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(groupItemWidth, RelativeLayout.LayoutParams.WRAP_CONTENT);
		Position position = switchPosition(viewContainer.getChildCount());
		params.rightMargin = -Integer.MAX_VALUE;
		params.leftMargin = position.x;
		params.topMargin = position.y;
		groupItemView.setLayoutParams(params);
		TextView text = (TextView) groupItemView.findViewById(R.id.newGroupText);
		text.setText("找到更多群组");
		viewContainer.addView(groupItemView);

		return groupView;
	}

	@Override
	public void onResume() {
		CircleMenu.show();
		CircleMenu.setPageName(getString(R.string.circlemenu_page_group));
		super.onResume();
	}
}
