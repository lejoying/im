package com.open.welinks.view;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.open.welinks.BusinessCardActivity;
import com.open.welinks.R;
import com.open.welinks.controller.BusinessCardController;
import com.open.welinks.model.Data.Relationship.Friend;
import com.open.welinks.model.Data.Relationship.Group;
import com.open.welinks.model.Data.UserInformation.User;

public class BusinessCardView {
	public BusinessCardController thisController;
	public BusinessCardView thisView;
	public BusinessCardActivity thisActivity;

	public LayoutInflater mInflater;
	public RelativeLayout backview;
	public LinearLayout content;
	public TextView spacing_one, spacing_two, spacing_three, title, business_title, lable_title, creattime_title, nickname, id, business, lable, creattime;
	public ImageView head, tdcode;
	public Button button_one, button_two, button_three;

	public BusinessCard businessCard;

	public enum Status {
		SELF, FRIEND, TEMPFRIEND, JOINEDGROUP, NOTJOINGROUP, SQUARE
	}

	public Status status;

	public BusinessCardView(BusinessCardActivity activity) {
		thisActivity = activity;
		thisView = this;
		initView();
	}

	public void initView() {
		mInflater = thisActivity.getLayoutInflater();
		thisActivity.setContentView(R.layout.activity_businesscard);

		backview = (RelativeLayout) thisActivity.findViewById(R.id.backview);
		content = (LinearLayout) thisActivity.findViewById(R.id.content);
		spacing_one = (TextView) thisActivity.findViewById(R.id.spacing_one);
		spacing_two = (TextView) thisActivity.findViewById(R.id.spacing_two);
		spacing_three = (TextView) thisActivity.findViewById(R.id.spacing_three);
		title = (TextView) thisActivity.findViewById(R.id.title);
		business_title = (TextView) thisActivity.findViewById(R.id.business_title);
		lable_title = (TextView) thisActivity.findViewById(R.id.lable_title);
		creattime_title = (TextView) thisActivity.findViewById(R.id.creattime_title);
		id = (TextView) thisActivity.findViewById(R.id.id);
		nickname = (TextView) thisActivity.findViewById(R.id.nickname);
		business = (TextView) thisActivity.findViewById(R.id.business);
		lable = (TextView) thisActivity.findViewById(R.id.lable);
		creattime = (TextView) thisActivity.findViewById(R.id.creattime);
		head = (ImageView) thisActivity.findViewById(R.id.head);
		tdcode = (ImageView) thisActivity.findViewById(R.id.tdcode);
		button_one = (Button) thisActivity.findViewById(R.id.button_one);
		button_two = (Button) thisActivity.findViewById(R.id.button_two);
		button_three = (Button) thisActivity.findViewById(R.id.button_three);
	}

