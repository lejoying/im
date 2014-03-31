package com.lejoying.wxgs.activity;

import com.lejoying.wxgs.R;
import com.lejoying.wxgs.SysApplication;
import com.lejoying.wxgs.R.layout;
import com.lejoying.wxgs.R.menu;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class MsgActivity extends Activity implements OnClickListener{
	private Button squaremeg_add;
	private ImageView squaremeg_back ,squaremsg_imageview;
	private TextView squaremsg_name,squaremsg_sign,squaremsg_id,squaremsg_msg;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_msg);
		SysApplication.getInstance().addActivity(this);
		squaremeg_back=(ImageView) findViewById(R.id.squaremeg_back);
		squaremeg_add=(Button) findViewById(R.id.squaremeg_add);
		
		squaremeg_back.setOnClickListener(this);
		squaremeg_add.setOnClickListener(this);
	}

	@Override
	public void onClick(View view) {
		// TODO Auto-generated method stub
		switch (view.getId()) {
		case R.id.squaremeg_back:
			 finish();
			break;

		case R.id.squaremeg_add:
			startActivity( new Intent(MsgActivity.this,InformationActivity.class));  
			
			break;
		}
		
	}

}
