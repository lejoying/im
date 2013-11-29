package com.lejoying.mc.fragment;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;

public interface BaseInterface {
	public void hideCircleMenu();

	public void showCircleMenuToTop(boolean lock, boolean showBack);

	public void showCircleMenuToBottom();

	public void popBackStack();

	public FragmentTransaction beginTransaction();

	public FragmentTransaction add(Fragment fragment, String tag);

	public FragmentTransaction add(int contentView, Fragment fragment);

	public FragmentTransaction add(int contentView, Fragment fragment,
			String tag);

	public FragmentTransaction addToBackStack(String tag);

	public FragmentTransaction setCustomAnimations(int enter, int exit,
			int popEnter, int popExit);

	public FragmentTransaction setCustomAnimations(int enter, int exit);

	public FragmentTransaction replace(int contentView, Fragment fragment);

	public FragmentTransaction replace(int contentView, Fragment fragment,
			String tag);

	public int commit();
}
