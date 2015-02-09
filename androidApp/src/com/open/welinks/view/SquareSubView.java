package com.open.welinks.view;

import java.io.File;
import java.util.ArrayList;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.facebook.rebound.BaseSpringSystem;
import com.facebook.rebound.SimpleSpringListener;
import com.facebook.rebound.Spring;
import com.facebook.rebound.SpringConfig;
import com.facebook.rebound.SpringSystem;
import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.open.lib.MyLog;
import com.open.lib.TouchImageView;
import com.open.lib.TouchTextView;
import com.open.lib.TouchView;
import com.open.lib.viewbody.ListBody1;
import com.open.lib.viewbody.ListBody1.MyListItemBody;
import com.open.welinks.ClassificationRecommendationActivity;
import com.open.welinks.NearbyActivity;
import com.open.welinks.R;
import com.open.welinks.ShareListActivity;
import com.open.welinks.ShareSectionActivity;
import com.open.welinks.controller.SquareSubController;
import com.open.welinks.model.Data;
import com.open.welinks.model.Data.Relationship.Group;
import com.open.welinks.model.FileHandlers;
import com.open.welinks.model.Parser;
import com.open.welinks.oss.DownloadFileList;

public class SquareSubView {

	public Data data = Data.getInstance();
	public Parser parser = Parser.getInstance();

	public String tag = "SquareSubView";
	public MyLog log = new MyLog(tag, true);

	public FileHandlers fileHandlers = FileHandlers.getInstance();
	public ImageLoader imageLoader = ImageLoader.getInstance();
	public DownloadFileList downloadFileList = DownloadFileList.getInstance();
	public ViewManage viewManage = ViewManage.getInstance();

	public Gson gson = new Gson();

	public SquareSubController thisController;

	public DisplayMetrics displayMetrics;

	public MainView mainView;

	// share
	public RelativeLayout squareView;
	public ViewGroup squareMessageView;

	public LayoutInflater mInflater;

	public ListBody1 locationListBody;

	public SquareSubView(MainView mainView) {
		this.mainView = mainView;
		viewManage.squareSubView = this;
	}

	public void initViews() {
		this.squareView = mainView.squareView;
		this.displayMetrics = mainView.displayMetrics;
		this.mInflater = mainView.mInflater;

		this.squareMessageView = (ViewGroup) squareView.findViewById(R.id.squareContainer);
		this.squareMessageView.setBackgroundColor(Color.parseColor("#F1F2F5"));

		this.locationListBody = new ListBody1();
		this.locationListBody.initialize(this.displayMetrics, this.squareMessageView);

		thisController.bindEvent();

		showLocationList();
	}

	public void showLocationList() {
		@SuppressWarnings("serial")
		ArrayList<Integer> iconList = new ArrayList<Integer>() {
			{
				add(R.drawable.sidebar_icon_discover_normal);
				add(R.drawable.sidebar_icon_days_normal);
				add(R.drawable.sidebar_icon_group_normal);
				add(R.drawable.sidebar_icon_category_normal);
				add(R.drawable.sidebar_icon_test_normal);
				add(R.drawable.sidebar_icon_beauty_normal);
			}
		};
		@SuppressWarnings("serial")
		ArrayList<String> list = new ArrayList<String>() {
			{
				add("附近的分享");
				add("热门话题");
				add("附近的群");
				add("分类推荐");
				add("附近的人");
				add("与我相关");
			}
		};
		this.locationListBody.height = 0 * this.displayMetrics.density;
		SelectBody body0 = new SelectBody(locationListBody);
		body0.initialize(0);
		RelativeLayout.LayoutParams layoutParams0 = new RelativeLayout.LayoutParams(android.widget.RelativeLayout.LayoutParams.MATCH_PARENT, (int) (190 * this.displayMetrics.density));
		this.locationListBody.containerView.addView(body0.cardView, layoutParams0);
		this.locationListBody.height += 190 * this.displayMetrics.density;
		body0.y = 0 * displayMetrics.density;
		body0.cardView.setY(body0.y);

		float width = (this.displayMetrics.widthPixels - 30 * this.displayMetrics.density) / 2;

		float height = (width - 5) / 2;
		this.locationListBody.height += 15 * this.displayMetrics.density;
		for (int i = 0; i < list.size(); i++) {
			String name = list.get(i);
			SelectBody body = new SelectBody(locationListBody);
			body.initialize(1);
			body.setContent(iconList.get(i), name);

			float w = 0, h = 0;
			if (i == 0) {
				w = width;
				h = height * 2;
				body.x = 10 * this.displayMetrics.density;
				body.y = this.locationListBody.height;
				body.cardView.setBackgroundResource(R.drawable.button_de485e_background);
			} else if (i == 1) {
				w = width;
				h = height;
				body.x = this.displayMetrics.widthPixels / 2 + 5 * this.displayMetrics.density;
				body.y = this.locationListBody.height;
				body.cardView.setBackgroundResource(R.drawable.button_7174e0_background);
			} else if (i == 2) {
				w = width;
				h = height;
				body.x = 10 * this.displayMetrics.density;
				body.y = this.locationListBody.height + height * 2 + 10 * this.displayMetrics.density;
				body.cardView.setBackgroundResource(R.drawable.button_f17437_background);
			} else if (i == 3) {
				w = width;
				h = height * 2;
				body.x = this.displayMetrics.widthPixels / 2 + 5 * this.displayMetrics.density;
				body.y = this.locationListBody.height + height * 1 + 10 * this.displayMetrics.density;
				body.cardView.setBackgroundResource(R.drawable.button_5c97e7_background);
			} else if (i == 4) {
				w = width;
				h = height;
				body.x = 10 * this.displayMetrics.density;
				body.y = this.locationListBody.height + height * 3 + 20 * this.displayMetrics.density;
				body.cardView.setBackgroundResource(R.drawable.button_eaa52e_background);
			} else if (i == 5) {
				w = width;
				h = height;
				body.x = this.displayMetrics.widthPixels / 2 + 5 * this.displayMetrics.density;
				body.y = this.locationListBody.height + height * 3 + 20 * this.displayMetrics.density;
				body.cardView.setBackgroundResource(R.drawable.button_39aadf_background);
			}
			body.cardView.setX(body.x);
			body.cardView.setY(body.y);
			body.itemHeight = h;

			FrameLayout.LayoutParams params = (LayoutParams) body.iconView.getLayoutParams();
			params.height = (int) height / 2;
			params.width = (int) height / 2;

			RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams((int) w, (int) h);
			this.locationListBody.containerView.addView(body.cardView, layoutParams);
		}
		this.locationListBody.height += 4 * height + 20 * this.displayMetrics.density;
	}

