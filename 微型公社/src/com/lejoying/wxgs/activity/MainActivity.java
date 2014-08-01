package com.lejoying.wxgs.activity;

import java.util.ArrayList;
import java.util.List;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
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
import android.widget.Toast;

import com.lejoying.wxgs.R;
import com.lejoying.wxgs.activity.mode.LoginModeManager;
import com.lejoying.wxgs.activity.mode.MainModeManager;
import com.lejoying.wxgs.activity.mode.fragment.GroupShareFragment;
import com.lejoying.wxgs.activity.mode.fragment.SquareFragment;
import com.lejoying.wxgs.activity.mode.fragment.SquareOnLineUserFragment;
import com.lejoying.wxgs.activity.utils.DataUtil;
import com.lejoying.wxgs.activity.utils.DataUtil.GetDataListener;
import com.lejoying.wxgs.activity.utils.LocationUtils;
import com.lejoying.wxgs.activity.utils.NotificationUtils;
import com.lejoying.wxgs.activity.view.BackgroundView;
import com.lejoying.wxgs.activity.view.widget.Alert;
import com.lejoying.wxgs.app.MainApplication;
import com.lejoying.wxgs.app.data.Data;
import com.lejoying.wxgs.app.data.entity.Group;
import com.lejoying.wxgs.app.handler.DataHandler.Modification;
import com.lejoying.wxgs.app.handler.OSSFileHandler.FileResult;
import com.lejoying.wxgs.app.service.PushService;

public class MainActivity extends BaseActivity {

	MainApplication app = MainApplication.getMainApplication();
	AudioManager audioManager;

	LayoutInflater inflater;

	public static MainActivity instance;

	public static ChatActivity chatInstance;

	public static final String TAG = "MainActivity";

	GroupsAdapter groupsAdapter;

	BackgroundView mBackground;

	public static int statusBarHeight;

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

	public static List<String> communityList;

	public static TextView communityNameTV;

	// public static String nowGroup;

	View vPopWindow, gPopWindowChild;
	PopupWindow popWindow, gPopWindow;

	int IS_SQUARE = 0x1, IS_GROUPS = 0x2, IS_CIRCLES = 0x3;
	int nowFragment = IS_SQUARE;

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
		mBackground.setBackground(R.drawable.background4);
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
		communityList = new ArrayList<String>();
		communityList.add("亦庄站");
		communityList.add("中关村站");
		communityList.add("天通苑站");
		initMode();

