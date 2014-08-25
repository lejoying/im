package com.open.welinks.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Date;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;

import com.google.gson.Gson;
import com.open.welinks.ImagesDirectoryActivity;
import com.open.welinks.PictureBrowseActivity;
import com.open.welinks.controller.UploadMultipart.UploadLoadingListener;
import com.open.welinks.model.Data;
import com.open.welinks.model.Data.Shares.Share;
import com.open.welinks.model.Data.Shares.Share.ShareContent;
import com.open.welinks.model.Data.Shares.Share.ShareContent.ShareContentItem;
import com.open.welinks.model.Data.Shares.Share.ShareMessage;
import com.open.welinks.model.Data.UserInformation.User;
import com.open.welinks.utils.SHA1;
import com.open.welinks.utils.StreamParser;
import com.open.welinks.view.ShareReleaseImageTextView;
import com.open.welinks.view.ViewManage;

public class ShareReleaseImageTextController {
	public Data data = Data.getInstance();
	public String tag = "ShareReleaseImageTextController";

	public Context context;
	public ShareReleaseImageTextView thisView;
	public ShareReleaseImageTextController thisController;
	public Activity thisActivity;

	public OnClickListener monClickListener;
	public OnTouchListener monOnTouchListener;
	public OnTouchListener onTouchListener;
	public UploadLoadingListener uploadLoadingListener;

	public int RESULT_REQUESTCODE_SELECTIMAGE = 0x01;

	public SHA1 sha1 = new SHA1();
	public File mSdCardFile;
	public File mImageFile;

	public UploadMultipartList uploadMultipartList = UploadMultipartList.getInstance();

	public ViewManage viewManage = ViewManage.getInstance();

	public int currentUploadCount = 0;

	public String currentSelectedGroup = data.localStatus.localData.currentSelectedGroup;
	public User currentUser = data.userInformation.currentUser;

	public Gson gson = new Gson();

	public Handler handler = new Handler();

	public ShareReleaseImageTextController(Activity thisActivity) {
		this.context = thisActivity;
		this.thisActivity = thisActivity;

		// Initialize the image directory
		mSdCardFile = Environment.getExternalStorageDirectory();
		mImageFile = new File(mSdCardFile, "welinks/images/");
		if (!mImageFile.exists())
			mImageFile.mkdirs();
	}

	public void initializeListeners() {
		uploadLoadingListener = new UploadLoadingListener() {

			@Override
			public void loading(UploadMultipart instance, int precent, long time, int status) {
			}

			@Override
			public void success(UploadMultipart instance, int time) {
				currentUploadCount++;
				if (currentUploadCount == 1) {
					viewManage.mainView.shareSubView.showShareMessages();
				}
			}
		};
		onTouchListener = new OnTouchListener() {
			GestureDetector backviewDetector = new GestureDetector(thisActivity, new GestureDetector.SimpleOnGestureListener() {

				@Override
				public boolean onDown(MotionEvent event) {
					onTouchEvent(event);
					return true;
				}

				@Override
				public boolean onSingleTapUp(MotionEvent e) {
					Intent intent = new Intent(thisActivity, PictureBrowseActivity.class);
					intent.putExtra("position", "0");
					thisActivity.startActivity(intent);
					return true;
				}

			});

			@Override
			public boolean onTouch(View view, MotionEvent event) {
				return backviewDetector.onTouchEvent(event);
			}
		};
		monOnTouchListener = new OnTouchListener() {

			@Override
			public boolean onTouch(View view, MotionEvent event) {
				int motionEvent = event.getAction();
				if (motionEvent == MotionEvent.ACTION_DOWN) {
					view.setBackgroundColor(Color.argb(143, 0, 0, 0));
				} else if (motionEvent == MotionEvent.ACTION_UP) {
					view.setBackgroundColor(Color.parseColor("#00000000"));
				}
				return false;
			}
		};
		monClickListener = new OnClickListener() {

			@Override
			public void onClick(View view) {
				if (view == thisView.mCancleButtonView) {
					thisActivity.finish();
				} else if (view == thisView.mConfirmButtonView) {
					sendImageTextShare();
					// thisActivity.finish();
				} else if (view == thisView.mSelectImageButtonView) {
					Intent intent = new Intent(thisActivity, ImagesDirectoryActivity.class);
					thisActivity.startActivityForResult(intent, RESULT_REQUESTCODE_SELECTIMAGE);
				} else if (view.getTag() != null) {
					// selected images onclick handle
					// Log.e(tag, view.getTag().toString() +
					// "------------------current");
					// Intent intent = new Intent(thisActivity,
					// PictureBrowseActivity.class);
					// intent.putExtra("position", view.getTag().toString());
					// thisActivity.startActivity(intent);
				}
			}
		};
	}

