package com.open.welinks.view;

import java.io.IOException;
import java.util.ArrayList;

import android.graphics.Color;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

import com.amap.api.maps.AMap;
import com.amap.api.maps.MapView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.open.welinks.NearbyActivity;
import com.open.welinks.R;
import com.open.welinks.controller.NearbyController;
import com.open.welinks.customView.SmallBusinessCardPopView;
import com.open.welinks.customView.ThreeChoicesView;
import com.open.welinks.model.TaskManageHolder;
import com.open.welinks.utils.StreamParser;

public class NearbyView {

	public NearbyView thisView;
	public NearbyController thisController;
	public NearbyActivity thisActivity;

	public TaskManageHolder taskManageHolder = TaskManageHolder.getInstance();

	public LayoutInflater mInflater;

	public RelativeLayout backView;
	public RelativeLayout rightContainer;
	public TextView backTitleContent;
	public RelativeLayout centerContainer;
	public ListView nearbyListView;
	public ThreeChoicesView threeChoicesView;

	public NearbyAdapter nearbyAdapter;

	public int NearbyLayoutID;

	public View maxView;

	public NearbyView(NearbyActivity thisActivity) {
		thisView = this;
		this.thisActivity = thisActivity;
	}

	public SmallBusinessCardPopView businessCardPopView;

	public DisplayMetrics metrics;
	public ImageView menuImage;

	public MapView mapView;
	public AMap mAMap;

	public TextView addressView;
	public TextView sortView;

	public View lbsMapView;

	public View lineView;

	public View searChView;
	public View locationView;

	public View positionView;

	public void initView() {

		this.metrics = new DisplayMetrics();

		thisActivity.getWindowManager().getDefaultDisplay().getMetrics(this.metrics);

		this.mInflater = thisActivity.getLayoutInflater();
		this.thisActivity.setContentView(R.layout.activity_nearby);
		this.backView = (RelativeLayout) thisActivity.findViewById(R.id.backView);
		RelativeLayout backMaxView = (RelativeLayout) thisActivity.findViewById(R.id.backMaxView);
		backMaxView.setBackgroundColor(Color.parseColor("#eeffffff"));
		this.backTitleContent = (TextView) thisActivity.findViewById(R.id.backTitleView);
		this.nearbyListView = (ListView) thisActivity.findViewById(R.id.nearby);
		this.rightContainer = (RelativeLayout) thisActivity.findViewById(R.id.rightContainer);
		this.centerContainer = (RelativeLayout) thisActivity.findViewById(R.id.centerContainer);
		this.maxView = thisActivity.findViewById(R.id.maxView);
		this.backTitleContent.setText("");
		this.threeChoicesView = new ThreeChoicesView(thisActivity, 0);
		this.centerContainer.addView(this.threeChoicesView);

		this.addressView = (TextView) thisActivity.findViewById(R.id.address);
		this.sortView = (TextView) thisActivity.findViewById(R.id.sort);

		// this.lineView = thisActivity.findViewById(R.id.line);
		this.searChView = thisActivity.findViewById(R.id.search);
		this.locationView = thisActivity.findViewById(R.id.location);
		this.positionView = thisActivity.findViewById(R.id.position);

		this.lbsMapView = thisActivity.findViewById(R.id.lbsmap);

		int width = (int) (this.metrics.density * 48);

		ImageView lineView = new ImageView(thisActivity);
		lineView.setBackgroundColor(Color.parseColor("#0099cd"));
		int lineWidth = (int) (1 * this.metrics.density);
		int linePadding = (int) (5 * this.metrics.density);
		RelativeLayout.LayoutParams lineParams = new RelativeLayout.LayoutParams(lineWidth, width - linePadding * 4);
		this.centerContainer.addView(lineView, lineParams);

		RelativeLayout.LayoutParams centerContainerParams = (LayoutParams) this.centerContainer.getLayoutParams();
//		this.centerContainer.setBackgroundColor(Color.parseColor("#380099cd"));
		this.centerContainer.setGravity(Gravity.CENTER_VERTICAL);
		centerContainerParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
		centerContainerParams.leftMargin = (int) (this.metrics.density * 53);

		RelativeLayout.LayoutParams threeChoicesViewParams = (LayoutParams) this.threeChoicesView.getLayoutParams();
		threeChoicesViewParams.leftMargin = (int) (((this.metrics.widthPixels - this.metrics.density * 180) / 2) - 48 * this.metrics.density);

		ImageView backImageView = (ImageView) thisActivity.findViewById(R.id.backImageView);
		backImageView.setImageResource(R.drawable.ab3);
		backImageView.setColorFilter(Color.parseColor("#0099cd"));
		backImageView.setAlpha(0.875f);
		RelativeLayout.LayoutParams backImageViewParams = (LayoutParams) backImageView.getLayoutParams();
		backImageViewParams.width = width;
		backImageViewParams.height = width;

		RelativeLayout.LayoutParams params = (LayoutParams) this.backView.getLayoutParams();
		params.height = width;
		params.width = (int) (width + 5 * this.metrics.density);
		this.backView.setPadding(0, 0, (int) (10 * this.metrics.density), 0);

		this.menuImage = new ImageView(thisActivity);
		this.menuImage.setImageResource(R.drawable.button_modifygroupname);
		this.menuImage.setColorFilter(Color.parseColor("#0099cd"));
		this.menuImage.setAlpha(0.875f);
		int moreWidth = (int) (53 * this.metrics.density);
		RelativeLayout.LayoutParams menuImageParams = new RelativeLayout.LayoutParams(moreWidth, width);
		int padding = (int) (5 * this.metrics.density);
		this.menuImage.setPadding(padding, padding, padding, padding);
		this.menuImage.setBackgroundResource(R.drawable.backview_background);
		this.rightContainer.addView(this.menuImage, menuImageParams);

		RelativeLayout.LayoutParams rightLayoutParams = (LayoutParams) this.rightContainer.getLayoutParams();
		rightLayoutParams.rightMargin = 0;

		this.businessCardPopView = new SmallBusinessCardPopView(thisActivity, this.maxView);

		mapView = (MapView) thisActivity.findViewById(R.id.mapView);
	}

