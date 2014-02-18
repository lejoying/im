package com.lejoying.mc.fragment;

import java.util.ArrayList;
import java.util.List;

import com.lejoying.mc.R;
import com.lejoying.mc.data.App;
import com.lejoying.mc.data.Circle;
import com.lejoying.mc.data.Group;
import com.lejoying.mc.data.handler.FileHandler.FileResult;
import com.lejoying.mc.view.FriendViewPager;

import android.database.DataSetObserver;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ScrollView;
import android.widget.TextView;

public class GroupFragment extends BaseFragment {

	App app = App.getInstance();

	ScrollView scrollview_group;

	GridView gv_page;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method
		mMCFragmentManager
				.setCircleMenuPageName("群组");
		scrollview_group = (ScrollView) inflater
				.inflate(R.layout.f_group, null);
		gv_page = (GridView) scrollview_group.findViewById(R.id.gv_page);
		gv_page.setAdapter(new MyAdapter(inflater));
		return scrollview_group;
	}

	class MyAdapter extends BaseAdapter {

		List<Group> groups;
		LayoutInflater inflater;

		public MyAdapter(LayoutInflater inflater) {
			// TODO Auto-generated constructor stub

			this.inflater = inflater;

			groups = new ArrayList<Group>();
			Group group1 = new Group();
			group1.gid = 1;
			group1.name = "联谊会";
			Group group2 = new Group();
			group2.gid = 2;
			group2.name = "生产队";
			Group group3 = new Group();
			group3.gid = 3;
			group3.name = "互助组";
			groups.add(group1);
			groups.add(group2);
			groups.add(group3);
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return groups.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return groups.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return groups.get(position).gid;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			if (convertView == null) {
				convertView = inflater.inflate(
						R.layout.f_group_panelitem_gridpage_item, null);
			}

			TextView textview_groupname = (TextView) convertView
					.findViewById(R.id.textview_groupname);
			TextView textview_membercount = (TextView) convertView
					.findViewById(R.id.textview_membercount);
			LinearLayout linearlayout_members = (LinearLayout) convertView
					.findViewById(R.id.linearlayout_members);

			textview_groupname.setText(groups.get(position).name);
			textview_membercount.setText("("
					+ groups.get(position).members.size() + "人)");

			for (int i = 0; i < 5; i++) {
				ImageView iv_head = new ImageView(getActivity());
				iv_head.setImageBitmap(app.fileHandler.defaultHead);
				LayoutParams params = new LayoutParams(40, 40);
				if (i != 0)
					params.setMargins(10, 0, 0, 0);
				iv_head.setLayoutParams(params);
				linearlayout_members.addView(iv_head);
			}

			return convertView;
		}
	}

	@Override
	public void onResume() {
		app.mark = app.groupFragment;
		super.onResume();
	}

	public void generateView() {
		class ItemHolder {
			TextView groupName;
			TextView memberCount;
			LinearLayout members;
		}
		// for (int m = circles.size() - 1; m > -1; m--) {
		// final Circle circle = circles.get(m);
		//
		// View group = app.inflater.inflate(R.layout.f_friend_panel, null);
		//
		// TextView tv_groupname = (TextView) group
		// .findViewById(R.id.tv_broadcast);
		// FriendViewPager vp_content = (FriendViewPager) group
		// .findViewById(R.id.vp_content);
		//
		// tv_groupname.setText(circle.name);
		//
		// PagerAdapter vp_contentAdapter;
		//
		// final List<String> phones = circle.phones;
		// final int pagecount = phones.size() % 6 == 0 ? phones.size() / 6
		// : phones.size() / 6 + 1;
		// final List<View> pageviews = new ArrayList<View>();
		// for (int i = 0; i < pagecount; i++) {
		// final int a = i;
		// BaseAdapter gridpageAdapter = new BaseAdapter() {
		// @Override
		// public View getView(final int position, View convertView,
		// final ViewGroup parent) {
		// ItemHolder itemHolder = null;
		// if (convertView == null) {
		// convertView = app.inflater.inflate(
		// R.layout.f_friend_panelitem_gridpage_item,
		// null);
		// itemHolder = new ItemHolder();
		// itemHolder.iv_head = (ImageView) convertView
		// .findViewById(R.id.iv_head);
		// itemHolder.tv_nickname = (TextView) convertView
		// .findViewById(R.id.tv_nickname);
		// convertView.setTag(itemHolder);
		// } else {
		// itemHolder = (ItemHolder) convertView.getTag();
		// }
		// final String headFileName = friends.get(phones.get(a
		// * 6 + position)).head;
		// final ImageView iv_head = itemHolder.iv_head;
		// app.fileHandler.getHeadImage(headFileName,
		// new FileResult() {
		// @Override
		// public void onResult(String where) {
		// iv_head.setImageBitmap(app.fileHandler.bitmaps
		// .get(headFileName));
		// }
		// });
		// itemHolder.tv_nickname.setText(friends.get(phones.get(a
		// * 6 + position)).nickName);
		//
		// return convertView;
		// }
		//
		// @Override
		// public long getItemId(int position) {
		// return position;
		// }
		//
		// @Override
		// public Object getItem(int position) {
		// return friends.get(phones.get(a * 6 + position));
		// }
		//
		// @Override
		// public int getCount() {
		// int nowcount = 0;
		// if (a < pagecount - 1) {
		// nowcount = 6;
		// } else {
		// nowcount = phones.size() - a * 6;
		// }
		// return nowcount;
		// }
		//
		// @Override
		// public void unregisterDataSetObserver(
		// DataSetObserver observer) {
		// if (observer != null) {
		// super.unregisterDataSetObserver(observer);
		// }
		// }
		//
		// };
		// GridView gridpage = (GridView) app.inflater.inflate(
		// R.layout.f_friend_panelitem_gridpage, null);
		// gridpage.setAdapter(gridpageAdapter);
		// pageviews.add(gridpage);
		// }
		//
		// vp_contentAdapter = new PagerAdapter() {
		// @Override
		// public boolean isViewFromObject(View arg0, Object arg1) {
		// return arg0 == arg1;
		// }
		//
		// @Override
		// public int getCount() {
		// return pageviews.size();
		// }
		//
		// @Override
		// public void destroyItem(View container, int position,
		// Object object) {
		// ((ViewPager) container).removeView(pageviews.get(position));
		// }
		//
		// @Override
		// public Object instantiateItem(View container, int position) {
		// ((ViewPager) container).addView(pageviews.get(position));
		// return pageviews.get(position);
		// }
		//
		// @Override
		// public void unregisterDataSetObserver(DataSetObserver observer) {
		// if (observer != null) {
		// super.unregisterDataSetObserver(observer);
		// }
		// }
		// };
		// vp_content.setAdapter(vp_contentAdapter);
		// viewList.add(group);
		// }
	}

	@Override
	protected EditText showSoftInputOnShow() {
		// TODO Auto-generated method stub
		return null;
	}

}
