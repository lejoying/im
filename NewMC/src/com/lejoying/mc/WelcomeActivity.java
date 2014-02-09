package com.lejoying.mc;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;

import com.lejoying.mc.adapter.AnimationAdapter;
import com.lejoying.mc.data.App;
import com.lejoying.mc.data.Data;
import com.lejoying.mc.data.handler.DataHandler.Modification;
import com.lejoying.mc.data.handler.DataHandler.UIModification;
import com.lejoying.mc.network.API;
import com.lejoying.mc.utils.AjaxAdapter;
import com.lejoying.mc.utils.MCNetUtils;
import com.lejoying.mc.utils.MCNetUtils.Settings;

public class WelcomeActivity extends Activity {

	App app = App.getInstance();

	ImageView imageview_mar;
	ImageView imageview_star;
	ImageView imageview_city;

	boolean isAnimationEnd;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout._welcome);

		checkLocalData();
		imageview_mar = (ImageView) findViewById(R.id.imageview_mar);
		imageview_star = (ImageView) findViewById(R.id.imageview_star);
		imageview_city = (ImageView) findViewById(R.id.imageview_city);

		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		final int width = dm.widthPixels;

		Animation marScaleAnimation = new ScaleAnimation(0.95f, 1f, 0.95f, 1f,
				0.5f, 0.5f);
		marScaleAnimation.setDuration(1800);
		marScaleAnimation.setAnimationListener(new AnimationAdapter() {
			@Override
			public void onAnimationStart(Animation animation) {
				super.onAnimationStart(animation);
				int[] cityLocation = new int[2];
				imageview_city.getLocationInWindow(cityLocation);
				int cityX = cityLocation[0];

				ImageView imageview_city_before = (ImageView) findViewById(R.id.imageview_city_before);
				imageview_city_before.getLocationInWindow(cityLocation);
				int cityBeforeX = cityLocation[0];
				Animation cityAnimation = new TranslateAnimation(cityBeforeX
						- cityX, 0, 0, 0);
				cityAnimation.setDuration(1800);
				imageview_city_before.setVisibility(View.GONE);
				imageview_city.setVisibility(View.VISIBLE);
				imageview_city.startAnimation(cityAnimation);

				int starWidth = imageview_star.getWidth();
				Animation starAnimation = new TranslateAnimation(0,
						-(width + starWidth), 0,
						(float) ((width + starWidth) / 2.08));
				starAnimation.setStartOffset(1000);
				starAnimation.setDuration(800);
				starAnimation.setAnimationListener(new AnimationAdapter() {
					@Override
					public void onAnimationEnd(Animation animation) {
						imageview_star.clearAnimation();
						imageview_city.clearAnimation();
						imageview_mar.clearAnimation();
						isAnimationEnd = true;
						selectDirection(STATUS_NONE);
					}
				});
				imageview_star.startAnimation(starAnimation);
			}

			@Override
			public void onAnimationEnd(Animation animation) {

			}
		});
		imageview_mar.startAnimation(marScaleAnimation);

	}

	String STATUS_NONE = "none";
	String STATUS_LOGIN = "login";
	String STATUS_MAIN = "main";
	String localDataStatus = STATUS_NONE;

	void checkLocalData() {
		new Thread() {
			public void run() {
				app.sDcardDataResolver.readConfig();
				if (app.config.lastLoginPhone == null
						|| app.config.lastLoginPhone.equals("none")
						|| app.config.lastLoginPhone.equals("")) {
					selectDirection(STATUS_LOGIN);
				} else {
					app.sDcardDataResolver.readData(new UIModification() {
						@Override
						public void modifyUI() {
							if (app.data.user.accessKey == null
									|| app.data.user.accessKey.equals("")) {
								selectDirection(STATUS_LOGIN);
							} else {
								MCNetUtils.ajax(new AjaxAdapter() {
									@Override
									public void setParams(Settings settings) {
										settings.url = API.ACCOUNT_GET;
										Bundle params = new Bundle();
										params.putString("phone",
												app.data.user.phone);
										params.putString("accessKey",
												app.data.user.accessKey);
										params.putString("target",
												app.data.user.phone);
										settings.params = params;
									}

									@Override
									public void onSuccess(JSONObject jData) {
										try {
											final JSONObject jUser = jData
													.getJSONObject("account");
											app.dataHandler.modifyData(
													new Modification() {
														public void modify(
																Data data) {
															app.mJSONHandler
																	.updateUser(
																			jUser,
																			data);
														}
													}, new UIModification() {

														@Override
														public void modifyUI() {
															selectDirection(STATUS_MAIN);
														}
													});
										} catch (JSONException e) {
											selectDirection(STATUS_LOGIN);
										}
									}

									@Override
									public void noInternet() {
										selectDirection(STATUS_MAIN);
									}

									@Override
									public void failed() {
										selectDirection(STATUS_MAIN);
									}

									@Override
									public void timeout() {
										selectDirection(STATUS_MAIN);
									}
								});
							}
						}
					});
				}
			}
		}.start();

	}

	void selectDirection(String localDataStatus) {
		if (!localDataStatus.equals(STATUS_NONE)) {
			this.localDataStatus = localDataStatus;
		}
		if (!isAnimationEnd) {
			return;
		}
		if (this.localDataStatus.equals(STATUS_MAIN)) {
			startToMain();
		} else if (this.localDataStatus.equals(STATUS_LOGIN)) {
			startToLogin();
		}
	}

	void startToLogin() {
		Intent intent = new Intent(WelcomeActivity.this, LoginActivity.class);
		startActivity(intent);
		WelcomeActivity.this.finish();
	}

	void startToMain() {
		Intent intent = new Intent(WelcomeActivity.this, MainActivity.class);
		startActivity(intent);
		WelcomeActivity.this.finish();
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

}
