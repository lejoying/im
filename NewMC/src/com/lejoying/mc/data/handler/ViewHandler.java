package com.lejoying.mc.data.handler;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import android.database.DataSetObserver;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.lejoying.mc.R;
import com.lejoying.mc.data.App;
import com.lejoying.mc.data.Circle;
import com.lejoying.mc.data.Friend;
import com.lejoying.mc.data.Message;
import com.lejoying.mc.data.handler.FileHandler.FileResult;
import com.lejoying.mc.view.FriendViewPager;

public class ViewHandler {
	App app;

	public void initialize(App app) {
		this.app = app;
	}

	Queue<Runnable> queue = new LinkedList<Runnable>();

	public interface GenerateViewListener {
		public void success(List<View> views);
	}

	public void generateFriendView(
			final GenerateViewListener generateViewListener) {
		Runnable runnable = new Runnable() {
			List<Circle> circles;
			Map<String, Friend> friends;
			List<Friend> newFriends;
			List<String> lastChatFriends;
			int newFriendsCount;
			int showMessageCount;

			@Override
			public void run() {
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
				showMessageCount = lastChatFriends.size() > 5 ? 5
						: lastChatFriends.size();

				List<View> friendViews = new ArrayList<View>();

				if (newFriendsCount != 0) {
					Button newFriendButton = (Button) (app.inflater.inflate(
							R.layout.f_button, null).findViewById(R.id.button));
					newFriendButton.setText(app.context
							.getString(R.string.btn_newfriends)
							+ "("
							+ newFriendsCount + ")");
					friendViews.add(newFriendButton);
				}

				for (int i = 0; i < showMessageCount; i++) {
					Friend friend = friends.get(lastChatFriends.get(i));
					View messageView = app.inflater.inflate(
							R.layout.f_messages_item, null);
					final ImageView iv_head = (ImageView) messageView
							.findViewById(R.id.iv_head);
					TextView tv_nickname = (TextView) messageView
							.findViewById(R.id.tv_nickname);
					TextView tv_lastchat = (TextView) messageView
							.findViewById(R.id.tv_lastchat);
					TextView tv_notread = (TextView) messageView
							.findViewById(R.id.tv_notread);

					tv_nickname.setText(friend.nickName);
					Message lastChatMessage = friend.messages
							.get(friend.messages.size() - 1);
					if (lastChatMessage.contentType.equals("text")) {
						tv_lastchat.setText(friend.messages.get(friend.messages
								.size() - 1).content);
					} else if (lastChatMessage.contentType.equals("image")) {
						tv_lastchat.setText(app.context
								.getString(R.string.message_picture));
					}

					Integer notread = friend.notReadMessagesCount;
					if (notread != null) {
						if (notread > 0) {
							tv_notread.setVisibility(View.VISIBLE);
							tv_notread.setText(notread.toString());
						} else {
							tv_notread.setText("");
							tv_notread.setVisibility(View.GONE);
						}
					}

					final String headFileName = friend.head;
					app.fileHandler.getHeadImage(headFileName,
							new FileResult() {
								@Override
								public void onResult(String where) {
									iv_head.setImageBitmap(app.fileHandler.bitmaps
											.get(headFileName));
								}
							});
					friendViews.add(messageView);
				}

				if (showMessageCount != 0) {
					Button showMoreMessage = (Button) app.inflater.inflate(
							R.layout.item_button, null);
					showMoreMessage.setText(app.context
							.getString(R.string.btn_moremessages));
					friendViews.add(showMoreMessage);
				}

				//generateCircles(friendViews, circles, friends);

				Button newGroup = (Button) app.inflater.inflate(
						R.layout.item_button, null);
				newGroup.setText(app.context
						.getString(R.string.btn_newgroup));
				friendViews.add(newGroup);

				Button findMoreFriend = (Button) app.inflater.inflate(
						R.layout.item_button, null);
				findMoreFriend.setText(app.context
						.getString(R.string.btn_findmorefriend));
				friendViews.add(findMoreFriend);

				if (generateViewListener != null) {
					generateViewListener.success(friendViews);
				}
			}
		};
		queue.offer(runnable);
		startHandle();
	}

	private void generateCircles(List<View> viewList, List<Circle> circles,
			final Map<String, Friend> friends) {
		class ItemHolder {
			ImageView iv_head;
			TextView tv_nickname;
		}
		for (int m = circles.size() - 1; m > -1; m--) {
			final Circle circle = circles.get(m);

			View group = app.inflater.inflate(R.layout.f_group_panel, null);

			TextView tv_groupname = (TextView) group
					.findViewById(R.id.tv_groupname);
			FriendViewPager vp_content = (FriendViewPager) group
					.findViewById(R.id.vp_content);

			tv_groupname.setText(circle.name);

			PagerAdapter vp_contentAdapter;

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
							convertView = app.inflater
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
						final String headFileName = friends.get(phones.get(a
								* 6 + position)).head;
						final ImageView iv_head = itemHolder.iv_head;
						app.fileHandler.getHeadImage(headFileName,
								new FileResult() {
									@Override
									public void onResult(String where) {
										iv_head.setImageBitmap(app.fileHandler.bitmaps
												.get(headFileName));
									}
								});
						itemHolder.tv_nickname.setText(friends.get(phones.get(a
								* 6 + position)).nickName);

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
				GridView gridpage = (GridView) app.inflater.inflate(
						R.layout.f_group_panelitem_gridpage, null);
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
			viewList.add(vp_content);
		}
	}

	boolean isStart = false;

	private void startHandle() {
		if (isStart || queue.size() == 0) {
			return;
		}
		isStart = true;
		final Runnable runnable = queue.poll();

		new Thread() {
			public void run() {
				runnable.run();
				isStart = false;
				startHandle();
			}
		}.start();
	}
}
