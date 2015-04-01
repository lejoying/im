package com.open.welinks;

import java.util.ArrayList;

import com.open.lib.MyLog;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class NewMainActivity extends Activity {

	public String tag = "NewMainActivity";
	public MyLog log = new MyLog(tag, true);

	public ListView listView;

	public LayoutInflater mInflater;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initViews();
		initData();
	}

	public ArrayList<String> menus;

	@SuppressWarnings("serial")
	private void initData() {
		menus = new ArrayList<String>() {
			{
				add("菜单1");
				add("菜单2");
				add("菜单3");
				add("菜单4");
				add("菜单5");
				add("菜单6");
				add("菜单7");
			}
		};
		MenuAdapter adapter = new MenuAdapter();
		this.listView.setAdapter(adapter);
	}

	FrameLayout content_frame;

	DisplayMetrics displayMetrics;

	private void initViews() {
		displayMetrics = new DisplayMetrics();
		this.getWindowManager().getDefaultDisplay().getMetrics(this.displayMetrics);
		this.mInflater = this.getLayoutInflater();
		this.setContentView(R.layout.activity_new_main);
		this.listView = (ListView) this.findViewById(R.id.left_drawer);
		this.content_frame = (FrameLayout) this.findViewById(R.id.content_frame);
		RelativeLayout layout = new RelativeLayout(this);
		LayoutParams layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		TextView textView = new TextView(this);
		textView.setText("1234567890");
		layout.addView(textView);
		layout.setBackgroundColor(Color.parseColor("#ffffff"));
		this.content_frame.addView(layout, layoutParams);
		this.listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

			}
		});
		DrawerLayout mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		mDrawerLayout.setScrimColor(Color.TRANSPARENT);
		ActionBarDrawerToggle mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.drawable.ic_launcher, R.string.app_name, R.string.action_settings) {

			public void onDrawerClosed(View view) {
				log.e("onDrawerClosed");
				content_frame.setTranslationX(0);
				invalidateOptionsMenu();
			}

			public void onDrawerOpened(View drawerView) {
				log.e("onDrawerOpened");
				content_frame.setTranslationX(150 * displayMetrics.density);
				invalidateOptionsMenu();
			}

			@Override
			public void onDrawerSlide(View drawerView, float slideOffset) {
				super.onDrawerSlide(drawerView, slideOffset);
				content_frame.setTranslationX(150 * displayMetrics.density * slideOffset);
			}
		};
		mDrawerLayout.setDrawerListener(mDrawerToggle);
	}

	public class MenuAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return menus.size();
		}

		@Override
		public Object getItem(int position) {
			return menus.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			MenuHolder holder;
			if (convertView == null) {
				holder = new MenuHolder();
				convertView = mInflater.inflate(R.layout.view_menu_item, null);
				holder.nameView = (TextView) convertView.findViewById(R.id.name);
				convertView.setTag(holder);
			} else {
				holder = (MenuHolder) convertView.getTag();
			}
			holder.nameView.setText(menus.get(position));
			return convertView;
		}

		public class MenuHolder {
			public TextView nameView;
		}
	}
}
