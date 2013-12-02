package com.lejoying.mc.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;

public interface BaseInterface {
	public void hideCircleMenu();

	public void showCircleMenuToTop(boolean lock, boolean showBack);

	public void showCircleMenuToBottom();

	public void setCircleMenuPageName(String pageName);

	public int relpaceToContent(Fragment fragment, boolean toBackStack);

	public void startNetworkForResult(int API, Bundle params,
			ReceiverListener listener);

	public void setNetworkRemainListener(RemainListener listener);

	public interface ReceiverListener {
		public void onReceive(int STATUS, String log);
	}

	public interface RemainListener {
		public String setRemainType();

		public void remain(int remain);
	}

}
