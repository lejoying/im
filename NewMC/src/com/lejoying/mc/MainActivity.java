package com.lejoying.mc;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import com.baidu.location.BDLocation;
import com.lejoying.mc.data.App;
import com.lejoying.mc.data.handler.LocationHandler.LocationListener;
import com.lejoying.mc.fragment.CircleMenuFragment;
import com.lejoying.mc.fragment.CircleMenuFragment.CircleDiskAnimationEnd;
import com.lejoying.mc.fragment.CircleMenuFragment.CircleMenuCreateListener;
import com.lejoying.mc.fragment.BusinessCardFragment;
import com.lejoying.mc.fragment.CircleMenuFragment.OnMenuItemClickListener;
import com.lejoying.mc.fragment.FriendsFragment;
import com.lejoying.mc.fragment.GroupFragment;
import com.lejoying.mc.fragment.ScanQRCodeFragment;
import com.lejoying.mc.fragment.SquareFragment;
import com.lejoying.mc.network.API;
import com.lejoying.mc.service.PushService;
import com.lejoying.mc.utils.AjaxAdapter;
import com.lejoying.mc.utils.MCNetUtils;
import com.lejoying.mc.utils.MCNetUtils.Settings;

public class MainActivity extends BaseFragmentActivity {

	App app = App.getInstance();

	public static MainActivity instance;

	public Fragment nowMain;

	public CircleMenuFragment circleMenuFragment;
	public FriendsFragment friendsFragment;
	public GroupFragment groupFragment;
	public SquareFragment squareFragment;

