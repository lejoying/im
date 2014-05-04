package com.lejoying.wxgs.activity;

import java.io.FileNotFoundException;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.lejoying.wxgs.R;
import com.lejoying.wxgs.activity.mode.LoginModeManager;
import com.lejoying.wxgs.activity.mode.MainModeManager;
import com.lejoying.wxgs.activity.utils.DataUtil;
import com.lejoying.wxgs.activity.utils.LocationUtils;
import com.lejoying.wxgs.activity.utils.DataUtil.GetDataListener;
import com.lejoying.wxgs.activity.view.BackgroundView;
import com.lejoying.wxgs.activity.view.widget.Alert;
import com.lejoying.wxgs.activity.view.widget.CircleMenu;
import com.lejoying.wxgs.app.MainApplication;
import com.lejoying.wxgs.app.data.Data;
import com.lejoying.wxgs.app.handler.DataHandler.Modification;
import com.lejoying.wxgs.app.parser.StreamParser;
import com.lejoying.wxgs.app.service.PushService;

public class MainActivity extends BaseActivity {

	MainApplication app = MainApplication.getMainApplication();
	AudioManager audioManager;

	public static MainActivity instance;

	public static final String TAG = "MainActivity";

	BackgroundView mBackground;

	public static final String MODE_LOGIN = "login";
	public static final String MODE_MAIN = "main";

	public String mode = "";

	int mContentID = R.id.fragmentContent;

	FragmentManager mFragmentManager;

	LongConnectionReceiver mReceiver;

	public LoginModeManager mLoginMode;
	public MainModeManager mMainMode;

	public View currentMenuSelected;
	public LinearLayout ll_menu_app;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		// getWindow().setSoftInputMode(
		// WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

		instance = this;

		setContentView(R.layout.activity_main);
		mBackground = (BackgroundView) findViewById(R.id.mainBackGround);
		mBackground.setBackground(R.drawable.background2);
		mFragmentManager = getSupportFragmentManager();

		ll_menu_app = (LinearLayout) findViewById(R.id.ll_menu_app);
		// ll_menu_app.setVisibility(View.VISIBLE);

		audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
		// audioManager.setSpeakerphoneOn(false);
		setVolumeControlStream(AudioManager.STREAM_VOICE_CALL);
		// audioManager.setMode(AudioManager.MODE_IN_CALL);
		audioManager.setMode(AudioManager.MODE_NORMAL);

		mReceiver = new LongConnectionReceiver();
		IntentFilter filter = new IntentFilter(PushService.LONGPULL_FAILED);
		registerReceiver(mReceiver, filter);

		Alert.initialize(this);
		CircleMenu.create(this);

		initMode();

