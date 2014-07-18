package com.lejoying.wxgs.activity;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.lejoying.wxgs.R;
import com.lejoying.wxgs.activity.utils.CommonNetConnection;
import com.lejoying.wxgs.activity.utils.DataUtil;
import com.lejoying.wxgs.activity.utils.DataUtil.GetDataListener;
import com.lejoying.wxgs.activity.utils.MCImageUtils;
import com.lejoying.wxgs.app.MainApplication;
import com.lejoying.wxgs.app.data.API;
import com.lejoying.wxgs.app.data.entity.Group;
import com.lejoying.wxgs.app.handler.NetworkHandler.Settings;
import com.lejoying.wxgs.app.handler.OSSFileHandler.FileResult;

public class GroupBusinessCardActivity extends Activity implements
		OnClickListener {
	public Group mGroup;

	private TextView tv_spacing;
	private TextView tv_spacing2;
	private TextView tv_mainbusiness;
	private RelativeLayout rl_show;
	private ScrollView sv_content;
	ImageView iv_head;
	TextView tv_bighead;
	ViewGroup group;
	View rl_bighead;
	// DEFINITION object
	private Handler handler;
	private static final int SCROLL = 0x51;
	private boolean stopSend;
	// private int width, height;

	// View mContent;
	LayoutInflater mInflater;

	ImageView iv_me_back;
	TextView tv_back_show;
	ImageView QRcodeImage;

	MainApplication app = MainApplication.getMainApplication();
	// MainModeManager mMainModeManager;

	String GROUPCARDTYPE = "groupcard";

	@SuppressLint("HandlerLeak")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Intent intent = getIntent();
		String gid = intent.getStringExtra("gid");
		if (gid != null && !"".equals(gid)) {
			mGroup = app.data.groupsMap.get(gid);
		}
		setContentView(R.layout.f_businesscard);
		iv_me_back = (ImageView) findViewById(R.id.iv_me_back);
		tv_back_show = (TextView) findViewById(R.id.tv_back_show);
		QRcodeImage = (ImageView) findViewById(R.id.iv_tdcode);
		getWindow().setBackgroundDrawableResource(R.drawable.background4);
		handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
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
		tv_spacing = (TextView) findViewById(R.id.tv_spacing);
		tv_spacing2 = (TextView) findViewById(R.id.tv_spacing2);
		tv_mainbusiness = (TextView) findViewById(R.id.tv_mainbusiness);
		rl_show = (RelativeLayout) findViewById(R.id.rl_show);

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
				GroupBusinessCardActivity.this.getWindowManager()
						.getDefaultDisplay().getMetrics(dm);
				// height = dm.heightPixels;
				// width = dm.widthPixels;
				Rect frame = new Rect();
				GroupBusinessCardActivity.this.getWindow().getDecorView()
						.getWindowVisibleDisplayFrame(frame);
				int statusBarHeight = frame.top;
				sv_content = (ScrollView) findViewById(R.id.sv_content);

				tv_spacing.setHeight((int) (dm.heightPixels
						- rl_show.getHeight() - statusBarHeight - tv_spacing2
						.getHeight()));

				sv_content.setOnTouchListener(new OnTouchListener() {

					@Override
					public boolean onTouch(View v, MotionEvent event) {
						// TODO Auto-generated method stub
						stopSend = true;
						new Thread() {
							@Override
							public void run() {
								// TODO Auto-generated method stub
								while (stopSend) {
									handler.sendEmptyMessage(SCROLL);
									int start = sv_content.getScrollY();
									try {
										Thread.sleep(100);
									} catch (InterruptedException e) {
										// TODO Auto-generated catch block
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
		initData();
		initEvent();
	}

	private void initEvent() {
		iv_me_back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
				// mMainModeManager.back();
			}
		});
	}

	public void onResume() {
		// CircleMenu.showBack();
		// mMainModeManager.handleMenu(false);
		super.onResume();
	}

	public void initData() {
		QRcodeImage.setImageBitmap(MCImageUtils.createQEcodeImage(
				GROUPCARDTYPE, mGroup.gid + ""));
		QRcodeImage.setScaleType(ScaleType.FIT_CENTER);
		tv_back_show.setText("群组资料");
		group = (ViewGroup) findViewById(R.id.ll_content);
		View tv_group = findViewById(R.id.tv_group_layout);
		View tv_square = findViewById(R.id.tv_square_layout);
		View tv_msg = findViewById(R.id.tv_msg_layout);
		rl_bighead = findViewById(R.id.rl_bighead);
		rl_bighead.setVisibility(View.GONE);

		iv_head = (ImageView) findViewById(R.id.iv_head);
		tv_bighead = (TextView) findViewById(R.id.tv_bighead);
		TextView tv_nickname = (TextView) findViewById(R.id.tv_nickname);

		TextView tv_id = (TextView) findViewById(R.id.tv_id);
		TextView tv_id_title = (TextView) findViewById(R.id.tv_id_title);
		TextView tv_alias = (TextView) findViewById(R.id.tv_alias);
		TextView tv_alias_title = (TextView) findViewById(R.id.tv_alias_title);

		TextView tv_business = (TextView) findViewById(R.id.tv_business);
		TextView tv_createTime = (TextView) findViewById(R.id.tv_mainbusiness);
		TextView tv_tag = (TextView) findViewById(R.id.tv_tag);
		TextView tv_phone_title = (TextView) findViewById(R.id.tv_phone_title);
		TextView tv_mainbusiness_title = (TextView) findViewById(R.id.tv_mainbusiness_title);
		TextView tv_sex_title = (TextView) findViewById(R.id.tv_sex_title);

		Button button1 = (Button) findViewById(R.id.button1);
		Button button2 = (Button) findViewById(R.id.button2);
		Button button3 = (Button) findViewById(R.id.button3);

		// tv_business.setVisibility(View.GONE);
		// tv_createTime.setVisibility(View.GONE);
		// tv_tag.setVisibility(View.GONE);
		// tv_phone_title.setVisibility(View.GONE);
		// tv_mainbusiness_title.setVisibility(View.GONE);
		// tv_sex_title.setVisibility(View.GONE);

		group.removeView(button2);
		group.removeView(button3);
		group.removeView(tv_group);
		group.removeView(tv_square);
		group.removeView(tv_msg);

		// List<String> myGroups=app.data.groups;
		// for(int i=0;i<myGroups.size();i++){
		// if(myGroups.get(i).equals(mGroup.members.get(i))){
		// button1.setText("开始聊天");
		// break;
		// }else{
		// button1.setText("加入群组");
		// }
		//
		// //System.out.println(myGroups.get(i));
		// }
		if (app.data.groupsMap.get(mGroup.gid) == null) {
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
					// mMainModeManager.back();
					Intent intent = new Intent(GroupBusinessCardActivity.this,
							ChatActivity.class);
					intent.putExtra("status", ChatActivity.CHAT_GROUP);
					intent.putExtra("gid", mGroup.gid + "");
					startActivity(intent);
					// mMainModeManager.mChatGroupFragment.mStatus =
					// ChatFriendFragment.CHAT_GROUP;
					// mMainModeManager.mChatGroupFragment.mNowChatGroup =
					// app.data.groupsMap
					// .get(mGroup.gid);
					// mMainModeManager
					// .showNext(mMainModeManager.mChatGroupFragment);
				}
			});
		}
		tv_id_title.setText("群组ID：");
		// tv_alias_title.setText("群组描述：");
		tv_alias_title.setVisibility(View.GONE);
		tv_nickname.setText(mGroup.name);
		tv_id.setText(String.valueOf(mGroup.gid));
		if (mGroup.description == null || mGroup.description.equals("")
				|| mGroup.description.equals("请输入群组描述信息")) {
			tv_business.setText("此群组暂无业务");
			tv_alias.setVisibility(View.GONE);
		} else {
			tv_alias.setText(mGroup.description);
		}
		final String headFileName = mGroup.icon;
		app.fileHandler.getHeadImage(headFileName, "男", new FileResult() {
			@Override
			public void onResult(String where, Bitmap bitmap) {
				iv_head.setImageBitmap(app.fileHandler.bitmaps
						.get(headFileName));
			}
		});
		iv_head.setOnClickListener(this);
		rl_bighead.setOnClickListener(this);
	}

	@SuppressWarnings("deprecation")
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.rl_bighead:
			rl_bighead.setVisibility(View.GONE);
			group.setVisibility(View.VISIBLE);
			// CircleMenu.showBack();
			break;
		case R.id.iv_head:
			tv_bighead.setBackgroundDrawable(new BitmapDrawable(
					app.fileHandler.bitmaps.get(mGroup.icon)));
			group.setVisibility(View.GONE);
			rl_bighead.setVisibility(View.VISIBLE);
			// CircleMenu.hide();
			break;
		default:
			break;
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
