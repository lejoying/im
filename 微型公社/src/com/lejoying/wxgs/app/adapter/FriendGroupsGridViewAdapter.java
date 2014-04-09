package com.lejoying.wxgs.app.adapter;

import java.util.List;

import com.lejoying.wxgs.R;
import com.lejoying.wxgs.activity.utils.MCImageUtils;
import com.lejoying.wxgs.app.data.entity.Group;

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
	 
	
	LayoutInflater inflater; 
	
	public FriendGroupsGridViewAdapter(LayoutInflater inflater,List<Group> groups){
        this.inflater = inflater;  
        this.groups=groups;
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
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		 if (convertView == null)  
	        {  
	            convertView = inflater.inflate(R.layout.f_bussinesscard_friendgroups, null);  
	            holder = new ViewHolder();
	            holder. tv_groupname=(TextView) convertView.findViewById(R.id.tv_groupname);
	            holder. tv_grouppic=(ImageView) convertView.findViewById(R.id.tv_grouppic);
	            
	            convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
		 holder. tv_groupname.setText(groups.get(position).name);
		 holder.tv_grouppic.setImageBitmap(MCImageUtils.getCircleBitmap(BitmapFactory.decodeResource(inflater.getContext().getResources(), R.drawable.face_man), true, 5, Color.WHITE));
		return convertView;
	}
	private class ViewHolder {
		public TextView tv_groupname;
		public ImageView tv_grouppic;
	}

}
