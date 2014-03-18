package com.lejoying.wxgs.activity.mode.fragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.animation.TranslateAnimation;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout.LayoutParams;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lejoying.wxgs.R;
import com.lejoying.wxgs.activity.mode.MainModeManager;
import com.lejoying.wxgs.activity.utils.CommonNetConnection;
import com.lejoying.wxgs.activity.utils.DataUtil;
import com.lejoying.wxgs.activity.utils.DataUtil.GetDataListener;
import com.lejoying.wxgs.activity.view.CommonViewPager;
import com.lejoying.wxgs.activity.view.ScrollContent;
import com.lejoying.wxgs.activity.view.ScrollContentAdapter;
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

	private final int TYPE_MAX_COUNT = 4;
	private final int TYPE_MESSAGE = 1;
	private final int TYPE_CIRCLE = 2;
	private final int TYPE_BUTTON = 3;

	View mContentView;
	ScrollContent mCirclesContent;

	List<Circle> circles;
	Map<String, Friend> friends;
	List<Friend> newFriends;
	List<String> lastChatFriends;
	int showMessageCount;
	int buttonCount;
	boolean showNewFriends;
	int messageFirstPosition;
	int circleFirstPosition;
	int newFriendsCount;

	RelativeLayout animationContenter;
	View editControl;
	View copy;
	String copyStatus = "move";// "move"||"copy"

	LayoutInflater mInflater;

	public CirclesAdapter mAdapter;

	public String mode = "normal";// "normal"||"edit"

	public void setMode(MainModeManager mainMode) {
		mMainModeManager = mainMode;
	}

	@Override
	public void onResume() {
		CircleMenu.show();
		CircleMenu.setPageName(getString(R.string.circlemenu_page_circles));
		super.onResume();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		mContentView = inflater.inflate(R.layout.fragment_circles, null);
		mCirclesContent = (ScrollContent) mContentView.findViewById(R.id.circlesContent);
		animationContenter = (RelativeLayout) mContentView.findViewById(R.id.animationContenter);
		editControl = mContentView.findViewById(R.id.editControl);
		copy = mContentView.findViewById(R.id.copy);
		initEvent();
		mInflater = inflater;

		// initData(true);
		// mAdapter = new CirclesAdapter(mCirclesContent);
		// mCirclesContent.setAdapter(mAdapter);

		notifyViews();

		return mContentView;
	}

	void initEvent() {
		copy.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				ImageView image = (ImageView) v.findViewById(R.id.img_copy);
				if (copyStatus.equals("move")) {
					copyStatus = "copy";
					image.setImageResource(R.drawable.choise_down);
				} else {
					copyStatus = "move";
					image.setImageResource(R.drawable.choise_up);
				}
			}
		});
	}

	void initData(boolean initShowMessages) {
		circles = app.data.circles;
		friends = app.data.friends;
		newFriends = app.data.newFriends;
		lastChatFriends = app.data.lastChatFriends;
		newFriendsCount = 0;
		for (Friend friend : newFriends) {
			if (friends.get(friend.phone) == null) {
				newFriendsCount++;
			}
		}
		if (newFriendsCount != 0) {
			showNewFriends = true;
		} else {
			showNewFriends = false;
		}
		if (initShowMessages || showMessageCount < 5) {
			showMessageCount = lastChatFriends.size() > 5 ? 5 : lastChatFriends.size();

		}
		buttonCount = showNewFriends ? 4 : 3;
		messageFirstPosition = showNewFriends ? 1 : 0;
		buttonCount = showMessageCount == 0 ? buttonCount - 1 : buttonCount;
		circleFirstPosition = messageFirstPosition + showMessageCount + 1;
		if (showMessageCount == 0) {
			circleFirstPosition = circleFirstPosition - 1;
		}
	}

	public void switchToEditMode(View view) {
		// Alert.showMessage("分组管理");

		if (mode.equals("normal")) {
			mode = "edit";
		}

		int[] location = new int[2];
		view.getLocationOnScreen(location);
		int y = location[1];

		int count = mCirclesContent.getChildCount();

		for (int i = 0; i < count; i++) {
			View v = mCirclesContent.getChildAt(i);
			v.setVisibility(View.GONE);
		}

		mCirclesContent.removeView(view);

		animationContenter.addView(view);
		RelativeLayout.LayoutParams params1 = (RelativeLayout.LayoutParams) view.getLayoutParams();
		params1.width = LayoutParams.MATCH_PARENT;
		view.setLayoutParams(params1);
		view.setVisibility(View.VISIBLE);
		view.findViewById(R.id.bottomBar).setVisibility(View.VISIBLE);
		TextView groupManager = (TextView) view.findViewById(R.id.panel_right_button);
		groupManager.setVisibility(View.VISIBLE);
		groupManager.setText("分组管理");

		TranslateAnimation animation = new TranslateAnimation(0, 0, y - 70, 0);
		animation.setDuration(200);

		editControl.setVisibility(View.VISIBLE);
		float deltaY = getActivity().getResources().getDisplayMetrics().density * 160 + 0.5f;
		TranslateAnimation editAnimation = new TranslateAnimation(0, 0, deltaY, 0);
		editAnimation.setDuration(400);
		editControl.startAnimation(editAnimation);

		view.startAnimation(animation);

	}

	public class CirclesAdapter extends ScrollContentAdapter {

		public CirclesAdapter(ScrollContent scrollContent) {
			super(scrollContent);
			// TODO Auto-generated constructor stub
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return showMessageCount + circles.size() + buttonCount;
		}

		@Override
		public int getViewTypeCount() {
			return TYPE_MAX_COUNT;
		}

		@Override
		public int getItemViewType(int position) {
			int type = 0;
			if (position >= messageFirstPosition && position < messageFirstPosition + showMessageCount) {
				type = TYPE_MESSAGE;
			} else if (position >= circleFirstPosition && position < circleFirstPosition + circles.size()) {
				type = TYPE_CIRCLE;
			} else {
				type = TYPE_BUTTON;
			}
			return type;
		}

		@Override
		public Object getItem(int arg0) {
			return arg0;
		}

		@Override
		public long getItemId(int arg0) {
			return arg0;
		}

		@Override
		public View getView(final int arg0, View fragmentView, ViewGroup arg2) {
			int type = getItemViewType(arg0);
			MessageHolder messageHolder = null;
			FriendHolder friendHolder = null;
			ButtonHolder bHolder = null;
			if (fragmentView == null) {
				switch (type) {
				case TYPE_MESSAGE:
					fragmentView = mInflater.inflate(R.layout.fragment_circles_messages_item, null);
					messageHolder = new MessageHolder();
					messageHolder.iv_head = (ImageView) fragmentView.findViewById(R.id.iv_head);
					messageHolder.tv_nickname = (TextView) fragmentView.findViewById(R.id.tv_nickname);
					messageHolder.tv_lastchat = (TextView) fragmentView.findViewById(R.id.tv_lastchat);
					messageHolder.tv_notread = (TextView) fragmentView.findViewById(R.id.tv_notread);
					fragmentView.setTag(messageHolder);
					break;
				case TYPE_CIRCLE:
					fragmentView = mInflater.inflate(R.layout.fragment_panel, null);
					friendHolder = new FriendHolder();
					friendHolder.tv_groupname = (TextView) fragmentView.findViewById(R.id.panel_name);
					friendHolder.viewPagerContent = (CommonViewPager) fragmentView.findViewById(R.id.commonViewPager);
					fragmentView.setTag(friendHolder);
					break;
				case TYPE_BUTTON:
					fragmentView = mInflater.inflate(R.layout.fragment_item_buttom, null);
					bHolder = new ButtonHolder();
					bHolder.button = (Button) fragmentView.findViewById(R.id.button);
					fragmentView.setTag(bHolder);
					break;
				default:
					break;
				}
			} else {
				switch (type) {
				case TYPE_MESSAGE:
					messageHolder = (MessageHolder) fragmentView.getTag();
					break;
				case TYPE_CIRCLE:
					friendHolder = (FriendHolder) fragmentView.getTag();
					break;
				case TYPE_BUTTON:
					bHolder = (ButtonHolder) fragmentView.getTag();
					break;
				default:
					break;
				}
			}
			fragmentView.setOnClickListener(null);
			switch (type) {
			case TYPE_MESSAGE:
				messageHolder.tv_nickname.setText(friends.get(lastChatFriends.get(arg0 - messageFirstPosition)).nickName);

				final Friend friend = friends.get(lastChatFriends.get(arg0 - messageFirstPosition));
				Message lastMessage = friend.messages.get(friend.messages.size() - 1);
				if (lastMessage.contentType.equals("text")) {

					messageHolder.tv_lastchat.setText(friend.messages.get(friend.messages.size() - 1).content);
				} else if (lastMessage.contentType.equals("image")) {
					messageHolder.tv_lastchat.setText(getString(R.string.text_picture));
				} else if (lastMessage.contentType.equals("voice")) {

				}
				final String headFileName = friend.head;
				final ImageView iv_head = messageHolder.iv_head;
				app.fileHandler.getHeadImage(headFileName, new FileResult() {
					@Override
					public void onResult(String where) {
						iv_head.setImageBitmap(app.fileHandler.bitmaps.get(headFileName));
					}
				});
				Integer notread = friends.get(lastChatFriends.get(arg0 - messageFirstPosition)).notReadMessagesCount;
				if (notread != null) {
					if (notread > 0) {
						messageHolder.tv_notread.setVisibility(View.VISIBLE);
						messageHolder.tv_notread.setText(notread.toString());
					} else {
						messageHolder.tv_notread.setText("");
						messageHolder.tv_notread.setVisibility(View.GONE);
					}
				}
				fragmentView.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View view) {
						mMainModeManager.mChatFragment.mStatus = ChatFragment.CHAT_FRIEND;
						mMainModeManager.mChatFragment.mNowChatFriend = friend;
						mMainModeManager.showNext(mMainModeManager.mChatFragment);
					}
				});
				break;
			case TYPE_CIRCLE:
				Circle circle = circles.get(circles.size() - (arg0 - circleFirstPosition) - 1);
				friendHolder.tv_groupname.setText(circle.name);
				fragmentView.setOnLongClickListener(new OnLongClickListener() {
					@Override
					public boolean onLongClick(View view) {
						switchToEditMode(view);
						return true;
					}
				});
				friendHolder.setCircle(circle, arg0);
				break;
			case TYPE_BUTTON:
				if (showNewFriends && arg0 == 0) {
					bHolder.button.setText(getActivity().getString(R.string.button_newfriend) + "(" + newFriendsCount + ")");
					bHolder.button.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							mMainModeManager.showNext(mMainModeManager.mNewFriendsFragment);
						}
					});
				} else if (arg0 == showMessageCount + messageFirstPosition) {
					bHolder.button.setText(getActivity().getString(R.string.button_moreMessage));
					bHolder.button.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							showMessageCount = lastChatFriends.size() > showMessageCount + 5 ? showMessageCount + 5 : lastChatFriends.size();
							initData(false);
							mAdapter.notifyDataSetChanged();
						}
					});
				} else if (arg0 == getCount() - 2) {
					bHolder.button.setText(getActivity().getString(R.string.button_newcircle));
					bHolder.button.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							final EditText circleName;
							new AlertDialog.Builder(getActivity()).setTitle("请输入分组名").setIcon(android.R.drawable.ic_dialog_info).setView(circleName = new EditText(getActivity())).setPositiveButton("确定", new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int which) {
									app.networkHandler.connection(new CommonNetConnection() {

										@Override
										protected void settings(Settings settings) {
											settings.url = API.DOMAIN + API.CIRCLE_ADDCIRCLE;
											Map<String, String> params = new HashMap<String, String>();
											params.put("phone", app.data.user.phone);
											params.put("accessKey", app.data.user.accessKey);
											params.put("name", circleName.getText().toString());
											settings.params = params;
										}

										@Override
										public void success(JSONObject jData) {
											DataUtil.getCircles(new GetDataListener() {

												@Override
												public void getSuccess() {
													mAdapter.notifyDataSetChanged();
												}
											});

										}
									});
								}
							}).setNegativeButton("取消", null).show();
							// MCNetTools.ajax(new AjaxAdapter() {
							//
							// @Override
							// public void setParams(Settings settings) {
							// settings.url = API.CIRCLE_ADDCIRCLE;
							// Bundle params = generateParams();
							// params.putString("circleName", "");
							// settings.params = params;
							// }
							//
							// @Override
							// public void onSuccess(JSONObject jData) {
							// // TODO Auto-generated method stub
							//
							// }
							// });
						}
					});
				} else if (arg0 == getCount() - 1) {
					bHolder.button.setText(getActivity().getString(R.string.button_findmorefriend));
					bHolder.button.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							mMainModeManager.showNext(mMainModeManager.mSearchFriendFragment);
						}
					});
				}
				break;
			default:
				break;
			}

			return fragmentView;
		}

		@Override
		public void notifyDataSetChanged() {
			initData(false);
			super.notifyDataSetChanged();
		}
	}

	Map<String, View> views = new HashMap<String, View>();
	List<String> normalShow = new ArrayList<String>();

	public void notifyViews() {
		generateViews();
		mCirclesContent.removeAllViews();
		for (int i = 0; i < normalShow.size(); i++) {
			View v = views.get(normalShow.get(i));
			LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			layoutParams.setMargins(layoutParams.leftMargin, 25, layoutParams.rightMargin, layoutParams.bottomMargin);
			v.setLayoutParams(layoutParams);
			mCirclesContent.addView(v);
		}
	}

	void generateViews() {
		// generate message views;
		int lastChatFriendsSize = app.data.lastChatFriends.size();
		lastChatFriendsSize = lastChatFriendsSize < 5 ? lastChatFriendsSize : 5;

		normalShow.clear();

		if (views.get("button#newfriend") == null) {
			View newFriendButtonView = generateNewFriendButtonView();
			views.put("button#newfriend", newFriendButtonView);
		}
		normalShow.add("button#newfriend");

		for (int i = 0; i < lastChatFriendsSize; i++) {
			String phone = app.data.lastChatFriends.get(i);
			if (views.get("message#" + phone) == null) {
				View v = generateMessageView(phone);
				views.put("message#" + phone, v);
			}
			normalShow.add("message#" + phone);
		}

		if (views.get("button#moremessage") == null) {
			View moreMessageButtonView = generateMoreMessageButtonView();
			views.put("button#moremessage", moreMessageButtonView);
		}
		normalShow.add("button#moremessage");

		// generate circles
		for (int i = 0; i < app.data.circles.size(); i++) {
			Circle circle = app.data.circles.get(i);
			if (views.get("group#" + circle.name) == null) {
				View v = generateCircleView(circle);
				views.put("group#" + circle.name, v);
			}
			normalShow.add("group#" + circle.name);
		}

		if (views.get("button#creategroup") == null) {
			View createGroupButtonView = generateCreateGroupButtonView();
			views.put("button#creategroup", createGroupButtonView);
		}
		normalShow.add("button#creategroup");

		if (views.get("button#findmore") == null) {
			View findMoreFriendButtonView = generateFindMoreFriendButtonView();
			views.put("button#findmore", findMoreFriendButtonView);
		}
		normalShow.add("button#findmore");

	}

	View generateNewFriendButtonView() {
		View newFriendButtonView = mInflater.inflate(R.layout.fragment_item_buttom, null);
		Button newFriendButton = (Button) newFriendButtonView.findViewById(R.id.button);
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
		View moreMessageButtonView = mInflater.inflate(R.layout.fragment_item_buttom, null);
		Button moreMessageButton = (Button) moreMessageButtonView.findViewById(R.id.button);
		moreMessageButton.setText("点击查看更多");
		moreMessageButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO show more messages
				// showMessageCount = lastChatFriends.size() > showMessageCount
				// + 5 ? showMessageCount + 5 : lastChatFriends.size();
				// initData(false);
				// mAdapter.notifyDataSetChanged();
			}
		});

		return moreMessageButtonView;

	}

	View generateFindMoreFriendButtonView() {
		View findMoreFriendButtonView = mInflater.inflate(R.layout.fragment_item_buttom, null);
		Button findMoreFriendButton = (Button) findMoreFriendButtonView.findViewById(R.id.button);
		findMoreFriendButton.setText("找到更多密友");
		findMoreFriendButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mMainModeManager.showNext(mMainModeManager.mSearchFriendFragment);
			}
		});

		return findMoreFriendButtonView;
	}

	View generateCreateGroupButtonView() {

		View createGroupButtonView = mInflater.inflate(R.layout.fragment_item_buttom, null);
		Button createGroupButton = (Button) createGroupButtonView.findViewById(R.id.button);
		createGroupButton.setText("新建分组");
		createGroupButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				CircleMenu.showBack();
				final EditText circleName;
				new AlertDialog.Builder(getActivity()).setTitle("请输入分组名").setIcon(android.R.drawable.ic_dialog_info).setView(circleName = new EditText(getActivity())).setPositiveButton("确定", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						app.networkHandler.connection(new CommonNetConnection() {

							@Override
							protected void settings(Settings settings) {
								settings.url = API.DOMAIN + API.CIRCLE_ADDCIRCLE;
								Map<String, String> params = new HashMap<String, String>();
								params.put("phone", app.data.user.phone);
								params.put("accessKey", app.data.user.accessKey);
								params.put("name", circleName.getText().toString());
								settings.params = params;
							}

							@Override
							public void success(JSONObject jData) {
								DataUtil.getCircles(new GetDataListener() {
									@Override
									public void getSuccess() {
										mAdapter.notifyDataSetChanged();
									}
								});

							}
						});
					}
				}).setNegativeButton("取消", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
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

		View messageView = mInflater.inflate(R.layout.fragment_circles_messages_item, null);
		final ImageView head = (ImageView) messageView.findViewById(R.id.iv_head);
		TextView nickName = (TextView) messageView.findViewById(R.id.tv_nickname);
		TextView lastChatMessage = (TextView) messageView.findViewById(R.id.tv_lastchat);
		TextView notReadCount = (TextView) messageView.findViewById(R.id.tv_notread);

		final Friend friend = friends.get(lastChatFriendPhone);

		nickName.setText(friend.nickName);
		Message lastMessage = friend.messages.get(friend.messages.size() - 1);
		if (lastMessage.contentType.equals("text")) {
			lastChatMessage.setText(friend.messages.get(friend.messages.size() - 1).content);
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

	class ItemHolder {
		ImageView iv_head;
		TextView tv_nickname;
	}

	View generateCircleView(Circle circle) {
		View circleView = mInflater.inflate(R.layout.fragment_panel, null);
		TextView groupName = (TextView) circleView.findViewById(R.id.panel_name);
		groupName.setText(circle.name);
		final CommonViewPager commonViewPager = (CommonViewPager) circleView.findViewById(R.id.commonViewPager);

		final GestureDetector detector = new GestureDetector(getActivity(), new GestureDetector.OnGestureListener() {

			@Override
			public boolean onSingleTapUp(MotionEvent e) {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public void onShowPress(MotionEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public void onLongPress(MotionEvent e) {
				switchToEditMode((View) commonViewPager.getParent().getParent());
			}

			@Override
			public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public boolean onDown(MotionEvent e) {
				// TODO Auto-generated method stub
				return true;
			}
		});

		final List<String> phones = circle.phones;
		final Map<String, Friend> friends = app.data.friends;
		final int pagecount = phones.size() % 6 == 0 ? phones.size() / 6 : phones.size() / 6 + 1;
		final List<View> pageviews = new ArrayList<View>();
		for (int i = 0; i < pagecount; i++) {
			final int a = i;
			BaseAdapter gridpageAdapter = new BaseAdapter() {
				@Override
				public View getView(final int position, View convertView, final ViewGroup parent) {
					ItemHolder itemHolder = null;
					if (convertView == null) {
						convertView = mInflater.inflate(R.layout.fragment_circles_gridpage_item, null);
						itemHolder = new ItemHolder();
						itemHolder.iv_head = (ImageView) convertView.findViewById(R.id.iv_head);
						itemHolder.tv_nickname = (TextView) convertView.findViewById(R.id.tv_nickname);
						convertView.setTag(itemHolder);
					} else {
						itemHolder = (ItemHolder) convertView.getTag();
					}
					if (phones.get(a * 6 + position) != null && friends.get(phones.get(a * 6 + position)) != null) {

						final String headFileName = friends.get(phones.get(a * 6 + position)).head;
						final ImageView iv_head = itemHolder.iv_head;
						app.fileHandler.getHeadImage(headFileName, new FileResult() {
							@Override
							public void onResult(String where) {
								iv_head.setImageBitmap(app.fileHandler.bitmaps.get(headFileName));
							}
						});
						itemHolder.tv_nickname.setText(friends.get(phones.get(a * 6 + position)).nickName);
						convertView.setOnClickListener(new OnClickListener() {

							@Override
							public void onClick(View v) {
								mMainModeManager.mBusinessCardFragment.mStatus = BusinessCardFragment.SHOW_FRIEND;
								mMainModeManager.mBusinessCardFragment.mShowFriend = friends.get(phones.get(a * 6 + position));
								mMainModeManager.showNext(mMainModeManager.mBusinessCardFragment);
							}
						});

						convertView.setOnLongClickListener(new OnLongClickListener() {

							@Override
							public boolean onLongClick(View v) {
								switchToEditMode((View) commonViewPager.getParent().getParent());
								return true;
							}
						});
					}
					return convertView;
				}

				@Override
				public long getItemId(int position) {
					return position;
				}

				@Override
				public Object getItem(int position) {
					return friends.get(phones.get(a * 6 + position));
				}

				@Override
				public int getCount() {
					int nowcount = 0;
					if (a < pagecount - 1) {
						nowcount = 6;
					} else {
						nowcount = phones.size() - a * 6;
					}
					return nowcount;
				}

				@Override
				public void unregisterDataSetObserver(DataSetObserver observer) {
					if (observer != null) {
						super.unregisterDataSetObserver(observer);
					}
				}

			};
			GridView gridpage = (GridView) mInflater.inflate(R.layout.fragment_circles_gridpage, null);
			gridpage.setAdapter(gridpageAdapter);
			pageviews.add(gridpage);

			gridpage.setOnTouchListener(new OnTouchListener() {
				@Override
				public boolean onTouch(View v, MotionEvent event) {
					return detector.onTouchEvent(event);
				}
			});

		}

		PagerAdapter contentAdapter = new PagerAdapter() {
			@Override
			public boolean isViewFromObject(View arg0, Object arg1) {
				return arg0 == arg1;
			}

			@Override
			public int getCount() {
				return pageviews.size();
			}

			@Override
			public void destroyItem(View container, int position, Object object) {
				((ViewPager) container).removeView(pageviews.get(position));
			}

			@Override
			public Object instantiateItem(View container, int position) {
				((ViewPager) container).addView(pageviews.get(position));
				return pageviews.get(position);
			}

			@Override
			public void unregisterDataSetObserver(DataSetObserver observer) {
				if (observer != null) {
					super.unregisterDataSetObserver(observer);
				}
			}
		};
		commonViewPager.setAdapter(contentAdapter);

		return circleView;
	}

	class MessageHolder {
		ImageView iv_head;
		TextView tv_nickname;
		TextView tv_lastchat;
		TextView tv_notread;
	}

	class FriendHolder {
		TextView tv_groupname;
		CommonViewPager viewPagerContent;
		PagerAdapter vp_contentAdapter;

		Circle circle;

		class ItemHolder {
			ImageView iv_head;
			TextView tv_nickname;
		}

		GestureDetector detector = new GestureDetector(getActivity(), new GestureDetector.OnGestureListener() {

			@Override
			public boolean onSingleTapUp(MotionEvent e) {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public void onShowPress(MotionEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public void onLongPress(MotionEvent e) {
				switchToEditMode((View) viewPagerContent.getParent().getParent());
			}

			@Override
			public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public boolean onDown(MotionEvent e) {
				// TODO Auto-generated method stub
				return true;
			}
		});

		public void setCircle(Circle c, final int viewPosition) {

			this.circle = c;
			final List<String> phones = circle.phones;
			final int pagecount = phones.size() % 6 == 0 ? phones.size() / 6 : phones.size() / 6 + 1;
			final List<View> pageviews = new ArrayList<View>();
			for (int i = 0; i < pagecount; i++) {
				final int a = i;
				BaseAdapter gridpageAdapter = new BaseAdapter() {
					@Override
					public View getView(final int position, View convertView, final ViewGroup parent) {
						ItemHolder itemHolder = null;
						if (convertView == null) {
							convertView = mInflater.inflate(R.layout.fragment_circles_gridpage_item, null);
							itemHolder = new ItemHolder();
							itemHolder.iv_head = (ImageView) convertView.findViewById(R.id.iv_head);
							itemHolder.tv_nickname = (TextView) convertView.findViewById(R.id.tv_nickname);
							convertView.setTag(itemHolder);
						} else {
							itemHolder = (ItemHolder) convertView.getTag();
						}
						if (phones.get(a * 6 + position) != null && friends.get(phones.get(a * 6 + position)) != null) {

							final String headFileName = friends.get(phones.get(a * 6 + position)).head;
							final ImageView iv_head = itemHolder.iv_head;
							app.fileHandler.getHeadImage(headFileName, new FileResult() {
								@Override
								public void onResult(String where) {
									iv_head.setImageBitmap(app.fileHandler.bitmaps.get(headFileName));
								}
							});
							itemHolder.tv_nickname.setText(friends.get(phones.get(a * 6 + position)).nickName);
							convertView.setOnClickListener(new OnClickListener() {

								@Override
								public void onClick(View v) {
									mMainModeManager.mBusinessCardFragment.mStatus = BusinessCardFragment.SHOW_FRIEND;
									mMainModeManager.mBusinessCardFragment.mShowFriend = friends.get(phones.get(a * 6 + position));
									mMainModeManager.showNext(mMainModeManager.mBusinessCardFragment);
								}
							});

							convertView.setOnLongClickListener(new OnLongClickListener() {

								@Override
								public boolean onLongClick(View v) {
									switchToEditMode((View) viewPagerContent.getParent().getParent());
									return true;
								}
							});
						}
						return convertView;
					}

					@Override
					public long getItemId(int position) {
						return position;
					}

					@Override
					public Object getItem(int position) {
						return friends.get(phones.get(a * 6 + position));
					}

					@Override
					public int getCount() {
						int nowcount = 0;
						if (a < pagecount - 1) {
							nowcount = 6;
						} else {
							nowcount = phones.size() - a * 6;
						}
						return nowcount;
					}

					@Override
					public void unregisterDataSetObserver(DataSetObserver observer) {
						if (observer != null) {
							super.unregisterDataSetObserver(observer);
						}
					}

				};
				GridView gridpage = (GridView) mInflater.inflate(R.layout.fragment_circles_gridpage, null);
				gridpage.setAdapter(gridpageAdapter);
				pageviews.add(gridpage);

				gridpage.setOnTouchListener(new OnTouchListener() {
					@Override
					public boolean onTouch(View v, MotionEvent event) {
						return detector.onTouchEvent(event);
					}
				});

			}
			vp_contentAdapter = new PagerAdapter() {
				@Override
				public boolean isViewFromObject(View arg0, Object arg1) {
					return arg0 == arg1;
				}

				@Override
				public int getCount() {
					return pageviews.size();
				}

				@Override
				public void destroyItem(View container, int position, Object object) {
					((ViewPager) container).removeView(pageviews.get(position));
				}

				@Override
				public Object instantiateItem(View container, int position) {
					((ViewPager) container).addView(pageviews.get(position));
					return pageviews.get(position);
				}

				@Override
				public void unregisterDataSetObserver(DataSetObserver observer) {
					if (observer != null) {
						super.unregisterDataSetObserver(observer);
					}
				}
			};
			viewPagerContent.setAdapter(vp_contentAdapter);
		}
	}

	class ButtonHolder {
		Button button;
	}

}
