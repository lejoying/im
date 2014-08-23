package com.open.welinks.view;

import com.open.welinks.R;
import com.open.welinks.controller.ChatController;

import android.app.Activity;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class ChatView {

	public DisplayMetrics displayMetrics;
	public LayoutInflater mInflater;

	public ChatController thisController;
	public Activity thisActivity;
	public Context context;
	public ChatView thisView;

	public RelativeLayout backview;
	public TextView name;
	public ImageView infomation;
	public RelativeLayout chat_content;
	public RelativeLayout chat_bottom_bar;
	public ImageView send;
	public ImageView more;
	public EditText input;
	public RelativeLayout chat_bottom_bar_selected;
	public RelativeLayout selectedface;
	public RelativeLayout selectpicture;
	public RelativeLayout makeaudio;
	public ImageView more_selected;

	public ChatView(Activity thisActivity) {
		this.thisActivity = thisActivity;
		context = thisActivity;
		thisView = this;
	}

	public void initViews() {

		mInflater = thisActivity.getLayoutInflater();
		displayMetrics = new DisplayMetrics();
		thisActivity.getWindowManager().getDefaultDisplay()
				.getMetrics(displayMetrics);
		thisActivity.setContentView(R.layout.activity_chat);

		backview = (RelativeLayout) thisActivity.findViewById(R.id.backview);
		name = (TextView) thisActivity.findViewById(R.id.name);
		infomation = (ImageView) thisActivity.findViewById(R.id.infomation);
		chat_content = (RelativeLayout) thisActivity
				.findViewById(R.id.chat_content);
		chat_bottom_bar = (RelativeLayout) thisActivity
				.findViewById(R.id.chat_bottom_bar);
		send = (ImageView) thisActivity.findViewById(R.id.send);
		more = (ImageView) thisActivity.findViewById(R.id.more);
		input = (EditText) thisActivity.findViewById(R.id.input);
		chat_bottom_bar_selected = (RelativeLayout) thisActivity
				.findViewById(R.id.chat_bottom_bar_selected);
		selectedface = (RelativeLayout) thisActivity
				.findViewById(R.id.selectedface);
		selectpicture = (RelativeLayout) thisActivity
				.findViewById(R.id.selectpicture);
		makeaudio = (RelativeLayout) thisActivity.findViewById(R.id.makeaudio);
		more_selected = (ImageView) thisActivity
				.findViewById(R.id.more_selected);

	}

}
