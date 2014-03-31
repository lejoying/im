package com.lejoying.wxgs.activity;

import com.lejoying.wxgs.R;
import com.lejoying.wxgs.SysApplication;
import com.lejoying.wxgs.R.layout;
import com.lejoying.wxgs.R.menu;
import com.lejoying.wxgs.adapter.MyExpandableListAdapter;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;

import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ExpandableListView.OnGroupCollapseListener;
import android.widget.ExpandableListView.OnGroupExpandListener;
import android.widget.ImageView;

public class NearActivity extends Activity implements OnClickListener {
	private ImageView near_back;
	private ExpandableListView near_expandablelistview;
	private String[] group = new String[] { "附近的人", "附近的群", "附近的活动" };
	// �������
	private String[][] buddy = new String[][] { { "暗示法国", "红人堂", "团购" },
			{ "热污染", "阿斯旺", "高山蔷薇", "天美意" }, { "巍峨", "宣传部", "前三个太阳", "阿斯蒂芬"} };

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_near);
		SysApplication.getInstance().addActivity(this);
		near_back = (ImageView) findViewById(R.id.near_back);
		near_expandablelistview = (ExpandableListView) findViewById(R.id.near_expandablelistview);

		ExpandableListAdapter adapter = new MyExpandableListAdapter(this,
				group, buddy);
		near_expandablelistview.setAdapter(adapter);
		near_expandablelistview.setGroupIndicator(null);
		// ����չ��
		near_expandablelistview
				.setOnGroupExpandListener(new OnGroupExpandListener() {
					public void onGroupExpand(int groupPosition) {
					}
				});
		// ����ر�
		near_expandablelistview
				.setOnGroupCollapseListener(new OnGroupCollapseListener() {
					public void onGroupCollapse(int groupPosition) {
					}
				});
		near_expandablelistview
				.setOnChildClickListener(new OnChildClickListener() {
					public boolean onChildClick(ExpandableListView arg0,
							View arg1, int groupPosition, int childPosition,
							long arg4) {

						Intent intent = new Intent(NearActivity.this,
								InformationActivity.class);
						// intent.putExtra("name", name);
						startActivity(intent);
						return true;
					}
				});
		near_back.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.near_back:
			finish();
			break;

		}
	}
}