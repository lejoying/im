package com.lejoying.mc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lejoying.adapter.CircleAdapter;
import com.lejoying.listener.CircleMenuItemClickListener;
import com.lejoying.mcutils.CircleMenu;
import com.lejoying.mcutils.Friend;
import com.lejoying.mcutils.MenuEntity;

public class FriendsActivity extends Activity {
	// DEFINITION view
	private LinearLayout ll_content;
	private RelativeLayout rl_control;

	// DEFINITION object
	private CircleAdapter circleAdapter;
	private CircleMenu cm;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.friends);
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
			friend.setNickName("好友" + i);
			if (i / 50 < 1) {
				friends1.add(friend);
			} else {
				friends2.add(friend);
			}
		}
		circlefriends.put("密友圈1", friends1);
		circlefriends.put("密友圈2", friends2);
		circlefriends.put("密友圈3", friends1);
		circlefriends.put("密友圈4", friends2);
		circlefriends.put("密友圈5", friends1);

		circleAdapter = new CircleAdapter(circlefriends, ll_content,
				rl_control, this);

		circleAdapter.createView();

		cm = new CircleMenu(this);
		List<MenuEntity> list = new ArrayList<MenuEntity>();
		list.add(new MenuEntity(0, "扫一扫"));
		list.add(new MenuEntity(0, "消息"));
		list.add(new MenuEntity(0, "分享"));
		list.add(new MenuEntity(0, "更多"));
		cm.showMenu(CircleMenu.SHOW_TOP, list, false);
		List<MenuEntity> list2 = new ArrayList<MenuEntity>();
		list2.add(new MenuEntity(0, "扫一扫"));
		list2.add(new MenuEntity(0, "消息"));
		list2.add(new MenuEntity(0, "分享"));
		list2.add(new MenuEntity(0, "分享"));
		list2.add(new MenuEntity(0, "分享"));
		list2.add(new MenuEntity(0, "更多"));
		cm.addMore(list2);

		cm.setCircleMenuItemClickListener(new CircleMenuItemClickListener() {
			@Override
			public void onItemClick(int item, ImageView icon, TextView text) {
				if (item == 1) {
					Intent intent = new Intent(FriendsActivity.this,
							ScanQRCodeActivity.class);
					startActivity(intent);
				} else if (item == 2) {
					Intent intent = new Intent(FriendsActivity.this,
							MessagesActivity.class);
					startActivity(intent);
				} else if (item == 3) {

				} else if (item == 4) {
					cm.showNext();
				}
			}
		});
	}

	@Override
	public void finish() {
		if (circleAdapter.getEditMode()) {
			circleAdapter.exitEdit();
		} else {
			super.finish();
		}
	}
}
