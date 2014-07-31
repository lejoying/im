package com.lejoying.wxgs.activity;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import com.lejoying.wxgs.R;
import com.lejoying.wxgs.activity.utils.CommonNetConnection;
import com.lejoying.wxgs.activity.utils.DataUtil;
import com.lejoying.wxgs.activity.utils.DataUtil.GetDataListener;
import com.lejoying.wxgs.activity.view.ScrollContainer;
import com.lejoying.wxgs.activity.view.ScrollContainer.OnPageChangedListener;
import com.lejoying.wxgs.activity.view.ScrollContainer.ViewContainer;
import com.lejoying.wxgs.activity.view.ScrollContainer.onInterceptTouchDownListener;
import com.lejoying.wxgs.activity.view.widget.Alert;
import com.lejoying.wxgs.activity.view.widget.Alert.AlertInputDialog;
import com.lejoying.wxgs.activity.view.widget.Alert.AlertInputDialog.OnDialogClickListener;
import com.lejoying.wxgs.app.MainApplication;
import com.lejoying.wxgs.app.data.API;
import com.lejoying.wxgs.app.data.Data;
import com.lejoying.wxgs.app.data.entity.Friend;
import com.lejoying.wxgs.app.data.entity.Group;
import com.lejoying.wxgs.app.handler.OSSFileHandler;
import com.lejoying.wxgs.app.handler.DataHandler.Modification;
import com.lejoying.wxgs.app.handler.NetworkHandler.Settings;
import com.lejoying.wxgs.app.handler.OSSFileHandler.FileMessageInfoInterface;
import com.lejoying.wxgs.app.handler.OSSFileHandler.FileMessageInfoSettings;
import com.lejoying.wxgs.app.handler.OSSFileHandler.FileResult;
import com.lejoying.wxgs.app.handler.OSSFileHandler.ImageMessageInfo;
import com.lejoying.wxgs.app.handler.OSSFileHandler.UploadFileInterface;
import com.lejoying.wxgs.app.handler.OSSFileHandler.UploadFileSettings;
import com.lejoying.wxgs.app.parser.StreamParser;

