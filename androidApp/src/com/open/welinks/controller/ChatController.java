package com.open.welinks.controller;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
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
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.open.welinks.BusinessCardActivity;
import com.open.welinks.ChatActivity;
import com.open.welinks.ImagesDirectoryActivity;
import com.open.welinks.PictureBrowseActivity;
import com.open.welinks.R;
import com.open.welinks.controller.DownloadFile.DownloadListener;
import com.open.welinks.controller.UploadMultipart.UploadLoadingListener;
import com.open.welinks.model.API;
import com.open.welinks.model.Data;
import com.open.welinks.model.Data.Relationship.Group;
import com.open.welinks.model.ResponseHandlers;
import com.open.welinks.model.Data.Messages.Message;
import com.open.welinks.model.Data.UserInformation.User;
import com.open.welinks.utils.MCImageUtils;
import com.open.welinks.view.ChatView;

public class ChatController {

	public String tag = "ChatController";

	public ChatController thisController;
	public ChatActivity thisActivity;
	public Context context;
	public ChatView thisView;

	public OnClickListener mOnClickListener;
	public OnTouchListener onTouchListener;
	public OnFocusChangeListener mFocusChangeListener;
	public UploadLoadingListener uploadLoadingListener;
	public DownloadListener downloadListener;

	public Data data = Data.getInstance();
	public UploadMultipartList uploadMultipartList = UploadMultipartList.getInstance();
	public ImageLoader imageLoader = ImageLoader.getInstance();
	public DownloadFileList downloadFileList = DownloadFileList.getInstance();
	public Gson gson = new Gson();

	public DownloadFile downloadFile = null;
	public DisplayImageOptions options;
	public InputMethodManager inputMethodManager;

	public Handler handler = new Handler();

	public String type, key;

