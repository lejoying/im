package com.open.welinks.view;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.text.InputType;
import android.view.Gravity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

import com.facebook.rebound.BaseSpringSystem;
import com.facebook.rebound.Spring;
import com.facebook.rebound.SpringConfig;
import com.facebook.rebound.SpringSystem;
import com.open.welinks.R;
import com.open.welinks.controller.LoginController;
import com.open.welinks.model.Data;
import com.open.welinks.utils.Alert;
import com.open.welinks.utils.Alert.AlertInputDialog;
import com.open.welinks.utils.Alert.AlertInputDialog.OnDialogClickListener;

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
	public EditText input1;
	public EditText input2;
	public View clearInput1;
	public View clearInput2;
	public TextView mainButton;
	public TextView leftBottomTextButton;
	public TextView rightBottomTextButton;
	public ImageView appIconToName;

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
	}

	public void initView() {

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
		mRootView = appIconToName;
	}

	int remainRegister = 0;
	int remainLogin = 0;
	int remainResetPassword = 0;

	public void setCardContent(Status status) {
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
			leftBottomTextButton.setVisibility(View.VISIBLE);
			leftBottomTextButton.setText("忘记密码?");
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

	public RelativeLayout cirlcesDialogContent;
	public PopupWindow circlePopWindow;
	public View circleDialogView;

	public void showCircleSettingDialog() {
		// DisplayMetrics displayMetrics = new DisplayMetrics();
		circleDialogView = thisActivity.getLayoutInflater().inflate(R.layout.circle_longclick_dialog, null);
		circlePopWindow = new PopupWindow(circleDialogView, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT, true);
		circlePopWindow.setBackgroundDrawable(new BitmapDrawable());
		// RelativeLayout mainContentView = (RelativeLayout)
		// circleDialogView.findViewById(R.id.mainContent);

		// cirlcesDialogContent = (RelativeLayout)
		// circleDialogView.findViewById(R.id.groupsContent);
		// RelativeLayout.LayoutParams mainContentParams =
		// (RelativeLayout.LayoutParams) mainContentView.getLayoutParams();
		// mainContentParams.height = (int) (displayMetrics.heightPixels *
		// 0.7578125f);
		// mainContentParams.leftMargin = (int) (20 / displayMetrics.density +
		// 0.5f);
		// mainContentParams.rightMargin = (int) (20 / displayMetrics.density +
		// 0.5f);
		// mainContentView.setLayoutParams(mainContentParams);
		circlePopWindow.showAtLocation(loginOrRegister, Gravity.CENTER, 0, 0);
	}

	public void showALert() {
		Alert.createInputDialog(context).setTitle("请输入信息").setOnConfirmClickListener(new OnDialogClickListener() {
			@Override
			public void onClick(final AlertInputDialog dialog) {

			}
		}).show();
	}
}