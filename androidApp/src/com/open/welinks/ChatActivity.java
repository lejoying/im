package com.open.welinks;

import com.open.welinks.controller.ChatController;
import com.open.welinks.view.ChatView;

import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.view.Menu;
import android.view.MotionEvent;

public class ChatActivity extends Activity {

	public ChatView chatView;
	public ChatController chatController;
	public Activity thisActivity;
	public Context context;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_chat);
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
		chatController.initializeListeners();
		chatController.bindEvent();

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.chat, menu);
		return true;
	}

	@Override
	protected void onResume() {
		chatController.onResume();
	}

	@Override
	protected void onPause() {
		chatController.onPause();
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		return chatController.onTouchEvent(event);
	}
}
