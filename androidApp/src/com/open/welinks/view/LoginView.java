package com.open.welinks.view;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.text.InputType;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

import com.facebook.rebound.BaseSpringSystem;
import com.facebook.rebound.SimpleSpringListener;
import com.facebook.rebound.Spring;
import com.facebook.rebound.SpringConfig;
import com.facebook.rebound.SpringSystem;
import com.open.welink.R;
import com.open.welinks.controller.LoginController;
import com.open.welinks.customView.ControlProgress;
import com.open.welinks.model.Data;

public class LoginView {
	public Data data = Data.getInstance();
	public String tag = "LoginView";

	public Context context;
	public LoginView thisView;
	public LoginController thisController;
	public Activity thisActivity;

	public View loginOrRegister;
	public View loginOrRegisterButton;
	public View loginButton;
	public View registerButton;

	public View card;
	public TextView leftTopText;
	public TextView rightTopTextButton;
	public TextView error_message;
	public EditText input1;
	public EditText input2;
	public View clearInput1;
	public View clearInput2;
	public TextView mainButton;
	public TextView leftBottomTextButton;
	public TextView rightBottomTextButton;
	public ImageView appIconToName;
	public ProgressBar progressBar;
	public ImageView cardTopLine;

	public View mRootView;

	public Animation animationNextOut;
	public Animation animationNextIn;
	public Animation animationBackOut;
	public Animation animationBackIn;

	public enum Status {
		welcome, start, loginOrRegister, loginUsePassword, verifyPhoneForRegister, verifyPhoneForResetPassword, verifyPhoneForLogin, setPassword, resetPassword
	}

	String 状态机;

	public Status status = Status.welcome;

	public SpringConfig ORIGAMI_SPRING_CONFIG = SpringConfig.fromOrigamiTensionAndFriction(10, 2);

	public BaseSpringSystem mSpringSystem = SpringSystem.create();
	public Spring mScaleSpring = mSpringSystem.createSpring().setSpringConfig(ORIGAMI_SPRING_CONFIG);

	public LoginView(Activity activity) {
		this.context = activity;
		this.thisActivity = activity;
		this.thisView = this;
	}

	public View controlProgressView;
	public DisplayMetrics displayMetrics;
	public ControlProgress controlProgress;

	public void initView() {

		displayMetrics = new DisplayMetrics();

		thisActivity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

		thisActivity.setContentView(R.layout.activity_login);

		animationNextOut = AnimationUtils.loadAnimation(context, R.anim.animation_next_out);
		animationNextIn = AnimationUtils.loadAnimation(context, R.anim.animation_next_in);
		animationBackOut = AnimationUtils.loadAnimation(context, R.anim.animation_back_out);
		animationBackIn = AnimationUtils.loadAnimation(context, R.anim.animation_back_in);

		loginOrRegister = thisActivity.findViewById(R.id.loginOrRegister);
		loginOrRegisterButton = thisActivity.findViewById(R.id.loginOrRegisterButton);
		loginButton = thisActivity.findViewById(R.id.loginButton);
		registerButton = thisActivity.findViewById(R.id.registerButton);

		card = thisActivity.findViewById(R.id.card);
		leftTopText = (TextView) thisActivity.findViewById(R.id.leftTopText);
		rightTopTextButton = (TextView) thisActivity.findViewById(R.id.rightTopTextButton);
		input1 = (EditText) thisActivity.findViewById(R.id.input1);
		input2 = (EditText) thisActivity.findViewById(R.id.input2);
		clearInput1 = thisActivity.findViewById(R.id.clearInput1);
		clearInput2 = thisActivity.findViewById(R.id.clearInput2);
		mainButton = (TextView) thisActivity.findViewById(R.id.mainButton);
		leftBottomTextButton = (TextView) thisActivity.findViewById(R.id.leftBottomTextButton);
		rightBottomTextButton = (TextView) thisActivity.findViewById(R.id.rightBottomTextButton);
		appIconToName = (ImageView) thisActivity.findViewById(R.id.appIconToName);
		error_message = (TextView) thisActivity.findViewById(R.id.err_message);
		progressBar = (ProgressBar) thisActivity.findViewById(R.id.progressBar);
		cardTopLine = (ImageView) thisActivity.findViewById(R.id.cardTopLine);
		cardTopLine.setVisibility(View.GONE);
		cardTopLine.setBackgroundColor(Color.RED);
		controlProgressView = thisActivity.findViewById(R.id.list_item_progress_container);
		controlProgress = new ControlProgress();
		controlProgress.initialize(controlProgressView, displayMetrics);

		cardTopLine.setVisibility(View.GONE);
		progressBar.setVisibility(View.VISIBLE);
		mRootView = appIconToName;
	}

	int remainRegister = 0;
	int remainLogin = 0;
	int remainResetPassword = 0;

