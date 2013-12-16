package com.lejoying.mc.fragment;

import com.lejoying.mc.R;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.EditText;

public class SearchFriendFragment extends BaseFragment implements
		OnClickListener {

	private View mContent;
	private EditText mView_phone;
	private View mView_search;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mMCFragmentManager.showCircleMenuToTop(true, true);
		mContent = inflater.inflate(R.layout.f_searchfriend, null);
		mView_phone = (EditText) mContent.findViewById(R.id.et_phone);
		mView_search = mContent.findViewById(R.id.btn_search);
		mView_search.setOnClickListener(this);
		return mContent;
	}

	@Override
	protected EditText showSoftInputOnShow() {
		return mView_phone;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_search:

			break;

		default:
			break;
		}
	}

}
