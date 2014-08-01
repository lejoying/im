package com.lejoying.wxgs.activity.mode.fragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lejoying.wxgs.R;
import com.lejoying.wxgs.activity.ChatActivity;
import com.lejoying.wxgs.activity.MainActivity;
import com.lejoying.wxgs.activity.mode.MainModeManager;
import com.lejoying.wxgs.activity.utils.TimeUtils;
import com.lejoying.wxgs.app.MainApplication;
import com.lejoying.wxgs.app.data.entity.Friend;
import com.lejoying.wxgs.app.data.entity.Group;
import com.lejoying.wxgs.app.data.entity.Message;
import com.lejoying.wxgs.app.handler.OSSFileHandler.FileResult;

public class ChatMessagesFragment extends BaseFragment {

	MainApplication app = MainApplication.getMainApplication();
	MainModeManager mMainModeManager;

	ChatMessagesAdapter messagesAdapter;

	View mContentView;
	ListView lv_messages;
	LayoutInflater mInflater;
	LinearLayout ll_not_messages;

	RelativeLayout current_me_circles;
	RelativeLayout current_me_message_list;
	RelativeLayout current_me_infomation;

	boolean isInit = false;

	public void setMode(MainModeManager mainMode) {
		mMainModeManager = mainMode;
	}

