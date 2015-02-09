package com.open.welinks;

import com.open.lib.MyLog;
import com.open.welinks.model.Data;
import com.open.welinks.model.Parser;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class CreateGroupOkActivity extends Activity {

	public String tag = "CreateGroupOkActivity";
	public MyLog log = new MyLog(tag, false);

	public Data data = Data.getInstance();
	public Parser parser = Parser.getInstance();

	public RelativeLayout backView;
	public TextView backTitileView;
	public TextView titleView;

	public TextView createGroupOKView;

	public OnClickListener mOnClickListener;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initView();
		initializeListeners();
		bindEvent();
	}

	private void bindEvent() {
		this.backView.setOnClickListener(mOnClickListener);
		this.createGroupOKView.setOnClickListener(mOnClickListener);

	}

	private void initializeListeners() {
		mOnClickListener = new OnClickListener() {

			@Override
			public void onClick(View view) {
				if (view.equals(backView)) {
					finish();
				} else if (view.equals(createGroupOKView)) {
					finish();
				}
			}
		};
	}

	private void initView() {
		setContentView(R.layout.activity_creategroup_ok);
		this.backView = (RelativeLayout) findViewById(R.id.backView);
		this.backTitileView = (TextView) findViewById(R.id.backTitleView);
		this.backTitileView.setText("邀请好友");
		this.titleView = (TextView) findViewById(R.id.titleContent);
		this.createGroupOKView = (TextView) findViewById(R.id.createGroupOK);
	}
}
