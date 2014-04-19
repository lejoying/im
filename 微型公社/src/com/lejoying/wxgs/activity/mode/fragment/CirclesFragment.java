package com.lejoying.wxgs.activity.mode.fragment;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout.LayoutParams;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lejoying.wxgs.R;
import com.lejoying.wxgs.activity.mode.BaseModeManager.KeyDownListener;
import com.lejoying.wxgs.activity.mode.MainModeManager;
import com.lejoying.wxgs.activity.utils.CommonNetConnection;
import com.lejoying.wxgs.activity.utils.DataUtil;
import com.lejoying.wxgs.activity.utils.DataUtil.GetDataListener;
import com.lejoying.wxgs.activity.view.ScrollContainer;
import com.lejoying.wxgs.activity.view.ScrollContainer.OnPageChangedListener;
import com.lejoying.wxgs.activity.view.ScrollContainer.onInterceptTouchDownListener;
import com.lejoying.wxgs.activity.view.manager.FrictionAnimation;
import com.lejoying.wxgs.activity.view.manager.FrictionAnimation.AnimatingView;
import com.lejoying.wxgs.activity.view.widget.Alert;
import com.lejoying.wxgs.activity.view.widget.Alert.AlertInputDialog;
import com.lejoying.wxgs.activity.view.widget.Alert.AlertInputDialog.OnDialogClickListener;
import com.lejoying.wxgs.activity.view.widget.CircleMenu;
import com.lejoying.wxgs.app.MainApplication;
import com.lejoying.wxgs.app.adapter.AnimationAdapter;
import com.lejoying.wxgs.app.data.API;
import com.lejoying.wxgs.app.data.Data;
import com.lejoying.wxgs.app.data.entity.Circle;
import com.lejoying.wxgs.app.data.entity.Friend;
import com.lejoying.wxgs.app.data.entity.Message;
import com.lejoying.wxgs.app.handler.DataHandler.Modification;
import com.lejoying.wxgs.app.handler.FileHandler.FileResult;
import com.lejoying.wxgs.app.handler.NetworkHandler.Settings;

public class CirclesFragment extends BaseFragment {

	MainApplication app = MainApplication.getMainApplication();
	MainModeManager mMainModeManager;

	View mContentView;
	ScrollContainer mScrollContainer;
	RelativeLayout circlesViewContenter;
	View editControl;
	LinearLayout tempFriendsList;
	View save;
	View copy;
	View newGroup;
	View modifyCircleName;
	View deleteCircle;

	RelativeLayout animationLayout;
	HorizontalScrollView tempFriendScroll;

	LayoutInflater mInflater;

	public String copyStatus = "move";// "move"||"copy"
	public String mode = "normal";// "normal"||"edit"

	public void setMode(MainModeManager mainMode) {
		mMainModeManager = mainMode;
	}

	@Override
	public void onResume() {
		CircleMenu.show();
		CircleMenu.setPageName(getString(R.string.circlemenu_page_circles));
		LinearLayout ll_menu_app = mMainModeManager.ll_menu_app;
		if (ll_menu_app.getVisibility() == View.GONE) {
			ll_menu_app.setVisibility(View.VISIBLE);
		}
		super.onResume();
	}

	FrictionAnimation decelerationAnimation = new FrictionAnimation();
	AnimatingView animatingView = new AnimatingView();

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mInflater = inflater;
		mContentView = inflater.inflate(R.layout.fragment_circles_scroll, null);
		mScrollContainer = (ScrollContainer) mContentView
				.findViewById(R.id.circlesViewContainer);
		circlesViewContenter = mScrollContainer.getViewContainer();

		editControl = mContentView.findViewById(R.id.editControl);

		save = mContentView.findViewById(R.id.save);
		copy = mContentView.findViewById(R.id.copy);
		newGroup = mContentView.findViewById(R.id.newGroup);
		modifyCircleName = mContentView.findViewById(R.id.modifyCircleName);
		deleteCircle = mContentView.findViewById(R.id.deleteCircle);

		tempFriendsList = (LinearLayout) mContentView
				.findViewById(R.id.tempFriendsList);
		animationLayout = (RelativeLayout) mContentView
				.findViewById(R.id.animationLayout);
		tempFriendScroll = (HorizontalScrollView) mContentView
				.findViewById(R.id.tempFriendScroll);

		animatingView.view = circlesViewContenter;

		density = getActivity().getResources().getDisplayMetrics().density;

		LinearLayout ll_menu_app = mMainModeManager.ll_menu_app;
		if (ll_menu_app.getVisibility() == View.GONE) {
			ll_menu_app.setVisibility(View.VISIBLE);
		}

		circleViewCommonAnimation();

		notifyViews();

		initEvent();

