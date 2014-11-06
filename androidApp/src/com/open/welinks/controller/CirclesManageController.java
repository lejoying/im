package com.open.welinks.controller;

import java.util.Date;

import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Vibrator;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.SoundEffectConstants;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.EditText;
import android.widget.Toast;

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.open.lib.MyLog;
import com.open.lib.viewbody.ListBody1;
import com.open.welinks.CirclesManageActivity;
import com.open.welinks.R;
import com.open.welinks.customView.Alert;
import com.open.welinks.customView.Alert.AlertInputDialog;
import com.open.welinks.customView.Alert.AlertInputDialog.OnDialogClickListener;
import com.open.welinks.model.API;
import com.open.welinks.model.Data;
import com.open.welinks.model.Parser;
import com.open.welinks.model.Data.Relationship.Circle;
import com.open.welinks.model.ResponseHandlers;
import com.open.welinks.view.CirclesManageView;
import com.open.welinks.view.CirclesManageView.CircleBody;

public class CirclesManageController {

	public Data data = Data.getInstance();
	public Parser parser = Parser.getInstance();

	public ResponseHandlers responseHandlers = ResponseHandlers.getInstance();

	public String tag = "CirclesManageController";
	public MyLog log = new MyLog(tag, true);

	public Context context;
	public CirclesManageView thisView;
	public CirclesManageController thisController;
	public Activity thisActivity;

	public OnClickListener mOnClickListener;
	public OnTouchListener mOnTouchListener;

	public GestureDetector mGesture;

	public View onClickView;
	public View onTouchDownView;
	public Circle onTouchDownCircle;

	public CirclesManageController(Activity thisActivity) {
		this.context = thisActivity;
		this.thisActivity = thisActivity;
		this.thisController = this;

		mGesture = new GestureDetector(thisActivity, new GestureListener());
	}

	public void initializeListeners() {
		mOnClickListener = new OnClickListener() {

			@Override
			public void onClick(View view) {
				if (view.equals(thisView.backView)) {
					thisActivity.finish();
				} else if (view.equals(thisView.circleDialogView)) {
					if (thisView.currentStatus == thisView.SHOW_DIALOG) {
						thisView.dismissCircleSettingDialog();
					}
				} else if (view.equals(thisView.modifyCircleNameView)) {
					if (thisView.currentStatus == thisView.SHOW_DIALOG) {
						thisView.dialogSpring.removeListener(thisView.dialogSpringListener);
						thisView.currentStatus = thisView.DIALOG_SWITCH;
						thisView.dialogOutSpring.addListener(thisView.dialogSpringListener);
						thisView.dialogOutSpring.setCurrentValue(1.0);
						thisView.dialogOutSpring.setEndValue(0);
						thisView.dialogInSpring.addListener(thisView.dialogSpringListener);
						thisView.inputDialigView.setVisibility(View.VISIBLE);
						thisView.dialogInSpring.setCurrentValue(1);
						thisView.dialogInSpring.setEndValue(0);
					}
				} else if (view.equals(thisView.deleteCircleView)) {
					thisView.thisController.deleteCircle(String.valueOf((Integer) (view.getTag(R.id.tag_first))));
					thisView.dismissCircleSettingDialog();
				} else if (view.equals(thisView.createCircleView)) {
					thisView.thisController.createCircle();
					thisView.dismissCircleSettingDialog();
				} else if (view.equals(thisView.cancleButton)) {
					thisView.dismissCircleSettingDialog();
				} else if (view.equals(thisView.confirmButton)) {
					EditText editText = ((EditText) (view.getTag(R.id.tag_first)));
					String inputContent = editText.getText().toString().trim();
					Circle circle = (Circle) view.getTag(R.id.tag_second);
					onConfirmButton(inputContent, circle);
					thisView.dismissCircleSettingDialog();
				}
			}
		};
		mOnTouchListener = new OnTouchListener() {

			@Override
			public boolean onTouch(View view, MotionEvent event) {
				int action = event.getAction();
				if (action == MotionEvent.ACTION_DOWN) {
					String view_class = (String) view.getTag(R.id.tag_class);

					Object viewTag = view.getTag(R.id.tag_first);
					if (Circle.class.isInstance(viewTag) == true) {
						Circle circle = (Circle) viewTag;
						Log.d(tag, "onTouch: rid:" + circle.rid + "name" + circle.name);

						onTouchDownCircle = circle;
						onTouchDownView = view;
					} else {
						Log.d(tag, "onTouch: " + (String) viewTag);
					}

				}
				return false;
			}
		};
	}

