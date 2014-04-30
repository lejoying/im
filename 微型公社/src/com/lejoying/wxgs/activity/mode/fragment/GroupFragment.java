package com.lejoying.wxgs.activity.mode.fragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.lejoying.wxgs.R;
import com.lejoying.wxgs.activity.mode.MainModeManager;
import com.lejoying.wxgs.activity.utils.CommonNetConnection;
import com.lejoying.wxgs.activity.view.ScrollContainer;
import com.lejoying.wxgs.activity.view.ScrollContainer.OnPageChangedListener;
import com.lejoying.wxgs.activity.view.ScrollContainer.ViewContainer;
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

	long nearByGroupDistance = 10000;

	View mContentView;
	ScrollContainer mScrollContainer;
	ViewContainer groupViewContainer;

	RelativeLayout current_me_group;
	RelativeLayout current_group_local;
	ImageView current_me_group_status;
	ImageView current_group_local_status;

	public void setMode(MainModeManager mainMode) {
		mMainModeManager = mainMode;
	}

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
			mScrollContainer = (ScrollContainer) mContentView
					.findViewById(R.id.groupViewContainer);
			groupViewContainer = mScrollContainer.getViewContainer();

			current_me_group = (RelativeLayout) mContentView
					.findViewById(R.id.current_me_group);
			current_group_local = (RelativeLayout) mContentView
					.findViewById(R.id.current_group_local);
			current_me_group_status = (ImageView) mContentView
					.findViewById(R.id.current_me_group_status);
			current_group_local_status = (ImageView) mContentView
					.findViewById(R.id.current_group_local_status);

			mMainModeManager.handleMenu(true);
			initEvent();

			notifyViews();
		}
		return mContentView;
	}

	void initEvent() {
		current_me_group.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (myGroup.getVisibility() == View.GONE) {
					myGroup.setVisibility(View.VISIBLE);
					nearByGroup.setVisibility(View.GONE);
					current_me_group_status.setVisibility(View.VISIBLE);
					current_group_local_status.setVisibility(View.GONE);
				}

			}
		});
		current_group_local.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (nearByGroup.getVisibility() == View.GONE) {
					nearByGroup.setVisibility(View.VISIBLE);
					myGroup.setVisibility(View.GONE);
					current_me_group_status.setVisibility(View.GONE);
					current_group_local_status.setVisibility(View.VISIBLE);
				}
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

			// RelativeLayout.LayoutParams params2 = new
			// RelativeLayout.LayoutParams(
			// RelativeLayout.LayoutParams.MATCH_PARENT,
			// RelativeLayout.LayoutParams.WRAP_CONTENT);
			// params2.topMargin = top;
			// params2.bottomMargin = -Integer.MAX_VALUE;
			// attentionGroup.setLayoutParams(params2);
			// groupViewContainer.addView(attentionGroup);
			//
			// top += (groupPanelHeight + (int) dp2px(25));
			//
			RelativeLayout.LayoutParams params3 = new RelativeLayout.LayoutParams(
					RelativeLayout.LayoutParams.MATCH_PARENT,
					RelativeLayout.LayoutParams.WRAP_CONTENT);
			// params3.topMargin = top;
			params3.topMargin = (int) dp2px(20);
			params3.bottomMargin = -Integer.MAX_VALUE;
			nearByGroup.setLayoutParams(params3);
			nearByGroup.setVisibility(View.GONE);
			groupViewContainer.addView(nearByGroup);

			// top += (groupPanelHeight + (int) dp2px(25));

			// RelativeLayout.LayoutParams params4 = new
			// RelativeLayout.LayoutParams(
			// RelativeLayout.LayoutParams.MATCH_PARENT,
			// RelativeLayout.LayoutParams.WRAP_CONTENT);
			// params4.topMargin = top;
			// params4.bottomMargin = -Integer.MAX_VALUE;
			// tempGroup.setLayoutParams(params4);
			// groupViewContainer.addView(tempGroup);
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

		// if (attentionGroup == null) {
		// groupHoldersMap.put("attentionGroup", new ArrayList<GroupHolder>());
		// attentionGroup = generateAttentionGroup();
		// }
		//
		if (nearByGroup == null) {
			groupHoldersMap.put("nearByGroup", new ArrayList<GroupHolder>());
			nearByGroup = generateNearByGroup();
		}

		// if (tempGroup == null) {
		// groupHoldersMap.put("tempGroup", new ArrayList<GroupHolder>());
		// tempGroup = generateTempGroup();
		// }
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
			position.x = groupItemWidth + (int) dp2px(16) + i / 4
					* groupScrollSpaceWidth;
		} else if ((i + 1) % 4 == 3) {
			position.y = (int) dp2px(103);
			position.x = (int) dp2px(10) + i / 4 * groupScrollSpaceWidth;
		} else if ((i + 1) % 4 == 0) {
			position.y = (int) dp2px(103);
			position.x = groupItemWidth + (int) dp2px(16) + i / 4
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
					if (gid != null && gid.equals(((GroupHolder) o).gid)) {
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
		ScrollContainer scrollContainer = (ScrollContainer) groupView
				.findViewById(R.id.viewContainer);
		final ViewContainer viewContainer = scrollContainer.getViewContainer();

		scrollContainer.setScrollStatus(ScrollContainer.SCROLL_PAGING);
		scrollContainer
				.setScrollDirection(ScrollContainer.DIRECTION_HORIZONTAL);

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

		return groupView;
	}

	void notifyGroups(List<Group> groups, List<GroupHolder> groupHolders,
			final View groupView) {

		List<GroupHolder> tempHolders = new ArrayList<GroupFragment.GroupHolder>();
		tempHolders.addAll(groupHolders);
		final LinearLayout ll_pagepoint = (LinearLayout) groupView
				.findViewById(R.id.ll_pagepoint);
		ll_pagepoint.removeAllViews();
		final int pageSize = ((groups.size() + 1) % 4) == 0 ? ((groups.size() + 1) / 4)
				: ((groups.size() + 1) / 4) + 1;
		final LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(
				android.widget.LinearLayout.LayoutParams.WRAP_CONTENT,
				android.widget.LinearLayout.LayoutParams.WRAP_CONTENT);
		for (int i = 0; i < pageSize; i++) {
			ImageView iv = new ImageView(getActivity());
			if (i == 0) {
				iv.setImageResource(R.drawable.point_white);
			} else {
				iv.setImageResource(R.drawable.point_blank);
			}
			iv.setLayoutParams(params1);
			ll_pagepoint.addView(iv);
		}
		ScrollContainer scrollContainer = (ScrollContainer) groupView
				.findViewById(R.id.viewContainer);
		final ViewContainer viewContainer = scrollContainer.getViewContainer();
		scrollContainer.setOnPageChangedListener(new OnPageChangedListener() {

			@Override
			public void pageChanged(int currentPage) {
				ll_pagepoint.removeAllViews();
				for (int i = 0; i < pageSize; i++) {
					ImageView iv = new ImageView(getActivity());
					if (i == currentPage) {
						iv.setImageResource(R.drawable.point_white);
					} else {
						iv.setImageResource(R.drawable.point_blank);
					}
					iv.setLayoutParams(params1);
					ll_pagepoint.addView(iv);
				}
			}
		});
		for (int i = 0; i < groups.size(); i++) {
			final Group group = groups.get(i);
			GroupHolder groupHolder = new GroupHolder();
			groupHolder.gid = String.valueOf(group.gid);
			int index = groupHolders.indexOf(groupHolder);
			if (index != -1) {
				groupHolder = groupHolders.remove(index);
				tempHolders.remove(groupHolder);
			} else {
				groupHolder.groupItemView = mInflater.inflate(
						R.layout.fragment_group_item, null);
				viewContainer.addView(groupHolder.groupItemView);
				groupHolder.groupItemView
						.setOnClickListener(new OnClickListener() {
							@Override
							public void onClick(View arg0) {
								if (nearByGroup.equals(groupView)) {
									mMainModeManager.mGroupBusinessCardFragment.mGroup = group;
									mMainModeManager
											.showNext(mMainModeManager.mGroupBusinessCardFragment);
								} else {
									mMainModeManager.mChatGroupFragment.mStatus = ChatFriendFragment.CHAT_GROUP;
									mMainModeManager.mChatGroupFragment.mNowChatGroup = group;
									mMainModeManager
											.showNext(mMainModeManager.mChatGroupFragment);
								}
							}
						});

				groupHolder.groupItemView
						.setOnLongClickListener(new OnLongClickListener() {

							@Override
							public boolean onLongClick(View v) {
								if (!nearByGroup.equals(groupView)) {
									mMainModeManager.mGroupManagerFragment.status = GroupManagerFragment.MODE_MANAGER;
									mMainModeManager.mGroupManagerFragment.mCurrentManagerGroup = group;
									mMainModeManager
											.showNext(mMainModeManager.mGroupManagerFragment);
								}
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

		for (GroupHolder holder : tempHolders) {
			if (holder.gid != null) {
				viewContainer.removeView(holder.groupItemView);
				groupHolders.remove(holder);
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
		ScrollContainer scrollContainer = (ScrollContainer) groupView
				.findViewById(R.id.viewContainer);
		final ViewContainer viewContainer = scrollContainer.getViewContainer();

		View bottomBar = groupView.findViewById(R.id.bottomBar);
		bottomBar.findViewById(R.id.buttonPreviousGroup).setVisibility(
				View.GONE);
		bottomBar.findViewById(R.id.tv_pagination).setVisibility(View.GONE);
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
		ScrollContainer scrollContainer = (ScrollContainer) groupView
				.findViewById(R.id.viewContainer);
		final ViewContainer viewContainer = scrollContainer.getViewContainer();
		scrollContainer.setScrollStatus(ScrollContainer.SCROLL_PAGING);
		scrollContainer
				.setScrollDirection(ScrollContainer.DIRECTION_HORIZONTAL);

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
		groupItemView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				nearByGroupDistance += 1000;
				requestLocation();
				Toast.makeText(getActivity(), "groupItemView",
						Toast.LENGTH_SHORT).show();
				// mMainModeManager.mGroupManagerFragment.status =
				// GroupManagerFragment.MODE_NEWGROUP;
				// mMainModeManager
				// .showNext(mMainModeManager.mGroupManagerFragment);
			}
		});
		viewContainer.addView(groupItemView);
		GroupHolder holder = new GroupHolder();
		holder.groupItemView = groupItemView;
		groupHoldersMap.get("nearByGroup").add(holder);
		return groupView;
	}

	View generateTempGroup() {
		View groupView = mInflater.inflate(R.layout.fragment_panel, null);
		TextView panelName = (TextView) groupView.findViewById(R.id.panel_name);
		panelName.setText("临时群组");
		ScrollContainer scrollContainer = (ScrollContainer) groupView
				.findViewById(R.id.viewContainer);
		final ViewContainer viewContainer = scrollContainer.getViewContainer();

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
		// CircleMenu.show();
		// CircleMenu.setPageName(getString(R.string.circlemenu_page_group));
		mMainModeManager.handleMenu(true);
		requestLocation();
		super.onResume();
	}

	void requestLocation() {
		app.locationHandler.requestLocation(new LocationListener() {
			@Override
			public void onReceiveLocation(BDLocation location) {
				getNearByGroup(location.getLongitude(), location.getLatitude());
			}
		});
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
						+ nearByGroupDistance + "\"}");
			}

			@Override
			public void success(final JSONObject jData) {
				app.dataHandler.exclude(new Modification() {
					List<Group> nearByGroups = new ArrayList<Group>();

					@Override
					public void modifyData(Data data) {
						try {
							GroupsAndFriends groupsAndFriends = JSONParser
									.generateGroupsFromJSON(jData
											.getJSONArray("groups"));
							System.out.println(groupsAndFriends.groups);
							for (Group group : groupsAndFriends.groups) {
								nearByGroups.add(group);
							}
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}

					@Override
					public void modifyUI() {
						notifyGroups(nearByGroups,
								groupHoldersMap.get("nearByGroup"), nearByGroup);
					}
				});
			}
		});
	}
}
