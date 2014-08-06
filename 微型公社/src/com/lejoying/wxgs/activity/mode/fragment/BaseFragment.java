package com.lejoying.wxgs.activity.mode.fragment;

import com.lejoying.wxgs.activity.MainActivity;
import com.lejoying.wxgs.activity.mode.MainModeManager;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.PauseOnScrollListener;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.EditText;

public abstract class BaseFragment extends Fragment {

	public InputMethodManager mInputMethodManager;

	private InputMethodManager getInputMethodManager() {
		if (mInputMethodManager == null) {
			mInputMethodManager = (InputMethodManager) getActivity()
					.getSystemService(Context.INPUT_METHOD_SERVICE);
		}
		return mInputMethodManager;
	}

	protected void showSoftInput(final EditText editText) {
		editText.requestFocus();
		getInputMethodManager().showSoftInput(editText,
				InputMethodManager.SHOW_FORCED);
	}

	protected boolean hideSoftInput() {
		boolean flag = false;
		if (getActivity().getCurrentFocus() != null) {
			flag = getInputMethodManager().hideSoftInputFromWindow(
					getActivity().getCurrentFocus().getWindowToken(), 0);
		}
		return flag;
	}

	/*********************************************/
	protected ImageLoader imageLoader = ImageLoader.getInstance();

	protected static final String STATE_PAUSE_ON_SCROLL = "STATE_PAUSE_ON_SCROLL";
	protected static final String STATE_PAUSE_ON_FLING = "STATE_PAUSE_ON_FLING";

	protected AbsListView listView;

	protected boolean pauseOnScroll = false;
	protected boolean pauseOnFling = true;

	// @Override
	// public void onRestoreInstanceState(Bundle savedInstanceState) {
	// pauseOnScroll = savedInstanceState.getBoolean(STATE_PAUSE_ON_SCROLL,
	// false);
	// pauseOnFling = savedInstanceState
	// .getBoolean(STATE_PAUSE_ON_FLING, true);
	// }

	@Override
	public void onResume() {
		super.onResume();
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		outState.putBoolean(STATE_PAUSE_ON_SCROLL, pauseOnScroll);
		outState.putBoolean(STATE_PAUSE_ON_FLING, pauseOnFling);
	}
}