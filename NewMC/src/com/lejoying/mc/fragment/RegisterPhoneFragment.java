package com.lejoying.mc.fragment;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.lejoying.mc.R;
import com.lejoying.mc.adapter.MCResponseAdapter;
import com.lejoying.mc.api.AccountManager;
import com.lejoying.mc.apiimpl.AccountManagerImpl;
import com.lejoying.mc.service.SMSService;

public class RegisterPhoneFragment extends BaseFragment implements
		OnClickListener {
	private View mContent;
	private EditText mView_phone;
	private Button mView_next;

	private AccountManager mAccountManager;

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

		mAccountManager = new AccountManagerImpl(getActivity());

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
				showMsg("手机号不能为空");
				showSoftInput(mView_phone);
				return;
			}
			Map<String, String> param = new HashMap<String, String>();
			param.put("phone", mView_phone.getText().toString());
			param.put("usage", "register");
			mAccountManager.verifyphone(param, new MCResponseAdapter(
					getActivity()) {
				@Override
				public void success(JSONObject data) {
					mMCFragmentManager.relpaceToContent(
							new RegisterCodeFragment(), true);
					Intent intent = new Intent(getActivity(), SMSService.class);
					intent.putExtra("action", SMSService.ACTION_REGISTER);
					getActivity().startService(intent);
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
