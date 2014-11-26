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

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.mInflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.thisActivity = this;
		this.thisView = new NewChatView(thisActivity);
		this.thisController = new NewChatController(thisActivity);

		this.thisView.thisController = this.thisController;
		this.thisController.thisView = this.thisView;

		this.thisController.onCreate();
		this.thisView.initViews();
		this.thisController.initListeners();
	}

	@Override
	protected void onResume() {
		super.onResume();
		this.thisController.onResume();
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
	protected void onDestroy() {
		this.thisController.onDestroy();
		super.onDestroy();
	}

}
