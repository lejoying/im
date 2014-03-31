package com.lejoying.wxgs.activity;

import com.lejoying.wxgs.MyAlert;
import com.lejoying.wxgs.R;
import com.lejoying.wxgs.SysApplication;
import com.lejoying.wxgs.R.id;
import com.lejoying.wxgs.R.layout;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;

public class MsgInfoActivity extends Activity implements OnClickListener {
	private Button msginfo_del;
	private ImageView msginfo_back;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_msg_info);
		Bundle extras = getIntent().getExtras(); 
		
		SysApplication.getInstance().addActivity(this);
		msginfo_del = (Button) findViewById(R.id.msginfo_del);
		msginfo_back = (ImageView) findViewById(R.id.msginfo_back);
		if(extras.getString("Type").equals("square")){
			msginfo_del.setVisibility(Button.INVISIBLE);
		}else{
		msginfo_del.setOnClickListener(this);
		}
		msginfo_back.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.msginfo_del:
			MyAlert.dialog(MsgInfoActivity.this, "提示", "确定要删除吗？");
			break;

		case R.id.msginfo_back:
			finish();
			break;
		}
	}

	
}
