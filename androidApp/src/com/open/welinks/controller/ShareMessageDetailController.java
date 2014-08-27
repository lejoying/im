package com.open.welinks.controller;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.open.welinks.PictureBrowseActivity;
import com.open.welinks.controller.DownloadFile.DownloadListener;
import com.open.welinks.model.Data;
import com.open.welinks.model.Data.Shares.Share.ShareMessage;
import com.open.welinks.view.InnerScrollView.OnScrollChangedListener;
import com.open.welinks.view.PictureBrowseView;
import com.open.welinks.view.ShareMessageDetailView;

public class ShareMessageDetailController {

	public Data data = Data.getInstance();
	public String tag = "ShareMessageDetailController";

	public Context context;
	public ShareMessageDetailView thisView;
	public ShareMessageDetailController thisController;
	public Activity thisActivity;

	public String gsid = "";
	public ShareMessage shareMessage;

	public OnClickListener mOnClickListener;
	public OnScrollChangedListener mOnScrollChangedListener;
	public OnTouchListener mOnTouchListener;
	public DownloadListener downloadListener;

	public int IMAGEBROWSE_REQUESTCODE = 0x01;

	public DownloadFileList downloadFileList = DownloadFileList.getInstance();

	public ShareMessageDetailController(Activity thisActivity) {
		this.thisActivity = thisActivity;
		this.context = thisActivity;
		thisController = this;
	}

	public void initData() {
		String gsid = thisActivity.getIntent().getStringExtra("gsid");
		if (gsid != null) {
			this.gsid = gsid;
			shareMessage = data.shares.shareMap.get(data.localStatus.localData.currentSelectedGroup).sharesMap.get(gsid);
		}
	}

	public void initializeListeners() {
		downloadListener = new DownloadListener() {

			@Override
			public void success(DownloadFile instance, int status) {
				final ImageView imageView = (ImageView) instance.view;
				thisView.imageLoader.displayImage("file://" + instance.path, imageView, thisView.displayImageOptions, new SimpleImageLoadingListener() {
					@Override
					public void onLoadingStarted(String imageUri, View view) {
					}

					@Override
					public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
					}

					@Override
					public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
						int height = (int) (loadedImage.getHeight() * (thisView.screenWidth / loadedImage.getWidth()));
						LinearLayout.LayoutParams params = new LinearLayout.LayoutParams((int) thisView.screenWidth, height);
						imageView.setLayoutParams(params);
					}
				});
			}

			@Override
			public void loading(DownloadFile instance, int precent, int status) {
				// TODO Auto-generated method stub

			}
		};
		mOnTouchListener = new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (thisView.commentInputView.getVisibility() == View.VISIBLE) {
					thisView.commentInputView.setVisibility(View.GONE);
				}
				return false;
			}
		};
		mOnScrollChangedListener = new OnScrollChangedListener() {

			@Override
			public void onScrollChangedListener(int l, int t, int oldl, int oldt) {
				if (thisView.commentInputView.getVisibility() == View.VISIBLE) {
					thisView.commentInputView.setVisibility(View.GONE);
				}
			}
		};
		mOnClickListener = new OnClickListener() {

			@Override
			public void onClick(View view) {
				if (view.equals(thisView.backView)) {
					thisActivity.finish();
				} else if (view.equals(thisView.praiseIconView)) {
					Toast.makeText(thisActivity, "praiseICon", Toast.LENGTH_SHORT).show();
				} else if (view.equals(thisView.commentIconView)) {
					if (thisView.commentInputView.getVisibility() == View.GONE) {
						thisView.commentInputView.setVisibility(View.VISIBLE);
						int offset = thisView.mainScrollInnerView.getMeasuredHeight() - thisView.mainScrollView.getHeight();
						thisView.mainScrollView.scrollTo(0, offset);
					} else {
						thisView.commentInputView.setVisibility(View.GONE);
					}
				} else if (view.equals(thisView.praiseUserContentView)) {
					Toast.makeText(thisActivity, "praiseUserContentView", Toast.LENGTH_SHORT).show();
				} else if (view.equals(thisView.confirmSendCommentView)) {
					Toast.makeText(thisActivity, "confirmSendCommentView", Toast.LENGTH_SHORT).show();
				} else if (view.getTag() != null) {
					String tagContent = (String) view.getTag();
					int index = tagContent.lastIndexOf("#");
					String type = tagContent.substring(0, index);
					String content = tagContent.substring(index + 1);
					if ("ShareMessageDetailImage".equals(type)) {
						data.tempData.selectedImageList = thisView.showImages;
						Intent intent = new Intent(thisActivity, PictureBrowseActivity.class);
						intent.putExtra("position", content);
						intent.putExtra("type", PictureBrowseView.IMAGEBROWSE_COMMON);
						thisActivity.startActivityForResult(intent, IMAGEBROWSE_REQUESTCODE);
						Toast.makeText(thisActivity, "ShareMessageDetailImage---------" + content, Toast.LENGTH_SHORT).show();
					}
				}
			}
		};
	}

	public void bindEvent() {
		thisView.backView.setOnClickListener(mOnClickListener);
		thisView.praiseUserContentView.setOnClickListener(mOnClickListener);
		thisView.praiseIconView.setOnClickListener(mOnClickListener);
		thisView.commentIconView.setOnClickListener(mOnClickListener);
		thisView.confirmSendCommentView.setOnClickListener(mOnClickListener);

		thisView.mainScrollView.setOnTouchListener(mOnTouchListener);
		thisView.detailScrollView.setOnScrollChangedListener(mOnScrollChangedListener);

	}

	public void finish() {
		data.tempData.selectedImageList = null;
	}
}
