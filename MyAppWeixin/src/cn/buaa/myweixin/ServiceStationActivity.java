package cn.buaa.myweixin;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
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
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import cn.buaa.myweixin.apiutils.ImageTools;

public class ServiceStationActivity extends Activity {

	private ViewPager vp_servicestation;
	private LayoutInflater inflater;
	private List<View> views;
	private LinearLayout ll_bottom;
	private List<ImageView> page_ivs;

	private Bitmap water;
	private Bitmap anmo;
	private Bitmap kuaidi;
	private Bitmap jiazheng;

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

		GVAdapter gvadapter = new GVAdapter(4);
		
		gv1.setAdapter(gvadapter);
		
		gv1.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Intent intent = new Intent(ServiceStationActivity.this,ChatServiceWaterActivity.class);
				startActivity(intent);
			}
		});

		views.add(gv1);

		water = ImageTools.getCircleBitmap(BitmapFactory.decodeResource(
				getResources(), R.drawable.ss_water), true, 3 ,Color.rgb(242, 242, 242));

		anmo = ImageTools.getCircleBitmap(BitmapFactory.decodeResource(
				getResources(), R.drawable.ss_anmo), true, 3 ,Color.rgb(242, 242, 242));
		
		jiazheng = ImageTools.getCircleBitmap(BitmapFactory.decodeResource(
				getResources(), R.drawable.ss_jiazheng), true, 3 ,Color.rgb(242, 242, 242));
		
		kuaidi = ImageTools.getCircleBitmap(BitmapFactory.decodeResource(
				getResources(), R.drawable.ss_kuaidi), true, 3 ,Color.rgb(242, 242, 242));

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

	}

	// 返回按钮
	public void back(View v) {
		finish();
	}

	// 添加按钮
	public void addStation(View v) {

	}
}