	public void bindEvent() {
		thisView.backView.setOnClickListener(mOnClickListener);
	}

	class GestureListener extends SimpleOnGestureListener {

		public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
			onTouchDownView = null;

			return false;
		}

		public boolean onDoubleTapEvent(MotionEvent event) {
			if (onTouchDownView != null && onTouchDownCircle != null) {
				String view_class = (String) onTouchDownView.getTag(R.id.tag_class);
				if (view_class == "card_title") {
					thisView.maxView.playSoundEffect(SoundEffectConstants.CLICK);

					thisView.showCircleSettingDialog(onTouchDownCircle);
					onTouchDownView = null;
					onTouchDownCircle = null;
				}
			}
			return false;
		}

		public void onLongPress(MotionEvent event) {
			if (onTouchDownView != null && onTouchDownCircle != null) {
				String view_class = (String) onTouchDownView.getTag(R.id.tag_class);
				if (view_class == "card_title") {

					thisView.maxView.playSoundEffect(SoundEffectConstants.CLICK);
					thisView.showCircleSettingDialog(onTouchDownCircle);
					onTouchDownView = null;
					onTouchDownCircle = null;
				} else if (view_class == "card_grip") {
					Circle circle = data.relationship.circlesMap.get("" + onTouchDownCircle.rid);

					CircleBody circleBody = (CircleBody) thisView.friendListBody.listItemBodiesMap.get("circle#" + circle.rid);

					circleBody.gripCardBackground.setVisibility(View.VISIBLE);

					Vibrator vibrator = (Vibrator) thisActivity.getSystemService(Service.VIBRATOR_SERVICE);
					long[] pattern = { 100, 100, 300 };
					vibrator.vibrate(pattern, -1);

					thisView.friendListBody.startOrdering("circle#" + circle.rid);
				} else if (view_class == "friend_view") {
					onTouchDownView = null;
					onClickView = null;
					Toast.makeText(thisActivity, "long press", Toast.LENGTH_SHORT).show();
				}
			} else if (onTouchDownView != null) {
				String view_class = (String) onTouchDownView.getTag(R.id.tag_class);
				if (view_class == "friend_view") {
					onTouchDownView = null;
					onClickView = null;
					Toast.makeText(thisActivity, "long press", Toast.LENGTH_SHORT).show();
					Intent intent = new Intent(thisActivity, CirclesManageActivity.class);
					thisActivity.startActivity(intent);
				}
			}
		}

		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
			ListBody1 listBody = thisView.friendListBody;
			if (listBody != null) {
				if (listBody.bodyStatus.state == listBody.bodyStatus.DRAGGING) {
					listBody.onFling(velocityX, velocityY);
				} else if (listBody.bodyStatus.state == listBody.bodyStatus.FIXED) {
					listBody.onFling(velocityX, velocityY);
				} else {
					Log.i(tag, "bodyStatus error:" + listBody.bodyStatus.state);
				}
			}
			return true;
		}
	}

	public boolean onTouchEvent(MotionEvent event) {
		int motionEvent = event.getAction();
		if (motionEvent == MotionEvent.ACTION_DOWN) {
			thisView.friendListBody.onTouchDown(event);
		} else if (motionEvent == MotionEvent.ACTION_MOVE) {
			thisView.friendListBody.onTouchMove(event);
		} else if (motionEvent == MotionEvent.ACTION_UP) {
			onSingleTapUp(event);
			thisView.friendListBody.onTouchUp(event);
		}
		mGesture.onTouchEvent(event);
		return true;
	}

	private void onSingleTapUp(MotionEvent event) {
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
				} else if (view_class.equals("addfriend_view")) {
					onClickView.performClick();
					onClickView = null;
				}
			}
		}
	}

	public void onConfirmButton(String inputContent, Circle inputCircle) {
		if ("".equals(inputContent)) {
			return;
		}
		data = parser.check();
		Circle circle = data.relationship.circlesMap.get("" + inputCircle.rid);
		circle.name = inputContent;
		data.relationship.isModified = true;

		CircleBody circleBody = (CircleBody) thisView.friendListBody.listItemBodiesMap.get("circle#" + circle.rid);

		circleBody.leftTopText.setText(inputContent);

		HttpUtils httpUtils = new HttpUtils();
		RequestParams params = new RequestParams();
		params.addBodyParameter("phone", data.userInformation.currentUser.phone);
		params.addBodyParameter("accessKey", data.userInformation.currentUser.accessKey);
		params.addBodyParameter("name", inputContent);
		params.addBodyParameter("rid", String.valueOf(circle.rid));

		httpUtils.send(HttpMethod.POST, API.CIRCLE_MODIFY, params, responseHandlers.circle_modify);
	}

	public void deleteCircle(final String rid) {
		if (!rid.equals("8888888") && !rid.equals("9999999")) {
			data = parser.check();
			final Circle defaultCircle = data.relationship.circlesMap.get("8888888");
			final Circle deleteCircle = data.relationship.circlesMap.get(rid);
			if (deleteCircle == null) {
				Toast.makeText(thisActivity, "该分组不存在.", Toast.LENGTH_SHORT).show();
				return;
			}
			Alert.createDialog(thisActivity).setTitle("是否删除该分组（" + deleteCircle.name + "）").setOnConfirmClickListener(new OnDialogClickListener() {
				@Override
				public void onClick(AlertInputDialog dialog) {
					for (String firend : deleteCircle.friends) {
						defaultCircle.friends.add(firend);
					}
					data.relationship.circles.remove(rid);
					data.relationship.circlesMap.remove(rid);
					data.relationship.isModified = true;

					HttpUtils httpUtils = new HttpUtils();
					RequestParams params = new RequestParams();
					params.addBodyParameter("phone", data.userInformation.currentUser.phone);
					params.addBodyParameter("accessKey", data.userInformation.currentUser.accessKey);
					params.addBodyParameter("rid", rid);

					httpUtils.send(HttpMethod.POST, API.CIRCLE_DELETE, params, responseHandlers.circle_delete);

					thisView.showCircles();

				}
			}).show();
		} else {
			Toast.makeText(thisActivity, "不能删除该分组.", Toast.LENGTH_SHORT).show();
		}
	}

	public void createCircle() {
		data = parser.check();
		Alert.createInputDialog(thisActivity).setTitle("请输入分组名").setOnConfirmClickListener(new OnDialogClickListener() {

			@Override
			public void onClick(AlertInputDialog dialog) {
				String circleName = dialog.getInputText().trim();
				if (!circleName.equals("")) {
					Circle circle = data.relationship.new Circle();
					circle.name = circleName;
					int rid = (int) new Date().getTime();
					circle.rid = rid;

					data.relationship.circles.add(String.valueOf(rid));
					data.relationship.circlesMap.put(String.valueOf(rid), circle);
					data.relationship.isModified = true;
					thisView.showCircles();

					HttpUtils httpUtils = new HttpUtils();
					RequestParams params = new RequestParams();
					params.addBodyParameter("phone", data.userInformation.currentUser.phone);
					params.addBodyParameter("accessKey", data.userInformation.currentUser.accessKey);
					params.addBodyParameter("name", circleName);
					params.addBodyParameter("rid", rid + "");

					httpUtils.send(HttpMethod.POST, API.CIRCLE_ADDCIRCLE, params, responseHandlers.circle_addcircle);

				} else {

				}
			}
		}).show();
	}
}