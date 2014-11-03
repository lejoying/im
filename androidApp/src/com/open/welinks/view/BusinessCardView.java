package com.open.welinks.view;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.open.lib.MyLog;
import com.open.welinks.BusinessCardActivity;
import com.open.welinks.R;
import com.open.welinks.controller.BusinessCardController;
import com.open.welinks.model.Data;
import com.open.welinks.model.Data.Relationship.Friend;
import com.open.welinks.model.Data.Relationship.Group;
import com.open.welinks.model.Data.UserInformation.User;
import com.open.welinks.model.FileHandlers;
import com.open.welinks.model.LBSHandlers;
import com.open.welinks.utils.DateUtil;
import com.open.welinks.utils.MCImageUtils;

public class BusinessCardView {

	public String tag = "BusinessCardView";
	public MyLog log = new MyLog(tag, true);

	public Data data = Data.getInstance();

	public String GROUPCARDTYPE = "groupcard";
	public String USERCARDTYPE = "usercard";

	public BusinessCardController thisController;
	public BusinessCardView thisView;
	public BusinessCardActivity thisActivity;

	public LayoutInflater mInflater;

	public RelativeLayout backView;
	public LinearLayout content, infomationLayout, sexLayout, ageLayout;
	public TextView spacingOne, spacingTwo, spacingThree, backTitleView, businessTitle, lableTitle, creatTimeTitle, nickName, id, business, lable, creatTime, sex, age, distance;
	public ImageView head, qrCodeView;
	public Button buttonOne, buttonTwo, buttonThree, buttonFour;
	public RelativeLayout rightContainer;
	public TextView rightTopButton;

	public View myShareView;
	public TextView shareTxView;

	public BusinessCard businessCard;

	public FileHandlers fileHandlers = FileHandlers.getInstance();
	public DisplayImageOptions options;

	public LBSHandlers lbsHandlers = LBSHandlers.getInstance();

	public DisplayMetrics displayMetrics;

	public Status status = Status.SELF;

	public enum Status {
		SELF, FRIEND, TEMPFRIEND, JOINEDGROUP, NOTJOINGROUP, SQUARE
	}

	public BusinessCardView(BusinessCardActivity activity) {
		thisActivity = activity;
		thisView = this;
	}

	public void initView() {
		mInflater = thisActivity.getLayoutInflater();

		displayMetrics = new DisplayMetrics();
		thisActivity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
		options = new DisplayImageOptions.Builder().showImageOnLoading(R.drawable.ic_stub).showImageForEmptyUri(R.drawable.ic_empty).showImageOnFail(R.drawable.ic_error).cacheInMemory(true).cacheOnDisk(true).considerExifParams(true).displayer(new RoundedBitmapDisplayer(45)).build();

		thisActivity.setContentView(R.layout.activity_businesscard);
		backView = (RelativeLayout) thisActivity.findViewById(R.id.backView);
		backTitleView = (TextView) thisActivity.findViewById(R.id.backTitleView);
		rightContainer = (RelativeLayout) thisActivity.findViewById(R.id.rightContainer);
		content = (LinearLayout) thisActivity.findViewById(R.id.content);
		infomationLayout = (LinearLayout) thisActivity.findViewById(R.id.infomation_layout);
		sexLayout = (LinearLayout) thisActivity.findViewById(R.id.sex_layout);
		ageLayout = (LinearLayout) thisActivity.findViewById(R.id.age_layout);
		spacingOne = (TextView) thisActivity.findViewById(R.id.spacing_one);
		spacingTwo = (TextView) thisActivity.findViewById(R.id.spacing_two);
		spacingThree = (TextView) thisActivity.findViewById(R.id.spacing_three);
		businessTitle = (TextView) thisActivity.findViewById(R.id.business_title);
		lableTitle = (TextView) thisActivity.findViewById(R.id.lable_title);
		creatTimeTitle = (TextView) thisActivity.findViewById(R.id.creattime_title);
		id = (TextView) thisActivity.findViewById(R.id.id);
		nickName = (TextView) thisActivity.findViewById(R.id.nickname);
		business = (TextView) thisActivity.findViewById(R.id.business);
		lable = (TextView) thisActivity.findViewById(R.id.lable);
		creatTime = (TextView) thisActivity.findViewById(R.id.creattime);
		sex = (TextView) thisActivity.findViewById(R.id.sex);
		age = (TextView) thisActivity.findViewById(R.id.age);
		head = (ImageView) thisActivity.findViewById(R.id.head);
		distance = (TextView) thisActivity.findViewById(R.id.distance);

		myShareView = thisActivity.findViewById(R.id.myShare);
		shareTxView = (TextView) thisActivity.findViewById(R.id.shareTx);

		qrCodeView = (ImageView) thisActivity.findViewById(R.id.tdcode);
		buttonOne = (Button) thisActivity.findViewById(R.id.button_one);
		buttonTwo = (Button) thisActivity.findViewById(R.id.button_two);
		buttonThree = (Button) thisActivity.findViewById(R.id.button_three);
		buttonFour = (Button) thisActivity.findViewById(R.id.button_four);

		rightTopButton = new TextView(thisActivity);
		int dp_5 = (int) (5 * displayMetrics.density);
		rightTopButton.setGravity(Gravity.CENTER);
		rightTopButton.setTextColor(Color.WHITE);
		rightTopButton.setPadding(dp_5 * 2, dp_5, dp_5 * 2, dp_5);
		rightTopButton.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
		rightTopButton.setText("修改资料");
		rightTopButton.setBackgroundResource(R.drawable.textview_bg);
		RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		layoutParams.setMargins(0, dp_5, (int) 0, dp_5);
		layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
		this.rightContainer.addView(rightTopButton, layoutParams);
		isGetData = false;
		fillData();
	}

