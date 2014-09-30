package com.open.welinks.controller;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Environment;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnTouchListener;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;

import com.google.gson.Gson;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.open.lib.MyLog;
import com.open.welinks.BusinessCardActivity;
import com.open.welinks.ChatActivity;
import com.open.welinks.GroupInfomationActivity;
import com.open.welinks.ImageScanActivity;
import com.open.welinks.ImagesDirectoryActivity;
import com.open.welinks.R;
import com.open.welinks.ShareMessageDetailActivity;
import com.open.welinks.model.API;
import com.open.welinks.model.Constant;
import com.open.welinks.model.Data;
import com.open.welinks.model.Data.Messages.Message;
import com.open.welinks.model.Data.Relationship.Friend;
import com.open.welinks.model.Data.Relationship.Group;
import com.open.welinks.model.Data.UserInformation.User;
import com.open.welinks.model.Parser;
import com.open.welinks.model.ResponseHandlers;
import com.open.welinks.utils.SHA1;
import com.open.welinks.utils.StreamParser;
import com.open.welinks.view.ChatView;
import com.open.welinks.view.ViewManage;

public class ChatController {

	public String tag = "ChatController";

	public Parser parser = Parser.getInstance();
	public MyLog log = new MyLog(tag, true);

	public ChatController thisController;
	public ChatActivity thisActivity;
	public Context context;
	public ChatView thisView;

	public OnClickListener mOnClickListener;
	public OnTouchListener onTouchListener;
	public OnFocusChangeListener mFocusChangeListener;
	public OnUploadLoadingListener uploadLoadingListener;
	public OnDownloadListener downloadListener, headDownloadListener;

	public Data data = Data.getInstance();
	public UploadMultipartList uploadMultipartList = UploadMultipartList.getInstance();
	public ImageLoader imageLoader = ImageLoader.getInstance();
	public DownloadFileList downloadFileList = DownloadFileList.getInstance();
	public Gson gson = new Gson();

	public DownloadFile downloadFile = null;
	public DisplayImageOptions options, headOptions;
	public InputMethodManager inputMethodManager;

	public Handler handler = new Handler();

	public Map<String, Map<String, String>> unsendMessageInfo;

	public String type, key;

	public User currentUser = data.userInformation.currentUser;

	public File sdFile;

	public ViewManage viewManage = ViewManage.getInstance();

	public ChatController(ChatActivity thisActivity) {
		this.thisActivity = thisActivity;
		context = thisActivity;
		thisController = this;
	}

	public void onCreate() {
		String key = thisActivity.getIntent().getStringExtra("id");
		if (key != null && !"".equals(key)) {
			this.key = key;
		}
		String type = thisActivity.getIntent().getStringExtra("type");
		if (type != null && !"".equals(type)) {
			this.type = type;
		}
		inputMethodManager = (InputMethodManager) thisActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
		sdFile = Environment.getExternalStorageDirectory();
		options = new DisplayImageOptions.Builder().showImageOnLoading(R.drawable.defaultimage).showImageForEmptyUri(R.drawable.defaultimage).showImageOnFail(R.drawable.defaultimage).cacheInMemory(true).cacheOnDisk(true).considerExifParams(true).bitmapConfig(Bitmap.Config.RGB_565).build();
		headOptions = new DisplayImageOptions.Builder().showImageOnLoading(R.drawable.ic_stub).showImageForEmptyUri(R.drawable.face_man).showImageOnFail(R.drawable.face_man).cacheInMemory(true).cacheOnDisk(true).considerExifParams(true).bitmapConfig(Bitmap.Config.RGB_565).displayer(new RoundedBitmapDisplayer(40)).build();
		unsendMessageInfo = new HashMap<String, Map<String, String>>();
		thisView.showChatViews();

		if (data.localStatus.localData.notSentMessagesMap != null) {
			String content = data.localStatus.localData.notSentMessagesMap.get(type + key);
			if (content != null) {
				thisView.inputMessageContentView.setText(content);
				// Log.e(tag, content);
			}
		}
	}

