package com.lejoying.wxgs.activity.mode.fragment;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lejoying.wxgs.R;
import com.lejoying.wxgs.activity.BusinessCardActivity;
import com.lejoying.wxgs.activity.MainActivity;
import com.lejoying.wxgs.activity.SettingActivity;
import com.lejoying.wxgs.activity.mode.MainModeManager;
import com.lejoying.wxgs.activity.view.widget.Alert;
import com.lejoying.wxgs.activity.view.widget.Alert.AlertInputDialog;
import com.lejoying.wxgs.app.MainApplication;
import com.lejoying.wxgs.app.handler.OSSFileHandler.FileResult;
import com.lejoying.wxgs.app.service.PushService;

/**
 * MyFragment 2014-7-30 上午10:25:09
 * 
 * @author 乔晓松 qiaoxiaosong@lejoying.com
 */
public class MyFragment extends BaseFragment implements OnClickListener {

	MainApplication app = MainApplication.getMainApplication();
	MainModeManager mMainModeManager;
	LayoutInflater mInflater;

	View mMainContentView;
	RelativeLayout current_me_circles;
	RelativeLayout current_me_message_list;
	RelativeLayout current_me_infomation;
	RelativeLayout userInfomationView;
	RelativeLayout myBusinessCardView;
	RelativeLayout exitCurrentUserView;
	RelativeLayout userSettingView;

	ImageView headImageView;
	TextView userNickname;
	TextView userMainBusiness;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mInflater = inflater;
		mMainContentView = mInflater.inflate(R.layout.f_my, null);
		current_me_circles = (RelativeLayout) mMainContentView
				.findViewById(R.id.current_me_circles);
		current_me_message_list = (RelativeLayout) mMainContentView
				.findViewById(R.id.current_me_message_list);
		current_me_infomation = (RelativeLayout) mMainContentView
				.findViewById(R.id.current_me_infomation);
		userInfomationView = (RelativeLayout) mMainContentView
				.findViewById(R.id.rl_userInfomation);
		myBusinessCardView = (RelativeLayout) mMainContentView
				.findViewById(R.id.rl_myBusinessCard);
		exitCurrentUserView = (RelativeLayout) mMainContentView
				.findViewById(R.id.rl_exitCurrentUser);
		userSettingView = (RelativeLayout) mMainContentView
				.findViewById(R.id.rl_settingwxgs);
		headImageView = (ImageView) mMainContentView
				.findViewById(R.id.iv_headImage);
		userNickname = (TextView) mMainContentView
				.findViewById(R.id.tv_userNickname);
		userMainBusiness = (TextView) mMainContentView
				.findViewById(R.id.tv_userMainBusiness);
		initEvent();
		initData();
		return mMainContentView;
	}

	void setOnTouch(final View view) {
		view.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					view.setBackgroundColor(Color.argb(143, 0, 0, 0));
					break;
				case MotionEvent.ACTION_UP:
					view.setBackgroundColor(Color.parseColor("#38ffffff"));
					break;
				}
				return false;
			}
		});
	}

	private void initData() {
		app.fileHandler.getHeadImage(app.data.user.head, app.data.user.sex,
				new FileResult() {

					@Override
					public void onResult(String where, Bitmap bitmap) {
						headImageView.setImageBitmap(bitmap);
					}
				});
		userNickname.setText(app.data.user.nickName);
		userMainBusiness.setText(app.data.user.mainBusiness);
	}

	private void initEvent() {
		userInfomationView.setOnClickListener(this);
		userSettingView.setOnClickListener(this);
		current_me_circles.setOnClickListener(this);
		current_me_message_list.setOnClickListener(this);
		myBusinessCardView.setOnClickListener(this);
		exitCurrentUserView.setOnClickListener(this);
		setOnTouch(exitCurrentUserView);
		setOnTouch(userSettingView);
		setOnTouch(myBusinessCardView);
	}

	public void setMode(MainModeManager mainMode) {
		mMainModeManager = mainMode;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.rl_userInfomation:
			Intent userInfomationIntent = new Intent(getActivity(),
					BusinessCardActivity.class);
			userInfomationIntent.putExtra("type",
					BusinessCardActivity.TYPE_SELF);
			startActivityForResult(userInfomationIntent, 101);
			break;
		case R.id.rl_myBusinessCard:
			Intent businessCardIntent = new Intent(getActivity(),
					BusinessCardActivity.class);
			businessCardIntent.putExtra("type", BusinessCardActivity.TYPE_SELF);
			startActivityForResult(businessCardIntent, 101);
			break;
		case R.id.rl_exitCurrentUser:
			Alert.createDialog(getActivity())
					.setTitle("退出登录后您将接收不到任何消息，确定要退出登录吗？")
					.setOnConfirmClickListener(
							new AlertInputDialog.OnDialogClickListener() {
								@Override
								public void onClick(AlertInputDialog dialog) {
									Intent service = new Intent(getActivity(),
											PushService.class);
									service.putExtra("operation", "stop");
									getActivity().startService(service);
									// finish();
								}
							}).show();
			break;
		case R.id.current_me_circles:
			MainActivity.instance.mMainMode.mCurrentMyFragment = MainActivity.instance.mMainMode.FRAGMENT_CIRCLE;
			MainActivity.instance.mMainMode
					.show(MainActivity.instance.mMainMode.mCirclesFragment);
			break;
		case R.id.current_me_message_list:
			MainActivity.instance.mMainMode.mCurrentMyFragment = MainActivity.instance.mMainMode.FRAGMENT_CHATMESSAGE;
			MainActivity.instance.mMainMode
					.show(MainActivity.instance.mMainMode.mChatMessagesFragment);
			break;
		case R.id.rl_settingwxgs:
			Intent settingIntent = new Intent(getActivity(),
					SettingActivity.class);
			startActivity(settingIntent);
			break;
		default:
			break;
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == 101 && resultCode == Activity.RESULT_OK) {
			initData();
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
}
