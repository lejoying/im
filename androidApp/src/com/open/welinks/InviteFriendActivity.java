package com.open.welinks;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import com.open.welinks.controller.InviteFriendController;
import com.open.welinks.model.Data;
import com.open.welinks.view.InviteFriendView;

public class InviteFriendActivity extends Activity {

	public Data data = Data.getInstance();
	public String tag = "InviteFriendActivity";

	public Context context;
	public InviteFriendView thisView;
	public InviteFriendController thisController;
	public Activity thisActivity;

	public static int INVITA_FRIEND_GROUP = 0x01;// Invite friends to add to the group
	public static int RECOMMEND_FRIEND_GROUP = 0x02;// Recommend group to friends
	public static int FORWARD_MESSAGE_FRIEND = 0x03;// Forwarding messages to friends

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		linkViewController();
	}

	public void linkViewController() {
		this.thisActivity = this;
		this.context = this;
		this.thisView = new InviteFriendView(thisActivity);
		this.thisController = new InviteFriendController(thisActivity);
		this.thisView.thisController = this.thisController;
		this.thisController.thisView = this.thisView;

		thisView.initView();
		// thisController.onCreate();
		thisController.initializeListeners();
		thisController.bindEvent();
	}
}
