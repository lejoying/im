package com.open.welinks.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import android.app.Activity;
import android.app.Service;
import android.content.Context;
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

import com.google.gson.Gson;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.open.lib.MyLog;
import com.open.lib.OpenLooper;
import com.open.lib.OpenLooper.LoopCallback;
import com.open.lib.viewbody.BodyCallback;
import com.open.lib.viewbody.ListBody1;
import com.open.lib.viewbody.ListBody1.MyListItemBody;
import com.open.welinks.R;
import com.open.welinks.customView.Alert;
import com.open.welinks.customView.Alert.AlertInputDialog;
import com.open.welinks.customView.Alert.AlertInputDialog.OnDialogClickListener;
import com.open.welinks.model.API;
import com.open.welinks.model.Data;
import com.open.welinks.model.Data.Relationship.Circle;
import com.open.welinks.model.Data.Relationship.Friend;
import com.open.welinks.model.Parser;
import com.open.welinks.model.ResponseHandlers;
import com.open.welinks.view.CirclesManageView;
import com.open.welinks.view.CirclesManageView.CircleBody;
import com.open.welinks.view.CirclesManageView.FriendBody;
import com.open.welinks.view.ViewManage;

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
	public BodyCallback bodyCallback;

	public GestureDetector mGesture;

	public View onClickView;
	public View onTouchDownView;
	public Circle onTouchDownCircle;
	public Friend onTouchDownFriend;

	public boolean isTouch = false;;

	public OpenLooper openLooper;
	public ListLoopCallback loopCallback;

	public CirclesManageController(Activity thisActivity) {
		this.context = thisActivity;
		this.thisActivity = thisActivity;
		this.thisController = this;

		openLooper = new OpenLooper();
		openLooper.createOpenLooper();
		loopCallback = new ListLoopCallback(openLooper);
		openLooper.loopCallback = loopCallback;

		mGesture = new GestureDetector(thisActivity, new GestureListener());

		log.e(ViewManage.getStatusBarHeight(thisActivity) + "----height");
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
					// String view_class = (String) view.getTag(R.id.tag_class);

					Object viewTag = view.getTag(R.id.tag_first);
					if (Circle.class.isInstance(viewTag) == true) {
						Circle circle = (Circle) viewTag;
						Log.d(tag, "onTouch: rid:" + circle.rid + "name" + circle.name);

						onTouchDownCircle = circle;
						onTouchDownView = view;
					} else {
						Log.d(tag, "onTouch: " + (String) viewTag);
					}
					Object viewTag2 = view.getTag(R.id.tag_second);
					if (Friend.class.isInstance(viewTag2) == true) {
						if (!isTouch) {
							Friend friend = (Friend) viewTag2;
							onTouchDownFriend = friend;
							onTouchDownView = view;
							isTouch = true;
						}
					} else {
						Log.d(tag, "onTouch: " + viewTag2);
					}
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
		thisView.backView.setOnClickListener(mOnClickListener);
		thisView.friendListBody.bodyCallback = this.bodyCallback;
	}

	int OrderingMoveDirection = 0;
	int OrderingMoveUp = 1;
	int OrderingMoveDown = -1;

	public class ListLoopCallback extends LoopCallback {
		public ListLoopCallback(OpenLooper openLooper) {
			openLooper.super();
		}

		@Override
		public void loop(double ellapsedMillis) {
			if (touchStatus.state == touchStatus.DRAG) {
				listBodyMove(OrderingMoveDirection, ellapsedMillis);
			}
			if (isLooper) {
				goNextPosition(ellapsedMillis);
			}
		}
	}

	float orderSpeed = 0.46f;

	public void listBodyMove(int direction, double delta) {

		float distance = (float) (direction * delta * this.orderSpeed);

		if (thisView.friendListBody.y + distance >= 0) {
			distance = -thisView.friendListBody.y;
			// this.openLooper.stop();
		} else if (thisView.friendListBody.y + distance <= -(thisView.friendListBody.height - thisView.friendListBody.containerHeight)) {
			distance = -thisView.friendListBody.y - (thisView.friendListBody.height - thisView.friendListBody.containerHeight);
			// this.openLooper.stop();
		}

		thisView.friendListBody.y += distance;
		thisView.friendListBody.setChildrenPosition();
	}

	float orderSpeedH = 0.27f;
	float orderSpeedV = 0.66f;

	boolean isLooper = false;

	public void goNextPosition(double delta) {
		float distanceH = (float) (delta * this.orderSpeedH);
		float distanceV = (float) (delta * this.orderSpeedV);
		Map<String, FriendBody> friendBodys = thisView.friendBodiesMap;
		boolean isLoop = false;

		for (Entry<String, FriendBody> entity : friendBodys.entrySet()) {
			FriendBody body = entity.getValue();
			if (body.state == body.STATIC_STATE) {
				continue;
			} else if (body.state == body.TRANSLATION_STATE) {
				if (!isLoop) {
					isLoop = true;
				}
				float x = 0, y = 0;
				float diffX = body.next_x - body.x;
				float diffY = body.next_y - body.y;
				if (Math.abs(diffX) < distanceV) {
					x = body.next_x;
				} else {
					if (diffX > 0) {
						x = body.x + distanceV;
						if (x > body.next_x) {
							x = body.next_x;
						}
					} else {
						x = body.x - distanceV;
						if (x < body.next_x) {
							x = body.next_x;
						}
					}
				}

				if (Math.abs(diffY) < distanceH) {
					y = body.next_y;
				} else {
					if (diffY > 0) {
						y = body.y + distanceH;
						if (y > body.next_y) {
							y = body.next_y;
						}
					} else {
						y = body.y - distanceH;
						if (y < body.next_y) {
							y = body.next_y;
						}
					}
				}
				body.x = x;
				body.y = y;
				body.friendView.setX(x);
				body.friendView.setY(y);

				body.state = body.MOVE_STATE;

				if (body.x == body.next_x && body.y == body.next_y) {
					body.state = body.STATIC_STATE;
				}
			} else if (body.state == body.MOVE_STATE) {
				if (!isLoop) {
					isLoop = true;
				}
				float x = 0, y = 0;
				float diffX = body.next_x - body.x;
				float diffY = body.next_y - body.y;
				if (Math.abs(diffX) < distanceV) {
					x = body.next_x;
				} else {
					if (diffX > 0) {
						x = body.x + distanceV;
						if (x > body.next_x) {
							x = body.next_x;
						}
					} else {
						x = body.x - distanceV;
						if (x < body.next_x) {
							x = body.next_x;
						}
					}
				}

				if (Math.abs(diffY) < distanceH) {
					y = body.next_y;
				} else {
					if (diffY > 0) {
						y = body.y + distanceH;
						if (y > body.next_y) {
							y = body.next_y;
						}
					} else {
						y = body.y - distanceH;
						if (y < body.next_y) {
							y = body.next_y;
						}
					}
				}
				body.x = x;
				body.y = y;
				body.friendView.setX(x);
				body.friendView.setY(y);

				if (body.x == body.next_x && body.y == body.next_y) {
					body.state = body.STATIC_STATE;
				}
			}
		}
		if (!isLoop) {
			// openLooper.stop();
			// log.e("stop-----------isLoop");
			// touchStatus.state = touchStatus.DRAG;
			isLooper = false;
		} else {
			// log.e("start-----------isLoop");
		}
	}

	public TouchStatus touchStatus = new TouchStatus();

	public class TouchStatus {
		public int NORMAL = 0, DRAG = 1, UP = 2;
		public int state = NORMAL;
	}

	public View dragView;

	public float x = 0;
	public float y = 0;

	public float touch_pre_x = 0;
	public float touch_pre_y = 0;

	public boolean onTouchEvent(MotionEvent event) {
		int motionEvent = event.getAction();
		if (motionEvent == MotionEvent.ACTION_DOWN) {
			touch_pre_x = event.getRawX();
			touch_pre_y = event.getRawY();
			if (touchStatus.state == touchStatus.NORMAL) {
				thisView.friendListBody.onTouchDown(event);
			} else if (touchStatus.state == touchStatus.DRAG) {
				x = event.getRawX();
				y = event.getRawY();
				dragView.setTranslationX(x);
				dragView.setTranslationY(y);
			}
		} else if (motionEvent == MotionEvent.ACTION_MOVE) {
			thisView.setViewXY();
			if (touchStatus.state == touchStatus.NORMAL) {
				thisView.friendListBody.onTouchMove(event);
			} else if (touchStatus.state == touchStatus.DRAG) {
				x = event.getRawX();
				y = event.getRawY();
				dragView.setTranslationX(x - dragView.getWidth() / 2);
				dragView.setTranslationY(y - dragView.getHeight() / 2 - 48);
				judgeDirection();
				checkViewPosition();
			}
		} else if (motionEvent == MotionEvent.ACTION_UP) {
			if (touchStatus.state == touchStatus.NORMAL) {
				onSingleTapUp(event);
				thisView.friendListBody.onTouchUp(event);
			} else if (touchStatus.state == touchStatus.DRAG) {
				touchStatus.state = touchStatus.UP;
				x = event.getRawX();
				y = event.getRawY();
				dragView.setTranslationX(x - dragView.getWidth() / 2);
				dragView.setTranslationY(y - dragView.getHeight() / 2 - 48);
				modifyFriendInCircle();
				dragView.setBackgroundResource(0);
				// thisView.maxView.removeView(dragView);
			}
		}
		mGesture.onTouchEvent(event);
		return true;
	}

	public FriendBody dragFriendBody;

	private void modifyFriendInCircle() {
		openLooper.stop();
		thisView.maxView.removeView(dragView);
		touchStatus.state = touchStatus.NORMAL;
		if (dragFriendBody == null) {
			return;
		}
		if (this.circleBody == null) {
			thisView.showCircles();
			return;
		}
		String rid = this.circleBody.circle.rid + "";
		String phone = this.dragFriendBody.friend.phone;
		if (this.friendBody == null) {
			this.circleBody.circle.friends.add(this.dragFriendBody.friend.phone);
			this.dragFriendBody.circle.friends.remove(this.dragFriendBody.friend.phone);
			this.dragFriendBody.key = "friend#" + this.circleBody.circle.rid + "#" + this.dragFriendBody.friend.phone;
			thisView.showCircles();
		} else {
			this.circleBody.circle.friends.add(this.friendBody.index - 1, this.dragFriendBody.friend.phone);
			this.dragFriendBody.circle.friends.remove(this.dragFriendBody.friend.phone);
			this.dragFriendBody.key = "friend#" + this.circleBody.circle.rid + "#" + this.dragFriendBody.friend.phone;
			thisView.showCircles();
		}
		modifyFriendRid(phone, rid);
		data.relationship.isModified = true;
	}

	public void judgeDirection() {
		if (y < thisView.displayMetrics.heightPixels / 3 + thisView.barHeight && (y - touch_pre_y) < 0) {
			if (thisView.friendListBody.y < 0) {
				this.OrderingMoveDirection = OrderingMoveUp;
				this.openLooper.start();
			}
		} else if (y > 2 * thisView.displayMetrics.heightPixels / 3 + thisView.barHeight && (y - touch_pre_y) > 0) {
			if (thisView.friendListBody.y > -(thisView.friendListBody.height - thisView.friendListBody.containerHeight)) {
				this.OrderingMoveDirection = OrderingMoveDown;
				this.openLooper.start();
			}
		} else {
			this.OrderingMoveDirection = 0;
			// this.openLooper.stop();
		}
	}

	public CircleBody circleBody;
	public FriendBody friendBody;
	public FriendBody beforeFriendBody;

	public int berfoePosition;

	public void checkViewPosition() {
		boolean isFriendHover = false;
		boolean isCircleHover = false;
		List<String> listItemsSequence = thisView.friendListBody.listItemsSequence;
		Map<String, MyListItemBody> listItemBodiesMap = thisView.friendListBody.listItemBodiesMap;
		for (int i = 0; i < listItemsSequence.size(); i++) {
			String cKey = listItemsSequence.get(i);
			CircleBody circleBady = (CircleBody) listItemBodiesMap.get(cKey);
			float y0 = circleBady.cy + circleBady.itemHeight;
			if (circleBady.cy < y && y < y0) {
				isCircleHover = true;
				this.circleBody = circleBady;
				// circleBady.cardView.setBackgroundColor(Color.RED);

				for (int j = 0; j < circleBady.friendsSequence.size(); j++) {
					String fKey = circleBady.friendsSequence.get(j);
					FriendBody friendBody = thisView.friendBodiesMap.get(fKey);
					float x1 = circleBady.cx + friendBody.x + friendBody.width;
					float y1 = circleBady.cy + friendBody.y + friendBody.height;
					if (circleBady.cx + friendBody.x < x && x < x1 && circleBady.cy + friendBody.y < y && y < y1 && friendBody.state == friendBody.STATIC_STATE) {
						// friendBody.friendView.setBackgroundColor(Color.GREEN);
						// TODO
						this.friendBody = friendBody;
						beforeX = circleBady.cx + friendBody.x;
						beforeY = circleBady.cy + friendBody.y;
						isFriendHover = true;
						nextPosition();
						if (!isLooper) {
							openLooper.start();
						}
						isLooper = true;
						break;
					} else {
						// friendBody.friendView.setBackgroundColor(Color.parseColor("#00000000"));
						// friendBody.friendView.setBackgroundColor(Color.parseColor("#00000000"));
					}
				}
			} else {
				// circleBady.cardView.setBackgroundColor(Color.BLUE);
			}
		}
		if (!isFriendHover) {
			beforeFriendBody = null;
			goHoming();
		}
		if (!isCircleHover) {
			circleBody = null;
		}
	}

	float beforeX = 0, beforeY = 0;

	public void goHoming() {

		float x = this.x;
		float y = this.y;

		if (friendBody == null || beforeX == 0 || beforeY == 0) {
			return;
		}

		float x0 = beforeX + friendBody.width;
		float y0 = beforeY + friendBody.height;

		if (beforeX < x && x < x0 && beforeY < y && y < y0) {
		} else {
			for (int i = 0; i < friendBody.friendsSequence.size(); i++) {
				FriendBody body = thisView.friendBodiesMap.get(friendBody.friendsSequence.get(i));
				int j = i;
				body.next_x = (j % 4 + 1) * thisView.spacing + (j % 4) * thisView.singleWidth;
				body.next_y = ((j / 4) * (95 * thisView.displayMetrics.density) + 64 * thisView.displayMetrics.density);
				body.state = body.TRANSLATION_STATE;
				body.index = j;
			}
			isLooper = true;
			openLooper.start();
			friendBody = null;
			beforeX = 0;
			beforeY = 0;
		}
	}

	public void nextPosition() {
		if (beforeFriendBody == friendBody) {
			return;
		} else {
			beforeFriendBody = friendBody;
			berfoePosition = friendBody.index;
		}
		int A = friendBody.index;
		// log.e(A + ">>>>>>>>>>>>>>>>>A");
		boolean isEmpty = false;
		for (int i = 0; i < friendBody.friendsSequence.size(); i++) {
			FriendBody body = thisView.friendBodiesMap.get(friendBody.friendsSequence.get(i));
			int j = i;

			if (body.index != i && 0 <= i && i < A) {
				j = body.index - 1;
				isEmpty = true;
			} else if (body.index != i && i == A) {
				j = body.index - 1;
				isEmpty = true;
			} else if (i >= A && !isEmpty) {
				if (body.index == i) {
					j = body.index + 1;
				} else {
					j = body.index;
				}
			}
			body.next_x = (j % 4 + 1) * thisView.spacing + (j % 4) * thisView.singleWidth;
			body.next_y = ((j / 4) * (95 * thisView.displayMetrics.density) + 64 * thisView.displayMetrics.density);
			body.state = body.TRANSLATION_STATE;
			body.index = j;
			// log.e(body.x + "--" + body.next_x);
		}
	}

	class GestureListener extends SimpleOnGestureListener {

		public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
			// onTouchDownView = null;

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
				}
			}
			if (onTouchDownView != null && onTouchDownFriend != null && onTouchDownCircle != null) {
				String view_class = (String) onTouchDownView.getTag(R.id.tag_class);
				if (view_class == "card_friend") {
					Circle circle = data.relationship.circlesMap.get("" + onTouchDownCircle.rid);

					CircleBody circleBody = (CircleBody) thisView.friendListBody.listItemBodiesMap.get("circle#" + circle.rid);

					Object viewTag = onTouchDownView.getTag(R.id.tag_second);
					if (Friend.class.isInstance(viewTag) == true) {
						Friend friend = (Friend) viewTag;
						FriendBody friendBody = thisView.friendBodiesMap.get("friend#" + circleBody.circle.rid + "#" + friend.phone);
						if (friendBody != null) {
							Vibrator vibrator = (Vibrator) thisActivity.getSystemService(Service.VIBRATOR_SERVICE);
							long[] pattern = { 100, 100, 300 };
							vibrator.vibrate(pattern, -1);

							// friendBody.friendBackground.setVisibility(View.VISIBLE);
							touchStatus.state = touchStatus.DRAG;
							dragFriendBody = friendBody;
							dragView = friendBody.friendView;
							circleBody.contaner.removeView(dragView);
							dragView.setBackgroundResource(R.drawable.card_login_background_press);
							thisView.maxView.removeView(dragView);
							thisView.maxView.addView(dragView);
							// x = event.getRawX() - 20 * thisView.displayMetrics.density;
							// y = event.getRawY() - 48 * thisView.displayMetrics.density - ViewUtil.getStatusBarHeight(thisActivity);
							x = circleBody.cx + friendBody.x;
							y = circleBody.cy + friendBody.y - 35 * thisView.displayMetrics.density;
							if (circleBody.friendsSequence.size() > 0) {
								if (friendBody.index < circleBody.friendsSequence.size())
									circleBody.friendsSequence.remove(friendBody.index);
							}
							dragView.setTranslationX(x);
							dragView.setTranslationY(y);
						}
					}
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

	private void onSingleTapUp(MotionEvent event) {
		if (onTouchDownView != null) {
			if (onTouchDownCircle != null && onTouchDownFriend == null) {
				Circle circle = data.relationship.circlesMap.get("" + onTouchDownCircle.rid);
				CircleBody circleBody = (CircleBody) thisView.friendListBody.listItemBodiesMap.get("circle#" + circle.rid);
				circleBody.gripCardBackground.setVisibility(View.INVISIBLE);

				onTouchDownView = null;
				onTouchDownCircle = null;
				thisView.friendListBody.stopOrdering();
			}
			if (onTouchDownFriend != null && onTouchDownCircle != null) {
				Circle circle = data.relationship.circlesMap.get("" + onTouchDownCircle.rid);

				CircleBody circleBody = (CircleBody) thisView.friendListBody.listItemBodiesMap.get("circle#" + circle.rid);

				Object viewTag = onTouchDownView.getTag(R.id.tag_second);
				if (Friend.class.isInstance(viewTag) == true) {
					Friend friend = (Friend) viewTag;
					FriendBody friendBody = thisView.friendBodiesMap.get("friend#" + circleBody.circle.rid + "#" + friend.phone);
					friendBody.friendBackground.setVisibility(View.INVISIBLE);
				}
				onTouchDownView = null;
				onTouchDownCircle = null;
				onTouchDownFriend = null;
			}
		}
		isTouch = false;
	}

	public void modifyFriendRid(String phone, String rid) {
		HttpUtils httpUtils = new HttpUtils();
		RequestParams params = new RequestParams();
		params.addBodyParameter("phone", data.userInformation.currentUser.phone);
		params.addBodyParameter("accessKey", data.userInformation.currentUser.accessKey);
		params.addBodyParameter("rid", rid);
		params.addBodyParameter("targetphones", "[\"" + phone + "\"]");

		httpUtils.send(HttpMethod.POST, API.RELATION_MODIFYCIRCLE, params, responseHandlers.modifyCircleCallBack);
	}

	public void modifyGroupSequence(String circleSequence) {
		RequestParams params = new RequestParams();
		HttpUtils httpUtils = new HttpUtils();
		params.addBodyParameter("phone", data.userInformation.currentUser.phone);
		params.addBodyParameter("accessKey", data.userInformation.currentUser.accessKey);
		params.addBodyParameter("sequence", circleSequence);

		httpUtils.send(HttpMethod.POST, API.RELATION_MODIFYCIRCLESEQUENCE, params, responseHandlers.modifyCircleSequenceCallBack);
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