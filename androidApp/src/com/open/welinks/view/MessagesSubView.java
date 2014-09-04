package com.open.welinks.view;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.open.lib.TouchView;
import com.open.lib.viewbody.ListBody1;
import com.open.lib.viewbody.ListBody1.MyListItemBody;

import com.open.welinks.R;
import com.open.welinks.controller.MessagesSubController;
import com.open.welinks.model.Data;
import com.open.welinks.model.Data.Messages.Message;
import com.open.welinks.utils.DateUtil;
import com.open.welinks.utils.MCImageUtils;

public class MessagesSubView {

	public Data data = Data.getInstance();

	public String tag = "MessagesSubView";

	public DisplayMetrics displayMetrics;

	public TouchView messagesView;

	public RelativeLayout noMessagesStatusView;

	public ListBody1 messageListBody;

	public MainView mainView;

	public MessagesSubController thisController;

	public MessagesSubView(MainView mainView) {
		this.mainView = mainView;

	}

	public void initViews() {
		this.messagesView = mainView.messagesView;
		this.displayMetrics = mainView.displayMetrics;

		messageListBody = new ListBody1();
		messageListBody.initialize(displayMetrics, messagesView);

		noMessagesStatusView = (RelativeLayout) messagesView.findViewById(R.id.NoMessagesStatus);

	}

	public void showMessages() {

		List<String> messagesOrder = data.messages.messagesOrder;
		Map<String, ArrayList<Message>> friendMessageMap = data.messages.friendMessageMap;
		Map<String, ArrayList<Message>> groupMessageMap = data.messages.groupMessageMap;

		this.messageListBody.listItemsSequence.clear();
		if (messagesOrder.size() > 0) {
			noMessagesStatusView.setVisibility(View.GONE);
		}
		for (int i = 0; i < messagesOrder.size(); i++) {
			String key = messagesOrder.get(i);
			Message message = null;
			if (key.indexOf("p") == 0) {
				message = friendMessageMap.get(key).get(0);
			} else if (key.indexOf("g") == 0) {
				message = groupMessageMap.get(key).get(0);
			}
			MessageBody messageBody = null;
			messageBody = new MessageBody(this.messageListBody);
			messageBody.initialize();
			messageBody.setContent(message);

			this.messageListBody.listItemsSequence.add("message#" + message.phone + "_" + message.time);
			this.messageListBody.listItemBodiesMap.put("message#" + message.phone + "_" + message.time, messageBody);

			TouchView.LayoutParams layoutParams = new TouchView.LayoutParams((int) (displayMetrics.widthPixels - displayMetrics.density * 20), (int) (70 * displayMetrics.density));
			messageBody.y = this.messageListBody.height;
			messageBody.cardView.setY(messageBody.y);
			messageBody.cardView.setX(0);
			messageBody.cardView.setTag(R.id.chat_content, message);
			messageBody.cardView.setOnClickListener(thisController.mOnClickListener);
			this.messageListBody.height = this.messageListBody.height + 80 * displayMetrics.density;
			this.messageListBody.containerView.addView(messageBody.cardView, layoutParams);
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

		public void setContent(Message message) {
			Resources resources = mainView.thisActivity.getResources();
			Bitmap bitmap = BitmapFactory.decodeResource(resources, R.drawable.face_man);
			bitmap = MCImageUtils.getCircleBitmap(bitmap, true, 5, Color.WHITE);
			headView.setImageBitmap(bitmap);
			nickNameView.setText(message.nickName);
			lastChatMessageView.setText(message.content);
			lastChatTimeView.setText(DateUtil.getChatMessageListTime(Long.valueOf(message.time)));
			notReadNumberView.setText("1");
		}
	}

}
