package com.open.welinks.view;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.rebound.BaseSpringSystem;
import com.facebook.rebound.Spring;
import com.facebook.rebound.SpringConfig;
import com.facebook.rebound.SpringSystem;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.open.lib.TouchView;
import com.open.welinks.R;
import com.open.welinks.controller.MeSubController;
import com.open.welinks.model.Data;
import com.open.welinks.model.Data.UserInformation.User;
import com.open.welinks.model.FileHandlers;
import com.open.welinks.model.Parser;
import com.open.welinks.utils.MCImageUtils;

public class MeSubView {

	public Data data = Data.getInstance();
	public Parser parser = Parser.getInstance();

	public String tag = "MeSubView";

	public ImageView userHeadImageView;
	public TextView userNickNameView;
	public TextView userBusinessView;

	public ImageView mAppIconToNameView;
	public View mRootView, myBusiness, mySetting;

	public TouchView dynamicListView, moreFriendView;

	public SpringConfig IMAGE_SPRING_CONFIG = SpringConfig.fromOrigamiTensionAndFriction(100, 4);
	public BaseSpringSystem mSpringSystem = SpringSystem.create();
	public Spring mMePageAppIconScaleSpring = mSpringSystem.createSpring().setSpringConfig(IMAGE_SPRING_CONFIG);

	public MainView mainView;

	public MeSubController thisController;

	public ViewManage viewManage = ViewManage.getInstance();

	public FileHandlers fileHandlers = FileHandlers.getInstance();
	public DisplayImageOptions options;

	public MeSubView(MainView mainView) {
		this.mainView = mainView;
		viewManage.meSubView = this;
	}

	public Bitmap bitmap;

	public void initViews() {
		Resources resources = mainView.thisActivity.getResources();
		bitmap = BitmapFactory.decodeResource(resources, R.drawable.face_man);
		bitmap = MCImageUtils.getCircleBitmap(bitmap, true, 5, Color.WHITE);

		userHeadImageView = (ImageView) mainView.meView.findViewById(R.id.iv_headImage);
		userNickNameView = (TextView) mainView.meView.findViewById(R.id.tv_userNickname);
		userBusinessView = (TextView) mainView.meView.findViewById(R.id.tv_userMainBusiness);

		myBusiness = mainView.meView.findViewById(R.id.businesscard);
		mySetting = mainView.meView.findViewById(R.id.mySetting);

		dynamicListView = (TouchView) mainView.meView.findViewById(R.id.dynamicList);
		moreFriendView = (TouchView) mainView.meView.findViewById(R.id.morefriend);

		mAppIconToNameView = (ImageView) mainView.meView.findViewById(R.id.appIconToName);
		mRootView = mAppIconToNameView;
		options = new DisplayImageOptions.Builder().showImageOnLoading(R.drawable.ic_stub).showImageForEmptyUri(R.drawable.ic_empty).showImageOnFail(R.drawable.ic_error).cacheInMemory(true).cacheOnDisk(true).considerExifParams(true).displayer(new RoundedBitmapDisplayer(60)).build();

		setUserData();
	}

	public void setUserData() {
		parser.check();
		User user = data.userInformation.currentUser;
		if (user != null) {
			fileHandlers.getHeadImage(user.head, this.userHeadImageView, options);
			// this.userHeadImageView.setImageBitmap(bitmap);
			this.userNickNameView.setText(user.nickName);
			this.userBusinessView.setText(user.mainBusiness);
		}
	}
}
