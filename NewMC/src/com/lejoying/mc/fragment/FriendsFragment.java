package com.lejoying.mc.fragment;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import android.content.Intent;
import android.database.DataSetObserver;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
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
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.lejoying.mc.R;
import com.lejoying.mc.data.App;
import com.lejoying.mc.data.Circle;
import com.lejoying.mc.data.Friend;
import com.lejoying.mc.data.Message;
import com.lejoying.mc.data.handler.DataHandler.UIModification;
import com.lejoying.mc.data.handler.FileHandler.FileResult;
import com.lejoying.mc.service.PushService;
import com.lejoying.mc.utils.MCImageUtils;
import com.lejoying.mc.view.FriendViewPager;

public class FriendsFragment extends BaseListFragment {

	private final int TYPE_MAX_COUNT = 4;
	private final int TYPE_MESSAGE = 1;
	private final int TYPE_CIRCLE = 2;
	private final int TYPE_BUTTON = 3;

	public static FriendsFragment instance;

	App app = App.getInstance();

	List<Circle> circles;
	Map<String, Friend> friends;
	List<Friend> newFriends;
	List<String> lastChatFriends;

	public FriendsAdapter mAdapter;

	private View mContent;
	private LayoutInflater mInflater;

	int showMessageCount;
	int buttonCount;
	boolean showNewFriends;
	int messageFirstPosition;
	int circleFirstPosition;

	Bitmap head;
	Map<Integer, List<View>> circlePageViews;

	int newFriendsCount;

	View rl_control;

	public static View editView;

	public void initData(boolean initShowMessages) {
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

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		mInflater = getActivity().getLayoutInflater();
		head = MCImageUtils.getCircleBitmap(BitmapFactory.decodeResource(
				getResources(), R.drawable.face_man), true, 10, Color.WHITE);
		Intent service = new Intent(getActivity(), PushService.class);
		service.putExtra("objective", "start");
		getActivity().startService(service);
		mAdapter = new FriendsAdapter();
		circlePageViews = new Hashtable<Integer, List<View>>();
		initData(true);
		app.serverHandler.getAllData();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mMCFragmentManager.showCircleMenuToTop(false, false);
		mMCFragmentManager
				.setCircleMenuPageName(getString(R.string.page_friend));
		mContent = inflater.inflate(R.layout.f_friends, null);
		rl_control = mContent.findViewById(R.id.rl_control);
		return mContent;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		if (getListAdapter() == null) {
			app.sDcardDataResolver.readLocalData(new UIModification() {
				@Override
				public void modifyUI() {
					initData(true);
					setListAdapter(mAdapter);
				}
			});
		}
	}

	public void onResume() {
		super.onResume();
		instance = this;
		app.mark = app.friendsFragment;
		initData(true);
		mAdapter.notifyDataSetChanged();
	}

	public void onDestroyView() {
		instance = null;
		super.onDestroyView();
	}

	public class FriendsAdapter extends BaseAdapter {