	public User currentUser = data.userInformation.currentUser;

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
		options = new DisplayImageOptions.Builder().showImageOnLoading(R.drawable.ic_stub).showImageForEmptyUri(R.drawable.ic_empty).showImageOnFail(R.drawable.ic_error).cacheInMemory(true).cacheOnDisk(true).considerExifParams(true).bitmapConfig(Bitmap.Config.RGB_565).build();
		thisView.showChatViews();
	}

	public boolean onTouchEvent(MotionEvent event) {
		return false;

	}

	public void onResume() {
		// TODO Auto-generated method stub

	}

	public void onPause() {
		// TODO Auto-generated method stub

	}

	public void onDestroy() {
		thisActivity.viewManage.chatView = null;
	}

	public void initializeListeners() {
		mOnClickListener = new OnClickListener() {

			@SuppressWarnings("unchecked")
			@Override
			public void onClick(View view) {
				if (view.getTag(R.id.tag_first) != null) {
					int position = (Integer) view.getTag(R.id.tag_first);
					data.tempData.selectedImageList = (ArrayList<String>) view.getTag(R.id.tag_second);
					Intent intent = new Intent(thisActivity, PictureBrowseActivity.class);
					intent.putExtra("position", String.valueOf(position));
					thisActivity.startActivity(intent);
				} else if (view.equals(thisView.backview)) {
					thisActivity.finish();
				} else if (view.equals(thisView.infomation)) {
					Intent intent = new Intent(thisActivity, BusinessCardActivity.class);
					intent.putExtra("key", key);
					intent.putExtra("type", type);
					thisActivity.startActivity(intent);
				} else if (view.equals(thisView.send)) {
					String text = thisView.input.getText().toString();
					sendMessage(text, "text");
					thisView.input.setText("");
				} else if (view.equals(thisView.more)) {
					showSelectTab();
				} else if (view.equals(thisView.selectedface)) {

				} else if (view.equals(thisView.selectpicture)) {
					data.tempData.selectedImageList = null;
					thisActivity.startActivityForResult(new Intent(thisActivity, ImagesDirectoryActivity.class), R.id.chat_content);
				} else if (view.equals(thisView.makeaudio)) {
					// TODO
				} else if (view.equals(thisView.more_selected)) {
					hideSelectTab();
				} else if (view.equals(thisView.input)) {
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
										thisView.chat_content.setSelection(thisView.mChatAdapter.getCount());

									}
								});
							};
						}.start();
					}
				}

			}
		};
		uploadLoadingListener = new UploadLoadingListener() {

			@Override
			public void success(UploadMultipart instance, int time) {
				thisView.mChatAdapter.notifyDataSetChanged();
			}

			@Override
			public void loading(UploadMultipart instance, int precent, long time, int status) {
				// TODO Auto-generated method stub

			}
		};
		downloadListener = new DownloadListener() {

			@Override
			public void success(DownloadFile instance, int status) {
				imageLoader.displayImage("file://" + instance.path, (ImageView) instance.view, options);
			}

			@Override
			public void loading(DownloadFile instance, int precent, int status) {
				// TODO Auto-generated method stub

			}

			@Override
			public void failure(DownloadFile instance, int status) {
				// TODO Auto-generated method stub
				
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
		thisView.backview.setOnClickListener(mOnClickListener);
		thisView.infomation.setOnClickListener(mOnClickListener);
		thisView.send.setOnClickListener(mOnClickListener);
		thisView.more.setOnClickListener(mOnClickListener);
		thisView.selectedface.setOnClickListener(mOnClickListener);
		thisView.selectpicture.setOnClickListener(mOnClickListener);
		thisView.makeaudio.setOnClickListener(mOnClickListener);
		thisView.more_selected.setOnClickListener(mOnClickListener);
		thisView.input.setOnClickListener(mOnClickListener);

	}

	@SuppressWarnings("unchecked")
	public ArrayList<String> getImageFromJson(String content) {
		return gson.fromJson(content, ArrayList.class);
	}

	public void setImageThumbnail(String fileName, ImageView view) {
		File sdFile = Environment.getExternalStorageDirectory();
		File file = new File(sdFile, "welinks/thumbnail/" + fileName);
		final String url = API.DOMAIN_OSS_THUMBNAIL + "images/" + fileName + "@" + (int) (178 * thisView.displayMetrics.density + 0.5f) / 2 + "w_" + (int) (106 * thisView.displayMetrics.density + 0.5f) / 2 + "h_1c_1e_100q";
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
			inputMethodManager.hideSoftInputFromWindow(thisView.input.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
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
		ArrayList<String> selectedImageList = data.tempData.selectedImageList;
		data.tempData.selectedImageList = null;
		File targetFolder = new File(Environment.getExternalStorageDirectory(), "welinks/images/");
		ArrayList<String> content = new ArrayList<String>();
		for (String filePath : selectedImageList) {
			Map<String, Object> map = MCImageUtils.processImagesInformation(filePath, targetFolder);
			content.add((String) map.get("fileName"));
			uploadFile(filePath, (String) map.get("fileName"), (byte[]) map.get("bytes"));
		}
		sendMessage(gson.toJson(content), "image");
	}

	public void sendMessage(String messageContent, String contentType) {
		Message message = data.messages.new Message();
		message.content = messageContent;
		message.contentType = contentType;
		message.sendType = "point";
		message.phone = data.userInformation.currentUser.phone;
		message.nickName = data.userInformation.currentUser.nickName;
		message.time = String.valueOf(new Date().getTime());
		message.status = "sending";
		message.type = message.MESSAGE_TYPE_SEND;
		if ("group".equals(type)) {
			message.gid = key;
			message.sendType = "group";
			data.messages.groupMessageMap.get(key).add(message);
		} else {
			data.messages.friendMessageMap.get(key).add(message);
		}
		thisView.mChatAdapter.notifyDataSetChanged();
		sendMessageToServer(contentType, messageContent);
	}

	public void sendMessageToServer(String contentType, String content) {
		HttpUtils httpUtils = new HttpUtils();
		RequestParams params = new RequestParams();

		SendMessage sendMessage = new SendMessage();
		sendMessage.contentType = contentType;
		sendMessage.content = content;

		params.addBodyParameter("phone", currentUser.phone);
		params.addBodyParameter("accessKey", currentUser.accessKey);
		params.addBodyParameter("sendType", type);
		params.addBodyParameter("message", gson.toJson(sendMessage));

		if ("group".equals(type)) {
			Group group = data.relationship.groupsMap.get(key);
			if (group == null) {
				group = data.relationship.new Group();
				group.members.add("154");
				group.members.add("155");
				group.members.add("156");
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

	public void uploadFile(final String filePath, final String fileName, final byte[] bytes) {
		new Thread() {
			@Override
			public void run() {
				UploadMultipart multipart = new UploadMultipart(filePath, fileName, bytes);
				uploadMultipartList.addMultipart(multipart);
				multipart.setUploadLoadingListener(uploadLoadingListener);
			}
		}.start();
	}

	public class SendMessage {
		public String contentType;// image voice text
		public String content;
	}
}