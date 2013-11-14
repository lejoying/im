package com.lejoying.mc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONObject;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lejoying.adapter.MCResponseAdapter;
import com.lejoying.api.AccountManager;
import com.lejoying.apiimpl.AccountManagerImpl;
import com.lejoying.mcutils.CircleMenu;
import com.lejoying.mcutils.MCTools;
import com.lejoying.mcutils.MenuEntity;

public class RegisterActivity extends Activity {

	private final int STATE_VERIFYPHONE = 0x001;
	private final int STATE_VERIFYCODE = 0x002;
	private final int STATE_SETPASS = 0x003;
	private int state;

	private RelativeLayout rl_regpanel_phone;
	private RelativeLayout rl_regpanel_code;
	private RelativeLayout rl_regpanel_pass;
	private EditText et_phone;
	private EditText et_code;
	private EditText et_pass;
	private TextView tv_resend;

	private AccountManager accountManager;

	private Handler handler;

	private int resendTime;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.register);

		// 启动activity时自动弹出软键盘
		getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);

		initView();
	}

	public void initView() {
		state = STATE_VERIFYPHONE;
		rl_regpanel_phone = (RelativeLayout) findViewById(R.id.rl_regpanel_phone);
		rl_regpanel_code = (RelativeLayout) findViewById(R.id.rl_regpanel_code);
		rl_regpanel_pass = (RelativeLayout) findViewById(R.id.rl_regpanel_pass);
		et_phone = (EditText) findViewById(R.id.et_phone);
		et_code = (EditText) findViewById(R.id.et_code);
		et_pass = (EditText) findViewById(R.id.et_pass);
		tv_resend = (TextView) findViewById(R.id.tv_resend);

		accountManager = new AccountManagerImpl(this);
		handler = MCTools.handler;

		CircleMenu circleMenu = new CircleMenu(this);
		List<MenuEntity> menuList = new ArrayList<MenuEntity>();
		menuList.add(new MenuEntity());
		menuList.add(new MenuEntity());
		menuList.add(new MenuEntity());
		menuList.add(new MenuEntity());
		menuList.add(new MenuEntity());
		menuList.add(new MenuEntity());
		circleMenu.showMenu(CircleMenu.SHOW_TOP, menuList);
	}

	public void next(View v) {
		if (state == STATE_VERIFYPHONE) {
			Map<String, String> param = new HashMap<String, String>();
			param.put("phone", et_phone.getText().toString());
			param.put("usage", "register");
			accountManager.verifyphone(param, new MCResponseAdapter(this) {
				@Override
				public void success(JSONObject data) {
					System.out.println(data);
					Animation animation = AnimationUtils.loadAnimation(
							RegisterActivity.this, R.anim.tran_out_top);
					animation.setAnimationListener(new AnimationListener() {

						@Override
						public void onAnimationStart(Animation animation) {
							// TODO Auto-generated method stub

						}

						@Override
						public void onAnimationRepeat(Animation animation) {
							// TODO Auto-generated method stub

						}

						@Override
						public void onAnimationEnd(Animation animation) {
							rl_regpanel_phone.setVisibility(View.GONE);
						}
					});
					rl_regpanel_phone.startAnimation(animation);

					rl_regpanel_code.setVisibility(View.VISIBLE);

					Animation animation2 = AnimationUtils.loadAnimation(
							RegisterActivity.this, R.anim.tran_in_bottom);
					rl_regpanel_code.startAnimation(animation2);
					resendTime = 60;
					Timer timer = new Timer();
					timer.schedule(new TimerTask() {
						@Override
						public void run() {
							handler.post(new Runnable() {
								@Override
								public void run() {
									tv_resend.setText("重新发送(" + resendTime
											+ ")");
									resendTime--;
								}
							});
						}
					}, 1000, 1000);

					state = STATE_VERIFYCODE;
				}
			});
		} else if (state == STATE_VERIFYCODE) {
			Animation animation = AnimationUtils.loadAnimation(this,
					R.anim.tran_out_top);
			animation.setAnimationListener(new AnimationListener() {

				@Override
				public void onAnimationStart(Animation animation) {
					// TODO Auto-generated method stub

				}

				@Override
				public void onAnimationRepeat(Animation animation) {
					// TODO Auto-generated method stub

				}

				@Override
				public void onAnimationEnd(Animation animation) {
					rl_regpanel_code.setVisibility(View.GONE);
				}
			});
			rl_regpanel_code.startAnimation(animation);

			rl_regpanel_pass.setVisibility(View.VISIBLE);

			Animation animation2 = AnimationUtils.loadAnimation(this,
					R.anim.tran_in_bottom);
			rl_regpanel_pass.startAnimation(animation2);

			state = STATE_SETPASS;
		} else if (state == STATE_SETPASS) {

		}
	}

}