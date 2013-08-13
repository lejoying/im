package cn.buaa.myweixin;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import cn.buaa.myweixin.utils.ImageTools;

public class ServiceStationActivity extends Activity {

	private ViewPager vp_servicestation;
	private LayoutInflater inflater;
	private List<View> views;
	private LinearLayout ll_bottom;
	private List<ImageView> page_ivs;

	private Bitmap head1;
	private Bitmap head2;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.service_station);
		initView();
	}

	public void initView() {
		vp_servicestation = (ViewPager) findViewById(R.id.vp_servicestation);
		ll_bottom = (LinearLayout) findViewById(R.id.ll_bottom);
		page_ivs = new ArrayList<ImageView>();
		views = new ArrayList<View>();
		inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);

		GridView gv1 = (GridView) inflater.inflate(
				R.layout.service_station_singlepage, null);
		GridView gv2 = (GridView) inflater.inflate(
				R.layout.service_station_singlepage, null);
		GridView gv3 = (GridView) inflater.inflate(
				R.layout.service_station_singlepage, null);
		GridView gv4 = (GridView) inflater.inflate(
				R.layout.service_station_singlepage, null);
		GridView gv5 = (GridView) inflater.inflate(
				R.layout.service_station_singlepage, null);

		gv1.setAdapter(new GVAdapter(15));
		gv2.setAdapter(new GVAdapter(15));
		gv3.setAdapter(new GVAdapter(15));
		gv4.setAdapter(new GVAdapter(15));
		gv5.setAdapter(new GVAdapter(6));

		views.add(gv1);
		views.add(gv2);
		views.add(gv3);
		views.add(gv4);
		views.add(gv5);

		head1 = ImageTools.getCircleBitmap(BitmapFactory.decodeResource(
				getResources(), R.drawable.xiaohei), true, 3 ,Color.rgb(242, 242, 242));

		head2 = ImageTools.getCircleBitmap(BitmapFactory.decodeResource(
				getResources(), R.drawable.service_head), true, 6 ,Color.rgb(242, 242, 242));

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
		vp_servicestation.setAdapter(pagerAdapter);

		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		final int screenWidth = dm.widthPixels;

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

		vp_servicestation.setOnPageChangeListener(new OnPageChangeListener() {

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
			TextView tv_name = (TextView) rl.findViewById(R.id.tv_name);
			if (position % 2 == 0) {
				iv_head.setImageBitmap(head1);
				tv_name.setText("小黑");
			} else {
				iv_head.setImageBitmap(head2);
				tv_name.setText("客服MM");
			}

			return rl;
		}

	}

	// 返回按钮
	public void back(View v) {
		finish();
	}

	// 添加按钮
	public void addStation(View v) {

	}
}
