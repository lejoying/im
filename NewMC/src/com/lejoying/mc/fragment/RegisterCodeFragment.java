package com.lejoying.mc.fragment;

import android.os.Bundle;
import android.telephony.NeighboringCellInfo;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.lejoying.mc.BaseFragmentActivity.NetworkRemainReceiver;
import com.lejoying.mc.R;
import com.lejoying.mc.api.API;
import com.lejoying.mc.fragment.BaseInterface.RemainListener;
import com.lejoying.mc.service.NetworkService;

public class RegisterCodeFragment extends BaseFragment implements
		OnClickListener {
	private View mContent;
	private EditText mView_code;
	private Button mView_next;
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
		mContent = inflater.inflate(R.layout.f_registercode, null);
		mView_code = (EditText) mContent.findViewById(R.id.et_code);
		mView_next = (Button) mContent.findViewById(R.id.btn_next);
		mView_sendcode = (TextView) mContent.findViewById(R.id.tv_sendcode);

		mView_next.setOnClickListener(this);
		mView_sendcode.setOnClickListener(this);

		mMCFragmentManager.setNetworkRemainListener(new RemainListener() {
			@Override
			public String setRemainType() {
				return NetworkService.REMAIN_REGISTER;
			}

			@Override
			public void remain(int remain) {
				System.out.println(remain);
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
			getActivity().getSupportFragmentManager().popBackStack();
			getActivity().getSupportFragmentManager().popBackStack();
			mMCFragmentManager.relpaceToContent(new RegisterPassFragment(),
					true);
			break;
		case R.id.tv_sendcode:

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
