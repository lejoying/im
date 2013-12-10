package com.lejoying.mc.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;

import com.lejoying.mc.service.handler.DataHandler.NotifyListener;
import com.lejoying.mc.service.handler.NetworkHandler.NetworkStatusListener;
import com.lejoying.mc.service.handler.NetworkRemain.RemainListener;

public interface BaseInterface {
	public void hideCircleMenu();

	public void showCircleMenuToTop(boolean lock, boolean showBack);

	public void showCircleMenuToBottom();

	public void setCircleMenuPageName(String pageName);
	
	public boolean circleMenuIsShow();

	public int relpaceToContent(Fragment fragment, boolean toBackStack);

	public void startToActivity(Class<?> clazz, boolean finishSelf);

	public void startNetworkForResult(String api, Bundle params,
			NetworkStatusListener listener);

	public void startNetworkForResult(String api, Bundle params,
			boolean showLoading, NetworkStatusListener listener);

	public void setNetworkRemainListener(RemainListener listener);

	public void setNotifyListener(NotifyListener listener);

	public void setFragmentKeyDownListener(OnKeyDownListener listener);

	public interface OnKeyDownListener {
		public boolean onKeyDown(int keyCode, KeyEvent event);
	}

}
