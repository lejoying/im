package com.open.welinks.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

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
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;

import com.google.gson.Gson;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.open.welinks.ImagesDirectoryActivity;
import com.open.welinks.PictureBrowseActivity;
import com.open.welinks.controller.UploadMultipart.UploadLoadingListener;
import com.open.welinks.model.Data;
import com.open.welinks.model.Data.ShareContent;
import com.open.welinks.model.Data.ShareContent.ShareContentItem;
import com.open.welinks.model.Data.Shares.Share;
import com.open.welinks.model.Data.Shares.Share.ShareMessage;
import com.open.welinks.model.Data.UserInformation.User;
import com.open.welinks.model.ResponseHandlers;
import com.open.welinks.utils.SHA1;
import com.open.welinks.utils.StreamParser;
import com.open.welinks.view.PictureBrowseView;
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
	public Map<String, String> uploadFileNameMap = new HashMap<String, String>();

	public String currentSelectedGroup = data.localStatus.localData.currentSelectedGroup;
	public User currentUser = data.userInformation.currentUser;

	public Gson gson = new Gson();

	public Handler handler = new Handler();

	public int IMAGEBROWSE_REQUESTCODE_OPTION = 0x01;

	public ShareReleaseImageTextController(Activity thisActivity) {
		this.context = thisActivity;
		this.thisActivity = thisActivity;

		// Initialize the image directory
		mSdCardFile = Environment.getExternalStorageDirectory();
		mImageFile = new File(mSdCardFile, "welinks/images/");
		if (!mImageFile.exists())
			mImageFile.mkdirs();

		data.tempData.selectedImageList = null;
	}

	public OnTouchListener mScrollOnTouchListener;

	public void initializeListeners() {
		mScrollOnTouchListener = new OnTouchListener() {
			GestureDetector backviewDetector = new GestureDetector(thisActivity, new GestureDetector.SimpleOnGestureListener() {

				@Override
				public boolean onDown(MotionEvent event) {
					return onTouchEvent(event);
				}

				@Override
				public boolean onSingleTapUp(MotionEvent e) {
					Intent intent = new Intent(thisActivity, PictureBrowseActivity.class);
					intent.putExtra("position", "0");
					intent.putExtra("type", PictureBrowseView.IMAGEBROWSE_OPTION);
					thisActivity.startActivityForResult(intent, IMAGEBROWSE_REQUESTCODE_OPTION);
					return true;
				}

				@Override
				public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
					// TODO Auto-generated method stub
					return super.onScroll(e1, e2, distanceX, distanceY);
				}

			});

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				return backviewDetector.onTouchEvent(event);
			}
		};
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
					return onTouchEvent(event);
				}

				@Override
				public boolean onSingleTapUp(MotionEvent e) {
					Intent intent = new Intent(thisActivity, PictureBrowseActivity.class);
					intent.putExtra("position", "0");
					intent.putExtra("type", PictureBrowseView.IMAGEBROWSE_OPTION);
					thisActivity.startActivityForResult(intent, IMAGEBROWSE_REQUESTCODE_OPTION);
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
					data.tempData.selectedImageList = null;
				} else if (view == thisView.mConfirmButtonView) {
					sendImageTextShare();
				} else if (view == thisView.mSelectImageButtonView) {
					Intent intent = new Intent(thisActivity, ImagesDirectoryActivity.class);
					thisActivity.startActivityForResult(intent, RESULT_REQUESTCODE_SELECTIMAGE);
				} else if (view.getTag() != null) {
					// selected images onclick handle
					Log.e(tag, view.getTag().toString() + "------------------current");
					Intent intent = new Intent(thisActivity, PictureBrowseActivity.class);
					intent.putExtra("position", view.getTag().toString());
					thisActivity.startActivityForResult(intent, IMAGEBROWSE_REQUESTCODE_OPTION);
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
		final String sendContent = thisView.mEditTextView.getText().toString().trim();
		if ("".equals(sendContent))
			return;
		thisActivity.finish();
		new Thread(new Runnable() {

			@Override
			public void run() {
				// if (data.shares.shareMap.get("") == null) {
				//
				// }
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
				shareMessage.status = "sending";

				ShareContent shareContent = data.new ShareContent();
				ShareContentItem shareContentItem = shareContent.new ShareContentItem();
				shareContentItem.type = "text";
				shareContentItem.detail = sendContent;
				shareContent.shareContentItems.add(shareContentItem);

				if (data.tempData.selectedImageList != null) {
					copyFileToSprecifiedDirecytory(shareContent, shareContent.shareContentItems);
				}

				String content = gson.toJson(shareContent.shareContentItems);
				Log.e(tag, content);
				shareMessage.content = content;

				// To add data to the data
				share.sharesOrder.add(0, shareMessage.gsid);
				share.sharesMap.put(shareMessage.gsid, shareMessage);
				sendMessageToServer(content, shareMessage.gsid);

				// Local data diaplay in MainHandler
				handler.post(new Runnable() {

					@Override
					public void run() {
						viewManage.mainView.shareSubView.showShareMessages();
					}
				});
				// init tempData data
				data.tempData.selectedImageList = null;
			}
		}).start();
	}

	public class SendShareMessage {
		public String type;// imagetext voicetext vote
		public String content;
	}

	public void sendMessageToServer(String content, String gsid) {
		SendShareMessage sendShareMessage = new SendShareMessage();
		sendShareMessage.type = "imagetext";
		sendShareMessage.content = content;

		HttpUtils httpUtils = new HttpUtils();
		RequestParams params = new RequestParams();
		params.addBodyParameter("phone", currentUser.phone);
		params.addBodyParameter("accessKey", currentUser.accessKey);
		params.addBodyParameter("gid", currentSelectedGroup);
		params.addBodyParameter("ogsid", gsid);
		params.addBodyParameter("message", gson.toJson(sendShareMessage));

		ResponseHandlers responseHandlers = ResponseHandlers.getInstance();

		httpUtils.send(HttpMethod.POST, "http://www.we-links.com/api2/share/sendshare", params, responseHandlers.share_sendShareCallBack);
	}

	public void copyFileToSprecifiedDirecytory(ShareContent shareContent, List<ShareContentItem> shareContentItems) {
		// The current selected pictures gallery
		ArrayList<String> selectedImageList = data.tempData.selectedImageList;
		for (int i = 0; i < selectedImageList.size(); i++) {
			String key = selectedImageList.get(i);
			String suffixName = key.substring(key.lastIndexOf("."));
			suffixName = suffixName.toLowerCase(Locale.getDefault());
			if (suffixName.equals(".jpg") || suffixName.equals(".jpeg")) {
				suffixName = ".osj";
			} else if (suffixName.equals(".png")) {
				suffixName = ".osp";
			}
			try {
				String fileName = "";
				File fromFile = new File(key);
				FileInputStream fileInputStream = new FileInputStream(fromFile);
				byte[] bytes = StreamParser.parseToByteArray(fileInputStream);
				String sha1FileName = sha1.getDigestOfString(bytes);
				fileName = sha1FileName + suffixName;
				File toFile = new File(mImageFile, fileName);
				FileOutputStream fileOutputStream = new FileOutputStream(toFile);
				StreamParser.parseToFile(bytes, fileOutputStream);
				fileInputStream.close();

				ShareContentItem shareContentItem = shareContent.new ShareContentItem();
				shareContentItem.type = "image";
				shareContentItem.detail = fileName;
				shareContentItems.add(shareContentItem);

				// upload file to oss server
				uploadFileNameMap.put(key, fileName);
				UploadMultipart multipart = new UploadMultipart(key, fileName, bytes);
				uploadMultipartList.addMultipart(multipart);
				multipart.setUploadLoadingListener(uploadLoadingListener);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public void onActivityResult(int requestCode, int resultCode, Intent data2) {
		if (requestCode == RESULT_REQUESTCODE_SELECTIMAGE && resultCode == Activity.RESULT_OK) {
			thisView.showSelectedImages();
		} else {
			Log.e(tag, "------------------result");
			ArrayList<String> selectedImageList = data.tempData.selectedImageList;
			if (selectedImageList != null) {
				if (selectedImageList.size() > 0) {
					RelativeLayout.LayoutParams layoutParams = (LayoutParams) thisView.mEditTextView.getLayoutParams();
					layoutParams.bottomMargin = (int) (thisView.displayMetrics.density * 100 + 0.5f);
					thisView.mImagesContentView.setVisibility(View.VISIBLE);
					if (selectedImageList.size() != thisView.mImagesContentView.getChildCount()) {
						thisView.showSelectedImages();
					}
				} else {
					RelativeLayout.LayoutParams layoutParams = (LayoutParams) thisView.mEditTextView.getLayoutParams();
					layoutParams.bottomMargin = (int) (thisView.displayMetrics.density * 50 + 0.5f);
					thisView.mImagesContentView.setVisibility(View.GONE);
				}
			} else {
				RelativeLayout.LayoutParams layoutParams = (LayoutParams) thisView.mEditTextView.getLayoutParams();
				layoutParams.bottomMargin = (int) (thisView.displayMetrics.density * 50 + 0.5f);
				thisView.mImagesContentView.setVisibility(View.GONE);
			}
		}
	}

	public void onBackPressed() {
		data.tempData.selectedImageList = null;
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

	public boolean onTouchEvent(MotionEvent event) {
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
		return true;
	}
}
