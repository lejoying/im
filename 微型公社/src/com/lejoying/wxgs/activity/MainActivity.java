package com.lejoying.wxgs.activity;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

import com.lejoying.wxgs.R;
import com.lejoying.wxgs.activity.mode.LoginModeManager;
import com.lejoying.wxgs.activity.mode.MainModeManager;
import com.lejoying.wxgs.activity.mode.fragment.ChatFriendFragment;
import com.lejoying.wxgs.activity.utils.DataUtil;
import com.lejoying.wxgs.activity.utils.DataUtil.GetDataListener;
import com.lejoying.wxgs.activity.utils.LocationUtils;
import com.lejoying.wxgs.activity.utils.NotificationUtils;
import com.lejoying.wxgs.activity.view.BackgroundView;
import com.lejoying.wxgs.activity.view.widget.Alert;
import com.lejoying.wxgs.app.MainApplication;
import com.lejoying.wxgs.app.data.Data;
import com.lejoying.wxgs.app.handler.DataHandler.Modification;
import com.lejoying.wxgs.app.parser.StreamParser;
import com.lejoying.wxgs.app.service.PushService;

public class MainActivity extends BaseActivity {

	MainApplication app = MainApplication.getMainApplication();
	AudioManager audioManager;

	LayoutInflater inflater;

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

	int height, width, dip, picwidth;
	float density;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub

		super.onCreate(savedInstanceState);

		// getWindow().setSoftInputMode(
		// WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

		instance = this;

		setContentView(R.layout.activity_main);
		inflater = (LayoutInflater) this
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mBackground = (BackgroundView) findViewById(R.id.mainBackGround);
		mBackground.setBackground(R.drawable.background3);
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
		// CircleMenu.create(this);

		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		density = dm.density;
		dip = (int) (40 * density + 0.5f);
		height = dm.heightPixels;
		width = dm.widthPixels;

		initMode();

