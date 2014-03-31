package com.lejoying.wxgs.adapter;

import com.lejoying.wxgs.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class MyExpandableListAdapter extends BaseExpandableListAdapter {
	private String[] group;   
    private String[][] buddy;  
    private Context context;  
    LayoutInflater inflater; 
	
    public MyExpandableListAdapter(Context context,String[] group,String[][] buddy){  
        this.context=context;
        inflater = LayoutInflater.from(context);  
        this.group=group;  
        this.buddy=buddy;  
    }  
    
	@Override
	public Object getChild(int groupPosition, int childPosition) {
		// TODO Auto-generated method stub
		return buddy[groupPosition][childPosition];
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		// TODO Auto-generated method stub
		return childPosition;  
	}

	@Override
	public View getChildView(int groupPosition, int childPosition, boolean arg2, View convertView,  
            ViewGroup arg4) {  
        
        convertView = inflater.inflate(R.layout.expandablelistview_child, null);  
        TextView nickTextView=(TextView) convertView.findViewById(R.id.expandable_listview_child_nick);  
        nickTextView.setText(getChild(groupPosition, childPosition).toString());  
        return convertView;  
       
    }  

	@Override
	public int getChildrenCount(int groupPosition) {
		// TODO Auto-generated method stub
		return buddy[groupPosition].length;  
	}

	@Override
	public Object getGroup(int groupPosition) {
		// TODO Auto-generated method stub
		return group[groupPosition];
	}

	@Override
	public int getGroupCount() {
		// TODO Auto-generated method stub
		return group.length;
	}

	@Override
	public long getGroupId(int groupPosition) {
		// TODO Auto-generated method stub
		return groupPosition;
	}

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup arg3) {  
        convertView = inflater.inflate(R.layout.expandablelistview_group, null);  
        TextView groupNameTextView=(TextView) convertView.findViewById(R.id.expandable_listview_group_name);  
        groupNameTextView.setText(getGroup(groupPosition).toString());  
        ImageView image = (ImageView) convertView.findViewById(R.id.expandable_listview_image);  
        image.setImageResource(R.drawable.collect);  
        //更换展开分组图片  
        if(!isExpanded){  
            image.setImageResource(R.drawable.launch);  
        }  
        return convertView;  
    } 

	@Override
	public boolean hasStableIds() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean isChildSelectable(int arg0, int arg1) {
		// TODO Auto-generated method stub
		return true;
	}

}
