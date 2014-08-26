package com.open.welinks.controller;

import android.util.Log;
import android.view.MotionEvent;
import android.view.SoundEffectConstants;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;
import android.widget.EditText;
import android.widget.TextView;

import com.open.welinks.R;
import com.open.welinks.model.Data;
import com.open.welinks.model.Data.Relationship.Circle;
import com.open.welinks.view.FriendsSubView;
import com.open.welinks.view.FriendsSubView.CircleBody;

public class FriendsSubController {

	public Data data = Data.getInstance();
	public String tag = "FriendsSubController";

	public FriendsSubView thisView;
	public FriendsSubController thisController;
	public MainController mainController;

	public OnLongClickListener onLongClickListener;
	public OnTouchListener onTouchListener;

	public View onTouchDownView;
	public Circle onTouchDownCircle;

	public FriendsSubController(MainController mainController) {
		this.mainController = mainController;
	}

	public void initializeListeners() {
		onLongClickListener = new OnLongClickListener() {

			@Override
			public boolean onLongClick(View view) {
				return true;
			}
		};

		onTouchListener = new OnTouchListener() {

			@Override
			public boolean onTouch(View view, MotionEvent event) {
				int action = event.getAction();
				if (action == MotionEvent.ACTION_DOWN) {
					// thisView.mainView.main_container.playSoundEffect(SoundEffectConstants.CLICK);
					Object viewTag = view.getTag(R.id.tag_first);
					if (Circle.class.isInstance(viewTag) == true) {
						Circle circle = (Circle) viewTag;
						Log.d(tag, "onTouch: rid:" + circle.rid + "name" + circle.name);
						thisView.friendListBody.onOrdering("circle#" + circle.rid);

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

}
