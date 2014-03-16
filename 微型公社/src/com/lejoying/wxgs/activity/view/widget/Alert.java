package com.lejoying.wxgs.activity.view.widget;

import android.content.Context;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.TextView;

import com.lejoying.wxgs.R;

public class Alert {

	static Alert mAlert;

	Context mContext;
	WindowManager mWindowManager;
	LayoutInflater mInflater;

	Handler mHandler;

	LayoutParams mCommonLayoutParams;
	LayoutParams mDialogLayoutParams;
	LayoutParams mKeyListenerLayoutParams;

	View mDialog;
	View mDialogKeyListener;
	TextView mDialogText;
	View mButtonConfirm;
	View mButtonCancel;
	boolean isDialogShow;

	View mLoading;
	View mLoadingKeyListener;
	boolean isLoadShowed;

	View mMessage;
	TextView mMessageText;
	boolean isMessageShow;
	Runnable mDelayToClearMessage;

	DialogListener mDialogListener;
	OnLoadingCancelListener mLoadingCancelListener;

	boolean isHide;

	private Alert(Context context) {
		this.mContext = context.getApplicationContext();
		mInflater = (LayoutInflater) mContext
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mWindowManager = (WindowManager) mContext
				.getSystemService(Context.WINDOW_SERVICE);

		mHandler = new Handler();

		initView();
		initEvent();

	}

	void initView() {
		mDialog = mInflater.inflate(R.layout.widget_alert_dialog, null);
		mDialogText = (TextView) mDialog.findViewById(R.id.dialog_text);
		mButtonConfirm = mDialog.findViewById(R.id.dialog_confirm);
		mButtonCancel = mDialog.findViewById(R.id.dialog_cancel);

		mLoading = mInflater.inflate(R.layout.widget_alert_loading, null);

		mMessage = mInflater.inflate(R.layout.widget_alert_message, null);
		mMessageText = (TextView) mMessage.findViewById(R.id.message_text);

		mKeyListenerLayoutParams = new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT, LayoutParams.TYPE_PHONE,
				LayoutParams.FLAG_NOT_TOUCH_MODAL, PixelFormat.TRANSPARENT);