	public boolean isGetData = false;

	public void fillData() {
		businessCard = new BusinessCard();
		buttonFour.setVisibility(View.GONE);
		if (status.equals(Status.SELF)) {
			sexLayout.setVisibility(View.VISIBLE);
			ageLayout.setVisibility(View.VISIBLE);
			rightTopButton.setText("修改资料");
			User user = thisController.data.userInformation.currentUser;
			if (!isGetData)
				thisController.getFriendCard(user.phone);
			businessCard.id = user.id;
			businessCard.icon = user.head;
			businessCard.sex = user.sex;
			businessCard.distance = "0";
			businessCard.nickname = user.nickName;
			businessCard.mainBusiness = user.mainBusiness;
			businessCard.sex = user.sex;
			businessCard.age = user.age;
			businessCard.lable = "暂无标签";
			businessCard.creattime = user.createTime;
			businessCard.button_one = "修改我的名片";
			businessCard.button_two = "";
			businessCard.button_three = "";
			sexLayout.setVisibility(View.VISIBLE);
			buttonTwo.setVisibility(View.GONE);
			buttonThree.setVisibility(View.GONE);
			qrCodeView.setImageBitmap(MCImageUtils.createQEcodeImage(USERCARDTYPE, user.phone));
		} else if (status.equals(Status.FRIEND)) {
			sexLayout.setVisibility(View.VISIBLE);
			ageLayout.setVisibility(View.VISIBLE);
			rightTopButton.setText("发起聊天");
			Friend friend = thisController.data.relationship.friendsMap.get(thisController.key);
			if (!isGetData)
				thisController.getFriendCard(friend.phone);
			businessCard.id = friend.id;
			businessCard.icon = friend.head;
			businessCard.sex = friend.sex;
			businessCard.age = friend.age + "";
			String nickName = "";
			if (friend.alias == null || "".equals(friend.alias)) {
				nickName = friend.nickName;
			} else {
				nickName = friend.alias + "(" + friend.nickName + ")";
			}
			User user = thisController.data.userInformation.currentUser;
			businessCard.distance = lbsHandlers.pointDistance(user.longitude, user.latitude, friend.longitude, friend.latitude);
			businessCard.nickname = nickName;
			businessCard.mainBusiness = friend.mainBusiness;
			businessCard.lable = "暂无标签";
			businessCard.creattime = friend.createTime;
			businessCard.button_one = "发起聊天";
			businessCard.button_two = "修改备注";
			businessCard.button_three = "解除好友关系";
			buttonFour.setVisibility(View.VISIBLE);
			if (user.blackList.contains(friend.phone)) {
				buttonFour.setText("从黑名单移除");
			} else {
				buttonFour.setText("添加到黑名单");
			}
			qrCodeView.setImageBitmap(MCImageUtils.createQEcodeImage(USERCARDTYPE, friend.phone));
		} else if (status.equals(Status.TEMPFRIEND)) {
			sexLayout.setVisibility(View.VISIBLE);
			ageLayout.setVisibility(View.VISIBLE);
			rightTopButton.setText("加为好友");
			Friend friend = data.tempData.tempFriend;
			if (friend == null) {
				thisController.getFriendCard(thisController.key);
			}
			if (data.relationship.friendsMap.get(thisController.key) != null) {
				friend = data.relationship.friendsMap.get(thisController.key);
				log.e("temp phone:" + friend.phone);
				if (!isGetData)
					thisController.getFriendCard(friend.phone);
				businessCard.id = friend.id;
				businessCard.icon = friend.head;
				businessCard.sex = friend.sex;
				businessCard.age = friend.age + "";
				User user = thisController.data.userInformation.currentUser;
				businessCard.distance = lbsHandlers.pointDistance(user.longitude, user.latitude, friend.longitude, friend.latitude);
				businessCard.nickname = friend.nickName;
				businessCard.mainBusiness = friend.mainBusiness;
				businessCard.lable = "暂无标签";
				businessCard.creattime = friend.createTime;
				businessCard.button_one = "加为好友";
				businessCard.button_two = "";
				businessCard.button_three = "";
				buttonTwo.setVisibility(View.GONE);
				buttonThree.setVisibility(View.GONE);
				qrCodeView.setImageBitmap(MCImageUtils.createQEcodeImage(USERCARDTYPE, friend.phone));
			}
		} else if (status.equals(Status.JOINEDGROUP)) {
			myShareView.setVisibility(View.GONE);
			sexLayout.setVisibility(View.GONE);
			ageLayout.setVisibility(View.GONE);
			rightTopButton.setText("发起群聊");
			Group group = thisController.data.relationship.groupsMap.get(thisController.key);
			if (!isGetData)
				thisController.getGroupCard(group.gid + "", "group");
			businessCard.id = group.gid;
			businessCard.icon = group.icon;
			businessCard.nickname = group.name;
			String description = "";
			if (group.description == null || group.description.equals("") || group.description.equals("请输入群组描述信息")) {
				description = "此群组暂无业务";
			} else {
				description = group.description;
			}
			User user = thisController.data.userInformation.currentUser;
			businessCard.distance = lbsHandlers.pointDistance(user.longitude, user.latitude, group.longitude, group.latitude);
			businessCard.mainBusiness = description;
			businessCard.lable = "暂无标签";
			businessCard.creattime = group.createTime;
			businessCard.button_one = "发起聊天";
			businessCard.button_two = "修改群名片";
			businessCard.button_three = "";
			buttonThree.setVisibility(View.GONE);
			qrCodeView.setImageBitmap(MCImageUtils.createQEcodeImage(GROUPCARDTYPE, group.gid + ""));
		} else if (status.equals(Status.NOTJOINGROUP)) {
			myShareView.setVisibility(View.GONE);
			sexLayout.setVisibility(View.GONE);
			ageLayout.setVisibility(View.GONE);
			rightTopButton.setText("加入群组");
			if (data.tempData.tempGroup == null) {
				thisController.getGroupCard(thisController.key, "group");
			}
			if (data.relationship.groupsMap.get(thisController.key) != null) {
				data.tempData.tempGroup = data.relationship.groupsMap.get(thisController.key);
				businessCard.id = data.tempData.tempGroup.gid;
				if (!isGetData)
					thisController.getGroupCard(data.tempData.tempGroup.gid + "", "group");
				businessCard.icon = data.tempData.tempGroup.icon;
				businessCard.nickname = data.tempData.tempGroup.name;
				businessCard.mainBusiness = data.tempData.tempGroup.description;
				User user = thisController.data.userInformation.currentUser;
				businessCard.distance = lbsHandlers.pointDistance(user.longitude, user.latitude, data.tempData.tempGroup.longitude, data.tempData.tempGroup.latitude);
				businessCard.lable = "暂无标签";
				businessCard.creattime = data.tempData.tempGroup.createTime;
				businessCard.button_one = "加入群组";
				businessCard.button_two = "";
				businessCard.button_three = "";
				buttonTwo.setVisibility(View.GONE);
				buttonThree.setVisibility(View.GONE);
				qrCodeView.setImageBitmap(MCImageUtils.createQEcodeImage(USERCARDTYPE, data.tempData.tempGroup.gid + ""));
			}
		} else if (status.equals(Status.SQUARE)) {
			myShareView.setVisibility(View.GONE);
			sexLayout.setVisibility(View.GONE);
			ageLayout.setVisibility(View.GONE);
			rightTopButton.setText("进入广场");
			rightTopButton.setVisibility(View.GONE);
			Group square = data.relationship.groupsMap.get(thisController.key);
			if (square == null) {
				thisController.getGroupCard(thisController.key, "community");
			} else {
				businessCard.id = square.gid;
				if (!isGetData)
					thisController.getGroupCard(square.gid + "", "community");
				businessCard.icon = square.icon;
				businessCard.nickname = square.name;
				businessCard.mainBusiness = square.description;
				User user = thisController.data.userInformation.currentUser;
				businessCard.distance = lbsHandlers.pointDistance(user.longitude, user.latitude, square.longitude, square.latitude);
				businessCard.lable = "暂无标签";
				businessCard.creattime = square.createTime;
				businessCard.button_one = "进入广场";
				businessCard.button_two = "";
				businessCard.button_three = "";
				buttonOne.setVisibility(View.GONE);
				buttonTwo.setVisibility(View.GONE);
				buttonThree.setVisibility(View.GONE);
				qrCodeView.setImageBitmap(MCImageUtils.createQEcodeImage(USERCARDTYPE, square.gid + ""));
				// rightTopButton.setText("修改资料");
				// businessCard.id = Integer.valueOf(thisController.key);
				// businessCard.icon = "";
				// businessCard.distance = "0";
				// businessCard.nickname = "";
				// businessCard.mainBusiness = "暂无描述";
				// businessCard.lable = "暂无标签";
				// businessCard.creattime = "0";
				// businessCard.button_one = "";
				// businessCard.button_two = "";
				// businessCard.button_three = "";
				// buttonOne.setVisibility(View.GONE);
				// buttonTwo.setVisibility(View.GONE);
				// buttonThree.setVisibility(View.GONE);
			}
		}
		if (businessCard.icon.equals("Head") || "".equals(businessCard.icon)) {
			Bitmap bitmap = MCImageUtils.getCircleBitmap(BitmapFactory.decodeResource(thisActivity.getResources(), R.drawable.face_man), true, 5, Color.WHITE);
			thisView.head.setImageBitmap(bitmap);
		} else {
			fileHandlers.getHeadImage(businessCard.icon, this.head, options);
			// thisController.setHeadImage(businessCard.icon, thisView.head);
		}
		qrCodeView.setScaleType(ScaleType.FIT_CENTER);
		setData(businessCard);
	}

