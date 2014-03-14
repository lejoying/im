package com.lejoying.wxgs.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;

import com.lejoying.wxgs.R;
import com.lejoying.wxgs.activity.view.widget.CircleMenu;
import com.lejoying.wxgs.app.MainApplication;
import com.lejoying.wxgs.app.adapter.AnimationAdapter;

public class LaunchActivity extends BaseActivity {

	public static final String TAG = "LaunchActivity";

	MainApplication app = MainApplication.getMainApplication();

	ImageView mViewMar;
	ImageView mViewStar;
	ImageView mViewCity;

	boolean isAnimationEnd;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.activity_launch);
		initView();
		initEvent();
		CircleMenu.create(this);
		super.onCreate(savedInstanceState);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	public void initView() {
		mViewMar = (ImageView) findViewById(R.id.imageview_mar);
		mViewStar = (ImageView) findViewById(R.id.imageview_star);
		mViewCity = (ImageView) findViewById(R.id.imageview_city);

		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		final int width = dm.widthPixels;

		Animation marScaleAnimation = new ScaleAnimation(0.95f, 1f, 0.95f, 1f,
				0.5f, 0.5f);
		marScaleAnimation.setDuration(1800);
		marScaleAnimation
				.setAnimationListener(new com.lejoying.wxgs.app.adapter.AnimationAdapter() {
					@Override
					public void onAnimationStart(Animation animation) {
						super.onAnimationStart(animation);
						int[] cityLocation = new int[2];
						mViewCity.getLocationInWindow(cityLocation);
						int cityX = cityLocation[0];

						ImageView imageview_city_before = (ImageView) findViewById(R.id.imageview_city_before);
						imageview_city_before.getLocationInWindow(cityLocation);
						int cityBeforeX = cityLocation[0];
						Animation cityAnimation = new TranslateAnimation(
								cityBeforeX - cityX, 0, 0, 0);
						cityAnimation.setDuration(1800);
						imageview_city_before.setVisibility(View.GONE);
						mViewCity.setVisibility(View.VISIBLE);
						mViewCity.startAnimation(cityAnimation);

						int starWidth = mViewStar.getWidth();
						Animation starAnimation = new TranslateAnimation(0,
								-(width + starWidth), 0,
								(float) ((width + starWidth) / 2.08));
						starAnimation.setStartOffset(1000);
						starAnimation.setDuration(800);
						starAnimation
								.setAnimationListener(new AnimationAdapter() {
									@Override
									public void onAnimationEnd(
											Animation animation) {
										mViewStar.clearAnimation();
										mViewCity.clearAnimation();
										mViewMar.clearAnimation();
										isAnimationEnd = true;
										startActivity(new Intent(
												LaunchActivity.this,
												MainActivity.class));
										finish();
									}
								});
						mViewStar.startAnimation(starAnimation);
					}

					@Override
					public void onAnimationEnd(Animation animation) {

					}
				});
		mViewMar.startAnimation(marScaleAnimation);
	}

	public void initEvent() {
		// TODO Auto-generated method stub

	}
}
