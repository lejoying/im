package com.lejoying.mc;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.lejoying.mcutils.ImageTools;

public class BusinessCardActivity extends Activity {

	private TextView tv_spacing;
	private TextView tv_spacing2;
	private TextView tv_spacing3;
	private TextView tv_mainbusiness;
	private RelativeLayout rl_show;
	private RelativeLayout rl_top;
	private ScrollView sv_bccontent;

	private boolean stopSend;
	private int startHight;

	private Handler handler;

	private LayoutInflater inflater;

	// test
	private ViewPager vp_content;
	private Bitmap water;
	private Bitmap anmo;
	private Bitmap kuaidi;
	private Bitmap jiazheng;
	private List<View> views;
	private List<ImageView> page_ivs;
	private LinearLayout ll_bottom;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.businesscardedit);
		initEdit();
	}

	public void initEdit() {
		handler = new Handler();
		inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		sv_bccontent = (ScrollView) findViewById(R.id.sv_bccontent);
		// test
		ll_bottom = (LinearLayout) findViewById(R.id.ll_bottom);
		vp_content = (ViewPager) findViewById(R.id.vp_content);
		page_ivs = new ArrayList<ImageView>();
		views = new ArrayList<View>();
		inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);

		GridView gv1 = (GridView) inflater.inflate(
				R.layout.service_station_singlepage, null);
		GridView gv2 = (GridView) inflater.inflate(
				R.layout.service_station_singlepage, null);
		GridView gv3 = (GridView) inflater.inflate(
				R.layout.service_station_singlepage, null);

		GVAdapter gvadapter = new GVAdapter(4);

		gv1.setAdapter(gvadapter);
		gv2.setAdapter(gvadapter);
		gv3.setAdapter(gvadapter);

		views.add(gv1);
		views.add(gv2);
		views.add(gv3);

		water = ImageTools.getCircleBitmap(BitmapFactory.decodeResource(
				getResources(), R.drawable.ss_water), true, 3, Color.rgb(242,
				242, 242));

		anmo = ImageTools.getCircleBitmap(BitmapFactory.decodeResource(
				getResources(), R.drawable.ss_anmo), true, 3, Color.rgb(242,
				242, 242));

		jiazheng = ImageTools.getCircleBitmap(BitmapFactory.decodeResource(
				getResources(), R.drawable.ss_jiazheng), true, 3, Color.rgb(
				242, 242, 242));

		kuaidi = ImageTools.getCircleBitmap(BitmapFactory.decodeResource(
				getResources(), R.drawable.ss_kuaidi), true, 3, Color.rgb(242,
				242, 242));

		PagerAdapter pagerAdapter = new PagerAdapter() {

			@Override
			public boolean isViewFromObject(View arg0, Object arg1) {
				return arg0 == arg1;
			}

			@Override
			public int getCount() {
				return views.size();
			}

			@Override
			public void destroyItem(View container, int position, Object object) {
				((ViewPager) container).removeView(views.get(position));
			}

			@Override
			public Object instantiateItem(View container, int position) {
				((ViewPager) container).addView(views.get(position));
				return views.get(position);
			}
		};
		vp_content.setAdapter(pagerAdapter);
		vp_content.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_MOVE)
					sv_bccontent.requestDisallowInterceptTouchEvent(true);
				else
					sv_bccontent.requestDisallowInterceptTouchEvent(false);
				return false;
			}
		});

		sv_bccontent.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				return false;
			}
		});

		for (int i = 0; i < views.size(); i++) {
			ImageView iv = new ImageView(this);
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
				page_ivs.get(arg0).setImageResource(R.drawable.point_white);
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

	}

	class GVAdapter extends BaseAdapter {

		int count;

		public GVAdapter(int count) {
			this.count = count;
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return count;
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			RelativeLayout rl = (RelativeLayout) inflater.inflate(
					R.layout.service_station_singleitem, null);
			ImageView iv_head = (ImageView) rl.findViewById(R.id.iv_head);
			TextView tv_name = (TextView) rl.findViewById(R.id.tv_nickName);
			if (position == 0) {
				iv_head.setImageBitmap(jiazheng);
				tv_name.setText("家政服务");
			}
			if (position == 1) {
				iv_head.setImageBitmap(anmo);
				tv_name.setText("按摩服务");
			}
			if (position == 2) {
				iv_head.setImageBitmap(water);
				tv_name.setText("送水服务");
			}
			if (position == 3) {
				iv_head.setImageBitmap(kuaidi);
				tv_name.setText("顺丰快递");
			}

			return rl;
		}
	};

	public void initView() {
		// find view
		tv_spacing = (TextView) findViewById(R.id.tv_spacing);
		tv_spacing2 = (TextView) findViewById(R.id.tv_spacing2);
		tv_spacing3 = (TextView) findViewById(R.id.tv_spacing3);
		tv_mainbusiness = (TextView) findViewById(R.id.tv_mainbusiness);
		rl_show = (RelativeLayout) findViewById(R.id.rl_show);
		rl_top = (RelativeLayout) findViewById(R.id.rl_top);
		sv_bccontent = (ScrollView) findViewById(R.id.sv_bccontent);

		// init object
		handler = new Handler();
		inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		// get spacing
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);

		Rect frame = new Rect();
		getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
		int statusBarHeight = frame.top;

		tv_spacing2 = (TextView) findViewById(R.id.tv_spacing2);

		tv_spacing.setHeight((int) (dm.heightPixels - rl_top.getHeight()
				- rl_show.getHeight() - statusBarHeight - tv_spacing2
				.getHeight()));

		tv_spacing3.setHeight((int) (dm.heightPixels * 0.2));

		sv_bccontent.setOnTouchListener(new OnTouchListener() {

			@SuppressLint("NewApi")
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				stopSend = true;
				new Thread() {
					@Override
					public void run() {
						// TODO Auto-generated method stub
						while (stopSend) {
							handler.post(new Runnable() {
								@Override
								public void run() {
									if (sv_bccontent.getScrollY() > 10) {
										tv_mainbusiness.setMaxLines(100);
									}
									if (sv_bccontent.getScrollY() < 10) {
										tv_mainbusiness.setMaxLines(3);
									}
								}
							});
							int start = sv_bccontent.getScrollY();
							try {
								Thread.sleep(100);
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							int stop = sv_bccontent.getScrollY();
							if (start == stop) {
								stopSend = false;
							}
						}

						super.run();
					}

				}.start();
				return false;
			}
		});

		// test
		ll_bottom = (LinearLayout) findViewById(R.id.ll_bottom);
		vp_content = (ViewPager) findViewById(R.id.vp_content);
		page_ivs = new ArrayList<ImageView>();
		views = new ArrayList<View>();
		inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);

		GridView gv1 = (GridView) inflater.inflate(
				R.layout.service_station_singlepage, null);
		GridView gv2 = (GridView) inflater.inflate(
				R.layout.service_station_singlepage, null);
		GridView gv3 = (GridView) inflater.inflate(
				R.layout.service_station_singlepage, null);

		GVAdapter gvadapter = new GVAdapter(4);

		gv1.setAdapter(gvadapter);
		gv2.setAdapter(gvadapter);
		gv3.setAdapter(gvadapter);

		views.add(gv1);
		views.add(gv2);
		views.add(gv3);

		water = ImageTools.getCircleBitmap(BitmapFactory.decodeResource(
				getResources(), R.drawable.ss_water), true, 3, Color.rgb(242,
				242, 242));

		anmo = ImageTools.getCircleBitmap(BitmapFactory.decodeResource(
				getResources(), R.drawable.ss_anmo), true, 3, Color.rgb(242,
				242, 242));

		jiazheng = ImageTools.getCircleBitmap(BitmapFactory.decodeResource(
				getResources(), R.drawable.ss_jiazheng), true, 3, Color.rgb(
				242, 242, 242));

		kuaidi = ImageTools.getCircleBitmap(BitmapFactory.decodeResource(
				getResources(), R.drawable.ss_kuaidi), true, 3, Color.rgb(242,
				242, 242));

		PagerAdapter pagerAdapter = new PagerAdapter() {

			@Override
			public boolean isViewFromObject(View arg0, Object arg1) {
				return arg0 == arg1;
			}

			@Override
			public int getCount() {
				return views.size();
			}

			@Override
			public void destroyItem(View container, int position, Object object) {
				((ViewPager) container).removeView(views.get(position));
			}

			@Override
			public Object instantiateItem(View container, int position) {
				((ViewPager) container).addView(views.get(position));
				return views.get(position);
			}
		};
		vp_content.setAdapter(pagerAdapter);
		vp_content.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_MOVE)
					sv_bccontent.requestDisallowInterceptTouchEvent(true);
				else
					sv_bccontent.requestDisallowInterceptTouchEvent(false);
				return false;
			}
		});

		sv_bccontent.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				return false;
			}
		});

		for (int i = 0; i < views.size(); i++) {
			ImageView iv = new ImageView(this);
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
				page_ivs.get(arg0).setImageResource(R.drawable.point_white);
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

	}

