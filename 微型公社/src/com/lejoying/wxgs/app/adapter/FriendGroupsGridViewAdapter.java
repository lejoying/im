package com.lejoying.wxgs.app.adapter;

import java.util.List;

import com.lejoying.wxgs.R;
import com.lejoying.wxgs.activity.utils.MCImageUtils;
import com.lejoying.wxgs.app.MainApplication;
import com.lejoying.wxgs.app.data.entity.Group;
import com.lejoying.wxgs.app.handler.FileHandler.FileResult;

import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class FriendGroupsGridViewAdapter extends BaseAdapter {
	private List<Group> groups;
	MainApplication app = MainApplication.getMainApplication();
	ViewHolder holder = null;
	LayoutInflater inflater;

	public FriendGroupsGridViewAdapter(LayoutInflater inflater,
			List<Group> groups) {
		this.inflater = inflater;
		this.groups = groups;
	}

	@Override
	public int getCount() {
		return groups.size();
	}

	@Override
	public Object getItem(int position) {
		return groups.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {

		if (convertView == null) {
			convertView = inflater.inflate(
					R.layout.f_bussinesscard_friendgroups, null);
			holder = new ViewHolder();
			holder.tv_groupname = (TextView) convertView
					.findViewById(R.id.tv_groupname);
			holder.tv_grouppic = (ImageView) convertView
					.findViewById(R.id.tv_grouppic);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		holder.tv_groupname.setText(groups.get(position).name);
		final String headFileName = groups.get(position).icon;
		app.fileHandler.getHeadImage(headFileName, new FileResult() {
			@Override
			public void onResult(String where) {	
				holder.tv_grouppic.setImageBitmap(app.fileHandler.bitmaps.get(headFileName));
			}
		});
		// app.fileHandler.getGroupHeadImage(headFileName, new FileResult() {
		// @Override
		// public void onResult(String where) {
		// app.UIHandler.post(new Runnable() {
		//
		// @Override
		// public void run() {
		// holder.tv_grouppic.setImageBitmap(MCImageUtils
		// .getCircleBitmap(app.fileHandler.bitmaps
		// .get(headFileName)));
		// }
		// });
		// }
		// });
		return convertView;
	}

	private class ViewHolder {
		public TextView tv_groupname;
		public ImageView tv_grouppic;
	}

}
