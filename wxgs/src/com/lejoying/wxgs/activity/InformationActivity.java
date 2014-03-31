package com.lejoying.wxgs.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.lejoying.wxgs.R;
import com.lejoying.wxgs.SysApplication;
import com.lejoying.wxgs.R.id;
import com.lejoying.wxgs.R.layout;
import com.lejoying.wxgs.adapter.InfoExpandableListAdapter;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Paint;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ExpandableListView.OnGroupCollapseListener;
import android.widget.ExpandableListView.OnGroupExpandListener;

public class InformationActivity extends Activity implements OnClickListener {
	private ImageView info_back, info_imageview;
	private TextView info_name, info_sign, info_id;
	private Button info_add;
	private ExpandableListView info_expandablelistview;
	private GridView info_gridview;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_information);
		SysApplication.getInstance().addActivity(this);

		info_expandablelistview = (ExpandableListView) findViewById(R.id.info_expandablelistview);
		info_gridview = (GridView) findViewById(R.id.info_gridview);
		info_back = (ImageView) findViewById(R.id.info_back);
		info_add = (Button) findViewById(R.id.info_add);
		info_name = (TextView) findViewById(R.id.info_name);
		info_sign = (TextView) findViewById(R.id.info_sign);
		info_id = (TextView) findViewById(R.id.info_id);

		// Ⱥ�����
		String[] group = new String[] { "用户的好友" };
		// �������
		String[][] buddy = new String[][] { { "ABCDEFG", "HIJKLMN", "OPQRST",
				"UVWXYZ" } };

		ExpandableListAdapter adapter = new InfoExpandableListAdapter(this,
				group, buddy);
		info_expandablelistview.setAdapter(adapter);
		info_expandablelistview.setGroupIndicator(null);

		// ����չ��
		info_expandablelistview
				.setOnGroupExpandListener(new OnGroupExpandListener() {
					public void onGroupExpand(int groupPosition) {
					}
				});
		// ����ر�
		info_expandablelistview
				.setOnGroupCollapseListener(new OnGroupCollapseListener() {
					public void onGroupCollapse(int groupPosition) {
					}
				});
		info_expandablelistview
				.setOnChildClickListener(new OnChildClickListener() {
					public boolean onChildClick(ExpandableListView arg0,
							View arg1, int groupPosition, int childPosition,
							long arg4) {

						return false;
					}
				});

		SimpleAdapter my_adapter = new SimpleAdapter(this, getInfoGroupData(),
				R.layout.my_group, new String[] { "picture", "name" },
				new int[] { R.id.my_group, R.id.my_group_name });
		info_gridview.setAdapter(my_adapter);
		info_gridview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub

			}
		});

		info_back.setOnClickListener(this); 
		info_add.setOnClickListener(this); 
	}

	private List<Map<String, Object>> getInfoGroupData() {
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		Map<String, Object> map;
		for (int i = 0; i < 20; i++) {
			map = new HashMap<String, Object>();
			map.put("picture", R.drawable.ic_launcher);
			map.put("name", i);
			list.add(map);
		}
		return list;
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.info_back:
			finish();
			break;

		case R.id.info_add:
			Intent intent = new Intent(InformationActivity.this,
					AffirmActivity.class);
			// intent.putExtra("name", name);
			startActivity(intent);
			break;
		}
	}
}
