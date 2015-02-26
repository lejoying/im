package com.open.welinks.view;

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
import com.open.welinks.controller.CirclesManageController;
import com.open.welinks.model.Data;
import com.open.welinks.model.Data.Relationship.Circle;
import com.open.welinks.model.Data.Relationship.Friend;
import com.open.welinks.model.Parser;
import com.open.welinks.model.TaskManageHolder;

public class CirclesManageView {

	public Data data = Data.getInstance();
	public Parser parser = Parser.getInstance();

	public TaskManageHolder taskManageHolder = TaskManageHolder.getInstance();

	public String tag = "CirclesManageView";
	public MyLog log = new MyLog(tag, true);

	public Context context;
	public CirclesManageView thisView;
	public CirclesManageController thisController;
	public Activity thisActivity;

	public LayoutInflater mInflater;
	public DisplayMetrics displayMetrics;
	public TouchView friendsView;

	public View backView;
	public TextView backTitleView;
	public RelativeLayout maxView;

	public ListBody1 friendListBody;

	public List<String> circles;
	public Map<String, Circle> circlesMap;
	public Map<String, Friend> friendsMap;

	public int barHeight;

	public CirclesManageView(Activity thisActivity) {
		this.context = thisActivity;
		this.thisActivity = thisActivity;
		this.thisView = this;
		taskManageHolder.viewManage.circlesManageView = this;
	}

	public int containerWidth;
	public int spacing;
	public int singleWidth;

	public void initView() {
		displayMetrics = new DisplayMetrics();
		thisActivity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
		this.mInflater = thisActivity.getLayoutInflater();
		barHeight = (int) (48 * thisView.displayMetrics.density + 0.5f);// + ViewUtil.getStatusBarHeight(context)
		containerWidth = (int) (displayMetrics.widthPixels - 20 * displayMetrics.density);
		spacing = (int) (20 * displayMetrics.density);
		singleWidth = (containerWidth - spacing * 5) / 4;
		thisActivity.setContentView(R.layout.activity_circleslist);
		this.friendsView = (TouchView) thisActivity.findViewById(R.id.friendsContainer);
		this.backView = thisActivity.findViewById(R.id.backView);
		this.maxView = (RelativeLayout) thisActivity.findViewById(R.id.maxView);
		View title_control_progress_container = thisActivity.findViewById(R.id.title_control_progress_container);
		title_control_progress_container.setVisibility(View.GONE);
		this.backTitleView = (TextView) thisActivity.findViewById(R.id.backTitleView);
		this.backTitleView.setText(thisActivity.getString(R.string.circleManage_group_manage));

		friendListBody = new ListBody1();
		friendListBody.initialize(displayMetrics, friendsView);
		friendListBody.active();

		initCircleSettingDialog();
		showCircles();
	}

	public void showCircles() {
		data = parser.check();
		circles = data.relationship.circles;
		circlesMap = data.relationship.circlesMap;
		friendsMap = data.relationship.friendsMap;

		this.friendListBody.containerView.removeAllViews();
		this.friendListBody.height = 0;
		// this.friendListBody.y = 0;

		if (circles == null || circlesMap == null) {
			return;
		}

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

			this.friendListBody.listItemsSequence.add(keyName);

			TouchView.LayoutParams layoutParams = new TouchView.LayoutParams((int) (displayMetrics.widthPixels - displayMetrics.density * 20), (int) (circleBody.itemHeight - 10 * displayMetrics.density));
			this.friendListBody.containerView.addView(circleBody.cardView, layoutParams);
			circleBody.y = this.friendListBody.height;
			circleBody.cardView.setY(circleBody.y);
			circleBody.cy = (this.friendListBody.height + 48 * displayMetrics.density + 0.5f) + 50;
			circleBody.cx = (int) (10 * displayMetrics.density + 0.5f);
			if (flag) {
				circleBody.cardView.setX(10 * displayMetrics.density + 0.5f);
			} else {
				circleBody.cardView.setX(0);
			}

			circleBody.index = i;
			circleBody.height = (int) circleBody.itemHeight;

			this.friendListBody.height = this.friendListBody.height + circleBody.itemHeight;

			log.v(tag, "this.friendListBody.height: " + this.friendListBody.height + "    circleBody.y:  " + circleBody.y);
		}

