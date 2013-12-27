package com.lejoying.mc.fragment;

import com.lejoying.mc.R;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;

public class ModifyFragment extends BaseFragment implements OnClickListener {

	View mContent;
	TextView tv_name;
	EditText et_name;
	TextView tv_yewu;
	EditText et_yewu;
	View rl_yewu;

	@Override
	protected EditText showSoftInputOnShow() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mMCFragmentManager.showCircleMenuToTop(true, true);
		mContent = inflater.inflate(R.layout.f_modifyinfo, null);
		initView();
		return mContent;
	}

	public void initView() {
		tv_name = (TextView) mContent.findViewById(R.id.tv_name);
		et_name = (EditText) mContent.findViewById(R.id.et_name);
		tv_yewu = (TextView) mContent.findViewById(R.id.tv_yewu);
		et_yewu = (EditText) mContent.findViewById(R.id.et_yewu);
		rl_yewu = mContent.findViewById(R.id.rl_yewu);
		tv_name.setOnClickListener(this);
		tv_yewu.setOnClickListener(this);

	}

	void modifyMode() {
		tv_name.setVisibility(View.GONE);
		tv_yewu.setVisibility(View.GONE);
		et_name.setVisibility(View.VISIBLE);
		rl_yewu.setVisibility(View.VISIBLE);
	}

	void endModify() {
		tv_name.setVisibility(View.VISIBLE);
		tv_yewu.setVisibility(View.VISIBLE);
		et_name.setVisibility(View.GONE);
		rl_yewu.setVisibility(View.GONE);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.tv_name:
			modifyMode();
			break;
		case R.id.tv_yewu:
			modifyMode();
			break;

		default:
			break;
		}
	}
}
