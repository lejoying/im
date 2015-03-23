package com.open.welinks.customView;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface.OnCancelListener;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.open.welinks.R;
import com.open.welinks.utils.BaseDataUtils;
import com.open.welinks.view.ViewManage;

public class Alert {

	static Alert mAlert;

	Context mContext;
	WindowManager mWindowManager;
	LayoutInflater mInflater;

	Handler mHandler;

	LayoutParams mCommonLayoutParams;
	LayoutParams mDialogLayoutParams;
	LayoutParams mKeyListenerLayoutParams;

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

	OnLoadingCancelListener mLoadingCancelListener;

	boolean isHide;

	private Alert(Context context) {
		this.mContext = context.getApplicationContext();
		mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mWindowManager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);

		mHandler = new Handler();

		initView();
		initEvent();

	}

	void initView() {

		mLoading = mInflater.inflate(R.layout.widget_alert_loading, null);

		mMessage = mInflater.inflate(R.layout.widget_alert_message, null);
		mMessageText = (TextView) mMessage.findViewById(R.id.message_text);

		mKeyListenerLayoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT, LayoutParams.TYPE_PHONE, LayoutParams.FLAG_NOT_TOUCH_MODAL, PixelFormat.TRANSPARENT);

		mDialogLayoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT, LayoutParams.TYPE_PHONE, LayoutParams.FLAG_NOT_FOCUSABLE | LayoutParams.FLAG_NOT_TOUCH_MODAL, PixelFormat.TRANSPARENT);

		mCommonLayoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT, LayoutParams.TYPE_PHONE, LayoutParams.FLAG_NOT_FOCUSABLE | LayoutParams.FLAG_NOT_TOUCH_MODAL | LayoutParams.FLAG_NOT_TOUCHABLE, PixelFormat.TRANSPARENT);

		mDialogLayoutParams.gravity = mKeyListenerLayoutParams.gravity = mCommonLayoutParams.gravity = Gravity.CENTER;

		mLoadingKeyListener = new View(mContext);

	}

	void initEvent() {

		mLoadingKeyListener.setOnKeyListener(new OnKeyListener() {

			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_BACK) {
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
			mWindowManager.addView(mLoadingKeyListener, mKeyListenerLayoutParams);
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
		} else {
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
			mAlert.mMessage.setVisibility(View.VISIBLE);
			mAlert.mLoading.setVisibility(View.VISIBLE);
			mAlert.mLoadingKeyListener.setVisibility(View.VISIBLE);
		}
	}

	public static void hide() {
		if (mAlert != null && !mAlert.isHide) {
			mAlert.isHide = true;
			mAlert.mMessage.setVisibility(View.GONE);
			mAlert.mLoading.setVisibility(View.GONE);
			mAlert.mLoadingKeyListener.setVisibility(View.GONE);
		}
	}

	public static AlertInputDialog createInputDialog(Context context) {
		AlertInputDialog dialog = new AlertInputDialog(context);
		dialog.showInput();
		return dialog;
	}

	public static AlertInputDialog createDialog(Context context) {
		AlertInputDialog dialog = new AlertInputDialog(context);
		dialog.hideInput();
		return dialog;
	}

	public static class AlertInputDialog {

		CommonDialog dialog;

		public AlertInputDialog(Context context) {
			dialog = new CommonDialog(context);
		}

		public AlertInputDialog setOnConfirmClickListener(OnDialogClickListener l) {
			dialog.confirmListener = l;
			return this;
		}

		public AlertInputDialog setOnCancelClickListener(OnDialogClickListener l) {
			dialog.cancelListener = l;
			return this;
		}

		public AlertInputDialog setOnCancelListener(OnCancelListener listener) {
			dialog.setOnCancelListener(listener);
			return this;
		}

		public AlertInputDialog show() {
			dialog.show();
			return this;
		}

		public AlertInputDialog setTitle(String text) {
			dialog.title.setText(text);
			return this;
		}

		public AlertInputDialog setDescription(String text) {
			dialog.description.setVisibility(View.VISIBLE);
			dialog.description.setText(text);
			return this;
		}

		public String getInputText() {
			return dialog.input.getText().toString();
		}

		public AlertInputDialog setInputHint(String text) {
			dialog.input.setHint(text);
			return this;
		}

		public AlertInputDialog setInputText(String text) {
			dialog.input.setText(text);
			dialog.input.setSelection(dialog.input.getText().toString().length());
			return this;
		}

		public AlertInputDialog setLeftButtonText(String text) {
			((Button) (dialog.confirmView)).setText(text);
			return this;
		}

		public AlertInputDialog setRightButtonText(String text) {
			((Button) (dialog.cancelView)).setText(text);
			return this;
		}

		void hideInput() {
			dialog.input.setVisibility(View.GONE);
		}

		void showInput() {
			dialog.input.setVisibility(View.VISIBLE);
		}

		public interface OnDialogClickListener {
			public void onClick(AlertInputDialog dialog);
		}

		class CommonDialog extends Dialog {

			AlertInputDialog.OnDialogClickListener confirmListener, cancelListener;
			TextView title;
			EditText input;
			View confirmView;
			View cancelView;
			TextView description;

			public CommonDialog(Context context) {
				super(context, R.style.AlertInputDialog);
				LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				View content = inflater.inflate(R.layout.widget_alert_dialog, null);
				title = (TextView) content.findViewById(R.id.title);
				description = (TextView) content.findViewById(R.id.description);
				input = (EditText) content.findViewById(R.id.input);
				confirmView = content.findViewById(R.id.confirm);
				cancelView = content.findViewById(R.id.cancel);
				setContentView(content, new ViewGroup.LayoutParams(context.getResources().getDisplayMetrics().widthPixels, ViewGroup.LayoutParams.WRAP_CONTENT));

				setCanceledOnTouchOutside(false);

				confirmView.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						cancel();
						if (confirmListener != null) {
							confirmListener.onClick(AlertInputDialog.this);
						}
					}
				});

				cancelView.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						cancel();
						if (cancelListener != null) {
							cancelListener.onClick(AlertInputDialog.this);
						}
					}
				});
			}
		}
	}

	public static AlertInputCommentDialog createInputCommentDialog(Context context) {
		AlertInputCommentDialog dialog = new AlertInputCommentDialog(context);
		dialog.showInput();
		return dialog;
	}

	public static class AlertInputCommentDialog {

		CommonDialog dialog;

		public AlertInputCommentDialog(Context context) {
			this.context = context;
			dialog = new CommonDialog(context);
		}

		public AlertInputCommentDialog setOnConfirmClickListener(OnDialogClickListener l) {
			dialog.confirmListener = l;
			return this;
		}

		public AlertInputCommentDialog setOnCancelClickListener(OnDialogClickListener l) {
			dialog.cancelListener = l;
			return this;
		}

		public AlertInputCommentDialog setOnCancelListener(OnCancelListener listener) {
			dialog.setOnCancelListener(listener);
			return this;
		}

		public AlertInputCommentDialog show() {
			dialog.show();
			return this;
		}

		public String getInputText() {
			return dialog.input.getText().toString();
		}

		public AlertInputCommentDialog setInputHint(String text) {
			dialog.input.setHint(text);
			return this;
		}

		public AlertInputCommentDialog setInputText(String text) {
			dialog.input.setText(text);
			dialog.input.setSelection(dialog.input.getText().toString().length());
			return this;
		}

		public AlertInputCommentDialog setLeftButtonText(String text) {
			((Button) (dialog.confirmView)).setText(text);
			return this;
		}

		void hideInput() {
			dialog.input.setVisibility(View.GONE);
		}

		void showInput() {
			dialog.input.setVisibility(View.VISIBLE);
		}

		public interface OnDialogClickListener {
			public void onClick(AlertInputCommentDialog dialog);
		}

		public AlertInputCommentDialog requestFocus() {
			dialog.input.requestFocus();
			@SuppressWarnings("static-access")
			InputMethodManager m = (InputMethodManager) dialog.getContext().getSystemService(dialog.getContext().INPUT_METHOD_SERVICE);
			m.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
			// m.showSoftInput(dialog.input, InputMethodManager.SHOW_FORCED);
			// InputMethodManagerUtils inputMethodManagerUtils = new InputMethodManagerUtils(context);
			// inputMethodManagerUtils.show(dialog.input);
			return this;
		}

		public Context context;

		class CommonDialog extends Dialog {

			AlertInputCommentDialog.OnDialogClickListener confirmListener, cancelListener;
			EditText input;
			View confirmView;

			public CommonDialog(Context context) {
				super(context, R.style.AlertInputDialog);
				LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				View content = inflater.inflate(R.layout.widget_alert_comment_input, null);
				input = (EditText) content.findViewById(R.id.input);
				confirmView = content.findViewById(R.id.confirm);
				setContentView(content, new ViewGroup.LayoutParams(context.getResources().getDisplayMetrics().widthPixels, ViewGroup.LayoutParams.WRAP_CONTENT));

				setCanceledOnTouchOutside(true);
				int height = BaseDataUtils.dpToPxint(45);// (int) (45 * viewManage.displayMetrics.density);
				LinearLayout.LayoutParams params = (android.widget.LinearLayout.LayoutParams) input.getLayoutParams();
				params.height = height;
				input.setOnFocusChangeListener(new OnFocusChangeListener() {

					@Override
					public void onFocusChange(View v, boolean hasFocus) {
						Log.e("Alert", "-------------:" + hasFocus);

					}
				});
				input.addTextChangedListener(new TextWatcher() {

					@Override
					public void onTextChanged(CharSequence s, int start, int before, int count) {
						int lineCount = input.getLineCount();
						if (lineCount == 1) {
							int height = BaseDataUtils.dpToPxint(45);
							LinearLayout.LayoutParams params = (android.widget.LinearLayout.LayoutParams) input.getLayoutParams();
							params.height = height;
							input.setLayoutParams(params);
						} else {
							LinearLayout.LayoutParams params = (android.widget.LinearLayout.LayoutParams) input.getLayoutParams();
							params.height = android.widget.LinearLayout.LayoutParams.WRAP_CONTENT;
							input.setLayoutParams(params);
						}
					}

					@Override
					public void beforeTextChanged(CharSequence s, int start, int count, int after) {
					}

					@Override
					public void afterTextChanged(Editable s) {
					}
				});
				// LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, height);

				confirmView.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						cancel();
						if (confirmListener != null) {
							confirmListener.onClick(AlertInputCommentDialog.this);
						}
					}
				});
			}
		}
	}

	public static AlertSelectDialog createSelectDialog(Context context) {
		AlertSelectDialog dialog = new AlertSelectDialog(context);
		return dialog;
	}

	public static class AlertSelectDialog {

		public View currentView;
		public String currentItem;

		CommonDialog dialog;

		public AlertSelectDialog(Context context) {
			dialog = new CommonDialog(context);
		}

		public AlertSelectDialog setOnConfirmClickListener(OnDialogClickListener2 l) {
			dialog.confirmListener = l;
			return this;
		}

		public AlertSelectDialog setOnCancelClickListener(OnDialogClickListener2 l) {
			dialog.cancelListener = l;
			return this;
		}

		public AlertSelectDialog setOnCancelListener(OnCancelListener listener) {
			dialog.setOnCancelListener(listener);
			return this;
		}

		public AlertSelectDialog show() {
			dialog.show();
			return this;
		}

		public AlertSelectDialog setTitle(String text) {
			dialog.title.setText(text);
			return this;
		}

		public AlertSelectDialog setLeftButtonText(String text) {
			((Button) (dialog.confirmView)).setText(text);
			return this;
		}

		public AlertSelectDialog setRightButtonText(String text) {
			((Button) (dialog.cancelView)).setText(text);
			return this;
		}

		public interface OnDialogClickListener2 {
			public void onClick(AlertSelectDialog dialog);
		}

		/**
		 * 
		 * @param i
		 * @return
		 */
		public AlertSelectDialog setCurrentItem(String permission) {
			if ("open".equals(permission)) {
				currentView = dialog.v1;
				currentItem = "open";
				dialog.description.setText("所有人可见,可以查看群分享并可以点赞和评论.");
				dialog.v1Status.setVisibility(View.VISIBLE);
				dialog.v2Status.setVisibility(View.GONE);
				dialog.v3Status.setVisibility(View.GONE);
			} else if ("semiopen".equals(permission)) {
				currentView = dialog.v2;
				currentItem = "semiopen";
				dialog.description.setText("所有人可见,非群组成员只能查看群分享,不能点赞和评论.");
				dialog.v1Status.setVisibility(View.GONE);
				dialog.v2Status.setVisibility(View.VISIBLE);
				dialog.v3Status.setVisibility(View.GONE);
			} else if ("private".equals(permission)) {
				currentView = dialog.v3;
				currentItem = "private";
				dialog.description.setText("仅群成员可以查看群分享,并可以点赞和评论.");
				dialog.v1Status.setVisibility(View.GONE);
				dialog.v2Status.setVisibility(View.GONE);
				dialog.v3Status.setVisibility(View.VISIBLE);
			} else {
				currentView = dialog.v1;
				currentItem = "open";
				dialog.description.setText("所有人可见,可以查看群分享并可以点赞和评论.");
				dialog.v1Status.setVisibility(View.VISIBLE);
				dialog.v2Status.setVisibility(View.GONE);
				dialog.v3Status.setVisibility(View.GONE);
			}
			return this;
		}

		class CommonDialog extends Dialog {

			AlertSelectDialog.OnDialogClickListener2 confirmListener, cancelListener;
			TextView title;
			View confirmView;
			View cancelView;
			View v1;
			View v1Status;
			View v2;
			View v2Status;
			View v3;
			View v3Status;
			TextView description;

			public CommonDialog(final Context context) {
				super(context, R.style.AlertInputDialog);
				LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				View content = inflater.inflate(R.layout.widget_alert_select, null);
				title = (TextView) content.findViewById(R.id.title);
				confirmView = content.findViewById(R.id.confirm);
				cancelView = content.findViewById(R.id.cancel);
				v1 = content.findViewById(R.id.openOption);
				v2 = content.findViewById(R.id.semiOpen);
				v3 = content.findViewById(R.id.privateOption);
				v1Status = content.findViewById(R.id.openStatus);
				v2Status = content.findViewById(R.id.semiOpenStatus);
				v3Status = content.findViewById(R.id.privateOptionStatus);
				description = (TextView) content.findViewById(R.id.description);
				currentView = v1;
				currentItem = "open";
				description.setText("所有人可见,可以查看群分享并可以点赞和评论.");
				setContentView(content, new ViewGroup.LayoutParams(context.getResources().getDisplayMetrics().widthPixels, ViewGroup.LayoutParams.WRAP_CONTENT));
				setCanceledOnTouchOutside(false);

				v1.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						currentView = v1;
						currentItem = "open";
						description.setText("所有人可见,可以查看群分享并可以点赞和评论.");
						v1Status.setVisibility(View.VISIBLE);
						v2Status.setVisibility(View.GONE);
						v3Status.setVisibility(View.GONE);
					}
				});
				v2.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						currentView = v2;
						currentItem = "semiopen";
						description.setText("所有人可见,非群组成员只能查看群分享,不能点赞和评论.");
						v1Status.setVisibility(View.GONE);
						v2Status.setVisibility(View.VISIBLE);
						v3Status.setVisibility(View.GONE);
					}
				});
				v3.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						currentView = v3;
						currentItem = "private";
						description.setText("仅群成员可以查看群分享,并可以点赞和评论.");
						v1Status.setVisibility(View.GONE);
						v2Status.setVisibility(View.GONE);
						v3Status.setVisibility(View.VISIBLE);
					}
				});

				confirmView.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						cancel();
						if (confirmListener != null) {
							confirmListener.onClick(AlertSelectDialog.this);
						}
					}
				});

				cancelView.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						cancel();
						if (cancelListener != null) {
							cancelListener.onClick(AlertSelectDialog.this);
						}
					}
				});
			}
		}
	}
}
