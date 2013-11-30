package com.lejoying.mc.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lejoying.mc.R;

public class FriendsFragment extends BaseFragment {

	private View mContent;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mContent = inflater.inflate(R.layout.f_friends, null);
		mMCFragmentManager.showCircleMenuToTop(false, false);
		mMCFragmentManager.setCircleMenuPageName("√‹”—»¶");
		
		
		return mContent;
	}

	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}

}