	public void fillData() {
		businessCard = new BusinessCard();
		if (status.equals(Status.SELF)) {
			User user = thisController.data.userInformation.currentUser;
			businessCard.id = user.id;
			businessCard.icon = user.head;
			businessCard.nickname = user.nickName;
			businessCard.mainBusiness = user.mainBusiness;
			businessCard.lable = "暂无标签";
			businessCard.creattime = "2014年 9月 1日";
			businessCard.button_one = "修改我的名片";
			businessCard.button_two = "";
			businessCard.button_three = "";
			button_two.setVisibility(View.GONE);
			button_three.setVisibility(View.GONE);
		} else if (status.equals(Status.FRIEND)) {
			Friend friend = thisController.data.relationship.friendsMap.get(thisController.key);
			businessCard.id = friend.id;
			businessCard.icon = friend.head;
			String nickName = "";
			if (friend.alias.equals("")) {
				nickName = friend.nickName;
			} else {
				nickName = friend.alias + "(" + friend.nickName + ")";
			}
			businessCard.nickname = nickName;
			businessCard.mainBusiness = friend.mainBusiness;
			businessCard.lable = "暂无标签";
			businessCard.creattime = "2014年 9月 1日";
			businessCard.button_one = "发起聊天";
			businessCard.button_two = "修改备注";
			businessCard.button_three = "解除好友关系";

		} else if (status.equals(Status.JOINEDGROUP)) {
			Group group = thisController.data.relationship.groupsMap.get(thisController.key);
			businessCard.id = group.gid;
			businessCard.icon = group.icon;
			businessCard.nickname = group.name;
			String description = "";
			if (group.description == null || group.description.equals("") || group.description.equals("请输入群组描述信息")) {
				description = "此群组暂无业务";
			} else {
				description = group.description;
			}
			businessCard.mainBusiness = description;
			businessCard.lable = "暂无标签";
			businessCard.creattime = "2014年 9月 1日";
			businessCard.button_one = "发起聊天";
			businessCard.button_two = "修改群名片";
			businessCard.button_three = "";
			button_three.setVisibility(View.GONE);

		} else if (status.equals(Status.TEMPFRIEND)) {
			businessCard.id = 0;
			businessCard.icon = "";
			businessCard.nickname = "";
			businessCard.mainBusiness = "";
			businessCard.lable = "暂无标签";
			businessCard.creattime = "2014年 9月 1日";
			businessCard.button_one = "加为好友";
			businessCard.button_two = "";
			businessCard.button_three = "";
			button_two.setVisibility(View.GONE);
			button_three.setVisibility(View.GONE);

		} else if (status.equals(Status.NOTJOINGROUP)) {
			businessCard.id = 0;
			businessCard.icon = "";
			businessCard.nickname = "";
			businessCard.mainBusiness = "";
			businessCard.lable = "暂无标签";
			businessCard.creattime = "2014年 9月 1日";
			businessCard.button_one = "加入群组";
			businessCard.button_two = "";
			businessCard.button_three = "";
			button_two.setVisibility(View.GONE);
			button_three.setVisibility(View.GONE);

		} else if (status.equals(Status.SQUARE)) {
			businessCard.id = Integer.valueOf(thisController.key);
			businessCard.icon = "";
			businessCard.nickname = "";
			businessCard.mainBusiness = "暂无描述";
			businessCard.lable = "暂无标签";
			businessCard.creattime = "2014年 9月 1日";
			businessCard.button_one = "";
			businessCard.button_two = "";
			businessCard.button_three = "";
			button_one.setVisibility(View.GONE);
			button_two.setVisibility(View.GONE);
			button_three.setVisibility(View.GONE);

		}
		setData(businessCard);
	}

	public void setData(BusinessCard businessCard) {
		if (status.equals(Status.SELF)) {
			title.setText("我的详情");
			business_title.setText("个人宣言:");
			lable_title.setText("爱好:");
			creattime_title.setText("注册时间:");
		} else if (status.equals(Status.FRIEND) || status.equals(Status.TEMPFRIEND)) {
			title.setText("个人详情");
			business_title.setText("个人宣言:");
			lable_title.setText("爱好:");
			creattime_title.setText("注册时间:");
		} else if (status.equals(Status.JOINEDGROUP) || status.equals(Status.NOTJOINGROUP)) {
			title.setText("群组详情");
			business_title.setText("主要业务:");
			lable_title.setText("标签:");
			creattime_title.setText("创建时间:");
		} else if (status.equals(Status.SQUARE)) {
			title.setText("广场详情");
			business_title.setText("主要业务:");
			lable_title.setText("标签:");
			creattime_title.setText("创建时间:");
		}
		nickname.setText(businessCard.nickname);
		id.setText(businessCard.id);
		business.setText(businessCard.mainBusiness);
		lable.setText(businessCard.lable);
		creattime.setText(businessCard.creattime);
		button_one.setText(businessCard.button_one);
		button_two.setText(businessCard.button_three);
		button_three.setText(businessCard.button_three);
	}

	public class BusinessCard {
		public int id = 0;
		public String icon = "";
		public String nickname = "";
		public String mainBusiness = "";
		public String lable = "";
		public String creattime = "";
		public String button_one = "";
		public String button_two = "";
		public String button_three = "";
	}
}
