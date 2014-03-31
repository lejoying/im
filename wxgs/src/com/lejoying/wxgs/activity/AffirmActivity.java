package com.lejoying.wxgs.activity;

import com.lejoying.wxgs.R;
import com.lejoying.wxgs.SysApplication;
import com.lejoying.wxgs.R.layout;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;

public class AffirmActivity extends Activity implements OnClickListener{
	private Spinner affirm_spinner;
	private Button affirm_button;
	private ImageView affirm_back;
	private ArrayAdapter affirmAdapter;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_affirm);
		SysApplication.getInstance().addActivity(this);
		
		affirm_spinner=(Spinner) findViewById(R.id.affirm_spinner);
		affirm_button=(Button) findViewById(R.id.affirm_button);
		affirm_back=(ImageView) findViewById(R.id.affirm_back);
		
		
		String[]groupingName={"我的好友","我的同学","我的同事","陌生的人"};
		affirmAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, groupingName);
		affirmAdapter
		.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		affirm_spinner.setAdapter(affirmAdapter);
		
		affirm_button.setOnClickListener(this); 
		affirm_back.setOnClickListener(this); 
	}
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.affirm_button:
			finish();
			break;
		case R.id.affirm_back:
			finish();
			break;
		}
	}

	

}
