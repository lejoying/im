package com.open.welinks.view;

import java.util.ArrayList;
import java.util.Map;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.open.welinks.NearbyActivity;
import com.open.welinks.R;
import com.open.welinks.controller.NearbyController;
import com.open.welinks.controller.NearbyController.Status;
import com.open.welinks.model.FileHandlers;
import com.open.welinks.utils.DateUtil;
import com.open.welinks.utils.DistanceUtils;

public class NearbyView {
	public NearbyView thisView;
	public NearbyController thisController;
	public NearbyActivity thisActivity;
	public LayoutInflater mInflater;

	public RelativeLayout backView;
	public RelativeLayout rightContainer;
	public TextView titleContent;
	public ListView nearby;
	public ThreeChoicesView threeChoicesView;

	public NearbyAdapter nearbyAdapter;

	// public Bitmap bitmap;

	public int NearbyLayoutID;

	public DisplayImageOptions headOptions;
	public FileHandlers fileHandlers = FileHandlers.getInstance();

	public NearbyView(NearbyActivity thisActivity) {
		thisView = this;
		this.thisActivity = thisActivity;
	}

	public void initView() {

		headOptions = new DisplayImageOptions.Builder().cacheInMemory(true).cacheOnDisk(true).considerExifParams(true).displayer(new RoundedBitmapDisplayer(45)).build();

		mInflater = thisActivity.getLayoutInflater();
		thisActivity.setContentView(R.layout.activity_nearby);
		backView = (RelativeLayout) thisActivity.findViewById(R.id.backView);
		titleContent = (TextView) thisActivity.findViewById(R.id.backTitleView);
		nearby = (ListView) thisActivity.findViewById(R.id.nearby);
		rightContainer = (RelativeLayout) thisActivity.findViewById(R.id.rightContainer);
		threeChoicesView = new ThreeChoicesView(thisActivity, 0);
		rightContainer.addView(threeChoicesView);

		titleContent.setText("附近");

		// bitmap = BitmapFactory.decodeResource(thisActivity.getResources(), R.drawable.face_man);
		// bitmap = MCImageUtils.getCircleBitmap(bitmap, true, 5, Color.WHITE);
	}

	public void fillData() {
		nearbyAdapter = new NearbyAdapter(thisController.mInfomations);
		nearby.setAdapter(nearbyAdapter);
	}

	public class NearbyAdapter extends BaseAdapter {
		ArrayList<Map<String, Object>> mInfomations;

		public NearbyAdapter(ArrayList<Map<String, Object>> mInfomations) {
			this.mInfomations = mInfomations;
		}

		@Override
		public int getCount() {
			return mInfomations.size();
		}

		@Override
		public Object getItem(int posotion) {
			return mInfomations.get(posotion);
		}

		@Override
		public long getItemId(int posotion) {
			return posotion;
		}

		@Override
		public View getView(int posotion, View convertView, ViewGroup parent) {
			Map<String, Object> infomation = mInfomations.get(posotion);
			NearbuHolder holder;
			convertView = mInflater.inflate(NearbyLayoutID, null);
			holder = new NearbuHolder();
			holder.head = (ImageView) convertView.findViewById(R.id.head);
			holder.name = (TextView) convertView.findViewById(R.id.name);
			holder.distance = (TextView) convertView.findViewById(R.id.distance);
			holder.mainBusiness = (TextView) convertView.findViewById(R.id.mainBusiness);

			if (thisController.status == Status.account) {
				holder.sex = (ImageView) convertView.findViewById(R.id.sex);
				holder.age = (TextView) convertView.findViewById(R.id.age);
				holder.recently = (TextView) convertView.findViewById(R.id.recently);
			} else {
				holder.address = (TextView) convertView.findViewById(R.id.address);
				holder.chatNum = (TextView) convertView.findViewById(R.id.chatNum);
				holder.members = (TextView) convertView.findViewById(R.id.members);
			}
			convertView.setTag(holder);

			if (thisController.status == Status.account) {
				String head = (String) infomation.get("head");
				fileHandlers.getHeadImage(head, holder.head, headOptions);
				// if ("".equals(head) || "Head".equals(head)) {
				// holder.head.setImageBitmap(bitmap);
				// } else {
				// thisController.setImageOnView(head, holder.head);
				// }
				String sex = (String) infomation.get("sex");
				int sexImageResource = 0;
				if ("男".equals(sex) || "male".equals(sex)) {
					sexImageResource = R.drawable.personalinfo_male;
				} else {
					sexImageResource = R.drawable.personalinfo_female;
				}
				String mainBusiness = (String) infomation.get("mainBusiness");
				if ("".equals(mainBusiness)) {
					holder.mainBusiness.setText("此用户暂无签名");
				} else {
					holder.mainBusiness.setText(mainBusiness);
				}
				holder.sex.setImageResource(sexImageResource);
				holder.name.setText((String) infomation.get("name"));
				holder.distance.setText(DistanceUtils.getDistance((Integer) infomation.get("distance")));
				holder.recently.setText(DateUtil.getChatMessageListTime(Long.valueOf((String) infomation.get("lastlogintime"))));
				holder.age.setText("20");
				convertView.setTag(R.id.tag_first, "point");
				convertView.setTag(R.id.tag_second, (String) infomation.get("phone"));
				convertView.setTag(R.id.tag_third, infomation);
			} else {
				String icon = (String) infomation.get("icon");
				fileHandlers.getHeadImage(icon, holder.head, headOptions);
				// if ("".equals(icon)) {
				// holder.head.setImageBitmap(bitmap);
				// } else {
				// thisController.setImageOnView((String) infomation.get("icon"), holder.head);
				// }
				String description = (String) infomation.get("description");
				if ("".equals(description)) {
					if (thisController.status == Status.group) {
						holder.mainBusiness.setText("此群组暂无描述");
					} else if (thisController.status == Status.square) {
						holder.mainBusiness.setText("此广场暂无描述");
					}
				} else {
					holder.mainBusiness.setText(description);
				}
				holder.name.setText((String) infomation.get("name"));
				holder.distance.setText(DistanceUtils.getDistance((Integer) infomation.get("distance")));
				holder.address.setText((String) infomation.get("address"));
				holder.members.setText("10/100");
				holder.chatNum.setText("10");
				convertView.setTag(R.id.tag_first, "group");
				convertView.setTag(R.id.tag_second, (String) infomation.get("gid"));
				convertView.setTag(R.id.tag_third, infomation);
			}
			convertView.setOnClickListener(thisController.mOnClickListener);
			return convertView;
		}

		class NearbuHolder {
			public ImageView head, sex;
			public TextView name, age, distance, mainBusiness, recently, address, chatNum, members;
		}
	}

}
