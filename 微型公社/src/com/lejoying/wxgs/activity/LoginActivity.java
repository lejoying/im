package com.lejoying.wxgs.activity;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;

import com.lejoying.wxgs.R;
import com.lejoying.wxgs.activity.view.widget.CircleMenu;

public class LoginActivity extends BaseActivity implements OnClickListener {

	public static final String TAG = "LoginActivity";

	View mCurrentView;

	View mPassLogin;
	EditText mPLoginInputPhone;
	EditText mInputPass;
	View mButtonPLogin;
	View mButtonRegister;
	View mButtonOpenCLogin;

	View mCodeLogin;
	EditText mCLoginInputPhone;
	EditText mInputCode;
	View mButtonCLogin;
	View mButtonSendCode;
	LayoutInflater mInflater;

	Animation mTranslateInTop;
	Animation mTranslateOutTop;
	Animation mTranslateInBottom;
	Animation mTranslateOutBottom;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		initView();
		initEvent();

		// 启动activity时自动弹出软键盘
		getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
		super.onCreate(savedInstanceState);
		overridePendingTransition(R.anim.activity_new, R.anim.activity_out);
	}

	@Override
	protected void onResume() {
		if (mCurrentView.getVisibility() == View.GONE) {
			mCurrentView.setVisibility(View.VISIBLE);
			mCurrentView.startAnimation(mTranslateInTop);
		}
		CircleMenu.show(this,CircleMenu.LOCATION_BOTTOM);
		if (mCurrentView.equals(mCodeLogin)) {
		} else {
			// CircleMenu.hide();
		}
		super.onResume();
	}

	public void initView() {
		mTranslateInTop = AnimationUtils.loadAnimation(this,
				R.anim.activity_back);
		mTranslateOutTop = AnimationUtils.loadAnimation(this,
				R.anim.activity_out);
		mTranslateInBottom = AnimationUtils.loadAnimation(this,
				R.anim.activity_new);
		mTranslateOutBottom = AnimationUtils.loadAnimation(this,
				R.anim.activity_finish);

		mInflater = getLayoutInflater();
		mPassLogin = mInflater.inflate(R.layout.page_login_pass, null);
		mCodeLogin = mInflater.inflate(R.layout.page_login_code, null);

		mPLoginInputPhone = (EditText) mPassLogin
				.findViewById(R.id.input_phone);
		mInputPass = (EditText) mPassLogin.findViewById(R.id.input_pass);
		mButtonPLogin = mPassLogin.findViewById(R.id.button_plogin);
		mButtonRegister = mPassLogin.findViewById(R.id.button_register);
		mButtonOpenCLogin = mPassLogin.findViewById(R.id.button_opencodelogin);

		mCLoginInputPhone = (EditText) mCodeLogin
				.findViewById(R.id.input_phone);
		mInputCode = (EditText) mCodeLogin.findViewById(R.id.input_code);
		mButtonCLogin = mCodeLogin.findViewById(R.id.button_clogin);
		mButtonSendCode = mCodeLogin.findViewById(R.id.button_sendcode);

		mCodeLogin.setVisibility(View.GONE);
		addContentView(mPassLogin, new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT));
		addContentView(mCodeLogin, new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT));
		mCurrentView = mPassLogin;
		mCurrentView.startAnimation(mTranslateInBottom);
	}

	public void initEvent() {
		mButtonPLogin.setOnClickListener(this);
		mButtonRegister.setOnClickListener(this);
		mButtonOpenCLogin.setOnClickListener(this);
		mButtonCLogin.setOnClickListener(this);
		mButtonSendCode.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.button_plogin:

			break;
		case R.id.button_register:
			mCurrentView.setVisibility(View.GONE);
			break;
		case R.id.button_opencodelogin:
			mCodeLogin.setVisibility(View.VISIBLE);
			mCLoginInputPhone.requestFocus();
			mPassLogin.startAnimation(mTranslateOutTop);
			mPassLogin.setVisibility(View.GONE);
			mCurrentView = mCodeLogin;
			mCurrentView.startAnimation(mTranslateInBottom);
			CircleMenu.showBack(LoginActivity.this);
			break;
		case R.id.button_clogin:

			break;
		case R.id.button_sendcode:

			break;

		default:
			break;
		}
	}

	@Override
	public void finish() {
		if (mCurrentView.equals(mCodeLogin)) {
			mCodeLogin.startAnimation(mTranslateOutBottom);
			mCodeLogin.setVisibility(View.GONE);
			mPassLogin.setVisibility(View.VISIBLE);
			mPLoginInputPhone.requestFocus();
			mCurrentView = mPassLogin;
			mCurrentView.startAnimation(mTranslateInTop);
			CircleMenu.hide();
		} else {
			super.finish();
		}
	}

}
