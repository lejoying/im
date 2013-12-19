package com.lejoying.mc.fragment;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
import com.lejoying.mc.utils.MCImageTools;

public class FriendsFragment extends BaseListFragment {

	private final int TYPE_MAX_COUNT = 4;
	private final int TYPE_MESSAGE = 1;
	private final int TYPE_CIRCLE = 2;
	private final int TYPE_BUTTON = 3;

	App app = App.getInstance();
	List<Circle> circles = app.data.circles;
	Map<String, Friend> friends = app.data.friends;
	List<String> lastChatFriends = app.data.lastChatFriends;

	int showMessageCount;

	private View mContent;

	private LayoutInflater mInflater;

	Bitmap head;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		mInflater = getActivity().getLayoutInflater();
		head = MCImageTools.getCircleBitmap(BitmapFactory.decodeResource(
				getResources(), R.drawable.xiaohei), true, 5, Color.WHITE);

		showMessageCount = lastChatFriends.size() > 5 ? 5 : lastChatFriends
				.size();

		changeContentFragment(new CircleMenuFragment());
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mMCFragmentManager.showCircleMenuToTop(false, false);
		mMCFragmentManager
				.setCircleMenuPageName(getString(R.string.page_friend));
		mContent = inflater.inflate(R.layout.f_friends, null);

		return mContent;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		setListAdapter(new FriendsAdapter());
	}

	class FriendsAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return showMessageCount + circles.size() + 3;
		}

		@Override
		public int getViewTypeCount() {
			return TYPE_MAX_COUNT;
		}

		@Override
		public int getItemViewType(int position) {
			int type = 0;
			if (position < showMessageCount) {
				type = TYPE_MESSAGE;
			} else if (position == showMessageCount
					|| position > (showMessageCount + circles.size())) {
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
				messageHolder.tv_nickname.setText(friends.get(lastChatFriends
						.get(arg0)).nickName);
				messageHolder.tv_lastchat.setText(friends.get(lastChatFriends
						.get(arg0)).messages.get(0).content);
				messageHolder.iv_head.setImageBitmap(head);
				Integer notread = friends.get(lastChatFriends.get(arg0)).notReadMessagesCount;
				if (notread != null) {
					messageHolder.tv_notread.setText(notread.toString());
				}
				break;
			case TYPE_CIRCLE:
				Circle circle = circles.get(arg0 - showMessageCount - 1);
				friendHolder.tv_groupname.setText(circle.name);
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

			final List<String> phones = circle.phones;
			final int pagecount = phones.size() % 6 == 0 ? phones.size() / 6
					: phones.size() / 6 + 1;

			if (phones.size() == 0 || pagecount == 0) {
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
						itemHolder.tv_nickname.setText(friends.get(phones.get(a
								* 6 + position)).nickName);
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
	protected EditText showSoftInputOnShow() {
		// TODO Auto-generated method stub
		return null;
	}

}
