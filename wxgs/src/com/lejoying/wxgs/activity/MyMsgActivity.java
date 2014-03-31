package com.lejoying.wxgs.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.lejoying.wxgs.MyAlert;
import com.lejoying.wxgs.R;
import com.lejoying.wxgs.SysApplication;
import com.lejoying.wxgs.adapter.MyMsgAdapter;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.AdapterView.OnItemClickListener;

public class MyMsgActivity extends Activity implements OnClickListener {
	private ImageView mymsg_back;
	private Button mymsg_new,mymsg_delall;
	private ListView mymsg_list;
	private int screenWidth;
	private String TYPE_SQUAREMSG="personal";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_my_msg);
		SysApplication.getInstance().addActivity(this);
		
		WindowManager wm = this.getWindowManager();
		screenWidth= wm.getDefaultDisplay().getWidth();
		mymsg_delall=(Button) findViewById(R.id.mymsg_delall);
		mymsg_back = (ImageView) findViewById(R.id.mymsg_back);
		mymsg_new = (Button) findViewById(R.id.mymsg_new);
		mymsg_list = (ListView) findViewById(R.id.mymsg_list);
		mymsg_back.setOnClickListener(this);
		mymsg_new.setOnClickListener(this);
		mymsg_delall.setOnClickListener(this);
//		SimpleAdapter adapter = new SimpleAdapter(this, getMessageData(),
//				R.layout.message_listview, new String[] { "picture", "name",
//						"speak", "time" }, new int[] { R.id.message_person,
//						R.id.message_name, R.id.message_speak,
//						R.id.message_time });
		
		MyMsgAdapter adapter=new MyMsgAdapter(this, screenWidth, getMessageData());
		
		mymsg_list.setAdapter(adapter);

		
		mymsg_list.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long id) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(MyMsgActivity.this,
						MsgInfoActivity.class);
				intent.putExtra("Type", TYPE_SQUAREMSG);
				startActivity(intent);
			}
		});
	}

	private List<Map<String, Object>> getMessageData() {
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();

		Map<String, Object> map = new HashMap<String, Object>();

		for (int i = 0; i < 50; i++) {
			map = new HashMap<String, Object>();
			map.put("picture", R.drawable.person);
			map.put("name", "My Messages " + i);
			map.put("speak", "This is my msg "+i);
			map.put("time", "00:00");

			list.add(map);
		}

		return list;
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.mymsg_back:
			finish();
			break;

		case R.id.mymsg_new:
			startActivity(new Intent(MyMsgActivity.this, SendActivity.class));
			break;
		case R.id.mymsg_delall:
			MyAlert.dialog(this, "提示", "全部删除后将不可恢复，是否继续");
			break;
		}
	}

}
