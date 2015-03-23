package com.open.welinks.view;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.rebound.BaseSpringSystem;
import com.facebook.rebound.SimpleSpringListener;
import com.facebook.rebound.Spring;
import com.facebook.rebound.SpringConfig;
import com.facebook.rebound.SpringSystem;
import com.open.lib.MyLog;
import com.open.lib.TouchView;
import com.open.lib.viewbody.ListBody1;
import com.open.lib.viewbody.ListBody1.MyListItemBody;
import com.open.welinks.R;
import com.open.welinks.controller.FriendsSubController;
import com.open.welinks.customView.SmallBusinessCardPopView;
import com.open.welinks.model.Data;
import com.open.welinks.model.Data.Relationship.Circle;
import com.open.welinks.model.Data.Relationship.Friend;
import com.open.welinks.model.Parser;
import com.open.welinks.model.TaskManageHolder;

public class FriendsSubView {

	public Data data = Data.getInstance();
	public Parser parser = Parser.getInstance();

	public TaskManageHolder taskManageHolder = TaskManageHolder.getInstance();

	public String tag = "FriendsSubView";

	public MyLog log = new MyLog(tag, true);

	public DisplayMetrics displayMetrics;

	public TouchView friendsView;

	public ListBody1 friendListBody;

	public List<String> circles;
	public Map<String, Circle> circlesMap;
	public Map<String, Friend> friendsMap;

	public MainView mainView;

	public FriendsSubView(MainView mainView) {
		this.mainView = mainView;
		taskManageHolder.viewManage.friendsSubView = this;
	}

	public void initData() {
	}

	public SmallBusinessCardPopView businessCardPopView;

	public void initViews() {

		this.friendsView = mainView.friendsView;
		this.displayMetrics = mainView.displayMetrics;
		this.mInflater = mainView.thisActivity.getLayoutInflater();

		friendsView = (TouchView) mainView.friendsView.findViewById(R.id.friendsContainer);
		friendListBody = new ListBody1();
		friendListBody.initialize(displayMetrics, friendsView);

		businessCardPopView = new SmallBusinessCardPopView(mainView.thisActivity, mainView.main_container);
	}

	public void showCircles() {
		data = parser.check();
		if (data.relationship == null || data.relationship.circles == null || data.relationship.circlesMap == null) {
			return;
		}
		circles = data.relationship.circles;
		circlesMap = data.relationship.circlesMap;
		friendsMap = data.relationship.friendsMap;

		this.friendListBody.containerView.removeAllViews();
		this.friendListBody.height = 0;
		// this.friendListBody.y = 0;

		this.friendListBody.listItemsSequence.clear();

		for (int i = 0; i < circles.size(); i++) {
			Circle circle = circlesMap.get(circles.get(i));
			// TODO why circle is null from user infomation update event
			if (circle == null) {
				continue;
			}
			String keyName = "circle#" + circle.rid;
			CircleBody circleBody = (CircleBody) this.friendListBody.listItemBodiesMap.get(keyName);
			boolean flag = true;
			if (circleBody == null) {
				flag = false;
				circleBody = new CircleBody(this.friendListBody);
				circleBody.initialize();
				this.friendListBody.listItemBodiesMap.put(keyName, circleBody);
			}
			circleBody.setContent(circle);

			if (this.friendListBody.listItemsSequence.contains(keyName)) {
				continue;
			}
			this.friendListBody.listItemsSequence.add(keyName);

			TouchView.LayoutParams layoutParams = new TouchView.LayoutParams((int) (displayMetrics.widthPixels - displayMetrics.density * 20), (int) (circleBody.itemHeight - 10 * displayMetrics.density));
			this.friendListBody.containerView.addView(circleBody.cardView, layoutParams);
			circleBody.y = this.friendListBody.height;
			circleBody.cardView.setY(circleBody.y);
			if (flag) {
				circleBody.cardView.setX(10 * displayMetrics.density + 0.5f);
			} else {
				circleBody.cardView.setX(0);
			}

			this.friendListBody.height = this.friendListBody.height + circleBody.itemHeight;

			log.v(tag, "this.friendListBody.height: " + this.friendListBody.height + "    circleBody.y:  " + circleBody.y);
		}

		this.friendListBody.containerHeight = (int) (this.displayMetrics.heightPixels - 38 - displayMetrics.density * 88);

		if (this.friendListBody.height < this.friendListBody.containerHeight) {
			this.friendListBody.y = 0;
		}
		this.friendListBody.setChildrenPosition();
	}

