package com.open.welinks.controller;

import android.content.Intent;
import android.sax.StartElementListener;
import android.view.View;
import android.view.View.OnClickListener;

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

	public MainController mainController;

	public MessagesSubController(MainController mainController) {
		this.mainController = mainController;
	}

	public void initializeListeners() {

		mOnClickListener = new OnClickListener() {

			@Override
			public void onClick(View view) {
				Message message = null;
				if ((message = (Message) view.getTag(R.id.chat_content)) != null) {
					Intent intent = new Intent(thisView.mainView.thisActivity,
							ChatActivity.class);
					intent.putExtra("id", message.phone);
					intent.putExtra("type", message.sendType);
					thisView.mainView.thisActivity.startActivity(intent);
				} else {

				}
			}
		};
	}

	public void bindEvent() {
	}

}