	public void setData(BusinessCard businessCard) {
		// log.e("createTime--------：" + businessCard.creattime);
		if (status.equals(Status.SELF)) {
			backTitleView.setText("我的详情");
			businessTitle.setText("个人宣言：");
			lableTitle.setText("爱好：");
			creatTimeTitle.setText("注册时间：");
		} else if (status.equals(Status.FRIEND) || status.equals(Status.TEMPFRIEND)) {
			backTitleView.setText("个人详情");
			businessTitle.setText("个人宣言：");
			lableTitle.setText("爱好：");
			creatTimeTitle.setText("注册时间：");
		} else if (status.equals(Status.JOINEDGROUP) || status.equals(Status.NOTJOINGROUP)) {
			backTitleView.setText("群组详情");
			businessTitle.setText("名称：");
			lableTitle.setText("标签：");
			creatTimeTitle.setText("创建时间：");
			buttonTwo.setVisibility(View.GONE);
		} else if (status.equals(Status.SQUARE)) {
			backTitleView.setText("广场详情");
			businessTitle.setText("名称：");
			lableTitle.setText("标签：");
			creatTimeTitle.setText("创建时间：");
		}

		if (!"".equals(businessCard.sex) && ("male".equals(businessCard.sex) || "男".equals(businessCard.sex))) {
			sex.setText("男");
			shareTxView.setText("他的分享");
		} else {
			sex.setText("女");
			shareTxView.setText("她的分享");
		}
		if (businessCard.id == data.userInformation.currentUser.id) {
			shareTxView.setText("我的分享");
		}
		age.setText(businessCard.age);
		distance.setText(businessCard.distance + "km");
		nickName.setText(businessCard.nickname);
		id.setText(String.valueOf(businessCard.id));
		business.setText(businessCard.mainBusiness);
		lable.setText(businessCard.lable);
		creatTime.setText(DateUtil.formatYearMonthDay2(businessCard.creattime));
		buttonOne.setText(businessCard.button_one);
		buttonTwo.setText(businessCard.button_two);
		buttonThree.setText(businessCard.button_three);
	}

	public class BusinessCard {
		public int id = 0;
		public String icon = "";
		public String nickname = "";
		public String mainBusiness = "";
		public String sex = "";
		public String age = "";
		public String distance;
		public String lable = "";
		public String creattime = "";
		public String button_one = "";
		public String button_two = "";
		public String button_three = "";
	}
}
