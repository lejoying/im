package com.open.welinks.view;

import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.rebound.BaseSpringSystem;
import com.facebook.rebound.Spring;
import com.facebook.rebound.SpringConfig;
import com.facebook.rebound.SpringSystem;
import com.open.welinks.R;
import com.open.welinks.controller.MeSubController;
import com.open.welinks.model.Data;

public class MeSubView {

	public Data data = Data.getInstance();

	public String tag = "MeSubView";

	public ImageView userHeadImageView;
	public TextView userNickNameView;
	public TextView userBusinessView;

	public ImageView mAppIconToNameView;
	public View mRootView, myBusiness, mySetting;

	public RelativeLayout dynamicListView, moreFriendView;

	public SpringConfig IMAGE_SPRING_CONFIG = SpringConfig.fromOrigamiTensionAndFriction(100, 4);
	public BaseSpringSystem mSpringSystem = SpringSystem.create();
	public Spring mMePageAppIconScaleSpring = mSpringSystem.createSpring().setSpringConfig(IMAGE_SPRING_CONFIG);

	public MainView mainView;

	public MeSubController thisController;

	public MeSubView(MainView mainView) {
		this.mainView = mainView;
	}

	public void initViews() {

		userHeadImageView = (ImageView) mainView.meView.findViewById(R.id.iv_headImage);
		userNickNameView = (TextView) mainView.meView.findViewById(R.id.tv_userNickname);
		userBusinessView = (TextView) mainView.meView.findViewById(R.id.tv_userMainBusiness);

		myBusiness = mainView.meView.findViewById(R.id.businesscard);
		mySetting = mainView.meView.findViewById(R.id.mySetting);

		dynamicListView = (RelativeLayout) mainView.meView.findViewById(R.id.dynamicList);
		moreFriendView = (RelativeLayout) mainView.meView.findViewById(R.id.morefriend);

		mAppIconToNameView = (ImageView) mainView.meView.findViewById(R.id.appIconToName);
		mRootView = mAppIconToNameView;
	}
}