	public void bindEvent() {
		thisView.mCancleButtonView.setOnClickListener(monClickListener);
		thisView.mConfirmButtonView.setOnClickListener(monClickListener);
		thisView.mSelectImageButtonView.setOnClickListener(monClickListener);
		thisView.mCancleButtonView.setOnTouchListener(monOnTouchListener);
		thisView.mConfirmButtonView.setOnTouchListener(monOnTouchListener);
		thisView.mSelectImageButtonView.setOnTouchListener(monOnTouchListener);

	}

	public void sendImageTextShare() {
		String sendContent = thisView.mEditTextView.getText().toString().trim();
		if ("".equals(sendContent))
			return;
		thisActivity.finish();
		if (data.tempData.selectedImageList != null) {
			copyFileToSprecifiedDirecytory();
		}
		if (data.shares.shareMap.get("") == null) {

		}
		// Package structure to share news
		if (data.shares == null) {
			data.shares = data.new Shares();
		}
		if (data.shares.shareMap.get(currentSelectedGroup) == null) {
			Share share = data.shares.new Share();
			data.shares.shareMap.put(currentSelectedGroup, share);
		}
		long time = new Date().getTime();
		Share share = data.shares.shareMap.get(currentSelectedGroup);
		ShareMessage shareMessage = share.new ShareMessage();
		shareMessage.mType = shareMessage.MESSAGE_TYPE_IMAGETEXT;
		shareMessage.gsid = currentUser.phone + "_" + time;
		shareMessage.type = "imagetext";
		shareMessage.phone = currentUser.phone;
		shareMessage.time = time;

		ShareContent shareContent = share.new ShareContent();
		ShareContentItem shareContentItem = shareContent.new ShareContentItem();
		shareContentItem.type = "text";
		shareContentItem.detail = sendContent;
		shareContent.shareContentItems.add(shareContentItem);

		ShareContentItem shareContentItem2 = shareContent.new ShareContentItem();
		shareContentItem2.type = "image";
		shareContentItem2.detail = data.tempData.selectedImageList.get(0);
		shareContent.shareContentItems.add(shareContentItem2);

		String content = gson.toJson(shareContent);
		Log.e(tag, content);
		// shareMessage.content = content;
		//
		// // To add data to the data
		// share.sharesOrder.add(0, shareMessage.gsid);
		// share.sharesMap.put(shareMessage.gsid, shareMessage);
		//
		// handler.post(new Runnable() {
		//
		// @Override
		// public void run() {
		// viewManage.mainView.shareSubView.showShareMessages();
		// }
		// });
	}

	public void copyFileToSprecifiedDirecytory() {
		// The current selected pictures gallery
		ArrayList<String> selectedImageList = data.tempData.selectedImageList;
		for (int i = 0; i < selectedImageList.size(); i++) {
			String key = selectedImageList.get(i);
			String suffixName = key.substring(key.lastIndexOf("."));
			if (suffixName.equals(".jpg") || suffixName.equals(".jpeg")) {
				suffixName = ".osj";
			} else if (suffixName.equals(".png")) {
				suffixName = ".osp";
			}
			try {
				File fromFile = new File(key);
				FileInputStream fileInputStream = new FileInputStream(fromFile);
				byte[] bytes = StreamParser.parseToByteArray(fileInputStream);
				String sha1FileName = sha1.getDigestOfString(bytes) + suffixName;
				File toFile = new File(mSdCardFile, sha1FileName);
				FileOutputStream fileOutputStream = new FileOutputStream(toFile);
				StreamParser.parseToFile(fileInputStream, fileOutputStream);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}

			UploadMultipart multipart = new UploadMultipart(key);
			uploadMultipartList.addMultipart(multipart);
			multipart.setUploadLoadingListener(uploadLoadingListener);
		}
	}

	public void onActivityResult(int requestCode, int resultCode, Intent data2) {
		if (requestCode == RESULT_REQUESTCODE_SELECTIMAGE && resultCode == Activity.RESULT_OK) {
			thisView.showSelectedImages();
		}
	}

	public void finish() {
		// data.tempData.selectedImageList = null;
	}

	public float pre_x = 0;
	public float pre_y = 0;
	long lastMillis = 0;

	public float pre_pre_x = 0;
	public float pre_pre_y = 0;
	long pre_lastMillis = 0;

	public void onTouchEvent(MotionEvent event) {
		float x = event.getX();
		float y = event.getY();
		long currentMillis = System.currentTimeMillis();

		if (event.getAction() == MotionEvent.ACTION_DOWN) {

			pre_x = x;
			pre_y = y;
			// Remember the current position
			thisView.myScrollImageBody.recordChildrenPosition();
		} else if (event.getAction() == MotionEvent.ACTION_MOVE) {
			if (lastMillis == 0) {
				lastMillis = currentMillis;
			}
			// Horizontal sliding
			thisView.myScrollImageBody.setChildrenPosition(x - pre_x, 0);

		} else if (event.getAction() == MotionEvent.ACTION_UP) {
			long delta = currentMillis - lastMillis;

			if (delta == 0 || x == pre_x || y == pre_y) {
				delta = currentMillis - pre_lastMillis;
				pre_x = pre_pre_x;
				pre_y = pre_pre_y;
			}
		}
	}
}
