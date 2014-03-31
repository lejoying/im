package com.lejoying.wxgs.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.lejoying.wxgs.R;
import com.lejoying.wxgs.SysApplication;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.SimpleAdapter;

public class OnlineActivity extends Activity implements OnClickListener{
	
	private GridView onlineView;
	private ImageView back;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_online);
		SysApplication.getInstance().addActivity(this);
		onlineView=(GridView)findViewById(R.id.online_GridView);
		back=(ImageView) findViewById(R.id.online_Back);
		
		SimpleAdapter adapter = new SimpleAdapter(this,getData(),R.layout.square_online,
				new String[]{"picture","name"},
				new int[]{R.id.online_person,R.id.online_name});
		onlineView.setAdapter(adapter);
		
		onlineView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(OnlineActivity.this,
						InformationActivity.class);
				// intent.putExtra("name", name);
				startActivity(intent);
			}
		});
		back.setOnClickListener(this); 
	}

	private  List<Map<String, Object>>  getData() {
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		Map<String, Object> map ;
		for(int i=0;i<200;i++){
			map=new HashMap<String, Object>();
			map.put("picture",  R.drawable.ic_launcher);
			map.put("name", i);
			list.add(map);
		}
		return list;
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.online_Back:
			finish();
			break;

		}
	}
	

}
