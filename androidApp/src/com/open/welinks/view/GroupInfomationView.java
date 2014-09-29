package com.open.welinks.view;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.FrameLayout.LayoutParams;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.open.welinks.R;
import com.open.welinks.controller.GroupInfomationController;
import com.open.welinks.model.Data;
import com.open.welinks.model.LBSHandlers;
import com.open.welinks.model.Data.Relationship.Friend;
import com.open.welinks.model.Data.UserInformation.User;
import com.open.welinks.model.FileHandlers;

public class GroupInfomationView {

	public Data data = Data.getInstance();
	public String tag = "GroupInfomationView";

	public Context context;
	public GroupInfomationView thisView;
	public GroupInfomationController thisController;
	public Activity thisActivity;

	public LayoutInflater mInflater;
	public float screenDensity;
	int screenHeight, screenWidth, screenDip;

	public RelativeLayout backView;
	public TextView groupCountView;
	public RelativeLayout memberContainerView;
	public TextView groupNameView;
	public TextView groupName2View;
	public RelativeLayout groupNameLayoutView;

	public RelativeLayout groupBusinessCardView;
	public SeekBar seekBar;

	public RelativeLayout groupMemberControlView;
	public RelativeLayout exit2DeleteGroupView;

	// dialog
	public RelativeLayout dialogContentView;
	public TextView dialogTitleView;
	public EditText dialogEditView;
	public TextView dialogConfirmView;
	public TextView dialogCancleView;

	public View maxView;

	public FileHandlers fileHandlers = FileHandlers.getInstance();

	public DisplayImageOptions options;

	public InputMethodManager inputMethodManager;

	public DisplayMetrics displayMetrics;

	public GroupInfomationView(Activity thisActivity) {
		this.thisActivity = thisActivity;
		this.context = thisActivity;
		this.thisView = this;
	}

	public void initView() {

		displayMetrics = new DisplayMetrics();
		thisActivity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
		options = new DisplayImageOptions.Builder().showImageOnLoading(R.drawable.ic_stub).showImageForEmptyUri(R.drawable.ic_empty).showImageOnFail(R.drawable.ic_error).cacheInMemory(true).cacheOnDisk(true).considerExifParams(true).displayer(new RoundedBitmapDisplayer(52)).build();

		inputMethodManager = (InputMethodManager) thisActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
		mInflater = thisActivity.getLayoutInflater();

		thisActivity.setContentView(R.layout.activity_group_infomation);
		maxView = thisActivity.findViewById(R.id.maxView);
		backView = (RelativeLayout) thisActivity.findViewById(R.id.backView);
		groupCountView = (TextView) thisActivity.findViewById(R.id.backTitleView);
		groupNameView = (TextView) thisActivity.findViewById(R.id.groupName);
		groupName2View = (TextView) thisActivity.findViewById(R.id.groupName2);
		memberContainerView = (RelativeLayout) thisActivity.findViewById(R.id.memberContainer);
		groupNameLayoutView = (RelativeLayout) thisActivity.findViewById(R.id.groupNameLayout);

		groupBusinessCardView = (RelativeLayout) thisActivity.findViewById(R.id.groupBusinessCard);

		seekBar = (SeekBar) thisActivity.findViewById(R.id.ldm_bottom_btn2_ssb);

		groupMemberControlView = (RelativeLayout) thisActivity.findViewById(R.id.groupMemberControl);
		exit2DeleteGroupView = (RelativeLayout) thisActivity.findViewById(R.id.exit2DeleteGroup);

		DisplayMetrics dm = new DisplayMetrics();
		thisActivity.getWindowManager().getDefaultDisplay().getMetrics(dm);
		screenDensity = dm.density;
		screenDip = (int) (40 * screenDensity + 0.5f);
		screenHeight = dm.heightPixels;
		screenWidth = dm.widthPixels;
		baseLeft = (int) (screenWidth - (dp2px(20) * 2) - (dp2px(55) * 4)) / 8;
		vWidth = (int) (screenWidth - (dp2px(20) * 2));
		headSpace = baseLeft * 2;
		head = (int) dp2px(55f);

		dialogContentView = (RelativeLayout) thisActivity.findViewById(R.id.inputDialogContent);
		dialogTitleView = (TextView) thisActivity.findViewById(R.id.title);
		dialogEditView = (EditText) thisActivity.findViewById(R.id.input);
		dialogConfirmView = (TextView) thisActivity.findViewById(R.id.confirm);
		dialogCancleView = (TextView) thisActivity.findViewById(R.id.cancel);

		dialogContentView.setVisibility(View.GONE);

		initSmallBusinessCardDialog();
	}

	public void showGroupMembers() {
		GroupBody groupBody = new GroupBody();
		groupBody.setData();
	}