	public void setCardContent(Status status) {
		cardTopLine.setVisibility(View.GONE);
		controlProgressView.setVisibility(View.GONE);
		progressBar.setVisibility(View.VISIBLE);
		if (status == Status.loginUsePassword) {
			leftTopText.setVisibility(View.VISIBLE);
			leftTopText.setText("登陆");
			rightTopTextButton.setVisibility(View.VISIBLE);
			rightTopTextButton.setText("取消");
			input1.setHint("请输入手机号");
			input2.setHint("请输入密码");
			mainButton.setText("登陆");
			leftBottomTextButton.setVisibility(View.VISIBLE);
			leftBottomTextButton.setText("忘记密码?");
			rightBottomTextButton.setVisibility(View.VISIBLE);
			rightBottomTextButton.setText("验证码登录");
			input1.setText("13566668888");
			if (data.userInformation.localConfig.line1Number != null && data.userInformation.localConfig.line1Number != "") {
				input1.setText(data.userInformation.localConfig.line1Number);
			} else {
				input1.setText("");
			}
			input2.setText("");
			input1.setInputType(InputType.TYPE_CLASS_NUMBER);
			input2.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
		} else if (status == Status.verifyPhoneForLogin) {
			thisView.controlProgressView.setVisibility(View.VISIBLE);
			thisView.progressBar.setVisibility(View.GONE);
			leftTopText.setVisibility(View.VISIBLE);
			leftTopText.setText("验证码登陆");
			rightTopTextButton.setVisibility(View.VISIBLE);
			rightTopTextButton.setText("取消");
			input1.setHint("请输入手机号");
			input2.setHint("请输入验证码");
			mainButton.setText("登陆");
			leftBottomTextButton.setVisibility(View.INVISIBLE);
			rightBottomTextButton.setVisibility(View.VISIBLE);
			if (remainLogin != 0) {
				rightBottomTextButton.setText("重新发送(" + remainLogin + ")");
			} else {
				rightBottomTextButton.setText("发送验证码");
			}
			if (data.userInformation.localConfig.line1Number != null && data.userInformation.localConfig.line1Number != "") {
				input1.setText(data.userInformation.localConfig.line1Number);
			} else {
				input1.setText("");
			}
			input2.setText("");
			input1.setInputType(InputType.TYPE_CLASS_NUMBER);
			input2.setInputType(InputType.TYPE_CLASS_NUMBER);
		} else if (status == Status.verifyPhoneForRegister) {
			leftTopText.setText("注册");
			mainButton.setText("下一步");
			rightTopTextButton.setVisibility(View.VISIBLE);
			rightTopTextButton.setText("取消");
			input1.setHint("请输入手机号");
			input2.setHint("请输入验证码");
			leftBottomTextButton.setVisibility(View.GONE);
			rightBottomTextButton.setVisibility(View.VISIBLE);
			if (remainRegister != 0) {
				rightBottomTextButton.setText("重新发送(" + remainRegister + ")");
			} else {
				rightBottomTextButton.setText("发送验证码");
			}
			if (data.userInformation.localConfig.line1Number != null && data.userInformation.localConfig.line1Number != "") {
				input1.setText(data.userInformation.localConfig.line1Number);
			} else {
				input1.setText("");
			}
			input1.setInputType(InputType.TYPE_CLASS_NUMBER);
			input2.setInputType(InputType.TYPE_CLASS_NUMBER);
		} else if (status == Status.verifyPhoneForResetPassword) {
			leftTopText.setVisibility(View.VISIBLE);
			leftTopText.setText("验证手机号");
			rightTopTextButton.setVisibility(View.VISIBLE);
			rightTopTextButton.setText("取消");
			input1.setHint("请输入手机号");
			input2.setHint("请输入验证码");
			mainButton.setText("下一步");
			leftBottomTextButton.setVisibility(View.INVISIBLE);
			rightBottomTextButton.setVisibility(View.VISIBLE);
			if (status == Status.verifyPhoneForResetPassword) {
				if (remainResetPassword != 0) {
					rightBottomTextButton.setText("重新发送(" + remainResetPassword + ")");
				} else {
					rightBottomTextButton.setText("发送验证码");
				}
				if (data.userInformation.localConfig.line1Number != null && data.userInformation.localConfig.line1Number != "") {
					input1.setText(data.userInformation.localConfig.line1Number);
				} else {
					input1.setText("");
				}
			}
			input2.setText("");
			input1.setInputType(InputType.TYPE_CLASS_NUMBER);
			input2.setInputType(InputType.TYPE_CLASS_NUMBER);
		} else if (status == Status.setPassword) {
			leftTopText.setVisibility(View.VISIBLE);
			leftTopText.setText("输入密码");
			rightTopTextButton.setVisibility(View.VISIBLE);
			rightTopTextButton.setText("取消");
			input1.setHint("请输入密码");
			input2.setHint("请确认密码");
			mainButton.setText("完成");
			leftBottomTextButton.setVisibility(View.INVISIBLE);
			rightBottomTextButton.setVisibility(View.INVISIBLE);
			input1.setText("");
			input2.setText("");
			input1.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
			input2.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
		} else if (status == Status.resetPassword) {
			leftTopText.setVisibility(View.VISIBLE);
			leftTopText.setText("重置密码");
			rightTopTextButton.setVisibility(View.VISIBLE);
			rightTopTextButton.setText("取消");
			input1.setHint("请输入密码");
			input2.setHint("请验证密码");
			mainButton.setText("完成");
			leftBottomTextButton.setVisibility(View.INVISIBLE);
			rightBottomTextButton.setVisibility(View.INVISIBLE);
			input1.setText("");
			input2.setText("");
			input1.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
			input2.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
		}
	}

