package com.lejoying.wxgs.activity;

import com.lejoying.wxgs.R;
import com.lejoying.wxgs.SysApplication;
import com.lejoying.wxgs.R.id;
import com.lejoying.wxgs.R.layout;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;

public class GroupAffirmActivity extends Activity implements OnClickListener {

	private Button groupaffirm_button;
	private ImageView groupaffirm_back;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_group_affirm);
		SysApplication.getInstance().addActivity(this);
		groupaffirm_button = (Button) findViewById(R.id.groupaffirm_button);
		groupaffirm_back = (ImageView) findViewById(R.id.groupaffirm_back);

		groupaffirm_button.setOnClickListener(this);
		groupaffirm_back.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.groupaffirm_button:
			finish();
			break;

		case R.id.groupaffirm_back:
			finish();
			break;
		}
	}

}
