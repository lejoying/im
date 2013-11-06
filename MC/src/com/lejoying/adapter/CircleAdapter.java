package com.lejoying.adapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lejoying.mc.R;
import com.lejoying.mcutils.Friend;

public class CircleAdapter {
	private Map<String, List<Friend>> circlefriends;
	private ViewGroup parent;
	private Activity activity;

	private LayoutInflater inflater;

	public CircleAdapter(Map<String, List<Friend>> circlefriends,
			ViewGroup parent, Activity activity) {
		this.circlefriends = circlefriends;
		this.parent = parent;
		this.activity = activity;
		if (activity != null) {
			this.inflater = (LayoutInflater) activity
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}
	}

	public void createView() {
		if (circlefriends != null) {
			Object[] circles =  circlefriends.keySet().toArray();
			for (int ccount = circles.length - 1; ccount >= 0; ccount--) {
				String circle = String.valueOf(circles[ccount]);
				RelativeLayout rl_group = (RelativeLayout) inflater.inflate(
						R.layout.group_panel, null);
				TextView tv_groupname = (TextView) rl_group
						.findViewById(R.id.tv_groupname);
				LinearLayout ll_bottom = (LinearLayout) rl_group
						.findViewById(R.id.ll_bottom);
				final List<ImageView> page_ivs = new ArrayList<ImageView>();
				tv_groupname.setText(circle);
				final List<Friend> friends = circlefriends.get(circle);
				final int pagecount = friends.size() % 6 == 0 ? friends.size() / 6
						: friends.size() / 6 + 1;
				final List<View> pageviews = new ArrayList<View>();
				for (int i = 0; i < pagecount; i++) {
					final int a = i;
					BaseAdapter gridpageAdapter = new BaseAdapter() {
						@Override
						public View getView(final int position,
								View convertView, final ViewGroup parent) {
							RelativeLayout rl_gridpage_item = (RelativeLayout) inflater
									.inflate(
											R.layout.group_panelitem_gridpageitem_user,
											null);
							ImageView iv_head = (ImageView) rl_gridpage_item
									.findViewById(R.id.iv_head);
							TextView tv_nickName = (TextView) rl_gridpage_item
									.findViewById(R.id.tv_nickName);

							iv_head.setImageDrawable(activity.getResources()
									.getDrawable(R.drawable.xiaohei));
							tv_nickName.setText(friends.get(position)
									.getNickName());

							return rl_gridpage_item;
						}

						@Override
						public long getItemId(int position) {
							return position;
						}

						@Override
						public Object getItem(int position) {
							return friends.get(a * 6 + position);
						}

						@Override
						public int getCount() {
							int nowcount = 0;
							if (a < pagecount - 1) {
								nowcount = 6;
							} else {
								nowcount = friends.size() - a * 6;
							}
							return nowcount;
						}
					};
					GridView gridpage = (GridView) inflater.inflate(
							R.layout.group_panelitem_gridpage, null);
					gridpage.setAdapter(gridpageAdapter);
					pageviews.add(gridpage);
				}

				ViewPager vp_content = (ViewPager) rl_group
						.findViewById(R.id.vp_content);
				PagerAdapter vp_contentAdapter = new PagerAdapter() {
					@Override
					public boolean isViewFromObject(View arg0, Object arg1) {
						return arg0 == arg1;
					}

					@Override
					public int getCount() {
						return pageviews.size();
					}

					@Override
					public void destroyItem(View container, int position,
							Object object) {
						((ViewPager) container).removeView(pageviews
								.get(position));
					}

					@Override
					public Object instantiateItem(View container, int position) {
						((ViewPager) container)
								.addView(pageviews.get(position));
						return pageviews.get(position);
					}
				};
				vp_content.setAdapter(vp_contentAdapter);

				for (int i = 0; i < pageviews.size(); i++) {
					ImageView iv = new ImageView(activity);
					if (i == 0) {
						iv.setImageResource(R.drawable.point_white);
					} else {
						iv.setImageResource(R.drawable.point_blank);
					}

					page_ivs.add(iv);
					ll_bottom.addView(iv);
				}

				vp_content.setOnPageChangeListener(new OnPageChangeListener() {

					@Override
					public void onPageSelected(int arg0) {
						// TODO Auto-generated method stub
						page_ivs.get(arg0).setImageResource(
								R.drawable.point_white);
						if (arg0 - 1 >= 0) {
							page_ivs.get(arg0 - 1).setImageResource(
									R.drawable.point_blank);
						}
						if (arg0 + 1 < page_ivs.size()) {
							page_ivs.get(arg0 + 1).setImageResource(
									R.drawable.point_blank);
						}
					}

					@Override
					public void onPageScrolled(int arg0, float arg1, int arg2) {
						// TODO Auto-generated method stub
					}

					@Override
					public void onPageScrollStateChanged(int arg0) {
						// TODO Auto-generated method stub
					}
				});

				parent.addView(rl_group);
			}
		}
	}
}
