package com.lejoying.mc.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.lejoying.mc.MainActivity;
import com.lejoying.mc.R;
import com.lejoying.mc.network.API;

public class LoginUseCodeFragment extends BaseFragment implements
		OnClickListener {
	private View mContent;

	private EditText mView_phone;
	private EditText mView_code;
	private Button mView_login;
	private TextView mView_sendcode;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mMCFragmentManager.showCircleMenuToTop(true, true);
		mContent = inflater.inflate(R.layout.f_clogin, null);

		mView_phone = (EditText) mContent.findViewById(R.id.et_clogin_phone);
		mView_code = (EditText) mContent.findViewById(R.id.et_clogin_code);
		mView_login = (Button) mContent.findViewById(R.id.btn_login);
		mView_sendcode = (TextView) mContent.findViewById(R.id.tv_sendcode);

		mView_login.setOnClickListener(this);
		mView_sendcode.setOnClickListener(this);

		

		return mContent;
	}

	@Override
	public void onPause() {
		super.onPause();
	}
	
	@Override
	public void onClick(View v) {
		String phone = mView_phone.getText().toString();
		String code = mView_code.getText().toString();
		if (phone == null || phone.equals("")) {
			showMsg(getString(R.string.app_phonenotnull));
			return;
		}
		switch (v.getId()) {
		case R.id.btn_login:
			if (code == null || code.equals("")) {
				showMsg(getString(R.string.app_codenotnull));
				return;
			}
			Bundle params = new Bundle();
			params.putString("phone", phone);
			params.putString("code", code);
			// mMCFragmentManager.startNetworkForResult(API.ACCOUNT_VERIFYCODE,
			// params, true, new NetworkStatusAdapter() {
			// @Override
			// public void success() {
			// mMCFragmentManager.startToActivity(
			// MainActivity.class, true);
			// }
			// });
			break;
		case R.id.tv_sendcode:
			if (phone == null || phone.equals("")) {
				showMsg(getString(R.string.app_phonenotnull));
				return;
			}
			Bundle verifyParams = new Bundle();
			verifyParams.putString("phone", phone);
			verifyParams.putString("usage", "login");
			// mMCFragmentManager.startNetworkForResult(API.ACCOUNT_VERIFYPHONE,
			// verifyParams, new NetworkStatusAdapter() {
			// @Override
			// public void success() {
			//
			// }
			// });

			break;
		default:
			break;
		}
	}

	@Override
	public EditText showSoftInputOnShow() {
		return mView_phone;
	}

}
