package com.lejoying.mc.fragment;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.support.v4.app.ListFragment;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.lejoying.mc.R;
import com.lejoying.mc.adapter.ToTryAdapter;
import com.lejoying.mc.listener.NetworkStatusListener;
import com.lejoying.mc.service.NetworkService;
import com.lejoying.mc.utils.MCNetTools;
import com.lejoying.mc.utils.ToTry;

public abstract class BaseListFragment extends ListFragment {

	public BaseInterface mMCFragmentManager;

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
		getListView().setCacheColorHint(Color.TRANSPARENT);
	}

	@Override
	public void onPause() {
		super.onPause();
		cleanMsg();
		hideSoftInput();
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		mMCFragmentManager.setNetworkRemainListener(null);
		mMCFragmentManager.setFragmentKeyDownListener(null);
		mMCFragmentManager.setNotifyListener(null);
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

	protected abstract class NetworkStatusAdapter implements
			NetworkStatusListener {
		@Override
		public void onReceive(int STATUS, String log) {
			switch (STATUS) {
			case NetworkService.STATUS_SUCCESS:
				success();
				break;
			case NetworkService.STATUS_UNSUCCESS:
				unSuccess(log);
				break;
			case NetworkService.STATUS_NOINTERNET:
				noInternet();
				break;
			case NetworkService.STATUS_FAILED:
				failed();
				break;

			default:
				break;
			}
		}

		public abstract void success();

		public void unSuccess(String log) {
			showMsg(log);
		}

		public void noInternet() {
			showMsg(getString(R.string.app_nointernet));

		}

		public void failed() {
			showMsg(getString(R.string.app_timeout));
		}
	}
}
