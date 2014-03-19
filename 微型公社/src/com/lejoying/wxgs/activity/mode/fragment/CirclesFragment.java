package com.lejoying.wxgs.activity.mode.fragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lejoying.wxgs.R;
import com.lejoying.wxgs.activity.mode.BaseModeManager.KeyDownListener;
import com.lejoying.wxgs.activity.mode.MainModeManager;
import com.lejoying.wxgs.activity.utils.CommonNetConnection;
import com.lejoying.wxgs.activity.utils.DataUtil;
import com.lejoying.wxgs.activity.utils.DataUtil.GetDataListener;
import com.lejoying.wxgs.activity.view.ScrollRelativeLayout;
import com.lejoying.wxgs.activity.view.manager.FrictionAnimation;
import com.lejoying.wxgs.activity.view.manager.FrictionAnimation.AnimatingView;
import com.lejoying.wxgs.activity.view.widget.CircleMenu;
import com.lejoying.wxgs.app.MainApplication;
import com.lejoying.wxgs.app.data.API;
import com.lejoying.wxgs.app.data.entity.Circle;
import com.lejoying.wxgs.app.data.entity.Friend;
import com.lejoying.wxgs.app.data.entity.Message;
import com.lejoying.wxgs.app.handler.FileHandler.FileResult;
import com.lejoying.wxgs.app.handler.NetworkHandler.Settings;

public class CirclesFragment extends BaseFragment {

	MainApplication app = MainApplication.getMainApplication();
	MainModeManager mMainModeManager;

	View mContentView;
	ScrollRelativeLayout circlesViewContenter;
	View editControl;

	LayoutInflater mInflater;

	public String copyStatus = "move";// "move"||"copy"
	public String mode = "normal";// "normal"||"edit"
	public String touchEvnetStatus = "static";// "static"||"moving_x"||"moving_y"

	public void setMode(MainModeManager mainMode) {
		mMainModeManager = mainMode;
	}

	@Override
	public void onResume() {
		CircleMenu.show();
		CircleMenu.setPageName(getString(R.string.circlemenu_page_circles));
		super.onResume();
	}

	FrictionAnimation decelerationAnimation = new FrictionAnimation();
	AnimatingView animatingView = new AnimatingView();

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mInflater = inflater;
		mContentView = inflater.inflate(R.layout.fragment_circles_scroll, null);
		circlesViewContenter = (ScrollRelativeLayout) mContentView
				.findViewById(R.id.circlesViewContainer);
		editControl = mContentView.findViewById(R.id.editControl);

		animatingView.view = circlesViewContenter;

		density = getActivity().getResources().getDisplayMetrics().density;
		notifyViews();

		initEvent();

