package com.lejoying.mc.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.lejoying.mc.BaseFragmentActivity;
import com.lejoying.mc.BaseFragmentActivity.ReceiveListener;
import com.lejoying.mc.adapter.ToTryAdapter;
import com.lejoying.mc.utils.MCNetTools;
import com.lejoying.mc.utils.ToTry;

public abstract class BaseFragment extends Fragment implements ReceiveListener {

	public BaseInterface mMCFragmentManager;

	public InputMethodManager mInputMethodManager;

	protected abstract EditText showSoftInputOnShow();

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		BaseFragmentActivity baseFragmentActivity = (BaseFragmentActivity) activity;
		mMCFragmentManager = baseFragmentActivity;
		baseFragmentActivity.setReceiveListener(this);
	}

	@Override
	public void onResume() {
		super.onResume();
		if (showSoftInputOnShow() != null) {
			showSoftInput(showSoftInputOnShow());
		}
	}

	@Override
	public void onPause() {
		super.onPause();
		cleanMsg();
		hideSoftInput();
	}

	@Override
	public void onReceive(Context context, Intent intent) {

	}

	private InputMethodManager getInputMethodManager() {
		if (mInputMethodManager == null) {
			mInputMethodManager = (InputMethodManager) getActivity()
					.getSystemService(Context.INPUT_METHOD_SERVICE);
		}
		return mInputMethodManager;
	}

	protected void showSoftInput(final EditText editText) {
		editText.requestFocus();
		ToTry.tryDoing(10, 500, new ToTryAdapter() {
			@Override
			public boolean isSuccess() {
				boolean flag = false;
				flag = getInputMethodManager().showSoftInput(editText,
						InputMethodManager.SHOW_FORCED);
				return flag;
			}

			@Override
			public void successed(long time) {
				editText.requestFocus();
			}
		});
	}

	protected boolean hideSoftInput() {
		boolean flag = false;
		if (getActivity().getCurrentFocus() != null) {
			flag = getInputMethodManager().hideSoftInputFromWindow(
					getActivity().getCurrentFocus().getWindowToken(), 0);
		}
		return flag;
	}

	protected void showMsg(String message) {
		MCNetTools.showMsg(getActivity(), message);
	}

	protected void cleanMsg() {
		MCNetTools.cleanMsg();
	}
}