		this.friendListBody.containerHeight = (int) (this.displayMetrics.heightPixels - ViewManage.getStatusBarHeight(context) - displayMetrics.density * 48);
		if (this.friendListBody.height < this.friendListBody.containerHeight) {
			this.friendListBody.y = 0;
		}
		this.friendListBody.setChildrenPosition();
	}

	public Map<String, FriendBody> friendBodiesMap = new HashMap<String, FriendBody>();

	public void setViewXY() {
		for (int i = 0; i < friendListBody.listItemsSequence.size(); i++) {
			CircleBody circleBody = (CircleBody) friendListBody.listItemBodiesMap.get(friendListBody.listItemsSequence.get(i));
			circleBody.setChildXY();
		}
	}

	public class CircleBody extends MyListItemBody {

		CircleBody(ListBody1 listBody) {
			listBody.super();
		}

		public List<String> friendsSequence = new ArrayList<String>();

		public TouchView cardView = null;
		public TextView leftTopText = null;
		public TouchView leftTopTextButton = null;
		public TouchView gripView = null;
		public TouchView contaner = null;
		public ImageView gripCardBackground = null;

		public int lineCount = 0;

		public CircleBody instance;

		public float cx, cy;

		public int index;
		public int height;

		public Circle circle;

		public View initialize() {
			instance = this;
			this.cardView = (TouchView) mInflater.inflate(R.layout.view_control_circle_card, null);
			this.leftTopText = (TextView) this.cardView.findViewById(R.id.leftTopText);
			this.gripView = (TouchView) this.cardView.findViewById(R.id.grip);
			this.contaner = (TouchView) this.cardView.findViewById(R.id.contaner);
			this.leftTopTextButton = (TouchView) this.cardView.findViewById(R.id.leftTopTextButton);

			this.gripCardBackground = (ImageView) this.cardView.findViewById(R.id.grip_card_background);

			this.leftTopTextButton.setOnTouchListener(thisController.mOnTouchListener);
			// this.leftTopText.setOnLongClickListener(mainView.thisController.onLongClickListener);

			this.gripView.setOnTouchListener(thisController.mOnTouchListener);

			itemWidth = displayMetrics.widthPixels - 20 * displayMetrics.density;
			itemHeight = 260 * displayMetrics.density;

			super.initialize(cardView);
			return cardView;
		}

		public void setChildXY() {
			this.cy = this.y + friendListBody.y + 48 * displayMetrics.density + 0.5f + 50;
			for (int i = 0; i < friendsSequence.size(); i++) {
				String key = friendsSequence.get(i);
				FriendBody friendBody = friendBodiesMap.get(key);
				friendBody.cy = this.cy + friendBody.y;
				friendBody.cx = this.cx + friendBody.x;
			}
		}

		public void setContent(Circle circle) {
			this.circle = circle;
			this.leftTopText.setText(circle.name);

			this.leftTopTextButton.setTag(R.id.tag_first, circle);
			this.leftTopTextButton.setTag(R.id.tag_class, "card_title");

			this.gripView.setTag(R.id.tag_first, circle);
			this.gripView.setTag(R.id.tag_class, "card_grip");

			int size = circle.friends.size();

			int lineCount = size / 4;
			if (lineCount == 0) {
				lineCount = 1;
			}
			int membrane = size % 4;
			if (size / 4 >= 1 && membrane != 0) {
				lineCount++;
			}
			itemHeight = (78 + lineCount * 96) * displayMetrics.density;// 174 to 78

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
				String key = "friend#" + circle.rid + "#" + phone;
				FriendBody friendBody = friendBodiesMap.get(key);
				if (friendBody == null) {
					friendBody = new FriendBody();
					friendBody.Initialize();
					friendBodiesMap.put(key, friendBody);
				}
				friendBody.key = key;

				friendsSequence.add(key);

				friendBody.setData(friend);
				this.contaner.addView(friendBody.friendView, layoutParams);
				int x = (i % 4 + 1) * spacing + (i % 4) * singleWidth;
				int y = (int) ((i / 4) * (95 * displayMetrics.density) + 64 * displayMetrics.density);

				friendBody.circle = circle;
				friendBody.circleBody = instance;
				friendBody.friend = friend;
				friendBody.index = i;
				friendBody.x = x;
				friendBody.y = y;
				friendBody.friendsSequence = friendsSequence;
				friendBody.friendView.setX(x);
				friendBody.friendView.setY(y);

				friendBody.friendView.setTag(R.id.tag_first, circle);
				friendBody.friendView.setTag(R.id.tag_second, friend);
				friendBody.friendView.setTag(R.id.tag_class, "card_friend");
				friendBody.friendView.setOnTouchListener(thisController.mOnTouchListener);
			}
		}
	}

	public class FriendBody {

		public View friendView = null;

		public ImageView friendBackground;
		public ImageView headImageView;
		public TextView nickNameView;

		public int index;
		public Circle circle;
		public CircleBody circleBody;
		public Friend friend;
		public float x, y;
		public float cx, cy;
		public int width, height;
		public float next_x, next_y;

		public int STATIC_STATE = 1;
		public int TRANSLATION_STATE = 2;
		public int MOVE_STATE = 3;
		public int state = STATIC_STATE;

		public String key;

		public List<String> friendsSequence;

		public View Initialize() {
			this.width = (int) (displayMetrics.density * 52 + 0.5f);
			this.height = (int) (displayMetrics.density * 75 + 0.5f);
			this.friendView = mInflater.inflate(R.layout.circles_gridpage_item, null);
			this.headImageView = (ImageView) this.friendView.findViewById(R.id.head_image);
			this.nickNameView = (TextView) this.friendView.findViewById(R.id.nickname);
			this.friendBackground = (ImageView) this.friendView.findViewById(R.id.grip_card_background);
			return friendView;
		}

		public void setData(Friend friend) {
			taskManageHolder.fileHandler.getHeadImage(friend.head, this.headImageView, taskManageHolder.viewManage.options52);
			if (friend.alias != null && !"".equals(friend.alias)) {
				this.nickNameView.setText(friend.alias);
			} else {
				this.nickNameView.setText(friend.nickName);
			}
			this.friendView.setTag(R.id.friendsContainer, friend);
			this.friendView.setTag(R.id.tag_class, "friend_view");
			this.friendView.setOnClickListener(thisController.mOnClickListener);
			// this.friendView.setOnTouchListener(thisController.onTouchListener);

		}
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

	public int SHOW_DIALOG = 0x01;
	public int DIALOG_SWITCH = 0x02;
	public int currentStatus = SHOW_DIALOG;

	public TextView modifyCircleNameView, deleteCircleView, createCircleView;
	public TextView cancleButton;
	public TextView confirmButton;
	public EditText inputEditView;
	public TextView circleName;

	float y;
	float height;
	float y0;

	@SuppressWarnings("deprecation")
	public void initCircleSettingDialog() {
		dialogSpring.addListener(dialogSpringListener);
		circleDialogView = mInflater.inflate(R.layout.circle_longclick_dialog, null);
		circleDialogView.setOnClickListener(thisController.mOnClickListener);
		dialogContentView = (RelativeLayout) circleDialogView.findViewById(R.id.dialogContent);
		inputDialigView = circleDialogView.findViewById(R.id.inputDialogContent);
		height = displayMetrics.density * 140 + 0.5f;
		// y = inputDialigView.getTranslationY();
		y = ((displayMetrics.heightPixels - height) / 2) + displayMetrics.heightPixels;
		inputDialigView.setTranslationY(y);
		dialogRootView = dialogContentView;
		y0 = dialogRootView.getTranslationY();

		circlePopWindow = new PopupWindow(circleDialogView, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT, true);
		circlePopWindow.setBackgroundDrawable(new BitmapDrawable());

		modifyCircleNameView = (TextView) circleDialogView.findViewById(R.id.modifyCircleName);
		modifyCircleNameView.setOnClickListener(thisController.mOnClickListener);

		deleteCircleView = (TextView) circleDialogView.findViewById(R.id.deleteCircle);
		deleteCircleView.setOnClickListener(thisController.mOnClickListener);

		createCircleView = (TextView) circleDialogView.findViewById(R.id.createCircle);
		createCircleView.setOnClickListener(thisController.mOnClickListener);

		circleName = (TextView) circleDialogView.findViewById(R.id.circleName);

		cancleButton = (TextView) circleDialogView.findViewById(R.id.cancel);
		confirmButton = (TextView) circleDialogView.findViewById(R.id.confirm);
		cancleButton.setOnClickListener(thisController.mOnClickListener);
		confirmButton.setOnClickListener(thisController.mOnClickListener);
		inputEditView = (EditText) circleDialogView.findViewById(R.id.input);

		confirmButton.setTag(R.id.tag_first, inputEditView);

		confirmButton.setTag(R.id.tag_class, "CircleSettingConfirmButton");
	}

	public void showCircleSettingDialog(Circle circle) {
		currentStatus = SHOW_DIALOG;
		deleteCircleView.setTag(R.id.tag_first, circle.rid);
		circleName.setText(circle.name);
		inputEditView.setText(circle.name);
		confirmButton.setTag(R.id.tag_second, circle);
		dialogSpring.setCurrentValue(0);
		dialogSpring.setEndValue(1);
		circlePopWindow.showAtLocation(maxView, Gravity.CENTER, 0, 0);
	}

	public void dismissCircleSettingDialog() {
		circlePopWindow.dismiss();
	}

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
