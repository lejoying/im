package com.open.welinks;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.open.lib.MyLog;
import com.open.welinks.model.Data;
import com.open.welinks.model.Parser;

public class CreateGroupStartActivity extends Activity {

	public Data data = Data.getInstance();
	public Parser parser = Parser.getInstance();
	public String tag = "CreateGroupStartActivity";
	public MyLog log = new MyLog(tag, false);

	public RelativeLayout backView;
	public TextView backTitileView;
	public TextView titleView;

	public EditText groupNameView;
	public TextView groupPositionView;

	public TextView okButtonView;

	public OnClickListener mOnClickListener;

	public String address, latitude, longitude;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initView();
		initializeListeners();
		bindEvent();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == R.id.tag_first && resultCode == Activity.RESULT_OK && data != null) {
			longitude = data.getStringExtra("longitude");
			latitude = data.getStringExtra("latitude");
			address = data.getStringExtra("address");
			groupPositionView.setText(address);
		}
	}

	private void initView() {
		setContentView(R.layout.activity_creategroupstart);
		this.backView = (RelativeLayout) findViewById(R.id.backView);
		this.backTitileView = (TextView) findViewById(R.id.backTitleView);
		this.backTitileView.setText("创建群组");
		this.titleView = (TextView) findViewById(R.id.titleContent);

		this.groupNameView = (EditText) findViewById(R.id.groupName);
		this.groupPositionView = (TextView) findViewById(R.id.groupPosition);

		this.okButtonView = (TextView) findViewById(R.id.okButton);
	}

	private void initializeListeners() {
		mOnClickListener = new OnClickListener() {

			@Override
			public void onClick(View view) {
				if (view.equals(backView)) {
					finish();
				} else if (view.equals(okButtonView)) {
					Intent intent = new Intent(CreateGroupStartActivity.this, CreateGroupOkActivity.class);
					startActivity(intent);
				} else if (view.equals(groupPositionView)) {
					Intent intent = new Intent(CreateGroupStartActivity.this, CreateGroupLocationActivity.class);
					startActivityForResult(intent, R.id.tag_first);
				}
			}
		};
	}

	private void bindEvent() {
		this.backView.setOnClickListener(mOnClickListener);
		this.okButtonView.setOnClickListener(mOnClickListener);
		this.groupPositionView.setOnClickListener(mOnClickListener);
	}
}
