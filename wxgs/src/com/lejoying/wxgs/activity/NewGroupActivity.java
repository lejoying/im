package com.lejoying.wxgs.activity;

import com.lejoying.wxgs.R;
import com.lejoying.wxgs.SysApplication;
import com.lejoying.wxgs.R.layout;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;

public class NewGroupActivity extends Activity implements OnClickListener{

	private ImageView newgroup_save,newgroup_back;
	private EditText newgroup_groupname;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_new_group);
		SysApplication.getInstance().addActivity(this);
		
		newgroup_save=(ImageView) findViewById(R.id.newgroup_save);
		newgroup_back=(ImageView) findViewById(R.id.newgroup_back);
	
		newgroup_save.setOnClickListener(this);
		newgroup_back.setOnClickListener(this);
	}

	@Override
	public void onClick(View view) {
		// TODO Auto-generated method stub
		switch (view.getId()) {
		case R.id.newgroup_save:
			finish();
			break;

		case R.id.newgroup_back:
			finish();
			break;
		}
		
	}

	
}
