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

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		// getWindow().setSoftInputMode(
		// WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

		instance = this;

		setContentView(R.layout.activity_main);
		mBackground = (BackgroundView) findViewById(R.id.mainBackGround);
		mBackground.setBackground(R.drawable.background1);
		mFragmentManager = getSupportFragmentManager();

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
				mMainMode.show(mMainMode.mCirclesFragment);
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
