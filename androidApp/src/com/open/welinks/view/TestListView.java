package com.open.welinks.view;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.open.lib.TouchView;
import com.open.lib.viewbody.ListBody1;
import com.open.lib.viewbody.ListBody1.MyListItemBody;
import com.open.welinks.R;
import com.open.welinks.controller.DownloadFileList;
import com.open.welinks.controller.TestListController;
import com.open.welinks.model.Data;
import com.open.welinks.model.Data.Relationship.Circle;
import com.open.welinks.model.Data.Relationship.Friend;

public class TestListView {

	public Data data = Data.getInstance();

	public String tag = "MainView";

	public DisplayMetrics displayMetrics;

	public TestListController thisController;
	public TestListView thisView;
	public Context context;
	public Activity thisActivity;
	public Map<String, View> viewsMap = new HashMap<String, View>();

	public LayoutInflater mInflater;

	public TouchView friendsView;

	public RelativeLayout title_messages_friends_me;

	public RelativeLayout main_container;

	public TestListView(Activity thisActivity) {
		this.context = thisActivity;
		this.thisActivity = thisActivity;
		this.thisView = this;

	}

	public ListBody1 friendListBody;

	public void initViews() {

		mInflater = thisActivity.getLayoutInflater();
		displayMetrics = new DisplayMetrics();

		thisActivity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

		thisActivity.setContentView(R.layout.activity_test_list_view);

		main_container = (RelativeLayout) thisActivity.findViewById(R.id.main_container);

		friendsView = (TouchView) thisActivity.findViewById(R.id.friendsContainer);

		friendListBody = new ListBody1();
		friendListBody.initialize(displayMetrics, friendsView);

		displayImageOptions = new DisplayImageOptions.Builder().showImageOnLoading(R.drawable.ic_stub).showImageForEmptyUri(R.drawable.ic_empty).showImageOnFail(R.drawable.ic_error).cacheInMemory(true).cacheOnDisk(true).considerExifParams(true).displayer(new RoundedBitmapDisplayer(40)).build();
	}

	public List<String> circles;
	public Map<String, Circle> circlesMap;
	public Map<String, Friend> friendsMap;

	public void showCircles() {

		circles = data.relationship.circles;
		circlesMap = data.relationship.circlesMap;
		friendsMap = data.relationship.friendsMap;

		this.friendListBody.listItemsSequence.clear();
		this.friendListBody.height = 2 * displayMetrics.density;
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

			Log.d(tag, "addView");
			Log.v(tag, "this.friendListBody.height: " + this.friendListBody.height + "    circleBody.y:  " + circleBody.y);

		}

		this.friendListBody.containerHeight = (int) (this.displayMetrics.heightPixels - 38);
		Log.v(tag, "containerHeight: " + this.friendListBody.containerHeight + "    heightPixels:" + this.displayMetrics.heightPixels);

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
		public ImageView gripCardBackground = null;

		int lineCount = 0;

		public View initialize() {

			this.cardView = (TouchView) thisView.mInflater.inflate(R.layout.view_control_circle_card, null);
			this.leftTopText = (TextView) this.cardView.findViewById(R.id.leftTopText);
			this.gripView = (TouchView) this.cardView.findViewById(R.id.grip);
			this.leftTopTextButton = (TouchView) this.cardView.findViewById(R.id.leftTopTextButton);

			this.gripCardBackground = (ImageView) this.cardView.findViewById(R.id.grip_card_background);

			this.leftTopTextButton.setOnTouchListener(thisController.onTouchListener);
			// this.leftTopText.setOnLongClickListener(mainView.thisController.onLongClickListener);

			this.gripView.setOnTouchListener(thisController.onTouchListener);

			itemWidth = thisView.displayMetrics.widthPixels - 20 * thisView.displayMetrics.density;
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

			TouchView.LayoutParams layoutParams = new TouchView.LayoutParams((int) (55 * displayMetrics.density), (int) (78 * displayMetrics.density));

			int lineCount = circle.friends.size() / 4;
			if (lineCount == 0) {
				lineCount = 1;
			}
			lineCount = 0;
			itemHeight = (174 + lineCount * 96) * displayMetrics.density;

			this.friendsSequence.clear();
			for (int i = 0; i < circle.friends.size() && i < 4; i++) {
				String phone = circle.friends.get(i);
				Friend friend = friendsMap.get(phone);

				FriendBody friendBody = new FriendBody();
				friendBody.Initialize();
				friendBody.setData(friend);

				this.cardView.addView(friendBody.friendView, layoutParams);

				int x = 120 * (int) displayMetrics.density * (i % 4) + (int) itemWidth / 16;
				int y = 140 * (int) displayMetrics.density * (i / 4) + 96 * (int) displayMetrics.density;

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
		public TextView nickNameView;

		public View Initialize() {
			this.friendView = thisView.mInflater.inflate(R.layout.circles_gridpage_item, null);
			this.headImageView = (ImageView) this.friendView.findViewById(R.id.head_image);
			this.nickNameView = (TextView) this.friendView.findViewById(R.id.nickname);
			return friendView;
		}

		public void setData(Friend friend) {

			// Resources resources = thisView.thisActivity.getResources();
			// Bitmap bitmap = BitmapFactory.decodeResource(resources, R.drawable.face_man);
			// bitmap = MCImageUtils.getCircleBitmap(bitmap, true, 5, Color.WHITE);
			// this.headImageView.setImageResource(R.drawable.face_man);

			imageLoader.displayImage("drawable://" + R.drawable.face_man, this.headImageView, displayImageOptions, mImageLoadingListener);

			this.nickNameView.setText(friend.nickName);
			this.friendView.setTag(R.id.friendsContainer, friend);
			this.friendView.setTag(R.id.tag_class, "friend_view");
			this.friendView.setOnClickListener(thisController.mOnClickListener);

			this.friendView.setOnTouchListener(thisController.onTouchListener);

		}
	}

	public ImageLoader imageLoader = ImageLoader.getInstance();
	public DisplayImageOptions displayImageOptions;
	public DownloadFileList downloadFileList = DownloadFileList.getInstance();

	public ImageLoadingListener mImageLoadingListener = new SimpleImageLoadingListener() {
		@Override
		public void onLoadingStarted(String imageUri, View view) {
		}

		@Override
		public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
		}

		@Override
		public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {

		}
	};

	public Gson gson = new Gson();

}
