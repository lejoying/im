package com.lejoying.mc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.lejoying.adapter.CircleAdapter;
import com.lejoying.mcutils.Friend;

public class BusinessCardActivity extends Activity {

	// DEFINITION view
	private LinearLayout ll_content;
	private RelativeLayout rl_control;

	// DEFINITION object
	private CircleAdapter circleAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.businesscard_new);
		initView();
	}

	@SuppressLint("NewApi")
	public void initView() {
		// INIT view
		ll_content = (LinearLayout) findViewById(R.id.ll_content);
		rl_control = (RelativeLayout) findViewById(R.id.rl_control);

		// INIT object
		List<Friend> friends1 = new ArrayList<Friend>();
		List<Friend> friends2 = new ArrayList<Friend>();

		// TODO
		Map<String, List<Friend>> circlefriends = new HashMap<String, List<Friend>>();
		for (int i = 0; i < 100; i++) {
			Friend friend = new Friend();
			friend.setNickName("ºÃÓÑ" + i);
			if (i / 50 < 1) {
				friends1.add(friend);
			} else {
				friends2.add(friend);
			}
		}
		circlefriends.put("ÃÜÓÑÈ¦1", friends1);
		circlefriends.put("ÃÜÓÑÈ¦2", friends2);
		circlefriends.put("ÃÜÓÑÈ¦3", friends1);
		circlefriends.put("ÃÜÓÑÈ¦4", friends2);
		circlefriends.put("ÃÜÓÑÈ¦5", friends1);

		circleAdapter = new CircleAdapter(circlefriends, ll_content,
				rl_control, this);

		circleAdapter.createView();
	}

	public void back(View v) {
		if (circleAdapter.getEditMode()) {
			circleAdapter.exitEdit();
		} else {
			finish();
		}
	}

	public void rightMenu(View v) {

	}

}
