package com.lejoying.adapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
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
import com.lejoying.mcutils.ImageTools;

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

	private WindowManager windowManager = null; // 窗口管理类
	// 窗口参数类
	private WindowManager.LayoutParams layoutParams = null;

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
		final Bitmap head = ImageTools.getCircleBitmap(BitmapFactory
				.decodeResource(activity.getResources(), R.drawable.xiaohei),
				true, 5, Color.rgb(255, 255, 255));
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
				ScrollView sv = ((ScrollView) (contentParent.getParent()));
				sv.setOnTouchListener(new OnTouchListener() {
					@Override
					public boolean onTouch(View view, MotionEvent event) {
						int action = event.getAction();
						// switch (action) {
						// case MotionEvent.ACTION_MOVE:
						//
						// break;
						// }
						return editMode;
					}
				});
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
							TextView tv_nickname = (TextView) rl_gridpage_item
									.findViewById(R.id.tv_nickname);

							iv_head.setImageBitmap(head);
							tv_nickname.setText(friends.get(a * 6 + position)
									.getNickName());

							// 设置长按用户头像进入编辑模式
							rl_gridpage_item
									.setOnLongClickListener(new OnLongClickListener() {
										@Override
										public boolean onLongClick(View v) {
											if (!editMode) {
												// 进入编辑模式
												editMode = true;
												beforePosition = viewList
														.indexOf(parent
																.getParent()
																.getParent()
																.getParent());
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
														animation
																.setAnimationListener(new AnimationListener() {

																	@Override
																	public void onAnimationEnd(
																			Animation animation) {
																	}

																	@Override
																	public void onAnimationRepeat(
																			Animation animation) {
																	}

																	@Override
																	public void onAnimationStart(
																			Animation animation) {
																		animView.setVisibility(View.INVISIBLE);
																	}
																});
														animView.startAnimation(animation);
													}
												}
												beforeScrollY = ((ScrollView) (contentParent
														.getParent()))
														.getScrollY();
												beforeGetY = viewList.get(
														beforePosition).getY();
												float scrollToY = -(beforeGetY - beforeScrollY);
												System.out
														.println(scrollToY);
												TranslateAnimation taAnimation = new TranslateAnimation(
														0f, 0f, 0f, scrollToY);
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
																contentParent
																		.scrollTo(
																				0,
																				Math.round(beforeGetY - beforeScrollY));
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
							contentParent.getParent()
									.requestDisallowInterceptTouchEvent(true);
							break;

						default:
							contentParent.getParent()
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
		contentParent.scrollTo(0, 0);
		// 隐藏选项卡
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
		// 好友圈位置还原

		Animation animation = new TranslateAnimation(0, 0,
				-(beforeGetY - beforeScrollY), 0);
		animation.setDuration(300);
		viewList.get(beforePosition).startAnimation(animation);

		for (int i = 0; i < viewList.size(); i++) {
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
				viewList.get(i).setVisibility(View.VISIBLE);
				viewList.get(i).startAnimation(inanimation);
			}
		}
		editMode = false;
	}

	public boolean getEditMode() {
		return editMode;
	}

	public ImageView startDrag(Bitmap bm, int y) {
		layoutParams = new WindowManager.LayoutParams();
		// 设置重力
		layoutParams.gravity = Gravity.TOP;
		// 横轴坐标不变
		layoutParams.x = 0;
		/**
		 * 
		 * y轴坐标为 视图相对于自身左上角的Y-touch点在列表项中的y +视图相对于屏幕左上角的Y，= 该view相对于屏幕左上角的位置
		 */
		layoutParams.y = y;
		/****
		 * 宽度和高度都为wrapContent
		 */
		layoutParams.width = LayoutParams.WRAP_CONTENT;
		layoutParams.height = LayoutParams.WRAP_CONTENT;

		/****
		 * 设置该layout参数的一些flags参数
		 */
		layoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
				| WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
				| WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
				| WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN;
		// 设置该window项是半透明的格式
		layoutParams.format = PixelFormat.TRANSLUCENT;
		// 设置没有动画
		layoutParams.windowAnimations = 0;

		// 配置一个影像ImageView
		ImageView imageViewForDragAni = new ImageView(activity);
		imageViewForDragAni.setImageBitmap(bm);
		// 配置该windowManager
		windowManager = (WindowManager) activity.getSystemService("window");
		windowManager.addView(imageViewForDragAni, layoutParams);
		return imageViewForDragAni;
	}
}
