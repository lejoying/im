package com.open.welinks.view;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.open.lib.TouchImageView;
import com.open.welinks.R;
import com.open.welinks.controller.ShareReleaseImageTextController;
import com.open.welinks.model.Data;
import com.open.welinks.model.Data.LocalStatus.LocalData.ShareDraft;

public class ShareReleaseImageTextView {
	public Data data = Data.getInstance();
	public String tag = "ShareReleaseImageTextView";

	public Context context;
	public ShareReleaseImageTextView thisView;
	public ShareReleaseImageTextController thisController;
	public Activity thisActivity;

	public EditText mEditTextView;
	public RelativeLayout mImagesContentView;
	public RelativeLayout mReleaseButtomBarView;

	public TextView mCancleButtonView;
	public TextView mConfirmButtonView;
	public ImageView mSelectImageButtonView;
	public ImageView mFaceView;
	public ImageView mVoiceView;

	public DisplayMetrics displayMetrics;
	public ImageLoader imageLoader = ImageLoader.getInstance();
	public DisplayImageOptions options;

	public MyScrollImageBody myScrollImageBody;

	public int showImageHeight;

	public ShareReleaseImageTextView(Activity thisActivity) {
		this.context = thisActivity;
		this.thisActivity = thisActivity;
		this.thisView = this;
	}

	public void initView() {
		displayMetrics = new DisplayMetrics();
		thisActivity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
		if (thisController.gtype.equals("square")) {
			showImageHeight = (int) (displayMetrics.density * 115 + 0.5f);
		} else {
			showImageHeight = (int) (displayMetrics.widthPixels * thisController.imageHeightScale);
		}

		thisActivity.setContentView(R.layout.share_release_imagetext);
		mEditTextView = (EditText) thisActivity.findViewById(R.id.releaseTextContentView);
		mImagesContentView = (RelativeLayout) thisActivity.findViewById(R.id.releaseImagesContent);
		mReleaseButtomBarView = (RelativeLayout) thisActivity.findViewById(R.id.releaseButtomBar);
		mCancleButtonView = (TextView) thisActivity.findViewById(R.id.releaseCancel);
		mConfirmButtonView = (TextView) thisActivity.findViewById(R.id.releaseConfirm);
		mSelectImageButtonView = (ImageView) thisActivity.findViewById(R.id.selectImageButton);
		mFaceView = (ImageView) thisActivity.findViewById(R.id.releaseFace);
		mVoiceView = (ImageView) thisActivity.findViewById(R.id.releaseVoice);

		if (thisController.type.equals("text")) {
			mEditTextView.setHint("请输入文本内容");
		} else if (thisController.type.equals("album")) {
			mEditTextView.setHint("请输入相册描述");
		} else if (thisController.type.equals("imagetext")) {
			mEditTextView.setHint("请输入图文内容");
		}

		int widthItem = displayMetrics.widthPixels / 5;
		RelativeLayout.LayoutParams cancleParams = (LayoutParams) mCancleButtonView.getLayoutParams();
		cancleParams.leftMargin = 0;
		cancleParams.width = widthItem;

		RelativeLayout.LayoutParams mVoiceViewParams = (LayoutParams) mVoiceView.getLayoutParams();
		mVoiceViewParams.leftMargin = widthItem * 1;
		mVoiceViewParams.width = widthItem;

		RelativeLayout.LayoutParams mFaceViewParams = (LayoutParams) mFaceView.getLayoutParams();
		mFaceViewParams.leftMargin = widthItem * 2;
		mFaceViewParams.width = widthItem;

		RelativeLayout.LayoutParams mSelectImageButtonViewParams = (LayoutParams) mSelectImageButtonView.getLayoutParams();
		mSelectImageButtonViewParams.leftMargin = widthItem * 3;
		mSelectImageButtonViewParams.width = widthItem;

		RelativeLayout.LayoutParams mConfirmButtonViewParams = (LayoutParams) mConfirmButtonView.getLayoutParams();
		mConfirmButtonViewParams.leftMargin = widthItem * 4;
		mConfirmButtonViewParams.width = widthItem;

		options = new DisplayImageOptions.Builder().showImageOnLoading(R.drawable.ic_stub).showImageForEmptyUri(R.drawable.ic_empty).showImageOnFail(R.drawable.ic_error).considerExifParams(true).displayer(new RoundedBitmapDisplayer(0)).build();
		myScrollImageBody = new MyScrollImageBody();
		myScrollImageBody.initialize(mImagesContentView);

		thisController.parser.check();
		if (data.localStatus.localData.notSendShareMessagesMap != null) {
			ShareDraft shareDraft = data.localStatus.localData.notSendShareMessagesMap.get(thisController.gtype);
			if (shareDraft != null) {
				this.mEditTextView.setText(shareDraft.content);
				if (!"".equals(shareDraft.imagesContent)) {
					data.tempData.selectedImageList = thisController.gson.fromJson(shareDraft.imagesContent, new TypeToken<ArrayList<String>>() {
					}.getType());
					this.showSelectedImages();
				}
			}
		}
	}

