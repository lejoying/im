package com.open.welinks.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Environment;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;

import com.open.welinks.ImagesDirectoryActivity;
import com.open.welinks.PictureBrowseActivity;
import com.open.welinks.controller.UploadMultipart.UploadLoadingListener;
import com.open.welinks.model.Data;
import com.open.welinks.utils.SHA1;
import com.open.welinks.utils.StreamParser;
import com.open.welinks.view.ShareReleaseImageTextView;

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

	public int currentUploadCount;

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
		if (data.tempData.selectedImageList != null) {
			copyFileToSprecifiedDirecytory();
		}
		if (data.shares.shareMap.get("") == null) {

		}
		// ShareMessage shareContent = new ShareMessage();
		// ShareContentItem shareContentItem = new ShareContentItem();

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
		data.tempData.selectedImageList = null;
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
