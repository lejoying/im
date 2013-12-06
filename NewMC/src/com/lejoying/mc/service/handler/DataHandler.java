package com.lejoying.mc.service.handler;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lejoying.mc.R;
import com.lejoying.mc.entity.Circle;
import com.lejoying.mc.entity.Friend;
import com.lejoying.mc.entity.Message;
import com.lejoying.mc.service.MainService;
import com.lejoying.mc.service.handler.MainServiceHandler.ServiceEvent;
import com.lejoying.mc.utils.MCDataTools;
import com.lejoying.mc.utils.MCImageTools;

public class DataHandler {

	private LayoutInflater mInflater;
	private Context mContext;
	private ServiceEvent mServiceEvent;

	private Bitmap head;

	DataHandler(Context context, ServiceEvent serviceEvent) {
		this.mContext = context;
		this.mServiceEvent = serviceEvent;
		this.mInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		head = MCImageTools.getCircleBitmap(BitmapFactory.decodeResource(
				mContext.getResources(), R.drawable.xiaohei), true, 5,
				Color.WHITE);
	}

	protected void process(Intent intent) {
		int notify = intent.getIntExtra("NOTIFY", -1);
		switch (notify) {
		case MainService.NOTIFY_MESSAGELIST:
			List<Message> messages = MCDataTools.getMessages(mContext, 0);
			System.out.println(messages);
			break;
		case MainService.NOTIFY_CHATMESSAGE:

			break;
		case MainService.NOTIFY_FRIEND:
			List<Circle> circles = MCDataTools.getCircles(mContext);
			List<View> circlesView = new ArrayList<View>();
			for (Circle circle : circles) {
				final View group = (RelativeLayout) mInflater.inflate(
						R.layout.f_group_panel, null);
				TextView tv_groupname = (TextView) group
						.findViewById(R.id.tv_groupname);
				tv_groupname.setText(circle.getName());

				final List<ImageView> page_ivs = new ArrayList<ImageView>();

				final List<Friend> friends = circle.getFriends();
				final int pagecount = friends.size() % 6 == 0 ? friends.size() / 6
						: friends.size() / 6 + 1;
				final List<View> pageviews = new ArrayList<View>();

				for (int i = 0; i < pagecount; i++) {
					final int a = i;
					BaseAdapter gridpageAdapter = new BaseAdapter() {
						@Override
						public View getView(int position, View convertView,
								final ViewGroup parent) {
							RelativeLayout rl_gridpage_item = (RelativeLayout) mInflater
									.inflate(
											R.layout.f_group_panelitem_gridpageitem_user,
											null);
							ImageView iv_head = (ImageView) rl_gridpage_item
									.findViewById(R.id.iv_head);
							TextView tv_nickname = (TextView) rl_gridpage_item
									.findViewById(R.id.tv_nickname);

							iv_head.setImageBitmap(head);
							tv_nickname.setText(friends.get(a * 6 + position)
									.getNickName());
							// 点击用户头像进行聊天
							rl_gridpage_item
									.setOnClickListener(new OnClickListener() {

										@Override
										public void onClick(View arg0) {
											// if (!editMode) {
											// Intent intent = new Intent(
											// activity,
											// ChatActivity.class);
											// activity.startActivity(intent);
											// }
										}
									});
							// 设置长按用户头像进入编辑模式
							rl_gridpage_item
									.setOnLongClickListener(new OnLongClickListener() {
										@Override
										public boolean onLongClick(View v) {
											// if (!editMode) {
											// // 进入编辑模式
											// editMode = true;
											// beforePosition = viewList
											// .indexOf(parent
											// .getParent()
											// .getParent()
											// .getParent());
											// for (int i = 0; i < viewList
											// .size(); i++) {
											// Animation animation = null;
											// final View animView = viewList
											// .get(i);
											// if (i < beforePosition) {
											// animation = AnimationUtils
											// .loadAnimation(
											// activity,
											// R.anim.tran_out_top);
											// }
											// if (i > beforePosition) {
											// animation = AnimationUtils
											// .loadAnimation(
											// activity,
											// R.anim.tran_out_bottom);
											// }
											// if (i != beforePosition) {
											// animation
											// .setAnimationListener(new
											// AnimationAdapter() {
											// @Override
											// public void onAnimationEnd(
											// Animation animation) {
											// animView.setVisibility(View.INVISIBLE);
											// }
											// });
											// animView.startAnimation(animation);
											// }
											// }
											// final int location[] = new
											// int[2];
											// viewList.get(beforePosition)
											// .getLocationInWindow(
											// location);
											// if (statusBarHeight == 0) {
											// Rect frame = new Rect();
											// activity.getWindow()
											// .getDecorView()
											// .getWindowVisibleDisplayFrame(
											// frame);
											// statusBarHeight = frame.top;
											// }
											// scrollToY = statusBarHeight
											// - location[1];
											//
											// TranslateAnimation taAnimation =
											// new TranslateAnimation(
											// 0f, 0f, 0f, scrollToY);
											// taAnimation.setDuration(300);
											// taAnimation
											// .setAnimationListener(new
											// AnimationListener() {
											//
											// @Override
											// public void onAnimationStart(
											// Animation animation) {
											// }
											//
											// @Override
											// public void onAnimationRepeat(
											// Animation animation) {
											// }
											//
											// @Override
											// public void onAnimationEnd(
											// Animation animation) {
											// viewList.get(
											// beforePosition)
											// .clearAnimation();
											// contentParent
											// .scrollTo(
											// 0,
											// Math.round(-scrollToY));
											// }
											// });
											//
											// controlPanel
											// .setVisibility(View.VISIBLE);
											// TranslateAnimation
											// tAnimation_contorlPanel = new
											// TranslateAnimation(
											// 0, 0, controlPanel
											// .getHeight(), 0);
											// tAnimation_contorlPanel
											// .setDuration(300);
											// controlPanel
											// .startAnimation(tAnimation_contorlPanel);
											// viewList.get(beforePosition)
											// .startAnimation(
											// taAnimation);
											// }
											//
											return false;
										}
									});

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
					GridView gridpage = (GridView) mInflater.inflate(
							R.layout.f_group_panelitem_gridpage, null);
					gridpage.setAdapter(gridpageAdapter);
					pageviews.add(gridpage);
				}

				ViewPager vp_content = (ViewPager) group
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
				// vp_content.setOnTouchListener(new OnTouchListener() {
				//
				// @Override
				// public boolean onTouch(View v, MotionEvent event) {
				// int action = event.getAction();
				// switch (action) {
				// case MotionEvent.ACTION_MOVE:
				// contentParent.getParent()
				// .requestDisallowInterceptTouchEvent(true);
				// break;
				//
				// default:
				// contentParent.getParent()
				// .requestDisallowInterceptTouchEvent(false);
				// break;
				// }
				//
				// return false;
				// }
				// });

				// for (int i = 0; i < pageviews.size(); i++) {
				// ImageView iv = new ImageView(activity);
				// if (i == 0) {
				// iv.setImageResource(R.drawable.point_white);
				// } else {
				// iv.setImageResource(R.drawable.point_blank);
				// }
				//
				// page_ivs.add(iv);
				// ll_bottom.addView(iv);
				// }

				vp_content.setOnPageChangeListener(new OnPageChangeListener() {

					@Override
					public void onPageSelected(int arg0) {
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
					}

					@Override
					public void onPageScrollStateChanged(int arg0) {
					}
				});

				circlesView.add(group);
			}
			break;
		default:
			break;
		}
	}
}
