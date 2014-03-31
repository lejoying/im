package com.lejoying.wxgs.activity;

import com.lejoying.wxgs.R;
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
import android.widget.TextView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ExpandableListView.OnGroupCollapseListener;
import android.widget.ExpandableListView.OnGroupExpandListener;

public class GroupInfoActivity extends Activity implements OnClickListener {
	private ImageView groupinfo_back, groupinfo_imageview;
	private Button groupinfo_add;
	private TextView groupinfo_name;
	private ExpandableListView groupinfo_expandablelistview;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_group_info);
		groupinfo_back = (ImageView) findViewById(R.id.groupinfo_back);
		groupinfo_add = (Button) findViewById(R.id.groupinfo_add);
		groupinfo_expandablelistview = (ExpandableListView) findViewById(R.id.groupinfo_expandablelistview);

		// Ⱥ�����
		String[] group = new String[] { "我的群组" };
		// �������
		String[][] buddy = new String[][] { { "ABCDEFG", "HIJKLMN", "OPQRST",
				"UVWXYZ" } };

		ExpandableListAdapter adapter = new MyExpandableListAdapter(this,
				group, buddy);
		groupinfo_expandablelistview.setAdapter(adapter);
		groupinfo_expandablelistview.setGroupIndicator(null);

		// ����չ��
		groupinfo_expandablelistview
				.setOnGroupExpandListener(new OnGroupExpandListener() {
					public void onGroupExpand(int groupPosition) {
					}
				});
		// ����ر�
		groupinfo_expandablelistview
				.setOnGroupCollapseListener(new OnGroupCollapseListener() {
					public void onGroupCollapse(int groupPosition) {
					}
				});
		groupinfo_expandablelistview
				.setOnChildClickListener(new OnChildClickListener() {
					public boolean onChildClick(ExpandableListView arg0,
							View arg1, int groupPosition, int childPosition,
							long arg4) {

						return false;
					}
				});

		groupinfo_back.setOnClickListener(this);
		groupinfo_add.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.groupinfo_back:
			finish();
			break;

		case R.id.groupinfo_add:
			Intent intent = new Intent(GroupInfoActivity.this,
					GroupAffirmActivity.class);
			// intent.putExtra("name", name);
			startActivity(intent);
			
			break;
		}
	}

}