	public class SelectBody extends MyListItemBody {

		public TouchView cardView;
		public TouchImageView iconView;
		public TouchTextView nameView;

		public TouchImageView coverView;

		public SelectBody(ListBody1 listBody) {
			listBody.super();
		}

		public TouchView initialize(int type) {
			if (type == 1) {
				this.cardView = (TouchView) mInflater.inflate(R.layout.view_location_item, null);
				this.iconView = (TouchImageView) this.cardView.findViewById(R.id.icon);
				this.nameView = (TouchTextView) this.cardView.findViewById(R.id.name);

				this.cardView.setOnTouchListener(thisController.mOnTouchListener);
				this.cardView.setOnClickListener(thisController.mOnClickListener);
			} else if (type == 0) {
				this.cardView = (TouchView) mInflater.inflate(R.layout.view_location_imageview, null);
				this.coverView = (TouchImageView) this.cardView.findViewById(R.id.cover);
				setConver(this.coverView);
			}
			return this.cardView;
		}

		public void setContent(int id, String name) {
			this.cardView.setTag(R.id.tag_class, "SelectBody");
			this.cardView.setTag(R.id.tag_first, id);
			this.iconView.setImageResource(id);
			this.iconView.setAlpha(0.875f);
			this.nameView.setText(name);
		}
	}

	public void setConver(ImageView imageView) {
		final Group group = data.relationship.groupsMap.get(data.localStatus.localData.currentSelectedGroup);
		File file = new File(fileHandlers.sdcardBackImageFolder, group.cover);
		if (group.cover == null || "".equals(group.cover)) {
			imageLoader.displayImage("drawable://" + R.drawable.tempicon, imageView);
			return;
		}
		final String path = file.getAbsolutePath();
		if (file.exists()) {
			imageLoader.displayImage("file://" + path, imageView, new SimpleImageLoadingListener() {
				@Override
				public void onLoadingStarted(String imageUri, View view) {
				}

				@Override
				public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
					// downloadConver(group.cover, path);
				}

				@Override
				public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
				}
			});
		} else {
			if (group.cover != null) {
				// downloadConver(group.cover, path);
			} else {
				imageLoader.displayImage("drawable://" + R.drawable.tempicon, imageView);
			}
		}
	}

	public View targetView;
	public BaseSpringSystem mSpringSystem = SpringSystem.create();
	public SpringConfig IMAGE_SPRING_CONFIG = SpringConfig.fromOrigamiTensionAndFriction(320, 9);
	public Spring dialogSpring = mSpringSystem.createSpring().setSpringConfig(IMAGE_SPRING_CONFIG);
	public DialogShowSpringListener dialogSpringListener = new DialogShowSpringListener();

	public class DialogShowSpringListener extends SimpleSpringListener {
		@Override
		public void onSpringUpdate(Spring spring) {
			float mappedValue = (float) spring.getCurrentValue();
			targetView.setScaleX(mappedValue);
			targetView.setScaleY(mappedValue);
		}

		public int id;

		@Override
		public void onSpringAtRest(Spring spring) {
			// if (spring.getCurrentValue() == 1) {
			// thisController.isTouchDown = false;
			// }
			if (id == R.drawable.sidebar_icon_discover_normal) {
				Intent intent = new Intent(thisController.thisActivity, ShareSectionActivity.class);
				intent.putExtra("key", "91");
				thisController.thisActivity.startActivity(intent);
			} else if (id == R.drawable.sidebar_icon_days_normal) {
				Intent intent = new Intent(thisController.thisActivity, NearbyActivity.class);
				intent.putExtra("type", "square");
				thisController.thisActivity.startActivity(intent);
			} else if (id == R.drawable.sidebar_icon_group_normal) {
				Intent intent = new Intent(thisController.thisActivity, NearbyActivity.class);
				intent.putExtra("type", "group");
				thisController.thisActivity.startActivity(intent);
			} else if (id == R.drawable.sidebar_icon_category_normal) {
				Intent intent = new Intent(thisController.thisActivity, ClassificationRecommendationActivity.class);
				thisController.thisActivity.startActivity(intent);
			} else if (id == R.drawable.sidebar_icon_test_normal) {
				Intent intent = new Intent(thisController.thisActivity, NearbyActivity.class);
				intent.putExtra("type", "account");
				thisController.thisActivity.startActivity(intent);
			} else if (id == R.drawable.sidebar_icon_beauty_normal) {
				Intent intent = new Intent(thisController.thisActivity, ShareListActivity.class);
				intent.putExtra("key", data.userInformation.currentUser.phone);
				thisController.thisActivity.startActivity(intent);
			}
		}
	}
}
