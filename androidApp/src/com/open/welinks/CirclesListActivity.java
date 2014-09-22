package com.open.welinks;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.open.lib.TouchView;
import com.open.lib.viewbody.ListBody1;
import com.open.lib.viewbody.ListBody1.MyListItemBody;
import com.open.welinks.model.Data;
import com.open.welinks.model.Data.Relationship.Circle;
import com.open.welinks.model.Data.Relationship.Friend;
import com.open.welinks.model.FileHandlers;
import com.open.welinks.model.Parser;
import com.open.welinks.utils.MCImageUtils;

public class CirclesListActivity extends Activity {

	public Data data = Data.getInstance();

	public String tag = "CirclesListActivity";

	public DisplayMetrics displayMetrics;
	public TouchView friendsView;

	public ListBody1 friendListBody;

	public List<String> circles;
	public Map<String, Circle> circlesMap;
	public Map<String, Friend> friendsMap;

	public Parser parser = Parser.getInstance();

	public FileHandlers fileHandlers = FileHandlers.getInstance();

	public DisplayImageOptions options;

	public LayoutInflater mInflater;

	public OnClickListener mOnClickListener;
	public OnTouchListener mOntouchListener;

	public View onTouchDownView;

	public GestureDetector mGesture;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mGesture = new GestureDetector(this, new GestureListener());
		initViews();
		initializeListeners();
		bindEvent();
		showCircles();
	}

	public void initializeListeners() {
		mOnClickListener = new OnClickListener() {

			@Override
			public void onClick(View view) {
				String view_class = (String) view.getTag(R.id.tag_class);
				if (view_class.equals("friend_view")) {
					View status = (View) view.getTag(R.id.tag_first);
					if (status.getVisibility() == View.VISIBLE) {
						status.setVisibility(View.GONE);
					} else {
						status.setVisibility(View.VISIBLE);
					}
				}
				Toast.makeText(CirclesListActivity.this, "view click", Toast.LENGTH_SHORT).show();
			}
		};
		mOntouchListener = new OnTouchListener() {

			@Override
			public boolean onTouch(View view, MotionEvent event) {
				int action = event.getAction();
				if (action == MotionEvent.ACTION_DOWN) {
					String view_class = (String) view.getTag(R.id.tag_class);
					if (view_class.equals("friend_view")) {
						onTouchDownView = view;
					}
				}
				return false;
			}
		};
	}

	public void bindEvent() {

	}

	class GestureListener extends SimpleOnGestureListener {

		public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
			onTouchDownView = null;

			return false;
		}
	}

	public void onSingleTapUp(MotionEvent event) {
		if (onTouchDownView != null) {
			String view_class = (String) onTouchDownView.getTag(R.id.tag_class);
			if (view_class.equals("friend_view")) {
				onTouchDownView.performClick();
				onTouchDownView = null;
			}
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		super.onTouchEvent(event);
		int motionEvent = event.getAction();
		if (motionEvent == MotionEvent.ACTION_DOWN) {
			friendListBody.onTouchDown(event);
		} else if (motionEvent == MotionEvent.ACTION_MOVE) {
			friendListBody.onTouchMove(event);
		} else if (motionEvent == MotionEvent.ACTION_UP) {
			onSingleTapUp(event);
			friendListBody.onTouchUp(event);
		}
		mGesture.onTouchEvent(event);
		return true;
	}

	public void initViews() {
		displayMetrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

		setContentView(R.layout.activity_circleslist);
		this.friendsView = (TouchView) findViewById(R.id.friendsContainer);
		this.mInflater = getLayoutInflater();

		friendListBody = new ListBody1();
		friendListBody.initialize(displayMetrics, friendsView);
		friendListBody.active();
		options = new DisplayImageOptions.Builder().showImageOnLoading(R.drawable.ic_stub).showImageForEmptyUri(R.drawable.ic_empty).showImageOnFail(R.drawable.ic_error).cacheInMemory(true).cacheOnDisk(true).considerExifParams(true).displayer(new RoundedBitmapDisplayer(52)).build();
	}

	public void showCircles() {
		data = parser.check();
		circles = data.relationship.circles;
		circlesMap = data.relationship.circlesMap;
		friendsMap = data.relationship.friendsMap;

		this.friendListBody.containerView.removeAllViews();
		this.friendListBody.height = 0;
		this.friendListBody.y = 0;

		this.friendListBody.listItemsSequence.clear();

		for (int i = 0; i < circles.size(); i++) {
			Circle circle = circlesMap.get(circles.get(i));

			CircleBody circleBody = null;
			circleBody = new CircleBody(this.friendListBody);
			circleBody.initialize();
			circleBody.setContent(circle);

			this.friendListBody.listItemsSequence.add("circle#" + circle.rid);
			this.friendListBody.listItemBodiesMap.put("circle#" + circle.rid, circleBody);

			TouchView.LayoutParams layoutParams = new TouchView.LayoutParams((int) (displayMetrics.widthPixels - displayMetrics.density * 20), (int) (circleBody.itemHeight - 10 * displayMetrics.density));
			circleBody.y = this.friendListBody.height;
			circleBody.cardView.setY(circleBody.y);
			circleBody.cardView.setX(0);

			this.friendListBody.containerView.addView(circleBody.cardView, layoutParams);
			this.friendListBody.height = this.friendListBody.height + circleBody.itemHeight;
			// Log.d(tag, "addView");
			Log.v(tag, "this.friendListBody.height: " + this.friendListBody.height + "    circleBody.y:  " + circleBody.y);

		}

		this.friendListBody.containerHeight = (int) (this.displayMetrics.heightPixels - 38 - displayMetrics.density * 48);
	}

	Bitmap bitmap = null;

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
		public ImageView gripCardBackground = null;

		int lineCount = 0;

		public View initialize() {

			Resources resources = getResources();
			bitmap = BitmapFactory.decodeResource(resources, R.drawable.face_man);
			bitmap = MCImageUtils.getCircleBitmap(bitmap, true, 5, Color.WHITE);

			this.cardView = (TouchView) mInflater.inflate(R.layout.view_control_circle_card, null);
			this.leftTopText = (TextView) this.cardView.findViewById(R.id.leftTopText);
			this.gripView = (TouchView) this.cardView.findViewById(R.id.grip);
			this.leftTopTextButton = (TouchView) this.cardView.findViewById(R.id.leftTopTextButton);

			this.gripCardBackground = (ImageView) this.cardView.findViewById(R.id.grip_card_background);

			// this.leftTopTextButton.setOnTouchListener(thisController.onTouchListener);

			// this.gripView.setOnTouchListener(thisController.onTouchListener);

			itemWidth = displayMetrics.widthPixels - 20 * displayMetrics.density;
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

			int lineCount = circle.friends.size() / 4;
			if (lineCount == 0) {
				lineCount = 1;
			}
			int membrane = circle.friends.size() % 4;
			if (membrane != 0) {
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
			for (int i = 0; i < circle.friends.size(); i++) {
				String phone = circle.friends.get(i);
				Friend friend = friendsMap.get(phone);

				FriendBody friendBody = new FriendBody();
				friendBody.Initialize();
				friendBody.setData(friend);

				this.cardView.addView(friendBody.friendView, layoutParams);
				int x = (i % 4 + 1) * spacing + (i % 4) * singleWidth;
				int y = (int) ((i / 4) * (95 * displayMetrics.density) + 64 * displayMetrics.density);
				friendBody.friendView.setX(x);
				friendBody.friendView.setY(y);

				if (this.friendBodiesMap.get(phone) == null) {
					// optimize friendBodiesMap pool
				}
			}
		}
	}

	public class FriendBody {
		public View friendView = null;

		public ImageView headImageView;
		public ImageView headImageStatusView;

		public TextView nickNameView;

		public View Initialize() {
			this.friendView = mInflater.inflate(R.layout.circles_gridpage_item, null);
			this.headImageView = (ImageView) this.friendView.findViewById(R.id.head_image);
			this.headImageStatusView = (ImageView) this.friendView.findViewById(R.id.head_image_status);
			this.nickNameView = (TextView) this.friendView.findViewById(R.id.nickname);
			return friendView;
		}

		public void setData(Friend friend) {

			fileHandlers.getHeadImage(friend.head, this.headImageView, options);
			// this.headImageView.setImageBitmap(bitmap);

			this.nickNameView.setText(friend.nickName);
			this.friendView.setTag(R.id.friendsContainer, friend);
			this.friendView.setTag(R.id.tag_class, "friend_view");
			this.friendView.setTag(R.id.tag_first, headImageStatusView);
			this.friendView.setOnClickListener(mOnClickListener);

			this.friendView.setOnTouchListener(mOntouchListener);

		}
	}
}