	public class GroupBody {
		public List<String> friendSequence = new ArrayList<String>();
		public Map<String, FriendBody> friendSequenceMap = new HashMap<String, FriendBody>();

		public List<String> members;
		public Map<String, Friend> friendsMap;

		public GroupBody() {
			members = thisController.currentGroup.members;
			friendsMap = data.relationship.friendsMap;
		}

		public void setData() {
			memberContainerView.removeAllViews();
			this.friendSequence.clear();
			groupCountView.setText("群组信息 ( " + members.size() + "人 )");
			groupNameView.setText(thisController.currentGroup.name);
			groupName2View.setText(thisController.currentGroup.name);
			A: for (int i = 0; i < members.size(); i++) {
				String key = members.get(i);
				Friend friend = friendsMap.get(key);
				FriendBody friendBody = null;
				friendBody = new FriendBody();
				friendBody.initialization();
				friendBody.setData(friend);
				memberContainerView.addView(friendBody.friendView);
				friendBody.position = switchPosition(i);
				RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams((int) dp2px(55f), (int) (78 * screenDensity));
				params.rightMargin = -Integer.MAX_VALUE;

				params.topMargin = friendBody.position.y;
				params.leftMargin = friendBody.position.x;
				friendBody.friendView.setLayoutParams(params);
				// friendBody.friendView.setBackgroundColor(Color.RED);
				friendBody.headImageView.setTag(R.id.tag_class, "head_click");
				friendBody.headImageView.setTag(R.id.tag_first, friend.phone);
				friendBody.headImageView.setOnClickListener(thisController.mOnClickListener);
				if (i > 8)
					break A;
			}
		}

		public void resolveFriendsPositions() {

		}

		public void setFriendsPositions() {

		}
	}

	public class FriendBody {

		public View friendView;
		public ImageView headImageView;
		public TextView nickNameView;

		public Position position;

		public View initialization() {
			this.friendView = mInflater.inflate(R.layout.circles_gridpage_item, null);
			this.headImageView = (ImageView) this.friendView.findViewById(R.id.head_image);
			this.nickNameView = (TextView) this.friendView.findViewById(R.id.nickname);
			return this.friendView;
		}

		public void setData(Friend friend) {
			fileHandlers.getHeadImage(friend.head, this.headImageView, options);
			this.nickNameView.setText(friend.nickName);
		}
	}

	class Position {
		int x = 0;
		int y = 0;
	}

	int baseLeft;
	int headSpace;
	int head;
	int vWidth;

	public float dp2px(float px) {
		float dp = screenDensity * px + 0.5f;
		return dp;
	}

	public Position switchPosition(int i) {
		Position position = new Position();
		int baseX = (int) dp2px(i / 8 * 326);
		if ((i + 1) % 8 == 1) {
			position.y = (int) dp2px(11);
			position.x = (int) (baseLeft + baseX);
		} else if ((i + 1) % 8 == 2) {
			position.y = (int) dp2px(11);
			position.x = (int) (baseLeft + head + headSpace + baseX);
		} else if ((i + 1) % 8 == 3) {
			position.y = (int) dp2px(11);
			position.x = (int) (baseLeft + head + headSpace + head + headSpace + baseX);
		} else if ((i + 1) % 8 == 4) {
			position.y = (int) dp2px(11);
			position.x = (int) (baseLeft + head + headSpace + head + headSpace + head + headSpace + baseX);
		} else if ((i + 1) % 8 == 5) {
			position.y = (int) dp2px(11 + 73 + 27);
			position.x = (int) (baseLeft + baseX);
		} else if ((i + 1) % 8 == 6) {
			position.y = (int) dp2px(11 + 73 + 27);
			position.x = (int) (baseLeft + head + headSpace + baseX);
		} else if ((i + 1) % 8 == 7) {
			position.y = (int) dp2px(11 + 73 + 27);
			position.x = (int) (baseLeft + head + headSpace + head + headSpace + baseX);
		} else if ((i + 1) % 8 == 0) {
			position.y = (int) dp2px(11 + 73 + 27);
			position.x = (int) (baseLeft + head + headSpace + head + headSpace + head + headSpace + baseX);
		}
		return position;
	}

	// small businesscard
	public DisplayImageOptions smallBusinessCardOptions;
	public View userCardMainView;
	public PopupWindow userCardPopWindow;
	public RelativeLayout userBusinessContainer;
	public TextView goInfomationView;
	public TextView goChatView;
	public ImageView userHeadView;
	public TextView userNickNameView;
	public TextView userAgeView;
	public TextView distanceView;
	public TextView lastLoginTimeView;
	public LinearLayout optionTwoView;
	public TextView singleButtonView;
	public TextView cardStatusView;

