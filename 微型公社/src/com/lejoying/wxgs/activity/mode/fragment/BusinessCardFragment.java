package com.lejoying.wxgs.activity.mode.fragment;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.lejoying.wxgs.R;
import com.lejoying.wxgs.activity.MapStorageDirectoryActivity;
import com.lejoying.wxgs.activity.mode.MainModeManager;
import com.lejoying.wxgs.activity.utils.CommonNetConnection;
import com.lejoying.wxgs.activity.utils.MCImageUtils;
import com.lejoying.wxgs.activity.view.widget.Alert;
import com.lejoying.wxgs.activity.view.widget.Alert.AlertInputDialog;
import com.lejoying.wxgs.activity.view.widget.Alert.AlertInputDialog.OnDialogClickListener;
import com.lejoying.wxgs.app.MainApplication;
import com.lejoying.wxgs.app.adapter.FriendGroupsGridViewAdapter;
import com.lejoying.wxgs.app.data.API;
import com.lejoying.wxgs.app.data.Data;
import com.lejoying.wxgs.app.data.entity.Friend;
import com.lejoying.wxgs.app.data.entity.Group;
import com.lejoying.wxgs.app.data.entity.User;
import com.lejoying.wxgs.app.handler.OSSFileHandler;
import com.lejoying.wxgs.app.handler.DataHandler.Modification;
import com.lejoying.wxgs.app.handler.NetworkHandler.NetConnection;
import com.lejoying.wxgs.app.handler.NetworkHandler.Settings;
import com.lejoying.wxgs.app.handler.OSSFileHandler.FileMessageInfoInterface;
import com.lejoying.wxgs.app.handler.OSSFileHandler.FileMessageInfoSettings;
import com.lejoying.wxgs.app.handler.OSSFileHandler.FileResult;
import com.lejoying.wxgs.app.handler.OSSFileHandler.ImageMessageInfo;
import com.lejoying.wxgs.app.handler.OSSFileHandler.SaveBitmapInterface;
import com.lejoying.wxgs.app.handler.OSSFileHandler.SaveSettings;
import com.lejoying.wxgs.app.handler.OSSFileHandler.UploadFileInterface;
import com.lejoying.wxgs.app.handler.OSSFileHandler.UploadFileSettings;
import com.lejoying.wxgs.app.parser.JSONParser;
import com.lejoying.wxgs.app.parser.JSONParser.GroupsAndFriends;
import com.lejoying.wxgs.app.service.PushService;

public class BusinessCardFragment extends BaseFragment {

	public int mStatus;
	public Friend mShowFriend;
	int RESULT_SELECTPICTURE = 0x34;
	int RESULT_TAKEPICTURE = 0x54;
	int RESULT_CATPICTURE = 0x23;
	public static final int SHOW_SELF = 1;
	public static final int SHOW_FRIEND = 2;
	public static final int SHOW_TEMPFRIEND = 3;

	MainApplication app = MainApplication.getMainApplication();
	MainModeManager mMainModeManager;
	private static final int SCROLL = 0x51;

	View mContent;
	View tv_group;
	View tv_square;
	View tv_msg;
	View rl_bighead;
	LayoutInflater mInflater;

	private TextView tv_spacing;
	private TextView tv_spacing2;
	private TextView tv_mainbusiness;
	private RelativeLayout rl_show;
	private ScrollView sv_content;
	private GridView gridView;

	RelativeLayout rl_business_back;
	ImageView iv_me_back;
	TextView tv_back_show;
	TextView tv_alias;
	TextView tv_alias_title;
	View backView;
	ImageView QRcodeImage;

	// DEFINITION object
	private Handler handler;
	private boolean stopSend;
	private int width, height;

	FriendGroupsGridViewAdapter adapter = null;
	File tempFile;
	String backgroudFileName;
	String headname;

	String USERCARDTYPE = "usercard";

	public void setMode(MainModeManager mainMode) {
		mMainModeManager = mainMode;
	}

