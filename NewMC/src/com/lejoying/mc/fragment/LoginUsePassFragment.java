package com.lejoying.mc.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.lejoying.mc.R;

public class LoginUsePassFragment extends BaseFragment implements
		OnClickListener {

	LoginUsrPassListener listener;

	private View mContent;

	private EditText mView_phone;
	private EditText mView_pass;
	private Button mView_login;
	private Button mView_register;
	private TextView mView_clogin;

	@Override
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		super.onAttach(activity);
		try {
			listener = (LoginUsrPassListener) activity;
		} catch (ClassCastException e) {
			
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mContent = inflater.inflate(R.layout.f_plogin, null);

		mView_phone = (EditText) mContent.findViewById(R.id.et_plogin_phone);
		mView_pass = (EditText) mContent.findViewById(R.id.et_plogin_pass);
		mView_login = (Button) mContent.findViewById(R.id.btn_login);
		mView_register = (Button) mContent.findViewById(R.id.btn_register);
		mView_clogin = (TextView) mContent.findViewById(R.id.tv_clogin);

		initView();
		return mContent;
	}

	private void initView() {
		mView_login.setOnClickListener(this);
		mView_register.setOnClickListener(this);
		mView_clogin.setOnClickListener(this);
	}

	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}

	@Override
	public void onClick(View v) {
		listener.onClick(v);
	}

	public interface LoginUsrPassListener {
		public void onClick(View v);
	}
}
