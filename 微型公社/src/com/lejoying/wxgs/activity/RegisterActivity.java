package com.lejoying.wxgs.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;

import com.lejoying.wxgs.R;

public class RegisterActivity extends BaseActivity {

	public static final String TAG = "RegisterActivity";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		// 启动activity时自动弹出软键盘
		getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
		View v = getLayoutInflater()
				.inflate(R.layout.activity_login_code, null);
		addContentView(v, new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT));
		v.startAnimation(AnimationUtils.loadAnimation(this,
				R.anim.translate_in_bottom));
		v.findViewById(R.id.button_clogin).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						startActivity(new Intent(RegisterActivity.this,
								LoginActivity.class));
					}
				});
		super.onCreate(savedInstanceState);
	}

	@Override
	protected void onResume() {
		// CircleMenu.showBack(this);
		super.onResume();
	}

	@Override
	public void initView() {
		// TODO Auto-generated method stub

	}

	@Override
	public void initEvent() {
		// TODO Auto-generated method stub

	}

}
