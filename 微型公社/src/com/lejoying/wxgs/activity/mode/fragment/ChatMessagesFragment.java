package com.lejoying.wxgs.activity.mode.fragment;

import android.os.Bundle;
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
import com.lejoying.wxgs.activity.MainActivity;
import com.lejoying.wxgs.activity.mode.MainModeManager;
import com.lejoying.wxgs.activity.view.widget.CircleMenu;
import com.lejoying.wxgs.app.MainApplication;
import com.lejoying.wxgs.app.data.entity.Friend;
import com.lejoying.wxgs.app.data.entity.Group;
import com.lejoying.wxgs.app.data.entity.Message;
import com.lejoying.wxgs.app.handler.FileHandler.FileResult;

public class ChatMessagesFragment extends BaseFragment {

	MainApplication app = MainApplication.getMainApplication();
	MainModeManager mMainModeManager;

	MyMessagesAdapter messagesAdapter;

	View mContentView;
	ListView lv_messages;
	LayoutInflater mInflater;
	LinearLayout ll_not_messages;

	RelativeLayout current_me_circles;
	RelativeLayout current_me_message_list;
	RelativeLayout current_me_infomation;

	public void setMode(MainModeManager mainMode) {
		mMainModeManager = mainMode;
	}

	@Override
	public void onResume() {
		CircleMenu.show();
		CircleMenu.setPageName(getString(R.string.circlemenu_page_square));
		super.onResume();
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
		messagesAdapter = new MyMessagesAdapter();
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
				MainActivity.instance.mMainMode
						.show(MainActivity.instance.mMainMode.mCirclesFragment);
			}
		});
		current_me_infomation.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				MainActivity.instance.mMainMode.mBusinessCardFragment.mStatus = BusinessCardFragment.SHOW_SELF;
				MainActivity.instance.mMainMode
						.showNext(MainActivity.instance.mMainMode.mBusinessCardFragment);
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

	class MyMessagesAdapter extends BaseAdapter {

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
			long CHAT_TYPE_FRIEND = 0X0001;
			long CHAT_TYPE_GROUP = 0X0002;
			long chatType = CHAT_TYPE_FRIEND;
			String chatItem = app.data.lastChatFriends.get(position);
			View messageView = null;
			if (convertView == null) {
				messageView = mInflater.inflate(
						R.layout.fragment_circles_messages_item, null);
			} else {
				messageView = convertView;
			}
			final ImageView head = (ImageView) messageView
					.findViewById(R.id.iv_head);
			TextView nickName = (TextView) messageView
					.findViewById(R.id.tv_nickname);
			TextView lastChatMessage = (TextView) messageView
					.findViewById(R.id.tv_lastchat);
			TextView notReadCount = (TextView) messageView
					.findViewById(R.id.tv_notread);
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
				Message lastMessage = null;
				Integer notread;
				if (chatType == CHAT_TYPE_FRIEND) {
					chatName = chatFriend.nickName;
					chatHeadImgName = chatFriend.head;
					lastMessage = friend.messages
							.get(friend.messages.size() - 1);
					notread = chatFriend.notReadMessagesCount;
				} else {
					chatName = chatGroup.name + "(群组)";
					chatHeadImgName = chatGroup.icon;
					lastMessage = chatGroup.messages.get(chatGroup.messages
							.size() - 1);
					notread = chatGroup.notReadMessagesCount;
				}
				nickName.setText(chatName);
				if (lastMessage.contentType.equals("text")) {
					lastChatMessage.setText(lastMessage.content);
				} else if (lastMessage.contentType.equals("image")) {
					lastChatMessage.setText(getString(R.string.text_picture));
				} else if (lastMessage.contentType.equals("voice")) {
					lastChatMessage.setText(getActivity().getResources()
							.getString(R.string.text_voice));
				}
				final String headFileName = chatHeadImgName;
				app.fileHandler.getHeadImage(headFileName, new FileResult() {
					@Override
					public void onResult(String where) {
						head.setImageBitmap(app.fileHandler.bitmaps
								.get(headFileName));
					}
				});

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
						if (chatFriend != null) {
							mMainModeManager.mChatFragment.mStatus = ChatFriendFragment.CHAT_FRIEND;
							mMainModeManager.mChatFragment.mNowChatFriend = chatFriend;
							mMainModeManager
									.showNext(mMainModeManager.mChatFragment);
							notifyViews();
						} else {
							mMainModeManager.mChatGroupFragment.mStatus = ChatFriendFragment.CHAT_GROUP;
							mMainModeManager.mChatGroupFragment.mNowChatGroup = chatGroup;
							mMainModeManager
									.showNext(mMainModeManager.mChatGroupFragment);
							notifyViews();
						}
					}
				});
			}
			return messageView;
		}
	}
}
