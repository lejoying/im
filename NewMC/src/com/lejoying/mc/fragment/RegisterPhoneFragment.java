package com.lejoying.mc.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.lejoying.mc.R;
import com.lejoying.mc.api.API;

public class RegisterPhoneFragment extends BaseFragment implements
		OnClickListener {
	private View mContent;
	private EditText mView_phone;
	private Button mView_next;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mMCFragmentManager.showCircleMenuToTop(true, true);
		mContent = inflater.inflate(R.layout.f_registerphone, null);
		mView_phone = (EditText) mContent.findViewById(R.id.et_phone);
		mView_next = (Button) mContent.findViewById(R.id.btn_next);
		mView_next.setOnClickListener(this);
		return mContent;
	}

	@Override
	public void onPause() {
		super.onPause();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_next:
			if (mView_phone.getText().toString().equals("")) {
				showMsg(getString(R.string.app_phonenotnull));
				showSoftInput(mView_phone);
				return;
			}
			final Bundle params = new Bundle();
			params.putString("phone", mView_phone.getText().toString());
			params.putString("usage", "register");
			mMCFragmentManager.startNetworkForResult(API.ACCOUNT_VERIFYPHONE,
					params, true, new NetworkStatusAdapter() {
						@Override
						public void success() {
							mMCFragmentManager.replaceToContent(
									new RegisterCodeFragment(), true);
						}
					});
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
