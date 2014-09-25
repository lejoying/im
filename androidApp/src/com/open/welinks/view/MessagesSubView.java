package com.open.welinks.view;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.open.lib.MyLog;
import com.open.lib.TouchView;
import com.open.lib.viewbody.ListBody1;
import com.open.lib.viewbody.ListBody1.MyListItemBody;
import com.open.welinks.R;
import com.open.welinks.controller.MessagesSubController;
import com.open.welinks.model.Data;
import com.open.welinks.model.Data.Messages.Message;
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
			}
			MessageBody messageBody = null;
			if (this.messageListBody.listItemBodiesMap.get(key2) != null) {
				if ("p".equals(type)) {
					messageBody = (MessageBody) this.messageListBody.listItemBodiesMap.get(key2);

				} else if ("g".equals(type)) {
					messageBody = (MessageBody) this.messageListBody.listItemBodiesMap.get(key2);
				}
				messageBody.setContent(message, fileName);
			} else {
				messageBody = new MessageBody(this.messageListBody);
				messageBody.initialize();
				messageBody.setContent(message, fileName);
				this.messageListBody.listItemBodiesMap.put(key2, messageBody);
			}
			this.messageListBody.listItemsSequence.add(key2);
			TouchView.LayoutParams layoutParams = new TouchView.LayoutParams((int) (displayMetrics.widthPixels - displayMetrics.density * 20), (int) (70 * displayMetrics.density));
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

		public View initialize() {
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