		return mContentView;
	}

	public void notifyViews() {
		if (mode.equals("normal")) {
			generateViews();
			circlesViewContenter.removeAllViews();
			int top = 25;
			int scrollToY = 0;
			for (int i = 0; i < normalShow.size(); i++) {
				View v = views.get(normalShow.get(i));
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

				top = top + height + 25;
				v.setLayoutParams(layoutParams);
				circlesViewContenter.addView(v);
			}

			circlesViewContenter.scrollTo(0, scrollToY);
		}
	}

	void initEvent() {

		circlesViewContenter.setOnTouchListener(new OnTouchListener() {
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
					x0 = x;
					y0 = y;
				} else if (event.getAction() == MotionEvent.ACTION_MOVE) {
					if (lastMillis == 0) {
						lastMillis = currentMillis;
						return true;
					}
					dy = y - y0;
					dx = x - x0;
					if (touchEvnetStatus.equals("moving_y")) {
						if (mode.equals("edit")) {
							return false;
						}
						circlesViewContenter.scrollBy(0, -(int) (dy));
						y0 = y;
					} else if (touchEvnetStatus.equals("static")) {
						if (dy * dy + dx * dx > 400) {
							if (dy * dy > dx * dx) {
								touchEvnetStatus = "moving_y";
							} else {
								touchEvnetStatus = "moving_x";
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

					System.out
							.println("vx:    " + vx + "     ----vy:    " + vy);

					FrictionAnimation decelerationAnimation = new FrictionAnimation(
							vx, vy);
					// circlesViewContenter.startAnimation(decelerationAnimation);
					if (touchEvnetStatus.equals("moving_y")
							|| touchEvnetStatus.equals("moving_x")) {
						touchEvnetStatus = "static";
					}
				}

				return true;
			}
		});
	}

	int currentEditPosition = -1;
	int statusBarHeight;

	public void switchToEditMode(View view) {
		// Alert.showMessage("分组管理");

		if (mode.equals("normal")) {
			mode = "edit";
		} else {
			return;
		}
		final int screenWidth = getScreenWidth();
		int marginLeft = (screenWidth - view.getWidth()) / 2;
		int cardWidth = view.getWidth();

		mMainModeManager.setKeyDownListener(new KeyDownListener() {

			@Override
			public boolean onKeyDown(int keyCode, KeyEvent event) {
				if (keyCode == KeyEvent.KEYCODE_BACK) {
					swichToNormalMode();
				}
				return false;
			}
		});

		circlesViewContenter.setGravity(Gravity.TOP | Gravity.LEFT);
		statusBarHeight = circlesViewContenter.getRootView().getTop();
		System.out.println(statusBarHeight);

		CircleMenu.showBack();

		int[] location = new int[2];
		view.getLocationOnScreen(location);
		int y = location[1];

		circlesViewContenter.removeAllViews();
		circlesViewContenter.scrollTo(0, 0);

		for (int i = 0; i < circles.size(); i++) {
			String group = circles.get(i);
			View v = views.get(group);
			RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
					cardWidth, LayoutParams.WRAP_CONTENT);
			layoutParams.setMargins(i * screenWidth + marginLeft,
					(int) dp2px(20), -Integer.MAX_VALUE, 0);
			v.setLayoutParams(layoutParams);
			TextView manager = (TextView) v
					.findViewById(R.id.panel_right_button);
			manager.setText("分组管理");
			manager.setVisibility(View.VISIBLE);
			v.findViewById(R.id.bottomBar).setVisibility(View.VISIBLE);

			View buttonPreviousGroup = v.findViewById(R.id.buttonPreviousGroup);
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
			circlesViewContenter.addView(v);

			if (v.equals(view)) {
				currentEditPosition = i;
			}
		}
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
		if (mode.equals("edit")) {
			mode = "normal";
		} else {
			return;
		}
		CircleMenu.show();
		mMainModeManager.setKeyDownListener(null);

		TranslateAnimation editControlIn = new TranslateAnimation(0, 0, 0,
				dp2px(160));
		editControlIn.setDuration(400);
		editControl.setVisibility(View.GONE);
		editControl.startAnimation(editControlIn);

		final int screenWidth = getScreenWidth();

		circlesViewContenter.removeAllViews();
		circlesViewContenter
				.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.TOP);

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

	public float dp2px(float px) {
		float dp = density * px + 0.5f;
		return dp;
	}

	Map<String, View> views = new HashMap<String, View>();
	List<String> normalShow = new ArrayList<String>();
	List<String> circles = new ArrayList<String>();

	void generateViews() {
		// generate message views;
		int lastChatFriendsSize = app.data.lastChatFriends.size();
		lastChatFriendsSize = lastChatFriendsSize < 5 ? lastChatFriendsSize : 5;

		normalShow.clear();
		circles.clear();

		if (views.get("button#newfriend") == null) {
			View newFriendButtonView = generateNewFriendButtonView();
			newFriendButtonView.setTag(46);
			views.put("button#newfriend", newFriendButtonView);
		}
		normalShow.add("button#newfriend");

		for (int i = 0; i < lastChatFriendsSize; i++) {
			String phone = app.data.lastChatFriends.get(i);
			if (views.get("message#" + phone) == null) {
				View v = generateMessageView(phone);
				v.setTag(74);
				views.put("message#" + phone, v);
			}
			normalShow.add("message#" + phone);
		}

		if (views.get("button#moremessage") == null) {
			View moreMessageButtonView = generateMoreMessageButtonView();
			moreMessageButtonView.setTag(46);
			views.put("button#moremessage", moreMessageButtonView);
		}
		normalShow.add("button#moremessage");

		// generate circles
		for (int i = 0; i < app.data.circles.size(); i++) {
			Circle circle = app.data.circles.get(i);
			if (views.get("group#" + circle.rid) == null) {
				View v = generateCircleView(circle);
				views.put("group#" + circle.rid, v);
				v.setTag(272);
			}
			normalShow.add("group#" + circle.rid);
			circles.add("group#" + circle.rid);
		}

		if (views.get("button#creategroup") == null) {
			View createGroupButtonView = generateCreateGroupButtonView();
			createGroupButtonView.setTag(46);
			views.put("button#creategroup", createGroupButtonView);
		}
		normalShow.add("button#creategroup");

		if (views.get("button#findmore") == null) {
			View findMoreFriendButtonView = generateFindMoreFriendButtonView();
			findMoreFriendButtonView.setTag(46);
			views.put("button#findmore", findMoreFriendButtonView);
		}
		normalShow.add("button#findmore");

	}

	View generateNewFriendButtonView() {
		View newFriendButtonView = mInflater.inflate(
				R.layout.fragment_item_buttom, null);
		Button newFriendButton = (Button) newFriendButtonView
				.findViewById(R.id.button);
		newFriendButton.setText("新的好友");
		newFriendButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mMainModeManager.showNext(mMainModeManager.mNewFriendsFragment);
			}
		});

		return newFriendButtonView;

	}

	View generateMoreMessageButtonView() {
		View moreMessageButtonView = mInflater.inflate(
				R.layout.fragment_item_buttom, null);
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
				R.layout.fragment_item_buttom, null);
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

	View generateCreateGroupButtonView() {

		View createGroupButtonView = mInflater.inflate(
				R.layout.fragment_item_buttom, null);
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
		final ImageView head = (ImageView) messageView
				.findViewById(R.id.iv_head);
		TextView nickName = (TextView) messageView
				.findViewById(R.id.tv_nickname);
		TextView lastChatMessage = (TextView) messageView
				.findViewById(R.id.tv_lastchat);
		TextView notReadCount = (TextView) messageView
				.findViewById(R.id.tv_notread);

		final Friend friend = app.data.friends.get(lastChatFriendPhone);

		nickName.setText(friend.nickName);
		Message lastMessage = friend.messages.get(friend.messages.size() - 1);
		if (lastMessage.contentType.equals("text")) {
			lastChatMessage
					.setText(friend.messages.get(friend.messages.size() - 1).content);
		} else if (lastMessage.contentType.equals("image")) {
			lastChatMessage.setText(getString(R.string.text_picture));
		} else if (lastMessage.contentType.equals("voice")) {

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
				mMainModeManager.mChatFragment.mStatus = ChatFragment.CHAT_FRIEND;
				mMainModeManager.mChatFragment.mNowChatFriend = friend;
				mMainModeManager.showNext(mMainModeManager.mChatFragment);
			}
		});

		return messageView;
	}

	View generateCircleView(Circle circle) {
		final View circleView = mInflater
				.inflate(R.layout.fragment_panel, null);

		TextView groupName = (TextView) circleView
				.findViewById(R.id.panel_name);
		groupName.setText(circle.name);
		final RelativeLayout friendContainer = (RelativeLayout) circleView
				.findViewById(R.id.friendContainer);

		List<String> phones = circle.phones;
		Map<String, Friend> friends = app.data.friends;
		int pagecount = phones.size() % 6 == 0 ? phones.size() / 6 : phones
				.size() / 6 + 1;

		for (int i = 0; i < phones.size(); i++) {
			View convertView = mInflater.inflate(
					R.layout.fragment_circles_gridpage_item, null);
			final ImageView head = (ImageView) convertView
					.findViewById(R.id.iv_head);
			TextView nickname = (TextView) convertView
					.findViewById(R.id.tv_nickname);
			Friend friend = friends.get(phones.get(i));
			nickname.setText(friend.nickName);
			final String headFileName = friend.head;
			app.fileHandler.getHeadImage(headFileName, new FileResult() {
				@Override
				public void onResult(String where) {
					head.setImageBitmap(app.fileHandler.bitmaps
							.get(headFileName));
				}
			});
			RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
					(int) dp2px(55f),
					android.widget.RelativeLayout.LayoutParams.WRAP_CONTENT);
			params.rightMargin = -Integer.MAX_VALUE;
			if ((i + 1) % 6 == 1) {
				params.topMargin = (int) dp2px(11);
				params.leftMargin = (int) dp2px(26 + i / 6 * 326);
			} else if ((i + 1) % 6 == 2) {
				params.topMargin = (int) dp2px(11);
				params.leftMargin = (int) dp2px(26 + 55 + 48 + i / 6 * 326);
			} else if ((i + 1) % 6 == 3) {
				params.topMargin = (int) dp2px(11);
				params.leftMargin = (int) dp2px(26 + 55 + 48 + 55 + 48 + i / 6
						* 326);
			} else if ((i + 1) % 6 == 4) {
				params.topMargin = (int) dp2px(11 + 73 + 27);
				params.leftMargin = (int) dp2px(26 + i / 6 * 326);
			} else if ((i + 1) % 6 == 5) {
				params.topMargin = (int) dp2px(11 + 73 + 27);
				params.leftMargin = (int) dp2px(26 + 55 + 48 + i / 6 * 326);
			} else if ((i + 1) % 6 == 0) {
				params.topMargin = (int) dp2px(11 + 73 + 27);
				params.leftMargin = (int) dp2px(26 + 55 + 48 + 55 + 48 + i / 6
						* 326);
			}

			// convertView.setOnClickListener(new OnClickListener() {
			//
			// @Override
			// public void onClick(View v) {
			// // TODO Auto-generated method stub
			//
			// }
			// });

			// convertView.setOnLongClickListener(new OnLongClickListener() {
			//
			// @Override
			// public boolean onLongClick(View v) {
			// if (touchEvnetStatus.equals("moving_x")
			// || touchEvnetStatus.equals("moving_y")) {
			// return false;
			// }
			// switchToEditMode(circleView);
			// return true;
			// }
			// });

			convertView.setLayoutParams(params);
			friendContainer.addView(convertView);
		}

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
						if (touchEvnetStatus.equals("moving_x")
								|| touchEvnetStatus.equals("moving_y")) {
							return;
						}
						switchToEditMode(circleView);
					}

					@Override
					public boolean onScroll(MotionEvent e1, MotionEvent e2,
							float distanceX, float distanceY) {
						dx = e2.getRawX() - x0;
					if (touchEvnetStatus.equals("moving_x")) {
							friendContainer.scrollBy(-(int) (dx), 0);
							x0 = e2.getRawX();
					}
				return true;
			}
		});

		friendContainer.setOnTouchListener(new OnTouchListener() {

			// float x0 = 0;
			// float dx = 0;

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// float x = event.getRawX();
				// float y = event.getRawY();
		//
				// long currentMillis = System.currentTimeMillis();
		//
				// if (event.getAction() == MotionEvent.ACTION_DOWN) {
				// x0 = x;
				// } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
				// dx = x - x0;
		//
				// if (touchEvnetStatus.equals("moving_x")) {
				// v.scrollBy(-(int) (dx), 0);
				// x0 = x;
		// }
				// } else if (event.getAction() == MotionEvent.ACTION_UP) {
		//
		// }

				return detector.onTouchEvent(event);
			}
		});

		circleView.setOnLongClickListener(new OnLongClickListener() {

			@Override
			public boolean onLongClick(View v) {
				if (touchEvnetStatus.equals("moving_x")
						|| touchEvnetStatus.equals("moving_y")) {
					return false;
				}
				switchToEditMode(v);
				return true;
			}
		});

		return circleView;
	}

}
