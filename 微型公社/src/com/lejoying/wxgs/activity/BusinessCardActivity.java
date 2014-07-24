package com.lejoying.wxgs.activity;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.lejoying.wxgs.R;
import com.lejoying.wxgs.activity.mode.fragment.ModifyFragment;
import com.lejoying.wxgs.activity.utils.CommonNetConnection;
import com.lejoying.wxgs.activity.utils.DataUtil;
import com.lejoying.wxgs.activity.utils.DataUtil.GetDataListener;
import com.lejoying.wxgs.activity.utils.MCImageUtils;
import com.lejoying.wxgs.activity.view.widget.Alert;
import com.lejoying.wxgs.activity.view.widget.Alert.AlertInputDialog;
import com.lejoying.wxgs.activity.view.widget.Alert.AlertInputDialog.OnDialogClickListener;
import com.lejoying.wxgs.app.MainApplication;
import com.lejoying.wxgs.app.data.API;
import com.lejoying.wxgs.app.data.Data;
import com.lejoying.wxgs.app.data.entity.Friend;
import com.lejoying.wxgs.app.data.entity.Group;
import com.lejoying.wxgs.app.handler.DataHandler.Modification;
import com.lejoying.wxgs.app.handler.NetworkHandler.Settings;
import com.lejoying.wxgs.app.handler.OSSFileHandler.FileResult;
import com.lejoying.wxgs.app.service.PushService;