		@Override
		public int getCount() {
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
					arg1 = mInflater.inflate(R.layout.f_messages_item, null);
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
					arg1 = mInflater.inflate(R.layout.f_friend_panel, null);
					friendHolder = new FriendHolder();
					friendHolder.tv_groupname = (TextView) arg1
							.findViewById(R.id.textView_groupNameAndMemberCount);
					friendHolder.vp_content = (FriendViewPager) arg1
							.findViewById(R.id.vp_content);
					arg1.setTag(friendHolder);
					break;
				case TYPE_BUTTON:
					arg1 = mInflater.inflate(R.layout.f_button, null);
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
							.setText(getString(R.string.message_picture));
				} else if (lastMessage.contentType.equals("voice")) {

				}
				final String headFileName = friend.head;
				final ImageView iv_head = messageHolder.iv_head;
				app.fileHandler.getHeadImage(headFileName, new FileResult() {
					@Override
					public void onResult(String where) {
						iv_head.setImageBitmap(app.fileHandler.bitmaps
								.get(headFileName));
						if (where == app.fileHandler.FROM_WEB) {
							mAdapter.notifyDataSetChanged();
						}
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
						mMCFragmentManager.replaceToContent(new ChatFragment(),
								true);
					}
				});
				break;
			case TYPE_CIRCLE:
				Circle circle = circles.get(arg0 - circleFirstPosition);
				friendHolder.tv_groupname.setText(circle.name);
				arg1.setOnLongClickListener(new OnLongClickListener() {

					@Override
					public boolean onLongClick(View v) {
						System.out.println(getListView()
								.getFirstVisiblePosition());
						System.out.println(getListAdapter().getView(
								getListView().getFirstVisiblePosition(), null,
								null));
						System.out.println(getListView()
								.getLastVisiblePosition());
						return true;
					}
				});
				friendHolder.setCircle(circle, arg0);
				break;
			case TYPE_BUTTON:
				if (showNewFriends && arg0 == 0) {
					bHolder.button.setText(getActivity().getString(
							R.string.btn_newfriends)
							+ "(" + newFriendsCount + ")");
					bHolder.button.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							mMCFragmentManager.replaceToContent(
									new NewFriendsFragment(), true);
						}
					});
				} else if (arg0 == showMessageCount + messageFirstPosition) {
					bHolder.button.setText(getActivity().getString(
							R.string.btn_moremessages));
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
							R.string.btn_newgroup));
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
							R.string.btn_findmorefriend));
					bHolder.button.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							mMCFragmentManager.replaceToContent(
									new SearchFriendFragment(), true);
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
		public void unregisterDataSetObserver(DataSetObserver observer) {
			super.unregisterDataSetObserver(observer);
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
		FriendViewPager vp_content;
		PagerAdapter vp_contentAdapter;

		// GestureDetector gestureDetector;

		Circle circle;

		class ItemHolder {
			ImageView iv_head;
			TextView tv_nickname;
		}

		// void generateGestureDetector() {
		// if (gestureDetector == null) {
		// OnGestureListener listener = new OnGestureListener() {
		//
		// @Override
		// public boolean onSingleTapUp(MotionEvent e) {
		// // TODO Auto-generated method stub
		// return false;
		// }
		//
		// @Override
		// public void onShowPress(MotionEvent e) {
		// // TODO Auto-generated method stub
		//
		// }
		//
		// @Override
		// public boolean onScroll(MotionEvent e1, MotionEvent e2,
		// float distanceX, float distanceY) {
		// getListView().requestDisallowInterceptTouchEvent(true);
		// return true;
		// }
		//
		// @Override
		// public void onLongPress(MotionEvent e) {
		// // TODO Auto-generated method stub
		//
		// }
		//
		// @Override
		// public boolean onFling(MotionEvent e1, MotionEvent e2,
		// float velocityX, float velocityY) {
		// boolean flag = true;
		// if (velocityX > velocityY) {
		// getListView().requestDisallowInterceptTouchEvent(
		// false);
		// flag = true;
		// }
		// return flag;
		// }
		//
		// @Override
		// public boolean onDown(MotionEvent e) {
		// // TODO Auto-generated method stub
		// return true;
		// }
		// };
		// gestureDetector = new GestureDetector(getActivity(), listener);
		// }
		// }

		public void setCircle(Circle c, final int viewPosition) {
			// generateGestureDetector();
			this.circle = c;
			final List<String> phones = circle.phones;
			final int pagecount = phones.size() % 6 == 0 ? phones.size() / 6
					: phones.size() / 6 + 1;
			List<View> pageviews = circlePageViews.get(circle.rid);
			if (pageviews == null) {
				pageviews = new ArrayList<View>();
				for (int i = 0; i < pagecount; i++) {
					final int a = i;
					BaseAdapter gridpageAdapter = new BaseAdapter() {
						@Override
						public View getView(final int position,
								View convertView, final ViewGroup parent) {
							ItemHolder itemHolder = null;
							if (convertView == null) {
								convertView = mInflater
										.inflate(
												R.layout.f_friend_panelitem_gridpage_item,
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
											app.data.tempFriend = (Friend) getItem(position);
											app.businessCardStatus = app.SHOW_FRIEND;
											mMCFragmentManager
													.replaceToContent(
															new BusinessCardFragment(),
															true);
										}
									});

							convertView
									.setOnLongClickListener(new OnLongClickListener() {

										@Override
										public boolean onLongClick(View v) {
											editView = getListAdapter()
													.getView(viewPosition,
															null, null);
											mMCFragmentManager
													.replaceToContent(
															new EditFragment(),
															true);
											return false;
										}
									});
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
							R.layout.f_friend_panelitem_gridpage, null);
					gridpage.setAdapter(gridpageAdapter);
					pageviews.add(gridpage);
					// gridpage.setOnTouchListener(new OnTouchListener() {
					//
					// @Override
					// public boolean onTouch(View v, MotionEvent event) {
					// return gestureDetector.onTouchEvent(event);
					// }
					// });
				}
				circlePageViews.put(circle.rid, pageviews);
			}
			if (circlePageViews.get(circle.rid).size() == 0) {
				vp_content.setAdapter(null);
				return;
			}
			if (vp_contentAdapter == null) {
				vp_contentAdapter = new PagerAdapter() {
					@Override
					public boolean isViewFromObject(View arg0, Object arg1) {
						return arg0 == arg1;
					}

					@Override
					public int getCount() {
						return circlePageViews.get(circle.rid).size();
					}

					@Override
					public void destroyItem(View container, int position,
							Object object) {
						if (circlePageViews.get(circle.rid).size() > position)
							((ViewPager) container).removeView(circlePageViews
									.get(circle.rid).get(position));
					}

					@Override
					public Object instantiateItem(View container, int position) {
						if (circlePageViews.get(circle.rid).get(position)
								.getParent() != null) {
							((ViewGroup) (circlePageViews.get(circle.rid).get(
									position).getParent())).removeAllViews();
						}
						((ViewPager) container).addView(circlePageViews.get(
								circle.rid).get(position));
						return circlePageViews.get(circle.rid).get(position);
					}

					@Override
					public void unregisterDataSetObserver(
							DataSetObserver observer) {
						if (observer != null) {
							super.unregisterDataSetObserver(observer);
						}
					}
				};
			}

			vp_content.setAdapter(vp_contentAdapter);
		}

	}

	class ButtonHolder {
		Button button;
	}

	@Override
	protected EditText showSoftInputOnShow() {
		// TODO Auto-generated method stub
		return null;
	}
}