package com.lejoying.mc.fragment;

import android.support.v4.app.Fragment;
import android.view.KeyEvent;

public interface BaseInterface {
	public void hideCircleMenu();

	public void showCircleMenuToTop(boolean lock, boolean showBack);

	public void showCircleMenuToBottom();

	public void setCircleMenuPageName(String pageName);

	public boolean circleMenuIsShow();

	public int replaceToContent(Fragment fragment, boolean toBackStack);

	public void startToActivity(Class<?> clazz, boolean finishSelf);

	public void setNotifyListener(NotifyListener notifyListener);

	public interface NotifyListener {
		public static final int NOTIFY_MESSAGEANDFRIEND = 0x01;

		public void notifyDataChanged(int notify);
	}

	public void setFragmentKeyDownListener(OnKeyDownListener listener);

	public interface OnKeyDownListener {
		public boolean onKeyDown(int keyCode, KeyEvent event);
	}

}
