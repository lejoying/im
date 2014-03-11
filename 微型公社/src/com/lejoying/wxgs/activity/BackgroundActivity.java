package com.lejoying.wxgs.activity;

import com.lejoying.wxgs.R;
import com.lejoying.wxgs.activity.view.widget.CircleMenu;

public class BackgroundActivity extends BaseActivity {

	boolean isCreated;

	@Override
	protected void onCreate(android.os.Bundle savedInstanceState) {
		setContentView(R.layout.activity_background);
		super.onCreate(savedInstanceState);
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		if (isCreated) {
			CircleMenu.hide();
			finish();
		}
		isCreated = true;
		super.onResume();
	}

}