//	class GVAdapter extends BaseAdapter {
//
//		int count;
//
//		public GVAdapter(int count) {
//			this.count = count;
//		}
//
//		@Override
//		public int getCount() {
//			// TODO Auto-generated method stub
//			return count;
//		}
//
//		@Override
//		public Object getItem(int position) {
//			// TODO Auto-generated method stub
//			return position;
//		}
//
//		@Override
//		public long getItemId(int position) {
//			// TODO Auto-generated method stub
//			return position;
//		}
//
//		@Override
//		public View getView(int position, View convertView, ViewGroup parent) {
//			// TODO Auto-generated method stub
//			RelativeLayout rl = (RelativeLayout) inflater.inflate(
//					R.layout.service_station_singleitem, null);
//			ImageView iv_head = (ImageView) rl.findViewById(R.id.iv_head);
//			TextView tv_name = (TextView) rl.findViewById(R.id.tv_nickName);
//			if (position == 0) {
//				iv_head.setImageBitmap(jiazheng);
//				tv_name.setText("家政服务");
//			}
//			if (position == 1) {
//				iv_head.setImageBitmap(anmo);
//				tv_name.setText("按摩服务");
//			}
//			if (position == 2) {
//				iv_head.setImageBitmap(water);
//				tv_name.setText("送水服务");
//			}
//			if (position == 3) {
//				iv_head.setImageBitmap(kuaidi);
//				tv_name.setText("顺丰快递");
//			} else {
//				iv_head.setImageBitmap(jiazheng);
//				tv_name.setText("家政服务");
//			}
//
//			return rl;
//		}
//
//	}

	// @Override
	// public void onWindowFocusChanged(boolean hasFocus) {
	//
	// tv_spacing = (TextView) findViewById(R.id.tv_spacing);
	// if (startHight == 0) {
	// startHight = tv_spacing.getHeight();
	// }
	// if (tv_spacing.getHeight() == startHight) {
	// initView();
	// }
	// super.onWindowFocusChanged(hasFocus);
	// }

	public void back(View v) {
		finish();
	}

}
