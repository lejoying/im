package com.lejoying.wxgs.activity.page;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.lejoying.wxgs.R;
import com.lejoying.wxgs.activity.LoginActivity;

public class PLoginPage extends BasePage {

	LoginActivity mLoginActivity;

	EditText mPLoginInputPhone;
	EditText mInputPass;
	View mButtonPLogin;
	View mButtonRegister;
	View mButtonOpenCLogin;

	public PLoginPage(LoginActivity loginActivity) {
		super(loginActivity);
		// TODO Auto-generated constructor stub
		this.mLoginActivity = loginActivity;
	}

	@Override
	public View initView(View content, LayoutInflater inflater) {
		content = inflater.inflate(R.layout.page_login_pass, null);
		mPLoginInputPhone = (EditText) content.findViewById(R.id.input_phone);
		mInputPass = (EditText) content.findViewById(R.id.input_pass);
		mButtonPLogin = content.findViewById(R.id.button_plogin);
		mButtonRegister = content.findViewById(R.id.button_register);
		mButtonOpenCLogin = content.findViewById(R.id.button_opencodelogin);
		return content;
	}

	@Override
	public void initEvent(View content) {
		// TODO Auto-generated method stub

	}

}
