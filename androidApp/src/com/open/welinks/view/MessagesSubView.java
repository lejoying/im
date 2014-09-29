package com.open.welinks.view;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.util.DisplayMetrics;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.open.lib.MyLog;
import com.open.lib.TouchView;
import com.open.lib.viewbody.ListBody1;
import com.open.lib.viewbody.ListBody1.MyListItemBody;
import com.open.welinks.R;
import com.open.welinks.controller.MessagesSubController;
import com.open.welinks.model.Data;
import com.open.welinks.model.Data.Event.EventMessage;
import com.open.welinks.model.Data.Messages.Message;
import com.open.welinks.model.Data.Relationship.Friend;
import com.open.welinks.model.Data.Relationship.Group;
import com.open.welinks.model.FileHandlers;
import com.open.welinks.model.Parser;
import com.open.welinks.utils.DateUtil;

public class MessagesSubView {

	public Data data = Data.getInstance();
	public Parser parser = Parser.getInstance();
	public String tag = "MessagesSubView";
	public MyLog log = new MyLog(tag, true);

	public Gson gson = new Gson();

	public DisplayMetrics displayMetrics;

	public TouchView messagesView;

	public TouchView noMessagesStatusView;

	public ListBody1 messageListBody;

	public MainView mainView;

	public MessagesSubController thisController;

	// public List<String> messagesKeepOnlyOne;

	public boolean inited = false;

	public FileHandlers fileHandlers = FileHandlers.getInstance();
	public DisplayImageOptions options;

	public ViewManage viewManage = ViewManage.getInstance();

	public MessagesSubView(MainView mainView) {
		this.mainView = mainView;
		viewManage.messagesSubView = this;
	}

	public void initViews() {
		this.messagesView = mainView.messagesView;
		this.displayMetrics = mainView.displayMetrics;

		messageListBody = new ListBody1();
		messageListBody.initialize(displayMetrics, messagesView);

		// messagesKeepOnlyOne = new ArrayList<String>();

		noMessagesStatusView = (TouchView) messagesView.findViewById(R.id.NoMessagesStatus);
		options = new DisplayImageOptions.Builder().cacheInMemory(true).cacheOnDisk(true).showImageOnLoading(R.drawable.ic_stub).showImageForEmptyUri(R.drawable.ic_empty).showImageOnFail(R.drawable.ic_error).cacheInMemory(true).cacheOnDisk(true).considerExifParams(true).displayer(new RoundedBitmapDisplayer(50)).build();

	}

	public void onResume() {
		if (inited) {
			showMessagesSequence();
		} else {
			inited = true;
		}
	}

	public void onDestroy() {
		inited = false;
	}

	public void showMessagesSequence() {
		data = parser.check();
		if (data.messages == null) {
			return;
		}
		List<String> messagesOrder = data.messages.messagesOrder;
		Map<String, ArrayList<Message>> friendMessageMap = data.messages.friendMessageMap;
		Map<String, ArrayList<Message>> groupMessageMap = data.messages.groupMessageMap;

		this.messageListBody.containerView.removeAllViews();
		this.messageListBody.listItemsSequence.clear();
		// this.messageListBody.listItemBodiesMap.clear();
		this.messageListBody.height = 0;

		// messagesKeepOnlyOne.clear();

		if (messagesOrder.size() > 0) {
			noMessagesStatusView.setVisibility(View.GONE);
		}
		for (int i = 0; i < messagesOrder.size(); i++) {
			String key = messagesOrder.get(i);
			log.e("message list key:" + key);
			Message message = null;
			String fileName = "";
			String type = "";
			String key2 = "";
			if (key.indexOf("p") == 0) {
				type = "p";
				int size = friendMessageMap.get(key).size();
				if (size != 0) {
					size--;
				} else {
					return;
				}
				message = friendMessageMap.get(key).get(size);
				key2 = "message#" + message.phone + "_" + message.time;
				try {
					String phone = "";
					if (message.phone.equals(data.userInformation.currentUser.phone)) {
						phone = (String) gson.fromJson(message.phoneto, List.class).get(0);
					} else {
						phone = message.phone;
					}
					fileName = data.relationship.friendsMap.get(phone).head;
				} catch (Exception e) {
					fileName = "";
				}
			} else if (key.indexOf("g") == 0) {
				type = "g";
				int size = groupMessageMap.get(key).size();
				if (size != 0) {
					size--;
				} else {
					return;
				}
				message = groupMessageMap.get(key).get(size);
				key2 = "message#" + message.gid + "_" + message.time;
				try {
					fileName = data.relationship.groupsMap.get(message.gid).icon;
				} catch (Exception e) {
					fileName = "";
				}
			} else {
				key2 = key;
			}
			MessageBody messageBody = null;
			if (this.messageListBody.listItemBodiesMap.get(key2) != null) {
				if ("p".equals(type)) {
					messageBody = (MessageBody) this.messageListBody.listItemBodiesMap.get(key2);
					messageBody.setContent(message, fileName);
				} else if ("g".equals(type)) {
					messageBody = (MessageBody) this.messageListBody.listItemBodiesMap.get(key2);
					messageBody.setContent(message, fileName);
				} else {
					messageBody = (MessageBody) this.messageListBody.listItemBodiesMap.get(key);
					messageBody.setContent(null, "");
				}
			} else {
				messageBody = new MessageBody(this.messageListBody);
				if (key.indexOf("event_user") == 0) {
					messageBody.initialize(-1);
					messageBody.setContent(null, "");
					this.messageListBody.listItemsSequence.add(key2);
					this.messageListBody.listItemBodiesMap.put(key2, messageBody);
				} else if (key.indexOf("event_group") == 0) {
					messageBody.initialize(-2);
					messageBody.setContent(null, "");
					this.messageListBody.listItemsSequence.add(key2);
					this.messageListBody.listItemBodiesMap.put(key2, messageBody);
				} else {
					messageBody.initialize(i);
					messageBody.setContent(message, fileName);
					this.messageListBody.listItemsSequence.add(key);
					this.messageListBody.listItemBodiesMap.put(key, messageBody);
				}
			}
			TouchView.LayoutParams layoutParams = new TouchView.LayoutParams((int) (displayMetrics.widthPixels), (int) (80 * displayMetrics.density));
			messageBody.y = this.messageListBody.height;
			messageBody.cardView.setY(messageBody.y);
			// messageBody.cardView.setX(0);
			messageBody.itemHeight = 80 * displayMetrics.density;
			this.messageListBody.height = this.messageListBody.height + 80 * displayMetrics.density;
			this.messageListBody.containerView.addView(messageBody.cardView, layoutParams);

			messageBody.cardView.setTag(R.id.tag_class, "message_view");
			messageBody.cardView.setTag(R.id.tag_first, key);
			messageBody.cardView.setOnTouchListener(thisController.mOnTouchListener);
			messageBody.cardView.setOnClickListener(thisController.mOnClickListener);

		}
		this.messageListBody.containerHeight = (int) (this.displayMetrics.heightPixels - 38 - displayMetrics.density * 88);
		this.messageListBody.setChildrenPosition();
	}

