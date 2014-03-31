package com.lejoying.wxgs.adapter;

import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.sax.StartElementListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.lejoying.wxgs.R;
import com.lejoying.wxgs.activity.InformationActivity;
import com.lejoying.wxgs.activity.MainActivity;
import com.lejoying.wxgs.activity.MsgActivity;

public class SquareViewAdapter extends BaseAdapter implements OnClickListener{
	
	private List<Map<String, Object>> list;
	private Context context;
	private LayoutInflater layoutInflater;
	
	public SquareViewAdapter(Context context, List<Map<String, Object>> list) {
		 this.layoutInflater=LayoutInflater.from(context);
		this.context = context;
		this.list = list;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return list.size();
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return list.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return arg0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup arg2) {
		// TODO Auto-generated method stub
		ViewHolder holder = null;
		if (convertView == null) {
			// 得到每一行的布局资源文件
			convertView=layoutInflater.inflate(
					R.layout.square_listview, null);
			// 实例化ViewHolder对象
			holder = new ViewHolder();
			// 为holder添加内容
			holder.square_name = (TextView) convertView
					.findViewById(R.id.person_name);
			holder.square_speak = (TextView) convertView
					.findViewById(R.id.person_speak);
			holder.square_time = (TextView) convertView
					.findViewById(R.id.person_time);
			holder.square_distance = (TextView) convertView
					.findViewById(R.id.person_distance);
			holder.picture = (ImageView) convertView
					.findViewById(R.id.person_pic);

			// 将组件的缓存对象保存到convertView
			convertView.setTag(holder);
		} else {
			// 从convertView中取出缓存对象
			holder = (ViewHolder) convertView.getTag();
		}

		holder.square_name.setText((String) list.get(position).get("name"));
		holder.square_speak.setText((String) list.get(position).get("speak"));
		holder.square_time.setText((String) list.get(position).get("time"));
		holder.square_distance.setText((String) list.get(position).get(
				"distance"));
		holder.picture.setImageResource((Integer) list.get(position).get(
				"picture"));

		holder.square_speak.setOnClickListener(this);
		holder.picture.setOnClickListener(this);
		holder.square_name.setOnClickListener(this);
		return convertView;
	}

	private class ViewHolder {
		public TextView square_name, square_speak, square_time,
				square_distance;
		public ImageView picture;
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		Intent intent;
		switch (v.getId()) {
		case R.id.person_name:
			intent= new Intent(context, InformationActivity.class);
			context.startActivity(intent);
			break;
		case R.id.person_pic:
			intent = new Intent(context, InformationActivity.class);
			context.startActivity(intent);
			break;
		case R.id.person_speak:
			intent = new Intent(context, MsgActivity.class);
			context.startActivity(intent);
			break;
		}
	}
}