		switchMode();

	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		if ("chatFriend".equals(NotificationUtils.showFragment)) {
			mMainMode.mChatFragment.mStatus = ChatFriendFragment.CHAT_FRIEND;
			mMainMode.mChatFragment.mNowChatFriend = app.data.friends
					.get(NotificationUtils.message.phone);
			mMainMode.showNext(mMainMode.mChatFragment);
		}
		NotificationUtils.cancelNotification(MainActivity.this);
		super.onWindowFocusChanged(hasFocus);
	}

	@Override
	protected void onDestroy() {
		unregisterReceiver(mReceiver);
		System.out.println("destroy");
		instance = null;
		super.onDestroy();
	}

	void initMode() {
		System.out.println(mMainMode == null);
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
									data.currentSquare = localData.currentSquare;
									data.newFriends = localData.newFriends;
									data.squareFlags = localData.squareFlags;
									data.squareMessages = localData.squareMessages;
									data.squareMessagesClassify = localData.squareMessagesClassify;
									data.squareMessagesMap = localData.squareMessagesMap;
									data.squareCollects = localData.squareCollects;
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

		RelativeLayout selectCommunity = (RelativeLayout) findViewById(R.id.rl_communityName);

		RelativeLayout rl_square_menu = (RelativeLayout) findViewById(R.id.rl_square_menu);
		RelativeLayout rl_group_menu = (RelativeLayout) findViewById(R.id.rl_group_menu);
		RelativeLayout rl_me_menu = (RelativeLayout) findViewById(R.id.rl_me_menu);
		RelativeLayout rl_release_menu = (RelativeLayout) findViewById(R.id.rl_release_menu);

		final ImageView iv_square_menu = (ImageView) findViewById(R.id.iv_square_menu);
		final ImageView iv_group_menu = (ImageView) findViewById(R.id.iv_group_menu);
		final ImageView iv_me_menu = (ImageView) findViewById(R.id.iv_me_menu);

		iv_square_menu.setImageResource(R.drawable.square_icon_selected);

		selectCommunity.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				showPopWindow(MainActivity.this, mBackground);
			}
		});

		rl_square_menu.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mMainMode.show(mMainMode.mSquareFragment);
				iv_square_menu
						.setImageResource(R.drawable.square_icon_selected);
				iv_group_menu.setImageResource(R.drawable.group_icon);
				iv_me_menu.setImageResource(R.drawable.person_icon);
			}
		});
		rl_group_menu.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mMainMode.show(mMainMode.mGroupFragment);
				iv_square_menu.setImageResource(R.drawable.square_icon);
				iv_group_menu.setImageResource(R.drawable.group_icon_selected);
				iv_me_menu.setImageResource(R.drawable.person_icon);
			}
		});
		rl_me_menu.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mMainMode.show(mMainMode.mCirclesFragment);
				iv_square_menu.setImageResource(R.drawable.square_icon);
				iv_group_menu.setImageResource(R.drawable.group_icon);
				iv_me_menu.setImageResource(R.drawable.person_icon_selected);
			}
		});
		rl_release_menu.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				System.out.println("release");
				Intent intent = new Intent(MainActivity.this,
						ReleaseActivity.class);
				startActivity(intent);
			}
		});
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		System.out.println("KeyDown");
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

	class LongConnectionReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			switchMode();
		}
	}

	private void showPopWindow(Context context, View parent) {
		final View vPopWindow = inflater.inflate(R.layout.square_dialog, null,
				false);
		List<String> list = new ArrayList<String>();
		list.add("天通苑站");
		list.add("回龙观站");
		list.add("亦庄文化园站");
		// list.add("宋家庄站");
		// list.add("北京西站");
		// list.add("天通苑站");
		// list.add("回龙观站");
		// list.add("亦庄文化园站");
		// list.add("宋家庄站");
		// list.add("北京西站");
		float contentHeight = height * 0.721393f;
		LinearLayout llTop = (LinearLayout) vPopWindow
				.findViewById(R.id.ll_top);
		LinearLayout.LayoutParams topParams = new LinearLayout.LayoutParams(
				llTop.getLayoutParams());
		topParams.height = (int) (contentHeight * 0.0476694915254237f);
		llTop.setLayoutParams(topParams);

		ListView squares = (ListView) vPopWindow.findViewById(R.id.lv_squares);
		LinearLayout.LayoutParams sParams = new LinearLayout.LayoutParams(
				squares.getLayoutParams());
		sParams.height = (int) (contentHeight * 0.7733050847457627f);// //0.578936170212766
		// sParams.height = (int) (height * 0.548936170212766f);
		squares.setLayoutParams(sParams);// 0.7733050847457627
		squares.setAdapter(new MyAdapter(list));

		final PopupWindow popWindow = new PopupWindow(vPopWindow,
				LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT, true);
		RelativeLayout content = (RelativeLayout) vPopWindow
				.findViewById(R.id.rl_content);
		// content.setBackgroundColor(Color.GREEN);
		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
				content.getLayoutParams());
		params.width = (int) (width * 0.82941176f);
		params.addRule(RelativeLayout.CENTER_IN_PARENT);
		params.height = (int) (contentHeight);
		content.setLayoutParams(params);
		LinearLayout layout = (LinearLayout) vPopWindow
				.findViewById(R.id.ll_dialog);
		RelativeLayout.LayoutParams params2 = new RelativeLayout.LayoutParams(
				layout.getLayoutParams());
		params2.width = (int) (width * 0.760588235f);
		params2.addRule(RelativeLayout.CENTER_HORIZONTAL);
		params2.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
		params2.height = (int) (height * 0.70149254f);
		layout.setLayoutParams(params2);

		ImageView btClose = (ImageView) vPopWindow.findViewById(R.id.iv_close);
		RelativeLayout.LayoutParams closeParams = new RelativeLayout.LayoutParams(
				btClose.getLayoutParams());
		closeParams.width = (int) (width * 0.063146f);
		closeParams.height = (int) (width * 0.063146f);
		closeParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
		btClose.setLayoutParams(closeParams);

		LinearLayout searchSquare = (LinearLayout) vPopWindow
				.findViewById(R.id.ll_searchsquare);
		// searchSquare.setBackgroundColor(Color.RED);
		LinearLayout.LayoutParams searchSquareParams = new LinearLayout.LayoutParams(
				btClose.getLayoutParams());
		searchSquareParams.width = (int) (width * 0.6404028436018957f);
		searchSquareParams.height = (int) (height * 0.0520520520520521f);
		searchSquareParams.topMargin = (int) (contentHeight * 0.0637204522096608f);// 0.0677966101694915
		// 0.0478723404255319
		searchSquareParams.gravity = Gravity.CENTER;
		searchSquare.setLayoutParams(searchSquareParams);
		TextView machTv = (TextView) vPopWindow.findViewById(R.id.tv_mach);
		machTv.setTextSize(TypedValue.COMPLEX_UNIT_PX,
				width * 0.0443951165371809f);
		vPopWindow.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				popWindow.dismiss();
			}
		});
		content.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				popWindow.dismiss();
			}
		});
		popWindow.showAtLocation(parent, Gravity.CENTER, 0, 0);
	}

	class MyAdapter extends BaseAdapter {

		List<String> list;

		public MyAdapter(List<String> list) {
			this.list = list;
		}

		@Override
		public int getCount() {
			return list.size();
		}

		@Override
		public Object getItem(int arg0) {
			return list.get(arg0);
		}

		@Override
		public long getItemId(int arg0) {
			return arg0;
		}

		@Override
		public View getView(int arg0, View arg1, ViewGroup arg2) {
			View v = inflater.inflate(R.layout.square_dialog_item, null);
			float textSize = (float) (width * 0.03657817109f);
			TextView tv = (TextView) v.findViewById(R.id.tv_square);
			// tv.setBackgroundColor(Color.BLUE);
			tv.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
			tv.setText(list.get(arg0));
			LinearLayout.LayoutParams tvParams = new LinearLayout.LayoutParams(
					tv.getLayoutParams());
			tvParams.width = (int) (width * 0.4872856298048492f);
			tv.setLayoutParams(tvParams);
			ImageView iv = (ImageView) v.findViewById(R.id.iv_square);
			// iv.setBackgroundColor(Color.RED);
			LinearLayout.LayoutParams ivpParams = new LinearLayout.LayoutParams(
					iv.getLayoutParams());
			ivpParams.width = (int) (width * 0.102064897f);
			ivpParams.height = (int) (width * 0.102064897f);
			ivpParams.leftMargin = (int) (width * 0.073900293255132f);
			// ivpParams.leftMargin = (int) (width * 0.046697799f);
			ivpParams.bottomMargin = 5;
			iv.setLayoutParams(ivpParams);

			ImageView ivSelected = (ImageView) v
					.findViewById(R.id.iv_selected_status);
			LinearLayout.LayoutParams selectedParams = new LinearLayout.LayoutParams(
					ivSelected.getLayoutParams());
			selectedParams.width = (int) (width * 0.0372560615020698f);
			selectedParams.height = (int) (height * 0.013013013013013f);
			selectedParams.topMargin = -10;
			selectedParams.gravity = Gravity.CENTER_HORIZONTAL;
			ivSelected.setLayoutParams(selectedParams);
			if (arg0 != 0) {
				ivSelected.setVisibility(View.GONE);
			}
			ImageView line = (ImageView) v.findViewById(R.id.iv_line);
			if (arg0 != getCount() - 1) {
				LinearLayout.LayoutParams linepParams = new LinearLayout.LayoutParams(
						line.getLayoutParams());
				linepParams.width = (int) (width * 0.6410408042578356f);
				linepParams.gravity = Gravity.CENTER_HORIZONTAL;
				line.setLayoutParams(linepParams);
			} else {
				line.setVisibility(View.GONE);
			}
			return v;
		}
	}

}
