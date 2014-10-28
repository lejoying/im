package com.open.welinks.controller;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.util.Log;
import android.view.GestureDetector;
import android.view.KeyEvent;
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
import com.open.lib.MyLog;
import com.open.welinks.ImageScanActivity;
import com.open.welinks.ImagesDirectoryActivity;
import com.open.welinks.customListener.OnUploadLoadingListener;
import com.open.welinks.customView.Alert;
import com.open.welinks.customView.Alert.AlertInputDialog;
import com.open.welinks.customView.Alert.AlertInputDialog.OnDialogClickListener;
import com.open.welinks.customView.ControlProgress;
import com.open.welinks.model.API;
import com.open.welinks.model.Data;
import com.open.welinks.model.Data.LocalStatus.LocalData.ShareDraft;
import com.open.welinks.model.Data.Shares.Share;
import com.open.welinks.model.Data.Shares.Share.ShareMessage;
import com.open.welinks.model.Data.UserInformation.User;
import com.open.welinks.model.FileHandlers;
import com.open.welinks.model.Parser;
import com.open.welinks.model.ResponseHandlers;
import com.open.welinks.model.SubData;
import com.open.welinks.model.SubData.ShareContent;
import com.open.welinks.model.SubData.ShareContent.ShareContentItem;
import com.open.welinks.utils.SHA1;
import com.open.welinks.utils.StreamParser;
import com.open.welinks.view.ShareReleaseImageTextView;
import com.open.welinks.view.ShareSubView.SharesMessageBody;
import com.open.welinks.view.ViewManage;

public class ShareReleaseImageTextController {
	public Data data = Data.getInstance();
	public Parser parser = Parser.getInstance();
	public String tag = "ShareReleaseImageTextController";

	public MyLog log = new MyLog(tag, true);

	public Context context;
	public ShareReleaseImageTextView thisView;
	public ShareReleaseImageTextController thisController;
	public Activity thisActivity;

	public OnClickListener monClickListener;
	public OnTouchListener monOnTouchListener;
	public OnTouchListener onTouchListener;
	public OnUploadLoadingListener uploadLoadingListener;

	public int RESULT_REQUESTCODE_SELECTIMAGE = 0x01;

	public FileHandlers fileHandlers = FileHandlers.getInstance();
	public SHA1 sha1 = new SHA1();
	public File sdcardFolder;
	public File sdcardImageFolder;
	public File sdcardThumbnailFolder;

	public UploadMultipartList uploadMultipartList = UploadMultipartList.getInstance();

	public ViewManage viewManage = ViewManage.getInstance();

	public int currentUploadCount = 0;
	public int totalUploadCount = 0;
	public Map<String, String> uploadFileNameMap = new HashMap<String, String>();

	public String currentSelectedGroup = "";
	public User currentUser = data.userInformation.currentUser;

	public Gson gson = new Gson();

	public Handler handler = new Handler();

	public int IMAGEBROWSE_REQUESTCODE_OPTION = 0x01;

	public String type, gid, gtype;

	public ShareMessage shareMessage;

	public ShareReleaseImageTextController(Activity thisActivity) {
		this.context = thisActivity;
		this.thisActivity = thisActivity;

		gtype = thisActivity.getIntent().getStringExtra("gtype");
		type = thisActivity.getIntent().getStringExtra("type");
		gid = thisActivity.getIntent().getStringExtra("gid");
		currentSelectedGroup = gid;
		// Initialize the image directory
		sdcardImageFolder = fileHandlers.sdcardImageFolder;
		if (gtype.equals("square")) {
			sdcardThumbnailFolder = fileHandlers.sdcardSquareThumbnailFolder;
		} else {
			sdcardThumbnailFolder = fileHandlers.sdcardThumbnailFolder;
			// showImageHeight = (int) (thisView.displayMetrics.widthPixels *
			// imageHeightScale);
		}
		data.tempData.selectedImageList = null;

	}

	public OnTouchListener mScrollOnTouchListener;

	int totalLength = 0;
	Map<String, Integer> fileTotalLengthMap = new HashMap<String, Integer>();
	Map<String, Integer> currentUploadLength = new HashMap<String, Integer>();

