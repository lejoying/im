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

import com.lejoying.mc.adapter.ToTryAdapter;
import com.lejoying.mc.fragment.BaseInterface;
import com.lejoying.mc.fragment.CircleMenuFragment;
import com.lejoying.mc.listener.NetworkStatusListener;
import com.lejoying.mc.listener.NotifyListener;
import com.lejoying.mc.listener.RemainListener;
import com.lejoying.mc.service.NetworkService;
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
	private String loadingAPI;

	private Map<String, NetworkStatusListener> mReceiverListreners;
	private Map<String, Integer> mNetworkPermission;
	private RemainListener mRemainListener;
	private NotifyListener mNotifyListener;
	private OnKeyDownListener mKeyDownListener;

	private NetworkRemainReceiver mNetworkRemainReceiver;
	private NetworkReceiver mNetworkReceiver;
	private NotifyReceiver mNotifyReceiver;

	private InputMethodManager mInputMethodManager;

	public abstract Fragment setFirstPreview();

	protected abstract int setBackground();

	@Override
	protected void onCreate(Bundle arg0) {
		// TODO Auto-generated method stub
		super.onCreate(arg0);
		mFragmentManager = getSupportFragmentManager();

		mReceiverListreners = new Hashtable<String, NetworkStatusListener>();
		mNetworkPermission = new Hashtable<String, Integer>();

		mNetworkReceiver = new NetworkReceiver();
		IntentFilter networkFilter = new IntentFilter();
		networkFilter.addAction(NetworkService.ACTION_STATUS);
		registerReceiver(mNetworkReceiver, networkFilter);

		mNetworkRemainReceiver = new NetworkRemainReceiver();
		IntentFilter networkRemainFilter = new IntentFilter();
		networkRemainFilter.addAction(NetworkService.ACTION_REMAIN);
		registerReceiver(mNetworkRemainReceiver, networkRemainFilter);

		mNotifyReceiver = new NotifyReceiver();
		IntentFilter notifyFilter = new IntentFilter();
		notifyFilter.addAction(NetworkService.ACTION_NOTIFY);
		registerReceiver(mNotifyReceiver, notifyFilter);

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
		unregisterReceiver(mNetworkReceiver);
		unregisterReceiver(mNetworkRemainReceiver);
		unregisterReceiver(mNotifyReceiver);
		mReceiverListreners = null;
		mNetworkPermission = null;
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
			NetworkStatusListener listener) {
		startNetwork(api, params, false, listener);
	}

	@Override
	public void startNetworkForResult(String api, Bundle params,
			boolean showLoading, NetworkStatusListener listener) {
		startNetwork(api, params, showLoading, listener);
	}

	@Override
	public void setNetworkRemainListener(RemainListener listener) {
		mRemainListener = listener;
	}

	@Override
	public void setNotifyListener(NotifyListener listener) {
		mNotifyListener = listener;
	}

	protected void startNetwork(String api, Bundle params, boolean showLoading,
			NetworkStatusListener listener) {
		int permissionCode = createPermissionCode();
		mReceiverListreners.put(api, listener);
		mNetworkPermission.put(api, permissionCode);
		Intent service = new Intent(this, NetworkService.class);
		service.putExtra("SERVICE", NetworkService.SERVICE_NETWORK);
		service.putExtra("API", api);
		service.putExtra("PERMISSION", permissionCode);
		if (params != null) {
			service.putExtras(params);
		}
		startService(service);
		if (showLoading) {
			startLoading(api);
		}
	}

	protected void startDataProcessing(int what, boolean showLoading) {
		startDataProcessing(what, null, showLoading);
	}

	protected void startDataProcessing(int what, Bundle params,
			boolean showLoading) {
		Intent service = new Intent(this, NetworkService.class);
		service.putExtra("SERVICE", NetworkService.SERVICE_DATA);
		service.putExtra("WHAT", what);
		if (params != null) {
			service.putExtras(params);
		}
		startService(service);
		if (showLoading) {
			startLoading(null);
		}
	}

	public class NetworkReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			int PERMISSION = intent.getIntExtra("PERMISSION", -1);
			String api = intent.getStringExtra("API");
			if (mNetworkPermission == null
					|| mNetworkPermission.get(api) == null
					|| PERMISSION != mNetworkPermission.get(api)) {
				return;
			}

			if (api != null && mReceiverListreners != null) {
				NetworkStatusListener listener = mReceiverListreners.get(api);
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

	public class NotifyReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			if (mNotifyListener != null) {
				mNotifyListener.notifyChanged();
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
		if (loadingAPI != null) {
			mNetworkPermission.remove(loadingAPI);
			mReceiverListreners.remove(loadingAPI);
		}
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

	protected Integer createPermissionCode() {
		return (int) (Math.random() * 100000);
	}
}
