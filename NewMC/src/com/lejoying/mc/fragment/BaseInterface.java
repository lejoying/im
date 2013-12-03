package com.lejoying.mc.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;

public interface BaseInterface {
	public void hideCircleMenu();

	public void showCircleMenuToTop(boolean lock, boolean showBack);

	public void showCircleMenuToBottom();

	public void setCircleMenuPageName(String pageName);

	public int relpaceToContent(Fragment fragment, boolean toBackStack);

	public void startToActivity(Class<?> clazz, boolean finishSelf);

	public void startNetworkForResult(String api, Bundle params,
			ReceiverListener listener);

	public void startNetworkForResult(String api, Bundle params,
			boolean showLoading, ReceiverListener listener);

	public void setNetworkRemainListener(RemainListener listener);

	public void setFragmentKeyDownListener(OnKeyDownListener listener);

	public interface OnKeyDownListener {
		public boolean onKeyDown(int keyCode, KeyEvent event);
	}

	public interface ReceiverListener {
		public void onReceive(int STATUS, String log);
	}

	public interface RemainListener {
		public String setRemainType();

		public void remain(int remain);
	}

}