	public void initializeListeners() {
		uploadLoadingListener = new OnUploadLoadingListener() {

			@Override
			public void onLoading(UploadMultipart instance, int precent, long time, int status) {
				double currentLength = 0;
				String path = instance.path;
				int singleLength = fileTotalLengthMap.get(path);
				int length = singleLength * precent / 100;
				currentUploadLength.put(path, length);
				// currentLength += length;

				for (Entry<String, Integer> entry : currentUploadLength.entrySet()) {
					String key = entry.getKey();
					int value = entry.getValue();
					if (key != null && !"".equals(key)) {
						currentLength += value;
					}
				}
				final double currentPrecent = currentLength / totalLength;
				fileHandlers.handler.post(new Runnable() {

					@Override
					public void run() {
						if (shareMessage != null) {
							String keyName = "message#" + shareMessage.gsid;
							SharesMessageBody listItemBody = (SharesMessageBody) viewManage.shareSubView.shareMessageListBody.listItemBodiesMap.get(keyName);
							if (listItemBody != null) {
								ControlProgress controlProgress = listItemBody.controlProgress;
								if (controlProgress != null) {
									controlProgress.moveTo((int) (currentPrecent * 100));
								}
							}
						}
					}
				});
			}

			@Override
			public void onSuccess(UploadMultipart instance, int time) {
				// progress bar down
				int singleLength = fileTotalLengthMap.get(instance.path);
				currentUploadLength.put(instance.path, singleLength);
				double currentLength = 0;
				for (Entry<String, Integer> entry : currentUploadLength.entrySet()) {
					String key = entry.getKey();
					int value = entry.getValue();
					if (key != null && !"".equals(key)) {
						currentLength += value;
					}
				}
				final double currentPrecent = currentLength / totalLength;
				log.e("currentPrecent：" + currentPrecent);
				fileHandlers.handler.post(new Runnable() {

					@Override
					public void run() {
						if (shareMessage != null) {
							// log.e("shareMessage：" + shareMessage);
							String keyName = "message#" + shareMessage.gsid;
							SharesMessageBody listItemBody = (SharesMessageBody) viewManage.shareSubView.shareMessageListBody.listItemBodiesMap.get(keyName);
							if (listItemBody != null) {
								// log.e("listItemBody：" + listItemBody);
								ControlProgress controlProgress = listItemBody.controlProgress;
								if (controlProgress != null) {
									// log.e("controlProgress：" + controlProgress);
									controlProgress.moveTo((int) (currentPrecent * 100));
								} else {
									log.e("controlProgress");
								}
							} else {
								log.e("listItemBody");
							}
						} else {
							log.e("shareMessage：null");
						}
					}
				});
				// progress bar up

				currentUploadCount++;
				if (currentUploadCount == 1) {
					if (gtype.equals("group") || gtype.equals("share")) {
						viewManage.postNotifyView("ShareSubViewMessage");
					} else {
						viewManage.postNotifyView("SquareSubViewMessage");
					}
					if (totalUploadCount == currentUploadCount) {
						sendMessageToServer(shareMessage.content, shareMessage.gsid);
					}
				} else if (totalUploadCount == currentUploadCount) {
					sendMessageToServer(shareMessage.content, shareMessage.gsid);
				}
			}
		};
		mScrollOnTouchListener = new OnTouchListener() {
			GestureDetector backviewDetector = new GestureDetector(thisActivity, new GestureDetector.SimpleOnGestureListener() {

				@Override
				public boolean onDown(MotionEvent event) {
					return onTouchEvent(event);
				}

				@Override
				public boolean onSingleTapUp(MotionEvent e) {
					Intent intent = new Intent(thisActivity, ImageScanActivity.class);
					intent.putExtra("position", "0");
					intent.putExtra("type", ImageScanActivity.IMAGEBROWSE_OPTION);
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
		onTouchListener = new OnTouchListener() {
			GestureDetector backviewDetector = new GestureDetector(thisActivity, new GestureDetector.SimpleOnGestureListener() {

				@Override
				public boolean onDown(MotionEvent event) {
					return onTouchEvent(event);
				}

				@Override
				public boolean onSingleTapUp(MotionEvent e) {
					Intent intent = new Intent(thisActivity, ImageScanActivity.class);
					intent.putExtra("position", "0");
					intent.putExtra("type", ImageScanActivity.IMAGEBROWSE_OPTION);
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
					saveDraftDialog();
				} else if (view == thisView.mConfirmButtonView) {
					parser.check();
					if (data.localStatus.localData.notSendShareMessagesMap != null) {
						data.localStatus.localData.notSendShareMessagesMap.remove(gtype);
					}
					sendImageTextShare();
				} else if (view == thisView.mSelectImageButtonView) {
					Intent intent = new Intent(thisActivity, ImagesDirectoryActivity.class);
					thisActivity.startActivityForResult(intent, RESULT_REQUESTCODE_SELECTIMAGE);
				} else if (view.getTag() != null) {
					// selected images onclick handle
					Log.e(tag, view.getTag().toString() + "------------------current");
					Intent intent = new Intent(thisActivity, ImageScanActivity.class);
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
		thisView.mVoiceView.setOnTouchListener(monOnTouchListener);
		thisView.mFaceView.setOnTouchListener(monOnTouchListener);

	}

	public void sendImageTextShare() {

		final String sendContent = thisView.mEditTextView.getText().toString().trim();
		boolean flag = false;
		if (data.tempData.selectedImageList == null) {
			flag = true;
		} else {
			if (data.tempData.selectedImageList.size() == 0) {
				flag = true;
			} else {
				flag = false;
			}
		}
		if ("".equals(sendContent) && flag) {
			return;
		}
		viewManage.shareSubView.isShowFirstMessageAnimation = true;
		thisActivity.finish();
		new Thread(new Runnable() {

			@Override
			public void run() {
				// Package structure to share news
				long time = new Date().getTime();
				parser.check();
				ShareDraft shareDraft = data.localStatus.localData.new ShareDraft();
				shareDraft.gid = gid;
				shareDraft.gsid = currentUser.phone + "_" + time;
				shareDraft.gtype = gtype;
				shareDraft.content = thisView.mEditTextView.getText().toString();
				if (data.tempData.selectedImageList != null) {
					if (data.tempData.selectedImageList.size() != 0) {
						shareDraft.imagesContent = gson.toJson(data.tempData.selectedImageList);
					} else {
						shareDraft.imagesContent = "";
					}
				} else {
					shareDraft.imagesContent = "";
				}
				if (data.localStatus.localData.shareReleaseSequece == null) {
					data.localStatus.localData.shareReleaseSequece = new ArrayList<String>();
				}
				if (data.localStatus.localData.shareReleaseSequeceMap == null) {
					data.localStatus.localData.shareReleaseSequeceMap = new HashMap<String, ShareDraft>();
				}
				data.localStatus.localData.shareReleaseSequece.add(shareDraft.gsid);
				data.localStatus.localData.shareReleaseSequeceMap.put(shareDraft.gsid, shareDraft);

				if (data.shares == null) {
					data.shares = data.new Shares();
				}
				if (data.shares.shareMap.get(currentSelectedGroup) == null) {
					Share share = data.shares.new Share();
					data.shares.shareMap.put(currentSelectedGroup, share);
				}
				Share share = data.shares.shareMap.get(currentSelectedGroup);
				shareMessage = share.new ShareMessage();
				shareMessage.mType = shareMessage.MESSAGE_TYPE_IMAGETEXT;
				shareMessage.gsid = currentUser.phone + "_" + time;
				shareMessage.type = "imagetext";
				shareMessage.phone = currentUser.phone;
				shareMessage.time = time;
				shareMessage.status = "sending";

				ShareContent shareContent = SubData.getInstance().new ShareContent();
				ShareContentItem shareContentItem = shareContent.new ShareContentItem();
				shareContentItem.type = "text";
				shareContentItem.detail = sendContent;
				shareContent.shareContentItems.add(shareContentItem);

				if (data.tempData.selectedImageList != null) {
					totalUploadCount = data.tempData.selectedImageList.size();
					if (totalUploadCount != 0) {
						copyFileToSprecifiedDirecytory(shareContent, shareContent.shareContentItems);
					} else {
						String content = gson.toJson(shareContent.shareContentItems);
						sendMessageToServer(content, shareMessage.gsid);
					}
				} else {
					String content = gson.toJson(shareContent.shareContentItems);
					sendMessageToServer(content, shareMessage.gsid);
				}

				String content = gson.toJson(shareContent.shareContentItems);
				Log.e(tag, content);
				shareMessage.content = content;

				// To add data to the data
				share.shareMessagesOrder.add(0, shareMessage.gsid);
				share.shareMessagesMap.put(shareMessage.gsid, shareMessage);
				data.shares.isModified = true;

				// Local data diaplay in MainHandler
				if ("square".equals(gtype)) {
					viewManage.postNotifyView("SquareSubViewMessage");
				}
				if ("share".equals(gtype)) {
					viewManage.postNotifyView("ShareSubViewMessage");
				}

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
		responseHandlers.share_sendShareCallBack.gid = currentSelectedGroup;
		responseHandlers.share_sendShareCallBack.ogsid = gsid;

		httpUtils.send(HttpMethod.POST, API.SHARE_SENDSHARE, params, responseHandlers.share_sendShareCallBack);
	}

	public float imageHeightScale = 0.5686505598114319f;

	public void copyFileToSprecifiedDirecytory(ShareContent shareContent, List<ShareContentItem> shareContentItems) {
		// The current selected pictures gallery
		ArrayList<String> selectedImageList = data.tempData.selectedImageList;
		log.e("copy file to the position,file count:" + selectedImageList.size());
		totalLength = 0;
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
				byte[] bytes = null;
				bytes = fileHandlers.getImageFileBytes(fromFile, thisView.displayMetrics.heightPixels, thisView.displayMetrics.heightPixels);
				int fileLength = bytes.length;
				totalLength += fileLength;
				fileTotalLengthMap.put(key, fileLength);

				String sha1FileName = sha1.getDigestOfString(bytes);
				fileName = sha1FileName + suffixName;
				File toFile = new File(sdcardImageFolder, fileName);
				FileOutputStream fileOutputStream = new FileOutputStream(toFile);
				StreamParser.parseToFile(bytes, fileOutputStream);

				if (i == 0) {
					int showImageWidth = (int) (thisView.displayMetrics.widthPixels - 20 * thisView.displayMetrics.density - 0.5f);
					File toSnapFile = new File(sdcardThumbnailFolder, fileName);
					fileHandlers.makeImageThumbnail(fromFile, showImageWidth, thisView.showImageHeight, toSnapFile, fileName);
				}

				ShareContentItem shareContentItem = shareContent.new ShareContentItem();
				shareContentItem.type = "image";
				shareContentItem.detail = fileName;
				shareContentItems.add(shareContentItem);

				// upload file to oss server
				uploadFileNameMap.put(key, fileName);
				UploadMultipart multipart = new UploadMultipart(key, fileName, bytes, UploadMultipart.UPLOAD_TYPE_IMAGE);
				bytes = null;
				System.gc();
				multipart.path = key;
				uploadMultipartList.addMultipart(multipart);
				multipart.setUploadLoadingListener(uploadLoadingListener);
			} catch (Exception e) {
				e.printStackTrace();
				log.e(e.toString());
			}
		}
	}

	public void onActivityResult(int requestCode, int resultCode, Intent data2) {
		if (requestCode == RESULT_REQUESTCODE_SELECTIMAGE && resultCode == Activity.RESULT_OK) {
			thisView.showSelectedImages();
		} else {
			// Log.e(tag, "------------------result");
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

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			saveDraftDialog();
		}
		return false;
	}

	public void saveDraftDialog() {
		String content = thisView.mEditTextView.getText().toString();
		boolean flag = false;
		if (data.tempData.selectedImageList == null) {
			flag = false;
		} else {
			if (data.tempData.selectedImageList.size() == 0) {
				flag = false;
			} else {
				flag = true;
			}
		}
		if (!"".equals(content) || flag) {
			Alert.createDialog(thisActivity).setTitle("是否保存草稿？").setOnConfirmClickListener(new OnDialogClickListener() {

				@Override
				public void onClick(AlertInputDialog dialog) {
					parser.check();
					ShareDraft shareDraft = data.localStatus.localData.new ShareDraft();
					shareDraft.content = thisView.mEditTextView.getText().toString();
					if (data.tempData.selectedImageList != null) {
						if (data.tempData.selectedImageList.size() != 0) {
							shareDraft.imagesContent = gson.toJson(data.tempData.selectedImageList);
						} else {
							shareDraft.imagesContent = "";
						}
					} else {
						shareDraft.imagesContent = "";
					}
					data.tempData.selectedImageList = null;
					if (data.localStatus.localData.notSendShareMessagesMap == null) {
						data.localStatus.localData.notSendShareMessagesMap = new HashMap<String, ShareDraft>();
					}
					data.localStatus.localData.notSendShareMessagesMap.put(gtype, shareDraft);
					thisActivity.finish();
				}
			}).setOnCancelClickListener(new OnDialogClickListener() {

				@Override
				public void onClick(AlertInputDialog dialog) {
					parser.check();
					if (data.localStatus.localData.notSendShareMessagesMap == null) {
						data.localStatus.localData.notSendShareMessagesMap = new HashMap<String, ShareDraft>();
					} else {
						data.localStatus.localData.notSendShareMessagesMap.remove(gtype);
					}
					data.tempData.selectedImageList = null;
					thisActivity.finish();
				}
			}).show();
		} else {
			data.tempData.selectedImageList = null;
			thisActivity.finish();
		}
	}
}
