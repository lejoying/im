package com.open.welinks.controller;

import java.io.File;
import java.util.ArrayList;
import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.TranslateAnimation;

import com.google.gson.Gson;
import com.open.welinks.ImagesDirectoryActivity;
import com.open.welinks.R;
import com.open.welinks.controller.UploadMultipart.UploadLoadingListener;
import com.open.welinks.model.Data;
import com.open.welinks.model.Data.MessageContent;
import com.open.welinks.model.Data.Messages.Message;
import com.open.welinks.utils.MCImageUtils;
import com.open.welinks.view.ChatView;

public class ChatController {

	public String tag = "UserIntimateController";

	public ChatController thisController;
	public Activity thisActivity;
	public Context context;
	public ChatView thisView;

	public OnClickListener mOnClickListener;
	public OnTouchListener onTouchListener;
	public UploadLoadingListener uploadLoadingListener;

	public Data data = Data.getInstance();
	public UploadMultipartList uploadMultipartList = UploadMultipartList
			.getInstance();
	public Gson gson = new Gson();

	public String type, id;

	public ChatController(Activity thisActivity) {
		this.thisActivity = thisActivity;
		context = thisActivity;
		thisController = this;
	}

	public void onCreate() {
		String id = thisActivity.getIntent().getStringExtra("id");
		if (id != null && !"".equals(id)) {
			this.id = id;
		}
		String type = thisActivity.getIntent().getStringExtra("type");
		if (type != null && !"".equals(type)) {
			this.type = type;
		}
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

	public void initializeListeners() {
		mOnClickListener = new OnClickListener() {

			@Override
			public void onClick(View view) {
				if (view.equals(thisView.backview)) {
					thisActivity.finish();
				} else if (view.equals(thisView.infomation)) {

				} else if (view.equals(thisView.send)) {

				} else if (view.equals(thisView.more)) {
					showSelectTab();
				} else if (view.equals(thisView.selectedface)) {

				} else if (view.equals(thisView.selectpicture)) {
					thisActivity.startActivityForResult(new Intent(
							thisActivity, ImagesDirectoryActivity.class),
							R.id.chat_content);
				} else if (view.equals(thisView.makeaudio)) {
					// TODO
				} else if (view.equals(thisView.more_selected)) {
					hideSelectTab();
				}

			}
		};
		uploadLoadingListener = new UploadLoadingListener() {

			@Override
			public void success(UploadMultipart instance, int time) {
				// TODO Auto-generated method stub

			}

			@Override
			public void loading(UploadMultipart instance, int precent,
					long time, int status) {
				// TODO Auto-generated method stub

			}
		};
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

	}

	public MessageContent getMessageContent(String content) {
		return gson.fromJson(content, MessageContent.class);
	}

	public void showSelectTab() {
		Animation outAnimation = new TranslateAnimation(0,
				thisView.chat_bottom_bar.getWidth(), 0, 0);
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

		Animation inAnimation = new TranslateAnimation(
				-thisView.chat_bottom_bar_selected.getWidth(), 0, 0, 0);
		inAnimation.setDuration(150);
		thisView.chat_bottom_bar_selected.setVisibility(View.VISIBLE);
		thisView.chat_bottom_bar_selected.startAnimation(inAnimation);
	}

	public void hideSelectTab() {
		Animation outAnimation = new TranslateAnimation(0,
				-thisView.chat_bottom_bar_selected.getWidth(), 0, 0);
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

		Animation inAnimation = new TranslateAnimation(
				thisView.chat_bottom_bar.getWidth(), 0, 0, 0);
		inAnimation.setDuration(150);
		thisView.chat_bottom_bar.setVisibility(View.VISIBLE);
		thisView.chat_bottom_bar.startAnimation(inAnimation);
	}

	public void addImagesToMessage() {
		ArrayList<String> selectedImageList = data.tempData.selectedImageList;
		data.tempData.selectedImageList = null;
		MessageContent content = data.new MessageContent();
		File targetFolder = new File(Environment.getExternalStorageDirectory(),
				"welinks/images/");
		for (String filePath : selectedImageList) {
			Map<String, Object> map = MCImageUtils.processImagesInformation(
					filePath, targetFolder);
			content.images.add((String) map.get("fileName"));
			uploadFile(filePath, (String) map.get("fileName"),
					(byte[]) map.get("bytes"));
		}
		sendMessage(content);
	}

	public void sendMessage(MessageContent content) {
		String contentType = "";
		if ("".equals(content.text)) {
			if ("".equals(content.voice)) {
				contentType = "image";
			} else {
				contentType = "voice";
			}
		} else {
			contentType = "text";
		}
		Message message = data.messages.new Message();
		message.content = gson.toJson(content);
		message.contentType = contentType;
		message.gid = "";
		message.sendType = "";
		message.phone = data.userInformation.currentUser.phone;
		message.nickName = data.userInformation.currentUser.nickName;
		message.status = "";
		message.time = "";
		message.type = Message.MESSAGE_TYPE_SEND;
	}

	public void uploadFile(final String filePath, final String fileName,
			final byte[] bytes) {
		new Thread() {
			@Override
			public void run() {
				UploadMultipart multipart = new UploadMultipart(filePath,
						fileName, bytes);
				uploadMultipartList.addMultipart(multipart);
				multipart.setUploadLoadingListener(uploadLoadingListener);
			}
		}.start();
	}
}
