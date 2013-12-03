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
import com.lejoying.mc.fragment.CircleMenuFragment.CircleMenuListener;
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
	private boolean isFirstViewCreated;
	private boolean isLoading;
	private String loadingAPI;

	private Map<String, ReceiverListener> mReceiverListreners;
	private RemainListener mRemainListener;
	private OnKeyDownListener mKeyDownListener;

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

		View circleMenuView = findViewById(R.id.fl_circleMenu);

		if (circleMenuView != null && !isCircleMenuCreated) {
			isCircleMenuCreated = true;
			circleMenuView.bringToFront();
			mCircle = new CircleMenuFragment();
			mFragmentManager.beginTransaction()
					.replace(R.id.fl_circleMenu, mCircle).commit();
			mCircle.setCircleMenuListener(new CircleMenuListener() {
				@Override
				public void onCreated() {
					System.out.println("¥¥Ω®¡À");
					createFirstView();
				}
			});
		} else if (!isFirstViewCreated) {
			createFirstView();
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

	private void createFirstView() {
		if (setFirstPreview() != null) {
			mFragmentManager.beginTransaction()
					.replace(mContentId, setFirstPreview()).commit();
		}
		isFirstViewCreated = true;
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		unregisterReceiver(mNetworkReceiver);
		unregisterReceiver(mNetworkRemainReceiver);
	}

	@Override
	public void setFragmentKeyDownListener(OnKeyDownListener listener) {
		this.mKeyDownListener = listener;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && isLoading) {
			mReceiverListreners.remove(loadingAPI);
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
		if (mCircle != null) {
			mCircle.hideCircleMenu(null);
		}
	}

	@Override
	public void showCircleMenuToTop(boolean lock, boolean showBack) {
		if (mCircle != null) {
			mCircle.showToTop(lock, showBack);
		}
	}

	@Override
	public void showCircleMenuToBottom() {
		if (mCircle != null) {
			mCircle.showToBottom();
		}
	}

	@Override
	public void setCircleMenuPageName(String pageName) {
		if (mCircle != null) {
			mCircle.setPageName(pageName);
		}
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
	public void startToActivity(Class<?> clazz, boolean finishSelf) {
		Intent intent = new Intent(this, clazz);
		startActivity(intent);
		if (finishSelf) {
			finish();
		}
	}

	@Override
	public void startNetworkForResult(String api, Bundle params,
			ReceiverListener listener) {
		startNetworkForResult(api, params, false, listener);
	}

	@Override
	public void startNetworkForResult(String api, Bundle params,
			boolean showLoading, ReceiverListener listener) {
		if (mReceiverListreners == null) {
			mReceiverListreners = new Hashtable<String, BaseInterface.ReceiverListener>();
		}
		mReceiverListreners.put(api, listener);
		Intent service = new Intent(this, NetworkService.class);
		service.putExtra("API", api);
		if (params != null) {
			service.putExtras(params);
		}
		startService(service);
		if (showLoading) {
			startLoading(api);
		}
	}

	@Override
	public void setNetworkRemainListener(RemainListener listener) {
		mRemainListener = listener;
	}

	public class NetworkReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			String api = intent.getStringExtra("API");
			if (api != null) {
				ReceiverListener listener = mReceiverListreners.get(api);
				if (listener != null) {
					if (isLoading && api.equals(loadingAPI)) {
						cancelLoading();
					}
					mReceiverListreners.remove(api);
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

	private void startLoading(String api) {
		mLoadingView.setVisibility(View.VISIBLE);
		isLoading = true;
		loadingAPI = api;
		hideSoftInput();
	}

	private void cancelLoading() {
		mLoadingView.setVisibility(View.GONE);
		isLoading = false;
		loadingAPI = "";
		// toggleSoftInput();
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