	public class CircleBody extends MyListItemBody {

		CircleBody(ListBody1 listBody) {
			listBody.super();
		}

		public List<String> friendsSequence = new ArrayList<String>();
		public Map<String, FriendBody> friendBodiesMap = new HashMap<String, FriendBody>();

		public TouchView cardView = null;
		public TextView leftTopText = null;
		public TouchView leftTopTextButton = null;
		public TouchView gripView = null;
		public TouchView contaner = null;
		public ImageView gripCardBackground = null;

		public int lineCount = 0;

		public View initialize() {
			this.cardView = (TouchView) mainView.mInflater.inflate(R.layout.view_control_circle_card, null);
			this.leftTopText = (TextView) this.cardView.findViewById(R.id.leftTopText);
			this.gripView = (TouchView) this.cardView.findViewById(R.id.grip);
			this.contaner = (TouchView) this.cardView.findViewById(R.id.contaner);
			this.leftTopTextButton = (TouchView) this.cardView.findViewById(R.id.leftTopTextButton);

			this.gripCardBackground = (ImageView) this.cardView.findViewById(R.id.grip_card_background);

			this.leftTopTextButton.setOnTouchListener(thisController.onTouchListener);
			// this.leftTopText.setOnLongClickListener(mainView.thisController.onLongClickListener);

			this.gripView.setOnTouchListener(thisController.onTouchListener);

			this.contaner.setOnTouchListener(thisController.onTouchListener);

			itemWidth = mainView.displayMetrics.widthPixels - 20 * mainView.displayMetrics.density;
			itemHeight = 260 * displayMetrics.density;

			super.initialize(cardView);
			return cardView;
		}

		public void setContent(Circle circle) {
			this.leftTopText.setText(circle.name);

			this.leftTopTextButton.setTag(R.id.tag_first, circle);
			this.leftTopTextButton.setTag(R.id.tag_class, "card_title");

			this.gripView.setTag(R.id.tag_first, circle);
			this.gripView.setTag(R.id.tag_class, "card_grip");

			this.contaner.setTag(R.id.tag_first, circle);
			this.contaner.setTag(R.id.tag_class, "card_contaner");

			int size = circle.friends.size();
			if (circle.rid == 8888888) {
				size += 1;
			}

			int lineCount = size / 4;
			if (lineCount == 0) {
				lineCount = 1;
			}
			int membrane = size % 4;
			if (size / 4 >= 1 && membrane != 0) {
				lineCount++;
			}
			itemHeight = (78 + lineCount * 96) * displayMetrics.density;// 174 to 78

			//
			int containerWidth = (int) (displayMetrics.widthPixels - 20 * displayMetrics.density);
			int spacing = (int) (20 * displayMetrics.density);
			int singleWidth = (containerWidth - spacing * 5) / 4;
			//

			TouchView.LayoutParams layoutParams = new TouchView.LayoutParams(singleWidth, (int) (78 * displayMetrics.density));
			this.friendsSequence.clear();
			this.contaner.removeAllViews();
			for (int i = 0; i < size; i++) {
				Friend friend = null;
				String phone = null;
				if (i >= circle.friends.size()) {
					phone = null;
				} else {
					phone = circle.friends.get(i);
				}
				if (phone != null) {
					friend = friendsMap.get(phone);
				}
				String key = "friend#" + phone;
				FriendBody friendBody = friendBodiesMap.get(key);
				// friendBody = null;
				if (friendBody == null) {
					friendBody = new FriendBody();
					if (circle.rid == 8888888 && i == size - 1) {
						friendBody.Initialize(true);
					} else {
						friendBody.Initialize(false);
					}
				}

				friendBody.setData(friend);
				this.contaner.addView(friendBody.friendView, layoutParams);
				int x = (i % 4 + 1) * spacing + (i % 4) * singleWidth;
				int y = (int) ((i / 4) * (95 * displayMetrics.density) + 64 * displayMetrics.density);

				friendBody.friendView.setX(x);
				friendBody.friendView.setY(y);
			}
		}
	}