public class GroupInformationActivity extends Activity implements
		OnClickListener {

	MainApplication app = MainApplication.getMainApplication();

	ScrollContainer mScrollContainer;
	ViewContainer viewContainer;
	CircleHolder circleHolder;
	LayoutInflater mInflater;

	ScrollView groupInfomationScrollView;

	TextView groupMembersCount;
	TextView panelName;
	RelativeLayout bottomBar;
	TextView buttonPreviousGroup;
	LinearLayout ll_pagepoint;
	LinearLayout groupMembersPanel;

	RelativeLayout groupMemberControlView;
	RelativeLayout groupBusinessView;
	RelativeLayout groupSetBackGroundView;
	RelativeLayout checkChatMessagesView;
	RelativeLayout deletChatMessagesView;
	RelativeLayout exit2DeleteGroupView;

	LinearLayout backView;

	// View mContent;
	int height, width, dip;
	float density;
	int baseLeft;// 26
	int headSpace;// 48
	int head;
	int vWidth;

	Group mCurrentGroupInfomation;

	boolean isInterceptTouchEvent = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		String gid = getIntent().getStringExtra("gid");
		if (gid == null || "".equals(gid)) {
			return;
		}
		mCurrentGroupInfomation = app.data.groupsMap.get(gid);

		mInflater = this.getLayoutInflater();
		setContentView(R.layout.activity_group_information);
		groupInfomationScrollView = (ScrollView) findViewById(R.id.sv_groupInfomation);
		groupMembersCount = (TextView) findViewById(R.id.groupCount);
		panelName = (TextView) findViewById(R.id.panel_name);
		bottomBar = (RelativeLayout) findViewById(R.id.bottomBar);

		buttonPreviousGroup = (TextView) findViewById(R.id.buttonPreviousGroup);

		groupMembersPanel = (LinearLayout) findViewById(R.id.panel_ll);
		ll_pagepoint = (LinearLayout) findViewById(R.id.ll_pagepoint);

		mScrollContainer = (ScrollContainer) findViewById(R.id.viewContainer);
		viewContainer = mScrollContainer.getViewContainer();

		backView = (LinearLayout) findViewById(R.id.ll_backview);
		groupMemberControlView = (RelativeLayout) findViewById(R.id.rl_groupmembercontrol);
		groupBusinessView = (RelativeLayout) findViewById(R.id.rl_groupBusiness);
		groupSetBackGroundView = (RelativeLayout) findViewById(R.id.rl_groupSetBackGround);
		checkChatMessagesView = (RelativeLayout) findViewById(R.id.rl_checkChatMessages);
		deletChatMessagesView = (RelativeLayout) findViewById(R.id.rl_deleteChatMessages);
		exit2DeleteGroupView = (RelativeLayout) findViewById(R.id.rl_exit2DeleteGroup);

		initData();
		initEvent();

		circleHolder = new CircleHolder();
		notifyMembersViews(viewContainer, circleHolder);

	}

	private void initData() {
		panelName.setText("群组成员");
		bottomBar.setVisibility(View.VISIBLE);
		buttonPreviousGroup.setText("");
		groupMembersPanel.setBackgroundResource(R.drawable.lineborder);
		mScrollContainer.setScrollStatus(ScrollContainer.SCROLL_PAGING);
		mScrollContainer
				.setScrollDirection(ScrollContainer.DIRECTION_HORIZONTAL);

		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		density = dm.density;
		dip = (int) (40 * density + 0.5f);
		height = dm.heightPixels;
		width = dm.widthPixels;
		baseLeft = (int) (width - (dp2px(20) * 2) - (dp2px(55) * 4)) / 8;
		vWidth = (int) (width - (dp2px(20) * 2));
		headSpace = baseLeft * 2;
		head = (int) dp2px(55f);
	}

	private void initEvent() {
		groupMemberControlView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(GroupInformationActivity.this,
						GroupMemberManageActivity.class);
				intent.putExtra("gid", mCurrentGroupInfomation.gid + "");
				startActivityForResult(intent, 101);
			}
		});
		groupSetBackGroundView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(GroupInformationActivity.this,
						ChatBackGroundSettingActivity.class);
				startActivityForResult(intent,
						ChatBackGroundSettingActivity.RESULT_SELECTPICTURE);
			}
		});
		checkChatMessagesView.setOnClickListener(this);
		groupBusinessView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(GroupInformationActivity.this,
						BusinessCardActivity.class);
				intent.putExtra("gid", mCurrentGroupInfomation.gid + "");
				intent.putExtra("type", BusinessCardActivity.TYPE_GROUP);
				startActivity(intent);
			}
		});
		exit2DeleteGroupView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Alert.createDialog(GroupInformationActivity.this)
						.setTitle("删除并退出后,将不再接收此群聊消息").setLeftButtonText("否")
						.setRightButtonText("是")
						.setOnCancelClickListener(new OnDialogClickListener() {

							@Override
							public void onClick(AlertInputDialog dialog) {
								exit2DeleteGroupView.setClickable(false);
								exitGroup();
							}
						}).show();
			}
		});
		deletChatMessagesView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Alert.createDialog(GroupInformationActivity.this)
						.setLeftButtonText("否").setRightButtonText("是")
						.setTitle("确认删除该群组的聊天记录吗？")
						.setOnCancelClickListener(new OnDialogClickListener() {

							@Override
							public void onClick(AlertInputDialog dialog) {
								app.dataHandler.exclude(new Modification() {

									@Override
									public void modifyData(Data data) {
										Group group = data.groupsMap
												.get(mCurrentGroupInfomation.gid
														+ "");
										group.messages.clear();
									}
								});
							}
						}).show();
			}
		});
		backView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});
		groupInfomationScrollView.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				groupInfomationScrollView
						.requestDisallowInterceptTouchEvent(false);
				return false;
			}
		});
		final GestureDetector mInterceptDetector = new GestureDetector(
				GroupInformationActivity.this,
				new GestureDetector.SimpleOnGestureListener() {
					@Override
					public boolean onDown(MotionEvent e) {
						return true;
					}

					@Override
					public boolean onScroll(MotionEvent e1, MotionEvent e2,
							float distanceX, float distanceY) {
						float absDistanceX = Math.abs(distanceX);
						float absDistanceY = Math.abs(distanceY);
						if (absDistanceX > absDistanceY) {
							isInterceptTouchEvent = true;
						} else {
							isInterceptTouchEvent = false;
						}
						return true;
					}
				});
		mScrollContainer.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				isInterceptTouchEvent = false;
				mInterceptDetector.onTouchEvent(event);
				groupInfomationScrollView
						.requestDisallowInterceptTouchEvent(isInterceptTouchEvent);
				return false;
			}
		});

	}

	private void notifyMembersViews(final ViewContainer viewContainer,
			CircleHolder circleHolder) {
		viewContainer.removeAllViews();
		List<String> friends = mCurrentGroupInfomation.members;
		Map<String, Friend> friendsMap = app.data.groupFriends;
		final int pageSize = (friends.size() % 8) == 0 ? (friends.size() / 8)
				: (friends.size() / 8) + 1;
		final LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
				android.widget.LinearLayout.LayoutParams.WRAP_CONTENT,
				android.widget.LinearLayout.LayoutParams.WRAP_CONTENT);
		ll_pagepoint.removeAllViews();
		for (int i = 0; i < pageSize; i++) {
			ImageView iv = new ImageView(GroupInformationActivity.this);
			if (i == 0) {
				iv.setImageResource(R.drawable.point_white);
			} else {
				iv.setImageResource(R.drawable.point_blank);
			}
			iv.setLayoutParams(params);
			ll_pagepoint.addView(iv);
		}
		groupMembersCount.setText(" 群组信息  (" + friends.size() + "人)");
		for (int i = 0; i < friends.size(); i++) {
			final Friend friend = friendsMap.get(friends.get(i));
			FriendHolder friendHolder = new FriendHolder();
			friendHolder.phone = friend.phone;
			if (friend.phone.equals("")) {
				continue;
			}
			int index = circleHolder.friendHolders.indexOf(friendHolder);
			friendHolder = (index != -1 ? circleHolder.friendHolders
					.remove(index) : null);
			View convertView;
			if (friendHolder == null) {
				convertView = generateFriendView(friend);
				friendHolder = new FriendHolder();
				friendHolder.phone = friend.phone;
				friendHolder.view = convertView;
				viewContainer.addView(convertView);
				// final GestureDetector mInterceptDetector = new
				// GestureDetector(
				// GroupInformationActivity.this,
				// new GestureDetector.SimpleOnGestureListener() {
				// @Override
				// public boolean onDown(MotionEvent e) {
				// // isInterceptTouchEvent = false;
				// Toast.makeText(GroupInformationActivity.this,
				// "hahha", Toast.LENGTH_LONG).show();
				// return true;
				// }
				//
				// @Override
				// public boolean onScroll(MotionEvent e1,
				// MotionEvent e2, float distanceX,
				// float distanceY) {
				// float absDistanceX = Math.abs(distanceX);
				// float absDistanceY = Math.abs(distanceY);
				// if (absDistanceX > absDistanceY) {
				// isInterceptTouchEvent = true;
				// } else {
				// isInterceptTouchEvent = false;
				// }
				// return true;
				// }
				// });
				// convertView.setOnTouchListener(new OnTouchListener() {
				//
				// @Override
				// public boolean onTouch(View v, MotionEvent event) {
				// isInterceptTouchEvent = false;
				// mInterceptDetector.onTouchEvent(event);
				// groupInfomationScrollView
				// .requestDisallowInterceptTouchEvent(isInterceptTouchEvent);
				// return false;
				// }
				// });
				convertView.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						Intent intent = new Intent(
								GroupInformationActivity.this,
								BusinessCardActivity.class);
						if (friend.phone.equals(app.data.user.phone)) {
							intent.putExtra("type",
									BusinessCardActivity.TYPE_SELF);
						} else if (app.data.friends.get(friend.phone) != null) {
							intent.putExtra("type",
									BusinessCardActivity.TYPE_FRIEND);
						} else {
							intent.putExtra("type",
									BusinessCardActivity.TYPE_TEMPFRIEND);
							intent.putExtra("friend", friend);
						}
						intent.putExtra("phone", friend.phone);
						startActivity(intent);
					}
				});
			}

			circleHolder.friendHolders.add(i, friendHolder);
			mScrollContainer
					.setOnPageChangedListener(new OnPageChangedListener() {

						@Override
						public void pageChanged(int currentPage) {
							ll_pagepoint.removeAllViews();
							for (int i = 0; i < pageSize; i++) {
								ImageView iv = new ImageView(
										GroupInformationActivity.this);
								if (i == currentPage) {
									iv.setImageResource(R.drawable.point_white);
								} else {
									iv.setImageResource(R.drawable.point_blank);
								}
								iv.setLayoutParams(params);
								ll_pagepoint.addView(iv);
							}
						}
					});
		}
		final GestureDetector detector = new GestureDetector(
				GroupInformationActivity.this, new SimpleOnGestureListener() {
					float x0 = 0;
					float dx = 0;

					@Override
					public boolean onDown(MotionEvent e) {
						x0 = e.getRawX();
						return true;
					}

					@Override
					public boolean onScroll(MotionEvent e1, MotionEvent e2,
							float distanceX, float distanceY) {
						dx = e2.getRawX() - x0;
						viewContainer.scrollBy(-(int) (dx), 0);
						x0 = e2.getRawX();
						return true;
					}
				});
		mScrollContainer
				.setOnInterceptTouchDownListener(new onInterceptTouchDownListener() {

					@Override
					public void onInterceptTouchDown(MotionEvent ev) {
						mScrollContainer.requestDisallowInterceptToScroll(true);
					}
				});
		resolveFriendsPositions(circleHolder);
		setFriendsPositions(circleHolder);
	}

	View generateFriendView(Friend friend) {
		View convertView = mInflater.inflate(
				R.layout.fragment_circles_gridpage_item, null);
		final ImageView head = (ImageView) convertView
				.findViewById(R.id.iv_head);
		TextView nickname = (TextView) convertView
				.findViewById(R.id.tv_nickname);
		nickname.setText(friend.nickName);
		final String headFileName = friend.head;
		app.fileHandler.getHeadImage(headFileName, friend.sex,
				new FileResult() {
					@Override
					public void onResult(String where, Bitmap bitmap) {
						head.setImageBitmap(app.fileHandler.bitmaps
								.get(headFileName));
					}
				});
		return convertView;
	}

	public int getScreenWidth() {
		return getResources().getDisplayMetrics().widthPixels;
	}

	public int getScreenHeight() {
		return getResources().getDisplayMetrics().heightPixels;
	}

	public float dp2px(float px) {
		float dp = getResources().getDisplayMetrics().density * px + 0.5f;
		return dp;
	}

	class Position {
		int x = 0;
		int y = 0;
	}

	Position switchPosition(int i) {
		Position position = new Position();
		int baseX = (int) dp2px(i / 8 * 326);
		if ((i + 1) % 8 == 1) {
			position.y = (int) dp2px(11);
			position.x = (int) (baseLeft + baseX);
		} else if ((i + 1) % 8 == 2) {
			position.y = (int) dp2px(11);
			position.x = (int) (baseLeft + head + headSpace + baseX);
		} else if ((i + 1) % 8 == 3) {
			position.y = (int) dp2px(11);
			position.x = (int) (baseLeft + head + headSpace + head + headSpace + baseX);
		} else if ((i + 1) % 8 == 4) {
			position.y = (int) dp2px(11);
			position.x = (int) (baseLeft + head + headSpace + head + headSpace
					+ head + headSpace + baseX);
		} else if ((i + 1) % 8 == 5) {
			position.y = (int) dp2px(11 + 73 + 27);
			position.x = (int) (baseLeft + baseX);
		} else if ((i + 1) % 8 == 6) {
			position.y = (int) dp2px(11 + 73 + 27);
			position.x = (int) (baseLeft + head + headSpace + baseX);
		} else if ((i + 1) % 8 == 7) {
			position.y = (int) dp2px(11 + 73 + 27);
			position.x = (int) (baseLeft + head + headSpace + head + headSpace + baseX);
		} else if ((i + 1) % 8 == 0) {
			position.y = (int) dp2px(11 + 73 + 27);
			position.x = (int) (baseLeft + head + headSpace + head + headSpace
					+ head + headSpace + baseX);
		}
		return position;
	}

	class FriendHolder {
		Position position;
		View view;
		String phone = "";
		int index;

		@Override
		public boolean equals(Object o) {
			boolean flag = false;
			if (o != null) {
				if (o instanceof FriendHolder) {
					FriendHolder h = (FriendHolder) o;
					if (phone.equals(h.phone)) {
						flag = true;
					}
				} else if (o instanceof String) {
					String s = (String) o;
					if (phone.equals(s)) {
						flag = true;
					}
				}
			}
			return flag;
		}
	}

	class CircleHolder {
		public List<FriendHolder> friendHolders = new ArrayList<FriendHolder>();
	}

	public void resolveFriendsPositions(CircleHolder circleHolder) {
		for (int i = 0; i < circleHolder.friendHolders.size(); i++) {
			FriendHolder friendHolder = circleHolder.friendHolders.get(i);
			friendHolder.position = switchPosition(i);
			friendHolder.index = i;
		}
	}

	public void setFriendsPositions(CircleHolder circleHolder) {
		for (int i = 0; i < circleHolder.friendHolders.size(); i++) {
			FriendHolder friendHolder = circleHolder.friendHolders.get(i);

			RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
					(int) dp2px(55f),
					android.widget.RelativeLayout.LayoutParams.WRAP_CONTENT);
			params.rightMargin = -Integer.MAX_VALUE;

			params.topMargin = friendHolder.position.y;
			params.leftMargin = friendHolder.position.x;
			friendHolder.view.setLayoutParams(params);
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == 101 && resultCode == Activity.RESULT_OK) {
			circleHolder = new CircleHolder();
			notifyMembersViews(viewContainer, circleHolder);
		} else if (requestCode == ChatBackGroundSettingActivity.RESULT_SELECTPICTURE
				&& resultCode == Activity.RESULT_OK) {
			ArrayList<String> photoList = data
					.getStringArrayListExtra("photoList");
			@SuppressWarnings("unchecked")
			HashMap<String, HashMap<String, Object>> photoListMap = (HashMap<String, HashMap<String, Object>>) data
					.getSerializableExtra("photoListMap");
			getImageMessageInfo(
					photoList.get(0),
					(String) photoListMap.get(photoList.get(0)).get(
							"contentType"), "images");
		} else if (requestCode == ChatBackGroundSettingActivity.RESULT_SELECTPICTURE
				&& resultCode == Activity.RESULT_FIRST_USER) {
			getImageMessageInfo(data.getStringExtra("path"), "image/jpeg",
					"camera");
		}
	}

	void getImageMessageInfo(final String filePath, final String contentType,
			final String type) {
		app.fileHandler.getFileMessageInfo(new FileMessageInfoInterface() {

			@Override
			public void setParams(FileMessageInfoSettings settings) {
				settings.FILE_TYPE = OSSFileHandler.FILE_TYPE_SDSELECTIMAGE;
				settings.path = filePath;
				settings.fileName = filePath.substring(filePath
						.lastIndexOf("/"));
			}

			@Override
			public void onSuccess(ImageMessageInfo imageMessageInfo) {
				File fromFile = new File(filePath);
				if ("camera".equals(type)) {
					File toFile = new File(app.sdcardBackImageFolder,
							imageMessageInfo.fileName);
					fromFile.renameTo(toFile);
				} else if ("images".equals(type)) {
					File toFile = new File(app.sdcardBackImageFolder,
							imageMessageInfo.fileName);
					if (!toFile.exists()) {
						try {
							FileOutputStream fileOutputStream = new FileOutputStream(
									toFile);
							FileInputStream fileInputStream = new FileInputStream(
									fromFile);
							StreamParser.parseToFile(fileInputStream,
									fileOutputStream);
						} catch (FileNotFoundException e) {
							e.printStackTrace();
						}
					}
				}
				checkImage(imageMessageInfo, contentType, filePath);
			}
		});
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
						modifyGroupBackGround(imageMessageInfo.fileName);
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
								modifyGroupBackGround(imageMessageInfo.fileName);
							}
						});
					}
				} catch (JSONException e) {
					e.printStackTrace();
					Alert.showMessage("设置群组背景图片失败");
				}
			}
		});
	}

	void modifyGroupBackGround(final String fileName) {
		// Toast.makeText(GroupInformationActivity.this, "image---hahahhaha",
		// Toast.LENGTH_LONG).show();
		app.networkHandler.connection(new CommonNetConnection() {
			@Override
			protected void settings(Settings settings) {
				settings.url = API.DOMAIN + API.GROUP_MODIFY;
				Map<String, String> params = new HashMap<String, String>();
				params.put("phone", app.data.user.phone);
				params.put("accessKey", app.data.user.accessKey);
				params.put("gid", mCurrentGroupInfomation.gid + "");
				params.put("background", fileName);
				settings.params = params;
			}

			@Override
			public void success(JSONObject jData) {
				// System.out.println(jData + "----------------");
			}
		});
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			// setResult(Activity.RESULT_OK);
			finish();
		}
		return true;
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.rl_checkChatMessages:
			setResult(Activity.RESULT_OK);
			finish();
			break;

		default:
			break;
		}

	}

	void exitGroup() {
		final JSONArray membersArray = new JSONArray();
		membersArray.put(app.data.user.phone);
		app.networkHandler.connection(new CommonNetConnection() {
			@Override
			protected void settings(Settings settings) {
				settings.url = API.DOMAIN + API.GROUP_REMOVEMEMBERS;
				Map<String, String> params = new HashMap<String, String>();
				params.put("phone", app.data.user.phone);
				params.put("accessKey", app.data.user.accessKey);
				params.put("gid", mCurrentGroupInfomation.gid + "");
				params.put("members", membersArray.toString());
				settings.params = params;
			}

			@Override
			public void success(JSONObject jData) {
				DataUtil.getGroups(new GetDataListener() {

					@Override
					public void getSuccess() {
						app.UIHandler.post(new Runnable() {

							@Override
							public void run() {
								setResult(Activity.RESULT_CANCELED);
								finish();
							}
						});
					}
				});
			}

			@Override
			protected void failed(int failedType) {
				exit2DeleteGroupView.setClickable(true);
				super.failed(failedType);
			}

			@Override
			protected void failed(int failedType, int responseCode) {
				exit2DeleteGroupView.setClickable(true);
				super.failed(failedType, responseCode);
			}
		});
	}
}
