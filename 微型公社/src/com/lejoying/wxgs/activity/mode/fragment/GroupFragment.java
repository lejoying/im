package com.lejoying.wxgs.activity.mode.fragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.lejoying.wxgs.R;
import com.lejoying.wxgs.activity.mode.MainModeManager;
import com.lejoying.wxgs.activity.utils.CommonNetConnection;
import com.lejoying.wxgs.activity.view.ScrollRelativeLayout;
import com.lejoying.wxgs.activity.view.widget.CircleMenu;
import com.lejoying.wxgs.app.MainApplication;
import com.lejoying.wxgs.app.data.API;
import com.lejoying.wxgs.app.data.Data;
import com.lejoying.wxgs.app.data.entity.Friend;
import com.lejoying.wxgs.app.data.entity.Group;
import com.lejoying.wxgs.app.handler.DataHandler.Modification;
import com.lejoying.wxgs.app.handler.FileHandler.FileResult;
import com.lejoying.wxgs.app.handler.LocationHandler.LocationListener;
import com.lejoying.wxgs.app.handler.NetworkHandler.Settings;
import com.lejoying.wxgs.app.parser.JSONParser;
import com.lejoying.wxgs.app.parser.JSONParser.GroupsAndFriends;

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

	public View onCreateView(android.view.LayoutInflater inflater,
			android.view.ViewGroup container,
			android.os.Bundle savedInstanceState) {
		if (mContentView == null) {
			mInflater = inflater;
			density = getActivity().getResources().getDisplayMetrics().density;
			screenHeight = getScreenHeight();
			screenWidth = getScreenWidth();
			groupPanelWidth = screenWidth - (int) dp2px(20);
			groupPanelHeight = (int) (dp2px(10 + 1 + 220 + 37) + sp2px(
					getActivity(), 18));
			groupScrollSpaceWidth = screenWidth - (int) dp2px(40);
			groupItemWidth = groupScrollSpaceWidth / 2 - (int) dp2px(8);
			headSize = (int) dp2px(22);
			headMargin = (int) ((groupItemWidth - dp2px(22) - headSize * 5) / 6);

			mContentView = inflater.inflate(R.layout.fragment_group, null);
			groupViewContainer = (ScrollRelativeLayout) mContentView
					.findViewById(R.id.groupViewContainer);

			initEvent();

			notifyViews();
		}
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
								groupViewContainer.interceptTouchEvent();
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
					if (touchEvnetStatus == TouchEvnetStatus.MOVING_Y
							|| touchEvnetStatus == TouchEvnetStatus.MOVING_X) {
						touchEvnetStatus = TouchEvnetStatus.END;
					}
				}

				return true;
			}
		});

	}

	boolean added;

	public void notifyViews() {

		generateViews();

		if (!added) {
			added = true;
			int top = (int) dp2px(20);
			groupViewContainer.setGravity(Gravity.TOP
					| Gravity.CENTER_HORIZONTAL);

			RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
					RelativeLayout.LayoutParams.MATCH_PARENT,
					RelativeLayout.LayoutParams.WRAP_CONTENT);
			params.topMargin = top;
			params.bottomMargin = -Integer.MAX_VALUE;
			myGroup.setLayoutParams(params);

			groupViewContainer.addView(myGroup);

			top += (groupPanelHeight + (int) dp2px(25));

			RelativeLayout.LayoutParams params2 = new RelativeLayout.LayoutParams(
					RelativeLayout.LayoutParams.MATCH_PARENT,
					RelativeLayout.LayoutParams.WRAP_CONTENT);
			params2.topMargin = top;
			params2.bottomMargin = -Integer.MAX_VALUE;
			attentionGroup.setLayoutParams(params2);
			groupViewContainer.addView(attentionGroup);

			top += (groupPanelHeight + (int) dp2px(25));

			RelativeLayout.LayoutParams params3 = new RelativeLayout.LayoutParams(
					RelativeLayout.LayoutParams.MATCH_PARENT,
					RelativeLayout.LayoutParams.WRAP_CONTENT);
			params3.topMargin = top;
			params3.bottomMargin = -Integer.MAX_VALUE;
			nearByGroup.setLayoutParams(params3);
			groupViewContainer.addView(nearByGroup);

			top += (groupPanelHeight + (int) dp2px(25));

			RelativeLayout.LayoutParams params4 = new RelativeLayout.LayoutParams(
					RelativeLayout.LayoutParams.MATCH_PARENT,
					RelativeLayout.LayoutParams.WRAP_CONTENT);
			params4.topMargin = top;
			params4.bottomMargin = -Integer.MAX_VALUE;
			tempGroup.setLayoutParams(params4);
			groupViewContainer.addView(tempGroup);
		}
	}

	View myGroup;
	View attentionGroup;
	View nearByGroup;
	View tempGroup;

	void generateViews() {
		if (myGroup == null) {
			groupHoldersMap.put("myGroup", new ArrayList<GroupHolder>());
			myGroup = generateMyGroup();
		}
		List<Group> myGroups = new ArrayList<Group>();
		for (String gid : app.data.groups) {
			myGroups.add(app.data.groupsMap.get(gid));
		}
		notifyGroups(myGroups, groupHoldersMap.get("myGroup"), myGroup);

		if (attentionGroup == null) {
			groupHoldersMap.put("attentionGroup", new ArrayList<GroupHolder>());
			attentionGroup = generateAttentionGroup();
		}

		if (nearByGroup == null) {
			groupHoldersMap.put("nearByGroup", new ArrayList<GroupHolder>());
			nearByGroup = generateNearByGroup();
		}

		if (tempGroup == null) {
			groupHoldersMap.put("tempGroup", new ArrayList<GroupHolder>());
			tempGroup = generateTempGroup();
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
		float dp = density * px + 0.5f;
		return dp;
	}

	class Position {
		int x = 0;
		int y = 0;
	}

	Position switchPosition(int i) {
		Position position = new Position();
		if ((i + 1) % 4 == 1) {
			position.y = (int) dp2px(8);
			position.x = (int) dp2px(10) + i / 4 * groupScrollSpaceWidth;
		} else if ((i + 1) % 4 == 2) {
			position.y = (int) dp2px(8);
			position.x = groupItemWidth + (int) dp2px(18) + i / 4
					* groupScrollSpaceWidth;
		} else if ((i + 1) % 4 == 3) {
			position.y = (int) dp2px(103);
			position.x = (int) dp2px(10) + i / 4 * groupScrollSpaceWidth;
		} else if ((i + 1) % 4 == 0) {
			position.y = (int) dp2px(103);
			position.x = groupItemWidth + (int) dp2px(18) + i / 4
					* groupScrollSpaceWidth;
		}
		return position;
	}

	void notifyPosition(int index, GroupHolder groupHolder) {
		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
				groupItemWidth, RelativeLayout.LayoutParams.WRAP_CONTENT);
		Position position = switchPosition(index);
		params.rightMargin = -Integer.MAX_VALUE;
		params.leftMargin = position.x;
		params.topMargin = position.y;
		groupHolder.groupItemView.setLayoutParams(params);
	}

	class GroupHolder {
		View groupItemView;
		String gid;

		@Override
		public boolean equals(Object o) {
			boolean flag = false;
			if (o != null) {
				if (o instanceof GroupHolder) {
					if (gid.equals(((GroupHolder) o).gid)) {
						flag = true;
					}
				}
			}
			return flag;
		}
	}

	Map<String, List<GroupHolder>> groupHoldersMap = new HashMap<String, List<GroupHolder>>();

	View generateMyGroup() {
		View groupView = mInflater.inflate(R.layout.fragment_panel, null);
		TextView panelName = (TextView) groupView.findViewById(R.id.panel_name);
		panelName.setText("我加入的群组");
		final ScrollRelativeLayout viewContainer = (ScrollRelativeLayout) groupView
				.findViewById(R.id.viewContainer);

		View bottomBar = groupView.findViewById(R.id.bottomBar);
		bottomBar.findViewById(R.id.buttonPreviousGroup).setVisibility(
				View.GONE);
		TextView buttonManager = (TextView) bottomBar
				.findViewById(R.id.buttonNextGroup);
		buttonManager.setText("群组设置");
		bottomBar.setVisibility(View.VISIBLE);

		View groupItemView = mInflater.inflate(
				R.layout.fragment_group_item_add, null);

		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
				groupItemWidth, RelativeLayout.LayoutParams.WRAP_CONTENT);
		Position position = switchPosition(viewContainer.getChildCount());
		params.rightMargin = -Integer.MAX_VALUE;
		params.leftMargin = position.x;
		params.topMargin = position.y;
		groupItemView.setLayoutParams(params);

		groupItemView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mMainModeManager.mGroupManagerFragment.status = GroupManagerFragment.MODE_NEWGROUP;
				mMainModeManager
						.showNext(mMainModeManager.mGroupManagerFragment);
			}
		});

		viewContainer.addView(groupItemView);

		GroupHolder holder = new GroupHolder();
		holder.groupItemView = groupItemView;
		groupHoldersMap.get("myGroup").add(holder);

		final GestureDetector detector = new GestureDetector(getActivity(),
				new SimpleOnGestureListener() {
					float lastX = 0;

					@Override
					public boolean onDown(MotionEvent e) {
						lastX = e.getRawX();
						return true;
					}

					@Override
					public void onLongPress(MotionEvent e) {
					}

					@Override
					public boolean onScroll(MotionEvent e1, MotionEvent e2,
							float distanceX, float distanceY) {
						float currentX = e2.getRawX();
						if (touchEvnetStatus == TouchEvnetStatus.MOVING_X) {
							if (lastX == 0) {
								lastX = currentX;
							}
							viewContainer
									.scrollBy(-(int) (currentX - lastX), 0);
							lastX = currentX;
							viewContainer.interceptTouchEvent();
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

	void notifyGroups(List<Group> groups, List<GroupHolder> groupHolders,
			View groupView) {

		final ScrollRelativeLayout viewContainer = (ScrollRelativeLayout) groupView
				.findViewById(R.id.viewContainer);

		for (int i = 0; i < groups.size(); i++) {
			final Group group = groups.get(i);
			GroupHolder groupHolder = new GroupHolder();
			groupHolder.gid = String.valueOf(group.gid);
			int index = groupHolders.indexOf(groupHolder);
			if (index != -1) {
				groupHolder = groupHolders.remove(index);
			} else {
				groupHolder.groupItemView = mInflater.inflate(
						R.layout.fragment_group_item, null);
				viewContainer.addView(groupHolder.groupItemView);
				groupHolder.groupItemView
						.setOnClickListener(new OnClickListener() {

							@Override
							public void onClick(View arg0) {
								if (touchEvnetStatus != TouchEvnetStatus.STATIC) {
									return;
								}
								mMainModeManager.mChatGroupFragment.mStatus = ChatFriendFragment.CHAT_GROUP;
								mMainModeManager.mChatGroupFragment.mNowChatGroup = group;
								mMainModeManager
										.showNext(mMainModeManager.mChatGroupFragment);
							}
						});

				groupHolder.groupItemView
						.setOnLongClickListener(new OnLongClickListener() {

							@Override
							public boolean onLongClick(View v) {
								mMainModeManager.mGroupManagerFragment.status = GroupManagerFragment.MODE_MANAGER;
								mMainModeManager.mGroupManagerFragment.mCurrentManagerGroup = group;
								mMainModeManager
										.showNext(mMainModeManager.mGroupManagerFragment);
								return true;
							}
						});
			}
			groupHolders.add(i, groupHolder);

			TextView groupName = (TextView) groupHolder.groupItemView
					.findViewById(R.id.groupName);
			TextView memberCount = (TextView) groupHolder.groupItemView
					.findViewById(R.id.memberCount);
			TextView notReadMessagesCount = (TextView) groupHolder.groupItemView
					.findViewById(R.id.tv_notread);
			if (group.notReadMessagesCount != 0) {
				notReadMessagesCount.setVisibility(View.VISIBLE);
				if (group.notReadMessagesCount > 99) {
					notReadMessagesCount.setText("99+");
				} else {
					notReadMessagesCount.setText(String
							.valueOf(group.notReadMessagesCount));
				}
			} else {
				notReadMessagesCount.setVisibility(View.GONE);
			}
			groupName.setText(group.name);
			memberCount.setText("(" + String.valueOf(group.members.size())
					+ ")");

			LinearLayout members = (LinearLayout) groupHolder.groupItemView
					.findViewById(R.id.members);

			members.removeAllViews();
			for (int j = 0; j < group.members.size(); j++) {
				if (j == 5) {
					break;
				}
				Friend groupFriend = app.data.groupFriends.get(group.members
						.get(j));
				final ImageView head = new ImageView(getActivity());
				android.widget.LinearLayout.LayoutParams params = new android.widget.LinearLayout.LayoutParams(
						headSize, headSize);
				params.leftMargin = headMargin;
				head.setLayoutParams(params);
				final String fileName = groupFriend.head;
				app.fileHandler.getHeadImage(fileName, new FileResult() {
					@Override
					public void onResult(String where) {
						head.setImageBitmap(app.fileHandler.bitmaps
								.get(fileName));
					}
				});
				members.addView(head);
			}

		}

		for (int i = 0; i < groupHolders.size(); i++) {
			notifyPosition(i, groupHolders.get(i));
		}
	}

	View generateAttentionGroup() {
		View groupView = mInflater.inflate(R.layout.fragment_panel, null);
		TextView panelName = (TextView) groupView.findViewById(R.id.panel_name);
		panelName.setText("我关注的群组");
		final ScrollRelativeLayout viewContainer = (ScrollRelativeLayout) groupView
				.findViewById(R.id.viewContainer);

		View bottomBar = groupView.findViewById(R.id.bottomBar);
		bottomBar.findViewById(R.id.buttonPreviousGroup).setVisibility(
				View.GONE);
		TextView buttonManager = (TextView) bottomBar
				.findViewById(R.id.buttonNextGroup);
		buttonManager.setText("查看群组分享");
		bottomBar.setVisibility(View.VISIBLE);

		View groupItemView = mInflater.inflate(
				R.layout.fragment_group_item_add, null);
		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
				groupItemWidth, RelativeLayout.LayoutParams.WRAP_CONTENT);
		Position position = switchPosition(viewContainer.getChildCount());
		params.rightMargin = -Integer.MAX_VALUE;
		params.leftMargin = position.x;
		params.topMargin = position.y;
		groupItemView.setLayoutParams(params);
		TextView text = (TextView) groupItemView
				.findViewById(R.id.newGroupText);
		text.setText("找到更多群组");
		viewContainer.addView(groupItemView);

		return groupView;
	}

	View generateNearByGroup() {
		View groupView = mInflater.inflate(R.layout.fragment_panel, null);
		TextView panelName = (TextView) groupView.findViewById(R.id.panel_name);
		panelName.setText("附近活跃群组");
		final ScrollRelativeLayout viewContainer = (ScrollRelativeLayout) groupView
				.findViewById(R.id.viewContainer);

		View bottomBar = groupView.findViewById(R.id.bottomBar);
		bottomBar.findViewById(R.id.buttonPreviousGroup).setVisibility(
				View.GONE);
		TextView buttonManager = (TextView) bottomBar
				.findViewById(R.id.buttonNextGroup);
		buttonManager.setText("亦庄站");
		bottomBar.setVisibility(View.VISIBLE);

		View groupItemView = mInflater.inflate(
				R.layout.fragment_group_item_add, null);
		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
				groupItemWidth, RelativeLayout.LayoutParams.WRAP_CONTENT);
		Position position = switchPosition(viewContainer.getChildCount());
		params.rightMargin = -Integer.MAX_VALUE;
		params.leftMargin = position.x;
		params.topMargin = position.y;
		groupItemView.setLayoutParams(params);
		TextView text = (TextView) groupItemView
				.findViewById(R.id.newGroupText);
		text.setText("找到更多群组");
		viewContainer.addView(groupItemView);

		return groupView;
	}

	View generateTempGroup() {
		View groupView = mInflater.inflate(R.layout.fragment_panel, null);
		TextView panelName = (TextView) groupView.findViewById(R.id.panel_name);
		panelName.setText("临时群组");
		final ScrollRelativeLayout viewContainer = (ScrollRelativeLayout) groupView
				.findViewById(R.id.viewContainer);

		View bottomBar = groupView.findViewById(R.id.bottomBar);
		bottomBar.findViewById(R.id.buttonPreviousGroup).setVisibility(
				View.GONE);
		TextView buttonManager = (TextView) bottomBar
				.findViewById(R.id.buttonNextGroup);
		buttonManager.setText("临时的群组");
		bottomBar.setVisibility(View.VISIBLE);

		View groupItemView = mInflater.inflate(
				R.layout.fragment_group_item_add, null);
		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
				groupItemWidth, RelativeLayout.LayoutParams.WRAP_CONTENT);
		Position position = switchPosition(viewContainer.getChildCount());
		params.rightMargin = -Integer.MAX_VALUE;
		params.leftMargin = position.x;
		params.topMargin = position.y;
		groupItemView.setLayoutParams(params);
		TextView text = (TextView) groupItemView
				.findViewById(R.id.newGroupText);
		text.setText("找到更多群组");
		viewContainer.addView(groupItemView);

		return groupView;
	}

	@Override
	public void onResume() {
		CircleMenu.show();
		CircleMenu.setPageName(getString(R.string.circlemenu_page_group));
		app.locationHandler.requestLocation(new LocationListener() {
			@Override
			public void onReceiveLocation(BDLocation location) {
				getNearByGroup(location.getLongitude(), location.getLatitude());
			}
		});
		super.onResume();
	}

	void getNearByGroup(final double longitude, final double latitude) {
		app.networkHandler.connection(new CommonNetConnection() {

			@Override
			protected void settings(Settings settings) {
				settings.url = API.DOMAIN + API.LBS_NEARBYGROUPS;
				settings.params = new HashMap<String, String>();
				settings.params.put("phone", app.data.user.phone);
				settings.params.put("accessKey", app.data.user.accessKey);
				settings.params.put("area", "{\"longitude\":\"" + longitude
						+ "\",\"latitude\":\"" + latitude + "\",\"radius\":\""
						+ 2000 + "\"}");
			}

			@Override
			public void success(final JSONObject jData) {
				app.dataHandler.exclude(new Modification() {

					@Override
					public void modifyData(Data data) {
						try {
							GroupsAndFriends groupsAndFriends = JSONParser
									.generateGroupsFromJSON(jData
											.getJSONArray("groups"));
							System.out.println(groupsAndFriends.groups);
							for (Group group : groupsAndFriends.groups) {

							}
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				});
			}
		});
	}
}
