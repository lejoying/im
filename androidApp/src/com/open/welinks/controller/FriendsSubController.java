package com.open.welinks.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.app.Service;
import android.content.Intent;
import android.os.Vibrator;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SoundEffectConstants;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;
import android.widget.Toast;

import com.google.gson.Gson;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.open.lib.MyLog;
import com.open.lib.viewbody.BodyCallback;
import com.open.welinks.CirclesManageActivity;
import com.open.welinks.NearbyActivity;
import com.open.welinks.R;
import com.open.welinks.SearchFriendActivity;
import com.open.welinks.customListener.MyOnClickListener;
import com.open.welinks.customView.Alert;
import com.open.welinks.customView.Alert.AlertInputDialog;
import com.open.welinks.customView.Alert.AlertInputDialog.OnDialogClickListener;
import com.open.welinks.model.API;
import com.open.welinks.model.Constant;
import com.open.welinks.model.Data;
import com.open.welinks.model.Data.Relationship.Circle;
import com.open.welinks.model.Data.Relationship.Friend;
import com.open.welinks.model.Data.UserInformation.User;
import com.open.welinks.model.Parser;
import com.open.welinks.model.ResponseHandlers;
import com.open.welinks.view.FriendsSubView;
import com.open.welinks.view.FriendsSubView.CircleBody;

public class FriendsSubController {

	public Data data = Data.getInstance();
	public Parser parser = Parser.getInstance();

	public String tag = "FriendsSubController";
	public MyLog log = new MyLog(tag, true);

	public ResponseHandlers responseHandlers = ResponseHandlers.getInstance();

	public FriendsSubView thisView;
	public FriendsSubController thisController;
	public OnClickListener mOnClickListener;
	public MainController mainController;

	public OnLongClickListener onLongClickListener;
	public OnTouchListener onTouchListener;

	public BodyCallback bodyCallback;

	public View onTouchDownView;
	public View onClickView;

	public Circle onTouchDownCircle;

	public boolean isTouchDown = false;

	public FriendsSubController(MainController mainController) {
		this.mainController = mainController;
	}