	public class FriendBody {

		public View friendView = null;

		public ImageView headImageView;
		public TextView nickNameView;

		public boolean flag = false;

		public View Initialize(boolean flag) {
			this.flag = flag;
			this.friendView = mainView.mInflater.inflate(R.layout.circles_gridpage_item, null);
			this.headImageView = (ImageView) this.friendView.findViewById(R.id.head_image);
			this.nickNameView = (TextView) this.friendView.findViewById(R.id.nickname);
			return friendView;
		}

		public void setData(Friend friend) {
			if (this.flag) {
				this.headImageView.setImageResource(R.drawable.chatgroupmore);
				this.headImageView.setColorFilter(Color.parseColor("#99cdcdcd"));
				this.nickNameView.setText("添加好友");
				this.friendView.setTag(R.id.tag_class, "addfriend_view");
			} else {
				taskManageHolder.fileHandler.getHeadImage(friend.head, this.headImageView, taskManageHolder.viewManage.options52);
				if (friend.alias != null && !"".equals(friend.alias)) {
					this.nickNameView.setText(friend.alias);
				} else {
					this.nickNameView.setText(friend.nickName);
				}
				this.friendView.setTag(R.id.friendsContainer, friend);
				this.friendView.setTag(R.id.tag_class, "friend_view");
			}
			this.friendView.setOnClickListener(thisController.mOnClickListener);
			this.friendView.setOnTouchListener(thisController.onTouchListener);

		}
	}

	public PopupWindow inputPopWindow;
	public View inputDialogView;

	@SuppressWarnings("deprecation")
	public void showInputDialog() {
		mInflater = mainView.thisActivity.getLayoutInflater();
		inputDialogView = mInflater.inflate(R.layout.widget_alert_input_dialog, null);
		inputPopWindow = new PopupWindow(inputDialogView, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT, true);
		inputPopWindow.setBackgroundDrawable(new BitmapDrawable());
		inputPopWindow.showAtLocation(mainView.main_container, Gravity.CENTER, 0, 0);
	}

	public BaseSpringSystem mSpringSystem = SpringSystem.create();
	public SpringConfig IMAGE_SPRING_CONFIG = SpringConfig.fromOrigamiTensionAndFriction(40, 9);
	public SpringConfig IMAGE_SPRING_CONFIG_TO = SpringConfig.fromOrigamiTensionAndFriction(40, 15);
	public Spring dialogSpring = mSpringSystem.createSpring().setSpringConfig(IMAGE_SPRING_CONFIG);
	public Spring dialogOutSpring = mSpringSystem.createSpring().setSpringConfig(IMAGE_SPRING_CONFIG_TO);
	public Spring dialogInSpring = mSpringSystem.createSpring().setSpringConfig(IMAGE_SPRING_CONFIG_TO);
	public View dialogRootView;
	public DialogShowSpringListener dialogSpringListener = new DialogShowSpringListener();

	public RelativeLayout cirlcesDialogContent;
	public PopupWindow circlePopWindow;
	public View circleDialogView;

	public RelativeLayout dialogContentView;
	public View inputDialigView;

	public LayoutInflater mInflater;

	public int SHOW_DIALOG = 0x01;
	public int DIALOG_SWITCH = 0x02;
	public int currentStatus = SHOW_DIALOG;

