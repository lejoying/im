package com.lejoying.mc.fragment;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Rect;
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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.lejoying.mc.R;
import com.lejoying.mc.adapter.ToTryAdapter;
import com.lejoying.mc.data.App;
import com.lejoying.mc.data.handler.FileHandler.FileResult;
import com.lejoying.mc.utils.MCImageTools;
import com.lejoying.mc.utils.ToTry;

public class BusinessCardFragment extends BaseFragment {
	App app = App.getInstance();
	private static final int SCROLL = 0x51;

	View mContent;

	private TextView tv_spacing;
	private TextView tv_spacing2;
	private TextView tv_spacing3;
	private TextView tv_mainbusiness;
	private RelativeLayout rl_show;
	private ScrollView sv_content;

	// DEFINITION object
	private Handler handler;
	private boolean stopSend;

	@Override
	protected EditText showSoftInputOnShow() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mMCFragmentManager.showCircleMenuToTop(true, true);
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
		return mContent;
	}

	@Override
	public void onResume() {
		app.mark = app.businessCardFragment;
		super.onResume();
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {

		tv_spacing = (TextView) mContent.findViewById(R.id.tv_spacing);
		tv_spacing2 = (TextView) mContent.findViewById(R.id.tv_spacing2);
		tv_spacing3 = (TextView) mContent.findViewById(R.id.tv_spacing3);
		tv_mainbusiness = (TextView) mContent
				.findViewById(R.id.tv_mainbusiness);
		rl_show = (RelativeLayout) mContent.findViewById(R.id.rl_show);

		initData();
		ToTry.tryDoing(10, 200, new ToTryAdapter() {

			@Override
			public void successed(long time) {
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
				tv_spacing3.setHeight((int) (dm.heightPixels * 0.2));

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

			@Override
			public boolean isSuccess() {
				return rl_show.getHeight() != 0;
			}
		});

		super.onActivityCreated(savedInstanceState);
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
		String fileName = "";
		if (app.businessCardStatus == app.SHOW_TEMPFRIEND) {
			tv_nickname.setText(app.data.tempFriend.nickName);
			tv_phone.setText(app.data.tempFriend.phone);
			fileName = app.data.tempFriend.head;
			tv_mainbusiness.setText(app.data.tempFriend.mainBusiness);
			button1.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					mMCFragmentManager.replaceToContent(
							new AddFriendFragment(), true);
				}
			});
			button2.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub

				}
			});
		} else if (app.businessCardStatus == app.SHOW_SELF) {
			button1.setText("修改个人信息");
			tv_nickname.setText(app.data.user.nickName);
			fileName = app.data.user.head;
			tv_phone.setText(app.data.user.phone);
			tv_mainbusiness.setText(app.data.user.mainBusiness);
			group.removeView(button2);
			button1.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					mMCFragmentManager.replaceToContent(new ModifyFragment(),
							true);
				}
			});

		} else if (app.businessCardStatus == app.SHOW_FRIEND) {
			button1.setText("发起聊天");
			button2.setText("修改备注");
			tv_nickname.setText(app.data.tempFriend.nickName);
			tv_phone.setText(app.data.tempFriend.phone);
			fileName = app.data.tempFriend.head;
			tv_mainbusiness.setText(app.data.tempFriend.mainBusiness);
			button1.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					app.data.nowChatFriend = app.data.tempFriend;
					mMCFragmentManager.replaceToContent(new ChatFragment(),
							true);
				}
			});
			button2.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub

				}
			});
		}
		final String headFileName = fileName;
		app.fileHandler.getImageFile(headFileName, new FileResult() {
			@Override
			public void onResult(String where) {
				if (where == app.fileHandler.FROM_DEFAULT) {
					iv_head.setImageBitmap(app.fileHandler.defaultHead);
				} else if (where != app.fileHandler.FROM_MEMORY) {
					Bitmap head = app.fileHandler.bitmaps.get(headFileName);
					head = MCImageTools.getCircleBitmap(head, true, 5,
							Color.WHITE);
					app.fileHandler.bitmaps.put(headFileName, head);
					iv_head.setImageBitmap(head);
				} else {
					iv_head.setImageBitmap(app.fileHandler.bitmaps
							.get(headFileName));
				}
			}
		});
	}
}
