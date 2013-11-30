package com.lejoying.mc;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;

import com.lejoying.mc.fragment.BaseInterface;
import com.lejoying.mc.fragment.CircleMenuFragment;

public abstract class BaseFragmentActivity extends FragmentActivity implements
		BaseInterface {

	private FragmentManager mFragmentManager;

	private CircleMenuFragment mCircle;

	private int mContentId;

	// private FragmentTransaction mFragmentTransaction;

	public abstract boolean createCircleMenu();

	public abstract Fragment setFirstPreview();

	public abstract int setContentFragmentId();

	@Override
	protected void onCreate(Bundle arg0) {
		// TODO Auto-generated method stub
		super.onCreate(arg0);
		mFragmentManager = getSupportFragmentManager();

		mContentId = setContentFragmentId();

		if (createCircleMenu()) {
			mCircle = new CircleMenuFragment();
			mFragmentManager.beginTransaction()
					.add(R.id.fl_circleMenu, mCircle).commit();
		}

		if (setFirstPreview() != null) {
			if (mFragmentManager.getFragments() == null
					|| mFragmentManager.getFragments().size() == 0) {
				mFragmentManager.beginTransaction()
						.add(R.id.fl_content, setFirstPreview()).commit();
			}
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (mCircle != null) {
			if (keyCode == KeyEvent.KEYCODE_BACK && mCircle.cancelMenu()) {
				return true;
			}
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public void hideCircleMenu() {
		mCircle.hideCircleMenu(null);
	}

	@Override
	public void showCircleMenuToTop(boolean lock, boolean showBack) {
		mCircle.showToTop(lock, showBack);
	}

	@Override
	public void showCircleMenuToBottom() {
		mCircle.showToBottom();
	}

	@Override
	public void setCircleMenuPageName(String pageName) {
		mCircle.setPageName(pageName);
	}

	@Override
	public int relpaceToContent(Fragment fragment, boolean toBackStack) {
		FragmentTransaction transaction = mFragmentManager.beginTransaction();
		transaction.setCustomAnimations(R.anim.activity_in,
				R.anim.activity_out, R.anim.activity_in2, R.anim.activity_out2);
		transaction.replace(mContentId, fragment);
		if (toBackStack) {
			transaction.addToBackStack(null);
		}
		return transaction.commit();
	}

	// @Override
	// public void popBackStack() {
	// mFragmentManager.popBackStack();
	// }
	//
	//
	// @Override
	// public FragmentTransaction beginTransaction() {
	// mFragmentTransaction = mFragmentManager.beginTransaction();
	// mFragmentTransaction.setCustomAnimations(R.anim.activity_in,
	// R.anim.activity_out, R.anim.activity_in2, R.anim.activity_out2);
	// return mFragmentTransaction;
	// }
	//
	// @Override
	// public FragmentTransaction add(Fragment fragment, String tag) {
	// return mFragmentTransaction.add(fragment, tag);
	// }
	//
	// @Override
	// public FragmentTransaction add(int contentView, Fragment fragment) {
	// return mFragmentTransaction.add(contentView, fragment);
	// }
	//
	// @Override
	// public FragmentTransaction add(int contentView, Fragment fragment,
	// String tag) {
	// return mFragmentTransaction.add(contentView, fragment, tag);
	// }
	//
	// @Override
	// public FragmentTransaction addToBackStack(String tag) {
	// return mFragmentTransaction.addToBackStack(tag);
	// }
	//
	// @Override
	// public FragmentTransaction setCustomAnimations(int enter, int exit,
	// int popEnter, int popExit) {
	// return mFragmentTransaction.setCustomAnimations(enter, exit, popEnter,
	// popExit);
	// }
	//
	// @Override
	// public FragmentTransaction setCustomAnimations(int enter, int exit) {
	// return mFragmentTransaction.setCustomAnimations(enter, exit);
	// }
	//
	// @Override
	// public FragmentTransaction replace(int contentView, Fragment fragment) {
	// return mFragmentTransaction.replace(contentView, fragment);
	// }
	//
	// @Override
	// public FragmentTransaction replace(int contentView, Fragment fragment,
	// String tag) {
	// return mFragmentTransaction.replace(contentView, fragment, tag);
	// }
	//
	// @Override
	// public int commit() {
	// return mFragmentTransaction.commit();
	// }

}