	public boolean onTouchEvent(MotionEvent event) {
		return false;

	}

	public void onResume() {
		thisView.dismissUserCardDialogView();
	}

	public void onPause() {
	}

	public void onDestroy() {

	}

	public void initializeListeners() {
		mOnClickListener = new OnClickListener() {

			@SuppressWarnings("unchecked")
			@Override
			public void onClick(View view) {
				if (view.equals(thisView.backView)) {
					thisActivity.finish();
					// mFinish();
				} else if (view.equals(thisView.userCardMainView)) {
					thisView.dismissUserCardDialogView();
				} else if (view.equals(thisView.singleButtonView)) {
					Intent intent = new Intent(thisActivity, BusinessCardActivity.class);
					intent.putExtra("key", (String) view.getTag(R.id.tag_first));
					intent.putExtra("type", (String) view.getTag(R.id.tag_second));
					if (view.getTag(R.id.tag_third) != null) {
						intent.putExtra("isTemp", (Boolean) view.getTag(R.id.tag_third));
					}
					thisActivity.startActivity(intent);
				} else if (view.equals(thisView.infomation_layout)) {
					if ("point".equals(type)) {
						Intent intent = new Intent(thisActivity, BusinessCardActivity.class);
						intent.putExtra("key", key);
						intent.putExtra("type", type);
						thisActivity.startActivity(intent);
					} else if ("group".equals(type)) {
						Intent intent = new Intent(thisActivity, GroupInfomationActivity.class);
						intent.putExtra("gid", key);
						thisActivity.startActivity(intent);
					}
				} else if (view.equals(thisView.sendMessageView)) {
					String text = thisView.inputMessageContentView.getText().toString();
					sendMessageToLocal(text, "text", new Date().getTime());
				} else if (view.equals(thisView.moreOptions)) {
					showSelectTab();
				} else if (view.equals(thisView.selectedFaceview)) {
					// TODO
				} else if (view.equals(thisView.selectPictureView)) {
					data.tempData.selectedImageList = null;
					thisActivity.startActivityForResult(new Intent(thisActivity, ImagesDirectoryActivity.class), R.id.chat_content);
				} else if (view.equals(thisView.makeAudioView)) {
					// TODO
				} else if (view.equals(thisView.moreSelectedView)) {
					hideSelectTab();
				} else if (view.equals(thisView.inputMessageContentView)) {
					if (inputMethodManager.isActive()) {
						new Thread() {
							public void run() {
								try {
									sleep(100);
								} catch (InterruptedException e) {
									e.printStackTrace();
								}
								handler.post(new Runnable() {

									@Override
									public void run() {
										thisView.chatContentListView.setSelection(thisView.mChatAdapter.getCount());
									}
								});
							};
						}.start();
					}
				} else if (view.getTag(R.id.tag_class) != null) {
					String tag_class = (String) view.getTag(R.id.tag_class);
					if ("head_click".equals(tag_class)) {
						String phone = (String) view.getTag(R.id.tag_first);
						User user = data.userInformation.currentUser;
						if (user.phone.equals(phone)) {
							thisView.setSmallBusinessCardContent(phone, user.head, user.nickName, user.age, user.longitude, user.latitude);
						} else {
							Friend friend = data.relationship.friendsMap.get(phone);
							thisView.setSmallBusinessCardContent(phone, friend.head, friend.nickName, friend.age + "", friend.longitude, friend.latitude);
						}
						thisView.showUserCardDialogView();
					}
				} else if (view.getTag(R.id.tag_first) != null) {
					data.tempData.selectedImageList = (ArrayList<String>) view.getTag(R.id.tag_first);
					Intent intent = new Intent(thisActivity, ImageScanActivity.class);
					intent.putExtra("position", String.valueOf(0));
					thisActivity.startActivity(intent);
				} else if (view.getTag(R.id.tag_second) != null) {
					Intent intent = new Intent(thisActivity, ShareMessageDetailActivity.class);
					intent.putExtra("gid", (String) view.getTag(R.id.tag_second));
					intent.putExtra("gsid", (String) view.getTag(R.id.tag_third));
					thisActivity.startActivity(intent);
				}
			}
		};
		onTouchListener = new OnTouchListener() {

			@Override
			public boolean onTouch(View view, MotionEvent event) {
				if (view.equals(thisView.chatContentListView) && event.getAction() == MotionEvent.ACTION_DOWN) {
					if (inputMethodManager.isActive()) {
						inputMethodManager.hideSoftInputFromWindow(thisView.inputMessageContentView.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
					}
				}
				return false;
			}
		};
		uploadLoadingListener = new OnUploadLoadingListener() {

			@Override
			public void onSuccess(UploadMultipart instance, int time) {
				String first_param = (String) instance.view.getTag(R.id.tag_first);
				int total = Integer.valueOf(unsendMessageInfo.get(first_param).get("total"));
				int current = Integer.valueOf(unsendMessageInfo.get(first_param).get("current"));
				current++;
				unsendMessageInfo.get((String) instance.view.getTag(R.id.tag_first)).put("current", current + "");
				if (current == total) {
					String time0 = (String) instance.view.getTag(R.id.tag_first);
					sendMessageToServer("image", unsendMessageInfo.remove(time0).get("content"), time0);
				}
				thisView.mChatAdapter.notifyDataSetChanged();
			}

			@Override
			public void onLoading(UploadMultipart instance, int precent, long time, int status) {
			}
		};
		downloadListener = new OnDownloadListener() {

			@Override
			public void onSuccess(DownloadFile instance, int status) {
				imageLoader.displayImage("file://" + instance.path, (ImageView) instance.view, options);
			}

			@Override
			public void onLoading(DownloadFile instance, int precent, int status) {

			}

			@Override
			public void onFailure(DownloadFile instance, int status) {

			}
		};
		headDownloadListener = new OnDownloadListener() {

			@Override
			public void onSuccess(DownloadFile instance, int status) {
				imageLoader.displayImage("file://" + instance.path, (ImageView) instance.view, headOptions);
			}

			@Override
			public void onLoading(DownloadFile instance, int precent, int status) {

			}

			@Override
			public void onFailure(DownloadFile instance, int status) {
				ImageView headView = (ImageView) instance.view;
				// headView.setImageBitmap(thisView.bitmap);
				thisView.fileHandlers.getHeadImage("", headView, headOptions);
			}
		};
		mFocusChangeListener = new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View view, boolean hasFocus) {

			}
		};
	}

	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == R.id.chat_content && resultCode == Activity.RESULT_OK) {
			addImagesToMessage();
		}
	}

	public void bindEvent() {
		thisView.userCardMainView.setOnClickListener(mOnClickListener);
		thisView.singleButtonView.setOnClickListener(mOnClickListener);
		thisView.backView.setOnClickListener(mOnClickListener);
		thisView.infomation_layout.setOnClickListener(mOnClickListener);
		thisView.sendMessageView.setOnClickListener(mOnClickListener);
		thisView.moreOptions.setOnClickListener(mOnClickListener);
		thisView.selectedFaceview.setOnClickListener(mOnClickListener);
		thisView.selectPictureView.setOnClickListener(mOnClickListener);
		thisView.makeAudioView.setOnClickListener(mOnClickListener);
		thisView.moreSelectedView.setOnClickListener(mOnClickListener);
		thisView.inputMessageContentView.setOnClickListener(mOnClickListener);
		thisView.chatContentListView.setOnTouchListener(onTouchListener);

	}

	@SuppressWarnings("unchecked")
	public ArrayList<String> getImageFromJson(String content) {
		return gson.fromJson(content, ArrayList.class);
	}

	public void setImageThumbnail(String fileName, ImageView view, int width, int height) {
		File file = new File(thisView.fileHandlers.sdcardThumbnailFolder, fileName);
		final String url = API.DOMAIN_OSS_THUMBNAIL + "images/" + fileName + "@" + (int) (width * thisView.displayMetrics.density + 0.5f) / 2 + "w_" + (int) (height * thisView.displayMetrics.density + 0.5f) / 2 + "h_1c_1e_100q";
		final String path = file.getAbsolutePath();
		imageLoader.displayImage("file://" + path, view, options, new SimpleImageLoadingListener() {
			@Override
			public void onLoadingStarted(String imageUri, View view) {
			}

			@Override
			public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
				downloadFile = new DownloadFile(url, path);
				downloadFile.view = view;
				downloadFile.setDownloadFileListener(thisController.downloadListener);
				downloadFileList.addDownloadFile(downloadFile);
			}

			@Override
			public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {

			}
		});
	}

	public void showSelectTab() {
		if (inputMethodManager.isActive()) {
			inputMethodManager.hideSoftInputFromWindow(thisView.inputMessageContentView.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
		}
		Animation outAnimation = new TranslateAnimation(0, thisView.chat_bottom_bar.getWidth(), 0, 0);
		outAnimation.setDuration(150);
		outAnimation.setAnimationListener(new AnimationListener() {
			@Override
			public void onAnimationEnd(Animation animation) {
				thisView.chat_bottom_bar.clearAnimation();
				thisView.chat_bottom_bar.setVisibility(View.GONE);
			}

			@Override
			public void onAnimationStart(Animation animation) {
			}

			@Override
			public void onAnimationRepeat(Animation animation) {
			}
		});
		thisView.chat_bottom_bar.startAnimation(outAnimation);

		Animation inAnimation = new TranslateAnimation(-thisView.chat_bottom_bar_selected.getWidth(), 0, 0, 0);
		inAnimation.setDuration(150);
		thisView.chat_bottom_bar_selected.setVisibility(View.VISIBLE);
		thisView.chat_bottom_bar_selected.startAnimation(inAnimation);
	}

	public void hideSelectTab() {
		Animation outAnimation = new TranslateAnimation(0, -thisView.chat_bottom_bar_selected.getWidth(), 0, 0);
		outAnimation.setDuration(150);
		outAnimation.setAnimationListener(new AnimationListener() {
			@Override
			public void onAnimationEnd(Animation animation) {
				thisView.chat_bottom_bar_selected.clearAnimation();
				thisView.chat_bottom_bar_selected.setVisibility(View.GONE);
			}

			@Override
			public void onAnimationStart(Animation animation) {
			}

			@Override
			public void onAnimationRepeat(Animation animation) {
			}
		});
		thisView.chat_bottom_bar_selected.startAnimation(outAnimation);

		Animation inAnimation = new TranslateAnimation(thisView.chat_bottom_bar.getWidth(), 0, 0, 0);
		inAnimation.setDuration(150);
		thisView.chat_bottom_bar.setVisibility(View.VISIBLE);
		thisView.chat_bottom_bar.startAnimation(inAnimation);
	}

	public void addImagesToMessage() {
		new Thread(new Runnable() {

			@Override
			public void run() {
				ArrayList<String> selectedImageList = data.tempData.selectedImageList;
				if (selectedImageList.size() == 0) {
					return;
				}
				data.tempData.selectedImageList = null;
				File targetFolder = new File(Environment.getExternalStorageDirectory(), "welinks/images/");
				ArrayList<String> content = new ArrayList<String>();
				long time = new Date().getTime();
				View view = new View(thisActivity);
				// view.setTag(R.id.tag_first, selectedImageList.size());
				// view.setTag(R.id.tag_second, 0);
				view.setTag(R.id.tag_first, String.valueOf(time));

				Map<String, String> map0 = new HashMap<String, String>();
				map0.put("total", selectedImageList.size() + "");
				map0.put("current", 0 + "");
				unsendMessageInfo.put(time + "", map0);

				for (int i = 0; i < selectedImageList.size(); i++) {
					String filePath = selectedImageList.get(i);
					Map<String, Object> map = processImagesInformation(i, filePath, targetFolder);
					content.add((String) map.get("fileName"));
					uploadFile(filePath, (String) map.get("fileName"), (byte[]) map.get("bytes"), view);
				}
				String messageContent = gson.toJson(content);
				map0.put("content", messageContent);
				sendMessageToLocal(gson.toJson(content), "image", time);
			}
		}).start();
	}

	public SHA1 sha1 = new SHA1();

	Map<String, Object> processImagesInformation(int i, String filePath, File targetFolder) {

		Map<String, Object> map = new HashMap<String, Object>();
		String suffixName = filePath.substring(filePath.lastIndexOf("."));
		if (suffixName.equals(".jpg") || suffixName.equals(".jpeg")) {
			suffixName = ".osj";
		} else if (suffixName.equals(".png")) {
			suffixName = ".osp";
		}
		String fileName = "";
		File fromFile = new File(filePath);
		// long fileLength = fromFile.length();
		byte[] bytes = thisView.fileHandlers.getImageFileBytes(fromFile, thisView.displayMetrics.heightPixels, thisView.displayMetrics.heightPixels);
		map.put("bytes", bytes);
		String sha1FileName = sha1.getDigestOfString(bytes);
		fileName = sha1FileName + suffixName;
		map.put("fileName", fileName);
		File toFile = new File(thisView.fileHandlers.sdcardImageFolder, fileName);
		try {
			FileOutputStream fileOutputStream = new FileOutputStream(toFile);
			StreamParser.parseToFile(bytes, fileOutputStream);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		if (i == 0) {
			File toSnapFile = new File(thisView.fileHandlers.sdcardThumbnailFolder, fileName);
			thisView.fileHandlers.makeImageThumbnail(fromFile, thisView.imageWidth, thisView.imageHeight, toSnapFile, fileName);
		}
		return map;
	}

	public void sendMessageToLocal(String messageContent, String contentType, long time) {
		// TODO
		if ("text".equals(contentType)) {
			if ("".equals(messageContent.trim())) {
				return;
			}
		} else if ("image".equals(contentType)) {
			// TODO
		}
		handler.post(new Runnable() {

			@Override
			public void run() {
				thisView.inputMessageContentView.setText("");
			}
		});
		parser.check();
		List<String> messagesOrder = data.messages.messagesOrder;
		String key0 = "";
		if ("point".equals(type)) {
			key0 = "p" + key;
			if (messagesOrder.contains(key0)) {
				messagesOrder.remove(key0);
			}
			messagesOrder.add(0, key0);
		} else if ("group".equals(type)) {
			key0 = "g" + key;
			if (messagesOrder.contains(key0)) {
				messagesOrder.remove(key0);
			}
			messagesOrder.add(0, key0);
		}
		data.messages.isModified = true;

		User user = data.userInformation.currentUser;
		Message message = data.messages.new Message();
		message.content = messageContent;
		message.contentType = contentType;
		message.phone = user.phone;
		message.nickName = user.nickName;
		message.time = String.valueOf(time);
		message.status = "sending";
		message.type = Constant.MESSAGE_TYPE_SEND;
		if ("group".equals(type)) {
			message.gid = key;
			message.sendType = "group";

			parser.check();
			// TODO null point exception ?
			message.phoneto = data.relationship.groupsMap.get(key).members.toString();
			Map<String, ArrayList<Message>> groupMessageMap = data.messages.groupMessageMap;
			if (groupMessageMap == null) {
				groupMessageMap = new HashMap<String, ArrayList<Message>>();
				data.messages.groupMessageMap = groupMessageMap;
			}
			ArrayList<Message> messages = groupMessageMap.get(key0);
			if (messages == null) {
				messages = new ArrayList<Message>();
				groupMessageMap.put(key0, messages);
			}
			messages.add(message);
		} else if ("point".equals(type)) {
			message.sendType = "point";
			message.phoneto = "[\"" + key + "\"]";
			parser.check();
			Map<String, ArrayList<Message>> friendMessageMap = data.messages.friendMessageMap;
			if (friendMessageMap == null) {
				friendMessageMap = new HashMap<String, ArrayList<Message>>();
				data.messages.friendMessageMap = friendMessageMap;
			}
			ArrayList<Message> messages = friendMessageMap.get(key0);
			if (messages == null) {
				messages = new ArrayList<Message>();
				friendMessageMap.put(key0, messages);
			}
			messages.add(message);
		}
		handler.post(new Runnable() {

			@Override
			public void run() {
				thisView.mChatAdapter.notifyDataSetChanged();
			}
		});
		if ("text".equals(contentType)) {
			sendMessageToServer(contentType, messageContent, message.time);
		}
	}

	public void sendMessageToServer(String contentType, String content, String time) {
		HttpUtils httpUtils = new HttpUtils();
		RequestParams params = new RequestParams();

		params.addBodyParameter("phone", currentUser.phone);
		params.addBodyParameter("accessKey", currentUser.accessKey);
		params.addBodyParameter("sendType", type);
		params.addBodyParameter("contentType", contentType);
		params.addBodyParameter("content", content);
		params.addBodyParameter("time", time);
		if ("group".equals(type)) {
			Group group = data.relationship.groupsMap.get(key);
			if (group == null) {
				group = data.relationship.new Group();
			}
			params.addBodyParameter("gid", key);
			params.addBodyParameter("phoneto", gson.toJson(group.members));
		} else if ("point".equals(type)) {
			List<String> phoneto = new ArrayList<String>();
			phoneto.add(key);
			params.addBodyParameter("phoneto", gson.toJson(phoneto));
			params.addBodyParameter("gid", "");
		}

		ResponseHandlers responseHandlers = ResponseHandlers.getInstance();
		httpUtils.send(HttpMethod.POST, API.MESSAGE_SEND, params, responseHandlers.message_sendMessageCallBack);
	}

	public void uploadFile(final String filePath, final String fileName, final byte[] bytes, final View view) {
		UploadMultipart multipart = new UploadMultipart(filePath, fileName, bytes, UploadMultipart.UPLOAD_TYPE_IMAGE);
		multipart.view = view;
		uploadMultipartList.addMultipart(multipart);
		multipart.setUploadLoadingListener(uploadLoadingListener);
	}

	public void setHeadImage(String fileName, ImageView view) {
		File sdFile = Environment.getExternalStorageDirectory();
		File file = new File(sdFile, "welinks/heads/" + fileName);
		final String url = API.DOMAIN_COMMONIMAGE + "heads/" + fileName;
		final String path = file.getAbsolutePath();
		imageLoader.displayImage("file://" + file.getAbsolutePath(), view, headOptions, new SimpleImageLoadingListener() {
			@Override
			public void onLoadingStarted(String imageUri, View view) {
			}

			@Override
			public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
				downloadFile = new DownloadFile(url, path);
				downloadFile.view = view;
				downloadFile.setDownloadFileListener(thisController.headDownloadListener);
				downloadFileList.addDownloadFile(downloadFile);
			}

			@Override
			public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {

			}
		});
	}

	public void onBackPressed() {
		// mFinish();
		// thisActivity.finish();
	}

	public void finish() {
		String content = thisView.inputMessageContentView.getText().toString();
		if (!"".equals(content)) {
			Map<String, String> notSentMessagesMap = data.localStatus.localData.notSentMessagesMap;
			if (notSentMessagesMap == null) {
				notSentMessagesMap = new HashMap<String, String>();
				data.localStatus.localData.notSentMessagesMap = notSentMessagesMap;
			}
			notSentMessagesMap.put(type + key, content);
		}
		// log.e(content + "------message");
		viewManage.chatView = null;
		viewManage.messagesSubView.showMessagesSequence();
	}
}
