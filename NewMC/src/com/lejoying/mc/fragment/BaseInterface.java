package com.lejoying.mc.fragment;

import android.support.v4.app.Fragment;
import android.view.KeyEvent;

public interface BaseInterface {

	public int replaceToContent(Fragment fragment, boolean toBackStack);

	public void startToActivity(Class<?> clazz, boolean finishSelf);

	public void setFragmentKeyDownListener(OnKeyDownListener listener);

	public interface OnKeyDownListener {
		public boolean onKeyDown(int keyCode, KeyEvent event);
	}

}
