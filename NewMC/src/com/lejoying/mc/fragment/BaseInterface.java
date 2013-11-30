package com.lejoying.mc.fragment;

import android.support.v4.app.Fragment;

public interface BaseInterface {
	public void hideCircleMenu();

	public void showCircleMenuToTop(boolean lock, boolean showBack);

	public void showCircleMenuToBottom();

	public void setCircleMenuPageName(String pageName);

	public int relpaceToContent(Fragment fragment, boolean toBackStack);
}
