package com.lejoying.adapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.lejoying.mc.R;
import com.lejoying.mcutils.Friend;

@SuppressLint("NewApi")
public class CircleAdapter {
	private Map<String, List<Friend>> circlefriends;
	private ViewGroup contentParent;
	private Activity activity;
	private LayoutInflater inflater;
	private ViewGroup controlPanel;

	private List<View> viewList;

	private int beforeScrollY;
	private float beforeGetY;
	private int beforePosition;

	private boolean editMode;

	public CircleAdapter(Map<String, List<Friend>> circlefriends,
			ViewGroup cParent, ViewGroup controlPanel, Activity activity) {
		this.circlefriends = circlefriends;
		this.contentParent = cParent;
		this.activity = activity;
		this.controlPanel = controlPanel;
		if (activity != null) {
			this.inflater = (LayoutInflater) activity
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}
	}

	public void createView() {
		viewList = new ArrayList<View>();
		if (circlefriends != null) {
			Object[] circles = circlefriends.keySet().toArray();
			for (int ccount = circles.length - 1; ccount >= 0; ccount--) {
				String circle = String.valueOf(circles[ccount]);
				final RelativeLayout rl_group = (RelativeLayout) inflater
						.inflate(R.layout.group_panel, null);
				TextView tv_groupname = (TextView) rl_group
						.findViewById(R.id.tv_groupname);
				tv_groupname.setText(circle);

				LinearLayout ll_bottom = (LinearLayout) rl_group
						.findViewById(R.id.ll_bottom);
				final List<ImageView> page_ivs = new ArrayList<ImageView>();

				final List<Friend> friends = circlefriends.get(circle);
				final int pagecount = friends.size() % 6 == 0 ? friends.size() / 6
						: friends.size() / 6 + 1;
				final List<View> pageviews = new ArrayList<View>();
				for (int i = 0; i < pagecount; i++) {
					final int a = i;
					BaseAdapter gridpageAdapter = new BaseAdapter() {
						@Override
						public View getView(int position, View convertView,
								final ViewGroup parent) {
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
							tv_nickName.setText(friends.get(a * 6 + position)
									.getNickName());

							// ���ó����û�ͷ�����༭ģʽ
							rl_gridpage_item
									.setOnLongClickListener(new OnLongClickListener() {
										@Override
										public boolean onLongClick(View v) {
											beforePosition = viewList
													.indexOf(parent.getParent()
															.getParent()
															.getParent());
											if (!editMode) {
												for (int i = 0; i < viewList
														.size(); i++) {
													Animation animation = null;
													final View animView = viewList
															.get(i);
													if (i < beforePosition) {
														animation = AnimationUtils
																.loadAnimation(
																		activity,
																		R.anim.tran_out_top);
													}
													if (i > beforePosition) {
														animation = AnimationUtils
																.loadAnimation(
																		activity,
																		R.anim.tran_out_bottom);
													}
													if (i != beforePosition) {
														animView.startAnimation(animation);
													}
												}
												beforeScrollY = ((ScrollView) (contentParent
														.getParent()))
														.getScrollY();
												beforeGetY = viewList.get(
														beforePosition).getY();
												TranslateAnimation taAnimation = new TranslateAnimation(
														0,
														0,
														0,
														-(beforeGetY - beforeScrollY));
												taAnimation.setDuration(300);
												taAnimation
														.setAnimationListener(new AnimationListener() {

															@Override
															public void onAnimationStart(
																	Animation animation) {
															}

															@Override
															public void onAnimationRepeat(
																	Animation animation) {
															}

															@Override
															public void onAnimationEnd(
																	Animation animation) {
																viewList.get(
																		beforePosition)
																		.clearAnimation();
																contentParent.removeAllViewsInLayout();
																contentParent.addView(viewList.get(beforePosition));
																// ����༭ģʽ
																editMode = true;
															}
														});

												controlPanel
														.setVisibility(View.VISIBLE);
												TranslateAnimation tAnimation_contorlPanel = new TranslateAnimation(
														0, 0, controlPanel
																.getHeight(), 0);
												tAnimation_contorlPanel
														.setDuration(300);
												controlPanel
														.startAnimation(tAnimation_contorlPanel);
												viewList.get(beforePosition)
														.startAnimation(
																taAnimation);
											}

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
				vp_content.setOnTouchListener(new OnTouchListener() {

					@Override
					public boolean onTouch(View v, MotionEvent event) {
						int action = event.getAction();
						switch (action) {
						case MotionEvent.ACTION_MOVE:
							rl_group.getParent().getParent()
									.requestDisallowInterceptTouchEvent(true);
							break;

						default:
							rl_group.getParent().getParent()
									.requestDisallowInterceptTouchEvent(false);
							break;
						}

						return false;
					}
				});

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

				viewList.add(rl_group);
				contentParent.addView(rl_group);
			}
		}
	}

	public void exitEdit() {
		// ����ѡ�
		TranslateAnimation ta_exit = new TranslateAnimation(0, 0, 0,
				controlPanel.getHeight());
		ta_exit.setDuration(300);
		ta_exit.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {

			}

			@Override
			public void onAnimationRepeat(Animation animation) {

			}

			@Override
			public void onAnimationEnd(Animation animation) {
				controlPanel.setVisibility(View.INVISIBLE);
			}
		});
		controlPanel.startAnimation(ta_exit);
		// ����Ȧλ�û�ԭ
		contentParent.removeAllViewsInLayout();

		for (int i = 0; i < viewList.size(); i++) {
			contentParent.addView(viewList.get(i));
			Animation inanimation = null;
			if (i > beforePosition) {
				inanimation = AnimationUtils.loadAnimation(activity,
						R.anim.tran_in_bottom);
			}
			if (i < beforePosition) {
				inanimation = AnimationUtils.loadAnimation(activity,
						R.anim.tran_in_top);
			}
			if (i != beforePosition) {
				viewList.get(i).startAnimation(inanimation);
			} else {
				inanimation = new TranslateAnimation(0, 0,
						-(beforeGetY - beforeScrollY), 0);
				inanimation.setDuration(300);
				viewList.get(beforePosition).startAnimation(inanimation);
			}
		}

		// �ع����༭ǰ״̬
		final ScrollView sv = ((ScrollView) (contentParent.getParent()));
		sv.post(new Runnable() {
			@Override
			public void run() {
				sv.scrollTo(0, beforeScrollY);
			}
		});
		editMode = false;
	}

	public boolean getEditMode() {
		return editMode;
	}
}