	@SuppressLint("HandlerLeak")
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		mInflater = inflater;
		mContent = inflater.inflate(R.layout.f_businesscard, null);
		mMainModeManager.handleMenu(false);
		handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				int what = msg.what;
				switch (what) {
				case SCROLL:
					if (sv_content.getScrollY() > 10) {
						tv_mainbusiness.setMaxLines(100);
					}
					if (sv_content.getScrollY() < 10) {
						tv_mainbusiness.setMaxLines(3);
					}
					break;
				}
				super.handleMessage(msg);
			}
		};

		tv_spacing = (TextView) mContent.findViewById(R.id.tv_spacing);
		tv_spacing2 = (TextView) mContent.findViewById(R.id.tv_spacing2);
		// tv_mainbusiness = (TextView) mContent
		// .findViewById(R.id.tv_mainbusiness);
		// rl_show = (RelativeLayout) mContent.findViewById(R.id.rl_show);
		QRcodeImage = (ImageView) mContent.findViewById(R.id.iv_tdcode);

		initData();

		AsyncTask<Integer, Integer, Boolean> asyncTask = new AsyncTask<Integer, Integer, Boolean>() {

			@Override
			protected Boolean doInBackground(Integer... params) {
				while (rl_show.getHeight() == 0)
					;
				return true;
			}

			@Override
			protected void onPostExecute(Boolean result) {
				DisplayMetrics dm = new DisplayMetrics();
				getActivity().getWindowManager().getDefaultDisplay()
						.getMetrics(dm);
				height = dm.heightPixels;
				width = dm.widthPixels;
				Rect frame = new Rect();
				getActivity().getWindow().getDecorView()
						.getWindowVisibleDisplayFrame(frame);
				int statusBarHeight = frame.top;
				sv_content = (ScrollView) mContent
						.findViewById(R.id.sv_content);

				tv_spacing.setHeight((int) (dm.heightPixels
						- rl_show.getHeight() - statusBarHeight - tv_spacing2
						.getHeight()));

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
		};
		asyncTask.execute();
		return mContent;
	}

	@Override
	public void onResume() {
		mMainModeManager.handleMenu(false);
		super.onResume();
	}

	public void initData() {
		String cardPhone = "";
		if (mStatus == SHOW_SELF) {
			cardPhone = app.data.user.phone;
		} else {
			cardPhone = mShowFriend.phone;
		}
		QRcodeImage.setImageBitmap(MCImageUtils.createQEcodeImage(USERCARDTYPE,
				cardPhone));
		QRcodeImage.setScaleType(ScaleType.FIT_CENTER);
		final ViewGroup group = (ViewGroup) mContent
				.findViewById(R.id.ll_content);
		tv_group = // mContent.findViewById(R.id.tv_group_layout);
		generateFriendGroup();
		tv_square = mContent.findViewById(R.id.tv_square_layout);
		tv_msg = mContent.findViewById(R.id.tv_msg_layout);
		rl_bighead = mContent.findViewById(R.id.rl_bighead);
		rl_bighead.setVisibility(View.INVISIBLE);
		rl_business_back = (RelativeLayout) mContent
				.findViewById(R.id.rl_business_back);
		iv_me_back = (ImageView) mContent.findViewById(R.id.iv_me_back);
		backView = mContent.findViewById(R.id.backview);
		tv_back_show = (TextView) mContent.findViewById(R.id.tv_back_show);

		final ImageView iv_head = (ImageView) mContent
				.findViewById(R.id.iv_head);
		final TextView tv_bighead = (TextView) mContent
				.findViewById(R.id.tv_bighead);
		TextView tv_squarepanel_name = (TextView) mContent
				.findViewById(R.id.tv_squarepanel_name);
		TextView tv_grouppanel_name = (TextView) mContent
				.findViewById(R.id.tv_grouppanel_name);
		TextView tv_msgpanel_name = (TextView) mContent
				.findViewById(R.id.tv_msgpanel_name);
		TextView tv_nickname = (TextView) mContent
				.findViewById(R.id.tv_nickname);
		TextView tv_phone = (TextView) mContent.findViewById(R.id.tv_business);
		// TextView tv_mainbusiness = (TextView) mContent
		// .findViewById(R.id.tv_mainbusiness);
		TextView tv_id = (TextView) mContent.findViewById(R.id.tv_id);
		TextView tv_sex = (TextView) mContent.findViewById(R.id.tv_sex);
		// tv_alias = (TextView) mContent.findViewById(R.id.tv_alias);
		// tv_alias_title = (TextView)
		// mContent.findViewById(R.id.tv_alias_title);
		Button button1 = (Button) mContent.findViewById(R.id.button1);
		Button button2 = (Button) mContent.findViewById(R.id.button2);
		Button button3 = (Button) mContent.findViewById(R.id.button3);
		String fileName = "", phone = "";

		if (mStatus == SHOW_TEMPFRIEND) {
			tv_back_show.setText("用户资料");
			if (mShowFriend.sex.equals("男")) {
				tv_grouppanel_name.setText("他的群组");
				tv_msgpanel_name.setText("他的广播");

			} else {
				tv_grouppanel_name.setText("她的群组");
				tv_msgpanel_name.setText("她的广播");
			}
			tv_squarepanel_name.setText("常驻广场");
			tv_id.setText(String.valueOf(mShowFriend.id));
			tv_sex.setText(mShowFriend.sex);
			tv_nickname.setText(mShowFriend.nickName);
			tv_alias.setVisibility(View.GONE);
			tv_alias_title.setVisibility(View.GONE);
			if (mShowFriend.phone.length() == 11) {
				phone = mShowFriend.phone.substring(0, 3) + "****"
						+ mShowFriend.phone.substring(7);
			} else {
				phone = mShowFriend.phone;
			}

			tv_phone.setText(phone);
			fileName = mShowFriend.head;
			tv_mainbusiness.setText(mShowFriend.mainBusiness);
			group.removeView(button3);
			button1.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					mMainModeManager.mAddFriendFragment.mAddFriend = mShowFriend;
					mMainModeManager
							.showNext(mMainModeManager.mAddFriendFragment);
				}
			});
			button2.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					//

				}
			});
		} else if (mStatus == SHOW_SELF) {
			tv_back_show.setText("个人资料");
			tv_alias.setVisibility(View.GONE);
			tv_alias_title.setVisibility(View.GONE);
			button1.setText("修改个人信息");
			button2.setText("退出登录");
			tv_id.setText(String.valueOf(app.data.user.id));
			tv_sex.setText(app.data.user.sex);
			tv_nickname.setText(app.data.user.nickName);
			fileName = app.data.user.head;
			tv_phone.setText(app.data.user.phone);
			tv_mainbusiness.setText(app.data.user.mainBusiness);
			group.removeView(button3);
			group.removeView(tv_group);
			group.removeView(tv_square);

			tv_spacing.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					Alert.createDialog(getActivity())
							.setTitle("更换封面")
							.setLeftButtonText("拍照")
							.setRightButtonText("相册")
							.setOnConfirmClickListener(
									new OnDialogClickListener() {

										@Override
										public void onClick(
												AlertInputDialog dialog) {
											tempFile = new File(
													app.sdcardImageFolder,
													"userBackground");
											int i = 1;
											while (tempFile.exists()) {
												tempFile = new File(
														app.sdcardImageFolder,
														"userBackground"
																+ (i++));
											}
											Uri uri = Uri.fromFile(tempFile);
											Intent tackPicture = new Intent(
													MediaStore.ACTION_IMAGE_CAPTURE);
											tackPicture
													.putExtra(
															MediaStore.Images.Media.ORIENTATION,
															0);
											tackPicture.putExtra(
													MediaStore.EXTRA_OUTPUT,
													uri);
											startActivityForResult(tackPicture,
													RESULT_TAKEPICTURE);
										}
									})
							.setOnCancelClickListener(
									new OnDialogClickListener() {

										@Override
										public void onClick(
												AlertInputDialog dialog) {
											Intent selectFromGallery = new Intent(
													getActivity(),
													MapStorageDirectoryActivity.class);
											MapStorageDirectoryActivity.max = 1;
											startActivityForResult(
													selectFromGallery,
													RESULT_SELECTPICTURE);
										}
									}).show();
				}
			});
			button1.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					// mMainModeManager.showNext(mMainModeManager.mModifyFragment);
				}
			});

			button2.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					Alert.createDialog(getActivity())
							.setTitle("退出登录后您将接收不到任何消息，确定要退出登录吗？")
							.setOnConfirmClickListener(
									new AlertInputDialog.OnDialogClickListener() {
										@Override
										public void onClick(
												AlertInputDialog dialog) {
											Intent service = new Intent(
													getActivity(),
													PushService.class);
											service.putExtra("operation",
													"stop");
											getActivity().startService(service);
										}
									}).show();
				}
			});

		} else if (mStatus == SHOW_FRIEND) {
			tv_back_show.setText("好友资料");
			if (mShowFriend.alias == null || mShowFriend.alias.equals("")) {
				tv_alias.setVisibility(View.GONE);
				tv_alias_title.setVisibility(View.GONE);
			} else {
				tv_alias.setText(mShowFriend.alias);
			}
			if (mShowFriend.sex.equals("男")) {
				tv_grouppanel_name.setText("他的群组");
				tv_msgpanel_name.setText("他的广播");
			} else {
				tv_grouppanel_name.setText("她的群组");
				tv_msgpanel_name.setText("她的广播");
			}
			tv_squarepanel_name.setText("常驻广场");
			button1.setText("发起聊天");
			button2.setText("修改备注");
			button3.setText("解除好友关系");
			tv_id.setText(String.valueOf(mShowFriend.id));
			tv_sex.setText(mShowFriend.sex);
			tv_nickname.setText(mShowFriend.nickName);
			if (mShowFriend.phone.length() == 11) {
				phone = mShowFriend.phone.substring(0, 3) + "****"
						+ mShowFriend.phone.substring(7);
			} else {
				phone = mShowFriend.phone;
			}

			tv_phone.setText(phone);
			fileName = mShowFriend.head;
			tv_mainbusiness.setText(mShowFriend.mainBusiness);
			button1.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					// mMainModeManager.mChatFragment.mStatus =
					// ChatFriendFragment.CHAT_FRIEND;
					// mMainModeManager.mChatFragment.mNowChatFriend =
					// app.data.friends
					// .get(mShowFriend.phone);
					// mMainModeManager.showNext(mMainModeManager.mChatFragment);
				}
			});
			button2.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					final AlertInputDialog alert = new AlertInputDialog(
							getActivity());
					if (mShowFriend.alias != null
							&& !mShowFriend.alias.equals("")) {
						alert.setInputText(mShowFriend.alias);
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
																					.get(mShowFriend.phone).alias = alias;
																			mShowFriend.alias = alias;
																		}

																		@Override
																		public void modifyUI() {
																			if (mShowFriend.alias != null
																					&& !mShowFriend.alias
																							.equals("")) {
																				if (tv_alias
																						.getVisibility() == View.GONE
																						|| tv_alias
																								.getVisibility() == View.INVISIBLE) {
																					tv_alias.setVisibility(View.VISIBLE);
																				}
																				if (tv_alias_title
																						.getVisibility() == View.GONE
																						|| tv_alias_title
																								.getVisibility() == View.INVISIBLE) {
																					tv_alias_title
																							.setVisibility(View.VISIBLE);
																				}
																				tv_alias.setText(mShowFriend.alias);
																			} else {
																				tv_alias.setVisibility(View.GONE);
																				tv_alias_title
																						.setVisibility(View.GONE);
																			}
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
																	mShowFriend.phone);
															params.put("alias",
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
					Alert.createDialog(getActivity())
							.setTitle(
									"确定解除和" + mShowFriend.nickName + "的好友关系吗？")
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
																							+ mShowFriend.phone);
																			data.newFriends
																					.remove(mShowFriend);
																			data.friends
																					.remove(mShowFriend.phone);
																			for (String rid : data.circles) {
																				data.circlesMap
																						.get(rid).phones
																						.remove(mShowFriend);
																			}
																		}

																		@Override
																		public void modifyUI() {
																			if (mMainModeManager.mCirclesFragment
																					.isAdded()) {
																				mMainModeManager.mCirclesFragment
																						.notifyViews();
																			}

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
																			+ mShowFriend.phone
																			+ "\"]");
															settings.params = params;
														}
													});
											mMainModeManager.back();
										}
									}).show();

				}
			});
		}
		if (mStatus == SHOW_SELF) {
			backgroudFileName = app.data.user.userBackground;
			headname = app.data.user.head;
		} else {
			backgroudFileName = mShowFriend.userBackground;
			headname = mShowFriend.head;
		}
		rl_business_back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
			}
		});

		final GestureDetector backViewDetector = new GestureDetector(
				getActivity(), new GestureDetector.SimpleOnGestureListener() {
					@Override
					public boolean onDown(MotionEvent e) {
						// TODO Auto-generated method stub
						return true;
					}

					@Override
					public boolean onSingleTapUp(MotionEvent e) {
						mMainModeManager.back();
						return true;
					}
				});
		backView.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					backView.setBackgroundColor(Color.argb(143, 0, 0, 0));
					break;
				case MotionEvent.ACTION_UP:
					backView.setBackgroundColor(Color.argb(0, 0, 0, 0));
					break;
				}
				backViewDetector.onTouchEvent(event);
				return true;
			}
		});

		iv_me_back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
			}
		});
		rl_bighead.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				rl_bighead.setVisibility(View.GONE);
				group.setVisibility(View.VISIBLE);
				// CircleMenu.showBack();
			}
		});
		iv_head.setOnClickListener(new OnClickListener() {
			@SuppressWarnings("deprecation")
			@Override
			public void onClick(View v) {
				// RelativeLayout.LayoutParams layoutParams = new
				// RelativeLayout.LayoutParams(
				// LayoutParams.MATCH_PARENT, width);
				// layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
				// tv_bighead.setLayoutParams(layoutParams);
				tv_bighead.setBackgroundDrawable(new BitmapDrawable(
						app.fileHandler.bitmaps.get(headname)));
				group.setVisibility(View.GONE);
				rl_bighead.setVisibility(View.VISIBLE);
				// CircleMenu.hide();
			}
		});
		app.fileHandler.getBackgroundImage(backgroudFileName, new FileResult() {
			@SuppressWarnings("deprecation")
			@Override
			public void onResult(String where, Bitmap bitmap) {
				mContent.setBackgroundDrawable(new BitmapDrawable(bitmap));

			}
		});
		final String headFileName = fileName;
		String sex;
		if (mStatus == SHOW_SELF) {
			sex = app.data.user.sex;
		} else {
			sex = mShowFriend.sex;
		}
		app.fileHandler.getHeadImage(headFileName, sex, new FileResult() {
			@Override
			public void onResult(String where, Bitmap bitmap) {
				iv_head.setImageBitmap(app.fileHandler.bitmaps
						.get(headFileName));
			}
		});
		// group.removeView(tv_group);
		group.removeView(tv_square);
		group.removeView(tv_msg);
	}

	View generateFriendGroup() {
		View groupView = mContent.findViewById(R.id.tv_group_layout);
		gridView = (GridView) mContent.findViewById(R.id.tv_gridview);

		if (mStatus != SHOW_SELF) {
			final List<Group> groups = getFriendGroups();
			adapter = new FriendGroupsGridViewAdapter(mInflater, groups, width,
					height);
			gridView.setAdapter(adapter);
			setColumns(gridView, groups);
			gridView.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {
					mMainModeManager.mGroupBusinessCardFragment.mGroup = groups
							.get(position);
					mMainModeManager
							.showNext(mMainModeManager.mGroupBusinessCardFragment);
				}
			});
		}
		return groupView;
	}

	public void setColumns(GridView gridView, List<Group> groups) {
		DisplayMetrics outMetrics = new DisplayMetrics();
		getActivity().getWindowManager().getDefaultDisplay()
				.getMetrics(outMetrics);
		float density = outMetrics.density;
		int itemWidth = (int) (85 * density);
		int spacingWidth = (int) (10 * density);
		int verticalSpacing = (int) (20 * density);

		ViewGroup.LayoutParams params = gridView.getLayoutParams();
		int listSize = getNumColumns(groups.size());
		params.width = itemWidth * listSize + (listSize - 1) * spacingWidth;
		gridView.setNumColumns(listSize);
		gridView.setLayoutParams(params);

		gridView.setStretchMode(GridView.NO_STRETCH);

		gridView.setHorizontalSpacing(spacingWidth);
		gridView.setVerticalSpacing(verticalSpacing);
		gridView.setColumnWidth(itemWidth);

	}

	public int getNumColumns(int groupNum) {
		int listSize;
		if (groupNum <= 3) {
			listSize = groupNum;
		} else if (groupNum > 3 && groupNum < 6) {
			listSize = 3;
		} else {
			if (groupNum % 2 == 0) {
				listSize = groupNum / 2;
			} else {
				listSize = groupNum / 2 + 1;
			}
		}
		return listSize;
	}

	public List<Group> getFriendGroups() {
		final List<Group> friendGroups = new ArrayList<Group>();
		NetConnection netConnection = new CommonNetConnection() {

			@Override
			protected void settings(Settings settings) {
				settings.url = API.DOMAIN + API.GROUP_GETUSERGROUPS;
				Map<String, String> params = new HashMap<String, String>();
				params.put("phone", app.data.user.phone);
				params.put("accessKey", app.data.user.accessKey);
				params.put("target", mShowFriend.phone);
				settings.params = params;
			}

			@Override
			public void success(final JSONObject jData) {

				app.dataHandler.exclude(new Modification() {
					public void modifyData(Data data) {
						try {
							JSONArray jGroups = jData.getJSONArray("groups");
							GroupsAndFriends groupsAndFriends = JSONParser
									.generateGroupsFromJSON(jGroups);
							if (groupsAndFriends.groups != null) {
								friendGroups.addAll(groupsAndFriends.groups);

							}
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}

					@Override
					public void modifyUI() {

						adapter.notifyDataSetChanged();
						setColumns(gridView, friendGroups);
					}
				});

			}
		};
		app.networkHandler.connection(netConnection);
		return friendGroups;
	}

	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == RESULT_SELECTPICTURE
				&& resultCode == Activity.RESULT_OK) {
			Uri selectedImage = Uri.parse("file://"
					+ MapStorageDirectoryActivity.selectedImages.get(0));
			startPhotoZoom(selectedImage);
		} else if (requestCode == RESULT_TAKEPICTURE
				&& resultCode == Activity.RESULT_OK) {
			Uri uri = Uri.fromFile(tempFile);
			startPhotoZoom(uri);
		} else if (requestCode == RESULT_CATPICTURE
				&& resultCode == Activity.RESULT_OK && data != null) {
			if (tempFile != null && tempFile.exists()) {
				tempFile.delete();
			}

			final Bitmap userBackgroud = (Bitmap) data.getExtras().get("data");
			app.fileHandler.saveBitmap(new SaveBitmapInterface() {

				@Override
				public void setParams(SaveSettings settings) {
					settings.source = userBackgroud;
					settings.compressFormat = settings.PNG;
					settings.folder = app.sdcardImageFolder;
				}

				@Override
				public void onSuccess(final String fileName, String base64) {
					if (!fileName.equals(app.data.user.userBackground)) {
						final String path = new File(app.sdcardImageFolder,
								fileName).getAbsolutePath();
						app.fileHandler
								.getFileMessageInfo(new FileMessageInfoInterface() {

									@Override
									public void setParams(
											FileMessageInfoSettings settings) {
										settings.FILE_TYPE = OSSFileHandler.FILE_TYPE_SDSELECTIMAGE;
										settings.path = path;
										settings.fileName = fileName;
									}

									@Override
									public void onSuccess(
											ImageMessageInfo imageMessageInfo) {
										checkImage(imageMessageInfo,
												"image/png", path);
									}
								});

					}
				}
			});
		}

	}

	public void startPhotoZoom(Uri uri) {
		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setDataAndType(uri, "image/*");
		intent.putExtra("crop", "true");
		intent.putExtra("aspectX", width);
		intent.putExtra("aspectY", height);

		intent.putExtra("outputX", width / 2);
		intent.putExtra("outputY", height / 2);
		intent.putExtra("return-data", true);
		startActivityForResult(intent, RESULT_CATPICTURE);
	}

	public void checkImage(final ImageMessageInfo imageMessageInfo,
			final String contentType, final String path) {

		app.networkHandler.connection(new CommonNetConnection() {

			@Override
			protected void settings(Settings settings) {
				settings.url = API.DOMAIN + API.IMAGE_CHECK;
				Map<String, String> params = new HashMap<String, String>();
				params.put("phone", app.data.user.phone);
				params.put("accessKey", app.data.user.accessKey);
				params.put("filename", imageMessageInfo.fileName);
				settings.params = params;
			}

			@Override
			public void success(JSONObject jData) {
				try {
					if (jData.getBoolean("exists")) {
						User user = new User();
						user.userBackground = imageMessageInfo.fileName;
						modify(user);
					} else {
						app.fileHandler.uploadFile(new UploadFileInterface() {

							@Override
							public void setParams(UploadFileSettings settings) {
								settings.imageMessageInfo = imageMessageInfo;
								settings.contentType = contentType;
								settings.fileName = imageMessageInfo.fileName;
								settings.path = path;
								settings.uploadFileType = OSSFileHandler.UPLOAD_FILE_TYPE_BACKGROUNDS;
							}

							@Override
							public void onSuccess(Boolean flag, String fileName) {
								User user = new User();
								user.userBackground = fileName;
								modify(user);
							}
						});
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		});

	}

	public void uploadImage(final String fileName, final String base64) {
		app.networkHandler.connection(new CommonNetConnection() {

			@Override
			protected void settings(Settings settings) {
				settings.url = API.DOMAIN + API.IMAGE_UPLOAD;
				Map<String, String> params = new HashMap<String, String>();
				params.put("phone", app.data.user.phone);
				params.put("accessKey", app.data.user.accessKey);
				params.put("filename", fileName);
				params.put("imagedata", base64);
				settings.params = params;
			}

			@Override
			public void success(JSONObject jData) {
				User user = new User();
				user.userBackground = fileName;
				modify(user);
			}
		});
	}

	public void modify(final User user) {
		JSONObject account = new JSONObject();
		try {
			if (user.userBackground != null && !user.userBackground.equals("")) {
				account.put("userBackground", user.userBackground);
			}

		} catch (JSONException e) {
			e.printStackTrace();
		}
		final Map<String, String> params = new HashMap<String, String>();
		params.put("phone", app.data.user.phone);
		params.put("accessKey", app.data.user.accessKey);
		params.put("account", account.toString());

		app.dataHandler.exclude(new Modification() {

			@Override
			public void modifyData(Data data) {
				if (user.userBackground != null
						&& !user.userBackground.equals("")) {
					data.user.userBackground = user.userBackground;
				}

			}

			@SuppressWarnings("deprecation")
			@Override
			public void modifyUI() {

				if (user.userBackground != null
						&& !user.userBackground.equals("")) {
					final String backgroudFileName = app.data.user.userBackground;
					app.fileHandler.getBackgroundImage(backgroudFileName,
							new FileResult() {
								@Override
								public void onResult(String where, Bitmap bitmap) {
									mContent.setBackgroundDrawable(new BitmapDrawable(
											bitmap));

								}
							});

				}
				if (mMainModeManager.mBusinessCardFragment.isAdded()) {
					mMainModeManager.mBusinessCardFragment.initData();
				}
			}
		});

		app.networkHandler.connection(new CommonNetConnection() {

			@Override
			protected void settings(Settings settings) {
				settings.url = API.DOMAIN + API.ACCOUNT_MODIFY;
				settings.params = params;
			}

			@Override
			public void success(JSONObject jData) {
			}
		});
	}
}
