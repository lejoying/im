package com.open.welinks.view;

import android.graphics.Color;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
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

	public FileHandlers fileHandlers = FileHandlers.getInstance();

	public LBSHandlers lbsHandlers = LBSHandlers.getInstance();

	public ImageLoader imageLoader = ImageLoader.getInstance();

	public ViewManage viewManage = ViewManage.getInstance();

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
		rightTopButton.setText(thisActivity.getString(R.string.business_modify_card));
		rightTopButton.setBackgroundResource(R.drawable.textview_bg);
		RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		layoutParams.setMargins(0, dp_5, (int) 0, dp_5);
		layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
		RelativeLayout.LayoutParams rightContainerParams = (LayoutParams) rightContainer.getLayoutParams();
		rightContainerParams.rightMargin = dp_5;
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
			rightTopButton.setText(thisActivity.getString(R.string.business_modify_card));
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
			businessCard.lable = thisActivity.getString(R.string.business_not_label);
			businessCard.creattime = user.createTime;
			businessCard.button_one = thisActivity.getString(R.string.business_modify_mycard);
			businessCard.button_two = "";
			businessCard.button_three = "";
			sexLayout.setVisibility(View.VISIBLE);
			buttonTwo.setVisibility(View.GONE);
			buttonThree.setVisibility(View.GONE);
			qrCodeView.setImageBitmap(MCImageUtils.createQEcodeImage(USERCARDTYPE, user.phone));
		} else if (status.equals(Status.FRIEND)) {
			sexLayout.setVisibility(View.VISIBLE);
			ageLayout.setVisibility(View.VISIBLE);
			rightTopButton.setText(thisActivity.getString(R.string.business_initiate_chat));
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
			businessCard.lable = thisActivity.getString(R.string.business_not_label);
			businessCard.creattime = friend.createTime;
			businessCard.button_one = thisActivity.getString(R.string.business_initiate_chat);
			businessCard.button_two = thisActivity.getString(R.string.business_modify_remarks);
			businessCard.button_three = thisActivity.getString(R.string.business_relieve_relation);
			buttonFour.setVisibility(View.VISIBLE);
			if (user.blackList.contains(friend.phone)) {
				buttonFour.setText(thisActivity.getString(R.string.business_blacklist_remove));
			} else {
				buttonFour.setText(thisActivity.getString(R.string.business_add_to_blacklist));
			}
			qrCodeView.setImageBitmap(MCImageUtils.createQEcodeImage(USERCARDTYPE, friend.phone));
		} else if (status.equals(Status.TEMPFRIEND)) {
			sexLayout.setVisibility(View.VISIBLE);
			ageLayout.setVisibility(View.VISIBLE);
			rightTopButton.setText(thisActivity.getString(R.string.business_add_as_friend));
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
				businessCard.lable = thisActivity.getString(R.string.business_not_label);
				businessCard.creattime = friend.createTime;
				businessCard.button_one = thisActivity.getString(R.string.business_add_as_friend);
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
			rightTopButton.setText(thisActivity.getString(R.string.business_chat_room));
			Group group = thisController.data.relationship.groupsMap.get(thisController.key);
			if (!isGetData)
				thisController.getGroupCard(group.gid + "", "group");
			businessCard.id = group.gid;
			businessCard.icon = group.icon;
			businessCard.nickname = group.name;
			String description = "";
			if (group.description == null || group.description.equals("") || group.description.equals(thisActivity.getString(R.string.business_input_room_info))) {
				description = thisActivity.getString(R.string.business_room_not_business);
			} else {
				description = group.description;
			}
			User user = thisController.data.userInformation.currentUser;
			businessCard.distance = lbsHandlers.pointDistance(user.longitude, user.latitude, group.longitude, group.latitude);
			businessCard.mainBusiness = description;
			businessCard.lable = thisActivity.getString(R.string.business_not_label);
			businessCard.creattime = group.createTime;
			businessCard.button_one = thisActivity.getString(R.string.business_chat_room);
			businessCard.button_two = thisActivity.getString(R.string.business_add_to_groupcircle);
			businessCard.button_three = "";
			buttonThree.setVisibility(View.GONE);
			qrCodeView.setImageBitmap(MCImageUtils.createQEcodeImage(GROUPCARDTYPE, group.gid + ""));
		} else if (status.equals(Status.NOTJOINGROUP)) {
			myShareView.setVisibility(View.GONE);
			sexLayout.setVisibility(View.GONE);
			ageLayout.setVisibility(View.GONE);
			rightTopButton.setText(thisActivity.getString(R.string.business_add_to_room));
			if (data.tempData.tempGroup == null) {
				if (!isGetData)
					thisController.getGroupCard(thisController.key, "group");
			} else {
				if (!isGetData)
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
				businessCard.lable = thisActivity.getString(R.string.business_not_label);
				businessCard.creattime = data.tempData.tempGroup.createTime;
				businessCard.button_one = thisActivity.getString(R.string.business_add_to_room);
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
			rightTopButton.setText(thisActivity.getString(R.string.business_go_community));
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
				businessCard.lable = thisActivity.getString(R.string.business_not_label);
				businessCard.creattime = square.createTime;
				businessCard.button_one = thisActivity.getString(R.string.business_go_community);
				businessCard.button_two = "";
				businessCard.button_three = "";
				buttonOne.setVisibility(View.GONE);
				buttonTwo.setVisibility(View.GONE);
				buttonThree.setVisibility(View.GONE);
				qrCodeView.setImageBitmap(MCImageUtils.createQEcodeImage(USERCARDTYPE, square.gid + ""));
			}
		}
		if (businessCard.icon.equals("Head") || "".equals(businessCard.icon)) {
			imageLoader.displayImage("drawable://" + R.drawable.face_man, thisView.head, viewManage.options45);
		} else {
			fileHandlers.getHeadImage(businessCard.icon, this.head, viewManage.options45);
			// thisController.setHeadImage(businessCard.icon, thisView.head);
		}
		qrCodeView.setScaleType(ScaleType.FIT_CENTER);
		setData(businessCard);
	}

	public void setData(BusinessCard businessCard) {
		// log.e("createTime--------：" + businessCard.creattime);
		if (status.equals(Status.SELF)) {
			backTitleView.setText(thisActivity.getString(R.string.business_my_info));
			businessTitle.setText(thisActivity.getString(R.string.business_personal_statement));
			lableTitle.setText(thisActivity.getString(R.string.business_hobbies));
			creatTimeTitle.setText(thisActivity.getString(R.string.business_regist_time));
		} else if (status.equals(Status.FRIEND) || status.equals(Status.TEMPFRIEND)) {
			backTitleView.setText(thisActivity.getString(R.string.business_personal_info));
			businessTitle.setText(thisActivity.getString(R.string.business_personal_statement));
			lableTitle.setText(thisActivity.getString(R.string.business_hobbies));
			creatTimeTitle.setText(thisActivity.getString(R.string.business_regist_time));
		} else if (status.equals(Status.JOINEDGROUP)) {
			backTitleView.setText(thisActivity.getString(R.string.business_room_info));
			businessTitle.setText(thisActivity.getString(R.string.business_room_description));
			lableTitle.setText(thisActivity.getString(R.string.business_label));
			creatTimeTitle.setText(thisActivity.getString(R.string.business_regist_time));
		} else if (status.equals(Status.NOTJOINGROUP)) {
			backTitleView.setText(thisActivity.getString(R.string.business_room_info));
			businessTitle.setText(thisActivity.getString(R.string.business_room_description));
			lableTitle.setText(thisActivity.getString(R.string.business_label));
			creatTimeTitle.setText(thisActivity.getString(R.string.business_regist_time));
			buttonTwo.setVisibility(View.GONE);
		} else if (status.equals(Status.SQUARE)) {
			backTitleView.setText(thisActivity.getString(R.string.business_community_info));
			businessTitle.setText(thisActivity.getString(R.string.business_room_name));
			lableTitle.setText(thisActivity.getString(R.string.business_label));
			creatTimeTitle.setText(thisActivity.getString(R.string.business_regist_time));
		}

		if (!"".equals(businessCard.sex) && ("male".equals(businessCard.sex) || (thisActivity.getString(R.string.business_male)).equals(businessCard.sex))) {
			sex.setText(thisActivity.getString(R.string.business_male));
			shareTxView.setText(thisActivity.getString(R.string.business_he_share));
		} else {
			sex.setText(thisActivity.getString(R.string.business_female));
			shareTxView.setText(thisActivity.getString(R.string.business_she_share));
		}
		if (businessCard.id == data.userInformation.currentUser.id) {
			shareTxView.setText(thisActivity.getString(R.string.business_my_share));
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
