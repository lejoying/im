package com.open.welinks.view;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.open.welinks.R;
import com.open.welinks.controller.InviteFriendController;
import com.open.welinks.model.Data;

public class InviteFriendView {

	public Data data = Data.getInstance();
	public String tag = "InviteFriendView";

	public Context context;
	public InviteFriendView thisView;
	public InviteFriendController thisController;
	public Activity thisActivity;

	public LayoutInflater mInflater;

	public LinearLayout backView;
	public TextView confirmButtonView;

	public InviteFriendView(Activity thisActivity) {
		this.thisActivity = thisActivity;
		this.context = thisActivity;
		this.thisView = this;
	}

	public void initView() {
		thisActivity.setContentView(R.layout.activity_group_invite_selected_friend);

		backView = (LinearLayout) thisActivity.findViewById(R.id.backView);
		confirmButtonView = (TextView) thisActivity.findViewById(R.id.confirmButton);

		mInflater = thisActivity.getLayoutInflater();
	}
}
