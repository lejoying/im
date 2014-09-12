package com.open.welinks.view;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.open.welinks.R;
import com.open.welinks.controller.GroupInfomationController;
import com.open.welinks.model.Data;
import com.open.welinks.model.Data.Relationship.Friend;
import com.open.welinks.utils.MCImageUtils;

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

	public Bitmap defaultBitmapHead;
	public InputMethodManager inputMethodManager;

	public GroupInfomationView(Activity thisActivity) {
		this.thisActivity = thisActivity;
		this.context = thisActivity;
		this.thisView = this;
	}

	public void initView() {

		inputMethodManager = (InputMethodManager) thisActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
		thisActivity.setContentView(R.layout.activity_group_infomation);

		mInflater = thisActivity.getLayoutInflater();

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

		Resources resources = thisActivity.getResources();
		defaultBitmapHead = BitmapFactory.decodeResource(resources, R.drawable.face_man);
		defaultBitmapHead = MCImageUtils.getCircleBitmap(defaultBitmapHead, true, 5, Color.WHITE);

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
			this.headImageView.setImageBitmap(defaultBitmapHead);
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
}
