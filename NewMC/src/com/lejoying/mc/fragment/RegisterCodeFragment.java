package com.lejoying.mc.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.lejoying.data.App;
import com.lejoying.mc.R;
import com.lejoying.mc.api.API;
import com.lejoying.mc.service.handler.MainServiceHandler;
import com.lejoying.mc.service.handler.NetworkRemain.RemainListener;

public class RegisterCodeFragment extends BaseFragment implements
		OnClickListener {
	private View mContent;
	private EditText mView_code;
	private Button mView_next;
	private TextView mView_sendcode;
	private TextView mView_phone;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mMCFragmentManager.showCircleMenuToTop(true, true);
		mContent = inflater.inflate(R.layout.f_registercode, null);
		mView_code = (EditText) mContent.findViewById(R.id.et_code);
		mView_next = (Button) mContent.findViewById(R.id.btn_next);
		mView_phone = (TextView) mContent.findViewById(R.id.tv_phone);
		mView_sendcode = (TextView) mContent.findViewById(R.id.tv_sendcode);
		mView_phone
				.setText(App.getInstance().registerBundle.getString("phone"));
		mView_next.setOnClickListener(this);
		mView_sendcode.setOnClickListener(this);

		mMCFragmentManager.setNetworkRemainListener(new RemainListener() {
			@Override
			public String setRemainType() {
				return MainServiceHandler.REMAIN_REGISTER;
			}

			@Override
			public void remain(int remain) {
				if (remain > 0) {
					mView_sendcode.setText(getString(R.string.tv_resend) + "("
							+ remain + ")");
				} else {
					mView_sendcode.setText(getString(R.string.tv_resend));
				}
			}
		});

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
			String code = mView_code.getText().toString();
			if (code.equals("")) {
				showMsg(getString(R.string.app_codenotnull));
				return;
			}
			Bundle nextParams = new Bundle();
			nextParams.putString("phone",
					App.getInstance().registerBundle.getString("phone"));
			nextParams.putString("code", code);
			mMCFragmentManager.startNetworkForResult(API.ACCOUNT_VERIFYCODE,
					nextParams, true, new NetworkStatusAdapter() {
						@Override
						public void success() {
							mMCFragmentManager.replaceToContent(
									new RegisterPassFragment(), true);
						}
					});
			break;
		case R.id.tv_sendcode:
			Bundle resendParams = App.getInstance().registerBundle;
			resendParams.remove("code");
			mMCFragmentManager.startNetworkForResult(API.ACCOUNT_VERIFYPHONE,
					resendParams, new NetworkStatusAdapter() {
						@Override
						public void success() {
						}
					});
			break;
		default:
			break;
		}
	}

	@Override
	public EditText showSoftInputOnShow() {
		return mView_code;
	}

}