		switchMode();

	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		if ("chatFriend".equals(NotificationUtils.showFragment)) {
			// mMainMode.mChatFragment.mStatus = ChatActivity.CHAT_FRIEND;
			// mMainMode.mChatFragment.mNowChatFriend = app.data.friends
			// .get(NotificationUtils.message.phone);
			// mMainMode.showNext(mMainMode.mChatFragment);
			Intent intent = new Intent(MainActivity.this, ChatActivity.class);
			intent.putExtra("status", ChatActivity.CHAT_FRIEND);
			intent.putExtra("phone", NotificationUtils.message.phone);
			startActivity(intent);
		} else if ("chatGroup".equals(NotificationUtils.showFragment)) {
			Intent intent = new Intent(MainActivity.this, ChatActivity.class);
			intent.putExtra("status", ChatActivity.CHAT_GROUP);
			intent.putExtra("gid", NotificationUtils.message.gid);
			startActivity(intent);

			// mMainMode.mChatGroupFragment.mStatus =
			// ChatFriendFragment.CHAT_GROUP;
			// mMainMode.mChatFragment.mNowChatGroup = app.data.groupsMap
			// .get(NotificationUtils.message.gid);
			// mMainMode.show(mMainMode.mChatGroupFragment);
		} else if ("chatList".equals(NotificationUtils.showFragment)) {
			mMainMode.show(mMainMode.mChatMessagesFragment);
		}
		NotificationUtils.cancelNotification(MainActivity.this);
		statusBarHeight = height - mBackground.getHeight();
		super.onWindowFocusChanged(hasFocus);
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
		// if (mLoginMode == null) {
		// mLoginMode = new LoginModeManager(this);
		// }
	}

	public void switchMode() {
		if (app.data.user.phone.equals("")
				|| app.data.user.accessKey.equals("")) {
			// if (!mode.equals(MODE_LOGIN)) {
			// mode = MODE_LOGIN;
			// mMainMode.release();
			// mLoginMode.initialize();
			// mLoginMode.show(mLoginMode.mLoginUsePassFragment);
			// }

			mMainMode.release();
			mode = MODE_LOGIN;
			Intent intent = new Intent(MainActivity.this, LoginActivity.class);
			intent.putExtra("operation", "ReLogin");
			startActivity(intent);
			finish();
		} else if (!app.data.user.phone.equals("")
				&& !app.data.user.accessKey.equals("")) {
			LocationUtils.updateLocation();
			if (!mode.equals(MODE_MAIN)) {
				mode = MODE_MAIN;
				mMainMode.initialize();
				initEvent();
				ll_menu_app.setVisibility(View.VISIBLE);
				mMainMode.show(mMainMode.mSquareFragment);
				PushService.startIMLongPull(this);

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
		communityNameTV = (TextView) findViewById(R.id.iv_release_menu1);
		RelativeLayout rl_square_menu = (RelativeLayout) findViewById(R.id.rl_square_menu);
		RelativeLayout rl_group_menu = (RelativeLayout) findViewById(R.id.rl_group_menu);
		RelativeLayout rl_me_menu = (RelativeLayout) findViewById(R.id.rl_me_menu);
		RelativeLayout rl_release_menu = (RelativeLayout) findViewById(R.id.rl_release_menu);

		final ImageView iv_release_menu = (ImageView) findViewById(R.id.iv_release_menu);
		final ImageView iv_square_menu = (ImageView) findViewById(R.id.iv_square_menu);
		final ImageView iv_group_menu = (ImageView) findViewById(R.id.iv_group_menu);
		final ImageView iv_me_menu = (ImageView) findViewById(R.id.iv_me_menu);

		iv_square_menu.setImageResource(R.drawable.square_icon_selected);
		iv_group_menu.setImageResource(R.drawable.group_icon);
		iv_me_menu.setImageResource(R.drawable.person_icon);

		selectCommunity.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (nowFragment == IS_GROUPS) {
					showGroupPopWindow(MainActivity.this, mBackground);
				} else if (nowFragment == IS_SQUARE) {
					showPopWindow(MainActivity.this, mBackground);
				}
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
				if (nowFragment == IS_GROUPS) {
					iv_release_menu.setImageResource(R.drawable.square_release);
				}
				if ("98".equals(SquareFragment.mCurrentSquareID)) {
					communityNameTV.setText(communityList.get(0));
				} else if ("99".equals(SquareFragment.mCurrentSquareID)) {
					communityNameTV.setText(communityList.get(1));
				} else if ("100".equals(SquareFragment.mCurrentSquareID)) {
					communityNameTV.setText(communityList.get(2));
				}
				nowFragment = IS_SQUARE;
				mMainMode.mCurrentMenuSelected = mMainMode.MSQUARE;
			}
		});
		rl_group_menu.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mMainMode.show(mMainMode.mGroupShareFragment);
				iv_square_menu.setImageResource(R.drawable.square_icon);
				iv_group_menu.setImageResource(R.drawable.group_icon_selected);
				iv_me_menu.setImageResource(R.drawable.person_icon);
				iv_release_menu.setImageResource(R.drawable.gshare_group);
				initGroupShare();
				nowFragment = IS_GROUPS;
				mMainMode.mCurrentMenuSelected = mMainMode.MGROUPSHARE;
			}
		});
		rl_me_menu.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (mMainMode.mCurrentMyFragment == mMainMode.FRAGMENT_CIRCLE) {
					mMainMode.show(mMainMode.mCirclesFragment);
				} else if (mMainMode.mCurrentMyFragment == mMainMode.FRAGMENT_CHATMESSAGE) {
					mMainMode.show(mMainMode.mChatMessagesFragment);
				} else if (mMainMode.mCurrentMyFragment == mMainMode.FRAGMENT_MY) {
					mMainMode.show(mMainMode.mMyFragment);
				}
				iv_square_menu.setImageResource(R.drawable.square_icon);
				iv_group_menu.setImageResource(R.drawable.group_icon);
				iv_me_menu.setImageResource(R.drawable.person_icon_selected);
				iv_release_menu.setImageResource(R.drawable.scanner);
				communityNameTV.setText(app.data.user.nickName);
				nowFragment = IS_CIRCLES;
				mMainMode.mCurrentMenuSelected = mMainMode.MCIRCLES;
			}
		});
		rl_release_menu.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (nowFragment == IS_GROUPS) {
					mMainMode.mCurrentMenuSelected = mMainMode.MGROUPS;
					mMainMode.show(mMainMode.mGroupFragment);
				} else if (nowFragment == IS_SQUARE) {
					Intent intent = new Intent(MainActivity.this,
							ReleaseActivity.class);
					startActivity(intent);
				} else if (nowFragment == IS_CIRCLES) {
					 mMainMode.showNext(mMainMode.mScanQRCodeFragment);
					// Intent intent = new Intent(MainActivity.this,
					// ScanQRCodeActivity.class);
					// startActivity(intent);
				}
			}
		});
	}

	public void initGroupShare() {
		if ("".equals(app.data.currentGroup) || app.data.currentGroup == null) {
			if (app.data.groups.size() > 0) {
				app.dataHandler.exclude(new Modification() {

					@Override
					public void modifyData(Data data) {
						data.currentGroup = app.data.groups.get(0);

					}
				});
				String groupName = app.data.groupsMap.get(app.data.groups
						.get(0)).name;
				communityNameTV.setText(groupName.substring(0,
						groupName.length() > 8 ? 8 : groupName.length()));
			}
		} else {
			if (app.data.groups.size() > 0) {
				if (app.data.groupsMap.get(app.data.currentGroup) != null) {
					String groupName = app.data.groupsMap
							.get(app.data.currentGroup).name;
					communityNameTV.setText(app.data.groupsMap
							.get(app.data.currentGroup).name.substring(0,
							groupName.length() > 8 ? 8 : groupName.length()));
				} else {
					app.dataHandler.exclude(new Modification() {

						@Override
						public void modifyData(Data data) {
							data.currentGroup = app.data.groups.get(0);

						}
					});
					String groupName = app.data.groupsMap.get(app.data.groups
							.get(0)).name;
					communityNameTV.setText(groupName.substring(0,
							groupName.length() > 8 ? 8 : groupName.length()));
				}
			}
		}
	}

	boolean isExit;

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (mode.equals(MODE_LOGIN)) {
			return mLoginMode.onKeyDown(keyCode, event)
					&& super.onKeyDown(keyCode, event);
		} else if (mode.equals(MODE_MAIN)) {
			isExit = mMainMode.onKeyDown(keyCode, event)
					&& super.onKeyDown(keyCode, event);
			if (mMainMode.mCurrentMenuSelected == mMainMode.MGROUPS) {
				isExit = false;
				mMainMode.mCurrentMenuSelected = mMainMode.MGROUPSHARE;
				mMainMode.show(mMainMode.mGroupShareFragment);
			}
			return isExit;
		}
		return super.onKeyDown(keyCode, event);

	}

	long firstTime = 0;

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && isExit) {
			long secondTime = System.currentTimeMillis();
			if (secondTime - firstTime > 1000) {
				Toast.makeText(MainActivity.this, "再按一次退出程序",
						Toast.LENGTH_SHORT).show();
				firstTime = secondTime;
				return true;
			} else {
				DataUtil.saveData(this);
				System.exit(0);
			}
		}
		return super.onKeyUp(keyCode, event);
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

	@SuppressWarnings("deprecation")
	private void showGroupPopWindow(Context context, View parent) {
		gPopWindowChild = inflater.inflate(R.layout.group_dialog, null, false);
		gPopWindow = new PopupWindow(gPopWindowChild,
				LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT, true);
		gPopWindow.setBackgroundDrawable(new BitmapDrawable());
		RelativeLayout rl_content = (RelativeLayout) gPopWindowChild
				.findViewById(R.id.rl_content);
		ListView groups = (ListView) gPopWindowChild
				.findViewById(R.id.lv_groups);

		LayoutParams rl_params = (LayoutParams) rl_content.getLayoutParams();
		rl_params.height = (int) (height * 0.7578125f);
		rl_params.leftMargin = (int) (20 / density + 0.5f);
		rl_params.rightMargin = (int) (20 / density + 0.5f);
		rl_content.setLayoutParams(rl_params);
		groupsAdapter = new GroupsAdapter(app.data.groups);
		groups.setAdapter(groupsAdapter);

		gPopWindowChild.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				gPopWindow.dismiss();
			}
		});
		gPopWindow.showAtLocation(parent, Gravity.CENTER, 0, 0);
	}

	private void showPopWindow(Context context, View parent) {
		vPopWindow = inflater.inflate(R.layout.square_dialog, null, false);
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
		sParams.height = (int) (contentHeight * 0.7733050847457627f);
		squares.setLayoutParams(sParams);
		squares.setAdapter(new SquaresAdapter(communityList));

		popWindow = new PopupWindow(vPopWindow, LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT, true);
		RelativeLayout content = (RelativeLayout) vPopWindow
				.findViewById(R.id.rl_content);
		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
				content.getLayoutParams());
		params.width = (int) (width * 0.82941176f);
		params.topMargin = (int) (((int) (height - contentHeight) / 2) - (height - mBackground
				.getHeight()) * 1.5);
		params.addRule(RelativeLayout.CENTER_HORIZONTAL);
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
		LinearLayout.LayoutParams searchSquareParams = new LinearLayout.LayoutParams(
				btClose.getLayoutParams());
		searchSquareParams.width = (int) (width * 0.6404028436018957f);
		searchSquareParams.height = (int) (height * 0.0520520520520521f);
		searchSquareParams.topMargin = (int) (contentHeight * 0.0447204522096608f);
		searchSquareParams.gravity = Gravity.CENTER;
		searchSquare.setLayoutParams(searchSquareParams);
		TextView machTv = (TextView) vPopWindow.findViewById(R.id.tv_mach);
		machTv.setTextSize(TypedValue.COMPLEX_UNIT_PX,
				width * 0.0443951165371809f);
		searchSquare.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Alert.showMessage("更多社区站尚未开放，敬请期待！");
			}
		});
		btClose.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				popWindow.dismiss();
			}
		});
		vPopWindow.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				popWindow.dismiss();
			}
		});
		content.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// popWindow.dismiss();
			}
		});
		popWindow.showAtLocation(parent, Gravity.CENTER, 0, 0);
	}

	class GroupsAdapter extends BaseAdapter {

		List<String> list = new ArrayList<String>();

		public GroupsAdapter(List<String> list) {
			this.list = list;
		}

		@Override
		public int getCount() {
			return list.size();
		}

		@Override
		public Object getItem(int position) {
			return list.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(final int position, View convertView,
				ViewGroup parent) {
			final GroupsHolder groupsHolder;
			final Group group = app.data.groupsMap.get(list.get(position));
			if (convertView == null) {
				groupsHolder = new GroupsHolder();
				convertView = inflater
						.inflate(R.layout.group_dialog_item, null);
				groupsHolder.ll_dialog = (LinearLayout) convertView
						.findViewById(R.id.ll_dialog);
				groupsHolder.iv_group = (ImageView) convertView
						.findViewById(R.id.iv_square);
				groupsHolder.iv_mony = (ImageView) convertView
						.findViewById(R.id.iv_mony);
				groupsHolder.iv_selected_status = (ImageView) convertView
						.findViewById(R.id.iv_selected_status);
				groupsHolder.tv_square = (TextView) convertView
						.findViewById(R.id.tv_square);
				convertView.setTag(groupsHolder);
			} else {
				groupsHolder = (GroupsHolder) convertView.getTag();
			}
			groupsHolder.tv_square.setText(group.name);
			app.fileHandler.getHeadImage(group.icon, "男", new FileResult() {
				@Override
				public void onResult(String where, Bitmap bitmap) {
					groupsHolder.iv_group
							.setImageBitmap(app.fileHandler.bitmaps
									.get(group.icon));
				}
			});
			if (GroupShareFragment.mCurrentGroupShareID.equals(list
					.get(position))) {
				groupsHolder.iv_selected_status.setVisibility(View.VISIBLE);
			} else {
				groupsHolder.iv_selected_status.setVisibility(View.GONE);

			}
			groupsHolder.ll_dialog.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					GroupShareFragment.mCurrentGroupShareID = list
							.get(position);
					communityNameTV.setText(app.data.groupsMap
							.get(GroupShareFragment.mCurrentGroupShareID).name);
					app.dataHandler.exclude(new Modification() {

						@Override
						public void modifyData(Data data) {
							data.currentGroup = GroupShareFragment.mCurrentGroupShareID;
						}

						@Override
						public void modifyUI() {
							mMainMode.mGroupShareFragment.setGroupShare();
						}
					});
					gPopWindow.dismiss();
					notifyDataSetChanged();
				}
			});
			return convertView;
		}

		class GroupsHolder {
			LinearLayout ll_dialog;
			ImageView iv_group, iv_mony, iv_selected_status, iv_line;
			TextView tv_square;
		}
	}

	class SquaresAdapter extends BaseAdapter {

		List<String> list;

		public SquaresAdapter(List<String> list) {
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
		public View getView(final int arg0, View arg1, ViewGroup arg2) {
			int selectId = 0;
			if ("98".equals(SquareFragment.mCurrentSquareID)) {
				selectId = 0;
			} else if ("99".equals(SquareFragment.mCurrentSquareID)) {
				selectId = 1;
			} else if ("100".equals(SquareFragment.mCurrentSquareID)) {
				selectId = 2;
			}
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
			final ImageView iv = (ImageView) v.findViewById(R.id.iv_square);
			// TODO modify square headimage
			app.fileHandler.getHeadImage(app.data.user.head, app.data.user.sex,
					new FileResult() {

						@Override
						public void onResult(String where, Bitmap bitmap) {
							iv.setImageBitmap(app.fileHandler.bitmaps
									.get(app.data.user.head));
						}
					});
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
			selectedParams.width = (int) (width * 0.0392560615020698f);
			selectedParams.height = (int) (height * 0.019013013013013f);
			selectedParams.topMargin = -15;
			selectedParams.gravity = Gravity.CENTER_HORIZONTAL;
			// ivSelected.setLayoutParams(selectedParams);
			if (arg0 != selectId) {
				ivSelected.setVisibility(View.GONE);
			}
			ImageView line = (ImageView) v.findViewById(R.id.iv_line);
			// if (arg0 != getCount() - 1) {
			LinearLayout.LayoutParams linepParams = new LinearLayout.LayoutParams(
					line.getLayoutParams());
			linepParams.width = (int) (width * 0.6410408042578356f);
			linepParams.gravity = Gravity.CENTER_HORIZONTAL;
			line.setLayoutParams(linepParams);
			// } else {
			// line.setVisibility(View.GONE);
			// }

			ImageView ivMony = (ImageView) v.findViewById(R.id.iv_mony);
			ivMony.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// Intent intent = new Intent(MainActivity.this,
					// SquareOnLineUserActivity.class);
					String mSquareId = "98";
					if (arg0 == 0) {
						mSquareId = "98";
					} else if (arg0 == 1) {
						mSquareId = "99";
					} else {
						mSquareId = "100";
					}
					// intent.putExtra("mSquareID", mSquareId);
					// startActivity(intent);
					SquareOnLineUserFragment.mSquareID = mSquareId;
					mMainMode.showNext(mMainMode.mSquareOnLineUserFragment);
					popWindow.dismiss();
				}
			});

			v.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					app.data.isChanged = true;
					if (arg0 == 0
							&& !"98".equals(SquareFragment.mCurrentSquareID)) {
						SquareFragment.mCurrentSquareID = "98";
						communityNameTV.setText(list.get(arg0));
						String flag = app.data.squareFlags
								.get(SquareFragment.mCurrentSquareID);
						flag = flag == null ? "0" : flag;
						PushService.startSquareLongPull(app,
								SquareFragment.mCurrentSquareID, flag);
						MainActivity.instance.mMainMode.mSquareFragment
								.changeSquareData();
					} else if (arg0 == 1
							&& !"99".equals(SquareFragment.mCurrentSquareID)) {
						SquareFragment.mCurrentSquareID = "99";
						communityNameTV.setText(list.get(arg0));
						String flag = app.data.squareFlags
								.get(SquareFragment.mCurrentSquareID);
						flag = flag == null ? "0" : flag;
						PushService.startSquareLongPull(app,
								SquareFragment.mCurrentSquareID, flag);
						MainActivity.instance.mMainMode.mSquareFragment
								.changeSquareData();
					} else if (arg0 == 2
							&& !"100".equals(SquareFragment.mCurrentSquareID)) {
						SquareFragment.mCurrentSquareID = "100";
						communityNameTV.setText(list.get(arg0));
						String flag = app.data.squareFlags
								.get(SquareFragment.mCurrentSquareID);
						flag = flag == null ? "0" : flag;
						PushService.startSquareLongPull(app,
								SquareFragment.mCurrentSquareID, flag);
						MainActivity.instance.mMainMode.mSquareFragment
								.changeSquareData();
					}
					popWindow.dismiss();
				}
			});
			return v;
		}
	}

}