	public class MessageBody extends MyListItemBody {

		MessageBody(ListBody1 listBody) {
			listBody.super();
		}

		public View cardView = null;

		public ImageView headView;
		public TextView nickNameView;
		public TextView lastChatTimeView;
		public TextView lastChatMessageView;
		public TextView notReadNumberView;

		public TextView groupIconView;

		public int i;

		public View initialize(int i) {
			this.i = i;
			this.cardView = mainView.mInflater.inflate(R.layout.chat_message_item, null);
			this.headView = (ImageView) this.cardView.findViewById(R.id.userHeadView);
			this.nickNameView = (TextView) this.cardView.findViewById(R.id.tv_nickname);
			this.lastChatTimeView = (TextView) this.cardView.findViewById(R.id.tv_time);
			this.lastChatMessageView = (TextView) this.cardView.findViewById(R.id.tv_lastchatcontent);
			this.notReadNumberView = (TextView) this.cardView.findViewById(R.id.tv_notread);
			this.groupIconView = (TextView) this.cardView.findViewById(R.id.groupIcon);

			this.itemHeight = 80 * displayMetrics.density;
			super.initialize(cardView);
			return cardView;
		}

		public void setContent(Message message, String fileName) {
			notReadNumberView.setVisibility(View.GONE);
			lastChatMessageView.setText("");
			if (i == -1) {// event_user
				nickNameView.setText("个人动态");

				headView.setImageResource(R.drawable.msg_list_friends_notice_icon);
				lastChatTimeView.setText("");
				if (data.event.userNotReadMessage) {
					lastChatTimeView.setVisibility(View.VISIBLE);
					lastChatTimeView.setBackgroundResource(R.drawable.noread_message);
					FrameLayout.LayoutParams layoutParams = (LayoutParams) lastChatTimeView.getLayoutParams();
					layoutParams.width = 30;
					layoutParams.height = 30;
				} else {
					lastChatTimeView.setVisibility(View.GONE);
				}

				String content = "";
				try {
					Friend friend;
					String nickName = "";
					String key = data.event.userEvents.get(data.event.userEvents.size() - 1);
					EventMessage event = data.event.userEventsMap.get(key);
					if ("relation_newfriend".equals(event.type)) {
						friend = data.relationship.friendsMap.get(event.phone);
						if (event.content != null) {
							content = event.content;
						}
						if (friend != null) {
							nickName = friend.nickName;
						} else {
							nickName = event.phone;
						}
						content = "【" + nickName + "】  请求加你为好友!验证信息:" + content;
					} else if ("relation_addfriend".equals(event.type)) {
						friend = data.relationship.friendsMap.get(event.phoneTo);
						if (event.content != null) {
							content = event.content;
						}
						if (friend != null) {
							nickName = friend.nickName;
						} else {
							nickName = event.phone;
						}
						content = "您请求加" + nickName + "为好友!验证信息:" + content;
					} else if ("account_dataupdate".equals(event.type)) {
						content = "更新个人资料";
					}
				} catch (JsonSyntaxException e) {
					e.printStackTrace();
					content = "";
				}
				lastChatMessageView.setText(content);
			} else if (i == -2) {// event_group
				nickNameView.setText("群组动态");

				headView.setImageResource(R.drawable.msg_list_group_notice_icon);
				lastChatTimeView.setText("");
				if (data.event.groupNotReadMessage) {
					lastChatTimeView.setVisibility(View.VISIBLE);
					lastChatTimeView.setBackgroundResource(R.drawable.noread_message);
					FrameLayout.LayoutParams layoutParams = (LayoutParams) lastChatTimeView.getLayoutParams();
					layoutParams.width = 30;
					layoutParams.height = 30;
				} else {
					lastChatTimeView.setVisibility(View.GONE);
				}
				String content = "";
				try {
					String key = data.event.groupEvents.get(data.event.groupEvents.size() - 1);
					EventMessage event = data.event.groupEventsMap.get(key);
					if (event == null) {
						content = "";
					} else {
						String nickName = event.phone;
						Friend friend = data.relationship.friendsMap.get(nickName);
						if (friend != null) {
							nickName = friend.nickName;
							if (friend.phone.equals(data.userInformation.currentUser.phone)) {
								nickName = "您";
							}
						}
						final Group group = data.relationship.groupsMap.get(event.gid + "");
						String groupName = event.gid;
						if (group != null) {
							groupName = group.name;
						}
						String contentType = event.type;
						if ("group_addmembers".equals(contentType)) {
							content = "【" + nickName + "】 邀请了" + event.content + "个好友到 【" + groupName + "】 群组中.";
						} else if ("group_removemembers".equals(contentType)) {
							content = "【" + nickName + "】 从【" + groupName + "】 移除了" + event.content + "个好友.";
						} else if ("group_dataupdate".equals(contentType)) {
							content = "【" + nickName + "】 更新了 【" + groupName + "】 的资料信息.";
						} else if ("group_create".equals(contentType)) {
							content = "【" + nickName + "】创建了新的群组:【" + groupName + "】.";
						} else if ("group_addme".equals(contentType)) {
							content = "【" + nickName + "】把你从添加到群组：【" + groupName + "】.";
						} else if ("group_removeme".equals(contentType)) {
							content = "【" + nickName + "】退出了【" + groupName + "】群组.";
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
					content = "";
				}
				lastChatMessageView.setText(content);
			} else {
				data = parser.check();
				fileHandlers.getHeadImage(fileName, headView, options);
				if ("text".equals(message.contentType)) {
					lastChatMessageView.setText(message.content);
				} else if ("image".equals(message.contentType)) {
					lastChatMessageView.setText("[图片]");
				} else if ("voice".equals(message.contentType)) {
					lastChatMessageView.setText("[声音]");
				} else if ("share".equals(message.contentType)) {
					lastChatMessageView.setText("[分享]");
				}
				lastChatTimeView.setText(DateUtil.getChatMessageListTime(Long.valueOf(message.time)));
				String sendType = message.sendType;
				this.groupIconView.setVisibility(View.GONE);
				if ("point".equals(sendType)) {
					if (this.groupIconView.getVisibility() == View.VISIBLE) {
						this.groupIconView.setVisibility(View.GONE);
					}
					if (data.relationship.friendsMap.get(message.phone) == null) {
						nickNameView.setText(message.nickName);
						notReadNumberView.setVisibility(View.GONE);
					} else {
						String phone = "";
						if (message.phone.equals(data.userInformation.currentUser.phone)) {
							phone = (String) gson.fromJson(message.phoneto, List.class).get(0);
						} else {
							phone = message.phone;
						}
						nickNameView.setText(data.relationship.friendsMap.get(phone).nickName);
						int notReadMessagesCount;
						if ((notReadMessagesCount = data.relationship.friendsMap.get(message.phone).notReadMessagesCount) == 0) {
							notReadNumberView.setVisibility(View.GONE);
						} else {
							notReadNumberView.setVisibility(View.VISIBLE);
							notReadNumberView.setText(String.valueOf(notReadMessagesCount));
						}
					}
				} else if ("group".equals(sendType)) {
					if (this.groupIconView.getVisibility() == View.GONE) {
						this.groupIconView.setVisibility(View.VISIBLE);
					}
					if (data.relationship.groupsMap.get(message.gid) == null) {
						nickNameView.setText(message.nickName);
						notReadNumberView.setVisibility(View.GONE);
					} else {
						nickNameView.setText(data.relationship.groupsMap.get(message.gid).name);
						int notReadMessagesCount;
						if ((notReadMessagesCount = data.relationship.groupsMap.get(message.gid).notReadMessagesCount) == 0) {
							notReadNumberView.setVisibility(View.GONE);
						} else {
							notReadNumberView.setVisibility(View.VISIBLE);
							notReadNumberView.setText(String.valueOf(notReadMessagesCount));
						}
					}
				}
			}
		}
	}
}