public class BusinessCardActivity extends BaseActivity implements
		OnClickListener {

	public static final int TYPE_GROUP = 0x11, TYPE_FRIEND = 0x22,
			TYPE_SELF = 0x33, SCROLL = 0x51, REQUEST_BACK = 0x99;
	public static final int RESULT_SELECTPICTURE = 0x123,
			RESULT_SELECTHEAD = 0xa4, RESULT_TAKEPICTURE = 0xa3,
			RESULT_TAKEHEAD = 0xa5, RESULT_CATPICTURE = 0x3d;

	public static int type;
	public static String gid, frindPhone;
	public Group mGroup;
	public Friend mFriend;

	FragmentManager mFragmentManager;
	ModifyFragment mModifyFragment;

	private TextView tv_spacing;
	private TextView tv_spacing2;
	private ScrollView sv_content;
	ImageView iv_head;
	TextView tv_bighead;
	ViewGroup group;
	View rl_bighead;
	ImageView iv_me_back;
	TextView tv_back_show;
	ImageView QRcodeImage;

	LinearLayout ll_show;
	TextView tv_business;

	private Handler handler;
	LayoutInflater mInflater;
	MainApplication app = MainApplication.getMainApplication();

	private boolean stopSend;
	// private int width, height;
	String GROUPCARDTYPE = "groupcard";
	String USERCARDTYPE = "usercard";

	@SuppressLint("HandlerLeak")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Intent intent = getIntent();
		type = intent.getIntExtra("type", TYPE_FRIEND);
		if (type == TYPE_GROUP) {
			gid = intent.getStringExtra("gid");
			if (gid != null && !"".equals(gid)) {
				mGroup = app.data.groupsMap.get(gid);
			}
		} else if (type == TYPE_FRIEND) {
			frindPhone = intent.getStringExtra("phone");
		}
		setContentView(R.layout.f_businesscard);
		mInflater = getLayoutInflater();
		getWindow().setBackgroundDrawableResource(R.drawable.background4);
		mFragmentManager = this.getSupportFragmentManager();
		mModifyFragment = new ModifyFragment();
		handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				int what = msg.what;
				switch (what) {
				case SCROLL:
					if (sv_content.getScrollY() > 10) {
						tv_business.setMaxLines(100);
					}
					if (sv_content.getScrollY() < 10) {
						tv_business.setMaxLines(3);
					}
					break;
				}
				super.handleMessage(msg);
			}
		};

		AsyncTask<Integer, Integer, Boolean> asyncTask = new AsyncTask<Integer, Integer, Boolean>() {

			@Override
			protected Boolean doInBackground(Integer... params) {
				while (ll_show.getHeight() == 0)
					;
				return true;
			}

			@Override
			protected void onPostExecute(Boolean result) {
				DisplayMetrics dm = new DisplayMetrics();
				BusinessCardActivity.this.getWindowManager()
						.getDefaultDisplay().getMetrics(dm);
				// height = dm.heightPixels;
				// width = dm.widthPixels;
				Rect frame = new Rect();
				BusinessCardActivity.this.getWindow().getDecorView()
						.getWindowVisibleDisplayFrame(frame);
				int statusBarHeight = frame.top;

				tv_spacing.setHeight((int) (dm.heightPixels
						- ll_show.getHeight() - statusBarHeight - tv_spacing2
						.getHeight()));

			}
		};
		initData();
		initEvent();
		asyncTask.execute();
	}

	@Override
	public void onBackPressed() {
		mFinish();
	}

	private void initEvent() {
		iv_head.setOnClickListener(this);
		rl_bighead.setOnClickListener(this);
		iv_me_back.setOnClickListener(this);
		tv_spacing.setOnClickListener(this);

		sv_content.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				stopSend = true;
				new Thread() {
					@Override
					public void run() {
						while (stopSend) {
							handler.sendEmptyMessage(SCROLL);
							int start = sv_content.getScrollY();
							try {
								Thread.sleep(100);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
							int stop = sv_content.getScrollY();
							if (start == stop) {
								stopSend = false;
							}
						}
						super.run();
					}

				}.start();
				return false;
			}
		});
	}

	public void initData() {
		iv_me_back = (ImageView) findViewById(R.id.iv_me_back);
		tv_back_show = (TextView) findViewById(R.id.tv_back_show);
		QRcodeImage = (ImageView) findViewById(R.id.iv_tdcode);
		tv_spacing = (TextView) findViewById(R.id.tv_spacing);
		tv_spacing2 = (TextView) findViewById(R.id.tv_spacing2);

		tv_business = (TextView) findViewById(R.id.tv_business);
		ll_show = (LinearLayout) findViewById(R.id.ll_show);

		sv_content = (ScrollView) findViewById(R.id.sv_content);

		group = (ViewGroup) findViewById(R.id.ll_content);
		View tv_group = findViewById(R.id.tv_group_layout);
		View tv_square = findViewById(R.id.tv_square_layout);
		View tv_msg = findViewById(R.id.tv_msg_layout);
		rl_bighead = findViewById(R.id.rl_bighead);

		iv_head = (ImageView) findViewById(R.id.iv_head);
		tv_bighead = (TextView) findViewById(R.id.tv_bighead);
		TextView tv_nickname = (TextView) findViewById(R.id.tv_nickname);
		TextView tv_id = (TextView) findViewById(R.id.tv_id);
		TextView tv_business = (TextView) findViewById(R.id.tv_business);
		TextView tv_createTime = (TextView) findViewById(R.id.tv_creattime);
		TextView tv_lable = (TextView) findViewById(R.id.tv_lable);

		TextView tv_business_title = (TextView) findViewById(R.id.tv_business_title);
		TextView tv_lable_title = (TextView) findViewById(R.id.tv_lable_title);
		TextView tv_creattime_title = (TextView) findViewById(R.id.tv_creattime_title);

		Button button1 = (Button) findViewById(R.id.button1);
		Button button2 = (Button) findViewById(R.id.button2);
		Button button3 = (Button) findViewById(R.id.button3);

		switch (type) {
		case TYPE_GROUP:
			QRcodeImage.setImageBitmap(MCImageUtils.createQEcodeImage(
					GROUPCARDTYPE, mGroup.gid + ""));
			QRcodeImage.setScaleType(ScaleType.FIT_CENTER);

			tv_back_show.setText("群组资料");
			rl_bighead.setVisibility(View.GONE);
			tv_business_title.setText("主要业务:");
			tv_lable_title.setText("标签:");

			group.removeView(tv_group);
			group.removeView(tv_square);
			group.removeView(tv_msg);

			if (app.data.groupsMap.get(String.valueOf(mGroup.gid)) == null) {
				group.removeView(button2);
				group.removeView(button3);
				button1.setText("加入群组");
				button1.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						addMembersToGroup();
					}
				});
			} else {
				button1.setText("发起聊天");
				button1.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						Intent intent = new Intent(BusinessCardActivity.this,
								ChatActivity.class);
						intent.putExtra("status", ChatActivity.CHAT_GROUP);
						intent.putExtra("gid", mGroup.gid + "");
						startActivity(intent);
					}
				});
				button2.setText("修改群名片");
				button2.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						FragmentTransaction transaction = mFragmentManager
								.beginTransaction();
						transaction.setCustomAnimations(R.anim.translate_new,
								R.anim.translate_out);
						if (mModifyFragment.isAdded()) {
							transaction.show(mModifyFragment);
						} else {
							transaction.replace(R.id.fl_fragment,
									mModifyFragment);
							transaction.addToBackStack(null);
						}
						transaction.commit();
					}
				});
				button3.setText("推荐给好友");
				button3.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub

					}
				});
			}
			tv_nickname.setText(mGroup.name);
			tv_id.setText(String.valueOf(mGroup.gid));
			if (mGroup.description == null || mGroup.description.equals("")
					|| mGroup.description.equals("请输入群组描述信息")) {
				tv_business.setText("此群组暂无业务");
			} else {
				tv_business.setText(mGroup.description);
			}
			final String headFileName = mGroup.icon;
			app.fileHandler.getHeadImage(headFileName, "男", new FileResult() {
				@Override
				public void onResult(String where, Bitmap bitmap) {
					iv_head.setImageBitmap(app.fileHandler.bitmaps
							.get(headFileName));
				}
			});
			break;
		case TYPE_FRIEND:
			if (app.data.friends.get(frindPhone) != null) {
				mFriend = app.data.friends.get(frindPhone);

				group.removeView(tv_group);
				group.removeView(tv_square);
				group.removeView(tv_msg);

				QRcodeImage.setImageBitmap(MCImageUtils.createQEcodeImage(
						USERCARDTYPE, mFriend.phone + ""));
				QRcodeImage.setScaleType(ScaleType.FIT_CENTER);

				tv_back_show.setText("好友资料");

				button1.setText("发起聊天");
				button2.setText("修改备注");
				button3.setText("解除好友关系");

				tv_business.setText(mFriend.mainBusiness);
				tv_id.setText(String.valueOf(mFriend.id));
				app.fileHandler.getHeadImage(mFriend.head, mFriend.sex,
						new FileResult() {
							@Override
							public void onResult(String where, Bitmap bitmap) {
								iv_head.setImageBitmap(bitmap);
							}
						});
				button1.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						Intent intent = new Intent(BusinessCardActivity.this,
								ChatActivity.class);
						intent.putExtra("status", ChatActivity.CHAT_FRIEND);
						intent.putExtra("phone", mFriend.phone);
						startActivity(intent);
					}
				});
				button2.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						final AlertInputDialog alert = new AlertInputDialog(
								BusinessCardActivity.this);
						if (mFriend.alias != null && !mFriend.alias.equals("")) {
							alert.setInputText(mFriend.alias);
						}
						alert.setTitle("请输入好友的备注")
								.setLeftButtonText("修改")
								.setOnConfirmClickListener(
										new OnDialogClickListener() {
											@Override
											public void onClick(
													AlertInputDialog dialog) {
												final String alias = alert
														.getInputText();

												app.networkHandler
														.connection(new CommonNetConnection() {

															@Override
															public void success(
																	JSONObject jData) {
																app.dataHandler
																		.exclude(new Modification() {
																			@Override
																			public void modifyData(
																					Data data) {
																				data.friends
																						.get(mFriend.phone).alias = alias;
																				mFriend.alias = alias;
																			}

																		});
															}

															@Override
															protected void settings(
																	Settings settings) {
																settings.url = API.DOMAIN
																		+ API.RELATION_MODIFYALIAS;
																Map<String, String> params = new HashMap<String, String>();
																params.put(
																		"phone",
																		app.data.user.phone);
																params.put(
																		"accessKey",
																		app.data.user.accessKey);
																params.put(
																		"friend",
																		mFriend.phone);
																params.put(
																		"alias",
																		alias);
																settings.params = params;
															}
														});
											}
										}).show();

					}
				});
				button3.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						Alert.createDialog(BusinessCardActivity.this)
								.setTitle(
										"确定解除和" + mFriend.nickName + "的好友关系吗？")
								.setOnConfirmClickListener(
										new AlertInputDialog.OnDialogClickListener() {

											@Override
											public void onClick(
													AlertInputDialog dialog) {
												app.networkHandler
														.connection(new CommonNetConnection() {

															@Override
															public void success(
																	JSONObject jData) {
																app.dataHandler
																		.exclude(new Modification() {

																			@Override
																			public void modifyData(
																					Data data) {
																				data.lastChatFriends
																						.remove("f"
																								+ mFriend.phone);
																				data.newFriends
																						.remove(mFriend);
																				data.friends
																						.remove(mFriend.phone);
																				for (String rid : data.circles) {
																					data.circlesMap
																							.get(rid).phones
																							.remove(mFriend);
																				}
																			}

																			@Override
																			public void modifyUI() {

																			}
																		});
															}

															@Override
															protected void settings(
																	Settings settings) {
																settings.url = API.DOMAIN
																		+ API.RELATION_DELETEFRIEND;
																Map<String, String> params = new HashMap<String, String>();
																params.put(
																		"phone",
																		app.data.user.phone);
																params.put(
																		"accessKey",
																		app.data.user.accessKey);
																params.put(
																		"phoneto",
																		"[\""
																				+ mFriend.phone
																				+ "\"]");
																settings.params = params;
															}
														});
												// mMainModeManager.back();
												finish();
											}
										}).show();

					}
				});

			} else {

			}
			break;
		case TYPE_SELF:
			QRcodeImage.setImageBitmap(MCImageUtils.createQEcodeImage(
					USERCARDTYPE, app.data.user.phone + ""));
			QRcodeImage.setScaleType(ScaleType.FIT_CENTER);

			group.removeView(button3);
			group.removeView(tv_group);
			group.removeView(tv_square);
			group.removeView(tv_msg);

			tv_business_title.setText("个人宣言:");
			tv_lable_title.setText("爱好:");

			button1.setText("修改我的名片");
			button2.setText("退出登录");
			tv_back_show.setText("个人资料");
			tv_nickname.setText(app.data.user.nickName);
			tv_id.setText(String.valueOf(app.data.user.id));
			tv_business.setText(app.data.user.mainBusiness);

			app.fileHandler.getHeadImage(app.data.user.head, app.data.user.sex,
					new FileResult() {
						@Override
						public void onResult(String where, Bitmap bitmap) {
							iv_head.setImageBitmap(bitmap);
						}
					});
			button1.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {

				}
			});

			button2.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					Alert.createDialog(BusinessCardActivity.this)
							.setTitle("退出登录后您将接收不到任何消息，确定要退出登录吗？")
							.setOnConfirmClickListener(
									new AlertInputDialog.OnDialogClickListener() {
										@Override
										public void onClick(
												AlertInputDialog dialog) {
											Intent service = new Intent(
													BusinessCardActivity.this,
													PushService.class);
											service.putExtra("operation",
													"stop");
											BusinessCardActivity.this
													.startService(service);
											finish();
										}
									}).show();
				}
			});
			break;
		default:
			break;
		}
	}

	@SuppressWarnings("deprecation")
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.rl_bighead:
			rl_bighead.setVisibility(View.GONE);
			group.setVisibility(View.VISIBLE);
			break;
		case R.id.iv_head:
			tv_bighead.setBackgroundDrawable(new BitmapDrawable(
					app.fileHandler.bitmaps.get(mGroup.icon)));
			group.setVisibility(View.GONE);
			rl_bighead.setVisibility(View.VISIBLE);
			break;
		case R.id.iv_me_back:
			mFinish();
			break;
		case R.id.tv_spacing:
			// Intent intent = new Intent(BusinessCardActivity.this,
			// ChatBackGroundSettingActivity.class);
			// startActivityForResult(intent, REQUEST_BACK);
			break;
		default:
			break;
		}
	}

	private void mFinish() {
		if (mModifyFragment.isAdded()) {
			mFragmentManager.popBackStack();
		} else {
			finish();
		}
	}

	public void addMembersToGroup() {
		app.networkHandler.connection(new CommonNetConnection() {
			@Override
			protected void settings(Settings settings) {
				settings.url = API.DOMAIN + API.GROUP_ADDMEMBERS;
				Map<String, String> params = new HashMap<String, String>();
				params.put("phone", app.data.user.phone);
				params.put("accessKey", app.data.user.accessKey);
				params.put("gid", String.valueOf(mGroup.gid));

				params.put("members", "[\"" + app.data.user.phone + "\"]");
				settings.params = params;
			}

			@Override
			public void success(JSONObject jData) {
				app.UIHandler.post(new Runnable() {

					@Override
					public void run() {
						initData();
						DataUtil.getGroups(new GetDataListener() {

							@Override
							public void getSuccess() {
								// if
								// (MainActivity.instance.mMainMode.mChatGroupFragment
								// .isAdded()) {
								// mMainModeManager.mChatGroupFragment.mAdapter
								// .notifyDataSetChanged();
								// mMainModeManager.mGroupFragment
								// .notifyViews();
								// }
							}
						});
					}
				});
			}
		});
	}
}