	public Gson gson = new Gson();

	public void fillData() {
		nearbyAdapter = new NearbyAdapter();
		try {
			byte[] bytes = StreamParser.parseToByteArray(thisActivity.getAssets().open("testhot.js"));
			String result = new String(bytes);
			this.hots = gson.fromJson(result, new TypeToken<ArrayList<HotContent>>() {
			}.getType());
			nearbyListView.setAdapter(nearbyAdapter);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public ArrayList<HotContent> hots = new ArrayList<HotContent>();

	public class HotContent {
		public String cover;
		public String content;
	}

	public ViewManage viewManage = ViewManage.getInstance();

	public ImageLoader imageLoader = ImageLoader.getInstance();

	public class NearbyAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return hots.size();
		}

		@Override
		public Object getItem(int posotion) {
			return hots.get(posotion);
		}

		@Override
		public long getItemId(int posotion) {
			return posotion;
		}

		@Override
		public View getView(int posotion, View convertView, ViewGroup parent) {
			HotHolder holder;
			if (convertView == null) {
				holder = new HotHolder();
				convertView = mInflater.inflate(R.layout.view_location_hot, null);
				holder.coverImageView = (ImageView) convertView.findViewById(R.id.cover);
				holder.contentView = (TextView) convertView.findViewById(R.id.content);
				convertView.setTag(holder);
			} else {
				holder = (HotHolder) convertView.getTag();
			}
			HotContent hotContent = hots.get(posotion);
			if (hotContent != null) {
				if (hotContent.cover.equals("0B4FFBABE6B23AC301EC410ED53B138904843C5E.osp")) {
					imageLoader.displayImage("drawable://" + R.drawable.a1, holder.coverImageView);
				} else if (hotContent.cover.equals("0BD297A73EB1E1C9F20F05B14E18457926755E49.osp")) {
					imageLoader.displayImage("drawable://" + R.drawable.a2, holder.coverImageView);
				} else if (hotContent.cover.equals("0D4E5EA50301A9B7E11035EDA0FBD946D5998AB8.osj")) {
					imageLoader.displayImage("drawable://" + R.drawable.a3, holder.coverImageView);
				} else if (hotContent.cover.equals("1ABE24D6C1A8A623EE55823E63E3416AB8A7D10F.osp")) {
					imageLoader.displayImage("drawable://" + R.drawable.a4, holder.coverImageView);
				} else if (hotContent.cover.equals("009A3379FCF1A85F02BBA7B31C806472C19B9D91.osp")) {
					imageLoader.displayImage("drawable://" + R.drawable.a5, holder.coverImageView);
				} else if (hotContent.cover.equals("119F605941E84A11442962DD5240DC7A17332DAC.osp")) {
					imageLoader.displayImage("drawable://" + R.drawable.a6, holder.coverImageView);
				} else if (hotContent.cover.equals("2025DD0F1E4957B812B18C318DE4B6A0318F34D0.osj")) {
					imageLoader.displayImage("drawable://" + R.drawable.a7, holder.coverImageView);
				}
				holder.contentView.setText(hotContent.content);
			}
			return convertView;
		}

		public class HotHolder {
			public ImageView coverImageView;
			public TextView contentView;
		}
	}

	public void onResume() {
		businessCardPopView.dismissUserCardDialogView();
		mapView.onResume();
	}

	public void onPause() {
		mapView.onPause();
	}

	public void onDestroy() {
		mapView.onDestroy();
	}

	public void onSaveInstanceState(Bundle outState) {
		mapView.onSaveInstanceState(outState);
	}
}
