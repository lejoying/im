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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

public class MyActivity extends Activity implements OnClickListener {
	private ImageView myinfo_back, myinfo_save, myinfo_alter;
	private EditText myinfo_editText_sign, myinfo_editText_nick;
	private RadioGroup mRadioGroup;
	private RadioButton man, woman;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_my);
		SysApplication.getInstance().addActivity(this);

		myinfo_back = (ImageView) findViewById(R.id.myinfo_back);
		myinfo_save = (ImageView) findViewById(R.id.myinfo_save);
		myinfo_alter = (ImageView) findViewById(R.id.myinfo_alter);
		mRadioGroup = (RadioGroup) findViewById(R.id.my_radiogroup);
		man = (RadioButton) findViewById(R.id.man);
		woman = (RadioButton) findViewById(R.id.woman);

		mRadioGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup arg0, int arg1) {
				// TODO Auto-generated method stub

			}
		});

		myinfo_back.setOnClickListener(this);
		myinfo_save.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.myinfo_back:
			finish();
			break;

		case R.id.myinfo_save:
			finish();
			break;

		}

	}

}
