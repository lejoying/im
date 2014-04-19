package com.lejoying.wxgs.activity.mode;

import java.util.ArrayList;
import java.util.List;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;

import com.lejoying.wxgs.R;
import com.lejoying.wxgs.activity.BaseActivity;

public abstract class BaseModeManager {

	public static final int CLEAR_ALL = -1;

	FragmentManager mFragmentManager;
	public List<Fragment> mBackStack;
	Fragment mCurrentFragment;

	int mContentID = R.id.fragmentContent;

	public BaseModeManager(BaseActivity activity) {
		mFragmentManager = activity.getSupportFragmentManager();
		mBackStack = new ArrayList<Fragment>();
	}

	public void show(Fragment fragment) {
		if (!fragment.equals(mCurrentFragment) || !mCurrentFragment.isAdded()) {
			FragmentTransaction transaction = mFragmentManager
					.beginTransaction();
			// transaction.setCustomAnimations(R.anim.translate_new,
			// R.anim.translate_out);
			if (mCurrentFragment != null && mCurrentFragment.isAdded()) {
				transaction.hide(mCurrentFragment);
			}
			mCurrentFragment = fragment;
			if (fragment.isAdded()) {
				transaction.show(fragment);
				fragment.onResume();
			} else {
				transaction.add(mContentID, fragment);
			}
			transaction.commit();
		}
	}

	public void showNext(Fragment fragment) {
		if (!fragment.equals(mCurrentFragment) || !mCurrentFragment.isAdded()) {
			FragmentTransaction transaction = mFragmentManager
					.beginTransaction();
			transaction.setCustomAnimations(R.anim.translate_new,
					R.anim.translate_out);
			if (mCurrentFragment != null) {
				mBackStack.add(0, mCurrentFragment);
				transaction.hide(mCurrentFragment);
			}
			mCurrentFragment = fragment;
			if (fragment.isAdded()) {
				transaction.show(fragment);
				fragment.onResume();
			} else {
				transaction.add(mContentID, fragment);
			}
			transaction.commit();
		}
	}

	public boolean back() {
		if (mBackStack.size() != 0) {
			Fragment fragment = mBackStack.remove(0);
			FragmentTransaction transaction = mFragmentManager
					.beginTransaction();
			transaction.setCustomAnimations(R.anim.translate_back,
					R.anim.translate_finish);
			if (mCurrentFragment != null) {
				transaction.remove(mCurrentFragment);
			}
			mCurrentFragment = fragment;
			transaction.show(fragment);
			fragment.onResume();
			transaction.commit();
			return true;
		}
		return false;
	}

	public void clearBackStack(int count) {
		if (mBackStack.size() != 0) {
			if (count >= mBackStack.size() || count == CLEAR_ALL) {
				FragmentTransaction transaction = mFragmentManager
						.beginTransaction();
				for (Fragment fragment : mBackStack) {
					if (fragment.isAdded())
						transaction.remove(fragment);
				}
				transaction.commit();
				mBackStack.clear();
			} else {
				FragmentTransaction transaction = mFragmentManager
						.beginTransaction();
				for (int i = 0; i < count; i++) {
					Fragment fragment = mBackStack.remove(0);
					if (fragment.isAdded())
						transaction.remove(fragment);
				}
				transaction.commit();
			}
		}
	}

	public void release() {
		List<Fragment> fragments = mFragmentManager.getFragments();
		if (fragments != null) {
			mFragmentManager
					.beginTransaction()
					.setCustomAnimations(R.anim.translate_new,
							R.anim.translate_out).remove(mCurrentFragment)
					.commit();
			FragmentTransaction transaction = mFragmentManager
					.beginTransaction();
			for (Fragment fragment : fragments) {
				if (fragment != null && fragment.isAdded()) {
					transaction.remove(fragment);
				}
			}
			transaction.commit();
		}
		clearBackStack(CLEAR_ALL);
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		return (mKeyDownListener != null ? mKeyDownListener.onKeyDown(keyCode,
				event) : true)
				&& (keyCode == KeyEvent.KEYCODE_BACK ? !back() : true);
	}

	public interface KeyDownListener {
		public boolean onKeyDown(int keyCode, KeyEvent event);
	}

	KeyDownListener mKeyDownListener;

	public void setKeyDownListener(KeyDownListener listener) {
		mKeyDownListener = listener;
	}

	public abstract void initialize();
}
