package com.open.welinks.controller;

import android.app.Service;
import android.os.Vibrator;
import android.content.Intent;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SoundEffectConstants;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;

import com.open.welinks.ChatActivity;
import com.open.welinks.R;
import com.open.welinks.model.Data;
import com.open.welinks.model.Data.Relationship.Friend;
import com.open.welinks.model.Data.Relationship.Circle;
import com.open.welinks.view.FriendsSubView;
import com.open.welinks.view.FriendsSubView.CircleBody;

public class FriendsSubController {

	public Data data = Data.getInstance();

	public String tag = "UserIntimateActivity";

	public FriendsSubView thisView;
	public FriendsSubController thisController;
	public OnClickListener mOnClickListener;
	public MainController mainController;

	public OnLongClickListener onLongClickListener;
	public OnTouchListener onTouchListener;

	public View onTouchDownView;
	public View onClickView;

	public Circle onTouchDownCircle;

	public FriendsSubController(MainController mainController) {
		this.mainController = mainController;
	}

	public void initializeListeners() {
		mOnClickListener = new OnClickListener() {

			@Override
			public void onClick(View view) {
				Friend friend = null;
				if ((friend = (Friend) view.getTag(R.id.friendsContainer)) != null) {
					Intent intent = new Intent(thisView.mainView.thisActivity, ChatActivity.class);
					intent.putExtra("id", friend.phone);
					intent.putExtra("type", "point");
					thisView.mainView.thisActivity.startActivity(intent);
				}
			}
		};

		onLongClickListener = new OnLongClickListener() {

			public boolean onLongClick(View view) {
				return true;
			}
		};

		onTouchListener = new OnTouchListener() {

			@Override
			public boolean onTouch(View view, MotionEvent event) {
				int action = event.getAction();
				if (action == MotionEvent.ACTION_DOWN) {
					String view_class = (String) view.getTag(R.id.tag_class);
					if (view_class.equals("friend_view")) {
						onTouchDownView = view;
						onClickView = view;
					}
					// thisView.mainView.main_container.playSoundEffect(SoundEffectConstants.CLICK);
					Object viewTag = view.getTag(R.id.tag_first);
					if (Circle.class.isInstance(viewTag) == true) {
						Circle circle = (Circle) viewTag;
						Log.d(tag, "onTouch: rid:" + circle.rid + "name" + circle.name);

						onTouchDownCircle = circle;
						onTouchDownView = view;
					} else {
						Log.d(tag, "onTouch: " + (String) viewTag);
					}
					// thisView.friendsSubView.showCircleSettingDialog();
				}
				return false;
			}
		};
	}

	public void bindEvent() {

	}

	public void onLongPress(MotionEvent event) {
		if (onTouchDownView != null && onTouchDownCircle != null) {
			String view_class = (String) onTouchDownView.getTag(R.id.tag_class);
			if (view_class == "card_title") {

				thisView.mainView.main_container.playSoundEffect(SoundEffectConstants.CLICK);
				thisView.showCircleSettingDialog(onTouchDownCircle);
				onTouchDownView = null;
				onTouchDownCircle = null;
			} else if (view_class == "card_grip") {
				Circle circle = data.relationship.circlesMap.get("" + onTouchDownCircle.rid);

				CircleBody circleBody = (CircleBody) thisView.friendListBody.listItemBodiesMap.get("circle#" + circle.rid);

				circleBody.gripCardBackground.setVisibility(View.VISIBLE);

				Vibrator vibrator = (Vibrator) this.mainController.thisActivity.getSystemService(Service.VIBRATOR_SERVICE);
				long[] pattern = { 100, 100, 300 };
				vibrator.vibrate(pattern, -1);

				thisView.friendListBody.startOrdering("circle#" + circle.rid);
			}

		}
	}

	public void onSingleTapUp(MotionEvent event) {
		if (onTouchDownView != null) {
			if (onTouchDownCircle != null) {
				Circle circle = data.relationship.circlesMap.get("" + onTouchDownCircle.rid);
				CircleBody circleBody = (CircleBody) thisView.friendListBody.listItemBodiesMap.get("circle#" + circle.rid);
				circleBody.gripCardBackground.setVisibility(View.INVISIBLE);

				onTouchDownView = null;
				onTouchDownCircle = null;
				thisView.friendListBody.stopOrdering();
			}
			if (onClickView != null) {
				String view_class = (String) onClickView.getTag(R.id.tag_class);
				if (view_class.equals("friend_view")) {
					onClickView.performClick();
					onClickView = null;
				}
			}

		}

	}

	public void onDoubleTapEvent(MotionEvent event) {
		if (onTouchDownView != null && onTouchDownCircle != null) {
			String view_class = (String) onTouchDownView.getTag(R.id.tag_class);
			if (view_class == "card_title") {
				thisView.mainView.main_container.playSoundEffect(SoundEffectConstants.CLICK);

				thisView.showCircleSettingDialog(onTouchDownCircle);
				onTouchDownView = null;
				onTouchDownCircle = null;
			}
		}
	}

	public void onConfirmButton(String inputContent, Circle inputCircle) {
		if ("".equals(inputContent)) {
			return;
		}
		Circle circle = data.relationship.circlesMap.get("" + inputCircle.rid);
		circle.name = inputContent;

		CircleBody circleBody = (CircleBody) thisView.friendListBody.listItemBodiesMap.get("circle#" + circle.rid);

		circleBody.leftTopText.setText(inputContent);

	}

	public void onScroll() {
		onClickView = null;
	}

}
