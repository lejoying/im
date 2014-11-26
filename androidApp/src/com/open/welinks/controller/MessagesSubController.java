package com.open.welinks.controller;

import android.content.Intent;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;

import com.google.gson.Gson;
import com.open.lib.MyLog;
import com.open.welinks.ChatActivity;
import com.open.welinks.DynamicListActivity;
import com.open.welinks.NewChatActivity;
import com.open.welinks.R;
import com.open.welinks.customListener.MyOnClickListener;
import com.open.welinks.model.Data;
import com.open.welinks.model.Data.Messages.Message;
import com.open.welinks.view.MessagesSubView;

public class MessagesSubController {

	public Data data = Data.getInstance();
	public Gson gson = new Gson();
	public String tag = "MessagesSubController";
	public MyLog log = new MyLog(tag, true);

	public MessagesSubView thisView;
	public MessagesSubController thisconController;

	public MyOnClickListener mOnClickListener;
	public OnTouchListener mOnTouchListener;

	public MainController mainController;

	public Handler handler = new Handler();

	public Message message;

	public View onTouchDownView;

	public boolean isTouchDown = false;

	public MessagesSubController(MainController mainController) {
		this.mainController = mainController;
		thisconController = this;
	}

	public void initializeListeners() {

		mOnClickListener = new MyOnClickListener() {

			public void onClickEffective(View view) {
				if (view.getTag(R.id.tag_class) != null) {
					String view_class = (String) view.getTag(R.id.tag_class);
					if (view_class.equals("message_view")) {
						String key = (String) view.getTag(R.id.tag_first);
						if ("event_user".equals(key)) {
							Intent intent = new Intent(mainController.thisActivity, DynamicListActivity.class);
							intent.putExtra("type", 3);
							mainController.thisActivity.startActivity(intent);
						} else if ("event_group".equals(key)) {
							Intent intent = new Intent(mainController.thisActivity, DynamicListActivity.class);
							intent.putExtra("type", 2);
							mainController.thisActivity.startActivity(intent);
						} else {

							String type = key.substring(0, 1);
							String value = key.substring(1);
							if ("p".equals(type)) {
								type = "point";
							} else if ("g".equals(type)) {
								type = "group";
							}
							Intent intent = new Intent(thisView.mainView.thisActivity, ChatActivity.class);
							intent.putExtra("id", value);
							intent.putExtra("type", type);
							thisView.mainView.thisActivity.startActivityForResult(intent, R.id.tag_second);
						}
						log.e("key:" + key);
					}
				}
			}
		};
		mOnTouchListener = new OnTouchListener() {

			@Override
			public boolean onTouch(View view, MotionEvent event) {
				int action = event.getAction();
				if (action == MotionEvent.ACTION_DOWN) {
					if (isTouchDown) {
						return false;
					}
					String view_class = (String) view.getTag(R.id.tag_class);
					if (view_class.equals("message_view")) {
						onTouchDownView = view;
						isTouchDown = true;
						try {
							((ViewGroup) onTouchDownView).getChildAt(1).setVisibility(View.VISIBLE);
						} catch (Exception e) {
						}
					}
					Log.e(tag, "ACTION_DOWN---" + view_class);
				}
				return false;
			}
		};
	}

	public void bindEvent() {
		// mainController.thisView.userTopbarNameParentView.setOnClickListener(mOnClickListener);
	}

	public void addMessageToSubView(Message message) {
		if ("point".equals(message.sendType)) {
			String key = "p" + message.phone;
			if (data.messages.messagesOrder.contains(key)) {
				data.messages.messagesOrder.remove(key);
			}
			data.messages.messagesOrder.add(0, key);
		} else if ("group".equals(message.sendType)) {
			String key = "g" + message.gid;
			if (data.messages.messagesOrder.contains(key)) {
				data.messages.messagesOrder.remove(key);
			}
			data.messages.messagesOrder.add(0, key);
		}
		handler.post(new Runnable() {

			@Override
			public void run() {
				thisView.showMessagesSequence();
			}
		});

	}

	public void onActivityResult(int requestCode, int resultCode, Intent data2) {
		if (requestCode == R.id.tag_second) {
			// thisView.showMessagesSequence();
		}
	}

	public void onSingleTapUp(MotionEvent event) {

		if (onTouchDownView != null) {
			String view_class = (String) onTouchDownView.getTag(R.id.tag_class);
			if (view_class.equals("message_view")) {
				try {
					((ViewGroup) onTouchDownView).getChildAt(1).setVisibility(View.INVISIBLE);
				} catch (Exception e) {
				}
				onTouchDownView.performClick();
			}
			onTouchDownView = null;
		}
		isTouchDown = false;
	}

	public void onScroll() {
		try {
			((ViewGroup) onTouchDownView).getChildAt(1).setVisibility(View.INVISIBLE);
		} catch (Exception e) {
		}
		onTouchDownView = null;
	}
}
