package com.lejoying.mc;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.inputmethod.InputMethodManager;

import com.lejoying.mc.adapter.ToTryAdapter;
import com.lejoying.mc.fragment.BaseInterface;
import com.lejoying.mc.fragment.CircleMenuFragment;
import com.lejoying.mc.utils.ToTry;
import com.lejoying.mc.view.BackgroundView;

public abstract class BaseFragmentActivity extends FragmentActivity implements
		BaseInterface {

	private FragmentManager mFragmentManager;

	private CircleMenuFragment mCircle;

	private int mContentId;

	private View mLoadingView;
	private View mCircleMenuView;

	private boolean isCircleMenuCreated;
	private boolean isBackgroundCreated;
	private boolean isLoadingCreated;
	private boolean isFirstViewCreated;
	private boolean isLoading;

	private OnKeyDownListener mKeyDownListener;

	private InputMethodManager mInputMethodManager;

	protected abstract Fragment setFirstPreview();

	protected abstract int setBackground();

	@Override
	protected void onCreate(Bundle arg0) {
		// TODO Auto-generated method stub
		super.onCreate(arg0);
		mFragmentManager = getSupportFragmentManager();

		mContentId = R.id.fl_content;

	}

	@Override
	protected void onStart() {
		super.onStart();

		View backgroundView = findViewById(R.id.fl_background);

		if (backgroundView != null && !isBackgroundCreated) {
			isBackgroundCreated = true;
			BackgroundView background = (BackgroundView) backgroundView
					.findViewById(R.id.background);
			background.setBackground(setBackground());
		}

		mCircleMenuView = findViewById(R.id.fl_circleMenu);

		if (mCircleMenuView != null && !isCircleMenuCreated) {
			isCircleMenuCreated = true;
			mCircleMenuView.bringToFront();
			mCircle = new CircleMenuFragment();
			mFragmentManager.beginTransaction()
					.replace(R.id.fl_circleMenu, mCircle).commit();

		}

		if (setFirstPreview() != null && !isFirstViewCreated) {
			isFirstViewCreated = true;
			mFragmentManager.beginTransaction()
					.replace(mContentId, setFirstPreview()).commit();
		}

		mLoadingView = findViewById(R.id.loading);

		if (mLoadingView != null && !isLoadingCreated) {
			isLoadingCreated = true;
			mLoadingView.bringToFront();
			mLoadingView.setVisibility(View.GONE);
			mLoadingView.setOnTouchListener(new OnTouchListener() {
				@Override
				public boolean onTouch(View v, MotionEvent event) {
					return true;
				}
			});

			mLoadingView.setVisibility(View.GONE);
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	public void setFragmentKeyDownListener(OnKeyDownListener listener) {
		this.mKeyDownListener = listener;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && isLoading) {
			cancelLoading();
			return true;
		}
		if (mCircle != null) {
			if (keyCode == KeyEvent.KEYCODE_BACK && mCircle.cancelMenu()) {
				return true;
			}
		}
		if (mKeyDownListener != null
				&& mKeyDownListener.onKeyDown(keyCode, event)) {
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public void hideCircleMenu() {
		if (isCircleMenuCreated && mCircle.isCreated()) {
			mCircle.hideCircleMenu(null);
		} else if (isCircleMenuCreated && !mCircle.isCreated()) {
			circleMenuIsCreated(new CircleMenuCreatedListener() {
				@Override
				public void isCreated() {
					mCircle.hideCircleMenu(null);
				}
			});
		}
	}

	@Override
	public void showCircleMenuToTop(final boolean lock, final boolean showBack) {
		if (isCircleMenuCreated && mCircle.isCreated()) {
			mCircle.showToTop(lock, showBack);
		} else if (isCircleMenuCreated && !mCircle.isCreated()) {
			circleMenuIsCreated(new CircleMenuCreatedListener() {
				@Override
				public void isCreated() {
					mCircle.showToTop(lock, showBack);
				}
			});
		}
	}

	@Override
	public void showCircleMenuToBottom() {
		if (isCircleMenuCreated && mCircle.isCreated()) {
			mCircle.showToBottom();
		} else if (isCircleMenuCreated && !mCircle.isCreated()) {
			circleMenuIsCreated(new CircleMenuCreatedListener() {
				@Override
				public void isCreated() {
					mCircle.showToBottom();
				}
			});
		}
	}

	@Override
	public void setCircleMenuPageName(final String pageName) {
		if (isCircleMenuCreated && mCircle.isCreated()) {
			mCircle.setPageName(pageName);
		} else if (isCircleMenuCreated && !mCircle.isCreated()) {
			circleMenuIsCreated(new CircleMenuCreatedListener() {
				@Override
				public void isCreated() {
					mCircle.setPageName(pageName);
				}
			});
		}
	}

	@Override
	public boolean circleMenuIsShow() {
		return mCircle.isShow();
	}

	private void circleMenuIsCreated(final CircleMenuCreatedListener listener) {
		ToTry.tryDoing(100, 50, new ToTryAdapter() {

			@Override
			public void successed(long time) {
				listener.isCreated();
			}

			@Override
			public boolean isSuccess() {
				return mCircle.isCreated();
			}
		});
	}

	private interface CircleMenuCreatedListener {
		public void isCreated();
	}

	@Override
	public int replaceToContent(Fragment fragment, boolean toBackStack) {
		return replaceToContentFragment(fragment, toBackStack);
	}

	public int replaceToContentFragment(Fragment fragment, boolean toBackStack) {
		FragmentTransaction transaction = mFragmentManager.beginTransaction();
		transaction.setCustomAnimations(R.anim.activity_in,
				R.anim.activity_out, R.anim.activity_in2, R.anim.activity_out2);
		transaction.replace(mContentId, fragment);
		if (toBackStack) {
			transaction.addToBackStack(null);
		}
		return transaction.commit();
	}

	@Override
	public void startToActivity(Class<?> clazz, boolean finishSelf) {
		Intent intent = new Intent(this, clazz);
		startActivity(intent);
		if (finishSelf) {
			finish();
		}
	}

	protected void startLoading(String api) {
		mLoadingView.setVisibility(View.VISIBLE);
		isLoading = true;
		hideSoftInput();
	}

	protected void cancelLoading() {
		mLoadingView.setVisibility(View.GONE);
		isLoading = false;
	}

	private InputMethodManager getInputMethodManager() {
		if (mInputMethodManager == null) {
			mInputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		}
		return mInputMethodManager;
	}

	protected boolean hideSoftInput() {
		boolean flag = false;
		if (getCurrentFocus() != null) {
			flag = getInputMethodManager().hideSoftInputFromWindow(
					getCurrentFocus().getWindowToken(), 0);
		}
		return flag;
	}

	protected void toggleSoftInput() {
		if (getInputMethodManager().isActive()) {
			getInputMethodManager().toggleSoftInput(0,
					InputMethodManager.HIDE_NOT_ALWAYS);
		}
	}
	
}
