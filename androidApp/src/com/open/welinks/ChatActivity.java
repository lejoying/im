package com.open.welinks;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;

import com.open.welinks.controller.ChatController;
import com.open.welinks.model.TaskManageHolder;
import com.open.welinks.view.ChatView;

public class ChatActivity extends Activity {

	public ChatView thisView;
	public ChatController thisController;
	public ChatActivity thisActivity;
	public LayoutInflater mInflater;
	public TaskManageHolder taskManageHolder = TaskManageHolder.getInstance();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.thisActivity = this;
		this.mInflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.thisView = new ChatView(thisActivity);
		this.thisController = new ChatController(thisActivity);

		this.thisView.thisController = this.thisController;
		this.thisController.thisView = this.thisView;

		taskManageHolder.viewManage.chatView = thisView;

		this.thisController.onCreate();
		this.thisView.initViews();
		this.thisController.initData();
		this.thisView.fillData();

		thisView.locationMapView.onCreate(savedInstanceState);
	}

	@Override
	protected void onResume() {
		super.onResume();
		this.thisController.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
		this.thisController.onPause();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		this.thisController.onSaveInstanceState(outState);
		super.onSaveInstanceState(outState);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		this.thisController.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public void finish() {
		this.thisController.finish();
		super.finish();
	}

	@Override
	protected void onDestroy() {
		this.thisController.onDestroy();
		super.onDestroy();
	}

	@Override
	public void onBackPressed() {
		this.thisController.onBackPressed();
	}

}
