package com.lejoying.wxgs.activity.mode.fragment;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Rect;
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
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.lejoying.wxgs.R;
import com.lejoying.wxgs.activity.mode.MainModeManager;
import com.lejoying.wxgs.activity.utils.CommonNetConnection;
import com.lejoying.wxgs.activity.view.widget.Alert;
import com.lejoying.wxgs.activity.view.widget.CircleMenu;
import com.lejoying.wxgs.activity.view.widget.Alert.DialogListener;
import com.lejoying.wxgs.app.MainApplication;
import com.lejoying.wxgs.app.data.API;
import com.lejoying.wxgs.app.data.Data;
import com.lejoying.wxgs.app.data.entity.Friend;
import com.lejoying.wxgs.app.handler.DataHandler.Modification;
import com.lejoying.wxgs.app.handler.FileHandler.FileResult;
import com.lejoying.wxgs.app.handler.NetworkHandler.Settings;
import com.lejoying.wxgs.app.service.PushService;

public class BusinessCardFragment extends BaseFragment {

	public int mStatus;
	public Friend mShowFriend;
	public static final int SHOW_SELF = 1;
	public static final int SHOW_FRIEND = 2;
	public static final int SHOW_TEMPFRIEND = 3;

	MainApplication app = MainApplication.getMainApplication();
	MainModeManager mMainModeManager;
	private static final int SCROLL = 0x51;

	View mContent;

	private TextView tv_spacing;
	private TextView tv_spacing2;
	private TextView tv_mainbusiness;
	private RelativeLayout rl_show;
	private ScrollView sv_content;

	// DEFINITION object
	private Handler handler;
	private boolean stopSend;

	public void setMode(MainModeManager mainMode) {
		mMainModeManager = mainMode;
	}

	@SuppressLint("HandlerLeak")
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mContent = inflater.inflate(R.layout.f_businesscard, null);

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

		tv_spacing = (TextView) mContent.findViewById(R.id.tv_spacing);
		tv_spacing2 = (TextView) mContent.findViewById(R.id.tv_spacing2);
		tv_mainbusiness = (TextView) mContent
				.findViewById(R.id.tv_mainbusiness);
		rl_show = (RelativeLayout) mContent.findViewById(R.id.rl_show);

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
		return mContent;
	}

	@Override
	public void onResume() {
		CircleMenu.showBack();
		super.onResume();
	}

	public void initData() {
		ViewGroup group = (ViewGroup) mContent.findViewById(R.id.ll_content);
		final ImageView iv_head = (ImageView) mContent
				.findViewById(R.id.iv_head);
		TextView tv_nickname = (TextView) mContent
				.findViewById(R.id.tv_nickname);
		TextView tv_phone = (TextView) mContent.findViewById(R.id.tv_phone);
		TextView tv_mainbusiness = (TextView) mContent
				.findViewById(R.id.tv_mainbusiness);
		Button button1 = (Button) mContent.findViewById(R.id.button1);
		Button button2 = (Button) mContent.findViewById(R.id.button2);
		Button button3 = (Button) mContent.findViewById(R.id.button3);
		String fileName = "";
		if (mStatus == SHOW_TEMPFRIEND) {
			tv_nickname.setText(mShowFriend.nickName);
			tv_phone.setText(mShowFriend.phone);
			fileName = mShowFriend.head;
			tv_mainbusiness.setText(mShowFriend.mainBusiness);
			button1.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					// mMCFragmentManager.replaceToContent(
					// new AddFriendFragment(), true);
				}
			});
			button2.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub

				}
			});
		} else if (mStatus == SHOW_SELF) {
			button1.setText("修改个人信息");
			button2.setText("退出登录");
			tv_nickname.setText(app.data.user.nickName);
			fileName = app.data.user.head;
			tv_phone.setText(app.data.user.phone);
			tv_mainbusiness.setText(app.data.user.mainBusiness);
			group.removeView(button3);
			button1.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					// mMCFragmentManager.replaceToContent(new ModifyFragment(),
					// true);
				}
			});

			button2.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					Alert.showDialog("退出登录后您将接收不到任何消息，确定要退出登录吗？",
							new DialogListener() {

								@Override
								public void onCancel() {
									// TODO Auto-generated method stub

								}

								@Override
								public boolean confirm() {
									Intent service = new Intent(getActivity(),
											PushService.class);
									service.putExtra("operation", "stop");
									getActivity().startService(service);
									return true;
								}

								@Override
								public void cancel() {
									// TODO Auto-generated method stub

								}
							});
				}
			});

		} else if (mStatus == SHOW_FRIEND) {
			button1.setText("发起聊天");
			button2.setText("修改备注");
			button3.setText("解除好友关系");
			tv_nickname.setText(mShowFriend.nickName);
			tv_phone.setText(mShowFriend.phone);
			fileName = mShowFriend.head;
			tv_mainbusiness.setText(mShowFriend.mainBusiness);
			button1.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					mMainModeManager.mChatFragment.mStatus = ChatFragment.CHAT_FRIEND;
					mMainModeManager.mChatFragment.mNowChatFriend = mShowFriend;
					mMainModeManager.showNext(mMainModeManager.mChatFragment);
				}
			});
			button2.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub

				}
			});
			button3.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					Alert.showDialog(
							"确定解除和" + mShowFriend.nickName + "的好友关系吗？",
							new DialogListener() {

								@Override
								public void onCancel() {
									// TODO Auto-generated method stub

								}

								@Override
								public boolean confirm() {
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
																	app.data.friends
																			.remove(mShowFriend.phone);
																}

																@Override
																public void modifyUI() {
																	mMainModeManager.mCirclesFragment.mAdapter
																			.notifyDataSetChanged();
																}
															});
												}

												@Override
												protected void settings(
														Settings settings) {
													settings.url = API.DOMAIN
															+ API.RELATION_DELETEFRIEND;
													Map<String, String> params = new HashMap<String, String>();
													params.put("phone",
															app.data.user.phone);
													params.put(
															"accessKey",
															app.data.user.accessKey);
													params.put("phoneto", "["
															+ mShowFriend.phone
															+ "]");
													settings.params = params;
												}
											});
									mMainModeManager.back();
									return true;
								}

								@Override
								public void cancel() {
									// TODO Auto-generated method stub

								}
							});

				}
			});
		}
		final String headFileName = fileName;
		app.fileHandler.getHeadImage(headFileName, new FileResult() {
			@Override
			public void onResult(String where) {
				iv_head.setImageBitmap(app.fileHandler.bitmaps
						.get(headFileName));
			}
		});
	}

}
