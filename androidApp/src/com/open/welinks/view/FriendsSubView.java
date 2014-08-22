package com.open.welinks.view;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.graphics.drawable.BitmapDrawable;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
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
import com.open.lib.viewbody.ListBody;
import com.open.lib.viewbody.ListBody.MyListItemBody;
import com.open.welinks.R;
import com.open.welinks.model.Data;
import com.open.welinks.model.Data.Relationship.Circle;
import com.open.welinks.model.Data.Relationship.Friend;

public class FriendsSubView {

	public Data data = Data.getInstance();

	public String tag = "FriendsSubView";

	public DisplayMetrics displayMetrics;

	public RelativeLayout friendsView;

	public ListBody friendListBody;

	public List<String> circles;
	public Map<String, Circle> circlesMap;
	public Map<String, Friend> friendsMap;

	public MainView mainView;

	public FriendsSubView(MainView mainView) {
		this.mainView = mainView;
	}

	public void initData() {
	}

	public void initViews() {

		this.friendsView = mainView.friendsView;
		this.displayMetrics = mainView.displayMetrics;

		friendsView = (RelativeLayout) mainView.friendsView.findViewById(R.id.friendsContainer);
		friendListBody = new ListBody();
		friendListBody.initialize(displayMetrics, friendsView);

	}

	public void showCircles() {

		circles = data.relationship.circles;
		circlesMap = data.relationship.circlesMap;
		friendsMap = data.relationship.friendsMap;

		this.friendListBody.listItemsSequence.clear();

		for (int i = 0; i < circles.size(); i++) {
			Circle circle = circlesMap.get(circles.get(i));

			CircleBody circleBody = null;
			circleBody = new CircleBody(this.friendListBody);
			circleBody.initialize();
			circleBody.setContent(circle);

			this.friendListBody.listItemsSequence.add("circle#" + circle.rid);
			this.friendListBody.listItemBodiesMap.put("circle#" + circle.rid, circleBody);

			RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, (int) (260 * displayMetrics.density));
			circleBody.y = 270 * displayMetrics.density * i + 2 * displayMetrics.density;
			circleBody.cardView.setY(circleBody.y);
			circleBody.cardView.setX(0);

			this.friendListBody.containerView.addView(circleBody.cardView, layoutParams);
			this.friendListBody.height = this.friendListBody.height + 270 * displayMetrics.density;
			Log.d(tag, "addView");

		}
	}

	public class CircleBody extends MyListItemBody {

		CircleBody(ListBody listBody) {
			listBody.super();
		}

		public List<String> friendsSequence = new ArrayList<String>();
		public Map<String, FriendBody> friendBodiesMap = new HashMap<String, FriendBody>();

		public View cardView = null;
		public TextView leftTopText = null;

		public View initialize() {

			this.cardView = mainView.mInflater.inflate(R.layout.view_control_circle_card, null);
			this.leftTopText = (TextView) this.cardView.findViewById(R.id.leftTopText);

			this.leftTopText.setOnClickListener(mainView.thisController.mOnClickListener);
			this.leftTopText.setOnLongClickListener(mainView.thisController.onLongClickListener);

			super.initialize(cardView);
			return cardView;
		}

		public void setContent(Circle circle) {
			this.leftTopText.setText(circle.name);
			this.leftTopText.setTag(circle);

			this.friendsSequence.clear();
			for (int i = 0; i < circle.friends.size(); i++) {
				String phone = circle.friends.get(i);
				Friend friend = friendsMap.get(phone);
				if (this.friendBodiesMap.get(phone) == null) {

				}
			}
		}
	}

	public class FriendBody {
		public View cardView = null;

		public ImageView headView;
		public TextView nickNameView;

		public View Initialize() {
			cardView = mainView.mInflater.inflate(R.layout.circles_gridpage_item, null);

			return cardView;
		}

		public void setData(Friend friend) {

		}
	}

	public PopupWindow inputPopWindow;
	public View inputDialogView;

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

	public TextView modifyCircleNameView;
	public TextView cancleButton;
	public TextView confirmButton;
	public EditText inputEditView;

	public void showCircleSettingDialog(View view) {
		currentStatus = SHOW_DIALOG;
		mInflater = mainView.thisActivity.getLayoutInflater();
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

		cancleButton = (TextView) circleDialogView.findViewById(R.id.cancel);
		confirmButton = (TextView) circleDialogView.findViewById(R.id.confirm);
		cancleButton.setOnClickListener(mainView.thisController.mOnClickListener);
		confirmButton.setOnClickListener(mainView.thisController.mOnClickListener);
		inputEditView = (EditText) circleDialogView.findViewById(R.id.input);
		Circle circle = (Circle) view.getTag();
		inputEditView.setText(circle.name);
		inputEditView.setTag(view);
		confirmButton.setTag(inputEditView);
	}

	public void dismissCircleSettingDialog() {
		circlePopWindow.dismiss();
	}

	float y;
	float height;
	float y0;

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
