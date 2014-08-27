package com.open.welinks.view;

import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.open.welinks.R;
import com.open.welinks.controller.DownloadFile;
import com.open.welinks.controller.ShareMessageDetailController;
import com.open.welinks.model.API;
import com.open.welinks.model.Data;
import com.open.welinks.model.Data.ShareContent;
import com.open.welinks.model.Data.ShareContent.ShareContentItem;

public class ShareMessageDetailView {

	public Data data = Data.getInstance();
	public String tag = "ShareMessageDetailView";

	public Context context;
	public ShareMessageDetailView thisView;
	public ShareMessageDetailController thisController;
	public Activity thisActivity;

	public LayoutInflater mInflater;

	public ImageLoader imageLoader = ImageLoader.getInstance();
	public DisplayImageOptions displayImageOptions;

	float screenHeight;
	public float screenWidth;
	float screenDip;
	float screenDensity;
	public File mSdCardFile;
	public File mImageFile;

	public Gson gson = new Gson();

	public RelativeLayout backView;

	public LinearLayout shareMessageDetailContentView;
	public ScrollView mainScrollView;
	public InnerScrollView detailScrollView;

	public LinearLayout mainScrollInnerView;

	public LinearLayout praiseUserContentView;

	public RelativeLayout commentInputView;
	public EditText commentEditTextView;
	public RelativeLayout confirmSendCommentView;

	public ImageView commentIconView;
	public ImageView praiseIconView;

	public ShareMessageDetailView(Activity thisActivity) {
		this.thisActivity = thisActivity;
		this.context = thisActivity;
		thisView = this;

		mSdCardFile = Environment.getExternalStorageDirectory();
		mImageFile = new File(mSdCardFile, "welinks/images/");
		if (!mImageFile.exists())
			mImageFile.mkdirs();
	}

	public void initView() {
		initData();
		thisActivity.setContentView(R.layout.activity_share_message_detail);
		shareMessageDetailContentView = (LinearLayout) thisActivity.findViewById(R.id.shareMessageDetailContentView);
		mainScrollView = (ScrollView) thisActivity.findViewById(R.id.mainScrollView);
		detailScrollView = (InnerScrollView) thisActivity.findViewById(R.id.detailScrollView);
		mainScrollInnerView = (LinearLayout) thisActivity.findViewById(R.id.mainScrollInnerView);
		detailScrollView.parentScrollView = mainScrollView;
		mainScrollView.setOverScrollMode(View.OVER_SCROLL_NEVER);
		detailScrollView.setOverScrollMode(View.OVER_SCROLL_NEVER);

		praiseUserContentView = (LinearLayout) thisActivity.findViewById(R.id.praiseUserContentView);

		commentInputView = (RelativeLayout) thisActivity.findViewById(R.id.commentInputView);
		commentEditTextView = (EditText) thisActivity.findViewById(R.id.commentEditTextView);
		confirmSendCommentView = (RelativeLayout) thisActivity.findViewById(R.id.rl_sendComment);

		backView = (RelativeLayout) thisActivity.findViewById(R.id.backview);

		commentIconView = (ImageView) thisActivity.findViewById(R.id.commentIcon);
		praiseIconView = (ImageView) thisActivity.findViewById(R.id.praiseIconView);

		android.view.ViewGroup.LayoutParams detailScrollViewParams = detailScrollView.getLayoutParams();
		detailScrollViewParams.height = (int) (screenHeight - getStatusBarHeight(thisActivity) - 150 * screenDensity + 0.5f);

		showShareMessageDetail();
	}

	public void initData() {
		mInflater = thisActivity.getLayoutInflater();
		displayImageOptions = new DisplayImageOptions.Builder().showImageOnLoading(R.drawable.ic_stub).showImageForEmptyUri(R.drawable.ic_empty).showImageOnFail(R.drawable.ic_error).cacheInMemory(true).cacheOnDisk(true).considerExifParams(true).bitmapConfig(Bitmap.Config.RGB_565).build();
		DisplayMetrics displayMetrics = new DisplayMetrics();
		thisActivity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
		screenDensity = displayMetrics.density;
		screenDip = (int) (40 * screenDensity + 0.5f);
		screenHeight = displayMetrics.heightPixels;
		screenWidth = displayMetrics.widthPixels;
	}

	public ArrayList<String> showImages;

	public void showShareMessageDetail() {
		String content = thisController.shareMessage.content;
		ShareContent shareContent = gson.fromJson("{shareContentItems:" + content + "}", ShareContent.class);
		List<ShareContentItem> shareContentItems = shareContent.shareContentItems;
		String textContent = "";
		int index = 0;
		showImages = new ArrayList<String>();
		for (int i = 0; i < shareContentItems.size(); i++) {
			final ImageView imageView = new ImageView(thisActivity);
			shareMessageDetailContentView.addView(imageView);
			ShareContentItem shareContentItem = shareContentItems.get(i);
			String type = shareContentItem.type;
			if (type.equals("text")) {
				textContent = shareContentItem.detail;
				continue;
			}
			String imageFileName = shareContentItem.detail;

			imageView.setTag("ShareMessageDetailImage#" + index);
			index++;
			imageView.setOnClickListener(thisController.mOnClickListener);

			File currentImageFile = new File(mImageFile, imageFileName);
			String filepath = currentImageFile.getAbsolutePath();
			showImages.add(filepath);
			boolean isFlag = false;
			String path = "";
			if (currentImageFile.exists()) {
				BitmapFactory.Options boptions = new BitmapFactory.Options();
				boptions.inJustDecodeBounds = true;
				BitmapFactory.decodeFile(currentImageFile.getAbsolutePath(), boptions);
				if (boptions.outWidth > 0) {
					isFlag = true;
				}
			}
			if (isFlag) {
				path = "file://" + filepath;
			} else {
				path = API.DOMAIN_COMMONIMAGE + "images/" + imageFileName;
			}

			if (!isFlag) {
				DownloadFile downloadFile = new DownloadFile(path, filepath);
				downloadFile.view = imageView;
				downloadFile.setDownloadFileListener(thisController.downloadListener);
				thisController.downloadFileList.addDownloadFile(downloadFile);
			} else {
				imageLoader.displayImage(path, imageView, displayImageOptions, new SimpleImageLoadingListener() {
					@Override
					public void onLoadingStarted(String imageUri, View view) {
					}

					@Override
					public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
					}

					@Override
					public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
						int height = (int) (loadedImage.getHeight() * (screenWidth / loadedImage.getWidth()));
						LinearLayout.LayoutParams params = new LinearLayout.LayoutParams((int) screenWidth, height);
						imageView.setLayoutParams(params);
					}
				});
			}
		}
		if (!"".equals(textContent)) {
			TextView textview = new TextView(thisActivity);
			textview.setTextColor(Color.WHITE);
			textview.setBackgroundColor(Color.parseColor("#26ffffff"));
			textview.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
			int padding = (int) (10 * screenDensity + 0.5f);
			textview.setPadding(padding, padding, padding, padding);
			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
			textview.setLayoutParams(params);
			textview.setText(textContent);
			shareMessageDetailContentView.addView(textview);
		}
	}

	public static int getStatusBarHeight(Context context) {
		Class<?> c = null;
		Object obj = null;
		Field field = null;
		int x = 0, statusBarHeight = 0;
		try {
			c = Class.forName("com.android.internal.R$dimen");
			obj = c.newInstance();
			field = c.getField("status_bar_height");
			x = Integer.parseInt(field.get(obj).toString());
			statusBarHeight = context.getResources().getDimensionPixelSize(x);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		return statusBarHeight;
	}

}
