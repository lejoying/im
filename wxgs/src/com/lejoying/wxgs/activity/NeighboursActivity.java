package com.lejoying.wxgs.activity;

import com.lejoying.wxgs.R;
import com.lejoying.wxgs.SysApplication;
import com.lejoying.wxgs.R.layout;
import com.lejoying.wxgs.R.menu;
import com.lejoying.wxgs.adapter.GroupExpandableListAdapter;
import com.lejoying.wxgs.adapter.MyExpandableListAdapter;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ExpandableListView.OnGroupCollapseListener;
import android.widget.ExpandableListView.OnGroupExpandListener;

public class NeighboursActivity extends Activity implements OnClickListener{
	private Button neighbours_newgroup;
	private ImageView neighbours_back;
	private ExpandableListView neighbours_expandablelistview;
	private String[] group = new String[] { "附近的群组", "我创建的群组", "我加入的群组" };
	// �������
	private String[][] buddy = new String[][] { { "A群组", "B群组", "C群组" },
			{ "D群组", "E群组", "F群组", "G群组" }, { "H群组", "I群组", "J群组", "K群组" } };

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_neighbours);
		SysApplication.getInstance().addActivity(this);
		neighbours_back = (ImageView) findViewById(R.id.neighbours_back);
		neighbours_expandablelistview = (ExpandableListView) findViewById(R.id.neighbours_expandablelistview);
		neighbours_newgroup=(Button) findViewById(R.id.neighbours_newgroup);
		ExpandableListAdapter adapter = new GroupExpandableListAdapter(this,
				group, buddy);
		neighbours_expandablelistview.setAdapter(adapter);
		neighbours_expandablelistview.setGroupIndicator(null);
		// ����չ��
		neighbours_expandablelistview
				.setOnGroupExpandListener(new OnGroupExpandListener() {
					public void onGroupExpand(int groupPosition) {
					}
				});
		// ����ر�
		neighbours_expandablelistview
				.setOnGroupCollapseListener(new OnGroupCollapseListener() {
					public void onGroupCollapse(int groupPosition) {
					}
				});
		neighbours_expandablelistview
				.setOnChildClickListener(new OnChildClickListener() {
					public boolean onChildClick(ExpandableListView arg0,
							View arg1, int groupPosition, int childPosition,
							long arg4) {
							
						Intent intent = new Intent(NeighboursActivity.this,
								GroupInfoActivity.class);
						// intent.putExtra("name", name);
						startActivity(intent);
						return true;
					}
				});
		neighbours_back.setOnClickListener(this); 
		neighbours_newgroup.setOnClickListener(this); 
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.neighbours_back:
			finish();
			break;

		case R.id.neighbours_newgroup:
			Intent intent = new Intent(NeighboursActivity.this,
					NewGroupActivity.class);
			// intent.putExtra("name", name);
			startActivity(intent);
			break;
		}
	}
}
