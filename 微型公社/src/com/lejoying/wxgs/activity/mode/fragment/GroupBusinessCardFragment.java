package com.lejoying.wxgs.activity.mode.fragment;

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
import android.view.ViewGroup;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.lejoying.wxgs.R;
import com.lejoying.wxgs.activity.mode.MainModeManager;
import com.lejoying.wxgs.activity.view.widget.CircleMenu;
import com.lejoying.wxgs.app.MainApplication;
import com.lejoying.wxgs.app.data.entity.Group;
import com.lejoying.wxgs.app.handler.FileHandler.FileResult;

public class GroupBusinessCardFragment extends BaseFragment implements
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
	private int width, height;

	View mContent;
	LayoutInflater mInflater;

	MainApplication app = MainApplication.getMainApplication();
	MainModeManager mMainModeManager;

	public void setMode(MainModeManager mainMode) {
		mMainModeManager = mainMode;
	}

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mInflater = inflater;
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
		return mContent;
	}

	public void onResume() {
		CircleMenu.showBack();
		super.onResume();
	}

	public void initData() {
		group = (ViewGroup) mContent.findViewById(R.id.ll_content);
		View tv_group = mContent.findViewById(R.id.tv_group_layout);
		View tv_square = mContent.findViewById(R.id.tv_square_layout);
		View tv_msg = mContent.findViewById(R.id.tv_msg_layout);
		rl_bighead = mContent.findViewById(R.id.rl_bighead);
		rl_bighead.setVisibility(View.GONE);

		iv_head = (ImageView) mContent.findViewById(R.id.iv_head);
		tv_bighead = (TextView) mContent.findViewById(R.id.tv_bighead);
		TextView tv_nickname = (TextView) mContent
				.findViewById(R.id.tv_nickname);

		TextView tv_id = (TextView) mContent.findViewById(R.id.tv_id);
		TextView tv_id_title = (TextView) mContent
				.findViewById(R.id.tv_id_title);
		TextView tv_alias = (TextView) mContent.findViewById(R.id.tv_alias);
		TextView tv_alias_title = (TextView) mContent
				.findViewById(R.id.tv_alias_title);

		TextView tv_phone = (TextView) mContent.findViewById(R.id.tv_phone);
		TextView tv_mainbusiness = (TextView) mContent
				.findViewById(R.id.tv_mainbusiness);
		TextView tv_sex = (TextView) mContent.findViewById(R.id.tv_sex);
		TextView tv_phone_title = (TextView) mContent
				.findViewById(R.id.tv_phone_title);
		TextView tv_mainbusiness_title = (TextView) mContent
				.findViewById(R.id.tv_mainbusiness_title);
		TextView tv_sex_title = (TextView) mContent
				.findViewById(R.id.tv_sex_title);

		Button button1 = (Button) mContent.findViewById(R.id.button1);
		Button button2 = (Button) mContent.findViewById(R.id.button2);
		Button button3 = (Button) mContent.findViewById(R.id.button3);

		tv_phone.setVisibility(View.GONE);
		tv_mainbusiness.setVisibility(View.GONE);
		tv_sex.setVisibility(View.GONE);
		tv_phone_title.setVisibility(View.GONE);
		tv_mainbusiness_title.setVisibility(View.GONE);
		tv_sex_title.setVisibility(View.GONE);
		group.removeView(button1);
		group.removeView(button2);
		group.removeView(button3);
		group.removeView(tv_group);
		group.removeView(tv_square);
		group.removeView(tv_msg);

		tv_id_title.setText("群组ID：");
		tv_alias_title.setText("群组描述：");

		tv_nickname.setText(mGroup.name);
		tv_id.setText(String.valueOf(mGroup.gid));
		if (mGroup.description == null || mGroup.description.equals("")) {
			tv_alias.setText("次群组暂无描述");
		} else {
			tv_alias.setText(mGroup.description);
		}
		final String headFileName = mGroup.icon;
		app.fileHandler.getHeadImage(headFileName, new FileResult() {
			@Override
			public void onResult(String where) {
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
			CircleMenu.showBack();
			break;
		case R.id.iv_head:
			tv_bighead.setBackgroundDrawable(new BitmapDrawable(
					app.fileHandler.bitmaps.get(mGroup.icon)));
			group.setVisibility(View.GONE);
			rl_bighead.setVisibility(View.VISIBLE);
			CircleMenu.hide();
			break;
		default:
			break;
		}
	}
}
