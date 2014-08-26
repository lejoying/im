package com.open.welinks.controller;

import android.app.Activity;
import android.content.Context;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;

import com.open.welinks.model.Data;
import com.open.welinks.view.ChatView;

public class ChatController {

	public String tag = "ChatController";

	public ChatController thisController;
	public Activity thisActivity;
	public Context context;
	public ChatView thisView;

	public OnClickListener mOnClickListener;
	public OnTouchListener onTouchListener;

	public Data data = Data.getInstance();

	public String type, id;

	public ChatController(Activity thisActivity) {
		this.thisActivity = thisActivity;
		context = thisActivity;
		thisController = this;
	}

	public void onCreate() {
		String id = thisActivity.getIntent().getStringExtra("id");
		if (id != null && !"".equals(id)) {
			this.id = id;
		}
		String type = thisActivity.getIntent().getStringExtra("type");
		if (type != null && !"".equals(type)) {
			this.type = type;
		}
		thisView.showChatViews();
	}

	public void initializeListeners() {
		mOnClickListener = new OnClickListener() {

			@Override
			public void onClick(View view) {
				if (view.equals(thisView.backview)) {

				} else if (view.equals(thisView.infomation)) {

				} else if (view.equals(thisView.send)) {

				} else if (view.equals(thisView.send)) {

				} else if (view.equals(thisView.selectedface)) {

				} else if (view.equals(thisView.selectpicture)) {

				} else if (view.equals(thisView.makeaudio)) {

				} else if (view.equals(thisView.more_selected)) {

				}

			}
		};
	}

	public void bindEvent() {
		thisView.backview.setOnClickListener(mOnClickListener);
		thisView.infomation.setOnClickListener(mOnClickListener);
		thisView.send.setOnClickListener(mOnClickListener);
		thisView.more.setOnClickListener(mOnClickListener);
		thisView.selectedface.setOnClickListener(mOnClickListener);
		thisView.selectpicture.setOnClickListener(mOnClickListener);
		thisView.makeaudio.setOnClickListener(mOnClickListener);
		thisView.more_selected.setOnClickListener(mOnClickListener);

	}

	public boolean onTouchEvent(MotionEvent event) {
		return false;

	}

	public void onResume() {
		// TODO Auto-generated method stub

	}

	public void onPause() {
		// TODO Auto-generated method stub

	}
}
