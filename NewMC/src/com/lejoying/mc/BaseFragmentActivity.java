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
import android.view.View;

import com.lejoying.mc.fragment.BaseInterface;
import com.lejoying.mc.fragment.CircleMenuFragment;
import com.lejoying.mc.service.NetworkService;
import com.lejoying.mc.view.BackgroundView;

public abstract class BaseFragmentActivity extends FragmentActivity implements
		BaseInterface {

	private FragmentManager mFragmentManager;

	private CircleMenuFragment mCircle;

	private int mContentId;

	private boolean isCircleMenuCreated;
	private boolean isBackgroundCreated;

	private Map<Integer, ReceiverListener> mReceiverListreners;
	private RemainListener mRemainListener;

	private NetworkRemainReceiver mNetworkRemainReceiver;
	private NetworkReceiver mNetworkReceiver;

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
			circleMenuView.getParent().bringChildToFront(circleMenuView);
			mCircle = new CircleMenuFragment();
			mFragmentManager.beginTransaction()
					.replace(R.id.fl_circleMenu, mCircle).commit();
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
	public void startNetworkForResult(int API, Bundle bundle,
			ReceiverListener listener) {
		if (mReceiverListreners == null) {
			mReceiverListreners = new Hashtable<Integer, BaseInterface.ReceiverListener>();
		}
		mReceiverListreners.put(API, listener);
		Intent service = new Intent(this, NetworkService.class);
		service.putExtra("API", API);
		if (bundle != null) {
			service.putExtras(bundle);
		}
		startService(service);
	}

	@Override
	public void setNetworkRemainListener(RemainListener listener) {
		mRemainListener = listener;
	}

	public class NetworkReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			ReceiverListener listener = mReceiverListreners.get(intent
					.getIntExtra("API", -1));
			if (listener != null) {
				listener.onReceive(intent.getIntExtra("STATUS", -1),
						intent.getStringExtra("LOG"));
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

}
