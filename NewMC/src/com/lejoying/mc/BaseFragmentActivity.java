package com.lejoying.mc;

import java.util.Hashtable;
import java.util.Map;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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

import com.lejoying.mc.fragment.BaseInterface;
import com.lejoying.mc.fragment.CircleMenuFragment;
import com.lejoying.mc.service.NetworkService;
import com.lejoying.mc.view.BackgroundView;

public abstract class BaseFragmentActivity extends FragmentActivity implements
		BaseInterface {

	private FragmentManager mFragmentManager;

	private CircleMenuFragment mCircle;

	private int mContentId;

	private View mLoadingView;

	private boolean isCircleMenuCreated;
	private boolean isBackgroundCreated;
	private boolean isLoadingCreated;
	private boolean isLoading;

	private Map<Integer, ReceiverListener> mReceiverListreners;
	private RemainListener mRemainListener;

	private NetworkRemainReceiver mNetworkRemainReceiver;
	private NetworkReceiver mNetworkReceiver;

	private InputMethodManager mInputMethodManager;

	public abstract Fragment setFirstPreview();

	protected abstract int setBackground();

	@Override
	protected void onCreate(Bundle arg0) {
		// TODO Auto-generated method stub
		super.onCreate(arg0);
		mFragmentManager = getSupportFragmentManager();

		mNetworkReceiver = new NetworkReceiver();
		IntentFilter networkFilter = new IntentFilter();
		networkFilter.addAction(NetworkService.ACTION);
		registerReceiver(mNetworkReceiver, networkFilter);

		mNetworkRemainReceiver = new NetworkRemainReceiver();
		IntentFilter networkRemainfilter = new IntentFilter();
		networkRemainfilter.addAction(NetworkService.ACTION_REMAIN);
		registerReceiver(mNetworkRemainReceiver, networkRemainfilter);

		mContentId = R.id.fl_content;

		if (setFirstPreview() != null) {
			if (mFragmentManager.getFragments() == null
					|| mFragmentManager.getFragments().size() == 0) {
				mFragmentManager.beginTransaction()
						.replace(mContentId, setFirstPreview(), "first")
						.commit();
			}
		}
	}

	@Override
	protected void onResume() {
		super.onResume();

		View backgroundView = findViewById(R.id.fl_background);

		if (backgroundView != null && !isBackgroundCreated) {
			isBackgroundCreated = true;
			BackgroundView background = (BackgroundView) backgroundView
					.findViewById(R.id.background);
			background.setBackground(setBackground());
		}

		View circleMenuView = findViewById(R.id.fl_circleMenu);

		if (circleMenuView != null && !isCircleMenuCreated) {
			isCircleMenuCreated = true;
			circleMenuView.bringToFront();
			mCircle = new CircleMenuFragment();
			mFragmentManager.beginTransaction()
					.replace(R.id.fl_circleMenu, mCircle).commit();
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
			mLoadingView.setFocusable(true);
			mLoadingView.setClickable(true);

			mLoadingView.setVisibility(View.GONE);
		}

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		unregisterReceiver(mNetworkReceiver);
		unregisterReceiver(mNetworkRemainReceiver);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (mCircle != null) {
			if (keyCode == KeyEvent.KEYCODE_BACK && isLoading) {
				cancelLoading();
				return true;
			}
			if (keyCode == KeyEvent.KEYCODE_BACK && mCircle.cancelMenu()) {
				return true;
			}
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public void hideCircleMenu() {
		if (mCircle != null) {
			mCircle.hideCircleMenu(null);
		}
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

	@Override
	public void startNetworkForResult(int api, Bundle bundle,
			ReceiverListener listener) {
		if (mReceiverListreners == null) {
			mReceiverListreners = new Hashtable<Integer, BaseInterface.ReceiverListener>();
		}
		mReceiverListreners.put(api, listener);
		Intent service = new Intent(this, NetworkService.class);
		service.putExtra("API", api);
		if (bundle != null) {
			service.putExtras(bundle);
		}
		startService(service);
		startLoading();
	}

	@Override
	public void setNetworkRemainListener(RemainListener listener) {
		mRemainListener = listener;
	}

	public class NetworkReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (isLoading) {
				cancelLoading();
				ReceiverListener listener = mReceiverListreners.get(intent
						.getIntExtra("API", -1));
				if (listener != null) {
					listener.onReceive(intent.getIntExtra("STATUS", -1),
							intent.getStringExtra("LOG"));
				}
			}
		}
	}

	public class NetworkRemainReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (mRemainListener != null) {
				int remain = intent.getIntExtra(
						mRemainListener.setRemainType(), -1);
				if (remain != -1) {
					mRemainListener.remain(remain);
				}
			}
		}
	}

	private void startLoading() {
		mLoadingView.setVisibility(View.VISIBLE);
		isLoading = true;
		hideSoftInput();
	}

	private void cancelLoading() {
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
}
