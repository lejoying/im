package com.lejoying.mc;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.lejoying.mc.fragment.BaseInterface;
import com.lejoying.mc.fragment.CircleMenuFragment;

public abstract class BaseFragmentActivity extends FragmentActivity implements
		BaseInterface {

	private FragmentManager mFragmentManager;

	private CircleMenuFragment circle;
	private FragmentTransaction mFragmentTransaction;

	public abstract boolean createCircleMenu();

	public abstract Fragment setFirstPreview();

	@Override
	protected void onCreate(Bundle arg0) {
		// TODO Auto-generated method stub
		super.onCreate(arg0);
		mFragmentManager = getSupportFragmentManager();
		
		if (createCircleMenu()) {
			circle = new CircleMenuFragment();
			mFragmentManager.beginTransaction().add(R.id.fl_circleMenu, circle)
					.commit();
		}

		if (setFirstPreview() != null) {
			mFragmentManager.beginTransaction()
					.add(R.id.fl_content, setFirstPreview()).commit();
		}
	}
	
	@Override
	public void popBackStack() {
		mFragmentManager.popBackStack();
	}

	@Override
	public void hideCircleMenu() {
		circle.hideCircleMenu(null);
	}

	@Override
	public void showCircleMenuToTop(boolean lock, boolean showBack) {
		circle.showToTop(lock, showBack);
	}

	@Override
	public void showCircleMenuToBottom() {
		circle.showToBottom();
	}

	@Override
	public FragmentTransaction beginTransaction() {
		mFragmentTransaction = mFragmentManager.beginTransaction();
		mFragmentTransaction.setCustomAnimations(R.anim.activity_in,
				R.anim.activity_out, R.anim.activity_in2, R.anim.activity_out2);
		return mFragmentTransaction;
	}

	@Override
	public FragmentTransaction add(Fragment fragment, String tag) {
		return mFragmentTransaction.add(fragment, tag);
	}

	@Override
	public FragmentTransaction add(int contentView, Fragment fragment) {
		return mFragmentTransaction.add(contentView, fragment);
	}

	@Override
	public FragmentTransaction add(int contentView, Fragment fragment,
			String tag) {
		return mFragmentTransaction.add(contentView, fragment, tag);
	}

	@Override
	public FragmentTransaction addToBackStack(String tag) {
		return mFragmentTransaction.addToBackStack(tag);
	}

	@Override
	public FragmentTransaction setCustomAnimations(int enter, int exit,
			int popEnter, int popExit) {
		return mFragmentTransaction.setCustomAnimations(enter, exit, popEnter,
				popExit);
	}

	@Override
	public FragmentTransaction setCustomAnimations(int enter, int exit) {
		return mFragmentTransaction.setCustomAnimations(enter, exit);
	}

	@Override
	public FragmentTransaction replace(int contentView, Fragment fragment) {
		return mFragmentTransaction.replace(contentView, fragment);
	}

	@Override
	public FragmentTransaction replace(int contentView, Fragment fragment,
			String tag) {
		return mFragmentTransaction.replace(contentView, fragment, tag);
	}

	@Override
	public int commit() {
		return mFragmentTransaction.commit();
	}

}