	public PopupWindow inputPopWindow;
	public View inputDialogView;

	@SuppressWarnings("deprecation")
	public void showInputDialog() {
		mInflater = thisActivity.getLayoutInflater();
		inputDialogView = mInflater.inflate(R.layout.widget_alert_input_dialog, null);
		inputPopWindow = new PopupWindow(inputDialogView, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT, true);
		inputPopWindow.setBackgroundDrawable(new BitmapDrawable());
		inputPopWindow.showAtLocation(loginOrRegister, Gravity.CENTER, 0, 0);
	}

	// public BaseSpringSystem mSpringSystem0 = SpringSystem.create();
	public SpringConfig IMAGE_SPRING_CONFIG = SpringConfig.fromOrigamiTensionAndFriction(40, 9);
	public SpringConfig IMAGE_SPRING_CONFIG_TO = SpringConfig.fromOrigamiTensionAndFriction(40, 15);
	public Spring dialogSpring = mSpringSystem.createSpring().setSpringConfig(IMAGE_SPRING_CONFIG);
	public Spring dialogOutSpring = mSpringSystem.createSpring().setSpringConfig(IMAGE_SPRING_CONFIG_TO);
	public Spring dialogInSpring = mSpringSystem.createSpring().setSpringConfig(IMAGE_SPRING_CONFIG_TO);
	public View dialogRootView;
	public DialogShowSpringListener dialogSpringListener = new DialogShowSpringListener();

	public RelativeLayout cirlcesDialogContent;
	public PopupWindow circlePopWindow;
	public View circleDialogView;

	public RelativeLayout dialogContentView;
	public View inputDialigView;

	public LayoutInflater mInflater;

	public int SHOW_DIALOG = 0x01;
	public int DIALOG_SWITCH = 0x02;
	public int currentStatus = SHOW_DIALOG;

	@SuppressWarnings("deprecation")
	public void showCircleSettingDialog() {
		currentStatus = SHOW_DIALOG;
		mInflater = thisActivity.getLayoutInflater();
		dialogSpring.addListener(dialogSpringListener);
		// final DisplayMetrics displayMetrics = new DisplayMetrics();
		circleDialogView = mInflater.inflate(R.layout.circle_longclick_dialog, null);
		dialogContentView = (RelativeLayout) circleDialogView.findViewById(R.id.dialogContent);
		inputDialigView = circleDialogView.findViewById(R.id.inputDialogContent);

		dialogRootView = dialogContentView;
		dialogSpring.setCurrentValue(0);
		dialogSpring.setEndValue(1);

		circlePopWindow = new PopupWindow(circleDialogView, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT, true);
		circlePopWindow.setBackgroundDrawable(new BitmapDrawable());
		circlePopWindow.showAtLocation(loginOrRegister, Gravity.CENTER, 0, 0);

		circleDialogView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (currentStatus == SHOW_DIALOG) {
					dialogSpring.removeListener(dialogSpringListener);
					// circlePopWindow.dismiss();
					// dialogRootView = circleDialogView;
					currentStatus = DIALOG_SWITCH;
					dialogOutSpring.addListener(dialogSpringListener);
					dialogOutSpring.setCurrentValue(1.2);
					dialogOutSpring.setEndValue(0);
					dialogInSpring.addListener(dialogSpringListener);
					inputDialigView.setVisibility(View.VISIBLE);
					dialogInSpring.setCurrentValue(1);
					dialogInSpring.setEndValue(0);
				}
			}
		});
	}

	private class DialogShowSpringListener extends SimpleSpringListener {
		@Override
		public void onSpringUpdate(Spring spring) {
			float mappedValue = (float) spring.getCurrentValue();
			if (spring.equals(dialogSpring)) {
				dialogRootView.setScaleX(mappedValue);
				dialogRootView.setScaleY(mappedValue);
			} else if (spring.equals(dialogOutSpring)) {
				float y = dialogRootView.getTranslationY();
				dialogRootView.setTranslationY(y - 100 * mappedValue);
				Log.e(tag, mappedValue + "---------------");
				if (mappedValue <= 0.8f) {

				}
			} else if (spring.equals(dialogInSpring)) {
				float y = inputDialigView.getTranslationY();
				inputDialigView.setTranslationY(y - 1280 * mappedValue);
			}
		}
	}
}