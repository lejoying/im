package com.lejoying.mc.data.handler;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.lejoying.mc.R;
import com.lejoying.mc.data.App;
import com.lejoying.mc.data.Circle;
import com.lejoying.mc.data.Friend;
import com.lejoying.mc.data.Message;
import com.lejoying.mc.data.handler.FileHandler.FileResult;

public class ViewHandler {
	App app;

	public void initialize(App app) {
		this.app = app;
	}

	Queue<Runnable> queue = new LinkedList<Runnable>();

	List<View> friendViews = new ArrayList<View>();

	public void generateFriendView() {
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
					Button newFriendButton = (Button) app.inflater.inflate(
							R.layout.item_button, null);
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

				generateCircles(friendViews,circles);

				Button newGroup = (Button) app.inflater.inflate(
						R.layout.item_button, null);
				newGroup.setText(app.context
						.getString(R.string.btn_moremessages));
				friendViews.add(newGroup);

				Button findMoreFriend = (Button) app.inflater.inflate(
						R.layout.item_button, null);
				findMoreFriend.setText(app.context
						.getString(R.string.btn_moremessages));
				friendViews.add(findMoreFriend);
				
				ViewHandler.this.friendViews = friendViews;
			}
		};
		queue.offer(runnable);
		startHandle();
	}

	private void generateCircles(List<View> viewList,List<Circle> circles) {
		
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