		switchMode();
	}

	@Override
	protected void onDestroy() {
		unregisterReceiver(mReceiver);
		instance = null;
		super.onDestroy();
	}

	void initMode() {
		if (mMainMode == null) {
			mMainMode = new MainModeManager(this);
		}
		if (mLoginMode == null) {
			mLoginMode = new LoginModeManager(this);
		}
	}

	public void switchMode() {
		if (app.data.user.phone.equals("")
				|| app.data.user.accessKey.equals("")) {
			if (!mode.equals(MODE_LOGIN)) {
				mode = MODE_LOGIN;
				mMainMode.release();
				mLoginMode.initialize();
				mLoginMode.show(mLoginMode.mLoginUsePassFragment);
			}
		} else if (!app.data.user.phone.equals("")
				&& !app.data.user.accessKey.equals("")) {
			LocationUtils.updateLocation();
			if (!mode.equals(MODE_MAIN)) {
				mode = MODE_MAIN;
				mLoginMode.release();
				mMainMode.initialize();
				initEvent();
				ll_menu_app.setVisibility(View.VISIBLE);
				// mMainMode.show(mMainMode.mSquareFragment);
				mMainMode.show(mMainMode.mSquareFragment);
				PushService.startIMLongPull(this);

				if (app.data.isClear) {
					app.dataHandler.exclude(new Modification() {
						@Override
						public void modifyData(Data data) {
							try {
								Data localData = (Data) StreamParser
										.parseToObject(openFileInput(data.user.phone));
								if (localData != null) {
									data.user.head = localData.user.head;
									data.user.nickName = localData.user.nickName;
									data.user.mainBusiness = localData.user.mainBusiness;
									data.circles = localData.circles;
									data.friends = localData.friends;
									data.groups = localData.groups;
									data.groupFriends = localData.groupFriends;
									data.lastChatFriends = localData.lastChatFriends;
									data.newFriends = localData.newFriends;
									data.squareFlags = localData.squareFlags;
									data.squareMessages = localData.squareMessages;
									data.squareMessagesClassify = localData.squareMessagesClassify;
									data.squareMessagesMap = localData.squareMessagesMap;
								}
							} catch (FileNotFoundException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}

					});
				}

				DataUtil.getUser(new GetDataListener() {
					@Override
					public void getSuccess() {
						DataUtil.getGroups(new GetDataListener() {
							@Override
							public void getSuccess() {
								DataUtil.getCircles(new GetDataListener() {
									@Override
									public void getSuccess() {
										DataUtil.getMessages(new GetDataListener() {
											@Override
											public void getSuccess() {
												DataUtil.getAskFriends(new GetDataListener() {
													@Override
													public void getSuccess() {
														// mAdapter.notifyDataSetChanged();
														if (mMainMode.mGroupFragment
																.isAdded()) {
															mMainMode.mGroupFragment
																	.notifyViews();
														}
														if (mMainMode.mCirclesFragment
																.isAdded()) {
															mMainMode.mCirclesFragment
																	.notifyViews();
														}
													}

												});
											}
										});
									}

								});
							}
						});

					}

				});
			}
		}
	}

	private void initEvent() {
		RelativeLayout rl_square_menu = (RelativeLayout) findViewById(R.id.rl_square_menu);
		final ImageView iv_square_menu = (ImageView) findViewById(R.id.iv_square_menu);
		RelativeLayout rl_group_menu = (RelativeLayout) findViewById(R.id.rl_group_menu);
		final ImageView iv_group_menu = (ImageView) findViewById(R.id.iv_group_menu);
		RelativeLayout rl_me_menu = (RelativeLayout) findViewById(R.id.rl_me_menu);
		final ImageView iv_me_menu = (ImageView) findViewById(R.id.iv_me_menu);
		RelativeLayout rl_release_menu = (RelativeLayout) findViewById(R.id.rl_release_menu);

		currentMenuSelected = iv_square_menu;
		rl_square_menu.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (currentMenuSelected != iv_square_menu) {
					mMainMode.show(mMainMode.mSquareFragment);
					modifyMenuSelected(currentMenuSelected, iv_square_menu);
				}
			}
		});
		rl_group_menu.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (currentMenuSelected != iv_group_menu) {
					mMainMode.show(mMainMode.mGroupFragment);
					modifyMenuSelected(currentMenuSelected, iv_group_menu);
				}
			}
		});
		rl_me_menu.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (currentMenuSelected != iv_me_menu) {
					mMainMode.show(mMainMode.mCirclesFragment);
					modifyMenuSelected(currentMenuSelected, iv_me_menu);
				}
			}
		});
		rl_release_menu.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				startActivity(new Intent(MainActivity.this,
						ReleaseActivity.class));
			}
		});
	}

	void modifyMenuSelected(View v1, View v2) {
		if (v1 != null) {
			v1.setVisibility(View.GONE);
		}
		if (v1 != null) {
			v2.setVisibility(View.VISIBLE);
			currentMenuSelected = v2;
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (mode.equals(MODE_LOGIN)) {
			return mLoginMode.onKeyDown(keyCode, event)
					&& super.onKeyDown(keyCode, event);
		} else if (mode.equals(MODE_MAIN)) {
			return mMainMode.onKeyDown(keyCode, event)
					&& super.onKeyDown(keyCode, event);
		}
		return super.onKeyDown(keyCode, event);

	}

	@Override
	protected void onPause() {
		if (mode.equals(MODE_MAIN)) {
			DataUtil.saveData(this);
		}
		super.onPause();
	}

	@Override
	public void finish() {
		CircleMenu.hideImmediately(false);
		super.finish();
	}

	class LongConnectionReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			switchMode();
		}
	}

}
