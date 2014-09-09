package com.open.welinks.controller;

import android.content.Intent;
import android.os.Handler;
import android.sax.StartElementListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;

import com.open.welinks.ChatActivity;
import com.open.welinks.R;
import com.open.welinks.model.Data;
import com.open.welinks.model.Data.Messages.Message;
import com.open.welinks.view.MessagesSubView;

public class MessagesSubController {

	public Data data = Data.getInstance();
	public String tag = "MessagesSubController";
	public MessagesSubView thisView;
	public MessagesSubController thisconController;

	public OnClickListener mOnClickListener;
	public OnTouchListener mOnTouchListener;

	public MainController mainController;

	public Handler handler = new Handler();

	public Message message;

	public MessagesSubController(MainController mainController) {
		this.mainController = mainController;
		thisconController = this;
	}

	public void initializeListeners() {

		mOnClickListener = new OnClickListener() {

			@Override
			public void onClick(View view) {

			}
		};
		mOnTouchListener = new OnTouchListener() {

			@Override
			public boolean onTouch(View view, MotionEvent event) {
				Message message = null;
				if ((message = (Message) view.getTag(R.id.tag_first)) != null) {
					thisconController.message = message;
				}
				return false;
			}
		};
	}

	public void bindEvent() {
	}

	public void addMessageToSubView(Message message) {
		if ("point".equals(message.sendType)) {
			data.messages.messagesOrder.add(0, "p" + message.phone);
		} else {
			data.messages.messagesOrder.add(0, "g" + message.gid);
		}
		handler.post(new Runnable() {

			@Override
			public void run() {
				thisView.showMessages();
			}
		});

	}

	public void onSingleTapUp(MotionEvent event) {
		Intent intent = new Intent(thisView.mainView.thisActivity, ChatActivity.class);
		String sendType = message.sendType;
		if ("point".equals(sendType)) {
			intent.putExtra("id", message.phone);
			data.relationship.friendsMap.get(message.phone).notReadMessagesCount = 0;
		} else if ("group".equals(sendType)) {
			intent.putExtra("id", message.gid);
			data.relationship.groupsMap.get(message.gid).notReadMessagesCount = 0;
		}
		intent.putExtra("type", sendType);
		thisView.mainView.thisActivity.startActivity(intent);
	}
}
