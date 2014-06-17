package com.lejoying.wxgs.activity;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.lejoying.wxgs.R;
import com.lejoying.wxgs.app.MainApplication;

public class LoginActivity extends Activity {

	private MainApplication app = MainApplication.getMainApplication();

	private Handler handler;

	private View loginOrRegister;
	private View loginOrRegisterButton;
	private View loginButton;
	private View registerButton;
	private View card;

	private enum Status {
		welcome, start, loginOrRegister, loginUseThePassword, verifyPhoneForRegister, verifyPhoneForResetPassword, verifyPhoneForLogin, setPassword, resetPassword
	}

	private Status status;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		if (app.config.isNeedToShowWelcome) {
			status = Status.welcome;
		} else {
			status = Status.start;
		}

		setContentView(R.layout.activity_login);

		initView();

		handler = new Handler();
		handler.postDelayed(new Runnable() {
			@Override
			public void run() {
				Animation animationIn = AnimationUtils.loadAnimation(
						LoginActivity.this, R.anim.animation_set_back_in);
				loginOrRegisterButton.setVisibility(View.VISIBLE);
				loginOrRegisterButton.startAnimation(animationIn);
			}
		}, 2000);

		loginButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) {
				Animation animationOut = AnimationUtils.loadAnimation(
						LoginActivity.this, R.anim.animation_set_next_out);
				loginOrRegister.setVisibility(View.INVISIBLE);
				loginOrRegister.startAnimation(animationOut);
				Animation animationIn = AnimationUtils.loadAnimation(
						LoginActivity.this, R.anim.animation_set_next_in);
				card.setVisibility(View.VISIBLE);
				animationIn.setStartOffset(animationOut.getDuration());
				card.startAnimation(animationIn);
			}
		});
		registerButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) {
				Animation animationOut = AnimationUtils.loadAnimation(
						LoginActivity.this, R.anim.animation_set_next_out);
				loginOrRegister.setVisibility(View.INVISIBLE);
				loginOrRegister.startAnimation(animationOut);
				Animation animationIn = AnimationUtils.loadAnimation(
						LoginActivity.this, R.anim.animation_set_next_in);
				card.setVisibility(View.VISIBLE);
				card.startAnimation(animationIn);
			}
		});

		super.onCreate(savedInstanceState);
	}

	private void initView() {
		loginOrRegister = findViewById(R.id.loginOrRegister);
		loginOrRegisterButton = findViewById(R.id.loginOrRegisterButton);
		loginButton = findViewById(R.id.loginButton);
		registerButton = findViewById(R.id.registerButton);
		card = findViewById(R.id.card);
	}
}