	int width;

	public void showSelectedImages() {
		this.mImagesContentView.removeAllViews();
		ArrayList<String> selectedImageList = data.tempData.selectedImageList;
		if (selectedImageList.size() > 0) {
			this.mImagesContentView.setVisibility(View.VISIBLE);
			RelativeLayout.LayoutParams layoutParams = (LayoutParams) this.mEditTextView.getLayoutParams();
			layoutParams.bottomMargin = (int) (displayMetrics.density * 100 + 0.5f);
		} else {
			this.mImagesContentView.setVisibility(View.GONE);
			RelativeLayout.LayoutParams layoutParams = (LayoutParams) this.mEditTextView.getLayoutParams();
			layoutParams.bottomMargin = (int) (displayMetrics.density * 50 + 0.5f);
		}
		for (int i = 0; i < selectedImageList.size(); i++) {
			String key = selectedImageList.get(i);
			ImageBody imageBody = new ImageBody();
			imageBody.initialize();

			width = (int) (displayMetrics.density * 50);
			RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(width, width);
			myScrollImageBody.contentView.addView(imageBody.imageView, layoutParams);
			float x = i * (width + 2 * displayMetrics.density) + 2 * displayMetrics.density;
			if (i == 0) {
				x = 2 * displayMetrics.density;
			}
			imageBody.imageView.setX(x);// Translation
			imageLoader.displayImage("file://" + key, imageBody.imageView, options);
			myScrollImageBody.selectedImagesSequence.add(key);
			myScrollImageBody.selectedImagesSequenceMap.put(key, imageBody);
			imageBody.imageView.setTag(i);
			imageBody.imageView.setOnClickListener(thisController.monClickListener);
			imageBody.imageView.setOnTouchListener(thisController.mScrollOnTouchListener);
		}
		myScrollImageBody.contentView.setOnTouchListener(thisController.onTouchListener);
	}

	public class MyScrollImageBody {
		public ArrayList<String> selectedImagesSequence = new ArrayList<String>();
		public HashMap<String, ImageBody> selectedImagesSequenceMap = new HashMap<String, ImageBody>();

		public RelativeLayout contentView;

		public RelativeLayout initialize(RelativeLayout view) {
			this.contentView = view;
			return view;
		}

		public void recordChildrenPosition() {
			for (int i = 0; i < selectedImagesSequence.size(); i++) {
				String key = selectedImagesSequence.get(i);
				ImageBody imageBody = selectedImagesSequenceMap.get(key);
				imageBody.x = imageBody.imageView.getX();
				imageBody.y = imageBody.imageView.getY();
			}
		}

		public void setChildrenPosition(float deltaX, float deltaY) {
			float screenWidth = displayMetrics.widthPixels;
			float totalLength = selectedImagesSequence.size() * (width + 2 * displayMetrics.density) + 2 * displayMetrics.density;
			if (totalLength < screenWidth) {
				return;
			}
			for (int i = 0; i < selectedImagesSequence.size(); i++) {
				String key = selectedImagesSequence.get(i);
				ImageBody imageBody = selectedImagesSequenceMap.get(key);
				if ((imageBody.x + deltaX) < (screenWidth - totalLength))
					break;
				if (i == 0 && (imageBody.x + deltaX) > (5 * displayMetrics.density))
					break;
				imageBody.imageView.setX(imageBody.x + deltaX);
				imageBody.imageView.setY(imageBody.y + deltaY);
			}
		}
	}

	public class ImageBody {
		public int i;

		public float x;
		public float y;
		public TouchImageView imageView;

		public TouchImageView initialize() {
			this.imageView = new TouchImageView(context);
			return this.imageView;
		}
	}
}
