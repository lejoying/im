package com.open.welinks.view;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.rebound.BaseSpringSystem;
import com.facebook.rebound.Spring;
import com.facebook.rebound.SpringConfig;
import com.facebook.rebound.SpringSystem;
import com.open.lib.TouchView;
import com.open.welink.R;
import com.open.welinks.controller.MeSubController;
import com.open.welinks.model.Data;
import com.open.welinks.model.Data.UserInformation.User;
import com.open.welinks.model.Parser;
import com.open.welinks.model.TaskManageHolder;

public class MeSubView {

	public Data data = Data.getInstance();
	public Parser parser = Parser.getInstance();

	public TaskManageHolder taskManageHolder = TaskManageHolder.getInstance();

	public String tag = "MeSubView";

	public ImageView userHeadImageView;
	public TextView userNickNameView;
	public TextView userBusinessView;

	public ImageView mAppIconToNameView;
	public View mRootView, myBusiness, mySetting;

	public TouchView dynamicListView, moreFriendView;
	public ImageView dynamicListStatusView;

	public SpringConfig IMAGE_SPRING_CONFIG = SpringConfig.fromOrigamiTensionAndFriction(100, 4);
	public BaseSpringSystem mSpringSystem = SpringSystem.create();
	public Spring mMePageAppIconScaleSpring = mSpringSystem.createSpring().setSpringConfig(IMAGE_SPRING_CONFIG);

	public MainView mainView;

	public MeSubController thisController;

	public MeSubView(MainView mainView) {
		this.mainView = mainView;
		taskManageHolder.viewManage.meSubView = this;
	}

	public void initViews() {
		userHeadImageView = (ImageView) mainView.meView.findViewById(R.id.iv_headImage);
		userNickNameView = (TextView) mainView.meView.findViewById(R.id.tv_userNickname);
		userBusinessView = (TextView) mainView.meView.findViewById(R.id.tv_userMainBusiness);

		myBusiness = mainView.meView.findViewById(R.id.businesscard);
		mySetting = mainView.meView.findViewById(R.id.mySetting);

		dynamicListView = (TouchView) mainView.meView.findViewById(R.id.dynamicList);
		dynamicListStatusView = (ImageView) mainView.meView.findViewById(R.id.dynamicListStatus);
		moreFriendView = (TouchView) mainView.meView.findViewById(R.id.morefriend);

		mAppIconToNameView = (ImageView) mainView.meView.findViewById(R.id.appIconToName);
		mRootView = mAppIconToNameView;
		setUserData();
	}

	public void setUserData() {
		parser.check();
		User user = data.userInformation.currentUser;
		if (user != null) {
			taskManageHolder.fileHandler.getHeadImage(user.head, this.userHeadImageView, taskManageHolder.viewManage.options60);
			this.userNickNameView.setText(user.nickName);
			this.userBusinessView.setText(user.mainBusiness);
			if (data.event.userNotReadMessage || data.event.groupNotReadMessage) {
				dynamicListStatusView.setVisibility(View.VISIBLE);
			} else {
				dynamicListStatusView.setVisibility(View.GONE);
			}
		}
	}
}
