package com.lejoying.mc.fragment;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.database.DataSetObserver;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.lejoying.mc.R;
import com.lejoying.mc.entity.Circle;
import com.lejoying.mc.entity.Friend;
import com.lejoying.mc.entity.Message;
import com.lejoying.mc.fragment.BaseInterface.NotifyListener;
import com.lejoying.mc.utils.MCDataTools;
import com.lejoying.mc.utils.MCImageTools;

public class FriendsFragment extends BaseListFragment {

	private final int TYPE_MAX_COUNT = 3;
	private final int TYPE_MESSAGE = 1;
	private final int TYPE_CIRCLE = 2;
	private final int TYPE_BUTTON = 3;

	private View mContent;
	private FriendsAdapter mFriendsAdapter;
	private FriendsHandler mHandler;
	private List<Message> messages;
	private List<Circle> circles;
	private int circlesSize;
	private int messagesSize;

	private Map<String, Integer> notReadMessages;

	private LayoutInflater mInflater;

	Bitmap head;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		messages = MCDataTools.getMessages(getActivity(), 5);
		circles = MCDataTools.getCircles(getActivity());
		notReadMessages = MCDataTools.getNotReadMessages(getActivity());

		messagesSize = messages.size();
		circlesSize = circles.size();

		mInflater = getActivity().getLayoutInflater();
		head = MCImageTools.getCircleBitmap(BitmapFactory.decodeResource(
				getResources(), R.drawable.xiaohei), true, 5, Color.WHITE);

		changeContentFragment(new CircleMenuFragment());
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mMCFragmentManager.showCircleMenuToTop(false, false);
		mMCFragmentManager
				.setCircleMenuPageName(getString(R.string.page_friend));
		mContent = inflater.inflate(R.layout.f_friends, null);
		mFriendsAdapter = new FriendsAdapter();

		mHandler = new FriendsHandler();
		// getActivity().getSupportFragmentManager().beginTransaction()
		// .replace(R.id.fl_bottom, new FriendEditTabFragment()).commit();

		mMCFragmentManager.setNotifyListener(new NotifyListener() {
			@Override
			public synchronized void notifyDataChanged(int notify) {
				mHandler.sendEmptyMessage(notify);
			}
		});

		return mContent;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		setListAdapter(mFriendsAdapter);
	}

	class FriendsAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return messagesSize + circlesSize + 3;
		}

		@Override
		public int getViewTypeCount() {
			return TYPE_MAX_COUNT + 1;
		}

		@Override
		public int getItemViewType(int position) {
			int type = 0;
			if (position < messagesSize) {
				type = TYPE_MESSAGE;
			} else if (position == messagesSize
					|| position > (messagesSize + circlesSize)) {
				type = TYPE_BUTTON;
			} else {
				type = TYPE_CIRCLE;
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
		public View getView(int arg0, View arg1, ViewGroup arg2) {
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
					arg1 = mInflater.inflate(R.layout.f_group_panel, null);
					friendHolder = new FriendHolder();
					friendHolder.tv_groupname = (TextView) arg1
							.findViewById(R.id.tv_groupname);
					friendHolder.vp_content = (ViewPager) arg1
							.findViewById(R.id.vp_content);
					arg1.setTag(friendHolder);
					break;
				case TYPE_BUTTON:
					arg1 = mInflater.inflate(R.layout.f_button, null);
					bHolder = new ButtonHolder();
					bHolder.button = (Button) arg1;
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

			switch (type) {
			case TYPE_MESSAGE:
				messageHolder.tv_nickname.setText(messages.get(arg0)
						.getFriend().getNickName());
				messageHolder.tv_lastchat.setText(messages.get(arg0)
						.getContent());
				messageHolder.iv_head.setImageBitmap(head);
				Integer notread = notReadMessages.get(messages.get(arg0)
						.getFriend().getPhone());
				if (notread != null) {
					messageHolder.tv_notread.setText(notread.toString());
				}
				break;
			case TYPE_CIRCLE:
				Circle circle = circles.get(arg0 - messagesSize - 1);
				friendHolder.tv_groupname.setText(circle.getName());
				friendHolder.setCircle(circle);
				break;
			case TYPE_BUTTON:
				bHolder.button.setText("添加好友");
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
		ViewPager vp_content;

		PagerAdapter vp_contentAdapter;

		List<View> pageviews = new ArrayList<View>();

		class ItemHolder {
			ImageView iv_head;
			TextView tv_nickname;
		}

		public void setCircle(Circle circle) {

			final List<Friend> friends = circle.getFriends();
			final int pagecount = friends.size() % 6 == 0 ? friends.size() / 6
					: friends.size() / 6 + 1;

			if (friends.size() == 0 || pagecount == 0) {
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
						return pageviews.size();
					}

					@Override
					public void destroyItem(View container, int position,
							Object object) {
						((ViewPager) container).removeView(pageviews
								.get(position));
					}

					@Override
					public Object instantiateItem(View container, int position) {
						((ViewPager) container)
								.addView(pageviews.get(position));
						return pageviews.get(position);
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

			if (pageviews.size() != 0) {
				vp_content.setAdapter(vp_contentAdapter);
				return;
			}

			for (int i = 0; i < pagecount; i++) {
				final int a = i;
				BaseAdapter gridpageAdapter = new BaseAdapter() {
					@Override
					public View getView(final int position, View convertView,
							final ViewGroup parent) {
						ItemHolder itemHolder = null;
						if (convertView == null) {
							convertView = mInflater
									.inflate(
											R.layout.f_group_panelitem_gridpageitem_user,
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

						itemHolder.iv_head.setImageBitmap(head);
						itemHolder.tv_nickname.setText(friends.get(
								a * 6 + position).getNickName());
						convertView.setOnClickListener(new OnClickListener() {

							@Override
							public void onClick(View v) {
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
						return friends.get(a * 6 + position);
					}

					@Override
					public int getCount() {
						int nowcount = 0;
						if (a < pagecount - 1) {
							nowcount = 6;
						} else {
							nowcount = friends.size() - a * 6;
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
						R.layout.f_group_panelitem_gridpage, null);
				gridpage.setAdapter(gridpageAdapter);
				pageviews.add(gridpage);
				vp_contentAdapter.notifyDataSetChanged();

			}

		}

	}

	class ButtonHolder {
		Button button;
	}

	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}

	@Override
	public EditText showSoftInputOnShow() {
		// TODO Auto-generated method stub
		return null;
	}

	class FriendsHandler extends Handler {
		@Override
		public void handleMessage(android.os.Message msg) {
			int what = msg.what;
			switch (what) {
			case NotifyListener.NOTIFY_MESSAGEANDFRIEND:
				// mFriendsAdapter.notifyDataSetChanged();
				break;

			default:
				break;
			}
		}
	}

}