		mDialogLayoutParams = new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT, LayoutParams.TYPE_PHONE,
				LayoutParams.FLAG_NOT_FOCUSABLE
						| LayoutParams.FLAG_NOT_TOUCH_MODAL,
				PixelFormat.TRANSPARENT);

		mCommonLayoutParams = new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT, LayoutParams.TYPE_PHONE,
				LayoutParams.FLAG_NOT_FOCUSABLE
						| LayoutParams.FLAG_NOT_TOUCH_MODAL
						| LayoutParams.FLAG_NOT_TOUCHABLE,
				PixelFormat.TRANSPARENT);

		mDialogLayoutParams.gravity = mKeyListenerLayoutParams.gravity = mCommonLayoutParams.gravity = Gravity.CENTER;

		mDialogKeyListener = new View(mContext);
		mLoadingKeyListener = new View(mContext);

	}

	void initEvent() {

		mDialogKeyListener.setOnKeyListener(new OnKeyListener() {

			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if (event.getAction() == KeyEvent.ACTION_DOWN
						&& keyCode == KeyEvent.KEYCODE_BACK) {
					cancelDialog();
				}
				return true;
			}
		});

		mButtonConfirm.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (mDialogListener != null && mDialogListener.confirm()) {
					cancelDialog();
				}
			}
		});

		mButtonCancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				cancelDialog();
				if (mDialogListener != null) {
					mDialogListener.cancel();
				}
			}
		});

		mLoadingKeyListener.setOnKeyListener(new OnKeyListener() {

			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if (event.getAction() == KeyEvent.ACTION_DOWN
						&& keyCode == KeyEvent.KEYCODE_BACK) {
					cancelLoading();
					if (mLoadingCancelListener != null) {
						mLoadingCancelListener.loadingCancel();
					}
				}
				return true;
			}
		});

	}

	public static void initialize(Context context) {
		if (mAlert == null) {
			mAlert = new Alert(context);
		}
	}

	public interface DialogListener {
		public boolean confirm();

		public void cancel();

		public void onCancel();
	}

	public static void showDialog(String message, DialogListener listener) {
		if (mAlert != null) {
			mAlert.dialog(message, listener);
		}
	}

	void dialog(final String message, final DialogListener listener) {
		mHandler.post(new Runnable() {
			@Override
			public void run() {
				if (!isDialogShow && message != null) {
					isDialogShow = true;
					mDialogListener = listener;
					mDialogText.setText(message);
					mWindowManager.addView(mDialogKeyListener,
							mKeyListenerLayoutParams);
					mWindowManager.addView(mDialog, mDialogLayoutParams);
				}
			}
		});
	}

	void cancelDialog() {
		mHandler.post(new Runnable() {
			@Override
			public void run() {
				if (isDialogShow) {
					isDialogShow = false;
					mWindowManager.removeView(mDialogKeyListener);
					mWindowManager.removeView(mDialog);
				}
			}
		});
	}

	public interface OnLoadingCancelListener {
		public void loadingCancel();
	}

	public static void showLoading(OnLoadingCancelListener onLoadingCancel) {
		if (mAlert != null) {
			mAlert.loading(onLoadingCancel);
		}
	}

	void loading(OnLoadingCancelListener onLoadingCancel) {
		if (!isLoadShowed) {
			isLoadShowed = true;
			mWindowManager.addView(mLoadingKeyListener,
					mKeyListenerLayoutParams);
			mWindowManager.addView(mLoading, mCommonLayoutParams);
			this.mLoadingCancelListener = onLoadingCancel;
		}
	}

	public static void removeLoading() {
		if (mAlert != null) {
			mAlert.cancelLoading();
		}
	}

	void cancelLoading() {
		mHandler.post(new Runnable() {

			@Override
			public void run() {
				if (isLoadShowed) {
					isLoadShowed = false;
					mWindowManager.removeView(mLoading);
					mWindowManager.removeView(mLoadingKeyListener);
				}
			}
		});
	}

	public static void showMessage(String message) {
		if (mAlert != null) {
			mAlert.showMessageText(message, 1200);
		}
	}

	public static void showMessage(String message, long showTime) {
		if (mAlert != null) {
			mAlert.showMessageText(message, showTime);
		}
	}

	void showMessageText(final String message, long showTime) {
		mHandler.post(new Runnable() {
			@Override
			public void run() {
				mMessageText.setText(message);
				if (!isMessageShow) {
					isMessageShow = true;
					mWindowManager.addView(mMessage, mCommonLayoutParams);
				}
			}
		});
		if (mDelayToClearMessage != null) {
			mHandler.removeCallbacks(mDelayToClearMessage);
		}
		mHandler.postDelayed(mDelayToClearMessage = new Runnable() {
			@Override
			public void run() {
				cancelShowMessage();
			}
		}, showTime);

	}

	public static void clearMessage() {
		if (mAlert != null) {
			mAlert.cancelShowMessage();
		}
	}

	void cancelShowMessage() {
		if (isMessageShow) {
			isMessageShow = false;
			mWindowManager.removeView(mMessage);
		}
	}

	public static void recover() {
		if (mAlert != null && mAlert.isHide) {
			mAlert.isHide = false;
			mAlert.mDialog.setVisibility(View.VISIBLE);
			mAlert.mMessage.setVisibility(View.VISIBLE);
			mAlert.mLoading.setVisibility(View.VISIBLE);
			mAlert.mDialogKeyListener.setVisibility(View.VISIBLE);
			mAlert.mLoadingKeyListener.setVisibility(View.VISIBLE);
		}
	}

	public static void hide() {
		if (mAlert != null && !mAlert.isHide) {
			mAlert.isHide = true;
			mAlert.mDialog.setVisibility(View.GONE);
			mAlert.mMessage.setVisibility(View.GONE);
			mAlert.mLoading.setVisibility(View.GONE);
			mAlert.mDialogKeyListener.setVisibility(View.GONE);
			mAlert.mLoadingKeyListener.setVisibility(View.GONE);
		}
	}
}
