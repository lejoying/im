package com.lejoying.mc;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lejoying.mcutils.CircleMenu;
import com.lejoying.mcutils.ImageTools;
import com.lejoying.mcutils.MenuEntity;

public class MessagesActivity extends Activity {

	private ListView lv_messages;

	private LayoutInflater inflater;

	private Bitmap bm;

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

		CircleMenu circleMenu = new CircleMenu(this);
		List<MenuEntity> list = new ArrayList<MenuEntity>();

		list.add(new MenuEntity(0, "我的名片"));
		list.add(new MenuEntity(0, "密友圈"));
		list.add(new MenuEntity(0, "社区服务"));
		list.add(new MenuEntity(0, "更多"));
		list.add(new MenuEntity(0, "订单"));
		list.add(new MenuEntity(0, "资金账户"));

		circleMenu.showMenu(CircleMenu.SHOW_TOP, list);
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
