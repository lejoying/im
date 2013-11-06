package com.lejoying.mc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewParent;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.lejoying.adapter.CircleAdapter;
import com.lejoying.mcutils.Friend;

public class BusinessCardActivity extends Activity {

	// DEFINITION view
	private ScrollView sv_content;
	private LinearLayout ll_content;

	// DEFINITION object
	private LayoutInflater inflater;
	private Handler handler;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.businesscard);
		initView();
	}

	@SuppressLint("NewApi")
	public void initView() {
		// INIT view
		sv_content = (ScrollView) findViewById(R.id.sv_content);
		ll_content = (LinearLayout) findViewById(R.id.ll_content);

		// INIT object
		inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		handler = new Handler();
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

		CircleAdapter circleAdapter = new CircleAdapter(circlefriends,
				ll_content, this);

		circleAdapter.createView();
		// final RelativeLayout rl_group = (RelativeLayout) inflater.inflate(
		// R.layout.group_panel, null);
		// TextView tv_groupname = (TextView) rl_group
		// .findViewById(R.id.tv_groupname);
		//
		// LinearLayout ll_bottom = (LinearLayout) rl_group
		// .findViewById(R.id.ll_bottom);
		// final List<ImageView> page_ivs = new ArrayList<ImageView>();
		//
		// tv_groupname.setText("密友圈1");
		// GridView gridpage1 = (GridView) inflater.inflate(
		// R.layout.group_panelitem_gridpage, null);
		// GridView gridpage2 = (GridView) inflater.inflate(
		// R.layout.group_panelitem_gridpage, null);
		// GridView gridpage3 = (GridView) inflater.inflate(
		// R.layout.group_panelitem_gridpage, null);
		//
		// final List<String> griditems = new ArrayList<String>();
		// griditems.add("用户1");
		// griditems.add("用户2");
		// griditems.add("用户3");
		// griditems.add("用户4");
		// griditems.add("用户5");
		// griditems.add("用户6");
		//
		// BaseAdapter gridpageAdapter = new BaseAdapter() {
		//
		// @Override
		// public View getView(final int position, View convertView,
		// final ViewGroup parent) {
		// RelativeLayout rl_gridpage_item = (RelativeLayout) inflater
		// .inflate(R.layout.group_panelitem_gridpageitem_user,
		// null);
		// ImageView iv_head = (ImageView) rl_gridpage_item
		// .findViewById(R.id.iv_head);
		// TextView tv_nickName = (TextView) rl_gridpage_item
		// .findViewById(R.id.tv_nickName);
		//
		// iv_head.setImageDrawable(getResources().getDrawable(
		// R.drawable.xiaohei));
		// tv_nickName.setText(griditems.get(position));
		//
		// rl_gridpage_item
		// .setOnLongClickListener(new OnLongClickListener() {
		//
		// @Override
		// public boolean onLongClick(View v) {
		// startEdit(parent.getParent().getParent());
		// return true;
		// }
		// });
		//
		// return rl_gridpage_item;
		// }
		//
		// @Override
		// public long getItemId(int position) {
		// return position;
		// }
		//
		// @Override
		// public Object getItem(int position) {
		// return griditems.get(position);
		// }
		//
		// @Override
		// public int getCount() {
		// return griditems.size();
		// }
		// };
		//
		// gridpage1.setAdapter(gridpageAdapter);
		// gridpage2.setAdapter(gridpageAdapter);
		// gridpage3.setAdapter(gridpageAdapter);
		//
		// final List<View> views = new ArrayList<View>();
		// views.add(gridpage1);
		// views.add(gridpage2);
		// views.add(gridpage3);
		// ViewPager vp_content = (ViewPager) rl_group
		// .findViewById(R.id.vp_content);
		// PagerAdapter vp_contentAdapter = new PagerAdapter() {
		// @Override
		// public boolean isViewFromObject(View arg0, Object arg1) {
		// return arg0 == arg1;
		// }
		//
		// @Override
		// public int getCount() {
		// return views.size();
		// }
		//
		// @Override
		// public void destroyItem(View container, int position, Object object)
		// {
		// ((ViewPager) container).removeView(views.get(position));
		// }
		//
		// @Override
		// public Object instantiateItem(View container, int position) {
		// ((ViewPager) container).addView(views.get(position));
		// return views.get(position);
		// }
		// };
		// vp_content.setAdapter(vp_contentAdapter);
		// vp_content.setOnTouchListener(new OnTouchListener() {
		//
		// @Override
		// public boolean onTouch(View v, MotionEvent event) {
		// if (event.getAction() == MotionEvent.ACTION_MOVE) {
		// sv_content.requestDisallowInterceptTouchEvent(true);
		// }
		// return false;
		// }
		// });
		//
		// for (int i = 0; i < views.size(); i++) {
		// ImageView iv = new ImageView(this);
		// if (i == 0) {
		// iv.setImageResource(R.drawable.point_white);
		// } else {
		// iv.setImageResource(R.drawable.point_blank);
		// }
		//
		// page_ivs.add(iv);
		// ll_bottom.addView(iv);
		// }
		//
		// vp_content.setOnPageChangeListener(new OnPageChangeListener() {
		//
		// @Override
		// public void onPageSelected(int arg0) {
		// // TODO Auto-generated method stub
		// page_ivs.get(arg0).setImageResource(R.drawable.point_white);
		// if (arg0 - 1 >= 0) {
		// page_ivs.get(arg0 - 1).setImageResource(
		// R.drawable.point_blank);
		// }
		// if (arg0 + 1 < page_ivs.size()) {
		// page_ivs.get(arg0 + 1).setImageResource(
		// R.drawable.point_blank);
		// }
		// }
		//
		// @Override
		// public void onPageScrolled(int arg0, float arg1, int arg2) {
		// // TODO Auto-generated method stub
		// }
		//
		// @Override
		// public void onPageScrollStateChanged(int arg0) {
		// // TODO Auto-generated method stub
		// }
		// });
		//
		// final RelativeLayout rl2 = (RelativeLayout) inflater.inflate(
		// R.layout.group_panel, null);
		// final RelativeLayout rl3 = (RelativeLayout) inflater.inflate(
		// R.layout.group_panel, null);
		// TextView tv_groupname2 = (TextView)
		// rl2.findViewById(R.id.tv_groupname);
		// tv_groupname2.setText("密友圈2");
		// TextView tv_groupname3 = (TextView)
		// rl3.findViewById(R.id.tv_groupname);
		// tv_groupname3.setText("密友圈3");
		//
		// ll_content.addView(rl_group);
		// ll_content.addView(rl2);
		// ll_content.addView(rl3);

	}

	public void startEdit(ViewParent v) {
		setContentView(R.layout.businesscardedit);
		TextView tv_groupname = (TextView) findViewById(R.id.tv_groupname);
		ViewPager vp_content = (ViewPager) findViewById(R.id.vp_content);
		tv_groupname.setText(((TextView) ((RelativeLayout) v)
				.findViewById(R.id.tv_groupname)).getText());
		ViewPager vp = (ViewPager) (((RelativeLayout) v)
				.findViewById(R.id.vp_content));
		vp_content.setAdapter(vp.getAdapter());
	}

	public void back(View v) {
		finish();
	}

	public void rightMenu(View v) {

	}

}
