package com.lejoying.mc.fragment;

import android.app.Activity;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.lejoying.mc.adapter.ToTryAdapter;
import com.lejoying.mc.utils.ToTry;

public abstract class BaseFragment extends Fragment {

	public BaseInterface mMCFragmentManager;

	public FragmentManager mFragmentManager;

	public InputMethodManager mInputMethodManager;

	protected abstract EditText showSoftInputOnShow();

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		mMCFragmentManager = (BaseInterface) activity;
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
		hideSoftInput();
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		mMCFragmentManager.setFragmentKeyDownListener(null);
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
				if (mMCFragmentManager.circleMenuIsShow()) {
					flag = getInputMethodManager().showSoftInput(editText,
							InputMethodManager.SHOW_FORCED);
				}
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

	public FragmentManager getmFragmentManager() {
		if (mFragmentManager == null) {
			mFragmentManager = getActivity().getSupportFragmentManager();
		}
		return mFragmentManager;
	}

}
