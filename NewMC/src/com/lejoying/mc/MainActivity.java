package com.lejoying.mc;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentManager.OnBackStackChangedListener;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.lejoying.mc.fragment.CircleMenuFragment;
import com.lejoying.mc.fragment.LoginUseCodeFragment;
import com.lejoying.mc.fragment.LoginUsePassFragment;
import com.lejoying.mc.fragment.LoginUsePassFragment.LoginUsrPassListener;

public class MainActivity extends BaseFragmentActivity implements
		LoginUsrPassListener {

	private CircleMenuFragment circle;
	private LoginUsePassFragment pLogin;
	private LoginUseCodeFragment cLogin;

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout._main);
		circle = new CircleMenuFragment();
		mFragmentManager.beginTransaction().add(R.id.fl_circleMenu, circle)
				.commit();
		pLogin = new LoginUsePassFragment();
		mFragmentManager.beginTransaction()
				.setCustomAnimations(R.anim.activity_in, R.anim.activity_out)
				.add(R.id.fl_content, pLogin).commit();
		cLogin = new LoginUseCodeFragment();
	}

	@Override
	public View onCreateView(String name, Context context, AttributeSet attrs) {
		// TODO Auto-generated method stub
		return super.onCreateView(name, context, attrs);
	}

	public void login(View v) {
		System.out.println("is Click");
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {

		return false;
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.tv_clogin:
			mFragmentManager
					.beginTransaction()
					.setCustomAnimations(R.anim.activity_in,
							R.anim.activity_out, R.anim.activity_in2,
							R.anim.activity_out2).addToBackStack("clogin")
					.replace(R.id.fl_content, cLogin).commit();
			mFragmentManager
					.addOnBackStackChangedListener(new OnBackStackChangedListener() {

						@Override
						public void onBackStackChanged() {
							System.out.println();
						}
					});
			circle.showToTop();
			break;

		default:
			break;
		}
	}
}
