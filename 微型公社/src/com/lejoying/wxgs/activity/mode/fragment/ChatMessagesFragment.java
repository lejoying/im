package com.lejoying.wxgs.activity.mode.fragment;

import org.w3c.dom.Text;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.lejoying.wxgs.R;
import com.lejoying.wxgs.activity.mode.MainModeManager;
import com.lejoying.wxgs.activity.view.widget.CircleMenu;
import com.lejoying.wxgs.app.MainApplication;
import com.lejoying.wxgs.app.data.entity.Friend;
import com.lejoying.wxgs.app.data.entity.Message;
import com.lejoying.wxgs.app.handler.FileHandler.FileResult;

public class ChatMessagesFragment extends BaseFragment {

	MainApplication app = MainApplication.getMainApplication();
	MainModeManager mMainModeManager;

	View mContentView;
	ListView lv_messages;
	LayoutInflater mInflater;

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
		TextView headView = new TextView(getActivity());
		headView.setHeight(10);
		lv_messages.addHeaderView(headView);
		lv_messages.addFooterView(headView);
		lv_messages.setAdapter(new MyMessagesAdapter());
		return mContentView;
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
			String phone = app.data.lastChatFriends.get(position);
			final Friend friend = app.data.friends.get(phone);
			View messageView = null;
			if (convertView == null) {
				messageView = mInflater.inflate(
						R.layout.fragment_circles_messages_item, null);
			} else {
				messageView = convertView;
			}
			if (friend != null) {
				final ImageView head = (ImageView) messageView
						.findViewById(R.id.iv_head);
				TextView nickName = (TextView) messageView
						.findViewById(R.id.tv_nickname);
				TextView lastChatMessage = (TextView) messageView
						.findViewById(R.id.tv_lastchat);
				TextView notReadCount = (TextView) messageView
						.findViewById(R.id.tv_notread);

				nickName.setText(friend.nickName);
				Message lastMessage = friend.messages.get(friend.messages
						.size() - 1);
				if (lastMessage.contentType.equals("text")) {
					lastChatMessage.setText(friend.messages.get(friend.messages
							.size() - 1).content);
				} else if (lastMessage.contentType.equals("image")) {
					lastChatMessage.setText(getString(R.string.text_picture));
				} else if (lastMessage.contentType.equals("voice")) {
					// lastChatMessage.setText(getString(R.string.text_voice));
					lastChatMessage.setText(getActivity().getResources()
							.getString(R.string.text_voice));
				}
				final String headFileName = friend.head;
				app.fileHandler.getHeadImage(headFileName, new FileResult() {
					@Override
					public void onResult(String where) {
						head.setImageBitmap(app.fileHandler.bitmaps
								.get(headFileName));
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
						mMainModeManager
								.showNext(mMainModeManager.mChatFragment);
					}
				});
			}
			return messageView;
		}
	}
}
