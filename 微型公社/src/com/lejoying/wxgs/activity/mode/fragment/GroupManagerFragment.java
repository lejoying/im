package com.lejoying.wxgs.activity.mode.fragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.EditText;
import android.widget.FrameLayout.LayoutParams;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baidu.mapapi.map.MKMapStatus;
import com.baidu.mapapi.map.SupportMapFragment;
import com.baidu.platform.comapi.basestruct.GeoPoint;
import com.lejoying.wxgs.R;
import com.lejoying.wxgs.activity.mode.MainModeManager;
import com.lejoying.wxgs.activity.mode.fragment.CirclesFragment.CircleHolder;
import com.lejoying.wxgs.activity.mode.fragment.CirclesFragment.FriendHolder;
import com.lejoying.wxgs.activity.utils.CommonNetConnection;
import com.lejoying.wxgs.activity.utils.DataUtil;
import com.lejoying.wxgs.activity.utils.DataUtil.GetDataListener;
import com.lejoying.wxgs.activity.view.ScrollRelativeLayout;
import com.lejoying.wxgs.activity.view.widget.Alert;
import com.lejoying.wxgs.activity.view.widget.Alert.DialogListener;
import com.lejoying.wxgs.activity.view.widget.CircleMenu;
import com.lejoying.wxgs.app.MainApplication;
import com.lejoying.wxgs.app.adapter.AnimationAdapter;
import com.lejoying.wxgs.app.data.API;
import com.lejoying.wxgs.app.data.Data;
import com.lejoying.wxgs.app.data.entity.Circle;
import com.lejoying.wxgs.app.data.entity.Friend;
import com.lejoying.wxgs.app.data.entity.Group;
import com.lejoying.wxgs.app.handler.DataHandler.Modification;
import com.lejoying.wxgs.app.handler.FileHandler.FileResult;
import com.lejoying.wxgs.app.handler.NetworkHandler.Settings;

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

	View nextButton;
	View backButton;
	View newGroupCompleteButton;

	View addMembers;
	View removeMembers;
	View quitTheGroup;
	View modifyGroupName;

	RelativeLayout animationLayout;
	HorizontalScrollView tempFriendScroll;
	LinearLayout tempFriendsList;

	public static final String MODE_MANAGER = "manager";
	public static final String MODE_NEWGROUP = "new";
	public String status;

	public String newStatus = "friend";// "friend"||"map"||"complete"

	List<Friend> seleteFriendList;

	private static final GeoPoint GEO_BEIJING = new GeoPoint(
			(int) (39.945 * 1E6), (int) (116.404 * 1E6));

	public void setMode(MainModeManager mainMode) {
		mMainModeManager = mainMode;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		circleViewCommonAnimation();
		isRemove = false;
		seleteFriendList = new ArrayList<Friend>();
		screenWidth = getScreenWidth();
		mInflater = inflater;
		mContentView = inflater.inflate(R.layout.fragment_group_manager, null);
		circlesViewContenter = (ScrollRelativeLayout) mContentView
				.findViewById(R.id.circlesViewContainer);
		editControl = mContentView.findViewById(R.id.editControl);
		nextBar = mContentView.findViewById(R.id.nextBar);
		buttonList = mContentView.findViewById(R.id.buttonList);
		selectFriend = mContentView.findViewById(R.id.selectFriend);
		nextButton = mContentView.findViewById(R.id.buttonNext);
		backButton = mContentView.findViewById(R.id.buttonCancel);
		newGroupCompleteButton = mContentView.findViewById(R.id.buttonComplete);

		addMembers = mContentView.findViewById(R.id.addMembers);
		removeMembers = mContentView.findViewById(R.id.removeMembers);
		quitTheGroup = mContentView.findViewById(R.id.quitTheGroup);
		modifyGroupName = mContentView.findViewById(R.id.modifyGroupName);

		tempFriendsList = (LinearLayout) mContentView
				.findViewById(R.id.tempFriendsList);
		animationLayout = (RelativeLayout) mContentView
				.findViewById(R.id.animationLayout);
		tempFriendScroll = (HorizontalScrollView) mContentView
				.findViewById(R.id.tempFriendScroll);

		notifyViews();

		initEvent();
		return mContentView;
	}

	SupportMapFragment map;

	void initEvent() {
		nextButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) {
				if (status.equals(MODE_NEWGROUP)) {
					if (newStatus.equals("friend")) {

						if (seleteFriendList.size() == 0) {
							Alert.showMessage("请选择好友");
							return;
						}

						newStatus = "map";
						selectFriend.setVisibility(View.GONE);

						RelativeLayout.LayoutParams params = (android.widget.RelativeLayout.LayoutParams) editControl
								.getLayoutParams();
						params.height = (int) dp2px(80);
						editControl.setLayoutParams(params);

						views.get(circles.get(currentFriendIndex));

						circlesViewContenter.scrollTo((circles.size() + 1)
								* screenWidth, 0);

						TranslateAnimation outAnimation = new TranslateAnimation(
								(circles.size() - currentFriendIndex)
										* screenWidth, (circles.size()
										- currentFriendIndex - 1)
										* screenWidth, 0, 0);
						outAnimation.setDuration(300);

						views.get(circles.get(currentFriendIndex))
								.startAnimation(outAnimation);

						TranslateAnimation animation = new TranslateAnimation(
								screenWidth, 0, 0, 0);
						animation.setDuration(300);

						views.get("map").startAnimation(animation);

						map.getMapView()
								.getController()
								.setMapStatus(
										newMapStatusWithGeoPointAndZoom(
												GEO_BEIJING, 15));

					} else if (newStatus.equals("map")) {
						newStatus = "complete";

						circlesViewContenter.scrollTo((circles.size() + 2)
								* screenWidth, 0);

						TranslateAnimation animation = new TranslateAnimation(
								screenWidth, 0, 0, 0);
						animation.setDuration(300);

						views.get("map").startAnimation(animation);
						views.get("complete").startAnimation(animation);

						backButton.setVisibility(View.GONE);
						nextButton.setVisibility(View.GONE);
						newGroupCompleteButton.setVisibility(View.VISIBLE);
						GeoPoint center = map.getMapView().getMapCenter();
						final double latitude = center.getLatitudeE6() / 1E6;
						final double longitude = center.getLongitudeE6() / 1E6;

						final Group newGroup = new Group();

						CommonNetConnection createGroup = new CommonNetConnection() {

							@Override
							protected void settings(Settings settings) {
								settings.url = API.DOMAIN + API.GROUP_CREATE;
								Map<String, String> params = new HashMap<String, String>();
								params.put("phone", app.data.user.phone);
								params.put("accessKey", app.data.user.accessKey);
								params.put("type", "createGroup");
								params.put("name", "新建群组");
								StringBuffer members = new StringBuffer("[\""
										+ app.data.user.phone + "\",");
								for (Friend friend : seleteFriendList) {
									members.append("\"" + friend.phone + "\",");
								}
								members.replace(members.length() - 1,
										members.length(), "]");
								params.put("members", members.toString());
								String location = "{\"longitude\":\""
										+ longitude + "\",\"latitude\":\""
										+ latitude + "\"}";
								params.put("location", location);
								settings.params = params;
							}

							@Override
							public void success(JSONObject jData) {
								// TODO refresh UI
								DataUtil.getGroups(new GetDataListener() {

									@Override
									public void getSuccess() {
										if (mMainModeManager.mChatGroupFragment
												.isAdded()) {
											mMainModeManager.mChatGroupFragment.mAdapter
													.notifyDataSetChanged();
										}
										mMainModeManager.mGroupFragment
												.notifyViews();
									}
								});
							}
						};
						app.networkHandler.connection(createGroup);
					}
				} else if (status.equals(MODE_MANAGER)) {
					if (seleteFriendList.size() == 0) {
						if (isRemove) {
							Alert.showMessage("请选择要移除的好友");
						} else {
							Alert.showMessage("请选择好友");
						}
						return;
					}
					final CommonNetConnection modifyMembers = new CommonNetConnection() {
						@Override
						protected void settings(Settings settings) {
							if (!isRemove) {
								settings.url = API.DOMAIN
										+ API.GROUP_ADDMEMBERS;
							} else {
								settings.url = API.DOMAIN
										+ API.GROUP_REMOVEMEMBERS;
							}
							Map<String, String> params = new HashMap<String, String>();
							params.put("phone", app.data.user.phone);
							params.put("accessKey", app.data.user.accessKey);
							params.put("gid",
									String.valueOf(mCurrentManagerGroup.gid));
							StringBuffer members = new StringBuffer("[");
							for (Friend friend : seleteFriendList) {
								members.append("\"" + friend.phone + "\",");
							}
							members.replace(members.length() - 1,
									members.length(), "]");
							params.put("members", members.toString());
							settings.params = params;
						}

						@Override
						public void success(JSONObject jData) {
							DataUtil.getGroups(new GetDataListener() {

								@Override
								public void getSuccess() {
									if (mMainModeManager.mChatGroupFragment
											.isAdded()) {
										mMainModeManager.mChatGroupFragment.mAdapter
												.notifyDataSetChanged();
									}
									mMainModeManager.mGroupFragment
											.notifyViews();
								}
							});

							app.UIHandler.post(new Runnable() {

								@Override
								public void run() {
									selectFriend.setVisibility(View.GONE);
									buttonList.setVisibility(View.VISIBLE);
									nextBar.setVisibility(View.GONE);
									RelativeLayout.LayoutParams params = (android.widget.RelativeLayout.LayoutParams) editControl
											.getLayoutParams();
									params.height = (int) dp2px(80);
									editControl.setLayoutParams(params);
									if (isRemove) {
										isRemove = false;
									} else {

										RelativeLayout viewContainer = (RelativeLayout) views
												.get("group").findViewById(
														R.id.viewContainer);
										for (Friend friend : seleteFriendList) {
											FriendHolder holder = new FriendHolder();
											View friendView = generateFriendView(friend);
											holder.view = friendView;
											viewContainer.addView(friendView);
											groupHolder.friendHolders
													.add(holder);
										}

										resolveFriendsPositions(groupHolder);
										setFriendsPositions(groupHolder);

										circlesViewContenter.scrollTo(0, 0);
										TranslateAnimation animation1 = new TranslateAnimation(
												-screenWidth, 0, 0, 0);
										animation1.setDuration(300);
										TranslateAnimation animation2 = new TranslateAnimation(
												-screenWidth
														* (currentFriendIndex + 1),
												-screenWidth
														* (currentFriendIndex),
												0, 0);
										animation2.setDuration(300);
										views.get("group").startAnimation(
												animation1);
										views.get(
												circles.get(currentFriendIndex))
												.startAnimation(animation2);
										currentFriendIndex = 0;
									}
								}
							});

						}
					};
					Alert.showDialog(isRemove ? "确定要移除这些成员吗？" : "确定要添加这些好友吗？",
							new DialogListener() {

								@Override
								public void onCancel() {
									// TODO Auto-generated method stub

								}

								@Override
								public boolean confirm() {
									app.networkHandler
											.connection(modifyMembers);
									tempFriendsList.removeAllViews();
									return true;
								}

								@Override
								public void cancel() {
									// TODO Auto-generated method stub

								}
							});
				}
			}
		});

		backButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (status.equals(MODE_MANAGER)) {

				} else if (status.equals(MODE_NEWGROUP)) {

				}
			}
		});

		addMembers.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				circlesViewContenter.scrollTo(screenWidth, 0);
				TranslateAnimation animation = new TranslateAnimation(
						screenWidth, 0, 0, 0);
				currentFriendIndex = 0;
				animation.setDuration(300);
				views.get("group").startAnimation(animation);
				views.get(circles.get(0)).startAnimation(animation);
				selectFriend.setVisibility(View.VISIBLE);
				buttonList.setVisibility(View.GONE);
				RelativeLayout.LayoutParams params = (android.widget.RelativeLayout.LayoutParams) editControl
						.getLayoutParams();
				params.height = (int) dp2px(160);
				editControl.setLayoutParams(params);
				nextBar.setVisibility(View.VISIBLE);
			}
		});

		removeMembers.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				selectFriend.setVisibility(View.VISIBLE);
				buttonList.setVisibility(View.GONE);
				nextBar.setVisibility(View.VISIBLE);
				RelativeLayout.LayoutParams params = (android.widget.RelativeLayout.LayoutParams) editControl
						.getLayoutParams();
				params.height = (int) dp2px(160);
				editControl.setLayoutParams(params);
				isRemove = true;
			}
		});

		modifyGroupName.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				final EditText groupName;
				new AlertDialog.Builder(getActivity())
						.setTitle("请输入新的群组名称")
						.setIcon(android.R.drawable.ic_dialog_info)
						.setView(groupName = new EditText(getActivity()))
						.setPositiveButton("确定",
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										if (groupName.getText().toString()
												.equals("")) {
											Alert.showMessage("群组名称不能为空");
											return;
										}
										app.networkHandler
												.connection(new CommonNetConnection() {

													@Override
													protected void settings(
															Settings settings) {
														settings.url = API.DOMAIN
																+ API.GROUP_MODIFY;
														Map<String, String> params = new HashMap<String, String>();
														params.put(
																"phone",
																app.data.user.phone);
														params.put(
																"accessKey",
																app.data.user.accessKey);
														params.put(
																"gid",
																String.valueOf(mCurrentManagerGroup.gid));
														params.put(
																"name",
																groupName
																		.getText()
																		.toString());
														settings.params = params;
													}

													@Override
													public void success(
															JSONObject jData) {
														System.out
																.println(jData);
														DataUtil.getGroups(new GetDataListener() {

															@Override
															public void getSuccess() {
																if (mMainModeManager.mChatGroupFragment
																		.isAdded()) {
																	mMainModeManager.mChatGroupFragment.mAdapter
																			.notifyDataSetChanged();
																}
																mMainModeManager.mGroupFragment
																		.notifyViews();
															}
														});

													}
												});
										((TextView) views.get("group")
												.findViewById(R.id.panel_name))
												.setText(groupName.getText());
									}
								}).setNegativeButton("取消", null).show();
			}
		});

		quitTheGroup.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Alert.showDialog("确定要退出" + mCurrentManagerGroup.name + "吗？",
						new DialogListener() {

							@Override
							public void onCancel() {
								// TODO Auto-generated method stub

							}

							@Override
							public boolean confirm() {
								CommonNetConnection quitTheGroup = new CommonNetConnection() {

									@Override
									protected void settings(Settings settings) {
										settings.url = API.DOMAIN
												+ API.GROUP_REMOVEMEMBERS;
										Map<String, String> params = new HashMap<String, String>();
										params.put("phone", app.data.user.phone);
										params.put("accessKey",
												app.data.user.accessKey);
										params.put(
												"gid",
												String.valueOf(mCurrentManagerGroup.gid));
										params.put("members", "[\""
												+ app.data.user.phone + "\"]");
										settings.params = params;
									}

									@Override
									public void success(JSONObject jData) {
										System.out.println(jData);
									}
								};
								app.networkHandler.connection(quitTheGroup);
								app.dataHandler.exclude(new Modification() {
									@Override
									public void modifyData(Data data) {
										data.groups.remove(String
												.valueOf(mCurrentManagerGroup.gid));
										data.groupsMap.remove(String
												.valueOf(mCurrentManagerGroup.gid));
									}

									@Override
									public void modifyUI() {
										mMainModeManager.mGroupFragment
												.notifyViews();
										mMainModeManager.clearBackStack(1);
										mMainModeManager.back();
									}
								});
								return true;
							}

							@Override
							public void cancel() {
								// TODO Auto-generated method stub

							}
						});
			}
		});

		newGroupCompleteButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mMainModeManager.back();
			}
		});
	}

	private MKMapStatus newMapStatusWithGeoPointAndZoom(GeoPoint p, float zoom) {
		MKMapStatus status = new MKMapStatus();
		status.targetGeo = p;
		status.zoom = zoom;
		return status;
	}

	int screenWidth;

	@Override
	public void onResume() {
		CircleMenu.showBack();
		super.onResume();
	}

	Map<String, View> views = new HashMap<String, View>();
	List<String> circles = new ArrayList<String>();
	int currentFriendIndex;

	@Override
	public void onDestroyView() {
		views.clear();
		circles.clear();
		map = null;
		super.onDestroyView();
	}

	CircleHolder groupHolder;

	private void notifyViews() {

		int marginTop = (int) dp2px(20);

		circlesViewContenter.removeAllViews();

		Map<String, CircleHolder> circleHolders = new Hashtable<String, CircleHolder>();

		// generate and add group
		groupHolder = new CircleHolder();
		View v = generateGroup(groupHolder);
		circlesViewContenter.setGravity(Gravity.LEFT | Gravity.TOP);
		RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
				screenWidth, LayoutParams.WRAP_CONTENT);
		layoutParams.setMargins(0, marginTop, layoutParams.rightMargin,
				-Integer.MAX_VALUE);
		v.setLayoutParams(layoutParams);
		circlesViewContenter.addView(v);
		views.put("group", v);

		// generate circles
		for (int i = 0; i < app.data.circles.size(); i++) {
			Circle circle = app.data.circlesMap.get(app.data.circles.get(i));
			if (views.get("group#" + circle.rid) == null) {
				CircleHolder circleHolder = new CircleHolder();
				circleHolders.put("group#" + circle.rid, circleHolder);
				View view = generateCircleView(circle, circleHolder);
				views.put("group#" + circle.rid, view);
			}
			circles.add("group#" + circle.rid);
		}
		// add circles
		for (int i = 0; i < circles.size(); i++) {
			String group = circles.get(i);
			View view = views.get(group);
			RelativeLayout.LayoutParams layoutParams3 = new RelativeLayout.LayoutParams(
					screenWidth, LayoutParams.WRAP_CONTENT);
			layoutParams3.setMargins((i + 1) * screenWidth, marginTop,
					-Integer.MAX_VALUE, 0);
			view.setLayoutParams(layoutParams3);
			TextView manager = (TextView) view
					.findViewById(R.id.panel_right_button);
			manager.setText("添加成员");
			manager.setVisibility(View.VISIBLE);
			view.findViewById(R.id.bottomBar).setVisibility(View.VISIBLE);
			if (view.getParent() != null) {
				((ViewGroup) view.getParent()).removeView(view);
			}
			circlesViewContenter.addView(view);

			View buttonPreviousGroup = view
					.findViewById(R.id.buttonPreviousGroup);
			buttonPreviousGroup.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					TranslateAnimation animation = new TranslateAnimation(
							-screenWidth, 0, 0, 0);
					animation.setDuration(300);

					circlesViewContenter.scrollTo(
							circlesViewContenter.getScrollX() - screenWidth, 0);

					views.get(circles.get(currentFriendIndex)).startAnimation(
							animation);
					views.get(circles.get(--currentFriendIndex))
							.startAnimation(animation);
				}
			});

			View buttonNextGroup = view.findViewById(R.id.buttonNextGroup);
			buttonNextGroup.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {

					TranslateAnimation animation = new TranslateAnimation(
							screenWidth, 0, 0, 0);
					animation.setDuration(300);

					circlesViewContenter.scrollTo(
							circlesViewContenter.getScrollX() + screenWidth, 0);
					views.get(circles.get(currentFriendIndex)).startAnimation(
							animation);
					views.get(circles.get(++currentFriendIndex))
							.startAnimation(animation);
				}
			});

			if (i == 0) {
				buttonPreviousGroup.setVisibility(View.GONE);
			}
			if (i == circles.size() - 1) {
				buttonNextGroup.setVisibility(View.GONE);
			}
		}

		// generate map view
		views.put("map", generateMapView());
		RelativeLayout.LayoutParams mapViewlayoutParams = new RelativeLayout.LayoutParams(
				screenWidth, LayoutParams.WRAP_CONTENT);
		mapViewlayoutParams.setMargins((circles.size() + 1) * screenWidth,
				marginTop, -Integer.MAX_VALUE, -Integer.MAX_VALUE);
		circlesViewContenter.addView(views.get("map"), mapViewlayoutParams);

		// generate complete view
		views.put("complete", generateCompleteView());
		RelativeLayout.LayoutParams completeViewlayoutParams = new RelativeLayout.LayoutParams(
				screenWidth, LayoutParams.WRAP_CONTENT);
		completeViewlayoutParams.setMargins((circles.size() + 2) * screenWidth,
				marginTop, -Integer.MAX_VALUE, -Integer.MAX_VALUE);
		circlesViewContenter.addView(views.get("complete"),
				completeViewlayoutParams);

		if (status.equals(MODE_MANAGER)) {
			currentFriendIndex = 0;
			RelativeLayout.LayoutParams params = (android.widget.RelativeLayout.LayoutParams) editControl
					.getLayoutParams();
			params.height = (int) dp2px(80);
			editControl.setLayoutParams(params);
			selectFriend.setVisibility(View.GONE);
			buttonList.setVisibility(View.VISIBLE);
			nextBar.setVisibility(View.GONE);
		} else if (status.equals(MODE_NEWGROUP)) {
			newStatus = "friend";
			currentFriendIndex = 0;
			RelativeLayout.LayoutParams params = (android.widget.RelativeLayout.LayoutParams) editControl
					.getLayoutParams();
			params.height = (int) dp2px(160);
			editControl.setLayoutParams(params);
			selectFriend.setVisibility(View.VISIBLE);
			buttonList.setVisibility(View.GONE);
			nextBar.setVisibility(View.VISIBLE);
			circlesViewContenter.scrollTo(screenWidth, 0);
			map = SupportMapFragment.newInstance();
			getActivity().getSupportFragmentManager().beginTransaction()
					.replace(R.id.mapContainer, map).commit();
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
		float dp = getActivity().getResources().getDisplayMetrics().density
				* px + 0.5f;
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
					(int) dp2px(55f),
					android.widget.RelativeLayout.LayoutParams.WRAP_CONTENT);
			params.rightMargin = -Integer.MAX_VALUE;

			params.topMargin = friendHolder.position.y;
			params.leftMargin = friendHolder.position.x;
			friendHolder.view.setLayoutParams(params);
		}
	}

	View generateCompleteView() {
		View contentView = mInflater.inflate(R.layout.fragment_panel, null);

		TextView groupName = (TextView) contentView
				.findViewById(R.id.panel_name);
		groupName.setText("创建群组完成");

		View viewContainer = contentView.findViewById(R.id.viewContainer);
		viewContainer.setVisibility(View.GONE);
		TextView textContainer = (TextView) contentView
				.findViewById(R.id.textContainer);
		textContainer.setVisibility(View.VISIBLE);
		textContainer
				.setText("您的群组已经创建完成，1km范围内的用户可以查看到，您需要增加群的影响力和覆盖范围，请增加群的活跃度,提升群组等级");

		return contentView;
	}

	View generateMapView() {
		View mapView = mInflater.inflate(R.layout.fragment_panel_map, null);

		TextView groupName = (TextView) mapView.findViewById(R.id.panel_name);
		groupName.setText("请选择地点");

		return mapView;
	}

	boolean isRemove;

	public Map<String, FriendHolder> tempFriendHolders = new Hashtable<String, FriendHolder>();

	View generateGroup(final CircleHolder circleHolder) {
		final View circleView = mInflater
				.inflate(R.layout.fragment_panel, null);

		if (mCurrentManagerGroup == null) {
			return circleView;
		}
		TextView groupName = (TextView) circleView
				.findViewById(R.id.panel_name);
		groupName.setText(mCurrentManagerGroup.name);
		TextView rightGroupName = (TextView) circleView
				.findViewById(R.id.panel_right_button);
		rightGroupName.setText("群组管理");
		rightGroupName.setVisibility(View.VISIBLE);
		final RelativeLayout friendContainer = (RelativeLayout) circleView
				.findViewById(R.id.viewContainer);

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

			convertView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(final View holderView) {
					if (!isRemove || friend.phone.equals(app.data.user.phone)) {
						return;
					}
					if (seleteFriendList.contains(friend)) {
						Alert.showMessage("用户已选择");
						return;
					}
					seleteFriendList.add(friend);
					final View friendView = generateFriendView(friend);
					final View animationView = generateFriendView(friend);
					tempFriendScroll.smoothScrollTo(0, 0);
					int[] location = new int[2];
					holderView.getLocationInWindow(location);

					// change
					circleHolder.friendHolders.remove(friendHolder);

					tempFriendHolders.put(friend.phone, friendHolder);

					RelativeLayout friendContainer = (RelativeLayout) circleView
							.findViewById(R.id.viewContainer);
					// change
					friendContainer.removeView(holderView);

					int animationFromIndex = friendHolder.index;
					int animationCount = 6 - animationFromIndex % 6;

					// change
					resolveFriendsPositions(circleHolder);
					setFriendsPositions(circleHolder);

					for (int i = 0; i < animationCount; i++) {
						int index = animationFromIndex + i;
						if (index < circleHolder.friendHolders.size()) {
							View view = circleHolder.friendHolders.get(index).view;
							if (index % 6 == 2) {
								view.startAnimation(friendToPreLineAnimation);
							} else {
								view.startAnimation(friendToLeftAnimation);
							}
						}
					}

					RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
							(int) dp2px(55f),
							android.widget.RelativeLayout.LayoutParams.WRAP_CONTENT);
					params.leftMargin = location[0];
					params.topMargin = location[1] - 50;
					animationView.setLayoutParams(params);
					animationLayout.addView(animationView);

					LinearLayout.LayoutParams tempParams = new LinearLayout.LayoutParams(
							(int) dp2px(55f),
							LinearLayout.LayoutParams.WRAP_CONTENT);
					tempParams.leftMargin = (int) dp2px(20);
					friendView.setVisibility(View.INVISIBLE);
					if (tempFriendsList.getChildCount() == 0) {
						tempParams.rightMargin = (int) dp2px(75);
					}
					tempFriendsList.addView(friendView, 0, tempParams);

					friendView.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View view) {
							seleteFriendList.remove(friend);
							RelativeLayout friendContainer = (RelativeLayout) circleView
									.findViewById(R.id.viewContainer);
							friendContainer.scrollTo(0, 0);

							final FriendHolder friendHolder = tempFriendHolders
									.get(friend.phone);
							friendContainer.addView(friendHolder.view);

							friendHolder.view.setVisibility(View.INVISIBLE);
							circleHolder.friendHolders.add(0, friendHolder);

							resolveFriendsPositions(circleHolder);
							setFriendsPositions(circleHolder);

							LinearLayout.LayoutParams tempParams = new LinearLayout.LayoutParams(
									(int) dp2px(55f),
									LinearLayout.LayoutParams.WRAP_CONTENT);
							tempParams.leftMargin = (int) dp2px(20);
							tempParams.rightMargin = (int) dp2px(75);
							if (tempFriendsList.getChildCount() != 0) {
								tempFriendsList.getChildAt(
										tempFriendsList.getChildCount() - 1)
										.setLayoutParams(tempParams);
							}

							for (int i = 1; i < circleHolder.friendHolders
									.size(); i++) {
								if (i > 6) {
									break;
								}
								View friendView = circleHolder.friendHolders
										.get(i).view;
								if (i == 3) {
									friendView
											.startAnimation(friendToNextLineAnimation);
								} else if (i == 6) {
									friendView
											.startAnimation(lastFriendToRightAnimation);
								} else {
									friendView
											.startAnimation(friendToRightAnimation);
								}
							}

							int[] location = new int[2];
							view.getLocationOnScreen(location);

							RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
									(int) dp2px(55f),
									android.widget.RelativeLayout.LayoutParams.WRAP_CONTENT);
							params.leftMargin = location[0];
							params.topMargin = location[1] - 50;
							animationView.setLayoutParams(params);

							animationLayout.addView(animationView);

							int count = tempFriendsList.getChildCount();
							int index = 0;
							for (int i = 0; i < count; i++) {
								View v = tempFriendsList.getChildAt(i);
								if (v.equals(view)) {
									index = i;
									break;
								}
							}
							tempFriendsList.removeView(view);
							for (int i = index; i < count - 1; i++) {
								View v = tempFriendsList.getChildAt(i);
								v.startAnimation(allTempFriendMoveToLeft);
							}

							TranslateAnimation moveToCircleAnimation = new TranslateAnimation(
									0, dp2px(46) - location[0], 0, dp2px(75)
											- (location[1] - 50));
							moveToCircleAnimation.setDuration(270);
							moveToCircleAnimation
									.setAnimationListener(new AnimationAdapter() {
										@Override
										public void onAnimationEnd(
												Animation animation) {
											animationLayout
													.removeView(animationView);
											friendHolder.view
													.setVisibility(View.VISIBLE);
										}
									});
							animationView.startAnimation(moveToCircleAnimation);
						}
					});

					int count = tempFriendsList.getChildCount();
					for (int i = 1; i < count; i++) {
						tempFriendsList.getChildAt(i).startAnimation(
								allTempFriendMoveToRight);
					}

					int currnetX = (int) dp2px(20);
					int currentY = animationLayout.getHeight()
							- (int) dp2px(155);

					TranslateAnimation moveToTempListAnimation = new TranslateAnimation(
							0, currnetX - location[0], 0, currentY
									- (location[1] - 50));
					moveToTempListAnimation.setDuration(270);
					moveToTempListAnimation
							.setAnimationListener(new AnimationAdapter() {
								@Override
								public void onAnimationEnd(Animation animation) {
									animationLayout.removeView(animationView);
									friendView.setVisibility(View.VISIBLE);
								}
							});
					animationView.startAnimation(moveToTempListAnimation);
				}
			});

			friendContainer.addView(convertView);
		}

		resolveFriendsPositions(circleHolder);
		setFriendsPositions(circleHolder);

		final GestureDetector detector = new GestureDetector(getActivity(),
				new SimpleOnGestureListener() {
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
					public boolean onScroll(MotionEvent e1, MotionEvent e2,
							float distanceX, float distanceY) {
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
		final View circleView = mInflater
				.inflate(R.layout.fragment_panel, null);

		TextView groupName = (TextView) circleView
				.findViewById(R.id.panel_name);
		groupName.setText(circle.name);
		final RelativeLayout friendContainer = (RelativeLayout) circleView
				.findViewById(R.id.viewContainer);

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

			convertView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(final View holderView) {
					if (status.equals(MODE_MANAGER)
							&& mCurrentManagerGroup.members
									.contains(friend.phone)) {
						Alert.showMessage("群组中已存在");
						return;
					}
					if (seleteFriendList.contains(friend)) {
						Alert.showMessage("用户已添加");
						return;
					}
					seleteFriendList.add(friend);
					final View tempFriend = generateFriendView(friend);
					final View animationView = generateFriendView(friend);

					tempFriendScroll.smoothScrollTo(0, 0);
					int[] location = new int[2];
					holderView.getLocationInWindow(location);

					RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
							(int) dp2px(55f),
							android.widget.RelativeLayout.LayoutParams.WRAP_CONTENT);

					params.leftMargin = location[0];
					params.topMargin = location[1] - 50;
					animationView.setLayoutParams(params);
					animationLayout.addView(animationView);

					LinearLayout.LayoutParams tempParams = new LinearLayout.LayoutParams(
							(int) dp2px(55f),
							LinearLayout.LayoutParams.WRAP_CONTENT);
					tempParams.leftMargin = (int) dp2px(20);
					tempFriend.setVisibility(View.INVISIBLE);
					if (tempFriendsList.getChildCount() == 0) {
						tempParams.rightMargin = (int) dp2px(75);
					}
					tempFriendsList.addView(tempFriend, 0, tempParams);

					tempFriend.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View view) {
							seleteFriendList.remove(friend);
							int count = tempFriendsList.getChildCount();
							int index = 0;
							for (int i = 0; i < count; i++) {
								View v = tempFriendsList.getChildAt(i);
								if (v.equals(view)) {
									index = i;
									break;
								}
							}
							tempFriendsList.removeView(view);
							for (int i = index; i < count - 1; i++) {
								View v = tempFriendsList.getChildAt(i);
								v.startAnimation(allTempFriendMoveToLeft);
							}
						}
					});

					int count = tempFriendsList.getChildCount();
					for (int i = 1; i < count; i++) {
						tempFriendsList.getChildAt(i).startAnimation(
								allTempFriendMoveToRight);
					}

					int currnetX = (int) dp2px(20);
					int currentY = animationLayout.getHeight()
							- (int) dp2px(155);

					TranslateAnimation moveToTempListAnimation = new TranslateAnimation(
							0, currnetX - location[0], 0, currentY
									- (location[1] - 50));
					moveToTempListAnimation.setDuration(270);
					moveToTempListAnimation
							.setAnimationListener(new AnimationAdapter() {
								@Override
								public void onAnimationEnd(Animation animation) {
									animationLayout.removeView(animationView);
									tempFriend.setVisibility(View.VISIBLE);
								}
							});
					animationView.startAnimation(moveToTempListAnimation);

				}
			});

			friendContainer.addView(convertView);
		}

		resolveFriendsPositions(circleHolder);
		setFriendsPositions(circleHolder);

		final GestureDetector detector = new GestureDetector(getActivity(),
				new SimpleOnGestureListener() {
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
					public boolean onScroll(MotionEvent e1, MotionEvent e2,
							float distanceX, float distanceY) {
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

		lastFriendToRightAnimation = new TranslateAnimation(dp2px(-118), 0,
				dp2px(100), dp2px(100));
		lastFriendToRightAnimation.setStartOffset(150);
		lastFriendToRightAnimation.setDuration(120);

		friendToLeftAnimation = new TranslateAnimation(dp2px(103), 0, 0, 0);
		friendToLeftAnimation.setStartOffset(150);
		friendToLeftAnimation.setDuration(120);

		friendToRightAnimation = new TranslateAnimation(dp2px(-103), 0, 0, 0);

		friendToRightAnimation.setStartOffset(150);
		friendToRightAnimation.setDuration(120);

		friendToNextLineAnimation = new TranslateAnimation(dp2px(206), 0,
				dp2px(-100), 0);

		friendToNextLineAnimation.setStartOffset(150);
		friendToNextLineAnimation.setDuration(120);

		friendToPreLineAnimation = new TranslateAnimation(dp2px(-206), 0,
				dp2px(100), 0);

		friendToPreLineAnimation.setStartOffset(150);
		friendToPreLineAnimation.setDuration(120);

		allTempFriendMoveToLeft = new TranslateAnimation(dp2px(75), 0, 0, 0);
		allTempFriendMoveToLeft.setDuration(120);

		allTempFriendMoveToRight = new TranslateAnimation(-dp2px(75), 0, 0, 0);
		allTempFriendMoveToRight.setStartOffset(150);
		allTempFriendMoveToRight.setDuration(120);
	}

	View generateFriendView(Friend friend) {
		View convertView = mInflater.inflate(
				R.layout.fragment_circles_gridpage_item, null);
		final ImageView head = (ImageView) convertView
				.findViewById(R.id.iv_head);
		TextView nickname = (TextView) convertView
				.findViewById(R.id.tv_nickname);
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
