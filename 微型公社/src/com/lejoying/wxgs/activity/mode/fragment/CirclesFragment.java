package com.lejoying.wxgs.activity.mode.fragment;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.database.DataSetObserver;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.lejoying.wxgs.R;
import com.lejoying.wxgs.activity.mode.MainModeManager;
import com.lejoying.wxgs.activity.utils.DataUtil;
import com.lejoying.wxgs.activity.utils.DataUtil.GetDataListener;
import com.lejoying.wxgs.activity.view.CommonViewPager;
import com.lejoying.wxgs.activity.view.ScrollContent;
import com.lejoying.wxgs.activity.view.ScrollContentAdapter;
import com.lejoying.wxgs.activity.view.widget.CircleMenu;
import com.lejoying.wxgs.app.MainApplication;
import com.lejoying.wxgs.app.data.Data;
import com.lejoying.wxgs.app.data.entity.Circle;
import com.lejoying.wxgs.app.data.entity.Friend;
import com.lejoying.wxgs.app.data.entity.Message;
import com.lejoying.wxgs.app.handler.DataHandler.Modification;
import com.lejoying.wxgs.app.handler.FileHandler.FileResult;
import com.lejoying.wxgs.app.parser.StreamParser;

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

	LayoutInflater mInflater;

	public CirclesAdapter mAdapter;

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
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mContentView = inflater.inflate(R.layout.fragment_circles, null);
		mCirclesContent = (ScrollContent) mContentView
				.findViewById(R.id.circlesContent);
		mInflater = inflater;
		initData(true);
		mAdapter = new CirclesAdapter(mCirclesContent);
		mCirclesContent.setAdapter(mAdapter);

		if (app.data.isClear) {
			app.dataHandler.exclude(new Modification() {
				@Override
				public void modifyData(Data data) {
					try {
						Data localData = (Data) StreamParser
								.parseToObject(getActivity().openFileInput(
										data.user.phone));
						if (localData != null) {
							data.user.head = localData.user.head;
							data.user.nickName = localData.user.nickName;
							data.user.mainBusiness = localData.user.mainBusiness;
							data.circles = localData.circles;
							data.friends = localData.friends;
							data.groups = localData.groups;
							data.groupFriends = localData.groupFriends;
							data.lastChatFriends = localData.lastChatFriends;
							data.newFriends = localData.newFriends;
						}
					} catch (FileNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

				@Override
				public void modifyUI() {
					mAdapter.notifyDataSetChanged();
					super.modifyUI();
				}
			});
		}

		DataUtil.getUser(new GetDataListener() {

			@Override
			public void getSuccess() {
				DataUtil.getCircles(new GetDataListener() {

					@Override
					public void getSuccess() {
						DataUtil.getMessages(new GetDataListener() {

							@Override
							public void getSuccess() {
								DataUtil.getAskFriends(new GetDataListener() {

									@Override
									public void getSuccess() {
										mAdapter.notifyDataSetChanged();
									}

									@Override
									public void getFailed() {
										// TODO Auto-generated method stub

									}
								});
							}

							@Override
							public void getFailed() {
								// TODO Auto-generated method stub

							}
						});
					}

					@Override
					public void getFailed() {
						// TODO Auto-generated method stub

					}
				});
			}

			@Override
			public void getFailed() {
				// TODO Auto-generated method stub

			}
		});

		return mContentView;
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
		if (initShowMessages) {
			showMessageCount = lastChatFriends.size() > 5 ? 5 : lastChatFriends
					.size();
		}
		buttonCount = showNewFriends ? 4 : 3;
		messageFirstPosition = showNewFriends ? 1 : 0;
		buttonCount = showMessageCount == 0 ? buttonCount - 1 : buttonCount;
		circleFirstPosition = messageFirstPosition + showMessageCount + 1;
		if (showMessageCount == 0) {
			circleFirstPosition = circleFirstPosition - 1;
		}
	}

	class CirclesAdapter extends ScrollContentAdapter {

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
			if (position >= messageFirstPosition
					&& position < messageFirstPosition + showMessageCount) {
				type = TYPE_MESSAGE;
			} else if (position >= circleFirstPosition
					&& position < circleFirstPosition + circles.size()) {
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
		public View getView(final int arg0, View arg1, ViewGroup arg2) {
			int type = getItemViewType(arg0);
			MessageHolder messageHolder = null;
			FriendHolder friendHolder = null;
			ButtonHolder bHolder = null;
			if (arg1 == null) {
				switch (type) {
				case TYPE_MESSAGE:
					arg1 = mInflater.inflate(
							R.layout.fragment_circles_messages_item, null);
					messageHolder = new MessageHolder();
					messageHolder.iv_head = (ImageView) arg1
							.findViewById(R.id.iv_head);
					messageHolder.tv_nickname = (TextView) arg1
							.findViewById(R.id.tv_nickname);
					messageHolder.tv_lastchat = (TextView) arg1
							.findViewById(R.id.tv_lastchat);
					messageHolder.tv_notread = (TextView) arg1
							.findViewById(R.id.tv_notread);
					arg1.setTag(messageHolder);
					break;
				case TYPE_CIRCLE:
					arg1 = mInflater.inflate(R.layout.fragment_panel, null);
					friendHolder = new FriendHolder();
					friendHolder.tv_groupname = (TextView) arg1
							.findViewById(R.id.panel_name);
					friendHolder.vp_content = (CommonViewPager) arg1
							.findViewById(R.id.commonViewPager);
					arg1.setTag(friendHolder);
					break;
				case TYPE_BUTTON:
					arg1 = mInflater.inflate(R.layout.fragment_item_buttom,
							null);
					bHolder = new ButtonHolder();
					bHolder.button = (Button) arg1.findViewById(R.id.button);
					arg1.setTag(bHolder);
					break;
				default:
					break;
				}
			} else {
				switch (type) {
				case TYPE_MESSAGE:
					messageHolder = (MessageHolder) arg1.getTag();
					break;
				case TYPE_CIRCLE:
					friendHolder = (FriendHolder) arg1.getTag();
					break;
				case TYPE_BUTTON:
					bHolder = (ButtonHolder) arg1.getTag();
					break;
				default:
					break;
				}
			}
			arg1.setOnClickListener(null);
			switch (type) {
			case TYPE_MESSAGE:
				messageHolder.tv_nickname.setText(friends.get(lastChatFriends
						.get(arg0 - messageFirstPosition)).nickName);

				final Friend friend = friends.get(lastChatFriends.get(arg0
						- messageFirstPosition));
				Message lastMessage = friend.messages.get(friend.messages
						.size() - 1);
				if (lastMessage.contentType.equals("text")) {

					messageHolder.tv_lastchat.setText(friend.messages
							.get(friend.messages.size() - 1).content);
				} else if (lastMessage.contentType.equals("image")) {
					messageHolder.tv_lastchat
							.setText(getString(R.string.text_picture));
				} else if (lastMessage.contentType.equals("voice")) {

				}
				final String headFileName = friend.head;
				final ImageView iv_head = messageHolder.iv_head;
				app.fileHandler.getHeadImage(headFileName, new FileResult() {
					@Override
					public void onResult(String where) {
						iv_head.setImageBitmap(app.fileHandler.bitmaps
								.get(headFileName));
					}
				});
				Integer notread = friends.get(lastChatFriends.get(arg0
						- messageFirstPosition)).notReadMessagesCount;
				if (notread != null) {
					if (notread > 0) {
						messageHolder.tv_notread.setVisibility(View.VISIBLE);
						messageHolder.tv_notread.setText(notread.toString());
					} else {
						messageHolder.tv_notread.setText("");
						messageHolder.tv_notread.setVisibility(View.GONE);
					}
				}
				arg1.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View view) {
						app.data.nowChatFriend = friends.get(lastChatFriends
								.get(arg0 - messageFirstPosition));
						// mMCFragmentManager.replaceToContent(new
						// ChatFragment(),
						// true);
					}
				});
				break;
			case TYPE_CIRCLE:
				Circle circle = circles.get(arg0 - circleFirstPosition);
				friendHolder.tv_groupname.setText(circle.name);
				arg1.setOnLongClickListener(new OnLongClickListener() {

					@Override
					public boolean onLongClick(View v) {
						return true;
					}
				});
				friendHolder.setCircle(circle, arg0);
				break;
			case TYPE_BUTTON:
				if (showNewFriends && arg0 == 0) {
					bHolder.button.setText(getActivity().getString(
							R.string.button_newfriend)
							+ "(" + newFriendsCount + ")");
					bHolder.button.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							// mMCFragmentManager.replaceToContent(
							// new NewFriendsFragment(), true);
						}
					});
				} else if (arg0 == showMessageCount + messageFirstPosition) {
					bHolder.button.setText(getActivity().getString(
							R.string.button_moreMessage));
					bHolder.button.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							showMessageCount = lastChatFriends.size() > showMessageCount + 5 ? showMessageCount + 5
									: lastChatFriends.size();
							initData(false);
							mAdapter.notifyDataSetChanged();
						}
					});
				} else if (arg0 == getCount() - 2) {
					bHolder.button.setText(getActivity().getString(
							R.string.button_newcircle));
					bHolder.button.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
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
					bHolder.button.setText(getActivity().getString(
							R.string.button_findmorefriend));
					bHolder.button.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							mMainModeManager
									.showNext(mMainModeManager.mSearchFriendFragment);
						}
					});
				}
				break;
			default:
				break;
			}

			return arg1;
		}

		@Override
		public void notifyDataSetChanged() {
			initData(false);
			super.notifyDataSetChanged();
		}
	}

	class MessageHolder {
		ImageView iv_head;
		TextView tv_nickname;
		TextView tv_lastchat;
		TextView tv_notread;
	}

	class FriendHolder {
		TextView tv_groupname;
		CommonViewPager vp_content;
		PagerAdapter vp_contentAdapter;

		Circle circle;

		class ItemHolder {
			ImageView iv_head;
			TextView tv_nickname;
		}

		public void setCircle(Circle c, final int viewPosition) {
			this.circle = c;
			final List<String> phones = circle.phones;
			final int pagecount = phones.size() % 6 == 0 ? phones.size() / 6
					: phones.size() / 6 + 1;
			final List<View> pageviews = new ArrayList<View>();
			for (int i = 0; i < pagecount; i++) {
				final int a = i;
				BaseAdapter gridpageAdapter = new BaseAdapter() {
					@Override
					public View getView(final int position, View convertView,
							final ViewGroup parent) {
						ItemHolder itemHolder = null;
						if (convertView == null) {
							convertView = mInflater.inflate(
									R.layout.fragment_circles_gridpage_item,
									null);
							itemHolder = new ItemHolder();
							itemHolder.iv_head = (ImageView) convertView
									.findViewById(R.id.iv_head);
							itemHolder.tv_nickname = (TextView) convertView
									.findViewById(R.id.tv_nickname);
							convertView.setTag(itemHolder);
						} else {
							itemHolder = (ItemHolder) convertView.getTag();
						}
						if (phones.get(a * 6 + position) != null
								&& friends.get(phones.get(a * 6 + position)) != null) {

							final String headFileName = friends.get(phones
									.get(a * 6 + position)).head;
							final ImageView iv_head = itemHolder.iv_head;
							app.fileHandler.getHeadImage(headFileName,
									new FileResult() {
										@Override
										public void onResult(String where) {
											iv_head.setImageBitmap(app.fileHandler.bitmaps
													.get(headFileName));
											if (where == app.fileHandler.FROM_WEB) {
												mAdapter.notifyDataSetChanged();
											}
										}
									});
							itemHolder.tv_nickname.setText(friends.get(phones
									.get(a * 6 + position)).nickName);
							convertView
									.setOnClickListener(new OnClickListener() {

										@Override
										public void onClick(View v) {
										}
									});

							convertView
									.setOnLongClickListener(new OnLongClickListener() {

										@Override
										public boolean onLongClick(View v) {
											return false;
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
					public void unregisterDataSetObserver(
							DataSetObserver observer) {
						if (observer != null) {
							super.unregisterDataSetObserver(observer);
						}
					}

				};
				GridView gridpage = (GridView) mInflater.inflate(
						R.layout.fragment_circles_gridpage, null);
				gridpage.setAdapter(gridpageAdapter);
				pageviews.add(gridpage);
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
				public void destroyItem(View container, int position,
						Object object) {
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
			vp_content.setAdapter(vp_contentAdapter);
		}
	}

	class ButtonHolder {
		Button button;
	}

}
