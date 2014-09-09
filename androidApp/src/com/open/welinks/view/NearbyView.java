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

import com.open.welinks.NearbyActivity;
import com.open.welinks.R;
import com.open.welinks.controller.NearbyController;
import com.open.welinks.controller.NearbyController.Status;
import com.open.welinks.utils.DateUtil;
import com.open.welinks.utils.DistanceUtils;
import com.open.welinks.view.ThreeChoicesView.OnItemClickListener;

public class NearbyView {
	public NearbyView thisView;
	public NearbyController thisController;
	public NearbyActivity thisActivity;
	public LayoutInflater mInflater;

	public RelativeLayout backView;
	public TextView title;
	public ListView nearby;

	public NearbyAdapter nearbyAdapter;

	public int NearbyLayoutID;

	public NearbyView(NearbyActivity thisActivity) {
		this.thisActivity = thisActivity;
	}

	public void initView() {
		mInflater = thisActivity.getLayoutInflater();
		thisActivity.setContentView(R.layout.activity_nearby);
		backView = (RelativeLayout) thisActivity.findViewById(R.id.backView);
		title = (TextView) thisActivity.findViewById(R.id.title);
		nearby = (ListView) thisActivity.findViewById(R.id.nearby);

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
			if (convertView == null) {
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
			} else {
				holder = (NearbuHolder) convertView.getTag();
			}

			if (thisController.status == Status.account) {
				thisController.setImageOnView((String) infomation.get("head"), holder.head);
				String sex = (String) infomation.get("sex");
				int sexImageResource = 0;
				if ("男".equals(sex) || "male".equals(sex)) {
					sexImageResource = R.drawable.personalinfo_male;
				} else {
					sexImageResource = R.drawable.personalinfo_male;
				}
				holder.sex.setImageResource(sexImageResource);
				holder.name.setText((String) infomation.get("name"));
				holder.distance.setText(DistanceUtils.getDistance((Integer) infomation.get("distance")));
				holder.mainBusiness.setText((String) infomation.get("mainBusiness"));
				holder.recently.setText(DateUtil.getChatMessageListTime((Long) infomation.get("recently")));
				holder.age.setText("");
			} else {
				thisController.setImageOnView((String) infomation.get("icon"), holder.head);
				holder.name.setText((String) infomation.get("name"));
				holder.distance.setText(DistanceUtils.getDistance((Integer) infomation.get("distance")));
				holder.mainBusiness.setText((String) infomation.get("mainBusiness"));
				holder.address.setText((String) infomation.get("address"));
				holder.members.setText(thisController.data.relationship.groupsMap.get((String) infomation.get("gid")).members.size());
				holder.chatNum.setText("");
			}
			return convertView;
		}

		class NearbuHolder {
			public ImageView head, sex;
			public TextView name, age, distance, mainBusiness, recently, address, chatNum, members;
		}
	}

}
