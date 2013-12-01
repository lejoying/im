package com.lejoying.mc.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.lejoying.mc.R;

public class RegisterPassFragment extends BaseFragment implements
		OnClickListener {
	private View mContent;
	private EditText mView_pass;
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
		mContent = inflater.inflate(R.layout.f_registerpass, null);
		mView_pass = (EditText) mContent.findViewById(R.id.et_pass);
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

			break;

		default:
			break;
		}
	}

	@Override
	public EditText showSoftInputOnShow() {
		return mView_pass;
	}
}
