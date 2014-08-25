package com.open.welinks.controller;

import android.util.Log;
import android.view.MotionEvent;
import android.view.SoundEffectConstants;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;

import com.open.welinks.model.Data;
import com.open.welinks.model.Data.Relationship.Circle;
import com.open.welinks.view.FriendsSubView;

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
				thisView.showCircleSettingDialog(view);
				return true;
			}
		};

		onTouchListener = new OnTouchListener() {

			@Override
			public boolean onTouch(View view, MotionEvent event) {
				int action = event.getAction();
				if (action == MotionEvent.ACTION_DOWN) {
					thisView.mainView.main_container.playSoundEffect(SoundEffectConstants.CLICK);
					Object viewTag = view.getTag();
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
		if(onTouchDownView!=null&&onTouchDownCircle!=null){
			thisView.showCircleSettingDialog(onTouchDownView);
		}
	}

}