	public void initializeListeners() {
		mOnClickListener = new MyOnClickListener() {

			public void onClickEffective(View view) {
				if (view.equals(mainController.thisView.userTopbarNameParentView)) {
					// thisView.businessCardPopView.cardView.setSmallBusinessCardContent(thisView.businessCardPopView.cardView.TYPE_POINT, data.userInformation.currentUser.phone);
					// thisView.businessCardPopView.showUserCardDialogView();
					Intent intent = new Intent(thisView.mainView.thisActivity, NearbyActivity.class);
					intent.putExtra("type", "newest");
					thisView.mainView.thisActivity.startActivity(intent);
					thisView.mainView.thisActivity.finish();
				}
				Friend friend = null;
				if ((friend = (Friend) view.getTag(R.id.friendsContainer)) != null) {
					thisView.businessCardPopView.cardView.setSmallBusinessCardContent(thisView.businessCardPopView.cardView.TYPE_POINT, friend.phone);
					thisView.businessCardPopView.showUserCardDialogView();
					// Toast.makeText(mainController.thisActivity, friend.friendStatus + "---status", Toast.LENGTH_SHORT).show();
				}
				String view_class = (String) view.getTag(R.id.tag_class);
				if (view_class != null) {
					if (view_class.equals("addfriend_view")) {
						Intent intent = new Intent(mainController.thisActivity, SearchFriendActivity.class);
						mainController.thisActivity.startActivity(intent);
						// Toast.makeText(mainController.thisActivity, "addMembers", Toast.LENGTH_SHORT).show();
					}
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
					} else if (view_class.equals("addfriend_view")) {
						onTouchDownView = view;
						onClickView = view;
					}
					// thisView.mainView.main_container.playSoundEffect(SoundEffectConstants.CLICK);
					Object viewTag = view.getTag(R.id.tag_first);
					if (Circle.class.isInstance(viewTag) == true) {

						if (!isTouchDown) {
							Circle circle = (Circle) viewTag;
							Log.d(tag, "onTouch: rid:" + circle.rid + "name" + circle.name);

							onTouchDownCircle = circle;
							onTouchDownView = view;
							isTouchDown = true;
						}
					} else {
						Log.d(tag, "onTouch: " + (String) viewTag);
					}
					// thisView.friendsSubView.showCircleSettingDialog();
				}
				return false;
			}
		};
		bodyCallback = new BodyCallback() {
			@Override
			public void onStopOrdering(List<String> listItemsSequence) {
				super.onStopOrdering(listItemsSequence);
				List<String> circles = new ArrayList<String>();

				List<Circle2> list = new ArrayList<Circle2>();
				for (int i = 0; i < listItemsSequence.size(); i++) {
					String key = listItemsSequence.get(i);
					String rid = key.substring(key.indexOf("#") + 1);
					circles.add(rid);
					Circle circleData = data.relationship.circlesMap.get(rid);
					if (circleData != null) {
						log.e(circleData.rid + "---" + circleData.name);
						list.add(new Circle2(circleData.rid + "", circleData.name));
					}
				}
				// modify local data
				Gson gson = new Gson();
				String oldSequece = gson.toJson(data.relationship.circles);
				data.relationship.circles = circles;
				data.relationship.isModified = true;
				String ridSequence = gson.toJson(circles);
				// modify server data
				if (!oldSequece.equals(ridSequence)) {
					String circleSequece = gson.toJson(list);
					log.e(circleSequece);
					modifyGroupSequence(circleSequece);
					log.e("分组顺序发生改动");
				} else {
					log.e(oldSequece);
					log.e(ridSequence);
					log.e("分组顺序没有改动");
				}
			}
		};
	}

	public class Circle2 {
		public String rid;
		public String name;

		public Circle2(String rid, String name) {
			this.rid = rid;
			this.name = name;
		}
	}

	public void bindEvent() {
		mainController.thisView.userTopbarNameParentView.setOnClickListener(mOnClickListener);
		thisView.friendListBody.bodyCallback = this.bodyCallback;
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
			} else if (view_class == "card_contaner") {
				// onTouchDownView = null;
				// onClickView = null;
				// Toast.makeText(mainController.thisActivity, "long press max", Toast.LENGTH_SHORT).show();
				if (onClickView == null || onClickView.getTag(R.id.tag_class) == null) {
					Intent intent = new Intent(mainController.thisActivity, CirclesManageActivity.class);
					mainController.thisActivity.startActivity(intent);
				} else {
					String view_class2 = (String) onClickView.getTag(R.id.tag_class);
					if (!"addfriend_view".equals(view_class2)) {
						Intent intent = new Intent(mainController.thisActivity, CirclesManageActivity.class);
						mainController.thisActivity.startActivity(intent);
					}
				}
			}
		}
		isTouchDown = false;
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
				} else if (view_class.equals("addfriend_view")) {
					onClickView.performClick();
					onClickView = null;
				}
			}
		}
		isTouchDown = false;
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
		data = parser.check();
		Circle circle = data.relationship.circlesMap.get("" + inputCircle.rid);
		if (circle == null) {
			Toast.makeText(mainController.thisActivity, "该分组不存在.", Toast.LENGTH_SHORT).show();
			return;
		}
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

	public void onScroll() {
		onClickView = null;
	}

	public void modifyGroupSequence(String circleSequence) {
		RequestParams params = new RequestParams();
		HttpUtils httpUtils = new HttpUtils();
		params.addBodyParameter("phone", data.userInformation.currentUser.phone);
		params.addBodyParameter("accessKey", data.userInformation.currentUser.accessKey);
		params.addBodyParameter("sequence", circleSequence);

		httpUtils.send(HttpMethod.POST, API.RELATION_MODIFYCIRCLESEQUENCE, params, responseHandlers.modifyCircleSequenceCallBack);
	}

	public void deleteCircle(final String rid) {
		if (!rid.equals(Constant.DEFAULTCIRCLEID + "") && !rid.equals(Constant.defaultContactId + "")) {
			data = parser.check();
			final Circle defaultCircle = data.relationship.circlesMap.get(Constant.DEFAULTCIRCLEID + "");
			final Circle deleteCircle = data.relationship.circlesMap.get(rid);
			if (deleteCircle == null) {
				Toast.makeText(mainController.thisActivity, "该分组不存在.", Toast.LENGTH_SHORT).show();
				return;
			}
			Alert.createDialog(thisView.mainView.context).setTitle("是否删除该分组（" + deleteCircle.name + "）").setOnConfirmClickListener(new OnDialogClickListener() {
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
					User currentUser = data.userInformation.currentUser;
					params.addBodyParameter("phone", currentUser.phone);
					params.addBodyParameter("accessKey", currentUser.accessKey);
					params.addBodyParameter("rid", rid);

					httpUtils.send(HttpMethod.POST, API.CIRCLE_DELETE, params, responseHandlers.circle_delete);

					thisView.showCircles();

				}
			}).show();
		} else {
			Toast.makeText(mainController.thisActivity, "不能删除该分组.", Toast.LENGTH_SHORT).show();
		}
	}

	public void createCircle() {
		data = parser.check();
		Alert.createInputDialog(thisView.mainView.context).setTitle("请输入分组名").setOnConfirmClickListener(new OnDialogClickListener() {

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
					User currentUser = data.userInformation.currentUser;
					params.addBodyParameter("phone", currentUser.phone);
					params.addBodyParameter("accessKey", currentUser.accessKey);
					params.addBodyParameter("name", circleName);
					params.addBodyParameter("rid", rid + "");

					httpUtils.send(HttpMethod.POST, API.CIRCLE_ADDCIRCLE, params, responseHandlers.circle_addcircle);

				} else {

				}
			}
		}).show();
	}
}