	@SuppressWarnings("deprecation")
	public void initSmallBusinessCardDialog() {
		userCardMainView = mInflater.inflate(R.layout.account_info_pop, null);
		optionTwoView = (LinearLayout) userCardMainView.findViewById(R.id.optionTwo);
		userNickNameView = (TextView) userCardMainView.findViewById(R.id.userNickName);
		userAgeView = (TextView) userCardMainView.findViewById(R.id.userAge);
		distanceView = (TextView) userCardMainView.findViewById(R.id.userDistance);
		lastLoginTimeView = (TextView) userCardMainView.findViewById(R.id.lastLoginTime);
		userBusinessContainer = (RelativeLayout) userCardMainView.findViewById(R.id.userBusinessView);
		int height = (int) (displayMetrics.heightPixels * 0.5f - 50 * displayMetrics.density) + getStatusBarHeight(thisActivity);
		userBusinessContainer.getLayoutParams().height = height;
		goInfomationView = (TextView) userCardMainView.findViewById(R.id.goInfomation);
		goChatView = (TextView) userCardMainView.findViewById(R.id.goChat);
		singleButtonView = (TextView) userCardMainView.findViewById(R.id.singleButton);
		cardStatusView = (TextView) userCardMainView.findViewById(R.id.cardStatus);
		// singleButtonView.setVisibility(View.GONE);
		userHeadView = (ImageView) userCardMainView.findViewById(R.id.userHead);
		userHeadView.getLayoutParams().height = height;
		userCardPopWindow = new PopupWindow(userCardMainView, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT, true);
		userCardPopWindow.setBackgroundDrawable(new BitmapDrawable());
		smallBusinessCardOptions = new DisplayImageOptions.Builder().showImageOnLoading(R.drawable.ic_stub).showImageForEmptyUri(R.drawable.ic_empty).showImageOnFail(R.drawable.ic_error).cacheInMemory(true).cacheOnDisk(true).considerExifParams(true).displayer(new RoundedBitmapDisplayer(10)).build();
	}

	LBSHandlers lbsHandlers = LBSHandlers.getInstance();

	public void setSmallBusinessCardContent(String phone, String head, String nickName, String age, String longitude, String latitude) {
		User user = data.userInformation.currentUser;
		goInfomationView.setTag(R.id.tag_first, phone);
		goChatView.setTag(R.id.tag_first, phone);
		singleButtonView.setTag(R.id.tag_first, phone);
		fileHandlers.getHeadImage(head, userHeadView, smallBusinessCardOptions);
		userNickNameView.setText(nickName);
		userAgeView.setText(age + "");
		distanceView.setText(lbsHandlers.pointDistance(user.longitude, user.latitude, longitude, latitude) + "km");
		lastLoginTimeView.setText("0小时前");
		if (user.phone.equals(phone)) {
			optionTwoView.setVisibility(View.GONE);
			singleButtonView.setVisibility(View.VISIBLE);
			cardStatusView.setText("自己");
			singleButtonView.setTag(R.id.tag_second, "point");
		} else {
			if (data.relationship.friends.contains(phone)) {
				optionTwoView.setVisibility(View.VISIBLE);
				singleButtonView.setVisibility(View.GONE);
				cardStatusView.setText("已是好友");
				goInfomationView.setTag(R.id.tag_second, "point");
				goInfomationView.setTag(R.id.tag_third, false);
			} else {
				optionTwoView.setVisibility(View.GONE);
				singleButtonView.setVisibility(View.VISIBLE);
				cardStatusView.setText("不是好友");
				singleButtonView.setTag(R.id.tag_second, "point");
				singleButtonView.setTag(R.id.tag_third, true);
				data.tempData.tempFriend = data.relationship.friendsMap.get(phone);
			}
		}
	}

	public void showUserCardDialogView() {
		if (userCardPopWindow != null && !userCardPopWindow.isShowing()) {
			userCardPopWindow.showAtLocation(maxView, Gravity.CENTER, 0, 0);
		}
	}

	public void dismissUserCardDialogView() {
		if (userCardPopWindow != null && userCardPopWindow.isShowing()) {
			userCardPopWindow.dismiss();
		}
	}

	public static int getStatusBarHeight(Context context) {
		Class<?> c = null;
		Object obj = null;
		Field field = null;
		int x = 0, statusBarHeight = 0;
		try {
			c = Class.forName("com.android.internal.R$dimen");
			obj = c.newInstance();
			field = c.getField("status_bar_height");
			x = Integer.parseInt(field.get(obj).toString());
			statusBarHeight = context.getResources().getDimensionPixelSize(x);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		return statusBarHeight;
	}
}
