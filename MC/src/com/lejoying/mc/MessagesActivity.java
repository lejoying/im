package com.lejoying.mc;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lejoying.listener.CircleMenuItemClickListener;
import com.lejoying.mcutils.CircleMenu;
import com.lejoying.mcutils.ImageTools;
import com.lejoying.mcutils.MenuEntity;
import com.lejoying.view.CircleMenuView;

public class MessagesActivity extends Activity {

	private ListView lv_messages;

	private LayoutInflater inflater;

	private Bitmap bm;

	private CircleMenu circleMenu;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.messages);
		initView();
	}

	public void initView() {
		lv_messages = (ListView) findViewById(R.id.lv_messages);

		inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		bm = ImageTools.getCircleBitmap(BitmapFactory.decodeResource(
				getResources(), R.drawable.xiaohei), true, 4, Color.rgb(255,
				255, 255));

		lv_messages.setAdapter(new MessagesListAdapter());

		circleMenu = new CircleMenu(this);
		List<MenuEntity> list = new ArrayList<MenuEntity>();

		list.add(new MenuEntity(R.drawable.test_menu_item1, "扫一扫"));
		list.add(new MenuEntity(R.drawable.test_menu_item2, "密友圈"));
		list.add(new MenuEntity(R.drawable.test_menu_item3, "分享"));
		list.add(new MenuEntity(R.drawable.test_menu_item4,
				CircleMenu.CIRCLE_MORE));

		circleMenu.showMenu(CircleMenu.SHOW_TOP, list, false);

		circleMenu
				.setCircleMenuItemClickListener(new CircleMenuItemClickListener() {

					@Override
					public void onItemClick(int item, ImageView icon,
							TextView text) {
						if (item == 1) {
							Intent intent = new Intent(MessagesActivity.this,
									ScanQRCodeActivity.class);
							startActivity(intent);
						} else if (item == 2) {
							Intent intent = new Intent(MessagesActivity.this,
									FriendsActivity.class);
							startActivity(intent);
						}
					}
				});
		lv_messages.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				System.out.println("xxxxxxx");
				Intent intent = new Intent(MessagesActivity.this,
						ChatActivity.class);
				startActivity(intent);
			}
		});
	}

	class MessagesListAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return 10;
		}

		@Override
		public Object getItem(int arg0) {
			// TODO Auto-generated method stub
			return arg0;
		}

		@Override
		public long getItemId(int arg0) {
			// TODO Auto-generated method stub
			return arg0;
		}

		@Override
		public View getView(int arg0, View arg1, ViewGroup arg2) {
			RelativeLayout rl = (RelativeLayout) inflater.inflate(
					R.layout.messages_item, null);
			ImageView iv_head = (ImageView) rl.findViewById(R.id.iv_head);
			TextView tv_nickname = (TextView) rl.findViewById(R.id.tv_nickname);
			TextView tv_lastchat = (TextView) rl.findViewById(R.id.tv_lastchat);

			iv_head.setImageBitmap(bm);
			tv_nickname.setText("用户" + arg0);
			tv_lastchat.setText("你好");
			return rl;
		}

	}
}
