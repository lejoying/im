package com.lejoying.mc.fragment;

import com.lejoying.mc.R;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

public class FriendEditTabFragment extends BaseFragment {

	private View mContent;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mContent = inflater.inflate(R.layout.f_friendedittab, null);
		return mContent;
	}

	@Override
	protected EditText showSoftInputOnShow() {
		// TODO Auto-generated method stub
		return null;
	}

}
