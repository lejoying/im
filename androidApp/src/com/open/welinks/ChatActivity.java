package com.open.welinks;

import com.open.welinks.controller.ChatController;
import com.open.welinks.view.ChatView;
import com.open.welinks.view.ViewManage;

import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.MotionEvent;

public class ChatActivity extends Activity {

	public String tag = "ChatActivity";

	public ChatView chatView;
	public ChatController chatController;
	public Activity thisActivity;
	public Context context;

	public ViewManage viewManage = ViewManage.getInstance();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		linkViewController();
	}

	private void linkViewController() {
		thisActivity = this;
		context = this;
		chatView = new ChatView(this);
		chatController = new ChatController(this);

		chatView.thisController = chatController;
		chatController.thisView = chatView;

		chatView.initViews();
		viewManage.chatView = chatView;

		chatController.initializeListeners();
		chatController.onCreate();
		chatController.bindEvent();

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		chatController.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	protected void onResume() {
		super.onResume();
		chatController.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
		chatController.onPause();
	}

	@Override
	protected void onDestroy() {
		chatController.onDestroy();
		super.onDestroy();
	}

	@Override
	public void finish() {
		chatController.finish();
		super.finish();
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		return chatController.onTouchEvent(event);
	}

	@Override
	public void onBackPressed() {
		chatController.onBackPressed();
		super.onBackPressed();
	}
}