	@Override
	public void onResume() {
		super.onResume();
		mMainModeManager.handleMenu(true);
		if (messagesAdapter != null && isInit) {
			messagesAdapter.notifyDataSetChanged();
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mInflater = inflater;
		mContentView = mInflater.inflate(R.layout.f_chat_messages_list, null);
		lv_messages = (ListView) mContentView.findViewById(R.id.lv_messages);
		ll_not_messages = (LinearLayout) mContentView
				.findViewById(R.id.ll_not_messages);

		current_me_circles = (RelativeLayout) mContentView
				.findViewById(R.id.current_me_circles);
		current_me_message_list = (RelativeLayout) mContentView
				.findViewById(R.id.current_me_message_list);
		current_me_infomation = (RelativeLayout) mContentView
				.findViewById(R.id.current_me_infomation);

		TextView headView = new TextView(getActivity());
		headView.setHeight(10);
		lv_messages.addHeaderView(headView);
		lv_messages.addFooterView(headView);
		messagesAdapter = new ChatMessagesAdapter();
		lv_messages.setAdapter(messagesAdapter);
		if (app.data.lastChatFriends.size() == 0) {
			ll_not_messages.setVisibility(View.VISIBLE);
		} else {
			ll_not_messages.setVisibility(View.GONE);
		}
		initEvent();
		return mContentView;
	}

	private void initEvent() {
		current_me_circles.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				MainActivity.instance.mMainMode.mCurrentMyFragment = MainActivity.instance.mMainMode.FRAGMENT_CIRCLE;
				MainActivity.instance.mMainMode
						.show(MainActivity.instance.mMainMode.mCirclesFragment);
			}
		});
		current_me_infomation.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				MainActivity.instance.mMainMode.mCurrentMyFragment = MainActivity.instance.mMainMode.FRAGMENT_MY;
				MainActivity.instance.mMainMode
						.show(MainActivity.instance.mMainMode.mMyFragment);
				// Intent intent = new Intent(getActivity(),
				// BusinessCardActivity.class);
				// intent.putExtra("type", BusinessCardActivity.TYPE_SELF);
				// startActivity(intent);
				// MainActivity.instance.mMainMode.mBusinessCardFragment.mStatus
				// = BusinessCardFragment.SHOW_SELF;
				// MainActivity.instance.mMainMode
				// .showNext(MainActivity.instance.mMainMode.mBusinessCardFragment);
			}
		});

	}

	public void notifyViews() {
		if (messagesAdapter != null) {
			messagesAdapter.notifyDataSetChanged();
			if (app.data.lastChatFriends.size() == 0) {
				ll_not_messages.setVisibility(View.VISIBLE);
			} else {
				ll_not_messages.setVisibility(View.GONE);
			}
		}

	}

	public void createShortCut() {

		// 创建快捷方式的Intent
		Intent shortcutintent = new Intent(
				"com.android.launcher.action.INSTALL_SHORTCUT");
		// 不允许重复创建
		shortcutintent.putExtra("duplicate", false);
		// 需要现实的名称
		shortcutintent.putExtra(Intent.EXTRA_SHORTCUT_NAME, "微信公社0");
		// 快捷图片(每次重绘logo生成一张新图)
		Parcelable icon = Intent.ShortcutIconResource.fromContext(getActivity()
				.getBaseContext(), R.drawable.notifyicon);
		shortcutintent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, icon);
		// 点击快捷图片，运行的程序主入口
		shortcutintent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, new Intent());
		// 发送广播。OK
		getActivity().sendBroadcast(shortcutintent);
	}

	class ChatMessagesAdapter extends BaseAdapter {

		@Override
		public void notifyDataSetChanged() {
			if (app.data.lastChatFriends.size() == 0) {
				if (ll_not_messages.getVisibility() == View.GONE)
					ll_not_messages.setVisibility(View.VISIBLE);
			} else {
				if (ll_not_messages.getVisibility() == View.VISIBLE)
					ll_not_messages.setVisibility(View.GONE);
			}
			super.notifyDataSetChanged();
		}

		@Override
		public int getCount() {
			return app.data.lastChatFriends.size();
		}

		@Override
		public Object getItem(int position) {
			return app.data.lastChatFriends.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (position == getCount() - 1) {
				isInit = true;
			}
			ChatMessageHolder chatMessageHolder = null;
			long CHAT_TYPE_FRIEND = 0X0001;
			long CHAT_TYPE_GROUP = 0X0002;
			long chatType = CHAT_TYPE_FRIEND;
			String chatItem = app.data.lastChatFriends.get(position);
			if (convertView == null) {
				chatMessageHolder = new ChatMessageHolder();
				convertView = mInflater.inflate(
						R.layout.fragment_circles_messages_item, null);
				chatMessageHolder.headView = (ImageView) convertView
						.findViewById(R.id.iv_head);
				chatMessageHolder.nickNameView = (TextView) convertView
						.findViewById(R.id.tv_nickname);
				chatMessageHolder.lastChatView = (TextView) convertView
						.findViewById(R.id.tv_lastchat);
				chatMessageHolder.notReadCountView = (TextView) convertView
						.findViewById(R.id.tv_notread);
				chatMessageHolder.lastChatTimeView = (TextView) convertView
						.findViewById(R.id.tv_time);
				convertView.setTag(chatMessageHolder);
			} else {
				chatMessageHolder = (ChatMessageHolder) convertView.getTag();
			}

			Friend friend = null;
			Group group = null;
			if ("f".equals(chatItem.substring(0, 1))) {
				chatType = CHAT_TYPE_FRIEND;
				friend = app.data.friends.get(chatItem.substring(1));
			} else {
				chatType = CHAT_TYPE_GROUP;
				group = app.data.groupsMap.get(chatItem.substring(1));
			}
			if (friend != null || group != null) {
				final Friend chatFriend = friend;
				final Group chatGroup = group;
				String chatName = "";
				String chatHeadImgName = "";
				String sex = "男";
				Message lastMessage = null;
				Integer notread = null;
				if (chatType == CHAT_TYPE_FRIEND) {
					chatName = chatFriend.nickName;
					if (!"".equals(chatFriend.alias)) {
						chatName = chatFriend.alias;
					}
					chatHeadImgName = chatFriend.head;
					sex = chatFriend.sex;
					lastMessage = friend.messages
							.get(friend.messages.size() - 1);
					notread = chatFriend.notReadMessagesCount;
				} else if (chatType == CHAT_TYPE_GROUP) {
					chatName = chatGroup.name + "(群组)";
					chatHeadImgName = chatGroup.icon;
					lastMessage = chatGroup.messages.get(chatGroup.messages
							.size() - 1);
					notread = chatGroup.notReadMessagesCount;
				}
				if (lastMessage == null)
					return convertView;
				chatMessageHolder.lastChatTimeView
						.setText(TimeUtils.getChatMessageListTime(Long
								.valueOf(lastMessage.time)));
				chatMessageHolder.nickNameView.setText(chatName);
				if (lastMessage.contentType.equals("text")) {
					String mLasastChatMessage;
					try {
						mLasastChatMessage = lastMessage.content.get(0);
					} catch (Exception e) {
						mLasastChatMessage = lastMessage.content.toString();
					}
					chatMessageHolder.lastChatView.setText(mLasastChatMessage);
				} else if (lastMessage.contentType.equals("image")) {
					chatMessageHolder.lastChatView
							.setText(getString(R.string.text_picture));
				} else if (lastMessage.contentType.equals("voice")) {
					chatMessageHolder.lastChatView.setText(getActivity()
							.getResources().getString(R.string.text_voice));
				}
				final String headFileName = chatHeadImgName;
				final ChatMessageHolder chatMessageHolder0 = chatMessageHolder;
				app.fileHandler.getHeadImage(headFileName, sex,
						new FileResult() {
							@Override
							public void onResult(String where, Bitmap bitmap) {
								chatMessageHolder0.headView
										.setImageBitmap(app.fileHandler.bitmaps
												.get(headFileName));
							}
						});

				if (notread != null) {
					if (notread > 0) {
						chatMessageHolder.notReadCountView
								.setVisibility(View.VISIBLE);
						chatMessageHolder.notReadCountView.setText(notread
								.toString());
					} else {
						chatMessageHolder.notReadCountView.setText("");
						chatMessageHolder.notReadCountView
								.setVisibility(View.GONE);
					}
				}
				convertView.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View view) {
						if (chatFriend != null) {
							Intent intent = new Intent(getActivity(),
									ChatActivity.class);
							intent.putExtra("status", ChatActivity.CHAT_FRIEND);
							intent.putExtra("phone", chatFriend.phone);
							startActivity(intent);
							// mMainModeManager.mChatFragment.mStatus =
							// ChatFriendFragment.CHAT_FRIEND;
							// mMainModeManager.mChatFragment.mNowChatFriend =
							// chatFriend;
							// mMainModeManager
							// .showNext(mMainModeManager.mChatFragment);
							notifyViews();
						} else {
							Intent intent = new Intent(getActivity(),
									ChatActivity.class);
							intent.putExtra("status", ChatActivity.CHAT_GROUP);
							intent.putExtra("gid", chatGroup.gid + "");
							startActivity(intent);
							// mMainModeManager.mChatGroupFragment.mStatus =
							// ChatFriendFragment.CHAT_GROUP;
							// mMainModeManager.mChatGroupFragment.mNowChatGroup
							// = chatGroup;
							// mMainModeManager
							// .showNext(mMainModeManager.mChatGroupFragment);
							notifyViews();
						}
					}
				});
			}
			return convertView;
		}
	}

	class ChatMessageHolder {
		ImageView headView;
		TextView nickNameView;
		TextView lastChatView;
		TextView notReadCountView;
		TextView lastChatTimeView;
	}
}