	public TextView modifyCircleNameView, deleteCircleView, createCircleView;
	public TextView cancleButton;
	public TextView confirmButton;
	public EditText inputEditView;
	public TextView circleName;

	@SuppressWarnings("deprecation")
	public void showCircleSettingDialog(Circle circle) {
		currentStatus = SHOW_DIALOG;
		dialogSpring.addListener(dialogSpringListener);
		// final DisplayMetrics displayMetrics = new DisplayMetrics();
		circleDialogView = mInflater.inflate(R.layout.circle_longclick_dialog, null);
		dialogContentView = (RelativeLayout) circleDialogView.findViewById(R.id.dialogContent);
		inputDialigView = circleDialogView.findViewById(R.id.inputDialogContent);
		height = displayMetrics.density * 140 + 0.5f;
		// y = inputDialigView.getTranslationY();
		y = ((displayMetrics.heightPixels - height) / 2) + displayMetrics.heightPixels;
		inputDialigView.setTranslationY(y);
		dialogRootView = dialogContentView;
		y0 = dialogRootView.getTranslationY();
		dialogSpring.setCurrentValue(0);
		dialogSpring.setEndValue(1);

		circlePopWindow = new PopupWindow(circleDialogView, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT, true);
		circlePopWindow.setBackgroundDrawable(new BitmapDrawable());
		circlePopWindow.showAtLocation(mainView.main_container, Gravity.CENTER, 0, 0);

		modifyCircleNameView = (TextView) circleDialogView.findViewById(R.id.modifyCircleName);
		modifyCircleNameView.setOnClickListener(mainView.thisController.mOnClickListener);

		deleteCircleView = (TextView) circleDialogView.findViewById(R.id.deleteCircle);
		deleteCircleView.setOnClickListener(mainView.thisController.mOnClickListener);
		deleteCircleView.setTag(R.id.tag_first, circle.rid);

		createCircleView = (TextView) circleDialogView.findViewById(R.id.createCircle);
		createCircleView.setOnClickListener(mainView.thisController.mOnClickListener);

		circleName = (TextView) circleDialogView.findViewById(R.id.circleName);

		cancleButton = (TextView) circleDialogView.findViewById(R.id.cancel);
		confirmButton = (TextView) circleDialogView.findViewById(R.id.confirm);
		cancleButton.setOnClickListener(mainView.thisController.mOnClickListener);
		confirmButton.setOnClickListener(mainView.thisController.mOnClickListener);
		inputEditView = (EditText) circleDialogView.findViewById(R.id.input);
		circleName.setText(circle.name);
		inputEditView.setText(circle.name);

		confirmButton.setTag(R.id.tag_first, inputEditView);
		confirmButton.setTag(R.id.tag_second, circle);

		confirmButton.setTag(R.id.tag_class, "CircleSettingConfirmButton");
	}

	public void dismissCircleSettingDialog() {
		circlePopWindow.dismiss();
	}

	float y;
	float height;
	float y0;

	public FriendsSubController thisController;

	private class DialogShowSpringListener extends SimpleSpringListener {
		@Override
		public void onSpringUpdate(Spring spring) {
			float mappedValue = (float) spring.getCurrentValue();
			if (spring.equals(dialogSpring)) {
				dialogRootView.setScaleX(mappedValue);
				dialogRootView.setScaleY(mappedValue);
			} else if (spring.equals(dialogOutSpring)) {
				dialogRootView.setTranslationY(y0 - displayMetrics.heightPixels * (1.0f - mappedValue));
				// Log.e(tag, mappedValue + "---------------");
				if (mappedValue <= 0.8f) {

				}
			} else if (spring.equals(dialogInSpring)) {
				float y0 = (mappedValue / 1f) * y;
				if (((displayMetrics.heightPixels - height) / 2) < y0)
					inputDialigView.setTranslationY(y0);
			}
		}
	}
}
