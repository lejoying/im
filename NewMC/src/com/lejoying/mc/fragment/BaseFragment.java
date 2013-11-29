package com.lejoying.mc.fragment;

import com.lejoying.mc.utils.MCTools;

import android.app.Activity;
import android.os.Handler;
import android.support.v4.app.Fragment;

public abstract class BaseFragment extends Fragment {

	protected BaseInterface mMCFragmentManager;

	public abstract String setTag();

	protected Handler handler;

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		mMCFragmentManager = (BaseInterface) activity;
		handler = MCTools.handler;
	}

}
