package com.open.welinks.view;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
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

	public String tag = "MessagesSubView";

	public DisplayMetrics displayMetrics;

	public TouchView messagesView;

	public RelativeLayout noMessagesStatusView;

	public ListBody1 messageListBody;

	public MainView mainView;

	public MessagesSubController thisController;

	public List<String> messagesKeepOnlyOne;

	public boolean inited = false;
	public Parser parser = Parser.getInstance();

	public FileHandlers fileHandlers = FileHandlers.getInstance();
	public DisplayImageOptions options;

	public MessagesSubView(MainView mainView) {
		this.mainView = mainView;

	}

	public void initViews() {
		this.messagesView = mainView.messagesView;
		this.displayMetrics = mainView.displayMetrics;

		messageListBody = new ListBody1();
		messageListBody.initialize(displayMetrics, messagesView);

		messagesKeepOnlyOne = new ArrayList<String>();

		noMessagesStatusView = (RelativeLayout) messagesView.findViewById(R.id.NoMessagesStatus);
		options = new DisplayImageOptions.Builder().showImageOnLoading(R.drawable.ic_stub).showImageForEmptyUri(R.drawable.ic_empty).showImageOnFail(R.drawable.ic_error).cacheInMemory(true).cacheOnDisk(true).considerExifParams(true).displayer(new RoundedBitmapDisplayer(50)).build();

	}

	public void onResume() {
		if (inited) {
			showMessages();
		} else {
			inited = true;
		}
	}

	public void onDestroy() {
		inited = false;
	}

	public void showMessages() {
		data = parser.check();
		if (data.messages == null) {
			return;
		}
		List<String> messagesOrder = data.messages.messagesOrder;
		Map<String, ArrayList<Message>> friendMessageMap = data.messages.friendMessageMap;
		Map<String, ArrayList<Message>> groupMessageMap = data.messages.groupMessageMap;

		this.messageListBody.containerView.removeAllViews();
		this.messageListBody.listItemsSequence.clear();
		this.messageListBody.listItemBodiesMap.clear();
		this.messageListBody.height = 0;

		messagesKeepOnlyOne.clear();

		if (messagesOrder.size() > 0) {
			noMessagesStatusView.setVisibility(View.GONE);
		}
		for (int i = 0; i < messagesOrder.size(); i++) {
			String key = messagesOrder.get(i);
			Message message = null;
			System.out.println(key + "-----------");
			String fileName = "";
			if (key.indexOf("p") == 0) {
				message = friendMessageMap.get(key).get(friendMessageMap.get(key).size() - 1);
				try {
					fileName = data.relationship.friendsMap.get(message.phone).head;
				} catch (Exception e) {
					fileName = "";
				}
			} else if (key.indexOf("g") == 0) {
				message = groupMessageMap.get(key).get(groupMessageMap.get(key).size() - 1);
				try {
					fileName = data.relationship.groupsMap.get(message.gid).icon;
				} catch (Exception e) {
					fileName = "";
				}
			}

			if (messagesKeepOnlyOne.contains(key)) {
				this.messageListBody.listItemsSequence.remove("message#" + message.phone + "_" + message.time);
				this.messageListBody.listItemsSequence.add("message#" + message.phone + "_" + message.time);

				MessageBody messageBody = (MessageBody) this.messageListBody.listItemBodiesMap.get("message#" + message.phone + "_" + message.time);
				messageBody.setContent(message, fileName);
			} else {
				messagesKeepOnlyOne.add(key);

				MessageBody messageBody = null;
				messageBody = new MessageBody(this.messageListBody);
				messageBody.initialize();
				messageBody.setContent(message, fileName);

				this.messageListBody.listItemsSequence.add("message#" + message.phone + "_" + message.time);
				this.messageListBody.listItemBodiesMap.put("message#" + message.phone + "_" + message.time, messageBody);

				TouchView.LayoutParams layoutParams = new TouchView.LayoutParams((int) (displayMetrics.widthPixels - displayMetrics.density * 20), (int) (70 * displayMetrics.density));
				messageBody.y = this.messageListBody.height;
				messageBody.cardView.setY(messageBody.y);
				messageBody.cardView.setX(0);
				messageBody.cardView.setTag(R.id.tag_first, message);
				messageBody.cardView.setOnTouchListener(thisController.mOnTouchListener);
				this.messageListBody.height = this.messageListBody.height + 80 * displayMetrics.density;
				this.messageListBody.containerView.addView(messageBody.cardView, layoutParams);
			}

		}
		this.messageListBody.containerHeight = (int) (this.displayMetrics.heightPixels - 38 - displayMetrics.density * 88);

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

		public View initialize() {

			this.cardView = mainView.mInflater.inflate(R.layout.chat_message_item, null);
			headView = (ImageView) this.cardView.findViewById(R.id.userHeadView);
			nickNameView = (TextView) this.cardView.findViewById(R.id.tv_nickname);
			lastChatTimeView = (TextView) this.cardView.findViewById(R.id.tv_time);
			lastChatMessageView = (TextView) this.cardView.findViewById(R.id.tv_lastchatcontent);
			notReadNumberView = (TextView) this.cardView.findViewById(R.id.tv_notread);
			super.initialize(cardView);
			return cardView;
		}

		public void setContent(Message message, String fileName) {
			data = parser.check();
			fileHandlers.getHeadImage(fileName, headView, options);
			if ("text".equals(message.contentType)) {
				lastChatMessageView.setText(message.content);
			} else if ("image".equals(message.contentType)) {
				lastChatMessageView.setText("图片");
			} else if ("voice".equals(message.contentType)) {
				lastChatMessageView.setText("声音");
			}
			lastChatTimeView.setText(DateUtil.getChatMessageListTime(Long.valueOf(message.time)));
			String sendType = message.sendType;
			if ("point".equals(sendType)) {
				if (data.relationship.friendsMap.get(message.phone) == null) {
					nickNameView.setText(message.nickName);
					notReadNumberView.setVisibility(View.GONE);
				} else {
					nickNameView.setText(data.relationship.friendsMap.get(message.phone).nickName);
					int notReadMessagesCount;
					if ((notReadMessagesCount = data.relationship.friendsMap.get(message.phone).notReadMessagesCount) == 0) {
						notReadNumberView.setVisibility(View.GONE);
					} else {
						notReadNumberView.setVisibility(View.VISIBLE);
						notReadNumberView.setText(String.valueOf(notReadMessagesCount));
					}
				}
			} else if ("group".equals(sendType)) {
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