	FragmentManager fragmentManager;

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout._main);

		fragmentManager = getSupportFragmentManager();

		friendsFragment = (FriendsFragment) fragmentManager
				.findFragmentById(R.id.friends);
		groupFragment = (GroupFragment) fragmentManager
				.findFragmentById(R.id.groups);
		squareFragment = (SquareFragment) fragmentManager
				.findFragmentById(R.id.square);
		circleMenuFragment = (CircleMenuFragment) fragmentManager
				.findFragmentById(R.id.circleMenu);

		fragmentManager.beginTransaction().hide(groupFragment)
				.hide(squareFragment).commit();

		nowMain = friendsFragment;

		if (circleMenuFragment.isCreated()) {
			circleMenuFragment.showToTop(false, false);
			circleMenuFragment.setPageName(getString(R.string.page_friend));
		} else {
			circleMenuFragment
					.setCreateListener(new CircleMenuCreateListener() {
						@Override
						public void created() {
							circleMenuFragment.showToTop(false, false);
							circleMenuFragment
									.setPageName(getString(R.string.page_friend));
						}
					});
		}

		Intent service = new Intent(this, PushService.class);
		service.putExtra("objective", "start");
		startService(service);
		instance = this;

		app.locationHandler.requestLocation(new LocationListener() {

			@Override
			public void onReceivePoi(BDLocation poiLocation) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onReceiveLocation(final BDLocation location) {

				location.getLatitude();

				MCNetUtils.ajax(new AjaxAdapter() {

					@Override
					public void setParams(Settings settings) {
						try {
							settings.url = API.LBS_UPDATELOCATION;
							Bundle params = new Bundle();
							params.putString("phone", app.data.user.phone);
							params.putString("accessKey",
									app.data.user.accessKey);
							JSONObject jLocation = new JSONObject();
							jLocation.put("longitude",
									String.valueOf(location.getLongitude()));
							jLocation.put("latitude",
									String.valueOf(location.getLatitude()));
							params.putString("location", jLocation.toString());

							JSONObject jUser = new JSONObject();

							jUser.put("mainBusiness",
									app.data.user.mainBusiness);
							jUser.put("head", app.data.user.head);
							jUser.put("nickName", app.data.user.nickName);

							params.putString("account", jUser.toString());
							settings.params = params;

						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}

					@Override
					public void onSuccess(JSONObject jData) {
						System.out.println(jData);
					}

				});

			}
		});

		initEvent();
	}

	public void initEvent() {
		circleMenuFragment
				.setOnMenuItemClickListener(new OnMenuItemClickListener() {
					@Override
					public void onItemClick(int itemLocation) {
						// TODO Auto-generated method stub
						switch (itemLocation) {
						case 1:
							circleMenuFragment
									.back(new CircleDiskAnimationEnd() {
										@Override
										public void outAnimationEnd() {
											changeMain(squareFragment);
										}

										@Override
										public void diskAnimationEnd() {

										}
									});
							break;
						case 2:
							circleMenuFragment
									.back(new CircleDiskAnimationEnd() {
										@Override
										public void outAnimationEnd() {
											if (!app.mark
													.equals(app.groupFragment)) {
												changeMain(groupFragment);
											}
										}

										@Override
										public void diskAnimationEnd() {

										}
									});
							break;
						case 3:
							circleMenuFragment
									.back(new CircleDiskAnimationEnd() {

										@Override
										public void outAnimationEnd() {
											startToActivity(HotActivity.class,
													false);
										}

										@Override
										public void diskAnimationEnd() {

										}
									});
							break;
						case 4:
							circleMenuFragment.showNext();
							break;
						case 11:
							circleMenuFragment
									.back(new CircleDiskAnimationEnd() {

										@Override
										public void outAnimationEnd() {
											app.businessCardStatus = app.SHOW_SELF;
											replaceToContent(
													new BusinessCardFragment(),
													true);
										}

										@Override
										public void diskAnimationEnd() {

										}
									});
							break;
						case 12:
							circleMenuFragment
									.back(new CircleDiskAnimationEnd() {
										@Override
										public void outAnimationEnd() {
											replaceToContent(
													new ScanQRCodeFragment(),
													true);
										}

										@Override
										public void diskAnimationEnd() {

										}
									});

							break;
						case 13:

							break;
						case 14:
							circleMenuFragment.showBack();
							break;
						case 15:
							app.data.user.accessKey = null;
							Intent service = new Intent(MainActivity.this,
									PushService.class);
							service.putExtra("objective", "stop");
							startService(service);
							startToActivity(LoginActivity.class, true);
							break;
						case 16:
							circleMenuFragment
									.back(new CircleDiskAnimationEnd() {
										@Override
										public void outAnimationEnd() {
											changeMain(friendsFragment);
										}

										@Override
										public void diskAnimationEnd() {

										}
									});
							break;

						default:
							break;
						}
					}
				});

	}

	public void changeMain(Fragment fragment) {
		boolean flag = false;
		if (app.mark.equals(app.friendsFragment)
				&& fragment instanceof FriendsFragment) {
			flag = true;
		} else if (app.mark.equals(app.groupFragment)
				&& fragment instanceof GroupFragment) {
			flag = true;
		} else if (app.mark.equals(app.squareFragment)
				&& fragment instanceof SquareFragment) {
			flag = true;
		}
		if (!flag) {
			fragmentManager
					.beginTransaction()
					.setCustomAnimations(R.anim.activity_in,
							R.anim.activity_out, R.anim.activity_in2,
							R.anim.activity_out2).hide(nowMain).show(fragment)
					.commit();
			nowMain = fragment;
			if (nowMain instanceof FriendsFragment) {
				app.mark = app.friendsFragment;
			} else if (nowMain instanceof GroupFragment) {
				app.mark = app.groupFragment;
			} else if (nowMain instanceof SquareFragment) {
				app.mark = app.squareFragment;
			}
		}
	}

	@Override
	protected int setBackground() {
		// TODO Auto-generated method stub
		return R.drawable.cloudy_d_blur;
	}

	@Override
	protected void onPause() {
		app.sDcardDataResolver.saveToSDcard();
		super.onPause();
	}

	@Override
	protected void onDestroy() {
		instance = null;
		super.onDestroy();
	}

	@Override
	public void finish() {
		Intent service = new Intent(this, PushService.class);
		service.putExtra("objective", "stop");
		startService(service);
		super.finish();
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	public void onConfigurationChanged(Configuration config) {
		super.onConfigurationChanged(config);
	}
}
