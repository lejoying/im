package com.open.welinks;

import com.open.welinks.controller.NewChatController;
import com.open.welinks.view.NewChatView;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;

public class NewChatActivity extends Activity {

	public NewChatView thisView;
	public NewChatController thisController;
	public NewChatActivity thisActivity;
	public LayoutInflater mInflater;
	public Bundle savedInstanceState;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.thisActivity = this;
		this.mInflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.savedInstanceState = savedInstanceState;
		this.thisView = new NewChatView(thisActivity);
		this.thisController = new NewChatController(thisActivity);

		this.thisView.thisController = this.thisController;
		this.thisController.thisView = this.thisView;

		thisController.viewManage.newChatView = thisView;

		this.thisController.onCreate();
		this.thisView.initViews();
		this.thisController.initData();
		this.thisView.fillData();
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
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		return super.onKeyDown(keyCode, event);
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

}