		return mContentView;
	}

	public void notifyViews() {
		notifyViews(true);
	}

	public void notifyViews(boolean initShowMessageCount) {
		circlesViewContenter.removeAllViews();
		generateViews(initShowMessageCount);
		if (mode.equals("normal")) {
			mScrollContainer.setScrollStatus(ScrollContainer.SCROLL_SMOOTH);
			mScrollContainer
					.setScrollDirection(ScrollContainer.DIRECTION_VERTICALITY);
			int top = 25;
			int scrollToY = 0;
			for (int i = 0; i < normalShow.size(); i++) {
				View v = views.get(normalShow.get(i));
				if (v.getParent() == null) {
					circlesViewContenter.addView(v, i);
				}
				// v.measure(ViewGroup.LayoutParams.MATCH_PARENT, -1);
				// int height = v.getHeight();
				int height = (int) dp2px(((Integer) v.getTag()).floatValue());
				RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
						LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
				layoutParams.setMargins(layoutParams.leftMargin, top,
						layoutParams.rightMargin, -Integer.MAX_VALUE);

				if (currentEditPosition != -1
						&& v.equals(views.get(circles.get(currentEditPosition)))) {
					scrollToY = top;
					scrollToY -= dp2px(20);
				}
				// float px = 0.1f * px + 0.5f;
				top = top + height + 25;
				v.setLayoutParams(layoutParams);

			}
			if (currentEditPosition != -1) {
				mScrollContainer.setAnticipatedHeight(scrollToY
						+ getScreenHeight());
				circlesViewContenter.scrollTo(0, scrollToY);
			}
		} else if (mode.equals("edit")) {
			mScrollContainer.setScrollStatus(ScrollContainer.SCROLL_PAGING);
			mScrollContainer
					.setScrollDirection(ScrollContainer.DIRECTION_HORIZONTAL);
			final int screenWidth = getScreenWidth();

			for (int i = 0; i < circles.size(); i++) {
				String group = circles.get(i);
				View v = views.get(group);
				RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
						screenWidth, LayoutParams.WRAP_CONTENT);
				layoutParams.setMargins(i * screenWidth, (int) dp2px(20),
						-Integer.MAX_VALUE, 0);
				v.setLayoutParams(layoutParams);
				TextView manager = (TextView) v
						.findViewById(R.id.panel_right_button);
				manager.setText("分组管理");
				manager.setVisibility(View.VISIBLE);
				v.findViewById(R.id.bottomBar).setVisibility(View.VISIBLE);
				final TextView tv_pagination = (TextView) v
						.findViewById(R.id.tv_pagination);
				tv_pagination.setVisibility(View.VISIBLE);
				tv_pagination.setText(i + 1 + "/" + circles.size());
				View buttonPreviousGroup = v
						.findViewById(R.id.buttonPreviousGroup);
				buttonPreviousGroup.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						if (currentEditPosition > 0) {
							View currentView = views.get(circles
									.get(currentEditPosition));
							currentEditPosition--;
							View previousView = views.get(circles
									.get(currentEditPosition));

							TranslateAnimation animation = new TranslateAnimation(
									-screenWidth, 0, 0, 0);
							animation.setDuration(300);

							circlesViewContenter.scrollTo(currentEditPosition
									* screenWidth, 0);

							currentView.startAnimation(animation);
							previousView.startAnimation(animation);

						}
					}
				});

				View buttonNextGroup = v.findViewById(R.id.buttonNextGroup);
				buttonNextGroup.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						if (currentEditPosition < circles.size() - 1) {
							View currentView = views.get(circles
									.get(currentEditPosition));
							currentEditPosition++;
							View nextView = views.get(circles
									.get(currentEditPosition));

							TranslateAnimation animation = new TranslateAnimation(
									screenWidth, 0, 0, 0);
							animation.setDuration(300);

							circlesViewContenter.scrollTo(currentEditPosition
									* screenWidth, 0);
							currentView.startAnimation(animation);
							nextView.startAnimation(animation);
						}
					}
				});

				if (circles.size() == 1) {
					v.findViewById(R.id.bottomBar).setVisibility(View.GONE);
				} else {
					if (i == 0) {
						buttonPreviousGroup.setVisibility(View.GONE);
					}
					if (i == circles.size() - 1) {
						buttonNextGroup.setVisibility(View.GONE);
					}
				}
				if (v.getParent() == null) {
					circlesViewContenter.addView(v, i);
				}
			}
			mScrollContainer.setAnticipatedWidth(circles.size() * screenWidth);
			mScrollContainer.setPage(currentEditPosition);
			mScrollContainer
					.setOnPageChangedListener(new OnPageChangedListener() {

						@Override
						public void pageChanged(int currentPage) {
							currentEditPosition = currentPage;
							System.out.println(currentPage);
						}
					});
		}
	}

	boolean isCopy;
	boolean isSaved;

	void resetSaveStatus() {
		if (isSaved) {
			isSaved = false;
			ImageView saveImage = (ImageView) save.findViewById(R.id.saveImage);
			TextView saveText = (TextView) save.findViewById(R.id.saveText);
			saveImage.setImageResource(R.drawable.save_up);
			saveText.setText("保存");
		}
	}

	void notifyFriendChanged() {

		Set<String> moveOutKeys = modifyFriend.moveOut.keySet();
		Set<String> moveInKeys = modifyFriend.moveIn.keySet();
		for (final String key : moveOutKeys) {
			final List<String> phones = modifyFriend.moveOut.get(key);
			if (phones.size() == 0 || (app.data.circlesMap.get(key) == null)) {
				continue;
			}
			app.dataHandler.exclude(new Modification() {

				@Override
				public void modifyData(Data data) {
					Circle circle = data.circlesMap.get(key);
					if (circle != null) {
						circle.phones.removeAll(phones);
					}
					data.circlesMap.get("-1").phones
							.addAll(checkFriends(phones));
				}
			});
			StringBuffer buffer = new StringBuffer("[");
			for (String phone : phones) {
				buffer.append("\"" + phone + "\",");
			}
			buffer.replace(buffer.length() - 1, buffer.length(), "]");
			final String phonesParam = buffer.toString();
			CommonNetConnection netConnection = new CommonNetConnection() {

				@Override
				protected void settings(Settings settings) {
					settings.url = API.DOMAIN + API.CIRCLE_MOVEOROUT;
					Map<String, String> params = new HashMap<String, String>();
					params.put("phone", app.data.user.phone);
					params.put("accessKey", app.data.user.accessKey);
					params.put("rid", key);
					params.put("phoneto", phonesParam);
					params.put("filter", "REMOVE");
					settings.params = params;
				}

				@Override
				public void success(JSONObject jData) {
					System.out.println(jData);
				}
			};
			app.networkHandler.connection(netConnection);
		}

		for (final String key : moveInKeys) {
			final List<String> phones = modifyFriend.moveIn.get(key);
			if (phones.size() == 0 || (app.data.circlesMap.get(key) == null)) {
				continue;
			}
			app.dataHandler.exclude(new Modification() {

				@Override
				public void modifyData(Data data) {
					Circle circle = data.circlesMap.get(key);
					if (circle != null) {
						circle.phones.addAll(0, phones);
					}
					data.circlesMap.get("-1").phones.removeAll(phones);

				}

				@Override
				public void modifyUI() {
					View defaultContent = views.get("group#-1");
					CircleHolder circleHolder = circleHolders.get("group#-1");
					if (defaultContent != null && circleHolder != null) {
						ScrollContainer scrollContainer = (ScrollContainer) defaultContent
								.findViewById(R.id.viewContainer);
						RelativeLayout friendContainer = scrollContainer
								.getViewContainer();

						for (String phone : phones) {
							FriendHolder holder = new FriendHolder();
							holder.phone = phone;
							int index = circleHolder.friendHolders
									.indexOf(holder);
							if (index != -1) {
								holder = circleHolder.friendHolders
										.remove(index);
								if (holder != null && holder.view != null) {
									friendContainer.removeView(holder.view);
								}
							}
						}
					}
				}
			});
			StringBuffer buffer = new StringBuffer("[");
			for (String phone : phones) {
				buffer.append("\"" + phone + "\",");
			}
			buffer.replace(buffer.length() - 1, buffer.length(), "]");
			final String phonesParam = buffer.toString();
			CommonNetConnection netConnection = new CommonNetConnection() {

				@Override
				protected void settings(Settings settings) {
					settings.url = API.DOMAIN + API.CIRCLE_MOVEOROUT;
					Map<String, String> params = new HashMap<String, String>();
					params.put("phone", app.data.user.phone);
					params.put("accessKey", app.data.user.accessKey);
					params.put("rid", key);
					params.put("phoneto", phonesParam);
					params.put("filter", "SHIFTIN");
					settings.params = params;
				}

				@Override
				public void success(JSONObject jData) {
					System.out.println(jData);
				}
			};
			app.networkHandler.connection(netConnection);
		}

		modifyFriend.clear();
	}

	void initEvent() {

		save.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				ImageView saveImage = (ImageView) save
						.findViewById(R.id.saveImage);
				TextView saveText = (TextView) save.findViewById(R.id.saveText);
				if (!isSaved) {
					isSaved = true;
					saveImage.setImageResource(R.drawable.saved_down);
					saveText.setText("已保存");
					notifyFriendChanged();
				}
			}
		});
		copy.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				ImageView copyStatus = (ImageView) copy
						.findViewById(R.id.copyImage);
				TextView copyStatusText = (TextView) copy
						.findViewById(R.id.copyText);
				if (!isCopy) {
					isCopy = true;
					copyStatus.setImageResource(R.drawable.choise_down);
					copyStatusText.setText("复制中");
				} else {
					isCopy = false;
					copyStatus.setImageResource(R.drawable.choise_up);
					copyStatusText.setText("复制");
				}
			}
		});

		newGroup.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Alert.createInputDialog(getActivity()).setTitle("请输入分组名")
						.setOnConfirmClickListener(new OnDialogClickListener() {

							@Override
							public void onClick(final AlertInputDialog dialog) {
								if (dialog.getInputText().equals("")) {
									Alert.showMessage("密友圈名称不能为空");
									return;
								}
								app.networkHandler
										.connection(new CommonNetConnection() {

											@Override
											protected void settings(
													Settings settings) {
												settings.url = API.DOMAIN
														+ API.CIRCLE_ADDCIRCLE;
												Map<String, String> params = new HashMap<String, String>();
												params.put("phone",
														app.data.user.phone);
												params.put("accessKey",
														app.data.user.accessKey);
												params.put("name",
														dialog.getInputText());
												settings.params = params;
											}

											@Override
											public void success(JSONObject jData) {
												DataUtil.getCircles(new GetDataListener() {
													@Override
													public void getSuccess() {
														// mAdapter.notifyDataSetChanged();
													}
												});

											}
										});
							}

						}).show();
			}
		});

		deleteCircle.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				final String rid = String.valueOf(
						circles.get(currentEditPosition)).substring(6);
				if (rid.equals("-1")) {
					Alert.showMessage("默认分组不能删除");
					return;
				}
				Alert.createDialog(getActivity())
						.setTitle("确定要删除该组？删除该组后该组好友如果不在其它分组将被自动转移到默认分组中。")
						.setOnConfirmClickListener(new OnDialogClickListener() {
							@Override
							public void onClick(AlertInputDialog dialog) {
								circles.remove("group#" + rid);
								View v = views.remove("group#" + rid);
								if (v != null) {
									circlesViewContenter.removeView(v);
								}
								app.dataHandler.exclude(new Modification() {
									@Override
									public void modifyData(Data data) {
										data.circles.remove(rid);
										Circle circle = data.circlesMap
												.remove(rid);
										if (circle != null) {
											data.circlesMap.get("-1").phones
													.addAll(checkFriends(circle.phones));
										}
									}

									@Override
									public void modifyUI() {
										notifyViews();
									}
								});
								CommonNetConnection deleteCirccle = new CommonNetConnection() {

									@Override
									protected void settings(Settings settings) {
										settings.url = API.DOMAIN
												+ API.CIRCLE_DELETE;
										Map<String, String> params = new HashMap<String, String>();
										params.put("phone", app.data.user.phone);
										params.put("accessKey",
												app.data.user.accessKey);
										params.put("rid", rid);
										settings.params = params;
									}

									@Override
									public void success(JSONObject jData) {
										DataUtil.getCircles(new GetDataListener() {
											@Override
											public void getSuccess() {
												notifyViews();
											}
										});
									}
								};
								app.networkHandler.connection(deleteCirccle);
							}
						}).show();
			}
		});

		modifyCircleName.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (String.valueOf(circles.get(currentEditPosition))
						.substring(6).equals("-1")) {
					Alert.showMessage("默认分组不能修改名称");
					return;
				}
				Circle circle = app.data.circlesMap.get(String.valueOf(
						circles.get(currentEditPosition)).substring(6));
				Alert.createInputDialog(getActivity()).setTitle("请输入新的密友圈名称")
						.setOnConfirmClickListener(new OnDialogClickListener() {

							@Override
							public void onClick(AlertInputDialog dialog) {
								final String circleName = dialog.getInputText();
								if (circleName.equals("")) {
									Alert.showMessage("密友圈名称不能为空");
									return;
								}
								app.networkHandler
										.connection(new CommonNetConnection() {

											@Override
											protected void settings(
													Settings settings) {
												settings.url = API.DOMAIN
														+ API.CIRCLE_MODIFY;
												Map<String, String> params = new HashMap<String, String>();
												params.put("phone",
														app.data.user.phone);
												params.put("accessKey",
														app.data.user.accessKey);
												params.put(
														"rid",
														String.valueOf(
																circles.get(currentEditPosition))
																.substring(6));
												params.put("name", circleName);
												settings.params = params;
											}

											@Override
											public void success(JSONObject jData) {
												System.out.println(jData);
											}
										});
								((TextView) views.get(
										circles.get(currentEditPosition))
										.findViewById(R.id.panel_name))
										.setText(circleName);
							}
						}).setInputText(circle.name).show();

			}
		});

	}

	List<String> checkFriends(Collection<String> checkPhones) {
		List<String> allInOtherCircleFriend = new ArrayList<String>();
		List<String> resultNoCircle = new ArrayList<String>();
		for (String rid : app.data.circles) {
			Circle circle = app.data.circlesMap.get(rid);
			allInOtherCircleFriend.addAll(circle.phones);
		}
		for (String phone : checkPhones) {
			if (!allInOtherCircleFriend.contains(phone)) {
				resultNoCircle.add(phone);
			}
		}
		return resultNoCircle;
	}

	int currentEditPosition = -1;

	public void switchToEditMode(View view) {
		// Alert.showMessage("分组管理");

		if (mode.equals("normal")) {
			mode = "edit";
		} else {
			return;
		}
		resetSaveStatus();

		final int screenWidth = getScreenWidth();

		mMainModeManager.setKeyDownListener(new KeyDownListener() {

			@Override
			public boolean onKeyDown(int keyCode, KeyEvent event) {
				if (keyCode == KeyEvent.KEYCODE_BACK) {
					swichToNormalMode();
				}
				return false;
			}
		});

		CircleMenu.showBack();

		int[] location = new int[2];
		view.getLocationOnScreen(location);
		int y = location[1];

		for (int i = 0; i < circles.size(); i++) {
			View v = views.get(circles.get(i));
			if (v.equals(view)) {
				currentEditPosition = i;
				break;
			}
		}

		circlesViewContenter.removeAllViews();
		circlesViewContenter.scrollTo(0, 0);

		notifyViews();

		TranslateAnimation editControlIn = new TranslateAnimation(0, 0,
				dp2px(160), 0);
		editControlIn.setDuration(500);
		editControl.setVisibility(View.VISIBLE);
		editControl.startAnimation(editControlIn);

		circlesViewContenter.scrollTo(currentEditPosition * screenWidth, 0);

		TranslateAnimation viewMove = new TranslateAnimation(0, 0, y - 50
				- dp2px(20), 0);
		viewMove.setDuration(350);
		view.startAnimation(viewMove);

	}

	public void swichToNormalMode() {
		notifyFriendChanged();
		if (tempFriendsList.getChildCount() != 0) {
			Alert.createDialog(getActivity())
					.setTitle("未放入其它密友圈的好友将自动转移到默认分组中。")
					.setOnConfirmClickListener(new OnDialogClickListener() {

						@Override
						public void onClick(AlertInputDialog dialog) {
							app.dataHandler.exclude(new Modification() {
								@Override
								public void modifyData(Data data) {
									Set<String> checkPhones = tempFriendHolders
											.keySet();
									data.circlesMap.get("-1").phones
											.addAll(checkFriends(checkPhones));
								}

								@Override
								public void modifyUI() {
									normalMode();
								}
							});
							tempFriendsList.removeAllViews();
							tempViewMap.clear();
							tempFriendHolders.clear();
						}
					});
		} else {
			normalMode();
		}
	}

	void normalMode() {
		CircleMenu.show();
		if (mode.equals("edit")) {
			mode = "normal";
		} else {
			return;
		}
		mMainModeManager.setKeyDownListener(null);

		TranslateAnimation editControlIn = new TranslateAnimation(0, 0, 0,
				dp2px(160));
		editControlIn.setDuration(400);
		editControl.setVisibility(View.GONE);
		editControl.startAnimation(editControlIn);

		circlesViewContenter.removeAllViews();

		for (int i = 0; i < circles.size(); i++) {
			String group = circles.get(i);
			View v = views.get(group);
			TextView manager = (TextView) v
					.findViewById(R.id.panel_right_button);
			manager.setVisibility(View.GONE);
			v.findViewById(R.id.bottomBar).setVisibility(View.GONE);

		}

		notifyViews();

		currentEditPosition = -1;
	}

	float density = 1.0f;

	public int getScreenWidth() {
		return getActivity().getResources().getDisplayMetrics().widthPixels;
	}

	public int getScreenHeight() {
		return getActivity().getResources().getDisplayMetrics().heightPixels;
	}

	public float dp2px(float px) {
		float dp = density * px + 0.5f;
		return dp;
	}

	Map<String, View> views = new HashMap<String, View>();
	List<String> normalShow = new ArrayList<String>();
	List<String> circles = new ArrayList<String>();
	List<String> messages = new ArrayList<String>();

	int lastChatFriendsSize;

	void generateViews(boolean initShowMessageCount) {
		// generate message views;
		if (initShowMessageCount) {
			lastChatFriendsSize = app.data.lastChatFriends.size();
			lastChatFriendsSize = lastChatFriendsSize < 5 ? lastChatFriendsSize
					: 5;
		}
		normalShow.clear();

		View newFriendButtonView = views.get("button#newfriend");
		if (newFriendButtonView == null) {
			newFriendButtonView = generateNewFriendButtonView();
			newFriendButtonView.setTag(46);
			views.put("button#newfriend", newFriendButtonView);
		}

		int newFriendsCount = 0;
		for (Friend friend : app.data.newFriends) {
			if (app.data.friends.get(friend.phone) == null) {
				newFriendsCount++;
			}
		}
		if (newFriendsCount != 0) {
			notifyNewFriendButtonView(newFriendButtonView, newFriendsCount);
			normalShow.add("button#newfriend");
		} else if (newFriendButtonView.getParent() != null) {
			((ViewGroup) newFriendButtonView.getParent())
					.removeView(newFriendButtonView);
		}

		// messages.clear();
		// for (int i = 0; i < lastChatFriendsSize; i++) {
		// String phone = app.data.lastChatFriends.get(i);
		// Friend friend = app.data.friends.get(phone);
		// if (friend == null) {
		// continue;
		// }
		// View messageView = views.get("message#" + phone);
		// if (messageView == null) {
		// messageView = generateMessageView(phone);
		// messageView.setTag(74);
		// views.put("message#" + phone, messageView);
		// }
		// messages.add("message#");
		// normalShow.add("message#" + phone);
		// notifyMessageView(messageView, friend);
		// }
		//
		// View moremessageView = views.get("button#moremessage");
		// if (moremessageView == null) {
		// moremessageView = generateMoreMessageButtonView();
		// moremessageView.setTag(46);
		// views.put("button#moremessage", moremessageView);
		// }
		// if (lastChatFriendsSize != 0) {
		// normalShow.add("button#moremessage");
		// } else if (moremessageView.getParent() != null) {
		// ((ViewGroup) moremessageView.getParent())
		// .removeView(moremessageView);
		// }

		circles.clear();
		// generate circles
		for (int i = 0; i < app.data.circles.size(); i++) {
			Circle circle = app.data.circlesMap.get(app.data.circles.get(i));

			View circleView = views.get("group#" + circle.rid);
			if (circleView == null) {
				CircleHolder circleHolder = new CircleHolder();
				circleHolders.put("group#" + circle.rid, circleHolder);
				circleView = generateCircleView();
				views.put("group#" + circle.rid, circleView);
				circleView.setTag(262);
			}
			notifyCircleView(circleView, circle,
					circleHolders.get("group#" + circle.rid));

			normalShow.add("group#" + circle.rid);
			circles.add("group#" + circle.rid);
		}

		// if (views.get("button#creategroup") == null) {
		// View createGroupButtonView = generateCreateGroupButtonView();
		// createGroupButtonView.setTag(46);
		// views.put("button#creategroup", createGroupButtonView);
		// }
		// normalShow.add("button#creategroup");

		if (views.get("button#findmore") == null) {
			View findMoreFriendButtonView = generateFindMoreFriendButtonView();
			findMoreFriendButtonView.setTag(46);
			views.put("button#findmore", findMoreFriendButtonView);
		}
		normalShow.add("button#findmore");

	}

	View generateNewFriendButtonView() {
		View newFriendButtonView = mInflater.inflate(
				R.layout.fragment_item_button, null);
		return newFriendButtonView;
	}

	void notifyNewFriendButtonView(View newFriendButtonView, int newFriendsCount) {
		Button newFriendButton = (Button) newFriendButtonView
				.findViewById(R.id.button);
		if (newFriendsCount != 0) {
			newFriendButton.setText("新的好友(" + newFriendsCount + ")");
		} else {
			newFriendButton.setText("新的好友");
		}
		newFriendButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mMainModeManager.showNext(mMainModeManager.mNewFriendsFragment);
			}
		});
	}

	View generateMoreMessageButtonView() {
		View moreMessageButtonView = mInflater.inflate(
				R.layout.fragment_item_button, null);
		Button moreMessageButton = (Button) moreMessageButtonView
				.findViewById(R.id.button);
		moreMessageButton.setText("点击查看更多");
		moreMessageButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

			}
		});

		return moreMessageButtonView;

	}

	View generateFindMoreFriendButtonView() {
		View findMoreFriendButtonView = mInflater.inflate(
				R.layout.fragment_item_button, null);
		Button findMoreFriendButton = (Button) findMoreFriendButtonView
				.findViewById(R.id.button);
		findMoreFriendButton.setText("找到更多密友");
		findMoreFriendButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mMainModeManager
						.showNext(mMainModeManager.mSearchFriendFragment);
			}
		});

		return findMoreFriendButtonView;
	}

	// unused
	View generateCreateGroupButtonView() {

		View createGroupButtonView = mInflater.inflate(
				R.layout.fragment_item_button, null);
		Button createGroupButton = (Button) createGroupButtonView
				.findViewById(R.id.button);
		createGroupButton.setText("新建分组");
		createGroupButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				CircleMenu.showBack();
				final EditText circleName;
				new AlertDialog.Builder(getActivity())
						.setTitle("请输入分组名")
						.setIcon(android.R.drawable.ic_dialog_info)
						.setView(circleName = new EditText(getActivity()))
						.setPositiveButton("确定",
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										CircleMenu.show();
										if (circleName.getText().toString()
												.equals("")) {
											Alert.showMessage("密友圈名称不能为空");
											return;
										}
										app.networkHandler
												.connection(new CommonNetConnection() {

													@Override
													protected void settings(
															Settings settings) {
														settings.url = API.DOMAIN
																+ API.CIRCLE_ADDCIRCLE;
														Map<String, String> params = new HashMap<String, String>();
														params.put(
																"phone",
																app.data.user.phone);
														params.put(
																"accessKey",
																app.data.user.accessKey);
														params.put(
																"name",
																circleName
																		.getText()
																		.toString());
														settings.params = params;
													}

													@Override
													public void success(
															JSONObject jData) {
														DataUtil.getCircles(new GetDataListener() {
															@Override
															public void getSuccess() {
																// mAdapter.notifyDataSetChanged();
															}
														});

													}
												});
									}
								})
						.setNegativeButton("取消",
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										CircleMenu.show();
									}
								}).setOnCancelListener(new OnCancelListener() {

							@Override
							public void onCancel(DialogInterface dialog) {
								CircleMenu.show();
							}
						}).show();
			}
		});

		return createGroupButtonView;

	}

	View generateMessageView(String lastChatFriendPhone) {
		View messageView = mInflater.inflate(
				R.layout.fragment_circles_messages_item, null);
		return messageView;
	}

	void notifyMessageView(View messageView, final Friend friend) {
		final ImageView head = (ImageView) messageView
				.findViewById(R.id.iv_head);
		TextView nickName = (TextView) messageView
				.findViewById(R.id.tv_nickname);
		TextView lastChatMessage = (TextView) messageView
				.findViewById(R.id.tv_lastchat);
		TextView notReadCount = (TextView) messageView
				.findViewById(R.id.tv_notread);

		nickName.setText(friend.nickName);
		Message lastMessage = friend.messages.get(friend.messages.size() - 1);
		if (lastMessage.contentType.equals("text")) {
			lastChatMessage
					.setText(friend.messages.get(friend.messages.size() - 1).content);
		} else if (lastMessage.contentType.equals("image")) {
			lastChatMessage.setText(getString(R.string.text_picture));
		} else if (lastMessage.contentType.equals("voice")) {
			// lastChatMessage.setText(getString(R.string.text_voice));
			lastChatMessage.setText(getActivity().getResources().getString(
					R.string.text_voice));
		}
		final String headFileName = friend.head;
		app.fileHandler.getHeadImage(headFileName, new FileResult() {
			@Override
			public void onResult(String where) {
				head.setImageBitmap(app.fileHandler.bitmaps.get(headFileName));
			}
		});

		Integer notread = friend.notReadMessagesCount;

		if (notread != null) {
			if (notread > 0) {
				notReadCount.setVisibility(View.VISIBLE);
				notReadCount.setText(notread.toString());
			} else {
				notReadCount.setText("");
				notReadCount.setVisibility(View.GONE);
			}
		}
		messageView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				mMainModeManager.mChatFragment.mStatus = ChatFriendFragment.CHAT_FRIEND;
				mMainModeManager.mChatFragment.mNowChatFriend = friend;
				mMainModeManager.showNext(mMainModeManager.mChatFragment);
			}
		});
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

	class CircleHolder {
		public List<FriendHolder> friendHolders = new ArrayList<FriendHolder>();
	}

	public Map<String, CircleHolder> circleHolders = new Hashtable<String, CircleHolder>();

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

	class ModifyFriend {
		public Map<String, List<String>> moveIn = new HashMap<String, List<String>>();
		public Map<String, List<String>> moveOut = new HashMap<String, List<String>>();

		public void clear() {
			moveIn.clear();
			moveOut.clear();
		}
	}

	ModifyFriend modifyFriend = new ModifyFriend();

	Map<String, View> tempViewMap = new HashMap<String, View>();
	public Map<String, FriendHolder> tempFriendHolders = new Hashtable<String, CirclesFragment.FriendHolder>();

	View generateCircleView() {
		final View circleView = mInflater
				.inflate(R.layout.fragment_panel, null);
		return circleView;
	}

	boolean checkFriendHolderIsInOtherCircle(String phone) {
		FriendHolder holder = new FriendHolder();
		holder.phone = phone;
		Set<String> keys = circleHolders.keySet();
		for (String key : keys) {
			if (!key.equals("group#-1")) {
				if (circleHolders.get(key).friendHolders.contains(holder)) {
					return true;
				}
			}
		}
		return false;
	}

	void notifyCircleView(final View circleView, Circle circle,
			CircleHolder circleHolder) {
		TextView groupName = (TextView) circleView
				.findViewById(R.id.panel_name);
		groupName.setText(circle.name);
		final LinearLayout ll_pagepoint = (LinearLayout) circleView
				.findViewById(R.id.ll_pagepoint);
		ll_pagepoint.removeAllViews();
		final int pageSize = (circle.phones.size() % 6) == 0 ? (circle.phones
				.size() / 6) : (circle.phones.size() / 6) + 1;
		final LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
				android.widget.LinearLayout.LayoutParams.WRAP_CONTENT,
				android.widget.LinearLayout.LayoutParams.WRAP_CONTENT);
		for (int i = 0; i < pageSize; i++) {
			ImageView iv = new ImageView(getActivity());
			if (i == 0) {
				iv.setImageResource(R.drawable.point_white);
			} else {
				iv.setImageResource(R.drawable.point_blank);
			}
			iv.setLayoutParams(params);
			ll_pagepoint.addView(iv);
		}
		final ScrollContainer scrollContainer = (ScrollContainer) circleView
				.findViewById(R.id.viewContainer);
		scrollContainer
				.setScrollDirection(ScrollContainer.DIRECTION_HORIZONTAL);
		scrollContainer.setScrollStatus(ScrollContainer.SCROLL_PAGING);
		final RelativeLayout container = scrollContainer.getViewContainer();

		List<String> phones = circle.phones;
		Map<String, Friend> friends = app.data.friends;

		for (int i = 0; i < phones.size(); i++) {
			final Friend friend = friends.get(phones.get(i));
			FriendHolder friendHolder = new FriendHolder();
			friendHolder.phone = friend.phone;
			int index = circleHolder.friendHolders.indexOf(friendHolder);
			friendHolder = (index != -1 ? circleHolder.friendHolders
					.remove(index) : null);
			View convertView;
			if (friendHolder == null) {
				if (tempFriendHolders.get(friend.phone) != null) {
					return;
				}
				convertView = generateFriendView(friend);
				friendHolder = new FriendHolder();
				friendHolder.phone = friend.phone;
				friendHolder.view = convertView;
				final FriendHolder clickHolder = friendHolder;
				convertView.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(final View holderView) {
						if (mode.equals("normal")) {
							mMainModeManager.mChatFragment.mStatus = ChatFriendFragment.CHAT_FRIEND;
							mMainModeManager.mChatFragment.mNowChatFriend = friend;
							mMainModeManager
									.showNext(mMainModeManager.mChatFragment);
						} else if (mode.equals("edit")) {
							final View friendView = generateFriendView(friend);
							final View animationView = generateFriendView(friend);
							tempFriendScroll.smoothScrollTo(0, 0);
							int[] location = new int[2];
							holderView.getLocationInWindow(location);

							CircleHolder chickHolder = circleHolders
									.get(circles.get(currentEditPosition));
							if (!isCopy) {
								// change
								chickHolder.friendHolders.remove(clickHolder);

								View circleView = views.get(circles
										.get(currentEditPosition));

								ScrollContainer scrollContainer = (ScrollContainer) circleView
										.findViewById(R.id.viewContainer);
								RelativeLayout friendContainer = scrollContainer
										.getViewContainer();

								friendContainer.removeView(holderView);

								// change
								resolveFriendsPositions(chickHolder);
								setFriendsPositions(chickHolder);

								int animationFromIndex = clickHolder.index;
								int animationCount = 6 - animationFromIndex % 6;

								for (int i = 0; i < animationCount; i++) {
									int index = animationFromIndex + i;
									if (index < chickHolder.friendHolders
											.size()) {
										View view = chickHolder.friendHolders
												.get(index).view;
										if (index % 6 == 2) {
											view.startAnimation(friendToPreLineAnimation);
										} else {
											view.startAnimation(friendToLeftAnimation);
										}
									}
								}

								tempFriendHolders
										.put(friend.phone, clickHolder);

								String fromRid = circles.get(
										currentEditPosition).substring(6);
								if (!fromRid.equals("-1")) {
									List<String> phones = modifyFriend.moveOut
											.get(fromRid);
									if (phones == null) {
										modifyFriend.moveOut
												.put(fromRid,
														(phones = new ArrayList<String>()));
									}
									if (modifyFriend.moveIn.get(fromRid) == null
											|| !modifyFriend.moveIn
													.get(fromRid).remove(
															friend.phone)) {
										phones.add(friend.phone);
									}
								}

								resetSaveStatus();
							} else {
								FriendHolder newHolder = new FriendHolder();
								newHolder.view = generateFriendView(friend);
								newHolder.view.setOnClickListener(this);
								newHolder.phone = friend.phone;
								tempFriendHolders.put(friend.phone, newHolder);
							}

							View tempFriendView;
							if ((tempFriendView = tempViewMap.get(friend.phone)) != null) {
								tempFriendsList.removeView(tempFriendView);
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
							tempViewMap.put(friend.phone, friendView);
							tempFriendsList.addView(friendView, 0, tempParams);

							friendView
									.setOnClickListener(new OnClickListener() {
										@Override
										public void onClick(View view) {

											String circleKey = circles
													.get(currentEditPosition);
											String toRid = circleKey
													.substring(6);

											if (toRid.equals("-1")
													&& checkFriendHolderIsInOtherCircle(friend.phone)) {
												Alert.showMessage("该好友已存在于其它分组.");
												return;
											}

											resetSaveStatus();
											View circleView = views
													.get(circleKey);
											ScrollContainer scrollContainer = (ScrollContainer) circleView
													.findViewById(R.id.viewContainer);
											RelativeLayout friendContainer = scrollContainer
													.getViewContainer();

											friendContainer.scrollTo(0, 0);

											final FriendHolder friendHolder = tempFriendHolders
													.remove(friend.phone);

											CircleHolder circleHolder = circleHolders
													.get(circleKey);

											int friendIndex;
											if ((friendIndex = circleHolder.friendHolders
													.indexOf(friendHolder)) != -1) {
												FriendHolder removeHolder = circleHolder.friendHolders
														.remove(friendIndex);
												friendContainer
														.removeView(removeHolder.view);
											} else {

												if (!toRid.equals("-1")) {
													if (modifyFriend.moveOut
															.get(toRid) == null
															|| !modifyFriend.moveOut
																	.get(toRid)
																	.remove(friend.phone)) {
														List<String> phones = modifyFriend.moveIn
																.get(toRid);
														if (phones == null) {
															modifyFriend.moveIn
																	.put(toRid,
																			(phones = new ArrayList<String>()));
														}
														phones.add(0,
																friend.phone);
													}
												}
											}

											friendContainer
													.addView(friendHolder.view);

											circleHolder.friendHolders.add(0,
													friendHolder);

											resolveFriendsPositions(circleHolder);
											setFriendsPositions(circleHolder);

											friendHolder.view
													.setVisibility(View.INVISIBLE);

											LinearLayout.LayoutParams tempParams = new LinearLayout.LayoutParams(
													(int) dp2px(55f),
													LinearLayout.LayoutParams.WRAP_CONTENT);
											tempParams.leftMargin = (int) dp2px(20);
											tempParams.rightMargin = (int) dp2px(75);
											if (tempFriendsList.getChildCount() != 0) {
												tempFriendsList
														.getChildAt(
																tempFriendsList
																		.getChildCount() - 1)
														.setLayoutParams(
																tempParams);
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
											animationView
													.setLayoutParams(params);

											animationLayout
													.addView(animationView);

											TranslateAnimation moveToCircleAnimation = new TranslateAnimation(
													0,
													dp2px(46) - location[0],
													0,
													dp2px(75)
															- (location[1] - 50));
											moveToCircleAnimation
													.setDuration(270);
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
											animationView
													.startAnimation(moveToCircleAnimation);

											int count = tempFriendsList
													.getChildCount();
											int index = 0;
											for (int i = 0; i < count; i++) {
												View v = tempFriendsList
														.getChildAt(i);
												if (v.equals(view)) {
													index = i;
													break;
												}
											}
											tempFriendsList.removeView(view);
											for (int i = index; i < count - 1; i++) {
												View v = tempFriendsList
														.getChildAt(i);
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
										public void onAnimationEnd(
												Animation animation) {
											animationLayout
													.removeView(animationView);
											friendView
													.setVisibility(View.VISIBLE);
										}
									});
							animationView
									.startAnimation(moveToTempListAnimation);

						}
					}
				});

				convertView.setOnLongClickListener(new OnLongClickListener() {

					@Override
					public boolean onLongClick(View v) {
						switchToEditMode(circleView);
						return true;
					}
				});

				container.addView(convertView);

			}

			circleHolder.friendHolders.add(i, friendHolder);

		}

		resolveFriendsPositions(circleHolder);
		setFriendsPositions(circleHolder);
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
					iv.setLayoutParams(params);
					ll_pagepoint.addView(iv);
				}
			}
		});
		scrollContainer
				.setOnInterceptTouchDownListener(new onInterceptTouchDownListener() {

					@Override
					public void onInterceptTouchDown(MotionEvent ev) {
						if (mode.equals("edit")) {
							mScrollContainer
									.requestDisallowInterceptToScroll(true);
						}
					}
				});
		scrollContainer.setOnLongClickListener(new OnLongClickListener() {

			@Override
			public boolean onLongClick(View v) {
				switchToEditMode(circleView);
				return true;
			}
		});

		circleView.setOnLongClickListener(new OnLongClickListener() {

			@Override
			public boolean onLongClick(View v) {
				switchToEditMode(v);
				return true;
			}
		});

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

		lastFriendToRightAnimation = new TranslateAnimation(dp2px(-120), 0,
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
		allTempFriendMoveToLeft.setStartOffset(150);
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
